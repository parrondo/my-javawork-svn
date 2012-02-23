/*     */ package com.dukascopy.transport.common.msg.datafeed;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderState;
/*     */ import com.dukascopy.transport.util.Bits;
/*     */ import com.dukascopy.transport.util.Bits.BitsSerializable;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.StreamCorruptedException;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ public class OrderData
/*     */   implements Bits.BitsSerializable
/*     */ {
/*     */   private static final int VERSION = 1;
/*     */   private static final String HEADER = "Od";
/*     */   private boolean isRollOver;
/*     */   private String orderGroupId;
/*     */   private OrderDirection orderDirection;
/*     */   private OrderState orderState;
/*     */   private String orderId;
/*     */   private BigDecimal amount;
/*     */   private long lastChanged;
/*     */   private long createdDate;
/*     */   private BigDecimal priceClient;
/*     */   private OrderSide side;
/*     */   private String extSysId;
/*     */   private String instrument;
/*     */   private String origGroupId;
/*     */   private BigDecimal orderCommission;
/*     */ 
/*     */   public void writeObject(OutputStream os)
/*     */     throws IOException
/*     */   {
/*  42 */     os.write("Od".getBytes());
/*  43 */     os.write(1);
/*  44 */     os.write(Bits.booleanBytes(this.isRollOver));
/*  45 */     Bits.writeObject(os, this.orderGroupId);
/*  46 */     Bits.writeObject(os, this.orderDirection);
/*  47 */     Bits.writeObject(os, this.orderState);
/*  48 */     Bits.writeObject(os, this.orderId);
/*  49 */     Bits.writeObject(os, this.amount);
/*  50 */     os.write(Bits.longBytes(this.lastChanged));
/*  51 */     os.write(Bits.longBytes(this.createdDate));
/*  52 */     Bits.writeObject(os, this.priceClient);
/*  53 */     Bits.writeObject(os, this.side);
/*  54 */     Bits.writeObject(os, this.extSysId);
/*  55 */     Bits.writeObject(os, this.instrument);
/*  56 */     Bits.writeObject(os, this.origGroupId);
/*  57 */     Bits.writeObject(os, this.orderCommission);
/*     */   }
/*     */ 
/*     */   public void readObject(InputStream is) throws IOException
/*     */   {
/*  62 */     byte[] header = Bits.read(is, new byte["Od".length()]);
/*  63 */     if (!Arrays.equals(header, "Od".getBytes())) {
/*  64 */       throw new StreamCorruptedException("Deserialization error, unknown header [" + new String(header, "UTF-8") + "]");
/*     */     }
/*  66 */     int version = is.read();
/*  67 */     if (version != 1) {
/*  68 */       throw new StreamCorruptedException("Versions doesn't match, stream version [" + version + "], class version [" + 1 + "]");
/*     */     }
/*  70 */     this.isRollOver = Bits.getBoolean((byte)is.read());
/*  71 */     this.orderGroupId = ((String)Bits.readObject(is, String.class));
/*  72 */     this.orderDirection = ((OrderDirection)Bits.readObject(is, OrderDirection.class));
/*  73 */     this.orderState = ((OrderState)Bits.readObject(is, OrderState.class));
/*  74 */     this.orderId = ((String)Bits.readObject(is, String.class));
/*  75 */     this.amount = ((BigDecimal)Bits.readObject(is, BigDecimal.class));
/*  76 */     this.lastChanged = Bits.getLong(Bits.read(is, new byte[8]));
/*  77 */     this.createdDate = Bits.getLong(Bits.read(is, new byte[8]));
/*  78 */     this.priceClient = ((BigDecimal)Bits.readObject(is, BigDecimal.class));
/*  79 */     this.side = ((OrderSide)Bits.readObject(is, OrderSide.class));
/*  80 */     this.extSysId = ((String)Bits.readObject(is, String.class));
/*  81 */     this.instrument = ((String)Bits.readObject(is, String.class));
/*  82 */     this.origGroupId = ((String)Bits.readObject(is, String.class));
/*  83 */     this.orderCommission = ((BigDecimal)Bits.readObject(is, BigDecimal.class));
/*     */   }
/*     */ 
/*     */   public boolean isRollOver() {
/*  87 */     return this.isRollOver;
/*     */   }
/*     */ 
/*     */   public void setRollOver(boolean rollOver) {
/*  91 */     this.isRollOver = rollOver;
/*     */   }
/*     */ 
/*     */   public String getOrderGroupId() {
/*  95 */     return this.orderGroupId;
/*     */   }
/*     */ 
/*     */   public void setOrderGroupId(String orderGroupId) {
/*  99 */     this.orderGroupId = orderGroupId;
/*     */   }
/*     */ 
/*     */   public OrderDirection getOrderDirection() {
/* 103 */     return this.orderDirection;
/*     */   }
/*     */ 
/*     */   public void setOrderDirection(OrderDirection orderDirection) {
/* 107 */     this.orderDirection = orderDirection;
/*     */   }
/*     */ 
/*     */   public OrderState getOrderState() {
/* 111 */     return this.orderState;
/*     */   }
/*     */ 
/*     */   public void setOrderState(OrderState orderState) {
/* 115 */     this.orderState = orderState;
/*     */   }
/*     */ 
/*     */   public String getOrderId() {
/* 119 */     return this.orderId;
/*     */   }
/*     */ 
/*     */   public void setOrderId(String orderId) {
/* 123 */     this.orderId = orderId;
/*     */   }
/*     */ 
/*     */   public BigDecimal getAmount() {
/* 127 */     return this.amount;
/*     */   }
/*     */ 
/*     */   public void setAmount(BigDecimal amount) {
/* 131 */     this.amount = amount;
/*     */   }
/*     */ 
/*     */   public long getLastChanged() {
/* 135 */     return this.lastChanged;
/*     */   }
/*     */ 
/*     */   public void setLastChanged(long lastChanged) {
/* 139 */     this.lastChanged = lastChanged;
/*     */   }
/*     */ 
/*     */   public long getCreatedDate() {
/* 143 */     return this.createdDate;
/*     */   }
/*     */ 
/*     */   public void setCreatedDate(long createdDate) {
/* 147 */     this.createdDate = createdDate;
/*     */   }
/*     */ 
/*     */   public BigDecimal getPriceClient() {
/* 151 */     return this.priceClient;
/*     */   }
/*     */ 
/*     */   public void setPriceClient(BigDecimal priceClient) {
/* 155 */     this.priceClient = priceClient;
/*     */   }
/*     */ 
/*     */   public OrderSide getSide() {
/* 159 */     return this.side;
/*     */   }
/*     */ 
/*     */   public void setSide(OrderSide side) {
/* 163 */     this.side = side;
/*     */   }
/*     */ 
/*     */   public String getExtSysId() {
/* 167 */     return this.extSysId;
/*     */   }
/*     */ 
/*     */   public void setExtSysId(String extSysId) {
/* 171 */     this.extSysId = extSysId;
/*     */   }
/*     */ 
/*     */   public String getInstrument() {
/* 175 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument) {
/* 179 */     this.instrument = instrument;
/*     */   }
/*     */ 
/*     */   public String getOrigGroupId() {
/* 183 */     return this.origGroupId;
/*     */   }
/*     */ 
/*     */   public void setOrigGroupId(String origGroupId) {
/* 187 */     this.origGroupId = origGroupId;
/*     */   }
/*     */ 
/*     */   public BigDecimal getOrderCommission() {
/* 191 */     return this.orderCommission;
/*     */   }
/*     */ 
/*     */   public void setOrderCommission(BigDecimal orderCommission) {
/* 195 */     this.orderCommission = orderCommission;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.datafeed.OrderData
 * JD-Core Version:    0.6.0
 */