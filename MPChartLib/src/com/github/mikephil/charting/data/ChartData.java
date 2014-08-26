package com.github.mikephil.charting.data;

import com.github.mikephil.charting.utils.Highlight;

import java.util.ArrayList;

/**
 * Class that holds all relevant data that represents the chart. That involves
 * at least one (or more) DataSets, and an array of x-values.
 *
 * @author Philipp Jahoda
 */
public class ChartData<T extends DataSet> {

  /**
   * maximum y-value in the y-value array
   */
  private float mYMax = 0.0f;

  /**
   * the minimum y-value in the y-value array
   */
  private float mYMin = 0.0f;

  /**
   * the total sum of all y-values
   */
  private float mYValueSum = 0f;

  /**
   * holds all x-values the chart represents
   */
  private ArrayList<Long> mXVals;
  private ArrayList<String> mXLabels;

  /**
   * holds all the datasets (e.g. different lines) the chart represents
   */
  private ArrayList<T> mDataSets;
  private LabelFormatter mLabelFormatter;

  /**
   * constructor for chart data
   *
   * @param xVals The values describing the x-axis. Must be at least as long
   * as the highest xIndex in the Entry objects across all
   * DataSets.
   * @param dataSets all DataSet objects the chart needs to represent
   * @param padding Number of fake entries to add
   */
  public ChartData(ArrayList<Long> xVals, ArrayList<T> dataSets, LabelFormatter labelFormatter, int padding) {
    init(xVals, dataSets, labelFormatter, padding);
  }

  public ChartData(ArrayList<Long> xVals, ArrayList<T> dataSets, LabelFormatter labelFormatter) {
    this(xVals, dataSets, labelFormatter, 0);
  }

  public ChartData(ArrayList<Long> xVals, ArrayList<T> dataSets) {
    this(xVals, dataSets, new LabelFormatter() {
      @Override
      public String formatValue(long value) {
        return String.valueOf(value);
      }
    }, 0);
  }

  /**
   * Constructor that takes only one DataSet
   *
   * @param xVals
   * @param data
   */
  public ChartData(ArrayList<Long> xVals, T data) {
    this(xVals, data, SIMPLE_LABEL_FORMATTER);
  }

  public ChartData(ArrayList<Long> xVals, T data, LabelFormatter formatter) {
    ArrayList<T> sets = new ArrayList<T>();
    sets.add(data);
    init(xVals, sets, formatter, 0);
  }

  private void init(ArrayList<Long> xVals, ArrayList<T> dataSets, LabelFormatter labelFormatter, int padding) {
    mLabelFormatter = labelFormatter;
    mXVals = xVals;
    mXLabels = new ArrayList<String>();
    mDataSets = dataSets;

    addPadding(padding);

    calcMinMax();
    calcYValueSum();

    for (int i = 0; i < mDataSets.size(); i++) {
      if (mDataSets.get(i)
          .getYVals()
          .size() > xVals.size()) {
        throw new IllegalArgumentException(
            "One or more of the DataSet Entry arrays are longer than the x-values array.");
      }
    }
  }

  private void addPadding(int count) {
    if (count > 0 && mXVals.size() > 0) {
      for (DataSet set : mDataSets) {
        for (Entry entry : set.getYVals()) {
          entry.setXIndex(entry.getXIndex() + count);
        }
      }

      while (count-- > 0) {
        mXVals.add(0, 0L);
        mXVals.add(0L);
        for (DataSet set : mDataSets) {
          ArrayList<Entry> yVals = set.getYVals();
          yVals.add(0, new Entry(yVals.get(0).getVal(), count));
          Entry lastEntry = yVals.get(yVals.size() - 1);
          yVals.add(new Entry(lastEntry.getVal(), lastEntry.getXIndex() + 1));
        }
      }
    }
  }

  /**
   * Call this method to let the CartData know that the underlying data has
   * changed.
   */
  public void notifyDataChanged() {
    doCalculations();
  }

  /**
   * Does all necessary calculations, if the underlying data has changed
   */
  private void doCalculations() {
    calcMinMax();
    calcYValueSum();
  }

  /**
   * calc minimum and maximum y value over all datasets
   */
  private void calcMinMax() {
    // check which dataset to use
    ArrayList<T> dataSets = mDataSets;

    mYMin = dataSets.get(0).getYMin();
    mYMax = dataSets.get(0).getYMax();

    for (int i = 0; i < dataSets.size(); i++) {
      if (dataSets.get(i).getYMin() < mYMin)
        mYMin = dataSets.get(i).getYMin();

      if (dataSets.get(i).getYMax() > mYMax)
        mYMax = dataSets.get(i).getYMax();
    }
  }

  /**
   * calculates the sum of all y-values in all datasets
   */
  private void calcYValueSum() {

    mYValueSum = 0;

    // check which dataset to use
    ArrayList<T> dataSets = mDataSets;
    for (int i = 0; i < dataSets.size(); i++) {
      mYValueSum += Math.abs(dataSets.get(i).getYValueSum());
    }
  }

