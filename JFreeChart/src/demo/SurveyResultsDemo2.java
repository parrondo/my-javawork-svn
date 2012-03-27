package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ExtendedCategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class SurveyResultsDemo2 extends ApplicationFrame
{
  public SurveyResultsDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(300, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(1.32D, "Results", "Sm.");
    localDefaultCategoryDataset.addValue(0.4D, "Results", "Med.");
    localDefaultCategoryDataset.addValue(2.62D, "Results", "Lg.");
    localDefaultCategoryDataset.addValue(1.44D, "Results", "All");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart(null, null, null, paramCategoryDataset, PlotOrientation.VERTICAL, false, true, false);
    localJFreeChart.setBackgroundPaint(Color.white);
    localJFreeChart.getPlot().setOutlinePaint(null);
    TextTitle localTextTitle = new TextTitle("Figure 8.5 - Case studies are available");
    localTextTitle.setHorizontalAlignment(HorizontalAlignment.LEFT);
    localTextTitle.setBackgroundPaint(Color.red);
    localTextTitle.setPaint(Color.white);
    localJFreeChart.setTitle(localTextTitle);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    ValueAxis localValueAxis = localCategoryPlot.getRangeAxis();
    localValueAxis.setRange(0.0D, 5.0D);
    localValueAxis.setVisible(false);
    ExtendedCategoryAxis localExtendedCategoryAxis = new ExtendedCategoryAxis(null);
    localExtendedCategoryAxis.setTickLabelFont(new Font("SansSerif", 1, 12));
    localExtendedCategoryAxis.setCategoryMargin(0.3D);
    localExtendedCategoryAxis.addSubLabel("Sm.", "(10)");
    localExtendedCategoryAxis.addSubLabel("Med.", "(10)");
    localExtendedCategoryAxis.addSubLabel("Lg.", "(10)");
    localExtendedCategoryAxis.addSubLabel("All", "(10)");
    localCategoryPlot.setDomainAxis(localExtendedCategoryAxis);
    BarRenderer localBarRenderer = (BarRenderer)localCategoryPlot.getRenderer();
    localBarRenderer.setSeriesPaint(0, new Color(156, 164, 74));
    localBarRenderer.setDrawBarOutline(false);
    localBarRenderer.setBaseItemLabelsVisible(true);
    localBarRenderer.setBaseItemLabelFont(new Font("SansSerif", 0, 18));
    ItemLabelPosition localItemLabelPosition = new ItemLabelPosition(ItemLabelAnchor.INSIDE12, TextAnchor.TOP_CENTER);
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
    SurveyResultsDemo2 localSurveyResultsDemo2 = new SurveyResultsDemo2("Survey Results Demo 2");
    localSurveyResultsDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localSurveyResultsDemo2);
    localSurveyResultsDemo2.setVisible(true);
  }
}