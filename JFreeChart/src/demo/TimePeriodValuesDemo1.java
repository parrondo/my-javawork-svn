package demo;

import java.awt.Dimension;
import java.text.SimpleDateFormat;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class TimePeriodValuesDemo1 extends ApplicationFrame
{
  public TimePeriodValuesDemo1(String paramString)
  {
    super(paramString);
    XYDataset localXYDataset1 = createDataset1();
    XYBarRenderer localXYBarRenderer = new XYBarRenderer();
    DateAxis localDateAxis = new DateAxis("Date");
    localDateAxis.setVerticalTickLabels(true);
    localDateAxis.setTickUnit(new DateTickUnit(DateTickUnitType.HOUR, 1));
    localDateAxis.setDateFormatOverride(new SimpleDateFormat("hh:mm"));
    localDateAxis.setLowerMargin(0.01D);
    localDateAxis.setUpperMargin(0.01D);
    NumberAxis localNumberAxis = new NumberAxis("Value");
    XYPlot localXYPlot = new XYPlot(localXYDataset1, localDateAxis, localNumberAxis, localXYBarRenderer);
    XYDataset localXYDataset2 = createDataset2();
    StandardXYItemRenderer localStandardXYItemRenderer = new StandardXYItemRenderer(3);
    localStandardXYItemRenderer.setBaseShapesFilled(true);
    localXYPlot.setDataset(1, localXYDataset2);
    localXYPlot.setRenderer(1, localStandardXYItemRenderer);
    JFreeChart localJFreeChart = new JFreeChart("Supply and Demand", localXYPlot);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    localChartPanel.setMouseZoomable(true);
    setContentPane(localChartPanel);
  }

  public XYDataset createDataset1()
  {
    TimePeriodValues localTimePeriodValues1 = new TimePeriodValues("Supply");
    TimePeriodValues localTimePeriodValues2 = new TimePeriodValues("Demand");
    Day localDay = new Day();
    for (int i = 0; i < 24; ++i)
    {
      Minute localMinute1 = new Minute(0, new Hour(i, localDay));
      Minute localMinute2 = new Minute(15, new Hour(i, localDay));
      Minute localMinute3 = new Minute(30, new Hour(i, localDay));
      Minute localMinute4 = new Minute(45, new Hour(i, localDay));
      Minute localMinute5 = new Minute(0, new Hour(i + 1, localDay));
      localTimePeriodValues1.add(new SimpleTimePeriod(localMinute1.getStart(), localMinute2.getStart()), Math.random());
      localTimePeriodValues2.add(new SimpleTimePeriod(localMinute2.getStart(), localMinute3.getStart()), Math.random());
      localTimePeriodValues1.add(new SimpleTimePeriod(localMinute3.getStart(), localMinute4.getStart()), Math.random());
      localTimePeriodValues2.add(new SimpleTimePeriod(localMinute4.getStart(), localMinute5.getStart()), Math.random());
    }
    TimePeriodValuesCollection localTimePeriodValuesCollection = new TimePeriodValuesCollection();
    localTimePeriodValuesCollection.addSeries(localTimePeriodValues1);
    localTimePeriodValuesCollection.addSeries(localTimePeriodValues2);
    return localTimePeriodValuesCollection;
  }

  public XYDataset createDataset2()
  {
    TimePeriodValues localTimePeriodValues = new TimePeriodValues("WebCOINS");
    Day localDay = new Day();
    for (int i = 0; i < 24; ++i)
    {
      Minute localMinute1 = new Minute(0, new Hour(i, localDay));
      Minute localMinute2 = new Minute(30, new Hour(i, localDay));
      Minute localMinute3 = new Minute(0, new Hour(i + 1, localDay));
      localTimePeriodValues.add(new SimpleTimePeriod(localMinute1.getStart(), localMinute2.getStart()), Math.random() * 2.0D);
      localTimePeriodValues.add(new SimpleTimePeriod(localMinute2.getStart(), localMinute3.getStart()), Math.random() * 2.0D);
    }
    TimePeriodValuesCollection localTimePeriodValuesCollection = new TimePeriodValuesCollection();
    localTimePeriodValuesCollection.addSeries(localTimePeriodValues);
    return localTimePeriodValuesCollection;
  }

  public static void main(String[] paramArrayOfString)
  {
    TimePeriodValuesDemo1 localTimePeriodValuesDemo1 = new TimePeriodValuesDemo1("Time Period Values Demo 1");
    localTimePeriodValuesDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localTimePeriodValuesDemo1);
    localTimePeriodValuesDemo1.setVisible(true);
  }
}