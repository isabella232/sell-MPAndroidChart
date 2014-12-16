package com.github.mikephil.charting.utils;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class AbbreviatingNumberFormat extends NumberFormat {
  private static final String[] SUFFIXES = new String[] { "", "K", "M", "B" };
  private static final long THOUSAND = 1000;
  private final NumberFormat mOriginalFormat;

  public AbbreviatingNumberFormat(NumberFormat originalFormat) {
    mOriginalFormat = originalFormat;
  }

  @Override
  public StringBuffer format(double value, StringBuffer buffer, FieldPosition field) {
    return format((long) value, buffer, field);
  }

  @Override
  public StringBuffer format(long value, StringBuffer buffer, FieldPosition field) {
    if (value == 0 || value < 1000) {
      return new StringBuffer(mOriginalFormat.format(value));
    }

    // cut 0s by 3 (000)
    int suffix = 0;
    double fValue = value;
    while (suffix < SUFFIXES.length) {
      double fNext = fValue / THOUSAND;
      if ((long)fNext*THOUSAND != fValue) {
        break;
      }
      fValue = fNext;
      suffix++;
    }

    long cutValue = (long) fValue;

    String currencyOut = mOriginalFormat.format(cutValue);
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
