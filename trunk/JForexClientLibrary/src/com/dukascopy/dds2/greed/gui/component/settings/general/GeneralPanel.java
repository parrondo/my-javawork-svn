/*    */ package com.dukascopy.dds2.greed.gui.component.settings.general;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.settings.ISettingsPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.settings.SettingsTabbedFrame;
/*    */ import java.awt.GridLayout;
/*    */ import javax.swing.BoxLayout;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class GeneralPanel extends JPanel
/*    */   implements ISettingsPanel
/*    */ {
/*    */   private final OtherGeneralsPanel otherSettingsPanel;
/*    */   private final DefaultValuesPanel defaultValuesPanel;
/*    */   private final LotAmountPanel amountLotPanel;
/*    */ 
/*    */   public GeneralPanel(SettingsTabbedFrame parent)
/*    */   {
/* 19 */     this.otherSettingsPanel = new OtherGeneralsPanel(parent);
/* 20 */     this.defaultValuesPanel = new DefaultValuesPanel(parent);
/* 21 */     this.amountLotPanel = new LotAmountPanel(parent);
/* 22 */     initPanels();
/*    */   }
/*    */ 
/*    */   private void initPanels() {
/* 26 */     setLayout(new GridLayout(1, 2));
/*    */ 
/* 28 */     JPanel leftPanel = new JPanel();
/* 29 */     leftPanel.setLayout(new BoxLayout(leftPanel, 1));
/*    */ 
/* 31 */     JPanel innerPanel = new JPanel();
/* 32 */     innerPanel.setLayout(new GridLayout(1, 1));
/* 33 */     innerPanel.add(this.amountLotPanel);
/*    */ 
/* 35 */     leftPanel.add(this.defaultValuesPanel);
/* 36 */     leftPanel.add(innerPanel);
/*    */ 
/* 38 */     add(leftPanel);
/* 39 */     add(this.otherSettingsPanel);
/*    */   }
/*    */ 
/*    */   public void resetFields()
/*    */   {
/* 44 */     this.amountLotPanel.resetFields();
/* 45 */     this.defaultValuesPanel.resetFields();
/* 46 */     this.otherSettingsPanel.resetFields();
/*    */   }
/*    */ 
/*    */   public boolean verifySettings()
/*    */   {
/* 51 */     return this.defaultValuesPanel.verifySettings();
/*    */   }
/*    */ 
/*    */   public void applySettings()
/*    */   {
/* 56 */     this.amountLotPanel.saveSettings();
/* 57 */     this.defaultValuesPanel.storeEntryOrderDefaults();
/* 58 */     this.otherSettingsPanel.saveSettings();
/*    */   }
/*    */ 
/*    */   public void resetToDefaults()
/*    */   {
/* 63 */     this.amountLotPanel.resetDefaults();
/* 64 */     this.defaultValuesPanel.resetDefaults();
/* 65 */     this.otherSettingsPanel.resetDefaults();
/*    */   }
/*    */ 
/*    */   public OtherGeneralsPanel getOtherSettingsPanel()
/*    */   {
/* 70 */     return this.otherSettingsPanel;
/*    */   }
/*    */ 
/*    */   public DefaultValuesPanel getDefaultValuesPanel() {
/* 74 */     return this.defaultValuesPanel;
/*    */   }
/*    */ 
/*    */   public LotAmountPanel getAmountLotPanel() {
/* 78 */     return this.amountLotPanel;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.settings.general.GeneralPanel
 * JD-Core Version:    0.6.0
 */