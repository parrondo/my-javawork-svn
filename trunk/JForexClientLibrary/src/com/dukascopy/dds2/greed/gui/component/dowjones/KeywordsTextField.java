/*    */ package com.dukascopy.dds2.greed.gui.component.dowjones;
/*    */ 
/*    */ import com.dukascopy.api.NewsFilter;
/*    */ import com.dukascopy.api.NewsFilter.TimeFrame;
/*    */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*    */ import java.util.Set;
/*    */ import javax.swing.JTextField;
/*    */ import javax.swing.event.DocumentEvent;
/*    */ import javax.swing.event.DocumentListener;
/*    */ import javax.swing.text.Document;
/*    */ 
/*    */ public class KeywordsTextField<NewsFilterClass extends NewsFilter> extends JTextField
/*    */ {
/*    */   private static final long serialVersionUID = -8876242773917191913L;
/*    */   private NewsFilterClass filter;
/*    */ 
/*    */   public KeywordsTextField(NewsFilterClass filter)
/*    */   {
/* 25 */     refresh(filter);
/*    */ 
/* 27 */     getDocument().addDocumentListener(new DocumentListener()
/*    */     {
/*    */       public void removeUpdate(DocumentEvent e) {
/* 30 */         KeywordsTextField.this.update();
/*    */       }
/*    */ 
/*    */       public void insertUpdate(DocumentEvent e)
/*    */       {
/* 35 */         KeywordsTextField.this.update();
/*    */       }
/*    */ 
/*    */       public void changedUpdate(DocumentEvent e)
/*    */       {
/* 40 */         KeywordsTextField.this.update();
/*    */       }
/*    */     });
/* 43 */     checkTimeFrame();
/*    */   }
/*    */ 
/*    */   public void refresh(NewsFilterClass filter) {
/* 47 */     this.filter = filter;
/* 48 */     StringBuilder text = new StringBuilder();
/* 49 */     for (String keyword : filter.getKeywords()) {
/* 50 */       text.append(keyword).append(" ");
/*    */     }
/* 52 */     setText(text.toString());
/*    */   }
/*    */ 
/*    */   private void update() {
/* 56 */     Set keywords = this.filter.getKeywords();
/* 57 */     keywords.clear();
/*    */ 
/* 59 */     String text = getText();
/* 60 */     if (!ObjectUtils.isNullOrEmpty(text))
/* 61 */       for (String keyword : text.split(" "))
/* 62 */         keywords.add(keyword);
/*    */   }
/*    */ 
/*    */   public void checkTimeFrame()
/*    */   {
/* 68 */     setEnabled(NewsFilter.TimeFrame.ONLINE != this.filter.getTimeFrame());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.KeywordsTextField
 * JD-Core Version:    0.6.0
 */