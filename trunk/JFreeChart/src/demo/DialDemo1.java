package demo;

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
import org.jfree.chart.plot.dial.DialBackground;
import org.jfree.chart.plot.dial.DialCap;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer.Pin;
import org.jfree.chart.plot.dial.DialPointer.Pointer;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.DialTextAnnotation;
import org.jfree.chart.plot.dial.DialValueIndicator;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialRange;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.ValueDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.StandardGradientPaintTransformer;

public class DialDemo1 extends JFrame
{
  public DialDemo1(String paramString)
  {
    super(paramString);
    setDefaultCloseOperation(3);
    setContentPane(createDemoPanel());
  }

  public static JPanel createDemoPanel()
  {
    return new DemoPanel();
  }

  public static void main(String[] paramArrayOfString)
  {
    DialDemo1 localDialDemo1 = new DialDemo1("JFreeChart: DialDemo1.java");
    localDialDemo1.pack();
    localDialDemo1.setVisible(true);
  }

  static class DemoPanel extends JPanel
    implements ChangeListener
  {
    JSlider slider;
    DefaultValueDataset dataset = new DefaultValueDataset(10.0D);

    public DemoPanel()
    {
      super(new BorderLayout());
      JFreeChart localJFreeChart = createStandardDialChart("Dial Demo 1", "Temperature", this.dataset, -40.0D, 60.0D, 10.0D, 4);
      DialPlot localDialPlot = (DialPlot)localJFreeChart.getPlot();
      StandardDialRange localStandardDialRange1 = new StandardDialRange(40.0D, 60.0D, Color.red);
      localStandardDialRange1.setInnerRadius(0.52D);
      localStandardDialRange1.setOuterRadius(0.55D);
      localDialPlot.addLayer(localStandardDialRange1);
      StandardDialRange localStandardDialRange2 = new StandardDialRange(10.0D, 40.0D, Color.orange);
      localStandardDialRange2.setInnerRadius(0.52D);
      localStandardDialRange2.setOuterRadius(0.55D);
      localDialPlot.addLayer(localStandardDialRange2);
      StandardDialRange localStandardDialRange3 = new StandardDialRange(-40.0D, 10.0D, Color.green);
      localStandardDialRange3.setInnerRadius(0.52D);
      localStandardDialRange3.setOuterRadius(0.55D);
      localDialPlot.addLayer(localStandardDialRange3);
      GradientPaint localGradientPaint = new GradientPaint(new Point(), new Color(255, 255, 255), new Point(), new Color(170, 170, 220));
      DialBackground localDialBackground = new DialBackground(localGradientPaint);
      localDialBackground.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.VERTICAL));
      localDialPlot.setBackground(localDialBackground);
      localDialPlot.removePointer(0);
      DialPointer.Pointer localPointer = new DialPointer.Pointer();
      localDialPlot.addPointer(localPointer);
      ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
      localChartPanel.setPreferredSize(new Dimension(400, 400));
      this.slider = new JSlider(-40, 60);
      this.slider.setMajorTickSpacing(10);
      this.slider.setPaintLabels(true);
      this.slider.addChangeListener(this);
      add(localChartPanel);
      add(this.slider, "South");
    }

    public static JFreeChart createStandardDialChart(String paramString1, String paramString2, ValueDataset paramValueDataset, double paramDouble1, double paramDouble2, double paramDouble3, int paramInt)
    {
      DialPlot localDialPlot = new DialPlot();
      localDialPlot.setDataset(paramValueDataset);
      localDialPlot.setDialFrame(new StandardDialFrame());
      localDialPlot.setBackground(new DialBackground());
      DialTextAnnotation localDialTextAnnotation = new DialTextAnnotation(paramString2);
      localDialTextAnnotation.setFont(new Font("Dialog", 1, 14));
      localDialTextAnnotation.setRadius(0.7D);
      localDialPlot.addLayer(localDialTextAnnotation);
      DialValueIndicator localDialValueIndicator = new DialValueIndicator(0);
      localDialPlot.addLayer(localDialValueIndicator);
      StandardDialScale localStandardDialScale = new StandardDialScale(paramDouble1, paramDouble2, -120.0D, -300.0D, 10.0D, 4);
      localStandardDialScale.setMajorTickIncrement(paramDouble3);
      localStandardDialScale.setMinorTickCount(paramInt);
      localStandardDialScale.setTickRadius(0.88D);
      localStandardDialScale.setTickLabelOffset(0.15D);
      localStandardDialScale.setTickLabelFont(new Font("Dialog", 0, 14));
      localDialPlot.addScale(0, localStandardDialScale);
      localDialPlot.addPointer(new DialPointer.Pin());
      DialCap localDialCap = new DialCap();
      localDialPlot.setCap(localDialCap);
      return new JFreeChart(paramString1, localDialPlot);
    }

    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      this.dataset.setValue(new Integer(this.slider.getValue()));
    }
  }
}