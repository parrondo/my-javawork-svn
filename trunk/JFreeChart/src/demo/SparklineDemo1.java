package demo;

import java.io.File;
import java.io.IOException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

public class SparklineDemo1
{
  public static void main(String[] paramArrayOfString)
  {
    XYSeries localXYSeries = new XYSeries("Series 1");
    localXYSeries.add(1.0D, 1.0D);
    localXYSeries.add(2.0D, 3.0D);
    localXYSeries.add(3.0D, 2.0D);
    localXYSeries.add(4.0D, 4.0D);
    XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection();
    localXYSeriesCollection.addSeries(localXYSeries);
    JFreeChart localJFreeChart = ChartFactory.createXYLineChart(null, "X", "Y", localXYSeriesCollection, PlotOrientation.VERTICAL, false, false, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setInsets(RectangleInsets.ZERO_INSETS);
    localXYPlot.setDomainGridlinesVisible(false);
    localXYPlot.setRangeGridlinesVisible(false);
    localXYPlot.setOutlinePaint(null);
    localXYPlot.getDomainAxis().setVisible(false);
    localXYPlot.getRangeAxis().setVisible(false);
    try
    {
      ChartUtilities.saveChartAsPNG(new File("Sparky.png"), localJFreeChart, 100, 20);
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }
}