package demo;

import java.awt.Color;
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
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class TimeSeriesDemo14 extends ApplicationFrame
{
  public TimeSeriesDemo14(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Bug Report Submissions for Java", "Date", "Evaluation ID", paramXYDataset, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainCrosshairVisible(true);
    localXYPlot.setRangeCrosshairVisible(true);
    XYItemRenderer localXYItemRenderer = localXYPlot.getRenderer();
    if (localXYItemRenderer instanceof XYLineAndShapeRenderer)
    {
      XYLineAndShapeRenderer localObject = (XYLineAndShapeRenderer)localXYItemRenderer;
      ((XYLineAndShapeRenderer)localObject).setBaseShapesVisible(true);
      ((XYLineAndShapeRenderer)localObject).setBaseShapesFilled(true);
      ((XYLineAndShapeRenderer)localObject).setUseFillPaint(true);
      ((XYLineAndShapeRenderer)localObject).setBaseFillPaint(Color.white);
    }
    Object localObject = (DateAxis)localXYPlot.getDomainAxis();
    ((DateAxis)localObject).setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
    return ((JFreeChart)localJFreeChart);
  }

  private static XYDataset createDataset()
  {
    TimeSeries localTimeSeries = new TimeSeries("Bugs");
    localTimeSeries.add(new Day(27, 6, 2005), 478474.0D);
    localTimeSeries.add(new Day(24, 1, 2006), 633804.0D);
    localTimeSeries.add(new Day(28, 4, 2006), 694096.0D);
    localTimeSeries.add(new Day(12, 5, 2006), 704680.0D);
    localTimeSeries.add(new Day(16, 5, 2006), 709599.0D);
    localTimeSeries.add(new Day(21, 6, 2006), 734754.0D);
    localTimeSeries.add(new Day(27, 7, 2006), 760008.0D);
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
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
    TimeSeriesDemo14 localTimeSeriesDemo14 = new TimeSeriesDemo14("Time Series Demo 14");
    localTimeSeriesDemo14.pack();
    RefineryUtilities.centerFrameOnScreen(localTimeSeriesDemo14);
    localTimeSeriesDemo14.setVisible(true);
  }
}