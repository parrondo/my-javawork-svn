package demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
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
import org.jfree.chart.plot.dial.DialPointer.Pointer;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.StandardGradientPaintTransformer;

public class DialDemo5 extends JFrame
{
  public DialDemo5(String paramString)
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
    DialDemo5 localDialDemo5 = new DialDemo5("JFreeChart: DialDemo5.java");
    localDialDemo5.pack();
    localDialDemo5.setVisible(true);
  }

  static class DemoPanel extends JPanel
    implements ChangeListener
  {
    DefaultValueDataset hoursDataset = new DefaultValueDataset(6.0D);
    DefaultValueDataset dataset2 = new DefaultValueDataset(15.0D);
    JSlider slider1;
    JSlider slider2;

    public DemoPanel()
    {
      super(new BorderLayout());
      DialPlot localDialPlot = new DialPlot();
      localDialPlot.setView(0.0D, 0.0D, 1.0D, 1.0D);
      localDialPlot.setDataset(0, this.hoursDataset);
      localDialPlot.setDataset(1, this.dataset2);
      StandardDialFrame localStandardDialFrame = new StandardDialFrame();
      localStandardDialFrame.setBackgroundPaint(Color.lightGray);
      localStandardDialFrame.setForegroundPaint(Color.darkGray);
      localDialPlot.setDialFrame(localStandardDialFrame);
      DialBackground localDialBackground = new DialBackground(Color.white);
      localDialBackground.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.VERTICAL));
      localDialPlot.setBackground(localDialBackground);
      StandardDialScale localStandardDialScale1 = new StandardDialScale(0.0D, 12.0D, 90.0D, -360.0D, 10.0D, 4);
      localStandardDialScale1.setFirstTickLabelVisible(false);
      localStandardDialScale1.setMajorTickIncrement(1.0D);
      localStandardDialScale1.setTickRadius(0.88D);
      localStandardDialScale1.setTickLabelOffset(0.15D);
      localStandardDialScale1.setTickLabelFont(new Font("Dialog", 0, 14));
      localDialPlot.addScale(0, localStandardDialScale1);
      StandardDialScale localStandardDialScale2 = new StandardDialScale(0.0D, 60.0D, 90.0D, -360.0D, 10.0D, 4);
      localStandardDialScale2.setVisible(false);
      localStandardDialScale2.setMajorTickIncrement(5.0D);
      localStandardDialScale2.setTickRadius(0.6800000000000001D);
      localStandardDialScale2.setTickLabelOffset(0.15D);
      localStandardDialScale2.setTickLabelFont(new Font("Dialog", 0, 14));
      localDialPlot.addScale(1, localStandardDialScale2);
      DialPointer.Pointer localPointer1 = new DialPointer.Pointer(0);
      localPointer1.setRadius(0.55D);
      localDialPlot.addLayer(localPointer1);
      localDialPlot.mapDatasetToScale(1, 1);
      DialPointer.Pointer localPointer2 = new DialPointer.Pointer(1);
      localDialPlot.addLayer(localPointer2);
      DialCap localDialCap = new DialCap();
      localDialCap.setRadius(0.1D);
      localDialPlot.setCap(localDialCap);
      JFreeChart localJFreeChart = new JFreeChart(localDialPlot);
      localJFreeChart.setTitle("Dial Demo 5");
      ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
      localChartPanel.setPreferredSize(new Dimension(400, 400));
      JPanel localJPanel = new JPanel(new GridLayout(2, 2));
      localJPanel.add(new JLabel("Hours:"));
      localJPanel.add(new JLabel("Minutes:"));
      this.slider1 = new JSlider(0, 12);
      this.slider1.setMajorTickSpacing(2);
      this.slider1.setPaintTicks(true);
      this.slider1.setPaintLabels(true);
      this.slider1.addChangeListener(this);
      localJPanel.add(this.slider1);
      localJPanel.add(this.slider1);
      this.slider2 = new JSlider(0, 60);
      this.slider2.setValue(15);
      this.slider2.setMajorTickSpacing(10);
      this.slider2.setPaintTicks(true);
      this.slider2.setPaintLabels(true);
      this.slider2.addChangeListener(this);
      localJPanel.add(this.slider2);
      add(localChartPanel);
      add(localJPanel, "South");
    }

    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      this.hoursDataset.setValue(new Integer(this.slider1.getValue()));
      this.dataset2.setValue(new Integer(this.slider2.getValue()));
    }
  }
}