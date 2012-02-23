/*     */ package com.dukascopy.dds2.greed.agent.strategy.objects;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.agent.AgentException;
/*     */ import com.dukascopy.dds2.greed.agent.IDDSAgent;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.model.type.Position;
/*     */ import com.dukascopy.transport.common.model.type.PositionSide;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class Order
/*     */ {
/*  26 */   private Position position = null;
/*     */ 
/*  28 */   private IDDSAgent agent = null;
/*     */ 
/*  35 */   private OrderGroupMessage ogm = null;
/*     */ 
/*     */   public Order(Position position)
/*     */   {
/*  32 */     this.position = position;
/*     */   }
/*     */ 
/*     */   public Order(OrderGroupMessage ogm)
/*     */   {
/*  38 */     this.ogm = ogm;
/*     */   }
/*     */ 
/*     */   public String getLabel() {
/*  42 */     String rc = "";
/*  43 */     if (this.ogm != null) {
/*  44 */       rc = this.ogm.getOpeningOrder().getTag();
/*     */     }
/*  46 */     if (this.position != null) {
/*  47 */       if (this.position.getOrderGroup() != null) {
/*  48 */         rc = this.position.getOrderGroup().getOpeningOrder().getTag();
/*  49 */         if (rc == null)
/*  50 */           rc = this.position.getOrderGroup().getOrderGroupId();
/*     */       }
/*     */       else
/*     */       {
/*  54 */         rc = this.position.getTag();
/*  55 */         if (rc == null) {
/*  56 */           rc = this.position.getPositionID();
/*     */         }
/*     */       }
/*     */     }
/*  60 */     return rc;
/*     */   }
/*     */ 
/*     */   public Date getOrderCreatedTime()
/*     */   {
/*  66 */     String str = null;
/*  67 */     Date rc = null;
/*  68 */     if (this.ogm != null) {
/*  69 */       rc = this.ogm.getOpeningOrder().getCreatedDate();
/*     */     }
/*  71 */     if (this.position != null) {
/*  72 */       rc = this.position.getOrderGroup().getOpeningOrder().getCreatedDate();
/*     */     }
/*     */ 
/*  75 */     return rc;
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/*  84 */     String rc = "";
/*  85 */     if (this.ogm != null) {
/*  86 */       rc = this.ogm.getOpeningOrder().getOrderGroupId();
/*     */     }
/*  88 */     if (this.position != null) {
/*  89 */       rc = this.position.getPositionID();
/*     */     }
/*  91 */     return rc;
/*     */   }
/*     */ 
/*     */   public double getProfitLoss() {
/*  95 */     double rc = 0.0D;
/*  96 */     if ((this.position != null) && (this.position.getProfitLoss() != null)) {
/*  97 */       rc = this.position.getProfitLoss().getValue().doubleValue();
/*     */     }
/*  99 */     return rc;
/*     */   }
/*     */ 
/*     */   public double getProfitLossPips()
/*     */   {
/* 104 */     if (this.position == null) {
/* 105 */       return 0.0D;
/*     */     }
/* 107 */     double pnl = getProfitLoss();
/* 108 */     double pipProfit = pnl / (getAmount() * getOnePip() * 1000000.0D);
/* 109 */     pipProfit = StratUtils.round(pipProfit, 3);
/* 110 */     return pipProfit;
/*     */   }
/*     */ 
/*     */   public double getAmount() {
/* 114 */     double rc = 0.0D;
/* 115 */     if ((this.position != null) && (this.position.getAmount() != null)) {
/* 116 */       rc = this.position.getAmount().getValue().doubleValue();
/*     */     }
/* 118 */     if (this.ogm != null) {
/* 119 */       if (this.ogm.getAmount() != null) {
/* 120 */         rc = this.ogm.getAmount().getValue().doubleValue();
/*     */       }
/* 122 */       if (rc == 0.0D) {
/* 123 */         rc = this.ogm.getOpeningOrder().getAmount().getValue().doubleValue();
/*     */       }
/*     */     }
/* 126 */     rc *= 1.0E-006D;
/* 127 */     return rc;
/*     */   }
/*     */ 
/*     */   public double getOnePip() {
/* 131 */     return Instrument.fromString(getSymbol()).getPipValue();
/*     */   }
/*     */ 
/*     */   public double getPriceOpen() {
/* 135 */     double rc = 0.0D;
/*     */ 
/* 137 */     if (this.ogm != null) {
/* 138 */       Money stopprice = this.ogm.getOpeningOrder().getPriceStop();
/* 139 */       if (stopprice != null) {
/* 140 */         rc = stopprice.getValue().doubleValue();
/*     */       }
/*     */     }
/* 143 */     if (this.position != null) {
/* 144 */       rc = this.position.getPriceOpen().getValue().doubleValue();
/*     */     }
/*     */ 
/* 149 */     return rc;
/*     */   }
/*     */ 
/*     */   public boolean isFilled() {
/* 153 */     boolean rc = false;
/* 154 */     if (this.position != null) {
/* 155 */       rc = true;
/*     */     }
/* 157 */     return rc;
/*     */   }
/*     */ 
/*     */   public String getSymbol() {
/* 161 */     String symbol = "";
/* 162 */     if (this.ogm != null) {
/* 163 */       symbol = this.ogm.getInstrument();
/*     */     }
/* 165 */     if (this.position != null) {
/* 166 */       symbol = this.position.getInstrument();
/*     */     }
/* 168 */     return StratUtils.normalizeSymbol(symbol);
/*     */   }
/*     */ 
/*     */   public int getOrderType() {
/* 172 */     int type = -1;
/* 173 */     if (this.ogm != null) {
/* 174 */       if (this.ogm.getOpeningOrder().getSide() == OrderSide.BUY) {
/* 175 */         type = 0;
/*     */       }
/* 177 */       if (this.ogm.getOpeningOrder().getSide() == OrderSide.SELL) {
/* 178 */         type = 1;
/*     */       }
/*     */     }
/*     */ 
/* 182 */     if (this.position != null) {
/* 183 */       OrderGroupMessage groupMessage = this.position.getOrderGroup();
/* 184 */       if (groupMessage != null) {
/* 185 */         if (groupMessage.getOpeningOrder().getSide() == OrderSide.BUY) {
/* 186 */           type = 0;
/*     */         }
/* 188 */         if (groupMessage.getOpeningOrder().getSide() == OrderSide.SELL)
/* 189 */           type = 1;
/*     */       }
/*     */       else {
/* 192 */         if (this.position.getPositionSide() == PositionSide.LONG) {
/* 193 */           type = 0;
/*     */         }
/* 195 */         if (this.position.getPositionSide() == PositionSide.SHORT) {
/* 196 */           type = 1;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 201 */     return type;
/*     */   }
/*     */ 
/*     */   public int close()
/*     */   {
/* 206 */     int rc = 0;
/* 207 */     if (this.ogm != null) {
/* 208 */       rc = this.agent.cancelOrder(getLabel());
/*     */     }
/* 210 */     if (this.position != null) {
/* 211 */       rc = this.agent.closePosition(getLabel());
/*     */     }
/* 213 */     return rc;
/*     */   }
/*     */ 
/*     */   public int close(double price, double amount) {
/* 217 */     int rc = 0;
/* 218 */     if (this.ogm != null) {
/* 219 */       if ((price != 0.0D) || (amount != 0.0D))
/* 220 */         rc = -99;
/*     */       else {
/* 222 */         rc = this.agent.cancelOrder(getLabel());
/*     */       }
/*     */     }
/* 225 */     if (this.position != null) {
/* 226 */       rc = this.agent.closePosition(getLabel(), price, amount);
/*     */     }
/* 228 */     return rc;
/*     */   }
/*     */ 
/*     */   public int closeProfitPosition(int profitPips) {
/* 232 */     int rc = 0;
/* 233 */     if (this.position != null) {
/* 234 */       rc = this.agent.closeProfitPosition(getLabel(), profitPips);
/*     */     }
/* 236 */     return rc;
/*     */   }
/*     */ 
/*     */   public int submitStop(int cmd, int pips) throws AgentException {
/* 240 */     return this.agent.submitStop(getLabel(), cmd, pips);
/*     */   }
/*     */ 
/*     */   public int submitStopPrice(int cmd, double price) throws AgentException {
/* 244 */     return this.agent.submitStop(getLabel(), cmd, 0, price);
/*     */   }
/*     */ 
/*     */   private double getPrice(int type) {
/* 248 */     double rc = 0.0D;
/*     */ 
/* 250 */     OrderMessage orderMessage = null;
/* 251 */     OrderGroupMessage group = null;
/*     */ 
/* 253 */     if (this.ogm != null) {
/* 254 */       group = this.ogm;
/*     */     }
/* 256 */     if (this.position != null) {
/* 257 */       group = this.position.getOrderGroup();
/*     */     }
/* 259 */     if (type == 0)
/* 260 */       orderMessage = group.getStopLossOrder();
/* 261 */     else if (type == 1) {
/* 262 */       orderMessage = group.getTakeProfitOrder();
/*     */     }
/* 264 */     if (orderMessage != null) {
/* 265 */       Money priceStop = orderMessage.getPriceStop();
/* 266 */       Money priceLimit = orderMessage.getPriceLimit();
/* 267 */       if (priceLimit != null) {
/* 268 */         rc = priceLimit.getValue().doubleValue();
/*     */       }
/* 270 */       if (priceStop != null) {
/* 271 */         rc = priceStop.getValue().doubleValue();
/*     */       }
/*     */     }
/*     */ 
/* 275 */     return rc;
/*     */   }
/*     */ 
/*     */   public void modify(double price)
/*     */   {
/*     */   }
/*     */ 
/*     */   public double getStopLossPrice()
/*     */   {
/* 285 */     return getPrice(0);
/*     */   }
/*     */ 
/*     */   public double getTakeProfitPrice() {
/* 289 */     return getPrice(1);
/*     */   }
/*     */ 
/*     */   public String getComment() {
/* 293 */     OrderGroupMessage group = null;
/*     */ 
/* 295 */     if (this.ogm != null) {
/* 296 */       group = this.ogm;
/*     */     }
/* 298 */     if (this.position != null) {
/* 299 */       group = this.position.getOrderGroup();
/*     */     }
/* 301 */     return group.optString("comment");
/*     */   }
/*     */ 
/*     */   public void setComment(String comment)
/*     */   {
/* 307 */     OrderGroupMessage group = null;
/*     */ 
/* 309 */     if (this.ogm != null) {
/* 310 */       group = this.ogm;
/*     */     }
/* 312 */     if (this.position != null) {
/* 313 */       group = this.position.getOrderGroup();
/*     */     }
/* 315 */     group.put("comment", comment);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 322 */     if (this.ogm != null) {
/* 323 */       return "Entry [" + getLabel() + "] " + getSymbol() + " " + getAmount();
/*     */     }
/* 325 */     if (this.position != null) {
/* 326 */       return "Filled [" + getLabel() + "] " + getSymbol() + " " + getAmount() + " open=" + getPriceOpen() + " [" + getProfitLossPips() + "]";
/*     */     }
/* 328 */     return super.toString();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 333 */     if ((obj instanceof Order)) {
/* 334 */       Order ord = (Order)obj;
/* 335 */       return ord.getId().equals(ord.getId());
/*     */     }
/* 337 */     return super.equals(obj);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 342 */     return Integer.parseInt(getId());
/*     */   }
/*     */ 
/*     */   public void setAgent(IDDSAgent agent) {
/* 346 */     this.agent = agent;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.objects.Order
 * JD-Core Version:    0.6.0
 */