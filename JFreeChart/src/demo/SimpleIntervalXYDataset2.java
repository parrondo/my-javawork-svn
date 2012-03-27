package demo;

import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;

public class SimpleIntervalXYDataset2 extends AbstractIntervalXYDataset
  implements IntervalXYDataset
{
  private Double[] yStart;
  private Double[] yEnd = new Double[3];
  private Double[] xValues = new Double[3];

  public SimpleIntervalXYDataset2(int paramInt)
  {
    this.xValues = new Double[paramInt];
    this.yStart = new Double[paramInt];
    this.yEnd = new Double[paramInt];
    double d = 100.0D;
    for (int i = 1; i <= paramInt; ++i)
    {
      this.xValues[(i - 1)] = new Double(i);
      d *= (1.0D + Math.random() / 10.0D - 0.05D);
      this.yStart[(i - 1)] = new Double(d);
      this.yEnd[(i - 1)] = new Double(this.yStart[(i - 1)].doubleValue() + Math.random() * 30.0D);
    }
  }

  public int getSeriesCount()
  {
    return 1;
  }

  public Comparable getSeriesKey(int paramInt)
  {
    return "Series 1";
  }

  public int getItemCount(int paramInt)
  {
    return this.xValues.length;
  }

  public Number getX(int paramInt1, int paramInt2)
  {
    return this.xValues[paramInt2];
  }

  public Number getY(int paramInt1, int paramInt2)
  {
    return this.yEnd[paramInt2];
  }

  public Number getStartX(int paramInt1, int paramInt2)
  {
    return this.xValues[paramInt2];
  }

  public Number getEndX(int paramInt1, int paramInt2)
  {
    return this.xValues[paramInt2];
  }

  public Number getStartY(int paramInt1, int paramInt2)
  {
    return this.yStart[paramInt2];
  }

  public Number getEndY(int paramInt1, int paramInt2)
  {
    return this.yEnd[paramInt2];
  }

  public void addChangeListener(DatasetChangeListener paramDatasetChangeListener)
  {
  }

  public void removeChangeListener(DatasetChangeListener paramDatasetChangeListener)
  {
  }
}