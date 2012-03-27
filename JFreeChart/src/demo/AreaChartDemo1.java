package demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.VerticalAlignment;
import org.jfree.util.UnitType;

public class AreaChartDemo1 extends ApplicationFrame
{
  public AreaChartDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(1.0D, "Series 1", "Type 1");
    localDefaultCategoryDataset.addValue(4.0D, "Series 1", "Type 2");
    localDefaultCategoryDataset.addValue(3.0D, "Series 1", "Type 3");
    localDefaultCategoryDataset.addValue(5.0D, "Series 1", "Type 4");
    localDefaultCategoryDataset.addValue(5.0D, "Series 1", "Type 5");
    localDefaultCategoryDataset.addValue(7.0D, "Series 1", "Type 6");
    localDefaultCategoryDataset.addValue(7.0D, "Series 1", "Type 7");
    localDefaultCategoryDataset.addValue(8.0D, "Series 1", "Type 8");
    localDefaultCategoryDataset.addValue(5.0D, "Series 2", "Type 1");
    localDefaultCategoryDataset.addValue(7.0D, "Series 2", "Type 2");
    localDefaultCategoryDataset.addValue(6.0D, "Series 2", "Type 3");
    localDefaultCategoryDataset.addValue(8.0D, "Series 2", "Type 4");
    localDefaultCategoryDataset.addValue(4.0D, "Series 2", "Type 5");
    localDefaultCategoryDataset.addValue(4.0D, "Series 2", "Type 6");
    localDefaultCategoryDataset.addValue(2.0D, "Series 2", "Type 7");
    localDefaultCategoryDataset.addValue(1.0D, "Series 2", "Type 8");
    localDefaultCategoryDataset.addValue(4.0D, "Series 3", "Type 1");
    localDefaultCategoryDataset.addValue(3.0D, "Series 3", "Type 2");
    localDefaultCategoryDataset.addValue(2.0D, "Series 3", "Type 3");
    localDefaultCategoryDataset.addValue(3.0D, "Series 3", "Type 4");
    localDefaultCategoryDataset.addValue(6.0D, "Series 3", "Type 5");
    localDefaultCategoryDataset.addValue(3.0D, "Series 3", "Type 6");
    localDefaultCategoryDataset.addValue(4.0D, "Series 3", "Type 7");
    localDefaultCategoryDataset.addValue(3.0D, "Series 3", "Type 8");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createAreaChart("Area Chart", "Category", "Value", paramCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
    localJFreeChart.setBackgroundPaint(Color.white);
    TextTitle localTextTitle = new TextTitle("An area chart demonstration.  We use this subtitle as an example of what happens when you get a really long title or subtitle.");
    localTextTitle.setPosition(RectangleEdge.TOP);
    localTextTitle.setPadding(new RectangleInsets(UnitType.RELATIVE, 0.05D, 0.05D, 0.05D, 0.05D));
    localTextTitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
    localJFreeChart.addSubtitle(localTextTitle);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setForegroundAlpha(0.5F);
    localCategoryPlot.setDomainGridlinesVisible(true);
    CategoryAxis localCategoryAxis = localCategoryPlot.getDomainAxis();
    localCategoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
    localCategoryAxis.setLowerMargin(0.0D);
    localCategoryAxis.setUpperMargin(0.0D);
    localCategoryAxis.addCategoryLabelToolTip("Type 1", "The first type.");
    localCategoryAxis.addCategoryLabelToolTip("Type 2", "The second type.");
    localCategoryAxis.addCategoryLabelToolTip("Type 3", "The third type.");
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    localNumberAxis.setLabelAngle(0.0D);
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
    AreaChartDemo1 localAreaChartDemo1 = new AreaChartDemo1("JFreeChart: AreaChartDemo1.java");
    localAreaChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localAreaChartDemo1);
    localAreaChartDemo1.setVisible(true);
  }
}