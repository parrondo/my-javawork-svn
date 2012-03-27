package demo;

import java.awt.Color;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.PieDataset;
import org.jfree.data.jdbc.JDBCPieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class JDBCPieChartDemo extends ApplicationFrame
{
  public JDBCPieChartDemo(String paramString)
  {
    super(paramString);
    PieDataset localPieDataset = readData();
    JFreeChart localJFreeChart = ChartFactory.createPieChart("JDBC Pie Chart Demo", localPieDataset, true, true, false);
    localJFreeChart.setBackgroundPaint(Color.yellow);
    PiePlot localPiePlot = (PiePlot)localJFreeChart.getPlot();
    localPiePlot.setNoDataMessage("No data available");
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    setContentPane(localChartPanel);
  }

  private PieDataset readData()
  {
    JDBCPieDataset localJDBCPieDataset = null;
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
      localJDBCPieDataset = new JDBCPieDataset(localConnection);
      String str2 = "SELECT * FROM PIEDATA1;";
      localJDBCPieDataset.executeQuery(str2);
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
    return localJDBCPieDataset;
  }

  public static void main(String[] paramArrayOfString)
  {
    JDBCPieChartDemo localJDBCPieChartDemo = new JDBCPieChartDemo("JDBC Pie Chart Demo");
    localJDBCPieChartDemo.pack();
    RefineryUtilities.centerFrameOnScreen(localJDBCPieChartDemo);
    localJDBCPieChartDemo.setVisible(true);
  }
}