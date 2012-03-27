package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class XYLineAndShapeRendererDemo2 extends ApplicationFrame
{
  public XYLineAndShapeRendererDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 300));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart()
  {
    XYDataset localXYDataset1 = createDataset(1, 1.0D);
    JFreeChart localJFreeChart = ChartFactory.createXYLineChart("XYLineAndShapeRenderer Demo 2", "X", "Y", localXYDataset1, PlotOrientation.VERTICAL, true, true, false);
    TextTitle localTextTitle = new TextTitle("This chart shows various combinations of the useFillPaint and useOutlinePaint flags.");
    localTextTitle.setFont(new Font("Dialog", 0, 10));
    localJFreeChart.addSubtitle(localTextTitle);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    NumberAxis localNumberAxis = (NumberAxis)localXYPlot.getRangeAxis();
    localNumberAxis.setAutoRangeIncludesZero(false);
    Ellipse2D.Double localDouble = new Ellipse2D.Double(-4.0D, -4.0D, 8.0D, 8.0D);
    XYLineAndShapeRenderer localXYLineAndShapeRenderer1 = (XYLineAndShapeRenderer)localXYPlot.getRenderer();
    localXYLineAndShapeRenderer1.setBaseShapesVisible(true);
    localXYLineAndShapeRenderer1.setSeriesShape(0, localDouble);
    localXYLineAndShapeRenderer1.setSeriesPaint(0, Color.red);
    localXYLineAndShapeRenderer1.setSeriesFillPaint(0, Color.yellow);
    localXYLineAndShapeRenderer1.setSeriesOutlinePaint(0, Color.gray);
    XYDataset localXYDataset2 = createDataset(2, 2.0D);
    XYLineAndShapeRenderer localXYLineAndShapeRenderer2 = new XYLineAndShapeRenderer();
    localXYPlot.setDataset(1, localXYDataset2);
    localXYPlot.setRenderer(1, localXYLineAndShapeRenderer2);
    localXYLineAndShapeRenderer2.setSeriesShape(0, localDouble);
    localXYLineAndShapeRenderer2.setSeriesPaint(0, Color.red);
    localXYLineAndShapeRenderer2.setSeriesFillPaint(0, Color.yellow);
    localXYLineAndShapeRenderer2.setSeriesOutlinePaint(0, Color.gray);
    localXYLineAndShapeRenderer2.setUseFillPaint(true);
    XYDataset localXYDataset3 = createDataset(3, 3.0D);
    XYLineAndShapeRenderer localXYLineAndShapeRenderer3 = new XYLineAndShapeRenderer();
    localXYPlot.setDataset(2, localXYDataset3);
    localXYPlot.setRenderer(2, localXYLineAndShapeRenderer3);
    localXYLineAndShapeRenderer3.setSeriesShape(0, localDouble);
    localXYLineAndShapeRenderer3.setSeriesPaint(0, Color.red);
    localXYLineAndShapeRenderer3.setSeriesFillPaint(0, Color.yellow);
    localXYLineAndShapeRenderer3.setSeriesOutlinePaint(0, Color.gray);
    localXYLineAndShapeRenderer3.setUseOutlinePaint(true);
    XYDataset localXYDataset4 = createDataset(4, 4.0D);
    XYLineAndShapeRenderer localXYLineAndShapeRenderer4 = new XYLineAndShapeRenderer();
    localXYPlot.setDataset(3, localXYDataset4);
    localXYPlot.setRenderer(3, localXYLineAndShapeRenderer4);
    localXYLineAndShapeRenderer4.setSeriesShape(0, localDouble);
    localXYLineAndShapeRenderer4.setSeriesPaint(0, Color.red);
    localXYLineAndShapeRenderer4.setSeriesFillPaint(0, Color.yellow);
    localXYLineAndShapeRenderer4.setSeriesOutlinePaint(0, Color.gray);
    localXYLineAndShapeRenderer4.setUseOutlinePaint(true);
    localXYLineAndShapeRenderer4.setUseFillPaint(true);
    XYDataset localXYDataset5 = createDataset(5, 5.0D);
    XYLineAndShapeRenderer localXYLineAndShapeRenderer5 = new XYLineAndShapeRenderer();
    localXYPlot.setDataset(4, localXYDataset5);
    localXYPlot.setRenderer(4, localXYLineAndShapeRenderer5);
    localXYLineAndShapeRenderer5.setSeriesShape(0, localDouble);
    localXYLineAndShapeRenderer5.setSeriesPaint(0, Color.red);
    localXYLineAndShapeRenderer5.setSeriesFillPaint(0, Color.yellow);
    localXYLineAndShapeRenderer5.setSeriesOutlinePaint(0, Color.gray);
    localXYLineAndShapeRenderer5.setUseOutlinePaint(true);
    localXYLineAndShapeRenderer5.setUseFillPaint(true);
    localXYLineAndShapeRenderer5.setDrawOutlines(false);
    return localJFreeChart;
  }

  private static XYDataset createDataset(int paramInt, double paramDouble)
  {
    XYSeries localXYSeries = new XYSeries("Series " + paramInt);
    localXYSeries.add(1.0D, paramDouble);
    localXYSeries.add(2.0D, paramDouble);
    localXYSeries.add(3.0D, paramDouble);
    XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection();
    localXYSeriesCollection.addSeries(localXYSeries);
    return localXYSeriesCollection;
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
    XYLineAndShapeRendererDemo2 localXYLineAndShapeRendererDemo2 = new XYLineAndShapeRendererDemo2("JFreeChart: XYLineAndShapeRendererDemo2");
    localXYLineAndShapeRendererDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localXYLineAndShapeRendererDemo2);
    localXYLineAndShapeRendererDemo2.setVisible(true);
  }
}