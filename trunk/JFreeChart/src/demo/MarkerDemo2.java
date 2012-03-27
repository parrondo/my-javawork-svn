package demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.PeriodAxis;
import org.jfree.chart.axis.PeriodAxisLabelInfo;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class MarkerDemo2 extends ApplicationFrame
{
  public MarkerDemo2(String paramString)
  {
    super(paramString);
    XYDataset localXYDataset = createDataset();
    JFreeChart localJFreeChart = createChart(localXYDataset);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    localChartPanel.setDomainZoomable(true);
    localChartPanel.setRangeZoomable(true);
    setContentPane(localChartPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYLineChart("Marker Demo 2", "X", "Temperature", paramXYDataset, PlotOrientation.VERTICAL, false, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    PeriodAxis localPeriodAxis = new PeriodAxis(null, new Hour(0, 30, 6, 2005), new Hour(23, 30, 6, 2005));
    PeriodAxisLabelInfo[] arrayOfPeriodAxisLabelInfo = new PeriodAxisLabelInfo[2];
    arrayOfPeriodAxisLabelInfo[0] = new PeriodAxisLabelInfo(Hour.class, new SimpleDateFormat("HH"));
    arrayOfPeriodAxisLabelInfo[1] = new PeriodAxisLabelInfo(Day.class, new SimpleDateFormat("dd-MMM"));
    localPeriodAxis.setLabelInfo(arrayOfPeriodAxisLabelInfo);
    localXYPlot.setDomainAxis(localPeriodAxis);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    localXYPlot.setDomainGridlinePaint(Color.lightGray);
    localXYPlot.setDomainGridlineStroke(new BasicStroke(1.0F));
    localXYPlot.setRangeGridlinePaint(Color.lightGray);
    localXYPlot.setRangeGridlineStroke(new BasicStroke(1.0F));
    localXYPlot.setRangeTickBandPaint(new Color(240, 240, 240));
    ValueAxis localValueAxis = localXYPlot.getRangeAxis();
    localValueAxis.setRange(0.0D, 100.0D);
    XYItemRenderer localXYItemRenderer = localXYPlot.getRenderer();
    localXYItemRenderer.setSeriesPaint(0, Color.green);
    localXYItemRenderer.setSeriesStroke(0, new BasicStroke(2.0F));
    ValueMarker localValueMarker1 = new ValueMarker(80.0D);
    localValueMarker1.setLabelOffsetType(LengthAdjustmentType.EXPAND);
    localValueMarker1.setPaint(Color.red);
    localValueMarker1.setStroke(new BasicStroke(2.0F));
    localValueMarker1.setLabel("Temperature Threshold");
    localValueMarker1.setLabelFont(new Font("SansSerif", 0, 11));
    localValueMarker1.setLabelPaint(Color.red);
    localValueMarker1.setLabelAnchor(RectangleAnchor.TOP_LEFT);
    localValueMarker1.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
    localXYPlot.addRangeMarker(localValueMarker1);
    Hour localHour1 = new Hour(18, 30, 6, 2005);
    Hour localHour2 = new Hour(20, 30, 6, 2005);
    double d1 = localHour1.getFirstMillisecond();
    double d2 = localHour2.getFirstMillisecond();
    IntervalMarker localIntervalMarker = new IntervalMarker(d1, d2);
    localIntervalMarker.setLabelOffsetType(LengthAdjustmentType.EXPAND);
    localIntervalMarker.setPaint(new Color(150, 150, 255));
    localIntervalMarker.setLabel("Automatic Cooling");
    localIntervalMarker.setLabelFont(new Font("SansSerif", 0, 11));
    localIntervalMarker.setLabelPaint(Color.blue);
    localIntervalMarker.setLabelAnchor(RectangleAnchor.TOP_LEFT);
    localIntervalMarker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
    localXYPlot.addDomainMarker(localIntervalMarker, Layer.BACKGROUND);
    ValueMarker localValueMarker2 = new ValueMarker(d1, Color.blue, new BasicStroke(2.0F));
    ValueMarker localValueMarker3 = new ValueMarker(d2, Color.blue, new BasicStroke(2.0F));
    localXYPlot.addDomainMarker(localValueMarker2, Layer.BACKGROUND);
    localXYPlot.addDomainMarker(localValueMarker3, Layer.BACKGROUND);
    return localJFreeChart;
  }

  private static XYDataset createDataset()
  {
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
    TimeSeries localTimeSeries = new TimeSeries("Temperature");
    localTimeSeries.add(new Hour(0, 30, 6, 2005), 45.299999999999997D);
    localTimeSeries.add(new Hour(1, 30, 6, 2005), 48.899999999999999D);
    localTimeSeries.add(new Hour(2, 30, 6, 2005), 52.100000000000001D);
    localTimeSeries.add(new Hour(3, 30, 6, 2005), 44.799999999999997D);
    localTimeSeries.add(new Hour(4, 30, 6, 2005), 49.899999999999999D);
    localTimeSeries.add(new Hour(5, 30, 6, 2005), 55.5D);
    localTimeSeries.add(new Hour(6, 30, 6, 2005), 58.200000000000003D);
    localTimeSeries.add(new Hour(7, 30, 6, 2005), 58.100000000000001D);
    localTimeSeries.add(new Hour(8, 30, 6, 2005), 63.700000000000003D);
    localTimeSeries.add(new Hour(9, 30, 6, 2005), 66.299999999999997D);
    localTimeSeries.add(new Hour(10, 30, 6, 2005), 69.799999999999997D);
    localTimeSeries.add(new Hour(11, 30, 6, 2005), 70.099999999999994D);
    localTimeSeries.add(new Hour(12, 30, 6, 2005), 72.400000000000006D);
    localTimeSeries.add(new Hour(13, 30, 6, 2005), 69.700000000000003D);
    localTimeSeries.add(new Hour(14, 30, 6, 2005), 68.599999999999994D);
    localTimeSeries.add(new Hour(15, 30, 6, 2005), 70.900000000000006D);
    localTimeSeries.add(new Hour(16, 30, 6, 2005), 73.400000000000006D);
    localTimeSeries.add(new Hour(17, 30, 6, 2005), 77.5D);
    localTimeSeries.add(new Hour(18, 30, 6, 2005), 82.900000000000006D);
    localTimeSeries.add(new Hour(19, 30, 6, 2005), 62.100000000000001D);
    localTimeSeries.add(new Hour(20, 30, 6, 2005), 37.299999999999997D);
    localTimeSeries.add(new Hour(21, 30, 6, 2005), 40.700000000000003D);
    localTimeSeries.add(new Hour(22, 30, 6, 2005), 44.200000000000003D);
    localTimeSeries.add(new Hour(23, 30, 6, 2005), 49.799999999999997D);
    localTimeSeriesCollection.addSeries(localTimeSeries);
    return localTimeSeriesCollection;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    MarkerDemo2 localMarkerDemo2 = new MarkerDemo2("JFreeChart: MarkerDemo2.java");
    localMarkerDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localMarkerDemo2);
    localMarkerDemo2.setVisible(true);
  }
}