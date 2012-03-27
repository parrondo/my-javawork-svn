package demo;

import java.awt.Dimension;
import java.text.NumberFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.DataUtilities;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.KeyedValues;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.SortOrder;

public class ParetoChartDemo1 extends ApplicationFrame
{
  public ParetoChartDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(550, 270));
    setContentPane(localJPanel);
  }

  public static JFreeChart createChart(CategoryDataset[] paramArrayOfCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart("Freshmeat Software Projects", "Language", "Projects", paramArrayOfCategoryDataset[0], PlotOrientation.VERTICAL, true, true, false);
    localJFreeChart.addSubtitle(new TextTitle("By Programming Language"));
    localJFreeChart.addSubtitle(new TextTitle("As at 5 March 2003"));
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    CategoryAxis localCategoryAxis = localCategoryPlot.getDomainAxis();
    localCategoryAxis.setLowerMargin(0.02D);
    localCategoryAxis.setUpperMargin(0.02D);
    localCategoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
    NumberAxis localNumberAxis1 = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis1.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    LineAndShapeRenderer localLineAndShapeRenderer = new LineAndShapeRenderer();
    NumberAxis localNumberAxis2 = new NumberAxis("Percent");
    localNumberAxis2.setNumberFormatOverride(NumberFormat.getPercentInstance());
    localCategoryPlot.setRangeAxis(1, localNumberAxis2);
    localCategoryPlot.setDataset(1, paramArrayOfCategoryDataset[1]);
    localCategoryPlot.setRenderer(1, localLineAndShapeRenderer);
    localCategoryPlot.mapDatasetToRangeAxis(1, 1);
    localCategoryPlot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static CategoryDataset[] createDatasets()
  {
    DefaultKeyedValues localDefaultKeyedValues = new DefaultKeyedValues();
    localDefaultKeyedValues.addValue("C", new Integer(4843));
    localDefaultKeyedValues.addValue("C++", new Integer(2098));
    localDefaultKeyedValues.addValue("C#", new Integer(26));
    localDefaultKeyedValues.addValue("Java", new Integer(1901));
    localDefaultKeyedValues.addValue("Perl", new Integer(2507));
    localDefaultKeyedValues.addValue("PHP", new Integer(1689));
    localDefaultKeyedValues.addValue("Python", new Integer(948));
    localDefaultKeyedValues.addValue("Ruby", new Integer(100));
    localDefaultKeyedValues.addValue("SQL", new Integer(263));
    localDefaultKeyedValues.addValue("Unix Shell", new Integer(485));
    localDefaultKeyedValues.sortByValues(SortOrder.DESCENDING);
    KeyedValues localKeyedValues = DataUtilities.getCumulativePercentages(localDefaultKeyedValues);
    CategoryDataset localCategoryDataset1 = DatasetUtilities.createCategoryDataset("Languages", localDefaultKeyedValues);
    CategoryDataset localCategoryDataset2 = DatasetUtilities.createCategoryDataset("Cumulative", localKeyedValues);
    return new CategoryDataset[] { localCategoryDataset1, localCategoryDataset2 };
  }

  public static JPanel createDemoPanel()
  {
    CategoryDataset[] arrayOfCategoryDataset = createDatasets();
    JFreeChart localJFreeChart = createChart(arrayOfCategoryDataset);
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    ParetoChartDemo1 localParetoChartDemo1 = new ParetoChartDemo1("JFreeChart: ParetoChartDemo1.java");
    localParetoChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localParetoChartDemo1);
    localParetoChartDemo1.setVisible(true);
  }
}