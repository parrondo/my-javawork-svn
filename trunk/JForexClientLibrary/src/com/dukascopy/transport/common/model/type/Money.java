/*     */ package com.dukascopy.transport.common.model.type;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ import java.util.Currency;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class Money
/*     */   implements Comparable<Money>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 200706201047L;
/*     */   private static Map<String, Currency> currencies;
/*  39 */   protected BigDecimal value = new BigDecimal(0);
/*     */   protected Currency currency;
/*     */ 
/*     */   public static Currency getCurrency(String name)
/*     */   {
/*  50 */     Currency currency = (Currency)currencies.get(name);
/*  51 */     if (currency == null) {
/*  52 */       currency = Currency.getInstance(name);
/*  53 */       currencies.put(name, currency);
/*     */     }
/*  55 */     return currency;
/*     */   }
/*     */ 
/*     */   public static Money of(String stringValue)
/*     */     throws IllegalArgumentException
/*     */   {
/*  63 */     if (stringValue == null)
/*  64 */       throw new IllegalArgumentException("stringValue should be not null");
/*  65 */     String[] tokens = stringValue.split("\\s+");
/*  66 */     if (tokens.length != 2)
/*  67 */       throw new IllegalArgumentException("stringValue " + stringValue + " should has 2 tokens");
/*  68 */     String currencyCode = tokens[1];
/*  69 */     Currency currency = getCurrency(currencyCode);
/*  70 */     return new Money(new BigDecimal(tokens[0]), currency);
/*     */   }
/*     */ 
/*     */   public Money(BigDecimal value, Currency currency)
/*     */   {
/*  82 */     this.value = value;
/*  83 */     this.currency = currency;
/*     */   }
/*     */ 
/*     */   public Money(String value, String currency) {
/*  87 */     this.value = new BigDecimal(value);
/*     */     try {
/*  89 */       this.currency = getCurrency(currency);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public Money add(Money added)
/*     */   {
/* 104 */     assert (added != null);
/* 105 */     if (added.getCurrency().equals(this.currency)) {
/* 106 */       return add(added.getValue());
/*     */     }
/* 108 */     throw new IllegalArgumentException("Cannot add money of another currency.");
/*     */   }
/*     */ 
/*     */   public Money add(BigDecimal addedValue)
/*     */   {
/* 120 */     assert (addedValue != null);
/* 121 */     return new Money(addedValue.add(this.value), this.currency);
/*     */   }
/*     */ 
/*     */   public Money subtract(Money subtracted)
/*     */   {
/* 133 */     assert (subtracted != null);
/* 134 */     if (subtracted.getCurrency().equals(this.currency)) {
/* 135 */       return subtract(subtracted.getValue());
/*     */     }
/* 137 */     throw new IllegalArgumentException("Cannot subtract money of another currency.");
/*     */   }
/*     */ 
/*     */   public Money subtract(BigDecimal value)
/*     */   {
/* 150 */     return add(value.negate());
/*     */   }
/*     */ 
/*     */   public Money multiply(BigDecimal value)
/*     */   {
/* 161 */     return new Money(this.value.multiply(value), getCurrency());
/*     */   }
/*     */ 
/*     */   public Money divide(BigDecimal value)
/*     */   {
/* 174 */     return new Money(this.value.divide(value, RoundingMode.HALF_EVEN), getCurrency());
/*     */   }
/*     */ 
/*     */   public Money negate()
/*     */   {
/* 183 */     return new Money(this.value.negate(), getCurrency());
/*     */   }
/*     */ 
/*     */   public Money abs()
/*     */   {
/* 192 */     return new Money(this.value.abs(), getCurrency());
/*     */   }
/*     */ 
/*     */   public Money round(int scale)
/*     */   {
/* 201 */     return new Money(this.value.setScale(scale, RoundingMode.HALF_EVEN).stripTrailingZeros(), getCurrency());
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 212 */     return this.value.stripTrailingZeros().toPlainString() + " " + this.currency;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 225 */     if (this == o)
/* 226 */       return true;
/* 227 */     if ((o == null) || (getClass() != o.getClass())) {
/* 228 */       return false;
/*     */     }
/* 230 */     Money money = (Money)o;
/*     */ 
/* 232 */     if (!this.currency.equals(money.currency)) {
/* 233 */       return false;
/*     */     }
/* 235 */     return this.value.compareTo(money.value) == 0;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 242 */     int result = this.value.hashCode();
/* 243 */     result = 29 * result + this.currency.hashCode();
/* 244 */     return result;
/*     */   }
/*     */ 
/*     */   public int compareTo(Money compared)
/*     */   {
/* 255 */     assert (compared.getCurrency().equals(this.currency));
/* 256 */     return getValue().compareTo(compared.getValue());
/*     */   }
/*     */ 
/*     */   public BigDecimal getValue()
/*     */   {
/* 265 */     return this.value;
/*     */   }
/*     */ 
/*     */   public Currency getCurrency()
/*     */   {
/* 274 */     return this.currency;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  23 */     currencies = new HashMap();
/*     */ 
/*  26 */     getCurrency("EUR");
/*  27 */     getCurrency("USD");
/*  28 */     getCurrency("JPY");
/*  29 */     getCurrency("GBP");
/*  30 */     getCurrency("CHF");
/*  31 */     getCurrency("CAD");
/*  32 */     getCurrency("NZD");
/*  33 */     getCurrency("AUD");
/*  34 */     getCurrency("DKK");
/*  35 */     getCurrency("NOK");
/*  36 */     getCurrency("SEK");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.model.type.Money
 * JD-Core Version:    0.6.0
 */