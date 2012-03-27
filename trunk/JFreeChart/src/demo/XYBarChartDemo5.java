package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;

public class XYBarChartDemo5 extends ApplicationFrame
{
  public XYBarChartDemo5(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 300));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(IntervalXYDataset paramIntervalXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYBarChart("US Budget Deficit", "Year", true, "$ Billion", paramIntervalXYDataset, PlotOrientation.VERTICAL, false, false, false);
    TextTitle localTextTitle = new TextTitle("Source: http://www.cbo.gov/showdoc.cfm?index=1821&sequence=0#table12");
    localTextTitle.setFont(new Font("Dialog", 0, 8));
    localTextTitle.setPosition(RectangleEdge.BOTTOM);
    localTextTitle.setHorizontalAlignment(HorizontalAlignment.RIGHT);
    localJFreeChart.addSubtitle(localTextTitle);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    XYBarRenderer localXYBarRenderer = (XYBarRenderer)localXYPlot.getRenderer();
    localXYBarRenderer.setDrawBarOutline(true);
    localXYBarRenderer.setSeriesOutlinePaint(0, Color.red);
    StandardXYToolTipGenerator localStandardXYToolTipGenerator = new StandardXYToolTipGenerator("{1} = {2}", new SimpleDateFormat("yyyy"), new DecimalFormat("0"));
    localXYBarRenderer.setBaseToolTipGenerator(localStandardXYToolTipGenerator);
    DateAxis localDateAxis = (DateAxis)localXYPlot.getDomainAxis();
    localDateAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
    localDateAxis.setLowerMargin(0.01D);
    localDateAxis.setUpperMargin(0.01D);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  private static IntervalXYDataset createDataset()
  {
    TimeSeries localTimeSeries = new TimeSeries("Budget", "Year", "$ Million");
    try
    {
      localTimeSeries.add(new Year(1980), -74.0D);
      localTimeSeries.add(new Year(1981), -79.0D);
      localTimeSeries.add(new Year(1982), -128.0D);
      localTimeSeries.add(new Year(1983), -208.0D);
      localTimeSeries.add(new Year(1984), -185.0D);
      localTimeSeries.add(new Year(1985), -212.0D);
      localTimeSeries.add(new Year(1986), -221.0D);
      localTimeSeries.add(new Year(1987), -150.0D);
      localTimeSeries.add(new Year(1988), -155.0D);
      localTimeSeries.add(new Year(1989), -153.0D);
      localTimeSeries.add(new Year(1990), -221.0D);
      localTimeSeries.add(new Year(1991), -269.0D);
      localTimeSeries.add(new Year(1992), -290.0D);
      localTimeSeries.add(new Year(1993), -255.0D);
      localTimeSeries.add(new Year(1994), -203.0D);
      localTimeSeries.add(new Year(1995), -164.0D);
      localTimeSeries.add(new Year(1996), -107.0D);
      localTimeSeries.add(new Year(1997), -22.0D);
      localTimeSeries.add(new Year(1998), 69.0D);
      localTimeSeries.add(new Year(1999), 126.0D);
      localTimeSeries.add(new Year(2000), 236.0D);
      localTimeSeries.add(new Year(2001), 128.0D);
      localTimeSeries.add(new Year(2002), -158.0D);
      localTimeSeries.add(new Year(2003), -378.0D);
      localTimeSeries.add(new Year(2004), -412.0D);
    }
    catch (Exception localException)
    {
      System.err.println(localException.getMessage());
    }
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection(localTimeSeries);
    return localTimeSeriesCollection;
  }

  public static JPanel createDemoPanel()
  {
    return new ChartPanel(createChart(createDataset()));
  }

  public static void main(String[] paramArrayOfString)
  {
    XYBarChartDemo5 localXYBarChartDemo5 = new XYBarChartDemo5("US Budget Deficit");
    localXYBarChartDemo5.pack();
    RefineryUtilities.centerFrameOnScreen(localXYBarChartDemo5);
    localXYBarChartDemo5.setVisible(true);
  }
}