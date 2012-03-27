package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.data.xy.CategoryTableXYDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class StackedXYAreaChartDemo2 extends ApplicationFrame
{
  public StackedXYAreaChartDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static TableXYDataset createDataset()
  {
    CategoryTableXYDataset localCategoryTableXYDataset = new CategoryTableXYDataset();
    localCategoryTableXYDataset.add(0.0D, 0.0D, "Series 1");
    localCategoryTableXYDataset.add(10.0D, 20.0D, "Series 1");
    localCategoryTableXYDataset.add(20.0D, 15.0D, "Series 1");
    localCategoryTableXYDataset.add(30.0D, 25.0D, "Series 1");
    localCategoryTableXYDataset.add(40.0D, 21.0D, "Series 1");
    localCategoryTableXYDataset.add(10.0D, 9.0D, "Series 2");
    localCategoryTableXYDataset.add(20.0D, -7.0D, "Series 2");
    localCategoryTableXYDataset.add(30.0D, 15.0D, "Series 2");
    localCategoryTableXYDataset.add(40.0D, 11.0D, "Series 2");
    localCategoryTableXYDataset.add(45.0D, -10.0D, "Series 2");
    localCategoryTableXYDataset.add(50.0D, 0.0D, "Series 2");
    return localCategoryTableXYDataset;
  }

  private static JFreeChart createChart(TableXYDataset paramTableXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createStackedXYAreaChart("Stacked XY Area Chart Demo 2", "X Value", "Y Value", paramTableXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    StackedXYAreaRenderer2 localStackedXYAreaRenderer2 = new StackedXYAreaRenderer2();
    localStackedXYAreaRenderer2.setRoundXCoordinates(true);
    localStackedXYAreaRenderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
    localXYPlot.setRenderer(0, localStackedXYAreaRenderer2);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    StackedXYAreaChartDemo2 localStackedXYAreaChartDemo2 = new StackedXYAreaChartDemo2("Stacked XY Area Chart Demo 2");
    localStackedXYAreaChartDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localStackedXYAreaChartDemo2);
    localStackedXYAreaChartDemo2.setVisible(true);
  }
}