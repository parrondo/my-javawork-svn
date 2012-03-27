package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.TableOrder;

public class MultiplePieChartDemo3 extends ApplicationFrame
{
  public MultiplePieChartDemo3(String paramString)
  {
    super(paramString);
    CategoryDataset localCategoryDataset = createDataset();
    JFreeChart localJFreeChart = createChart(localCategoryDataset);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart, true, true, true, false, true);
    localChartPanel.setPreferredSize(new Dimension(600, 380));
    setContentPane(localChartPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(5.6D, "Row 0", "Column 0");
    localDefaultCategoryDataset.addValue(4.3D, "Row 0", "Column 1");
    localDefaultCategoryDataset.addValue(6.7D, "Row 0", "Column 2");
    localDefaultCategoryDataset.addValue(4.4D, "Row 0", "Column 3");
    localDefaultCategoryDataset.addValue(6.1D, "Row 0", "Column 4");
    localDefaultCategoryDataset.addValue(5.8D, "Row 1", "Column 0");
    localDefaultCategoryDataset.addValue(3.2D, "Row 1", "Column 1");
    localDefaultCategoryDataset.addValue(4.5D, "Row 1", "Column 2");
    localDefaultCategoryDataset.addValue(7.0D, "Row 1", "Column 3");
    localDefaultCategoryDataset.addValue(5.8D, "Row 1", "Column 4");
    localDefaultCategoryDataset.addValue(5.3D, "Row 2", "Column 0");
    localDefaultCategoryDataset.addValue(6.7D, "Row 2", "Column 1");
    localDefaultCategoryDataset.addValue(7.1D, "Row 2", "Column 2");
    localDefaultCategoryDataset.addValue(4.2D, "Row 2", "Column 3");
    localDefaultCategoryDataset.addValue(9.0D, "Row 2", "Column 4");
    localDefaultCategoryDataset.addValue(5.6D, "Row 3", "Column 0");
    localDefaultCategoryDataset.addValue(5.6D, "Row 3", "Column 1");
    localDefaultCategoryDataset.addValue(5.6D, "Row 3", "Column 2");
    localDefaultCategoryDataset.addValue(5.6D, "Row 3", "Column 3");
    localDefaultCategoryDataset.addValue(5.6D, "Row 3", "Column 4");
    localDefaultCategoryDataset.addValue(5.6D, "Row 4", "Column 0");
    localDefaultCategoryDataset.addValue(5.6D, "Row 4", "Column 1");
    localDefaultCategoryDataset.addValue(5.6D, "Row 4", "Column 2");
    localDefaultCategoryDataset.addValue(5.6D, "Row 4", "Column 3");
    localDefaultCategoryDataset.addValue(5.6D, "Row 4", "Column 4");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createMultiplePieChart3D("Multiple Pie Chart Demo 3", paramCategoryDataset, TableOrder.BY_COLUMN, true, true, false);
    MultiplePiePlot localMultiplePiePlot = (MultiplePiePlot)localJFreeChart.getPlot();
    PiePlot localPiePlot = (PiePlot)localMultiplePiePlot.getPieChart().getPlot();
    localPiePlot.setMaximumLabelWidth(0.18D);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    MultiplePieChartDemo3 localMultiplePieChartDemo3 = new MultiplePieChartDemo3("JFreeChart: MultiplePieChartDemo3.java");
    localMultiplePieChartDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localMultiplePieChartDemo3);
    localMultiplePieChartDemo3.setVisible(true);
  }
}