package demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.PeriodAxis;
import org.jfree.chart.axis.PeriodAxisLabelInfo;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class PeriodAxisDemo3 extends ApplicationFrame
{
  public PeriodAxisDemo3(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(IntervalXYDataset paramIntervalXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYBarChart("Maximum Temperature", "Day", true, "Temperature", paramIntervalXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainCrosshairVisible(true);
    localXYPlot.setRangeCrosshairVisible(true);
    PeriodAxis localPeriodAxis = new PeriodAxis("Day");
    localPeriodAxis.setAutoRangeTimePeriodClass(Day.class);
    PeriodAxisLabelInfo[] arrayOfPeriodAxisLabelInfo = new PeriodAxisLabelInfo[3];
    arrayOfPeriodAxisLabelInfo[0] = new PeriodAxisLabelInfo(Day.class, new SimpleDateFormat("d"));
    arrayOfPeriodAxisLabelInfo[1] = new PeriodAxisLabelInfo(Day.class, new SimpleDateFormat("E"), new RectangleInsets(2.0D, 2.0D, 2.0D, 2.0D), new Font("SansSerif", 1, 10), Color.blue, false, new BasicStroke(0.0F), Color.lightGray);
    arrayOfPeriodAxisLabelInfo[2] = new PeriodAxisLabelInfo(Month.class, new SimpleDateFormat("MMM"));
    localPeriodAxis.setLabelInfo(arrayOfPeriodAxisLabelInfo);
    localXYPlot.setDomainAxis(localPeriodAxis);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  private static IntervalXYDataset createDataset()
  {
    TimeSeries localTimeSeries = new TimeSeries("Temperature");
    localTimeSeries.add(new Day(1, 4, 2006), 14.5D);
    localTimeSeries.add(new Day(2, 4, 2006), 11.5D);
    localTimeSeries.add(new Day(3, 4, 2006), 13.699999999999999D);
    localTimeSeries.add(new Day(4, 4, 2006), 10.5D);
    localTimeSeries.add(new Day(5, 4, 2006), 14.9D);
    localTimeSeries.add(new Day(6, 4, 2006), 15.699999999999999D);
    localTimeSeries.add(new Day(7, 4, 2006), 11.5D);
    localTimeSeries.add(new Day(8, 4, 2006), 9.5D);
    localTimeSeries.add(new Day(9, 4, 2006), 10.9D);
    localTimeSeries.add(new Day(10, 4, 2006), 14.1D);
    localTimeSeries.add(new Day(11, 4, 2006), 12.300000000000001D);
    localTimeSeries.add(new Day(12, 4, 2006), 14.300000000000001D);
    localTimeSeries.add(new Day(13, 4, 2006), 19.0D);
    localTimeSeries.add(new Day(14, 4, 2006), 17.899999999999999D);
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
    localTimeSeriesCollection.addSeries(localTimeSeries);
    return localTimeSeriesCollection;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    PeriodAxisDemo3 localPeriodAxisDemo3 = new PeriodAxisDemo3("JFreeChart: PeriodAxisDemo3.java");
    localPeriodAxisDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localPeriodAxisDemo3);
    localPeriodAxisDemo3.setVisible(true);
  }
}