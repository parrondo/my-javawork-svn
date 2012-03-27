package demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Paint;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.StandardGradientPaintTransformer;

public class BarChartDemo9 extends ApplicationFrame
{
  public BarChartDemo9(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(410.0D, "Network Traffic", "Monday");
    localDefaultCategoryDataset.addValue(680.0D, "Network Traffic", "Tuesday");
    localDefaultCategoryDataset.addValue(530.0D, "Network Traffic", "Wednesday");
    localDefaultCategoryDataset.addValue(570.0D, "Network Traffic", "Thursday");
    localDefaultCategoryDataset.addValue(330.0D, "Network Traffic", "Friday");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart("Bar Chart Demo 9", null, "Value", paramCategoryDataset, PlotOrientation.VERTICAL, false, true, false);
    TextTitle localTextTitle = localJFreeChart.getTitle();
    localTextTitle.setBorder(0.0D, 0.0D, 1.0D, 0.0D);
    localTextTitle.setBackgroundPaint(new GradientPaint(0.0F, 0.0F, Color.red, 350.0F, 0.0F, Color.white, true));
    localTextTitle.setExpandToFitSpace(true);
    localJFreeChart.setBackgroundPaint(new GradientPaint(0.0F, 0.0F, Color.yellow, 350.0F, 0.0F, Color.white, true));
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setNoDataMessage("NO DATA!");
    localCategoryPlot.setBackgroundPaint(null);
    localCategoryPlot.setInsets(new RectangleInsets(10.0D, 5.0D, 5.0D, 5.0D));
    localCategoryPlot.setOutlinePaint(Color.black);
    localCategoryPlot.setRangeGridlinePaint(Color.gray);
    localCategoryPlot.setRangeGridlineStroke(new BasicStroke(1.0F));
    Paint[] arrayOfPaint = createPaint();
    CustomBarRenderer localCustomBarRenderer = new CustomBarRenderer(arrayOfPaint);
    localCustomBarRenderer.setBarPainter(new StandardBarPainter());
    localCustomBarRenderer.setDrawBarOutline(true);
    localCustomBarRenderer.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.CENTER_HORIZONTAL));
    localCategoryPlot.setRenderer(localCustomBarRenderer);
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    localNumberAxis.setRange(0.0D, 800.0D);
    localNumberAxis.setTickMarkPaint(Color.black);
    return localJFreeChart;
  }

  private static Paint[] createPaint()
  {
    Paint[] arrayOfPaint = new Paint[5];
    arrayOfPaint[0] = new GradientPaint(0.0F, 0.0F, Color.red, 0.0F, 0.0F, Color.white);
    arrayOfPaint[1] = new GradientPaint(0.0F, 0.0F, Color.green, 0.0F, 0.0F, Color.white);
    arrayOfPaint[2] = new GradientPaint(0.0F, 0.0F, Color.blue, 0.0F, 0.0F, Color.white);
    arrayOfPaint[3] = new GradientPaint(0.0F, 0.0F, Color.orange, 0.0F, 0.0F, Color.white);
    arrayOfPaint[4] = new GradientPaint(0.0F, 0.0F, Color.magenta, 0.0F, 0.0F, Color.white);
    return arrayOfPaint;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    BarChartDemo9 localBarChartDemo9 = new BarChartDemo9("JFreeChart: BarChartDemo9.java");
    localBarChartDemo9.pack();
    RefineryUtilities.centerFrameOnScreen(localBarChartDemo9);
    localBarChartDemo9.setVisible(true);
  }

  static class CustomBarRenderer extends BarRenderer
  {
    private Paint[] colors;

    public CustomBarRenderer(Paint[] paramArrayOfPaint)
    {
      this.colors = paramArrayOfPaint;
    }

    public Paint getItemPaint(int paramInt1, int paramInt2)
    {
      return this.colors[(paramInt2 % this.colors.length)];
    }
  }
}