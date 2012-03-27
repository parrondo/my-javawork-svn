package demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class DynamicDataDemo2 extends ApplicationFrame
{
  public DynamicDataDemo2(String paramString)
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
    DynamicDataDemo2 localDynamicDataDemo2 = new DynamicDataDemo2("JFreeChart: DynamicDataDemo2.java");
    localDynamicDataDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localDynamicDataDemo2);
    localDynamicDataDemo2.setVisible(true);
  }

  static class MyDemoPanel extends DemoPanel
    implements ActionListener
  {
    private TimeSeries series1 = new TimeSeries("Random 1");
    private TimeSeries series2 = new TimeSeries("Random 2");
    private double lastValue1 = 100.0D;
    private double lastValue2 = 500.0D;

    public MyDemoPanel()
    {
      super(new BorderLayout());
      TimeSeriesCollection localTimeSeriesCollection1 = new TimeSeriesCollection(this.series1);
      TimeSeriesCollection localTimeSeriesCollection2 = new TimeSeriesCollection(this.series2);
      JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Dynamic Data Demo 2", "Time", "Value", localTimeSeriesCollection1, true, true, false);
      addChart(localJFreeChart);
      XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
      ValueAxis localValueAxis = localXYPlot.getDomainAxis();
      localValueAxis.setAutoRange(true);
      localValueAxis.setFixedAutoRange(10000.0D);
      localXYPlot.setDataset(1, localTimeSeriesCollection2);
      NumberAxis localNumberAxis = new NumberAxis("Range Axis 2");
      localNumberAxis.setAutoRangeIncludesZero(false);
      localXYPlot.setRenderer(1, new DefaultXYItemRenderer());
      localXYPlot.setRangeAxis(1, localNumberAxis);
      localXYPlot.mapDatasetToRangeAxis(1, 1);
      ChartUtilities.applyCurrentTheme(localJFreeChart);
      ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
      add(localChartPanel);
      JButton localJButton1 = new JButton("Add To Series 1");
      localJButton1.setActionCommand("ADD_DATA_1");
      localJButton1.addActionListener(this);
      JButton localJButton2 = new JButton("Add To Series 2");
      localJButton2.setActionCommand("ADD_DATA_2");
      localJButton2.addActionListener(this);
      JButton localJButton3 = new JButton("Add To Both");
      localJButton3.setActionCommand("ADD_BOTH");
      localJButton3.addActionListener(this);
      JPanel localJPanel = new JPanel(new FlowLayout());
      localJPanel.setBackground(Color.white);
      localJPanel.add(localJButton1);
      localJPanel.add(localJButton2);
      localJPanel.add(localJButton3);
      add(localJPanel, "South");
      localChartPanel.setPreferredSize(new Dimension(500, 270));
    }

    public void actionPerformed(ActionEvent paramActionEvent)
    {
      int i = 0;
      int j = 0;
      if (paramActionEvent.getActionCommand().equals("ADD_DATA_1"))
      {
        i = 1;
      }
      else if (paramActionEvent.getActionCommand().equals("ADD_DATA_2"))
      {
        j = 1;
      }
      else if (paramActionEvent.getActionCommand().equals("ADD_BOTH"))
      {
        i = 1;
        j = 1;
      }
      if (i != 0)
      {
        double d = 0.9D + 0.2D * Math.random();
        this.lastValue1 *= d;
        Millisecond localMillisecond = new Millisecond();
        System.out.println("Now = " + localMillisecond.toString());
        this.series1.add(new Millisecond(), this.lastValue1);
      }
      if (j == 0)
        return;
      double d = 0.9D + 0.2D * Math.random();
      this.lastValue2 *= d;
      Millisecond localMillisecond = new Millisecond();
      System.out.println("Now = " + localMillisecond.toString());
      this.series2.add(new Millisecond(), this.lastValue2);
    }
  }
}