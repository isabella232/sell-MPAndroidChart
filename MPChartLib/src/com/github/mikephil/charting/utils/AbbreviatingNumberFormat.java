package com.github.mikephil.charting.utils;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class AbbreviatingNumberFormat extends NumberFormat {
  private static final String[] SUFFIXES = new String[] { "", "K", "M", "B" };
  private static final long THOUSAND = 1000;
  private final NumberFormat mOriginalFormat;
  private final int mMaxDigits;
  private final int mFractionDigits;

  public AbbreviatingNumberFormat(NumberFormat originalFormat, int maxDigits, int fractionDigits) {
    mOriginalFormat = originalFormat;
    mMaxDigits = maxDigits;
    mFractionDigits = fractionDigits;
  }

  private static int getNumberOfNonFractionDigits(int number) {
    return String.valueOf(number).length(); // apparently using log10 for this is an overkill
  }

  @Override
  public StringBuffer format(double value, StringBuffer buffer, FieldPosition field) {
    return format((long) value, buffer, field);
  }

  @Override
  public StringBuffer format(long value, StringBuffer buffer, FieldPosition field) {
    float currency = value;
    int suffix = 0;
    while (currency >= THOUSAND
        && (suffix == 0 || suffix < SUFFIXES.length - 1)) {
      currency /= THOUSAND;
      suffix++;
    }
    mOriginalFormat.setMaximumFractionDigits(Math.min(mMaxDigits - getNumberOfNonFractionDigits((int) currency), mFractionDigits));

    String currencyOut = mOriginalFormat.format(currency);
    if (suffix != 0) {
      for (int i = currencyOut.length() - 1; i >= 0; --i) {
        if (Character.isDigit(currencyOut.charAt(i))) {
          currencyOut = currencyOut.substring(0, i + 1) + SUFFIXES[suffix]
              + currencyOut.substring(i + 1);
          break;
        }
      }
    }

    return new StringBuffer(currencyOut);
  }

  @Override
  public Number parse(String string, ParsePosition position) {
    return mOriginalFormat.parse(string, position);
  }
}
