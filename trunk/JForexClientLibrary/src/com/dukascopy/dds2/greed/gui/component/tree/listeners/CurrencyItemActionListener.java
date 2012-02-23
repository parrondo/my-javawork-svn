/*    */ package com.dukascopy.dds2.greed.gui.component.tree.listeners;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceNodeFactory;
/*    */ import com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper;
/*    */ import com.dukascopy.dds2.greed.model.MarketView;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ 
/*    */ public class CurrencyItemActionListener
/*    */   implements ActionListener
/*    */ {
/*    */   WorkspaceJTree workspaceJTree;
/*    */   WorkspaceNodeFactory workspaceNodeFactory;
/*    */   IWorkspaceHelper workspaceHelper;
/*    */   private MarketView marketView;
/*    */ 
/*    */   public CurrencyItemActionListener(WorkspaceJTree workspaceJTree, WorkspaceNodeFactory workspaceNodeFactory, IWorkspaceHelper workspaceHelper, MarketView marketView)
/*    */   {
/* 25 */     this.workspaceJTree = workspaceJTree;
/* 26 */     this.workspaceNodeFactory = workspaceNodeFactory;
/* 27 */     this.workspaceHelper = workspaceHelper;
/* 28 */     this.marketView = marketView;
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent e)
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.listeners.CurrencyItemActionListener
 * JD-Core Version:    0.6.0
 */