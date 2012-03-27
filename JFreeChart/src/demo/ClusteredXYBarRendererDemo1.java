package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.GridLayout;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.StandardGradientPaintTransformer;

public class ClusteredXYBarRendererDemo1 extends ApplicationFrame
{
  public ClusteredXYBarRendererDemo1(String paramString)
  {
    super(paramString);
    setContentPane(createDemoPanel());
  }

  private static JFreeChart createChart(String paramString, IntervalXYDataset paramIntervalXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYBarChart(paramString, null, true, "Y", paramIntervalXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    ClusteredXYBarRenderer localClusteredXYBarRenderer = new ClusteredXYBarRenderer(0.2D, false);
    localXYPlot.setRenderer(localClusteredXYBarRenderer);
    return localJFreeChart;
  }

  private static IntervalXYDataset createDataset()
  {
    TimeSeries localTimeSeries1 = new TimeSeries("Series 1");
    localTimeSeries1.add(new Day(1, 1, 2003), 54.299999999999997D);
    localTimeSeries1.add(new Day(2, 1, 2003), 20.300000000000001D);
    localTimeSeries1.add(new Day(3, 1, 2003), 43.399999999999999D);
    localTimeSeries1.add(new Day(4, 1, 2003), -12.0D);
    TimeSeries localTimeSeries2 = new TimeSeries("Series 2");
    localTimeSeries2.add(new Day(1, 1, 2003), 8.0D);
    localTimeSeries2.add(new Day(2, 1, 2003), 16.0D);
    localTimeSeries2.add(new Day(3, 1, 2003), 21.0D);
    localTimeSeries2.add(new Day(4, 1, 2003), 5.0D);
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
    localTimeSeriesCollection.addSeries(localTimeSeries1);
    localTimeSeriesCollection.addSeries(localTimeSeries2);
    return localTimeSeriesCollection;
  }

  public static JPanel createDemoPanel()
  {
    DemoPanel localDemoPanel = new DemoPanel(new GridLayout(2, 2));
    localDemoPanel.setPreferredSize(new Dimension(800, 600));
    IntervalXYDataset localIntervalXYDataset = createDataset();
    JFreeChart localJFreeChart1 = createChart("Vertical", localIntervalXYDataset);
    XYPlot localXYPlot1 = (XYPlot)localJFreeChart1.getPlot();
    XYBarRenderer localXYBarRenderer1 = (XYBarRenderer)localXYPlot1.getRenderer();
    localXYBarRenderer1.setDrawBarOutline(false);
    localXYBarRenderer1.setSeriesPaint(0, new GradientPaint(0.0F, 0.0F, Color.red, 0.0F, 0.0F, Color.yellow));
    localXYBarRenderer1.setSeriesPaint(1, new GradientPaint(0.0F, 0.0F, Color.blue, 0.0F, 0.0F, Color.green));
    localXYBarRenderer1.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.VERTICAL));
    ChartPanel localChartPanel1 = new ChartPanel(localJFreeChart1);
    localDemoPanel.add(localChartPanel1);
    JFreeChart localJFreeChart2 = createChart("Vertical / Inverted Axis", localIntervalXYDataset);
    XYPlot localXYPlot2 = (XYPlot)localJFreeChart2.getPlot();
    XYBarRenderer localXYBarRenderer2 = (XYBarRenderer)localXYPlot2.getRenderer();
    localXYBarRenderer2.setDrawBarOutline(false);
    localXYBarRenderer2.setSeriesPaint(0, new GradientPaint(0.0F, 0.0F, Color.red, 0.0F, 0.0F, Color.yellow));
    localXYBarRenderer2.setSeriesPaint(1, new GradientPaint(0.0F, 0.0F, Color.blue, 0.0F, 0.0F, Color.green));
    localXYBarRenderer2.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.HORIZONTAL));
    localXYPlot2.getDomainAxis().setInverted(true);
    ChartPanel localChartPanel2 = new ChartPanel(localJFreeChart2);
    localDemoPanel.add(localChartPanel2);
    JFreeChart localJFreeChart3 = createChart("Horizontal", localIntervalXYDataset);
    XYPlot localXYPlot3 = (XYPlot)localJFreeChart3.getPlot();
    localXYPlot3.setOrientation(PlotOrientation.HORIZONTAL);
    XYBarRenderer localXYBarRenderer3 = (XYBarRenderer)localXYPlot3.getRenderer();
    localXYBarRenderer3.setDrawBarOutline(false);
    localXYBarRenderer3.setSeriesPaint(0, new GradientPaint(0.0F, 0.0F, Color.red, 0.0F, 0.0F, Color.yellow));
    localXYBarRenderer3.setSeriesPaint(1, new GradientPaint(0.0F, 0.0F, Color.blue, 0.0F, 0.0F, Color.green));
    localXYBarRenderer3.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.CENTER_VERTICAL));
    ChartPanel localChartPanel3 = new ChartPanel(localJFreeChart3);
    localDemoPanel.add(localChartPanel3);
    JFreeChart localJFreeChart4 = createChart("Horizontal / Inverted Axis", localIntervalXYDataset);
    XYPlot localXYPlot4 = (XYPlot)localJFreeChart4.getPlot();
    localXYPlot4.setOrientation(PlotOrientation.HORIZONTAL);
    XYBarRenderer localXYBarRenderer4 = (XYBarRenderer)localXYPlot4.getRenderer();
    localXYBarRenderer4.setDrawBarOutline(false);
    localXYBarRenderer4.setSeriesPaint(0, new GradientPaint(0.0F, 0.0F, Color.red, 0.0F, 0.0F, Color.yellow));
    localXYBarRenderer4.setSeriesPaint(1, new GradientPaint(0.0F, 0.0F, Color.blue, 0.0F, 0.0F, Color.green));
    localXYBarRenderer4.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.CENTER_HORIZONTAL));
    localXYPlot4.getDomainAxis().setInverted(true);
    ChartPanel localChartPanel4 = new ChartPanel(localJFreeChart4);
    localDemoPanel.add(localChartPanel4);
    localDemoPanel.addChart(localJFreeChart1);
    localDemoPanel.addChart(localJFreeChart2);
    localDemoPanel.addChart(localJFreeChart3);
    localDemoPanel.addChart(localJFreeChart4);
    return localDemoPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    ClusteredXYBarRendererDemo1 localClusteredXYBarRendererDemo1 = new ClusteredXYBarRendererDemo1("JFreeChart: ClusteredXYBarRendererDemo1.java");
    localClusteredXYBarRendererDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localClusteredXYBarRendererDemo1);
    localClusteredXYBarRendererDemo1.setVisible(true);
  }
}