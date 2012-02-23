/*    */ package com.dukascopy.dds2.greed.actions.dowjones;
/*    */ 
/*    */ import com.dukascopy.api.INewsMessage;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.actions.AppActionEvent;
/*    */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*    */ import com.dukascopy.dds2.greed.gui.component.dowjones.news.DowJonesNewsPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.dowjones.news.NewsFrame;
/*    */ 
/*    */ public class NewsActionEvent extends AppActionEvent
/*    */ {
/*    */   private final INewsMessage newsMessage;
/*    */ 
/*    */   public NewsActionEvent(Object source, INewsMessage newsMessage)
/*    */   {
/* 19 */     super(source, false, true);
/* 20 */     this.newsMessage = newsMessage;
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter()
/*    */   {
/* 25 */     DowJonesNewsPanel newsPanel = null;
/*    */ 
/* 27 */     if (GreedContext.isStrategyAllowed()) {
/* 28 */       ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/* 29 */       newsPanel = clientForm.getNewsPanel();
/*    */     } else {
/* 31 */       NewsFrame newsFrame = NewsFrame.getInstance();
/* 32 */       if (newsFrame != null) {
/* 33 */         newsPanel = newsFrame.getNewsPanel();
/*    */       }
/*    */     }
/*    */ 
/* 37 */     if (newsPanel != null)
/* 38 */       newsPanel.add(this.newsMessage);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.dowjones.NewsActionEvent
 * JD-Core Version:    0.6.0
 */