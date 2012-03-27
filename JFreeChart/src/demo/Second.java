package demo;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Second
{
  public static void main(String[] paramArrayOfString)
  {
    XYSeries localXYSeries1 = new XYSeries("Advisory Range");
    localXYSeries1.add(new Integer(1200), new Integer(1));
    localXYSeries1.add(new Integer(1500), new Integer(1));
    XYSeries localXYSeries2 = new XYSeries("Normal Range");
    localXYSeries2.add(new Integer(2000), new Integer(4));
    localXYSeries2.add(new Integer(2300), new Integer(4));
    XYSeries localXYSeries3 = new XYSeries("Recommended");
    localXYSeries3.add(new Integer(2100), new Integer(2));
    XYSeries localXYSeries4 = new XYSeries("Current");
    localXYSeries4.add(new Integer(2400), new Integer(3));
    XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection();
    localXYSeriesCollection.addSeries(localXYSeries1);
    localXYSeriesCollection.addSeries(localXYSeries2);
    localXYSeriesCollection.addSeries(localXYSeries3);
    localXYSeriesCollection.addSeries(localXYSeries4);
    JFreeChart localJFreeChart = ChartFactory.createXYLineChart("My Chart", "Calories", "Y", localXYSeriesCollection, PlotOrientation.VERTICAL, true, true, false);
    StandardXYItemRenderer localStandardXYItemRenderer = new StandardXYItemRenderer(3, null);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setRenderer(localStandardXYItemRenderer);
    ValueAxis localValueAxis = localXYPlot.getRangeAxis();
    localValueAxis.setTickLabelsVisible(false);
    localValueAxis.setRange(0.0D, 5.0D);
    ChartFrame localChartFrame = new ChartFrame("Test", localJFreeChart);
    localChartFrame.pack();
    localChartFrame.setVisible(true);
  }
}