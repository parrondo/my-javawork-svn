package demo;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.PrintStream;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;

public class MouseListenerDemo4 extends ApplicationFrame
  implements ChartMouseListener
{
  private JFreeChart chart;
  private ChartPanel chartPanel;

  public MouseListenerDemo4(String paramString)
  {
    super(paramString);
    String str = "Mouse Listener Demo 4";
    XYDataset localXYDataset = createDataset();
    this.chart = ChartFactory.createXYLineChart(str, "X", "Y", localXYDataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot localXYPlot = (XYPlot)this.chart.getPlot();
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    this.chartPanel = new ChartPanel(this.chart);
    this.chartPanel.setMouseWheelEnabled(true);
    this.chartPanel.setPreferredSize(new Dimension(500, 270));
    this.chartPanel.setMouseZoomable(true);
    this.chartPanel.addChartMouseListener(this);
    setContentPane(this.chartPanel);
  }

  public XYDataset createDataset()
  {
    XYSeries localXYSeries = new XYSeries("Series 1");
    localXYSeries.add(12.5D, 11.0D);
    localXYSeries.add(15.0D, 9.300000000000001D);
    localXYSeries.add(20.0D, 21.0D);
    XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection();
    localXYSeriesCollection.addSeries(localXYSeries);
    return localXYSeriesCollection;
  }

  public void chartMouseClicked(ChartMouseEvent paramChartMouseEvent)
  {
    int i = paramChartMouseEvent.getTrigger().getX();
    int j = paramChartMouseEvent.getTrigger().getY();
    System.out.println("x = " + i + ", y = " + j);
    Point2D localPoint2D = this.chartPanel.translateScreenToJava2D(new Point(i, j));
    XYPlot localXYPlot = (XYPlot)this.chart.getPlot();
    ChartRenderingInfo localChartRenderingInfo = this.chartPanel.getChartRenderingInfo();
    Rectangle2D localRectangle2D = localChartRenderingInfo.getPlotInfo().getDataArea();
    ValueAxis localValueAxis1 = localXYPlot.getDomainAxis();
    RectangleEdge localRectangleEdge1 = localXYPlot.getDomainAxisEdge();
    ValueAxis localValueAxis2 = localXYPlot.getRangeAxis();
    RectangleEdge localRectangleEdge2 = localXYPlot.getRangeAxisEdge();
    double d1 = localValueAxis1.java2DToValue(localPoint2D.getX(), localRectangle2D, localRectangleEdge1);
    double d2 = localValueAxis2.java2DToValue(localPoint2D.getY(), localRectangle2D, localRectangleEdge2);
    System.out.println("Chart: x = " + d1 + ", y = " + d2);
  }

  public void chartMouseMoved(ChartMouseEvent paramChartMouseEvent)
  {
  }

  public static void main(String[] paramArrayOfString)
  {
    MouseListenerDemo4 localMouseListenerDemo4 = new MouseListenerDemo4("JFreeChart: MouseListenerDemo4.java");
    localMouseListenerDemo4.pack();
    RefineryUtilities.centerFrameOnScreen(localMouseListenerDemo4);
    localMouseListenerDemo4.setVisible(true);
  }
}