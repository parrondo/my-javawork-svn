/*    */ package com.dukascopy.dds2.greed.actions.dowjones;
/*    */ 
/*    */ import com.dukascopy.api.ICalendarMessage;
/*    */ import com.dukascopy.api.INewsMessage.Action;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.actions.AppActionEvent;
/*    */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*    */ import com.dukascopy.dds2.greed.gui.component.dowjones.calendar.CalendarFrame;
/*    */ import com.dukascopy.dds2.greed.gui.component.dowjones.calendar.DowJonesCalendarPanel;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*    */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*    */ 
/*    */ public class CalendarActionEvent extends AppActionEvent
/*    */ {
/*    */   private final ICalendarMessage calendarMessage;
/*    */ 
/*    */   public CalendarActionEvent(Object source, ICalendarMessage calendarMessage)
/*    */   {
/* 23 */     super(source, false, true);
/* 24 */     this.calendarMessage = calendarMessage;
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter()
/*    */   {
/* 29 */     DowJonesCalendarPanel calendarPanel = null;
/*    */ 
/* 31 */     if (GreedContext.isStrategyAllowed()) {
/* 32 */       ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/* 33 */       calendarPanel = clientForm.getCalendarPanel();
/*    */     } else {
/* 35 */       CalendarFrame calendarFrame = CalendarFrame.getInstance();
/* 36 */       if (calendarFrame != null) {
/* 37 */         calendarPanel = calendarFrame.getCalendarPanel();
/*    */       }
/*    */     }
/*    */ 
/* 41 */     if (calendarPanel == null) {
/* 42 */       return;
/*    */     }
/*    */ 
/* 45 */     if (this.calendarMessage != null) {
/* 46 */       INewsMessage.Action action = this.calendarMessage.getAction();
/*    */ 
/* 49 */       if (action == null) {
/* 50 */         calendarPanel.stopLoadingProgress();
/* 51 */         return;
/*    */       }
/*    */ 
/* 54 */       switch (1.$SwitchMap$com$dukascopy$api$INewsMessage$Action[this.calendarMessage.getAction().ordinal()]) {
/*    */       case 1:
/* 56 */         calendarPanel.insert(this.calendarMessage);
/* 57 */         break;
/*    */       case 2:
/* 59 */         calendarPanel.delete(this.calendarMessage.getId());
/*    */       }
/*    */     }
/*    */     else {
/* 63 */       NotificationUtilsProvider.getNotificationUtils().postWarningMessage(LocalizationManager.getText("warning.news.request.limit.exceeded"), true);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.dowjones.CalendarActionEvent
 * JD-Core Version:    0.6.0
 */