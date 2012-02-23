/*     */ package com.dukascopy.transport.common.msg.datafeed;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.util.Base64;
/*     */ import com.dukascopy.transport.util.Bits;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.zip.GZIPInputStream;
/*     */ import java.util.zip.GZIPOutputStream;
/*     */ 
/*     */ public class OrderGroupsBinaryMessage extends AbstractDFSMessage
/*     */ {
/*     */   public static final String TYPE = "orderGroupsBinaryMessage";
/*     */   private List<OrderGroupData> cachedGroups;
/*     */   private List<OrderData> cachedOrders;
/*     */   private List<MergeData> cachedMergedPositions;
/*     */   public static final String GROUPS = "groups";
/*     */   public static final String ORDERS = "orders";
/*     */   public static final String MSG_ORDER = "ord";
/*     */   public static final String MERGED_POSITIONS = "mergedPositions";
/*     */   public static final String HISTORY_FINISHED = "hf";
/*     */ 
/*     */   public OrderGroupsBinaryMessage()
/*     */   {
/*  45 */     setType("orderGroupsBinaryMessage");
/*     */   }
/*     */ 
/*     */   public OrderGroupsBinaryMessage(ProtocolMessage message) {
/*  49 */     super(message);
/*  50 */     setType("orderGroupsBinaryMessage");
/*  51 */     put("groups", message.get("groups"));
/*  52 */     put("orders", message.get("orders"));
/*  53 */     put("mergedPositions", message.get("mergedPositions"));
/*  54 */     put("hf", message.getBoolean("hf"));
/*  55 */     setMessageOrder(message.getInteger("ord"));
/*     */   }
/*     */ 
/*     */   public void setOrderGroups(List<OrderGroupData> orderGroups) throws IOException {
/*  59 */     this.cachedGroups = orderGroups;
/*  60 */     ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*  61 */     OutputStream os = new GZIPOutputStream(bos);
/*  62 */     Bits.writeObject(os, orderGroups);
/*  63 */     os.close();
/*     */ 
/*  65 */     String compressedAndEncoded = Base64.encode(bos.toByteArray());
/*  66 */     put("groups", compressedAndEncoded);
/*     */   }
/*     */ 
/*     */   public List<OrderGroupData> getOrderGroups() throws IOException {
/*  70 */     if (this.cachedGroups == null) {
/*  71 */       String compressedAndEncoded = getString("groups");
/*  72 */       InputStream input = new GZIPInputStream(new ByteArrayInputStream(Base64.decode(compressedAndEncoded)));
/*     */ 
/*  74 */       this.cachedGroups = ((List)Bits.readObject(input, ArrayList.class, OrderGroupData.class));
/*  75 */       put("groups", "");
/*     */     }
/*  77 */     return this.cachedGroups;
/*     */   }
/*     */ 
/*     */   public void setOrders(List<OrderData> orders) throws IOException {
/*  81 */     this.cachedOrders = orders;
/*  82 */     ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*  83 */     OutputStream os = new GZIPOutputStream(bos);
/*  84 */     Bits.writeObject(os, orders);
/*  85 */     os.close();
/*     */ 
/*  87 */     String compressedAndEncoded = Base64.encode(bos.toByteArray());
/*  88 */     put("orders", compressedAndEncoded);
/*     */   }
/*     */ 
/*     */   public List<OrderData> getOrders() throws IOException {
/*  92 */     if (this.cachedOrders == null) {
/*  93 */       String compressedAndEncoded = getString("orders");
/*  94 */       InputStream input = new GZIPInputStream(new ByteArrayInputStream(Base64.decode(compressedAndEncoded)));
/*     */ 
/*  96 */       this.cachedOrders = ((List)Bits.readObject(input, ArrayList.class, OrderData.class));
/*  97 */       put("orders", "");
/*     */     }
/*  99 */     return this.cachedOrders;
/*     */   }
/*     */ 
/*     */   public void setMerges(List<MergeData> merges) throws IOException {
/* 103 */     this.cachedMergedPositions = merges;
/* 104 */     ByteArrayOutputStream bos = new ByteArrayOutputStream();
/* 105 */     OutputStream os = new GZIPOutputStream(bos);
/* 106 */     Bits.writeObject(os, merges);
/* 107 */     os.close();
/*     */ 
/* 109 */     String compressedAndEncoded = Base64.encode(bos.toByteArray());
/* 110 */     put("mergedPositions", compressedAndEncoded);
/*     */   }
/*     */ 
/*     */   public List<MergeData> getMerges() throws IOException {
/* 114 */     if (this.cachedMergedPositions == null) {
/* 115 */       String compressedAndEncoded = getString("mergedPositions");
/* 116 */       InputStream input = new GZIPInputStream(new ByteArrayInputStream(Base64.decode(compressedAndEncoded)));
/*     */ 
/* 118 */       this.cachedMergedPositions = ((List)Bits.readObject(input, ArrayList.class, MergeData.class));
/* 119 */       put("mergedPositions", "");
/*     */     }
/* 121 */     return this.cachedMergedPositions;
/*     */   }
/*     */ 
/*     */   public Integer getMessageOrder() {
/* 125 */     return getInteger("ord");
/*     */   }
/*     */ 
/*     */   public void setMessageOrder(Integer msgOrder) {
/* 129 */     put("ord", msgOrder);
/*     */   }
/*     */ 
/*     */   public Boolean isHistoryFinished() {
/* 133 */     return Boolean.valueOf(getBoolean("hf"));
/*     */   }
/*     */ 
/*     */   public void setHistoryFinished(Boolean finished) {
/* 137 */     put("hf", finished);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.datafeed.OrderGroupsBinaryMessage
 * JD-Core Version:    0.6.0
 */