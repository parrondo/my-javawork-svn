package demo;

import org.jfree.data.xy.AbstractXYZDataset;
import org.jfree.data.xy.XYZDataset;

public class SampleXYZDataset extends AbstractXYZDataset
  implements XYZDataset
{
  private double[] xVal = { 2.1D, 2.375625D, 2.375625D, 2.232928726D, 2.232928726D, 1.860415253D, 1.840842668D, 1.905415253D, 2.336029412D, 3.8D };
  private double[] yVal = { 14.167999999999999D, 11.156000000000001D, 10.089D, 8.884D, 8.718999999999999D, 8.465999999999999D, 5.489D, 4.107D, 4.101D, 25.0D };
  private double[] zVal = { 2.45D, 2.791285714D, 2.791285714D, 2.2125D, 2.2125D, 2.22D, 2.1D, 2.22D, 1.64875D, 4.0D };

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
    return this.xVal.length;
  }

  public Number getX(int paramInt1, int paramInt2)
  {
    return new Double(this.xVal[paramInt2]);
  }

  public Number getY(int paramInt1, int paramInt2)
  {
    return new Double(this.yVal[paramInt2]);
  }

  public Number getZ(int paramInt1, int paramInt2)
  {
    return new Double(this.zVal[paramInt2]);
  }
}