package demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
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

public class StackedXYAreaRendererDemo1 extends ApplicationFrame
{
  public StackedXYAreaRendererDemo1(String paramString)
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
    JFreeChart localJFreeChart = ChartFactory.createStackedXYAreaChart("StackedXYAreaRendererDemo1", "X Value", "Y Value", paramTableXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    StackedXYAreaRenderer localStackedXYAreaRenderer = new StackedXYAreaRenderer(5);
    localStackedXYAreaRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
    localXYPlot.setRenderer(0, localStackedXYAreaRenderer);
    localXYPlot.setDomainCrosshairVisible(true);
    localXYPlot.setRangeCrosshairVisible(true);
    localStackedXYAreaRenderer.setShapePaint(Color.yellow);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    StackedXYAreaRendererDemo1 localStackedXYAreaRendererDemo1 = new StackedXYAreaRendererDemo1("StackedXYAreaRendererDemo1");
    localStackedXYAreaRendererDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localStackedXYAreaRendererDemo1);
    localStackedXYAreaRendererDemo1.setVisible(true);
  }
}