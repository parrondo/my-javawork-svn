package demo;

import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;

public class SimpleIntervalXYDataset extends AbstractIntervalXYDataset
  implements IntervalXYDataset
{
  private Double[] xStart = new Double[3];
  private Double[] xEnd = new Double[3];
  private Double[] yValues = new Double[3];

  public SimpleIntervalXYDataset()
  {
    this.xStart[0] = new Double(0.0D);
    this.xStart[1] = new Double(2.0D);
    this.xStart[2] = new Double(3.5D);
    this.xEnd[0] = new Double(2.0D);
    this.xEnd[1] = new Double(3.5D);
    this.xEnd[2] = new Double(4.0D);
    this.yValues[0] = new Double(3.0D);
    this.yValues[1] = new Double(4.5D);
    this.yValues[2] = new Double(2.5D);
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
    return 3;
  }

  public Number getX(int paramInt1, int paramInt2)
  {
    return this.xStart[paramInt2];
  }

  public Number getY(int paramInt1, int paramInt2)
  {
    return this.yValues[paramInt2];
  }

  public Number getStartX(int paramInt1, int paramInt2)
  {
    return this.xStart[paramInt2];
  }

  public Number getEndX(int paramInt1, int paramInt2)
  {
    return this.xEnd[paramInt2];
  }

  public Number getStartY(int paramInt1, int paramInt2)
  {
    return this.yValues[paramInt2];
  }

  public Number getEndY(int paramInt1, int paramInt2)
  {
    return this.yValues[paramInt2];
  }

  public void addChangeListener(DatasetChangeListener paramDatasetChangeListener)
  {
  }

  public void removeChangeListener(DatasetChangeListener paramDatasetChangeListener)
  {
  }
}