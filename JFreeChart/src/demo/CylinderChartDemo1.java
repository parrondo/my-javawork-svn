package demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Paint;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.StandardGradientPaintTransformer;

public class CylinderChartDemo1 extends ApplicationFrame
{
  public CylinderChartDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(4.0D, "S1", "Monday");
    localDefaultCategoryDataset.addValue(5.0D, "S1", "Tuesday");
    localDefaultCategoryDataset.addValue(-7.0D, "S1", "Wednesday");
    localDefaultCategoryDataset.addValue(6.0D, "S1", "Thursday");
    localDefaultCategoryDataset.addValue(4.0D, "S1", "Friday");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart3D("Cylinder Chart Demo 1", "Category", "Value", paramCategoryDataset, PlotOrientation.VERTICAL, false, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setRangePannable(true);
    Paint[] arrayOfPaint = createPaint();
    CustomCylinderRenderer localCustomCylinderRenderer = new CustomCylinderRenderer(arrayOfPaint);
    localCustomCylinderRenderer.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.CENTER_HORIZONTAL));
    localCustomCylinderRenderer.setBaseOutlinePaint(Color.gray);
    localCustomCylinderRenderer.setBaseOutlineStroke(new BasicStroke(0.3F));
    localCustomCylinderRenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
    localCategoryPlot.setRenderer(localCustomCylinderRenderer);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
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
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setMouseWheelEnabled(true);
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    CylinderChartDemo1 localCylinderChartDemo1 = new CylinderChartDemo1("JFreeChart: CylinderChartDemo1.java");
    localCylinderChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localCylinderChartDemo1);
    localCylinderChartDemo1.setVisible(true);
  }

  static class CustomCylinderRenderer extends CylinderRenderer
  {
    private Paint[] colors;

    public CustomCylinderRenderer(Paint[] paramArrayOfPaint)
    {
      this.colors = paramArrayOfPaint;
    }

    public Paint getItemPaint(int paramInt1, int paramInt2)
    {
      return this.colors[(paramInt2 % this.colors.length)];
    }
  }
}