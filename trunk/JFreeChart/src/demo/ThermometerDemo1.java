package demo;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ThermometerPlot;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.ValueDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;

public class ThermometerDemo1 extends ApplicationFrame
{
  public ThermometerDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    setContentPane(localJPanel);
  }

  public static JPanel createDemoPanel()
  {
    return new ContentPanel();
  }

  public static void main(String[] paramArrayOfString)
  {
    ThermometerDemo1 localThermometerDemo1 = new ThermometerDemo1("Thermometer Demo 1");
    localThermometerDemo1.pack();
    localThermometerDemo1.setVisible(true);
  }

  static class ContentPanel extends DemoPanel
    implements ChangeListener
  {
    JSlider slider = new JSlider(0, 200, 100);
    DefaultValueDataset dataset;

    public ContentPanel()
    {
      super(new BorderLayout());
      this.slider.setPaintLabels(true);
      this.slider.setPaintTicks(true);
      this.slider.setMajorTickSpacing(25);
      this.slider.addChangeListener(this);
      add(this.slider, "South");
      this.dataset = new DefaultValueDataset(this.slider.getValue());
      JFreeChart localJFreeChart = createChart(this.dataset);
      addChart(localJFreeChart);
      add(new ChartPanel(localJFreeChart));
    }

    private static JFreeChart createChart(ValueDataset paramValueDataset)
    {
      ThermometerPlot localThermometerPlot = new ThermometerPlot(paramValueDataset);
      JFreeChart localJFreeChart = new JFreeChart("Thermometer Demo 1", JFreeChart.DEFAULT_TITLE_FONT, localThermometerPlot, true);
      localThermometerPlot.setInsets(new RectangleInsets(5.0D, 5.0D, 5.0D, 5.0D));
      localThermometerPlot.setPadding(new RectangleInsets(10.0D, 10.0D, 10.0D, 10.0D));
      localThermometerPlot.setThermometerStroke(new BasicStroke(2.0F));
      localThermometerPlot.setThermometerPaint(Color.lightGray);
      localThermometerPlot.setUnits(1);
      localThermometerPlot.setGap(3);
      localThermometerPlot.setRange(0.0D, 200.0D);
      localThermometerPlot.setSubrange(0, 0.0D, 85.0D);
      localThermometerPlot.setSubrangePaint(0, Color.red);
      localThermometerPlot.setSubrange(1, 85.0D, 125.0D);
      localThermometerPlot.setSubrangePaint(1, Color.green);
      localThermometerPlot.setSubrange(2, 125.0D, 200.0D);
      localThermometerPlot.setSubrangePaint(2, Color.red);
      ChartUtilities.applyCurrentTheme(localJFreeChart);
      return localJFreeChart;
    }

    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      this.dataset.setValue(new Integer(this.slider.getValue()));
    }
  }
}