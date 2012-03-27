package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Quarter;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class TimeSeriesDemo2 extends ApplicationFrame
{
  public TimeSeriesDemo2(String paramString)
  {
    super(paramString);
    XYDataset localXYDataset = createDataset();
    JFreeChart localJFreeChart = createChart(localXYDataset);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localChartPanel);
  }

  private static XYDataset createDataset()
  {
    TimeSeries localTimeSeries = new TimeSeries("Quarterly Data");
    localTimeSeries.add(new Quarter(1, 2001), 500.19999999999999D);
    localTimeSeries.add(new Quarter(2, 2001), 694.10000000000002D);
    localTimeSeries.add(new Quarter(3, 2001), 734.39999999999998D);
    localTimeSeries.add(new Quarter(4, 2001), 453.19999999999999D);
    localTimeSeries.add(new Quarter(1, 2002), 500.19999999999999D);
    localTimeSeries.add(new Quarter(2, 2002), null);
    localTimeSeries.add(new Quarter(3, 2002), 734.39999999999998D);
    localTimeSeries.add(new Quarter(4, 2002), 453.19999999999999D);
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection(localTimeSeries);
    return localTimeSeriesCollection;
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Time Series Demo 2", "Time", "Value", paramXYDataset, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.addRangeMarker(new ValueMarker(550.0D));
    Quarter localQuarter = new Quarter(2, 2002);
    localXYPlot.addDomainMarker(new ValueMarker(localQuarter.getMiddleMillisecond()));
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    TimeSeriesDemo2 localTimeSeriesDemo2 = new TimeSeriesDemo2("Time Series Demo 2");
    localTimeSeriesDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localTimeSeriesDemo2);
    localTimeSeriesDemo2.setVisible(true);
  }
}