/*    */ package com.dukascopy.dds2.greed.gui.component.tree.listeners;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartsNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.IndicatorsNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategiesNode;
/*    */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*    */ import javax.swing.event.TreeExpansionEvent;
/*    */ import javax.swing.event.TreeWillExpandListener;
/*    */ import javax.swing.tree.ExpandVetoException;
/*    */ import javax.swing.tree.TreePath;
/*    */ 
/*    */ public class WorkspaceJTreeWillExpandListener
/*    */   implements TreeWillExpandListener
/*    */ {
/*    */   ClientSettingsStorage clientSettingsStorage;
/*    */ 
/*    */   public WorkspaceJTreeWillExpandListener(ClientSettingsStorage clientSettingsStorage)
/*    */   {
/* 17 */     this.clientSettingsStorage = clientSettingsStorage;
/*    */   }
/*    */ 
/*    */   public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
/* 21 */     if ((event.getPath().getLastPathComponent() instanceof ChartsNode)) {
/* 22 */       this.clientSettingsStorage.setChartsExpanded(true);
/*    */     }
/*    */ 
/* 25 */     if ((event.getPath().getLastPathComponent() instanceof StrategiesNode)) {
/* 26 */       this.clientSettingsStorage.setStrategiesExpanded(true);
/*    */     }
/*    */ 
/* 29 */     if ((event.getPath().getLastPathComponent() instanceof IndicatorsNode))
/* 30 */       this.clientSettingsStorage.setIndicatorsExpanded(true);
/*    */   }
/*    */ 
/*    */   public void treeWillCollapse(TreeExpansionEvent event)
/*    */     throws ExpandVetoException
/*    */   {
/* 37 */     if ((event.getPath().getLastPathComponent() instanceof ChartsNode)) {
/* 38 */       this.clientSettingsStorage.setChartsExpanded(false);
/*    */     }
/*    */ 
/* 41 */     if ((event.getPath().getLastPathComponent() instanceof StrategiesNode)) {
/* 42 */       this.clientSettingsStorage.setStrategiesExpanded(false);
/*    */     }
/*    */ 
/* 45 */     if ((event.getPath().getLastPathComponent() instanceof IndicatorsNode))
/* 46 */       this.clientSettingsStorage.setIndicatorsExpanded(false);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.listeners.WorkspaceJTreeWillExpandListener
 * JD-Core Version:    0.6.0
 */