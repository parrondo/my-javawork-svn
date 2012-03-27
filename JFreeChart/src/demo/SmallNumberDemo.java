package demo;

import java.awt.Dimension;
import java.io.PrintStream;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.StandardTickUnitSource;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class SmallNumberDemo extends ApplicationFrame
{
  public SmallNumberDemo(String paramString)
  {
    super(paramString);
    XYSeries localXYSeries = new XYSeries("Small Numbers");
    localXYSeries.add(1.E-005D, 1.0E-016D);
    localXYSeries.add(5.E-005D, 2.0E-012D);
    XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection(localXYSeries);
    JFreeChart localJFreeChart = ChartFactory.createXYLineChart("Small Number Demo", "X", "Y", localXYSeriesCollection, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    NumberAxis localNumberAxis1 = (NumberAxis)localXYPlot.getDomainAxis();
    localNumberAxis1.setStandardTickUnits(new StandardTickUnitSource());
    NumberAxis localNumberAxis2 = (NumberAxis)localXYPlot.getRangeAxis();
    localNumberAxis2.setStandardTickUnits(new StandardTickUnitSource());
    localNumberAxis2.setAutoRangeMinimumSize(4.9E-324D);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localChartPanel);
  }

  public static void main(String[] paramArrayOfString)
  {
    System.out.println("Min Double: 4.9E-324");
    SmallNumberDemo localSmallNumberDemo = new SmallNumberDemo("Small Number Demo");
    localSmallNumberDemo.pack();
    RefineryUtilities.centerFrameOnScreen(localSmallNumberDemo);
    localSmallNumberDemo.setVisible(true);
  }
}