package demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class XYErrorRendererDemo1 extends ApplicationFrame
{
  public XYErrorRendererDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(IntervalXYDataset paramIntervalXYDataset)
  {
    NumberAxis localNumberAxis1 = new NumberAxis("X");
    NumberAxis localNumberAxis2 = new NumberAxis("Y");
    XYErrorRenderer localXYErrorRenderer = new XYErrorRenderer();
    XYPlot localXYPlot = new XYPlot(paramIntervalXYDataset, localNumberAxis1, localNumberAxis2, localXYErrorRenderer);
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    localXYPlot.setBackgroundPaint(Color.lightGray);
    localXYPlot.setDomainGridlinePaint(Color.white);
    localXYPlot.setRangeGridlinePaint(Color.white);
    JFreeChart localJFreeChart = new JFreeChart("XYErrorRenderer Demo 1", localXYPlot);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  private static IntervalXYDataset createDataset()
  {
    XYIntervalSeriesCollection localXYIntervalSeriesCollection = new XYIntervalSeriesCollection();
    XYIntervalSeries localXYIntervalSeries1 = new XYIntervalSeries("Series 1");
    localXYIntervalSeries1.add(1.0D, 0.5D, 1.5D, 10.0D, 9.0D, 11.0D);
    localXYIntervalSeries1.add(10.0D, 8.699999999999999D, 11.210000000000001D, 6.1D, 4.34D, 7.54D);
    localXYIntervalSeries1.add(17.800000000000001D, 16.0D, 18.899999999999999D, 4.5D, 3.1D, 5.8D);
    XYIntervalSeries localXYIntervalSeries2 = new XYIntervalSeries("Series 2");
    localXYIntervalSeries2.add(3.0D, 2.5D, 3.5D, 7.0D, 6.0D, 8.0D);
    localXYIntervalSeries2.add(13.0D, 11.5D, 14.5D, 13.0D, 11.5D, 14.5D);
    localXYIntervalSeries2.add(24.0D, 22.699999999999999D, 25.210000000000001D, 16.100000000000001D, 14.34D, 17.539999999999999D);
    localXYIntervalSeriesCollection.addSeries(localXYIntervalSeries1);
    localXYIntervalSeriesCollection.addSeries(localXYIntervalSeries2);
    return localXYIntervalSeriesCollection;
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
    XYErrorRendererDemo1 localXYErrorRendererDemo1 = new XYErrorRendererDemo1("JFreeChart: XYErrorRendererDemo1.java");
    localXYErrorRendererDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localXYErrorRendererDemo1);
    localXYErrorRendererDemo1.setVisible(true);
  }
}