/*    */ package com.dukascopy.dds2.greed.gui.component.dowjones.calendar;
/*    */ 
/*    */ import com.dukascopy.api.CalendarFilter;
/*    */ import com.toedter.calendar.IDateEditor;
/*    */ import com.toedter.calendar.JDateChooser;
/*    */ import java.beans.PropertyChangeEvent;
/*    */ import java.beans.PropertyChangeListener;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Date;
/*    */ import java.util.TimeZone;
/*    */ 
/*    */ class FromDateChooser extends JDateChooser
/*    */ {
/*    */   private static final String DATE_FORMAT = "dd.MM.yyyy";
/*    */   private static final String MINIMAL_DATE = "27.07.2009";
/*    */   private CalendarFilter calendarFilter;
/*    */ 
/*    */   public FromDateChooser(CalendarFilter calendarFilter)
/*    */   {
/* 25 */     super(calendarFilter.getFrom(), "dd.MM.yyyy");
/*    */ 
/* 27 */     refresh(calendarFilter);
/*    */     try
/*    */     {
/* 30 */       setMinSelectableDate(new SimpleDateFormat("dd.MM.yyyy") {  }
/* 30 */       .parse("27.07.2009"));
/*    */     } catch (Exception ex) {
/*    */     }
/* 33 */     IDateEditor editor = getDateEditor();
/* 34 */     editor.addPropertyChangeListener(new PropertyChangeListener(editor)
/*    */     {
/*    */       public void propertyChange(PropertyChangeEvent evt) {
/* 37 */         FromDateChooser.this.update(this.val$editor.getDate());
/*    */       } } );
/*    */   }
/*    */ 
/*    */   public void refresh(CalendarFilter calendarFilter) {
/* 43 */     this.calendarFilter = calendarFilter;
/* 44 */     setDate(calendarFilter.getFrom());
/*    */   }
/*    */ 
/*    */   private void update(Date date) {
/* 48 */     if (date != null)
/* 49 */       this.calendarFilter.setFrom(date);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.calendar.FromDateChooser
 * JD-Core Version:    0.6.0
 */