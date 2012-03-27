package demo;

import java.awt.Dimension;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class StackedBarDemo1 extends ApplicationFrame
{
  public StackedBarDemo1(String paramString)
  {
    super(paramString);
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(1.0D, "Row 1", "Column 1");
    localDefaultCategoryDataset.addValue(5.0D, "Row 1", "Column 2");
    localDefaultCategoryDataset.addValue(3.0D, "Row 1", "Column 3");
    localDefaultCategoryDataset.addValue(2.0D, "Row 2", "Column 1");
    localDefaultCategoryDataset.addValue(3.0D, "Row 2", "Column 2");
    localDefaultCategoryDataset.addValue(2.0D, "Row 2", "Column 3");
    JFreeChart localJFreeChart = ChartFactory.createStackedBarChart("StackedBarDemo1", "Category", "Value", localDefaultCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localChartPanel);
  }

  public static void main(String[] paramArrayOfString)
  {
    StackedBarDemo1 localStackedBarDemo1 = new StackedBarDemo1("StackedBarDemo1");
    localStackedBarDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localStackedBarDemo1);
    localStackedBarDemo1.setVisible(true);
  }
}