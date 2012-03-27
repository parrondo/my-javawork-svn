package demo;

import java.awt.Dimension;
import java.text.DecimalFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.util.RelativeDateFormat;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class RelativeDateFormatDemo1 extends ApplicationFrame
{
  public RelativeDateFormatDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Exercise Chart", "Elapsed Time", "Beats Per Minute", paramXYDataset, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainCrosshairVisible(true);
    localXYPlot.setRangeCrosshairVisible(true);
    XYItemRenderer localXYItemRenderer = localXYPlot.getRenderer();
    if (localXYItemRenderer instanceof XYLineAndShapeRenderer)
    {
      XYLineAndShapeRenderer localObject = (XYLineAndShapeRenderer)localXYItemRenderer;
      ((XYLineAndShapeRenderer)localObject).setBaseShapesVisible(true);
      ((XYLineAndShapeRenderer)localObject).setBaseShapesFilled(true);
    }
    Object localObject = (DateAxis)localXYPlot.getDomainAxis();
    Minute localMinute = new Minute(0, 9, 1, 10, 2006);
    RelativeDateFormat localRelativeDateFormat = new RelativeDateFormat(localMinute.getFirstMillisecond());
    localRelativeDateFormat.setSecondFormatter(new DecimalFormat("00"));
    ((DateAxis)localObject).setDateFormatOverride(localRelativeDateFormat);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return ((JFreeChart)localJFreeChart);
  }

  private static XYDataset createDataset()
  {
    TimeSeries localTimeSeries = new TimeSeries("Heart Rate");
    localTimeSeries.add(new Second(45, 6, 9, 1, 10, 2006), 143.0D);
    localTimeSeries.add(new Second(33, 8, 9, 1, 10, 2006), 167.0D);
    localTimeSeries.add(new Second(10, 10, 9, 1, 10, 2006), 189.0D);
    localTimeSeries.add(new Second(19, 12, 9, 1, 10, 2006), 156.0D);
    localTimeSeries.add(new Second(5, 15, 9, 1, 10, 2006), 176.0D);
    localTimeSeries.add(new Second(12, 16, 9, 1, 10, 2006), 183.0D);
    localTimeSeries.add(new Second(6, 18, 9, 1, 10, 2006), 138.0D);
    localTimeSeries.add(new Second(11, 20, 9, 1, 10, 2006), 102.0D);
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
    RelativeDateFormatDemo1 localRelativeDateFormatDemo1 = new RelativeDateFormatDemo1("JFreeChart: RelativeDateFormatDemo1.java");
    localRelativeDateFormatDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localRelativeDateFormatDemo1);
    localRelativeDateFormatDemo1.setVisible(true);
  }
}