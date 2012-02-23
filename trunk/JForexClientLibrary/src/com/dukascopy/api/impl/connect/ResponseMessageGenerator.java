/*     */ package com.dukascopy.api.impl.connect;
/*     */ 
/*     */ import com.dukascopy.api.IMessage.Type;
/*     */ import com.dukascopy.api.IOrder.State;
/*     */ import com.dukascopy.transport.common.model.type.NotificationMessageCode;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class ResponseMessageGenerator
/*     */ {
/*  49 */   private static final Logger LOGGER = LoggerFactory.getLogger(ResponseMessageGenerator.class);
/*     */ 
/*  55 */   Map<PlatformOrderImpl.ServerRequest, Set<IMessage.Type>> byServerRequest = new HashMap();
/*  56 */   Map<MessageType, Set<IMessage.Type>> byMessageType = new HashMap();
/*  57 */   Map<IOrder.State, Set<IMessage.Type>> byOrderState = new HashMap();
/*     */ 
/*     */   public ResponseMessageGenerator() {
/*  60 */     this.byServerRequest.put(PlatformOrderImpl.ServerRequest.NONE, asSet(new IMessage.Type[] { IMessage.Type.ORDER_SUBMIT_OK, IMessage.Type.ORDER_CLOSE_OK, IMessage.Type.ORDER_FILL_OK, IMessage.Type.ORDERS_MERGE_OK, IMessage.Type.ORDER_CHANGED_OK }));
/*  61 */     this.byServerRequest.put(PlatformOrderImpl.ServerRequest.SUBMIT, asSet(new IMessage.Type[] { IMessage.Type.ORDER_SUBMIT_OK, IMessage.Type.ORDER_SUBMIT_REJECTED, IMessage.Type.ORDER_FILL_REJECTED, IMessage.Type.ORDER_FILL_OK }));
/*  62 */     this.byServerRequest.put(PlatformOrderImpl.ServerRequest.SET_REQ_AMOUNT, asSet(new IMessage.Type[] { IMessage.Type.ORDER_CHANGED_OK, IMessage.Type.ORDER_CHANGED_REJECTED }));
/*  63 */     this.byServerRequest.put(PlatformOrderImpl.ServerRequest.SET_OPEN_PRICE, asSet(new IMessage.Type[] { IMessage.Type.ORDER_CHANGED_OK, IMessage.Type.ORDER_CHANGED_REJECTED }));
/*  64 */     this.byServerRequest.put(PlatformOrderImpl.ServerRequest.CLOSE, asSet(new IMessage.Type[] { IMessage.Type.ORDER_CLOSE_OK, IMessage.Type.ORDER_CLOSE_REJECTED }));
/*  65 */     this.byServerRequest.put(PlatformOrderImpl.ServerRequest.SET_EXPIRATION, asSet(new IMessage.Type[] { IMessage.Type.ORDER_CHANGED_OK, IMessage.Type.ORDER_CHANGED_REJECTED }));
/*  66 */     this.byServerRequest.put(PlatformOrderImpl.ServerRequest.SET_SL, asSet(new IMessage.Type[] { IMessage.Type.ORDER_CHANGED_OK, IMessage.Type.ORDER_CHANGED_REJECTED }));
/*  67 */     this.byServerRequest.put(PlatformOrderImpl.ServerRequest.SET_TP, asSet(new IMessage.Type[] { IMessage.Type.ORDER_CHANGED_OK, IMessage.Type.ORDER_CHANGED_REJECTED }));
/*  68 */     this.byServerRequest.put(PlatformOrderImpl.ServerRequest.MERGE_SOURCE, asSet(new IMessage.Type[] { IMessage.Type.ORDER_CLOSE_OK, IMessage.Type.ORDER_CLOSE_REJECTED }));
/*  69 */     this.byServerRequest.put(PlatformOrderImpl.ServerRequest.MERGE_TARGET, asSet(new IMessage.Type[] { IMessage.Type.ORDERS_MERGE_OK, IMessage.Type.ORDERS_MERGE_REJECTED, IMessage.Type.ORDER_CLOSE_OK }));
/*  70 */     this.byServerRequest.put(PlatformOrderImpl.ServerRequest.CANCEL_ORDER, asSet(new IMessage.Type[] { IMessage.Type.ORDER_CLOSE_OK, IMessage.Type.ORDER_CLOSE_REJECTED }));
/*     */ 
/*  72 */     this.byOrderState.put(IOrder.State.CREATED, asSet(new IMessage.Type[] { IMessage.Type.ORDER_SUBMIT_OK, IMessage.Type.ORDER_SUBMIT_REJECTED, IMessage.Type.ORDERS_MERGE_OK, IMessage.Type.ORDERS_MERGE_REJECTED }));
/*  73 */     this.byOrderState.put(IOrder.State.OPENED, asSet(new IMessage.Type[] { IMessage.Type.ORDER_FILL_OK, IMessage.Type.ORDER_FILL_REJECTED, IMessage.Type.ORDER_CLOSE_OK, IMessage.Type.ORDER_CLOSE_REJECTED, IMessage.Type.ORDER_CHANGED_OK, IMessage.Type.ORDER_CHANGED_REJECTED }));
/*  74 */     this.byOrderState.put(IOrder.State.FILLED, asSet(new IMessage.Type[] { IMessage.Type.ORDER_CLOSE_OK, IMessage.Type.ORDER_CLOSE_REJECTED, IMessage.Type.ORDER_CHANGED_OK, IMessage.Type.ORDER_CHANGED_REJECTED, IMessage.Type.ORDERS_MERGE_REJECTED, IMessage.Type.ORDERS_MERGE_OK }));
/*  75 */     this.byOrderState.put(IOrder.State.CLOSED, asSet(new IMessage.Type[0]));
/*  76 */     this.byOrderState.put(IOrder.State.CANCELED, asSet(new IMessage.Type[0]));
/*     */ 
/*  78 */     this.byMessageType.put(MessageType.OK, asSet(new IMessage.Type[] { IMessage.Type.ORDER_SUBMIT_OK, IMessage.Type.ORDER_CHANGED_OK, IMessage.Type.ORDER_CLOSE_OK, IMessage.Type.ORDER_FILL_OK, IMessage.Type.ORDERS_MERGE_OK }));
/*     */ 
/*  80 */     this.byMessageType.put(MessageType.REJECT, asSet(new IMessage.Type[] { IMessage.Type.ORDER_SUBMIT_REJECTED, IMessage.Type.ORDER_CHANGED_REJECTED, IMessage.Type.ORDER_CLOSE_REJECTED, IMessage.Type.ORDER_FILL_REJECTED, IMessage.Type.ORDERS_MERGE_REJECTED }));
/*     */   }
/*     */ 
/*     */   private Set<IMessage.Type> asSet(IMessage.Type[] types)
/*     */   {
/*  85 */     return new HashSet(Arrays.asList(types));
/*     */   }
/*     */ 
/*     */   public IMessage.Type generateResponse(PlatformOrderImpl.ServerRequest lastServerRequest, IOrder.State state, NotificationMessageCode code, String text)
/*     */   {
/*  91 */     IMessage.Type rc = null;
/*     */ 
/*  93 */     switch (1.$SwitchMap$com$dukascopy$transport$common$model$type$NotificationMessageCode[code.ordinal()]) {
/*     */     case 1:
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/*     */     case 9:
/*     */     case 10:
/*     */     case 11:
/*     */     case 12:
/*     */     case 13:
/*     */     case 14:
/*     */     case 15:
/*     */     case 16:
/*     */     case 17:
/*     */     case 18:
/*     */     case 19:
/*     */     case 20:
/*     */     case 21:
/*     */     case 22:
/*     */     case 23:
/*     */     case 24:
/*     */     case 25:
/*     */     case 26:
/*     */     case 27:
/*     */     case 28:
/*     */     case 29:
/*     */     case 30:
/*     */     case 31:
/*     */     case 32:
/*     */     case 33:
/* 127 */       rc = retain(MessageType.REJECT, lastServerRequest, state, code, text);
/* 128 */       break;
/*     */     case 34:
/*     */     case 35:
/*     */     case 36:
/*     */     case 37:
/*     */     case 38:
/*     */     case 39:
/*     */     case 40:
/*     */     case 41:
/*     */     case 42:
/*     */     case 43:
/*     */     case 44:
/*     */     case 45:
/*     */     case 46:
/*     */     case 47:
/*     */     case 48:
/*     */     case 49:
/*     */     case 50:
/*     */     case 51:
/*     */     case 52:
/*     */     case 53:
/*     */     case 54:
/* 153 */       rc = IMessage.Type.NOTIFICATION;
/* 154 */       break;
/*     */     default:
/* 158 */       assertion(text, state, lastServerRequest, code);
/*     */     }
/*     */ 
/* 162 */     return rc;
/*     */   }
/*     */ 
/*     */   private IMessage.Type retain(MessageType messageType, PlatformOrderImpl.ServerRequest lastServerRequest, IOrder.State state, NotificationMessageCode code, String text) {
/* 166 */     IMessage.Type rc = null;
/* 167 */     List rcList = new ArrayList((Collection)this.byMessageType.get(messageType));
/* 168 */     rcList.retainAll((Collection)this.byServerRequest.get(lastServerRequest));
/* 169 */     rcList.retainAll((Collection)this.byOrderState.get(state));
/* 170 */     if (rcList.size() == 1)
/* 171 */       rc = (IMessage.Type)rcList.get(0);
/* 172 */     else if ((lastServerRequest != PlatformOrderImpl.ServerRequest.NONE) || (messageType != MessageType.REJECT))
/*     */     {
/* 175 */       assertion(text, state, lastServerRequest, code);
/*     */     }
/* 177 */     return rc;
/*     */   }
/*     */ 
/*     */   private void assertion(String msg, IOrder.State state, PlatformOrderImpl.ServerRequest lastServerRequest, NotificationMessageCode code)
/*     */   {
/* 182 */     LOGGER.error(msg);
/* 183 */     LOGGER.error("STATES[" + state + "][" + lastServerRequest + "][" + code + "]");
/* 184 */     LOGGER.error("----");
/*     */   }
/*     */ 
/*     */   static enum MessageType
/*     */   {
/*  52 */     OK, REJECT;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.ResponseMessageGenerator
 * JD-Core Version:    0.6.0
 */