package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class StackedAreaChartDemo1 extends ApplicationFrame
{
  public StackedAreaChartDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  public static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(1.0D, "S1", "C1");
    localDefaultCategoryDataset.addValue(2.0D, "S1", "C2");
    localDefaultCategoryDataset.addValue(3.0D, "S1", "C3");
    localDefaultCategoryDataset.addValue(4.0D, "S1", "C4");
    localDefaultCategoryDataset.addValue(5.0D, "S1", "C5");
    localDefaultCategoryDataset.addValue(6.0D, "S1", "C6");
    localDefaultCategoryDataset.addValue(7.0D, "S1", "C7");
    localDefaultCategoryDataset.addValue(8.0D, "S1", "C8");
    localDefaultCategoryDataset.addValue(6.0D, "S2", "C1");
    localDefaultCategoryDataset.addValue(3.0D, "S2", "C2");
    localDefaultCategoryDataset.addValue(4.0D, "S2", "C3");
    localDefaultCategoryDataset.addValue(3.0D, "S2", "C4");
    localDefaultCategoryDataset.addValue(9.0D, "S2", "C5");
    localDefaultCategoryDataset.addValue(7.0D, "S2", "C6");
    localDefaultCategoryDataset.addValue(2.0D, "S2", "C7");
    localDefaultCategoryDataset.addValue(3.0D, "S2", "C8");
    localDefaultCategoryDataset.addValue(1.0D, "S3", "C1");
    localDefaultCategoryDataset.addValue(7.0D, "S3", "C2");
    localDefaultCategoryDataset.addValue(6.0D, "S3", "C3");
    localDefaultCategoryDataset.addValue(7.0D, "S3", "C4");
    localDefaultCategoryDataset.addValue(4.0D, "S3", "C5");
    localDefaultCategoryDataset.addValue(5.0D, "S3", "C6");
    localDefaultCategoryDataset.addValue(3.0D, "S3", "C7");
    localDefaultCategoryDataset.addValue(1.0D, "S3", "C8");
    return localDefaultCategoryDataset;
  }

  public static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createStackedAreaChart("Stacked Area Chart", "Category", "Value", paramCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setForegroundAlpha(0.85F);
    CategoryAxis localCategoryAxis = localCategoryPlot.getDomainAxis();
    localCategoryAxis.setLowerMargin(0.0D);
    localCategoryAxis.setUpperMargin(0.0D);
    localCategoryAxis.setCategoryMargin(0.0D);
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    CategoryItemRenderer localCategoryItemRenderer = localCategoryPlot.getRenderer();
    localCategoryItemRenderer.setBaseItemLabelsVisible(true);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    StackedAreaChartDemo1 localStackedAreaChartDemo1 = new StackedAreaChartDemo1("JFreeChart: StackedAreaChartDemo1.java");
    localStackedAreaChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localStackedAreaChartDemo1);
    localStackedAreaChartDemo1.setVisible(true);
  }
}