package demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYDrawableAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class XYDrawableAnnotationDemo1 extends ApplicationFrame
{
  public XYDrawableAnnotationDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 300));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("XYDrawableAnnotationDemo1", null, "$ million", paramXYDataset, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setDomainPannable(true);
    localXYPlot.setRangePannable(true);
    DateAxis localDateAxis = (DateAxis)localXYPlot.getDomainAxis();
    localDateAxis.setLowerMargin(0.2D);
    localDateAxis.setUpperMargin(0.2D);
    localDateAxis.setStandardTickUnits(createStandardDateTickUnits());
    NumberAxis localNumberAxis = (NumberAxis)localXYPlot.getRangeAxis();
    localNumberAxis.setLowerMargin(0.2D);
    localNumberAxis.setUpperMargin(0.2D);
    XYLineAndShapeRenderer localXYLineAndShapeRenderer = new XYLineAndShapeRenderer();
    localXYLineAndShapeRenderer.setBaseShapesVisible(true);
    localXYLineAndShapeRenderer.setBaseLinesVisible(true);
    localXYLineAndShapeRenderer.setSeriesShape(0, new Ellipse2D.Double(-5.0D, -5.0D, 10.0D, 10.0D));
    localXYLineAndShapeRenderer.setSeriesShape(1, new Ellipse2D.Double(-5.0D, -5.0D, 10.0D, 10.0D));
    localXYLineAndShapeRenderer.setSeriesStroke(0, new BasicStroke(3.0F));
    localXYLineAndShapeRenderer.setSeriesStroke(1, new BasicStroke(3.0F, 1, 1, 5.0F, new float[] { 10.0F, 5.0F }, 0.0F));
    localXYLineAndShapeRenderer.setSeriesFillPaint(0, Color.white);
    localXYLineAndShapeRenderer.setSeriesFillPaint(1, Color.white);
    localXYLineAndShapeRenderer.setUseFillPaint(true);
    localXYLineAndShapeRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
    localXYLineAndShapeRenderer.setDefaultEntityRadius(6);
    localXYLineAndShapeRenderer.addAnnotation(new XYDrawableAnnotation(new Month(4, 2005).getFirstMillisecond(), 600.0D, 180.0D, 100.0D, 3.0D, createPieChart()));
    localXYLineAndShapeRenderer.addAnnotation(new XYDrawableAnnotation(new Month(9, 2007).getFirstMillisecond(), 1250.0D, 120.0D, 100.0D, 2.0D, createBarChart()));
    localXYPlot.setRenderer(localXYLineAndShapeRenderer);
    return localJFreeChart;
  }

  private static XYDataset createDataset()
  {
    TimeSeries localTimeSeries1 = new TimeSeries("Division A");
    localTimeSeries1.add(new Year(2005), 1520.0D);
    localTimeSeries1.add(new Year(2006), 1132.0D);
    localTimeSeries1.add(new Year(2007), 450.0D);
    localTimeSeries1.add(new Year(2008), 620.0D);
    TimeSeries localTimeSeries2 = new TimeSeries("Division B");
    localTimeSeries2.add(new Year(2005), 1200.0D);
    localTimeSeries2.add(new Year(2006), 1300.0D);
    localTimeSeries2.add(new Year(2007), 640.0D);
    localTimeSeries2.add(new Year(2008), 520.0D);
    TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
    localTimeSeriesCollection.addSeries(localTimeSeries1);
    localTimeSeriesCollection.addSeries(localTimeSeries2);
    return localTimeSeriesCollection;
  }

  private static JFreeChart createPieChart()
  {
    DefaultPieDataset localDefaultPieDataset = new DefaultPieDataset();
    localDefaultPieDataset.setValue("Engineering", 43.200000000000003D);
    localDefaultPieDataset.setValue("Research", 13.199999999999999D);
    localDefaultPieDataset.setValue("Advertising", 20.899999999999999D);
    PiePlot localPiePlot = new PiePlot(localDefaultPieDataset);
    localPiePlot.setBackgroundPaint(null);
    localPiePlot.setOutlinePaint(null);
    localPiePlot.setBaseSectionOutlinePaint(Color.white);
    localPiePlot.setBaseSectionOutlineStroke(new BasicStroke(2.0F));
    localPiePlot.setLabelFont(new Font("Dialog", 0, 18));
    localPiePlot.setMaximumLabelWidth(0.25D);
    JFreeChart localJFreeChart = new JFreeChart(localPiePlot);
    localJFreeChart.setBackgroundPaint(null);
    localJFreeChart.removeLegend();
    localJFreeChart.setPadding(RectangleInsets.ZERO_INSETS);
    return localJFreeChart;
  }

  private static JFreeChart createBarChart()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(10.0D, "R1", "Q1");
    localDefaultCategoryDataset.addValue(7.0D, "R1", "Q2");
    localDefaultCategoryDataset.addValue(8.0D, "R1", "Q3");
    localDefaultCategoryDataset.addValue(4.0D, "R1", "Q4");
    localDefaultCategoryDataset.addValue(10.6D, "R2", "Q1");
    localDefaultCategoryDataset.addValue(6.1D, "R2", "Q2");
    localDefaultCategoryDataset.addValue(8.5D, "R2", "Q3");
    localDefaultCategoryDataset.addValue(4.3D, "R2", "Q4");
    JFreeChart localJFreeChart = ChartFactory.createBarChart("Sales 2008", null, null, localDefaultCategoryDataset, PlotOrientation.VERTICAL, false, false, false);
    localJFreeChart.setBackgroundPaint(null);
    localJFreeChart.getPlot().setBackgroundPaint(new Color(200, 200, 255, 60));
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    JFreeChart localJFreeChart = createChart(createDataset());
    return new ChartPanel(localJFreeChart);
  }

  private static TickUnitSource createStandardDateTickUnits()
  {
    TickUnits localTickUnits = new TickUnits();
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy");
    localTickUnits.add(new DateTickUnit(DateTickUnitType.YEAR, 1, DateTickUnitType.YEAR, 1, localSimpleDateFormat));
    localTickUnits.add(new DateTickUnit(DateTickUnitType.YEAR, 2, DateTickUnitType.YEAR, 1, localSimpleDateFormat));
    localTickUnits.add(new DateTickUnit(DateTickUnitType.YEAR, 5, DateTickUnitType.YEAR, 5, localSimpleDateFormat));
    return localTickUnits;
  }

  public static void main(String[] paramArrayOfString)
  {
    XYDrawableAnnotationDemo1 localXYDrawableAnnotationDemo1 = new XYDrawableAnnotationDemo1("JFreeChart: XYDrawableAnnotationDemo1.java");
    localXYDrawableAnnotationDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localXYDrawableAnnotationDemo1);
    localXYDrawableAnnotationDemo1.setVisible(true);
  }
}