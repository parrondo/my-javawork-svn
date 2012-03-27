package demo;

import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class AnnotationDemo1 extends ApplicationFrame
{
  public AnnotationDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(360, 500));
    setContentPane(localJPanel);
  }

  private static XYSeriesCollection createDataset()
  {
    XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection();
    try
    {
      BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(XYSeriesCollection.class.getClassLoader().getResourceAsStream("demo/wtageinf.txt")));
      String str = localBufferedReader.readLine();
      str = localBufferedReader.readLine();
      str = localBufferedReader.readLine();
      str = localBufferedReader.readLine();
      XYSeries localXYSeries1 = new XYSeries("P3", true, false);
      XYSeries localXYSeries2 = new XYSeries("P5", true, false);
      XYSeries localXYSeries3 = new XYSeries("P10", true, false);
      XYSeries localXYSeries4 = new XYSeries("P25", true, false);
      XYSeries localXYSeries5 = new XYSeries("P50", true, false);
      XYSeries localXYSeries6 = new XYSeries("P75", true, false);
      XYSeries localXYSeries7 = new XYSeries("P90", true, false);
      XYSeries localXYSeries8 = new XYSeries("P95", true, false);
      XYSeries localXYSeries9 = new XYSeries("P97", true, false);
      for (str = localBufferedReader.readLine(); str != null; str = localBufferedReader.readLine())
      {
        int i = Integer.parseInt(str.substring(1, 8).trim());
        float f1 = Float.parseFloat(str.substring(9, 17).trim());
        float f2 = Float.parseFloat(str.substring(69, 86).trim());
        float f3 = Float.parseFloat(str.substring(87, 103).trim());
        float f4 = Float.parseFloat(str.substring(104, 122).trim());
        float f5 = Float.parseFloat(str.substring(123, 140).trim());
        float f6 = Float.parseFloat(str.substring(141, 158).trim());
        float f7 = Float.parseFloat(str.substring(159, 176).trim());
        float f8 = Float.parseFloat(str.substring(177, 193).trim());
        float f9 = Float.parseFloat(str.substring(194, 212).trim());
        float f10 = Float.parseFloat(str.substring(212, str.length()).trim());
        if (i != 1)
          continue;
        localXYSeries1.add(f1, f2);
        localXYSeries2.add(f1, f3);
        localXYSeries3.add(f1, f4);
        localXYSeries4.add(f1, f5);
        localXYSeries5.add(f1, f6);
        localXYSeries6.add(f1, f7);
        localXYSeries7.add(f1, f8);
        localXYSeries8.add(f1, f9);
        localXYSeries9.add(f1, f10);
      }
      localXYSeriesCollection.addSeries(localXYSeries1);
      localXYSeriesCollection.addSeries(localXYSeries2);
      localXYSeriesCollection.addSeries(localXYSeries3);
      localXYSeriesCollection.addSeries(localXYSeries4);
      localXYSeriesCollection.addSeries(localXYSeries5);
      localXYSeriesCollection.addSeries(localXYSeries6);
      localXYSeriesCollection.addSeries(localXYSeries7);
      localXYSeriesCollection.addSeries(localXYSeries8);
      localXYSeriesCollection.addSeries(localXYSeries9);
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      System.err.println(localFileNotFoundException);
    }
    catch (IOException localIOException)
    {
      System.err.println(localIOException);
    }
    return localXYSeriesCollection;
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createXYLineChart(null, "Age in Months", "Weight (kg)", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
    TextTitle localTextTitle1 = new TextTitle("Growth Charts: United States", new Font("SansSerif", 1, 14));
    TextTitle localTextTitle2 = new TextTitle("Weight-for-age percentiles: boys, birth to 36 months", new Font("SansSerif", 0, 11));
    localJFreeChart.addSubtitle(localTextTitle1);
    localJFreeChart.addSubtitle(localTextTitle2);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    NumberAxis localNumberAxis1 = (NumberAxis)localXYPlot.getDomainAxis();
    localNumberAxis1.setUpperMargin(0.12D);
    localNumberAxis1.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    NumberAxis localNumberAxis2 = (NumberAxis)localXYPlot.getRangeAxis();
    localNumberAxis2.setAutoRangeIncludesZero(false);
    XYTextAnnotation localXYTextAnnotation = null;
    Font localFont = new Font("SansSerif", 0, 9);
    localXYTextAnnotation = new XYTextAnnotation("3rd", 36.5D, 11.76D);
    localXYTextAnnotation.setFont(localFont);
    localXYTextAnnotation.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
    localXYPlot.addAnnotation(localXYTextAnnotation);
    localXYTextAnnotation = new XYTextAnnotation("5th", 36.5D, 12.039999999999999D);
    localXYTextAnnotation.setFont(localFont);
    localXYTextAnnotation.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
    localXYPlot.addAnnotation(localXYTextAnnotation);
    localXYTextAnnotation = new XYTextAnnotation("10th", 36.5D, 12.493D);
    localXYTextAnnotation.setFont(localFont);
    localXYTextAnnotation.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
    localXYPlot.addAnnotation(localXYTextAnnotation);
    localXYTextAnnotation = new XYTextAnnotation("25th", 36.5D, 13.313000000000001D);
    localXYTextAnnotation.setFont(localFont);
    localXYTextAnnotation.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
    localXYPlot.addAnnotation(localXYTextAnnotation);
    localXYTextAnnotation = new XYTextAnnotation("50th", 36.5D, 14.33D);
    localXYTextAnnotation.setFont(localFont);
    localXYTextAnnotation.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
    localXYPlot.addAnnotation(localXYTextAnnotation);
    localXYTextAnnotation = new XYTextAnnotation("75th", 36.5D, 15.478D);
    localXYTextAnnotation.setFont(localFont);
    localXYTextAnnotation.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
    localXYPlot.addAnnotation(localXYTextAnnotation);
    localXYTextAnnotation = new XYTextAnnotation("90th", 36.5D, 16.641999999999999D);
    localXYTextAnnotation.setFont(localFont);
    localXYTextAnnotation.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
    localXYPlot.addAnnotation(localXYTextAnnotation);
    localXYTextAnnotation = new XYTextAnnotation("95th", 36.5D, 17.408000000000001D);
    localXYTextAnnotation.setFont(localFont);
    localXYTextAnnotation.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
    localXYPlot.addAnnotation(localXYTextAnnotation);
    localXYTextAnnotation = new XYTextAnnotation("97th", 36.5D, 17.936D);
    localXYTextAnnotation.setFont(localFont);
    localXYTextAnnotation.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
    localXYPlot.addAnnotation(localXYTextAnnotation);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    localChartPanel.setMouseWheelEnabled(true);
    return localChartPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    AnnotationDemo1 localAnnotationDemo1 = new AnnotationDemo1("JFreeChart: AnnotationDemo1.java");
    localAnnotationDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localAnnotationDemo1);
    localAnnotationDemo1.setVisible(true);
  }
}