package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class StackedBarChartDemo2 extends ApplicationFrame
{
  public StackedBarChartDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(81.0D, "Against all torture", "Italy");
    localDefaultCategoryDataset.addValue(72.0D, "Against all torture", "Great Britain");
    localDefaultCategoryDataset.addValue(58.0D, "Against all torture", "USA");
    localDefaultCategoryDataset.addValue(48.0D, "Against all torture", "Israel");
    localDefaultCategoryDataset.addValue(43.0D, "Against all torture", "Russia");
    localDefaultCategoryDataset.addValue(23.0D, "Against all torture", "India");
    localDefaultCategoryDataset.addValue(59.0D, "Against all torture", "Average (*)");
    localDefaultCategoryDataset.addValue(5.0D, "-", "Italy");
    localDefaultCategoryDataset.addValue(4.0D, "-", "Great Britain");
    localDefaultCategoryDataset.addValue(6.0D, "-", "USA");
    localDefaultCategoryDataset.addValue(9.0D, "-", "Israel");
    localDefaultCategoryDataset.addValue(20.0D, "-", "Russia");
    localDefaultCategoryDataset.addValue(45.0D, "-", "India");
    localDefaultCategoryDataset.addValue(12.0D, "-", "Average (*)");
    localDefaultCategoryDataset.addValue(14.0D, "Some degree permissible", "Italy");
    localDefaultCategoryDataset.addValue(24.0D, "Some degree permissible", "Great Britain");
    localDefaultCategoryDataset.addValue(36.0D, "Some degree permissible", "USA");
    localDefaultCategoryDataset.addValue(43.0D, "Some degree permissible", "Israel");
    localDefaultCategoryDataset.addValue(37.0D, "Some degree permissible", "Russia");
    localDefaultCategoryDataset.addValue(32.0D, "Some degree permissible", "India");
    localDefaultCategoryDataset.addValue(29.0D, "Some degree permissible", "Average (*)");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createStackedBarChart("Public Opinion : Torture of Prisoners", "Country", "%", paramCategoryDataset, PlotOrientation.HORIZONTAL, false, true, false);
    localJFreeChart.getTitle().setMargin(2.0D, 0.0D, 0.0D, 0.0D);
    TextTitle localTextTitle1 = new TextTitle("Source: http://news.bbc.co.uk/1/hi/world/6063386.stm", new Font("Dialog", 0, 11));
    localTextTitle1.setPosition(RectangleEdge.BOTTOM);
    localTextTitle1.setHorizontalAlignment(HorizontalAlignment.RIGHT);
    localTextTitle1.setMargin(0.0D, 0.0D, 4.0D, 4.0D);
    localJFreeChart.addSubtitle(localTextTitle1);
    TextTitle localTextTitle2 = new TextTitle("(*) Across 27,000 respondents in 25 countries", new Font("Dialog", 0, 11));
    localTextTitle2.setPosition(RectangleEdge.BOTTOM);
    localTextTitle2.setHorizontalAlignment(HorizontalAlignment.RIGHT);
    localTextTitle2.setMargin(4.0D, 0.0D, 2.0D, 4.0D);
    localJFreeChart.addSubtitle(localTextTitle2);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    LegendItemCollection localLegendItemCollection = new LegendItemCollection();
    localLegendItemCollection.add(new LegendItem("Against all torture", null, null, null, new Rectangle2D.Double(-6.0D, -3.0D, 12.0D, 6.0D), Color.green));
    localLegendItemCollection.add(new LegendItem("Some degree permissible", null, null, null, new Rectangle2D.Double(-6.0D, -3.0D, 12.0D, 6.0D), Color.red));
    localCategoryPlot.setFixedLegendItems(localLegendItemCollection);
    localCategoryPlot.setInsets(new RectangleInsets(5.0D, 5.0D, 5.0D, 20.0D));
    LegendTitle localLegendTitle = new LegendTitle(localCategoryPlot);
    localLegendTitle.setPosition(RectangleEdge.BOTTOM);
    localJFreeChart.addSubtitle(localLegendTitle);
    localCategoryPlot.setDomainGridlinesVisible(true);
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    localNumberAxis.setUpperMargin(0.0D);
    BarRenderer localBarRenderer = (BarRenderer)localCategoryPlot.getRenderer();
    localBarRenderer.setDrawBarOutline(false);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    GradientPaint localGradientPaint1 = new GradientPaint(0.0F, 0.0F, Color.green, 0.0F, 0.0F, new Color(0, 64, 0));
    Color localColor = new Color(0, 0, 0, 0);
    GradientPaint localGradientPaint2 = new GradientPaint(0.0F, 0.0F, Color.red, 0.0F, 0.0F, new Color(64, 0, 0));
    localBarRenderer.setSeriesPaint(0, localGradientPaint1);
    localBarRenderer.setSeriesPaint(1, localColor);
    localBarRenderer.setSeriesPaint(2, localGradientPaint2);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    StackedBarChartDemo2 localStackedBarChartDemo2 = new StackedBarChartDemo2("JFreeChart: StackedBarChartDemo2.java");
    localStackedBarChartDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localStackedBarChartDemo2);
    localStackedBarChartDemo2.setVisible(true);
  }
}