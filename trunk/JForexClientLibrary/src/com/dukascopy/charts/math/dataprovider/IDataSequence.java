package com.dukascopy.charts.math.dataprovider;

import com.dukascopy.charts.data.datacache.Data;

public abstract interface IDataSequence<DataClass extends Data>
{
  public abstract boolean isEmpty();

  public abstract int size();

  public abstract long getFrom();

  public abstract long getTo();

  public abstract double getMin();

  public abstract double getMax();

  public abstract DataClass[] getData();

  public abstract int getExtraBefore();

  public abstract int getExtraAfter();

  public abstract double[] getFormulaOutputDouble(int paramInt1, int paramInt2);

  public abstract int[] getFormulaOutputInt(int paramInt1, int paramInt2);

  public abstract boolean isFormulasMinMaxEmpty(Integer paramInteger);

  public abstract double getFormulasMinFor(Integer paramInteger);

  public abstract double getFormulasMaxFor(Integer paramInteger);

  public abstract Object getFormulaValue(int paramInt1, int paramInt2, long paramLong);

  public abstract Boolean isFormulaDowntrendAt(int paramInt1, int paramInt2, long paramLong);

  public abstract long[][] getGaps();

  public abstract boolean isLatestDataVisible();

  public abstract DataClass getData(int paramInt);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.IDataSequence
 * JD-Core Version:    0.6.0
 */