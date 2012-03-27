package demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class DeviationRendererDemo1 extends ApplicationFrame
{
  public DeviationRendererDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static XYDataset createDataset()
  {
    YIntervalSeries localYIntervalSeries1 = new YIntervalSeries("Series 1");
    YIntervalSeries localYIntervalSeries2 = new YIntervalSeries("Series 2");
    double d1 = 100.0D;
    double d2 = 100.0D;
    for (int i = 0; i <= 100; ++i)
    {
      d1 = d1 + Math.random() - 0.48D;
      double d3 = 0.05D * i;
      if (i == 50)
        localYIntervalSeries1.add(i, d1, d1 - d3, d1 + d3);
      else
        localYIntervalSeries1.add(i, d1, d1 - d3, d1 + d3);
      d2 = d2 + Math.random() - 0.5D;
      double d4 = 0.07000000000000001D * i;
      localYIntervalSeries2.add(i, d2, d2 - d4, d2 + d4);
    }
    YIntervalSeriesCollection localYIntervalSeriesCollection = new YIntervalSeriesCollection();
    localYIntervalSeriesCollection.addSeries(localYIntervalSeries1);
    localYIntervalSeriesCollection.addSeries(localYIntervalSeries2);
    return localYIntervalSeriesCollection;
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYLineChart("DeviationRenderer - Demo 1", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    DeviationRenderer localDeviationRenderer = new DeviationRenderer(true, false);
    localDeviationRenderer.setSeriesStroke(0, new BasicStroke(3.0F, 1, 1));
    localDeviationRenderer.setSeriesStroke(0, new BasicStroke(3.0F, 1, 1));
    localDeviationRenderer.setSeriesStroke(1, new BasicStroke(3.0F, 1, 1));
    localDeviationRenderer.setSeriesFillPaint(0, new Color(255, 200, 200));
    localDeviationRenderer.setSeriesFillPaint(1, new Color(200, 200, 255));
    localXYPlot.setRenderer(localDeviationRenderer);
    NumberAxis localNumberAxis = (NumberAxis)localXYPlot.getRangeAxis();
    localNumberAxis.setAutoRangeIncludesZero(false);
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
    DeviationRendererDemo1 localDeviationRendererDemo1 = new DeviationRendererDemo1("JFreeChart : DeviationRendererDemo1.java");
    localDeviationRendererDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localDeviationRendererDemo1);
    localDeviationRendererDemo1.setVisible(true);
  }
}