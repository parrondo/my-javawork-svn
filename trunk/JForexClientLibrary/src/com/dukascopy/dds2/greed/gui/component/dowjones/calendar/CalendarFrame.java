/*    */ package com.dukascopy.dds2.greed.gui.component.dowjones.calendar;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.BasicDecoratedFrame;
/*    */ import com.dukascopy.dds2.greed.gui.component.HeaderPanel;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableHeaderPanel;
/*    */ import java.awt.Container;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.BoxLayout;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class CalendarFrame extends BasicDecoratedFrame
/*    */ {
/* 21 */   private static final Dimension SIZE = new Dimension(900, 400);
/*    */ 
/* 23 */   private static CalendarFrame instance = null;
/*    */   private final ActionListener actionListener;
/*    */   private final DowJonesCalendarPanel calendarPanel;
/*    */ 
/*    */   public static CalendarFrame getInstance(ActionListener actionListener)
/*    */   {
/* 28 */     if ((instance == null) || (!instance.isDisplayable()))
/* 29 */       instance = new CalendarFrame(actionListener);
/*    */     else {
/* 31 */       instance.setState(0);
/*    */     }
/*    */ 
/* 34 */     return instance;
/*    */   }
/*    */ 
/*    */   public static CalendarFrame getInstance() {
/* 38 */     return instance;
/*    */   }
/*    */ 
/*    */   private CalendarFrame(ActionListener actionListener) {
/* 42 */     this.actionListener = actionListener;
/* 43 */     setTitle("tab.dowjones.calendar");
/*    */ 
/* 45 */     JPanel content = new JPanel();
/* 46 */     content.setLayout(new BoxLayout(content, 1));
/*    */ 
/* 48 */     this.calendarPanel = new DowJonesCalendarPanel();
/* 49 */     HeaderPanel header = new JLocalizableHeaderPanel("tab.dowjones.calendar", false);
/*    */ 
/* 51 */     content.add(header);
/* 52 */     content.add(this.calendarPanel);
/* 53 */     getContentPane().add(content);
/*    */ 
/* 55 */     setSize(SIZE);
/* 56 */     setMinimumSize(SIZE);
/*    */ 
/* 58 */     setVisible(true);
/* 59 */     setDefaultCloseOperation(2);
/*    */   }
/*    */ 
/*    */   public DowJonesCalendarPanel getCalendarPanel() {
/* 63 */     return this.calendarPanel;
/*    */   }
/*    */ 
/*    */   public void dispose()
/*    */   {
/* 68 */     super.dispose();
/* 69 */     this.calendarPanel.unsubscribe();
/* 70 */     this.actionListener.actionPerformed(null);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.calendar.CalendarFrame
 * JD-Core Version:    0.6.0
 */