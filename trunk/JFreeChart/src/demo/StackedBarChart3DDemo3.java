package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class StackedBarChart3DDemo3 extends ApplicationFrame
{
  public StackedBarChart3DDemo3(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  public static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(10.0D, "Series 1", "C1");
    localDefaultCategoryDataset.addValue(5.0D, "Series 1", "C2");
    localDefaultCategoryDataset.addValue(6.0D, "Series 1", "C3");
    localDefaultCategoryDataset.addValue(7.0D, "Series 1", "C4");
    localDefaultCategoryDataset.addValue(8.0D, "Series 1", "C5");
    localDefaultCategoryDataset.addValue(9.0D, "Series 1", "C6");
    localDefaultCategoryDataset.addValue(10.0D, "Series 1", "C7");
    localDefaultCategoryDataset.addValue(11.0D, "Series 1", "C8");
    localDefaultCategoryDataset.addValue(3.0D, "Series 1", "C9");
    localDefaultCategoryDataset.addValue(4.0D, "Series 2", "C1");
    localDefaultCategoryDataset.addValue(7.0D, "Series 2", "C2");
    localDefaultCategoryDataset.addValue(17.0D, "Series 2", "C3");
    localDefaultCategoryDataset.addValue(-15.0D, "Series 2", "C4");
    localDefaultCategoryDataset.addValue(6.0D, "Series 2", "C5");
    localDefaultCategoryDataset.addValue(8.0D, "Series 2", "C6");
    localDefaultCategoryDataset.addValue(9.0D, "Series 2", "C7");
    localDefaultCategoryDataset.addValue(13.0D, "Series 2", "C8");
    localDefaultCategoryDataset.addValue(7.0D, "Series 2", "C9");
    localDefaultCategoryDataset.addValue(15.0D, "Series 3", "C1");
    localDefaultCategoryDataset.addValue(-14.0D, "Series 3", "C2");
    localDefaultCategoryDataset.addValue(12.0D, "Series 3", "C3");
    localDefaultCategoryDataset.addValue(11.0D, "Series 3", "C4");
    localDefaultCategoryDataset.addValue(10.0D, "Series 3", "C5");
    localDefaultCategoryDataset.addValue(0.0D, "Series 3", "C6");
    localDefaultCategoryDataset.addValue(7.0D, "Series 3", "C7");
    localDefaultCategoryDataset.addValue(9.0D, "Series 3", "C8");
    localDefaultCategoryDataset.addValue(11.0D, "Series 3", "C9");
    localDefaultCategoryDataset.addValue(14.0D, "Series 4", "C1");
    localDefaultCategoryDataset.addValue(3.0D, "Series 4", "C2");
    localDefaultCategoryDataset.addValue(7.0D, "Series 4", "C3");
    localDefaultCategoryDataset.addValue(0.0D, "Series 4", "C4");
    localDefaultCategoryDataset.addValue(9.0D, "Series 4", "C5");
    localDefaultCategoryDataset.addValue(6.0D, "Series 4", "C6");
    localDefaultCategoryDataset.addValue(7.0D, "Series 4", "C7");
    localDefaultCategoryDataset.addValue(9.0D, "Series 4", "C8");
    localDefaultCategoryDataset.addValue(10.0D, "Series 4", "C9");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createStackedBarChart3D("Stacked Bar Chart 3D Demo 3", "Category", "Value", paramCategoryDataset, PlotOrientation.HORIZONTAL, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    BarRenderer localBarRenderer = (BarRenderer)localCategoryPlot.getRenderer();
    localBarRenderer.setDrawBarOutline(false);
    localBarRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
    localBarRenderer.setBaseItemLabelsVisible(true);
    localBarRenderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
    localBarRenderer.setBaseNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    StackedBarChart3DDemo3 localStackedBarChart3DDemo3 = new StackedBarChart3DDemo3("Stacked Bar Chart 3D Demo 3");
    localStackedBarChart3DDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localStackedBarChart3DDemo3);
    localStackedBarChart3DDemo3.setVisible(true);
  }
}