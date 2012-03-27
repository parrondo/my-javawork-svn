package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.util.Arrays;
import java.util.List;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class BarChartDemo3 extends ApplicationFrame
{
  public BarChartDemo3(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    double[][] arrayOfDouble = { { 4.0D, 3.0D, -2.0D, 3.0D, 6.0D } };
    return DatasetUtilities.createCategoryDataset("Series ", "Category ", arrayOfDouble);
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart("Bar Chart Demo 3", "Category", "Value", paramCategoryDataset, PlotOrientation.VERTICAL, false, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setNoDataMessage("NO DATA!");
    localCategoryPlot.setRangePannable(true);
    Paint[] arrayOfPaint = { new Color(196, 215, 216), new Color(78, 137, 139), new Color(138, 177, 178), new Color(19, 97, 100) };
    CustomRenderer localCustomRenderer = new CustomRenderer(arrayOfPaint);
    localCustomRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
    localCustomRenderer.setBaseItemLabelsVisible(true);
    ItemLabelPosition localItemLabelPosition = new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER, 0.0D);
    localCustomRenderer.setBasePositiveItemLabelPosition(localItemLabelPosition);
    localCategoryPlot.setRenderer(localCustomRenderer);
    CategoryMarker localCategoryMarker = new CategoryMarker("Category 3");
    localCategoryMarker.setLabel("Special");
    localCategoryMarker.setPaint(new Color(221, 255, 221, 128));
    localCategoryMarker.setAlpha(0.5F);
    localCategoryMarker.setLabelAnchor(RectangleAnchor.TOP_LEFT);
    localCategoryMarker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
    localCategoryMarker.setLabelOffsetType(LengthAdjustmentType.CONTRACT);
    localCategoryPlot.addDomainMarker(localCategoryMarker, Layer.BACKGROUND);
    NumberAxis localNumberAxis1 = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis1.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    localNumberAxis1.setLowerMargin(0.15D);
    localNumberAxis1.setUpperMargin(0.15D);
    NumberAxis localNumberAxis2 = new NumberAxis(null);
    localNumberAxis2.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    localNumberAxis2.setLowerMargin(0.15D);
    localNumberAxis2.setUpperMargin(0.15D);
    localCategoryPlot.setRangeAxis(1, localNumberAxis2);
    CategoryAxis localCategoryAxis = new CategoryAxis(null);
    localCategoryPlot.setDomainAxis(1, localCategoryAxis);
    List localList = Arrays.asList(new Integer[] { new Integer(0), new Integer(1) });
    localCategoryPlot.mapDatasetToDomainAxes(0, localList);
    localCategoryPlot.mapDatasetToRangeAxes(0, localList);
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
    BarChartDemo3 localBarChartDemo3 = new BarChartDemo3("JFreeChart: BarChartDemo3.java");
    localBarChartDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localBarChartDemo3);
    localBarChartDemo3.setVisible(true);
  }

  static class CustomRenderer extends BarRenderer
  {
    private Paint[] colors;

    public CustomRenderer(Paint[] paramArrayOfPaint)
    {
      this.colors = paramArrayOfPaint;
    }

    public Paint getItemPaint(int paramInt1, int paramInt2)
    {
      return this.colors[(paramInt2 % this.colors.length)];
    }
  }
}