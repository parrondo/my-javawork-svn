package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class XYBarChartDemo2 extends ApplicationFrame
{
  public XYBarChartDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 300));
    setContentPane(localJPanel);
  }

  private static IntervalXYDataset createDataset()
  {
    TimeSeries localTimeSeries1 = new TimeSeries("Series 1");
    localTimeSeries1.add(new Day(1, 1, 2003), 54.299999999999997D);
    localTimeSeries1.add(new Day(2, 1, 2003), 20.300000000000001D);
    localTimeSeries1.add(new Day(3, 1, 2003), 43.399999999999999D);
    localTimeSeries1.add(new Day(4, 1, 2003), -12.0D);
    TimeSeries localTimeSeries2 = new TimeSeries("Series 2");
    localTimeSeries2.add(new Day(1, 1, 2003), 8.0D);
    localTimeSeries2.add(new Day(2, 1, 2003), 16.0D);
    localTimeSeries2.add(new Day(3, 1, 2003), 21.0D);
    localTimeSeries2.add(new Day(4, 1, 2003), 5.0D);
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
    localTimeSeriesCollection.addSeries(localTimeSeries1);
    localTimeSeriesCollection.addSeries(localTimeSeries2);
    return localTimeSeriesCollection;
  }

  private static JFreeChart createChart(IntervalXYDataset paramIntervalXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYBarChart("XY Bar Chart Demo 2", "Date", true, "Y", paramIntervalXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    ClusteredXYBarRenderer localClusteredXYBarRenderer = new ClusteredXYBarRenderer(0.0D, false);
    localXYPlot.setRenderer(localClusteredXYBarRenderer);
    localClusteredXYBarRenderer.setDrawBarOutline(false);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    return new ChartPanel(createChart(createDataset()));
  }

  public static void main(String[] paramArrayOfString)
  {
    XYBarChartDemo2 localXYBarChartDemo2 = new XYBarChartDemo2("JFreeChart: XYBarChartDemo2.java");
    localXYBarChartDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localXYBarChartDemo2);
    localXYBarChartDemo2.setVisible(true);
  }
}