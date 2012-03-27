package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.DefaultIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class XYBarChartDemo6 extends ApplicationFrame
{
  public XYBarChartDemo6(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 300));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(IntervalXYDataset paramIntervalXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYBarChart("XYBarChartDemo6", "X", false, "Y", paramIntervalXYDataset, PlotOrientation.HORIZONTAL, false, false, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    XYBarRenderer localXYBarRenderer = (XYBarRenderer)localXYPlot.getRenderer();
    localXYBarRenderer.setUseYInterval(true);
    localXYPlot.setRenderer(localXYBarRenderer);
    return localJFreeChart;
  }

  private static IntervalXYDataset createDataset()
  {
    DefaultIntervalXYDataset localDefaultIntervalXYDataset = new DefaultIntervalXYDataset();
    double[] arrayOfDouble1 = { 1.0D, 2.0D, 3.0D, 4.0D };
    double[] arrayOfDouble2 = { 0.9D, 1.8D, 2.7D, 3.6D };
    double[] arrayOfDouble3 = { 1.1D, 2.2D, 3.3D, 4.4D };
    double[] arrayOfDouble4 = { 1.0D, 2.0D, 3.0D, 4.0D };
    double[] arrayOfDouble5 = { 0.9D, 1.8D, 2.7D, 3.6D };
    double[] arrayOfDouble6 = { 1.1D, 2.2D, 3.3D, 4.4D };
    double[][] arrayOfDouble = { arrayOfDouble1, arrayOfDouble2, arrayOfDouble3, arrayOfDouble4, arrayOfDouble5, arrayOfDouble6 };
    localDefaultIntervalXYDataset.addSeries("Series 1", arrayOfDouble);
    return localDefaultIntervalXYDataset;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setMouseWheelEnabled(true);
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    XYBarChartDemo6 localXYBarChartDemo6 = new XYBarChartDemo6("JFreeChart : XYBarChartDemo6");
    localXYBarChartDemo6.pack();
    RefineryUtilities.centerFrameOnScreen(localXYBarChartDemo6);
    localXYBarChartDemo6.setVisible(true);
  }
}