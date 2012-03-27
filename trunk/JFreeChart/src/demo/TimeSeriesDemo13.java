package demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Week;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class TimeSeriesDemo13 extends ApplicationFrame
{
  public TimeSeriesDemo13(String paramString)
  {
    super(paramString);
    setContentPane(createDemoPanel());
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Weekly Data", "Date", "Value", paramXYDataset, true, true, false);
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
    TickUnits localTickUnits = new TickUnits();
    localTickUnits.add(new DateTickUnit(DateTickUnitType.DAY, 1, new SimpleDateFormat("MMM dd ''yy")));
    localTickUnits.add(new DateTickUnit(DateTickUnitType.DAY, 7, new SimpleDateFormat("MMM dd ''yy")));
    localTickUnits.add(new DateTickUnit(DateTickUnitType.MONTH, 1, new SimpleDateFormat("MMM ''yy")));
    ((DateAxis)localObject).setStandardTickUnits(localTickUnits);
    return ((JFreeChart)localJFreeChart);
  }

  private static XYDataset createDataset(int paramInt)
  {
    TimeSeries localTimeSeries = new TimeSeries("Random Data");
    Object localObject = new Week();
    double d = 100.0D;
    for (int i = 0; i < paramInt; ++i)
    {
      localTimeSeries.add((RegularTimePeriod)localObject, d);
      d *= (1.0D + (Math.random() - 0.499D) / 100.0D);
      localObject = ((RegularTimePeriod)localObject).next();
    }
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection(localTimeSeries);
    return ((XYDataset)localTimeSeriesCollection);
  }

  public static JPanel createDemoPanel()
  {
    XYDataset localXYDataset1 = createDataset(26);
    JFreeChart localJFreeChart1 = createChart(localXYDataset1);
    ChartPanel localChartPanel1 = new ChartPanel(localJFreeChart1);
    XYDataset localXYDataset2 = createDataset(1);
    JFreeChart localJFreeChart2 = createChart(localXYDataset2);
    ChartPanel localChartPanel2 = new ChartPanel(localJFreeChart2);
    JTabbedPane localJTabbedPane = new JTabbedPane();
    localJTabbedPane.add("Chart 1", localChartPanel1);
    localJTabbedPane.add("Chart 2", localChartPanel2);
    JPanel localJPanel = new JPanel(new BorderLayout());
    localJPanel.setPreferredSize(new Dimension(500, 270));
    localJPanel.add(localJTabbedPane);
    return localJPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    TimeSeriesDemo13 localTimeSeriesDemo13 = new TimeSeriesDemo13("Time Series Demo 13");
    localTimeSeriesDemo13.pack();
    RefineryUtilities.centerFrameOnScreen(localTimeSeriesDemo13);
    localTimeSeriesDemo13.setVisible(true);
  }
}