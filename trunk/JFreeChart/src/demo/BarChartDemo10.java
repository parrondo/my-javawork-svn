package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class BarChartDemo10 extends ApplicationFrame
{
  public BarChartDemo10(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static DefaultCategoryDataset createDataset()
  {
    String str1 = "First";
    String str2 = "Second";
    String str3 = "Third";
    String str4 = "Category 1";
    String str5 = "Category 2";
    String str6 = "Category 3";
    String str7 = "Category 4";
    String str8 = "Category 5";
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(31.0D, str1, str4);
    localDefaultCategoryDataset.addValue(44.0D, str1, str5);
    localDefaultCategoryDataset.addValue(33.0D, str1, str6);
    localDefaultCategoryDataset.addValue(45.0D, str1, str7);
    localDefaultCategoryDataset.addValue(35.0D, str1, str8);
    localDefaultCategoryDataset.addValue(45.0D, str2, str4);
    localDefaultCategoryDataset.addValue(37.0D, str2, str5);
    localDefaultCategoryDataset.addValue(46.0D, str2, str6);
    localDefaultCategoryDataset.addValue(38.0D, str2, str7);
    localDefaultCategoryDataset.addValue(44.0D, str2, str8);
    localDefaultCategoryDataset.addValue(34.0D, str3, str4);
    localDefaultCategoryDataset.addValue(43.0D, str3, str5);
    localDefaultCategoryDataset.addValue(32.0D, str3, str6);
    localDefaultCategoryDataset.addValue(43.0D, str3, str7);
    localDefaultCategoryDataset.addValue(36.0D, str3, str8);
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart("Bar Chart Demo 10", "Category", "Value", paramCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setDomainGridlinesVisible(true);
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    BarRenderer localBarRenderer = (BarRenderer)localCategoryPlot.getRenderer();
    localBarRenderer.setDrawBarOutline(false);
    GradientPaint localGradientPaint1 = new GradientPaint(0.0F, 0.0F, Color.blue, 0.0F, 0.0F, new Color(0, 0, 64));
    GradientPaint localGradientPaint2 = new GradientPaint(0.0F, 0.0F, Color.green, 0.0F, 0.0F, new Color(0, 64, 0));
    GradientPaint localGradientPaint3 = new GradientPaint(0.0F, 0.0F, Color.red, 0.0F, 0.0F, new Color(64, 0, 0));
    localBarRenderer.setSeriesPaint(0, localGradientPaint1);
    localBarRenderer.setSeriesPaint(1, localGradientPaint2);
    localBarRenderer.setSeriesPaint(2, localGradientPaint3);
    CategoryAxis localCategoryAxis = localCategoryPlot.getDomainAxis();
    localCategoryAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(0.5235987755982988D));
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = createDataset();
    JFreeChart localJFreeChart = createChart(localDefaultCategoryDataset);
    Animator localAnimator = new Animator(localDefaultCategoryDataset);
    localAnimator.start();
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    BarChartDemo10 localBarChartDemo10 = new BarChartDemo10("JFreeChart: BarChartDemo10.java");
    localBarChartDemo10.pack();
    RefineryUtilities.centerFrameOnScreen(localBarChartDemo10);
    localBarChartDemo10.setVisible(true);
  }
}