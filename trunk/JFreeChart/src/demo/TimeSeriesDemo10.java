package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class TimeSeriesDemo10 extends ApplicationFrame
{
  public TimeSeriesDemo10(String paramString)
  {
    super(paramString);
    XYDataset localXYDataset = createDataset();
    JFreeChart localJFreeChart = createChart(localXYDataset);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localChartPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Time Series Demo 10", "Time", "Value", paramXYDataset, true, true, false);
    return localJFreeChart;
  }

  private static XYDataset createDataset()
  {
    TimeSeries localTimeSeries = new TimeSeries("Per Minute Data");
    Hour localHour = new Hour();
    localTimeSeries.add(new Minute(1, localHour), 10.199999999999999D);
    localTimeSeries.add(new Minute(3, localHour), 17.300000000000001D);
    localTimeSeries.add(new Minute(9, localHour), 14.6D);
    localTimeSeries.add(new Minute(11, localHour), 11.9D);
    localTimeSeries.add(new Minute(15, localHour), 13.5D);
    localTimeSeries.add(new Minute(19, localHour), 10.9D);
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection(localTimeSeries);
    return localTimeSeriesCollection;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    TimeSeriesDemo10 localTimeSeriesDemo10 = new TimeSeriesDemo10("Time Series Demo 10");
    localTimeSeriesDemo10.pack();
    RefineryUtilities.centerFrameOnScreen(localTimeSeriesDemo10);
    localTimeSeriesDemo10.setVisible(true);
  }
}