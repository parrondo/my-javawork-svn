/*     */ package com.dukascopy.dds2.greed.gui.component.settings.general;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.UpdateGuiDefaultsAction;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.component.dialog.JTimeUnitComboBox;
/*     */ import com.dukascopy.dds2.greed.gui.component.settings.SettingsTabbedFrame;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableComboBox;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountLabel;
/*     */ import com.dukascopy.dds2.greed.gui.util.spinners.AmountJSpinner;
/*     */ import com.dukascopy.dds2.greed.gui.util.spinners.CommodityAmountJSpinner;
/*     */ import com.dukascopy.dds2.greed.gui.util.spinners.CommonJSpinner;
/*     */ import com.dukascopy.dds2.greed.gui.util.spinners.SettingsAmountJSpinner;
/*     */ import com.dukascopy.dds2.greed.gui.util.spinners.SlippageJSpinner;
/*     */ import com.dukascopy.dds2.greed.gui.util.spinners.TrailingStepJSpinner;
/*     */ import java.awt.Color;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ 
/*     */ public class DefaultValuesPanel extends JPanel
/*     */   implements DocumentListener, ChangeListener
/*     */ {
/*     */   private SettingsTabbedFrame parent;
/*     */   private JPanel errorPanel;
/*     */   private LotAmountLabel amountLabel;
/*     */   private LotAmountLabel amountLabelXAU;
/*     */   private LotAmountLabel amountLabelXAG;
/*  57 */   private CommonJSpinner amountSpinner = new SettingsAmountJSpinner();
/*     */   private CommonJSpinner amountSpinnerXAU;
/*     */   private CommonJSpinner amountSpinnerXAG;
/*     */   private CommonJSpinner defaultTrailingStepSpinner;
/*     */   private CommonJSpinner orderValidityTimeSpinner;
/*  64 */   private SlippageJSpinner defaultSlippageSpinner = new SlippageJSpinner(5.0D, 1000.0D, 0.1D, 1, false);
/*  65 */   private SlippageJSpinner defaultOpenIfPipOffsetSpinner = new SlippageJSpinner(10.0D, 1000.0D, 0.1D, 1, false);
/*  66 */   private SlippageJSpinner defaultSlPipOffsetSpinner = new SlippageJSpinner(10.0D, 1.0D, 1000.0D, 0.1D, 1, false);
/*  67 */   private SlippageJSpinner defaultTpPipOffsetSpinner = new SlippageJSpinner(10.0D, 1.0D, 1000.0D, 0.1D, 1, false);
/*     */   private JLocalizableComboBox comboBox;
/*     */   private JLabel errorLabel;
/*  74 */   private ClientSettingsStorage settings = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*     */ 
/*  76 */   private static final Integer TEN = new Integer(10);
/*     */ 
/*  78 */   public DefaultValuesPanel(SettingsTabbedFrame parent) { this.parent = parent;
/*  79 */     initAndShow();
/*  80 */     setBorder(new JLocalizableRoundedBorder(this, "border.default.values"));
/*     */   }
/*     */ 
/*     */   private void initAndShow()
/*     */   {
/*  86 */     int INSET = 5;
/*     */ 
/*  88 */     setLayout(new GridBagLayout());
/*  89 */     GridBagConstraints c = new GridBagConstraints();
/*  90 */     c.insets = new Insets(5, 5, 5, 5);
/*  91 */     c.weightx = 1.0D;
/*  92 */     c.weighty = 1.0D;
/*  93 */     c.anchor = 18;
/*  94 */     int gridY = 0;
/*     */ 
/*  97 */     JPanel pAmoPri = new JPanel(new GridLayout(9, 2, 5, 5));
/*     */ 
/*  99 */     Dimension SIZE = new Dimension(10, this.amountSpinner.getPreferredSize().height);
/*     */ 
/* 101 */     this.amountLabel = new LotAmountLabel(this.settings.restoreDefaultAmountLot());
/* 102 */     pAmoPri.add(this.amountLabel);
/* 103 */     this.amountSpinner.setHorizontalAlignment(4);
/* 104 */     this.amountSpinner.setPreferredSize(SIZE);
/* 105 */     this.amountSpinner.setMaximumSize(SIZE);
/* 106 */     this.amountSpinner.setSize(SIZE);
/*     */ 
/* 108 */     pAmoPri.add(this.amountSpinner);
/*     */ 
/* 111 */     this.amountLabelXAU = new LotAmountLabel("label.amunt.units.xau");
/* 112 */     pAmoPri.add(this.amountLabelXAU);
/* 113 */     this.amountSpinnerXAU = new CommodityAmountJSpinner(Instrument.XAUUSD);
/* 114 */     this.amountSpinnerXAU.setHorizontalAlignment(4);
/* 115 */     this.amountSpinnerXAU.setPreferredSize(SIZE);
/* 116 */     this.amountSpinnerXAU.setMaximumSize(SIZE);
/* 117 */     this.amountSpinnerXAU.setSize(SIZE);
/*     */ 
/* 119 */     pAmoPri.add(this.amountSpinnerXAU);
/*     */ 
/* 121 */     this.amountLabelXAG = new LotAmountLabel("label.amunt.units.xag");
/* 122 */     pAmoPri.add(this.amountLabelXAG);
/* 123 */     this.amountSpinnerXAG = new CommodityAmountJSpinner(Instrument.XAGUSD);
/* 124 */     this.amountSpinnerXAG.setHorizontalAlignment(4);
/* 125 */     this.amountSpinnerXAG.setPreferredSize(SIZE);
/* 126 */     this.amountSpinnerXAG.setMaximumSize(SIZE);
/* 127 */     this.amountSpinnerXAG.setSize(SIZE);
/*     */ 
/* 129 */     pAmoPri.add(this.amountSpinnerXAG);
/*     */ 
/* 133 */     JLabel slippageLabel = new JLocalizableLabel("label.slippage");
/* 134 */     this.defaultSlippageSpinner.setHorizontalAlignment(4);
/* 135 */     pAmoPri.add(slippageLabel);
/* 136 */     this.defaultSlippageSpinner.setPreferredSize(SIZE);
/* 137 */     pAmoPri.add(this.defaultSlippageSpinner);
/*     */ 
/* 140 */     JLabel openIfPipOffsetLabel = new JLocalizableLabel("label.entry.pips");
/* 141 */     this.defaultOpenIfPipOffsetSpinner.setHorizontalAlignment(4);
/* 142 */     this.defaultOpenIfPipOffsetSpinner.setPreferredSize(SIZE);
/* 143 */     pAmoPri.add(openIfPipOffsetLabel);
/* 144 */     pAmoPri.add(this.defaultOpenIfPipOffsetSpinner);
/*     */ 
/* 147 */     JLabel slPipOffsetLabel = new JLocalizableLabel("label.stop.loss.pips");
/* 148 */     this.defaultSlPipOffsetSpinner.setHorizontalAlignment(4);
/* 149 */     this.defaultSlPipOffsetSpinner.setPreferredSize(SIZE);
/* 150 */     pAmoPri.add(slPipOffsetLabel);
/* 151 */     pAmoPri.add(this.defaultSlPipOffsetSpinner);
/*     */ 
/* 154 */     JLabel tpPipOffsetLabel = new JLocalizableLabel("label.take.profit.pips");
/* 155 */     this.defaultTpPipOffsetSpinner.setHorizontalAlignment(4);
/* 156 */     this.defaultTpPipOffsetSpinner.setPreferredSize(SIZE);
/* 157 */     pAmoPri.add(tpPipOffsetLabel);
/* 158 */     pAmoPri.add(this.defaultTpPipOffsetSpinner);
/*     */ 
/* 161 */     JLabel trailingStepLabel = new JLocalizableLabel("label.trailing.step.pips");
/* 162 */     this.defaultTrailingStepSpinner = new TrailingStepJSpinner(GuiUtilsAndConstants.TEN.doubleValue());
/* 163 */     this.defaultTrailingStepSpinner.setHorizontalAlignment(4);
/* 164 */     this.defaultTrailingStepSpinner.setPreferredSize(SIZE);
/* 165 */     pAmoPri.add(trailingStepLabel);
/* 166 */     pAmoPri.add(this.defaultTrailingStepSpinner);
/*     */ 
/* 169 */     JLabel goodForTimeLabel = new JLocalizableLabel("label.good.for.time");
/* 170 */     this.orderValidityTimeSpinner = new CommonJSpinner(GuiUtilsAndConstants.ONE.doubleValue(), GuiUtilsAndConstants.ONE.doubleValue(), 1.7976931348623157E+308D, GuiUtilsAndConstants.ONE.doubleValue(), 0, false, false);
/*     */ 
/* 172 */     this.orderValidityTimeSpinner.setHorizontalAlignment(4);
/* 173 */     this.orderValidityTimeSpinner.setPreferredSize(SIZE);
/*     */ 
/* 175 */     this.comboBox = new JTimeUnitComboBox();
/* 176 */     this.comboBox.setPreferredSize(new Dimension(this.comboBox.getPreferredSize().width, this.amountSpinner.getPreferredSize().height));
/*     */ 
/* 178 */     JPanel innerPanel = new JPanel(new GridLayout(1, 2, 5, 5));
/* 179 */     innerPanel.add(this.orderValidityTimeSpinner);
/* 180 */     innerPanel.add(this.comboBox);
/*     */ 
/* 182 */     pAmoPri.add(goodForTimeLabel);
/* 183 */     pAmoPri.add(innerPanel);
/*     */ 
/* 185 */     c.gridx = 0;
/* 186 */     c.gridy = (gridY++);
/* 187 */     c.fill = 2;
/* 188 */     add(pAmoPri, c);
/*     */ 
/* 191 */     this.errorPanel = new JPanel(new FlowLayout(1));
/* 192 */     this.errorLabel = new JLocalizableLabel("button.ok");
/* 193 */     this.errorLabel.setForeground(Color.red);
/* 194 */     this.errorPanel.add(this.errorLabel);
/* 195 */     this.errorPanel.setVisible(false);
/* 196 */     this.errorPanel.addMouseListener(new MouseAdapter() {
/*     */       public void mouseReleased(MouseEvent e) {
/* 198 */         DefaultValuesPanel.this.errorPanel.setVisible(false);
/*     */       }
/*     */ 
/*     */       public void mouseEntered(MouseEvent e) {
/* 202 */         DefaultValuesPanel.this.setCursor(Cursor.getPredefinedCursor(12));
/*     */       }
/*     */ 
/*     */       public void mouseExited(MouseEvent e) {
/* 206 */         DefaultValuesPanel.this.setCursor(Cursor.getDefaultCursor());
/*     */       }
/*     */     });
/* 210 */     c.gridy = (gridY++);
/* 211 */     c.fill = 2;
/* 212 */     add(this.errorPanel, c);
/*     */ 
/* 244 */     this.amountSpinner.addChangeListener(this);
/* 245 */     this.defaultSlippageSpinner.addChangeListener(this);
/* 246 */     this.defaultOpenIfPipOffsetSpinner.addChangeListener(this);
/* 247 */     this.defaultSlPipOffsetSpinner.addChangeListener(this);
/* 248 */     this.defaultTpPipOffsetSpinner.addChangeListener(this);
/* 249 */     this.defaultTrailingStepSpinner.addChangeListener(this);
/* 250 */     this.orderValidityTimeSpinner.addChangeListener(this);
/*     */   }
/*     */ 
/*     */   public void insertUpdate(DocumentEvent e)
/*     */   {
/* 255 */     this.parent.settingsChanged(true);
/*     */   }
/*     */ 
/*     */   public void removeUpdate(DocumentEvent e)
/*     */   {
/* 260 */     this.parent.settingsChanged(true);
/*     */   }
/*     */ 
/*     */   public void changedUpdate(DocumentEvent e)
/*     */   {
/* 265 */     this.parent.settingsChanged(true);
/*     */   }
/*     */ 
/*     */   public void stateChanged(ChangeEvent e)
/*     */   {
/* 270 */     this.parent.settingsChanged(true);
/*     */   }
/*     */ 
/*     */   public boolean verifySettings()
/*     */   {
/* 275 */     if ((!this.amountSpinner.validateEditor()) || (!this.amountSpinnerXAU.validateEditor()) || (!this.amountSpinnerXAG.validateEditor()) || (!this.defaultSlippageSpinner.validateEditor()) || (!this.defaultOpenIfPipOffsetSpinner.validateEditor()) || (!this.defaultSlPipOffsetSpinner.validateEditor()) || (!this.defaultTpPipOffsetSpinner.validateEditor()) || (!this.defaultTrailingStepSpinner.validateEditor()) || (!this.orderValidityTimeSpinner.validateEditor()))
/*     */     {
/* 284 */       return false;
/*     */     }
/* 286 */     String defaultAmount = ((BigDecimal)this.amountSpinner.getValue()).toPlainString();
/* 287 */     String defaultSlippage = ((BigDecimal)this.defaultSlippageSpinner.getValue()).toPlainString();
/* 288 */     String defaultXAUAmount = ((BigDecimal)this.amountSpinnerXAU.getValue()).toPlainString();
/* 289 */     String defaultXAGAmount = ((BigDecimal)this.amountSpinnerXAG.getValue()).toPlainString();
/* 290 */     String defaultOpenIfOffset = ((BigDecimal)this.defaultOpenIfPipOffsetSpinner.getValue()).toPlainString();
/* 291 */     String defaultStopLossOffset = ((BigDecimal)this.defaultSlPipOffsetSpinner.getValue()).toPlainString();
/* 292 */     String defaultTakeProfitOffset = ((BigDecimal)this.defaultTpPipOffsetSpinner.getValue()).toPlainString();
/* 293 */     String defaultTrailingStep = ((BigDecimal)this.defaultTrailingStepSpinner.getValue()).toPlainString();
/* 294 */     String orderValidityTime = ((BigDecimal)this.orderValidityTimeSpinner.getValue()).toPlainString();
/*     */     try
/*     */     {
/* 297 */       Float.valueOf(defaultAmount);
/* 298 */       Float.valueOf(defaultXAUAmount);
/* 299 */       Float.valueOf(defaultXAGAmount);
/* 300 */       Float.valueOf(defaultSlippage);
/* 301 */       Float.valueOf(defaultOpenIfOffset);
/* 302 */       Float.valueOf(defaultStopLossOffset);
/* 303 */       Float.valueOf(defaultTakeProfitOffset);
/* 304 */       Float.valueOf(defaultTrailingStep);
/* 305 */       Float.valueOf(orderValidityTime);
/*     */     } catch (NumberFormatException e1) {
/* 307 */       return false;
/*     */     }
/*     */ 
/* 310 */     if (GreedContext.isContest()) {
/* 311 */       if (!fineTPandSLvalues(defaultStopLossOffset)) {
/* 312 */         JOptionPane.showMessageDialog(this, LocalizationManager.getText("validation.contest.min.sl"));
/* 313 */         return false;
/*     */       }
/*     */ 
/* 316 */       if (!fineTPandSLvalues(defaultTakeProfitOffset)) {
/* 317 */         JOptionPane.showConfirmDialog(this, LocalizationManager.getText("validation.contest.min.tp"));
/*     */ 
/* 321 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 325 */     return true;
/*     */   }
/*     */ 
/*     */   public void storeEntryOrderDefaults() {
/* 329 */     if ((!this.amountSpinner.validateEditor()) || (!this.amountSpinnerXAU.validateEditor()) || (!this.amountSpinnerXAG.validateEditor()) || (!this.defaultSlippageSpinner.validateEditor()) || (!this.defaultOpenIfPipOffsetSpinner.validateEditor()) || (!this.defaultSlPipOffsetSpinner.validateEditor()) || (!this.defaultTpPipOffsetSpinner.validateEditor()) || (!this.defaultTrailingStepSpinner.validateEditor()) || (!this.orderValidityTimeSpinner.validateEditor()))
/*     */     {
/* 338 */       return;
/*     */     }
/* 340 */     String defaultAmount = ((BigDecimal)this.amountSpinner.getValue()).toPlainString();
/* 341 */     String defaultXAUAmount = ((BigDecimal)this.amountSpinnerXAU.getValue()).toPlainString();
/* 342 */     String defaultXAGAmount = ((BigDecimal)this.amountSpinnerXAG.getValue()).toPlainString();
/* 343 */     String defaultSlippage = ((BigDecimal)this.defaultSlippageSpinner.getValue()).toPlainString();
/* 344 */     String defaultOpenIfOffset = ((BigDecimal)this.defaultOpenIfPipOffsetSpinner.getValue()).toPlainString();
/* 345 */     String defaultStopLossOffset = ((BigDecimal)this.defaultSlPipOffsetSpinner.getValue()).toPlainString();
/* 346 */     String defaultTakeProfitOffset = ((BigDecimal)this.defaultTpPipOffsetSpinner.getValue()).toPlainString();
/* 347 */     String defaultTrailingStep = ((BigDecimal)this.defaultTrailingStepSpinner.getValue()).toPlainString();
/* 348 */     String orderValidityTime = ((BigDecimal)this.orderValidityTimeSpinner.getValue()).toPlainString();
/*     */     try
/*     */     {
/* 351 */       Float.valueOf(defaultAmount);
/* 352 */       Float.valueOf(defaultXAUAmount);
/* 353 */       Float.valueOf(defaultXAGAmount);
/* 354 */       Float.valueOf(defaultSlippage);
/* 355 */       Float.valueOf(defaultOpenIfOffset);
/* 356 */       Float.valueOf(defaultStopLossOffset);
/* 357 */       Float.valueOf(defaultTakeProfitOffset);
/* 358 */       Float.valueOf(defaultTrailingStep);
/* 359 */       Float.valueOf(orderValidityTime);
/*     */     } catch (NumberFormatException e1) {
/* 361 */       return;
/*     */     }
/* 363 */     this.settings.saveDefaultAmount(Float.valueOf(defaultAmount));
/* 364 */     this.settings.saveDefaultXAUAmount(Float.valueOf(defaultXAUAmount));
/* 365 */     this.settings.saveDefaultXAGAmount(Float.valueOf(defaultXAGAmount));
/* 366 */     this.settings.saveDefaultSlippage(Float.valueOf(defaultSlippage));
/* 367 */     this.settings.saveDefaultOpenIfOffset(Float.valueOf(defaultOpenIfOffset));
/* 368 */     this.settings.saveDefaultStopLossOffset(Float.valueOf(defaultStopLossOffset));
/* 369 */     this.settings.saveDefaultTakeProfitOffset(Float.valueOf(defaultTakeProfitOffset));
/* 370 */     this.settings.saveDefaultTrailingStep(Float.valueOf(defaultTrailingStep));
/* 371 */     this.settings.saveOrderValidityTime(Float.valueOf(orderValidityTime));
/* 372 */     this.settings.saveOredrValidityTimeUnit(this.comboBox.getSelectedIndex());
/*     */ 
/* 377 */     updateGuiWithDefaults(defaultAmount, defaultSlippage);
/*     */   }
/*     */ 
/*     */   private void updateGuiWithDefaults(String defaultAmount, String defaultSlippage)
/*     */   {
/* 382 */     GreedContext.setConfig("backend.settings.updated", "false");
/* 383 */     Map settings = new HashMap();
/*     */ 
/* 385 */     settings.put("amount", defaultAmount);
/* 386 */     settings.put("slippageVal", defaultSlippage);
/*     */ 
/* 390 */     UpdateGuiDefaultsAction updateGuiDefaultsAction = new UpdateGuiDefaultsAction(this, settings);
/* 391 */     GreedContext.publishEvent(updateGuiDefaultsAction);
/*     */   }
/*     */ 
/*     */   public void resetFields()
/*     */   {
/* 407 */     this.amountSpinner.setValue(this.settings.restoreDefaultAmount());
/* 408 */     this.amountSpinnerXAU.setValue(this.settings.restoreDefaultXAUAmount());
/* 409 */     this.amountSpinnerXAG.setValue(this.settings.restoreDefaultXAGAmount());
/* 410 */     this.defaultSlippageSpinner.setValue(this.settings.restoreDefaultSlippage());
/* 411 */     this.defaultOpenIfPipOffsetSpinner.setValue(this.settings.restoreDefaultOpenIfOffset());
/* 412 */     this.defaultSlPipOffsetSpinner.setValue(this.settings.restoreDefaultStopLossOffset());
/* 413 */     this.defaultTpPipOffsetSpinner.setValue(this.settings.restoreDefaultTakeProfitOffset());
/* 414 */     this.defaultTrailingStepSpinner.setValue(this.settings.restoreDefaultTrailingStep());
/* 415 */     this.orderValidityTimeSpinner.setValue(this.settings.restoreOrderValidityTime());
/* 416 */     this.comboBox.setSelectedIndex(this.settings.restoreOrderValidityTimeUnit());
/*     */   }
/*     */ 
/*     */   public AmountJSpinner getDefaultAmountField() {
/* 420 */     return (AmountJSpinner)this.amountSpinner;
/*     */   }
/*     */ 
/*     */   public LotAmountLabel getDefaultAmountLabel() {
/* 424 */     return this.amountLabel;
/*     */   }
/*     */ 
/*     */   public void resetDefaults() {
/* 428 */     this.amountSpinner.setValue(BigDecimal.valueOf(ClientSettingsStorage.DEFAULT_AMOUNT_VALUE));
/* 429 */     this.amountSpinnerXAU.setValue(BigDecimal.valueOf(ClientSettingsStorage.DEFAULT_XAU_AMOUNT_VALUE));
/* 430 */     this.amountSpinnerXAG.setValue(BigDecimal.valueOf(ClientSettingsStorage.DEFAULT_XAG_AMOUNT_VALUE));
/* 431 */     this.defaultSlippageSpinner.setValue(BigDecimal.valueOf(ClientSettingsStorage.DEFAULT_SLIPPAGE_VALUE));
/* 432 */     this.defaultOpenIfPipOffsetSpinner.setValue(BigDecimal.valueOf(ClientSettingsStorage.DEFAULT_OPEN_IF_OFFSET_VALUE));
/* 433 */     this.defaultSlPipOffsetSpinner.setValue(BigDecimal.valueOf(ClientSettingsStorage.DEFAULT_STOP_LOSS_OFFSET_VALUE));
/* 434 */     this.defaultTpPipOffsetSpinner.setValue(BigDecimal.valueOf(ClientSettingsStorage.DEFAULT_TAKE_PROFIT_OFFSET_VALUE));
/* 435 */     this.defaultTrailingStepSpinner.setValue(BigDecimal.valueOf(ClientSettingsStorage.DEFAULT_TRAILING_STEP_VALUE));
/* 436 */     this.orderValidityTimeSpinner.setValue(BigDecimal.valueOf(ClientSettingsStorage.DEFAULT_VALIDATY_TIME_UNIT_VALUE));
/* 437 */     this.comboBox.setSelectedIndex(ClientSettingsStorage.DEFAULT_VALIDATY_TIME_UNIT_VALUE);
/*     */   }
/*     */ 
/*     */   private boolean fineTPandSLvalues(String value) {
/* 441 */     if (value == null) return false;
/* 442 */     Float floatValue = Float.valueOf(value);
/* 443 */     return floatValue.floatValue() >= TEN.intValue();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.settings.general.DefaultValuesPanel
 * JD-Core Version:    0.6.0
 */