package demo;

import java.awt.Dimension;
import java.text.NumberFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.AbstractCategoryItemLabelGenerator;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class ItemLabelDemo2 extends ApplicationFrame
{
  public ItemLabelDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(100.0D, "S1", "C1");
    localDefaultCategoryDataset.addValue(44.299999999999997D, "S1", "C2");
    localDefaultCategoryDataset.addValue(93.0D, "S1", "C3");
    localDefaultCategoryDataset.addValue(80.0D, "S2", "C1");
    localDefaultCategoryDataset.addValue(75.099999999999994D, "S2", "C2");
    localDefaultCategoryDataset.addValue(15.1D, "S2", "C3");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart("Item Label Demo 2", "Category", "Value", paramCategoryDataset, PlotOrientation.HORIZONTAL, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
    localCategoryPlot.setRangePannable(true);
    localCategoryPlot.setRangeZeroBaselineVisible(true);
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setUpperMargin(0.25D);
    BarRenderer localBarRenderer = (BarRenderer)localCategoryPlot.getRenderer();
    localBarRenderer.setBaseItemLabelsVisible(true);
    localBarRenderer.setItemLabelAnchorOffset(7.0D);
    localBarRenderer.setBaseItemLabelGenerator(new LabelGenerator(null));
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
    ItemLabelDemo2 localItemLabelDemo2 = new ItemLabelDemo2("JFreeChart: ItemLabelDemo2.java");
    localItemLabelDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localItemLabelDemo2);
    localItemLabelDemo2.setVisible(true);
  }

  static class LabelGenerator extends AbstractCategoryItemLabelGenerator
    implements CategoryItemLabelGenerator
  {
    private Integer category;
    private NumberFormat formatter;

    public LabelGenerator(int paramInt)
    {
      this(new Integer(paramInt));
    }

    public LabelGenerator(Integer paramInteger)
    {
      super("", NumberFormat.getInstance());
      this.formatter = NumberFormat.getPercentInstance();
      this.category = paramInteger;
    }

    public String generateLabel(CategoryDataset paramCategoryDataset, int paramInt1, int paramInt2)
    {
      String str = null;
      double d1 = 0.0D;
      if (this.category != null)
      {
        Number localNumber = paramCategoryDataset.getValue(paramInt1, this.category.intValue());
        d1 = localNumber.doubleValue();
      }
      else
      {
        d1 = calculateSeriesTotal(paramCategoryDataset, paramInt1);
      }
      Number localNumber = paramCategoryDataset.getValue(paramInt1, paramInt2);
      if (localNumber != null)
      {
        double d2 = localNumber.doubleValue();
        str = localNumber.toString() + " (" + this.formatter.format(d2 / d1) + ")";
      }
      return str;
    }

    private double calculateSeriesTotal(CategoryDataset paramCategoryDataset, int paramInt)
    {
      double d = 0.0D;
      for (int i = 0; i < paramCategoryDataset.getColumnCount(); ++i)
      {
        Number localNumber = paramCategoryDataset.getValue(paramInt, i);
        if (localNumber == null)
          continue;
        d += localNumber.doubleValue();
      }
      return d;
    }
  }
}