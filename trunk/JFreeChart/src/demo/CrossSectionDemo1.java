package demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYDataImageAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.GrayPaintScale;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.general.DefaultHeatMapDataset;
import org.jfree.data.general.HeatMapDataset;
import org.jfree.data.general.HeatMapUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RefineryUtilities;

public class CrossSectionDemo1 extends ApplicationFrame
{
  public CrossSectionDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    setContentPane(localJPanel);
  }

  private static HeatMapDataset createMapDataset()
  {
    DefaultHeatMapDataset localDefaultHeatMapDataset = new DefaultHeatMapDataset(501, 501, -250.0D, 250.0D, -250.0D, 250.0D);
    for (int i = 0; i < 501; ++i)
      for (int j = 0; j < 501; ++j)
        localDefaultHeatMapDataset.setZValue(i, j, Math.sin(Math.sqrt(i * j) / 10.0D));
    return localDefaultHeatMapDataset;
  }

  public static JPanel createDemoPanel()
  {
    return new MyDemoPanel();
  }

  public static void main(String[] paramArrayOfString)
  {
    CrossSectionDemo1 localCrossSectionDemo1 = new CrossSectionDemo1("JFreeChart: CrossSectionDemo1");
    localCrossSectionDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localCrossSectionDemo1);
    localCrossSectionDemo1.setVisible(true);
  }

  static class MyDemoPanel extends DemoPanel
    implements ChangeListener, ChartChangeListener
  {
    private HeatMapDataset dataset;
    private JFreeChart mainChart;
    private JFreeChart subchart1;
    private JFreeChart subchart2;
    private JSlider slider1;
    private JSlider slider2;
    private Crosshair crosshair1;
    private Crosshair crosshair2;
    private Range lastXRange;
    private Range lastYRange;

    public MyDemoPanel()
    {
      super(new BorderLayout());
      ChartPanel localChartPanel1 = (ChartPanel)createMainPanel();
      localChartPanel1.setPreferredSize(new Dimension(500, 270));
      CrosshairOverlay localCrosshairOverlay = new CrosshairOverlay();
      this.crosshair1 = new Crosshair(0.0D);
      this.crosshair1.setPaint(Color.red);
      this.crosshair2 = new Crosshair(0.0D);
      this.crosshair2.setPaint(Color.blue);
      localCrosshairOverlay.addDomainCrosshair(this.crosshair1);
      localCrosshairOverlay.addRangeCrosshair(this.crosshair2);
      localChartPanel1.addOverlay(localCrosshairOverlay);
      this.crosshair1.setLabelVisible(true);
      this.crosshair1.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
      this.crosshair1.setLabelBackgroundPaint(new Color(255, 255, 0, 100));
      this.crosshair2.setLabelVisible(true);
      this.crosshair2.setLabelBackgroundPaint(new Color(255, 255, 0, 100));
      add(localChartPanel1);
      JPanel localJPanel1 = new JPanel(new BorderLayout());
      XYSeriesCollection localXYSeriesCollection1 = new XYSeriesCollection();
      this.subchart1 = ChartFactory.createXYLineChart("Cross-section A", "Y", "Z", localXYSeriesCollection1, PlotOrientation.HORIZONTAL, false, false, false);
      XYPlot localXYPlot1 = (XYPlot)this.subchart1.getPlot();
      localXYPlot1.getDomainAxis().setLowerMargin(0.0D);
      localXYPlot1.getDomainAxis().setUpperMargin(0.0D);
      localXYPlot1.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
      ChartPanel localChartPanel2 = new ChartPanel(this.subchart1);
      localChartPanel2.setMinimumDrawWidth(0);
      localChartPanel2.setMinimumDrawHeight(0);
      localChartPanel2.setPreferredSize(new Dimension(200, 150));
      this.slider1 = new JSlider(-250, 250, 0);
      this.slider1.addChangeListener(this);
      this.slider1.setOrientation(1);
      localJPanel1.add(localChartPanel2);
      localJPanel1.add(this.slider1, "West");
      JPanel localJPanel2 = new JPanel(new BorderLayout());
      XYSeriesCollection localXYSeriesCollection2 = new XYSeriesCollection();
      this.subchart2 = ChartFactory.createXYLineChart("Cross-section B", "X", "Z", localXYSeriesCollection2, PlotOrientation.VERTICAL, false, false, false);
      XYPlot localXYPlot2 = (XYPlot)this.subchart2.getPlot();
      localXYPlot2.getDomainAxis().setLowerMargin(0.0D);
      localXYPlot2.getDomainAxis().setUpperMargin(0.0D);
      localXYPlot2.getRenderer().setSeriesPaint(0, Color.blue);
      ChartPanel localChartPanel3 = new ChartPanel(this.subchart2);
      localChartPanel3.setMinimumDrawWidth(0);
      localChartPanel3.setMinimumDrawHeight(0);
      localChartPanel3.setPreferredSize(new Dimension(200, 150));
      JPanel localJPanel3 = new JPanel();
      localJPanel3.setPreferredSize(new Dimension(200, 10));
      localJPanel2.add(localJPanel3, "East");
      this.slider2 = new JSlider(-250, 250, 0);
      this.slider2.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 200));
      this.slider2.addChangeListener(this);
      localJPanel2.add(localChartPanel3);
      localJPanel2.add(this.slider2, "North");
      add(localJPanel1, "East");
      add(localJPanel2, "South");
      this.mainChart.setNotify(true);
    }

    public JPanel createMainPanel()
    {
      this.mainChart = createChart(new XYSeriesCollection());
      this.mainChart.addChangeListener(this);
      ChartPanel localChartPanel = new ChartPanel(this.mainChart);
      localChartPanel.setFillZoomRectangle(true);
      localChartPanel.setMouseWheelEnabled(true);
      return localChartPanel;
    }

    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      int i;
      XYDataset localXYDataset;
      if (paramChangeEvent.getSource() == this.slider1)
      {
        this.crosshair2.setValue(this.slider1.getValue());
        i = this.slider1.getValue() - this.slider1.getMinimum();
        localXYDataset = HeatMapUtilities.extractColumnFromHeatMapDataset(this.dataset, i, "Y1");
        this.subchart2.getXYPlot().setDataset(localXYDataset);
      }
      else
      {
        if (paramChangeEvent.getSource() != this.slider2)
          return;
        this.crosshair1.setValue(this.slider2.getValue());
        i = this.slider2.getValue() - this.slider2.getMinimum();
        localXYDataset = HeatMapUtilities.extractRowFromHeatMapDataset(this.dataset, i, "Y2");
        this.subchart1.getXYPlot().setDataset(localXYDataset);
      }
    }

    public void chartChanged(ChartChangeEvent paramChartChangeEvent)
    {
      XYPlot localXYPlot1 = (XYPlot)this.mainChart.getPlot();
      if (!(localXYPlot1.getDomainAxis().getRange().equals(this.lastXRange)))
      {
        this.lastXRange = localXYPlot1.getDomainAxis().getRange();
        XYPlot localXYPlot2 = (XYPlot)this.subchart2.getPlot();
        localXYPlot2.getDomainAxis().setRange(this.lastXRange);
      }
      if (localXYPlot1.getRangeAxis().getRange().equals(this.lastYRange))
        return;
      this.lastYRange = localXYPlot1.getRangeAxis().getRange();
      XYPlot localXYPlot2 = (XYPlot)this.subchart1.getPlot();
      localXYPlot2.getDomainAxis().setRange(this.lastYRange);
    }

    private JFreeChart createChart(XYDataset paramXYDataset)
    {
      JFreeChart localJFreeChart = ChartFactory.createScatterPlot("CrossSectionDemo1", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, false, false);
      this.dataset = CrossSectionDemo1.createMapDataset();
      GrayPaintScale localGrayPaintScale = new GrayPaintScale(-1.0D, 1.0D, 128);
      BufferedImage localBufferedImage = HeatMapUtilities.createHeatMapImage(this.dataset, localGrayPaintScale);
      XYDataImageAnnotation localXYDataImageAnnotation = new XYDataImageAnnotation(localBufferedImage, -250.5D, -250.5D, 501.0D, 501.0D, true);
      XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
      localXYPlot.setDomainPannable(true);
      localXYPlot.setRangePannable(true);
      localXYPlot.getRenderer().addAnnotation(localXYDataImageAnnotation, Layer.BACKGROUND);
      NumberAxis localNumberAxis1 = (NumberAxis)localXYPlot.getDomainAxis();
      localNumberAxis1.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
      localNumberAxis1.setLowerMargin(0.0D);
      localNumberAxis1.setUpperMargin(0.0D);
      NumberAxis localNumberAxis2 = (NumberAxis)localXYPlot.getRangeAxis();
      localNumberAxis2.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
      localNumberAxis2.setLowerMargin(0.0D);
      localNumberAxis2.setUpperMargin(0.0D);
      return localJFreeChart;
    }
  }
}