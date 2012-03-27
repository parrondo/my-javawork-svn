package demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.about.ProjectInfo;

public class TimeSeriesDemo4 extends ApplicationFrame
{
  public TimeSeriesDemo4(String paramString)
  {
    super(paramString);
    XYDataset localXYDataset = createDataset();
    JFreeChart localJFreeChart = createChart(localXYDataset);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    localChartPanel.setMouseZoomable(true);
    setContentPane(localChartPanel);
  }

  private static XYDataset createDataset()
  {
    TimeSeries localTimeSeries = new TimeSeries("Random Data");
    Day localDay = new Day();
    localTimeSeries.add(new Hour(0, localDay), 500.19999999999999D);
    localTimeSeries.add(new Hour(2, localDay), 694.10000000000002D);
    localTimeSeries.add(new Hour(3, localDay), 734.39999999999998D);
    localTimeSeries.add(new Hour(4, localDay), 453.19999999999999D);
    localTimeSeries.add(new Hour(7, localDay), 500.19999999999999D);
    localTimeSeries.add(new Hour(8, localDay), null);
    localTimeSeries.add(new Hour(12, localDay), 734.39999999999998D);
    localTimeSeries.add(new Hour(16, localDay), 453.19999999999999D);
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection(localTimeSeries);
    return localTimeSeriesCollection;
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    String str = "₢₢₣₤₥₦₧₨₩₪";
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart(str, "Time", "Value", paramXYDataset, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setInsets(new RectangleInsets(0.0D, 0.0D, 0.0D, 20.0D));
    ValueMarker localValueMarker = new ValueMarker(700.0D);
    localValueMarker.setPaint(Color.blue);
    localValueMarker.setAlpha(0.8F);
    localXYPlot.addRangeMarker(localValueMarker);
    localXYPlot.setBackgroundPaint(null);
    localXYPlot.setBackgroundImage(JFreeChart.INFO.getLogo());
    localXYPlot.getDomainAxis().setLowerMargin(0.0D);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    TimeSeriesDemo4 localTimeSeriesDemo4 = new TimeSeriesDemo4("Time Series Demo 4");
    localTimeSeriesDemo4.pack();
    RefineryUtilities.centerFrameOnScreen(localTimeSeriesDemo4);
    localTimeSeriesDemo4.setVisible(true);
  }
}