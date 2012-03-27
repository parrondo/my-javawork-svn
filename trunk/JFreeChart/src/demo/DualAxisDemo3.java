package demo;

import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;

public class DualAxisDemo3 extends ApplicationFrame
{
  public DualAxisDemo3(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart()
  {
    CategoryDataset localCategoryDataset1 = createDataset1();
    JFreeChart localJFreeChart = ChartFactory.createBarChart("Dual Axis Chart", "Category", "Value", localCategoryDataset1, PlotOrientation.HORIZONTAL, true, true, false);
    LegendTitle localLegendTitle = (LegendTitle)localJFreeChart.getSubtitle(0);
    localLegendTitle.setPosition(RectangleEdge.LEFT);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
    localCategoryPlot.getDomainAxis().setMaximumCategoryLabelWidthRatio(10.0F);
    CategoryDataset localCategoryDataset2 = createDataset2();
    NumberAxis localNumberAxis = new NumberAxis("Secondary");
    localCategoryPlot.setRangeAxis(1, localNumberAxis);
    localCategoryPlot.setDataset(1, localCategoryDataset2);
    localCategoryPlot.mapDatasetToRangeAxis(1, 1);
    localCategoryPlot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
    LineAndShapeRenderer localLineAndShapeRenderer = new LineAndShapeRenderer();
    localCategoryPlot.setRenderer(1, localLineAndShapeRenderer);
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
    String str9 = "Category 6";
    String str10 = "Category 7";
    String str11 = "Category 8";
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(1.0D, str1, str4);
    localDefaultCategoryDataset.addValue(4.0D, str1, str5);
    localDefaultCategoryDataset.addValue(3.0D, str1, str6);
    localDefaultCategoryDataset.addValue(5.0D, str1, str7);
    localDefaultCategoryDataset.addValue(5.0D, str1, str8);
    localDefaultCategoryDataset.addValue(7.0D, str1, str9);
    localDefaultCategoryDataset.addValue(7.0D, str1, str10);
    localDefaultCategoryDataset.addValue(8.0D, str1, str11);
    localDefaultCategoryDataset.addValue(5.0D, str2, str4);
    localDefaultCategoryDataset.addValue(7.0D, str2, str5);
    localDefaultCategoryDataset.addValue(6.0D, str2, str6);
    localDefaultCategoryDataset.addValue(8.0D, str2, str7);
    localDefaultCategoryDataset.addValue(4.0D, str2, str8);
    localDefaultCategoryDataset.addValue(4.0D, str2, str9);
    localDefaultCategoryDataset.addValue(2.0D, str2, str10);
    localDefaultCategoryDataset.addValue(1.0D, str2, str11);
    localDefaultCategoryDataset.addValue(4.0D, str3, str4);
    localDefaultCategoryDataset.addValue(3.0D, str3, str5);
    localDefaultCategoryDataset.addValue(2.0D, str3, str6);
    localDefaultCategoryDataset.addValue(3.0D, str3, str7);
    localDefaultCategoryDataset.addValue(6.0D, str3, str8);
    localDefaultCategoryDataset.addValue(3.0D, str3, str9);
    localDefaultCategoryDataset.addValue(4.0D, str3, str10);
    localDefaultCategoryDataset.addValue(3.0D, str3, str11);
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
    String str7 = "Category 6";
    String str8 = "Category 7";
    String str9 = "Category 8";
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(15.0D, str1, str2);
    localDefaultCategoryDataset.addValue(24.0D, str1, str3);
    localDefaultCategoryDataset.addValue(31.0D, str1, str4);
    localDefaultCategoryDataset.addValue(25.0D, str1, str5);
    localDefaultCategoryDataset.addValue(56.0D, str1, str6);
    localDefaultCategoryDataset.addValue(37.0D, str1, str7);
    localDefaultCategoryDataset.addValue(77.0D, str1, str8);
    localDefaultCategoryDataset.addValue(18.0D, str1, str9);
    return localDefaultCategoryDataset;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart();
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    DualAxisDemo3 localDualAxisDemo3 = new DualAxisDemo3("JFreeChart: DualAxisDemo3.java");
    localDualAxisDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localDualAxisDemo3);
    localDualAxisDemo3.setVisible(true);
  }
}