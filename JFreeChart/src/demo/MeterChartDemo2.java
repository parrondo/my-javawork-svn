package demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.MeterInterval;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.data.Range;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.ValueDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class MeterChartDemo2 extends ApplicationFrame
{
  private static DefaultValueDataset dataset;

  public MeterChartDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(ValueDataset paramValueDataset)
  {
    MeterPlot localMeterPlot = new MeterPlot(paramValueDataset);
    localMeterPlot.addInterval(new MeterInterval("High", new Range(80.0D, 100.0D)));
    localMeterPlot.setDialOutlinePaint(Color.white);
    JFreeChart localJFreeChart = new JFreeChart("Meter Chart 2", JFreeChart.DEFAULT_TITLE_FONT, localMeterPlot, false);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    dataset = new DefaultValueDataset(50.0D);
    JFreeChart localJFreeChart = createChart(dataset);
    JPanel localJPanel = new JPanel(new BorderLayout());
    JSlider localJSlider = new JSlider(-10, 110, 50);
    localJSlider.setMajorTickSpacing(10);
    localJSlider.setMinorTickSpacing(5);
    localJSlider.setPaintLabels(true);
    localJSlider.setPaintTicks(true);
    localJSlider.addChangeListener(new ChangeListener()
    {
      public void stateChanged(ChangeEvent paramChangeEvent)
      {
        JSlider localJSlider = (JSlider)paramChangeEvent.getSource();
        MeterChartDemo2.dataset.setValue(new Integer(localJSlider.getValue()));
      }
    });
    localJPanel.add(new ChartPanel(localJFreeChart));
    localJPanel.add("South", localJSlider);
    return localJPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    MeterChartDemo2 localMeterChartDemo2 = new MeterChartDemo2("JFreeChart: MeterChartDemo2.java");
    localMeterChartDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localMeterChartDemo2);
    localMeterChartDemo2.setVisible(true);
  }
}