package demo;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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

public class DialDemo4 extends JFrame
{
  public static JPanel createDemoPanel()
  {
    return new DemoPanel();
  }

  public DialDemo4(String paramString)
  {
    super(paramString);
    setDefaultCloseOperation(3);
    setContentPane(createDemoPanel());
  }

  public static void main(String[] paramArrayOfString)
  {
    DialDemo4 localDialDemo4 = new DialDemo4("JFreeChart: DialDemo4.java");
    localDialDemo4.pack();
    localDialDemo4.setVisible(true);
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
      localDialPlot.setView(0.78D, 0.37D, 0.22D, 0.26D);
      localDialPlot.setDataset(this.dataset);
      ArcDialFrame localArcDialFrame = new ArcDialFrame(-10.0D, 20.0D);
      localArcDialFrame.setInnerRadius(0.7D);
      localArcDialFrame.setOuterRadius(0.9D);
      localArcDialFrame.setForegroundPaint(Color.darkGray);
      localArcDialFrame.setStroke(new BasicStroke(3.0F));
      localDialPlot.setDialFrame(localArcDialFrame);
      GradientPaint localGradientPaint = new GradientPaint(new Point(), new Color(255, 255, 255), new Point(), new Color(240, 240, 240));
      DialBackground localDialBackground = new DialBackground(localGradientPaint);
      localDialBackground.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.VERTICAL));
      localDialPlot.addLayer(localDialBackground);
      StandardDialScale localStandardDialScale = new StandardDialScale(0.0D, 100.0D, -8.0D, 16.0D, 10.0D, 4);
      localStandardDialScale.setTickRadius(0.82D);
      localStandardDialScale.setTickLabelOffset(-0.04D);
      localStandardDialScale.setMajorTickIncrement(25.0D);
      localStandardDialScale.setTickLabelFont(new Font("Dialog", 0, 14));
      localDialPlot.addScale(0, localStandardDialScale);
      DialPointer.Pin localPin = new DialPointer.Pin();
      localPin.setRadius(0.84D);
      localDialPlot.addLayer(localPin);
      JFreeChart localJFreeChart = new JFreeChart(localDialPlot);
      localJFreeChart.setTitle("Dial Demo 4");
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