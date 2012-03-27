package demo;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class XYBlockChartDemo2 extends ApplicationFrame
{
  public XYBlockChartDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYZDataset paramXYZDataset)
  {
    DateAxis localDateAxis = new DateAxis("Date");
    localDateAxis.setLowerMargin(0.0D);
    localDateAxis.setUpperMargin(0.0D);
    localDateAxis.setInverted(true);
    NumberAxis localNumberAxis = new NumberAxis("Hour");
    localNumberAxis.setUpperMargin(0.0D);
    localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    XYBlockRenderer localXYBlockRenderer = new XYBlockRenderer();
    localXYBlockRenderer.setBlockWidth(86400000.0D);
    localXYBlockRenderer.setBlockAnchor(RectangleAnchor.BOTTOM_LEFT);
    LookupPaintScale localLookupPaintScale = new LookupPaintScale(0.5D, 4.5D, Color.white);
    localLookupPaintScale.add(0.5D, Color.red);
    localLookupPaintScale.add(1.5D, Color.green);
    localLookupPaintScale.add(2.5D, Color.blue);
    localLookupPaintScale.add(3.5D, Color.yellow);
    localXYBlockRenderer.setPaintScale(localLookupPaintScale);
    XYPlot localXYPlot = new XYPlot(paramXYZDataset, localDateAxis, localNumberAxis, localXYBlockRenderer);
    localXYPlot.setOrientation(PlotOrientation.HORIZONTAL);
    localXYPlot.setBackgroundPaint(Color.lightGray);
    localXYPlot.setRangeGridlinePaint(Color.white);
    localXYPlot.setAxisOffset(new RectangleInsets(5.0D, 5.0D, 5.0D, 5.0D));
    JFreeChart localJFreeChart = new JFreeChart("XYBlockChartDemo2", localXYPlot);
    localJFreeChart.removeLegend();
    localJFreeChart.setBackgroundPaint(Color.white);
    SymbolAxis localSymbolAxis = new SymbolAxis(null, new String[] { "", "Unavailable", "Free", "Group 1", "Group 2" });
    localSymbolAxis.setRange(0.5D, 4.5D);
    localSymbolAxis.setPlot(new PiePlot());
    localSymbolAxis.setGridBandsVisible(false);
    PaintScaleLegend localPaintScaleLegend = new PaintScaleLegend(localLookupPaintScale, localSymbolAxis);
    localPaintScaleLegend.setMargin(new RectangleInsets(3.0D, 10.0D, 3.0D, 10.0D));
    localPaintScaleLegend.setPosition(RectangleEdge.BOTTOM);
    localPaintScaleLegend.setAxisOffset(5.0D);
    localJFreeChart.addSubtitle(localPaintScaleLegend);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  private static XYZDataset createDataset()
  {
    double[] arrayOfDouble1 = new double[2400];
    double[] arrayOfDouble2 = new double[2400];
    double[] arrayOfDouble3 = new double[2400];
    Object localObject = new Day();
    for (int i = 0; i < 100; ++i)
    {
      double d = 1.0D;
      for (int j = 0; j < 24; ++j)
      {
        if (Math.random() < 0.1D)
          d = Math.random() * 4.0D;
        arrayOfDouble1[(i * 24 + j)] = ((RegularTimePeriod)localObject).getFirstMillisecond();
        arrayOfDouble2[(i * 24 + j)] = j;
        arrayOfDouble3[(i * 24 + j)] = d;
      }
      localObject = ((RegularTimePeriod)localObject).next();
    }
    DefaultXYZDataset localDefaultXYZDataset = new DefaultXYZDataset();
    localDefaultXYZDataset.addSeries("Series 1", new double[][] { arrayOfDouble1, arrayOfDouble2, arrayOfDouble3 });
    return ((XYZDataset)localDefaultXYZDataset);
  }

  public static JPanel createDemoPanel()
  {
    return new ChartPanel(createChart(createDataset()));
  }

  public static void main(String[] paramArrayOfString)
  {
    XYBlockChartDemo2 localXYBlockChartDemo2 = new XYBlockChartDemo2("Block Chart Demo 2");
    localXYBlockChartDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localXYBlockChartDemo2);
    localXYBlockChartDemo2.setVisible(true);
  }
}