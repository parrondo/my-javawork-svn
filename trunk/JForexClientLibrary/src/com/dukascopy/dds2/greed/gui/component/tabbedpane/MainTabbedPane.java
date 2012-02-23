/*     */ package com.dukascopy.dds2.greed.gui.component.tabbedpane;
/*     */ 
/*     */ import com.dukascopy.api.impl.StrategyWrapper;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.StrategyTestPanel;
/*     */ import java.awt.Component;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTabbedPane;
/*     */ import javax.swing.JViewport;
/*     */ 
/*     */ public class MainTabbedPane extends JTabbedPane
/*     */ {
/*     */   public MainTabbedPane()
/*     */   {
/*  21 */     setTabLayoutPolicy(1);
/*     */   }
/*     */ 
/*     */   public void addStrategyTesterTab(int strategyId, StrategyWrapper strategyWrapper) {
/*  25 */     StrategyTestPanel strategyTestPanel = null;
/*  26 */     Component jScrollComponent = null;
/*  27 */     for (Component component : getComponents()) {
/*  28 */       if ((component instanceof JScrollPane)) {
/*  29 */         jScrollComponent = component;
/*  30 */         component = ((JScrollPane)component).getViewport().getView();
/*     */       }
/*  32 */       if ((component instanceof StrategyTestPanel)) {
/*  33 */         strategyTestPanel = (StrategyTestPanel)component;
/*  34 */         break;
/*     */       }
/*     */     }
/*  37 */     if (strategyTestPanel != null) {
/*  38 */       strategyTestPanel.initWithStrategy(strategyId, strategyWrapper);
/*  39 */       setSelectedComponent(jScrollComponent);
/*     */     }
/*     */   }
/*     */ 
/*     */   public JPanel createOrGetCustomBottomTab(String key) {
/*  44 */     JPanel bottomCustomTab = findIBottomTab(key);
/*  45 */     if (bottomCustomTab != null) {
/*  46 */       return bottomCustomTab;
/*     */     }
/*     */ 
/*  49 */     bottomCustomTab = new BottomCustomTab();
/*  50 */     addTab(key, bottomCustomTab);
/*     */ 
/*  52 */     return bottomCustomTab;
/*     */   }
/*     */ 
/*     */   private JPanel findIBottomTab(String key) {
/*  56 */     for (int i = 0; i < getTabCount(); i++) {
/*  57 */       Component component = getComponentAt(i);
/*  58 */       if ((component instanceof BottomCustomTab)) {
/*  59 */         String curKey = getTitleAt(i);
/*  60 */         if ((curKey != null) && (curKey.equals(key))) {
/*  61 */           return (JPanel)component;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  66 */     return null;
/*     */   }
/*     */ 
/*     */   public void removeBottomTab(String key) {
/*  70 */     JPanel iBottomTab = findIBottomTab(key);
/*  71 */     if (iBottomTab == null) {
/*  72 */       return;
/*     */     }
/*  74 */     remove(iBottomTab);
/*     */   }
/*     */ 
/*     */   class BottomCustomTab extends JPanel {
/*     */     BottomCustomTab() {
/*  79 */       JPopupMenu jPopupMenu = new JPopupMenu();
/*  80 */       JMenuItem closeMenuItem = new JMenuItem("Close");
/*  81 */       closeMenuItem.addActionListener(new ActionListener(MainTabbedPane.this)
/*     */       {
/*     */         public void actionPerformed(ActionEvent event) {
/*  84 */           MainTabbedPane.this.remove(MainTabbedPane.BottomCustomTab.this);
/*     */         }
/*     */       });
/*  88 */       jPopupMenu.add(closeMenuItem);
/*     */ 
/*  90 */       MainTabbedPane.this.addMouseListener(new MouseAdapter(MainTabbedPane.this, jPopupMenu)
/*     */       {
/*     */         public void mouseReleased(MouseEvent event) {
/*  93 */           processMouseReleasedPressedEvent(event);
/*     */         }
/*     */ 
/*     */         public void mousePressed(MouseEvent event) {
/*  97 */           processMouseReleasedPressedEvent(event);
/*     */         }
/*     */ 
/*     */         private void processMouseReleasedPressedEvent(MouseEvent event) {
/* 101 */           if (!event.isPopupTrigger()) {
/* 102 */             return;
/*     */           }
/* 104 */           this.val$jPopupMenu.show(MainTabbedPane.this, event.getX(), event.getY());
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tabbedpane.MainTabbedPane
 * JD-Core Version:    0.6.0
 */