package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class LineChart3DDemo1 extends ApplicationFrame
{
  public LineChart3DDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(143.19999999999999D, "S1", "C1");
    localDefaultCategoryDataset.addValue(120.2D, "S1", "C2");
    localDefaultCategoryDataset.addValue(135.0D, "S1", "C3");
    localDefaultCategoryDataset.addValue(115.0D, "S1", "C4");
    localDefaultCategoryDataset.addValue(98.700000000000003D, "S2", "C1");
    localDefaultCategoryDataset.addValue(63.200000000000003D, "S2", "C2");
    localDefaultCategoryDataset.addValue(71.400000000000006D, "S2", "C3");
    localDefaultCategoryDataset.addValue(55.0D, "S2", "C4");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createLineChart3D("Line Chart 3D Demo 1", null, "Class Count", paramCategoryDataset, PlotOrientation.VERTICAL, false, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setAutoRangeIncludesZero(false);
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    LineChart3DDemo1 localLineChart3DDemo1 = new LineChart3DDemo1("JFreeChart: LineChart3DDemo1.java");
    localLineChart3DDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localLineChart3DDemo1);
    localLineChart3DDemo1.setVisible(true);
  }
}