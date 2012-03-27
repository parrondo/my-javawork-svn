package demo;

import java.awt.Dimension;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class StackedBarDemo2 extends ApplicationFrame
{
  public StackedBarDemo2(String paramString)
  {
    super(paramString);
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(1.0D, "Row 1", "Column 1");
    localDefaultCategoryDataset.addValue(5.0D, "Row 1", "Column 2");
    localDefaultCategoryDataset.addValue(3.0D, "Row 1", "Column 3");
    localDefaultCategoryDataset.addValue(2.0D, "Row 2", "Column 1");
    localDefaultCategoryDataset.addValue(3.0D, "Row 2", "Column 2");
    localDefaultCategoryDataset.addValue(2.0D, "Row 2", "Column 3");
    JFreeChart localJFreeChart = ChartFactory.createStackedBarChart("StackedBarDemo2", "Category", "Value", localDefaultCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    StackedBarRenderer localStackedBarRenderer = (StackedBarRenderer)localCategoryPlot.getRenderer();
    localStackedBarRenderer.setRenderAsPercentages(true);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localChartPanel);
  }

  public static void main(String[] paramArrayOfString)
  {
    StackedBarDemo2 localStackedBarDemo2 = new StackedBarDemo2("StackedBarDemo2");
    localStackedBarDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localStackedBarDemo2);
    localStackedBarDemo2.setVisible(true);
  }
}