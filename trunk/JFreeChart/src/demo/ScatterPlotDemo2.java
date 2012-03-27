package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class ScatterPlotDemo2 extends ApplicationFrame
{
  public ScatterPlotDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createScatterPlot("Scatter Plot Demo 2", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainCrosshairVisible(true);
    localXYPlot.setDomainCrosshairLockedOnData(true);
    localXYPlot.setRangeCrosshairVisible(true);
    localXYPlot.setRangeCrosshairLockedOnData(true);
    localXYPlot.setDomainZeroBaselineVisible(true);
    localXYPlot.setRangeZeroBaselineVisible(true);
    XYDotRenderer localXYDotRenderer = new XYDotRenderer();
    localXYDotRenderer.setDotWidth(2);
    localXYDotRenderer.setDotHeight(2);
    localXYPlot.setRenderer(localXYDotRenderer);
    NumberAxis localNumberAxis = (NumberAxis)localXYPlot.getDomainAxis();
    localNumberAxis.setAutoRangeIncludesZero(false);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(new SampleXYDataset2());
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setMouseWheelEnabled(true);
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    ScatterPlotDemo2 localScatterPlotDemo2 = new ScatterPlotDemo2("JFreeChart: ScatterPlotDemo2.java");
    localScatterPlotDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localScatterPlotDemo2);
    localScatterPlotDemo2.setVisible(true);
  }
}