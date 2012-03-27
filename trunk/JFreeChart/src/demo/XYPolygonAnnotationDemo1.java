package demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPolygonAnnotation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.Layer;
import org.jfree.ui.RefineryUtilities;

public class XYPolygonAnnotationDemo1 extends ApplicationFrame
{
  public XYPolygonAnnotationDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  public static XYDataset createDataset()
  {
    DefaultXYDataset localDefaultXYDataset = new DefaultXYDataset();
    double[] arrayOfDouble1 = { 1.7D, 2.2D, 2.7D, 3.0D, 3.2D };
    double[] arrayOfDouble2 = { 4.0D, 3.0D, 6.0D, 1.0D, 9.0D };
    double[][] arrayOfDouble3 = { arrayOfDouble1, arrayOfDouble2 };
    localDefaultXYDataset.addSeries("Series 1", arrayOfDouble3);
    double[] arrayOfDouble4 = { 2.1D, 2.2D, 2.4D, 2.7D, 3.2D };
    double[] arrayOfDouble5 = { 4.5D, 6.0D, 4.0D, 8.0D, 5.1D };
    double[][] arrayOfDouble6 = { arrayOfDouble4, arrayOfDouble5 };
    localDefaultXYDataset.addSeries("Series 2", arrayOfDouble6);
    return localDefaultXYDataset;
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createScatterPlot("XYPolygonAnnotationDemo1", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    XYLineAndShapeRenderer localXYLineAndShapeRenderer = (XYLineAndShapeRenderer)localXYPlot.getRenderer();
    XYPolygonAnnotation localXYPolygonAnnotation = new XYPolygonAnnotation(new double[] { 2.0D, 5.0D, 2.5D, 8.0D, 3.0D, 5.0D, 2.5D, 2.0D }, null, null, new Color(200, 200, 255, 100));
    localXYPolygonAnnotation.setToolTipText("Target Zone");
    localXYLineAndShapeRenderer.addAnnotation(localXYPolygonAnnotation, Layer.BACKGROUND);
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
    XYPolygonAnnotationDemo1 localXYPolygonAnnotationDemo1 = new XYPolygonAnnotationDemo1("XYPolygonAnnotationDemo1");
    localXYPolygonAnnotationDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localXYPolygonAnnotationDemo1);
    localXYPolygonAnnotationDemo1.setVisible(true);
  }
}