  /**
   * Corrects all values that are kept as member variables after a new entry
   * was added. This saves recalculating all values.
   *
   * @param entry the new entry
   */
  public void notifyDataForNewEntry(Entry entry) {
    mYValueSum += Math.abs(entry.getVal());
    if (mYMin > entry.getVal()) {
      mYMin = entry.getVal();
    }
    if (mYMax < entry.getVal()) {
      mYMax = entry.getVal();
    }
  }

  public int getDataSetCount() {
    return mDataSets.size();
  }

  public float getYMin() {
    return mYMin;
  }

  public float getYMax() {
    return mYMax;
  }

  public float getYValueSum() {
    return mYValueSum;
  }

  /**
   * Checks if the ChartData object contains valid data
   *
   * @return
   */
  public boolean isValid() {
    if (mXVals == null || mXVals.size() <= 1)
      return false;

    if (mDataSets == null || mDataSets.size() < 1)
      return false;

    return true;
  }

  /**
   * returns the x-values the chart represents
   *
   * @return
   */
  public ArrayList<Long> getXVals() {
    return mXVals;
  }

  /**
   * returns the x-value labels the chart represents
   *
   * @return
   */
  public ArrayList<String> getXLabels() {
    return mXLabels;
  }

  /**
   * returns the Entries array from the DataSet at the given index. If a
   * filter is set, the filtered Entries are returned
   *
   * @param index
   * @return
   */
  public ArrayList<Entry> getYVals(int index) {
    return mDataSets.get(index).getYVals();
  }

  /**
   * Get the entry for a corresponding highlight object
   *
   * @param highlight
   * @return the entry that is highlighted
   */
  public Entry getEntryForHighlight(Highlight highlight) {
    return getDataSetByIndex(highlight.getDataSetIndex()).getEntryForXIndex(
        highlight.getXIndex());
  }

  /**
   * returns the dataset at the given index.
   *
   * @param index
   * @return
   */
  public DataSet getDataSetByIndex(int index) {
    return mDataSets.get(index);
  }

  /**
   * Retrieve a DataSet with a specific label from the ChartData. Search can
   * be case sensitive or not. IMPORTANT: This method does calculations at
   * runtime, do not over-use in performance critical situations.
   *
   * @param label
   * @param ignorecase if true, the search is not case-sensitive
   * @return
   */
  public DataSet getDataSetByLabel(String label, boolean ignorecase) {
    // check which dataset to use
    ArrayList<T> dataSets = mDataSets;

    if (ignorecase) {
      for (int i = 0; i < dataSets.size(); i++) {
        if (label.equalsIgnoreCase(dataSets.get(i).getLabel()))
          return dataSets.get(i);
      }
    } else {
      for (int i = 0; i < dataSets.size(); i++) {
        if (label.equals(dataSets.get(i).getLabel()))
          return dataSets.get(i);
      }
    }

    return null;
  }

  /**
   * returns all DataSet objects the ChartData represents. If a filter is set,
   * the filtered DataSets are returned
   *
   * @return
   */
  public ArrayList<T> getDataSets() {
    return mDataSets;
  }

  /**
   * This returns the original data set, regardless of any filter options.
   *
   * @return
   */
  public ArrayList<T> getOriginalDataSets() {
    return mDataSets;
  }

  /**
   * returns the total number of x-values this chartdata represents (the size
   * of the xvals array)
   *
   * @return
   */
  public int getXValCount() {
    return mXVals.size();
  }

  /**
   * returns the total number of y-values across all DataSets the chartdata
   * represents. If a filter is set, the filtered count is returned
   *
   * @return
   */
  public int getYValCount() {
    int count = 0;
    // check which dataset to use
    ArrayList<T> dataSets = mDataSets;

    for (int i = 0; i < dataSets.size(); i++) {
      count += dataSets.get(i).getEntryCount();
    }

    return count;
  }

  /**
   * Returns the labels of all DataSets as a string array.
   *
   * @return
   */
  public String[] getDataSetLabels() {

    String[] types = new String[mDataSets.size()];

    for (int i = 0; i < mDataSets.size(); i++) {
      types[i] = mDataSets.get(i).getLabel();
    }

    return types;
  }

  /**
   * Generates an x-values array filled with numbers in range specified by the
   * parameters. Can be used for convenience.
   *
   * @return
   */
  public static ArrayList<Long> generateXVals(int from, int to) {

    ArrayList<Long> xvals = new ArrayList<Long>();

    for (int i = from; i < to; i++) {
      xvals.add((long) i);
    }

    return xvals;
  }

  private static final LabelFormatter SIMPLE_LABEL_FORMATTER = new LabelFormatter() {
    @Override
    public String formatValue(long value) {
      return String.valueOf(value);
    }
  };

  public void populateXLabels() {
    mXLabels.clear();
    for (Long val : mXVals) {
      mXLabels.add(mLabelFormatter.formatValue(val));
    }
  }

  public interface LabelFormatter {
    String formatValue(long value);
  }
}
