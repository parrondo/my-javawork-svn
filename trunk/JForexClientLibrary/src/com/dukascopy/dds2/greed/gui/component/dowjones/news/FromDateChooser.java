/*    */ package com.dukascopy.dds2.greed.gui.component.dowjones.news;
/*    */ 
/*    */ import com.dukascopy.api.NewsFilter;
/*    */ import com.dukascopy.api.NewsFilter.TimeFrame;
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
/*    */   private NewsFilter newsFilter;
/*    */ 
/*    */   public FromDateChooser(NewsFilter calendarFilter)
/*    */   {
/* 25 */     super(calendarFilter.getFrom(), "dd.MM.yyyy");
/*    */ 
/* 27 */     refresh(calendarFilter);
/*    */     try
/*    */     {
/* 30 */       setMinSelectableDate(new SimpleDateFormat("dd.MM.yyyy") {  }
/* 30 */       .parse("27.07.2009"));
/* 31 */       setMaxSelectableDate(new Date());
/*    */     } catch (Exception ex) {
/*    */     }
/* 34 */     IDateEditor editor = getDateEditor();
/* 35 */     editor.addPropertyChangeListener(new PropertyChangeListener(editor)
/*    */     {
/*    */       public void propertyChange(PropertyChangeEvent evt) {
/* 38 */         FromDateChooser.this.update(this.val$editor.getDate());
/*    */       } } );
/*    */   }
/*    */ 
/*    */   public void refresh(NewsFilter newsFilter) {
/* 44 */     this.newsFilter = newsFilter;
/* 45 */     checkTimeFrame();
/*    */   }
/*    */ 
/*    */   public void checkTimeFrame() {
/* 49 */     setDate(this.newsFilter.getFrom());
/* 50 */     setEnabled(NewsFilter.TimeFrame.SPECIFIC_DATE == this.newsFilter.getTimeFrame());
/*    */   }
/*    */ 
/*    */   private void update(Date date) {
/* 54 */     if (date != null)
/* 55 */       this.newsFilter.setFrom(date);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.news.FromDateChooser
 * JD-Core Version:    0.6.0
 */