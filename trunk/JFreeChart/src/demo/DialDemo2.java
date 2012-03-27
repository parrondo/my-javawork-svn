package demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.GridLayout;
import java.awt.Point;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.dial.DialBackground;
import org.jfree.chart.plot.dial.DialCap;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.DialPointer.Pin;
import org.jfree.chart.plot.dial.DialPointer.Pointer;
import org.jfree.chart.plot.dial.DialTextAnnotation;
import org.jfree.chart.plot.dial.DialValueIndicator;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialRange;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.StandardGradientPaintTransformer;

public class DialDemo2 extends JFrame
{
  public DialDemo2(String paramString)
  {
    super(paramString);
    setDefaultCloseOperation(3);
    setContentPane(createDemoPanel());
  }

  public static JPanel createDemoPanel()
  {
    return new MyDemoPanel();
  }

  public static void main(String[] paramArrayOfString)
  {
    DialDemo2 localDialDemo2 = new DialDemo2("JFreeChart: DialDemo2.java");
    localDialDemo2.pack();
    localDialDemo2.setVisible(true);
  }

  static class MyDemoPanel extends DemoPanel
    implements ChangeListener
  {
    DefaultValueDataset dataset1 = new DefaultValueDataset(10.0D);
    DefaultValueDataset dataset2 = new DefaultValueDataset(50.0D);
    JSlider slider1;
    JSlider slider2;

    public MyDemoPanel()
    {
      super(new BorderLayout());
      DialPlot localDialPlot = new DialPlot();
      localDialPlot.setView(0.0D, 0.0D, 1.0D, 1.0D);
      localDialPlot.setDataset(0, this.dataset1);
      localDialPlot.setDataset(1, this.dataset2);
      StandardDialFrame localStandardDialFrame = new StandardDialFrame();
      localStandardDialFrame.setBackgroundPaint(Color.lightGray);
      localStandardDialFrame.setForegroundPaint(Color.darkGray);
      localDialPlot.setDialFrame(localStandardDialFrame);
      GradientPaint localGradientPaint = new GradientPaint(new Point(), new Color(255, 255, 255), new Point(), new Color(170, 170, 220));
      DialBackground localDialBackground = new DialBackground(localGradientPaint);
      localDialBackground.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.VERTICAL));
      localDialPlot.setBackground(localDialBackground);
      DialTextAnnotation localDialTextAnnotation = new DialTextAnnotation("Temperature");
      localDialTextAnnotation.setFont(new Font("Dialog", 1, 14));
      localDialTextAnnotation.setRadius(0.7D);
      localDialPlot.addLayer(localDialTextAnnotation);
      DialValueIndicator localDialValueIndicator1 = new DialValueIndicator(0);
      localDialValueIndicator1.setFont(new Font("Dialog", 0, 10));
      localDialValueIndicator1.setOutlinePaint(Color.darkGray);
      localDialValueIndicator1.setRadius(0.6D);
      localDialValueIndicator1.setAngle(-103.0D);
      localDialPlot.addLayer(localDialValueIndicator1);
      DialValueIndicator localDialValueIndicator2 = new DialValueIndicator(1);
      localDialValueIndicator2.setFont(new Font("Dialog", 0, 10));
      localDialValueIndicator2.setOutlinePaint(Color.red);
      localDialValueIndicator2.setRadius(0.6D);
      localDialValueIndicator2.setAngle(-77.0D);
      localDialPlot.addLayer(localDialValueIndicator2);
      StandardDialScale localStandardDialScale1 = new StandardDialScale(-40.0D, 60.0D, -120.0D, -300.0D, 10.0D, 4);
      localStandardDialScale1.setTickRadius(0.88D);
      localStandardDialScale1.setTickLabelOffset(0.15D);
      localStandardDialScale1.setTickLabelFont(new Font("Dialog", 0, 14));
      localDialPlot.addScale(0, localStandardDialScale1);
      StandardDialScale localStandardDialScale2 = new StandardDialScale(0.0D, 100.0D, -120.0D, -300.0D, 10.0D, 4);
      localStandardDialScale2.setTickRadius(0.5D);
      localStandardDialScale2.setTickLabelOffset(0.15D);
      localStandardDialScale2.setTickLabelFont(new Font("Dialog", 0, 10));
      localStandardDialScale2.setMajorTickPaint(Color.red);
      localStandardDialScale2.setMinorTickPaint(Color.red);
      localDialPlot.addScale(1, localStandardDialScale2);
      localDialPlot.mapDatasetToScale(1, 1);
      StandardDialRange localStandardDialRange = new StandardDialRange(90.0D, 100.0D, Color.blue);
      localStandardDialRange.setScaleIndex(1);
      localStandardDialRange.setInnerRadius(0.59D);
      localStandardDialRange.setOuterRadius(0.59D);
      localDialPlot.addLayer(localStandardDialRange);
      DialPointer.Pin localPin = new DialPointer.Pin(1);
      localPin.setRadius(0.55D);
      localDialPlot.addPointer(localPin);
      DialPointer.Pointer localPointer = new DialPointer.Pointer(0);
      localDialPlot.addPointer(localPointer);
      DialCap localDialCap = new DialCap();
      localDialCap.setRadius(0.1D);
      localDialPlot.setCap(localDialCap);
      JFreeChart localJFreeChart = new JFreeChart(localDialPlot);
      localJFreeChart.setTitle("Dial Demo 2");
      ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
      localChartPanel.setPreferredSize(new Dimension(400, 400));
      addChart(localJFreeChart);
      JPanel localJPanel = new JPanel(new GridLayout(2, 2));
      localJPanel.add(new JLabel("Outer Needle:"));
      localJPanel.add(new JLabel("Inner Needle:"));
      this.slider1 = new JSlider(-40, 60);
      this.slider1.setMajorTickSpacing(20);
      this.slider1.setPaintTicks(true);
      this.slider1.setPaintLabels(true);
      this.slider1.addChangeListener(this);
      localJPanel.add(this.slider1);
      localJPanel.add(this.slider1);
      this.slider2 = new JSlider(0, 100);
      this.slider2.setMajorTickSpacing(20);
      this.slider2.setPaintTicks(true);
      this.slider2.setPaintLabels(true);
      this.slider2.addChangeListener(this);
      localJPanel.add(this.slider2);
      add(localChartPanel);
      add(localJPanel, "South");
    }

    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      this.dataset1.setValue(new Integer(this.slider1.getValue()));
      this.dataset2.setValue(new Integer(this.slider2.getValue()));
    }
  }
}