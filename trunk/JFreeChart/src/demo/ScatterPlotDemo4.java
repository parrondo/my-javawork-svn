package demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class ScatterPlotDemo4 extends ApplicationFrame
{
  public ScatterPlotDemo4(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  public static JPanel createDemoPanel()
  {
    SampleXYDataset2 localSampleXYDataset2 = new SampleXYDataset2();
    JFreeChart localJFreeChart = ChartFactory.createScatterPlot("Scatter Plot Demo 4", "X", "Y", localSampleXYDataset2, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setRangeTickBandPaint(new Color(200, 200, 100, 100));
    XYDotRenderer localXYDotRenderer = new XYDotRenderer();
    localXYDotRenderer.setDotWidth(4);
    localXYDotRenderer.setDotHeight(4);
    localXYPlot.setRenderer(localXYDotRenderer);
    localXYPlot.setDomainCrosshairVisible(true);
    localXYPlot.setRangeCrosshairVisible(true);
    NumberAxis localNumberAxis = (NumberAxis)localXYPlot.getDomainAxis();
    localNumberAxis.setAutoRangeIncludesZero(false);
    localXYPlot.getRangeAxis().setInverted(true);
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    ScatterPlotDemo4 localScatterPlotDemo4 = new ScatterPlotDemo4("JFreeChart: ScatterPlotDemo4.java");
    localScatterPlotDemo4.pack();
    RefineryUtilities.centerFrameOnScreen(localScatterPlotDemo4);
    localScatterPlotDemo4.setVisible(true);
  }
}