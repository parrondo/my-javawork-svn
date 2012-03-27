package demo;

import java.awt.Dimension;
import java.text.DateFormat;
import java.util.Date;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class TimePeriodValuesDemo3 extends ApplicationFrame
{
  public TimePeriodValuesDemo3(String paramString)
  {
    super(paramString);
    XYDataset localXYDataset = createDataset();
    XYBarRenderer localXYBarRenderer = new XYBarRenderer();
    localXYBarRenderer.setDrawBarOutline(false);
    DateAxis localDateAxis = new DateAxis("Date");
    NumberAxis localNumberAxis = new NumberAxis("Value");
    XYPlot localXYPlot = new XYPlot(localXYDataset, localDateAxis, localNumberAxis, localXYBarRenderer);
    JFreeChart localJFreeChart = new JFreeChart("Time Period Values Demo 3", localXYPlot);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    localChartPanel.setMouseZoomable(true);
    setContentPane(localChartPanel);
  }

  public XYDataset createDataset()
  {
    TimePeriodValues localTimePeriodValues = new TimePeriodValues("Series 1");
    DateFormat localDateFormat = DateFormat.getInstance();
    try
    {
      Date localDate1 = localDateFormat.parse("11/5/2003 0:00:00.000");
      Date localDate2 = localDateFormat.parse("11/5/2003 0:15:00.000");
      Date localDate3 = localDateFormat.parse("11/5/2003 0:30:00.000");
      Date localDate4 = localDateFormat.parse("11/5/2003 0:45:00.000");
      Date localDate5 = localDateFormat.parse("11/5/2003 1:00:00.001");
      Date localDate6 = localDateFormat.parse("11/5/2003 1:14:59.999");
      Date localDate7 = localDateFormat.parse("11/5/2003 1:30:00.000");
      Date localDate8 = localDateFormat.parse("11/5/2003 1:45:00.000");
      Date localDate9 = localDateFormat.parse("11/5/2003 2:00:00.000");
      Date localDate10 = localDateFormat.parse("11/5/2003 2:15:00.000");
      localTimePeriodValues.add(new SimpleTimePeriod(localDate1, localDate2), 0.39D);
      localTimePeriodValues.add(new SimpleTimePeriod(localDate3, localDate4), 0.225D);
      localTimePeriodValues.add(new SimpleTimePeriod(localDate4, localDate5), 0.235D);
      localTimePeriodValues.add(new SimpleTimePeriod(localDate5, localDate6), 0.238D);
      localTimePeriodValues.add(new SimpleTimePeriod(localDate6, localDate7), 0.236D);
      localTimePeriodValues.add(new SimpleTimePeriod(localDate7, localDate8), 0.25D);
      localTimePeriodValues.add(new SimpleTimePeriod(localDate8, localDate9), 0.238D);
      localTimePeriodValues.add(new SimpleTimePeriod(localDate9, localDate10), 0.215D);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    TimePeriodValuesCollection localTimePeriodValuesCollection = new TimePeriodValuesCollection();
    localTimePeriodValuesCollection.addSeries(localTimePeriodValues);
    return localTimePeriodValuesCollection;
  }

  public static void main(String[] paramArrayOfString)
  {
    TimePeriodValuesDemo3 localTimePeriodValuesDemo3 = new TimePeriodValuesDemo3("JFreeChart: TimePeriodValuesDemo3.java");
    localTimePeriodValuesDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localTimePeriodValuesDemo3);
    localTimePeriodValuesDemo3.setVisible(true);
  }
}