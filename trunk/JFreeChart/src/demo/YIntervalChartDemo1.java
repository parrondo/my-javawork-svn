package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.YIntervalRenderer;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class YIntervalChartDemo1 extends ApplicationFrame
{
  public YIntervalChartDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 300));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(IntervalXYDataset paramIntervalXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createScatterPlot("Y Interval Chart Demo 1", "X", "Y", paramIntervalXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRenderer(new YIntervalRenderer());
    return localJFreeChart;
  }

  private static IntervalXYDataset createDataset()
  {
    double d = 100.0D;
    YIntervalSeries localYIntervalSeries = new YIntervalSeries("Series 1");
    for (int i = 0; i < 100; ++i)
    {
      d += Math.random() - 0.49D;
      localYIntervalSeries.add(i, d, d - 3.0D, d + 3.0D);
    }
    YIntervalSeriesCollection localYIntervalSeriesCollection = new YIntervalSeriesCollection();
    localYIntervalSeriesCollection.addSeries(localYIntervalSeries);
    return localYIntervalSeriesCollection;
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
    YIntervalChartDemo1 localYIntervalChartDemo1 = new YIntervalChartDemo1("JFreeChart: YIntervalChartDemo1.java");
    localYIntervalChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localYIntervalChartDemo1);
    localYIntervalChartDemo1.setVisible(true);
  }
}