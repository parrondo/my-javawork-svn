package demo;

import java.awt.Dimension;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class TimePeriodValuesDemo2 extends ApplicationFrame
{
  public TimePeriodValuesDemo2(String paramString)
  {
    super(paramString);
    XYDataset localXYDataset = createDataset();
    XYBarRenderer localXYBarRenderer = new XYBarRenderer();
    DateAxis localDateAxis = new DateAxis("Date");
    NumberAxis localNumberAxis = new NumberAxis("Value");
    XYPlot localXYPlot = new XYPlot(localXYDataset, localDateAxis, localNumberAxis, localXYBarRenderer);
    JFreeChart localJFreeChart = new JFreeChart("Time Period Values Demo", localXYPlot);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    localChartPanel.setMouseZoomable(true);
    setContentPane(localChartPanel);
  }

  public XYDataset createDataset()
  {
    TimePeriodValues localTimePeriodValues = new TimePeriodValues("Series 1");
    Day localDay1 = new Day();
    Day localDay2 = (Day)localDay1.next();
    Day localDay3 = (Day)localDay2.next();
    Day localDay4 = (Day)localDay3.next();
    Day localDay5 = (Day)localDay4.next();
    Day localDay6 = (Day)localDay5.next();
    Day localDay7 = (Day)localDay6.next();
    localTimePeriodValues.add(new SimpleTimePeriod(localDay6.getStart(), localDay6.getEnd()), 74.950000000000003D);
    localTimePeriodValues.add(new SimpleTimePeriod(localDay1.getStart(), localDay2.getEnd()), 55.75D);
    localTimePeriodValues.add(new SimpleTimePeriod(localDay7.getStart(), localDay7.getEnd()), 90.450000000000003D);
    localTimePeriodValues.add(new SimpleTimePeriod(localDay3.getStart(), localDay5.getEnd()), 105.75D);
    TimePeriodValuesCollection localTimePeriodValuesCollection = new TimePeriodValuesCollection();
    localTimePeriodValuesCollection.addSeries(localTimePeriodValues);
    return localTimePeriodValuesCollection;
  }

  public static void main(String[] paramArrayOfString)
  {
    TimePeriodValuesDemo2 localTimePeriodValuesDemo2 = new TimePeriodValuesDemo2("Time Period Values Demo 2");
    localTimePeriodValuesDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localTimePeriodValuesDemo2);
    localTimePeriodValuesDemo2.setVisible(true);
  }
}