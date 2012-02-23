/*    */ package com.dukascopy.dds2.greed.gui.component.settings.disclaimer;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.settings.AbstractSettingsPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.settings.SettingsTabbedFrame;
/*    */ import java.awt.Component;
/*    */ import java.awt.GridLayout;
/*    */ 
/*    */ public class DisclaimerSettingsPanel extends AbstractSettingsPanel
/*    */ {
/*    */   public DisclaimerSettingsPanel(SettingsTabbedFrame parent)
/*    */   {
/* 16 */     super(parent);
/*    */   }
/*    */ 
/*    */   protected void build()
/*    */   {
/* 21 */     setLayout(new GridLayout(1, 1));
/* 22 */     add(getDisclaimerOptionsPanel());
/*    */   }
/*    */ 
/*    */   private Component getDisclaimerOptionsPanel() {
/* 26 */     return new DisclaimerOptionsPanel();
/*    */   }
/*    */ 
/*    */   public void applySettings()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void resetToDefaults()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void resetFields()
/*    */   {
/*    */   }
/*    */ 
/*    */   public boolean verifySettings()
/*    */   {
/* 46 */     return true;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.settings.disclaimer.DisclaimerSettingsPanel
 * JD-Core Version:    0.6.0
 */