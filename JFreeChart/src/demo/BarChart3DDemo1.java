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
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class BarChart3DDemo1 extends ApplicationFrame
{
  public BarChart3DDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(10.0D, "Series 1", "Category 1");
    localDefaultCategoryDataset.addValue(4.0D, "Series 1", "Category 2");
    localDefaultCategoryDataset.addValue(15.0D, "Series 1", "Category 3");
    localDefaultCategoryDataset.addValue(14.0D, "Series 1", "Category 4");
    localDefaultCategoryDataset.addValue(-5.0D, "Series 2", "Category 1");
    localDefaultCategoryDataset.addValue(-7.0D, "Series 2", "Category 2");
    localDefaultCategoryDataset.addValue(14.0D, "Series 2", "Category 3");
    localDefaultCategoryDataset.addValue(-3.0D, "Series 2", "Category 4");
    localDefaultCategoryDataset.addValue(6.0D, "Series 3", "Category 1");
    localDefaultCategoryDataset.addValue(17.0D, "Series 3", "Category 2");
    localDefaultCategoryDataset.addValue(-12.0D, "Series 3", "Category 3");
    localDefaultCategoryDataset.addValue(7.0D, "Series 3", "Category 4");
    localDefaultCategoryDataset.addValue(7.0D, "Series 4", "Category 1");
    localDefaultCategoryDataset.addValue(15.0D, "Series 4", "Category 2");
    localDefaultCategoryDataset.addValue(11.0D, "Series 4", "Category 3");
    localDefaultCategoryDataset.addValue(0.0D, "Series 4", "Category 4");
    localDefaultCategoryDataset.addValue(-8.0D, "Series 5", "Category 1");
    localDefaultCategoryDataset.addValue(-6.0D, "Series 5", "Category 2");
    localDefaultCategoryDataset.addValue(10.0D, "Series 5", "Category 3");
    localDefaultCategoryDataset.addValue(-9.0D, "Series 5", "Category 4");
    localDefaultCategoryDataset.addValue(9.0D, "Series 6", "Category 1");
    localDefaultCategoryDataset.addValue(8.0D, "Series 6", "Category 2");
    localDefaultCategoryDataset.addValue(0.0D, "Series 6", "Category 3");
    localDefaultCategoryDataset.addValue(6.0D, "Series 6", "Category 4");
    localDefaultCategoryDataset.addValue(-10.0D, "Series 7", "Category 1");
    localDefaultCategoryDataset.addValue(9.0D, "Series 7", "Category 2");
    localDefaultCategoryDataset.addValue(7.0D, "Series 7", "Category 3");
    localDefaultCategoryDataset.addValue(7.0D, "Series 7", "Category 4");
    localDefaultCategoryDataset.addValue(11.0D, "Series 8", "Category 1");
    localDefaultCategoryDataset.addValue(13.0D, "Series 8", "Category 2");
    localDefaultCategoryDataset.addValue(9.0D, "Series 8", "Category 3");
    localDefaultCategoryDataset.addValue(9.0D, "Series 8", "Category 4");
    localDefaultCategoryDataset.addValue(-3.0D, "Series 9", "Category 1");
    localDefaultCategoryDataset.addValue(7.0D, "Series 9", "Category 2");
    localDefaultCategoryDataset.addValue(11.0D, "Series 9", "Category 3");
    localDefaultCategoryDataset.addValue(-10.0D, "Series 9", "Category 4");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart3D("3D Bar Chart Demo", "Category", "Value", paramCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setOutlineVisible(false);
    localCategoryPlot.setDomainGridlinesVisible(true);
    CategoryAxis localCategoryAxis = localCategoryPlot.getDomainAxis();
    localCategoryAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(0.3926990816987241D));
    localCategoryAxis.setCategoryMargin(0.0D);
    BarRenderer3D localBarRenderer3D = (BarRenderer3D)localCategoryPlot.getRenderer();
    localBarRenderer3D.setDrawBarOutline(false);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(600, 400));
    localChartPanel.setMouseWheelEnabled(true);
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    BarChart3DDemo1 localBarChart3DDemo1 = new BarChart3DDemo1("JFreeChart: BarChart3DDemo1.java");
    localBarChart3DDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localBarChart3DDemo1);
    localBarChart3DDemo1.setVisible(true);
  }
}