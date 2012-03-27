package demo;

import java.awt.Dimension;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultWindDataset;
import org.jfree.data.xy.WindDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class WindChartDemo1 extends ApplicationFrame
{
  public WindChartDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(localJPanel);
  }

  private static long millisForDate(int paramInt1, int paramInt2, int paramInt3)
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.set(paramInt3, paramInt2 - 1, paramInt1, 12, 0);
    return localCalendar.getTimeInMillis();
  }

  private static Object[] createItem(long paramLong, int paramInt1, int paramInt2)
  {
    return new Object[] { new Date(paramLong), new Integer(paramInt1), new Integer(paramInt2) };
  }

  public static WindDataset createDataset()
  {
    Object[] arrayOfObject1 = createItem(millisForDate(3, 1, 1999), 0, 10);
    Object[] arrayOfObject2 = createItem(millisForDate(4, 1, 1999), 1, 8);
    Object[] arrayOfObject3 = createItem(millisForDate(5, 1, 1999), 2, 10);
    Object[] arrayOfObject4 = createItem(millisForDate(6, 1, 1999), 3, 10);
    Object[] arrayOfObject5 = createItem(millisForDate(7, 1, 1999), 4, 7);
    Object[] arrayOfObject6 = createItem(millisForDate(8, 1, 1999), 5, 10);
    Object[] arrayOfObject7 = createItem(millisForDate(9, 1, 1999), 6, 8);
    Object[] arrayOfObject8 = createItem(millisForDate(10, 1, 1999), 7, 11);
    Object[] arrayOfObject9 = createItem(millisForDate(11, 1, 1999), 8, 10);
    Object[] arrayOfObject10 = createItem(millisForDate(12, 1, 1999), 9, 11);
    Object[] arrayOfObject11 = createItem(millisForDate(13, 1, 1999), 10, 3);
    Object[] arrayOfObject12 = createItem(millisForDate(14, 1, 1999), 11, 9);
    Object[] arrayOfObject13 = createItem(millisForDate(15, 1, 1999), 12, 11);
    Object[] arrayOfObject14 = createItem(millisForDate(16, 1, 1999), 0, 0);
    Object[][] arrayOfObject15 = { arrayOfObject1, arrayOfObject2, arrayOfObject3, arrayOfObject4, arrayOfObject5, arrayOfObject6, arrayOfObject7, arrayOfObject8, arrayOfObject9, arrayOfObject10, arrayOfObject11, arrayOfObject12, arrayOfObject13, arrayOfObject14 };
    Object[][][] arrayOfObject16 = {arrayOfObject15};
    return new DefaultWindDataset(arrayOfObject16);
  }

  private static JFreeChart createChart(WindDataset paramWindDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createWindPlot("Wind Chart Demo", "Date", "Direction / Force", paramWindDataset, true, false, false);
    return localJFreeChart;
  }

  public static JPanel createDemoPanel()
  {
    return new ChartPanel(createChart(createDataset()));
  }

  public static void main(String[] paramArrayOfString)
  {
    WindChartDemo1 localWindChartDemo1 = new WindChartDemo1("Wind Chart Demo 1");
    localWindChartDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localWindChartDemo1);
    localWindChartDemo1.setVisible(true);
  }
}