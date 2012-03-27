package demo;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedRangeCategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class CombinedCategoryPlotDemo2 extends ApplicationFrame
{
  public CombinedCategoryPlotDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  public static CategoryDataset createDataset1()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    String str1 = "First";
    String str2 = "Second";
    String str3 = "Type 1";
    String str4 = "Type 2";
    String str5 = "Type 3";
    String str6 = "Type 4";
    String str7 = "Type 5";
    String str8 = "Type 6";
    String str9 = "Type 7";
    String str10 = "Type 8";
    localDefaultCategoryDataset.addValue(1.0D, str1, str3);
    localDefaultCategoryDataset.addValue(4.0D, str1, str4);
    localDefaultCategoryDataset.addValue(3.0D, str1, str5);
    localDefaultCategoryDataset.addValue(5.0D, str1, str6);
    localDefaultCategoryDataset.addValue(5.0D, str1, str7);
    localDefaultCategoryDataset.addValue(7.0D, str1, str8);
    localDefaultCategoryDataset.addValue(7.0D, str1, str9);
    localDefaultCategoryDataset.addValue(8.0D, str1, str10);
    localDefaultCategoryDataset.addValue(5.0D, str2, str3);
    localDefaultCategoryDataset.addValue(7.0D, str2, str4);
    localDefaultCategoryDataset.addValue(6.0D, str2, str5);
    localDefaultCategoryDataset.addValue(8.0D, str2, str6);
    localDefaultCategoryDataset.addValue(4.0D, str2, str7);
    localDefaultCategoryDataset.addValue(4.0D, str2, str8);
    localDefaultCategoryDataset.addValue(2.0D, str2, str9);
    localDefaultCategoryDataset.addValue(1.0D, str2, str10);
    return localDefaultCategoryDataset;
  }

  public static CategoryDataset createDataset2()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    String str1 = "Third";
    String str2 = "Fourth";
    String str3 = "Sector 1";
    String str4 = "Sector 2";
    String str5 = "Sector 3";
    String str6 = "Sector 4";
    localDefaultCategoryDataset.addValue(11.0D, str1, str3);
    localDefaultCategoryDataset.addValue(14.0D, str1, str4);
    localDefaultCategoryDataset.addValue(13.0D, str1, str5);
    localDefaultCategoryDataset.addValue(15.0D, str1, str6);
    localDefaultCategoryDataset.addValue(15.0D, str2, str3);
    localDefaultCategoryDataset.addValue(17.0D, str2, str4);
    localDefaultCategoryDataset.addValue(16.0D, str2, str5);
    localDefaultCategoryDataset.addValue(18.0D, str2, str6);
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart()
  {
    CategoryDataset localCategoryDataset1 = createDataset1();
    CategoryAxis localCategoryAxis1 = new CategoryAxis("Class 1");
    localCategoryAxis1.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
    localCategoryAxis1.setMaximumCategoryLabelWidthRatio(5.0F);
    LineAndShapeRenderer localLineAndShapeRenderer = new LineAndShapeRenderer();
    localLineAndShapeRenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
    CategoryPlot localCategoryPlot1 = new CategoryPlot(localCategoryDataset1, localCategoryAxis1, null, localLineAndShapeRenderer);
    localCategoryPlot1.setDomainGridlinesVisible(true);
    CategoryDataset localCategoryDataset2 = createDataset2();
    CategoryAxis localCategoryAxis2 = new CategoryAxis("Class 2");
    localCategoryAxis2.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
    localCategoryAxis2.setMaximumCategoryLabelWidthRatio(5.0F);
    BarRenderer localBarRenderer = new BarRenderer();
    localBarRenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
    CategoryPlot localCategoryPlot2 = new CategoryPlot(localCategoryDataset2, localCategoryAxis2, null, localBarRenderer);
    localCategoryPlot2.setDomainGridlinesVisible(true);
    NumberAxis localNumberAxis = new NumberAxis("Value");
    CombinedRangeCategoryPlot localCombinedRangeCategoryPlot = new CombinedRangeCategoryPlot(localNumberAxis);
    localCombinedRangeCategoryPlot.setRangePannable(true);
    localCombinedRangeCategoryPlot.add(localCategoryPlot1, 3);
    localCombinedRangeCategoryPlot.add(localCategoryPlot2, 2);
    localCombinedRangeCategoryPlot.setOrientation(PlotOrientation.HORIZONTAL);
    JFreeChart localJFreeChart = new JFreeChart("Combined Range Category Plot Demo", new Font("SansSerif", 1, 12), localCombinedRangeCategoryPlot, true);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart();
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    String str = "JFreeChart: CombinedCategoryPlotDemo2.java";
    CombinedCategoryPlotDemo2 localCombinedCategoryPlotDemo2 = new CombinedCategoryPlotDemo2(str);
    localCombinedCategoryPlotDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localCombinedCategoryPlotDemo2);
    localCombinedCategoryPlotDemo2.setVisible(true);
  }
}