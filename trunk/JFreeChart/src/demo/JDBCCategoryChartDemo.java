package demo;

import java.awt.Color;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.jdbc.JDBCCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class JDBCCategoryChartDemo extends ApplicationFrame
{
  public JDBCCategoryChartDemo(String paramString)
  {
    super(paramString);
    CategoryDataset localCategoryDataset = readData();
    JFreeChart localJFreeChart = ChartFactory.createBarChart3D("JDBC Category Chart Demo", "Category", "Value", localCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
    localJFreeChart.setBackgroundPaint(Color.yellow);
    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
    setContentPane(localChartPanel);
  }

  private CategoryDataset readData()
  {
    JDBCCategoryDataset localJDBCCategoryDataset = null;
    String str1 = "jdbc:postgresql://localhost/jfreechartdb";
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
      localJDBCCategoryDataset = new JDBCCategoryDataset(localConnection);
      String str2 = "SELECT * FROM CATEGORYDATA1;";
      System.out.println("Once...");
      localJDBCCategoryDataset.executeQuery(str2);
      System.out.println("Again...");
      localJDBCCategoryDataset.executeQuery(str2);
      System.out.println("Done.");
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
    return localJDBCCategoryDataset;
  }

  public static void main(String[] paramArrayOfString)
  {
    JDBCCategoryChartDemo localJDBCCategoryChartDemo = new JDBCCategoryChartDemo("JDBC Category Chart Demo");
    localJDBCCategoryChartDemo.pack();
    RefineryUtilities.centerFrameOnScreen(localJDBCCategoryChartDemo);
    localJDBCCategoryChartDemo.setVisible(true);
  }
}