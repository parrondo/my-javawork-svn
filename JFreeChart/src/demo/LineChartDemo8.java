package demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.ShapeUtilities;

public class LineChartDemo8 extends ApplicationFrame
{
  public LineChartDemo8(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(0.0D, "Series 1", "Category 1");
    localDefaultCategoryDataset.addValue(2.0D, "Series 1", "Category 2");
    localDefaultCategoryDataset.addValue(1.0D, "Series 1", "Category 3");
    localDefaultCategoryDataset.addValue(3.0D, "Series 1", "Category 4");
    localDefaultCategoryDataset.addValue(5.0D, "Series 1", "Category 5");
    localDefaultCategoryDataset.addValue(2.0D, "Series 2", "Category 1");
    localDefaultCategoryDataset.addValue(4.0D, "Series 2", "Category 2");
    localDefaultCategoryDataset.addValue(4.0D, "Series 2", "Category 3");
    localDefaultCategoryDataset.addValue(5.0D, "Series 2", "Category 4");
    localDefaultCategoryDataset.addValue(2.0D, "Series 2", "Category 5");
    localDefaultCategoryDataset.addValue(1.0D, "Series 3", "Category 1");
    localDefaultCategoryDataset.addValue(3.0D, "Series 3", "Category 2");
    localDefaultCategoryDataset.addValue(5.0D, "Series 3", "Category 3");
    localDefaultCategoryDataset.addValue(2.0D, "Series 3", "Category 4");
    localDefaultCategoryDataset.addValue(0.0D, "Series 3", "Category 5");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createLineChart("Line Chart Demo 8", "Category", "Count", paramCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    SymbolAxis localSymbolAxis = new SymbolAxis("Group", new String[] { "A", "B", "C", "D", "E", "F" });
    localCategoryPlot.setRangeAxis(localSymbolAxis);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    LineAndShapeRenderer localLineAndShapeRenderer = (LineAndShapeRenderer)localCategoryPlot.getRenderer();
    localLineAndShapeRenderer.setSeriesShapesVisible(0, true);
    localLineAndShapeRenderer.setSeriesShapesVisible(1, false);
    localLineAndShapeRenderer.setSeriesShapesVisible(2, true);
    localLineAndShapeRenderer.setSeriesLinesVisible(2, false);
    localLineAndShapeRenderer.setSeriesShape(2, ShapeUtilities.createDiamond(4.0F));
    localLineAndShapeRenderer.setDrawOutlines(true);
    localLineAndShapeRenderer.setUseFillPaint(true);
    localLineAndShapeRenderer.setBaseFillPaint(Color.white);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    LineChartDemo8 localLineChartDemo8 = new LineChartDemo8("JFreeChart: LineChartDemo8.java");
    localLineChartDemo8.pack();
    RefineryUtilities.centerFrameOnScreen(localLineChartDemo8);
    localLineChartDemo8.setVisible(true);
  }
}