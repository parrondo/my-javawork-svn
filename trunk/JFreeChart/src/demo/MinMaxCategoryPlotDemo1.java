package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.MinMaxCategoryRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class MinMaxCategoryPlotDemo1 extends ApplicationFrame
{
  public MinMaxCategoryPlotDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  public static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(1.0D, "First", "C1");
    localDefaultCategoryDataset.addValue(4.0D, "First", "C2");
    localDefaultCategoryDataset.addValue(3.0D, "First", "C3");
    localDefaultCategoryDataset.addValue(5.0D, "First", "C4");
    localDefaultCategoryDataset.addValue(5.0D, "First", "C5");
    localDefaultCategoryDataset.addValue(7.0D, "First", "C6");
    localDefaultCategoryDataset.addValue(7.0D, "First", "C7");
    localDefaultCategoryDataset.addValue(8.0D, "First", "C8");
    localDefaultCategoryDataset.addValue(5.0D, "Second", "C1");
    localDefaultCategoryDataset.addValue(7.0D, "Second", "C2");
    localDefaultCategoryDataset.addValue(6.0D, "Second", "C3");
    localDefaultCategoryDataset.addValue(8.0D, "Second", "C4");
    localDefaultCategoryDataset.addValue(4.0D, "Second", "C5");
    localDefaultCategoryDataset.addValue(4.0D, "Second", "C6");
    localDefaultCategoryDataset.addValue(2.0D, "Second", "C7");
    localDefaultCategoryDataset.addValue(1.0D, "Second", "C8");
    localDefaultCategoryDataset.addValue(4.0D, "Third", "C1");
    localDefaultCategoryDataset.addValue(3.0D, "Third", "C2");
    localDefaultCategoryDataset.addValue(2.0D, "Third", "C3");
    localDefaultCategoryDataset.addValue(3.0D, "Third", "C4");
    localDefaultCategoryDataset.addValue(6.0D, "Third", "C5");
    localDefaultCategoryDataset.addValue(3.0D, "Third", "C6");
    localDefaultCategoryDataset.addValue(4.0D, "Third", "C7");
    localDefaultCategoryDataset.addValue(3.0D, "Third", "C8");
    return localDefaultCategoryDataset;
  }

  public static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart("Min/Max Category Plot", "Category", "Value", paramCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setRangePannable(true);
    MinMaxCategoryRenderer localMinMaxCategoryRenderer = new MinMaxCategoryRenderer();
    localMinMaxCategoryRenderer.setDrawLines(false);
    localCategoryPlot.setRenderer(localMinMaxCategoryRenderer);
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
    MinMaxCategoryPlotDemo1 localMinMaxCategoryPlotDemo1 = new MinMaxCategoryPlotDemo1("JFreeChart: MinMaxCategoryPlotDemo1.java");
    localMinMaxCategoryPlotDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localMinMaxCategoryPlotDemo1);
    localMinMaxCategoryPlotDemo1.setVisible(true);
  }
}