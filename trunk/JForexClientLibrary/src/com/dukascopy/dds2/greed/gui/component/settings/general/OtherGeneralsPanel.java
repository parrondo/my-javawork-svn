/*     */ package com.dukascopy.dds2.greed.gui.component.settings.general;
/*     */ 
/*     */ import com.dukascopy.charts.settings.ChartSettings;
/*     */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.component.AccountStatementPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.settings.SettingsTabbedFrame;
/*     */ import com.dukascopy.dds2.greed.gui.component.status.GreedStatusBar;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBox;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public class OtherGeneralsPanel extends JPanel
/*     */ {
/*     */   private final SettingsTabbedFrame parent;
/*  27 */   private ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*     */   private JCheckBox oneClickTradingCheck;
/*     */   private JCheckBox chartTradingCheckBox;
/*     */   private JCheckBox ordersValidation;
/*     */   private JCheckBox applySlippageToAll;
/*     */   private JCheckBox fillOrKillOrders;
/*     */   private JCheckBox applyStopLossToAll;
/*     */   private JCheckBox applyTakeProfitToAll;
/*     */   private JCheckBox applyTimeValidationToAll;
/*     */ 
/*     */   public OtherGeneralsPanel(SettingsTabbedFrame parent)
/*     */   {
/*  40 */     this.parent = parent;
/*  41 */     init();
/*     */   }
/*     */ 
/*     */   private void init() {
/*  45 */     int INSET = 3;
/*     */ 
/*  47 */     setLayout(new GridLayout(8, 1, 3, 3));
/*     */ 
/*  49 */     setBorder(BorderFactory.createEmptyBorder(35, 5, 10, 5));
/*     */ 
/*  51 */     addOneClickTrading();
/*     */ 
/*  53 */     addChartTrading();
/*     */ 
/*  55 */     addOrdersValidation();
/*     */ 
/*  57 */     addApplySlippageToAllMarket();
/*     */ 
/*  59 */     addApplyTimeValidationToAllBidOffers();
/*     */ 
/*  61 */     addApplyStopLossToAllMarket();
/*     */ 
/*  63 */     addApplyTakeProfitToAllMarket();
/*     */ 
/*  65 */     addFillOrKillOrders();
/*     */   }
/*     */ 
/*     */   private void addOneClickTrading() {
/*  69 */     this.oneClickTradingCheck = new JLocalizableCheckBox("check.one.click.trading");
/*  70 */     this.oneClickTradingCheck.setToolTipText("check.one.click.trading.tooltip");
/*  71 */     add(this.oneClickTradingCheck);
/*     */ 
/*  73 */     this.oneClickTradingCheck.setMnemonic(84);
/*  74 */     this.oneClickTradingCheck.addItemListener(new ItemListener() {
/*     */       public void itemStateChanged(ItemEvent itemEvent) {
/*  76 */         OtherGeneralsPanel.this.parent.settingsChanged(true);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void addChartTrading() {
/*  83 */     this.chartTradingCheckBox = new JLocalizableCheckBox("check.chart.trading");
/*  84 */     this.chartTradingCheckBox.setToolTipText("check.chart.trading.tooltip");
/*     */ 
/*  86 */     if (GreedContext.isContest()) {
/*  87 */       ChartSettings.set(ChartSettings.Option.TRADING, Boolean.valueOf(false));
/*  88 */       return;
/*     */     }
/*     */ 
/*  91 */     add(this.chartTradingCheckBox);
/*     */ 
/*  93 */     this.chartTradingCheckBox.addItemListener(new ItemListener() {
/*     */       public void itemStateChanged(ItemEvent itemEvent) {
/*  95 */         OtherGeneralsPanel.this.parent.settingsChanged(true);
/*     */       } } );
/*     */   }
/*     */ 
/*     */   private void addOrdersValidation() {
/* 101 */     this.ordersValidation = new JLocalizableCheckBox("check.orders.validation");
/* 102 */     this.ordersValidation.setToolTipText("check.orders.validation.tooltip");
/* 103 */     add(this.ordersValidation);
/*     */ 
/* 105 */     this.ordersValidation.addItemListener(new ItemListener() {
/*     */       public void itemStateChanged(ItemEvent itemEvent) {
/* 107 */         OtherGeneralsPanel.this.parent.settingsChanged(true);
/*     */       } } );
/*     */   }
/*     */ 
/*     */   private void addFillOrKillOrders() {
/* 113 */     this.fillOrKillOrders = new JLocalizableCheckBox("check.fill.or.kill");
/* 114 */     this.fillOrKillOrders.setToolTipText("check.fill.or.kill.tooltip");
/*     */ 
/* 116 */     if ((GreedContext.isTest()) || (GreedContext.isPreDemo())) {
/* 117 */       add(this.fillOrKillOrders);
/*     */     }
/*     */ 
/* 120 */     this.fillOrKillOrders.addItemListener(new ItemListener() {
/*     */       public void itemStateChanged(ItemEvent itemEvent) {
/* 122 */         OtherGeneralsPanel.this.parent.settingsChanged(true);
/*     */       } } );
/*     */   }
/*     */ 
/*     */   private void addApplySlippageToAllMarket() {
/* 128 */     this.applySlippageToAll = new JLocalizableCheckBox("check.one.default.slipp");
/* 129 */     this.applySlippageToAll.setToolTipText("check.one.default.slipp.tooltip");
/* 130 */     add(this.applySlippageToAll);
/*     */ 
/* 132 */     this.applySlippageToAll.addItemListener(new ItemListener() {
/*     */       public void itemStateChanged(ItemEvent itemEvent) {
/* 134 */         OtherGeneralsPanel.this.parent.settingsChanged(true);
/*     */       } } );
/*     */   }
/*     */ 
/*     */   private void addApplyStopLossToAllMarket() {
/* 140 */     this.applyStopLossToAll = new JLocalizableCheckBox("check.one.default.sl");
/* 141 */     this.applyStopLossToAll.setToolTipText("check.one.default.sl.tooltip");
/* 142 */     add(this.applyStopLossToAll);
/*     */ 
/* 144 */     this.applyStopLossToAll.addItemListener(new ItemListener() {
/*     */       public void itemStateChanged(ItemEvent itemEvent) {
/* 146 */         OtherGeneralsPanel.this.parent.settingsChanged(true);
/*     */       } } );
/*     */   }
/*     */ 
/*     */   private void addApplyTakeProfitToAllMarket() {
/* 152 */     this.applyTakeProfitToAll = new JLocalizableCheckBox("check.one.default.tp");
/* 153 */     this.applyTakeProfitToAll.setToolTipText("check.one.default.tp.tooltip");
/* 154 */     add(this.applyTakeProfitToAll);
/*     */ 
/* 156 */     this.applyTakeProfitToAll.addItemListener(new ItemListener() {
/*     */       public void itemStateChanged(ItemEvent itemEvent) {
/* 158 */         OtherGeneralsPanel.this.parent.settingsChanged(true);
/*     */       } } );
/*     */   }
/*     */ 
/*     */   private void addApplyTimeValidationToAllBidOffers() {
/* 164 */     this.applyTimeValidationToAll = new JLocalizableCheckBox("check.deafault.good.for");
/* 165 */     this.applyTimeValidationToAll.setToolTipText("check.deafault.good.for.tooltip");
/* 166 */     add(this.applyTimeValidationToAll);
/*     */ 
/* 168 */     this.applyTimeValidationToAll.addItemListener(new ItemListener() {
/*     */       public void itemStateChanged(ItemEvent itemEvent) {
/* 170 */         OtherGeneralsPanel.this.parent.settingsChanged(true);
/*     */       } } );
/*     */   }
/*     */ 
/*     */   public void resetFields() {
/* 176 */     this.oneClickTradingCheck.setSelected(this.storage.restoreOneClickState());
/* 177 */     this.chartTradingCheckBox.setSelected(ChartSettings.getBoolean(ChartSettings.Option.TRADING));
/* 178 */     this.ordersValidation.setSelected(this.storage.restoreOrderValidationOn());
/* 179 */     this.applySlippageToAll.setSelected(this.storage.restoreApplySlippageToAllMarketOrders());
/* 180 */     this.applyStopLossToAll.setSelected(this.storage.restoreApplyStopLossToAllMarketOrders());
/* 181 */     this.applyTakeProfitToAll.setSelected(this.storage.restoreApplyTakeProfitToAllMarketOrders());
/* 182 */     this.fillOrKillOrders.setSelected(this.storage.restoreFillOrKillOrders());
/* 183 */     this.applyTimeValidationToAll.setSelected(this.storage.restoreApplyTimeValidationToAllMarketOrders());
/*     */ 
/* 185 */     if (GreedContext.isContest()) {
/* 186 */       this.oneClickTradingCheck.setEnabled(false);
/* 187 */       this.applyStopLossToAll.setEnabled(false);
/* 188 */       this.applyTakeProfitToAll.setEnabled(false);
/*     */ 
/* 191 */       this.ordersValidation.setSelected(true);
/* 192 */       this.ordersValidation.setEnabled(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void saveSettings() {
/* 197 */     ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/* 198 */     clientForm.getStatusBar().getAccountStatement().getOneClickCheckbox().setSelected(this.oneClickTradingCheck.isSelected());
/* 199 */     this.storage.saveOneClickState(this.oneClickTradingCheck.isSelected());
/* 200 */     ChartSettings.set(ChartSettings.Option.TRADING, Boolean.valueOf(this.chartTradingCheckBox.isSelected()));
/* 201 */     this.storage.save(ChartSettings.Option.TRADING, String.valueOf(this.chartTradingCheckBox.isSelected()));
/* 202 */     this.storage.saveOrderValidationOn(this.ordersValidation.isSelected());
/* 203 */     this.storage.saveApplySlippageToAllMarketOrders(this.applySlippageToAll.isSelected());
/* 204 */     this.storage.saveApplyStopLossToAllMarketOrders(this.applyStopLossToAll.isSelected());
/* 205 */     this.storage.saveApplyTakeProfitToAllMarketOrders(this.applyTakeProfitToAll.isSelected());
/* 206 */     this.storage.saveFillOrKillOrders(this.fillOrKillOrders.isSelected());
/* 207 */     this.storage.saveApplyTimeValidationToAllMarketOrders(this.applyTimeValidationToAll.isSelected());
/*     */   }
/*     */ 
/*     */   public void resetDefaults()
/*     */   {
/* 212 */     boolean isContest = GreedContext.isContest();
/* 213 */     this.oneClickTradingCheck.setSelected(!isContest);
/* 214 */     this.chartTradingCheckBox.setSelected(true);
/* 215 */     this.ordersValidation.setSelected(true);
/* 216 */     this.applySlippageToAll.setSelected(false);
/* 217 */     this.applyStopLossToAll.setSelected(isContest);
/* 218 */     this.applyTakeProfitToAll.setSelected(isContest);
/* 219 */     this.fillOrKillOrders.setSelected(false);
/* 220 */     this.applyTimeValidationToAll.setSelected(false);
/*     */ 
/* 222 */     if (isContest) {
/* 223 */       this.oneClickTradingCheck.setEnabled(false);
/* 224 */       this.applyStopLossToAll.setEnabled(false);
/* 225 */       this.applyTakeProfitToAll.setEnabled(false);
/*     */ 
/* 227 */       this.ordersValidation.setSelected(true);
/* 228 */       this.ordersValidation.setEnabled(false);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.settings.general.OtherGeneralsPanel
 * JD-Core Version:    0.6.0
 */