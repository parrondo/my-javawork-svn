package demo;

import java.awt.Dimension;
import java.text.NumberFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class StackedBarChartDemo3 extends ApplicationFrame
{
  public StackedBarChartDemo3(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(10.0D, "Series 1", "Jan");
    localDefaultCategoryDataset.addValue(12.0D, "Series 1", "Feb");
    localDefaultCategoryDataset.addValue(13.0D, "Series 1", "Mar");
    localDefaultCategoryDataset.addValue(4.0D, "Series 2", "Jan");
    localDefaultCategoryDataset.addValue(3.0D, "Series 2", "Feb");
    localDefaultCategoryDataset.addValue(2.0D, "Series 2", "Mar");
    localDefaultCategoryDataset.addValue(2.0D, "Series 3", "Jan");
    localDefaultCategoryDataset.addValue(3.0D, "Series 3", "Feb");
    localDefaultCategoryDataset.addValue(2.0D, "Series 3", "Mar");
    localDefaultCategoryDataset.addValue(2.0D, "Series 4", "Jan");
    localDefaultCategoryDataset.addValue(3.0D, "Series 4", "Feb");
    localDefaultCategoryDataset.addValue(4.0D, "Series 4", "Mar");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createStackedBarChart("Stacked Bar Chart Demo 3", "Category", "Value", paramCategoryDataset, PlotOrientation.VERTICAL, true, false, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    ExtendedStackedBarRenderer localExtendedStackedBarRenderer = new ExtendedStackedBarRenderer();
    localExtendedStackedBarRenderer.setBaseItemLabelsVisible(true);
    localExtendedStackedBarRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
    localExtendedStackedBarRenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
    localCategoryPlot.setRenderer(localExtendedStackedBarRenderer);
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    localNumberAxis.setLowerMargin(0.15D);
    localNumberAxis.setUpperMargin(0.15D);
    localNumberAxis.setNumberFormatOverride(NumberFormat.getPercentInstance());
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
    StackedBarChartDemo3 localStackedBarChartDemo3 = new StackedBarChartDemo3("Stacked Bar Chart Demo 3");
    localStackedBarChartDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localStackedBarChartDemo3);
    localStackedBarChartDemo3.setVisible(true);
  }
}