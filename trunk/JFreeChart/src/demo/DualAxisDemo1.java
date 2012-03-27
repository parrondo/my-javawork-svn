package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.EmptyBlock;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.CompositeTitle;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class DualAxisDemo1 extends ApplicationFrame
{
  public DualAxisDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset1()
  {
    String str1 = "S1";
    String str2 = "S2";
    String str3 = "S3";
    String str4 = "Category 1";
    String str5 = "Category 2";
    String str6 = "Category 3";
    String str7 = "Category 4";
    String str8 = "Category 5";
    String str9 = "Category 6";
    String str10 = "Category 7";
    String str11 = "Category 8";
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(1.0D, str1, str4);
    localDefaultCategoryDataset.addValue(4.0D, str1, str5);
    localDefaultCategoryDataset.addValue(3.0D, str1, str6);
    localDefaultCategoryDataset.addValue(5.0D, str1, str7);
    localDefaultCategoryDataset.addValue(5.0D, str1, str8);
    localDefaultCategoryDataset.addValue(7.0D, str1, str9);
    localDefaultCategoryDataset.addValue(7.0D, str1, str10);
    localDefaultCategoryDataset.addValue(8.0D, str1, str11);
    localDefaultCategoryDataset.addValue(5.0D, str2, str4);
    localDefaultCategoryDataset.addValue(7.0D, str2, str5);
    localDefaultCategoryDataset.addValue(6.0D, str2, str6);
    localDefaultCategoryDataset.addValue(8.0D, str2, str7);
    localDefaultCategoryDataset.addValue(4.0D, str2, str8);
    localDefaultCategoryDataset.addValue(4.0D, str2, str9);
    localDefaultCategoryDataset.addValue(2.0D, str2, str10);
    localDefaultCategoryDataset.addValue(1.0D, str2, str11);
    localDefaultCategoryDataset.addValue(4.0D, str3, str4);
    localDefaultCategoryDataset.addValue(3.0D, str3, str5);
    localDefaultCategoryDataset.addValue(2.0D, str3, str6);
    localDefaultCategoryDataset.addValue(3.0D, str3, str7);
    localDefaultCategoryDataset.addValue(6.0D, str3, str8);
    localDefaultCategoryDataset.addValue(3.0D, str3, str9);
    localDefaultCategoryDataset.addValue(4.0D, str3, str10);
    localDefaultCategoryDataset.addValue(3.0D, str3, str11);
    return localDefaultCategoryDataset;
  }

  private static CategoryDataset createDataset2()
  {
    String str1 = "S4";
    String str2 = "Category 1";
    String str3 = "Category 2";
    String str4 = "Category 3";
    String str5 = "Category 4";
    String str6 = "Category 5";
    String str7 = "Category 6";
    String str8 = "Category 7";
    String str9 = "Category 8";
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(15.0D, str1, str2);
    localDefaultCategoryDataset.addValue(24.0D, str1, str3);
    localDefaultCategoryDataset.addValue(31.0D, str1, str4);
    localDefaultCategoryDataset.addValue(25.0D, str1, str5);
    localDefaultCategoryDataset.addValue(56.0D, str1, str6);
    localDefaultCategoryDataset.addValue(37.0D, str1, str7);
    localDefaultCategoryDataset.addValue(77.0D, str1, str8);
    localDefaultCategoryDataset.addValue(18.0D, str1, str9);
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart()
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart("DualAxisDemo1", "Category", "Value", createDataset1(), PlotOrientation.VERTICAL, false, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    CategoryDataset localCategoryDataset = createDataset2();
    localCategoryPlot.setDataset(1, localCategoryDataset);
    localCategoryPlot.mapDatasetToRangeAxis(1, 1);
    CategoryAxis localCategoryAxis = localCategoryPlot.getDomainAxis();
    localCategoryAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
    NumberAxis localNumberAxis = new NumberAxis("Secondary");
    localCategoryPlot.setRangeAxis(1, localNumberAxis);
    LineAndShapeRenderer localLineAndShapeRenderer = new LineAndShapeRenderer();
    localLineAndShapeRenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
    localCategoryPlot.setRenderer(1, localLineAndShapeRenderer);
    localCategoryPlot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
    LegendTitle localLegendTitle1 = new LegendTitle(localCategoryPlot.getRenderer(0));
    localLegendTitle1.setMargin(new RectangleInsets(2.0D, 2.0D, 2.0D, 2.0D));
    localLegendTitle1.setFrame(new BlockBorder());
    LegendTitle localLegendTitle2 = new LegendTitle(localCategoryPlot.getRenderer(1));
    localLegendTitle2.setMargin(new RectangleInsets(2.0D, 2.0D, 2.0D, 2.0D));
    localLegendTitle2.setFrame(new BlockBorder());
    BlockContainer localBlockContainer = new BlockContainer(new BorderArrangement());
    localBlockContainer.add(localLegendTitle1, RectangleEdge.LEFT);
    localBlockContainer.add(localLegendTitle2, RectangleEdge.RIGHT);
    localBlockContainer.add(new EmptyBlock(2000.0D, 0.0D));
    CompositeTitle localCompositeTitle = new CompositeTitle(localBlockContainer);
    localCompositeTitle.setPosition(RectangleEdge.BOTTOM);
    localJFreeChart.addSubtitle(localCompositeTitle);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart();
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    DualAxisDemo1 localDualAxisDemo1 = new DualAxisDemo1("JFreeChart: DualAxisDemo1.java");
    localDualAxisDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localDualAxisDemo1);
    localDualAxisDemo1.setVisible(true);
  }
}