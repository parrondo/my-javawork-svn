package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class MultipleAxisDemo4 extends ApplicationFrame
{
  public MultipleAxisDemo4(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(600, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart()
  {
    XYDataset localXYDataset1 = createDataset("March 2007", 100.0D, new Day(1, 3, 2007), 31);
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Multiple Axis Demo 4", "Date", "Value", localXYDataset1, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setOrientation(PlotOrientation.VERTICAL);
    DateAxis localDateAxis1 = (DateAxis)localXYPlot.getDomainAxis();
    localDateAxis1.setDateFormatOverride(new SimpleDateFormat("d-MMM-yyyy"));
    XYItemRenderer localXYItemRenderer = localXYPlot.getRenderer();
    localXYItemRenderer.setSeriesPaint(0, Color.red);
    NumberAxis localNumberAxis1 = (NumberAxis)localXYPlot.getRangeAxis();
    localNumberAxis1.setTickLabelPaint(Color.red);
    DateAxis localDateAxis2 = new DateAxis("Date");
    localDateAxis2.setDateFormatOverride(new SimpleDateFormat("d-MMM-yyyy"));
    localXYPlot.setDomainAxis(1, localDateAxis2);
    localXYPlot.setDomainAxisLocation(1, AxisLocation.TOP_OR_LEFT);
    NumberAxis localNumberAxis2 = new NumberAxis("Value");
    localNumberAxis2.setAutoRangeIncludesZero(false);
    localNumberAxis2.setTickLabelPaint(Color.blue);
    localXYPlot.setRangeAxis(1, localNumberAxis2);
    localXYPlot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
    XYDataset localXYDataset2 = createDataset("July 2007", 1000.0D, new Day(1, 7, 2007), 31);
    localXYPlot.setDataset(1, localXYDataset2);
    localXYPlot.mapDatasetToDomainAxis(1, 1);
    localXYPlot.mapDatasetToRangeAxis(1, 1);
    XYLineAndShapeRenderer localXYLineAndShapeRenderer = new XYLineAndShapeRenderer(true, false);
    localXYLineAndShapeRenderer.setSeriesPaint(0, Color.blue);
    localXYPlot.setRenderer(1, localXYLineAndShapeRenderer);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  private static XYDataset createDataset(String paramString, double paramDouble, RegularTimePeriod paramRegularTimePeriod, int paramInt)
  {
    TimeSeries localTimeSeries = new TimeSeries(paramString);
    RegularTimePeriod localRegularTimePeriod = paramRegularTimePeriod;
    double d = paramDouble;
    for (int i = 0; i < paramInt; ++i)
    {
      localTimeSeries.add(localRegularTimePeriod, d);
      localRegularTimePeriod = localRegularTimePeriod.next();
      d *= (1.0D + (Math.random() - 0.495D) / 10.0D);
    }
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
    localTimeSeriesCollection.addSeries(localTimeSeries);
    return localTimeSeriesCollection;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart();
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    MultipleAxisDemo4 localMultipleAxisDemo4 = new MultipleAxisDemo4("JFreeChart: MultipleAxisDemo4.java");
    localMultipleAxisDemo4.pack();
    RefineryUtilities.centerFrameOnScreen(localMultipleAxisDemo4);
    localMultipleAxisDemo4.setVisible(true);
  }
}