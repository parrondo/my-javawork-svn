package demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XIntervalSeries;
import org.jfree.data.xy.XIntervalSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class XIntervalSeriesCollectionDemo1 extends ApplicationFrame
{
  public XIntervalSeriesCollectionDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static IntervalXYDataset createDataset()
  {
    XIntervalSeriesCollection localXIntervalSeriesCollection = new XIntervalSeriesCollection();
    XIntervalSeries localXIntervalSeries = new XIntervalSeries("S1");
    localXIntervalSeries.add(5.0D, 2.0D, 7.5D, 12.300000000000001D);
    localXIntervalSeries.add(10.0D, 8.0D, 11.0D, 20.0D);
    localXIntervalSeriesCollection.addSeries(localXIntervalSeries);
    return localXIntervalSeriesCollection;
  }

  private static JFreeChart createChart(IntervalXYDataset paramIntervalXYDataset)
  {
    DateAxis localDateAxis = new DateAxis("Date");
    NumberAxis localNumberAxis = new NumberAxis("Y");
    XYBarRenderer localXYBarRenderer = new XYBarRenderer();
    localXYBarRenderer.setUseYInterval(false);
    XYPlot localXYPlot = new XYPlot(paramIntervalXYDataset, localDateAxis, localNumberAxis, localXYBarRenderer);
    JFreeChart localJFreeChart = new JFreeChart(localXYPlot);
    localXYPlot.setBackgroundPaint(Color.lightGray);
    localXYPlot.setDomainGridlinePaint(Color.white);
    localXYPlot.setDomainGridlinesVisible(true);
    localXYPlot.setRangeGridlinePaint(Color.white);
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
    XIntervalSeriesCollectionDemo1 localXIntervalSeriesCollectionDemo1 = new XIntervalSeriesCollectionDemo1("Demo 1");
    localXIntervalSeriesCollectionDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localXIntervalSeriesCollectionDemo1);
    localXIntervalSeriesCollectionDemo1.setVisible(true);
  }
}