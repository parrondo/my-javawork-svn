package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class XYSeriesDemo2 extends ApplicationFrame
{
  public XYSeriesDemo2(String paramString)
  {
    super(paramString);
    XYDataset localXYDataset = createDataset();
    JFreeChart localJFreeChart = createChart(localXYDataset);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localChartPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYLineChart("XY Series Demo 2", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    NumberAxis localNumberAxis = (NumberAxis)localXYPlot.getRangeAxis();
    localNumberAxis.setAutoRangeIncludesZero(false);
    localNumberAxis.setAutoRangeMinimumSize(1.0D);
    return localJFreeChart;
  }

  private static XYDataset createDataset()
  {
    XYSeries localXYSeries = new XYSeries("Flat Data");
    localXYSeries.add(1.0D, 100.0D);
    localXYSeries.add(5.0D, 100.0D);
    localXYSeries.add(4.0D, 100.0D);
    localXYSeries.add(12.5D, 100.0D);
    localXYSeries.add(17.300000000000001D, 100.0D);
    localXYSeries.add(21.199999999999999D, 100.0D);
    localXYSeries.add(21.899999999999999D, 100.0D);
    localXYSeries.add(25.600000000000001D, 100.0D);
    localXYSeries.add(30.0D, 100.0D);
    XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection(localXYSeries);
    return localXYSeriesCollection;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    XYSeriesDemo2 localXYSeriesDemo2 = new XYSeriesDemo2("JFreeChart: XYSeriesDemo2.java");
    localXYSeriesDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localXYSeriesDemo2);
    localXYSeriesDemo2.setVisible(true);
  }
}