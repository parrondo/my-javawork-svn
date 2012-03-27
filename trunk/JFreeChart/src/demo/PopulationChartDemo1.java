package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DefaultKeyedValues2DDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class PopulationChartDemo1 extends ApplicationFrame
{
  public PopulationChartDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  public static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createStackedBarChart("Population Chart Demo 1", "Age Group", "Population (millions)", paramCategoryDataset, PlotOrientation.HORIZONTAL, true, true, false);
    return localJFreeChart;
  }

  public static CategoryDataset createDataset()
  {
    DefaultKeyedValues2DDataset localDefaultKeyedValues2DDataset = new DefaultKeyedValues2DDataset();
    localDefaultKeyedValues2DDataset.addValue(-6.0D, "Male", "70+");
    localDefaultKeyedValues2DDataset.addValue(-8.0D, "Male", "60-69");
    localDefaultKeyedValues2DDataset.addValue(-11.0D, "Male", "50-59");
    localDefaultKeyedValues2DDataset.addValue(-13.0D, "Male", "40-49");
    localDefaultKeyedValues2DDataset.addValue(-14.0D, "Male", "30-39");
    localDefaultKeyedValues2DDataset.addValue(-15.0D, "Male", "20-29");
    localDefaultKeyedValues2DDataset.addValue(-19.0D, "Male", "10-19");
    localDefaultKeyedValues2DDataset.addValue(-21.0D, "Male", "0-9");
    localDefaultKeyedValues2DDataset.addValue(10.0D, "Female", "70+");
    localDefaultKeyedValues2DDataset.addValue(12.0D, "Female", "60-69");
    localDefaultKeyedValues2DDataset.addValue(13.0D, "Female", "50-59");
    localDefaultKeyedValues2DDataset.addValue(14.0D, "Female", "40-49");
    localDefaultKeyedValues2DDataset.addValue(15.0D, "Female", "30-39");
    localDefaultKeyedValues2DDataset.addValue(17.0D, "Female", "20-29");
    localDefaultKeyedValues2DDataset.addValue(19.0D, "Female", "10-19");
    localDefaultKeyedValues2DDataset.addValue(20.0D, "Female", "0-9");
    return localDefaultKeyedValues2DDataset;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    PopulationChartDemo1 localPopulationChartDemo1 = new PopulationChartDemo1("JFreeChart: PopulationChartDemo1.java");
    localPopulationChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localPopulationChartDemo1);
    localPopulationChartDemo1.setVisible(true);
  }
}