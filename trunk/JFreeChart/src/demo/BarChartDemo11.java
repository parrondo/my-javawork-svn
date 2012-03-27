package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.text.DecimalFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;

public class BarChartDemo11 extends ApplicationFrame
{
  public BarChartDemo11(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(23192.0D, "S1", "GNU General Public Licence");
    localDefaultCategoryDataset.addValue(3157.0D, "S1", "GNU Lesser General Public Licence");
    localDefaultCategoryDataset.addValue(1506.0D, "S1", "BSD Licence (Original)");
    localDefaultCategoryDataset.addValue(1283.0D, "S1", "BSD Licence (Revised)");
    localDefaultCategoryDataset.addValue(738.0D, "S1", "MIT/X Consortium Licence");
    localDefaultCategoryDataset.addValue(630.0D, "S1", "Artistic Licence");
    localDefaultCategoryDataset.addValue(585.0D, "S1", "Public Domain");
    localDefaultCategoryDataset.addValue(349.0D, "S1", "Apache Licence 2.0");
    localDefaultCategoryDataset.addValue(317.0D, "S1", "Apache Licence");
    localDefaultCategoryDataset.addValue(309.0D, "S1", "Mozilla Public Licence");
    localDefaultCategoryDataset.addValue(918.0D, "S1", "Other");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart("Open Source Projects By Licence", "Licence", "Project Count", paramCategoryDataset, PlotOrientation.HORIZONTAL, false, true, false);
    TextTitle localTextTitle = new TextTitle("Source: Freshmeat (http://www.freshmeat.net/)", new Font("Dialog", 0, 10));
    localTextTitle.setPosition(RectangleEdge.BOTTOM);
    localJFreeChart.addSubtitle(localTextTitle);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setDomainGridlinesVisible(true);
    localCategoryPlot.getDomainAxis().setMaximumCategoryLabelWidthRatio(0.8F);
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    BarRenderer localBarRenderer = (BarRenderer)localCategoryPlot.getRenderer();
    localBarRenderer.setDrawBarOutline(false);
    StandardCategoryToolTipGenerator localStandardCategoryToolTipGenerator = new StandardCategoryToolTipGenerator("{1}: {2} projects", new DecimalFormat("0"));
    localBarRenderer.setBaseToolTipGenerator(localStandardCategoryToolTipGenerator);
    GradientPaint localGradientPaint = new GradientPaint(0.0F, 0.0F, Color.blue, 0.0F, 0.0F, new Color(0, 0, 64));
    localBarRenderer.setSeriesPaint(0, localGradientPaint);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    BarChartDemo11 localBarChartDemo11 = new BarChartDemo11("JFreeChart: BarChartDemo11.java");
    localBarChartDemo11.pack();
    RefineryUtilities.centerFrameOnScreen(localBarChartDemo11);
    localBarChartDemo11.setVisible(true);
  }
}