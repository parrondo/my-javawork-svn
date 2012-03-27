package demo;

import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class StackedBarChart3DDemo4 extends ApplicationFrame
{
  public StackedBarChart3DDemo4(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  public static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(10.0D, "Series 1", "C1");
    localDefaultCategoryDataset.addValue(5.0D, "Series 1", "C2");
    localDefaultCategoryDataset.addValue(6.0D, "Series 1", "C3");
    localDefaultCategoryDataset.addValue(7.0D, "Series 1", "C4");
    localDefaultCategoryDataset.addValue(8.0D, "Series 1", "C5");
    localDefaultCategoryDataset.addValue(9.0D, "Series 1", "C6");
    localDefaultCategoryDataset.addValue(10.0D, "Series 1", "C7");
    localDefaultCategoryDataset.addValue(11.0D, "Series 1", "C8");
    localDefaultCategoryDataset.addValue(3.0D, "Series 1", "C9");
    localDefaultCategoryDataset.addValue(4.0D, "Series 2", "C1");
    localDefaultCategoryDataset.addValue(7.0D, "Series 2", "C2");
    localDefaultCategoryDataset.addValue(17.0D, "Series 2", "C3");
    localDefaultCategoryDataset.addValue(15.0D, "Series 2", "C4");
    localDefaultCategoryDataset.addValue(6.0D, "Series 2", "C5");
    localDefaultCategoryDataset.addValue(8.0D, "Series 2", "C6");
    localDefaultCategoryDataset.addValue(9.0D, "Series 2", "C7");
    localDefaultCategoryDataset.addValue(13.0D, "Series 2", "C8");
    localDefaultCategoryDataset.addValue(7.0D, "Series 2", "C9");
    localDefaultCategoryDataset.addValue(15.0D, "Series 3", "C1");
    localDefaultCategoryDataset.addValue(14.0D, "Series 3", "C2");
    localDefaultCategoryDataset.addValue(12.0D, "Series 3", "C3");
    localDefaultCategoryDataset.addValue(11.0D, "Series 3", "C4");
    localDefaultCategoryDataset.addValue(10.0D, "Series 3", "C5");
    localDefaultCategoryDataset.addValue(0.0D, "Series 3", "C6");
    localDefaultCategoryDataset.addValue(7.0D, "Series 3", "C7");
    localDefaultCategoryDataset.addValue(9.0D, "Series 3", "C8");
    localDefaultCategoryDataset.addValue(11.0D, "Series 3", "C9");
    localDefaultCategoryDataset.addValue(14.0D, "Series 4", "C1");
    localDefaultCategoryDataset.addValue(3.0D, "Series 4", "C2");
    localDefaultCategoryDataset.addValue(7.0D, "Series 4", "C3");
    localDefaultCategoryDataset.addValue(0.0D, "Series 4", "C4");
    localDefaultCategoryDataset.addValue(9.0D, "Series 4", "C5");
    localDefaultCategoryDataset.addValue(6.0D, "Series 4", "C6");
    localDefaultCategoryDataset.addValue(7.0D, "Series 4", "C7");
    localDefaultCategoryDataset.addValue(9.0D, "Series 4", "C8");
    localDefaultCategoryDataset.addValue(10.0D, "Series 4", "C9");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createStackedBarChart3D("Stacked Bar Chart 3D Demo 4", "Category", "Value", paramCategoryDataset, PlotOrientation.HORIZONTAL, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setNumberFormatOverride(new DecimalFormat("0%"));
    StackedBarRenderer3D localStackedBarRenderer3D = (StackedBarRenderer3D)localCategoryPlot.getRenderer();
    localStackedBarRenderer3D.setRenderAsPercentages(true);
    localStackedBarRenderer3D.setDrawBarOutline(false);
    localStackedBarRenderer3D.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{3}", NumberFormat.getIntegerInstance(), new DecimalFormat("0.0%")));
    localStackedBarRenderer3D.setBaseItemLabelsVisible(true);
    localStackedBarRenderer3D.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
    localStackedBarRenderer3D.setBaseNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    StackedBarChart3DDemo4 localStackedBarChart3DDemo4 = new StackedBarChart3DDemo4("Stacked Bar Chart 3D Demo 4");
    localStackedBarChart3DDemo4.pack();
    RefineryUtilities.centerFrameOnScreen(localStackedBarChart3DDemo4);
    localStackedBarChart3DDemo4.setVisible(true);
  }
}