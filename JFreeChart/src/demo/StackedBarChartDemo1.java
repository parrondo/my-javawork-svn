package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class StackedBarChartDemo1 extends ApplicationFrame
{
  public StackedBarChartDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(32.399999999999999D, "Series 1", "Category 1");
    localDefaultCategoryDataset.addValue(17.800000000000001D, "Series 2", "Category 1");
    localDefaultCategoryDataset.addValue(27.699999999999999D, "Series 3", "Category 1");
    localDefaultCategoryDataset.addValue(43.200000000000003D, "Series 1", "Category 2");
    localDefaultCategoryDataset.addValue(15.6D, "Series 2", "Category 2");
    localDefaultCategoryDataset.addValue(18.300000000000001D, "Series 3", "Category 2");
    localDefaultCategoryDataset.addValue(23.0D, "Series 1", "Category 3");
    localDefaultCategoryDataset.addValue(11.300000000000001D, "Series 2", "Category 3");
    localDefaultCategoryDataset.addValue(25.5D, "Series 3", "Category 3");
    localDefaultCategoryDataset.addValue(13.0D, "Series 1", "Category 4");
    localDefaultCategoryDataset.addValue(11.800000000000001D, "Series 2", "Category 4");
    localDefaultCategoryDataset.addValue(29.5D, "Series 3", "Category 4");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createStackedBarChart("Stacked Bar Chart Demo 1", "Category", "Value", paramCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    StackedBarRenderer localStackedBarRenderer = (StackedBarRenderer)localCategoryPlot.getRenderer();
    localStackedBarRenderer.setDrawBarOutline(false);
    localStackedBarRenderer.setBaseItemLabelsVisible(true);
    localStackedBarRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    StackedBarChartDemo1 localStackedBarChartDemo1 = new StackedBarChartDemo1("Stacked Bar Chart Demo 1");
    localStackedBarChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localStackedBarChartDemo1);
    localStackedBarChartDemo1.setVisible(true);
  }
}