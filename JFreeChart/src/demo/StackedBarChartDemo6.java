package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class StackedBarChartDemo6 extends ApplicationFrame
{
  public StackedBarChartDemo6(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    long l = 86400000L;
    localDefaultCategoryDataset.addValue(3L * l, "Series 1", "Category 1");
    localDefaultCategoryDataset.addValue(1L * l, "Series 2", "Category 1");
    localDefaultCategoryDataset.addValue(2L * l, "Series 3", "Category 1");
    localDefaultCategoryDataset.addValue(4L * l, "Series 1", "Category 2");
    localDefaultCategoryDataset.addValue(5L * l, "Series 2", "Category 2");
    localDefaultCategoryDataset.addValue(1L * l, "Series 3", "Category 2");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createStackedBarChart("Stacked Bar Chart Demo 6", "Category", "Value", paramCategoryDataset, PlotOrientation.HORIZONTAL, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    StackedBarRenderer localStackedBarRenderer = (StackedBarRenderer)localCategoryPlot.getRenderer();
    localStackedBarRenderer.setDrawBarOutline(false);
    long l = System.currentTimeMillis();
    localStackedBarRenderer.setBase(l);
    DateAxis localDateAxis = new DateAxis("Date");
    localDateAxis.setLowerMargin(0.0D);
    localCategoryPlot.setRangeAxis(localDateAxis);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    StackedBarChartDemo6 localStackedBarChartDemo6 = new StackedBarChartDemo6("Stacked Bar Chart Demo 6");
    localStackedBarChartDemo6.pack();
    RefineryUtilities.centerFrameOnScreen(localStackedBarChartDemo6);
    localStackedBarChartDemo6.setVisible(true);
  }
}