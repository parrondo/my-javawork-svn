/*    */ package com.dukascopy.dds2.greed.gui.component.tree.listeners;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.actions.ITreeAction;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.actions.TreeActionFactory;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.actions.TreeActionType;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.JMenu;
/*    */ import javax.swing.JMenuItem;
/*    */ import javax.swing.event.MenuEvent;
/*    */ import javax.swing.event.MenuListener;
/*    */ 
/*    */ public class ChartsMenuListener
/*    */   implements MenuListener
/*    */ {
/*    */   JMenu chartsMenu;
/*    */   IWorkspaceHelper workspaceHelper;
/*    */   WorkspaceJTree workspaceJTree;
/*    */   TreeActionFactory treeActionFactory;
/*    */   WorkspaceTreeNode workspaceTreeNode;
/*    */ 
/*    */   public ChartsMenuListener(JMenu chartsMenu, WorkspaceJTree workspaceJTree, IWorkspaceHelper workspaceHelper, TreeActionFactory treeActionFactory, WorkspaceTreeNode workspaceTreeNode)
/*    */   {
/* 33 */     this.chartsMenu = chartsMenu;
/* 34 */     this.workspaceJTree = workspaceJTree;
/* 35 */     this.workspaceHelper = workspaceHelper;
/* 36 */     this.treeActionFactory = treeActionFactory;
/* 37 */     this.workspaceTreeNode = workspaceTreeNode;
/*    */   }
/*    */ 
/*    */   public void menuCanceled(MenuEvent e) {
/* 41 */     removeAllItems(e);
/*    */   }
/*    */ 
/*    */   public void menuDeselected(MenuEvent e) {
/* 45 */     removeAllItems(e);
/*    */   }
/*    */ 
/*    */   public void menuSelected(MenuEvent e)
/*    */   {
/* 50 */     Instrument[] availableInstrumentsArray = this.workspaceHelper.getAvailableInstrumentsAsArray();
/* 51 */     for (Instrument instr : availableInstrumentsArray) {
/* 52 */       JMenuItem instrumentItem = new JMenuItem(instr.toString());
/* 53 */       instrumentItem.addActionListener(new ActionListener() {
/*    */         public void actionPerformed(ActionEvent event) {
/* 55 */           Instrument instrument = Instrument.fromString(((JMenuItem)event.getSource()).getText());
/* 56 */           ChartsMenuListener.this.treeActionFactory.createAction(TreeActionType.ADD_CHART, ChartsMenuListener.this.workspaceJTree).execute(new Object[] { ChartsMenuListener.this.workspaceTreeNode, instrument });
/*    */         }
/*    */       });
/* 60 */       this.chartsMenu.add(instrumentItem);
/*    */     }
/*    */   }
/*    */ 
/*    */   private void removeAllItems(MenuEvent e) {
/* 65 */     JMenu source = (JMenu)e.getSource();
/* 66 */     source.removeAll();
/* 67 */     source.validate();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.listeners.ChartsMenuListener
 * JD-Core Version:    0.6.0
 */