package demo;

import java.awt.Dimension;
import java.text.DecimalFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class StackedBarDemo3 extends ApplicationFrame
{
  public StackedBarDemo3(String paramString)
  {
    super(paramString);
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(1.0D, "Row 1", "Column 1");
    localDefaultCategoryDataset.addValue(5.0D, "Row 1", "Column 2");
    localDefaultCategoryDataset.addValue(3.0D, "Row 1", "Column 3");
    localDefaultCategoryDataset.addValue(2.0D, "Row 2", "Column 1");
    localDefaultCategoryDataset.addValue(3.0D, "Row 2", "Column 2");
    localDefaultCategoryDataset.addValue(2.0D, "Row 2", "Column 3");
    JFreeChart localJFreeChart = ChartFactory.createStackedBarChart("StackedBarDemo3", "Category", "Value", localDefaultCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    StackedBarRenderer localStackedBarRenderer = (StackedBarRenderer)localCategoryPlot.getRenderer();
    localStackedBarRenderer.setRenderAsPercentages(true);
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setLabel("Percentage");
    localNumberAxis.setNumberFormatOverride(new DecimalFormat("0.0%"));
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localChartPanel);
  }

  public static void main(String[] paramArrayOfString)
  {
    StackedBarDemo3 localStackedBarDemo3 = new StackedBarDemo3("StackedBarDemo3");
    localStackedBarDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localStackedBarDemo3);
    localStackedBarDemo3.setVisible(true);
  }
}