package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class XYAreaChartDemo1 extends ApplicationFrame
{
  public XYAreaChartDemo1(String paramString)
  {
    super(paramString);
    XYDataset localXYDataset = createDataset();
    JFreeChart localJFreeChart = createChart(localXYDataset);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localChartPanel);
  }

  private static XYDataset createDataset()
  {
    XYSeries localXYSeries1 = new XYSeries("Random 1");
    localXYSeries1.add(new Integer(1), new Double(500.19999999999999D));
    localXYSeries1.add(new Integer(2), new Double(694.10000000000002D));
    localXYSeries1.add(new Integer(3), new Double(-734.39999999999998D));
    localXYSeries1.add(new Integer(4), new Double(453.19999999999999D));
    localXYSeries1.add(new Integer(5), new Double(500.19999999999999D));
    localXYSeries1.add(new Integer(6), new Double(300.69999999999999D));
    localXYSeries1.add(new Integer(7), new Double(734.39999999999998D));
    localXYSeries1.add(new Integer(8), new Double(453.19999999999999D));
    XYSeries localXYSeries2 = new XYSeries("Random 2");
    localXYSeries2.add(new Integer(1), new Double(700.20000000000005D));
    localXYSeries2.add(new Integer(2), new Double(534.10000000000002D));
    localXYSeries2.add(new Integer(3), new Double(323.39999999999998D));
    localXYSeries2.add(new Integer(4), new Double(125.2D));
    localXYSeries2.add(new Integer(5), new Double(653.20000000000005D));
    localXYSeries2.add(new Integer(6), new Double(432.69999999999999D));
    localXYSeries2.add(new Integer(7), new Double(564.39999999999998D));
    localXYSeries2.add(new Integer(8), new Double(322.19999999999999D));
    XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection();
    localXYSeriesCollection.addSeries(localXYSeries1);
    localXYSeriesCollection.addSeries(localXYSeries2);
    localXYSeriesCollection.setIntervalWidth(0.0D);
    return localXYSeriesCollection;
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYAreaChart("XY Area Chart Demo", "Domain (X)", "Range (Y)", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setForegroundAlpha(0.65F);
    ValueAxis localValueAxis1 = localXYPlot.getDomainAxis();
    localValueAxis1.setTickMarkPaint(Color.black);
    localValueAxis1.setLowerMargin(0.0D);
    localValueAxis1.setUpperMargin(0.0D);
    ValueAxis localValueAxis2 = localXYPlot.getRangeAxis();
    localValueAxis2.setTickMarkPaint(Color.black);
    XYPointerAnnotation localXYPointerAnnotation = new XYPointerAnnotation("Test", 5.0D, -500.0D, 2.356194490192345D);
    localXYPointerAnnotation.setTipRadius(0.0D);
    localXYPointerAnnotation.setBaseRadius(35.0D);
    localXYPointerAnnotation.setFont(new Font("SansSerif", 0, 9));
    localXYPointerAnnotation.setPaint(Color.blue);
    localXYPointerAnnotation.setTextAnchor(TextAnchor.HALF_ASCENT_RIGHT);
    localXYPlot.addAnnotation(localXYPointerAnnotation);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    return new ChartPanel(createChart(createDataset()));
  }

  public static void main(String[] paramArrayOfString)
  {
    XYAreaChartDemo1 localXYAreaChartDemo1 = new XYAreaChartDemo1("XY Area Chart Demo");
    localXYAreaChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localXYAreaChartDemo1);
    localXYAreaChartDemo1.setVisible(true);
  }
}