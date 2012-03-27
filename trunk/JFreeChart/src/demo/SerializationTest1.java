package demo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
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

public class SerializationTest1 extends ApplicationFrame
  implements ActionListener
{
  private TimeSeries series = new TimeSeries("Random Data");
  private double lastValue = 100.0D;

  public SerializationTest1(String paramString)
  {
    super(paramString);
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection(this.series);
    JFreeChart localJFreeChart1 = createChart(localTimeSeriesCollection);
    JFreeChart localJFreeChart2 = null;
    try
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      ObjectOutputStream localObject1 = new ObjectOutputStream(localByteArrayOutputStream);
      ((ObjectOutput)localObject1).writeObject(localJFreeChart1);
      ((ObjectOutput)localObject1).close();
      localJFreeChart1 = null;
      localTimeSeriesCollection = null;
      this.series = null;
      System.gc();
      ObjectInputStream localObject2 = new ObjectInputStream(new ByteArrayInputStream(localByteArrayOutputStream.toByteArray()));
      localJFreeChart2 = (JFreeChart)((ObjectInput)localObject2).readObject();
      ((ObjectInput)localObject2).close();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    XYPlot localXYPlot = (XYPlot)localJFreeChart2.getPlot();
    Object localObject1 = (TimeSeriesCollection)localXYPlot.getDataset();
    this.series = ((TimeSeriesCollection)localObject1).getSeries(0);
    Object localObject2 = new ChartPanel(localJFreeChart2);
    JButton localJButton = new JButton("Add New Data Item");
    localJButton.setActionCommand("ADD_DATA");
    localJButton.addActionListener(this);
    JPanel localJPanel = new JPanel(new BorderLayout());
    localJPanel.add((Component)localObject2);
    localJPanel.add(localJButton, "South");
    ((ChartPanel)localObject2).setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Serialization Test 1", "Time", "Value", paramXYDataset, true, true, false);
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
    SerializationTest1 localSerializationTest1 = new SerializationTest1("Serialization Test 1");
    localSerializationTest1.pack();
    RefineryUtilities.centerFrameOnScreen(localSerializationTest1);
    localSerializationTest1.setVisible(true);
  }
}