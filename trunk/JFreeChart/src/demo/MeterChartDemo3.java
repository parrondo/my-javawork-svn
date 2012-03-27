package demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DialShape;
import org.jfree.chart.plot.MeterInterval;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.data.Range;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.ValueDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class MeterChartDemo3 extends ApplicationFrame
{
  public MeterChartDemo3(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(String paramString, ValueDataset paramValueDataset, DialShape paramDialShape)
  {
    MeterPlot localMeterPlot = new MeterPlot(paramValueDataset);
    localMeterPlot.setDialShape(paramDialShape);
    localMeterPlot.setRange(new Range(0.0D, 60.0D));
    localMeterPlot.addInterval(new MeterInterval("Normal", new Range(0.0D, 35.0D), Color.lightGray, new BasicStroke(2.0F), new Color(0, 255, 0, 64)));
    localMeterPlot.addInterval(new MeterInterval("Warning", new Range(35.0D, 50.0D), Color.lightGray, new BasicStroke(2.0F), new Color(255, 255, 0, 64)));
    localMeterPlot.addInterval(new MeterInterval("Critical", new Range(50.0D, 60.0D), Color.lightGray, new BasicStroke(2.0F), new Color(255, 0, 0, 128)));
    localMeterPlot.setNeedlePaint(Color.darkGray);
    localMeterPlot.setDialBackgroundPaint(Color.white);
    localMeterPlot.setDialOutlinePaint(Color.gray);
    localMeterPlot.setMeterAngle(260);
    localMeterPlot.setTickLabelsVisible(true);
    localMeterPlot.setTickLabelFont(new Font("Dialog", 1, 10));
    localMeterPlot.setTickLabelPaint(Color.darkGray);
    localMeterPlot.setTickSize(5.0D);
    localMeterPlot.setTickPaint(Color.lightGray);
    localMeterPlot.setValuePaint(Color.black);
    localMeterPlot.setValueFont(new Font("Dialog", 1, 14));
    JFreeChart localJFreeChart = new JFreeChart(paramString, JFreeChart.DEFAULT_TITLE_FONT, localMeterPlot, true);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JPanel localJPanel = new JPanel(new GridLayout(1, 3));
    DefaultValueDataset localDefaultValueDataset = new DefaultValueDataset(23.0D);
    ChartPanel localChartPanel1 = new ChartPanel(createChart("DialShape.PIE", localDefaultValueDataset, DialShape.PIE));
    ChartPanel localChartPanel2 = new ChartPanel(createChart("DialShape.CHORD", localDefaultValueDataset, DialShape.CHORD));
    ChartPanel localChartPanel3 = new ChartPanel(createChart("DialShape.CIRCLE", localDefaultValueDataset, DialShape.CIRCLE));
    localJPanel.add(localChartPanel1);
    localJPanel.add(localChartPanel2);
    localJPanel.add(localChartPanel3);
    return localJPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    MeterChartDemo3 localMeterChartDemo3 = new MeterChartDemo3("JFreeChart: MeterChartDemo3.java");
    localMeterChartDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localMeterChartDemo3);
    localMeterChartDemo3.setVisible(true);
  }
}