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
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class DualAxisDemo5 extends ApplicationFrame
{
  public DualAxisDemo5(String paramString)
  {
    super(paramString);
    CategoryDataset localCategoryDataset1 = createDataset1();
    CategoryDataset localCategoryDataset2 = createDataset2();
    JFreeChart localJFreeChart = createChart(localCategoryDataset1, localCategoryDataset2);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localChartPanel);
  }

  private static CategoryDataset createDataset1()
  {
    String str1 = "Series 1";
    String str2 = "Dummy 1";
    String str3 = "Category 1";
    String str4 = "Category 2";
    String str5 = "Category 3";
    String str6 = "Category 4";
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(1.0D, str1, str3);
    localDefaultCategoryDataset.addValue(4.0D, str1, str4);
    localDefaultCategoryDataset.addValue(3.0D, str1, str5);
    localDefaultCategoryDataset.addValue(5.0D, str1, str6);
    localDefaultCategoryDataset.addValue(null, str2, str3);
    localDefaultCategoryDataset.addValue(null, str2, str4);
    localDefaultCategoryDataset.addValue(null, str2, str5);
    localDefaultCategoryDataset.addValue(null, str2, str6);
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
    BarRenderer localBarRenderer1 = new BarRenderer();
    CategoryPlot local1 = new CategoryPlot(paramCategoryDataset1, localCategoryAxis, localNumberAxis1, localBarRenderer1)
    {
      public LegendItemCollection getLegendItems()
      {
        Object localObject2;
        LegendItemCollection localLegendItemCollection = new LegendItemCollection();
        CategoryDataset localCategoryDataset = getDataset();
        if (localCategoryDataset != null)
        {
          CategoryItemRenderer localObject1 = getRenderer();
          if (localObject1 != null)
          {
            localObject2 = ((CategoryItemRenderer)localObject1).getLegendItem(0, 0);
            localLegendItemCollection.add((LegendItem)localObject2);
          }
        }
        Object localObject1 = getDataset(1);
        if (localObject1 != null)
        {
          localObject2 = getRenderer(1);
          if (localObject2 != null)
          {
            LegendItem localLegendItem = ((CategoryItemRenderer)localObject2).getLegendItem(1, 1);
            localLegendItemCollection.add(localLegendItem);
          }
        }
        return ((LegendItemCollection)(LegendItemCollection)localLegendItemCollection);
      }
    };
    JFreeChart localJFreeChart = new JFreeChart("Dual Axis Bar Chart", local1);
    local1.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
    local1.setDataset(1, paramCategoryDataset2);
    local1.mapDatasetToRangeAxis(1, 1);
    NumberAxis localNumberAxis2 = new NumberAxis("Secondary");
    local1.setRangeAxis(1, localNumberAxis2);
    local1.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
    BarRenderer localBarRenderer2 = new BarRenderer();
    local1.setRenderer(1, localBarRenderer2);
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
    DualAxisDemo5 localDualAxisDemo5 = new DualAxisDemo5("JFreeChart: DualAxisDemo5.java");
    localDualAxisDemo5.pack();
    RefineryUtilities.centerFrameOnScreen(localDualAxisDemo5);
    localDualAxisDemo5.setVisible(true);
  }
}