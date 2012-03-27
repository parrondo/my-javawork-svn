package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.DecimalFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPosition;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ExtendedCategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.text.TextBlockAnchor;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class SurveyResultsDemo3 extends ApplicationFrame
{
  public SurveyResultsDemo3(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(300, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(2.61D, "Results", "Sm.");
    localDefaultCategoryDataset.addValue(2.7D, "Results", "Med.");
    localDefaultCategoryDataset.addValue(2.9D, "Results", "Lg.");
    localDefaultCategoryDataset.addValue(2.74D, "Results", "All");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart(null, null, null, paramCategoryDataset, PlotOrientation.HORIZONTAL, false, true, false);
    localJFreeChart.setBackgroundPaint(Color.white);
    localJFreeChart.getPlot().setOutlinePaint(null);
    TextTitle localTextTitle = new TextTitle("Figure 6 | Overall SEO Rating");
    localTextTitle.setHorizontalAlignment(HorizontalAlignment.LEFT);
    localTextTitle.setBackgroundPaint(Color.red);
    localTextTitle.setPaint(Color.white);
    localJFreeChart.setTitle(localTextTitle);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    ValueAxis localValueAxis = localCategoryPlot.getRangeAxis();
    localValueAxis.setRange(0.0D, 4.0D);
    localValueAxis.setVisible(false);
    ExtendedCategoryAxis localExtendedCategoryAxis = new ExtendedCategoryAxis(null);
    localExtendedCategoryAxis.setTickLabelFont(new Font("SansSerif", 1, 12));
    localExtendedCategoryAxis.setCategoryMargin(0.3D);
    localExtendedCategoryAxis.addSubLabel("Sm.", "(10)");
    localExtendedCategoryAxis.addSubLabel("Med.", "(10)");
    localExtendedCategoryAxis.addSubLabel("Lg.", "(10)");
    localExtendedCategoryAxis.addSubLabel("All", "(10)");
    CategoryLabelPositions localCategoryLabelPositions = localExtendedCategoryAxis.getCategoryLabelPositions();
    CategoryLabelPosition localCategoryLabelPosition = new CategoryLabelPosition(RectangleAnchor.LEFT, TextBlockAnchor.CENTER_LEFT);
    localExtendedCategoryAxis.setCategoryLabelPositions(CategoryLabelPositions.replaceLeftPosition(localCategoryLabelPositions, localCategoryLabelPosition));
    localCategoryPlot.setDomainAxis(localExtendedCategoryAxis);
    BarRenderer localBarRenderer = (BarRenderer)localCategoryPlot.getRenderer();
    localBarRenderer.setSeriesPaint(0, new Color(156, 164, 74));
    localBarRenderer.setDrawBarOutline(false);
    StandardCategoryItemLabelGenerator localStandardCategoryItemLabelGenerator = new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0.00"));
    localBarRenderer.setBaseItemLabelGenerator(localStandardCategoryItemLabelGenerator);
    localBarRenderer.setBaseItemLabelsVisible(true);
    localBarRenderer.setBaseItemLabelFont(new Font("SansSerif", 0, 18));
    ItemLabelPosition localItemLabelPosition = new ItemLabelPosition(ItemLabelAnchor.INSIDE3, TextAnchor.CENTER_RIGHT);
    localBarRenderer.setBasePositiveItemLabelPosition(localItemLabelPosition);
    localBarRenderer.setPositiveItemLabelPositionFallback(new ItemLabelPosition());
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    SurveyResultsDemo3 localSurveyResultsDemo3 = new SurveyResultsDemo3("Survey Results Demo 3");
    localSurveyResultsDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localSurveyResultsDemo3);
    localSurveyResultsDemo3.setVisible(true);
  }
}