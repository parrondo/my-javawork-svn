package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class ChartPanelSerializationTest extends ApplicationFrame
{
  public ChartPanelSerializationTest(String paramString)
  {
    super(paramString);
    XYDataset localXYDataset = createDataset();
    JFreeChart localJFreeChart = createChart(localXYDataset);
    ChartPanel localChartPanel1 = new ChartPanel(localJFreeChart, true);
    localChartPanel1.setMouseWheelEnabled(true);
    localChartPanel1.setPreferredSize(new Dimension(500, 270));
    localChartPanel1.setMouseZoomable(true, true);
    ChartPanel localChartPanel2 = null;
    try
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localByteArrayOutputStream);
      localObjectOutputStream.writeObject(localChartPanel1);
      localObjectOutputStream.close();
      ObjectInputStream localObjectInputStream = new ObjectInputStream(new ByteArrayInputStream(localByteArrayOutputStream.toByteArray()));
      localChartPanel2 = (ChartPanel)localObjectInputStream.readObject();
      localObjectInputStream.close();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    setContentPane(localChartPanel2);
  }

  private JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Legal & General Unit Trust Prices", "Date", "Price Per Unit", paramXYDataset, true, true, false);
    localJFreeChart.setBackgroundPaint(Color.white);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setBackgroundPaint(Color.lightGray);
    localXYPlot.setDomainGridlinePaint(Color.white);
    localXYPlot.setRangeGridlinePaint(Color.white);
    localXYPlot.setAxisOffset(new RectangleInsets(5.0D, 5.0D, 5.0D, 5.0D));
    localXYPlot.setDomainCrosshairVisible(true);
    localXYPlot.setRangeCrosshairVisible(true);
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(false);
    XYItemRenderer localXYItemRenderer = localXYPlot.getRenderer();
    if (localXYItemRenderer instanceof XYLineAndShapeRenderer)
    {
      XYLineAndShapeRenderer localObject = (XYLineAndShapeRenderer)localXYItemRenderer;
      ((XYLineAndShapeRenderer)localObject).setBaseShapesVisible(true);
      ((XYLineAndShapeRenderer)localObject).setBaseShapesFilled(true);
      ((XYLineAndShapeRenderer)localObject).setBaseItemLabelsVisible(true);
    }
    Object localObject = (DateAxis)localXYPlot.getDomainAxis();
    ((DateAxis)localObject).setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
    return ((JFreeChart)localJFreeChart);
  }

  private XYDataset createDataset()
  {
    TimeSeries localTimeSeries1 = new TimeSeries("L&G European Index Trust");
    localTimeSeries1.add(new Month(2, 2001), 181.80000000000001D);
    localTimeSeries1.add(new Month(3, 2001), 167.30000000000001D);
    localTimeSeries1.add(new Month(4, 2001), 153.80000000000001D);
    localTimeSeries1.add(new Month(5, 2001), 167.59999999999999D);
    localTimeSeries1.add(new Month(6, 2001), 158.80000000000001D);
    localTimeSeries1.add(new Month(7, 2001), 148.30000000000001D);
    localTimeSeries1.add(new Month(8, 2001), 153.90000000000001D);
    localTimeSeries1.add(new Month(9, 2001), 142.69999999999999D);
    localTimeSeries1.add(new Month(10, 2001), 123.2D);
    localTimeSeries1.add(new Month(11, 2001), 131.80000000000001D);
    localTimeSeries1.add(new Month(12, 2001), 139.59999999999999D);
    localTimeSeries1.add(new Month(1, 2002), 142.90000000000001D);
    localTimeSeries1.add(new Month(2, 2002), 138.69999999999999D);
    localTimeSeries1.add(new Month(3, 2002), 137.30000000000001D);
    localTimeSeries1.add(new Month(4, 2002), 143.90000000000001D);
    localTimeSeries1.add(new Month(5, 2002), 139.80000000000001D);
    localTimeSeries1.add(new Month(6, 2002), 137.0D);
    localTimeSeries1.add(new Month(7, 2002), 132.80000000000001D);
    TimeSeries localTimeSeries2 = new TimeSeries("L&G UK Index Trust");
    localTimeSeries2.add(new Month(2, 2001), 129.59999999999999D);
    localTimeSeries2.add(new Month(3, 2001), 123.2D);
    localTimeSeries2.add(new Month(4, 2001), 117.2D);
    localTimeSeries2.add(new Month(5, 2001), 124.09999999999999D);
    localTimeSeries2.add(new Month(6, 2001), 122.59999999999999D);
    localTimeSeries2.add(new Month(7, 2001), 119.2D);
    localTimeSeries2.add(new Month(8, 2001), 116.5D);
    localTimeSeries2.add(new Month(9, 2001), 112.7D);
    localTimeSeries2.add(new Month(10, 2001), 101.5D);
    localTimeSeries2.add(new Month(11, 2001), 106.09999999999999D);
    localTimeSeries2.add(new Month(12, 2001), 110.3D);
    localTimeSeries2.add(new Month(1, 2002), 111.7D);
    localTimeSeries2.add(new Month(2, 2002), 111.0D);
    localTimeSeries2.add(new Month(3, 2002), 109.59999999999999D);
    localTimeSeries2.add(new Month(4, 2002), 113.2D);
    localTimeSeries2.add(new Month(5, 2002), 111.59999999999999D);
    localTimeSeries2.add(new Month(6, 2002), 108.8D);
    localTimeSeries2.add(new Month(7, 2002), 101.59999999999999D);
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
    localTimeSeriesCollection.addSeries(localTimeSeries1);
    localTimeSeriesCollection.addSeries(localTimeSeries2);
    return localTimeSeriesCollection;
  }

  public static void main(String[] paramArrayOfString)
  {
    ChartPanelSerializationTest localChartPanelSerializationTest = new ChartPanelSerializationTest("Serialization Test");
    localChartPanelSerializationTest.pack();
    RefineryUtilities.centerFrameOnScreen(localChartPanelSerializationTest);
    localChartPanelSerializationTest.setVisible(true);
  }
}