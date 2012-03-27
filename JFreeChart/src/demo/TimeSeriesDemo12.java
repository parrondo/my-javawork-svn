package demo;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class TimeSeriesDemo12 extends ApplicationFrame
{
  public TimeSeriesDemo12(String paramString)
  {
    super(paramString);
    XYDataset localXYDataset = createDataset();
    JFreeChart localJFreeChart = createChart(localXYDataset);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    localChartPanel.setMouseZoomable(true);
    setContentPane(localChartPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Sample Chart", "Date", "Value", paramXYDataset, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainCrosshairVisible(true);
    localXYPlot.setRangeCrosshairVisible(false);
    XYItemRenderer localXYItemRenderer = localXYPlot.getRenderer();
    if (localXYItemRenderer instanceof XYLineAndShapeRenderer)
    {
      XYLineAndShapeRenderer localObject = (XYLineAndShapeRenderer)localXYItemRenderer;
      ((XYLineAndShapeRenderer)localObject).setBaseShapesVisible(true);
      ((XYLineAndShapeRenderer)localObject).setBaseShapesFilled(true);
      localXYItemRenderer.setSeriesStroke(0, new BasicStroke(2.0F));
      localXYItemRenderer.setSeriesStroke(1, new BasicStroke(2.0F));
    }
    Object localObject = (DateAxis)localXYPlot.getDomainAxis();
    ((DateAxis)localObject).setDateFormatOverride(new SimpleDateFormat("hh:mma"));
    return ((JFreeChart)localJFreeChart);
  }

  private static XYDataset createDataset()
  {
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
    TimeSeries localTimeSeries1 = new TimeSeries("Series 1");
    localTimeSeries1.add(new Minute(0, 0, 7, 12, 2003), 1.2D);
    localTimeSeries1.add(new Minute(30, 12, 7, 12, 2003), 3.0D);
    localTimeSeries1.add(new Minute(15, 14, 7, 12, 2003), 8.0D);
    TimeSeries localTimeSeries2 = new TimeSeries("Series 2");
    localTimeSeries2.add(new Minute(0, 3, 7, 12, 2003), 0.0D);
    localTimeSeries2.add(new Minute(30, 9, 7, 12, 2003), 0.0D);
    localTimeSeries2.add(new Minute(15, 10, 7, 12, 2003), 0.0D);
    localTimeSeriesCollection.addSeries(localTimeSeries1);
    localTimeSeriesCollection.addSeries(localTimeSeries2);
    return localTimeSeriesCollection;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    TimeSeriesDemo12 localTimeSeriesDemo12 = new TimeSeriesDemo12("Time Series Demo 12");
    localTimeSeriesDemo12.pack();
    RefineryUtilities.centerFrameOnScreen(localTimeSeriesDemo12);
    localTimeSeriesDemo12.setVisible(true);
  }
}