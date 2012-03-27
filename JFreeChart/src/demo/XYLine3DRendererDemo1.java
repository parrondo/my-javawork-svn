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
import org.jfree.chart.renderer.xy.XYLine3DRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class XYLine3DRendererDemo1 extends ApplicationFrame
{
  public XYLine3DRendererDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 300));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYLineChart("XYLine3DRenderer Demo 1", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    XYLine3DRenderer localXYLine3DRenderer = new XYLine3DRenderer();
    localXYLine3DRenderer.setWallPaint(Color.gray);
    localXYLine3DRenderer.setXOffset(2.0D);
    localXYLine3DRenderer.setYOffset(3.0D);
    localXYLine3DRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
    localXYLine3DRenderer.setDefaultEntityRadius(6);
    localXYPlot.setRenderer(localXYLine3DRenderer);
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
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    XYLine3DRendererDemo1 localXYLine3DRendererDemo1 = new XYLine3DRendererDemo1("JFreeChart: XYLine3DRendererDemo1.java");
    localXYLine3DRendererDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localXYLine3DRendererDemo1);
    localXYLine3DRendererDemo1.setVisible(true);
  }
}