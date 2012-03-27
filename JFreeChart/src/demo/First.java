package demo;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class First
{
  public static void main(String[] paramArrayOfString)
  {
    DefaultPieDataset localDefaultPieDataset = new DefaultPieDataset();
    localDefaultPieDataset.setValue("Category 1", 43.200000000000003D);
    localDefaultPieDataset.setValue("Category 2", 27.899999999999999D);
    localDefaultPieDataset.setValue("Category 3", 79.5D);
    JFreeChart localJFreeChart = ChartFactory.createPieChart("Sample Pie Chart", localDefaultPieDataset, true, true, false);
    ChartFrame localChartFrame = new ChartFrame("JFreeChart: First.java", localJFreeChart);
    localChartFrame.pack();
    localChartFrame.setVisible(true);
  }
}