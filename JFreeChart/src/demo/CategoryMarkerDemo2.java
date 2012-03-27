package demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class CategoryMarkerDemo2 extends ApplicationFrame
{
  public CategoryMarkerDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(21.0D, "Series 1", "Category 1");
    localDefaultCategoryDataset.addValue(50.0D, "Series 1", "Category 2");
    localDefaultCategoryDataset.addValue(152.0D, "Series 1", "Category 3");
    localDefaultCategoryDataset.addValue(184.0D, "Series 1", "Category 4");
    localDefaultCategoryDataset.addValue(299.0D, "Series 1", "Category 5");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createLineChart("Category Marker Demo 2", "Category", "Count", paramCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    LineAndShapeRenderer localLineAndShapeRenderer = (LineAndShapeRenderer)localCategoryPlot.getRenderer();
    localLineAndShapeRenderer.setSeriesShapesVisible(0, true);
    localLineAndShapeRenderer.setDrawOutlines(true);
    localLineAndShapeRenderer.setUseFillPaint(true);
    localLineAndShapeRenderer.setBaseFillPaint(Color.white);
    CategoryMarker localCategoryMarker = new CategoryMarker("Category 4", new Color(0, 0, 255, 25), new BasicStroke(1.0F));
    localCategoryMarker.setDrawAsLine(false);
    localCategoryMarker.setAlpha(1.0F);
    localCategoryMarker.setLabel("Marker Label");
    localCategoryMarker.setLabelFont(new Font("Dialog", 0, 11));
    localCategoryMarker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
    localCategoryMarker.setLabelOffset(new RectangleInsets(2.0D, 5.0D, 2.0D, 5.0D));
    localCategoryPlot.addDomainMarker(localCategoryMarker, Layer.BACKGROUND);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    CategoryMarkerDemo2 localCategoryMarkerDemo2 = new CategoryMarkerDemo2("JFreeChart: CategoryMarkerDemo2.java");
    localCategoryMarkerDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localCategoryMarkerDemo2);
    localCategoryMarkerDemo2.setVisible(true);
  }
}