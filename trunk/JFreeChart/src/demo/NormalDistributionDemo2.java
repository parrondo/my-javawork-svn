package demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.function.NormalDistributionFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class NormalDistributionDemo2 extends ApplicationFrame
{
  public NormalDistributionDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setMouseWheelEnabled(true);
    return localChartPanel;
  }

  public static XYDataset createDataset()
  {
    XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection();
    NormalDistributionFunction2D localNormalDistributionFunction2D1 = new NormalDistributionFunction2D(0.0D, 1.0D);
    XYSeries localXYSeries1 = DatasetUtilities.sampleFunction2DToSeries(localNormalDistributionFunction2D1, -5.1D, 5.1D, 121, "N1");
    localXYSeriesCollection.addSeries(localXYSeries1);
    NormalDistributionFunction2D localNormalDistributionFunction2D2 = new NormalDistributionFunction2D(0.0D, Math.sqrt(0.2D));
    XYSeries localXYSeries2 = DatasetUtilities.sampleFunction2DToSeries(localNormalDistributionFunction2D2, -5.1D, 5.1D, 121, "N2");
    localXYSeriesCollection.addSeries(localXYSeries2);
    NormalDistributionFunction2D localNormalDistributionFunction2D3 = new NormalDistributionFunction2D(0.0D, Math.sqrt(5.0D));
    XYSeries localXYSeries3 = DatasetUtilities.sampleFunction2DToSeries(localNormalDistributionFunction2D3, -5.1D, 5.1D, 121, "N3");
    localXYSeriesCollection.addSeries(localXYSeries3);
    NormalDistributionFunction2D localNormalDistributionFunction2D4 = new NormalDistributionFunction2D(-2.0D, Math.sqrt(0.5D));
    XYSeries localXYSeries4 = DatasetUtilities.sampleFunction2DToSeries(localNormalDistributionFunction2D4, -5.1D, 5.1D, 121, "N4");
    localXYSeriesCollection.addSeries(localXYSeries4);
    return localXYSeriesCollection;
  }

  public static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYLineChart("Normal Distribution Demo 2", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainZeroBaselineVisible(true);
    localXYPlot.setRangeZeroBaselineVisible(true);
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    ValueAxis localValueAxis = localXYPlot.getDomainAxis();
    localValueAxis.setLowerMargin(0.0D);
    localValueAxis.setUpperMargin(0.0D);
    XYLineAndShapeRenderer localXYLineAndShapeRenderer = (XYLineAndShapeRenderer)localXYPlot.getRenderer();
    localXYLineAndShapeRenderer.setDrawSeriesLineAsPath(true);
    localXYLineAndShapeRenderer.setSeriesStroke(0, new BasicStroke(1.5F));
    localXYLineAndShapeRenderer.setSeriesStroke(1, new BasicStroke(2.0F, 1, 1, 1.0F, new float[] { 6.0F, 4.0F }, 0.0F));
    localXYLineAndShapeRenderer.setSeriesStroke(2, new BasicStroke(2.0F, 1, 1, 1.0F, new float[] { 6.0F, 4.0F, 3.0F, 3.0F }, 0.0F));
    localXYLineAndShapeRenderer.setSeriesStroke(3, new BasicStroke(2.0F, 1, 1, 1.0F, new float[] { 4.0F, 4.0F }, 0.0F));
    XYPointerAnnotation localXYPointerAnnotation1 = new XYPointerAnnotation("μ = -2.0, σ² = 0.5", -2.0D, 0.564D, 3.926990816987241D);
    localXYPointerAnnotation1.setLabelOffset(4.0D);
    localXYPointerAnnotation1.setTextAnchor(TextAnchor.BOTTOM_RIGHT);
    localXYPointerAnnotation1.setBackgroundPaint(Color.yellow);
    localXYPlot.addAnnotation(localXYPointerAnnotation1);
    XYPointerAnnotation localXYPointerAnnotation2 = new XYPointerAnnotation("μ = 0.0, σ² = 0.2", 0.225D, 0.8D, 0.0D);
    localXYPointerAnnotation2.setLabelOffset(4.0D);
    localXYPointerAnnotation2.setTextAnchor(TextAnchor.CENTER_LEFT);
    localXYPointerAnnotation2.setBackgroundPaint(new Color(0, 0, 255, 63));
    localXYPlot.addAnnotation(localXYPointerAnnotation2);
    XYPointerAnnotation localXYPointerAnnotation3 = new XYPointerAnnotation("μ = 0.0, σ² = 1.0", 0.75D, 0.3D, 5.497787143782138D);
    localXYPointerAnnotation3.setLabelOffset(4.0D);
    localXYPointerAnnotation3.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
    localXYPointerAnnotation3.setBackgroundPaint(new Color(255, 0, 0, 63));
    localXYPlot.addAnnotation(localXYPointerAnnotation3);
    XYPointerAnnotation localXYPointerAnnotation4 = new XYPointerAnnotation("μ = 0.0, σ² = 5.0", 3.0D, 0.075D, 4.71238898038469D);
    localXYPointerAnnotation4.setLabelOffset(4.0D);
    localXYPointerAnnotation4.setTextAnchor(TextAnchor.BOTTOM_CENTER);
    localXYPointerAnnotation4.setBackgroundPaint(new Color(0, 255, 0, 63));
    localXYPlot.addAnnotation(localXYPointerAnnotation4);
    return localJFreeChart;
  }

  public static void main(String[] paramArrayOfString)
  {
    NormalDistributionDemo2 localNormalDistributionDemo2 = new NormalDistributionDemo2("JFreeChart: NormalDistributionDemo2.java");
    localNormalDistributionDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localNormalDistributionDemo2);
    localNormalDistributionDemo2.setVisible(true);
  }
}