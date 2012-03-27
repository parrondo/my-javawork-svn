package demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.text.NumberFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.CategoryAnchor;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.Layer;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class BarChart3DDemo4 extends ApplicationFrame
{
  public BarChart3DDemo4(String paramString)
  {
    super(paramString);
    CategoryDataset localCategoryDataset = createDataset();
    JFreeChart localJFreeChart = createChart(localCategoryDataset);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localChartPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(0.77D, "Series 1", "Robert");
    localDefaultCategoryDataset.addValue(0.9300000000000001D, "Series 1", "Mary");
    localDefaultCategoryDataset.addValue(0.59D, "Series 1", "John");
    localDefaultCategoryDataset.addValue(0.75D, "Series 1", "Ellen");
    localDefaultCategoryDataset.addValue(0.63D, "Series 1", "Jack");
    localDefaultCategoryDataset.addValue(0.95D, "Series 1", "David");
    localDefaultCategoryDataset.addValue(0.71D, "Series 1", "Mark");
    localDefaultCategoryDataset.addValue(0.65D, "Series 1", "Andy");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart3D("Student Grades", "Students", "Grade", paramCategoryDataset, PlotOrientation.VERTICAL, false, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    CustomBarRenderer3D localCustomBarRenderer3D = new CustomBarRenderer3D();
    localCustomBarRenderer3D.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
    localCustomBarRenderer3D.setBaseItemLabelsVisible(true);
    localCustomBarRenderer3D.setItemLabelAnchorOffset(10.0D);
    localCustomBarRenderer3D.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
    localCategoryPlot.setRenderer(localCustomBarRenderer3D);
    ValueMarker localValueMarker = new ValueMarker(0.7D, new Color(200, 200, 255), new BasicStroke(1.0F), new Color(200, 200, 255), new BasicStroke(1.0F), 1.0F);
    localCategoryPlot.addRangeMarker(localValueMarker, Layer.BACKGROUND);
    localCustomBarRenderer3D.setBaseItemLabelsVisible(true);
    localCustomBarRenderer3D.setMaximumBarWidth(0.05D);
    CategoryTextAnnotation localCategoryTextAnnotation = new CategoryTextAnnotation("Minimum grade to pass", "Robert", 0.71D);
    localCategoryTextAnnotation.setCategoryAnchor(CategoryAnchor.START);
    localCategoryTextAnnotation.setFont(new Font("SansSerif", 0, 12));
    localCategoryTextAnnotation.setTextAnchor(TextAnchor.BOTTOM_LEFT);
    localCategoryPlot.addAnnotation(localCategoryTextAnnotation);
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setNumberFormatOverride(NumberFormat.getPercentInstance());
    localNumberAxis.setUpperMargin(0.1D);
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
    BarChart3DDemo4 localBarChart3DDemo4 = new BarChart3DDemo4("JFreeChart: BarChart3DDemo4.java");
    localBarChart3DDemo4.pack();
    RefineryUtilities.centerFrameOnScreen(localBarChart3DDemo4);
    localBarChart3DDemo4.setVisible(true);
  }

  static class CustomBarRenderer3D extends BarRenderer3D
  {
    public Paint getItemPaint(int paramInt1, int paramInt2)
    {
      CategoryDataset localCategoryDataset = getPlot().getDataset();
      double d = localCategoryDataset.getValue(paramInt1, paramInt2).doubleValue();
      if (d >= 0.7D)
        return Color.green;
      return Color.red;
    }
  }
}