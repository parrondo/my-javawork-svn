package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.SubCategoryAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class StackedBarChartDemo5 extends ApplicationFrame
{
  public StackedBarChartDemo5(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(3396.0D, "S1", "C1");
    localDefaultCategoryDataset.addValue(1580.0D, "S2", "C1");
    localDefaultCategoryDataset.addValue(76.0D, "S3", "C1");
    localDefaultCategoryDataset.addValue(10100.0D, "S4", "C1");
    localDefaultCategoryDataset.addValue(3429.0D, "S1", "C2");
    localDefaultCategoryDataset.addValue(1562.0D, "S2", "C2");
    localDefaultCategoryDataset.addValue(61.0D, "S3", "C2");
    localDefaultCategoryDataset.addValue(-10100.0D, "S4", "C2");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createStackedBarChart("Stacked Bar Chart Demo 5", "Category", "Value", paramCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
    GroupedStackedBarRenderer localGroupedStackedBarRenderer = new GroupedStackedBarRenderer();
    KeyToGroupMap localKeyToGroupMap = new KeyToGroupMap("G1");
    localKeyToGroupMap.mapKeyToGroup("S1", "G1");
    localKeyToGroupMap.mapKeyToGroup("S2", "G1");
    localKeyToGroupMap.mapKeyToGroup("S3", "G2");
    localKeyToGroupMap.mapKeyToGroup("S4", "G3");
    localGroupedStackedBarRenderer.setSeriesToGroupMap(localKeyToGroupMap);
    localGroupedStackedBarRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
    localGroupedStackedBarRenderer.setBaseItemLabelsVisible(true);
    localGroupedStackedBarRenderer.setPositiveItemLabelPositionFallback(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER));
    localGroupedStackedBarRenderer.setItemMargin(0.1D);
    SubCategoryAxis localSubCategoryAxis = new SubCategoryAxis("Category / Group");
    localSubCategoryAxis.setCategoryMargin(0.05D);
    localSubCategoryAxis.addSubCategory("G1");
    localSubCategoryAxis.addSubCategory("G2");
    localSubCategoryAxis.addSubCategory("G3");
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setDomainAxis(localSubCategoryAxis);
    localCategoryPlot.setRenderer(localGroupedStackedBarRenderer);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    StackedBarChartDemo5 localStackedBarChartDemo5 = new StackedBarChartDemo5("Stacked Bar Chart Demo 5");
    localStackedBarChartDemo5.pack();
    RefineryUtilities.centerFrameOnScreen(localStackedBarChartDemo5);
    localStackedBarChartDemo5.setVisible(true);
  }
}