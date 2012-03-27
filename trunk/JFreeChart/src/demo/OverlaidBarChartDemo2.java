package demo;

import java.awt.BasicStroke;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LevelRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class OverlaidBarChartDemo2 extends ApplicationFrame
{
  public OverlaidBarChartDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart()
  {
    DefaultCategoryDataset localDefaultCategoryDataset1 = new DefaultCategoryDataset();
    localDefaultCategoryDataset1.addValue(1.0D, "S1", "Category 1");
    localDefaultCategoryDataset1.addValue(4.0D, "S1", "Category 2");
    localDefaultCategoryDataset1.addValue(3.0D, "S1", "Category 3");
    localDefaultCategoryDataset1.addValue(5.0D, "S1", "Category 4");
    localDefaultCategoryDataset1.addValue(5.0D, "S1", "Category 5");
    localDefaultCategoryDataset1.addValue(5.0D, "S2", "Category 1");
    localDefaultCategoryDataset1.addValue(7.0D, "S2", "Category 2");
    localDefaultCategoryDataset1.addValue(6.0D, "S2", "Category 3");
    localDefaultCategoryDataset1.addValue(8.0D, "S2", "Category 4");
    localDefaultCategoryDataset1.addValue(4.0D, "S2", "Category 5");
    BarRenderer localBarRenderer = new BarRenderer();
    localBarRenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
    CategoryPlot localCategoryPlot = new CategoryPlot();
    localCategoryPlot.setDataset(localDefaultCategoryDataset1);
    localCategoryPlot.setRenderer(localBarRenderer);
    localCategoryPlot.setDomainAxis(new CategoryAxis("Category"));
    localCategoryPlot.setRangeAxis(new NumberAxis("Value"));
    localCategoryPlot.setOrientation(PlotOrientation.VERTICAL);
    localCategoryPlot.setRangeGridlinesVisible(true);
    localCategoryPlot.setDomainGridlinesVisible(true);
    localCategoryPlot.setRangeZeroBaselineVisible(true);
    localCategoryPlot.setRangePannable(true);
    DefaultCategoryDataset localDefaultCategoryDataset2 = new DefaultCategoryDataset();
    localDefaultCategoryDataset2.addValue(6.0D, "Prior 1", "Category 1");
    localDefaultCategoryDataset2.addValue(7.0D, "Prior 1", "Category 2");
    localDefaultCategoryDataset2.addValue(2.0D, "Prior 1", "Category 3");
    localDefaultCategoryDataset2.addValue(6.0D, "Prior 1", "Category 4");
    localDefaultCategoryDataset2.addValue(6.0D, "Prior 1", "Category 5");
    localDefaultCategoryDataset2.addValue(4.0D, "Prior 2", "Category 1");
    localDefaultCategoryDataset2.addValue(2.0D, "Prior 2", "Category 2");
    localDefaultCategoryDataset2.addValue(1.0D, "Prior 2", "Category 3");
    localDefaultCategoryDataset2.addValue(3.0D, "Prior 2", "Category 4");
    localDefaultCategoryDataset2.addValue(2.0D, "Prior 2", "Category 5");
    LevelRenderer localLevelRenderer = new LevelRenderer();
    localCategoryPlot.setDataset(1, localDefaultCategoryDataset2);
    localCategoryPlot.setRenderer(1, localLevelRenderer);
    localCategoryPlot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
    JFreeChart localJFreeChart = new JFreeChart(localCategoryPlot);
    localJFreeChart.setTitle("OverlaidBarChartDemo2");
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    localLevelRenderer.setSeriesStroke(0, new BasicStroke(2.0F));
    localLevelRenderer.setSeriesStroke(1, new BasicStroke(2.0F));
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart();
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setMouseWheelEnabled(true);
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    OverlaidBarChartDemo2 localOverlaidBarChartDemo2 = new OverlaidBarChartDemo2("JFreeChart: OverlaidBarChartDemo2.java");
    localOverlaidBarChartDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localOverlaidBarChartDemo2);
    localOverlaidBarChartDemo2.setVisible(true);
  }
}