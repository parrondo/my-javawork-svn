package demo;

import java.awt.Dimension;
import java.text.DecimalFormat;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.IntervalBarRenderer;
import org.jfree.data.category.DefaultIntervalCategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class IntervalBarChartDemo1 extends ApplicationFrame
{
  public IntervalBarChartDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static IntervalCategoryDataset createDataset()
  {
    double[] arrayOfDouble1 = { 0.1D, 0.2D, 0.3D };
    double[] arrayOfDouble2 = { 0.3D, 0.4D, 0.5D };
    double[] arrayOfDouble3 = { 0.5D, 0.6D, 0.7D };
    double[] arrayOfDouble4 = { 0.7D, 0.8D, 0.9D };
    double[][] arrayOfDouble5 = { arrayOfDouble1, arrayOfDouble2 };
    double[][] arrayOfDouble6 = { arrayOfDouble3, arrayOfDouble4 };
    DefaultIntervalCategoryDataset localDefaultIntervalCategoryDataset = new DefaultIntervalCategoryDataset(arrayOfDouble5, arrayOfDouble6);
    return localDefaultIntervalCategoryDataset;
  }

  private static JFreeChart createChart(IntervalCategoryDataset paramIntervalCategoryDataset)
  {
    CategoryAxis localCategoryAxis = new CategoryAxis("Category");
    NumberAxis localNumberAxis = new NumberAxis("Percentage");
    localNumberAxis.setNumberFormatOverride(new DecimalFormat("0.00%"));
    IntervalBarRenderer localIntervalBarRenderer = new IntervalBarRenderer();
    CategoryPlot localCategoryPlot = new CategoryPlot(paramIntervalCategoryDataset, localCategoryAxis, localNumberAxis, localIntervalBarRenderer);
    JFreeChart localJFreeChart = new JFreeChart("IntervalBarChartDemo1", localCategoryPlot);
    localCategoryPlot.setDomainGridlinesVisible(true);
    localCategoryPlot.setRangePannable(true);
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
    IntervalBarChartDemo1 localIntervalBarChartDemo1 = new IntervalBarChartDemo1("JFreeChart: IntervalBarChartDemo1.java");
    localIntervalBarChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localIntervalBarChartDemo1);
    localIntervalBarChartDemo1.setVisible(true);
  }
}