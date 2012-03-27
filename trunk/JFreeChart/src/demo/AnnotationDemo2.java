package demo;

import java.awt.Color;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.EmptyBlock;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.CompositeTitle;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class AnnotationDemo2 extends ApplicationFrame
{
  public AnnotationDemo2(String paramString)
  {
    super(paramString);
    setContentPane(createDemoPanel());
  }

  private static XYDataset createDataset1()
  {
    XYSeries localXYSeries = new XYSeries("Random Data 1");
    localXYSeries.add(1.0D, 181.80000000000001D);
    localXYSeries.add(2.0D, 167.30000000000001D);
    localXYSeries.add(3.0D, 153.80000000000001D);
    localXYSeries.add(4.0D, 167.59999999999999D);
    localXYSeries.add(5.0D, 158.80000000000001D);
    localXYSeries.add(6.0D, 148.30000000000001D);
    localXYSeries.add(7.0D, 153.90000000000001D);
    localXYSeries.add(8.0D, 142.69999999999999D);
    localXYSeries.add(9.0D, 123.2D);
    localXYSeries.add(10.0D, 131.80000000000001D);
    localXYSeries.add(11.0D, 139.59999999999999D);
    localXYSeries.add(12.0D, 142.90000000000001D);
    localXYSeries.add(13.0D, 138.69999999999999D);
    localXYSeries.add(14.0D, 137.30000000000001D);
    localXYSeries.add(15.0D, 143.90000000000001D);
    localXYSeries.add(16.0D, 139.80000000000001D);
    localXYSeries.add(17.0D, 137.0D);
    localXYSeries.add(18.0D, 132.80000000000001D);
    XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection();
    localXYSeriesCollection.addSeries(localXYSeries);
    return localXYSeriesCollection;
  }

  private static XYDataset createDataset2()
  {
    XYSeries localXYSeries = new XYSeries("Random Data 2");
    localXYSeries.add(1.0D, 429.60000000000002D);
    localXYSeries.add(2.0D, 323.19999999999999D);
    localXYSeries.add(3.0D, 417.19999999999999D);
    localXYSeries.add(4.0D, 624.10000000000002D);
    localXYSeries.add(5.0D, 422.60000000000002D);
    localXYSeries.add(6.0D, 619.20000000000005D);
    localXYSeries.add(7.0D, 416.5D);
    localXYSeries.add(8.0D, 512.70000000000005D);
    localXYSeries.add(9.0D, 501.5D);
    localXYSeries.add(10.0D, 306.10000000000002D);
    localXYSeries.add(11.0D, 410.30000000000001D);
    localXYSeries.add(12.0D, 511.69999999999999D);
    localXYSeries.add(13.0D, 611.0D);
    localXYSeries.add(14.0D, 709.60000000000002D);
    localXYSeries.add(15.0D, 613.20000000000005D);
    localXYSeries.add(16.0D, 711.60000000000002D);
    localXYSeries.add(17.0D, 708.79999999999995D);
    localXYSeries.add(18.0D, 501.60000000000002D);
    XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection();
    localXYSeriesCollection.addSeries(localXYSeries);
    return localXYSeriesCollection;
  }

  private static JFreeChart createChart()
  {
    XYDataset localXYDataset = createDataset1();
    JFreeChart localJFreeChart = ChartFactory.createXYLineChart("Annotation Demo 2", "Date", "Price Per Unit", localXYDataset, PlotOrientation.VERTICAL, false, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    NumberAxis localNumberAxis1 = (NumberAxis)localXYPlot.getRangeAxis();
    localNumberAxis1.setAutoRangeIncludesZero(false);
    NumberAxis localNumberAxis2 = new NumberAxis("Secondary");
    localNumberAxis2.setAutoRangeIncludesZero(false);
    localXYPlot.setRangeAxis(1, localNumberAxis2);
    localXYPlot.setDataset(1, createDataset2());
    localXYPlot.mapDatasetToRangeAxis(1, 1);
    XYLineAndShapeRenderer localXYLineAndShapeRenderer1 = (XYLineAndShapeRenderer)localXYPlot.getRenderer();
    localXYLineAndShapeRenderer1.setBaseToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
    localXYLineAndShapeRenderer1.setBaseShapesVisible(true);
    localXYLineAndShapeRenderer1.setBaseShapesFilled(true);
    XYPointerAnnotation localXYPointerAnnotation1 = new XYPointerAnnotation("Annotation 1 (2.0, 167.3)", 2.0D, 167.30000000000001D, -0.7853981633974483D);
    localXYPointerAnnotation1.setTextAnchor(TextAnchor.BOTTOM_LEFT);
    localXYPointerAnnotation1.setPaint(Color.red);
    localXYPointerAnnotation1.setArrowPaint(Color.red);
    localXYLineAndShapeRenderer1.addAnnotation(localXYPointerAnnotation1);
    XYLineAndShapeRenderer localXYLineAndShapeRenderer2 = new XYLineAndShapeRenderer(true, true);
    localXYLineAndShapeRenderer2.setSeriesPaint(0, Color.black);
    localXYLineAndShapeRenderer1.setBaseToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
    XYPointerAnnotation localXYPointerAnnotation2 = new XYPointerAnnotation("Annotation 2 (15.0, 613.2)", 15.0D, 613.20000000000005D, 1.570796326794897D);
    localXYPointerAnnotation2.setTextAnchor(TextAnchor.TOP_CENTER);
    localXYLineAndShapeRenderer2.addAnnotation(localXYPointerAnnotation2);
    localXYPlot.setRenderer(1, localXYLineAndShapeRenderer2);
    LegendTitle localLegendTitle1 = new LegendTitle(localXYLineAndShapeRenderer1);
    LegendTitle localLegendTitle2 = new LegendTitle(localXYLineAndShapeRenderer2);
    BlockContainer localBlockContainer = new BlockContainer(new BorderArrangement());
    localBlockContainer.add(localLegendTitle1, RectangleEdge.LEFT);
    localBlockContainer.add(localLegendTitle2, RectangleEdge.RIGHT);
    localBlockContainer.add(new EmptyBlock(2000.0D, 0.0D));
    CompositeTitle localCompositeTitle = new CompositeTitle(localBlockContainer);
    localCompositeTitle.setFrame(new BlockBorder(Color.red));
    localCompositeTitle.setBackgroundPaint(Color.yellow);
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
    AnnotationDemo2 localAnnotationDemo2 = new AnnotationDemo2("JFreeChart: AnnotationDemo2.java");
    localAnnotationDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localAnnotationDemo2);
    localAnnotationDemo2.setVisible(true);
  }
}