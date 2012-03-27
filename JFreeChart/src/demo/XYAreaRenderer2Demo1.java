package demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer2;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class XYAreaRenderer2Demo1 extends ApplicationFrame
{
  public XYAreaRenderer2Demo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
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
    JFreeChart localJFreeChart = ChartFactory.createXYAreaChart("XYAreaRenderer2Demo1", "Domain (X)", "Range (Y)", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setRenderer(new XYAreaRenderer2());
    localXYPlot.setForegroundAlpha(0.65F);
    ValueAxis localValueAxis1 = localXYPlot.getDomainAxis();
    localValueAxis1.setTickMarkPaint(Color.black);
    localValueAxis1.setLowerMargin(0.0D);
    localValueAxis1.setUpperMargin(0.0D);
    ValueAxis localValueAxis2 = localXYPlot.getRangeAxis();
    localValueAxis2.setTickMarkPaint(Color.black);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    return new ChartPanel(createChart(createDataset()));
  }

  public static void main(String[] paramArrayOfString)
  {
    XYAreaRenderer2Demo1 localXYAreaRenderer2Demo1 = new XYAreaRenderer2Demo1("XYAreaRenderer2Demo1");
    localXYAreaRenderer2Demo1.pack();
    RefineryUtilities.centerFrameOnScreen(localXYAreaRenderer2Demo1);
    localXYAreaRenderer2Demo1.setVisible(true);
  }
}