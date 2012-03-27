package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.EmptyBlock;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.CompositeTitle;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;

public class DualAxisDemo2 extends ApplicationFrame
{
  public DualAxisDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static XYDataset createDataset1()
  {
    TimeSeries localTimeSeries = new TimeSeries("Random Data 1");
    localTimeSeries.add(new Month(2, 2001), 181.80000000000001D);
    localTimeSeries.add(new Month(3, 2001), 167.30000000000001D);
    localTimeSeries.add(new Month(4, 2001), 153.80000000000001D);
    localTimeSeries.add(new Month(5, 2001), 167.59999999999999D);
    localTimeSeries.add(new Month(6, 2001), 158.80000000000001D);
    localTimeSeries.add(new Month(7, 2001), 148.30000000000001D);
    localTimeSeries.add(new Month(8, 2001), 153.90000000000001D);
    localTimeSeries.add(new Month(9, 2001), 142.69999999999999D);
    localTimeSeries.add(new Month(10, 2001), 123.2D);
    localTimeSeries.add(new Month(11, 2001), 131.80000000000001D);
    localTimeSeries.add(new Month(12, 2001), 139.59999999999999D);
    localTimeSeries.add(new Month(1, 2002), 142.90000000000001D);
    localTimeSeries.add(new Month(2, 2002), 138.69999999999999D);
    localTimeSeries.add(new Month(3, 2002), 137.30000000000001D);
    localTimeSeries.add(new Month(4, 2002), 143.90000000000001D);
    localTimeSeries.add(new Month(5, 2002), 139.80000000000001D);
    localTimeSeries.add(new Month(6, 2002), 137.0D);
    localTimeSeries.add(new Month(7, 2002), 132.80000000000001D);
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
    localTimeSeriesCollection.addSeries(localTimeSeries);
    return localTimeSeriesCollection;
  }

  private static XYDataset createDataset2()
  {
    TimeSeries localTimeSeries = new TimeSeries("Random Data 2");
    localTimeSeries.add(new Month(2, 2001), 429.60000000000002D);
    localTimeSeries.add(new Month(3, 2001), 323.19999999999999D);
    localTimeSeries.add(new Month(4, 2001), 417.19999999999999D);
    localTimeSeries.add(new Month(5, 2001), 624.10000000000002D);
    localTimeSeries.add(new Month(6, 2001), 422.60000000000002D);
    localTimeSeries.add(new Month(7, 2001), 619.20000000000005D);
    localTimeSeries.add(new Month(8, 2001), 416.5D);
    localTimeSeries.add(new Month(9, 2001), 512.70000000000005D);
    localTimeSeries.add(new Month(10, 2001), 501.5D);
    localTimeSeries.add(new Month(11, 2001), 306.10000000000002D);
    localTimeSeries.add(new Month(12, 2001), 410.30000000000001D);
    localTimeSeries.add(new Month(1, 2002), 511.69999999999999D);
    localTimeSeries.add(new Month(2, 2002), 611.0D);
    localTimeSeries.add(new Month(3, 2002), 709.60000000000002D);
    localTimeSeries.add(new Month(4, 2002), 613.20000000000005D);
    localTimeSeries.add(new Month(5, 2002), 711.60000000000002D);
    localTimeSeries.add(new Month(6, 2002), 708.79999999999995D);
    localTimeSeries.add(new Month(7, 2002), 501.60000000000002D);
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
    localTimeSeriesCollection.addSeries(localTimeSeries);
    return localTimeSeriesCollection;
  }

  private static JFreeChart createChart()
  {
    XYDataset localXYDataset = createDataset1();
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Dual Axis Demo 2", "Date", "Price Per Unit", localXYDataset, false, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    NumberAxis localNumberAxis = new NumberAxis("Secondary");
    localNumberAxis.setAutoRangeIncludesZero(false);
    localXYPlot.setRangeAxis(1, localNumberAxis);
    localXYPlot.setDataset(1, createDataset2());
    localXYPlot.mapDatasetToRangeAxis(1, 1);
    XYItemRenderer localXYItemRenderer = localXYPlot.getRenderer();
    localXYItemRenderer.setBaseToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
    if (localXYItemRenderer instanceof XYLineAndShapeRenderer)
    {
      XYLineAndShapeRenderer localXYLineAndShapeRenderer = (XYLineAndShapeRenderer)localXYItemRenderer;
      localXYLineAndShapeRenderer.setBaseShapesVisible(true);
      localXYLineAndShapeRenderer.setBaseShapesFilled(true);
    }
    XYLineAndShapeRenderer localXYLineAndShapeRenderer = new XYLineAndShapeRenderer();
    localXYLineAndShapeRenderer.setSeriesPaint(0, Color.black);
    localXYLineAndShapeRenderer.setBaseShapesVisible(true);
    localXYLineAndShapeRenderer.setBaseToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
    localXYPlot.setRenderer(1, localXYLineAndShapeRenderer);
    DateAxis localDateAxis = (DateAxis)localXYPlot.getDomainAxis();
    localDateAxis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
    LegendTitle localLegendTitle1 = new LegendTitle(localXYItemRenderer);
    LegendTitle localLegendTitle2 = new LegendTitle(localXYLineAndShapeRenderer);
    BlockContainer localBlockContainer = new BlockContainer(new BorderArrangement());
    localBlockContainer.add(localLegendTitle1, RectangleEdge.LEFT);
    localBlockContainer.add(localLegendTitle2, RectangleEdge.RIGHT);
    localBlockContainer.add(new EmptyBlock(2000.0D, 0.0D));
    CompositeTitle localCompositeTitle = new CompositeTitle(localBlockContainer);
    localCompositeTitle.setPosition(RectangleEdge.BOTTOM);
    localJFreeChart.addSubtitle(localCompositeTitle);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart();
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setMouseWheelEnabled(true);
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    DualAxisDemo2 localDualAxisDemo2 = new DualAxisDemo2("JFreeChart: DualAxisDemo2.java");
    localDualAxisDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localDualAxisDemo2);
    localDualAxisDemo2.setVisible(true);
  }
}