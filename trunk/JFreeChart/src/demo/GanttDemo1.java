package demo;

import java.awt.Dimension;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class GanttDemo1 extends ApplicationFrame
{
  public GanttDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  public static IntervalCategoryDataset createDataset()
  {
    TaskSeries localTaskSeries1 = new TaskSeries("Scheduled");
    localTaskSeries1.add(new Task("Write Proposal", new SimpleTimePeriod(date(1, 3, 2001), date(5, 3, 2001))));
    localTaskSeries1.add(new Task("Obtain Approval", new SimpleTimePeriod(date(9, 3, 2001), date(9, 3, 2001))));
    localTaskSeries1.add(new Task("Requirements Analysis", new SimpleTimePeriod(date(10, 3, 2001), date(5, 4, 2001))));
    localTaskSeries1.add(new Task("Design Phase", new SimpleTimePeriod(date(6, 4, 2001), date(30, 4, 2001))));
    localTaskSeries1.add(new Task("Design Signoff", new SimpleTimePeriod(date(2, 5, 2001), date(2, 5, 2001))));
    localTaskSeries1.add(new Task("Alpha Implementation", new SimpleTimePeriod(date(3, 5, 2001), date(31, 6, 2001))));
    localTaskSeries1.add(new Task("Design Review", new SimpleTimePeriod(date(1, 7, 2001), date(8, 7, 2001))));
    localTaskSeries1.add(new Task("Revised Design Signoff", new SimpleTimePeriod(date(10, 7, 2001), date(10, 7, 2001))));
    localTaskSeries1.add(new Task("Beta Implementation", new SimpleTimePeriod(date(12, 7, 2001), date(12, 8, 2001))));
    localTaskSeries1.add(new Task("Testing", new SimpleTimePeriod(date(13, 8, 2001), date(31, 9, 2001))));
    localTaskSeries1.add(new Task("Final Implementation", new SimpleTimePeriod(date(1, 10, 2001), date(15, 10, 2001))));
    localTaskSeries1.add(new Task("Signoff", new SimpleTimePeriod(date(28, 10, 2001), date(30, 10, 2001))));
    TaskSeries localTaskSeries2 = new TaskSeries("Actual");
    localTaskSeries2.add(new Task("Write Proposal", new SimpleTimePeriod(date(1, 3, 2001), date(5, 3, 2001))));
    localTaskSeries2.add(new Task("Obtain Approval", new SimpleTimePeriod(date(9, 3, 2001), date(9, 3, 2001))));
    localTaskSeries2.add(new Task("Requirements Analysis", new SimpleTimePeriod(date(10, 3, 2001), date(15, 4, 2001))));
    localTaskSeries2.add(new Task("Design Phase", new SimpleTimePeriod(date(15, 4, 2001), date(17, 5, 2001))));
    localTaskSeries2.add(new Task("Design Signoff", new SimpleTimePeriod(date(30, 5, 2001), date(30, 5, 2001))));
    localTaskSeries2.add(new Task("Alpha Implementation", new SimpleTimePeriod(date(1, 6, 2001), date(12, 8, 2001))));
    localTaskSeries2.add(new Task("Design Review", new SimpleTimePeriod(date(12, 8, 2001), date(22, 8, 2001))));
    localTaskSeries2.add(new Task("Revised Design Signoff", new SimpleTimePeriod(date(25, 8, 2001), date(27, 8, 2001))));
    localTaskSeries2.add(new Task("Beta Implementation", new SimpleTimePeriod(date(27, 8, 2001), date(30, 9, 2001))));
    localTaskSeries2.add(new Task("Testing", new SimpleTimePeriod(date(31, 9, 2001), date(17, 10, 2001))));
    localTaskSeries2.add(new Task("Final Implementation", new SimpleTimePeriod(date(18, 10, 2001), date(5, 11, 2001))));
    localTaskSeries2.add(new Task("Signoff", new SimpleTimePeriod(date(10, 11, 2001), date(11, 11, 2001))));
    TaskSeriesCollection localTaskSeriesCollection = new TaskSeriesCollection();
    localTaskSeriesCollection.add(localTaskSeries1);
    localTaskSeriesCollection.add(localTaskSeries2);
    return localTaskSeriesCollection;
  }

  private static Date date(int paramInt1, int paramInt2, int paramInt3)
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.set(paramInt3, paramInt2, paramInt1);
    Date localDate = localCalendar.getTime();
    return localDate;
  }

  private static JFreeChart createChart(IntervalCategoryDataset paramIntervalCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createGanttChart("Gantt Chart Demo", "Task", "Date", paramIntervalCategoryDataset, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setRangePannable(true);
    localCategoryPlot.getDomainAxis().setMaximumCategoryLabelWidthRatio(10.0F);
    localCategoryPlot.setRangeCrosshairVisible(true);
    GanttRenderer localGanttRenderer = (GanttRenderer)localCategoryPlot.getRenderer();
    localGanttRenderer.setDrawBarOutline(false);
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
    GanttDemo1 localGanttDemo1 = new GanttDemo1("JFreeChart: GanttDemo1.java");
    localGanttDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localGanttDemo1);
    localGanttDemo1.setVisible(true);
  }
}