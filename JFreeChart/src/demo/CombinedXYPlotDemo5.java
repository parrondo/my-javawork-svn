package demo;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class CombinedXYPlotDemo5 extends ApplicationFrame
{
  public CombinedXYPlotDemo5(String paramString)
  {
    super(paramString);
    JFreeChart localJFreeChart = createCombinedChart();
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart, true, true, true, true, true);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localChartPanel);
  }

  private static JFreeChart createCombinedChart()
  {
    XYDataset localXYDataset1 = createDataset1();
    StandardXYItemRenderer localStandardXYItemRenderer1 = new StandardXYItemRenderer();
    NumberAxis localNumberAxis1 = new NumberAxis("Range 1");
    XYPlot localXYPlot1 = new XYPlot(localXYDataset1, null, localNumberAxis1, localStandardXYItemRenderer1);
    localXYPlot1.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
    XYTextAnnotation localXYTextAnnotation = new XYTextAnnotation("Hello!", 50.0D, 10000.0D);
    localXYTextAnnotation.setFont(new Font("SansSerif", 0, 9));
    localXYTextAnnotation.setRotationAngle(0.7853981633974483D);
    localXYPlot1.addAnnotation(localXYTextAnnotation);
    XYDataset localXYDataset2 = createDataset2();
    StandardXYItemRenderer localStandardXYItemRenderer2 = new StandardXYItemRenderer();
    NumberAxis localNumberAxis2 = new NumberAxis("Range 2");
    localNumberAxis2.setAutoRangeIncludesZero(false);
    XYPlot localXYPlot2 = new XYPlot(localXYDataset2, null, localNumberAxis2, localStandardXYItemRenderer2);
    localXYPlot2.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
    NumberAxis localNumberAxis3 = new NumberAxis("Domain");
    localNumberAxis3.setTickMarkInsideLength(5.0F);
    CombinedDomainXYPlot localCombinedDomainXYPlot = new CombinedDomainXYPlot(localNumberAxis3);
    localCombinedDomainXYPlot.setGap(10.0D);
    localCombinedDomainXYPlot.add(localXYPlot1, 1);
    localCombinedDomainXYPlot.add(localXYPlot2, 1);
    localCombinedDomainXYPlot.setOrientation(PlotOrientation.VERTICAL);
    JFreeChart localJFreeChart = new JFreeChart("CombinedDomainXYPlotDemo5", JFreeChart.DEFAULT_TITLE_FONT, localCombinedDomainXYPlot, true);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  private static XYDataset createDataset1()
  {
    XYSeries localXYSeries1 = new XYSeries("Series 1");
    localXYSeries1.add(10.0D, 12353.299999999999D);
    localXYSeries1.add(20.0D, 13734.4D);
    localXYSeries1.add(30.0D, 14525.299999999999D);
    localXYSeries1.add(40.0D, 13984.299999999999D);
    localXYSeries1.add(50.0D, 12999.4D);
    localXYSeries1.add(60.0D, 14274.299999999999D);
    localXYSeries1.add(70.0D, 15943.5D);
    localXYSeries1.add(80.0D, 14845.299999999999D);
    localXYSeries1.add(90.0D, 14645.4D);
    localXYSeries1.add(100.0D, 16234.6D);
    localXYSeries1.add(110.0D, 17232.299999999999D);
    localXYSeries1.add(120.0D, 14232.200000000001D);
    localXYSeries1.add(130.0D, 13102.200000000001D);
    localXYSeries1.add(140.0D, 14230.200000000001D);
    localXYSeries1.add(150.0D, 11235.200000000001D);
    XYSeries localXYSeries2 = new XYSeries("Series 2");
    localXYSeries2.add(10.0D, 15000.299999999999D);
    localXYSeries2.add(20.0D, 11000.4D);
    localXYSeries2.add(30.0D, 17000.299999999999D);
    localXYSeries2.add(40.0D, 15000.299999999999D);
    localXYSeries2.add(50.0D, 14000.4D);
    localXYSeries2.add(60.0D, 12000.299999999999D);
    localXYSeries2.add(70.0D, 11000.5D);
    localXYSeries2.add(80.0D, 12000.299999999999D);
    localXYSeries2.add(90.0D, 13000.4D);
    localXYSeries2.add(100.0D, 12000.6D);
    localXYSeries2.add(110.0D, 13000.299999999999D);
    localXYSeries2.add(120.0D, 17000.200000000001D);
    localXYSeries2.add(130.0D, 18000.200000000001D);
    localXYSeries2.add(140.0D, 16000.200000000001D);
    localXYSeries2.add(150.0D, 17000.200000000001D);
    XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection();
    localXYSeriesCollection.addSeries(localXYSeries1);
    localXYSeriesCollection.addSeries(localXYSeries2);
    return localXYSeriesCollection;
  }

  private static XYDataset createDataset2()
  {
    XYSeries localXYSeries = new XYSeries("Series 3");
    localXYSeries.add(10.0D, 16853.200000000001D);
    localXYSeries.add(20.0D, 19642.299999999999D);
    localXYSeries.add(30.0D, 18253.5D);
    localXYSeries.add(40.0D, 15352.299999999999D);
    localXYSeries.add(50.0D, 13532.0D);
    localXYSeries.add(100.0D, 12635.299999999999D);
    localXYSeries.add(110.0D, 13998.200000000001D);
    localXYSeries.add(120.0D, 11943.200000000001D);
    localXYSeries.add(130.0D, 16943.900000000001D);
    localXYSeries.add(140.0D, 17843.200000000001D);
    localXYSeries.add(150.0D, 16495.299999999999D);
    localXYSeries.add(160.0D, 17943.599999999999D);
    localXYSeries.add(170.0D, 18500.700000000001D);
    localXYSeries.add(180.0D, 19595.900000000001D);
    return new XYSeriesCollection(localXYSeries);
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createCombinedChart();
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    CombinedXYPlotDemo5 localCombinedXYPlotDemo5 = new CombinedXYPlotDemo5("JFreeChart: CombinedDomainXYPlotDemo5.java");
    localCombinedXYPlotDemo5.pack();
    RefineryUtilities.centerFrameOnScreen(localCombinedXYPlotDemo5);
    localCombinedXYPlotDemo5.setVisible(true);
  }
}