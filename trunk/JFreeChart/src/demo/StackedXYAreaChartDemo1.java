package demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class StackedXYAreaChartDemo1 extends ApplicationFrame
{
  public StackedXYAreaChartDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static TableXYDataset createDataset()
  {
    DefaultTableXYDataset localDefaultTableXYDataset = new DefaultTableXYDataset();
    XYSeries localXYSeries1 = new XYSeries("Series 1", true, false);
    localXYSeries1.add(5.0D, 5.0D);
    localXYSeries1.add(10.0D, 15.5D);
    localXYSeries1.add(15.0D, 9.5D);
    localXYSeries1.add(20.0D, 7.5D);
    localDefaultTableXYDataset.addSeries(localXYSeries1);
    XYSeries localXYSeries2 = new XYSeries("Series 2", true, false);
    localXYSeries2.add(5.0D, 5.0D);
    localXYSeries2.add(10.0D, 15.5D);
    localXYSeries2.add(15.0D, 9.5D);
    localXYSeries2.add(20.0D, 3.5D);
    localDefaultTableXYDataset.addSeries(localXYSeries2);
    return localDefaultTableXYDataset;
  }

  private static JFreeChart createChart(TableXYDataset paramTableXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createStackedXYAreaChart("Stacked XY Area Chart Demo 1", "X Value", "Y Value", paramTableXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    StackedXYAreaRenderer localStackedXYAreaRenderer = new StackedXYAreaRenderer();
    localStackedXYAreaRenderer.setSeriesPaint(0, Color.lightGray);
    localStackedXYAreaRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
    localXYPlot.setRenderer(0, localStackedXYAreaRenderer);
    localXYPlot.setDomainCrosshairVisible(true);
    localXYPlot.setRangeCrosshairVisible(true);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    StackedXYAreaChartDemo1 localStackedXYAreaChartDemo1 = new StackedXYAreaChartDemo1("Stacked XY Area Chart Demo 1");
    localStackedXYAreaChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localStackedXYAreaChartDemo1);
    localStackedXYAreaChartDemo1.setVisible(true);
  }
}