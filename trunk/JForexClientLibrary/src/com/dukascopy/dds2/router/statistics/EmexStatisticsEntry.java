/*     */ package com.dukascopy.dds2.router.statistics;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.math.BigDecimal;
/*     */ 
/*     */ public class EmexStatisticsEntry
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  10 */   private MinMaxAverageLong totalProcessTime = new MinMaxAverageLong();
/*     */ 
/*  12 */   private MinMaxAverageLong executionConfirmationTime = new MinMaxAverageLong();
/*     */ 
/*  14 */   private MinMaxAverageLong acceptedCancelTime = new MinMaxAverageLong();
/*     */ 
/*  16 */   private MinMaxAverageLong lateCancelTime = new MinMaxAverageLong();
/*     */ 
/*  18 */   private MinMaxAverageLong dcPriceTouchTime = new MinMaxAverageLong();
/*     */ 
/*  20 */   private MinMaxAverageLong crossWith2ndSideTime = new MinMaxAverageLong();
/*     */ 
/*  22 */   private MinMaxAverageLong executionResultAfter2ndSideCross = new MinMaxAverageLong();
/*     */ 
/*  24 */   private MinMaxAverageLong criticalDistanceReachTime = new MinMaxAverageLong();
/*     */ 
/*  26 */   private MinMaxAverageLong zlPriceTouchTime = new MinMaxAverageLong();
/*     */ 
/*  28 */   private MinMaxAverageLong usdMilTouchTime = new MinMaxAverageLong();
/*     */ 
/*  30 */   private MinMaxAverageLong filledBidOfferTime = new MinMaxAverageLong();
/*     */ 
/*  32 */   private MinMaxAverageLong cancelledBidOfferTime = new MinMaxAverageLong();
/*     */ 
/*  34 */   private MinMaxAverageBigDecimal executedAmount = new MinMaxAverageBigDecimal();
/*     */ 
/*  36 */   private long partialFills = 0L;
/*     */ 
/*  38 */   private long sentOffers = 0L;
/*     */ 
/*  40 */   private long sentCancels = 0L;
/*     */ 
/*  42 */   private long filledOffers = 0L;
/*     */ 
/*  44 */   private long rejectedOffers = 0L;
/*     */ 
/*  46 */   private long erroredOffers = 0L;
/*     */ 
/*  48 */   private long timeoutedOffers = 0L;
/*     */ 
/*  50 */   private long missedOffers = 0L;
/*     */ 
/*  52 */   private long acceptedCancels = 0L;
/*     */ 
/*  54 */   private long lateCancels = 0L;
/*     */ 
/*  56 */   private long cancelledOnRouter = 0L;
/*     */ 
/*  58 */   private long cancelDCPriceTouch = 0L;
/*     */ 
/*  60 */   private long cancelDCPriceTouchAccepted = 0L;
/*     */ 
/*  62 */   private long cancelDCPriceTouchLate = 0L;
/*     */ 
/*  64 */   private long cancelBidOfferTimeout = 0L;
/*     */ 
/*  66 */   private long cancelBidOfferTimeoutAccepted = 0L;
/*     */ 
/*  68 */   private long cancelBidOfferTimeoutLate = 0L;
/*     */ 
/*  70 */   private long cancelCriticalDistance = 0L;
/*     */ 
/*  72 */   private long cancelCriticalDistanceAccepted = 0L;
/*     */ 
/*  74 */   private long cancelCriticalDistanceLate = 0L;
/*     */ 
/*  76 */   private long cancelZLTouch = 0L;
/*     */ 
/*  78 */   private long cancelZLTouchAccepted = 0L;
/*     */ 
/*  80 */   private long cancelZLTouchLate = 0L;
/*     */ 
/*  82 */   private long cancelByNewOrder = 0L;
/*     */ 
/*  84 */   private long cancelByNewOrderAccepted = 0L;
/*     */ 
/*  86 */   private long cancelByNewOrderLate = 0L;
/*     */ 
/*  88 */   private long cancelUsdMil = 0L;
/*     */ 
/*  90 */   private long cancelUsdMilAccepted = 0L;
/*     */ 
/*  92 */   private long cancelUsdMilLate = 0L;
/*     */ 
/*  94 */   private long cancelPotentialOverExpo = 0L;
/*     */ 
/*  96 */   private long cancelPotentialOverExpoAccepted = 0L;
/*     */ 
/*  98 */   private long cancelPotentialOverExpoLate = 0L;
/*     */ 
/* 100 */   private long cancelOtherReason = 0L;
/*     */ 
/* 102 */   private long cancelOtherReasonAccepted = 0L;
/*     */ 
/* 104 */   private long cancelOtherReasonLate = 0L;
/*     */ 
/*     */   public MinMaxAverageLong getTotalProcessTime() {
/* 107 */     return this.totalProcessTime;
/*     */   }
/*     */ 
/*     */   public MinMaxAverageLong getExecutionConfirmationTime() {
/* 111 */     return this.executionConfirmationTime;
/*     */   }
/*     */ 
/*     */   public MinMaxAverageLong getAcceptedCancelTime() {
/* 115 */     return this.acceptedCancelTime;
/*     */   }
/*     */ 
/*     */   public MinMaxAverageLong getDcPriceTouchTime() {
/* 119 */     return this.dcPriceTouchTime;
/*     */   }
/*     */ 
/*     */   public MinMaxAverageLong getCrossWith2ndSideTime() {
/* 123 */     return this.crossWith2ndSideTime;
/*     */   }
/*     */ 
/*     */   public MinMaxAverageLong getExecutionResultAfter2ndSideCross() {
/* 127 */     return this.executionResultAfter2ndSideCross;
/*     */   }
/*     */ 
/*     */   public MinMaxAverageLong getCriticalDistanceReachTime() {
/* 131 */     return this.criticalDistanceReachTime;
/*     */   }
/*     */ 
/*     */   public MinMaxAverageLong getZlPriceTouchTime() {
/* 135 */     return this.zlPriceTouchTime;
/*     */   }
/*     */ 
/*     */   public MinMaxAverageLong getLateCancelTime() {
/* 139 */     return this.lateCancelTime;
/*     */   }
/*     */ 
/*     */   public MinMaxAverageLong getFilledBidOfferTime() {
/* 143 */     return this.filledBidOfferTime;
/*     */   }
/*     */ 
/*     */   public MinMaxAverageLong getUsdMilTouchTime() {
/* 147 */     return this.usdMilTouchTime;
/*     */   }
/*     */ 
/*     */   public MinMaxAverageLong getCancelledBidOfferTime() {
/* 151 */     return this.cancelledBidOfferTime;
/*     */   }
/*     */ 
/*     */   public MinMaxAverageBigDecimal getExecutedAmount() {
/* 155 */     return this.executedAmount;
/*     */   }
/*     */ 
/*     */   public long getSentOffers() {
/* 159 */     return this.sentOffers;
/*     */   }
/*     */ 
/*     */   public long getSentCancels() {
/* 163 */     return this.sentCancels;
/*     */   }
/*     */ 
/*     */   public long getFilledOffers() {
/* 167 */     return this.filledOffers;
/*     */   }
/*     */ 
/*     */   public long getRejectedOffers() {
/* 171 */     return this.rejectedOffers;
/*     */   }
/*     */ 
/*     */   public long getErroredOffers() {
/* 175 */     return this.erroredOffers;
/*     */   }
/*     */ 
/*     */   public long getTimeoutedOffers() {
/* 179 */     return this.timeoutedOffers;
/*     */   }
/*     */ 
/*     */   public long getMissedOffers() {
/* 183 */     return this.missedOffers;
/*     */   }
/*     */ 
/*     */   public long getAcceptedCancels() {
/* 187 */     return this.acceptedCancels;
/*     */   }
/*     */ 
/*     */   public long getLateCancels() {
/* 191 */     return this.lateCancels;
/*     */   }
/*     */ 
/*     */   public long getCancelledOnRouter() {
/* 195 */     return this.cancelledOnRouter;
/*     */   }
/*     */ 
/*     */   public long getCancelDCPriceTouch() {
/* 199 */     return this.cancelDCPriceTouch;
/*     */   }
/*     */ 
/*     */   public long getCancelDCPriceTouchAccepted() {
/* 203 */     return this.cancelDCPriceTouchAccepted;
/*     */   }
/*     */ 
/*     */   public long getCancelDCPriceTouchLate() {
/* 207 */     return this.cancelDCPriceTouchLate;
/*     */   }
/*     */ 
/*     */   public long getCancelBidOfferTimeout() {
/* 211 */     return this.cancelBidOfferTimeout;
/*     */   }
/*     */ 
/*     */   public long getCancelBidOfferTimeoutAccepted() {
/* 215 */     return this.cancelBidOfferTimeoutAccepted;
/*     */   }
/*     */ 
/*     */   public long getCancelBidOfferTimeoutLate() {
/* 219 */     return this.cancelBidOfferTimeoutLate;
/*     */   }
/*     */ 
/*     */   public long getCancelCriticalDistance() {
/* 223 */     return this.cancelCriticalDistance;
/*     */   }
/*     */ 
/*     */   public long getCancelCriticalDistanceAccepted() {
/* 227 */     return this.cancelCriticalDistanceAccepted;
/*     */   }
/*     */ 
/*     */   public long getCancelCriticalDistanceLate() {
/* 231 */     return this.cancelCriticalDistanceLate;
/*     */   }
/*     */ 
/*     */   public long getCancelZLTouch() {
/* 235 */     return this.cancelZLTouch;
/*     */   }
/*     */ 
/*     */   public long getCancelZLTouchAccepted() {
/* 239 */     return this.cancelZLTouchAccepted;
/*     */   }
/*     */ 
/*     */   public long getCancelZLTouchLate() {
/* 243 */     return this.cancelZLTouchLate;
/*     */   }
/*     */ 
/*     */   public long getCancelByNewOrder() {
/* 247 */     return this.cancelByNewOrder;
/*     */   }
/*     */ 
/*     */   public long getCancelByNewOrderAccepted() {
/* 251 */     return this.cancelByNewOrderAccepted;
/*     */   }
/*     */ 
/*     */   public long getCancelByNewOrderLate() {
/* 255 */     return this.cancelByNewOrderLate;
/*     */   }
/*     */ 
/*     */   public long getPartialFills() {
/* 259 */     return this.partialFills;
/*     */   }
/*     */ 
/*     */   public long getCancelUsdMil() {
/* 263 */     return this.cancelUsdMil;
/*     */   }
/*     */ 
/*     */   public long getCancelUsdMilAccepted() {
/* 267 */     return this.cancelUsdMilAccepted;
/*     */   }
/*     */ 
/*     */   public long getCancelUsdMilLate() {
/* 271 */     return this.cancelUsdMilLate;
/*     */   }
/*     */ 
/*     */   public long getCancelPotentialOverExpo() {
/* 275 */     return this.cancelPotentialOverExpo;
/*     */   }
/*     */ 
/*     */   public long getCancelPotentialOverExpoAccepted() {
/* 279 */     return this.cancelPotentialOverExpoAccepted;
/*     */   }
/*     */ 
/*     */   public long getCancelPotentialOverExpoLate() {
/* 283 */     return this.cancelPotentialOverExpoLate;
/*     */   }
/*     */ 
/*     */   public long getCancelOtherReason() {
/* 287 */     return this.cancelOtherReason;
/*     */   }
/*     */ 
/*     */   public long getCancelOtherReasonAccepted() {
/* 291 */     return this.cancelOtherReasonAccepted;
/*     */   }
/*     */ 
/*     */   public long getCancelOtherReasonLate() {
/* 295 */     return this.cancelOtherReasonLate;
/*     */   }
/*     */ 
/*     */   public void addAcceptedCancels() {
/* 299 */     this.acceptedCancels += 1L;
/*     */   }
/*     */ 
/*     */   public void addSentOffers() {
/* 303 */     this.sentOffers += 1L;
/*     */   }
/*     */ 
/*     */   public void addSentCancels() {
/* 307 */     this.sentCancels += 1L;
/*     */   }
/*     */ 
/*     */   public void addFilledOffers() {
/* 311 */     this.filledOffers += 1L;
/*     */   }
/*     */ 
/*     */   public void addRejectedOffers() {
/* 315 */     this.rejectedOffers += 1L;
/*     */   }
/*     */ 
/*     */   public void addErroredOffers() {
/* 319 */     this.erroredOffers += 1L;
/*     */   }
/*     */ 
/*     */   public void addTimeoutedOffers() {
/* 323 */     this.timeoutedOffers += 1L;
/*     */   }
/*     */ 
/*     */   public void addMissedOffers() {
/* 327 */     this.missedOffers += 1L;
/*     */   }
/*     */ 
/*     */   public void addLateCancels() {
/* 331 */     this.lateCancels += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelledOnRouter() {
/* 335 */     this.cancelledOnRouter += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelDCPriceTouch() {
/* 339 */     this.cancelDCPriceTouch += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelDCPriceTouchAccepted() {
/* 343 */     this.cancelDCPriceTouchAccepted += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelDCPriceTouchLate() {
/* 347 */     this.cancelDCPriceTouchLate += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelBidOfferTimeout() {
/* 351 */     this.cancelBidOfferTimeout += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelBidOfferTimeoutAccepted() {
/* 355 */     this.cancelBidOfferTimeoutAccepted += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelBidOfferTimeoutLate() {
/* 359 */     this.cancelBidOfferTimeoutLate += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelCriticalDistance() {
/* 363 */     this.cancelCriticalDistance += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelCriticalDistanceAccepted() {
/* 367 */     this.cancelCriticalDistanceAccepted += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelCriticalDistanceLate() {
/* 371 */     this.cancelCriticalDistanceLate += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelZLTouch() {
/* 375 */     this.cancelZLTouch += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelZLTouchAccepted() {
/* 379 */     this.cancelZLTouchAccepted += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelZLTouchLate() {
/* 383 */     this.cancelZLTouchLate += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelByNewOrder() {
/* 387 */     this.cancelByNewOrder += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelByNewOrderAccepted() {
/* 391 */     this.cancelByNewOrderAccepted += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelByNewOrderLate() {
/* 395 */     this.cancelByNewOrderLate += 1L;
/*     */   }
/*     */ 
/*     */   public void addPartialFills() {
/* 399 */     this.partialFills += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelUsdMil() {
/* 403 */     this.cancelUsdMil += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelUsdMilAccepted() {
/* 407 */     this.cancelUsdMilAccepted += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelUsdMilLate() {
/* 411 */     this.cancelUsdMilLate += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelPotentialOverExpo() {
/* 415 */     this.cancelPotentialOverExpo += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelPotentialOverExpoAccepted() {
/* 419 */     this.cancelPotentialOverExpoAccepted += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelPotentialOverExpoLate() {
/* 423 */     this.cancelPotentialOverExpoLate += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelOtherReason() {
/* 427 */     this.cancelOtherReason += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelOtherReasonAccepted() {
/* 431 */     this.cancelOtherReasonAccepted += 1L;
/*     */   }
/*     */ 
/*     */   public void addCancelOtherReasonLate() {
/* 435 */     this.cancelOtherReasonLate += 1L;
/*     */   }
/*     */ 
/*     */   public void updateTotalProcessTime(long totalProcessTime) {
/* 439 */     if (totalProcessTime < this.totalProcessTime.getMinimum()) {
/* 440 */       this.totalProcessTime.setMinimum(totalProcessTime);
/*     */     }
/* 442 */     if (totalProcessTime > this.totalProcessTime.getMaximum()) {
/* 443 */       this.totalProcessTime.setMaximum(totalProcessTime);
/*     */     }
/* 445 */     this.totalProcessTime.setCount(this.totalProcessTime.getCount() + 1L);
/* 446 */     this.totalProcessTime.setSum(this.totalProcessTime.getSum() + totalProcessTime);
/*     */   }
/*     */ 
/*     */   public void updateExecutionConfirmationTime(long executionConfirmationTime) {
/* 450 */     if (executionConfirmationTime < this.executionConfirmationTime.getMinimum()) {
/* 451 */       this.executionConfirmationTime.setMinimum(executionConfirmationTime);
/*     */     }
/* 453 */     if (executionConfirmationTime > this.executionConfirmationTime.getMaximum()) {
/* 454 */       this.executionConfirmationTime.setMaximum(executionConfirmationTime);
/*     */     }
/* 456 */     this.executionConfirmationTime.setCount(this.executionConfirmationTime.getCount() + 1L);
/* 457 */     this.executionConfirmationTime.setSum(this.executionConfirmationTime.getSum() + executionConfirmationTime);
/*     */   }
/*     */ 
/*     */   public void updateAcceptedCancelTime(long acceptedCancelTime) {
/* 461 */     if (acceptedCancelTime < this.acceptedCancelTime.getMinimum()) {
/* 462 */       this.acceptedCancelTime.setMinimum(acceptedCancelTime);
/*     */     }
/* 464 */     if (acceptedCancelTime > this.acceptedCancelTime.getMaximum()) {
/* 465 */       this.acceptedCancelTime.setMaximum(acceptedCancelTime);
/*     */     }
/* 467 */     this.acceptedCancelTime.setCount(this.acceptedCancelTime.getCount() + 1L);
/* 468 */     this.acceptedCancelTime.setSum(this.acceptedCancelTime.getSum() + acceptedCancelTime);
/*     */   }
/*     */ 
/*     */   public void updateDcPriceTouchTime(long dcPriceTouchTime) {
/* 472 */     if (dcPriceTouchTime < this.dcPriceTouchTime.getMinimum()) {
/* 473 */       this.dcPriceTouchTime.setMinimum(dcPriceTouchTime);
/*     */     }
/* 475 */     if (dcPriceTouchTime > this.dcPriceTouchTime.getMaximum()) {
/* 476 */       this.dcPriceTouchTime.setMaximum(dcPriceTouchTime);
/*     */     }
/* 478 */     this.dcPriceTouchTime.setCount(this.dcPriceTouchTime.getCount() + 1L);
/* 479 */     this.dcPriceTouchTime.setSum(this.dcPriceTouchTime.getSum() + dcPriceTouchTime);
/*     */   }
/*     */ 
/*     */   public void updateCrossWith2ndSideTime(long crossWith2ndSideTime) {
/* 483 */     if (crossWith2ndSideTime < this.crossWith2ndSideTime.getMinimum()) {
/* 484 */       this.crossWith2ndSideTime.setMinimum(crossWith2ndSideTime);
/*     */     }
/* 486 */     if (crossWith2ndSideTime > this.crossWith2ndSideTime.getMaximum()) {
/* 487 */       this.crossWith2ndSideTime.setMaximum(crossWith2ndSideTime);
/*     */     }
/* 489 */     this.crossWith2ndSideTime.setCount(this.crossWith2ndSideTime.getCount() + 1L);
/* 490 */     this.crossWith2ndSideTime.setSum(this.crossWith2ndSideTime.getSum() + crossWith2ndSideTime);
/*     */   }
/*     */ 
/*     */   public void updateExecutionResultAfter2ndSideCross(long executionResultAfter2ndSideCross) {
/* 494 */     if (executionResultAfter2ndSideCross < this.executionResultAfter2ndSideCross.getMinimum()) {
/* 495 */       this.executionResultAfter2ndSideCross.setMinimum(executionResultAfter2ndSideCross);
/*     */     }
/* 497 */     if (executionResultAfter2ndSideCross > this.executionResultAfter2ndSideCross.getMaximum()) {
/* 498 */       this.executionResultAfter2ndSideCross.setMaximum(executionResultAfter2ndSideCross);
/*     */     }
/* 500 */     this.executionResultAfter2ndSideCross.setCount(this.executionResultAfter2ndSideCross.getCount() + 1L);
/* 501 */     this.executionResultAfter2ndSideCross.setSum(this.executionResultAfter2ndSideCross.getSum() + executionResultAfter2ndSideCross);
/*     */   }
/*     */ 
/*     */   public void updateCriticalDistanceReachTime(long criticalDistanceReachTime) {
/* 505 */     if (criticalDistanceReachTime < this.criticalDistanceReachTime.getMinimum()) {
/* 506 */       this.criticalDistanceReachTime.setMinimum(criticalDistanceReachTime);
/*     */     }
/* 508 */     if (criticalDistanceReachTime > this.criticalDistanceReachTime.getMaximum()) {
/* 509 */       this.criticalDistanceReachTime.setMaximum(criticalDistanceReachTime);
/*     */     }
/* 511 */     this.criticalDistanceReachTime.setCount(this.criticalDistanceReachTime.getCount() + 1L);
/* 512 */     this.criticalDistanceReachTime.setSum(this.criticalDistanceReachTime.getSum() + criticalDistanceReachTime);
/*     */   }
/*     */ 
/*     */   public void updateZLPriceTouchTime(long zlPriceTouchTime) {
/* 516 */     if (zlPriceTouchTime < this.zlPriceTouchTime.getMinimum()) {
/* 517 */       this.zlPriceTouchTime.setMinimum(zlPriceTouchTime);
/*     */     }
/* 519 */     if (zlPriceTouchTime > this.zlPriceTouchTime.getMaximum()) {
/* 520 */       this.zlPriceTouchTime.setMaximum(zlPriceTouchTime);
/*     */     }
/* 522 */     this.zlPriceTouchTime.setCount(this.zlPriceTouchTime.getCount() + 1L);
/* 523 */     this.zlPriceTouchTime.setSum(this.zlPriceTouchTime.getSum() + zlPriceTouchTime);
/*     */   }
/*     */ 
/*     */   public void updateLateCancelTime(long lateCancelTime) {
/* 527 */     if (lateCancelTime < this.lateCancelTime.getMinimum()) {
/* 528 */       this.lateCancelTime.setMinimum(lateCancelTime);
/*     */     }
/* 530 */     if (lateCancelTime > this.lateCancelTime.getMaximum()) {
/* 531 */       this.lateCancelTime.setMaximum(lateCancelTime);
/*     */     }
/* 533 */     this.lateCancelTime.setCount(this.lateCancelTime.getCount() + 1L);
/* 534 */     this.lateCancelTime.setSum(this.lateCancelTime.getSum() + lateCancelTime);
/*     */   }
/*     */ 
/*     */   public void updateUsdMilTouchTime(long usdMilTouchTime) {
/* 538 */     if (usdMilTouchTime < this.usdMilTouchTime.getMinimum()) {
/* 539 */       this.usdMilTouchTime.setMinimum(usdMilTouchTime);
/*     */     }
/* 541 */     if (usdMilTouchTime > this.usdMilTouchTime.getMaximum()) {
/* 542 */       this.usdMilTouchTime.setMaximum(usdMilTouchTime);
/*     */     }
/* 544 */     this.usdMilTouchTime.setCount(this.usdMilTouchTime.getCount() + 1L);
/* 545 */     this.usdMilTouchTime.setSum(this.usdMilTouchTime.getSum() + usdMilTouchTime);
/*     */   }
/*     */ 
/*     */   public void updateFilledBidOfferTime(long filledBidOfferTime) {
/* 549 */     if (filledBidOfferTime < this.filledBidOfferTime.getMinimum()) {
/* 550 */       this.filledBidOfferTime.setMinimum(filledBidOfferTime);
/*     */     }
/* 552 */     if (filledBidOfferTime > this.filledBidOfferTime.getMaximum()) {
/* 553 */       this.filledBidOfferTime.setMaximum(filledBidOfferTime);
/*     */     }
/* 555 */     this.filledBidOfferTime.setCount(this.filledBidOfferTime.getCount() + 1L);
/* 556 */     this.filledBidOfferTime.setSum(this.filledBidOfferTime.getSum() + filledBidOfferTime);
/*     */   }
/*     */ 
/*     */   public void updateCancelledBidOfferTime(long cancelledBidOfferTime) {
/* 560 */     if (cancelledBidOfferTime < this.cancelledBidOfferTime.getMinimum()) {
/* 561 */       this.cancelledBidOfferTime.setMinimum(cancelledBidOfferTime);
/*     */     }
/* 563 */     if (cancelledBidOfferTime > this.cancelledBidOfferTime.getMaximum()) {
/* 564 */       this.cancelledBidOfferTime.setMaximum(cancelledBidOfferTime);
/*     */     }
/* 566 */     this.cancelledBidOfferTime.setCount(this.cancelledBidOfferTime.getCount() + 1L);
/* 567 */     this.cancelledBidOfferTime.setSum(this.cancelledBidOfferTime.getSum() + cancelledBidOfferTime);
/*     */   }
/*     */ 
/*     */   public void updateExecutedAmount(BigDecimal executedAmount) {
/* 571 */     if (executedAmount.compareTo(this.executedAmount.getMinimum()) < 0) {
/* 572 */       this.executedAmount.setMinimum(executedAmount);
/*     */     }
/* 574 */     if (executedAmount.compareTo(this.executedAmount.getMaximum()) > 0) {
/* 575 */       this.executedAmount.setMaximum(executedAmount);
/*     */     }
/* 577 */     this.executedAmount.setCount(this.executedAmount.getCount() + 1L);
/* 578 */     this.executedAmount.setSum(this.executedAmount.getSum().add(executedAmount));
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 583 */     return "EmexStatisticsEntry [acceptedCancels=" + this.acceptedCancels + ", cancelBidOfferTimeout=" + this.cancelBidOfferTimeout + ", cancelBidOfferTimeoutAccepted=" + this.cancelBidOfferTimeoutAccepted + ", cancelBidOfferTimeoutLate=" + this.cancelBidOfferTimeoutLate + ", cancelByNewOrder=" + this.cancelByNewOrder + ", cancelByNewOrderAccepted=" + this.cancelByNewOrderAccepted + ", cancelByNewOrderLate=" + this.cancelByNewOrderLate + ", cancelCriticalDistance=" + this.cancelCriticalDistance + ", cancelCriticalDistanceAccepted=" + this.cancelCriticalDistanceAccepted + ", cancelCriticalDistanceLate=" + this.cancelCriticalDistanceLate + ", cancelDCPriceTouch=" + this.cancelDCPriceTouch + ", cancelDCPriceTouchAccepted=" + this.cancelDCPriceTouchAccepted + ", cancelDCPriceTouchLate=" + this.cancelDCPriceTouchLate + ", cancelOtherReason=" + this.cancelOtherReason + ", cancelOtherReasonAccepted=" + this.cancelOtherReasonAccepted + ", cancelOtherReasonLate=" + this.cancelOtherReasonLate + ", cancelZLTouch=" + this.cancelZLTouch + ", cancelZLTouchAccepted=" + this.cancelZLTouchAccepted + ", cancelZLTouchLate=" + this.cancelZLTouchLate + ", cancelledOnRouter=" + this.cancelledOnRouter + ", erroredOffers=" + this.erroredOffers + ", filledOffers=" + this.filledOffers + ", lateCancels=" + this.lateCancels + ", missedOffers=" + this.missedOffers + ", rejectedOffers=" + this.rejectedOffers + ", sentCancels=" + this.sentCancels + ", sentOffers=" + this.sentOffers + ", timeoutedOffers=" + this.timeoutedOffers + ", partialFills=" + this.partialFills + ", cancelUsdMil=" + this.cancelUsdMil + ", cancelUsdMilAccepted=" + this.cancelUsdMilAccepted + ", cancelUsdMilLate=" + this.cancelUsdMilLate + ", cancelPotentialOverExpo=" + this.cancelPotentialOverExpo + ", cancelPotentialOverExpoAccepted=" + this.cancelPotentialOverExpoAccepted + ", cancelPotentialOverExpoLate=" + this.cancelPotentialOverExpoLate + ", executedAmount=" + "[min=" + this.executedAmount.getMinimum().toPlainString() + ", sum=" + this.executedAmount.getSum().toPlainString() + ", avg=" + this.executedAmount.getAverage().toPlainString() + "]" + ", cancelledBidOfferTime=" + "[min=" + this.cancelledBidOfferTime.getMinimum() + ", max=" + this.cancelledBidOfferTime.getMaximum() + ", avg=" + this.cancelledBidOfferTime.getAverage() + "]" + ", filledBidOfferTime=" + "[min=" + this.filledBidOfferTime.getMinimum() + ", max=" + this.filledBidOfferTime.getMaximum() + ", avg=" + this.filledBidOfferTime.getAverage() + "]" + ", usdMilTouchTime=" + "[min=" + this.usdMilTouchTime.getMinimum() + ", max=" + this.usdMilTouchTime.getMaximum() + ", avg=" + this.usdMilTouchTime.getAverage() + "]" + ", acceptedCancelTime=" + "[min=" + this.acceptedCancelTime.getMinimum() + ", max=" + this.acceptedCancelTime.getMaximum() + ", avg=" + this.acceptedCancelTime.getAverage() + "]" + ", lateCancelTime=" + "[min=" + this.lateCancelTime.getMinimum() + ", max=" + this.lateCancelTime.getMaximum() + ", avg=" + this.lateCancelTime.getAverage() + "]" + ", criticalDistanceReachTime=" + "[min=" + this.criticalDistanceReachTime.getMinimum() + ", max=" + this.criticalDistanceReachTime.getMaximum() + ", avg=" + this.criticalDistanceReachTime.getAverage() + "]" + ", crossWith2ndSideTime=" + "[min=" + this.crossWith2ndSideTime.getMinimum() + ", max=" + this.crossWith2ndSideTime.getMaximum() + ", avg=" + this.crossWith2ndSideTime.getAverage() + "]" + ", dcPriceTouchTime=" + "[min=" + this.dcPriceTouchTime.getMinimum() + ", max=" + this.dcPriceTouchTime.getMaximum() + ", avg=" + this.dcPriceTouchTime.getAverage() + "]" + ", executionConfirmationTime=" + "[min=" + this.executionConfirmationTime.getMinimum() + ", max=" + this.executionConfirmationTime.getMaximum() + ", avg=" + this.executionConfirmationTime.getAverage() + "]" + ", executionResultAfter2ndSideCross=" + "[min=" + this.executionResultAfter2ndSideCross.getMinimum() + ", max=" + this.executionResultAfter2ndSideCross.getMaximum() + ", avg=" + this.executionResultAfter2ndSideCross.getAverage() + "]" + ", totalProcessTime=" + "[min=" + this.totalProcessTime.getMinimum() + ", max=" + this.totalProcessTime.getMaximum() + ", avg=" + this.totalProcessTime.getAverage() + "]" + ", zlPriceTouchTime=" + "[min=" + this.zlPriceTouchTime.getMinimum() + ", max=" + this.zlPriceTouchTime.getMaximum() + ", avg=" + this.zlPriceTouchTime.getAverage() + "]]";
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.router.statistics.EmexStatisticsEntry
 * JD-Core Version:    0.6.0
 */