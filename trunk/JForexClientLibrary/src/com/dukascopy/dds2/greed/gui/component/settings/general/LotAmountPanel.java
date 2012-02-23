/*     */ package com.dukascopy.dds2.greed.gui.component.settings.general;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.component.settings.SettingsTabbedFrame;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRadioButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger.AmountLot;
/*     */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountLabel;
/*     */ import com.dukascopy.dds2.greed.gui.util.spinners.AmountJSpinner;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.math.BigDecimal;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JRadioButton;
/*     */ 
/*     */ public class LotAmountPanel extends JPanel
/*     */   implements ActionListener
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private SettingsTabbedFrame parent;
/*  29 */   private JRadioButton millionsButton = new JLocalizableRadioButton("radio.lot.millions");
/*  30 */   private JRadioButton thousandButton = new JLocalizableRadioButton("radio.lot.thousands");
/*  31 */   private JRadioButton unitsButton = new JLocalizableRadioButton("radio.lot.units");
/*     */ 
/*  33 */   private ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*     */ 
/*     */   public LotAmountPanel(SettingsTabbedFrame parent) {
/*  36 */     this.parent = parent;
/*  37 */     build();
/*     */   }
/*     */ 
/*     */   private void build()
/*     */   {
/*  42 */     setBorder(new JLocalizableRoundedBorder(this, "border.lot.amount"));
/*  43 */     setLayout(new BoxLayout(this, 1));
/*     */ 
/*  45 */     this.millionsButton.setSelected(isMillionsLot());
/*  46 */     this.thousandButton.setSelected(isThousandsLot());
/*  47 */     this.unitsButton.setSelected(isUnitLot());
/*     */ 
/*  49 */     ButtonGroup group = new ButtonGroup();
/*  50 */     group.add(this.millionsButton);
/*  51 */     group.add(this.thousandButton);
/*  52 */     group.add(this.unitsButton);
/*     */ 
/*  54 */     add(this.millionsButton, Float.valueOf(0.0F));
/*  55 */     add(this.thousandButton, Float.valueOf(0.0F));
/*  56 */     add(this.unitsButton, Float.valueOf(0.0F));
/*     */ 
/*  58 */     addChangeListeners();
/*     */   }
/*     */ 
/*     */   public void setEnabled(boolean enabled)
/*     */   {
/*  64 */     super.setEnabled(enabled);
/*  65 */     this.millionsButton.setEnabled(enabled);
/*  66 */     this.thousandButton.setEnabled(enabled);
/*  67 */     this.unitsButton.setEnabled(enabled);
/*     */   }
/*     */ 
/*     */   public void doSelectUnits() {
/*  71 */     this.unitsButton.setSelected(true);
/*     */   }
/*     */ 
/*     */   public void resetFields() {
/*  75 */     this.millionsButton.setSelected(isMillionsLot());
/*  76 */     this.thousandButton.setSelected(isThousandsLot());
/*  77 */     this.unitsButton.setSelected(isUnitLot());
/*  78 */     doChangeLot();
/*     */   }
/*     */ 
/*     */   public void resetDefaults() {
/*  82 */     this.millionsButton.setSelected(isDefaultMillionsLot());
/*  83 */     this.thousandButton.setSelected(isDefaultThousandsLot());
/*  84 */     this.unitsButton.setSelected(isDefaultUnitLot());
/*  85 */     doChangeLot();
/*     */   }
/*     */ 
/*     */   private void addChangeListeners() {
/*  89 */     this.thousandButton.addActionListener(this);
/*  90 */     this.millionsButton.addActionListener(this);
/*  91 */     this.unitsButton.addActionListener(this);
/*     */   }
/*     */ 
/*     */   public void saveSettings() {
/*  95 */     this.storage.saveLotAmount(getSelectedLot().value().intValue());
/*     */   }
/*     */ 
/*     */   private LotAmountChanger.AmountLot getSelectedLot() {
/*  99 */     if (this.millionsButton.isSelected()) return LotAmountChanger.AmountLot.MILLIONS;
/* 100 */     if (this.thousandButton.isSelected()) return LotAmountChanger.AmountLot.THOUSANDS;
/* 101 */     if (this.unitsButton.isSelected()) return LotAmountChanger.AmountLot.UNITS;
/* 102 */     return LotAmountChanger.AmountLot.MILLIONS;
/*     */   }
/*     */ 
/*     */   private boolean isMillionsLot() {
/* 106 */     return LotAmountChanger.AmountLot.MILLIONS.value().equals(this.storage.restoreAmountLot());
/*     */   }
/*     */ 
/*     */   private boolean isThousandsLot() {
/* 110 */     return LotAmountChanger.AmountLot.THOUSANDS.value().equals(this.storage.restoreAmountLot());
/*     */   }
/*     */ 
/*     */   private boolean isUnitLot() {
/* 114 */     return LotAmountChanger.AmountLot.UNITS.value().equals(this.storage.restoreAmountLot());
/*     */   }
/*     */ 
/*     */   private boolean isDefaultMillionsLot() {
/* 118 */     return LotAmountChanger.AmountLot.MILLIONS.value().equals(this.storage.restoreDefaultAmountLot());
/*     */   }
/*     */ 
/*     */   private boolean isDefaultThousandsLot() {
/* 122 */     return LotAmountChanger.AmountLot.THOUSANDS.value().equals(this.storage.restoreDefaultAmountLot());
/*     */   }
/*     */ 
/*     */   private boolean isDefaultUnitLot() {
/* 126 */     return LotAmountChanger.AmountLot.UNITS.value().equals(this.storage.restoreDefaultAmountLot());
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e)
/*     */   {
/* 131 */     this.parent.settingsChanged(true);
/* 132 */     doChangeLot();
/*     */   }
/*     */ 
/*     */   private void doChangeLot() {
/* 136 */     GeneralPanel gp = this.parent.getGeneralPanel();
/* 137 */     gp.getDefaultValuesPanel().getDefaultAmountField().changeLot(BigDecimal.valueOf(getSelectedLot().value().intValue()));
/* 138 */     gp.getDefaultValuesPanel().getDefaultAmountLabel().changeLot(BigDecimal.valueOf(getSelectedLot().value().intValue()));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.settings.general.LotAmountPanel
 * JD-Core Version:    0.6.0
 */