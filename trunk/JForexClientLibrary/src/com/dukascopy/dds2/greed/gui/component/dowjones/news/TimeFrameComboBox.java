/*    */ package com.dukascopy.dds2.greed.gui.component.dowjones.news;
/*    */ 
/*    */ import com.dukascopy.api.NewsFilter;
/*    */ import com.dukascopy.api.NewsFilter.TimeFrame;
/*    */ import java.awt.event.ActionEvent;
/*    */ import javax.swing.AbstractAction;
/*    */ import javax.swing.JComboBox;
/*    */ 
/*    */ class TimeFrameComboBox extends JComboBox
/*    */ {
/*    */   private NewsFilter newsFilter;
/*    */ 
/*    */   public TimeFrameComboBox(NewsFilter newsFilter, INewsFilterChangeListener newsFilterChangeListener)
/*    */   {
/* 19 */     super(NewsFilter.TimeFrame.values());
/*    */ 
/* 21 */     refresh(newsFilter);
/*    */ 
/* 23 */     setAction(new AbstractAction(newsFilterChangeListener)
/*    */     {
/*    */       public void actionPerformed(ActionEvent e) {
/* 26 */         TimeFrameComboBox.this.update();
/* 27 */         this.val$newsFilterChangeListener.newsFilterChanged();
/*    */       } } );
/*    */   }
/*    */ 
/*    */   public void refresh(NewsFilter newsFilter) {
/* 33 */     this.newsFilter = newsFilter;
/* 34 */     setSelectedItem(newsFilter.getTimeFrame());
/*    */   }
/*    */ 
/*    */   private void update() {
/* 38 */     this.newsFilter.setTimeFrame((NewsFilter.TimeFrame)getSelectedItem());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.news.TimeFrameComboBox
 * JD-Core Version:    0.6.0
 */