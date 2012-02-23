/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*    */ import com.dukascopy.dds2.greed.gui.component.news.NewsViaHttp;
/*    */ import java.io.IOException;
/*    */ import java.net.MalformedURLException;
/*    */ import java.net.URL;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class GetNewsAction extends AppActionEvent
/*    */ {
/* 18 */   private static final Logger LOGGER = LoggerFactory.getLogger(GetNewsAction.class);
/*    */   private String response;
/*    */   private URL url;
/*    */   private NewsViaHttp httpListener;
/*    */   private String urlString;
/*    */ 
/*    */   public GetNewsAction(Object source, String urlString)
/*    */   {
/* 27 */     super(source, false, true);
/* 28 */     this.urlString = urlString;
/* 29 */     this.httpListener = ((NewsViaHttp)source);
/*    */   }
/*    */ 
/*    */   public void doAction() {
/*    */     try {
/* 34 */       this.url = new URL(this.urlString);
/* 35 */       this.response = GuiUtilsAndConstants.receiveLineViaHttpOrHttps(this.url);
/*    */     } catch (MalformedURLException e) {
/* 37 */       LOGGER.error(e.getMessage(), e);
/*    */     } catch (IOException e) {
/* 39 */       LOGGER.error(e.getMessage(), e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter() {
/* 44 */     this.httpListener.parseNews(this.response);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.GetNewsAction
 * JD-Core Version:    0.6.0
 */