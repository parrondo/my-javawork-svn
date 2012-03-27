package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class XYSeriesDemo3 extends ApplicationFrame
{
  public XYSeriesDemo3(String paramString)
  {
    super(paramString);
    IntervalXYDataset localIntervalXYDataset = createDataset();
    JFreeChart localJFreeChart = createChart(localIntervalXYDataset);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localChartPanel);
  }

  private static IntervalXYDataset createDataset()
  {
    XYSeries localXYSeries = new XYSeries("Random Data");
    localXYSeries.add(1.0D, 400.19999999999999D);
    localXYSeries.add(5.0D, 294.10000000000002D);
    localXYSeries.add(4.0D, 100.0D);
    localXYSeries.add(12.5D, 734.39999999999998D);
    localXYSeries.add(17.300000000000001D, 453.19999999999999D);
    localXYSeries.add(21.199999999999999D, 500.19999999999999D);
    localXYSeries.add(21.899999999999999D, null);
    localXYSeries.add(25.600000000000001D, 734.39999999999998D);
    localXYSeries.add(30.0D, 453.19999999999999D);
    XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection(localXYSeries);
    return localXYSeriesCollection;
  }

  private static JFreeChart createChart(IntervalXYDataset paramIntervalXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYBarChart("XY Series Demo 3", "X", false, "Y", paramIntervalXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    IntervalMarker localIntervalMarker = new IntervalMarker(400.0D, 700.0D);
    localIntervalMarker.setLabel("Target Range");
    localIntervalMarker.setLabelFont(new Font("SansSerif", 2, 11));
    localIntervalMarker.setLabelAnchor(RectangleAnchor.LEFT);
    localIntervalMarker.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
    localIntervalMarker.setPaint(new Color(222, 222, 255, 128));
    localXYPlot.addRangeMarker(localIntervalMarker, Layer.BACKGROUND);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    XYSeriesDemo3 localXYSeriesDemo3 = new XYSeriesDemo3("JFreeChart: XYSeriesDemo3.java");
    localXYSeriesDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localXYSeriesDemo3);
    localXYSeriesDemo3.setVisible(true);
  }
}