package demo;

import java.awt.Color;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.jdbc.JDBCXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class JDBCXYChartDemo extends ApplicationFrame
{
  public JDBCXYChartDemo(String paramString)
  {
    super(paramString);
    XYDataset localXYDataset = readData();
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("JDBC XY Chart Demo", "Date", "Value", localXYDataset, true, true, false);
    localJFreeChart.setBackgroundPaint(Color.yellow);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    setContentPane(localChartPanel);
  }

  private XYDataset readData()
  {
    JDBCXYDataset localJDBCXYDataset = null;
    String str1 = "jdbc:postgresql://nomad/jfreechartdb";
    try
    {
      Class.forName("org.postgresql.Driver");
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      System.err.print("ClassNotFoundException: ");
      System.err.println(localClassNotFoundException.getMessage());
    }
    try
    {
      Connection localConnection = DriverManager.getConnection(str1, "jfreechart", "password");
      localJDBCXYDataset = new JDBCXYDataset(localConnection);
      String str2 = "SELECT * FROM XYDATA1;";
      localJDBCXYDataset.executeQuery(str2);
      localConnection.close();
    }
    catch (SQLException localSQLException)
    {
      System.err.print("SQLException: ");
      System.err.println(localSQLException.getMessage());
    }
    catch (Exception localException)
    {
      System.err.print("Exception: ");
      System.err.println(localException.getMessage());
    }
    return localJDBCXYDataset;
  }

  public static void main(String[] paramArrayOfString)
  {
    JDBCXYChartDemo localJDBCXYChartDemo = new JDBCXYChartDemo("JDBC XY Chart Demo");
    localJDBCXYChartDemo.pack();
    RefineryUtilities.centerFrameOnScreen(localJDBCXYChartDemo);
    localJDBCXYChartDemo.setVisible(true);
  }
}