package demo;

import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class OverlaidXYPlotDemo2 extends ApplicationFrame
{
  public OverlaidXYPlotDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart()
  {
    DateAxis localDateAxis = new DateAxis("Date");
    localDateAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
    NumberAxis localNumberAxis1 = new NumberAxis("Value");
    IntervalXYDataset localIntervalXYDataset = createDataset1();
    XYBarRenderer localXYBarRenderer = new XYBarRenderer(0.2D);
    localXYBarRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}: ({1}, {2})", new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00")));
    XYPlot localXYPlot = new XYPlot(localIntervalXYDataset, localDateAxis, localNumberAxis1, localXYBarRenderer);
    NumberAxis localNumberAxis2 = new NumberAxis("Value 2");
    localXYPlot.setRangeAxis(1, localNumberAxis2);
    XYDataset localXYDataset1 = createDataset2A();
    StandardXYItemRenderer localStandardXYItemRenderer = new StandardXYItemRenderer();
    localStandardXYItemRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}: ({1}, {2})", new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00")));
    localXYPlot.setDataset(1, localXYDataset1);
    localXYPlot.setRenderer(1, localStandardXYItemRenderer);
    XYDataset localXYDataset2 = createDataset2B();
    localXYPlot.setDataset(2, localXYDataset2);
    localXYPlot.setRenderer(2, new StandardXYItemRenderer());
    localXYPlot.mapDatasetToRangeAxis(2, 1);
    localXYPlot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
    localXYPlot.setOrientation(PlotOrientation.VERTICAL);
    JFreeChart localJFreeChart = new JFreeChart("Overlaid XYPlot Demo 2", JFreeChart.DEFAULT_TITLE_FONT, localXYPlot, true);
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

  private static XYDataset createDataset2A()
  {
    TimeSeries localTimeSeries = new TimeSeries("Series 2");
    localTimeSeries.add(new Day(3, 3, 2002), 16853.200000000001D);
    localTimeSeries.add(new Day(4, 3, 2002), 19642.299999999999D);
    localTimeSeries.add(new Day(5, 3, 2002), 18253.5D);
    localTimeSeries.add(new Day(6, 3, 2002), 15352.299999999999D);
    localTimeSeries.add(new Day(7, 3, 2002), 13532.0D);
    localTimeSeries.add(new Day(8, 3, 2002), 12635.299999999999D);
    localTimeSeries.add(new Day(9, 3, 2002), 13998.200000000001D);
    localTimeSeries.add(new Day(10, 3, 2002), 11943.200000000001D);
    localTimeSeries.add(new Day(11, 3, 2002), 16943.900000000001D);
    localTimeSeries.add(new Day(12, 3, 2002), 17843.200000000001D);
    localTimeSeries.add(new Day(13, 3, 2002), 16495.299999999999D);
    localTimeSeries.add(new Day(14, 3, 2002), 17943.599999999999D);
    localTimeSeries.add(new Day(15, 3, 2002), 18500.700000000001D);
    localTimeSeries.add(new Day(16, 3, 2002), 19595.900000000001D);
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection(localTimeSeries);
    localTimeSeriesCollection.setXPosition(TimePeriodAnchor.MIDDLE);
    return localTimeSeriesCollection;
  }

  private static XYDataset createDataset2B()
  {
    TimeSeries localTimeSeries = new TimeSeries("Series 2B");
    localTimeSeries.add(new Day(3, 3, 2002), 43.899999999999999D);
    localTimeSeries.add(new Day(4, 3, 2002), 72.599999999999994D);
    localTimeSeries.add(new Day(5, 3, 2002), 89.400000000000006D);
    localTimeSeries.add(new Day(6, 3, 2002), 23.800000000000001D);
    localTimeSeries.add(new Day(7, 3, 2002), 45.0D);
    localTimeSeries.add(new Day(8, 3, 2002), 65.799999999999997D);
    localTimeSeries.add(new Day(9, 3, 2002), 92.099999999999994D);
    localTimeSeries.add(new Day(10, 3, 2002), 84.700000000000003D);
    localTimeSeries.add(new Day(11, 3, 2002), 77.200000000000003D);
    localTimeSeries.add(new Day(12, 3, 2002), 65.099999999999994D);
    localTimeSeries.add(new Day(13, 3, 2002), 78.5D);
    localTimeSeries.add(new Day(14, 3, 2002), 75.299999999999997D);
    localTimeSeries.add(new Day(15, 3, 2002), 69.900000000000006D);
    localTimeSeries.add(new Day(20, 3, 2002), 56.600000000000001D);
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection(localTimeSeries);
    localTimeSeriesCollection.setXPosition(TimePeriodAnchor.MIDDLE);
    return localTimeSeriesCollection;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart();
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    OverlaidXYPlotDemo2 localOverlaidXYPlotDemo2 = new OverlaidXYPlotDemo2("JFreeChart : OverlaidXYPlotDemo2.java");
    localOverlaidXYPlotDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localOverlaidXYPlotDemo2);
    localOverlaidXYPlotDemo2.setVisible(true);
  }
}