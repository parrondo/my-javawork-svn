package demo;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class CombinedCategoryPlotDemo1 extends ApplicationFrame
{
  public CombinedCategoryPlotDemo1(String paramString)
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
    String str3 = "Type 1";
    String str4 = "Type 2";
    String str5 = "Type 3";
    String str6 = "Type 4";
    String str7 = "Type 5";
    String str8 = "Type 6";
    String str9 = "Type 7";
    String str10 = "Type 8";
    localDefaultCategoryDataset.addValue(11.0D, str1, str3);
    localDefaultCategoryDataset.addValue(14.0D, str1, str4);
    localDefaultCategoryDataset.addValue(13.0D, str1, str5);
    localDefaultCategoryDataset.addValue(15.0D, str1, str6);
    localDefaultCategoryDataset.addValue(15.0D, str1, str7);
    localDefaultCategoryDataset.addValue(17.0D, str1, str8);
    localDefaultCategoryDataset.addValue(17.0D, str1, str9);
    localDefaultCategoryDataset.addValue(18.0D, str1, str10);
    localDefaultCategoryDataset.addValue(15.0D, str2, str3);
    localDefaultCategoryDataset.addValue(17.0D, str2, str4);
    localDefaultCategoryDataset.addValue(16.0D, str2, str5);
    localDefaultCategoryDataset.addValue(18.0D, str2, str6);
    localDefaultCategoryDataset.addValue(14.0D, str2, str7);
    localDefaultCategoryDataset.addValue(14.0D, str2, str8);
    localDefaultCategoryDataset.addValue(12.0D, str2, str9);
    localDefaultCategoryDataset.addValue(11.0D, str2, str10);
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart()
  {
    CategoryDataset localCategoryDataset1 = createDataset1();
    NumberAxis localNumberAxis1 = new NumberAxis("Value");
    localNumberAxis1.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    LineAndShapeRenderer localLineAndShapeRenderer = new LineAndShapeRenderer();
    localLineAndShapeRenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
    CategoryPlot localCategoryPlot1 = new CategoryPlot(localCategoryDataset1, null, localNumberAxis1, localLineAndShapeRenderer);
    localCategoryPlot1.setDomainGridlinesVisible(true);
    CategoryDataset localCategoryDataset2 = createDataset2();
    NumberAxis localNumberAxis2 = new NumberAxis("Value");
    localNumberAxis2.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    BarRenderer localBarRenderer = new BarRenderer();
    localBarRenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
    CategoryPlot localCategoryPlot2 = new CategoryPlot(localCategoryDataset2, null, localNumberAxis2, localBarRenderer);
    localCategoryPlot2.setDomainGridlinesVisible(true);
    CategoryAxis localCategoryAxis = new CategoryAxis("Category");
    CombinedDomainCategoryPlot localCombinedDomainCategoryPlot = new CombinedDomainCategoryPlot(localCategoryAxis);
    localCombinedDomainCategoryPlot.add(localCategoryPlot1, 2);
    localCombinedDomainCategoryPlot.add(localCategoryPlot2, 1);
    JFreeChart localJFreeChart = new JFreeChart("Combined Domain Category Plot Demo", new Font("SansSerif", 1, 12), localCombinedDomainCategoryPlot, true);
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
    String str = "JFreeChart: CombinedCategoryPlotDemo1.java";
    CombinedCategoryPlotDemo1 localCombinedCategoryPlotDemo1 = new CombinedCategoryPlotDemo1(str);
    localCombinedCategoryPlotDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localCombinedCategoryPlotDemo1);
    localCombinedCategoryPlotDemo1.setVisible(true);
  }
}