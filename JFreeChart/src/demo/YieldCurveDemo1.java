package demo;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTick;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class YieldCurveDemo1 extends ApplicationFrame
{
  public YieldCurveDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("US$ Treasury Yields", "Date", "Yield", paramXYDataset, false, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    GregorianCalendar localGregorianCalendar = new GregorianCalendar(2005, 10, 15);
    localXYPlot.setDomainAxis(new CustomDateAxis("Date", localGregorianCalendar.getTime()));
    localXYPlot.setDomainCrosshairVisible(true);
    localXYPlot.setRangeCrosshairVisible(true);
    XYItemRenderer localXYItemRenderer = localXYPlot.getRenderer();
    if (localXYItemRenderer instanceof XYLineAndShapeRenderer)
    {
      XYLineAndShapeRenderer localObject = (XYLineAndShapeRenderer)localXYItemRenderer;
      ((XYLineAndShapeRenderer)localObject).setBaseShapesVisible(true);
      ((XYLineAndShapeRenderer)localObject).setBaseShapesFilled(true);
    }
    Object localObject = (DateAxis)localXYPlot.getDomainAxis();
    ((DateAxis)localObject).setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
    localJFreeChart.addSubtitle(new TextTitle("November 2005"));
    TextTitle localTextTitle = new TextTitle("Source: http://www.econstats.com/r/r_am1.htm");
    localTextTitle.setFont(new Font("Dialog", 0, 9));
    localTextTitle.setPosition(RectangleEdge.BOTTOM);
    localTextTitle.setHorizontalAlignment(HorizontalAlignment.RIGHT);
    localJFreeChart.addSubtitle(localTextTitle);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return ((JFreeChart)localJFreeChart);
  }

  private static XYDataset createDataset()
  {
    TimeSeries localTimeSeries = new TimeSeries("US$ Treasury Yields");
    Day localDay1 = new Day(1, 12, 2005);
    Day localDay2 = new Day(1, 2, 2006);
    Day localDay3 = new Day(1, 5, 2006);
    Day localDay4 = new Day(1, 12, 2006);
    Day localDay5 = new Day(1, 12, 2007);
    Day localDay6 = new Day(1, 12, 2008);
    Day localDay7 = new Day(1, 12, 2010);
    Day localDay8 = new Day(1, 12, 2012);
    Day localDay9 = new Day(1, 12, 2015);
    Day localDay10 = new Day(1, 12, 2025);
    localTimeSeries.add(localDay1, 3.79D);
    localTimeSeries.add(localDay2, 3.995D);
    localTimeSeries.add(localDay3, 4.26D);
    localTimeSeries.add(localDay4, 4.3225D);
    localTimeSeries.add(localDay5, 4.4475D);
    localTimeSeries.add(localDay6, 4.475D);
    localTimeSeries.add(localDay7, 4.52D);
    localTimeSeries.add(localDay8, 4.56D);
    localTimeSeries.add(localDay9, 4.625D);
    localTimeSeries.add(localDay10, 4.905D);
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
    YieldCurveDemo1 localYieldCurveDemo1 = new YieldCurveDemo1("JFreeChart: YieldCurveDemo1.java");
    localYieldCurveDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localYieldCurveDemo1);
    localYieldCurveDemo1.setVisible(true);
  }

  static class CustomDateAxis extends DateAxis
  {
    private Date base;

    public CustomDateAxis(String paramString, Date paramDate)
    {
      super(paramString);
      this.base = paramDate;
    }

    public List refreshTicks(Graphics2D paramGraphics2D, AxisState paramAxisState, Rectangle2D paramRectangle2D, RectangleEdge paramRectangleEdge)
    {
      ArrayList localArrayList = new ArrayList();
      GregorianCalendar localGregorianCalendar = new GregorianCalendar();
      localGregorianCalendar.setTime(this.base);
      localGregorianCalendar.add(2, 1);
      localArrayList.add(new DateTick(localGregorianCalendar.getTime(), "1M", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0D));
      localGregorianCalendar.add(2, 5);
      localGregorianCalendar.add(2, 6);
      localArrayList.add(new DateTick(localGregorianCalendar.getTime(), "1Y", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0D));
      localGregorianCalendar.add(1, 1);
      localArrayList.add(new DateTick(localGregorianCalendar.getTime(), "2Y", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0D));
      localGregorianCalendar.add(1, 1);
      localArrayList.add(new DateTick(localGregorianCalendar.getTime(), "3Y", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0D));
      localGregorianCalendar.add(1, 2);
      localArrayList.add(new DateTick(localGregorianCalendar.getTime(), "5Y", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0D));
      localGregorianCalendar.add(1, 5);
      localArrayList.add(new DateTick(localGregorianCalendar.getTime(), "10Y", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0D));
      localGregorianCalendar.add(1, 10);
      localArrayList.add(new DateTick(localGregorianCalendar.getTime(), "20Y", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0D));
      return localArrayList;
    }
  }
}