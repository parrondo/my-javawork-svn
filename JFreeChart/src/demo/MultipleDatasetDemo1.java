package demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class MultipleDatasetDemo1 extends ApplicationFrame
{
  public MultipleDatasetDemo1(String paramString)
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
    MultipleDatasetDemo1 localMultipleDatasetDemo1 = new MultipleDatasetDemo1("JFreeChart: MultipleDatasetDemo1.java");
    localMultipleDatasetDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localMultipleDatasetDemo1);
    localMultipleDatasetDemo1.setVisible(true);
  }

  static class MyDemoPanel extends DemoPanel
    implements ActionListener
  {
    private XYPlot plot;
    private int datasetIndex = 0;

    public MyDemoPanel()
    {
      super(new BorderLayout());
      TimeSeriesCollection localTimeSeriesCollection = createRandomDataset("Series 1");
      JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Multiple Dataset Demo 1", "Time", "Value", localTimeSeriesCollection, true, true, false);
      localJFreeChart.setBackgroundPaint(null);
      addChart(localJFreeChart);
      this.plot = ((XYPlot)localJFreeChart.getPlot());
      ValueAxis localValueAxis = this.plot.getDomainAxis();
      localValueAxis.setAutoRange(true);
      NumberAxis localNumberAxis = new NumberAxis("Range Axis 2");
      localNumberAxis.setAutoRangeIncludesZero(false);
      ChartUtilities.applyCurrentTheme(localJFreeChart);
      JPanel localJPanel1 = new JPanel(new BorderLayout());
      ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
      localJPanel1.add(localChartPanel);
      JButton localJButton1 = new JButton("Add Dataset");
      localJButton1.setActionCommand("ADD_DATASET");
      localJButton1.addActionListener(this);
      JButton localJButton2 = new JButton("Remove Dataset");
      localJButton2.setActionCommand("REMOVE_DATASET");
      localJButton2.addActionListener(this);
      JPanel localJPanel2 = new JPanel(new FlowLayout());
      localJPanel2.add(localJButton1);
      localJPanel2.add(localJButton2);
      localJPanel1.add(localJPanel2, "South");
      localChartPanel.setPreferredSize(new Dimension(500, 270));
      add(localJPanel1);
    }

    private TimeSeriesCollection createRandomDataset(String paramString)
    {
      TimeSeries localTimeSeries = new TimeSeries(paramString);
      double d = 100.0D;
      Object localObject = new Day();
      for (int i = 0; i < 50; ++i)
      {
        localTimeSeries.add((RegularTimePeriod)localObject, d);
        localObject = ((RegularTimePeriod)localObject).next();
        d *= (1.0D + Math.random() / 100.0D);
      }
      return ((TimeSeriesCollection)new TimeSeriesCollection(localTimeSeries));
    }

    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (paramActionEvent.getActionCommand().equals("ADD_DATASET"))
      {
        if (this.datasetIndex >= 20)
          return;
        this.datasetIndex += 1;
        this.plot.setDataset(this.datasetIndex, createRandomDataset("S" + this.datasetIndex));
        this.plot.setRenderer(this.datasetIndex, new StandardXYItemRenderer());
      }
      else
      {
        if ((!(paramActionEvent.getActionCommand().equals("REMOVE_DATASET"))) || (this.datasetIndex < 1))
          return;
        this.plot.setDataset(this.datasetIndex, null);
        this.plot.setRenderer(this.datasetIndex, null);
        this.datasetIndex -= 1;
      }
    }
  }
}