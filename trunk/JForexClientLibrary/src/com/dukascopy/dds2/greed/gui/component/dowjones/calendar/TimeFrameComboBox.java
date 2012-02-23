/*    */ package com.dukascopy.dds2.greed.gui.component.dowjones.calendar;
/*    */ 
/*    */ import com.dukascopy.api.CalendarFilter;
/*    */ import com.dukascopy.api.CalendarFilter.TimeFrame;
/*    */ import java.awt.event.ActionEvent;
/*    */ import javax.swing.AbstractAction;
/*    */ import javax.swing.JComboBox;
/*    */ 
/*    */ class TimeFrameComboBox extends JComboBox
/*    */ {
/*    */   private CalendarFilter calendarFilter;
/*    */ 
/*    */   public TimeFrameComboBox(CalendarFilter calendarFilter)
/*    */   {
/* 19 */     super(CalendarFilter.TimeFrame.values());
/*    */ 
/* 21 */     refresh(calendarFilter);
/*    */ 
/* 23 */     setAction(new AbstractAction()
/*    */     {
/*    */       public void actionPerformed(ActionEvent e) {
/* 26 */         TimeFrameComboBox.this.update();
/*    */       } } );
/*    */   }
/*    */ 
/*    */   public void refresh(CalendarFilter calendarFilter) {
/* 32 */     this.calendarFilter = calendarFilter;
/* 33 */     setSelectedItem(calendarFilter.getTimeFrame());
/*    */   }
/*    */ 
/*    */   private void update() {
/* 37 */     this.calendarFilter.setTimeFrame((CalendarFilter.TimeFrame)getSelectedItem());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.calendar.TimeFrameComboBox
 * JD-Core Version:    0.6.0
 */