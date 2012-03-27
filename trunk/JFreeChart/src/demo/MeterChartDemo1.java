package demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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

public class MeterChartDemo1 extends ApplicationFrame
{
  private static DefaultValueDataset dataset;

  public MeterChartDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(ValueDataset paramValueDataset)
  {
    MeterPlot localMeterPlot = new MeterPlot(paramValueDataset);
    localMeterPlot.setRange(new Range(0.0D, 60.0D));
    localMeterPlot.addInterval(new MeterInterval("Normal", new Range(0.0D, 35.0D), Color.lightGray, new BasicStroke(2.0F), new Color(0, 255, 0, 64)));
    localMeterPlot.addInterval(new MeterInterval("Warning", new Range(35.0D, 50.0D), Color.lightGray, new BasicStroke(2.0F), new Color(255, 255, 0, 64)));
    localMeterPlot.addInterval(new MeterInterval("Critical", new Range(50.0D, 60.0D), Color.lightGray, new BasicStroke(2.0F), new Color(255, 0, 0, 128)));
    localMeterPlot.setNeedlePaint(Color.darkGray);
    localMeterPlot.setDialBackgroundPaint(Color.white);
    localMeterPlot.setDialOutlinePaint(Color.gray);
    localMeterPlot.setDialShape(DialShape.CHORD);
    localMeterPlot.setMeterAngle(260);
    localMeterPlot.setTickLabelsVisible(true);
    localMeterPlot.setTickLabelFont(new Font("Dialog", 1, 10));
    localMeterPlot.setTickLabelPaint(Color.darkGray);
    localMeterPlot.setTickSize(5.0D);
    localMeterPlot.setTickPaint(Color.lightGray);
    localMeterPlot.setValuePaint(Color.black);
    localMeterPlot.setValueFont(new Font("Dialog", 1, 14));
    JFreeChart localJFreeChart = new JFreeChart("Meter Chart 1", JFreeChart.DEFAULT_TITLE_FONT, localMeterPlot, true);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    dataset = new DefaultValueDataset(23.0D);
    JFreeChart localJFreeChart = createChart(dataset);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    MeterChartDemo1 localMeterChartDemo1 = new MeterChartDemo1("JFreeChart: MeterChartDemo1.java");
    localMeterChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localMeterChartDemo1);
    localMeterChartDemo1.setVisible(true);
  }
}