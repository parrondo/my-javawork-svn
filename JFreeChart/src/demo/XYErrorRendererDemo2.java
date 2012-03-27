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
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class XYErrorRendererDemo2 extends ApplicationFrame
{
  public XYErrorRendererDemo2(String paramString)
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
    localXYErrorRenderer.setBaseLinesVisible(true);
    localXYErrorRenderer.setBaseShapesVisible(false);
    XYPlot localXYPlot = new XYPlot(paramIntervalXYDataset, localNumberAxis1, localNumberAxis2, localXYErrorRenderer);
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    localXYPlot.setBackgroundPaint(Color.lightGray);
    localXYPlot.setDomainGridlinePaint(Color.white);
    localXYPlot.setRangeGridlinePaint(Color.white);
    JFreeChart localJFreeChart = new JFreeChart("XYErrorRenderer Demo 2", localXYPlot);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  private static IntervalXYDataset createDataset()
  {
    YIntervalSeriesCollection localYIntervalSeriesCollection = new YIntervalSeriesCollection();
    YIntervalSeries localYIntervalSeries1 = new YIntervalSeries("Series 1");
    localYIntervalSeries1.add(1.0D, 10.0D, 9.0D, 11.0D);
    localYIntervalSeries1.add(10.0D, 6.1D, 4.34D, 7.54D);
    localYIntervalSeries1.add(17.800000000000001D, 4.5D, 3.1D, 5.8D);
    YIntervalSeries localYIntervalSeries2 = new YIntervalSeries("Series 2");
    localYIntervalSeries2.add(3.0D, 7.0D, 6.0D, 8.0D);
    localYIntervalSeries2.add(13.0D, 13.0D, 11.5D, 14.5D);
    localYIntervalSeries2.add(24.0D, 16.100000000000001D, 14.34D, 17.539999999999999D);
    localYIntervalSeriesCollection.addSeries(localYIntervalSeries1);
    localYIntervalSeriesCollection.addSeries(localYIntervalSeries2);
    return localYIntervalSeriesCollection;
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
    XYErrorRendererDemo2 localXYErrorRendererDemo2 = new XYErrorRendererDemo2("JFreeChart: XYErrorRendererDemo2.java");
    localXYErrorRendererDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localXYErrorRendererDemo2);
    localXYErrorRendererDemo2.setVisible(true);
  }
}