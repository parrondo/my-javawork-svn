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

public class OverlaidXYPlotDemo1 extends ApplicationFrame
{
  public OverlaidXYPlotDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart()
  {
    IntervalXYDataset localIntervalXYDataset = createDataset1();
    XYBarRenderer localXYBarRenderer = new XYBarRenderer(0.2D);
    localXYBarRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}: ({1}, {2})", new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00")));
    DateAxis localDateAxis = new DateAxis("Date");
    localDateAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
    NumberAxis localNumberAxis = new NumberAxis("Value");
    XYPlot localXYPlot = new XYPlot(localIntervalXYDataset, localDateAxis, localNumberAxis, localXYBarRenderer);
    XYDataset localXYDataset = createDataset2();
    StandardXYItemRenderer localStandardXYItemRenderer = new StandardXYItemRenderer();
    localStandardXYItemRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}: ({1}, {2})", new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00")));
    localXYPlot.setDataset(1, localXYDataset);
    localXYPlot.setRenderer(1, localStandardXYItemRenderer);
    localXYPlot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
    JFreeChart localJFreeChart = new JFreeChart("Overlaid XYPlot Demo 1", JFreeChart.DEFAULT_TITLE_FONT, localXYPlot, true);
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
    return new TimeSeriesCollection(localTimeSeries);
  }

  private static XYDataset createDataset2()
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
    return localTimeSeriesCollection;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart();
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    OverlaidXYPlotDemo1 localOverlaidXYPlotDemo1 = new OverlaidXYPlotDemo1("JFreeChart: OverlaidXYPlotDemo1.java");
    localOverlaidXYPlotDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localOverlaidXYPlotDemo1);
    localOverlaidXYPlotDemo1.setVisible(true);
  }
}