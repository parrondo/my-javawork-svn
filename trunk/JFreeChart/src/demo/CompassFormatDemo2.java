package demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CompassFormat;
import org.jfree.chart.axis.ModuloAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class CompassFormatDemo2 extends ApplicationFrame
{
  public CompassFormatDemo2(String paramString)
  {
    super(paramString);
    setContentPane(new MyDemoPanel());
  }

  public static JPanel createDemoPanel()
  {
    return new MyDemoPanel();
  }

  public static void main(String[] paramArrayOfString)
  {
    CompassFormatDemo2 localCompassFormatDemo2 = new CompassFormatDemo2("JFreeChart: CompassFormatDemo2.java");
    localCompassFormatDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localCompassFormatDemo2);
    localCompassFormatDemo2.setVisible(true);
  }

  private static class MyDemoPanel extends DemoPanel
    implements ChangeListener
  {
    private JSlider directionSlider;
    private JSlider fieldSlider;
    private ModuloAxis rangeAxis;
    private double direction = 0.0D;
    private double degrees = 45.0D;

    public MyDemoPanel()
    {
      super(new BorderLayout());
      JPanel localJPanel = new JPanel(new GridLayout(1, 2));
      this.fieldSlider = new JSlider(1, 10, 180, 45);
      this.fieldSlider.setPaintLabels(true);
      this.fieldSlider.setPaintTicks(true);
      this.fieldSlider.setMajorTickSpacing(10);
      this.fieldSlider.setMinorTickSpacing(5);
      this.fieldSlider.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.fieldSlider.addChangeListener(this);
      this.directionSlider = new JSlider(1, 0, 360, 0);
      this.directionSlider.setMajorTickSpacing(30);
      this.directionSlider.setMinorTickSpacing(5);
      this.directionSlider.setPaintLabels(true);
      this.directionSlider.setPaintTicks(true);
      this.directionSlider.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.directionSlider.setPaintTrack(true);
      this.directionSlider.addChangeListener(this);
      localJPanel.add(this.fieldSlider);
      localJPanel.add(this.directionSlider);
      JFreeChart localJFreeChart = createChart();
      addChart(localJFreeChart);
      ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
      localChartPanel.setPreferredSize(new Dimension(500, 270));
      add(localJPanel, "West");
      add(localChartPanel);
    }

    private XYDataset createDirectionDataset(int paramInt)
    {
      TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
      TimeSeries localTimeSeries = new TimeSeries("Wind Direction");
      Object localObject = new Minute();
      double d = 0.0D;
      for (int i = 0; i < paramInt; ++i)
      {
        localTimeSeries.add((RegularTimePeriod)localObject, d);
        localObject = ((RegularTimePeriod)localObject).next();
        d += (Math.random() - 0.5D) * 15.0D;
        if (d < 0.0D)
        {
          d += 360.0D;
        }
        else
        {
          if (d <= 360.0D)
            continue;
          d -= 360.0D;
        }
      }
      localTimeSeriesCollection.addSeries(localTimeSeries);
      return ((XYDataset)localTimeSeriesCollection);
    }

    private XYDataset createForceDataset(int paramInt)
    {
      TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
      TimeSeries localTimeSeries = new TimeSeries("Wind Force");
      Object localObject = new Minute();
      double d = 3.0D;
      for (int i = 0; i < paramInt; ++i)
      {
        localTimeSeries.add((RegularTimePeriod)localObject, d);
        localObject = ((RegularTimePeriod)localObject).next();
        d = Math.max(0.5D, d + (Math.random() - 0.5D) * 0.5D);
      }
      localTimeSeriesCollection.addSeries(localTimeSeries);
      return ((XYDataset)localTimeSeriesCollection);
    }

    private JFreeChart createChart()
    {
      XYDataset localXYDataset = createDirectionDataset(100);
      JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Time", "Date", "Direction", localXYDataset, true, true, false);
      XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
      localXYPlot.getDomainAxis().setLowerMargin(0.0D);
      localXYPlot.getDomainAxis().setUpperMargin(0.0D);
      this.rangeAxis = new ModuloAxis("Direction", new Range(0.0D, 360.0D));
      TickUnits localTickUnits = new TickUnits();
      localTickUnits.add(new NumberTickUnit(180.0D, new CompassFormat()));
      localTickUnits.add(new NumberTickUnit(90.0D, new CompassFormat()));
      localTickUnits.add(new NumberTickUnit(45.0D, new CompassFormat()));
      localTickUnits.add(new NumberTickUnit(22.5D, new CompassFormat()));
      this.rangeAxis.setStandardTickUnits(localTickUnits);
      XYLineAndShapeRenderer localXYLineAndShapeRenderer = new XYLineAndShapeRenderer();
      localXYLineAndShapeRenderer.setBaseLinesVisible(false);
      localXYLineAndShapeRenderer.setBaseShapesVisible(true);
      localXYPlot.setRenderer(localXYLineAndShapeRenderer);
      localXYPlot.setRangeAxis(this.rangeAxis);
      this.rangeAxis.setDisplayRange(-45.0D, 45.0D);
      XYAreaRenderer localXYAreaRenderer = new XYAreaRenderer();
      NumberAxis localNumberAxis = new NumberAxis("Force");
      localNumberAxis.setRange(0.0D, 12.0D);
      localXYAreaRenderer.setSeriesPaint(0, new Color(0, 0, 255, 128));
      localXYPlot.setDataset(1, createForceDataset(100));
      localXYPlot.setRenderer(1, localXYAreaRenderer);
      localXYPlot.setRangeAxis(1, localNumberAxis);
      localXYPlot.mapDatasetToRangeAxis(1, 1);
      ChartUtilities.applyCurrentTheme(localJFreeChart);
      return localJFreeChart;
    }

    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      if (paramChangeEvent.getSource() == this.directionSlider)
      {
        this.direction = this.directionSlider.getValue();
        this.rangeAxis.setDisplayRange(this.direction - this.degrees, this.direction + this.degrees);
      }
      else
      {
        if (paramChangeEvent.getSource() != this.fieldSlider)
          return;
        this.degrees = this.fieldSlider.getValue();
        this.rangeAxis.setDisplayRange(this.direction - this.degrees, this.direction + this.degrees);
      }
    }
  }
}