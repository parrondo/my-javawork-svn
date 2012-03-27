package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class BarChart3DDemo3 extends ApplicationFrame
{
  public BarChart3DDemo3(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(25.0D, "Series 1", "Category 1");
    localDefaultCategoryDataset.addValue(34.0D, "Series 1", "Category 2");
    localDefaultCategoryDataset.addValue(19.0D, "Series 2", "Category 1");
    localDefaultCategoryDataset.addValue(29.0D, "Series 2", "Category 2");
    localDefaultCategoryDataset.addValue(41.0D, "Series 3", "Category 1");
    localDefaultCategoryDataset.addValue(33.0D, "Series 3", "Category 2");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart3D("3D Bar Chart Demo", "Category", "Value", paramCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    CategoryAxis localCategoryAxis = localCategoryPlot.getDomainAxis();
    localCategoryAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(0.3926990816987241D));
    CategoryItemRenderer localCategoryItemRenderer = localCategoryPlot.getRenderer();
    localCategoryItemRenderer.setBaseItemLabelsVisible(true);
    BarRenderer localBarRenderer = (BarRenderer)localCategoryItemRenderer;
    localBarRenderer.setItemMargin(0.2D);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    BarChart3DDemo3 localBarChart3DDemo3 = new BarChart3DDemo3("JFreeChart: BarChart3DDemo3.java");
    localBarChart3DDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localBarChart3DDemo3);
    localBarChart3DDemo3.setVisible(true);
  }
}