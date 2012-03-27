package demo;

import org.jfree.data.xy.AbstractXYZDataset;
import org.jfree.data.xy.XYZDataset;

public class SampleXYZDataset2 extends AbstractXYZDataset
  implements XYZDataset
{
  private double[][] xVal = { { 1.0D, 2.0D, 3.0D }, { 4.0D, 5.0D, 6.0D } };
  private double[][] yVal = { { 1.0D, 2.0D, 3.0D }, { 4.0D, 5.0D, 6.0D } };
  private double[][] zVal = { { 1.1D, 2.2D, 3.3D }, { 4.4D, 5.5D, 6.6D } };

  public int getSeriesCount()
  {
    return this.xVal.length;
  }

  public Comparable getSeriesKey(int paramInt)
  {
    return "Series " + paramInt;
  }

  public int getItemCount(int paramInt)
  {
    return this.xVal[0].length;
  }

  public Number getX(int paramInt1, int paramInt2)
  {
    return new Double(this.xVal[paramInt1][paramInt2]);
  }

  public Number getY(int paramInt1, int paramInt2)
  {
    return new Double(this.yVal[paramInt1][paramInt2]);
  }

  public Number getZ(int paramInt1, int paramInt2)
  {
    return new Double(this.zVal[paramInt1][paramInt2]);
  }
}