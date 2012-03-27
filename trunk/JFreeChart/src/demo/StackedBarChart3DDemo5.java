package demo;

import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class StackedBarChart3DDemo5 extends ApplicationFrame
{
  private static int CHART_COUNT = 4;

  public StackedBarChart3DDemo5(String paramString)
  {
    super(paramString);
    setContentPane(createDemoPanel());
  }

  private static CategoryDataset createDataset(int paramInt)
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(1.0D, "Series 1", "Category 1");
    localDefaultCategoryDataset.addValue(2.0D, "Series 1", "Category 2");
    localDefaultCategoryDataset.addValue(1.5D, "Series 1", "Category 3");
    localDefaultCategoryDataset.addValue(1.5D, "Series 1", "Category 4");
    localDefaultCategoryDataset.addValue(-1.0D, "Series 2", "Category 1");
    localDefaultCategoryDataset.addValue(-1.9D, "Series 2", "Category 2");
    localDefaultCategoryDataset.addValue(-1.5D, "Series 2", "Category 3");
    localDefaultCategoryDataset.addValue(-1.5D, "Series 2", "Category 4");
    localDefaultCategoryDataset.addValue(1.0D, "Series 3", "Category 1");
    localDefaultCategoryDataset.addValue(1.9D, "Series 3", "Category 2");
    localDefaultCategoryDataset.addValue(1.5D, "Series 3", "Category 3");
    localDefaultCategoryDataset.addValue(1.5D, "Series 3", "Category 4");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(int paramInt, CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createStackedBarChart3D("Chart " + (paramInt + 1), "Category", "Value", paramCategoryDataset, PlotOrientation.VERTICAL, false, false, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.getDomainAxis().setMaximumCategoryLabelLines(2);
    ValueAxis localValueAxis = localCategoryPlot.getRangeAxis();
    localValueAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    return new MyDemoPanel();
  }

  public static void main(String[] paramArrayOfString)
  {
    StackedBarChart3DDemo5 localStackedBarChart3DDemo5 = new StackedBarChart3DDemo5("JFreeChart - Stacked Bar Chart 3D Demo 5");
    localStackedBarChart3DDemo5.pack();
    RefineryUtilities.centerFrameOnScreen(localStackedBarChart3DDemo5);
    localStackedBarChart3DDemo5.setVisible(true);
  }

  static class MyDemoPanel extends DemoPanel
  {
    private CategoryDataset[] datasets = new CategoryDataset[StackedBarChart3DDemo5.CHART_COUNT];
    private JFreeChart[] charts = new JFreeChart[StackedBarChart3DDemo5.CHART_COUNT];
    private ChartPanel[] panels = new ChartPanel[StackedBarChart3DDemo5.CHART_COUNT];

    public MyDemoPanel()
    {
      super(new GridLayout(2, 2));
      for (int i = 0; i < StackedBarChart3DDemo5.CHART_COUNT; ++i)
      {
        this.datasets[i] = StackedBarChart3DDemo5.createDataset(i);
        this.charts[i] = StackedBarChart3DDemo5.createChart(i, this.datasets[i]);
        addChart(this.charts[i]);
        this.panels[i] = new ChartPanel(this.charts[i]);
      }
      CategoryPlot localCategoryPlot1 = (CategoryPlot)this.charts[1].getPlot();
      CategoryPlot localCategoryPlot2 = (CategoryPlot)this.charts[2].getPlot();
      CategoryPlot localCategoryPlot3 = (CategoryPlot)this.charts[3].getPlot();
      localCategoryPlot1.getRangeAxis().setInverted(true);
      localCategoryPlot3.getRangeAxis().setInverted(true);
      localCategoryPlot2.setOrientation(PlotOrientation.HORIZONTAL);
      localCategoryPlot3.setOrientation(PlotOrientation.HORIZONTAL);
      add(this.panels[0]);
      add(this.panels[1]);
      add(this.panels[2]);
      add(this.panels[3]);
      setPreferredSize(new Dimension(800, 600));
    }
  }
}