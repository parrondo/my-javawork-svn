package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.GrayPaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class XYBlockChartDemo1 extends ApplicationFrame
{
  public XYBlockChartDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYZDataset paramXYZDataset)
  {
    NumberAxis localNumberAxis1 = new NumberAxis("X");
    localNumberAxis1.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    localNumberAxis1.setLowerMargin(0.0D);
    localNumberAxis1.setUpperMargin(0.0D);
    localNumberAxis1.setAxisLinePaint(Color.white);
    localNumberAxis1.setTickMarkPaint(Color.white);
    NumberAxis localNumberAxis2 = new NumberAxis("Y");
    localNumberAxis2.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    localNumberAxis2.setLowerMargin(0.0D);
    localNumberAxis2.setUpperMargin(0.0D);
    localNumberAxis2.setAxisLinePaint(Color.white);
    localNumberAxis2.setTickMarkPaint(Color.white);
    XYBlockRenderer localXYBlockRenderer = new XYBlockRenderer();
    GrayPaintScale localGrayPaintScale = new GrayPaintScale(-2.0D, 1.0D);
    localXYBlockRenderer.setPaintScale(localGrayPaintScale);
    XYPlot localXYPlot = new XYPlot(paramXYZDataset, localNumberAxis1, localNumberAxis2, localXYBlockRenderer);
    localXYPlot.setBackgroundPaint(Color.lightGray);
    localXYPlot.setDomainGridlinesVisible(false);
    localXYPlot.setRangeGridlinePaint(Color.white);
    localXYPlot.setAxisOffset(new RectangleInsets(5.0D, 5.0D, 5.0D, 5.0D));
    localXYPlot.setOutlinePaint(Color.blue);
    JFreeChart localJFreeChart = new JFreeChart("XYBlockChartDemo1", localXYPlot);
    localJFreeChart.removeLegend();
    NumberAxis localNumberAxis3 = new NumberAxis("Scale");
    localNumberAxis3.setAxisLinePaint(Color.white);
    localNumberAxis3.setTickMarkPaint(Color.white);
    localNumberAxis3.setTickLabelFont(new Font("Dialog", 0, 7));
    PaintScaleLegend localPaintScaleLegend = new PaintScaleLegend(new GrayPaintScale(), localNumberAxis3);
    localPaintScaleLegend.setStripOutlineVisible(false);
    localPaintScaleLegend.setSubdivisionCount(20);
    localPaintScaleLegend.setAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
    localPaintScaleLegend.setAxisOffset(5.0D);
    localPaintScaleLegend.setMargin(new RectangleInsets(5.0D, 5.0D, 5.0D, 5.0D));
    localPaintScaleLegend.setFrame(new BlockBorder(Color.red));
    localPaintScaleLegend.setPadding(new RectangleInsets(10.0D, 10.0D, 10.0D, 10.0D));
    localPaintScaleLegend.setStripWidth(10.0D);
    localPaintScaleLegend.setPosition(RectangleEdge.LEFT);
    localJFreeChart.addSubtitle(localPaintScaleLegend);
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    return localJFreeChart;
  }

  private static XYZDataset createDataset()
  {
    return new XYZDataset()
    {
      public int getSeriesCount()
      {
        return 1;
      }

      public int getItemCount(int paramInt)
      {
        return 10000;
      }

      public Number getX(int paramInt1, int paramInt2)
      {
        return new Double(getXValue(paramInt1, paramInt2));
      }

      public double getXValue(int paramInt1, int paramInt2)
      {
        return (paramInt2 / 100 - 50);
      }

      public Number getY(int paramInt1, int paramInt2)
      {
        return new Double(getYValue(paramInt1, paramInt2));
      }

      public double getYValue(int paramInt1, int paramInt2)
      {
        return (paramInt2 - (paramInt2 / 100 * 100) - 50);
      }

      public Number getZ(int paramInt1, int paramInt2)
      {
        return new Double(getZValue(paramInt1, paramInt2));
      }

      public double getZValue(int paramInt1, int paramInt2)
      {
        double d1 = getXValue(paramInt1, paramInt2);
        double d2 = getYValue(paramInt1, paramInt2);
        return Math.sin(Math.sqrt(d1 * d1 + d2 * d2) / 5.0D);
      }

      public void addChangeListener(DatasetChangeListener paramDatasetChangeListener)
      {
      }

      public void removeChangeListener(DatasetChangeListener paramDatasetChangeListener)
      {
      }

      public DatasetGroup getGroup()
      {
        return null;
      }

      public void setGroup(DatasetGroup paramDatasetGroup)
      {
      }

      public Comparable getSeriesKey(int paramInt)
      {
        return "sin(sqrt(x + y))";
      }

      public int indexOf(Comparable paramComparable)
      {
        return 0;
      }

      public DomainOrder getDomainOrder()
      {
        return DomainOrder.ASCENDING;
      }
    };
  }

  public static JPanel createDemoPanel()
  {
    return new ChartPanel(createChart(createDataset()));
  }

  public static void main(String[] paramArrayOfString)
  {
    XYBlockChartDemo1 localXYBlockChartDemo1 = new XYBlockChartDemo1("JFreeChart: XYBlockChartDemo1");
    localXYBlockChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localXYBlockChartDemo1);
    localXYBlockChartDemo1.setVisible(true);
  }
}