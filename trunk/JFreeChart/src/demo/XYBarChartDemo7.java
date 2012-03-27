package demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class XYBarChartDemo7 extends ApplicationFrame
{
  public XYBarChartDemo7(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 300));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(IntervalXYDataset paramIntervalXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYBarChart("XYBarChartDemo7", "Date", true, "Y", paramIntervalXYDataset, PlotOrientation.HORIZONTAL, true, false, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setRangePannable(true);
    localXYPlot.setRangeAxis(new DateAxis("Date"));
    SymbolAxis localSymbolAxis = new SymbolAxis("Series", new String[] { "S1", "S2", "S3" });
    localSymbolAxis.setGridBandsVisible(false);
    localXYPlot.setDomainAxis(localSymbolAxis);
    XYBarRenderer localXYBarRenderer = (XYBarRenderer)localXYPlot.getRenderer();
    localXYBarRenderer.setUseYInterval(true);
    localXYPlot.setRenderer(localXYBarRenderer);
    localXYPlot.setBackgroundPaint(Color.lightGray);
    localXYPlot.setDomainGridlinePaint(Color.white);
    localXYPlot.setRangeGridlinePaint(Color.white);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  private static IntervalXYDataset createDataset()
  {
    Day localDay1 = new Day(12, 6, 2007);
    Day localDay2 = new Day(13, 6, 2007);
    Day localDay3 = new Day(14, 6, 2007);
    Day localDay4 = new Day(15, 6, 2007);
    Day localDay5 = new Day(16, 6, 2007);
    Day localDay6 = new Day(17, 6, 2007);
    XYIntervalSeriesCollection localXYIntervalSeriesCollection = new XYIntervalSeriesCollection();
    XYIntervalSeries localXYIntervalSeries1 = new XYIntervalSeries("S1");
    XYIntervalSeries localXYIntervalSeries2 = new XYIntervalSeries("S2");
    XYIntervalSeries localXYIntervalSeries3 = new XYIntervalSeries("S3");
    addItem(localXYIntervalSeries1, localDay1, localDay2, 0);
    addItem(localXYIntervalSeries1, localDay4, localDay4, 0);
    addItem(localXYIntervalSeries2, localDay1, localDay6, 1);
    addItem(localXYIntervalSeries3, localDay3, localDay5, 2);
    localXYIntervalSeriesCollection.addSeries(localXYIntervalSeries1);
    localXYIntervalSeriesCollection.addSeries(localXYIntervalSeries2);
    localXYIntervalSeriesCollection.addSeries(localXYIntervalSeries3);
    return localXYIntervalSeriesCollection;
  }

  private static void addItem(XYIntervalSeries paramXYIntervalSeries, RegularTimePeriod paramRegularTimePeriod1, RegularTimePeriod paramRegularTimePeriod2, int paramInt)
  {
    paramXYIntervalSeries.add(paramInt, paramInt - 0.45D, paramInt + 0.45D, paramRegularTimePeriod1.getFirstMillisecond(), paramRegularTimePeriod1.getFirstMillisecond(), paramRegularTimePeriod2.getLastMillisecond());
  }

  public static JPanel createDemoPanel()
  {
    return new ChartPanel(createChart(createDataset()));
  }

  public static void main(String[] paramArrayOfString)
  {
    XYBarChartDemo7 localXYBarChartDemo7 = new XYBarChartDemo7("JFreeChart : XYBarChartDemo7.java");
    localXYBarChartDemo7.pack();
    RefineryUtilities.centerFrameOnScreen(localXYBarChartDemo7);
    localXYBarChartDemo7.setVisible(true);
  }
}