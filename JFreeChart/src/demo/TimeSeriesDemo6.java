package demo;

import java.awt.Dimension;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class TimeSeriesDemo6 extends ApplicationFrame
{
  public TimeSeriesDemo6(String paramString)
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
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Time Series Demo 6", "Date", "Value", paramXYDataset, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    DateAxis localDateAxis = (DateAxis)localXYPlot.getDomainAxis();
    localDateAxis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
    ValueAxis localValueAxis = localXYPlot.getRangeAxis();
    localValueAxis.setAutoRangeMinimumSize(1.0D);
    return localJFreeChart;
  }

  private static XYDataset createDataset()
  {
    double d = 0.0D;
    TimeSeries localTimeSeries = new TimeSeries("Series 1");
    localTimeSeries.add(new Month(2, 2001), d);
    localTimeSeries.add(new Month(3, 2001), d);
    localTimeSeries.add(new Month(4, 2001), d);
    localTimeSeries.add(new Month(5, 2001), d);
    localTimeSeries.add(new Month(6, 2001), d);
    localTimeSeries.add(new Month(7, 2001), d);
    localTimeSeries.add(new Month(8, 2001), d);
    localTimeSeries.add(new Month(9, 2001), d);
    localTimeSeries.add(new Month(10, 2001), d);
    localTimeSeries.add(new Month(11, 2001), d);
    localTimeSeries.add(new Month(12, 2001), d);
    localTimeSeries.add(new Month(1, 2002), d);
    localTimeSeries.add(new Month(2, 2002), d);
    localTimeSeries.add(new Month(3, 2002), d);
    localTimeSeries.add(new Month(4, 2002), d);
    localTimeSeries.add(new Month(5, 2002), d);
    localTimeSeries.add(new Month(6, 2002), d);
    localTimeSeries.add(new Month(7, 2002), d);
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
    TimeSeriesDemo6 localTimeSeriesDemo6 = new TimeSeriesDemo6("Time Series Demo 6");
    localTimeSeriesDemo6.pack();
    RefineryUtilities.centerFrameOnScreen(localTimeSeriesDemo6);
    localTimeSeriesDemo6.setVisible(true);
  }
}