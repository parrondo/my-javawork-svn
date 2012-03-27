package demo;

import java.awt.Dimension;
import java.awt.Font;
import java.text.NumberFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.AbstractCategoryItemLabelGenerator;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class ItemLabelDemo1 extends ApplicationFrame
{
  public ItemLabelDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(11.0D, "S1", "C1");
    localDefaultCategoryDataset.addValue(44.299999999999997D, "S1", "C2");
    localDefaultCategoryDataset.addValue(93.0D, "S1", "C3");
    localDefaultCategoryDataset.addValue(35.600000000000001D, "S1", "C4");
    localDefaultCategoryDataset.addValue(75.099999999999994D, "S1", "C5");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart("Item Label Demo 1", "Category", "Value", paramCategoryDataset, PlotOrientation.VERTICAL, false, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setRangePannable(true);
    localCategoryPlot.setRangeZeroBaselineVisible(true);
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setUpperMargin(0.15D);
    CategoryItemRenderer localCategoryItemRenderer = localCategoryPlot.getRenderer();
    localCategoryItemRenderer.setBaseItemLabelGenerator(new LabelGenerator(50.0D));
    localCategoryItemRenderer.setBaseItemLabelFont(new Font("Serif", 0, 20));
    localCategoryItemRenderer.setBaseItemLabelsVisible(true);
    return localJFreeChart;
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
    ItemLabelDemo1 localItemLabelDemo1 = new ItemLabelDemo1("JFreeChart: ItemLabelDemo1.java");
    localItemLabelDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localItemLabelDemo1);
    localItemLabelDemo1.setVisible(true);
  }

  static class LabelGenerator extends AbstractCategoryItemLabelGenerator
    implements CategoryItemLabelGenerator
  {
    private double threshold;

    public LabelGenerator(double paramDouble)
    {
      super("", NumberFormat.getInstance());
      this.threshold = paramDouble;
    }

    public String generateLabel(CategoryDataset paramCategoryDataset, int paramInt1, int paramInt2)
    {
      String str = null;
      Number localNumber = paramCategoryDataset.getValue(paramInt1, paramInt2);
      if (localNumber != null)
      {
        double d = localNumber.doubleValue();
        if (d > this.threshold)
          str = localNumber.toString();
      }
      return str;
    }
  }
}