package demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYDrawableAnnotation;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class MarkerDemo1 extends ApplicationFrame
{
  public MarkerDemo1(String paramString)
  {
    super(paramString);
    ChartPanel localChartPanel = (ChartPanel)createDemoPanel();
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    localChartPanel.setDomainZoomable(true);
    localChartPanel.setRangeZoomable(true);
    setContentPane(localChartPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createScatterPlot("Marker Demo 1", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
    LegendTitle localLegendTitle = (LegendTitle)localJFreeChart.getSubtitle(0);
    localLegendTitle.setPosition(RectangleEdge.RIGHT);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    localXYPlot.getRenderer().setBaseToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
    DateAxis localDateAxis = new DateAxis("Time");
    localDateAxis.setUpperMargin(0.5D);
    localXYPlot.setDomainAxis(localDateAxis);
    ValueAxis localValueAxis = localXYPlot.getRangeAxis();
    localValueAxis.setUpperMargin(0.3D);
    localValueAxis.setLowerMargin(0.5D);
    ValueMarker localValueMarker1 = new ValueMarker(200.0D);
    localValueMarker1.setLabelOffsetType(LengthAdjustmentType.EXPAND);
    localValueMarker1.setPaint(Color.green);
    localValueMarker1.setLabel("Bid Start Price");
    localValueMarker1.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
    localValueMarker1.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
    localXYPlot.addRangeMarker(localValueMarker1);
    ValueMarker localValueMarker2 = new ValueMarker(175.0D);
    localValueMarker2.setLabelOffsetType(LengthAdjustmentType.EXPAND);
    localValueMarker2.setPaint(Color.red);
    localValueMarker2.setLabel("Target Price");
    localValueMarker2.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
    localValueMarker2.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
    localXYPlot.addRangeMarker(localValueMarker2);
    Hour localHour1 = new Hour(2, new Day(22, 5, 2003));
    double d = localHour1.getFirstMillisecond();
    ValueMarker localValueMarker3 = new ValueMarker(d);
    localValueMarker3.setPaint(Color.orange);
    localValueMarker3.setLabel("Original Close (02:00)");
    localValueMarker3.setLabelAnchor(RectangleAnchor.TOP_LEFT);
    localValueMarker3.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
    localXYPlot.addDomainMarker(localValueMarker3);
    Minute localMinute1 = new Minute(15, localHour1);
    d = localMinute1.getFirstMillisecond();
    ValueMarker localValueMarker4 = new ValueMarker(d);
    localValueMarker4.setPaint(Color.red);
    localValueMarker4.setLabel("Close Date (02:15)");
    localValueMarker4.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
    localValueMarker4.setLabelTextAnchor(TextAnchor.TOP_LEFT);
    localXYPlot.addDomainMarker(localValueMarker4);
    Hour localHour2 = new Hour(2, new Day(22, 5, 2003));
    Minute localMinute2 = new Minute(10, localHour2);
    d = localMinute2.getFirstMillisecond();
    CircleDrawer localCircleDrawer = new CircleDrawer(Color.red, new BasicStroke(1.0F), null);
    XYDrawableAnnotation localXYDrawableAnnotation = new XYDrawableAnnotation(d, 163.0D, 11.0D, 11.0D, localCircleDrawer);
    localXYPlot.addAnnotation(localXYDrawableAnnotation);
    XYPointerAnnotation localXYPointerAnnotation = new XYPointerAnnotation("Best Bid", d, 163.0D, 2.356194490192345D);
    localXYPointerAnnotation.setBaseRadius(35.0D);
    localXYPointerAnnotation.setTipRadius(10.0D);
    localXYPointerAnnotation.setFont(new Font("SansSerif", 0, 9));
    localXYPointerAnnotation.setPaint(Color.blue);
    localXYPointerAnnotation.setTextAnchor(TextAnchor.HALF_ASCENT_RIGHT);
    localXYPlot.addAnnotation(localXYPointerAnnotation);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  private static XYDataset createDataset()
  {
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
    localTimeSeriesCollection.addSeries(createSupplier1Bids());
    localTimeSeriesCollection.addSeries(createSupplier2Bids());
    return localTimeSeriesCollection;
  }

  private static TimeSeries createSupplier1Bids()
  {
    Hour localHour = new Hour(1, new Day(22, 5, 2003));
    TimeSeries localTimeSeries = new TimeSeries("Supplier 1");
    localTimeSeries.add(new Minute(13, localHour), 200.0D);
    localTimeSeries.add(new Minute(14, localHour), 195.0D);
    localTimeSeries.add(new Minute(45, localHour), 190.0D);
    localTimeSeries.add(new Minute(46, localHour), 188.0D);
    localTimeSeries.add(new Minute(47, localHour), 185.0D);
    localTimeSeries.add(new Minute(52, localHour), 180.0D);
    return localTimeSeries;
  }

  private static TimeSeries createSupplier2Bids()
  {
    Hour localHour1 = new Hour(1, new Day(22, 5, 2003));
    Hour localHour2 = (Hour)localHour1.next();
    TimeSeries localTimeSeries = new TimeSeries("Supplier 2");
    localTimeSeries.add(new Minute(25, localHour1), 185.0D);
    localTimeSeries.add(new Minute(0, localHour2), 175.0D);
    localTimeSeries.add(new Minute(5, localHour2), 170.0D);
    localTimeSeries.add(new Minute(6, localHour2), 168.0D);
    localTimeSeries.add(new Minute(9, localHour2), 165.0D);
    localTimeSeries.add(new Minute(10, localHour2), 163.0D);
    return localTimeSeries;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setMouseWheelEnabled(true);
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    MarkerDemo1 localMarkerDemo1 = new MarkerDemo1("JFreeChart: MarkerDemo1.java");
    localMarkerDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localMarkerDemo1);
    localMarkerDemo1.setVisible(true);
  }
}