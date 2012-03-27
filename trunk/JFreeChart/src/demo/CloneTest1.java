package demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class CloneTest1 extends ApplicationFrame
  implements ActionListener
{
  private TimeSeries series = new TimeSeries("Random Data");
  private double lastValue = 100.0D;

  public CloneTest1(String paramString)
  {
    super(paramString);
    TimeSeriesCollection localTimeSeriesCollection1 = new TimeSeriesCollection(this.series);
    JFreeChart localJFreeChart1 = createChart(localTimeSeriesCollection1);
    JFreeChart localJFreeChart2 = null;
    try
    {
      localJFreeChart2 = (JFreeChart)localJFreeChart1.clone();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    XYPlot localXYPlot = (XYPlot)localJFreeChart2.getPlot();
    TimeSeriesCollection localTimeSeriesCollection2 = (TimeSeriesCollection)localXYPlot.getDataset();
    this.series = localTimeSeriesCollection2.getSeries(0);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart2);
    JButton localJButton = new JButton("Add New Data Item");
    localJButton.setActionCommand("ADD_DATA");
    localJButton.addActionListener(this);
    JPanel localJPanel = new JPanel(new BorderLayout());
    localJPanel.add(localChartPanel);
    localJPanel.add(localJButton, "South");
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Clone Test 1", "Time", "Value", paramXYDataset, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    ValueAxis localValueAxis = localXYPlot.getDomainAxis();
    localValueAxis.setAutoRange(true);
    localValueAxis.setFixedAutoRange(60000.0D);
    return localJFreeChart;
  }

  public void actionPerformed(ActionEvent paramActionEvent)
  {
    if (!(paramActionEvent.getActionCommand().equals("ADD_DATA")))
      return;
    double d = 0.9D + 0.2D * Math.random();
    this.lastValue *= d;
    Millisecond localMillisecond = new Millisecond();
    System.out.println("Now = " + localMillisecond.toString());
    this.series.add(new Millisecond(), this.lastValue);
  }

  public static void main(String[] paramArrayOfString)
  {
    CloneTest1 localCloneTest1 = new CloneTest1("JFreeChart : Clone Test 1");
    localCloneTest1.pack();
    RefineryUtilities.centerFrameOnScreen(localCloneTest1);
    localCloneTest1.setVisible(true);
  }
}