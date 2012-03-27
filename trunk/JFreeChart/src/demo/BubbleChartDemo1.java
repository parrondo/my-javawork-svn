package demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class BubbleChartDemo1 extends ApplicationFrame
{
  public BubbleChartDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYZDataset paramXYZDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBubbleChart("Bubble Chart Demo 1", "X", "Y", paramXYZDataset, PlotOrientation.HORIZONTAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setForegroundAlpha(0.65F);
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    XYItemRenderer localXYItemRenderer = localXYPlot.getRenderer();
    localXYItemRenderer.setSeriesPaint(0, Color.blue);
    NumberAxis localNumberAxis1 = (NumberAxis)localXYPlot.getDomainAxis();
    localNumberAxis1.setLowerMargin(0.15D);
    localNumberAxis1.setUpperMargin(0.15D);
    NumberAxis localNumberAxis2 = (NumberAxis)localXYPlot.getRangeAxis();
    localNumberAxis2.setLowerMargin(0.15D);
    localNumberAxis2.setUpperMargin(0.15D);
    return localJFreeChart;
  }

  public static XYZDataset createDataset()
  {
    DefaultXYZDataset localDefaultXYZDataset = new DefaultXYZDataset();
    double[] arrayOfDouble1 = { 2.1D, 2.3D, 2.3D, 2.2D, 2.2D, 1.8D, 1.8D, 1.9D, 2.3D, 3.8D };
    double[] arrayOfDouble2 = { 14.1D, 11.1D, 10.0D, 8.800000000000001D, 8.699999999999999D, 8.4D, 5.4D, 4.1D, 4.1D, 25.0D };
    double[] arrayOfDouble3 = { 2.4D, 2.7D, 2.7D, 2.2D, 2.2D, 2.2D, 2.1D, 2.2D, 1.6D, 4.0D };
    double[][] arrayOfDouble = { arrayOfDouble1, arrayOfDouble2, arrayOfDouble3 };
    localDefaultXYZDataset.addSeries("Series 1", arrayOfDouble);
    return localDefaultXYZDataset;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setMouseWheelEnabled(true);
    localChartPanel.setDomainZoomable(true);
    localChartPanel.setRangeZoomable(true);
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    BubbleChartDemo1 localBubbleChartDemo1 = new BubbleChartDemo1("JFreeChart: BubbleChartDemo1.java");
    localBubbleChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localBubbleChartDemo1);
    localBubbleChartDemo1.setVisible(true);
  }
}