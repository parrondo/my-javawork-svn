package demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class PolarChartDemo1 extends ApplicationFrame
{
  public PolarChartDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static XYDataset createDataset()
  {
    XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection();
    XYSeries localXYSeries1 = new XYSeries("Series 1");
    localXYSeries1.add(0.0D, 2.0D);
    localXYSeries1.add(90.0D, 13.0D);
    localXYSeries1.add(180.0D, 9.0D);
    localXYSeries1.add(270.0D, 8.0D);
    localXYSeriesCollection.addSeries(localXYSeries1);
    XYSeries localXYSeries2 = new XYSeries("Series 2");
    localXYSeries2.add(90.0D, -11.199999999999999D);
    localXYSeries2.add(180.0D, 21.399999999999999D);
    localXYSeries2.add(250.0D, 17.300000000000001D);
    localXYSeries2.add(355.0D, 10.9D);
    localXYSeriesCollection.addSeries(localXYSeries2);
    return localXYSeriesCollection;
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createPolarChart("Polar Chart Demo 1", paramXYDataset, true, false, false);
    PolarPlot localPolarPlot = (PolarPlot)localJFreeChart.getPlot();
    localPolarPlot.addCornerTextItem("Corner Item 1");
    localPolarPlot.addCornerTextItem("Corner Item 2");
    localPolarPlot.setAngleGridlinePaint(Color.white);
    localPolarPlot.setRadiusGridlinePaint(Color.white);
    NumberAxis localNumberAxis = (NumberAxis)localPolarPlot.getAxis();
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setMouseZoomable(false);
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    PolarChartDemo1 localPolarChartDemo1 = new PolarChartDemo1("JFreeChart: PolarChartDemo1.java");
    localPolarChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localPolarChartDemo1);
    localPolarChartDemo1.setVisible(true);
  }
}