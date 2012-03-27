package demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.annotations.XYShapeAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.Layer;
import org.jfree.ui.RefineryUtilities;

public class PlotOrientationDemo2 extends ApplicationFrame
{
  private static final int CHART_COUNT = 8;

  public PlotOrientationDemo2(String paramString)
  {
    super(paramString);
    setContentPane(new MyDemoPanel());
  }

  private static XYDataset createDataset(int paramInt)
  {
    XYSeries localXYSeries = new XYSeries("Series " + (paramInt + 1));
    localXYSeries.add(-10.0D, -5.0D);
    localXYSeries.add(10.0D, 5.0D);
    XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection();
    localXYSeriesCollection.addSeries(localXYSeries);
    return localXYSeriesCollection;
  }

  private static JFreeChart createChart(int paramInt, XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYLineChart("Chart " + (paramInt + 1), "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, false, false, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    XYLineAndShapeRenderer localXYLineAndShapeRenderer = (XYLineAndShapeRenderer)localXYPlot.getRenderer();
    localXYLineAndShapeRenderer.setBaseShapesVisible(true);
    localXYLineAndShapeRenderer.setBaseShapesFilled(true);
    ValueAxis localValueAxis1 = localXYPlot.getDomainAxis();
    localValueAxis1.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    ValueAxis localValueAxis2 = localXYPlot.getRangeAxis();
    localValueAxis2.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    return new MyDemoPanel();
  }

  public static void main(String[] paramArrayOfString)
  {
    PlotOrientationDemo2 localPlotOrientationDemo2 = new PlotOrientationDemo2("JFreeChart: PlotOrientationDemo2.java");
    localPlotOrientationDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localPlotOrientationDemo2);
    localPlotOrientationDemo2.setVisible(true);
  }

  static class MyDemoPanel extends DemoPanel
  {
    private XYDataset[] datasets = new XYDataset[8];
    private JFreeChart[] charts = new JFreeChart[8];
    private ChartPanel[] panels = new ChartPanel[8];

    public MyDemoPanel()
    {
      super(new GridLayout(2, 4));
      for (int i = 0; i < 8; ++i)
      {
        this.datasets[i] = PlotOrientationDemo2.createDataset(i);
        this.charts[i] = PlotOrientationDemo2.createChart(i, this.datasets[i]);
        XYPlot localXYPlot2 = (XYPlot)this.charts[i].getPlot();
        localXYPlot2.setDomainPannable(true);
        localXYPlot2.setRangePannable(true);
        XYShapeAnnotation localObject1 = new XYShapeAnnotation(new Rectangle2D.Double(1.0D, 2.0D, 2.0D, 3.0D), new BasicStroke(1.0F), Color.blue);
        XYLineAnnotation localObject2 = new XYLineAnnotation(0.0D, -5.0D, 10.0D, -5.0D);
        localXYPlot2.addAnnotation((XYAnnotation)localObject1);
        localXYPlot2.addAnnotation((XYAnnotation)localObject2);
        localXYPlot2.addDomainMarker(new IntervalMarker(5.0D, 10.0D), Layer.BACKGROUND);
        localXYPlot2.addRangeMarker(new IntervalMarker(-2.0D, 0.0D), Layer.BACKGROUND);
        addChart(this.charts[i]);
        this.panels[i] = new ChartPanel(this.charts[i]);
      }
      XYPlot localXYPlot1 = (XYPlot)this.charts[1].getPlot();
      XYPlot localXYPlot2 = (XYPlot)this.charts[2].getPlot();
      Object localObject1 = (XYPlot)this.charts[3].getPlot();
      Object localObject2 = (XYPlot)this.charts[4].getPlot();
      XYPlot localXYPlot3 = (XYPlot)this.charts[5].getPlot();
      XYPlot localXYPlot4 = (XYPlot)this.charts[6].getPlot();
      XYPlot localXYPlot5 = (XYPlot)this.charts[7].getPlot();
      localXYPlot1.getDomainAxis().setInverted(true);
      localXYPlot2.getRangeAxis().setInverted(true);
      ((XYPlot)localObject1).getDomainAxis().setInverted(true);
      ((XYPlot)localObject1).getRangeAxis().setInverted(true);
      localXYPlot3.getDomainAxis().setInverted(true);
      localXYPlot4.getRangeAxis().setInverted(true);
      ((XYPlot)localObject2).getDomainAxis().setInverted(true);
      ((XYPlot)localObject2).getRangeAxis().setInverted(true);
      ((XYPlot)localObject2).setOrientation(PlotOrientation.HORIZONTAL);
      localXYPlot3.setOrientation(PlotOrientation.HORIZONTAL);
      localXYPlot4.setOrientation(PlotOrientation.HORIZONTAL);
      localXYPlot5.setOrientation(PlotOrientation.HORIZONTAL);
      add(this.panels[0]);
      add(this.panels[1]);
      add(this.panels[4]);
      add(this.panels[5]);
      add(this.panels[2]);
      add(this.panels[3]);
      add(this.panels[6]);
      add(this.panels[7]);
      setPreferredSize(new Dimension(800, 600));
    }
  }
}