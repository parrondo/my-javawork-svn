/*    */ package com.dukascopy.dds2.greed.gui.component.dowjones.calendar;
/*    */ 
/*    */ import com.dukascopy.api.CalendarFilter;
/*    */ import com.dukascopy.api.INewsFilter.Type;
/*    */ import java.awt.event.ActionEvent;
/*    */ import javax.swing.AbstractAction;
/*    */ import javax.swing.JComboBox;
/*    */ 
/*    */ public class CalendarTypeComboBox extends JComboBox
/*    */ {
/*    */   private CalendarFilter calendarFilter;
/*    */ 
/*    */   public CalendarTypeComboBox(CalendarFilter calendarFilter)
/*    */   {
/* 21 */     addItem(null);
/* 22 */     for (INewsFilter.Type type : INewsFilter.Type.values()) {
/* 23 */       addItem(type);
/*    */     }
/*    */ 
/* 26 */     refresh(calendarFilter);
/*    */ 
/* 28 */     setAction(new AbstractAction()
/*    */     {
/*    */       public void actionPerformed(ActionEvent e) {
/* 31 */         CalendarTypeComboBox.this.update();
/*    */       } } );
/*    */   }
/*    */ 
/*    */   public void refresh(CalendarFilter calendarFilter) {
/* 37 */     this.calendarFilter = calendarFilter;
/* 38 */     setSelectedItem(calendarFilter.getType());
/*    */   }
/*    */ 
/*    */   private void update() {
/* 42 */     this.calendarFilter.setType((INewsFilter.Type)getSelectedItem());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.calendar.CalendarTypeComboBox
 * JD-Core Version:    0.6.0
 */