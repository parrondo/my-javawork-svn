package demo;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class XYSplineRendererDemo1a extends ApplicationFrame
{
  public XYSplineRendererDemo1a(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    getContentPane().add(localJPanel);
  }

  public static JPanel createDemoPanel()
  {
    NumberAxis localNumberAxis1 = new NumberAxis("X");
    localNumberAxis1.setAutoRangeIncludesZero(false);
    NumberAxis localNumberAxis2 = new NumberAxis("Y");
    localNumberAxis2.setAutoRangeIncludesZero(false);
    XYSplineRenderer localXYSplineRenderer = new XYSplineRenderer();
    XYPlot localXYPlot = new XYPlot(createSampleData(), localNumberAxis1, localNumberAxis2, localXYSplineRenderer);
    localXYPlot.setBackgroundPaint(Color.lightGray);
    localXYPlot.setDomainGridlinePaint(Color.white);
    localXYPlot.setRangeGridlinePaint(Color.white);
    localXYPlot.setAxisOffset(new RectangleInsets(4.0D, 4.0D, 4.0D, 4.0D));
    JFreeChart localJFreeChart = new JFreeChart("XYSplineRenderer", JFreeChart.DEFAULT_TITLE_FONT, localXYPlot, true);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    return localChartPanel;
  }

  private static XYDataset createSampleData()
  {
    XYSeries localXYSeries1 = new XYSeries("Series 1");
    localXYSeries1.add(2.0D, 56.270000000000003D);
    localXYSeries1.add(3.0D, 41.32D);
    localXYSeries1.add(4.0D, 31.449999999999999D);
    localXYSeries1.add(5.0D, 30.050000000000001D);
    localXYSeries1.add(6.0D, 24.690000000000001D);
    localXYSeries1.add(7.0D, 19.780000000000001D);
    localXYSeries1.add(8.0D, 20.940000000000001D);
    localXYSeries1.add(9.0D, 16.73D);
    localXYSeries1.add(10.0D, 14.210000000000001D);
    localXYSeries1.add(11.0D, 12.44D);
    XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection(localXYSeries1);
    XYSeries localXYSeries2 = new XYSeries("Series 2");
    localXYSeries2.add(11.0D, 56.270000000000003D);
    localXYSeries2.add(10.0D, 41.32D);
    localXYSeries2.add(9.0D, 31.449999999999999D);
    localXYSeries2.add(8.0D, 30.050000000000001D);
    localXYSeries2.add(7.0D, 24.690000000000001D);
    localXYSeries2.add(6.0D, 19.780000000000001D);
    localXYSeries2.add(5.0D, 20.940000000000001D);
    localXYSeries2.add(4.0D, 16.73D);
    localXYSeries2.add(3.0D, 14.210000000000001D);
    localXYSeries2.add(2.0D, 12.44D);
    localXYSeriesCollection.addSeries(localXYSeries2);
    return localXYSeriesCollection;
  }

  public static void main(String[] paramArrayOfString)
  {
    XYSplineRendererDemo1a localXYSplineRendererDemo1a = new XYSplineRendererDemo1a("JFreeChart: XYSplineRendererDemo1a.java");
    localXYSplineRendererDemo1a.pack();
    RefineryUtilities.centerFrameOnScreen(localXYSplineRendererDemo1a);
    localXYSplineRendererDemo1a.setVisible(true);
  }
}