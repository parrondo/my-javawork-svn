package demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis3D;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class DualAxisDemo4 extends ApplicationFrame
{
  public DualAxisDemo4(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart()
  {
    CategoryDataset localCategoryDataset1 = createDataset1();
    JFreeChart localJFreeChart = ChartFactory.createBarChart3D("Dual Axis Chart", "Category", "Value", localCategoryDataset1, PlotOrientation.VERTICAL, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
    localCategoryPlot.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
    CategoryItemRenderer localCategoryItemRenderer = localCategoryPlot.getRenderer();
    localCategoryItemRenderer.setSeriesPaint(0, Color.red);
    localCategoryItemRenderer.setSeriesPaint(1, Color.yellow);
    localCategoryItemRenderer.setSeriesPaint(2, Color.green);
    CategoryDataset localCategoryDataset2 = createDataset2();
    NumberAxis3D localNumberAxis3D = new NumberAxis3D("Secondary");
    localCategoryPlot.setRangeAxis(1, localNumberAxis3D);
    localCategoryPlot.setDataset(1, localCategoryDataset2);
    localCategoryPlot.mapDatasetToRangeAxis(1, 1);
    LineRenderer3D localLineRenderer3D = new LineRenderer3D();
    localLineRenderer3D.setSeriesPaint(0, Color.blue);
    localCategoryPlot.setRenderer(1, localLineRenderer3D);
    localCategoryPlot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  private static CategoryDataset createDataset1()
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

  private static CategoryDataset createDataset2()
  {
    String str1 = "Fourth";
    String str2 = "Category 1";
    String str3 = "Category 2";
    String str4 = "Category 3";
    String str5 = "Category 4";
    String str6 = "Category 5";
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(15.0D, str1, str2);
    localDefaultCategoryDataset.addValue(24.0D, str1, str3);
    localDefaultCategoryDataset.addValue(31.0D, str1, str4);
    localDefaultCategoryDataset.addValue(25.0D, str1, str5);
    localDefaultCategoryDataset.addValue(56.0D, str1, str6);
    return localDefaultCategoryDataset;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart();
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    DualAxisDemo4 localDualAxisDemo4 = new DualAxisDemo4("JFreeChart: DualAxisDemo4.java");
    localDualAxisDemo4.pack();
    RefineryUtilities.centerFrameOnScreen(localDualAxisDemo4);
    localDualAxisDemo4.setVisible(true);
  }
}