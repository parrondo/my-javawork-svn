package demo;

import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class CombinedXYPlotDemo2 extends ApplicationFrame
{
  public CombinedXYPlotDemo2(String paramString)
  {
    super(paramString);
    JFreeChart localJFreeChart = createCombinedChart();
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart, true, true, true, true, true);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localChartPanel);
  }

  private static JFreeChart createCombinedChart()
  {
    IntervalXYDataset localIntervalXYDataset = createDataset1();
    XYBarRenderer localXYBarRenderer = new XYBarRenderer(0.2D);
    localXYBarRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}: ({1}, {2})", new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0,000.0")));
    XYPlot localXYPlot1 = new XYPlot(localIntervalXYDataset, new DateAxis("Date"), null, localXYBarRenderer);
    XYDataset localXYDataset = createDataset2();
    StandardXYItemRenderer localStandardXYItemRenderer = new StandardXYItemRenderer();
    localStandardXYItemRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}: ({1}, {2})", new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0,000.0")));
    XYPlot localXYPlot2 = new XYPlot(localXYDataset, new DateAxis("Date"), null, localStandardXYItemRenderer);
    NumberAxis localNumberAxis = new NumberAxis("Value");
    localNumberAxis.setTickMarkInsideLength(3.0F);
    CombinedRangeXYPlot localCombinedRangeXYPlot = new CombinedRangeXYPlot(localNumberAxis);
    localCombinedRangeXYPlot.add(localXYPlot1, 1);
    localCombinedRangeXYPlot.add(localXYPlot2, 1);
    JFreeChart localJFreeChart = new JFreeChart("Combined (Range) XY Plot", JFreeChart.DEFAULT_TITLE_FONT, localCombinedRangeXYPlot, true);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  private static IntervalXYDataset createDataset1()
  {
    TimeSeries localTimeSeries = new TimeSeries("Series 1");
    localTimeSeries.add(new Day(1, 3, 2002), 12353.299999999999D);
    localTimeSeries.add(new Day(2, 3, 2002), 13734.4D);
    localTimeSeries.add(new Day(3, 3, 2002), 14525.299999999999D);
    localTimeSeries.add(new Day(4, 3, 2002), 13984.299999999999D);
    localTimeSeries.add(new Day(5, 3, 2002), 12999.4D);
    localTimeSeries.add(new Day(6, 3, 2002), 14274.299999999999D);
    localTimeSeries.add(new Day(7, 3, 2002), 15943.5D);
    localTimeSeries.add(new Day(8, 3, 2002), 14845.299999999999D);
    localTimeSeries.add(new Day(9, 3, 2002), 14645.4D);
    localTimeSeries.add(new Day(10, 3, 2002), 16234.6D);
    localTimeSeries.add(new Day(11, 3, 2002), 17232.299999999999D);
    localTimeSeries.add(new Day(12, 3, 2002), 14232.200000000001D);
    localTimeSeries.add(new Day(13, 3, 2002), 13102.200000000001D);
    localTimeSeries.add(new Day(14, 3, 2002), 14230.200000000001D);
    localTimeSeries.add(new Day(15, 3, 2002), 11235.200000000001D);
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection(localTimeSeries);
    return localTimeSeriesCollection;
  }

  private static XYDataset createDataset2()
  {
    TimeSeries localTimeSeries = new TimeSeries("Series 2");
    localTimeSeries.add(new Day(3, 3, 2002), 6853.1999999999998D);
    localTimeSeries.add(new Day(4, 3, 2002), 9642.2999999999993D);
    localTimeSeries.add(new Day(5, 3, 2002), 8253.5D);
    localTimeSeries.add(new Day(6, 3, 2002), 5352.3000000000002D);
    localTimeSeries.add(new Day(7, 3, 2002), 3532.0D);
    localTimeSeries.add(new Day(8, 3, 2002), 2635.3000000000002D);
    localTimeSeries.add(new Day(9, 3, 2002), 3998.1999999999998D);
    localTimeSeries.add(new Day(10, 3, 2002), 1943.2D);
    localTimeSeries.add(new Day(11, 3, 2002), 6943.8999999999996D);
    localTimeSeries.add(new Day(12, 3, 2002), 7843.1999999999998D);
    localTimeSeries.add(new Day(13, 3, 2002), 6495.3000000000002D);
    localTimeSeries.add(new Day(14, 3, 2002), 7943.6000000000004D);
    localTimeSeries.add(new Day(15, 3, 2002), 8500.7000000000007D);
    localTimeSeries.add(new Day(16, 3, 2002), 9595.8999999999996D);
    return new TimeSeriesCollection(localTimeSeries);
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createCombinedChart();
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    CombinedXYPlotDemo2 localCombinedXYPlotDemo2 = new CombinedXYPlotDemo2("JFreeChart: CombinedXYPlotDemo2.java");
    localCombinedXYPlotDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localCombinedXYPlotDemo2);
    localCombinedXYPlotDemo2.setVisible(true);
  }
}