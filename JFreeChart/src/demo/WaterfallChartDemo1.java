package demo;

import java.awt.Dimension;
import java.text.DecimalFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class WaterfallChartDemo1 extends ApplicationFrame
{
  public WaterfallChartDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(15.76D, "Product 1", "Labour");
    localDefaultCategoryDataset.addValue(8.66D, "Product 1", "Administration");
    localDefaultCategoryDataset.addValue(4.71D, "Product 1", "Marketing");
    localDefaultCategoryDataset.addValue(3.51D, "Product 1", "Distribution");
    localDefaultCategoryDataset.addValue(32.640000000000001D, "Product 1", "Total Expense");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createWaterfallChart("Product Cost Breakdown", "Expense Category", "Cost Per Unit", paramCategoryDataset, PlotOrientation.VERTICAL, false, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    ValueAxis localValueAxis = localCategoryPlot.getRangeAxis();
    DecimalFormat localDecimalFormat1 = new DecimalFormat("##,###");
    localDecimalFormat1.setNegativePrefix("(");
    localDecimalFormat1.setNegativeSuffix(")");
    TickUnits localTickUnits = new TickUnits();
    localTickUnits.add(new NumberTickUnit(5.0D, localDecimalFormat1));
    localTickUnits.add(new NumberTickUnit(10.0D, localDecimalFormat1));
    localTickUnits.add(new NumberTickUnit(20.0D, localDecimalFormat1));
    localTickUnits.add(new NumberTickUnit(50.0D, localDecimalFormat1));
    localTickUnits.add(new NumberTickUnit(100.0D, localDecimalFormat1));
    localTickUnits.add(new NumberTickUnit(200.0D, localDecimalFormat1));
    localTickUnits.add(new NumberTickUnit(500.0D, localDecimalFormat1));
    localTickUnits.add(new NumberTickUnit(1000.0D, localDecimalFormat1));
    localTickUnits.add(new NumberTickUnit(2000.0D, localDecimalFormat1));
    localTickUnits.add(new NumberTickUnit(5000.0D, localDecimalFormat1));
    localValueAxis.setStandardTickUnits(localTickUnits);
    BarRenderer localBarRenderer = (BarRenderer)localCategoryPlot.getRenderer();
    localBarRenderer.setDrawBarOutline(false);
    localBarRenderer.setBase(5.0D);
    DecimalFormat localDecimalFormat2 = new DecimalFormat("$##,###.00");
    localDecimalFormat2.setNegativePrefix("(");
    localDecimalFormat2.setNegativeSuffix(")");
    localBarRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", localDecimalFormat2));
    localBarRenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator("{0}, {1}) = {2}", new DecimalFormat("$##,###.00")));
    localBarRenderer.setBaseItemLabelsVisible(true);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    return new ChartPanel(createChart(createDataset()));
  }

  public static void main(String[] paramArrayOfString)
  {
    WaterfallChartDemo1 localWaterfallChartDemo1 = new WaterfallChartDemo1("JFreeChart: WaterfallChartDemo1.java");
    localWaterfallChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localWaterfallChartDemo1);
    localWaterfallChartDemo1.setVisible(true);
  }
}