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
import org.jfree.chart.annotations.XYDataImageAnnotation;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.annotations.XYShapeAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.about.ProjectInfo;

public class PlotOrientationDemo1 extends ApplicationFrame
{
  private static int CHART_COUNT = 8;

  public PlotOrientationDemo1(String paramString)
  {
    super(paramString);
    setContentPane(createDemoPanel());
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
    PlotOrientationDemo1 localPlotOrientationDemo1 = new PlotOrientationDemo1("JFreeChart: PlotOrientationDemo1.java");
    localPlotOrientationDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localPlotOrientationDemo1);
    localPlotOrientationDemo1.setVisible(true);
  }

  static class MyDemoPanel extends DemoPanel
  {
    private XYDataset[] datasets = new XYDataset[PlotOrientationDemo1.CHART_COUNT];
    private JFreeChart[] charts = new JFreeChart[PlotOrientationDemo1.CHART_COUNT];
    private ChartPanel[] panels = new ChartPanel[PlotOrientationDemo1.CHART_COUNT];

    public MyDemoPanel()
    {
      super(new GridLayout(2, 4));
      for (int i = 0; i < PlotOrientationDemo1.CHART_COUNT; ++i)
      {
        this.datasets[i] = PlotOrientationDemo1.createDataset(i);
        this.charts[i] = PlotOrientationDemo1.createChart(i, this.datasets[i]);
        XYPlot localXYPlot2 = (XYPlot)this.charts[i].getPlot();
        localXYPlot2.setDomainPannable(true);
        localXYPlot2.setRangePannable(true);
        XYShapeAnnotation localObject1 = new XYShapeAnnotation(new Rectangle2D.Double(-2.0D, -3.0D, 1.0D, 4.0D), new BasicStroke(1.0F), Color.blue, Color.yellow);
        XYLineAnnotation localObject2 = new XYLineAnnotation(0.0D, -5.0D, 10.0D, -5.0D);
        XYDataImageAnnotation localObject3 = new XYDataImageAnnotation(JFreeChart.INFO.getLogo(), 5.0D, 2.0D, 6.0D, 4.0D, true);
        localXYPlot2.addAnnotation((XYAnnotation)localObject1);
        localXYPlot2.addAnnotation((XYAnnotation)localObject2);
        localXYPlot2.addAnnotation((XYAnnotation)localObject3);
        localXYPlot2.setQuadrantPaint(0, new Color(230, 230, 255));
        localXYPlot2.setQuadrantPaint(1, new Color(230, 255, 230));
        localXYPlot2.setQuadrantPaint(2, new Color(255, 230, 230));
        localXYPlot2.setQuadrantPaint(3, new Color(255, 230, 255));
        addChart(this.charts[i]);
        this.panels[i] = new ChartPanel(this.charts[i]);
        this.panels[i].setMouseWheelEnabled(true);
      }
      XYPlot localXYPlot1 = (XYPlot)this.charts[1].getPlot();
      XYPlot localXYPlot2 = (XYPlot)this.charts[2].getPlot();
      Object localObject1 = (XYPlot)this.charts[3].getPlot();
      Object localObject2 = (XYPlot)this.charts[4].getPlot();
      Object localObject3 = (XYPlot)this.charts[5].getPlot();
      XYPlot localXYPlot3 = (XYPlot)this.charts[6].getPlot();
      XYPlot localXYPlot4 = (XYPlot)this.charts[7].getPlot();
      localXYPlot1.getDomainAxis().setInverted(true);
      localXYPlot2.getRangeAxis().setInverted(true);
      ((XYPlot)localObject1).getDomainAxis().setInverted(true);
      ((XYPlot)localObject1).getRangeAxis().setInverted(true);
      ((XYPlot)localObject3).getDomainAxis().setInverted(true);
      localXYPlot3.getRangeAxis().setInverted(true);
      ((XYPlot)localObject2).getDomainAxis().setInverted(true);
      ((XYPlot)localObject2).getRangeAxis().setInverted(true);
      ((XYPlot)localObject2).setOrientation(PlotOrientation.HORIZONTAL);
      ((XYPlot)localObject3).setOrientation(PlotOrientation.HORIZONTAL);
      localXYPlot3.setOrientation(PlotOrientation.HORIZONTAL);
      localXYPlot4.setOrientation(PlotOrientation.HORIZONTAL);
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