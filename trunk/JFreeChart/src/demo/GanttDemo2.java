package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class GanttDemo2 extends ApplicationFrame
{
  public GanttDemo2(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(IntervalCategoryDataset paramIntervalCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createGanttChart("Gantt Chart Demo", "Task", "Date", paramIntervalCategoryDataset, true, true, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    localCategoryPlot.setRangePannable(true);
    localCategoryPlot.getDomainAxis().setMaximumCategoryLabelWidthRatio(10.0F);
    CategoryItemRenderer localCategoryItemRenderer = localCategoryPlot.getRenderer();
    localCategoryItemRenderer.setSeriesPaint(0, Color.blue);
    return localJFreeChart;
  }

  private static IntervalCategoryDataset createDataset()
  {
    TaskSeries localTaskSeries = new TaskSeries("Scheduled");
    Task localTask1 = new Task("Write Proposal", date(1, 3, 2001), date(5, 3, 2001));
    localTask1.setPercentComplete(1.0D);
    localTaskSeries.add(localTask1);
    Task localTask2 = new Task("Obtain Approval", date(9, 3, 2001), date(9, 3, 2001));
    localTask2.setPercentComplete(1.0D);
    localTaskSeries.add(localTask2);
    Task localTask3 = new Task("Requirements Analysis", date(10, 3, 2001), date(5, 4, 2001));
    Task localTask4 = new Task("Requirements 1", date(10, 3, 2001), date(25, 3, 2001));
    localTask4.setPercentComplete(1.0D);
    Task localTask5 = new Task("Requirements 2", date(1, 4, 2001), date(5, 4, 2001));
    localTask5.setPercentComplete(1.0D);
    localTask3.addSubtask(localTask4);
    localTask3.addSubtask(localTask5);
    localTaskSeries.add(localTask3);
    Task localTask6 = new Task("Design Phase", date(6, 4, 2001), date(30, 4, 2001));
    Task localTask7 = new Task("Design 1", date(6, 4, 2001), date(10, 4, 2001));
    localTask7.setPercentComplete(1.0D);
    Task localTask8 = new Task("Design 2", date(15, 4, 2001), date(20, 4, 2001));
    localTask8.setPercentComplete(1.0D);
    Task localTask9 = new Task("Design 3", date(23, 4, 2001), date(30, 4, 2001));
    localTask9.setPercentComplete(0.5D);
    localTask6.addSubtask(localTask7);
    localTask6.addSubtask(localTask8);
    localTask6.addSubtask(localTask9);
    localTaskSeries.add(localTask6);
    Task localTask10 = new Task("Design Signoff", date(2, 5, 2001), date(2, 5, 2001));
    localTaskSeries.add(localTask10);
    Task localTask11 = new Task("Alpha Implementation", date(3, 5, 2001), date(31, 6, 2001));
    localTask11.setPercentComplete(0.6D);
    localTaskSeries.add(localTask11);
    Task localTask12 = new Task("Design Review", date(1, 7, 2001), date(8, 7, 2001));
    localTask12.setPercentComplete(0.0D);
    localTaskSeries.add(localTask12);
    Task localTask13 = new Task("Revised Design Signoff", date(10, 7, 2001), date(10, 7, 2001));
    localTask13.setPercentComplete(0.0D);
    localTaskSeries.add(localTask13);
    Task localTask14 = new Task("Beta Implementation", date(12, 7, 2001), date(12, 8, 2001));
    localTask14.setPercentComplete(0.0D);
    localTaskSeries.add(localTask14);
    Task localTask15 = new Task("Testing", date(13, 8, 2001), date(31, 9, 2001));
    localTask15.setPercentComplete(0.0D);
    localTaskSeries.add(localTask15);
    Task localTask16 = new Task("Final Implementation", date(1, 10, 2001), date(15, 10, 2001));
    localTask16.setPercentComplete(0.0D);
    localTaskSeries.add(localTask16);
    Task localTask17 = new Task("Signoff", date(28, 10, 2001), date(30, 10, 2001));
    localTask17.setPercentComplete(0.0D);
    localTaskSeries.add(localTask17);
    TaskSeriesCollection localTaskSeriesCollection = new TaskSeriesCollection();
    localTaskSeriesCollection.add(localTaskSeries);
    return localTaskSeriesCollection;
  }

  private static Date date(int paramInt1, int paramInt2, int paramInt3)
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.set(paramInt3, paramInt2, paramInt1);
    Date localDate = localCalendar.getTime();
    return localDate;
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
    GanttDemo2 localGanttDemo2 = new GanttDemo2("JFreeChart: GanttDemo2.java");
    localGanttDemo2.pack();
    RefineryUtilities.centerFrameOnScreen(localGanttDemo2);
    localGanttDemo2.setVisible(true);
  }
}