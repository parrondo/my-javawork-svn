/*     */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*     */ 
/*     */ import com.dukascopy.api.impl.CustIndicatorWrapper;
/*     */ import com.dukascopy.api.impl.ServiceWrapper;
/*     */ import com.dukascopy.api.indicators.IIndicator;
/*     */ import com.dukascopy.charts.persistence.CustomIndicatorBean;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceType;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.impl.EditorFileHandler;
/*     */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.ServiceSourceEditorPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*     */ import com.dukascopy.dds2.greed.gui.component.filechooser.ChooserSelectionWrapper;
/*     */ import com.dukascopy.dds2.greed.gui.component.filechooser.TransportFileChooser;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.StrategiesContentPane;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.DefaultStrategyPresetsController;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.table.StrategiesTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.toolbar.IStrategiesToolbarController;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.toolbar.StrategiesToolbarController;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.AbstractServiceTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.CustIndTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategyTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceNodeFactory;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceRootNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.transport.common.datafeed.FileType;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Calendar;
/*     */ import java.util.List;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.filechooser.FileFilter;
/*     */ 
/*     */ abstract class ServiceAction extends TreeAction
/*     */ {
/*     */   public static final String FILE_EXT_JFX = ".jfx";
/*     */   public static final String FILE_EXT_JAVA = ".java";
/*     */   public static final String FILE_EXT_MQ4 = ".mq4";
/*     */   protected WorkspaceNodeFactory workspaceNodeFactory;
/*     */   protected IChartTabsAndFramesController chartTabsAndFramesController;
/*     */ 
/*     */   public ServiceAction(IChartTabsAndFramesController chartTabsAndFramesController, WorkspaceJTree workspaceJTree, WorkspaceNodeFactory workspaceNodeFactory)
/*     */   {
/*  48 */     super(workspaceJTree);
/*  49 */     this.chartTabsAndFramesController = chartTabsAndFramesController;
/*  50 */     this.workspaceNodeFactory = workspaceNodeFactory;
/*     */   }
/*     */ 
/*     */   public void openSource() throws IOException
/*     */   {
/*  55 */     ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*     */ 
/*  57 */     String lastOpenedPath = (String)GreedContext.get("lastOpenedStrategiesDirectoryPath");
/*  58 */     if (lastOpenedPath == null) {
/*  59 */       lastOpenedPath = storage.getMyStrategiesPath();
/*     */     }
/*  61 */     JFileChooser fileChooser = new JFileChooser(lastOpenedPath);
/*  62 */     fileChooser.setFileSelectionMode(0);
/*  63 */     fileChooser.setMultiSelectionEnabled(true);
/*     */ 
/*  65 */     fileChooser.addChoosableFileFilter(new FileFilter() {
/*     */       public boolean accept(File dir) {
/*  67 */         return (dir.getName().endsWith(".java")) || (dir.getName().endsWith(".mq4")) || (dir.isDirectory());
/*     */       }
/*     */ 
/*     */       public String getDescription()
/*     */       {
/*  74 */         return "Strategies and Custom Indicators";
/*     */       }
/*     */     });
/*  79 */     ChooserSelectionWrapper selection = TransportFileChooser.showOpenDialog(FileType.STRATEGY, fileChooser, (JFrame)GreedContext.get("clientGui"), Boolean.valueOf(true), GreedContext.CLIENT_MODE);
/*     */ 
/*  85 */     List selectedFiles = Arrays.asList(fileChooser.getSelectedFiles());
/*     */ 
/*  87 */     for (File serviceSourceFile : selectedFiles)
/*     */     {
/*  89 */       AbstractServiceTreeNode serviceTreeNode = null;
/*     */ 
/*  91 */       if (serviceSourceFile != null)
/*     */       {
/*  93 */         WorkspaceTreeNode treeNode = this.workspaceJTree.getWorkspaceRoot().getServiceBySourceFile(serviceSourceFile);
/*     */ 
/*  95 */         if (treeNode != null) {
/*  96 */           serviceTreeNode = (AbstractServiceTreeNode)treeNode;
/*     */         }
/*  99 */         else if (implementsInterface(serviceSourceFile, IIndicator.class))
/*     */         {
/* 101 */           CustIndicatorWrapper custIndicatorWrapper = new CustIndicatorWrapper();
/* 102 */           custIndicatorWrapper.setSourceFile(serviceSourceFile);
/* 103 */           custIndicatorWrapper.setNewUnsaved(false);
/* 104 */           custIndicatorWrapper.setIsModified(false);
/*     */ 
/* 106 */           serviceTreeNode = this.workspaceNodeFactory.createServiceTreeNodeFrom(custIndicatorWrapper);
/*     */ 
/* 108 */           storage.save(new CustomIndicatorBean(serviceTreeNode.getId(), custIndicatorWrapper.getSourceFile(), custIndicatorWrapper.getBinaryFile()));
/* 109 */           this.workspaceJTree.addCustIndTreeNode((CustIndTreeNode)serviceTreeNode);
/*     */         }
/*     */         else
/*     */         {
/* 114 */           IStrategiesToolbarController strategiesController = new StrategiesToolbarController(storage);
/* 115 */           StrategyNewBean strategyBean = strategiesController.createStrategyBean(serviceSourceFile, new DefaultStrategyPresetsController(), "DEFAULT_PRESET_ID");
/*     */ 
/* 120 */           serviceTreeNode = this.workspaceNodeFactory.createStrategyTreeNodeFrom(strategyBean);
/* 121 */           storage.saveStrategyNewBean(strategyBean);
/* 122 */           this.workspaceJTree.addStrategyTreeNode((StrategyTreeNode)serviceTreeNode);
/*     */ 
/* 124 */           ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getStrategiesPanel().getTable().addStrategy(strategyBean);
/*     */         }
/*     */ 
/* 129 */         this.workspaceJTree.selectNode(serviceTreeNode);
/* 130 */         edit(serviceTreeNode, false, serviceTreeNode.getServiceSourceType());
/*     */ 
/* 132 */         GreedContext.putInSingleton("lastOpenedStrategiesDirectoryPath", serviceSourceFile.getCanonicalPath());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void edit(AbstractServiceTreeNode serviceTreeNode, boolean newFile, ServiceSourceType type) {
/* 138 */     ServiceWrapper serviceWrapper = serviceTreeNode.getServiceWrapper();
/* 139 */     boolean editable = serviceWrapper.isEditable();
/* 140 */     serviceTreeNode.setEditable(editable);
/* 141 */     if (!editable) {
/* 142 */       return;
/*     */     }
/*     */ 
/* 145 */     if (!this.chartTabsAndFramesController.selectServiceSourceEditor(serviceTreeNode.getId())) {
/* 146 */       this.chartTabsAndFramesController.addServiceSourceEditor(serviceTreeNode.getId(), serviceWrapper.getName(), serviceWrapper.getSourceFile(), type, false, newFile);
/*     */     }
/*     */ 
/* 155 */     if (newFile) {
/* 156 */       ServiceSourceEditorPanel ssep = this.chartTabsAndFramesController.getEditorPanel(serviceWrapper);
/* 157 */       if (ssep != null) {
/* 158 */         ssep.setIsNewFile(true);
/* 159 */         long creationTime = Calendar.getInstance().getTimeInMillis();
/* 160 */         ssep.setFileCreationTime(creationTime);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean implementsInterface(File javaSourceFile, Class<?> clazz)
/*     */     throws IOException
/*     */   {
/* 175 */     if ((javaSourceFile == null) || (clazz == null)) {
/* 176 */       return false;
/*     */     }
/*     */ 
/* 179 */     EditorFileHandler fileHandler = new EditorFileHandler();
/* 180 */     String fileContent = fileHandler.readFromFile(javaSourceFile);
/*     */ 
/* 182 */     if (fileContent == null) {
/* 183 */       return false;
/*     */     }
/*     */ 
/* 188 */     int optionOne = fileContent.indexOf("implements " + clazz.getSimpleName());
/* 189 */     int optionTwo = fileContent.indexOf("implements " + clazz.getCanonicalName());
/*     */ 
/* 191 */     return (optionOne > 0) || (optionTwo > 0);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.ServiceAction
 * JD-Core Version:    0.6.0
 */