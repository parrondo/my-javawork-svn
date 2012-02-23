/*     */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*     */ 
/*     */ import com.dukascopy.api.impl.ServiceWrapper;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.impl.JFXFileFilter;
/*     */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.component.filechooser.ChooserSelectionWrapper;
/*     */ import com.dukascopy.dds2.greed.gui.component.filechooser.TransportFileChooser;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.StrategiesContentPane;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.table.StrategiesTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.AbstractServiceTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategyTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.util.IOUtils;
/*     */ import com.dukascopy.transport.common.datafeed.FileType;
/*     */ import com.dukascopy.transport.common.msg.strategy.FileItem;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JFrame;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ class SaveAsAction extends TreeAction
/*     */ {
/*  30 */   private static final Logger LOGGER = LoggerFactory.getLogger(SaveAsAction.class);
/*     */ 
/*     */   public SaveAsAction(WorkspaceJTree workspaceJTree) {
/*  33 */     super(workspaceJTree);
/*     */   }
/*     */ 
/*     */   protected Object executeInternal(Object param)
/*     */   {
/*  38 */     AbstractServiceTreeNode stNode = (AbstractServiceTreeNode)param;
/*  39 */     return saveAs(stNode);
/*     */   }
/*     */ 
/*     */   private Boolean saveAs(AbstractServiceTreeNode serviceNode)
/*     */   {
/*  44 */     JFrame frame = (JFrame)GreedContext.get("clientGui");
/*     */ 
/*  46 */     ServiceWrapper sw = serviceNode.getServiceWrapper();
/*     */ 
/*  48 */     File binaryFile = sw.getBinaryFile();
/*     */ 
/*  50 */     if (binaryFile != null)
/*     */     {
/*  52 */       JFileChooser fileChooser = new JFileChooser(binaryFile.getParentFile());
/*     */ 
/*  54 */       fileChooser.setFileSelectionMode(0);
/*  55 */       fileChooser.setMultiSelectionEnabled(false);
/*  56 */       fileChooser.setDialogTitle("Save file as...");
/*     */ 
/*  58 */       JFXFileFilter ff = new JFXFileFilter();
/*     */ 
/*  60 */       fileChooser.setFileFilter(ff);
/*     */ 
/*  62 */       ChooserSelectionWrapper selection = TransportFileChooser.showSaveDialog(FileType.STRATEGY, fileChooser, frame, binaryFile.getName());
/*     */ 
/*  64 */       if (selection != null)
/*     */       {
/*  67 */         FileItem fileItem = selection.getFileItem();
/*     */ 
/*  69 */         String fileName = fileItem.getFileName();
/*     */ 
/*  71 */         String extension = StratUtils.getExtension(fileName);
/*     */ 
/*  73 */         if ((extension == null) || (!extension.equals(ff.getExtension()))) {
/*  74 */           fileName = fileName + "." + ff.getExtension();
/*     */         }
/*     */ 
/*  77 */         fileItem.setFileName(fileName);
/*     */ 
/*  81 */         File selectedFile = new File(fileName);
/*     */ 
/*  83 */         FileOutputStream fos = null;
/*  84 */         FileInputStream fis = null;
/*     */         try
/*     */         {
/*  87 */           fos = new FileOutputStream(selectedFile);
/*  88 */           fis = new FileInputStream(binaryFile);
/*     */ 
/*  90 */           StratUtils.turboPipe(fis, fos);
/*     */         } catch (Exception e) {
/*  92 */           LOGGER.error(e.getMessage(), e);
/*     */         } finally {
/*  94 */           IOUtils.closeQuietly(fis);
/*  95 */           IOUtils.closeQuietly(fos);
/*     */         }
/*     */ 
/*  98 */         sw.setBinaryFile(selectedFile);
/*     */ 
/* 100 */         if ((serviceNode instanceof StrategyTreeNode))
/*     */         {
/* 102 */           StrategyTreeNode strategyNode = (StrategyTreeNode)serviceNode;
/* 103 */           strategyNode.setBinaryFile(selectedFile);
/*     */ 
/* 105 */           ClientSettingsStorage settingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 106 */           settingsStorage.saveStrategyNewBean(strategyNode.getStrategy());
/*     */ 
/* 108 */           JForexClientFormLayoutManager clientFormLayoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/* 109 */           clientFormLayoutManager.getStrategiesPanel().getTable().repaint();
/*     */         }
/*     */ 
/* 182 */         return Boolean.valueOf(true);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 187 */     return Boolean.valueOf(false);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.SaveAsAction
 * JD-Core Version:    0.6.0
 */