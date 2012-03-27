package demo;

import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;

public class SampleXYDataset2 extends AbstractXYDataset
  implements XYDataset, DomainInfo, RangeInfo
{
  private static final int DEFAULT_SERIES_COUNT = 4;
  private static final int DEFAULT_ITEM_COUNT = 40;
  private static final double DEFAULT_RANGE = 200.0D;
  private Double[][] xValues;
  private Double[][] yValues;
  private int seriesCount;
  private int itemCount;
  private Number domainMin;
  private Number domainMax;
  private Number rangeMin;
  private Number rangeMax;
  private Range domainRange;
  private Range range;

  public SampleXYDataset2()
  {
    this(4, 40);
  }

  public SampleXYDataset2(int paramInt1, int paramInt2)
  {
    this.xValues = new Double[paramInt1][paramInt2];
    this.yValues = new Double[paramInt1][paramInt2];
    this.seriesCount = paramInt1;
    this.itemCount = paramInt2;
    double d1 = (1.0D / 0.0D);
    double d2 = (-1.0D / 0.0D);
    double d3 = (1.0D / 0.0D);
    double d4 = (-1.0D / 0.0D);
    for (int i = 0; i < paramInt1; ++i)
      for (int j = 0; j < paramInt2; ++j)
      {
        double d5 = (Math.random() - 0.5D) * 200.0D;
        this.xValues[i][j] = new Double(d5);
        if (d5 < d1)
          d1 = d5;
        if (d5 > d2)
          d2 = d5;
        double d6 = (Math.random() + 0.5D) * 6.0D * d5 + d5;
        this.yValues[i][j] = new Double(d6);
        if (d6 < d3)
          d3 = d6;
        if (d6 <= d4)
          continue;
        d4 = d6;
      }
    this.domainMin = new Double(d1);
    this.domainMax = new Double(d2);
    this.domainRange = new Range(d1, d2);
    this.rangeMin = new Double(d3);
    this.rangeMax = new Double(d4);
    this.range = new Range(d3, d4);
  }

  public Number getX(int paramInt1, int paramInt2)
  {
    return this.xValues[paramInt1][paramInt2];
  }

  public Number getY(int paramInt1, int paramInt2)
  {
    return this.yValues[paramInt1][paramInt2];
  }

  public int getSeriesCount()
  {
    return this.seriesCount;
  }

  public Comparable getSeriesKey(int paramInt)
  {
    return "Sample " + paramInt;
  }

  public int getItemCount(int paramInt)
  {
    return this.itemCount;
  }

  public double getDomainLowerBound()
  {
    return this.domainMin.doubleValue();
  }

  public double getDomainLowerBound(boolean paramBoolean)
  {
    return this.domainMin.doubleValue();
  }

  public double getDomainUpperBound()
  {
    return this.domainMax.doubleValue();
  }

  public double getDomainUpperBound(boolean paramBoolean)
  {
    return this.domainMax.doubleValue();
  }

  public Range getDomainBounds()
  {
    return this.domainRange;
  }

  public Range getDomainBounds(boolean paramBoolean)
  {
    return this.domainRange;
  }

  public Range getDomainRange()
  {
    return this.domainRange;
  }

  public double getRangeLowerBound()
  {
    return this.rangeMin.doubleValue();
  }

  public double getRangeLowerBound(boolean paramBoolean)
  {
    return this.rangeMin.doubleValue();
  }

  public double getRangeUpperBound()
  {
    return this.rangeMax.doubleValue();
  }

  public double getRangeUpperBound(boolean paramBoolean)
  {
    return this.rangeMax.doubleValue();
  }

  public Range getRangeBounds(boolean paramBoolean)
  {
    return this.range;
  }

  public Range getValueRange()
  {
    return this.range;
  }

  public Number getMinimumDomainValue()
  {
    return this.domainMin;
  }

  public Number getMaximumDomainValue()
  {
    return this.domainMax;
  }

  public Number getMinimumRangeValue()
  {
    return this.domainMin;
  }

  public Number getMaximumRangeValue()
  {
    return this.domainMax;
  }
}