package demo;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Point;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.dial.ArcDialFrame;
import org.jfree.chart.plot.dial.DialBackground;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.DialPointer.Pin;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.StandardGradientPaintTransformer;

public class DialDemo3 extends JFrame
{
  public static JPanel createDemoPanel()
  {
    return new DemoPanel();
  }

  public DialDemo3(String paramString)
  {
    super(paramString);
    setDefaultCloseOperation(3);
    setContentPane(createDemoPanel());
  }

  public static void main(String[] paramArrayOfString)
  {
    DialDemo3 localDialDemo3 = new DialDemo3("JFreeChart: DialDemo3.java");
    localDialDemo3.pack();
    localDialDemo3.setVisible(true);
  }

  static class DemoPanel extends JPanel
    implements ChangeListener
  {
    JSlider slider;
    DefaultValueDataset dataset = new DefaultValueDataset(50.0D);

    public DemoPanel()
    {
      super(new BorderLayout());
      DialPlot localDialPlot = new DialPlot();
      localDialPlot.setView(0.21D, 0.0D, 0.58D, 0.3D);
      localDialPlot.setDataset(this.dataset);
      ArcDialFrame localArcDialFrame = new ArcDialFrame(60.0D, 60.0D);
      localArcDialFrame.setInnerRadius(0.6D);
      localArcDialFrame.setOuterRadius(0.9D);
      localArcDialFrame.setForegroundPaint(Color.darkGray);
      localArcDialFrame.setStroke(new BasicStroke(3.0F));
      localDialPlot.setDialFrame(localArcDialFrame);
      GradientPaint localGradientPaint = new GradientPaint(new Point(), new Color(255, 255, 255), new Point(), new Color(240, 240, 240));
      DialBackground localDialBackground = new DialBackground(localGradientPaint);
      localDialBackground.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.VERTICAL));
      localDialPlot.addLayer(localDialBackground);
      StandardDialScale localStandardDialScale = new StandardDialScale(0.0D, 100.0D, 115.0D, -50.0D, 10.0D, 4);
      localStandardDialScale.setTickRadius(0.88D);
      localStandardDialScale.setTickLabelOffset(0.07000000000000001D);
      localStandardDialScale.setMajorTickIncrement(25.0D);
      localDialPlot.addScale(0, localStandardDialScale);
      DialPointer.Pin localPin = new DialPointer.Pin();
      localPin.setRadius(0.82D);
      localDialPlot.addLayer(localPin);
      JFreeChart localJFreeChart = new JFreeChart(localDialPlot);
      localJFreeChart.setTitle("Dial Demo 3");
      ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
      localChartPanel.setPreferredSize(new Dimension(400, 250));
      this.slider = new JSlider(0, 100);
      this.slider.setMajorTickSpacing(10);
      this.slider.setPaintLabels(true);
      this.slider.addChangeListener(this);
      add(localChartPanel);
      add(this.slider, "South");
    }

    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      this.dataset.setValue(new Integer(this.slider.getValue()));
    }
  }
}