package demo;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class LineChartDemo5 extends ApplicationFrame
{
  public LineChartDemo5(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    String str1 = "First";
    String str2 = "Second";
    String str3 = "Third";
    String str4 = "Type 1";
    String str5 = "Type 2";
    String str6 = "Type 3";
    String str7 = "Type 4";
    String str8 = "Type 5";
    String str9 = "Type 6";
    String str10 = "Type 7";
    String str11 = "Type 8";
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

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createLineChart("Line Chart Demo 5", "Type", "Value", paramCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
    Shape[] arrayOfShape = new Shape[3];
    int[] arrayOfInt1 = { -3, 3, -3 };
    int[] arrayOfInt2 = { -3, 0, 3 };
    arrayOfShape[0] = new Polygon(arrayOfInt1, arrayOfInt2, 3);
    arrayOfShape[1] = new Rectangle2D.Double(-2.0D, -3.0D, 3.0D, 6.0D);
    arrayOfInt1 = new int[] { -3, 3, 3 };
    arrayOfInt2 = new int[] { 0, -3, 3 };
    arrayOfShape[2] = new Polygon(arrayOfInt1, arrayOfInt2, 3);
    DefaultDrawingSupplier localDefaultDrawingSupplier = new DefaultDrawingSupplier(DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE, DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE, DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE, DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE, arrayOfShape);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setOrientation(PlotOrientation.HORIZONTAL);
    localCategoryPlot.setDrawingSupplier(localDefaultDrawingSupplier);
    localCategoryPlot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0F, 1, 1, 1.0F, new float[] { 10.0F, 6.0F }, 0.0F));
    localCategoryPlot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0F, 1, 1, 1.0F, new float[] { 6.0F, 6.0F }, 0.0F));
    localCategoryPlot.getRenderer().setSeriesStroke(2, new BasicStroke(2.0F, 1, 1, 1.0F, new float[] { 2.0F, 6.0F }, 0.0F));
    LineAndShapeRenderer localLineAndShapeRenderer = (LineAndShapeRenderer)localCategoryPlot.getRenderer();
    localLineAndShapeRenderer.setBaseShapesVisible(true);
    localLineAndShapeRenderer.setBaseItemLabelsVisible(true);
    localLineAndShapeRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    localNumberAxis.setAutoRangeIncludesZero(false);
    localNumberAxis.setUpperMargin(0.12D);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    LineChartDemo5 localLineChartDemo5 = new LineChartDemo5("JFreeChart: LineChartDemo5.java");
    localLineChartDemo5.pack();
    RefineryUtilities.centerFrameOnScreen(localLineChartDemo5);
    localLineChartDemo5.setVisible(true);
  }
}