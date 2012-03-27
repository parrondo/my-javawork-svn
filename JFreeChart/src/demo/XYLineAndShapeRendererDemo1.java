package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class XYLineAndShapeRendererDemo1 extends ApplicationFrame
{
  public XYLineAndShapeRendererDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 300));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYLineChart("XYLineAndShapeRenderer Demo 1", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    XYLineAndShapeRenderer localXYLineAndShapeRenderer = new XYLineAndShapeRenderer();
    localXYLineAndShapeRenderer.setSeriesLinesVisible(0, true);
    localXYLineAndShapeRenderer.setSeriesShapesVisible(0, false);
    localXYLineAndShapeRenderer.setSeriesLinesVisible(1, false);
    localXYLineAndShapeRenderer.setSeriesShapesVisible(1, true);
    localXYLineAndShapeRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
    localXYLineAndShapeRenderer.setDefaultEntityRadius(6);
    localXYPlot.setRenderer(localXYLineAndShapeRenderer);
    return localJFreeChart;
  }

  private static XYDataset createDataset()
  {
    XYSeries localXYSeries1 = new XYSeries("Series 1");
    localXYSeries1.add(1.0D, 3.3D);
    localXYSeries1.add(2.0D, 4.4D);
    localXYSeries1.add(3.0D, 1.7D);
    XYSeries localXYSeries2 = new XYSeries("Series 2");
    localXYSeries2.add(1.0D, 7.3D);
    localXYSeries2.add(2.0D, 0.0D);
    localXYSeries2.add(3.0D, 9.6D);
    localXYSeries2.add(4.0D, 5.6D);
    XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection();
    localXYSeriesCollection.addSeries(localXYSeries1);
    localXYSeriesCollection.addSeries(localXYSeries2);
    return localXYSeriesCollection;
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
    XYLineAndShapeRendererDemo1 localXYLineAndShapeRendererDemo1 = new XYLineAndShapeRendererDemo1("JFreeChart: XYLineAndShapeRendererDemo1.java");
    localXYLineAndShapeRendererDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localXYLineAndShapeRendererDemo1);
    localXYLineAndShapeRendererDemo1.setVisible(true);
  }
}