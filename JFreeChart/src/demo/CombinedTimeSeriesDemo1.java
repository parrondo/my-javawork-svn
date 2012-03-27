package demo;

import java.awt.Dimension;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class CombinedTimeSeriesDemo1 extends ApplicationFrame
{
  public CombinedTimeSeriesDemo1(String paramString)
  {
    super(paramString);
    setContentPane(createDemoPanel());
  }

  public static JPanel createDemoPanel()
  {
    TimeSeries localTimeSeries1 = new TimeSeries("Annual");
    localTimeSeries1.add(new Year(1998), 80.0D);
    localTimeSeries1.add(new Year(1999), 85.0D);
    localTimeSeries1.add(new Year(2000), 87.599999999999994D);
    TimeSeriesCollection localTimeSeriesCollection1 = new TimeSeriesCollection(localTimeSeries1);
    TimeSeries localTimeSeries2 = new TimeSeries("Monthly A");
    localTimeSeries2.add(new Month(7, 2000), 85.799999999999997D);
    localTimeSeries2.add(new Month(8, 2000), 85.799999999999997D);
    localTimeSeries2.add(new Month(9, 2000), 85.799999999999997D);
    localTimeSeries2.add(new Month(10, 2000), 86.5D);
    localTimeSeries2.add(new Month(11, 2000), 86.5D);
    localTimeSeries2.add(new Month(12, 2000), 86.5D);
    localTimeSeries2.add(new Month(1, 2001), 87.700000000000003D);
    localTimeSeries2.add(new Month(2, 2001), 87.700000000000003D);
    localTimeSeries2.add(new Month(3, 2001), 87.700000000000003D);
    localTimeSeries2.add(new Month(4, 2001), 88.5D);
    localTimeSeries2.add(new Month(5, 2001), 88.5D);
    localTimeSeries2.add(new Month(6, 2001), 88.5D);
    localTimeSeries2.add(new Month(7, 2001), 90.0D);
    localTimeSeries2.add(new Month(8, 2001), 90.0D);
    localTimeSeries2.add(new Month(9, 2001), 90.0D);
    localTimeSeries2.add(new Month(10, 2001), 90.0D);
    localTimeSeries2.add(new Month(11, 2001), 90.0D);
    localTimeSeries2.add(new Month(12, 2001), 90.0D);
    localTimeSeries2.add(new Month(1, 2002), 90.0D);
    localTimeSeries2.add(new Month(2, 2002), 90.0D);
    localTimeSeries2.add(new Month(3, 2002), 90.0D);
    localTimeSeries2.add(new Month(4, 2002), 90.0D);
    localTimeSeries2.add(new Month(5, 2002), 90.0D);
    localTimeSeries2.add(new Month(6, 2002), 90.0D);
    TimeSeries localTimeSeries3 = new TimeSeries("Monthly B");
    localTimeSeries3.add(new Month(7, 2000), 83.799999999999997D);
    localTimeSeries3.add(new Month(8, 2000), 83.799999999999997D);
    localTimeSeries3.add(new Month(9, 2000), 83.799999999999997D);
    localTimeSeries3.add(new Month(10, 2000), 84.5D);
    localTimeSeries3.add(new Month(11, 2000), 84.5D);
    localTimeSeries3.add(new Month(12, 2000), 84.5D);
    localTimeSeries3.add(new Month(1, 2001), 85.700000000000003D);
    localTimeSeries3.add(new Month(2, 2001), 85.700000000000003D);
    localTimeSeries3.add(new Month(3, 2001), 85.700000000000003D);
    localTimeSeries3.add(new Month(4, 2001), 86.5D);
    localTimeSeries3.add(new Month(5, 2001), 86.5D);
    localTimeSeries3.add(new Month(6, 2001), 86.5D);
    localTimeSeries3.add(new Month(7, 2001), 88.0D);
    localTimeSeries3.add(new Month(8, 2001), 88.0D);
    localTimeSeries3.add(new Month(9, 2001), 88.0D);
    localTimeSeries3.add(new Month(10, 2001), 88.0D);
    localTimeSeries3.add(new Month(11, 2001), 88.0D);
    localTimeSeries3.add(new Month(12, 2001), 88.0D);
    localTimeSeries3.add(new Month(1, 2002), 88.0D);
    localTimeSeries3.add(new Month(2, 2002), 88.0D);
    localTimeSeries3.add(new Month(3, 2002), 88.0D);
    localTimeSeries3.add(new Month(4, 2002), 88.0D);
    localTimeSeries3.add(new Month(5, 2002), 88.0D);
    localTimeSeries3.add(new Month(6, 2002), 88.0D);
    TimeSeriesCollection localTimeSeriesCollection2 = new TimeSeriesCollection();
    localTimeSeriesCollection2.addSeries(localTimeSeries2);
    localTimeSeriesCollection2.addSeries(localTimeSeries3);
    TimeSeries localTimeSeries4 = new TimeSeries("XXX");
    localTimeSeries4.add(new Month(7, 2000), 81.5D);
    localTimeSeries4.add(new Month(8, 2000), 86.0D);
    localTimeSeries4.add(new Month(9, 2000), 82.0D);
    localTimeSeries4.add(new Month(10, 2000), 89.5D);
    localTimeSeries4.add(new Month(11, 2000), 88.0D);
    localTimeSeries4.add(new Month(12, 2000), 88.0D);
    localTimeSeries4.add(new Month(1, 2001), 90.0D);
    localTimeSeries4.add(new Month(2, 2001), 89.5D);
    localTimeSeries4.add(new Month(3, 2001), 90.200000000000003D);
    localTimeSeries4.add(new Month(4, 2001), 90.599999999999994D);
    localTimeSeries4.add(new Month(5, 2001), 87.5D);
    localTimeSeries4.add(new Month(6, 2001), 91.0D);
    localTimeSeries4.add(new Month(7, 2001), 89.700000000000003D);
    localTimeSeries4.add(new Month(8, 2001), 87.0D);
    localTimeSeries4.add(new Month(9, 2001), 91.200000000000003D);
    localTimeSeries4.add(new Month(10, 2001), 84.0D);
    localTimeSeries4.add(new Month(11, 2001), 90.0D);
    localTimeSeries4.add(new Month(12, 2001), 92.0D);
    TimeSeriesCollection localTimeSeriesCollection3 = new TimeSeriesCollection(localTimeSeries4);
    XYBarRenderer localXYBarRenderer = new XYBarRenderer(0.2D);
    localXYBarRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0} ({1}, {2})", new SimpleDateFormat("yyyy"), new DecimalFormat("0.00")));
    XYPlot localXYPlot1 = new XYPlot(localTimeSeriesCollection1, new DateAxis("Date"), null, localXYBarRenderer);
    XYAreaRenderer localXYAreaRenderer = new XYAreaRenderer();
    XYPlot localXYPlot2 = new XYPlot(localTimeSeriesCollection2, new DateAxis("Date"), null, localXYAreaRenderer);
    StandardXYItemRenderer localStandardXYItemRenderer = new StandardXYItemRenderer(3);
    localStandardXYItemRenderer.setBaseShapesFilled(true);
    localXYPlot2.setDataset(1, localTimeSeriesCollection3);
    localXYPlot2.setRenderer(1, localStandardXYItemRenderer);
    localXYPlot2.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
    NumberAxis localNumberAxis = new NumberAxis("Value");
    localNumberAxis.setAutoRangeIncludesZero(false);
    CombinedRangeXYPlot localCombinedRangeXYPlot = new CombinedRangeXYPlot(localNumberAxis);
    localCombinedRangeXYPlot.add(localXYPlot1, 1);
    localCombinedRangeXYPlot.add(localXYPlot2, 4);
    JFreeChart localJFreeChart = new JFreeChart("Sample Combined Plot", JFreeChart.DEFAULT_TITLE_FONT, localCombinedRangeXYPlot, true);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    localChartPanel.addChartMouseListener(new ChartMouseListener()
    {
      public void chartMouseClicked(ChartMouseEvent paramChartMouseEvent)
      {
        System.out.println(paramChartMouseEvent.getEntity());
      }

      public void chartMouseMoved(ChartMouseEvent paramChartMouseEvent)
      {
        System.out.println(paramChartMouseEvent.getEntity());
      }
    });
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    CombinedTimeSeriesDemo1 localCombinedTimeSeriesDemo1 = new CombinedTimeSeriesDemo1("JFreeChart: CombinedTimeSeriesDemo1.java");
    localCombinedTimeSeriesDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localCombinedTimeSeriesDemo1);
    localCombinedTimeSeriesDemo1.setVisible(true);
  }
}