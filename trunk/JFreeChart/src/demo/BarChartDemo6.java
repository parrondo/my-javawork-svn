package demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class BarChartDemo6 extends ApplicationFrame
{
  public BarChartDemo6(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(83.0D, "First", "Factor 1");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart(null, "Category", "Score (%)", paramCategoryDataset, PlotOrientation.HORIZONTAL, false, true, false);
    localJFreeChart.setBackgroundPaint(Color.yellow);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setInsets(new RectangleInsets(0.0D, 0.0D, 0.0D, 0.0D));
    localCategoryPlot.setAxisOffset(RectangleInsets.ZERO_INSETS);
    localCategoryPlot.setRangeGridlinesVisible(false);
    CategoryAxis localCategoryAxis = localCategoryPlot.getDomainAxis();
    localCategoryAxis.setLowerMargin(0.2D);
    localCategoryAxis.setUpperMargin(0.2D);
    localCategoryAxis.setVisible(false);
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setRange(0.0D, 100.0D);
    localNumberAxis.setVisible(false);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    BarChartDemo6 localBarChartDemo6 = new BarChartDemo6("JFreeChart: BarChartDemo6.java");
    localBarChartDemo6.pack();
    RefineryUtilities.centerFrameOnScreen(localBarChartDemo6);
    localBarChartDemo6.setVisible(true);
  }
}