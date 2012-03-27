package demo;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.data.general.DefaultValueDataset;

public class MeterChartDemo4
{
  public static void main(String[] paramArrayOfString)
  {
    DefaultValueDataset localDefaultValueDataset = new DefaultValueDataset(75.0D);
    MeterPlot localMeterPlot = new MeterPlot(localDefaultValueDataset);
    JFreeChart localJFreeChart = new JFreeChart("Scaled Image Test", localMeterPlot);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    try
    {
      File localFile = new File("meterchart100.png");
      BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(localFile));
      BufferedImage localBufferedImage = localJFreeChart.createBufferedImage(200, 200, 400.0D, 400.0D, null);
      ChartUtilities.writeBufferedImageAsPNG(localBufferedOutputStream, localBufferedImage);
    }
    catch (IOException localIOException)
    {
      System.out.println(localIOException.toString());
    }
  }
}