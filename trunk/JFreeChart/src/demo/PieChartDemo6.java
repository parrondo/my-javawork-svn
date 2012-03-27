package demo;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class PieChartDemo6 extends ApplicationFrame
{
  public PieChartDemo6(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(800, 600));
    setContentPane(localJPanel);
  }

  private static PieDataset createDataset()
  {
    DefaultPieDataset localDefaultPieDataset = new DefaultPieDataset();
    localDefaultPieDataset.setValue("S1", 7.0D);
    localDefaultPieDataset.setValue("S2", null);
    localDefaultPieDataset.setValue("S3", 0.0D);
    localDefaultPieDataset.setValue("S4", 3.0D);
    localDefaultPieDataset.setValue("S5", -1.0D);
    return localDefaultPieDataset;
  }

  private static JFreeChart createChart(String paramString, PieDataset paramPieDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createPieChart(paramString, paramPieDataset, true, true, false);
    PiePlot localPiePlot = (PiePlot)localJFreeChart.getPlot();
    localPiePlot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} = {1}"));
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    DemoPanel localDemoPanel = new DemoPanel(new GridLayout(2, 2));
    JFreeChart localJFreeChart1 = createChart("Pie Chart 1", createDataset());
    Font localFont = new Font("Dialog", 0, 12);
    localJFreeChart1.addSubtitle(new TextTitle("Ignore nulls: false; Ignore zeros: false;", localFont));
    JFreeChart localJFreeChart2 = createChart("Pie Chart 2", createDataset());
    localJFreeChart2.addSubtitle(new TextTitle("Ignore nulls: true; Ignore zeros: false;", localFont));
    PiePlot localPiePlot1 = (PiePlot)localJFreeChart2.getPlot();
    localPiePlot1.setIgnoreNullValues(true);
    localPiePlot1.setIgnoreZeroValues(false);
    JFreeChart localJFreeChart3 = createChart("Pie Chart 3", createDataset());
    localJFreeChart3.addSubtitle(new TextTitle("Ignore nulls: false; Ignore zeros: true;", localFont));
    PiePlot localPiePlot2 = (PiePlot)localJFreeChart3.getPlot();
    localPiePlot2.setIgnoreNullValues(false);
    localPiePlot2.setIgnoreZeroValues(true);
    JFreeChart localJFreeChart4 = createChart("Pie Chart 4", createDataset());
    localJFreeChart4.addSubtitle(new TextTitle("Ignore nulls: true; Ignore zeros: true;", localFont));
    PiePlot localPiePlot3 = (PiePlot)localJFreeChart4.getPlot();
    localPiePlot3.setIgnoreNullValues(true);
    localPiePlot3.setIgnoreZeroValues(true);
    localDemoPanel.add(new ChartPanel(localJFreeChart1));
    localDemoPanel.add(new ChartPanel(localJFreeChart2));
    localDemoPanel.add(new ChartPanel(localJFreeChart3));
    localDemoPanel.add(new ChartPanel(localJFreeChart4));
    localDemoPanel.addChart(localJFreeChart1);
    localDemoPanel.addChart(localJFreeChart2);
    localDemoPanel.addChart(localJFreeChart3);
    localDemoPanel.addChart(localJFreeChart4);
    return localDemoPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    PieChartDemo6 localPieChartDemo6 = new PieChartDemo6("JFreeChart: PieChartDemo6.java");
    localPieChartDemo6.pack();
    RefineryUtilities.centerFrameOnScreen(localPieChartDemo6);
    localPieChartDemo6.setVisible(true);
  }
}