package demo;

import java.awt.Dimension;
import java.awt.Font;
import java.text.NumberFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class ItemLabelDemo3 extends ApplicationFrame
{
  public ItemLabelDemo3(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(51.0D, "Series 1", "Apples");
    localDefaultCategoryDataset.addValue(44.299999999999997D, "Series 1", "Bananas");
    localDefaultCategoryDataset.addValue(93.0D, "Series 1", "Oranges");
    localDefaultCategoryDataset.addValue(35.600000000000001D, "Series 1", "Pears");
    localDefaultCategoryDataset.addValue(75.099999999999994D, "Series 1", "Plums");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart("Item Label Demo 3", "Category", "Value", paramCategoryDataset, PlotOrientation.VERTICAL, false, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setRangePannable(true);
    localCategoryPlot.setRangeZeroBaselineVisible(true);
    CategoryAxis localCategoryAxis = localCategoryPlot.getDomainAxis();
    localCategoryAxis.setVisible(false);
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setUpperMargin(0.15D);
    CategoryItemRenderer localCategoryItemRenderer = localCategoryPlot.getRenderer();
    StandardCategoryItemLabelGenerator localStandardCategoryItemLabelGenerator = new StandardCategoryItemLabelGenerator("{1}", NumberFormat.getInstance());
    localCategoryItemRenderer.setBaseItemLabelGenerator(localStandardCategoryItemLabelGenerator);
    localCategoryItemRenderer.setBaseItemLabelFont(new Font("SansSerif", 0, 12));
    localCategoryItemRenderer.setBaseItemLabelsVisible(true);
    localCategoryItemRenderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER, -1.570796326794897D));
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
    ItemLabelDemo3 localItemLabelDemo3 = new ItemLabelDemo3("JFreeChart: ItemLabelDemo3.java");
    localItemLabelDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localItemLabelDemo3);
    localItemLabelDemo3.setVisible(true);
  }
}