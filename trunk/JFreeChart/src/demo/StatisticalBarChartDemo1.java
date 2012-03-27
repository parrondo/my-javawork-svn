package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class StatisticalBarChartDemo1 extends ApplicationFrame
{
  public StatisticalBarChartDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultStatisticalCategoryDataset localDefaultStatisticalCategoryDataset = new DefaultStatisticalCategoryDataset();
    localDefaultStatisticalCategoryDataset.add(10.0D, 2.4D, "Row 1", "Column 1");
    localDefaultStatisticalCategoryDataset.add(15.0D, 4.4D, "Row 1", "Column 2");
    localDefaultStatisticalCategoryDataset.add(13.0D, 2.1D, "Row 1", "Column 3");
    localDefaultStatisticalCategoryDataset.add(7.0D, 1.3D, "Row 1", "Column 4");
    localDefaultStatisticalCategoryDataset.add(22.0D, 2.4D, "Row 2", "Column 1");
    localDefaultStatisticalCategoryDataset.add(18.0D, 4.4D, "Row 2", "Column 2");
    localDefaultStatisticalCategoryDataset.add(28.0D, 2.1D, "Row 2", "Column 3");
    localDefaultStatisticalCategoryDataset.add(17.0D, 1.3D, "Row 2", "Column 4");
    return localDefaultStatisticalCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createLineChart("Statistical Bar Chart Demo 1", "Type", "Value", paramCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    localNumberAxis.setAutoRangeIncludesZero(false);
    StatisticalBarRenderer localStatisticalBarRenderer = new StatisticalBarRenderer();
    localStatisticalBarRenderer.setDrawBarOutline(false);
    localStatisticalBarRenderer.setErrorIndicatorPaint(Color.black);
    localStatisticalBarRenderer.setIncludeBaseInRange(false);
    localCategoryPlot.setRenderer(localStatisticalBarRenderer);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    localStatisticalBarRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
    localStatisticalBarRenderer.setBaseItemLabelsVisible(true);
    localStatisticalBarRenderer.setBaseItemLabelPaint(Color.yellow);
    localStatisticalBarRenderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.INSIDE6, TextAnchor.BOTTOM_CENTER));
    GradientPaint localGradientPaint1 = new GradientPaint(0.0F, 0.0F, Color.blue, 0.0F, 0.0F, new Color(0, 0, 64));
    GradientPaint localGradientPaint2 = new GradientPaint(0.0F, 0.0F, Color.green, 0.0F, 0.0F, new Color(0, 64, 0));
    localStatisticalBarRenderer.setSeriesPaint(0, localGradientPaint1);
    localStatisticalBarRenderer.setSeriesPaint(1, localGradientPaint2);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    StatisticalBarChartDemo1 localStatisticalBarChartDemo1 = new StatisticalBarChartDemo1("Statistical Bar Chart Demo");
    localStatisticalBarChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localStatisticalBarChartDemo1);
    localStatisticalBarChartDemo1.setVisible(true);
  }
}