package demo;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.io.PrintStream;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class ScatterPlotDemo3 extends ApplicationFrame
{
  public ScatterPlotDemo3(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createScatterPlot("Scatter Plot Demo 3", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainCrosshairVisible(true);
    localXYPlot.setDomainCrosshairLockedOnData(true);
    localXYPlot.setRangeCrosshairVisible(true);
    localXYPlot.setRangeCrosshairLockedOnData(true);
    localXYPlot.setDomainZeroBaselineVisible(true);
    localXYPlot.setRangeZeroBaselineVisible(true);
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    NumberAxis localNumberAxis = (NumberAxis)localXYPlot.getDomainAxis();
    localNumberAxis.setAutoRangeIncludesZero(false);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(new SampleXYDataset2());
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setMouseWheelEnabled(true);
    localChartPanel.addChartMouseListener(new MyChartMouseListener(localChartPanel));
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    ScatterPlotDemo3 localScatterPlotDemo3 = new ScatterPlotDemo3("JFreeChart: ScatterPlotDemo3.java");
    localScatterPlotDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localScatterPlotDemo3);
    localScatterPlotDemo3.setVisible(true);
  }

  static class MyChartMouseListener
    implements ChartMouseListener
  {
    ChartPanel panel;

    public MyChartMouseListener(ChartPanel paramChartPanel)
    {
      this.panel = paramChartPanel;
    }

    public void chartMouseClicked(ChartMouseEvent paramChartMouseEvent)
    {
      int i = paramChartMouseEvent.getTrigger().getX();
      int j = paramChartMouseEvent.getTrigger().getY();
      Point2D localPoint2D = this.panel.translateScreenToJava2D(new Point(i, j));
      XYPlot localXYPlot = (XYPlot)this.panel.getChart().getPlot();
      ChartRenderingInfo localChartRenderingInfo = this.panel.getChartRenderingInfo();
      Rectangle2D localRectangle2D = localChartRenderingInfo.getPlotInfo().getDataArea();
      double d1 = localXYPlot.getDomainAxis().java2DToValue(localPoint2D.getX(), localRectangle2D, localXYPlot.getDomainAxisEdge());
      double d2 = localXYPlot.getRangeAxis().java2DToValue(localPoint2D.getY(), localRectangle2D, localXYPlot.getRangeAxisEdge());
      ValueAxis localValueAxis1 = localXYPlot.getDomainAxis();
      ValueAxis localValueAxis2 = localXYPlot.getRangeAxis();
      double d3 = localValueAxis1.valueToJava2D(d1, localRectangle2D, localXYPlot.getDomainAxisEdge());
      double d4 = localValueAxis2.valueToJava2D(d2, localRectangle2D, localXYPlot.getRangeAxisEdge());
      Point localPoint = this.panel.translateJava2DToScreen(new Point2D.Double(d3, d4));
      System.out.println("Mouse coordinates are (" + i + ", " + j + "), in data space = (" + d1 + ", " + d2 + ").");
      System.out.println("--> (" + localPoint.getX() + ", " + localPoint.getY() + ")");
    }

    public void chartMouseMoved(ChartMouseEvent paramChartMouseEvent)
    {
    }
  }
}