/*    */ package com.dukascopy.dds2.greed.gui.component.settings.theme;
/*    */ 
/*    */ import com.dukascopy.charts.persistence.ITheme;
/*    */ import com.dukascopy.charts.persistence.ThemeManager;
/*    */ import com.dukascopy.charts.theme.CommonThemeSettingsPanel;
/*    */ import com.dukascopy.charts.theme.CommonThemeSettingsPanel.ThemesPanel;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.component.settings.ISettingsPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.settings.SettingsTabbedFrame;
/*    */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*    */ 
/*    */ public class ThemeSettingsPanel extends CommonThemeSettingsPanel
/*    */   implements ISettingsPanel
/*    */ {
/*    */   protected final SettingsTabbedFrame parent;
/*    */ 
/*    */   public ThemeSettingsPanel(SettingsTabbedFrame parent)
/*    */   {
/* 20 */     super(-1, ThemeManager.getTheme());
/* 21 */     this.parent = parent;
/*    */   }
/*    */ 
/*    */   public void applySettings()
/*    */   {
/* 27 */     if (this.theme == null) {
/* 28 */       return;
/*    */     }
/*    */ 
/* 31 */     String themeName = this.themesPanel.getSelectedThemeName();
/*    */ 
/* 33 */     if (ThemeManager.isDefault(themeName)) {
/* 34 */       ThemeManager.setTheme(themeName);
/*    */     } else {
/* 36 */       ITheme clone = this.theme.clone();
/* 37 */       clone.setName(themeName);
/* 38 */       ThemeManager.setTheme(clone);
/*    */     }
/* 40 */     ((ClientSettingsStorage)GreedContext.get("settingsStorage")).saveSelectedTheme(this.theme);
/*    */   }
/*    */ 
/*    */   public void resetFields()
/*    */   {
/* 45 */     this.theme = ThemeManager.getTheme().clone();
/* 46 */     this.themesPanel.setTheme(this.theme);
/*    */ 
/* 48 */     repaintComponents();
/*    */   }
/*    */ 
/*    */   protected void settingsChanged(boolean enableSave)
/*    */   {
/* 53 */     this.parent.settingsChanged(enableSave);
/* 54 */     super.settingsChanged(enableSave);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.settings.theme.ThemeSettingsPanel
 * JD-Core Version:    0.6.0
 */