package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.GridLayout;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class AxisOffsetsDemo1 extends ApplicationFrame
{
  public AxisOffsetsDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    String str1 = "S1";
    String str2 = "S2";
    String str3 = "S3";
    String str4 = "C1";
    String str5 = "C2";
    String str6 = "C3";
    String str7 = "C4";
    String str8 = "C5";
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

  private static JFreeChart createChart(String paramString, CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart(paramString, "Category", "Value", paramCategoryDataset, PlotOrientation.VERTICAL, false, true, false);
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
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart1 = createChart("Axis Offsets: 0", createDataset());
    CategoryPlot localCategoryPlot1 = (CategoryPlot)localJFreeChart1.getPlot();
    localCategoryPlot1.setAxisOffset(RectangleInsets.ZERO_INSETS);
    ChartPanel localChartPanel1 = new ChartPanel(localJFreeChart1);
    localChartPanel1.setMinimumDrawWidth(0);
    localChartPanel1.setMinimumDrawHeight(0);
    JFreeChart localJFreeChart2 = createChart("Axis Offsets: 5", createDataset());
    ChartPanel localChartPanel2 = new ChartPanel(localJFreeChart2);
    localChartPanel2.setMinimumDrawWidth(0);
    localChartPanel2.setMinimumDrawHeight(0);
    CategoryPlot localCategoryPlot2 = (CategoryPlot)localJFreeChart2.getPlot();
    localCategoryPlot2.setAxisOffset(new RectangleInsets(5.0D, 5.0D, 5.0D, 5.0D));
    DemoPanel localDemoPanel = new DemoPanel(new GridLayout(2, 1));
    localDemoPanel.add(localChartPanel1);
    localDemoPanel.add(localChartPanel2);
    localDemoPanel.addChart(localJFreeChart1);
    localDemoPanel.addChart(localJFreeChart2);
    return localDemoPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    AxisOffsetsDemo1 localAxisOffsetsDemo1 = new AxisOffsetsDemo1("JFreeChart: AxisOffsetsDemo1.java");
    localAxisOffsetsDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localAxisOffsetsDemo1);
    localAxisOffsetsDemo1.setVisible(true);
  }
}