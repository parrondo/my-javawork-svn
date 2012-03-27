package demo;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.TableOrder;

public class MultiplePieChartDemo1 extends ApplicationFrame
{
  public MultiplePieChartDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(600, 380));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    double[][] arrayOfDouble = { { 3.0D, 4.0D, 3.0D, 5.0D }, { 5.0D, 7.0D, 6.0D, 8.0D }, { 5.0D, 7.0D, (0.0D / 0.0D), 3.0D }, { 1.0D, 2.0D, 3.0D, 4.0D }, { 2.0D, 3.0D, 2.0D, 3.0D } };
    CategoryDataset localCategoryDataset = DatasetUtilities.createCategoryDataset("Region ", "Sales/Q", arrayOfDouble);
    return localCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart1 = ChartFactory.createMultiplePieChart("Multiple Pie Chart", paramCategoryDataset, TableOrder.BY_ROW, true, true, false);
    MultiplePiePlot localMultiplePiePlot = (MultiplePiePlot)localJFreeChart1.getPlot();
    JFreeChart localJFreeChart2 = localMultiplePiePlot.getPieChart();
    PiePlot localPiePlot = (PiePlot)localJFreeChart2.getPlot();
    localPiePlot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}"));
    localPiePlot.setLabelFont(new Font("SansSerif", 0, 8));
    return localJFreeChart1;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    MultiplePieChartDemo1 localMultiplePieChartDemo1 = new MultiplePieChartDemo1("JFreeChart: MultiplePieChartDemo1.java");
    localMultiplePieChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localMultiplePieChartDemo1);
    localMultiplePieChartDemo1.setVisible(true);
  }
}