package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.CategoryStepRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class CategoryStepChartDemo1 extends ApplicationFrame
{
  public CategoryStepChartDemo1(String paramString)
  {
    super(paramString);
    CategoryDataset localCategoryDataset = createDataset();
    ChartPanel localChartPanel = (ChartPanel)createDemoPanel();
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    localChartPanel.setEnforceFileExtensions(false);
    setContentPane(localChartPanel);
  }

  private static CategoryDataset createDataset()
  {
    double[][] arrayOfDouble = { { 1.0D, 4.0D, 3.0D, 5.0D, 5.0D, 7.0D, 7.0D, 8.0D }, { 5.0D, 7.0D, 6.0D, 8.0D, 4.0D, 4.0D, 2.0D, 1.0D }, { 4.0D, 3.0D, 2.0D, 3.0D, 6.0D, 3.0D, 4.0D, 3.0D } };
    CategoryDataset localCategoryDataset = DatasetUtilities.createCategoryDataset("Series ", "Type ", arrayOfDouble);
    return localCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    CategoryStepRenderer localCategoryStepRenderer = new CategoryStepRenderer(true);
    localCategoryStepRenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
    CategoryAxis localCategoryAxis = new CategoryAxis("Category");
    NumberAxis localNumberAxis = new NumberAxis("Value");
    CategoryPlot localCategoryPlot = new CategoryPlot(paramCategoryDataset, localCategoryAxis, localNumberAxis, localCategoryStepRenderer);
    localCategoryPlot.setRangePannable(true);
    JFreeChart localJFreeChart = new JFreeChart("Category Step Chart", localCategoryPlot);
    localCategoryPlot.setAxisOffset(new RectangleInsets(5.0D, 5.0D, 5.0D, 5.0D));
    localCategoryPlot.setDomainGridlinesVisible(true);
    localCategoryPlot.setRangeGridlinesVisible(true);
    localCategoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
    localCategoryAxis.setLowerMargin(0.0D);
    localCategoryAxis.setUpperMargin(0.0D);
    localCategoryAxis.addCategoryLabelToolTip("Type 1", "The first type.");
    localCategoryAxis.addCategoryLabelToolTip("Type 2", "The second type.");
    localCategoryAxis.addCategoryLabelToolTip("Type 3", "The third type.");
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    localNumberAxis.setLabelAngle(0.0D);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setMouseWheelEnabled(true);
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    CategoryStepChartDemo1 localCategoryStepChartDemo1 = new CategoryStepChartDemo1("JFreeChart : CategoryStepChartDemo1.java");
    localCategoryStepChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localCategoryStepChartDemo1);
    localCategoryStepChartDemo1.setVisible(true);
  }
}