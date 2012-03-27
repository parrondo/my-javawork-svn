package demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class ScatterPlotDemo1 extends ApplicationFrame
{
  public ScatterPlotDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createScatterPlot("Scatter Plot Demo 1", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setNoDataMessage("NO DATA");
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    localXYPlot.setDomainZeroBaselineVisible(true);
    localXYPlot.setRangeZeroBaselineVisible(true);
    localXYPlot.setDomainGridlineStroke(new BasicStroke(0.0F));
    localXYPlot.setDomainMinorGridlineStroke(new BasicStroke(0.0F));
    localXYPlot.setDomainGridlinePaint(Color.blue);
    localXYPlot.setRangeGridlineStroke(new BasicStroke(0.0F));
    localXYPlot.setRangeMinorGridlineStroke(new BasicStroke(0.0F));
    localXYPlot.setRangeGridlinePaint(Color.blue);
    localXYPlot.setDomainMinorGridlinesVisible(true);
    localXYPlot.setRangeMinorGridlinesVisible(true);
    XYLineAndShapeRenderer localXYLineAndShapeRenderer = (XYLineAndShapeRenderer)localXYPlot.getRenderer();
    localXYLineAndShapeRenderer.setSeriesOutlinePaint(0, Color.black);
    localXYLineAndShapeRenderer.setUseOutlinePaint(true);
    NumberAxis localNumberAxis1 = (NumberAxis)localXYPlot.getDomainAxis();
    localNumberAxis1.setAutoRangeIncludesZero(false);
    localNumberAxis1.setTickMarkInsideLength(2.0F);
    localNumberAxis1.setTickMarkOutsideLength(2.0F);
    localNumberAxis1.setMinorTickCount(2);
    localNumberAxis1.setMinorTickMarksVisible(true);
    NumberAxis localNumberAxis2 = (NumberAxis)localXYPlot.getRangeAxis();
    localNumberAxis2.setTickMarkInsideLength(2.0F);
    localNumberAxis2.setTickMarkOutsideLength(2.0F);
    localNumberAxis2.setMinorTickCount(2);
    localNumberAxis2.setMinorTickMarksVisible(true);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(new SampleXYDataset2());
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setMouseWheelEnabled(true);
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    ScatterPlotDemo1 localScatterPlotDemo1 = new ScatterPlotDemo1("JFreeChart: ScatterPlotDemo1.java");
    localScatterPlotDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localScatterPlotDemo1);
    localScatterPlotDemo1.setVisible(true);
  }
}