package demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;

public class LineChartDemo1 extends ApplicationFrame
{
  public LineChartDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(212.0D, "Classes", "JDK 1.0");
    localDefaultCategoryDataset.addValue(504.0D, "Classes", "JDK 1.1");
    localDefaultCategoryDataset.addValue(1520.0D, "Classes", "JDK 1.2");
    localDefaultCategoryDataset.addValue(1842.0D, "Classes", "JDK 1.3");
    localDefaultCategoryDataset.addValue(2991.0D, "Classes", "JDK 1.4");
    localDefaultCategoryDataset.addValue(3500.0D, "Classes", "JDK 1.5");
    return localDefaultCategoryDataset;
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createLineChart("Java Standard Class Library", null, "Class Count", paramCategoryDataset, PlotOrientation.VERTICAL, false, true, false);
    localJFreeChart.addSubtitle(new TextTitle("Number of Classes By Release"));
    TextTitle localTextTitle = new TextTitle("Source: Java In A Nutshell (5th Edition) by David Flanagan (O'Reilly)");
    localTextTitle.setFont(new Font("SansSerif", 0, 10));
    localTextTitle.setPosition(RectangleEdge.BOTTOM);
    localTextTitle.setHorizontalAlignment(HorizontalAlignment.RIGHT);
    localJFreeChart.addSubtitle(localTextTitle);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setRangePannable(true);
    localCategoryPlot.setRangeGridlinesVisible(false);
    URL localURL = LineChartDemo1.class.getClassLoader().getResource("OnBridge11small.png");
    if (localURL != null)
    {
      ImageIcon localObject = new ImageIcon(localURL);
      localJFreeChart.setBackgroundImage(((ImageIcon)localObject).getImage());
      localCategoryPlot.setBackgroundPaint(null);
    }
    Object localObject = (NumberAxis)localCategoryPlot.getRangeAxis();
    ((NumberAxis)localObject).setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    ChartUtilities.applyCurrentTheme(localJFreeChart);
    LineAndShapeRenderer localLineAndShapeRenderer = (LineAndShapeRenderer)localCategoryPlot.getRenderer();
    localLineAndShapeRenderer.setBaseShapesVisible(true);
    localLineAndShapeRenderer.setDrawOutlines(true);
    localLineAndShapeRenderer.setUseFillPaint(true);
    localLineAndShapeRenderer.setBaseFillPaint(Color.white);
    localLineAndShapeRenderer.setSeriesStroke(0, new BasicStroke(3.0F));
    localLineAndShapeRenderer.setSeriesOutlineStroke(0, new BasicStroke(2.0F));
    localLineAndShapeRenderer.setSeriesShape(0, new Ellipse2D.Double(-5.0D, -5.0D, 10.0D, 10.0D));
    return ((JFreeChart)localJFreeChart);
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
    LineChartDemo1 localLineChartDemo1 = new LineChartDemo1("JFreeChart: LineChartDemo1.java");
    localLineChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localLineChartDemo1);
    localLineChartDemo1.setVisible(true);
  }
}