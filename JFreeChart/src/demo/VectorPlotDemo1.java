package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.text.NumberFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.VectorRenderer;
import org.jfree.data.xy.VectorSeries;
import org.jfree.data.xy.VectorSeriesCollection;
import org.jfree.data.xy.VectorXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class VectorPlotDemo1 extends ApplicationFrame
{
  public VectorPlotDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(VectorXYDataset paramVectorXYDataset)
  {
    NumberAxis localNumberAxis1 = new NumberAxis("X");
    localNumberAxis1.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    localNumberAxis1.setLowerMargin(0.01D);
    localNumberAxis1.setUpperMargin(0.01D);
    localNumberAxis1.setAutoRangeIncludesZero(false);
    NumberAxis localNumberAxis2 = new NumberAxis("Y");
    localNumberAxis2.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    localNumberAxis2.setLowerMargin(0.01D);
    localNumberAxis2.setUpperMargin(0.01D);
    localNumberAxis2.setAutoRangeIncludesZero(false);
    VectorRenderer localVectorRenderer = new VectorRenderer();
    localVectorRenderer.setSeriesPaint(0, Color.blue);
    localVectorRenderer.setBaseToolTipGenerator(new VectorToolTipGenerator());
    XYPlot localXYPlot = new XYPlot(paramVectorXYDataset, localNumberAxis1, localNumberAxis2, localVectorRenderer);
    localXYPlot.setBackgroundPaint(Color.lightGray);
    localXYPlot.setDomainGridlinePaint(Color.white);
    localXYPlot.setRangeGridlinePaint(Color.white);
    localXYPlot.setAxisOffset(new RectangleInsets(5.0D, 5.0D, 5.0D, 5.0D));
    localXYPlot.setOutlinePaint(Color.black);
    JFreeChart localJFreeChart = new JFreeChart("Vector Plot Demo 1", localXYPlot);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  private static VectorXYDataset createDataset()
  {
    VectorSeries localVectorSeries = new VectorSeries("Series 1");
    double d1 = 0.0D;
    while (d1 < 20.0D)
    {
      double d2 = 0.0D;
      while (d2 < 20.0D)
      {
        localVectorSeries.add(d1 + 10.0D, d2 + 10.0D, Math.sin(d1 / 5.0D) / 2.0D, Math.cos(d2 / 5.0D) / 2.0D);
        d2 += 1.0D;
      }
      d1 += 1.0D;
    }
    VectorSeriesCollection localVectorSeriesCollection = new VectorSeriesCollection();
    localVectorSeriesCollection.addSeries(localVectorSeries);
    return localVectorSeriesCollection;
  }

  public static JPanel createDemoPanel()
  {
    return new ChartPanel(createChart(createDataset()));
  }

  public static void main(String[] paramArrayOfString)
  {
    VectorPlotDemo1 localVectorPlotDemo1 = new VectorPlotDemo1("JFreeChart : Vector Plot Demo 1");
    localVectorPlotDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localVectorPlotDemo1);
    localVectorPlotDemo1.setVisible(true);
  }

  static class VectorToolTipGenerator extends StandardXYToolTipGenerator
  {
    public String generateToolTip(XYDataset paramXYDataset, int paramInt1, int paramInt2)
    {
      if (paramXYDataset instanceof VectorXYDataset)
      {
        VectorXYDataset localVectorXYDataset = (VectorXYDataset)paramXYDataset;
        double d1 = localVectorXYDataset.getXValue(paramInt1, paramInt2);
        double d2 = localVectorXYDataset.getVectorXValue(paramInt1, paramInt2);
        double d3 = localVectorXYDataset.getYValue(paramInt1, paramInt2);
        double d4 = localVectorXYDataset.getVectorYValue(paramInt1, paramInt2);
        NumberFormat localNumberFormat1 = getXFormat();
        NumberFormat localNumberFormat2 = getYFormat();
        return localNumberFormat1.format(d1) + " (" + localNumberFormat1.format(d2) + "), " + localNumberFormat2.format(d3) + " (" + localNumberFormat2.format(d4) + ")";
      }
      return super.generateToolTip(paramXYDataset, paramInt1, paramInt2);
    }
  }
}