package demo;

import java.awt.Dimension;
import java.io.PrintStream;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.SamplingXYLineRenderer;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class TimeSeriesDemo5 extends ApplicationFrame
{
  public TimeSeriesDemo5(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static XYDataset createDataset()
  {
    TimeSeries localTimeSeries = new TimeSeries("Random Data");
    Day localDay = new Day(1, 1, 1990);
    double d = 100.0D;
    for (int i = 0; i < 100000; ++i)
      try
      {
        d = d + Math.random() - 0.5D;
        localTimeSeries.add(localDay, new Double(d));
        localDay = (Day)localDay.next();
      }
      catch (SeriesException localSeriesException)
      {
        System.err.println("Error adding to series");
      }
    return new TimeSeriesCollection(localTimeSeries);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Test", "Day", "Value", paramXYDataset, false, false, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    SamplingXYLineRenderer localSamplingXYLineRenderer = new SamplingXYLineRenderer();
    localXYPlot.setRenderer(localSamplingXYLineRenderer);
    return localJFreeChart;
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
    String str = "²âÊÔ";
    TimeSeriesDemo5 localTimeSeriesDemo5 = new TimeSeriesDemo5(str);
    localTimeSeriesDemo5.pack();
    RefineryUtilities.positionFrameRandomly(localTimeSeriesDemo5);
    localTimeSeriesDemo5.setVisible(true);
  }
}