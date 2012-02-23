/*     */ package com.dukascopy.transport.common.msg.state;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class StateMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "state";
/*     */   private static final String STATES = "s";
/*     */   private static final String IS_FULL_STATE = "f";
/*     */ 
/*     */   public StateMessage()
/*     */   {
/*  24 */     setType("state");
/*  25 */     put("s", new JSONArray());
/*     */   }
/*     */ 
/*     */   public StateMessage(ProtocolMessage msg) {
/*  29 */     super(msg);
/*  30 */     setType("state");
/*     */ 
/*  32 */     JSONArray msgStates = msg.getJSONArray("s");
/*  33 */     JSONArray states = new JSONArray();
/*  34 */     for (int i = 0; i < msgStates.length(); i++) {
/*  35 */       ProtocolMessage propertyState = ProtocolMessage.parse(msgStates.getJSONObject(i).toString());
/*  36 */       if ((propertyState != null) && ((propertyState instanceof StatePropertyMessage))) {
/*  37 */         states.put(propertyState);
/*     */       }
/*     */     }
/*  40 */     put("s", states);
/*  41 */     put("f", msg.getBool("f"));
/*  42 */     put("serviceName", msg.getString("serviceName"));
/*     */   }
/*     */ 
/*     */   public void addStateProperty(StatePropertyMessage state) {
/*  46 */     JSONArray jsonStates = getJSONArray("s");
/*  47 */     jsonStates.put(state);
/*  48 */     put("s", jsonStates);
/*     */   }
/*     */ 
/*     */   public void addStateProperties(StatePropertyMessage[] states) {
/*  52 */     JSONArray jsonStates = getJSONArray("s");
/*  53 */     for (StatePropertyMessage state : states) {
/*  54 */       jsonStates.put(state);
/*     */     }
/*  56 */     put("s", jsonStates);
/*     */   }
/*     */ 
/*     */   public void addStateProperties(List<StatePropertyMessage> states) {
/*  60 */     JSONArray jsonStates = getJSONArray("s");
/*  61 */     for (StatePropertyMessage state : states) {
/*  62 */       jsonStates.put(state);
/*     */     }
/*  64 */     put("s", jsonStates);
/*     */   }
/*     */ 
/*     */   public void setStateProperties(List<StatePropertyMessage> states) {
/*  68 */     JSONArray jsonStates = new JSONArray();
/*  69 */     for (StatePropertyMessage state : states) {
/*  70 */       jsonStates.put(state);
/*     */     }
/*  72 */     put("s", jsonStates);
/*     */   }
/*     */ 
/*     */   public List<StatePropertyMessage> getStateProperties() {
/*  76 */     List states = new ArrayList();
/*  77 */     JSONArray jsonStates = getJSONArray("s");
/*  78 */     for (int i = 0; i < jsonStates.length(); i++) {
/*  79 */       states.add((StatePropertyMessage)jsonStates.getJSONObject(i));
/*     */     }
/*  81 */     return states;
/*     */   }
/*     */ 
/*     */   public void setFullState(boolean isFullState) {
/*  85 */     put("f", isFullState);
/*     */   }
/*     */ 
/*     */   public boolean isFullState() {
/*  89 */     Boolean isFullState = getBool("f");
/*  90 */     if (isFullState == null) {
/*  91 */       return false;
/*     */     }
/*  93 */     return isFullState.booleanValue();
/*     */   }
/*     */ 
/*     */   public void setServiceName(String serviceName) {
/*  97 */     put("serviceName", serviceName);
/*     */   }
/*     */ 
/*     */   public String getServiceName() {
/* 101 */     return getString("serviceName");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.state.StateMessage
 * JD-Core Version:    0.6.0
 */