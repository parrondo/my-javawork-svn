package demo;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.PrintStream;
import javax.swing.Timer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ChartTiming3
  implements ActionListener
{
  private boolean finished;

  public void run()
  {
    this.finished = false;
    XYSeries localXYSeries = new XYSeries("Random Data");
    for (int i = 0; i < 1440; ++i)
    {
      double d1 = Math.random();
      double d2 = Math.random();
      localXYSeries.add(d1, d2);
    }
    XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection(localXYSeries);
    boolean bool = true;
    JFreeChart localJFreeChart = ChartFactory.createScatterPlot("Scatter plot timing", "X", "Y", localXYSeriesCollection, PlotOrientation.VERTICAL, bool, false, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setRenderer(new XYDotRenderer());
    BufferedImage localBufferedImage = new BufferedImage(400, 300, 1);
    Graphics2D localGraphics2D = localBufferedImage.createGraphics();
    Rectangle2D.Double localDouble = new Rectangle2D.Double(0.0D, 0.0D, 400.0D, 300.0D);
    Timer localTimer = new Timer(10000, this);
    localTimer.setRepeats(false);
    int j = 0;
    localTimer.start();
    while (!(this.finished))
    {
      localJFreeChart.draw(localGraphics2D, localDouble, null, null);
      System.out.println("Charts drawn..." + j);
      if (this.finished)
        continue;
      ++j;
    }
    System.out.println("DONE");
  }

  public void actionPerformed(ActionEvent paramActionEvent)
  {
    this.finished = true;
  }

  public static void main(String[] paramArrayOfString)
  {
    ChartTiming3 localChartTiming3 = new ChartTiming3();
    localChartTiming3.run();
  }
}