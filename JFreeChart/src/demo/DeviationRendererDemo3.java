package demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.data.time.Quarter;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class DeviationRendererDemo3 extends ApplicationFrame
{
  public DeviationRendererDemo3(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static XYDataset createDataset()
  {
    YIntervalSeries localYIntervalSeries1 = new YIntervalSeries("Band A");
    YIntervalSeries localYIntervalSeries2 = new YIntervalSeries("Band B");
    YIntervalSeries localYIntervalSeries3 = new YIntervalSeries("Band C");
    Object localObject = new Quarter(1, 2005);
    double d = 0.0D;
    for (int i = 0; i <= 12; ++i)
    {
      d += (Math.random() - 0.5D) * 15.0D;
      localYIntervalSeries1.add(((RegularTimePeriod)localObject).getMiddleMillisecond(), d, d + 10.0D, Math.max(50.0D, d + 30.0D));
      localYIntervalSeries2.add(((RegularTimePeriod)localObject).getMiddleMillisecond(), d, d - 10.0D, d + 10.0D);
      localYIntervalSeries3.add(((RegularTimePeriod)localObject).getMiddleMillisecond(), d, Math.min(-50.0D, d - 30.0D), d - 10.0D);
      localObject = ((RegularTimePeriod)localObject).next();
    }
    YIntervalSeriesCollection localYIntervalSeriesCollection = new YIntervalSeriesCollection();
    localYIntervalSeriesCollection.addSeries(localYIntervalSeries1);
    localYIntervalSeriesCollection.addSeries(localYIntervalSeries2);
    localYIntervalSeriesCollection.addSeries(localYIntervalSeries3);
    return ((XYDataset)localYIntervalSeriesCollection);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYLineChart("DeviationRenderer - Demo 3", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, false, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    DeviationRenderer localDeviationRenderer = new DeviationRenderer(false, false);
    localDeviationRenderer.setSeriesStroke(0, new BasicStroke(3.0F, 1, 1));
    localDeviationRenderer.setSeriesStroke(0, new BasicStroke(3.0F, 1, 1));
    localDeviationRenderer.setSeriesStroke(1, new BasicStroke(3.0F, 1, 1));
    localDeviationRenderer.setSeriesFillPaint(0, Color.red);
    localDeviationRenderer.setSeriesFillPaint(1, Color.orange);
    localDeviationRenderer.setSeriesFillPaint(2, Color.green);
    localXYPlot.setRenderer(localDeviationRenderer);
    DateAxis localDateAxis = new DateAxis("Date");
    localDateAxis.setLowerMargin(0.0D);
    localDateAxis.setUpperMargin(0.0D);
    localXYPlot.setDomainAxis(localDateAxis);
    NumberAxis localNumberAxis = (NumberAxis)localXYPlot.getRangeAxis();
    localNumberAxis.setRange(-40.0D, 40.0D);
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  public static void main(String[] paramArrayOfString)
  {
    DeviationRendererDemo3 localDeviationRendererDemo3 = new DeviationRendererDemo3("JFreeChart : DeviationRendererDemo3.java");
    localDeviationRendererDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localDeviationRendererDemo3);
    localDeviationRendererDemo3.setVisible(true);
  }
}