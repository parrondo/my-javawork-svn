package demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class XYBlockChartDemo3 extends ApplicationFrame
{
  public XYBlockChartDemo3(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYZDataset paramXYZDataset)
  {
    NumberAxis localNumberAxis1 = new NumberAxis("X");
    localNumberAxis1.setLowerMargin(0.0D);
    localNumberAxis1.setUpperMargin(0.0D);
    NumberAxis localNumberAxis2 = new NumberAxis("Y");
    localNumberAxis2.setAutoRangeIncludesZero(false);
    localNumberAxis2.setInverted(true);
    localNumberAxis2.setLowerMargin(0.0D);
    localNumberAxis2.setUpperMargin(0.0D);
    localNumberAxis2.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    XYBlockRenderer localXYBlockRenderer = new XYBlockRenderer();
    LookupPaintScale localLookupPaintScale = new LookupPaintScale(0.5D, 3.5D, Color.black);
    localLookupPaintScale.add(0.5D, Color.green);
    localLookupPaintScale.add(1.5D, Color.orange);
    localLookupPaintScale.add(2.5D, Color.red);
    localXYBlockRenderer.setPaintScale(localLookupPaintScale);
    XYPlot localXYPlot = new XYPlot(paramXYZDataset, localNumberAxis1, localNumberAxis2, localXYBlockRenderer);
    localXYPlot.setBackgroundPaint(Color.lightGray);
    localXYPlot.setDomainGridlinePaint(Color.white);
    localXYPlot.setRangeGridlinePaint(Color.white);
    localXYPlot.setForegroundAlpha(0.66F);
    localXYPlot.setAxisOffset(new RectangleInsets(5.0D, 5.0D, 5.0D, 5.0D));
    JFreeChart localJFreeChart = new JFreeChart("XYBlockChartDemo3", localXYPlot);
    localJFreeChart.removeLegend();
    SymbolAxis localSymbolAxis = new SymbolAxis(null, new String[] { "", "OK", "Uncertain", "Bad" });
    localSymbolAxis.setRange(0.5D, 3.5D);
    localSymbolAxis.setPlot(new PiePlot());
    localSymbolAxis.setGridBandsVisible(false);
    PaintScaleLegend localPaintScaleLegend = new PaintScaleLegend(localLookupPaintScale, localSymbolAxis);
    localPaintScaleLegend.setAxisOffset(5.0D);
    localPaintScaleLegend.setPosition(RectangleEdge.BOTTOM);
    localPaintScaleLegend.setMargin(new RectangleInsets(5.0D, 5.0D, 5.0D, 5.0D));
    localJFreeChart.addSubtitle(localPaintScaleLegend);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  private static void setValue(double[][] paramArrayOfDouble, int paramInt1, int paramInt2, double paramDouble)
  {
    paramArrayOfDouble[0][((paramInt2 - 8) * 60 + paramInt1)] = paramInt1;
    paramArrayOfDouble[1][((paramInt2 - 8) * 60 + paramInt1)] = paramInt2;
    paramArrayOfDouble[2][((paramInt2 - 8) * 60 + paramInt1)] = paramDouble;
  }

  private static XYZDataset createDataset()
  {
    double[] arrayOfDouble1 = new double[840];
    double[] arrayOfDouble2 = new double[840];
    double[] arrayOfDouble3 = new double[840];
    double[][] arrayOfDouble = { arrayOfDouble1, arrayOfDouble2, arrayOfDouble3 };
    for (int i = 0; i < 60; ++i)
      for (int j = 8; j < 22; ++j)
        setValue(arrayOfDouble, i, j, 0.0D);
    for (int i = 8; i < 12; ++i)
      for (int j = 13; j < 48; ++j)
        setValue(arrayOfDouble, j, i, 1.0D);
    for (int i = 12; i < 20; ++i)
      for (int j = 23; j < 43; ++j)
        setValue(arrayOfDouble, j, i, 1.0D);
    setValue(arrayOfDouble, 2, 20, 2.0D);
    setValue(arrayOfDouble, 5, 20, 3.0D);
    setValue(arrayOfDouble, 6, 20, 3.0D);
    setValue(arrayOfDouble, 7, 20, 3.0D);
    setValue(arrayOfDouble, 8, 20, 3.0D);
    setValue(arrayOfDouble, 9, 20, 3.0D);
    setValue(arrayOfDouble, 11, 20, 3.0D);
    setValue(arrayOfDouble, 17, 20, 2.0D);
    setValue(arrayOfDouble, 18, 20, 2.0D);
    setValue(arrayOfDouble, 19, 20, 2.0D);
    setValue(arrayOfDouble, 20, 20, 2.0D);
    setValue(arrayOfDouble, 22, 20, 2.0D);
    setValue(arrayOfDouble, 25, 20, 2.0D);
    setValue(arrayOfDouble, 28, 20, 2.0D);
    setValue(arrayOfDouble, 35, 20, 2.0D);
    for (int i = 40; i < 60; ++i)
      setValue(arrayOfDouble, i, 20, 3.0D);
    for (int i = 23; i < 43; ++i)
      setValue(arrayOfDouble, i, 21, 1.0D);
    DefaultXYZDataset localDefaultXYZDataset = new DefaultXYZDataset();
    localDefaultXYZDataset.addSeries("Series 1", arrayOfDouble);
    return localDefaultXYZDataset;
  }

  public static JPanel createDemoPanel()
  {
    return new ChartPanel(createChart(createDataset()));
  }

  public static void main(String[] paramArrayOfString)
  {
    XYBlockChartDemo3 localXYBlockChartDemo3 = new XYBlockChartDemo3("Block Chart Demo 3");
    localXYBlockChartDemo3.pack();
    RefineryUtilities.centerFrameOnScreen(localXYBlockChartDemo3);
    localXYBlockChartDemo3.setVisible(true);
  }
}