package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class YIntervalChartDemo2 extends ApplicationFrame
{
  public YIntervalChartDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static void add(YIntervalSeries paramYIntervalSeries, int paramInt1, int paramInt2, int paramInt3, double paramDouble1, double paramDouble2)
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.set(paramInt1, paramInt2, paramInt3);
    paramYIntervalSeries.add(localCalendar.getTime().getTime(), paramDouble1, paramDouble1 - paramDouble2, paramDouble1 + paramDouble2);
  }

  private static IntervalXYDataset createDataset()
  {
    YIntervalSeries localYIntervalSeries = new YIntervalSeries("Series 1");
    add(localYIntervalSeries, 2005, 4, 16, 1309.0D, 13.0D);
    add(localYIntervalSeries, 2005, 9, 18, 1312.0D, 12.0D);
    add(localYIntervalSeries, 2005, 10, 7, 1309.0D, 12.0D);
    add(localYIntervalSeries, 2006, 0, 12, 1311.0D, 12.0D);
    add(localYIntervalSeries, 2006, 1, 7, 1311.0D, 13.0D);
    add(localYIntervalSeries, 2006, 3, 3, 1309.0D, 13.0D);
    add(localYIntervalSeries, 2006, 3, 4, 1307.0D, 13.0D);
    add(localYIntervalSeries, 2006, 3, 6, 1305.0D, 13.0D);
    add(localYIntervalSeries, 2006, 3, 13, 1303.0D, 13.0D);
    add(localYIntervalSeries, 2006, 3, 25, 1308.0D, 13.0D);
    add(localYIntervalSeries, 2006, 3, 28, 1311.0D, 13.0D);
    add(localYIntervalSeries, 2006, 4, 2, 1306.0D, 13.0D);
    add(localYIntervalSeries, 2006, 4, 15, 1303.0D, 13.0D);
    add(localYIntervalSeries, 2006, 4, 18, 1311.0D, 13.0D);
    add(localYIntervalSeries, 2006, 10, 16, 1301.0D, 13.0D);
    YIntervalSeriesCollection localYIntervalSeriesCollection = new YIntervalSeriesCollection();
    localYIntervalSeriesCollection.addSeries(localYIntervalSeries);
    return localYIntervalSeriesCollection;
  }

  private static JFreeChart createChart(IntervalXYDataset paramIntervalXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYLineChart("YIntervalChartDemo2", "Date", "Value", paramIntervalXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    localXYPlot.setDomainAxis(new DateAxis("Date"));
    NumberAxis localNumberAxis = (NumberAxis)localXYPlot.getRangeAxis();
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    localNumberAxis.setAutoRangeIncludesZero(false);
    XYErrorRenderer localXYErrorRenderer = new XYErrorRenderer();
    localXYErrorRenderer.setBaseLinesVisible(true);
    localXYErrorRenderer.setUseFillPaint(true);
    localXYErrorRenderer.setBaseFillPaint(Color.white);
    localXYPlot.setRenderer(localXYErrorRenderer);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setMouseWheelEnabled(true);
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    YIntervalChartDemo2 localYIntervalChartDemo2 = new YIntervalChartDemo2("JFreeChart: YIntervalChartDemo2.java");
    localYIntervalChartDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localYIntervalChartDemo2);
    localYIntervalChartDemo2.setVisible(true);
  }
}