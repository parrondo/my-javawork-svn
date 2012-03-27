package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.SubCategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.StandardGradientPaintTransformer;

public class StackedBarChartDemo4 extends ApplicationFrame
{
  public StackedBarChartDemo4(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(590, 350));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(20.300000000000001D, "Product 1 (US)", "Jan 04");
    localDefaultCategoryDataset.addValue(27.199999999999999D, "Product 1 (US)", "Feb 04");
    localDefaultCategoryDataset.addValue(19.699999999999999D, "Product 1 (US)", "Mar 04");
    localDefaultCategoryDataset.addValue(19.399999999999999D, "Product 1 (Europe)", "Jan 04");
    localDefaultCategoryDataset.addValue(10.9D, "Product 1 (Europe)", "Feb 04");
    localDefaultCategoryDataset.addValue(18.399999999999999D, "Product 1 (Europe)", "Mar 04");
    localDefaultCategoryDataset.addValue(16.5D, "Product 1 (Asia)", "Jan 04");
    localDefaultCategoryDataset.addValue(15.9D, "Product 1 (Asia)", "Feb 04");
    localDefaultCategoryDataset.addValue(16.100000000000001D, "Product 1 (Asia)", "Mar 04");
    localDefaultCategoryDataset.addValue(13.199999999999999D, "Product 1 (Middle East)", "Jan 04");
    localDefaultCategoryDataset.addValue(14.4D, "Product 1 (Middle East)", "Feb 04");
    localDefaultCategoryDataset.addValue(13.699999999999999D, "Product 1 (Middle East)", "Mar 04");
    localDefaultCategoryDataset.addValue(23.300000000000001D, "Product 2 (US)", "Jan 04");
    localDefaultCategoryDataset.addValue(16.199999999999999D, "Product 2 (US)", "Feb 04");
    localDefaultCategoryDataset.addValue(28.699999999999999D, "Product 2 (US)", "Mar 04");
    localDefaultCategoryDataset.addValue(12.699999999999999D, "Product 2 (Europe)", "Jan 04");
    localDefaultCategoryDataset.addValue(17.899999999999999D, "Product 2 (Europe)", "Feb 04");
    localDefaultCategoryDataset.addValue(12.6D, "Product 2 (Europe)", "Mar 04");
    localDefaultCategoryDataset.addValue(15.4D, "Product 2 (Asia)", "Jan 04");
    localDefaultCategoryDataset.addValue(21.0D, "Product 2 (Asia)", "Feb 04");
    localDefaultCategoryDataset.addValue(11.1D, "Product 2 (Asia)", "Mar 04");
    localDefaultCategoryDataset.addValue(23.800000000000001D, "Product 2 (Middle East)", "Jan 04");
    localDefaultCategoryDataset.addValue(23.399999999999999D, "Product 2 (Middle East)", "Feb 04");
    localDefaultCategoryDataset.addValue(19.300000000000001D, "Product 2 (Middle East)", "Mar 04");
    localDefaultCategoryDataset.addValue(11.9D, "Product 3 (US)", "Jan 04");
    localDefaultCategoryDataset.addValue(31.0D, "Product 3 (US)", "Feb 04");
    localDefaultCategoryDataset.addValue(22.699999999999999D, "Product 3 (US)", "Mar 04");
    localDefaultCategoryDataset.addValue(15.300000000000001D, "Product 3 (Europe)", "Jan 04");
    localDefaultCategoryDataset.addValue(14.4D, "Product 3 (Europe)", "Feb 04");
    localDefaultCategoryDataset.addValue(25.300000000000001D, "Product 3 (Europe)", "Mar 04");
    localDefaultCategoryDataset.addValue(23.899999999999999D, "Product 3 (Asia)", "Jan 04");
    localDefaultCategoryDataset.addValue(19.0D, "Product 3 (Asia)", "Feb 04");
    localDefaultCategoryDataset.addValue(10.1D, "Product 3 (Asia)", "Mar 04");
    localDefaultCategoryDataset.addValue(13.199999999999999D, "Product 3 (Middle East)", "Jan 04");
    localDefaultCategoryDataset.addValue(15.5D, "Product 3 (Middle East)", "Feb 04");
    localDefaultCategoryDataset.addValue(10.1D, "Product 3 (Middle East)", "Mar 04");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createStackedBarChart("Stacked Bar Chart Demo 4", "Category", "Value", paramCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
    GroupedStackedBarRenderer localGroupedStackedBarRenderer = new GroupedStackedBarRenderer();
    KeyToGroupMap localKeyToGroupMap = new KeyToGroupMap("G1");
    localKeyToGroupMap.mapKeyToGroup("Product 1 (US)", "G1");
    localKeyToGroupMap.mapKeyToGroup("Product 1 (Europe)", "G1");
    localKeyToGroupMap.mapKeyToGroup("Product 1 (Asia)", "G1");
    localKeyToGroupMap.mapKeyToGroup("Product 1 (Middle East)", "G1");
    localKeyToGroupMap.mapKeyToGroup("Product 2 (US)", "G2");
    localKeyToGroupMap.mapKeyToGroup("Product 2 (Europe)", "G2");
    localKeyToGroupMap.mapKeyToGroup("Product 2 (Asia)", "G2");
    localKeyToGroupMap.mapKeyToGroup("Product 2 (Middle East)", "G2");
    localKeyToGroupMap.mapKeyToGroup("Product 3 (US)", "G3");
    localKeyToGroupMap.mapKeyToGroup("Product 3 (Europe)", "G3");
    localKeyToGroupMap.mapKeyToGroup("Product 3 (Asia)", "G3");
    localKeyToGroupMap.mapKeyToGroup("Product 3 (Middle East)", "G3");
    localGroupedStackedBarRenderer.setSeriesToGroupMap(localKeyToGroupMap);
    localGroupedStackedBarRenderer.setItemMargin(0.1D);
    localGroupedStackedBarRenderer.setDrawBarOutline(false);
    SubCategoryAxis localSubCategoryAxis = new SubCategoryAxis("Product / Month");
    localSubCategoryAxis.setCategoryMargin(0.05D);
    localSubCategoryAxis.addSubCategory("Product 1");
    localSubCategoryAxis.addSubCategory("Product 2");
    localSubCategoryAxis.addSubCategory("Product 3");
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setDomainAxis(localSubCategoryAxis);
    localCategoryPlot.setRenderer(localGroupedStackedBarRenderer);
    localCategoryPlot.setFixedLegendItems(createLegendItems());
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    localSubCategoryAxis.setSubLabelFont(new Font("Tahoma", 2, 10));
    GradientPaint localGradientPaint1 = new GradientPaint(0.0F, 0.0F, new Color(34, 34, 255), 0.0F, 0.0F, new Color(136, 136, 255));
    localGroupedStackedBarRenderer.setSeriesPaint(0, localGradientPaint1);
    localGroupedStackedBarRenderer.setSeriesPaint(4, localGradientPaint1);
    localGroupedStackedBarRenderer.setSeriesPaint(8, localGradientPaint1);
    GradientPaint localGradientPaint2 = new GradientPaint(0.0F, 0.0F, new Color(34, 255, 34), 0.0F, 0.0F, new Color(136, 255, 136));
    localGroupedStackedBarRenderer.setSeriesPaint(1, localGradientPaint2);
    localGroupedStackedBarRenderer.setSeriesPaint(5, localGradientPaint2);
    localGroupedStackedBarRenderer.setSeriesPaint(9, localGradientPaint2);
    GradientPaint localGradientPaint3 = new GradientPaint(0.0F, 0.0F, new Color(255, 34, 34), 0.0F, 0.0F, new Color(255, 136, 136));
    localGroupedStackedBarRenderer.setSeriesPaint(2, localGradientPaint3);
    localGroupedStackedBarRenderer.setSeriesPaint(6, localGradientPaint3);
    localGroupedStackedBarRenderer.setSeriesPaint(10, localGradientPaint3);
    GradientPaint localGradientPaint4 = new GradientPaint(0.0F, 0.0F, new Color(255, 255, 34), 0.0F, 0.0F, new Color(255, 255, 136));
    localGroupedStackedBarRenderer.setSeriesPaint(3, localGradientPaint4);
    localGroupedStackedBarRenderer.setSeriesPaint(7, localGradientPaint4);
    localGroupedStackedBarRenderer.setSeriesPaint(11, localGradientPaint4);
    localGroupedStackedBarRenderer.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.HORIZONTAL));
    return localJFreeChart;
  }

  private static LegendItemCollection createLegendItems()
  {
    LegendItemCollection localLegendItemCollection = new LegendItemCollection();
    LegendItem localLegendItem1 = new LegendItem("US", "-", null, null, Plot.DEFAULT_LEGEND_ITEM_BOX, new Color(34, 34, 255));
    LegendItem localLegendItem2 = new LegendItem("Europe", "-", null, null, Plot.DEFAULT_LEGEND_ITEM_BOX, new Color(34, 255, 34));
    LegendItem localLegendItem3 = new LegendItem("Asia", "-", null, null, Plot.DEFAULT_LEGEND_ITEM_BOX, new Color(255, 34, 34));
    LegendItem localLegendItem4 = new LegendItem("Middle East", "-", null, null, Plot.DEFAULT_LEGEND_ITEM_BOX, new Color(255, 255, 34));
    localLegendItemCollection.add(localLegendItem1);
    localLegendItemCollection.add(localLegendItem2);
    localLegendItemCollection.add(localLegendItem3);
    localLegendItemCollection.add(localLegendItem4);
    return localLegendItemCollection;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    StackedBarChartDemo4 localStackedBarChartDemo4 = new StackedBarChartDemo4("Stacked Bar Chart Demo 4");
    localStackedBarChartDemo4.pack();
    RefineryUtilities.centerFrameOnScreen(localStackedBarChartDemo4);
    localStackedBarChartDemo4.setVisible(true);
  }
}