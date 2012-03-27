package demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.text.NumberFormat;
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

public class MultiplePieChartDemo2 extends ApplicationFrame
{
  public MultiplePieChartDemo2(String paramString)
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
    double[][] arrayOfDouble = { { 3.0D, 4.0D, 3.0D, 5.0D }, { 5.0D, 7.0D, 6.0D, 8.0D }, { 5.0D, 7.0D, 3.0D, 8.0D }, { 1.0D, 2.0D, 3.0D, 4.0D }, { 2.0D, 3.0D, 2.0D, 3.0D } };
    CategoryDataset localCategoryDataset = DatasetUtilities.createCategoryDataset("Region ", "Sales/Q", arrayOfDouble);
    return localCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart1 = ChartFactory.createMultiplePieChart("Multiple Pie Chart", paramCategoryDataset, TableOrder.BY_COLUMN, true, true, false);
    MultiplePiePlot localMultiplePiePlot = (MultiplePiePlot)localJFreeChart1.getPlot();
    localMultiplePiePlot.setBackgroundPaint(Color.white);
    localMultiplePiePlot.setOutlineStroke(new BasicStroke(1.0F));
    JFreeChart localJFreeChart2 = localMultiplePiePlot.getPieChart();
    PiePlot localPiePlot = (PiePlot)localJFreeChart2.getPlot();
    localPiePlot.setBackgroundPaint(null);
    localPiePlot.setOutlineStroke(null);
    localPiePlot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} ({2})", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()));
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
    MultiplePieChartDemo2 localMultiplePieChartDemo2 = new MultiplePieChartDemo2("JFreeChart: MultiplePieChartDemo2.java");
    localMultiplePieChartDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localMultiplePieChartDemo2);
    localMultiplePieChartDemo2.setVisible(true);
  }
}