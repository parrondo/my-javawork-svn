package demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.PeriodAxis;
import org.jfree.chart.axis.PeriodAxisLabelInfo;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class PeriodAxisDemo2 extends ApplicationFrame
{
  public PeriodAxisDemo2(String paramString)
  {
    super(paramString);
    XYDataset localXYDataset = createDataset();
    JFreeChart localJFreeChart = createChart(localXYDataset);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    localChartPanel.setMouseZoomable(true, true);
    setContentPane(localChartPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Legal & General Unit Trust Prices", "Date", "Price Per Unit", paramXYDataset, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainCrosshairVisible(true);
    localXYPlot.setRangeCrosshairVisible(true);
    XYItemRenderer localXYItemRenderer = localXYPlot.getRenderer();
    if (localXYItemRenderer instanceof XYLineAndShapeRenderer)
    {
      XYLineAndShapeRenderer localObject = (XYLineAndShapeRenderer)localXYItemRenderer;
      ((XYLineAndShapeRenderer)localObject).setBaseShapesVisible(true);
      ((XYLineAndShapeRenderer)localObject).setBaseShapesFilled(true);
      ((XYLineAndShapeRenderer)localObject).setBaseItemLabelsVisible(true);
    }
    Object localObject = new PeriodAxis("Date");
    ((PeriodAxis)localObject).setTimeZone(TimeZone.getTimeZone("Pacific/Auckland"));
    ((PeriodAxis)localObject).setAutoRangeTimePeriodClass(Day.class);
    PeriodAxisLabelInfo[] arrayOfPeriodAxisLabelInfo = new PeriodAxisLabelInfo[3];
    arrayOfPeriodAxisLabelInfo[0] = new PeriodAxisLabelInfo(Day.class, new SimpleDateFormat("d"));
    arrayOfPeriodAxisLabelInfo[1] = new PeriodAxisLabelInfo(Month.class, new SimpleDateFormat("MMM"), new RectangleInsets(2.0D, 2.0D, 2.0D, 2.0D), new Font("SansSerif", 1, 10), Color.blue, false, new BasicStroke(0.0F), Color.lightGray);
    arrayOfPeriodAxisLabelInfo[2] = new PeriodAxisLabelInfo(Year.class, new SimpleDateFormat("yyyy"));
    ((PeriodAxis)localObject).setLabelInfo(arrayOfPeriodAxisLabelInfo);
    localXYPlot.setDomainAxis((ValueAxis)localObject);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return ((JFreeChart)localJFreeChart);
  }

  private static XYDataset createDataset()
  {
    TimeSeries localTimeSeries = new TimeSeries("L&G European Index Trust");
    localTimeSeries.add(new Day(24, 1, 2004), 181.80000000000001D);
    localTimeSeries.add(new Day(25, 1, 2004), 167.30000000000001D);
    localTimeSeries.add(new Day(26, 1, 2004), 153.80000000000001D);
    localTimeSeries.add(new Day(27, 1, 2004), 167.59999999999999D);
    localTimeSeries.add(new Day(28, 1, 2004), 158.80000000000001D);
    localTimeSeries.add(new Day(29, 1, 2004), 148.30000000000001D);
    localTimeSeries.add(new Day(30, 1, 2004), 153.90000000000001D);
    localTimeSeries.add(new Day(31, 1, 2004), 142.69999999999999D);
    localTimeSeries.add(new Day(1, 2, 2004), 123.2D);
    localTimeSeries.add(new Day(2, 2, 2004), 131.80000000000001D);
    localTimeSeries.add(new Day(3, 2, 2004), 139.59999999999999D);
    localTimeSeries.add(new Day(4, 2, 2004), 142.90000000000001D);
    localTimeSeries.add(new Day(5, 2, 2004), 138.69999999999999D);
    localTimeSeries.add(new Day(6, 2, 2004), 137.30000000000001D);
    localTimeSeries.add(new Day(7, 2, 2004), 143.90000000000001D);
    localTimeSeries.add(new Day(8, 2, 2004), 139.80000000000001D);
    localTimeSeries.add(new Day(9, 2, 2004), 137.0D);
    localTimeSeries.add(new Day(10, 2, 2004), 132.80000000000001D);
    TimeZone localTimeZone = TimeZone.getTimeZone("Pacific/Auckland");
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection(localTimeZone);
    localTimeSeriesCollection.addSeries(localTimeSeries);
    localTimeSeriesCollection.setXPosition(TimePeriodAnchor.MIDDLE);
    return localTimeSeriesCollection;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    PeriodAxisDemo2 localPeriodAxisDemo2 = new PeriodAxisDemo2("JFreeChart: PeriodAxisDemo2.java");
    localPeriodAxisDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localPeriodAxisDemo2);
    localPeriodAxisDemo2.setVisible(true);
  }
}