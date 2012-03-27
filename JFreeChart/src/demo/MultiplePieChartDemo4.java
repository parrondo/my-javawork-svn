package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.TableOrder;

public class MultiplePieChartDemo4 extends ApplicationFrame
{
  public MultiplePieChartDemo4(String paramString)
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
    localDefaultCategoryDataset.addValue(3.2D, "Row 0", "Column 1");
    localDefaultCategoryDataset.addValue(1.8D, "Row 0", "Column 2");
    localDefaultCategoryDataset.addValue(0.2D, "Row 0", "Column 3");
    localDefaultCategoryDataset.addValue(4.1D, "Row 0", "Column 4");
    localDefaultCategoryDataset.addValue(9.800000000000001D, "Row 1", "Column 0");
    localDefaultCategoryDataset.addValue(6.3D, "Row 1", "Column 1");
    localDefaultCategoryDataset.addValue(0.1D, "Row 1", "Column 2");
    localDefaultCategoryDataset.addValue(1.9D, "Row 1", "Column 3");
    localDefaultCategoryDataset.addValue(9.6D, "Row 1", "Column 4");
    localDefaultCategoryDataset.addValue(7.0D, "Row 2", "Column 0");
    localDefaultCategoryDataset.addValue(5.2D, "Row 2", "Column 1");
    localDefaultCategoryDataset.addValue(2.8D, "Row 2", "Column 2");
    localDefaultCategoryDataset.addValue(8.800000000000001D, "Row 2", "Column 3");
    localDefaultCategoryDataset.addValue(7.2D, "Row 2", "Column 4");
    localDefaultCategoryDataset.addValue(9.5D, "Row 3", "Column 0");
    localDefaultCategoryDataset.addValue(1.2D, "Row 3", "Column 1");
    localDefaultCategoryDataset.addValue(4.5D, "Row 3", "Column 2");
    localDefaultCategoryDataset.addValue(4.4D, "Row 3", "Column 3");
    localDefaultCategoryDataset.addValue(0.2D, "Row 3", "Column 4");
    localDefaultCategoryDataset.addValue(3.5D, "Row 4", "Column 0");
    localDefaultCategoryDataset.addValue(6.7D, "Row 4", "Column 1");
    localDefaultCategoryDataset.addValue(9.0D, "Row 4", "Column 2");
    localDefaultCategoryDataset.addValue(1.0D, "Row 4", "Column 3");
    localDefaultCategoryDataset.addValue(5.2D, "Row 4", "Column 4");
    localDefaultCategoryDataset.addValue(5.1D, "Row 5", "Column 0");
    localDefaultCategoryDataset.addValue(6.7D, "Row 5", "Column 1");
    localDefaultCategoryDataset.addValue(0.9D, "Row 5", "Column 2");
    localDefaultCategoryDataset.addValue(3.3D, "Row 5", "Column 3");
    localDefaultCategoryDataset.addValue(3.9D, "Row 5", "Column 4");
    localDefaultCategoryDataset.addValue(5.6D, "Row 6", "Column 0");
    localDefaultCategoryDataset.addValue(5.6D, "Row 6", "Column 1");
    localDefaultCategoryDataset.addValue(5.6D, "Row 6", "Column 2");
    localDefaultCategoryDataset.addValue(5.6D, "Row 6", "Column 3");
    localDefaultCategoryDataset.addValue(5.6D, "Row 6", "Column 4");
    localDefaultCategoryDataset.addValue(7.5D, "Row 7", "Column 0");
    localDefaultCategoryDataset.addValue(9.0D, "Row 7", "Column 1");
    localDefaultCategoryDataset.addValue(3.4D, "Row 7", "Column 2");
    localDefaultCategoryDataset.addValue(4.1D, "Row 7", "Column 3");
    localDefaultCategoryDataset.addValue(0.5D, "Row 7", "Column 4");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart1 = ChartFactory.createMultiplePieChart3D("Multiple Pie Chart Demo 4", paramCategoryDataset, TableOrder.BY_COLUMN, false, true, false);
    MultiplePiePlot localMultiplePiePlot = (MultiplePiePlot)localJFreeChart1.getPlot();
    localMultiplePiePlot.setLimit(0.1D);
    JFreeChart localJFreeChart2 = localMultiplePiePlot.getPieChart();
    PiePlot localPiePlot = (PiePlot)localJFreeChart2.getPlot();
    localPiePlot.setIgnoreNullValues(true);
    localPiePlot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}"));
    localPiePlot.setMaximumLabelWidth(0.2D);
    return localJFreeChart1;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    MultiplePieChartDemo4 localMultiplePieChartDemo4 = new MultiplePieChartDemo4("JFreeChart: MultiplePieChartDemo4.java");
    localMultiplePieChartDemo4.pack();
    RefineryUtilities.centerFrameOnScreen(localMultiplePieChartDemo4);
    localMultiplePieChartDemo4.setVisible(true);
  }
}