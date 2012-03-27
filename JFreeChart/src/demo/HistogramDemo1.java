package demo;

import java.awt.Dimension;
import java.io.IOException;
import java.util.Random;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class HistogramDemo1 extends ApplicationFrame
{
  public HistogramDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static IntervalXYDataset createDataset()
  {
    HistogramDataset localHistogramDataset = new HistogramDataset();
    double[] arrayOfDouble = new double[1000];
    Random localRandom = new Random(12345678L);
    for (int i = 0; i < 1000; ++i)
      arrayOfDouble[i] = (localRandom.nextGaussian() + 5.0D);
    localHistogramDataset.addSeries("H1", arrayOfDouble, 100, 2.0D, 8.0D);
    arrayOfDouble = new double[1000];
    for (int i = 0; i < 1000; ++i)
      arrayOfDouble[i] = (localRandom.nextGaussian() + 7.0D);
    localHistogramDataset.addSeries("H2", arrayOfDouble, 100, 4.0D, 10.0D);
    return localHistogramDataset;
  }

  private static JFreeChart createChart(IntervalXYDataset paramIntervalXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createHistogram("Histogram Demo 1", null, null, paramIntervalXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    localXYPlot.setForegroundAlpha(0.85F);
    NumberAxis localNumberAxis = (NumberAxis)localXYPlot.getRangeAxis();
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    XYBarRenderer localXYBarRenderer = (XYBarRenderer)localXYPlot.getRenderer();
    localXYBarRenderer.setDrawBarOutline(false);
    localXYBarRenderer.setBarPainter(new StandardXYBarPainter());
    localXYBarRenderer.setShadowVisible(false);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setMouseWheelEnabled(true);
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
    throws IOException
  {
    HistogramDemo1 localHistogramDemo1 = new HistogramDemo1("JFreeChart: HistogramDemo1.java");
    localHistogramDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localHistogramDemo1);
    localHistogramDemo1.setVisible(true);
  }
}