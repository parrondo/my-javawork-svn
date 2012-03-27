package demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYShapeRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;

public class XYShapeRendererDemo1 extends ApplicationFrame
{
  public XYShapeRendererDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYZDataset paramXYZDataset)
  {
    NumberAxis localNumberAxis1 = new NumberAxis("X");
    localNumberAxis1.setAutoRangeIncludesZero(false);
    NumberAxis localNumberAxis2 = new NumberAxis("Y");
    localNumberAxis2.setAutoRangeIncludesZero(false);
    XYShapeRenderer localXYShapeRenderer = new XYShapeRenderer();
    LookupPaintScale localLookupPaintScale = new LookupPaintScale(1.0D, 4.0D, new Color(0, 0, 255));
    localLookupPaintScale.add(2.0D, new Color(100, 100, 255));
    localLookupPaintScale.add(3.0D, new Color(200, 200, 255));
    localXYShapeRenderer.setPaintScale(localLookupPaintScale);
    XYPlot localXYPlot = new XYPlot(paramXYZDataset, localNumberAxis1, localNumberAxis2, localXYShapeRenderer);
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    JFreeChart localJFreeChart = new JFreeChart("XYShapeRendererDemo1", localXYPlot);
    localJFreeChart.removeLegend();
    NumberAxis localNumberAxis3 = new NumberAxis("Score");
    localNumberAxis3.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    PaintScaleLegend localPaintScaleLegend = new PaintScaleLegend(localLookupPaintScale, localNumberAxis3);
    localPaintScaleLegend.setPosition(RectangleEdge.RIGHT);
    localPaintScaleLegend.setMargin(4.0D, 4.0D, 40.0D, 4.0D);
    localPaintScaleLegend.setAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
    localJFreeChart.addSubtitle(localPaintScaleLegend);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static XYZDataset createDataset()
  {
    DefaultXYZDataset localDefaultXYZDataset = new DefaultXYZDataset();
    double[] arrayOfDouble1 = { 2.1D, 2.3D, 2.3D, 2.2D, 2.2D, 1.8D, 1.8D, 1.9D, 2.3D, 2.8D };
    double[] arrayOfDouble2 = { 14.1D, 17.100000000000001D, 10.0D, 8.800000000000001D, 8.699999999999999D, 8.4D, 5.4D, 4.1D, 4.1D, 25.0D };
    double[] arrayOfDouble3 = { 2.4D, 2.7D, 1.7D, 2.2D, 1.3D, 2.2D, 2.1D, 3.2D, 1.6D, 3.4D };
    double[][] arrayOfDouble = { arrayOfDouble1, arrayOfDouble2, arrayOfDouble3 };
    localDefaultXYZDataset.addSeries("Series 1", arrayOfDouble);
    return localDefaultXYZDataset;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setMouseWheelEnabled(true);
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    XYShapeRendererDemo1 localXYShapeRendererDemo1 = new XYShapeRendererDemo1("JFreeChart: XYShapeRendererDemo1.java");
    localXYShapeRendererDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localXYShapeRendererDemo1);
    localXYShapeRendererDemo1.setVisible(true);
  }
}