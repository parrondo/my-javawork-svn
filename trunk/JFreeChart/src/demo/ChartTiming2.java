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

public class ChartTiming2
  implements ActionListener
{
  private boolean finished;

  public void run()
  {
    this.finished = false;
    SampleXYDataset2 localSampleXYDataset2 = new SampleXYDataset2(1, 1440);
    boolean bool = true;
    JFreeChart localJFreeChart = ChartFactory.createScatterPlot("Scatter plot timing", "X", "Y", localSampleXYDataset2, PlotOrientation.VERTICAL, bool, false, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setRenderer(new XYDotRenderer());
    BufferedImage localBufferedImage = new BufferedImage(400, 300, 1);
    Graphics2D localGraphics2D = localBufferedImage.createGraphics();
    Rectangle2D.Double localDouble = new Rectangle2D.Double(0.0D, 0.0D, 400.0D, 300.0D);
    Timer localTimer = new Timer(10000, this);
    localTimer.setRepeats(false);
    int i = 0;
    localTimer.start();
    while (!(this.finished))
    {
      localJFreeChart.draw(localGraphics2D, localDouble, null, null);
      System.out.println("Charts drawn..." + i);
      if (this.finished)
        continue;
      ++i;
    }
    System.out.println("DONE");
  }

  public void actionPerformed(ActionEvent paramActionEvent)
  {
    this.finished = true;
  }

  public static void main(String[] paramArrayOfString)
  {
    ChartTiming2 localChartTiming2 = new ChartTiming2();
    localChartTiming2.run();
  }
}