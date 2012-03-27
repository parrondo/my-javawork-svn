package demo;

import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;

public class SampleXYDataset extends AbstractXYDataset
  implements XYDataset
{
  private double translate = 0.0D;

  public double getTranslate()
  {
    return this.translate;
  }

  public void setTranslate(double paramDouble)
  {
    this.translate = paramDouble;
    notifyListeners(new DatasetChangeEvent(this, this));
  }

  public Number getX(int paramInt1, int paramInt2)
  {
    return new Double(-10.0D + this.translate + paramInt2 / 10.0D);
  }

  public Number getY(int paramInt1, int paramInt2)
  {
    if (paramInt1 == 0)
      return new Double(Math.cos(-10.0D + this.translate + paramInt2 / 10.0D));
    return new Double(2.0D * Math.sin(-10.0D + this.translate + paramInt2 / 10.0D));
  }

  public int getSeriesCount()
  {
    return 2;
  }

  public Comparable getSeriesKey(int paramInt)
  {
    if (paramInt == 0)
      return "y = cosine(x)";
    if (paramInt == 1)
      return "y = 2*sine(x)";
    return "Error";
  }

  public int getItemCount(int paramInt)
  {
    return 200;
  }
}