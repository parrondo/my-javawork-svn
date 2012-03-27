package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.date.SerialDate;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class TimeSeriesDemo11 extends ApplicationFrame
{
  public TimeSeriesDemo11(String paramString)
  {
    super(paramString);
    setContentPane(createDemoPanel());
  }

  private static JFreeChart createChart(String paramString, XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart(paramString, "Date", "Price", paramXYDataset, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setOrientation(PlotOrientation.VERTICAL);
    XYItemRenderer localXYItemRenderer = localXYPlot.getRenderer();
    localXYItemRenderer.setSeriesPaint(0, Color.blue);
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
      localRegularTimePeriod = localRegularTimePeriod.previous();
      d *= (1.0D + (Math.random() - 0.495D) / 10.0D);
    }
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
    localTimeSeriesCollection.addSeries(localTimeSeries);
    return localTimeSeriesCollection;
  }

  public static JPanel createDemoPanel()
  {
    DemoPanel localDemoPanel = new DemoPanel(new GridLayout(2, 2));
    localDemoPanel.setPreferredSize(new Dimension(800, 600));
    Day localDay1 = new Day();
    XYDataset localXYDataset = createDataset("Series 1", 100.0D, localDay1, 365);
    JFreeChart localJFreeChart1 = createChart("Chart 1 : 1 Year", localXYDataset);
    ChartPanel localChartPanel1 = new ChartPanel(localJFreeChart1);
    localDemoPanel.add(localChartPanel1);
    JFreeChart localJFreeChart2 = createChart("Chart 2 : 6 Months", localXYDataset);
    SerialDate localSerialDate1 = localDay1.getSerialDate();
    SerialDate localSerialDate2 = SerialDate.addMonths(-6, localSerialDate1);
    Day localDay2 = new Day(localSerialDate2);
    XYPlot localXYPlot1 = (XYPlot)localJFreeChart2.getPlot();
    DateAxis localDateAxis1 = (DateAxis)localXYPlot1.getDomainAxis();
    localDateAxis1.setRange(localDay2.getStart(), localDay1.getEnd());
    ChartPanel localChartPanel2 = new ChartPanel(localJFreeChart2);
    localDemoPanel.add(localChartPanel2);
    JFreeChart localJFreeChart3 = createChart("Chart 3 : 3 Months", localXYDataset);
    SerialDate localSerialDate3 = SerialDate.addMonths(-3, localSerialDate1);
    Day localDay3 = new Day(localSerialDate3);
    XYPlot localXYPlot2 = (XYPlot)localJFreeChart3.getPlot();
    DateAxis localDateAxis2 = (DateAxis)localXYPlot2.getDomainAxis();
    localDateAxis2.setRange(localDay3.getStart(), localDay1.getEnd());
    ChartPanel localChartPanel3 = new ChartPanel(localJFreeChart3);
    localDemoPanel.add(localChartPanel3);
    JFreeChart localJFreeChart4 = createChart("Chart 4 : 1 Month", localXYDataset);
    SerialDate localSerialDate4 = SerialDate.addMonths(-1, localSerialDate1);
    Day localDay4 = new Day(localSerialDate4);
    XYPlot localXYPlot3 = (XYPlot)localJFreeChart4.getPlot();
    DateAxis localDateAxis3 = (DateAxis)localXYPlot3.getDomainAxis();
    localDateAxis3.setRange(localDay4.getStart(), localDay1.getEnd());
    ChartPanel localChartPanel4 = new ChartPanel(localJFreeChart4);
    localDemoPanel.add(localChartPanel4);
    localDemoPanel.addChart(localJFreeChart1);
    localDemoPanel.addChart(localJFreeChart2);
    localDemoPanel.addChart(localJFreeChart3);
    localDemoPanel.addChart(localJFreeChart4);
    return localDemoPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    TimeSeriesDemo11 localTimeSeriesDemo11 = new TimeSeriesDemo11("Time Series Demo 11");
    localTimeSeriesDemo11.pack();
    RefineryUtilities.centerFrameOnScreen(localTimeSeriesDemo11);
    localTimeSeriesDemo11.setVisible(true);
  }
}