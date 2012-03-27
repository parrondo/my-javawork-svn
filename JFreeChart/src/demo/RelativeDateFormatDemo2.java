package demo;

import java.awt.Dimension;
import java.text.DecimalFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.util.RelativeDateFormat;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class RelativeDateFormatDemo2 extends ApplicationFrame
{
  public RelativeDateFormatDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(IntervalXYDataset paramIntervalXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYBarChart("RelativeDateFormat Demo 2", "Date ", true, "Time To Complete", paramIntervalXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainCrosshairVisible(true);
    localXYPlot.setRangeCrosshairVisible(true);
    XYBarRenderer localXYBarRenderer = (XYBarRenderer)localXYPlot.getRenderer();
    localXYBarRenderer.setDrawBarOutline(false);
    DateAxis localDateAxis = new DateAxis();
    RelativeDateFormat localRelativeDateFormat = new RelativeDateFormat();
    localRelativeDateFormat.setShowZeroDays(false);
    localRelativeDateFormat.setSecondFormatter(new DecimalFormat("00"));
    localDateAxis.setDateFormatOverride(localRelativeDateFormat);
    localXYPlot.setRangeAxis(localDateAxis);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  private static IntervalXYDataset createDataset()
  {
    TimeSeries localTimeSeries = new TimeSeries("Completion");
    localTimeSeries.add(new Day(19, 1, 2007), 3343000.0D);
    localTimeSeries.add(new Day(20, 1, 2007), 3420000.0D);
    localTimeSeries.add(new Day(21, 1, 2007), 3515000.0D);
    localTimeSeries.add(new Day(22, 1, 2007), 3315000.0D);
    localTimeSeries.add(new Day(23, 1, 2007), 3490000.0D);
    localTimeSeries.add(new Day(24, 1, 2007), 3556000.0D);
    localTimeSeries.add(new Day(25, 1, 2007), 3383000.0D);
    localTimeSeries.add(new Day(26, 1, 2007), 3575000.0D);
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
    RelativeDateFormatDemo2 localRelativeDateFormatDemo2 = new RelativeDateFormatDemo2("JFreeChart: RelativeDateFormatDemo2.java");
    localRelativeDateFormatDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localRelativeDateFormatDemo2);
    localRelativeDateFormatDemo2.setVisible(true);
  }
}