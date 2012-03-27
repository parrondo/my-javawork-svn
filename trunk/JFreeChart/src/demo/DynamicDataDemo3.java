package demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.UnitType;

public class DynamicDataDemo3 extends ApplicationFrame
{
  public DynamicDataDemo3(String paramString)
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
    DynamicDataDemo3 localDynamicDataDemo3 = new DynamicDataDemo3("JFreeChart: DynamicDataDemo3.java");
    localDynamicDataDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localDynamicDataDemo3);
    localDynamicDataDemo3.setVisible(true);
  }

  static class MyDemoPanel extends DemoPanel
    implements ActionListener
  {
    public static final int SUBPLOT_COUNT = 3;
    private TimeSeriesCollection[] datasets;
    private double[] lastValue = new double[3];

    public MyDemoPanel()
    {
      super(new BorderLayout());
      CombinedDomainXYPlot localCombinedDomainXYPlot = new CombinedDomainXYPlot(new DateAxis("Time"));
      this.datasets = new TimeSeriesCollection[3];
      for (int i = 0; i < 3; ++i)
      {
        this.lastValue[i] = 100.0D;
        TimeSeries localObject1 = new TimeSeries("Random " + i);
        this.datasets[i] = new TimeSeriesCollection((TimeSeries)localObject1);
        NumberAxis localObject2 = new NumberAxis("Y" + i);
        ((NumberAxis)localObject2).setAutoRangeIncludesZero(false);
        XYPlot localObject3 = new XYPlot(this.datasets[i], null, (ValueAxis)localObject2, new StandardXYItemRenderer());
        ((XYPlot)localObject3).setBackgroundPaint(Color.lightGray);
        ((XYPlot)localObject3).setDomainGridlinePaint(Color.white);
        ((XYPlot)localObject3).setRangeGridlinePaint(Color.white);
        localCombinedDomainXYPlot.add((XYPlot)localObject3);
      }
      JFreeChart localJFreeChart = new JFreeChart("Dynamic Data Demo 3", localCombinedDomainXYPlot);
      addChart(localJFreeChart);
      Object localObject1 = (LegendTitle)localJFreeChart.getSubtitle(0);
      ((LegendTitle)localObject1).setPosition(RectangleEdge.RIGHT);
      ((LegendTitle)localObject1).setMargin(new RectangleInsets(UnitType.ABSOLUTE, 0.0D, 4.0D, 0.0D, 4.0D));
      localJFreeChart.setBorderPaint(Color.black);
      localJFreeChart.setBorderVisible(true);
      Object localObject2 = localCombinedDomainXYPlot.getDomainAxis();
      ((ValueAxis)localObject2).setAutoRange(true);
      ((ValueAxis)localObject2).setFixedAutoRange(20000.0D);
      ChartUtilities.applyCurrentTheme(localJFreeChart);
      Object localObject3 = new ChartPanel(localJFreeChart);
      add((Component)localObject3);
      JPanel localJPanel = new JPanel(new FlowLayout());
      for (int j = 0; j < 3; ++j)
      {
        JButton localJButton2 = new JButton("Series " + j);
        localJButton2.setActionCommand("ADD_DATA_" + j);
        localJButton2.addActionListener(this);
        localJPanel.add(localJButton2);
      }
      JButton localJButton1 = new JButton("ALL");
      localJButton1.setActionCommand("ADD_ALL");
      localJButton1.addActionListener(this);
      localJPanel.add(localJButton1);
      add(localJPanel, "South");
      ((ChartPanel)localObject3).setPreferredSize(new Dimension(500, 470));
      ((ChartPanel)localObject3).setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    public void actionPerformed(ActionEvent paramActionEvent)
    {
      for (int i = 0; i < 3; ++i)
      {
        if (!(paramActionEvent.getActionCommand().endsWith(String.valueOf(i))))
          continue;
        Millisecond localMillisecond2 = new Millisecond();
        System.out.println("Now = " + localMillisecond2.toString());
        this.lastValue[i] *= (0.9D + 0.2D * Math.random());
        this.datasets[i].getSeries(0).add(new Millisecond(), this.lastValue[i]);
      }
      if (!(paramActionEvent.getActionCommand().equals("ADD_ALL")))
        return;
      Millisecond localMillisecond1 = new Millisecond();
      System.out.println("Now = " + localMillisecond1.toString());
      for (int j = 0; j < 3; ++j)
      {
        this.lastValue[j] *= (0.9D + 0.2D * Math.random());
        this.datasets[j].getSeries(0).add(new Millisecond(), this.lastValue[j]);
      }
    }
  }
}