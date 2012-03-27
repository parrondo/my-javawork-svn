package demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class TranslateDemo1 extends ApplicationFrame
{
  public TranslateDemo1(String paramString)
  {
    super(paramString);
    setContentPane(createDemoPanel());
  }

  public static JPanel createDemoPanel()
  {
    return new MyDemoPanel();
  }

  public static void main(String[] paramArrayOfString)
  {
    TranslateDemo1 localTranslateDemo1 = new TranslateDemo1("Translate Demo 1");
    localTranslateDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localTranslateDemo1);
    localTranslateDemo1.setVisible(true);
  }

  private static class MyDemoPanel extends DemoPanel
    implements ChangeListener
  {
    private TimeSeries series;
    private ChartPanel chartPanel;
    private JFreeChart chart = createChart();
    private JSlider slider;
    private TranslatingXYDataset dataset;

    public MyDemoPanel()
    {
      super(new BorderLayout());
      addChart(this.chart);
      this.chartPanel = new ChartPanel(this.chart);
      this.chartPanel.setPreferredSize(new Dimension(600, 270));
      this.chartPanel.setDomainZoomable(true);
      this.chartPanel.setRangeZoomable(true);
      CompoundBorder localCompoundBorder = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4), BorderFactory.createEtchedBorder());
      this.chartPanel.setBorder(localCompoundBorder);
      add(this.chartPanel);
      JPanel localJPanel = new JPanel(new BorderLayout());
      localJPanel.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));
      this.slider = new JSlider(-200, 200, 0);
      this.slider.setPaintLabels(true);
      this.slider.setMajorTickSpacing(50);
      this.slider.setPaintTicks(true);
      this.slider.addChangeListener(this);
      localJPanel.add(this.slider);
      add(localJPanel, "South");
    }

    private JFreeChart createChart()
    {
      XYDataset localXYDataset = createDataset("Random 1", 100.0D, new Minute(), 200);
      JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Translate Demo 1", "Time of Day", "Value", localXYDataset, true, true, false);
      XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
      localXYPlot.setOrientation(PlotOrientation.VERTICAL);
      localXYPlot.setDomainCrosshairVisible(true);
      localXYPlot.setDomainCrosshairLockedOnData(false);
      localXYPlot.setRangeCrosshairVisible(false);
      DateAxis localDateAxis = (DateAxis)localXYPlot.getDomainAxis();
      Range localRange = DatasetUtilities.findDomainBounds(this.dataset);
      localDateAxis.setRange(localRange);
      return localJFreeChart;
    }

    private XYDataset createDataset(String paramString, double paramDouble, RegularTimePeriod paramRegularTimePeriod, int paramInt)
    {
      this.series = new TimeSeries(paramString);
      RegularTimePeriod localRegularTimePeriod = paramRegularTimePeriod;
      double d = paramDouble;
      for (int i = 0; i < paramInt; ++i)
      {
        this.series.add(localRegularTimePeriod, d);
        localRegularTimePeriod = localRegularTimePeriod.next();
        d *= (1.0D + (Math.random() - 0.495D) / 10.0D);
      }
      TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
      localTimeSeriesCollection.addSeries(this.series);
      this.dataset = new TranslatingXYDataset(localTimeSeriesCollection);
      return this.dataset;
    }

    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      int i = this.slider.getValue();
      this.dataset.setTranslate(i * 60 * 1000.0D);
    }

    static class TranslatingXYDataset extends AbstractXYDataset
      implements XYDataset, DatasetChangeListener
    {
      private XYDataset underlying;
      private double translate;

      public TranslatingXYDataset(XYDataset paramXYDataset)
      {
        this.underlying = paramXYDataset;
        this.underlying.addChangeListener(this);
        this.translate = 0.0D;
      }

      public double getTranslate()
      {
        return this.translate;
      }

      public void setTranslate(double paramDouble)
      {
        this.translate = paramDouble;
        fireDatasetChanged();
      }

      public int getItemCount(int paramInt)
      {
        return this.underlying.getItemCount(paramInt);
      }

      public double getXValue(int paramInt1, int paramInt2)
      {
        return (this.underlying.getXValue(paramInt1, paramInt2) + this.translate);
      }

      public Number getX(int paramInt1, int paramInt2)
      {
        return new Double(getXValue(paramInt1, paramInt2));
      }

      public Number getY(int paramInt1, int paramInt2)
      {
        return new Double(getYValue(paramInt1, paramInt2));
      }

      public double getYValue(int paramInt1, int paramInt2)
      {
        return this.underlying.getYValue(paramInt1, paramInt2);
      }

      public int getSeriesCount()
      {
        return this.underlying.getSeriesCount();
      }

      public Comparable getSeriesKey(int paramInt)
      {
        return this.underlying.getSeriesKey(paramInt);
      }

      public void datasetChanged(DatasetChangeEvent paramDatasetChangeEvent)
      {
        fireDatasetChanged();
      }
    }
  }
}