package demo;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class MouseListenerDemo3 extends ApplicationFrame
  implements ChartMouseListener
{
  private JFreeChart chart;

  public MouseListenerDemo3(String paramString)
  {
    super(paramString);
    String str = "Legal & General Unit Trust Prices";
    XYDataset localXYDataset = createDataset();
    this.chart = ChartFactory.createTimeSeriesChart(str, "Date", "Price Per Unit", localXYDataset, true, true, false);
    this.chart.addSubtitle(new TextTitle("Click on the legend to see series highlighted..."));
    XYPlot localXYPlot = (XYPlot)this.chart.getPlot();
    DateAxis localDateAxis = (DateAxis)localXYPlot.getDomainAxis();
    localDateAxis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
    ChartPanel localChartPanel = new ChartPanel(this.chart);
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    localChartPanel.setMouseZoomable(true);
    localChartPanel.addChartMouseListener(this);
    setContentPane(localChartPanel);
  }

  public XYDataset createDataset()
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
    localTimeSeriesCollection.setXPosition(TimePeriodAnchor.MIDDLE);
    return localTimeSeriesCollection;
  }

  public void chartMouseClicked(ChartMouseEvent paramChartMouseEvent)
  {
    ChartEntity localChartEntity = paramChartMouseEvent.getEntity();
    if ((localChartEntity == null) || (!(localChartEntity instanceof LegendItemEntity)))
      return;
    LegendItemEntity localLegendItemEntity = (LegendItemEntity)localChartEntity;
    Comparable localComparable = localLegendItemEntity.getSeriesKey();
    XYPlot localXYPlot = (XYPlot)this.chart.getPlot();
    XYDataset localXYDataset = localXYPlot.getDataset();
    XYItemRenderer localXYItemRenderer = localXYPlot.getRenderer();
    for (int i = 0; i < localXYDataset.getSeriesCount(); ++i)
    {
      localXYItemRenderer.setSeriesStroke(i, new BasicStroke(1.0F));
      if (!(localXYDataset.getSeriesKey(i).equals(localComparable)))
        continue;
      localXYItemRenderer.setSeriesStroke(i, new BasicStroke(2.0F));
    }
  }

  public void chartMouseMoved(ChartMouseEvent paramChartMouseEvent)
  {
  }

  public static void main(String[] paramArrayOfString)
  {
    MouseListenerDemo3 localMouseListenerDemo3 = new MouseListenerDemo3("JFreeChart: MouseListenerDemo3.java");
    localMouseListenerDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localMouseListenerDemo3);
    localMouseListenerDemo3.setVisible(true);
  }
}