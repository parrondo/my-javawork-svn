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
import org.jfree.data.time.Month;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class PeriodAxisDemo1 extends ApplicationFrame
{
  public PeriodAxisDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
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
    ((PeriodAxis)localObject).setAutoRangeTimePeriodClass(Month.class);
    ((PeriodAxis)localObject).setMajorTickTimePeriodClass(Month.class);
    PeriodAxisLabelInfo[] arrayOfPeriodAxisLabelInfo = new PeriodAxisLabelInfo[2];
    arrayOfPeriodAxisLabelInfo[0] = new PeriodAxisLabelInfo(Month.class, new SimpleDateFormat("MMM"), new RectangleInsets(2.0D, 2.0D, 2.0D, 2.0D), new Font("SansSerif", 1, 10), Color.blue, false, new BasicStroke(0.0F), Color.lightGray);
    arrayOfPeriodAxisLabelInfo[1] = new PeriodAxisLabelInfo(Year.class, new SimpleDateFormat("yyyy"));
    ((PeriodAxis)localObject).setLabelInfo(arrayOfPeriodAxisLabelInfo);
    localXYPlot.setDomainAxis((ValueAxis)localObject);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return ((JFreeChart)localJFreeChart);
  }

  private static XYDataset createDataset()
  {
    TimeSeries localTimeSeries1 = new TimeSeries("L&G European Index Trust");
    localTimeSeries1.add(new Month(2, 2001), 181.80000000000001D);
    localTimeSeries1.add(new Month(3, 2001), 167.30000000000001D);
    localTimeSeries1.add(new Month(4, 2001), 153.80000000000001D);
    localTimeSeries1.add(new Month(5, 2001), 167.59999999999999D);
    localTimeSeries1.add(new Month(6, 2001), 158.80000000000001D);
    localTimeSeries1.add(new Month(7, 2001), 148.30000000000001D);
    localTimeSeries1.add(new Month(8, 2001), 153.90000000000001D);
    localTimeSeries1.add(new Month(9, 2001), 142.69999999999999D);
    localTimeSeries1.add(new Month(10, 2001), 123.2D);
    localTimeSeries1.add(new Month(11, 2001), 131.80000000000001D);
    localTimeSeries1.add(new Month(12, 2001), 139.59999999999999D);
    localTimeSeries1.add(new Month(1, 2002), 142.90000000000001D);
    localTimeSeries1.add(new Month(2, 2002), 138.69999999999999D);
    localTimeSeries1.add(new Month(3, 2002), 137.30000000000001D);
    localTimeSeries1.add(new Month(4, 2002), 143.90000000000001D);
    localTimeSeries1.add(new Month(5, 2002), 139.80000000000001D);
    localTimeSeries1.add(new Month(6, 2002), 137.0D);
    localTimeSeries1.add(new Month(7, 2002), 132.80000000000001D);
    TimeSeries localTimeSeries2 = new TimeSeries("L&G UK Index Trust");
    localTimeSeries2.add(new Month(2, 2001), 129.59999999999999D);
    localTimeSeries2.add(new Month(3, 2001), 123.2D);
    localTimeSeries2.add(new Month(4, 2001), 117.2D);
    localTimeSeries2.add(new Month(5, 2001), 124.09999999999999D);
    localTimeSeries2.add(new Month(6, 2001), 122.59999999999999D);
    localTimeSeries2.add(new Month(7, 2001), 119.2D);
    localTimeSeries2.add(new Month(8, 2001), 116.5D);
    localTimeSeries2.add(new Month(9, 2001), 112.7D);
    localTimeSeries2.add(new Month(10, 2001), 101.5D);
    localTimeSeries2.add(new Month(11, 2001), 106.09999999999999D);
    localTimeSeries2.add(new Month(12, 2001), 110.3D);
    localTimeSeries2.add(new Month(1, 2002), 111.7D);
    localTimeSeries2.add(new Month(2, 2002), 111.0D);
    localTimeSeries2.add(new Month(3, 2002), 109.59999999999999D);
    localTimeSeries2.add(new Month(4, 2002), 113.2D);
    localTimeSeries2.add(new Month(5, 2002), 111.59999999999999D);
    localTimeSeries2.add(new Month(6, 2002), 108.8D);
    localTimeSeries2.add(new Month(7, 2002), 101.59999999999999D);
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
    localTimeSeriesCollection.addSeries(localTimeSeries1);
    localTimeSeriesCollection.addSeries(localTimeSeries2);
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
    PeriodAxisDemo1 localPeriodAxisDemo1 = new PeriodAxisDemo1("JFreeChart: PeriodAxisDemo1.java");
    localPeriodAxisDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localPeriodAxisDemo1);
    localPeriodAxisDemo1.setVisible(true);
  }
}