/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*    */ import com.dukascopy.dds2.greed.model.Notification;
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*    */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*    */ import com.dukascopy.transport.common.msg.response.OkResponseMessage;
/*    */ import java.util.Map;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class AccountSettingsSendAction extends AppActionEvent
/*    */ {
/* 24 */   private static final Logger LOGGER = LoggerFactory.getLogger(AccountSettingsSendAction.class);
/*    */   private Map<String, String> _settings;
/*    */   private ProtocolMessage _submitResult;
/*    */ 
/*    */   public AccountSettingsSendAction(Object source, Map<String, String> settings)
/*    */   {
/* 34 */     super(source, false, true);
/* 35 */     this._settings = settings;
/*    */   }
/*    */ 
/*    */   public void doAction()
/*    */   {
/* 43 */     GreedTransportClient client = (GreedTransportClient)GreedContext.get("transportClient");
/* 44 */     AccountInfoMessage am = new AccountInfoMessage();
/* 45 */     this._settings.remove("DEFAULT_AMOUNT");
/* 46 */     this._settings.remove("DEFAULT_SLIPPAGE");
/* 47 */     this._settings.remove("DEFAULT_AMOUNT".toLowerCase());
/* 48 */     this._settings.remove("DEFAULT_SLIPPAGE".toLowerCase());
/*    */ 
/* 51 */     am.setClientSettings(this._settings);
/* 52 */     LOGGER.debug(am.toString());
/* 53 */     this._submitResult = client.controlRequest(am);
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter()
/*    */   {
/* 61 */     if ((this._submitResult instanceof OkResponseMessage)) {
/* 62 */       Notification notification = new Notification("Settings saved.");
/* 63 */       PostMessageAction action = new PostMessageAction(this, notification);
/* 64 */       GreedContext.publishEvent(action);
/* 65 */     } else if ((this._submitResult instanceof ErrorResponseMessage)) {
/* 66 */       ErrorResponseMessage error = (ErrorResponseMessage)this._submitResult;
/* 67 */       Notification notification = new Notification("Settings have not been saved (" + error.getReason() + ").");
/* 68 */       PostMessageAction post = new PostMessageAction(this, notification);
/* 69 */       GreedContext.publishEvent(post);
/*    */     } else {
/* 71 */       LOGGER.debug(" what kind of response is that? = " + this._submitResult);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.AccountSettingsSendAction
 * JD-Core Version:    0.6.0
 */