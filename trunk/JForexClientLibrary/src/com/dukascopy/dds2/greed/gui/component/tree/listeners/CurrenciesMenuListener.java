/*     */ package com.dukascopy.dds2.greed.gui.component.tree.listeners;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceNodeFactory;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceRootNode;
/*     */ import com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenuItem;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import java.awt.Font;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.Set;
/*     */ import javax.swing.JMenu;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.event.MenuEvent;
/*     */ import javax.swing.event.MenuListener;
/*     */ import javax.swing.tree.DefaultTreeModel;
/*     */ 
/*     */ public class CurrenciesMenuListener
/*     */   implements MenuListener
/*     */ {
/*     */   private Font nonTradableFont;
/*     */   private JLocalizableMenuItem addAllCurrencysItem;
/*     */   private JLocalizableMenuItem removeAllCurrencysItem;
/*     */   JMenu currenciesMenu;
/*     */   public IWorkspaceHelper workspaceHelper;
/*     */   public WorkspaceNodeFactory workspaceNodeFactory;
/*     */   public WorkspaceJTree workspaceJTree;
/*     */   public MarketView marketView;
/*     */ 
/*     */   public CurrenciesMenuListener(JMenu currenciesMenu)
/*     */   {
/*  40 */     this.currenciesMenu = currenciesMenu;
/*  41 */     Font regularFont = currenciesMenu.getFont();
/*  42 */     this.nonTradableFont = new Font(regularFont.getName(), 2, regularFont.getSize());
/*     */ 
/*  44 */     this.workspaceHelper = ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceHelper();
/*  45 */     this.workspaceNodeFactory = ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceNodeFactory();
/*  46 */     this.workspaceJTree = ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceJTree();
/*  47 */     this.marketView = ((MarketView)GreedContext.get("marketView"));
/*     */   }
/*     */ 
/*     */   public void menuCanceled(MenuEvent e) {
/*  51 */     removeAllItems(e);
/*     */   }
/*     */ 
/*     */   public void menuDeselected(MenuEvent e) {
/*  55 */     removeAllItems(e);
/*     */   }
/*     */ 
/*     */   public void menuSelected(MenuEvent e)
/*     */   {
/*  61 */     boolean instrumentAdded = false;
/*     */ 
/*  63 */     Instrument[] availableInstrumentsArray = this.workspaceHelper.getAvailableInstrumentsAsArray();
/*  64 */     for (Instrument instr : availableInstrumentsArray) {
/*  65 */       if (instr == null)
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  83 */     if (!instrumentAdded) {
/*  84 */       JMenuItem noInstrAvailable = new JMenuItem("<Empty List>");
/*  85 */       noInstrAvailable.setEnabled(false);
/*  86 */       this.currenciesMenu.add(noInstrAvailable);
/*     */     }
/*     */ 
/*  89 */     this.currenciesMenu.addSeparator();
/*  90 */     this.currenciesMenu.add(getAddAllCurrencysItem());
/*     */ 
/*  93 */     this.addAllCurrencysItem.setEnabled(instrumentAdded);
/*     */   }
/*     */ 
/*     */   private void removeAllItems(MenuEvent e) {
/*  97 */     JMenu source = (JMenu)e.getSource();
/*  98 */     source.removeAll();
/*  99 */     source.validate();
/*     */   }
/*     */ 
/*     */   private JLocalizableMenuItem getAddAllCurrencysItem() {
/* 103 */     if (this.addAllCurrencysItem == null) {
/* 104 */       this.addAllCurrencysItem = new JLocalizableMenuItem("item.add.currency.all");
/* 105 */       this.addAllCurrencysItem.addActionListener(new ActionListener()
/*     */       {
/*     */         public void actionPerformed(ActionEvent e) {
/* 108 */           CurrenciesMenuListener.this.workspaceHelper.subscribeToInstruments(Instrument.toStringSet(CurrenciesMenuListener.this.workspaceHelper.getUnsubscribedInstruments()));
/*     */         }
/*     */       });
/*     */     }
/* 113 */     return this.addAllCurrencysItem;
/*     */   }
/*     */ 
/*     */   private JLocalizableMenuItem getRemoveAllCurrencysItem() {
/* 117 */     if (this.removeAllCurrencysItem == null)
/*     */     {
/* 124 */       this.removeAllCurrencysItem = new JLocalizableMenuItem("item.remove.currency.all");
/* 125 */       this.removeAllCurrencysItem.addActionListener(new ActionListener()
/*     */       {
/*     */         public void actionPerformed(ActionEvent e) {
/* 128 */           CurrenciesMenuListener.this.removeAllInstr();
/*     */         } } );
/*     */     }
/* 132 */     return this.removeAllCurrencysItem;
/*     */   }
/*     */ 
/*     */   private void removeAllInstr() {
/* 136 */     Set subscribedInstruments = this.workspaceHelper.getSubscribedInstruments();
/* 137 */     for (Instrument instr : this.workspaceHelper.getSubscribedInstruments()) {
/* 138 */       if (removeInstrument(instr)) {
/* 139 */         subscribedInstruments.remove(instr);
/*     */       }
/*     */     }
/* 142 */     this.workspaceHelper.subscribeToInstruments(Instrument.toStringSet(subscribedInstruments));
/*     */   }
/*     */ 
/*     */   private boolean removeInstrument(Instrument instrument) {
/* 146 */     boolean isSuccessful = false;
/* 147 */     Set subscribedInstrument = this.workspaceHelper.getSubscribedInstruments();
/* 148 */     WorkspaceRootNode rootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/*     */ 
/* 151 */     return isSuccessful;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.listeners.CurrenciesMenuListener
 * JD-Core Version:    0.6.0
 */