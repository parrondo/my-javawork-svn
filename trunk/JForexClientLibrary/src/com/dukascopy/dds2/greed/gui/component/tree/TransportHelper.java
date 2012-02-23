/*     */ package com.dukascopy.dds2.greed.gui.component.tree;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.IIndicators.AppliedPrice;
/*     */ import com.dukascopy.api.IIndicators.MaType;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.Unit;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public final class TransportHelper
/*     */ {
/*  27 */   private static final Logger LOGGER = LoggerFactory.getLogger(TransportHelper.class);
/*     */ 
/*     */   public static String valueToString(Object value, Class<?> type)
/*     */   {
/*  32 */     if (value == null)
/*  33 */       return null;
/*  34 */     if (type.equals(Boolean.TYPE))
/*  35 */       return Boolean.toString(((Boolean)value).booleanValue());
/*  36 */     if (type.equals(Boolean.class))
/*  37 */       return Boolean.toString(((Boolean)value).booleanValue());
/*  38 */     if (type.equals(String.class))
/*  39 */       return value.toString();
/*  40 */     if (type.equals(Integer.class))
/*  41 */       return Integer.toString(((Integer)value).intValue());
/*  42 */     if (type.equals(Integer.TYPE))
/*  43 */       return Integer.toString(((Integer)value).intValue());
/*  44 */     if (type.equals(Long.class))
/*  45 */       return Long.toString(((Long)value).longValue());
/*  46 */     if (type.equals(Long.TYPE))
/*  47 */       return Long.toString(((Long)value).longValue());
/*  48 */     if (type.equals(Double.class))
/*  49 */       return Double.toString(((Double)value).doubleValue());
/*  50 */     if (type.equals(Double.TYPE))
/*  51 */       return Double.toString(((Double)value).doubleValue());
/*  52 */     if ((value instanceof Date)) {
/*  53 */       long time = ((Date)value).getTime();
/*  54 */       return Long.toString(time);
/*  55 */     }if ((value instanceof Calendar)) {
/*  56 */       long time = ((Calendar)value).getTimeInMillis();
/*  57 */       return Long.toString(time);
/*  58 */     }if ((value instanceof Period))
/*  59 */       return ((Period)value).name();
/*  60 */     if ((value instanceof Enum)) {
/*  61 */       return ((Enum)value).name();
/*     */     }
/*  63 */     LOGGER.error("Unsupported type : {}", type);
/*  64 */     return null;
/*     */   }
/*     */ 
/*     */   public static Object stringToValue(String string, Class<?> type)
/*     */   {
/*  69 */     if (type.equals(String.class)) {
/*  70 */       return string;
/*     */     }
/*     */ 
/*  73 */     if ((string == null) || (string.length() < 1)) {
/*  74 */       return null;
/*     */     }
/*     */     try
/*     */     {
/*  78 */       if (type.equals(Boolean.TYPE))
/*  79 */         return Boolean.valueOf(Boolean.parseBoolean(string));
/*  80 */       if (type.equals(Boolean.class))
/*  81 */         return Boolean.valueOf(string);
/*  82 */       if (type.equals(Integer.class))
/*  83 */         return Integer.valueOf(string);
/*  84 */       if (type.equals(Integer.TYPE))
/*  85 */         return Integer.valueOf(Integer.valueOf(string).intValue());
/*  86 */       if (type.equals(Long.class))
/*  87 */         return Long.valueOf(string);
/*  88 */       if (type.equals(Long.TYPE))
/*  89 */         return Long.valueOf(Long.valueOf(string).longValue());
/*  90 */       if (type.equals(Double.class))
/*  91 */         return Double.valueOf(string);
/*  92 */       if (type.equals(Double.TYPE))
/*  93 */         return Double.valueOf(Double.valueOf(string).doubleValue());
/*  94 */       if (type.equals(Date.class)) {
/*  95 */         long millis = Long.valueOf(string).longValue();
/*  96 */         return new Date(millis);
/*  97 */       }if (Calendar.class.isAssignableFrom(type)) {
/*  98 */         long millis = Long.valueOf(string).longValue();
/*  99 */         Calendar value = Calendar.getInstance();
/* 100 */         value.setTimeInMillis(millis);
/* 101 */         return value;
/* 102 */       }if (type.equals(Period.class))
/* 103 */         return Period.valueOf(string);
/* 104 */       if (type.equals(Instrument.class))
/* 105 */         return Instrument.valueOf(string);
/* 106 */       if (type.equals(OfferSide.class))
/* 107 */         return OfferSide.valueOf(string);
/* 108 */       if (type.equals(Filter.class))
/* 109 */         return Filter.valueOf(string);
/* 110 */       if (type.equals(Unit.class))
/* 111 */         return Unit.valueOf(string);
/* 112 */       if (type.equals(DataType.class))
/* 113 */         return DataType.valueOf(string);
/* 114 */       if (type.equals(IIndicators.AppliedPrice.class))
/* 115 */         return IIndicators.AppliedPrice.valueOf(string);
/* 116 */       if (type.equals(IIndicators.MaType.class)) {
/* 117 */         return IIndicators.MaType.valueOf(string);
/*     */       }
/* 119 */       LOGGER.error("Unsupported type : {}", type);
/* 120 */       return null;
/*     */     }
/*     */     catch (RuntimeException e) {
/* 123 */       LOGGER.error("Invalid value : " + string, e);
/* 124 */     }return null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.TransportHelper
 * JD-Core Version:    0.6.0
 */