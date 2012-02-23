/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.api.IStrategy;
/*    */ import com.dukascopy.api.impl.CustIndicatorWrapper;
/*    */ import com.dukascopy.charts.persistence.CustomIndicatorBean;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.util.IncorrectClassTypeException;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.FileChooserDialogHelper;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.CustIndTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceNodeFactory;
/*    */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ class OpenCustIndAction extends CustomIndicatorAction
/*    */ {
/* 22 */   private static final Logger LOGGER = LoggerFactory.getLogger(OpenCustIndAction.class);
/*    */   final ClientSettingsStorage jForexClientSettingsStorage;
/*    */ 
/*    */   OpenCustIndAction(WorkspaceJTree workspaceJTree, WorkspaceNodeFactory workspaceNodeFactory, IChartTabsAndFramesController chartTabsAndFramesController, ClientSettingsStorage jForexClientSettingsStorage)
/*    */   {
/* 31 */     super(chartTabsAndFramesController, workspaceJTree, workspaceNodeFactory);
/*    */ 
/* 33 */     this.jForexClientSettingsStorage = jForexClientSettingsStorage;
/*    */   }
/*    */ 
/*    */   protected Object executeInternal(Object param) {
/* 37 */     CustIndicatorWrapper custIndWrapper = null;
/*    */     try {
/* 39 */       custIndWrapper = FileChooserDialogHelper.openCustIndFileChooser();
/*    */     } catch (IncorrectClassTypeException e) {
/* 41 */       LOGGER.error("An attempt to load indicator from non-indicator file", e);
/*    */     }
/* 43 */     if (custIndWrapper == null) {
/* 44 */       return null;
/*    */     }
/*    */     try
/*    */     {
/* 48 */       if ((custIndWrapper.getSourceFile() != null) && (implementsInterface(custIndWrapper.getSourceFile(), IStrategy.class))) {
/* 49 */         return null;
/*    */       }
/*    */     }
/*    */     catch (IOException ex)
/*    */     {
/*    */     }
/* 55 */     if ((custIndWrapper.getSourceFile() == null) && (custIndWrapper.getBinaryFile() != null)) {
/* 56 */       String sourceFilePath = custIndWrapper.getBinaryFile().getAbsolutePath().substring(0, custIndWrapper.getBinaryFile().getAbsolutePath().lastIndexOf(".jfx"));
/* 57 */       File sourceFile = new File(sourceFilePath + ".java");
/* 58 */       if (!sourceFile.exists()) {
/* 59 */         sourceFile = new File(sourceFilePath + ".mq4");
/* 60 */         if (!sourceFile.exists()) {
/* 61 */           sourceFile = null;
/*    */         }
/*    */       }
/* 64 */       custIndWrapper.setSourceFile(sourceFile);
/*    */     }
/*    */ 
/* 67 */     CustIndTreeNode custIndTreeNode = this.workspaceNodeFactory.createServiceTreeNodeFrom(custIndWrapper);
/* 68 */     this.workspaceJTree.addCustIndTreeNode(custIndTreeNode);
/* 69 */     this.jForexClientSettingsStorage.save(new CustomIndicatorBean(custIndTreeNode.getId(), custIndWrapper.getSourceFile(), custIndWrapper.getBinaryFile()));
/* 70 */     this.workspaceJTree.selectNode(custIndTreeNode);
/* 71 */     edit(custIndTreeNode, false);
/*    */ 
/* 73 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.OpenCustIndAction
 * JD-Core Version:    0.6.0
 */