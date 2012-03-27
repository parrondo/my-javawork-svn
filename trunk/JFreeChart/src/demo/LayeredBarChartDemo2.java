package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LayeredBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.SortOrder;

public class LayeredBarChartDemo2 extends ApplicationFrame
{
  public LayeredBarChartDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
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
    localDefaultCategoryDataset.addValue(1.0D, str1, str4);
    localDefaultCategoryDataset.addValue(4.0D, str1, str5);
    localDefaultCategoryDataset.addValue(3.0D, str1, str6);
    localDefaultCategoryDataset.addValue(5.0D, str1, str7);
    localDefaultCategoryDataset.addValue(5.0D, str1, str8);
    localDefaultCategoryDataset.addValue(5.0D, str2, str4);
    localDefaultCategoryDataset.addValue(7.0D, str2, str5);
    localDefaultCategoryDataset.addValue(6.0D, str2, str6);
    localDefaultCategoryDataset.addValue(8.0D, str2, str7);
    localDefaultCategoryDataset.addValue(4.0D, str2, str8);
    localDefaultCategoryDataset.addValue(4.0D, str3, str4);
    localDefaultCategoryDataset.addValue(3.0D, str3, str5);
    localDefaultCategoryDataset.addValue(2.0D, str3, str6);
    localDefaultCategoryDataset.addValue(3.0D, str3, str7);
    localDefaultCategoryDataset.addValue(6.0D, str3, str8);
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart("Layered Bar Chart Demo 2", "Category", "Value", paramCategoryDataset, PlotOrientation.HORIZONTAL, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setDomainGridlinesVisible(true);
    localCategoryPlot.setRangePannable(true);
    localCategoryPlot.setRangeZeroBaselineVisible(true);
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    LayeredBarRenderer localLayeredBarRenderer = new LayeredBarRenderer();
    localLayeredBarRenderer.setDrawBarOutline(false);
    localCategoryPlot.setRenderer(localLayeredBarRenderer);
    localCategoryPlot.setRowRenderingOrder(SortOrder.DESCENDING);
    GradientPaint localGradientPaint1 = new GradientPaint(0.0F, 0.0F, Color.blue, 0.0F, 0.0F, new Color(0, 0, 64));
    GradientPaint localGradientPaint2 = new GradientPaint(0.0F, 0.0F, Color.green, 0.0F, 0.0F, new Color(0, 64, 0));
    GradientPaint localGradientPaint3 = new GradientPaint(0.0F, 0.0F, Color.red, 0.0F, 0.0F, new Color(64, 0, 0));
    localLayeredBarRenderer.setSeriesPaint(0, localGradientPaint1);
    localLayeredBarRenderer.setSeriesPaint(1, localGradientPaint2);
    localLayeredBarRenderer.setSeriesPaint(2, localGradientPaint3);
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
    LayeredBarChartDemo2 localLayeredBarChartDemo2 = new LayeredBarChartDemo2("JFreeChart: LayeredBarChartDemo2.java");
    localLayeredBarChartDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localLayeredBarChartDemo2);
    localLayeredBarChartDemo2.setVisible(true);
  }
}