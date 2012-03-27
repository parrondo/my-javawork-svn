package demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PieLabelLinkStyle;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.SortOrder;

public class PieChartDemo4 extends ApplicationFrame
{
  public PieChartDemo4(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static DefaultPieDataset createDataset()
  {
    DefaultPieDataset localDefaultPieDataset = new DefaultPieDataset();
    localDefaultPieDataset.setValue("Section A", new Double(43.200000000000003D));
    localDefaultPieDataset.setValue("Section B", new Double(10.0D));
    localDefaultPieDataset.setValue("Section C", new Double(27.5D));
    localDefaultPieDataset.setValue("Section D", new Double(17.5D));
    localDefaultPieDataset.setValue("Section E", new Double(11.0D));
    localDefaultPieDataset.setValue("Section F", new Double(19.399999999999999D));
    return localDefaultPieDataset;
  }

  private static JFreeChart createChart(PieDataset paramPieDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createPieChart("Pie Chart Demo 4", paramPieDataset, true, true, false);
    PiePlot localPiePlot = (PiePlot)localJFreeChart.getPlot();
    localPiePlot.setNoDataMessage("No data available");
    localPiePlot.setCircular(false);
    localPiePlot.setLabelGap(0.02D);
    localPiePlot.setExplodePercent("Section D", 0.5D);
    localPiePlot.setLabelLinkStyle(PieLabelLinkStyle.CUBIC_CURVE);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    return new MyDemoPanel(createDataset());
  }

  public static void main(String[] paramArrayOfString)
  {
    PieChartDemo4 localPieChartDemo4 = new PieChartDemo4("JFreeChart: PieChartDemo4.java");
    localPieChartDemo4.pack();
    RefineryUtilities.centerFrameOnScreen(localPieChartDemo4);
    localPieChartDemo4.setVisible(true);
  }

  private static class MyDemoPanel extends DemoPanel
    implements ActionListener
  {
    JFreeChart chart;
    DefaultPieDataset dataset;
    boolean ascendingByKey = false;
    boolean ascendingByValue = false;

    public MyDemoPanel(DefaultPieDataset paramDefaultPieDataset)
    {
      super(new BorderLayout());
      this.dataset = paramDefaultPieDataset;
      this.chart = PieChartDemo4.createChart(paramDefaultPieDataset);
      addChart(this.chart);
      ChartPanel localChartPanel = new ChartPanel(this.chart);
      add(localChartPanel);
      JPanel localJPanel = new JPanel(new FlowLayout());
      JButton localJButton1 = new JButton("By Key");
      localJButton1.setActionCommand("BY_KEY");
      localJButton1.addActionListener(this);
      JButton localJButton2 = new JButton("By Value");
      localJButton2.setActionCommand("BY_VALUE");
      localJButton2.addActionListener(this);
      JButton localJButton3 = new JButton("Random");
      localJButton3.setActionCommand("RANDOM");
      localJButton3.addActionListener(this);
      JCheckBox localJCheckBox = new JCheckBox("Simple Labels");
      localJCheckBox.setActionCommand("LABELS");
      localJCheckBox.addActionListener(this);
      localJPanel.add(localJButton1);
      localJPanel.add(localJButton2);
      localJPanel.add(localJButton3);
      localJPanel.add(localJCheckBox);
      add(localJPanel, "South");
    }

    public void actionPerformed(ActionEvent paramActionEvent)
    {
      String str = paramActionEvent.getActionCommand();
      if ("BY_KEY".equals(str))
      {
        if (!(this.ascendingByKey))
        {
          this.dataset.sortByKeys(SortOrder.ASCENDING);
          this.ascendingByKey = true;
        }
        else
        {
          this.dataset.sortByKeys(SortOrder.DESCENDING);
          this.ascendingByKey = false;
        }
      }
      else if ("BY_VALUE".equals(str))
      {
        if (!(this.ascendingByValue))
        {
          this.dataset.sortByValues(SortOrder.ASCENDING);
          this.ascendingByValue = true;
        }
        else
        {
          this.dataset.sortByValues(SortOrder.DESCENDING);
          this.ascendingByValue = false;
        }
      }
      else
      {
        Object localObject1;
        if ("RANDOM".equals(str))
        {
          localObject1 = new ArrayList(this.dataset.getKeys());
          Collections.shuffle((List)localObject1);
          DefaultPieDataset localDefaultPieDataset = new DefaultPieDataset();
          Iterator localIterator = ((List)localObject1).iterator();
          while (localIterator.hasNext())
          {
            Comparable localObject2 = (Comparable)localIterator.next();
            localDefaultPieDataset.setValue((Comparable)localObject2, this.dataset.getValue((Comparable)localObject2));
          }
          Object localObject2 = (PiePlot)this.chart.getPlot();
          ((PiePlot)localObject2).setDataset(localDefaultPieDataset);
          this.dataset = localDefaultPieDataset;
        }
        else
        {
          if (!("LABELS".equals(str)))
            return;
          localObject1 = (PiePlot)this.chart.getPlot();
          boolean bool = ((PiePlot)localObject1).getSimpleLabels();
          if (bool)
          {
            ((PiePlot)localObject1).setInteriorGap(0.05D);
            ((PiePlot)localObject1).setSimpleLabels(false);
          }
          else
          {
            ((PiePlot)localObject1).setInteriorGap(0.01D);
            ((PiePlot)localObject1).setSimpleLabels(true);
          }
        }
      }
    }
  }
}