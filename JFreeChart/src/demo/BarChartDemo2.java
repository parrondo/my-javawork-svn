package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategorySeriesLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.StandardGradientPaintTransformer;

public class BarChartDemo2 extends ApplicationFrame
{
  public BarChartDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    double[][] arrayOfDouble = { { 1.0D, 43.0D, 35.0D, 58.0D, 54.0D, 77.0D, 71.0D, 89.0D }, { 54.0D, 75.0D, 63.0D, 83.0D, 43.0D, 46.0D, 27.0D, 13.0D }, { 41.0D, 33.0D, 22.0D, 34.0D, 62.0D, 32.0D, 42.0D, 34.0D } };
    return DatasetUtilities.createCategoryDataset("Series ", "Factor ", arrayOfDouble);
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart("Bar Chart Demo 2", "Category", "Score (%)", paramCategoryDataset, PlotOrientation.HORIZONTAL, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setRange(0.0D, 100.0D);
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    BarRenderer localBarRenderer = (BarRenderer)localCategoryPlot.getRenderer();
    GradientPaint localGradientPaint1 = new GradientPaint(0.0F, 0.0F, new Color(0, 0, 128), 0.0F, 0.0F, Color.blue);
    GradientPaint localGradientPaint2 = new GradientPaint(0.0F, 0.0F, new Color(0, 128, 0), 0.0F, 0.0F, Color.green);
    GradientPaint localGradientPaint3 = new GradientPaint(0.0F, 0.0F, new Color(128, 0, 0), 0.0F, 0.0F, Color.red);
    localBarRenderer.setSeriesPaint(0, localGradientPaint1);
    localBarRenderer.setSeriesPaint(1, localGradientPaint2);
    localBarRenderer.setSeriesPaint(2, localGradientPaint3);
    localBarRenderer.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.HORIZONTAL));
    localBarRenderer.setDrawBarOutline(false);
    localBarRenderer.setLegendItemToolTipGenerator(new StandardCategorySeriesLabelGenerator("Tooltip: {0}"));
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    BarChartDemo2 localBarChartDemo2 = new BarChartDemo2("JFreeChart: BarChartDemo2.java");
    localBarChartDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localBarChartDemo2);
    localBarChartDemo2.setVisible(true);
  }
}