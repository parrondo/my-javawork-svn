package demo;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.PrintStream;
import javax.swing.Timer;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.FastScatterPlot;

public class ChartTiming4
  implements ActionListener
{
  private boolean finished;
  private float[][] data = new float[2][1440];

  public void run()
  {
    this.finished = false;
    populateData();
    FastScatterPlot localFastScatterPlot = new FastScatterPlot(this.data, new NumberAxis("X"), new NumberAxis("Y"));
    JFreeChart localJFreeChart = new JFreeChart("Fast Scatter Plot Timing", JFreeChart.DEFAULT_TITLE_FONT, localFastScatterPlot, true);
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

  private void populateData()
  {
    for (int i = 0; i < this.data[0].length; ++i)
    {
      float f = i;
      this.data[0][i] = f;
      this.data[1][i] = (100.0F + 2.0F * f + (float)Math.random() * 1440.0F);
    }
  }

  public static void main(String[] paramArrayOfString)
  {
    ChartTiming4 localChartTiming4 = new ChartTiming4();
    localChartTiming4.run();
  }
}