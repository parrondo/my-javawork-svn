/*    */ package com.dukascopy.dds2.greed.gui.component.menu;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*    */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.text.MessageFormat;
/*    */ import java.util.Properties;
/*    */ import javax.swing.AbstractAction;
/*    */ import javax.swing.JOptionPane;
/*    */ 
/*    */ class CalendarMenuAction extends AbstractAction
/*    */ {
/*    */   private String calendarKey;
/* 21 */   private Properties properties = (Properties)GreedContext.get("calendars");
/* 22 */   private String baseUrl = (String)this.properties.get("base.calendar.url");
/*    */ 
/*    */   CalendarMenuAction(String calendarKey) {
/* 25 */     super(calendarKey);
/* 26 */     this.calendarKey = calendarKey;
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent e) {
/* 30 */     if (null == this.baseUrl) {
/* 31 */       ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/* 32 */       JOptionPane.showMessageDialog(clientForm, LocalizationManager.getText("joption.pane.calendar.not.available "), GuiUtilsAndConstants.LABEL_SHORT_NAME, -1);
/*    */ 
/* 38 */       return;
/*    */     }
/* 40 */     GuiUtilsAndConstants.openURL(getCalendarUrl());
/*    */   }
/*    */ 
/*    */   private String getCalendarUrl()
/*    */   {
/* 45 */     String firstPart = MessageFormat.format(this.baseUrl, new Object[] { getLanguageForWeb() }).trim();
/* 46 */     return firstPart + ((String)this.properties.get(this.calendarKey.trim())).trim();
/*    */   }
/*    */ 
/*    */   private String getLanguageForWeb() {
/* 50 */     switch (1.$SwitchMap$com$dukascopy$dds2$greed$gui$l10n$LocalizationManager$Language[LocalizationManager.getLanguage().ordinal()]) { case 1:
/* 51 */       return "russian";
/*    */     case 2:
/* 52 */       return "deutsch";
/*    */     case 3:
/* 53 */       return "english";
/*    */     case 4:
/* 54 */       return "french";
/*    */     case 5:
/* 55 */       return "chinese";
/*    */     case 6:
/* 56 */       return "spanish";
/*    */     case 7:
/* 57 */       return "italian"; }
/* 58 */     return "english";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.menu.CalendarMenuAction
 * JD-Core Version:    0.6.0
 */