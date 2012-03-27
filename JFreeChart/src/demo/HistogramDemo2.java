package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.SimpleHistogramBin;
import org.jfree.data.statistics.SimpleHistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class HistogramDemo2 extends ApplicationFrame
{
  public HistogramDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static IntervalXYDataset createDataset()
  {
    SimpleHistogramDataset localSimpleHistogramDataset = new SimpleHistogramDataset("Series 1");
    SimpleHistogramBin localSimpleHistogramBin1 = new SimpleHistogramBin(0.0D, 1.0D, true, false);
    SimpleHistogramBin localSimpleHistogramBin2 = new SimpleHistogramBin(1.0D, 2.0D, true, false);
    SimpleHistogramBin localSimpleHistogramBin3 = new SimpleHistogramBin(2.0D, 3.0D, true, false);
    SimpleHistogramBin localSimpleHistogramBin4 = new SimpleHistogramBin(3.0D, 4.0D, true, true);
    localSimpleHistogramBin1.setItemCount(1);
    localSimpleHistogramBin2.setItemCount(10);
    localSimpleHistogramBin3.setItemCount(15);
    localSimpleHistogramBin4.setItemCount(20);
    localSimpleHistogramDataset.addBin(localSimpleHistogramBin1);
    localSimpleHistogramDataset.addBin(localSimpleHistogramBin2);
    localSimpleHistogramDataset.addBin(localSimpleHistogramBin3);
    localSimpleHistogramDataset.addBin(localSimpleHistogramBin4);
    return localSimpleHistogramDataset;
  }

  private static JFreeChart createChart(IntervalXYDataset paramIntervalXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createHistogram("HistogramDemo2", null, null, paramIntervalXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setForegroundAlpha(0.85F);
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    NumberAxis localNumberAxis = (NumberAxis)localXYPlot.getRangeAxis();
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    XYBarRenderer localXYBarRenderer = (XYBarRenderer)localXYPlot.getRenderer();
    localXYBarRenderer.setDrawBarOutline(false);
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
    HistogramDemo2 localHistogramDemo2 = new HistogramDemo2("JFreeChart: HistogramDemo2.java");
    localHistogramDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localHistogramDemo2);
    localHistogramDemo2.setVisible(true);
  }
}