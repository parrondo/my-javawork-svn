/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.net.HttpURLConnection;
/*    */ import java.net.URL;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class SendLogAction extends AppActionEvent
/*    */ {
/* 19 */   private static final Logger LOGGER = LoggerFactory.getLogger(SendLogAction.class);
/*    */   private final String data;
/*    */   private final String request;
/*    */   private final boolean usePost;
/*    */ 
/*    */   public SendLogAction(Object source, String request, String data, boolean usePost)
/*    */   {
/* 26 */     super(source, false, false);
/* 27 */     this.data = data;
/* 28 */     this.usePost = usePost;
/* 29 */     this.request = request;
/*    */   }
/*    */ 
/*    */   public void doAction() {
/*    */     try {
/* 34 */       if (this.usePost)
/* 35 */         doPostLog(this.request, this.data);
/*    */       else
/* 37 */         doGetLog(this.request, this.data);
/*    */     } catch (IOException e) {
/* 39 */       LOGGER.error(e.getMessage(), e);
/*    */     }
/*    */   }
/*    */ 
/*    */   private int doGetLog(String request, String message) throws IOException {
/* 44 */     String req = request + "&data=" + message;
/*    */ 
/* 48 */     URL url = new URL(req);
/* 49 */     HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
/* 50 */     GreedTransportClient tc = (GreedTransportClient)GreedContext.get("transportClient");
/*    */ 
/* 52 */     urlc.setDoInput(true);
/* 53 */     urlc.setDoOutput(false);
/* 54 */     urlc.connect();
/* 55 */     int response = urlc.getResponseCode();
/* 56 */     if (LOGGER.isDebugEnabled()) {
/* 57 */       LOGGER.debug("Trade Log (" + response + ") :" + req);
/*    */     }
/* 59 */     urlc.disconnect();
/* 60 */     return response;
/*    */   }
/*    */ 
/*    */   private int doPostLog(String request, String message) throws IOException {
/* 64 */     URL url = new URL(request);
/*    */ 
/* 66 */     HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
/* 67 */     GreedTransportClient tc = (GreedTransportClient)GreedContext.get("transportClient");
/*    */ 
/* 69 */     urlc.setDoInput(true);
/* 70 */     urlc.setDoOutput(true);
/* 71 */     urlc.setUseCaches(false);
/* 72 */     urlc.setRequestMethod("POST");
/* 73 */     urlc.setRequestProperty("Content-Length", Integer.toString(message.getBytes().length) + 5);
/* 74 */     urlc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
/* 75 */     DataOutputStream outStream = new DataOutputStream(urlc.getOutputStream());
/* 76 */     outStream.writeBytes("data=" + message);
/* 77 */     outStream.flush();
/* 78 */     outStream.close();
/* 79 */     int response = urlc.getResponseCode();
/* 80 */     urlc.disconnect();
/* 81 */     if (LOGGER.isDebugEnabled()) {
/* 82 */       LOGGER.debug("Trade Log (" + response + ") :" + request);
/*    */     }
/* 84 */     return response;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.SendLogAction
 * JD-Core Version:    0.6.0
 */