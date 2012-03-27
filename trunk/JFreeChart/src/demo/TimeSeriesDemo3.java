package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.labels.StandardXYSeriesLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class TimeSeriesDemo3 extends ApplicationFrame
{
  public TimeSeriesDemo3(String paramString)
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
    TimeSeries localTimeSeries1 = new TimeSeries("Series 1");
    localTimeSeries1.add(new Month(1, 2002), 500.19999999999999D);
    localTimeSeries1.add(new Month(2, 2002), 694.10000000000002D);
    localTimeSeries1.add(new Month(3, 2002), 734.39999999999998D);
    localTimeSeries1.add(new Month(4, 2002), 453.19999999999999D);
    localTimeSeries1.add(new Month(5, 2002), 500.19999999999999D);
    localTimeSeries1.add(new Month(6, 2002), 345.60000000000002D);
    localTimeSeries1.add(new Month(7, 2002), 500.19999999999999D);
    localTimeSeries1.add(new Month(8, 2002), 694.10000000000002D);
    localTimeSeries1.add(new Month(9, 2002), 734.39999999999998D);
    localTimeSeries1.add(new Month(10, 2002), 453.19999999999999D);
    localTimeSeries1.add(new Month(11, 2002), 500.19999999999999D);
    localTimeSeries1.add(new Month(12, 2002), 345.60000000000002D);
    TimeSeries localTimeSeries2 = new TimeSeries("Series 2");
    localTimeSeries2.add(new Month(1, 2002), 234.09999999999999D);
    localTimeSeries2.add(new Month(2, 2002), 623.70000000000005D);
    localTimeSeries2.add(new Month(3, 2002), 642.5D);
    localTimeSeries2.add(new Month(4, 2002), 651.39999999999998D);
    localTimeSeries2.add(new Month(5, 2002), 643.5D);
    localTimeSeries2.add(new Month(6, 2002), 785.60000000000002D);
    localTimeSeries2.add(new Month(7, 2002), 234.09999999999999D);
    localTimeSeries2.add(new Month(8, 2002), 623.70000000000005D);
    localTimeSeries2.add(new Month(9, 2002), 642.5D);
    localTimeSeries2.add(new Month(10, 2002), 651.39999999999998D);
    localTimeSeries2.add(new Month(11, 2002), 643.5D);
    localTimeSeries2.add(new Month(12, 2002), 785.60000000000002D);
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
    localTimeSeriesCollection.addSeries(localTimeSeries1);
    localTimeSeriesCollection.addSeries(localTimeSeries2);
    return localTimeSeriesCollection;
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Time Series Demo 3", "Time", "Value", paramXYDataset, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    DateAxis localDateAxis = (DateAxis)localXYPlot.getDomainAxis();
    localDateAxis.setTickUnit(new DateTickUnit(DateTickUnitType.MONTH, 1, new SimpleDateFormat("MMM-yyyy")));
    localDateAxis.setVerticalTickLabels(true);
    XYLineAndShapeRenderer localXYLineAndShapeRenderer = (XYLineAndShapeRenderer)localXYPlot.getRenderer();
    localXYLineAndShapeRenderer.setBaseShapesVisible(true);
    localXYLineAndShapeRenderer.setSeriesFillPaint(0, Color.red);
    localXYLineAndShapeRenderer.setSeriesFillPaint(1, Color.white);
    localXYLineAndShapeRenderer.setUseFillPaint(true);
    localXYLineAndShapeRenderer.setLegendItemToolTipGenerator(new StandardXYSeriesLabelGenerator("Tooltip {0}"));
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    TimeSeriesDemo3 localTimeSeriesDemo3 = new TimeSeriesDemo3("Time Series Demo 3");
    localTimeSeriesDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localTimeSeriesDemo3);
    localTimeSeriesDemo3.setVisible(true);
  }
}