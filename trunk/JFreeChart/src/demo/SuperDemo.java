package demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartTransferable;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.FontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

public class SuperDemo extends ApplicationFrame
  implements ActionListener, TreeSelectionListener
{
  public static final String EXIT_COMMAND = "EXIT";
  private JPanel displayPanel;
  private JPanel chartContainer;
  private JPanel descriptionContainer;
  private JTextPane descriptionPane;
  private JEditorPane editorPane;
  private TreePath defaultChartPath;

  public SuperDemo(String paramString)
  {
    super(paramString);
    setContentPane(createContent());
    setJMenuBar(createMenuBar());
  }

  private JComponent createContent()
  {
    JPanel localJPanel1 = new JPanel(new BorderLayout());
    JTabbedPane localJTabbedPane = new JTabbedPane();
    JPanel localJPanel2 = new JPanel(new BorderLayout());
    localJPanel2.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    JSplitPane localJSplitPane = new JSplitPane(1);
    JTree localJTree = new JTree(createTreeModel());
    localJTree.addTreeSelectionListener(this);
    JScrollPane localJScrollPane = new JScrollPane(localJTree);
    localJScrollPane.setPreferredSize(new Dimension(300, 100));
    localJSplitPane.setLeftComponent(localJScrollPane);
    localJSplitPane.setRightComponent(createChartDisplayPanel());
    localJPanel2.add(localJSplitPane);
    localJTabbedPane.add("Demos", localJPanel2);
    MemoryUsageDemo localMemoryUsageDemo = new MemoryUsageDemo(300000);
    localMemoryUsageDemo.new DataGenerator(1000).start();
    localJTabbedPane.add("Memory Usage", localMemoryUsageDemo);
    localJTabbedPane.add("Source Code", createSourceCodePanel());
    localJTabbedPane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    localJPanel1.add(localJTabbedPane);
    localJTree.setSelectionPath(this.defaultChartPath);
    return localJPanel1;
  }

  private JMenuBar createMenuBar()
  {
    JMenuBar localJMenuBar = new JMenuBar();
    JMenu localJMenu1 = new JMenu("File", true);
    localJMenu1.setMnemonic('F');
    JMenuItem localJMenuItem1 = new JMenuItem("Export to PDF...", 112);
    localJMenuItem1.setActionCommand("EXPORT_TO_PDF");
    localJMenuItem1.addActionListener(this);
    localJMenu1.add(localJMenuItem1);
    localJMenu1.addSeparator();
    JMenuItem localJMenuItem2 = new JMenuItem("Exit", 120);
    localJMenuItem2.setActionCommand("EXIT");
    localJMenuItem2.addActionListener(this);
    localJMenu1.add(localJMenuItem2);
    localJMenuBar.add(localJMenu1);
    JMenu localJMenu2 = new JMenu("Edit", false);
    localJMenuBar.add(localJMenu2);
    JMenuItem localJMenuItem3 = new JMenuItem("Copy", 67);
    localJMenuItem3.setActionCommand("COPY");
    localJMenuItem3.addActionListener(this);
    localJMenu2.add(localJMenuItem3);
    JMenu localJMenu3 = new JMenu("Theme", true);
    localJMenu3.setMnemonic('T');
    JCheckBoxMenuItem localJCheckBoxMenuItem1 = new JCheckBoxMenuItem("JFree", true);
    localJCheckBoxMenuItem1.setActionCommand("JFREE_THEME");
    localJCheckBoxMenuItem1.addActionListener(this);
    localJMenu3.add(localJCheckBoxMenuItem1);
    JCheckBoxMenuItem localJCheckBoxMenuItem2 = new JCheckBoxMenuItem("Darkness", false);
    localJCheckBoxMenuItem2.setActionCommand("DARKNESS_THEME");
    localJCheckBoxMenuItem2.addActionListener(this);
    localJMenu3.add(localJCheckBoxMenuItem2);
    JCheckBoxMenuItem localJCheckBoxMenuItem3 = new JCheckBoxMenuItem("Legacy", false);
    localJCheckBoxMenuItem3.setActionCommand("LEGACY_THEME");
    localJCheckBoxMenuItem3.addActionListener(this);
    localJMenu3.add(localJCheckBoxMenuItem3);
    ButtonGroup localButtonGroup = new ButtonGroup();
    localButtonGroup.add(localJCheckBoxMenuItem1);
    localButtonGroup.add(localJCheckBoxMenuItem2);
    localButtonGroup.add(localJCheckBoxMenuItem3);
    localJMenuBar.add(localJMenu3);
    return localJMenuBar;
  }

  private JPanel createSourceCodePanel()
  {
    JPanel localJPanel = new JPanel(new BorderLayout());
    localJPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    this.editorPane = new JEditorPane();
    this.editorPane.setEditable(false);
    this.editorPane.setFont(new Font("Monospaced", 0, 12));
    updateSourceCodePanel("source.html");
    JScrollPane localJScrollPane = new JScrollPane(this.editorPane);
    localJScrollPane.setVerticalScrollBarPolicy(20);
    localJScrollPane.setPreferredSize(new Dimension(250, 145));
    localJScrollPane.setMinimumSize(new Dimension(10, 10));
    localJPanel.add(localJScrollPane);
    return localJPanel;
  }

  private void updateSourceCodePanel(String paramString)
  {
    URL localURL = null;
    if (paramString != null)
      SuperDemo.class.getResource(paramString);
    if (localURL == null)
      localURL = SuperDemo.class.getResource("source.html");
    if (localURL != null)
      try
      {
        this.editorPane.setPage(localURL);
      }
      catch (IOException localIOException)
      {
        System.err.println("Attempted to read a bad URL: " + localURL);
      }
    else
      System.err.println("Couldn't find file: source.html");
  }

  private void copyToClipboard()
  {
    JFreeChart localJFreeChart = null;
    int i = 0;
    int j = 0;
    Component localComponent = this.chartContainer.getComponent(0);
    if (localComponent instanceof ChartPanel)
    {
      ChartPanel localObject = (ChartPanel)localComponent;
      localJFreeChart = ((ChartPanel)localObject).getChart();
      i = ((ChartPanel)localObject).getWidth();
      j = ((ChartPanel)localObject).getHeight();
    }
    else if (localComponent instanceof DemoPanel)
    {
      DemoPanel localObject = (DemoPanel)localComponent;
      localJFreeChart = (JFreeChart)((DemoPanel)localObject).charts.get(0);
      i = ((DemoPanel)localObject).getWidth();
      j = ((DemoPanel)localObject).getHeight();
    }
    if (localJFreeChart == null)
      return;
    Object localObject = Toolkit.getDefaultToolkit().getSystemClipboard();
    ChartTransferable localChartTransferable = new ChartTransferable(localJFreeChart, i, j);
    ((Clipboard)localObject).setContents(localChartTransferable, null);
  }

  public void actionPerformed(ActionEvent paramActionEvent)
  {
    String str = paramActionEvent.getActionCommand();
    if (str.equals("EXPORT_TO_PDF"))
    {
      exportToPDF();
    }
    else if (str.equals("COPY"))
    {
      copyToClipboard();
    }
    else if (str.equals("LEGACY_THEME"))
    {
      ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
      applyThemeToChart();
    }
    else if (str.equals("JFREE_THEME"))
    {
      ChartFactory.setChartTheme(StandardChartTheme.createJFreeTheme());
      applyThemeToChart();
    }
    else if (str.equals("DARKNESS_THEME"))
    {
      ChartFactory.setChartTheme(StandardChartTheme.createDarknessTheme());
      applyThemeToChart();
    }
    else
    {
      if (!(str.equals("EXIT")))
        return;
      attemptExit();
    }
  }

  private void applyThemeToChart()
  {
    Object localObject;
    Component localComponent = this.chartContainer.getComponent(0);
    if (localComponent instanceof ChartPanel)
    {
      localObject = (ChartPanel)localComponent;
      ChartUtilities.applyCurrentTheme(((ChartPanel)localObject).getChart());
    }
    else
    {
      if (!(localComponent instanceof DemoPanel))
        return;
      localObject = (DemoPanel)localComponent;
      JFreeChart[] arrayOfJFreeChart = ((DemoPanel)localObject).getCharts();
      for (int i = 0; i < arrayOfJFreeChart.length; ++i)
        ChartUtilities.applyCurrentTheme(arrayOfJFreeChart[i]);
    }
  }

  private void exportToPDF()
  {
    Object localObject;
    JFreeChart localJFreeChart1 = null;
    int i = 0;
    int j = 0;
    Component localComponent = this.chartContainer.getComponent(0);
    if (localComponent instanceof ChartPanel)
    {
      localObject = (ChartPanel)localComponent;
      localJFreeChart1 = ((ChartPanel)localObject).getChart();
      i = ((ChartPanel)localObject).getWidth();
      j = ((ChartPanel)localObject).getHeight();
    }
    else if (localComponent instanceof DemoPanel)
    {
      localObject = (DemoPanel)localComponent;
      localJFreeChart1 = (JFreeChart)((DemoPanel)localObject).charts.get(0);
      i = ((DemoPanel)localObject).getWidth();
      j = ((DemoPanel)localObject).getHeight();
    }
    if (localJFreeChart1 != null)
    {
      localObject = new JFileChooser();
      ((JFileChooser)localObject).setName("untitled.pdf");
      ((JFileChooser)localObject).setFileFilter(new FileFilter()
      {
        public boolean accept(File paramFile)
        {
          return ((paramFile.isDirectory()) || (paramFile.getName().endsWith(".pdf")));
        }

        public String getDescription()
        {
          return "Portable Document Format (PDF)";
        }
      });
      int k = ((JFileChooser)localObject).showSaveDialog(this);
      if (k == 0)
        try
        {
          JFreeChart localJFreeChart2 = (JFreeChart)localJFreeChart1.clone();
          PDFExportTask localPDFExportTask = new PDFExportTask(localJFreeChart2, i, j, ((JFileChooser)localObject).getSelectedFile());
          Thread localThread = new Thread(localPDFExportTask);
          localThread.start();
        }
        catch (CloneNotSupportedException localCloneNotSupportedException)
        {
          localCloneNotSupportedException.printStackTrace();
        }
    }
    else
    {
      localObject = "Unable to export the selected item.  There is ";
      localObject = ((String)localObject) + "either no chart selected,\nor else the chart is not ";
      localObject = ((String)localObject) + "at the expected location in the component hierarchy\n";
      localObject = ((String)localObject) + "(future versions of the demo may include code to ";
      localObject = ((String)localObject) + "handle these special cases).";
      JOptionPane.showMessageDialog(this, localObject, "PDF Export", 1);
    }
  }

  public static void writeChartAsPDF(OutputStream paramOutputStream, JFreeChart paramJFreeChart, int paramInt1, int paramInt2, FontMapper paramFontMapper)
    throws IOException
  {
    Rectangle localRectangle = new Rectangle(paramInt1, paramInt2);
    Document localDocument = new Document(localRectangle, 50.0F, 50.0F, 50.0F, 50.0F);
    try
    {
      PdfWriter localPdfWriter = PdfWriter.getInstance(localDocument, paramOutputStream);
      localDocument.addAuthor("JFreeChart");
      localDocument.addSubject("Demonstration");
      localDocument.open();
      PdfContentByte localPdfContentByte = localPdfWriter.getDirectContent();
      PdfTemplate localPdfTemplate = localPdfContentByte.createTemplate(paramInt1, paramInt2);
      Graphics2D localGraphics2D = localPdfTemplate.createGraphics(paramInt1, paramInt2, paramFontMapper);
      Rectangle2D.Double localDouble = new Rectangle2D.Double(0.0D, 0.0D, paramInt1, paramInt2);
      paramJFreeChart.draw(localGraphics2D, localDouble);
      localGraphics2D.dispose();
      localPdfContentByte.addTemplate(localPdfTemplate, 0.0F, 0.0F);
    }
    catch (DocumentException localDocumentException)
    {
      System.err.println(localDocumentException.getMessage());
    }
    localDocument.close();
  }

  public static void saveChartAsPDF(File paramFile, JFreeChart paramJFreeChart, int paramInt1, int paramInt2, FontMapper paramFontMapper)
    throws IOException
  {
    BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(paramFile));
    writeChartAsPDF(localBufferedOutputStream, paramJFreeChart, paramInt1, paramInt2, paramFontMapper);
    localBufferedOutputStream.close();
  }

  private void attemptExit()
  {
    String str1 = "Confirm";
    String str2 = "Are you sure you want to exit the demo?";
    int i = JOptionPane.showConfirmDialog(this, str2, str1, 0, 3);
    if (i != 0)
      return;
    dispose();
    System.exit(0);
  }

  private JPanel createChartDisplayPanel()
  {
    this.displayPanel = new JPanel(new BorderLayout());
    this.chartContainer = new JPanel(new BorderLayout());
    this.chartContainer.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4), BorderFactory.createLineBorder(Color.black)));
    this.chartContainer.add(createNoDemoSelectedPanel());
    this.descriptionContainer = new JPanel(new BorderLayout());
    this.descriptionContainer.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    this.descriptionContainer.setPreferredSize(new Dimension(600, 140));
    this.descriptionPane = new JTextPane();
    this.descriptionPane.setEditable(false);
    JScrollPane localJScrollPane = new JScrollPane(this.descriptionPane, 20, 31);
    this.descriptionContainer.add(localJScrollPane);
    displayDescription("select.html");
    JSplitPane localJSplitPane = new JSplitPane(0);
    localJSplitPane.setTopComponent(this.chartContainer);
    localJSplitPane.setBottomComponent(this.descriptionContainer);
    this.displayPanel.add(localJSplitPane);
    localJSplitPane.setDividerLocation(0.75D);
    return this.displayPanel;
  }

  private TreeModel createTreeModel()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("JFreeChart");
    MutableTreeNode localMutableTreeNode = createShowcaseNode(localDefaultMutableTreeNode);
    localDefaultMutableTreeNode.add(localMutableTreeNode);
    localDefaultMutableTreeNode.add(createAreaChartsNode());
    localDefaultMutableTreeNode.add(createBarChartsNode());
    localDefaultMutableTreeNode.add(createStackedBarChartsNode());
    localDefaultMutableTreeNode.add(createCombinedAxisChartsNode());
    localDefaultMutableTreeNode.add(createFinancialChartsNode());
    localDefaultMutableTreeNode.add(createGanttChartsNode());
    localDefaultMutableTreeNode.add(createLineChartsNode());
    localDefaultMutableTreeNode.add(createMeterChartsNode());
    localDefaultMutableTreeNode.add(createMultipleAxisChartsNode());
    localDefaultMutableTreeNode.add(createOverlaidChartsNode());
    localDefaultMutableTreeNode.add(createPieChartsNode());
    localDefaultMutableTreeNode.add(createStatisticalChartsNode());
    localDefaultMutableTreeNode.add(createTimeSeriesChartsNode());
    localDefaultMutableTreeNode.add(createXYChartsNode());
    localDefaultMutableTreeNode.add(createMiscellaneousChartsNode());
    return new DefaultTreeModel(localDefaultMutableTreeNode);
  }

  private MutableTreeNode createPieChartsNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("Pie Charts");
    localDefaultMutableTreeNode.add(createNode("demo.PieChartDemo1", "PieChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.PieChartDemo2", "PieChartDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.PieChartDemo3", "PieChartDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.PieChartDemo4", "PieChartDemo4.java"));
    localDefaultMutableTreeNode.add(createNode("demo.PieChartDemo5", "PieChartDemo5.java"));
    localDefaultMutableTreeNode.add(createNode("demo.PieChartDemo6", "PieChartDemo6.java"));
    localDefaultMutableTreeNode.add(createNode("demo.PieChartDemo7", "PieChartDemo7.java"));
    localDefaultMutableTreeNode.add(createNode("demo.PieChartDemo8", "PieChartDemo8.java"));
    localDefaultMutableTreeNode.add(createNode("demo.PieChart3DDemo1", "PieChart3DDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.PieChart3DDemo2", "PieChart3DDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.PieChart3DDemo3", "PieChart3DDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.MultiplePieChartDemo1", "MultiplePieChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.MultiplePieChartDemo2", "MultiplePieChartDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.MultiplePieChartDemo3", "MultiplePieChartDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.MultiplePieChartDemo4", "MultiplePieChartDemo4.java"));
    localDefaultMutableTreeNode.add(createNode("demo.RingChartDemo1", "RingChartDemo1.java"));
    return localDefaultMutableTreeNode;
  }

  private MutableTreeNode createOverlaidChartsNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("Overlaid Charts");
    localDefaultMutableTreeNode.add(createNode("demo.OverlaidBarChartDemo1", "OverlaidBarChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.OverlaidBarChartDemo2", "OverlaidBarChartDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.OverlaidXYPlotDemo1", "OverlaidXYPlotDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.OverlaidXYPlotDemo2", "OverlaidXYPlotDemo2.java"));
    return localDefaultMutableTreeNode;
  }

  private MutableTreeNode createBarChartsNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("Bar Charts");
    localDefaultMutableTreeNode.add(createCategoryBarChartsNode());
    localDefaultMutableTreeNode.add(createXYBarChartsNode());
    return localDefaultMutableTreeNode;
  }

  private MutableTreeNode createStackedBarChartsNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("Bar Charts - Stacked");
    localDefaultMutableTreeNode.add(createNode("demo.PopulationChartDemo1", "PopulationChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.StackedBarChartDemo1", "StackedBarChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.StackedBarChartDemo2", "StackedBarChartDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.StackedBarChartDemo3", "StackedBarChartDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.StackedBarChartDemo4", "StackedBarChartDemo4.java"));
    localDefaultMutableTreeNode.add(createNode("demo.StackedBarChartDemo5", "StackedBarChartDemo5.java"));
    localDefaultMutableTreeNode.add(createNode("demo.StackedBarChartDemo6", "StackedBarChartDemo6.java"));
    localDefaultMutableTreeNode.add(createNode("demo.StackedBarChartDemo7", "StackedBarChartDemo7.java"));
    localDefaultMutableTreeNode.add(createNode("demo.StackedBarChart3DDemo1", "StackedBarChart3DDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.StackedBarChart3DDemo2", "StackedBarChart3DDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.StackedBarChart3DDemo3", "StackedBarChart3DDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.StackedBarChart3DDemo4", "StackedBarChart3DDemo4.java"));
    localDefaultMutableTreeNode.add(createNode("demo.StackedBarChart3DDemo5", "StackedBarChart3DDemo5.java"));
    return localDefaultMutableTreeNode;
  }

  private MutableTreeNode createCategoryBarChartsNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("CategoryPlot");
    localDefaultMutableTreeNode.add(createNode("demo.BarChartDemo1", "BarChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.BarChartDemo2", "BarChartDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.BarChartDemo3", "BarChartDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.BarChartDemo4", "BarChartDemo4.java"));
    localDefaultMutableTreeNode.add(createNode("demo.BarChartDemo5", "BarChartDemo5.java"));
    localDefaultMutableTreeNode.add(createNode("demo.BarChartDemo6", "BarChartDemo6.java"));
    localDefaultMutableTreeNode.add(createNode("demo.BarChartDemo7", "BarChartDemo7.java"));
    localDefaultMutableTreeNode.add(createNode("demo.BarChartDemo8", "BarChartDemo8.java"));
    localDefaultMutableTreeNode.add(createNode("demo.BarChartDemo9", "BarChartDemo9.java"));
    localDefaultMutableTreeNode.add(createNode("demo.BarChartDemo10", "BarChartDemo10.java"));
    localDefaultMutableTreeNode.add(createNode("demo.BarChartDemo11", "BarChartDemo11.java"));
    localDefaultMutableTreeNode.add(createNode("demo.BarChart3DDemo1", "BarChart3DDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.BarChart3DDemo2", "BarChart3DDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.BarChart3DDemo3", "BarChart3DDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.BarChart3DDemo4", "BarChart3DDemo4.java"));
    localDefaultMutableTreeNode.add(createNode("demo.CylinderChartDemo1", "CylinderChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.CylinderChartDemo2", "CylinderChartDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.IntervalBarChartDemo1", "IntervalBarChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.LayeredBarChartDemo1", "LayeredBarChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.LayeredBarChartDemo2", "LayeredBarChartDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.SlidingCategoryDatasetDemo1", "SlidingCategoryDatasetDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.SlidingCategoryDatasetDemo2", "SlidingCategoryDatasetDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.StatisticalBarChartDemo1", "StatisticalBarChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.SurveyResultsDemo1", "SurveyResultsDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.SurveyResultsDemo2", "SurveyResultsDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.SurveyResultsDemo3", "SurveyResultsDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.WaterfallChartDemo1", "WaterfallChartDemo1.java"));
    return localDefaultMutableTreeNode;
  }

  private MutableTreeNode createXYBarChartsNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("XYPlot");
    localDefaultMutableTreeNode.add(createNode("demo.XYBarChartDemo1", "XYBarChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYBarChartDemo2", "XYBarChartDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYBarChartDemo3", "XYBarChartDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYBarChartDemo4", "XYBarChartDemo4.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYBarChartDemo5", "XYBarChartDemo5.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYBarChartDemo6", "XYBarChartDemo6.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYBarChartDemo7", "XYBarChartDemo7.java"));
    localDefaultMutableTreeNode.add(createNode("demo.ClusteredXYBarRendererDemo1", "ClusteredXYBarRendererDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.StackedXYBarChartDemo1", "StackedXYBarChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.StackedXYBarChartDemo2", "StackedXYBarChartDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.StackedXYBarChartDemo3", "StackedXYBarChartDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.RelativeDateFormatDemo1", "RelativeDateFormatDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.RelativeDateFormatDemo2", "RelativeDateFormatDemo2.java"));
    return localDefaultMutableTreeNode;
  }

  private MutableTreeNode createLineChartsNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode1 = new DefaultMutableTreeNode("Line Charts");
    DefaultMutableTreeNode localDefaultMutableTreeNode2 = new DefaultMutableTreeNode(new DemoDescription("demo.AnnotationDemo1", "AnnotationDemo1.java"));
    DefaultMutableTreeNode localDefaultMutableTreeNode3 = new DefaultMutableTreeNode(new DemoDescription("demo.LineChartDemo1", "LineChartDemo1.java"));
    DefaultMutableTreeNode localDefaultMutableTreeNode4 = new DefaultMutableTreeNode(new DemoDescription("demo.LineChartDemo2", "LineChartDemo2.java"));
    DefaultMutableTreeNode localDefaultMutableTreeNode5 = new DefaultMutableTreeNode(new DemoDescription("demo.LineChartDemo3", "LineChartDemo3.java"));
    DefaultMutableTreeNode localDefaultMutableTreeNode6 = new DefaultMutableTreeNode(new DemoDescription("demo.LineChartDemo4", "LineChartDemo4.java"));
    DefaultMutableTreeNode localDefaultMutableTreeNode7 = new DefaultMutableTreeNode(new DemoDescription("demo.LineChartDemo5", "LineChartDemo5.java"));
    DefaultMutableTreeNode localDefaultMutableTreeNode8 = new DefaultMutableTreeNode(new DemoDescription("demo.LineChartDemo6", "LineChartDemo6.java"));
    DefaultMutableTreeNode localDefaultMutableTreeNode9 = new DefaultMutableTreeNode(new DemoDescription("demo.LineChartDemo7", "LineChartDemo7.java"));
    DefaultMutableTreeNode localDefaultMutableTreeNode10 = new DefaultMutableTreeNode(new DemoDescription("demo.LineChartDemo8", "LineChartDemo8.java"));
    DefaultMutableTreeNode localDefaultMutableTreeNode11 = new DefaultMutableTreeNode(new DemoDescription("demo.LineChart3DDemo1", "LineChart3DDemo1.java"));
    DefaultMutableTreeNode localDefaultMutableTreeNode12 = new DefaultMutableTreeNode(new DemoDescription("demo.StatisticalLineChartDemo1", "StatisticalLineChartDemo1.java"));
    DefaultMutableTreeNode localDefaultMutableTreeNode13 = new DefaultMutableTreeNode(new DemoDescription("demo.XYSplineRendererDemo1", "XYSplineRendererDemo1.java"));
    DefaultMutableTreeNode localDefaultMutableTreeNode14 = new DefaultMutableTreeNode(new DemoDescription("demo.XYStepRendererDemo1", "XYStepRendererDemo1.java"));
    DefaultMutableTreeNode localDefaultMutableTreeNode15 = new DefaultMutableTreeNode(new DemoDescription("demo.XYStepRendererDemo2", "XYStepRendererDemo2.java"));
    localDefaultMutableTreeNode1.add(localDefaultMutableTreeNode2);
    localDefaultMutableTreeNode1.add(localDefaultMutableTreeNode3);
    localDefaultMutableTreeNode1.add(localDefaultMutableTreeNode4);
    localDefaultMutableTreeNode1.add(localDefaultMutableTreeNode5);
    localDefaultMutableTreeNode1.add(localDefaultMutableTreeNode6);
    localDefaultMutableTreeNode1.add(localDefaultMutableTreeNode7);
    localDefaultMutableTreeNode1.add(localDefaultMutableTreeNode8);
    localDefaultMutableTreeNode1.add(localDefaultMutableTreeNode9);
    localDefaultMutableTreeNode1.add(localDefaultMutableTreeNode10);
    localDefaultMutableTreeNode1.add(localDefaultMutableTreeNode11);
    localDefaultMutableTreeNode1.add(localDefaultMutableTreeNode12);
    localDefaultMutableTreeNode1.add(localDefaultMutableTreeNode13);
    localDefaultMutableTreeNode1.add(localDefaultMutableTreeNode14);
    localDefaultMutableTreeNode1.add(localDefaultMutableTreeNode15);
    return localDefaultMutableTreeNode1;
  }

  private MutableTreeNode createNode(String paramString1, String paramString2)
  {
    return new DefaultMutableTreeNode(new DemoDescription(paramString1, paramString2));
  }

  private MutableTreeNode createShowcaseNode(DefaultMutableTreeNode paramDefaultMutableTreeNode)
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("*** Showcase Charts ***");
    MutableTreeNode localMutableTreeNode1 = createNode("demo.PieChartDemo4", "PieChartDemo4.java");
    localDefaultMutableTreeNode.add(localMutableTreeNode1);
    localDefaultMutableTreeNode.add(createNode("demo.MultiplePieChartDemo1", "MultiplePieChartDemo1.java"));
    MutableTreeNode localMutableTreeNode2 = createNode("demo.BarChart3DDemo1", "BarChart3DDemo1.java");
    this.defaultChartPath = new TreePath(new Object[] { paramDefaultMutableTreeNode, localDefaultMutableTreeNode, localMutableTreeNode2 });
    localDefaultMutableTreeNode.add(localMutableTreeNode2);
    localDefaultMutableTreeNode.add(createNode("demo.StatisticalBarChartDemo1", "StatisticalBarChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.HistogramDemo1", "HistogramDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.StackedBarChartDemo2", "StackedBarChartDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.StackedXYBarChartDemo2", "StackedXYBarChartDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.NormalDistributionDemo2", "NormalDistributionDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.ParetoChartDemo1", "ParetoChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.WaterfallChartDemo1", "WaterfallChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.LineChartDemo1", "LineChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.AnnotationDemo1", "AnnotationDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYSplineRendererDemo1", "XYSplineRendererDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.DualAxisDemo1", "DualAxisDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.PriceVolumeDemo1", "PriceVolumeDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.YieldCurveDemo1", "YieldCurveDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.MultipleAxisDemo1", "MultipleAxisDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.DifferenceChartDemo1", "DifferenceChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.DifferenceChartDemo2", "DifferenceChartDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.DeviationRendererDemo2", "DeviationRendererDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.DialDemo2a", "DialDemo2a.java"));
    localDefaultMutableTreeNode.add(createNode("demo.VectorPlotDemo1", "VectorPlotDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.CrosshairDemo2", "CrosshairDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYDrawableAnnotationDemo1", "XYDrawableAnnotationDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYTaskDatasetDemo2", "XYTaskDatasetDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.SlidingCategoryDatasetDemo2", "SlidingCategoryDatasetDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.CrossSectionDemo1", "CrossSectionDemo1.java"));
    return localDefaultMutableTreeNode;
  }

  private MutableTreeNode createAreaChartsNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("Area Charts");
    localDefaultMutableTreeNode.add(createNode("demo.AreaChartDemo1", "AreaChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.StackedAreaChartDemo1", "StackedAreaChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.StackedXYAreaChartDemo1", "StackedXYAreaChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.StackedXYAreaChartDemo2", "StackedXYAreaChartDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.StackedXYAreaRendererDemo1", "StackedXYAreaRendererDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYAreaChartDemo1", "XYAreaChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYAreaChartDemo2", "XYAreaChartDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYAreaRenderer2Demo1", "XYAreaRenderer2Demo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYStepAreaRendererDemo1", "XYStepAreaRendererDemo1.java"));
    return localDefaultMutableTreeNode;
  }

  private MutableTreeNode createStatisticalChartsNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("Statistical Charts");
    localDefaultMutableTreeNode.add(createNode("demo.BoxAndWhiskerChartDemo1", "BoxAndWhiskerChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.BoxAndWhiskerChartDemo2", "BoxAndWhiskerChartDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.HistogramDemo1", "HistogramDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.HistogramDemo2", "HistogramDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.MinMaxCategoryPlotDemo1", "MinMaxCategoryPlotDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.NormalDistributionDemo1", "NormalDistributionDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.NormalDistributionDemo2", "NormalDistributionDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.RegressionDemo1", "RegressionDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.ScatterPlotDemo1", "ScatterPlotDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.ScatterPlotDemo2", "ScatterPlotDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.ScatterPlotDemo3", "ScatterPlotDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.ScatterPlotDemo4", "ScatterPlotDemo4.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYErrorRendererDemo1", "XYErrorRendererDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYErrorRendererDemo2", "XYErrorRendererDemo2.java"));
    return localDefaultMutableTreeNode;
  }

  private MutableTreeNode createTimeSeriesChartsNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("Time Series Charts");
    localDefaultMutableTreeNode.add(createNode("demo.TimeSeriesDemo1", "TimeSeriesDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.TimeSeriesDemo2", "TimeSeriesDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.TimeSeriesDemo3", "TimeSeriesDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.TimeSeriesDemo4", "TimeSeriesDemo4.java"));
    localDefaultMutableTreeNode.add(createNode("demo.TimeSeriesDemo5", "TimeSeriesDemo5.java"));
    localDefaultMutableTreeNode.add(createNode("demo.TimeSeriesDemo6", "TimeSeriesDemo6.java"));
    localDefaultMutableTreeNode.add(createNode("demo.TimeSeriesDemo7", "TimeSeriesDemo7.java"));
    localDefaultMutableTreeNode.add(createNode("demo.TimeSeriesDemo8", "TimeSeriesDemo8.java"));
    localDefaultMutableTreeNode.add(createNode("demo.TimeSeriesDemo9", "TimeSeriesDemo9.java"));
    localDefaultMutableTreeNode.add(createNode("demo.TimeSeriesDemo10", "TimeSeriesDemo10.java"));
    localDefaultMutableTreeNode.add(createNode("demo.TimeSeriesDemo11", "TimeSeriesDemo11.java"));
    localDefaultMutableTreeNode.add(createNode("demo.TimeSeriesDemo12", "TimeSeriesDemo12.java"));
    localDefaultMutableTreeNode.add(createNode("demo.TimeSeriesDemo13", "TimeSeriesDemo13.java"));
    localDefaultMutableTreeNode.add(createNode("demo.PeriodAxisDemo1", "PeriodAxisDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.PeriodAxisDemo2", "PeriodAxisDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.PeriodAxisDemo3", "PeriodAxisDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.RelativeDateFormatDemo1", "RelativeDateFormatDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.DeviationRendererDemo1", "DeviationRendererDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.DeviationRendererDemo2", "DeviationRendererDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.DifferenceChartDemo1", "DifferenceChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.DifferenceChartDemo2", "DifferenceChartDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.CompareToPreviousYearDemo", "CompareToPreviousYearDemo.java"));
    return localDefaultMutableTreeNode;
  }

  private MutableTreeNode createFinancialChartsNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("Financial Charts");
    localDefaultMutableTreeNode.add(createNode("demo.CandlestickChartDemo1", "CandlestickChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.HighLowChartDemo1", "HighLowChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.HighLowChartDemo2", "HighLowChartDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.HighLowChartDemo3", "HighLowChartDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.MovingAverageDemo1", "MovingAverageDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.PriceVolumeDemo1", "PriceVolumeDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.PriceVolumeDemo2", "PriceVolumeDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.YieldCurveDemo1", "YieldCurveDemo1.java"));
    return localDefaultMutableTreeNode;
  }

  private MutableTreeNode createXYChartsNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("XY Charts");
    localDefaultMutableTreeNode.add(createNode("demo.ScatterPlotDemo1", "ScatterPlotDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.ScatterPlotDemo2", "ScatterPlotDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.ScatterPlotDemo3", "ScatterPlotDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.LogAxisDemo1", "LogAxisDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.Function2DDemo1", "Function2DDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYBlockChartDemo1", "XYBlockChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYBlockChartDemo2", "XYBlockChartDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYBlockChartDemo3", "XYBlockChartDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYLineAndShapeRendererDemo1", "XYLineAndShapeRendererDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYLineAndShapeRendererDemo2", "XYLineAndShapeRendererDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYSeriesDemo1", "XYSeriesDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYSeriesDemo2", "XYSeriesDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYSeriesDemo3", "XYSeriesDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYShapeRendererDemo1", "XYShapeRendererDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.VectorPlotDemo1", "VectorPlotDemo1.java"));
    return localDefaultMutableTreeNode;
  }

  private MutableTreeNode createMeterChartsNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("Dial / Meter Charts");
    localDefaultMutableTreeNode.add(createNode("demo.DialDemo1", "DialDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.DialDemo2", "DialDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.DialDemo2a", "DialDemo2a.java"));
    localDefaultMutableTreeNode.add(createNode("demo.DialDemo3", "DialDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.DialDemo4", "DialDemo4.java"));
    localDefaultMutableTreeNode.add(createNode("demo.DialDemo5", "DialDemo5.java"));
    localDefaultMutableTreeNode.add(createNode("demo.MeterChartDemo1", "MeterChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.MeterChartDemo2", "MeterChartDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.MeterChartDemo3", "MeterChartDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.ThermometerDemo1", "ThermometerDemo1.java"));
    return localDefaultMutableTreeNode;
  }

  private MutableTreeNode createMultipleAxisChartsNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("Multiple Axis Charts");
    localDefaultMutableTreeNode.add(createNode("demo.DualAxisDemo1", "DualAxisDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.DualAxisDemo2", "DualAxisDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.DualAxisDemo3", "DualAxisDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.DualAxisDemo4", "DualAxisDemo4.java"));
    localDefaultMutableTreeNode.add(createNode("demo.DualAxisDemo5", "DualAxisDemo5.java"));
    localDefaultMutableTreeNode.add(createNode("demo.MultipleAxisDemo1", "MultipleAxisDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.MultipleAxisDemo2", "MultipleAxisDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.MultipleAxisDemo3", "MultipleAxisDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.ParetoChartDemo1", "ParetoChartDemo1.java"));
    return localDefaultMutableTreeNode;
  }

  private MutableTreeNode createCombinedAxisChartsNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("Combined Axis Charts");
    localDefaultMutableTreeNode.add(createNode("demo.CombinedCategoryPlotDemo1", "CombinedCategoryPlotDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.CombinedCategoryPlotDemo2", "CombinedCategoryPlotDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.CombinedTimeSeriesDemo1", "CombinedTimeSeriesDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.CombinedXYPlotDemo1", "CombinedXYPlotDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.CombinedXYPlotDemo2", "CombinedXYPlotDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.CombinedXYPlotDemo3", "CombinedXYPlotDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.CombinedXYPlotDemo4", "CombinedXYPlotDemo4.java"));
    return localDefaultMutableTreeNode;
  }

  private MutableTreeNode createGanttChartsNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("Gantt Charts");
    localDefaultMutableTreeNode.add(createNode("demo.GanttDemo1", "GanttDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.GanttDemo2", "GanttDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.SlidingGanttDatasetDemo1", "SlidingGanttDatasetDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYTaskDatasetDemo1", "XYTaskDatasetDemo1"));
    localDefaultMutableTreeNode.add(createNode("demo.XYTaskDatasetDemo2", "XYTaskDatasetDemo2"));
    return localDefaultMutableTreeNode;
  }

  private MutableTreeNode createMiscellaneousChartsNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("Miscellaneous");
    localDefaultMutableTreeNode.add(createAnnotationsNode());
    localDefaultMutableTreeNode.add(createCrosshairChartsNode());
    localDefaultMutableTreeNode.add(createDynamicChartsNode());
    localDefaultMutableTreeNode.add(createItemLabelsNode());
    localDefaultMutableTreeNode.add(createLegendNode());
    localDefaultMutableTreeNode.add(createMarkersNode());
    localDefaultMutableTreeNode.add(createOrientationNode());
    localDefaultMutableTreeNode.add(createNode("demo.AxisOffsetsDemo1", "AxisOffsetsDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.BubbleChartDemo1", "BubbleChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.BubbleChartDemo2", "BubbleChartDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.CategoryLabelPositionsDemo1", "CategoryLabelPositionsDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.CategoryStepChartDemo1", "CategoryStepChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.CompassDemo1", "CompassDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.CompassFormatDemo1", "CompassFormatDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.CompassFormatDemo2", "CompassFormatDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.EventFrequencyDemo1", "EventFrequencyDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.GradientPaintTransformerDemo1", "GradientPaintTransformerDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.GridBandDemo1", "GridBandDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.HideSeriesDemo1", "HideSeriesDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.HideSeriesDemo2", "HideSeriesDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.HideSeriesDemo3", "HideSeriesDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.MultipleDatasetDemo1", "MultipleDatasetDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.PolarChartDemo1", "PolarChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.ScatterRendererDemo1", "ScatterRendererDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.SpiderWebChartDemo1", "SpiderWebChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.SymbolAxisDemo1", "SymbolAxisDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.ThermometerDemo1", "ThermometerDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.ThermometerDemo2", "ThermometerDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.ThumbnailDemo1", "ThumbnailDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.TranslateDemo1", "TranslateDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.WindChartDemo1", "WindChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.YIntervalChartDemo1", "YIntervalChartDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.YIntervalChartDemo2", "YIntervalChartDemo2.java"));
    return localDefaultMutableTreeNode;
  }

  private MutableTreeNode createAnnotationsNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("Annotations");
    localDefaultMutableTreeNode.add(createNode("demo.AnnotationDemo1", "AnnotationDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.AnnotationDemo2", "AnnotationDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.CategoryPointerAnnotationDemo1", "CategoryPointerAnnotationDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYBoxAnnotationDemo1", "XYBoxAnnotationDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYPolygonAnnotationDemo1", "XYPolygonAnnotationDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.XYTitleAnnotationDemo1", "XYTitleAnnotationDemo1.java"));
    return localDefaultMutableTreeNode;
  }

  private MutableTreeNode createCrosshairChartsNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("Crosshairs");
    localDefaultMutableTreeNode.add(createNode("demo.CrosshairDemo1", "CrosshairDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.CrosshairDemo2", "CrosshairDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.CrosshairDemo3", "CrosshairDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.CrosshairDemo4", "CrosshairDemo4.java"));
    return localDefaultMutableTreeNode;
  }

  private MutableTreeNode createDynamicChartsNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("Dynamic Charts");
    localDefaultMutableTreeNode.add(createNode("demo.DynamicDataDemo1", "DynamicDataDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.DynamicDataDemo2", "DynamicDataDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.DynamicDataDemo3", "DynamicDataDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.MouseOverDemo1", "MouseOverDemo1.java"));
    return localDefaultMutableTreeNode;
  }

  private MutableTreeNode createItemLabelsNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("Item Labels");
    localDefaultMutableTreeNode.add(createNode("demo.ItemLabelDemo1", "ItemLabelDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.ItemLabelDemo2", "ItemLabelDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.ItemLabelDemo3", "ItemLabelDemo3.java"));
    localDefaultMutableTreeNode.add(createNode("demo.ItemLabelDemo4", "ItemLabelDemo4.java"));
    localDefaultMutableTreeNode.add(createNode("demo.ItemLabelDemo5", "ItemLabelDemo5.java"));
    return localDefaultMutableTreeNode;
  }

  private MutableTreeNode createLegendNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("Legends");
    localDefaultMutableTreeNode.add(createNode("demo.LegendWrapperDemo1", "LegendWrapperDemo1.java"));
    return localDefaultMutableTreeNode;
  }

  private MutableTreeNode createMarkersNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("Markers");
    localDefaultMutableTreeNode.add(createNode("demo.CategoryMarkerDemo1", "CategoryMarkerDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.CategoryMarkerDemo2", "CategoryMarkerDemo2.java"));
    localDefaultMutableTreeNode.add(createNode("demo.MarkerDemo1", "MarkerDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.MarkerDemo2", "MarkerDemo2.java"));
    return localDefaultMutableTreeNode;
  }

  private MutableTreeNode createOrientationNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = new DefaultMutableTreeNode("Plot Orientation");
    localDefaultMutableTreeNode.add(createNode("demo.PlotOrientationDemo1", "PlotOrientationDemo1.java"));
    localDefaultMutableTreeNode.add(createNode("demo.PlotOrientationDemo2", "PlotOrientationDemo2.java"));
    return localDefaultMutableTreeNode;
  }

  private void displayDescription(String paramString)
  {
    URL localURL = SuperDemo.class.getResource(paramString);
    if (localURL != null)
      try
      {
        this.descriptionPane.setPage(localURL);
      }
      catch (IOException localIOException)
      {
        System.err.println("Attempted to read a bad URL: " + localURL);
      }
    else
      System.err.println("Couldn't find file: " + paramString);
  }

  public void valueChanged(TreeSelectionEvent paramTreeSelectionEvent)
  {
    String str = null;
    TreePath localTreePath = paramTreeSelectionEvent.getPath();
    Object localObject1 = localTreePath.getLastPathComponent();
    if (localObject1 != null)
    {
      DefaultMutableTreeNode localDefaultMutableTreeNode = (DefaultMutableTreeNode)localObject1;
      Object localObject2 = localDefaultMutableTreeNode.getUserObject();
      if (localObject2 instanceof DemoDescription)
      {
        DemoDescription localDemoDescription = (DemoDescription)localObject2;
        str = localDemoDescription.getDescription();
        updateSourceCodePanel(str);
        SwingUtilities.invokeLater(new DisplayDemo(this, localDemoDescription));
      }
      else
      {
        this.chartContainer.removeAll();
        this.chartContainer.add(createNoDemoSelectedPanel());
        this.displayPanel.validate();
        displayDescription("select.html");
        updateSourceCodePanel(null);
      }
    }
    System.out.println(localObject1);
  }

  private JPanel createNoDemoSelectedPanel()
  {
    JPanel local2 = new JPanel(new FlowLayout())
    {
      public String getToolTipText()
      {
        return "(" + getWidth() + ", " + getHeight() + ")";
      }
    };
    ToolTipManager.sharedInstance().registerComponent(local2);
    local2.add(new JLabel("No demo selected"));
    local2.setPreferredSize(new Dimension(600, 400));
    return local2;
  }

  public static void main(String[] paramArrayOfString)
  {
    try
    {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
    }
    catch (Exception localException1)
    {
      try
      {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch (Exception localException2)
      {
        localException2.printStackTrace();
      }
    }
    SuperDemo localSuperDemo = new SuperDemo("JFreeChart 1.0.13 Demo Collection");
    localSuperDemo.pack();
    RefineryUtilities.centerFrameOnScreen(localSuperDemo);
    localSuperDemo.setVisible(true);
  }

  static class DisplayDemo
    implements Runnable
  {
    private SuperDemo app;
    private DemoDescription demoDescription;

    public DisplayDemo(SuperDemo paramSuperDemo, DemoDescription paramDemoDescription)
    {
      this.app = paramSuperDemo;
      this.demoDescription = paramDemoDescription;
    }

    public void run()
    {
      try
      {
        Class localClass = Class.forName(this.demoDescription.getClassName());
        Method localMethod = localClass.getDeclaredMethod("createDemoPanel", (Class[])null);
        JPanel localJPanel = (JPanel)localMethod.invoke(null, (Object[])null);
        this.app.chartContainer.removeAll();
        this.app.chartContainer.add(localJPanel);
        this.app.displayPanel.validate();
        String str1 = localClass.getName();
        String str2 = str1;
        int i = str1.lastIndexOf(46);
        if (i > 0)
          str2 = str1.substring(i + 1);
        str2 = str2 + ".html";
        this.app.displayDescription(str2);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        localClassNotFoundException.printStackTrace();
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        localNoSuchMethodException.printStackTrace();
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        localInvocationTargetException.printStackTrace();
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        localIllegalAccessException.printStackTrace();
      }
    }
  }

  static class PDFExportTask
    implements Runnable
  {
    JFreeChart chart;
    int width;
    int height;
    File file;

    public PDFExportTask(JFreeChart paramJFreeChart, int paramInt1, int paramInt2, File paramFile)
    {
      this.chart = paramJFreeChart;
      this.file = paramFile;
      this.width = paramInt1;
      this.height = paramInt2;
      paramJFreeChart.setBorderVisible(true);
      paramJFreeChart.setPadding(new RectangleInsets(2.0D, 2.0D, 2.0D, 2.0D));
    }

    public void run()
    {
      try
      {
        SuperDemo.saveChartAsPDF(this.file, this.chart, this.width, this.height, new DefaultFontMapper());
      }
      catch (IOException localIOException)
      {
        localIOException.printStackTrace();
      }
    }
  }
}