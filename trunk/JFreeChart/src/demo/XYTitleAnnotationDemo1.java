package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;

public class XYTitleAnnotationDemo1 extends ApplicationFrame
{
  public XYTitleAnnotationDemo1(String paramString)
  {
    super(paramString);
    ChartPanel localChartPanel = (ChartPanel)createDemoPanel();
    localChartPanel.setPreferredSize(new Dimension(500, 270));
    localChartPanel.setMouseZoomable(true);
    setContentPane(localChartPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Legal & General Unit Trust Prices", "Date", "Price Per Unit", paramXYDataset, false, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    localXYPlot.setDomainCrosshairVisible(true);
    localXYPlot.setRangeCrosshairVisible(true);
    LegendTitle localLegendTitle = new LegendTitle(localXYPlot);
    localLegendTitle.setItemFont(new Font("Dialog", 0, 9));
    localLegendTitle.setBackgroundPaint(new Color(200, 200, 255, 100));
    localLegendTitle.setFrame(new BlockBorder(Color.white));
    localLegendTitle.setPosition(RectangleEdge.BOTTOM);
    XYTitleAnnotation localXYTitleAnnotation = new XYTitleAnnotation(0.98D, 0.02D, localLegendTitle, RectangleAnchor.BOTTOM_RIGHT);
    localXYTitleAnnotation.setMaxWidth(0.48D);
    localXYPlot.addAnnotation(localXYTitleAnnotation);
    XYItemRenderer localXYItemRenderer = localXYPlot.getRenderer();
    if (localXYItemRenderer instanceof XYLineAndShapeRenderer)
    {
      XYLineAndShapeRenderer localObject = (XYLineAndShapeRenderer)localXYItemRenderer;
      ((XYLineAndShapeRenderer)localObject).setBaseShapesVisible(true);
      ((XYLineAndShapeRenderer)localObject).setBaseShapesFilled(true);
    }
    Object localObject = (DateAxis)localXYPlot.getDomainAxis();
    ((DateAxis)localObject).setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
    ValueAxis localValueAxis = localXYPlot.getRangeAxis();
    localValueAxis.setLowerMargin(0.35D);
    return ((JFreeChart)localJFreeChart);
  }

  private static XYDataset createDataset()
  {
    TimeSeries localTimeSeries1 = new TimeSeries("L&G European Index Trust");
    localTimeSeries1.add(new Month(2, 2001), 181.80000000000001D);
    localTimeSeries1.add(new Month(3, 2001), 167.30000000000001D);
    localTimeSeries1.add(new Month(4, 2001), 153.80000000000001D);
    localTimeSeries1.add(new Month(5, 2001), 167.59999999999999D);
    localTimeSeries1.add(new Month(6, 2001), 158.80000000000001D);
    localTimeSeries1.add(new Month(7, 2001), 148.30000000000001D);
    localTimeSeries1.add(new Month(8, 2001), 153.90000000000001D);
    localTimeSeries1.add(new Month(9, 2001), 142.69999999999999D);
    localTimeSeries1.add(new Month(10, 2001), 123.2D);
    localTimeSeries1.add(new Month(11, 2001), 131.80000000000001D);
    localTimeSeries1.add(new Month(12, 2001), 139.59999999999999D);
    localTimeSeries1.add(new Month(1, 2002), 142.90000000000001D);
    localTimeSeries1.add(new Month(2, 2002), 138.69999999999999D);
    localTimeSeries1.add(new Month(3, 2002), 137.30000000000001D);
    localTimeSeries1.add(new Month(4, 2002), 143.90000000000001D);
    localTimeSeries1.add(new Month(5, 2002), 139.80000000000001D);
    localTimeSeries1.add(new Month(6, 2002), 137.0D);
    localTimeSeries1.add(new Month(7, 2002), 132.80000000000001D);
    TimeSeries localTimeSeries2 = new TimeSeries("L&G UK Index Trust");
    localTimeSeries2.add(new Month(2, 2001), 129.59999999999999D);
    localTimeSeries2.add(new Month(3, 2001), 123.2D);
    localTimeSeries2.add(new Month(4, 2001), 117.2D);
    localTimeSeries2.add(new Month(5, 2001), 124.09999999999999D);
    localTimeSeries2.add(new Month(6, 2001), 122.59999999999999D);
    localTimeSeries2.add(new Month(7, 2001), 119.2D);
    localTimeSeries2.add(new Month(8, 2001), 116.5D);
    localTimeSeries2.add(new Month(9, 2001), 112.7D);
    localTimeSeries2.add(new Month(10, 2001), 101.5D);
    localTimeSeries2.add(new Month(11, 2001), 106.09999999999999D);
    localTimeSeries2.add(new Month(12, 2001), 110.3D);
    localTimeSeries2.add(new Month(1, 2002), 111.7D);
    localTimeSeries2.add(new Month(2, 2002), 111.0D);
    localTimeSeries2.add(new Month(3, 2002), 109.59999999999999D);
    localTimeSeries2.add(new Month(4, 2002), 113.2D);
    localTimeSeries2.add(new Month(5, 2002), 111.59999999999999D);
    localTimeSeries2.add(new Month(6, 2002), 108.8D);
    localTimeSeries2.add(new Month(7, 2002), 101.59999999999999D);
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
    localTimeSeriesCollection.addSeries(localTimeSeries1);
    localTimeSeriesCollection.addSeries(localTimeSeries2);
    return localTimeSeriesCollection;
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
    XYTitleAnnotationDemo1 localXYTitleAnnotationDemo1 = new XYTitleAnnotationDemo1("XYTitleAnnotationDemo1");
    localXYTitleAnnotationDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localXYTitleAnnotationDemo1);
    localXYTitleAnnotationDemo1.setVisible(true);
  }
}