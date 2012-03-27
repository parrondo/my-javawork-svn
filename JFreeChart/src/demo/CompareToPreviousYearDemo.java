package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class CompareToPreviousYearDemo extends ApplicationFrame
{
  public CompareToPreviousYearDemo(String paramString)
  {
    super(paramString);
    JFreeChart localJFreeChart = createChart();
    ChartPanel localChartPanel = (ChartPanel)createDemoPanel();
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    localChartPanel.setMouseZoomable(true, true);
    setContentPane(localChartPanel);
  }

  private static JFreeChart createChart()
  {
    XYDataset localXYDataset1 = createDataset2006();
    XYDataset localXYDataset2 = createDataset2007();
    DateAxis localDateAxis1 = new DateAxis("Date");
    Month localMonth1 = new Month(1, 2007);
    Month localMonth2 = new Month(12, 2007);
    localDateAxis1.setRange(localMonth1.getFirstMillisecond(), localMonth2.getLastMillisecond());
    localDateAxis1.setDateFormatOverride(new SimpleDateFormat("MMM"));
    localDateAxis1.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
    XYLineAndShapeRenderer localXYLineAndShapeRenderer1 = new XYLineAndShapeRenderer();
    localXYLineAndShapeRenderer1.setUseFillPaint(true);
    localXYLineAndShapeRenderer1.setBaseFillPaint(Color.white);
    localXYLineAndShapeRenderer1.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{1}: {2}", new SimpleDateFormat("MMM-yyyy"), new DecimalFormat("0.00")));
    XYPlot localXYPlot = new XYPlot(localXYDataset2, localDateAxis1, new NumberAxis("Sales"), localXYLineAndShapeRenderer1);
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    DateAxis localDateAxis2 = new DateAxis();
    localDateAxis2.setVisible(false);
    localXYPlot.setDomainAxis(1, localDateAxis2);
    localXYPlot.setDataset(1, localXYDataset1);
    localXYPlot.mapDatasetToDomainAxis(1, 1);
    XYLineAndShapeRenderer localXYLineAndShapeRenderer2 = new XYLineAndShapeRenderer();
    localXYLineAndShapeRenderer2.setSeriesPaint(0, Color.blue);
    localXYLineAndShapeRenderer2.setUseFillPaint(true);
    localXYLineAndShapeRenderer2.setBaseFillPaint(Color.white);
    localXYLineAndShapeRenderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{1}: {2}", new SimpleDateFormat("MMM-yyyy"), new DecimalFormat("0.00")));
    localXYPlot.setRenderer(1, localXYLineAndShapeRenderer2);
    JFreeChart localJFreeChart = new JFreeChart("Sales Comparison Chart", localXYPlot);
    localXYPlot.setDomainCrosshairVisible(true);
    localXYPlot.setRangeCrosshairVisible(true);
    DateAxis localDateAxis3 = (DateAxis)localXYPlot.getDomainAxis();
    localDateAxis3.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  private static XYDataset createDataset2006()
  {
    TimeSeries localTimeSeries = new TimeSeries("Sales 2006");
    localTimeSeries.add(new Month(1, 2006), 100.0D);
    localTimeSeries.add(new Month(2, 2006), 102.3D);
    localTimeSeries.add(new Month(3, 2006), 105.7D);
    localTimeSeries.add(new Month(4, 2006), 104.2D);
    localTimeSeries.add(new Month(5, 2006), 114.7D);
    localTimeSeries.add(new Month(6, 2006), 121.7D);
    localTimeSeries.add(new Month(7, 2006), 155.59999999999999D);
    localTimeSeries.add(new Month(8, 2006), 143.19999999999999D);
    localTimeSeries.add(new Month(9, 2006), 131.90000000000001D);
    localTimeSeries.add(new Month(10, 2006), 120.0D);
    localTimeSeries.add(new Month(11, 2006), 109.90000000000001D);
    localTimeSeries.add(new Month(12, 2006), 99.599999999999994D);
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
    localTimeSeriesCollection.addSeries(localTimeSeries);
    localTimeSeriesCollection.setXPosition(TimePeriodAnchor.MIDDLE);
    return localTimeSeriesCollection;
  }

  private static XYDataset createDataset2007()
  {
    TimeSeries localTimeSeries = new TimeSeries("Sales 2007");
    localTimeSeries.add(new Month(1, 2007), 163.90000000000001D);
    localTimeSeries.add(new Month(2, 2007), 163.80000000000001D);
    localTimeSeries.add(new Month(3, 2007), 162.0D);
    localTimeSeries.add(new Month(4, 2007), 167.09999999999999D);
    localTimeSeries.add(new Month(5, 2007), 170.0D);
    localTimeSeries.add(new Month(6, 2007), 175.69999999999999D);
    localTimeSeries.add(new Month(7, 2007), 171.90000000000001D);
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
    localTimeSeriesCollection.addSeries(localTimeSeries);
    localTimeSeriesCollection.setXPosition(TimePeriodAnchor.MIDDLE);
    return localTimeSeriesCollection;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart();
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setMouseWheelEnabled(true);
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    CompareToPreviousYearDemo localCompareToPreviousYearDemo = new CompareToPreviousYearDemo("JFreeChart: CompareToPreviousYearDemo.java");
    localCompareToPreviousYearDemo.pack();
    RefineryUtilities.centerFrameOnScreen(localCompareToPreviousYearDemo);
    localCompareToPreviousYearDemo.setVisible(true);
  }
}