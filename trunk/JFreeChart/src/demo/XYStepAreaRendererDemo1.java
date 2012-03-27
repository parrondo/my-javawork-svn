package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYStepAreaRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class XYStepAreaRendererDemo1 extends ApplicationFrame
{
  public XYStepAreaRendererDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 300));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYLineChart("XYStepAreaRenderer Demo 1", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    XYStepAreaRenderer localXYStepAreaRenderer = new XYStepAreaRenderer(2);
    localXYStepAreaRenderer.setDataBoundsIncludesVisibleSeriesOnly(false);
    localXYStepAreaRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
    localXYStepAreaRenderer.setDefaultEntityRadius(6);
    localXYPlot.setRenderer(localXYStepAreaRenderer);
    return localJFreeChart;
  }

  private static XYDataset createDataset()
  {
    XYSeries localXYSeries1 = new XYSeries("Series 1");
    localXYSeries1.add(1.0D, 3.0D);
    localXYSeries1.add(2.0D, 4.0D);
    localXYSeries1.add(3.0D, 2.0D);
    localXYSeries1.add(6.0D, 3.0D);
    XYSeries localXYSeries2 = new XYSeries("Series 2");
    localXYSeries2.add(1.0D, 7.0D);
    localXYSeries2.add(2.0D, 6.0D);
    localXYSeries2.add(3.0D, 9.0D);
    localXYSeries2.add(4.0D, 5.0D);
    localXYSeries2.add(6.0D, 4.0D);
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
    XYStepAreaRendererDemo1 localXYStepAreaRendererDemo1 = new XYStepAreaRendererDemo1("XYStepAreaRenderer Demo 1");
    localXYStepAreaRendererDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localXYStepAreaRendererDemo1);
    localXYStepAreaRendererDemo1.setVisible(true);
  }
}