/*     */ package com.dukascopy.dds2.greed.gui.component.chart;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.HeadlessException;
/*     */ import java.awt.Image;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ class UndockedJFrame extends JFrame
/*     */   implements DockedUndockedFrame, Localizable
/*     */ {
/*  23 */   private Image inactiveChartFrameImage = StratUtils.loadImage("rc/media/titlebar_chart_inactive.png");
/*  24 */   private Image activeChartFrameImage = StratUtils.loadImage("rc/media/titlebar_chart_active.png");
/*  25 */   private Image inactiveStrategyFrameImage = StratUtils.loadImage("rc/media/titlebar_strategy_inactive.png");
/*  26 */   private Image activeStrategyFrameImage = StratUtils.loadImage("rc/media/titlebar_strategy_active.png");
/*  27 */   private Image inactiveIndicatorFrameImage = StratUtils.loadImage("rc/media/titlebar_indicator_inactive.png");
/*  28 */   private Image activeIndicatorFrameImage = StratUtils.loadImage("rc/media/titlebar_indicator_active.png");
/*     */   private TabsAndFramePanel content;
/*     */   private String title;
/*     */   private String toolTipText;
/*     */ 
/*     */   UndockedJFrame(String title)
/*     */     throws HeadlessException
/*     */   {
/*  37 */     this.title = title;
/*     */   }
/*     */ 
/*     */   public void setFrameContent(TabsAndFramePanel tabsAndFramePanel)
/*     */   {
/*  42 */     this.content = tabsAndFramePanel;
/*  43 */     JPanel contentPane = new JPanel(new BorderLayout());
/*  44 */     tabsAndFramePanel.placeComponentsOn(contentPane, this);
/*  45 */     setContentPane(contentPane);
/*  46 */     addWindowListener(new WindowAdapter() {
/*     */       public void windowActivated(WindowEvent e) {
/*  48 */         UndockedJFrame.this.loadFrameImageIcon(true);
/*  49 */         if ((UndockedJFrame.this.getContent() instanceof ChartPanel))
/*  50 */           ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getTabbedPane().setLastActiveChartPanelId(UndockedJFrame.this.getPanelId());
/*     */       }
/*     */ 
/*     */       public void windowDeactivated(WindowEvent e) {
/*  54 */         UndockedJFrame.this.loadFrameImageIcon(false);
/*     */       }
/*     */     });
/*  59 */     if (shouldBeLocalized()) {
/*  60 */       LocalizationManager.addLocalizable(this);
/*     */     }
/*     */ 
/*  63 */     setTitle(this.title);
/*     */   }
/*     */ 
/*     */   public int getPanelId()
/*     */   {
/*  68 */     return this.content.getPanelId();
/*     */   }
/*     */ 
/*     */   public TabsAndFramePanel getContent() {
/*  72 */     return this.content;
/*     */   }
/*     */ 
/*     */   public void updateMenuItems(TabsOrderingMenuContainer tabsOrderingMenuContainer) {
/*  76 */     tabsOrderingMenuContainer.setMenuItemEnabled(false, new TabsOrderingMenuContainer.Action[] { TabsOrderingMenuContainer.Action.UNDOCK });
/*     */   }
/*     */ 
/*     */   public void setSelected(boolean isSelected) {
/*  80 */     if (isSelected)
/*  81 */       requestFocus();
/*     */   }
/*     */ 
/*     */   public boolean isSelected()
/*     */   {
/*  86 */     return isActive();
/*     */   }
/*     */ 
/*     */   public void setMaximum(boolean isMaximum)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean isUndocked() {
/*  94 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean shouldBeLocalized() {
/*  98 */     return ((getContent() instanceof ChartPanel)) || ((getContent() instanceof BottomPanelWithProfitLossLabel)) || ((getContent() instanceof BottomPanelWithProfitLossLabel));
/*     */   }
/*     */ 
/*     */   public void localize()
/*     */   {
/* 103 */     setTitle(this.title);
/*     */   }
/*     */ 
/*     */   public void setTitle(String title) {
/* 107 */     this.title = title;
/* 108 */     if ((getContent() instanceof ChartPanel)) {
/* 109 */       String output = LocalizationManager.getTextWithArgumentKeys("tab.title.tamplate", (Object[])this.title.split(","));
/* 110 */       super.setTitle(output);
/*     */     } else {
/* 112 */       super.setTitle(this.title);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getTitle_() {
/* 117 */     return this.title;
/*     */   }
/*     */ 
/*     */   public String getToolTipText()
/*     */   {
/* 122 */     return this.toolTipText;
/*     */   }
/*     */ 
/*     */   public void setToolTipText(String toolTipText)
/*     */   {
/* 127 */     this.toolTipText = toolTipText;
/*     */   }
/*     */ 
/*     */   private void loadFrameImageIcon(boolean isActive) {
/* 131 */     if ((this.content != null) && 
/* 132 */       ((this.content instanceof TabsAndFramePanelWithToolBar)))
/* 133 */       setIconImage(((TabsAndFramePanelWithToolBar)this.content).getPanelType(), isActive);
/*     */   }
/*     */ 
/*     */   private void setIconImage(TabedPanelType type, boolean isActive)
/*     */   {
/* 139 */     Image frameImage = null;
/* 140 */     if (TabedPanelType.CHART.equals(type))
/* 141 */       frameImage = isActive ? this.activeChartFrameImage : this.inactiveChartFrameImage;
/* 142 */     else if (TabedPanelType.STRATEGY.equals(type))
/* 143 */       frameImage = isActive ? this.activeStrategyFrameImage : this.inactiveStrategyFrameImage;
/* 144 */     else if (TabedPanelType.INDICATOR.equals(type)) {
/* 145 */       frameImage = isActive ? this.activeIndicatorFrameImage : this.inactiveIndicatorFrameImage;
/*     */     }
/*     */ 
/* 148 */     if (frameImage != null)
/* 149 */       setIconImage(frameImage);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.UndockedJFrame
 * JD-Core Version:    0.6.0
 */