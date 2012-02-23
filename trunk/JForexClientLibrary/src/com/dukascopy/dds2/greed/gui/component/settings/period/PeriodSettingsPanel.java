/*     */ package com.dukascopy.dds2.greed.gui.component.settings.period;
/*     */ 
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*     */ import com.dukascopy.charts.utils.helper.LocalizedMessageHelper;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.ChartPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.toolbar.ChartToolBar;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.toolbar.ChartToolBar.ComboBoxType;
/*     */ import com.dukascopy.dds2.greed.gui.component.settings.AbstractSettingsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.settings.SettingsTabbedFrame;
/*     */ import com.dukascopy.dds2.greed.gui.component.settings.period.panel.AbstractModePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.settings.period.panel.SimpleModePanel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.List;
/*     */ import javax.swing.JComboBox;
/*     */ 
/*     */ public class PeriodSettingsPanel extends AbstractSettingsPanel
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private AbstractModePanel simpleModePanel;
/*     */ 
/*     */   public PeriodSettingsPanel(SettingsTabbedFrame parent)
/*     */   {
/*  38 */     super(parent);
/*     */   }
/*     */ 
/*     */   protected void build()
/*     */   {
/*  43 */     setLayout(new BorderLayout());
/*  44 */     add(getSimpleModePanel(), "Center");
/*     */   }
/*     */ 
/*     */   public void applySettings()
/*     */   {
/*  49 */     List periods = getSimpleModePanel().getPresentedPeriods();
/*  50 */     periods = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).sortChartPeriods(periods);
/*  51 */     ((ClientSettingsStorage)GreedContext.get("settingsStorage")).saveChartPeriods(periods);
/*     */ 
/*  53 */     applyPeriodsChangeForChartToolBars(periods);
/*     */   }
/*     */ 
/*     */   private void applyPeriodsChangeForChartToolBars(List<JForexPeriod> periods) {
/*  57 */     ClientFormLayoutManager layoutManager = (ClientFormLayoutManager)GreedContext.get("layoutManager");
/*  58 */     IChartTabsAndFramesController chartTabsAndFramesController = layoutManager.getChartTabsController();
/*  59 */     for (Integer i : getChartsController().getChartControllerIdies()) {
/*  60 */       ChartPanel chartPanel = chartTabsAndFramesController.getChartPanelByPanelId(i.intValue());
/*  61 */       if (chartPanel != null) {
/*  62 */         ChartToolBar chartToolBar = chartPanel.getToolBar();
/*  63 */         JComboBox cmb = chartToolBar.getComboBox(ChartToolBar.ComboBoxType.PERIODS);
/*  64 */         applyPeriodsChangeForPeriodsCombo(cmb, periods);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void applyPeriodsChangeForPeriodsCombo(JComboBox cmb, List<JForexPeriod> periods)
/*     */   {
/*  71 */     if (periods != null) {
/*  72 */       ActionListener[] als = cmb.getActionListeners();
/*  73 */       for (ActionListener al : als) {
/*  74 */         cmb.removeActionListener(al);
/*     */       }
/*     */ 
/*  77 */       Object selectedItem = cmb.getSelectedItem();
/*     */ 
/*  79 */       cmb.removeAllItems();
/*     */ 
/*  81 */       for (JForexPeriod period : periods) {
/*  82 */         cmb.addItem(period);
/*     */       }
/*     */ 
/*  85 */       cmb.setSelectedItem(selectedItem);
/*     */ 
/*  87 */       for (ActionListener al : als)
/*  88 */         cmb.addActionListener(al);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void resetFields()
/*     */   {
/*  95 */     List periods = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).restoreChartPeriods();
/*  96 */     getSimpleModePanel().clear();
/*  97 */     getSimpleModePanel().addPeriods(periods);
/*     */   }
/*     */ 
/*     */   public void resetToDefaults()
/*     */   {
/* 103 */     getSimpleModePanel().resetPeriodsToDefaults();
/*     */   }
/*     */ 
/*     */   public boolean verifySettings()
/*     */   {
/* 109 */     List periods = getSimpleModePanel().getPresentedPeriods();
/* 110 */     if (periods.isEmpty()) {
/* 111 */       LocalizedMessageHelper.showInformtionMessage(this, LocalizationManager.getText("at.least.one.period.has.to.be.selected"));
/* 112 */       return false;
/*     */     }
/* 114 */     return true;
/*     */   }
/*     */ 
/*     */   protected AbstractModePanel getSimpleModePanel() {
/* 118 */     if (this.simpleModePanel == null) {
/* 119 */       List defaultPeriods = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).getDefaultChartPeriods();
/* 120 */       this.simpleModePanel = new SimpleModePanel("custom.period.selection", defaultPeriods);
/*     */     }
/* 122 */     return this.simpleModePanel;
/*     */   }
/*     */ 
/*     */   protected static DDSChartsController getChartsController() {
/* 126 */     return (DDSChartsController)GreedContext.get("chartsController");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.settings.period.PeriodSettingsPanel
 * JD-Core Version:    0.6.0
 */