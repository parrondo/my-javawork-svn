package demo;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class BoxAndWhiskerChartDemo1 extends ApplicationFrame
{
  public BoxAndWhiskerChartDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static BoxAndWhiskerCategoryDataset createDataset()
  {
    int i = 3;
    int j = 5;
    int k = 20;
    DefaultBoxAndWhiskerCategoryDataset localDefaultBoxAndWhiskerCategoryDataset = new DefaultBoxAndWhiskerCategoryDataset();
    for (int l = 0; l < i; ++l)
      for (int i1 = 0; i1 < j; ++i1)
      {
        List localList = createValueList(0.0D, 20.0D, k);
        localDefaultBoxAndWhiskerCategoryDataset.add(localList, "Series " + l, "Category " + i1);
      }
    return localDefaultBoxAndWhiskerCategoryDataset;
  }

  private static List createValueList(double paramDouble1, double paramDouble2, int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < paramInt; ++i)
    {
      double d = paramDouble1 + Math.random() * (paramDouble2 - paramDouble1);
      localArrayList.add(new Double(d));
    }
    return localArrayList;
  }

  private static JFreeChart createChart(BoxAndWhiskerCategoryDataset paramBoxAndWhiskerCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBoxAndWhiskerChart("Box and Whisker Chart Demo 1", "Category", "Value", paramBoxAndWhiskerCategoryDataset, true);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setDomainGridlinesVisible(true);
    localCategoryPlot.setRangePannable(true);
    NumberAxis localNumberAxis = (NumberAxis)localCategoryPlot.getRangeAxis();
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
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
    BoxAndWhiskerChartDemo1 localBoxAndWhiskerChartDemo1 = new BoxAndWhiskerChartDemo1("JFreeChart: BoxAndWhiskerChartDemo1.java");
    localBoxAndWhiskerChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localBoxAndWhiskerChartDemo1);
    localBoxAndWhiskerChartDemo1.setVisible(true);
  }
}