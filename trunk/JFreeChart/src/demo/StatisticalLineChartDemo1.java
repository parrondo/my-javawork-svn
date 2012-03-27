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
import org.jfree.chart.renderer.category.StatisticalLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class StatisticalLineChartDemo1 extends ApplicationFrame
{
  public StatisticalLineChartDemo1(String paramString)
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
    JFreeChart localJFreeChart = ChartFactory.createLineChart("Statistical Line Chart Demo 1", "Type", "Value", paramCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setRangePannable(true);
    CategoryAxis localCategoryAxis = localCategoryPlot.getDomainAxis();
    localCategoryAxis.setUpperMargin(0.0D);
    localCategoryAxis.setLowerMargin(0.0D);
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    localNumberAxis.setAutoRangeIncludesZero(true);
    StatisticalLineAndShapeRenderer localStatisticalLineAndShapeRenderer = new StatisticalLineAndShapeRenderer(true, false);
    localStatisticalLineAndShapeRenderer.setUseSeriesOffset(true);
    localCategoryPlot.setRenderer(localStatisticalLineAndShapeRenderer);
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
    StatisticalLineChartDemo1 localStatisticalLineChartDemo1 = new StatisticalLineChartDemo1("JFreeChart: StatisticalLineChartDemo1.java");
    localStatisticalLineChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localStatisticalLineChartDemo1);
    localStatisticalLineChartDemo1.setVisible(true);
  }
}