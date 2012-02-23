/*    */ package com.dukascopy.dds2.greed.mt.func;
/*    */ 
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.model.AccountStatement;
/*    */ import com.dukascopy.dds2.greed.mt.common.AgentBase.CommonExecution;
/*    */ import com.dukascopy.dds2.greed.mt.exceptions.MTAgentException;
/*    */ import com.dukascopy.dds2.greed.util.CurrencyConverter;
/*    */ import com.dukascopy.transport.common.model.type.Money;
/*    */ import com.dukascopy.transport.common.model.type.Position;
/*    */ import com.dukascopy.transport.common.model.type.PositionSide;
/*    */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*    */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*    */ import java.math.BigDecimal;
/*    */ import java.util.Currency;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class MOrderProfit extends AgentBase.CommonExecution
/*    */ {
/* 20 */   private static Logger log = LoggerFactory.getLogger(MOrderProfit.class);
/*    */   private CurrencyConverter converter;
/*    */   private AccountStatement accountStatement;
/*    */ 
/*    */   public double execute(int id)
/*    */     throws MTAgentException
/*    */   {
/* 25 */     double returnValue = 0.0D;
/*    */ 
/* 27 */     Integer mtId = new Integer(id);
/* 28 */     OrderGroupMessage msg = getOrderGroup(mtId);
/*    */ 
/* 30 */     Money profit = calculateProfitLoss(msg.getPosition());
/* 31 */     if (profit != null) {
/* 32 */       returnValue = profit.getValue().doubleValue();
/*    */     }
/* 34 */     setError(id, 0, "ERR_NO_ERROR_MSG");
/* 35 */     return returnValue;
/*    */   }
/*    */ 
/*    */   public Money calculateProfitLoss(Position position) {
/* 39 */     if (position == null) {
/* 40 */       return null;
/* 43 */     }
/*    */ this.accountStatement = ((AccountStatement)GreedContext.get("accountStatement"));
/*    */     Currency accountCurrency;
/*    */     try { accountCurrency = this.accountStatement.getLastAccountState().getCurrency();
/*    */     } catch (NullPointerException e) {
/* 48 */       log.warn(e.getMessage());
/* 49 */       return null;
/*    */     }
/* 51 */     BigDecimal profitLoss = getProfitLoss(position);
/*    */ 
/* 53 */     OfferSide closeSide = position.getPositionSide() == PositionSide.LONG ? OfferSide.BID : OfferSide.ASK;
/* 54 */     if (profitLoss != null) {
/*    */       try {
/* 56 */         String instrument = position.getString("instrument");
/* 57 */         String sourceCurrencyString = instrument.substring(4);
/* 58 */         Currency sourceCurrency = Money.getCurrency(sourceCurrencyString);
/* 59 */         BigDecimal value = this.converter.convert(profitLoss, sourceCurrency, accountCurrency, closeSide);
/* 60 */         if (value != null) {
/* 61 */           return new Money(value, accountCurrency);
/*    */         }
/* 63 */         return null;
/*    */       }
/*    */       catch (IllegalStateException e) {
/* 66 */         return null;
/*    */       } catch (NullPointerException npe) {
/* 68 */         return null;
/*    */       }
/*    */     }
/* 71 */     return null;
/*    */   }
/*    */ 
/*    */   private BigDecimal getProfitLoss(Position position)
/*    */   {
/* 76 */     String profitLoss = position.getString("p_l");
/* 77 */     if (profitLoss != null) {
/* 78 */       return new BigDecimal(profitLoss);
/*    */     }
/* 80 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.func.MOrderProfit
 * JD-Core Version:    0.6.0
 */