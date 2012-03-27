package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class XYBarChartDemo4 extends ApplicationFrame
{
  public XYBarChartDemo4(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 300));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(IntervalXYDataset paramIntervalXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYBarChart("XYBarChartDemo4", "X", false, "Y", paramIntervalXYDataset, PlotOrientation.VERTICAL, true, false, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    NumberAxis localNumberAxis = (NumberAxis)localXYPlot.getDomainAxis();
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    XYBarRenderer localXYBarRenderer = (XYBarRenderer)localXYPlot.getRenderer();
    localXYBarRenderer.setDrawBarOutline(false);
    return localJFreeChart;
  }

  private static IntervalXYDataset createDataset()
  {
    XYSeries localXYSeries = new XYSeries("Series 1");
    localXYSeries.add(1.0D, 5.0D);
    localXYSeries.add(2.0D, 70.799999999999997D);
    localXYSeries.add(3.0D, 48.299999999999997D);
    XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection();
    localXYSeriesCollection.addSeries(localXYSeries);
    return new XYBarDataset(localXYSeriesCollection, 0.9D);
  }

  public static JPanel createDemoPanel()
  {
    return new ChartPanel(createChart(createDataset()));
  }

  public static void main(String[] paramArrayOfString)
  {
    XYBarChartDemo4 localXYBarChartDemo4 = new XYBarChartDemo4("XY Bar Chart Demo 4");
    localXYBarChartDemo4.pack();
    RefineryUtilities.centerFrameOnScreen(localXYBarChartDemo4);
    localXYBarChartDemo4.setVisible(true);
  }
}