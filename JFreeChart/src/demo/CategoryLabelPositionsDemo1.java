package demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class CategoryLabelPositionsDemo1 extends ApplicationFrame
{
  static JFreeChart chart;
  static JCheckBox invertCheckBox;
  static JRadioButton horizontalRadioButton;
  static JRadioButton verticalRadioButton;
  static JSlider slider;

  public CategoryLabelPositionsDemo1(String paramString)
  {
    super(paramString);
    JPanel localJPanel = createDemoPanel();
    localJPanel.setPreferredSize(new Dimension(500, 350));
    setContentPane(localJPanel);
  }

  private static JFreeChart createChart(CategoryDataset paramCategoryDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createBarChart("CategoryLabelPositionsDemo1", "Category", "Value", paramCategoryDataset, PlotOrientation.VERTICAL, false, false, false);
    CategoryPlot localCategoryPlot = (CategoryPlot)localJFreeChart.getPlot();
    CategoryAxis localCategoryAxis = localCategoryPlot.getDomainAxis();
    localCategoryAxis.setMaximumCategoryLabelLines(2147483647);
    localCategoryAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(0.7853981633974483D));
    return localJFreeChart;
  }

  public static CategoryDataset createDataset()
  {
    DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
    localDefaultCategoryDataset.addValue(1.0D, "R1", "Category 1 (Long)");
    localDefaultCategoryDataset.addValue(5.0D, "R1", "Category 2 (Long)");
    localDefaultCategoryDataset.addValue(3.0D, "R1", "Category 3 (Long)");
    localDefaultCategoryDataset.addValue(2.0D, "R1", "Category 4 (Long)");
    localDefaultCategoryDataset.addValue(9.0D, "R1", "Category 5 (Long)");
    localDefaultCategoryDataset.addValue(7.0D, "R1", "Category 6 (Long)");
    localDefaultCategoryDataset.addValue(6.0D, "R1", "Category 7 (Long)");
    localDefaultCategoryDataset.addValue(8.0D, "R1", "Category 8 (Long)");
    return localDefaultCategoryDataset;
  }

  public static JPanel createDemoPanel()
  {
    CategoryDataset localCategoryDataset = createDataset();
    chart = createChart(localCategoryDataset);
    DemoPanel localDemoPanel = new DemoPanel(new BorderLayout());
    JPanel localJPanel1 = new JPanel(new BorderLayout());
    JPanel localJPanel2 = new JPanel();
    invertCheckBox = new JCheckBox("Invert Range Axis?");
    invertCheckBox.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramActionEvent)
      {
        CategoryPlot localCategoryPlot = (CategoryPlot)CategoryLabelPositionsDemo1.chart.getPlot();
        localCategoryPlot.getRangeAxis().setInverted(CategoryLabelPositionsDemo1.invertCheckBox.isSelected());
      }
    });
    localJPanel2.add(invertCheckBox);
    ButtonGroup localButtonGroup = new ButtonGroup();
    horizontalRadioButton = new JRadioButton("Horizontal", false);
    horizontalRadioButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramActionEvent)
      {
        if (!(CategoryLabelPositionsDemo1.horizontalRadioButton.isSelected()))
          return;
        CategoryPlot localCategoryPlot = (CategoryPlot)CategoryLabelPositionsDemo1.chart.getPlot();
        localCategoryPlot.setOrientation(PlotOrientation.HORIZONTAL);
      }
    });
    localButtonGroup.add(horizontalRadioButton);
    verticalRadioButton = new JRadioButton("Vertical", true);
    verticalRadioButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramActionEvent)
      {
        if (!(CategoryLabelPositionsDemo1.verticalRadioButton.isSelected()))
          return;
        CategoryPlot localCategoryPlot = (CategoryPlot)CategoryLabelPositionsDemo1.chart.getPlot();
        localCategoryPlot.setOrientation(PlotOrientation.VERTICAL);
      }
    });
    localButtonGroup.add(verticalRadioButton);
    localJPanel2.add(horizontalRadioButton);
    localJPanel2.add(verticalRadioButton);
    localJPanel2.setBorder(new TitledBorder("Plot Settings: "));
    JPanel localJPanel3 = new JPanel(new BorderLayout());
    slider = new JSlider(0, 90, 45);
    slider.setMajorTickSpacing(10);
    slider.setMinorTickSpacing(5);
    slider.setPaintLabels(true);
    slider.setPaintTicks(true);
    slider.addChangeListener(new ChangeListener()
    {
      public void stateChanged(ChangeEvent paramChangeEvent)
      {
        CategoryPlot localCategoryPlot = (CategoryPlot)CategoryLabelPositionsDemo1.chart.getPlot();
        CategoryAxis localCategoryAxis = localCategoryPlot.getDomainAxis();
        localCategoryAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(CategoryLabelPositionsDemo1.slider.getValue() * 3.141592653589793D / 180.0D));
      }
    });
    localJPanel3.add(slider);
    localJPanel3.setBorder(new TitledBorder("Axis Label Rotation Angle:"));
    localJPanel1.add("North", localJPanel2);
    localJPanel1.add(localJPanel3);
    localDemoPanel.add(new ChartPanel(chart));
    localDemoPanel.addChart(chart);
    localDemoPanel.add("South", localJPanel1);
    return localDemoPanel;
  }

  public static void main(String[] paramArrayOfString)
  {
    CategoryLabelPositionsDemo1 localCategoryLabelPositionsDemo1 = new CategoryLabelPositionsDemo1("JFreeChart: CategoryLabelPositionsDemo1.java");
    localCategoryLabelPositionsDemo1.pack();
    RefineryUtilities.centerFrameOnScreen(localCategoryLabelPositionsDemo1);
    localCategoryLabelPositionsDemo1.setVisible(true);
  }
}