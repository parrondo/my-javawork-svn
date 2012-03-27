package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class DualAxisDemo6 extends ApplicationFrame
{
  public DualAxisDemo6(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset1()
  {
    String str1 = "Series 1A";
    String str2 = "Series 1B";
    String str3 = "Category 1";
    String str4 = "Category 2";
    String str5 = "Category 3";
    String str6 = "Category 4";
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(1.0D, str1, str3);
    localDefaultCategoryDataset.addValue(4.0D, str1, str4);
    localDefaultCategoryDataset.addValue(3.0D, str1, str5);
    localDefaultCategoryDataset.addValue(5.0D, str1, str6);
    localDefaultCategoryDataset.addValue(3.0D, str2, str3);
    localDefaultCategoryDataset.addValue(6.0D, str2, str4);
    localDefaultCategoryDataset.addValue(1.0D, str2, str5);
    localDefaultCategoryDataset.addValue(5.0D, str2, str6);
    return localDefaultCategoryDataset;
  }

  private static CategoryDataset createDataset2()
  {
    String str1 = "Dummy 2";
    String str2 = "Series 2";
    String str3 = "Category 1";
    String str4 = "Category 2";
    String str5 = "Category 3";
    String str6 = "Category 4";
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(null, str1, str3);
    localDefaultCategoryDataset.addValue(null, str1, str4);
    localDefaultCategoryDataset.addValue(null, str1, str5);
    localDefaultCategoryDataset.addValue(null, str1, str6);
    localDefaultCategoryDataset.addValue(75.0D, str2, str3);
    localDefaultCategoryDataset.addValue(87.0D, str2, str4);
    localDefaultCategoryDataset.addValue(96.0D, str2, str5);
    localDefaultCategoryDataset.addValue(68.0D, str2, str6);
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset1, CategoryDataset paramCategoryDataset2)
  {
    CategoryAxis localCategoryAxis = new CategoryAxis("Category");
    NumberAxis localNumberAxis1 = new NumberAxis("Value");
    GroupedStackedBarRenderer localGroupedStackedBarRenderer = new GroupedStackedBarRenderer();
    KeyToGroupMap localKeyToGroupMap = new KeyToGroupMap("G1");
    localKeyToGroupMap.mapKeyToGroup("Series 1A", "G1");
    localKeyToGroupMap.mapKeyToGroup("Series 1B", "G1");
    localKeyToGroupMap.mapKeyToGroup("NOTHING", "G2");
    localGroupedStackedBarRenderer.setSeriesToGroupMap(localKeyToGroupMap);
    CategoryPlot local1 = new CategoryPlot(paramCategoryDataset1, localCategoryAxis, localNumberAxis1, localGroupedStackedBarRenderer)
    {
      public LegendItemCollection getLegendItems()
      {
        LegendItemCollection localLegendItemCollection = new LegendItemCollection();
        localLegendItemCollection.addAll(getRenderer().getLegendItems());
        CategoryDataset localCategoryDataset = getDataset(1);
        if (localCategoryDataset != null)
        {
          CategoryItemRenderer localCategoryItemRenderer = getRenderer(1);
          if (localCategoryItemRenderer != null)
          {
            LegendItem localLegendItem = localCategoryItemRenderer.getLegendItem(1, 1);
            localLegendItemCollection.add(localLegendItem);
          }
        }
        return localLegendItemCollection;
      }
    };
    JFreeChart localJFreeChart = new JFreeChart("Dual Axis Bar Chart", local1);
    local1.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
    local1.setDataset(1, paramCategoryDataset2);
    local1.mapDatasetToRangeAxis(1, 1);
    NumberAxis localNumberAxis2 = new NumberAxis("Secondary");
    local1.setRangeAxis(1, localNumberAxis2);
    local1.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
    BarRenderer localBarRenderer = new BarRenderer();
    local1.setRenderer(1, localBarRenderer);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset1(), createDataset2());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    DualAxisDemo6 localDualAxisDemo6 = new DualAxisDemo6("JFreeChart: DualAxisDemo6.java");
    localDualAxisDemo6.pack();
    RefineryUtilities.centerFrameOnScreen(localDualAxisDemo6);
    localDualAxisDemo6.setVisible(true);
  }
}