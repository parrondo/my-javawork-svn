package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.GridLayout;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.StandardGradientPaintTransformer;

public class GradientPaintTransformerDemo1 extends ApplicationFrame
{
  public GradientPaintTransformerDemo1(String paramString)
  {
    super(paramString);
    setContentPane(createDemoPanel());
  }

  private static JFreeChart createChart(String paramString, CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart(paramString, null, "Value", paramCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    BarRenderer localBarRenderer = (BarRenderer)localCategoryPlot.getRenderer();
    localBarRenderer.setItemMargin(0.02D);
    return localJFreeChart;
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(7.0D, "Series 1", "Category 1");
    localDefaultCategoryDataset.addValue(5.0D, "Series 2", "Category 1");
    return localDefaultCategoryDataset;
  }

  public static JPanel createDemoPanel()
  {
    DemoPanel localDemoPanel = new DemoPanel(new GridLayout(2, 2));
    localDemoPanel.setPreferredSize(new Dimension(800, 600));
    CategoryDataset localCategoryDataset = createDataset();
    JFreeChart localJFreeChart1 = createChart("Type: VERTICAL", localCategoryDataset);
    CategoryPlot localCategoryPlot1 = (CategoryPlot)localJFreeChart1.getPlot();
    BarRenderer localBarRenderer1 = (BarRenderer)localCategoryPlot1.getRenderer();
    localBarRenderer1.setBarPainter(new StandardBarPainter());
    localBarRenderer1.setDrawBarOutline(false);
    localBarRenderer1.setSeriesPaint(0, new GradientPaint(0.0F, 0.0F, Color.red, 0.0F, 0.0F, Color.yellow));
    localBarRenderer1.setSeriesPaint(1, new GradientPaint(0.0F, 0.0F, Color.blue, 0.0F, 0.0F, Color.green));
    localBarRenderer1.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.VERTICAL));
    ChartPanel localChartPanel1 = new ChartPanel(localJFreeChart1);
    localDemoPanel.add(localChartPanel1);
    JFreeChart localJFreeChart2 = createChart("Type: HORIZONTAL", localCategoryDataset);
    CategoryPlot localCategoryPlot2 = (CategoryPlot)localJFreeChart2.getPlot();
    BarRenderer localBarRenderer2 = (BarRenderer)localCategoryPlot2.getRenderer();
    localBarRenderer2.setBarPainter(new StandardBarPainter());
    localBarRenderer2.setDrawBarOutline(false);
    localBarRenderer2.setSeriesPaint(0, new GradientPaint(0.0F, 0.0F, Color.red, 0.0F, 0.0F, Color.yellow));
    localBarRenderer2.setSeriesPaint(1, new GradientPaint(0.0F, 0.0F, Color.blue, 0.0F, 0.0F, Color.green));
    localBarRenderer2.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.HORIZONTAL));
    ChartPanel localChartPanel2 = new ChartPanel(localJFreeChart2);
    localDemoPanel.add(localChartPanel2);
    JFreeChart localJFreeChart3 = createChart("Type: CENTER_VERTICAL", localCategoryDataset);
    CategoryPlot localCategoryPlot3 = (CategoryPlot)localJFreeChart3.getPlot();
    BarRenderer localBarRenderer3 = (BarRenderer)localCategoryPlot3.getRenderer();
    localBarRenderer3.setBarPainter(new StandardBarPainter());
    localBarRenderer3.setDrawBarOutline(false);
    localBarRenderer3.setSeriesPaint(0, new GradientPaint(0.0F, 0.0F, Color.red, 0.0F, 0.0F, Color.yellow));
    localBarRenderer3.setSeriesPaint(1, new GradientPaint(0.0F, 0.0F, Color.blue, 0.0F, 0.0F, Color.green));
    localBarRenderer3.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.CENTER_VERTICAL));
    ChartPanel localChartPanel3 = new ChartPanel(localJFreeChart3);
    localDemoPanel.add(localChartPanel3);
    JFreeChart localJFreeChart4 = createChart("Type: CENTER_HORIZONTAL", localCategoryDataset);
    CategoryPlot localCategoryPlot4 = (CategoryPlot)localJFreeChart4.getPlot();
    BarRenderer localBarRenderer4 = (BarRenderer)localCategoryPlot4.getRenderer();
    localBarRenderer4.setBarPainter(new StandardBarPainter());
    localBarRenderer4.setDrawBarOutline(false);
    localBarRenderer4.setSeriesPaint(0, new GradientPaint(0.0F, 0.0F, Color.red, 0.0F, 0.0F, Color.yellow));
    localBarRenderer4.setSeriesPaint(1, new GradientPaint(0.0F, 0.0F, Color.blue, 0.0F, 0.0F, Color.green));
    localBarRenderer4.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.CENTER_HORIZONTAL));
    ChartPanel localChartPanel4 = new ChartPanel(localJFreeChart4);
    localDemoPanel.add(localChartPanel4);
    localDemoPanel.addChart(localJFreeChart1);
    localDemoPanel.addChart(localJFreeChart2);
    localDemoPanel.addChart(localJFreeChart3);
    localDemoPanel.addChart(localJFreeChart4);
    return localDemoPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    GradientPaintTransformerDemo1 localGradientPaintTransformerDemo1 = new GradientPaintTransformerDemo1("JFreeChart: GradientPaintTransformerDemo1.java");
    localGradientPaintTransformerDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localGradientPaintTransformerDemo1);
    localGradientPaintTransformerDemo1.setVisible(true);
  }
}