package demo;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class SymbolAxisDemo1 extends ApplicationFrame
{
  public SymbolAxisDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 300));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    SymbolAxis localSymbolAxis1 = new SymbolAxis("Domain", new String[] { "A", "B", "C", "DDDDDDDDDDDDDDDDDDD" });
    SymbolAxis localSymbolAxis2 = new SymbolAxis("Range", new String[] { "V", "X", "Y", "Z" });
    localSymbolAxis2.setUpperMargin(0.0D);
    XYLineAndShapeRenderer localXYLineAndShapeRenderer = new XYLineAndShapeRenderer(false, true);
    XYPlot localXYPlot = new XYPlot(paramXYDataset, localSymbolAxis1, localSymbolAxis2, localXYLineAndShapeRenderer);
    JFreeChart localJFreeChart = new JFreeChart("SymbolAxis Demo 1", localXYPlot);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  private static XYDataset createDataset()
  {
    XYSeries localXYSeries1 = new XYSeries("Series 1");
    localXYSeries1.add(0.0D, 3.0D);
    localXYSeries1.add(1.0D, 3.0D);
    localXYSeries1.add(2.0D, 0.0D);
    localXYSeries1.add(3.0D, 1.0D);
    XYSeries localXYSeries2 = new XYSeries("Series 2");
    localXYSeries2.add(0.0D, 1.0D);
    localXYSeries2.add(1.0D, 2.0D);
    localXYSeries2.add(2.0D, 1.0D);
    localXYSeries2.add(3.0D, 3.0D);
    XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection();
    localXYSeriesCollection.addSeries(localXYSeries1);
    localXYSeriesCollection.addSeries(localXYSeries2);
    return localXYSeriesCollection;
  }

  public static JPanel createDemoPanel()
  {
    return new ChartPanel(createChart(createDataset()));
  }

  public static void main(String[] paramArrayOfString)
  {
    SymbolAxisDemo1 localSymbolAxisDemo1 = new SymbolAxisDemo1("JFreeChart: SymbolAxisDemo1.java");
    localSymbolAxisDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localSymbolAxisDemo1);
    localSymbolAxisDemo1.setVisible(true);
  }
}