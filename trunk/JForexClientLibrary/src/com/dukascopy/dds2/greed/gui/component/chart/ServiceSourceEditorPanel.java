/*      */ package com.dukascopy.dds2.greed.gui.component.chart;
/*      */ 
/*      */ import com.dukascopy.api.JFException;
/*      */ import com.dukascopy.api.impl.CustIndicatorWrapper;
/*      */ import com.dukascopy.api.impl.ServiceWrapper;
/*      */ import com.dukascopy.api.impl.StrategyWrapper;
/*      */ import com.dukascopy.charts.persistence.IdManager;
/*      */ import com.dukascopy.charts.utils.helper.LocalizedMessageHelper;
/*      */ import com.dukascopy.dds2.greed.GreedContext;
/*      */ import com.dukascopy.dds2.greed.actions.CompileAndRunAction;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.ide.EditorFactory;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.Editor;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.Editor.Action;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.EditorRegistry;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.FileChangeListener;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceLanguage;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceType;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.ide.impl.EditorStatusBar;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.ide.jdoc.JDocIndexFinishListener;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.ide.jdoc.JDocSrch;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.ide.jdoc.JDocSrchResult;
/*      */ import com.dukascopy.dds2.greed.connector.IConverter;
/*      */ import com.dukascopy.dds2.greed.connector.helpers.ConverterHelpers;
/*      */ import com.dukascopy.dds2.greed.connector.helpers.ExternalEngine;
/*      */ import com.dukascopy.dds2.greed.gui.ClientFormLayoutManager;
/*      */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*      */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*      */ import com.dukascopy.dds2.greed.gui.component.RightExpandablePane;
/*      */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*      */ import com.dukascopy.dds2.greed.gui.component.chart.toolbar.ServiceSourceEditorToolBar;
/*      */ import com.dukascopy.dds2.greed.gui.component.javadoc.JDocBrowser;
/*      */ import com.dukascopy.dds2.greed.gui.component.javadoc.JDocSrchTable;
/*      */ import com.dukascopy.dds2.greed.gui.component.javadoc.JDocSrchTableModel;
/*      */ import com.dukascopy.dds2.greed.gui.component.javadoc.OpenListener;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.StrategiesContentPane;
/*      */ import com.dukascopy.dds2.greed.gui.component.table.ScrollPaneHeaderRenderer;
/*      */ import com.dukascopy.dds2.greed.gui.component.tree.FileChooserDialogHelper;
/*      */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*      */ import com.dukascopy.dds2.greed.gui.component.tree.actions.OpenStrategyAction;
/*      */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.AbstractServiceTreeNode;
/*      */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategyTreeNode;
/*      */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceRootNode;
/*      */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceTreeNode;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*      */ import com.dukascopy.dds2.greed.gui.resizing.ResizingManager.ComponentSize;
/*      */ import com.dukascopy.dds2.greed.gui.resizing.components.ResizableIcon;
/*      */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*      */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*      */ import com.dukascopy.dds2.greed.util.NotificationUtils;
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Cursor;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.KeyListener;
/*      */ import java.awt.event.MouseAdapter;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import javax.swing.AbstractButton;
/*      */ import javax.swing.BorderFactory;
/*      */ import javax.swing.Box;
/*      */ import javax.swing.BoxLayout;
/*      */ import javax.swing.Icon;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JFrame;
/*      */ import javax.swing.JOptionPane;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JScrollPane;
/*      */ import javax.swing.JSeparator;
/*      */ import javax.swing.JTextField;
/*      */ import javax.swing.JViewport;
/*      */ import javax.swing.ListSelectionModel;
/*      */ import javax.swing.border.CompoundBorder;
/*      */ import javax.swing.event.CaretEvent;
/*      */ import javax.swing.event.CaretListener;
/*      */ import javax.swing.plaf.basic.BasicButtonUI;
/*      */ import javax.swing.table.JTableHeader;
/*      */ import javax.swing.table.TableColumn;
/*      */ import javax.swing.table.TableColumnModel;
/*      */ import javax.swing.tree.DefaultTreeModel;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class ServiceSourceEditorPanel extends TabsAndFramePanelWithToolBar
/*      */   implements JDocIndexFinishListener
/*      */ {
/*      */   private static final long serialVersionUID = 1L;
/*  112 */   private static final Logger LOGGER = LoggerFactory.getLogger(ServiceSourceEditorPanel.class);
/*      */   private static final String HELP_URL_KEY = "api.doc.url";
/*      */   private static final String DEFAULT_HELP_URL = "http://www.dukascopy.com/swiss/docs/api/";
/*      */   private static final String ID_SEARCH_IN_DOC_FIELD = "ID_SEARCH_IN_DOC_FIELD";
/*      */   private static final String SRCH_JAVADOC_ACTION = "SrchJavadocAction";
/*      */   private ResizableIcon back;
/*      */   private ResizableIcon forw;
/*      */   private ResizableIcon backFaded;
/*      */   private ResizableIcon forwFaded;
/*  122 */   private ResizableIcon srchIcon = new ResizableIcon("toolbar_zoom_area_active.png");
/*      */   private final ServiceSourceType serviceSourceType;
/*      */   private EditorStatusBar statusBar;
/*  128 */   private boolean isNewFile = false;
/*      */ 
/*  130 */   private long fileCreationTime = 0L;
/*      */ 
/*  133 */   private ServiceSourceLanguage language = ServiceSourceLanguage.JAVA;
/*      */   private Editor editor;
/*      */   private JPanel content;
/*      */   private RightExpandablePane translatedJavaSplitPane;
/*      */   private Editor translatedJavaViewer;
/*      */   private JPanel translatedJavaContent;
/*      */   private int translatedJavaPanelId;
/*      */   private RightExpandablePane javadocSplitPane;
/*      */   private JPanel jdocSrchPanel;
/*      */   private JTextField srchField;
/*      */   private JLocalizableButton srchBtn;
/*      */   private JLocalizableButton forwardBtn;
/*      */   private JLocalizableButton backBtn;
/*      */   private JDocSrchTableModel jdocSrchTableModel;
/*      */   private JDocSrchTable table;
/*      */   private JScrollPane srchResultPane;
/*      */   private JScrollPane jdocBrowserPane;
/*      */   private JDocBrowser jdocBrowser;
/*      */   private IConverter converter;
/*  156 */   private String prefFileName = "Strategy";
/*      */   private ExternalEngine engine;
/*      */   private NavigationManager navigationManager;
/*      */ 
/*      */   public ServiceSourceEditorPanel(int panelId, File source, ServiceSourceType serviceSourceType)
/*      */     throws IOException
/*      */   {
/*  162 */     super(panelId, new ServiceSourceEditorToolBar(), ServiceSourceType.STRATEGY.equals(serviceSourceType) ? TabedPanelType.STRATEGY : TabedPanelType.INDICATOR);
/*  163 */     this.serviceSourceType = serviceSourceType;
/*      */ 
/*  165 */     this.editor = EditorFactory.getRegistry().openEditor(panelId, source, this.statusBar, new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  167 */         Editor.Action action = Editor.Action.valueOf(e.getActionCommand());
/*      */ 
/*  169 */         if (Editor.Action.COMPILE == action)
/*  170 */           ServiceSourceEditorPanel.this.compile();
/*  171 */         else if (Editor.Action.HELP == action)
/*  172 */           GuiUtilsAndConstants.openUrlByPropertyName("api.doc.url", "http://www.dukascopy.com/swiss/docs/api/");
/*  173 */         else if (Editor.Action.SAVE == action)
/*  174 */           ServiceSourceEditorPanel.this.save();
/*      */       }
/*      */     }
/*      */     , serviceSourceType, GreedContext.CLIENT_MODE, "text/java");
/*      */ 
/*  179 */     if ((source != null) && (this.editor.isMQLSource())) {
/*  180 */       setSourceLanguage(ServiceSourceLanguage.MQ4);
/*      */     }
/*      */ 
/*  183 */     getToolBar().build(this);
/*  184 */     this.statusBar = new EditorStatusBar();
/*      */ 
/*  194 */     this.editor.addCaretListener(new CaretListener()
/*      */     {
/*      */       public void caretUpdate(CaretEvent e) {
/*  197 */         if ((ServiceSourceEditorPanel.this.editor.contentWasModified()) && (!ServiceSourceEditorPanel.this.isNewFile()))
/*  198 */           ServiceSourceEditorPanel.this.renewFileNames(true);
/*      */       }
/*      */     });
/*  203 */     this.translatedJavaPanelId = IdManager.getInstance().getNextServiceId();
/*      */ 
/*  205 */     this.translatedJavaViewer = EditorFactory.getRegistry().openEditor(this.translatedJavaPanelId, FileChooserDialogHelper.createEmptyFile("java"), this.statusBar, new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  207 */         Editor.Action action = Editor.Action.valueOf(e.getActionCommand());
/*      */ 
/*  209 */         if (Editor.Action.COMPILE == action)
/*  210 */           ServiceSourceEditorPanel.this.compile();
/*  211 */         else if (Editor.Action.HELP == action)
/*  212 */           GuiUtilsAndConstants.openUrlByPropertyName("api.doc.url", "http://www.dukascopy.com/swiss/docs/api/");
/*      */       }
/*      */     }
/*      */     , ServiceSourceType.STRATEGY, GreedContext.CLIENT_MODE, "text/java");
/*      */ 
/*  216 */     this.translatedJavaViewer.setEditable(false);
/*      */   }
/*      */ 
/*      */   public static File adaptToJavaClassFileName(File file) throws JFException
/*      */   {
/*  221 */     if ((!Pattern.compile("^[A-Z_]([A-Za-z0-9_])+$").matcher(cutFileName(file.getName())).find()) && (conformFileNameAutoCorrection())) {
/*  222 */       String newFileName = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\") + 1);
/*  223 */       String suffix = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
/*      */ 
/*  225 */       newFileName = new StringBuilder().append(newFileName).append(validateFileName(file.getName())).toString();
/*  226 */       File newFile = new File(new StringBuilder().append(newFileName).append(suffix).toString());
/*      */ 
/*  228 */       if (newFile.exists()) {
/*  229 */         NotificationUtils.getInstance().postWarningMessage(LocalizationManager.getTextWithArguments("file.exists.error", new Object[] { file.getAbsolutePath(), newFile.getName() }));
/*  230 */         new JFException(LocalizationManager.getTextWithArguments("file.exists.error", new Object[] { file.getAbsolutePath(), newFile.getName() }));
/*      */       }
/*      */ 
/*  233 */       if (file.renameTo(newFile)) {
/*  234 */         file = newFile;
/*      */       }
/*      */     }
/*      */ 
/*  238 */     return file;
/*      */   }
/*      */ 
/*      */   private static String validateFileName(String fileName) throws JFException
/*      */   {
/*  243 */     String prefFileName = cutFileName(fileName);
/*  244 */     Pattern validClassNamePattern = Pattern.compile("^[A-Z_]([A-Za-z0-9_])+$");
/*      */ 
/*  246 */     if (!validClassNamePattern.matcher(prefFileName).find()) {
/*  247 */       prefFileName = Pattern.compile("[^A-Za-z_\\d]").matcher(prefFileName).replaceAll("_");
/*  248 */       prefFileName = Pattern.compile("^[^A-Z|_]").matcher(prefFileName).replaceAll("_");
/*      */ 
/*  250 */       if (!validClassNamePattern.matcher(prefFileName).find()) {
/*  251 */         throw new JFException(LocalizationManager.getTextWithArguments("rename.file.error", new Object[] { fileName }));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  256 */     return prefFileName;
/*      */   }
/*      */ 
/*      */   private String getClassName() {
/*  260 */     String conetnt = this.editor.getContent();
/*      */ 
/*  262 */     if ((conetnt != null) && (!"".equalsIgnoreCase(conetnt)) && (this.editor.getContent().contains("class "))) {
/*  263 */       conetnt = this.editor.getContent().substring(this.editor.getContent().indexOf("class "));
/*  264 */       return conetnt.subSequence(6, conetnt.indexOf(" ", 6)).toString();
/*      */     }
/*  266 */     return this.prefFileName;
/*      */   }
/*      */ 
/*      */   private void renewFileNames(boolean isModified)
/*      */   {
/*  271 */     String fileName = this.editor.getFile().getName();
/*  272 */     if (isModified) {
/*  273 */       fileName = new StringBuilder().append("*").append(fileName).toString();
/*      */     }
/*  275 */     WorkspaceRootNode workspaceRootNode = (WorkspaceRootNode)((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceJTree().getModel().getRoot();
/*  276 */     WorkspaceTreeNode service = workspaceRootNode.getServiceByPanelId(getPanelId());
/*      */ 
/*  278 */     if (!(service instanceof AbstractServiceTreeNode)) {
/*  279 */       return;
/*      */     }
/*      */ 
/*  282 */     ServiceWrapper wrapper = ((AbstractServiceTreeNode)service).getServiceWrapper();
/*  283 */     wrapper.setIsModified(isModified);
/*      */ 
/*  285 */     ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getChartTabsController().setTabTitle(getPanelId(), fileName);
/*      */   }
/*      */ 
/*      */   private void setConverter()
/*      */   {
/*  290 */     if (getServiceSourceType() == ServiceSourceType.STRATEGY) {
/*  291 */       if (getSourceLanguage() == ServiceSourceLanguage.MQ4) {
/*  292 */         this.engine = ExternalEngine.MT4STRATEGY;
/*      */       }
/*      */ 
/*      */     }
/*  296 */     else if ((getServiceSourceType() == ServiceSourceType.INDICATOR) && 
/*  297 */       (getSourceLanguage() == ServiceSourceLanguage.MQ4)) {
/*  298 */       this.engine = ExternalEngine.MT4INDICATOR;
/*      */     }
/*      */ 
/*  303 */     this.converter = ConverterHelpers.getInstance(this.engine);
/*      */   }
/*      */ 
/*      */   private ExternalEngine getEngine() {
/*  307 */     return this.engine;
/*      */   }
/*      */ 
/*      */   private static boolean conformFileNameAutoCorrection()
/*      */   {
/*  312 */     StringBuilder message = new StringBuilder();
/*  313 */     message.append(LocalizedMessageHelper.formatMessage(LocalizationManager.getText("joption.pane.message.invalid.file.name"), false, false, true, true));
/*  314 */     message.append(LocalizationManager.getText("joption.pane.conform.file.auto.correction"));
/*      */ 
/*  316 */     int reply = JOptionPane.showConfirmDialog((JFrame)GreedContext.get("clientGui"), message.toString(), LocalizationManager.getText("joption.pane.conform.file.auto.correction.title"), 0, 2);
/*      */ 
/*  321 */     if (0 == reply) {
/*  322 */       return true;
/*      */     }
/*      */ 
/*  325 */     return 1 != reply;
/*      */   }
/*      */ 
/*      */   public long getFileCreationTime()
/*      */   {
/*  332 */     return this.fileCreationTime;
/*      */   }
/*      */ 
/*      */   public void setFileCreationTime(long fileCreationTime) {
/*  336 */     this.fileCreationTime = fileCreationTime;
/*      */   }
/*      */ 
/*      */   public boolean isNewFile() {
/*  340 */     return this.isNewFile;
/*      */   }
/*      */ 
/*      */   public void setIsNewFile(boolean newCreatedFile) {
/*  344 */     this.isNewFile = newCreatedFile;
/*      */   }
/*      */ 
/*      */   public Editor getEditor() {
/*  348 */     return this.editor;
/*      */   }
/*      */ 
/*      */   public Editor getJavaEditor() {
/*  352 */     return this.translatedJavaViewer;
/*      */   }
/*      */ 
/*      */   public ServiceSourceType getServiceSourceType() {
/*  356 */     return this.serviceSourceType;
/*      */   }
/*      */ 
/*      */   public void build() {
/*  360 */     setLayout(new BorderLayout());
/*  361 */     createSplitPanes();
/*      */ 
/*  363 */     add(this.javadocSplitPane, "Center");
/*  364 */     add(this.statusBar, "South");
/*      */   }
/*      */ 
/*      */   private void createSplitPanes()
/*      */   {
/*  369 */     this.navigationManager = new NavigationManager(null);
/*      */ 
/*  371 */     this.content = new JPanel();
/*  372 */     this.content.setLayout(new BorderLayout());
/*  373 */     this.content.requestFocus();
/*  374 */     this.content.add(this.editor.getGUIComponent(), "Center");
/*      */ 
/*  376 */     this.translatedJavaContent = new JPanel();
/*  377 */     this.translatedJavaContent.setLayout(new BorderLayout());
/*  378 */     this.translatedJavaContent.requestFocus();
/*  379 */     this.translatedJavaContent.add(this.translatedJavaViewer.getGUIComponent(), "Center");
/*      */ 
/*  381 */     this.translatedJavaSplitPane = new RightExpandablePane("TranslatedJavaSplitPane", this.content, this.translatedJavaContent, 0.5D, 5, false, false);
/*      */ 
/*  383 */     JPanel jdocSrchPanel = createJDocSrchPanel();
/*      */ 
/*  385 */     this.javadocSplitPane = new RightExpandablePane("JavadocSplitPane", this.translatedJavaSplitPane, jdocSrchPanel, 0.5D, 5, false, true);
/*      */   }
/*      */ 
/*      */   private JPanel createJDocSrchPanel() {
/*  389 */     this.back = new ResizableIcon("back.png");
/*  390 */     this.forw = new ResizableIcon("forw.png");
/*  391 */     this.backFaded = new ResizableIcon("back_faded.png");
/*  392 */     this.forwFaded = new ResizableIcon("forw_faded.png");
/*      */ 
/*  395 */     this.srchField = new JTextField(20);
/*  396 */     this.srchField.setName("ID_SEARCH_IN_DOC_FIELD");
/*  397 */     this.srchField.addKeyListener(new SrchListener(null));
/*  398 */     setSize(this.srchField, ResizingManager.ComponentSize.SIZE_110X24.getSize());
/*      */ 
/*  400 */     JPanel srchToolbar = new JPanel();
/*      */ 
/*  403 */     srchToolbar.setLayout(new BoxLayout(srchToolbar, 0));
/*  404 */     CompoundBorder toolBarBorder = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(2, 2, 2, 2));
/*  405 */     srchToolbar.setBorder(toolBarBorder);
/*      */ 
/*  407 */     createSrchButton();
/*  408 */     createBackButton();
/*  409 */     createForwardButton();
/*  410 */     JSeparator separator = new JSeparator(1);
/*  411 */     setSize(separator, new Dimension(2, 24));
/*      */ 
/*  413 */     srchToolbar.add(Box.createHorizontalStrut(2));
/*  414 */     srchToolbar.add(this.srchField);
/*  415 */     srchToolbar.add(Box.createHorizontalStrut(2));
/*  416 */     srchToolbar.add(this.srchBtn);
/*      */ 
/*  418 */     srchToolbar.add(Box.createHorizontalStrut(2));
/*  419 */     srchToolbar.add(separator);
/*  420 */     srchToolbar.add(Box.createHorizontalStrut(2));
/*      */ 
/*  422 */     srchToolbar.add(this.backBtn);
/*  423 */     srchToolbar.add(Box.createHorizontalStrut(2));
/*  424 */     srchToolbar.add(this.forwardBtn);
/*  425 */     srchToolbar.add(Box.createHorizontalStrut(5));
/*      */ 
/*  427 */     srchToolbar.add(Box.createHorizontalGlue());
/*  428 */     srchToolbar.add(createCloseButton());
/*  429 */     srchToolbar.setMaximumSize(new Dimension(10000, 100));
/*      */ 
/*  431 */     Component srchResultTable = createSrchResultTable();
/*  432 */     this.srchResultPane = new JScrollPane(srchResultTable);
/*  433 */     this.srchResultPane.setVerticalScrollBarPolicy(22);
/*  434 */     this.srchResultPane.setHorizontalScrollBarPolicy(30);
/*  435 */     this.srchResultPane.getViewport().setBackground(GreedContext.GLOBAL_BACKGROUND);
/*  436 */     this.srchResultPane.setCorner("UPPER_RIGHT_CORNER", new ScrollPaneHeaderRenderer());
/*      */ 
/*  438 */     this.jdocBrowser = new JDocBrowser(this.navigationManager);
/*  439 */     this.jdocBrowserPane = new JScrollPane(this.jdocBrowser);
/*  440 */     this.jdocBrowserPane.setVerticalScrollBarPolicy(22);
/*  441 */     this.jdocBrowserPane.setHorizontalScrollBarPolicy(30);
/*  442 */     this.jdocBrowserPane.getViewport().setBackground(GreedContext.GLOBAL_BACKGROUND);
/*      */ 
/*  444 */     this.jdocBrowserPane.setVisible(false);
/*      */ 
/*  447 */     this.jdocSrchPanel = new JPanel();
/*  448 */     this.jdocSrchPanel.setLayout(new BoxLayout(this.jdocSrchPanel, 1));
/*  449 */     this.jdocSrchPanel.add(srchToolbar);
/*  450 */     this.jdocSrchPanel.add(this.srchResultPane);
/*  451 */     this.jdocSrchPanel.add(this.jdocBrowserPane);
/*      */ 
/*  453 */     this.jdocSrchPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
/*      */ 
/*  455 */     this.srchBtn.setEnabled(false);
/*  456 */     this.srchField.setEditable(false);
/*  457 */     this.srchField.setText(LocalizationManager.getText("generate.index"));
/*  458 */     this.jdocSrchPanel.setCursor(Cursor.getPredefinedCursor(3));
/*  459 */     JDocSrch.initialize(this);
/*      */ 
/*  461 */     return this.jdocSrchPanel;
/*      */   }
/*      */ 
/*      */   public void fire() {
/*  465 */     this.jdocSrchPanel.setCursor(Cursor.getDefaultCursor());
/*  466 */     this.srchField.setEditable(true);
/*  467 */     this.srchField.setText("");
/*  468 */     this.srchField.requestFocusInWindow();
/*  469 */     this.srchBtn.setEnabled(true);
/*      */   }
/*      */ 
/*      */   private JLocalizableButton createSrchButton() {
/*  473 */     this.srchBtn = new JLocalizableButton(this.srchIcon, ResizingManager.ComponentSize.SIZE_24X24);
/*  474 */     this.srchBtn.setToolTipText("tooltip.javadoc.srch");
/*  475 */     this.srchBtn.setActionCommand("SrchJavadocAction");
/*  476 */     this.srchBtn.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  479 */         if (e.getActionCommand().equals("SrchJavadocAction"))
/*  480 */           ServiceSourceEditorPanel.this.srch();
/*      */       }
/*      */     });
/*  484 */     this.srchBtn.setActionCommand("SrchJavadocAction");
/*  485 */     setSize(this.srchBtn, ResizingManager.ComponentSize.SIZE_24X24.getSize());
/*  486 */     return this.srchBtn;
/*      */   }
/*      */ 
/*      */   private void createBackButton() {
/*  490 */     this.backBtn = new JLocalizableButton(this.back, ResizingManager.ComponentSize.SIZE_24X24);
/*  491 */     this.backBtn.setInactiveIcon(this.backFaded);
/*      */ 
/*  493 */     this.backBtn.setToolTipText("tooltip.javadoc.back");
/*  494 */     this.backBtn.setActionCommand("SrchJavadocAction");
/*  495 */     this.backBtn.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  498 */         if (e.getActionCommand().equals("SrchJavadocAction"))
/*  499 */           ServiceSourceEditorPanel.this.navigationManager.goBackward();
/*      */       }
/*      */     });
/*  503 */     this.backBtn.setActionCommand("SrchJavadocAction");
/*  504 */     setSize(this.backBtn, ResizingManager.ComponentSize.SIZE_24X24.getSize());
/*      */ 
/*  506 */     this.backBtn.setActive(false);
/*      */   }
/*      */ 
/*      */   private void createForwardButton() {
/*  510 */     this.forwardBtn = new JLocalizableButton(this.forw, ResizingManager.ComponentSize.SIZE_24X24);
/*  511 */     this.forwardBtn.setInactiveIcon(this.forwFaded);
/*      */ 
/*  513 */     this.forwardBtn.setToolTipText("tooltip.javadoc.forw");
/*  514 */     this.forwardBtn.setActionCommand("SrchJavadocAction");
/*  515 */     this.forwardBtn.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  518 */         if (e.getActionCommand().equals("SrchJavadocAction"))
/*  519 */           ServiceSourceEditorPanel.this.navigationManager.goForward();
/*      */       }
/*      */     });
/*  523 */     setSize(this.forwardBtn, ResizingManager.ComponentSize.SIZE_24X24.getSize());
/*      */ 
/*  525 */     this.forwardBtn.setActive(false);
/*      */   }
/*      */ 
/*      */   private JButton createCloseButton() {
/*  529 */     Icon closeImageEnabled = new ResizableIcon("titlebar_close_tab_active.gif");
/*  530 */     Icon closeImageDisabled = new ResizableIcon("titlebar_close_tab_inactive.gif");
/*      */ 
/*  532 */     JButton closeButton = new TabButton(LocalizationManager.getText("tooltip.javadoc.close"), closeImageEnabled, closeImageDisabled, "SrchJavadocAction");
/*  533 */     setSize(closeButton, ResizingManager.ComponentSize.SIZE_24X24.getSize());
/*  534 */     return closeButton;
/*      */   }
/*      */ 
/*      */   private Component createSrchResultTable() {
/*  538 */     this.jdocSrchTableModel = new JDocSrchTableModel();
/*  539 */     this.table = new JDocSrchTable(this.jdocSrchTableModel);
/*      */ 
/*  541 */     this.table.getSelectionModel().setSelectionMode(0);
/*  542 */     TableColumnModel model = this.table.getTableHeader().getColumnModel();
/*  543 */     model.getColumn(0).setHeaderValue(LocalizationManager.getText("column.jdoc.file"));
/*  544 */     model.getColumn(1).setHeaderValue(LocalizationManager.getText("column.jdoc.text"));
/*      */ 
/*  546 */     this.table.setBackground(GreedContext.GLOBAL_BACKGROUND);
/*  547 */     this.table.setSelectionBackground(GreedContext.SELECTION_COLOR);
/*  548 */     this.table.getTableHeader().setReorderingAllowed(false);
/*  549 */     this.table.getTableHeader().setResizingAllowed(false);
/*      */ 
/*  551 */     this.table.addMouseListener(new MouseAdapter() {
/*      */       public void mouseClicked(MouseEvent mouseEvent) {
/*  553 */         if (mouseEvent.getClickCount() == 2)
/*  554 */           ServiceSourceEditorPanel.this.openJDocViewer();
/*      */       }
/*      */ 
/*      */       public void mouseEntered(MouseEvent mouseEvent)
/*      */       {
/*  559 */         ServiceSourceEditorPanel.this.setCursor(Cursor.getPredefinedCursor(12));
/*      */       }
/*      */ 
/*      */       public void mouseExited(MouseEvent mouseEvent) {
/*  563 */         ServiceSourceEditorPanel.this.setCursor(Cursor.getDefaultCursor());
/*      */       }
/*      */     });
/*  567 */     return this.table;
/*      */   }
/*      */ 
/*      */   public void addFileChangeListener(FileChangeListener fileChangeListener) {
/*  571 */     this.editor.addFileChangeListener(fileChangeListener);
/*      */   }
/*      */ 
/*      */   public File getSourceFile() {
/*  575 */     return this.editor.getFile();
/*      */   }
/*      */ 
/*      */   public boolean isCloseAllowed() {
/*  579 */     return (this.editor.close()) && (this.translatedJavaViewer.close());
/*      */   }
/*      */ 
/*      */   public void open() {
/*      */     try {
/*  584 */       new OpenStrategyAction(((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceJTree(), ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceNodeFactory(), ((ClientFormLayoutManager)GreedContext.get("layoutManager")).getChartTabsController(), (ClientSettingsStorage)GreedContext.get("settingsStorage"), this).openSource();
/*      */     } catch (IOException ex) {
/*  586 */       LOGGER.error(ex.getMessage(), ex);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean save() {
/*  591 */     if (isNewFile()) {
/*  592 */       JForexClientFormLayoutManager layoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/*  593 */       WorkspaceJTree workspaceJTree = layoutManager.getWorkspaceJTree();
/*  594 */       WorkspaceTreeNode treeNode = workspaceJTree.getWorkspaceRoot().getServiceByPanelId(getPanelId());
/*      */ 
/*  596 */       boolean saveAs = saveAs();
/*      */ 
/*  598 */       if ((saveAs) && ((treeNode instanceof StrategyTreeNode))) {
/*  599 */         layoutManager.getStrategiesPanel().addStrategyFromFile(getSourceFile(), (StrategyTreeNode)treeNode);
/*      */       }
/*      */ 
/*  602 */       return saveAs;
/*      */     }
/*      */     try {
/*  605 */       if (this.editor.save()) {
/*  606 */         renewFileNames(false);
/*  607 */         repaint();
/*  608 */         return true;
/*      */       }
/*  610 */       return false;
/*      */     } catch (IOException e) {
/*  612 */       LOGGER.error(e.getMessage(), e);
/*  613 */       JOptionPane.showMessageDialog((JFrame)GreedContext.get("clientGui"), new StringBuilder().append(LocalizationManager.getText("joption.pane.cannot.save")).append(e.getMessage()).toString(), LocalizationManager.getText("joption.pane.error"), 0);
/*  614 */     }return false;
/*      */   }
/*      */ 
/*      */   public boolean saveAs()
/*      */   {
/*      */     try {
/*  620 */       if (getSourceLanguage() == ServiceSourceLanguage.JAVA) {
/*  621 */         this.prefFileName = getClassName();
/*      */       }
/*      */ 
/*  624 */       if (this.editor.saveAs((JFrame)GreedContext.get("clientGui"), this.prefFileName, getSourceLanguage())) {
/*  625 */         this.isNewFile = false;
/*  626 */         return true;
/*      */       }
/*      */     } catch (IOException e) {
/*  629 */       LOGGER.error(e.getMessage(), e);
/*  630 */       JOptionPane.showMessageDialog((JFrame)GreedContext.get("clientGui"), new StringBuilder().append(LocalizationManager.getText("joption.pane.cannot.save")).append(e.getMessage()).toString(), LocalizationManager.getText("joption.pane.error"), 0);
/*      */     }
/*  632 */     return false;
/*      */   }
/*      */ 
/*      */   public void find() {
/*  636 */     this.editor.find();
/*      */   }
/*      */ 
/*      */   public void replace() {
/*  640 */     this.editor.replace();
/*      */   }
/*      */ 
/*      */   public void organizeImports() {
/*  644 */     this.editor.organizeImports();
/*      */   }
/*      */ 
/*      */   public ServiceSourceEditorToolBar getToolBar() {
/*  648 */     return (ServiceSourceEditorToolBar)this.toolBar;
/*      */   }
/*      */ 
/*      */   private void switchSyntaxStyle() {
/*  652 */     if (this.editor != null)
/*  653 */       if (getSourceLanguage().equals(ServiceSourceLanguage.JAVA))
/*  654 */         this.editor.setSyntaxStyle("text/java");
/*  655 */       else if (getSourceLanguage().equals(ServiceSourceLanguage.MQ4))
/*      */       {
/*  659 */         this.editor.setSyntaxStyle("text/cpp");
/*      */       }
/*      */   }
/*      */ 
/*      */   public ServiceSourceLanguage getSourceLanguage()
/*      */   {
/*  665 */     return this.language;
/*      */   }
/*      */ 
/*      */   public void setSourceLanguage(ServiceSourceLanguage language)
/*      */   {
/*  670 */     if (this.language != language) {
/*  671 */       this.language = language;
/*  672 */       switchSyntaxStyle();
/*      */     }
/*      */   }
/*      */ 
/*      */   private String getSourceActualPath() {
/*  677 */     ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*      */ 
/*  679 */     if (ServiceSourceType.INDICATOR.equals(getServiceSourceType()))
/*  680 */       return storage.getMyIndicatorsPath();
/*  681 */     if (ServiceSourceType.STRATEGY.equals(getServiceSourceType())) {
/*  682 */       return storage.getMyStrategiesPath();
/*      */     }
/*  684 */     return null;
/*      */   }
/*      */ 
/*      */   public static String cutFileName(String name) {
/*  688 */     if (name.contains(".")) {
/*  689 */       return name.substring(0, name.lastIndexOf("."));
/*      */     }
/*  691 */     return name;
/*      */   }
/*      */ 
/*      */   public void removeEditors() {
/*  695 */     EditorRegistry editorRegistry = EditorFactory.getRegistry();
/*  696 */     editorRegistry.removeReference(getPanelId());
/*  697 */     editorRegistry.removeReference(this.translatedJavaPanelId);
/*      */   }
/*      */ 
/*      */   public void toggleTranslatedJavaPane() {
/*  701 */     this.translatedJavaSplitPane.toggleExanded();
/*      */   }
/*      */ 
/*      */   public void expandTranslatedJavaPane() {
/*  705 */     if (this.translatedJavaSplitPane != null)
/*  706 */       this.translatedJavaSplitPane.expand();
/*      */   }
/*      */ 
/*      */   public void collapseTranslatedJavaPane()
/*      */   {
/*  711 */     if (this.translatedJavaSplitPane != null)
/*  712 */       this.translatedJavaSplitPane.collapse();
/*      */   }
/*      */ 
/*      */   public void expandJavadocHelpPane()
/*      */   {
/*  717 */     if (this.javadocSplitPane != null) {
/*  718 */       this.javadocSplitPane.expand();
/*      */     }
/*  720 */     if (this.srchField != null)
/*  721 */       this.srchField.requestFocusInWindow();
/*      */   }
/*      */ 
/*      */   public void collapseJavadocHelpPane()
/*      */   {
/*  726 */     if (this.javadocSplitPane != null)
/*  727 */       this.javadocSplitPane.collapse();
/*      */   }
/*      */ 
/*      */   public void toggleJavadocHelpPane()
/*      */   {
/*  732 */     if (this.javadocSplitPane != null)
/*  733 */       if (this.javadocSplitPane.isExpanded()) {
/*  734 */         this.javadocSplitPane.collapse();
/*      */       } else {
/*  736 */         this.javadocSplitPane.expand();
/*  737 */         if (this.srchField != null)
/*  738 */           this.srchField.requestFocusInWindow();
/*      */       }
/*      */   }
/*      */ 
/*      */   public void compile()
/*      */   {
/*  745 */     if (save()) {
/*  746 */       ServiceWrapper serviceWrapper = null;
/*      */ 
/*  748 */       if (this.serviceSourceType == ServiceSourceType.INDICATOR)
/*  749 */         serviceWrapper = new CustIndicatorWrapper();
/*      */       else {
/*  751 */         serviceWrapper = new StrategyWrapper();
/*      */       }
/*      */ 
/*  755 */       serviceWrapper.setSourceFile(this.editor.getFile());
/*  756 */       CompileAndRunAction runAction = new CompileAndRunAction(this, serviceWrapper, null, false, null);
/*  757 */       GreedContext.publishEvent(runAction);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean translate() {
/*      */     try {
/*  763 */       if (this.isNewFile) {
/*  764 */         save();
/*      */       }
/*  766 */       if (!this.isNewFile) {
/*  767 */         File newFile = adaptToJavaClassFileName(this.editor.getFile());
/*      */ 
/*  769 */         if (newFile != null) {
/*  770 */           this.prefFileName = cutFileName(newFile.getName());
/*      */           try
/*      */           {
/*  773 */             this.editor.saveAs(newFile);
/*      */           } catch (IOException e) {
/*  775 */             LOGGER.error(e.getMessage(), e);
/*  776 */             JOptionPane.showMessageDialog((JFrame)GreedContext.get("clientGui"), new StringBuilder().append(LocalizationManager.getText("joption.pane.cannot.save")).append(e.getMessage()).toString(), LocalizationManager.getText("joption.pane.error"), 0);
/*      */           }
/*  778 */           setConverter();
/*      */ 
/*  780 */           this.converter.convert(new StringBuilder(this.editor.getContent()), this.prefFileName, getSourceActualPath(), getEngine());
/*  781 */           this.translatedJavaViewer.setContent(this.converter.getConvertionResult().toString());
/*  782 */           toggleTranslatedJavaPane();
/*      */ 
/*  784 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (JFException e) {
/*  789 */       NotificationUtils.getInstance().postErrorMessage(e.getMessage());
/*  790 */       return false;
/*      */     }
/*  792 */     return false;
/*      */   }
/*      */ 
/*      */   public void openJDocViewer() {
/*  796 */     int rowSelected = this.table.getSelectedRow();
/*      */ 
/*  798 */     JDocSrchTableModel model = (JDocSrchTableModel)this.table.getModel();
/*      */ 
/*  800 */     JDocSrchResult result = model.getRow(rowSelected);
/*      */ 
/*  802 */     this.navigationManager.openJdocViewer(result.getQuery(), result.getFilePath(), "openBrowser");
/*      */   }
/*      */ 
/*      */   public void srch() {
/*  806 */     this.navigationManager.srch(this.srchField.getText());
/*      */   }
/*      */ 
/*      */   private void setSize(JComponent component, Dimension size)
/*      */   {
/*  960 */     component.setPreferredSize(size);
/*  961 */     component.setMinimumSize(size);
/*  962 */     component.setMaximumSize(size);
/*      */   }
/*      */ 
/*      */   private class TabButton extends JButton
/*      */   {
/*      */     public TabButton(String toolTip, Icon enabledIcon, Icon disabledIcon, String actionCommand) {
/*  968 */       super();
/*  969 */       setPreferredSize(new Dimension(17, 17));
/*  970 */       setToolTipText(toolTip);
/*  971 */       setUI(new BasicButtonUI());
/*  972 */       setContentAreaFilled(false);
/*  973 */       setFocusable(false);
/*  974 */       setBorder(BorderFactory.createLineBorder(Color.GRAY));
/*  975 */       setBorderPainted(false);
/*  976 */       addMouseListener(new MouseAdapter(ServiceSourceEditorPanel.this, enabledIcon, disabledIcon) {
/*      */         public void mouseEntered(MouseEvent e) {
/*  978 */           ServiceSourceEditorPanel.TabButton.this.setIcon(this.val$enabledIcon);
/*      */         }
/*      */ 
/*      */         public void mouseExited(MouseEvent e) {
/*  982 */           ServiceSourceEditorPanel.TabButton.this.setIcon(this.val$disabledIcon);
/*      */         }
/*      */       });
/*  985 */       addMouseListener(new MouseAdapter(ServiceSourceEditorPanel.this) {
/*      */         public void mouseEntered(MouseEvent e) {
/*  987 */           Component component = e.getComponent();
/*  988 */           if ((component instanceof AbstractButton)) {
/*  989 */             AbstractButton button = (AbstractButton)component;
/*  990 */             if (button.isEnabled())
/*  991 */               button.setBorderPainted(true);
/*      */           }
/*      */         }
/*      */ 
/*      */         public void mouseExited(MouseEvent e)
/*      */         {
/*  997 */           Component component = e.getComponent();
/*  998 */           if ((component instanceof AbstractButton)) {
/*  999 */             AbstractButton button = (AbstractButton)component;
/* 1000 */             if (button.isEnabled())
/* 1001 */               button.setBorderPainted(false);
/*      */           }
/*      */         }
/*      */       });
/* 1006 */       setRolloverEnabled(true);
/* 1007 */       addActionListener(new ActionListener(ServiceSourceEditorPanel.this) {
/*      */         public void actionPerformed(ActionEvent e) {
/* 1009 */           ServiceSourceEditorPanel.this.javadocSplitPane.collapse();
/*      */         }
/*      */       });
/*      */     }
/*      */ 
/*      */     public void updateUI()
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   private class SrchListener
/*      */     implements KeyListener
/*      */   {
/*      */     private SrchListener()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void keyTyped(KeyEvent e)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void keyPressed(KeyEvent e)
/*      */     {
/*  949 */       if (e.getKeyCode() == 10)
/*  950 */         ServiceSourceEditorPanel.this.srch();
/*      */     }
/*      */ 
/*      */     public void keyReleased(KeyEvent e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   private class NavigationItem
/*      */   {
/*      */     private boolean isBrowser;
/*      */     private String query;
/*      */     private String filePath;
/*      */     private String fileName;
/*      */     private String bookmark;
/*      */ 
/*      */     public NavigationItem(String query, String filePath, String bookmark)
/*      */     {
/*  909 */       this.isBrowser = true;
/*  910 */       this.query = query;
/*  911 */       this.filePath = filePath;
/*      */ 
/*  913 */       this.bookmark = bookmark;
/*      */     }
/*      */ 
/*      */     public NavigationItem(String query) {
/*  917 */       this.isBrowser = false;
/*  918 */       this.query = query;
/*  919 */       this.filePath = null;
/*  920 */       this.fileName = null;
/*  921 */       this.bookmark = null;
/*      */     }
/*      */ 
/*      */     public String getQuery() {
/*  925 */       return this.query;
/*      */     }
/*      */ 
/*      */     public String getFilePath() {
/*  929 */       return this.filePath;
/*      */     }
/*      */ 
/*      */     public String getBookmark()
/*      */     {
/*  937 */       return this.bookmark;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class NavigationManager
/*      */     implements OpenListener
/*      */   {
/*  811 */     private List<ServiceSourceEditorPanel.NavigationItem> navigationList = new LinkedList();
/*  812 */     private int currentItem = -1;
/*      */ 
/*      */     private NavigationManager() {  }
/*      */ 
/*  815 */     private void addItem(ServiceSourceEditorPanel.NavigationItem navItem) { this.currentItem += 1;
/*  816 */       if (this.currentItem > 0) {
/*  817 */         this.navigationList = this.navigationList.subList(0, this.currentItem);
/*      */       }
/*  819 */       this.navigationList.add(navItem);
/*  820 */       ServiceSourceEditorPanel.this.forwardBtn.setActive(false);
/*      */ 
/*  822 */       if (this.currentItem - 1 >= 0)
/*  823 */         ServiceSourceEditorPanel.this.backBtn.setActive(true);
/*      */       else
/*  825 */         ServiceSourceEditorPanel.this.backBtn.setActive(false);
/*      */     }
/*      */ 
/*      */     private void go(ServiceSourceEditorPanel.NavigationItem navItem)
/*      */     {
/*  830 */       if (navItem.isBrowser) {
/*  831 */         ServiceSourceEditorPanel.this.jdocBrowser.open(navItem.getQuery(), navItem.getFilePath(), navItem.getBookmark());
/*  832 */         ServiceSourceEditorPanel.this.srchResultPane.setVisible(false);
/*  833 */         ServiceSourceEditorPanel.this.jdocBrowserPane.setVisible(true);
/*      */       }
/*      */       else {
/*  836 */         if (navItem.getQuery() != null) {
/*  837 */           ServiceSourceEditorPanel.this.srchField.setText(navItem.getQuery());
/*  838 */           List results = JDocSrch.getInstance().srch(navItem.getQuery());
/*  839 */           if (results != null) {
/*  840 */             ServiceSourceEditorPanel.this.jdocSrchTableModel.addSrchResult(results);
/*  841 */             ServiceSourceEditorPanel.this.jdocSrchTableModel.fireTableDataChanged();
/*      */           }
/*      */         }
/*  844 */         ServiceSourceEditorPanel.this.jdocBrowserPane.setVisible(false);
/*  845 */         ServiceSourceEditorPanel.this.srchResultPane.setVisible(true);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void goForward()
/*      */     {
/*  851 */       if (this.currentItem + 1 < this.navigationList.size()) {
/*  852 */         this.currentItem += 1;
/*  853 */         ServiceSourceEditorPanel.NavigationItem navItem = (ServiceSourceEditorPanel.NavigationItem)this.navigationList.get(this.currentItem);
/*  854 */         go(navItem);
/*      */ 
/*  856 */         ServiceSourceEditorPanel.this.backBtn.setActive(true);
/*  857 */         if (this.currentItem + 1 < this.navigationList.size())
/*  858 */           ServiceSourceEditorPanel.this.forwardBtn.setActive(true);
/*      */         else
/*  860 */           ServiceSourceEditorPanel.this.forwardBtn.setActive(false);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void goBackward()
/*      */     {
/*  867 */       if (this.currentItem - 1 >= 0) {
/*  868 */         this.currentItem -= 1;
/*  869 */         ServiceSourceEditorPanel.NavigationItem navItem = (ServiceSourceEditorPanel.NavigationItem)this.navigationList.get(this.currentItem);
/*  870 */         go(navItem);
/*      */ 
/*  872 */         ServiceSourceEditorPanel.this.forwardBtn.setActive(true);
/*  873 */         if (this.currentItem - 1 >= 0)
/*  874 */           ServiceSourceEditorPanel.this.backBtn.setActive(true);
/*      */         else
/*  876 */           ServiceSourceEditorPanel.this.backBtn.setActive(false);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void openJdocViewer(String query, String filePath, String bookmark)
/*      */     {
/*  882 */       ServiceSourceEditorPanel.NavigationItem navItem = new ServiceSourceEditorPanel.NavigationItem(ServiceSourceEditorPanel.this, query, filePath, bookmark);
/*  883 */       addItem(navItem);
/*  884 */       go(navItem);
/*      */     }
/*      */ 
/*      */     public void srch(String query) {
/*  888 */       ServiceSourceEditorPanel.NavigationItem navItem = new ServiceSourceEditorPanel.NavigationItem(ServiceSourceEditorPanel.this, query);
/*  889 */       addItem(navItem);
/*  890 */       go(navItem);
/*      */     }
/*      */ 
/*      */     public void opened(String query, String filePath, String bookmark)
/*      */     {
/*  896 */       ServiceSourceEditorPanel.NavigationItem navItem = new ServiceSourceEditorPanel.NavigationItem(ServiceSourceEditorPanel.this, query, filePath, bookmark);
/*  897 */       addItem(navItem);
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.ServiceSourceEditorPanel
 * JD-Core Version:    0.6.0
 */