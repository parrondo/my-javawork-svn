/*     */ package com.dukascopy.api.impl;
/*     */ 
/*     */ import com.dukascopy.api.Configurable;
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StrategyRunParameter;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.params.Variable;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*     */ import java.lang.reflect.Field;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.MessageFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public final class StrategyMessages
/*     */ {
/*  32 */   private static final Logger LOGGER = LoggerFactory.getLogger(JForexTaskManager.class);
/*     */   private static final String PARAMS_SEPARATOR = ", ";
/*     */   private static final String UNKNOWN_VALUE = "?";
/*     */   private static final String WITH_PARAMETERS = " with parameters ";
/*     */   private static final String WITH_NO_PARAMETERS = " with no parameters";
/*     */   private static final String ON_THE_LOCAL_COMPUTER = " on the local computer";
/*     */   private static final String ON_THE_REMOTE_SERVER = " on the remote server";
/*  40 */   private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'");
/*     */   private static final DecimalFormat DECIMAL_FORMATTER;
/*     */ 
/*     */   public static void startingStrategy(boolean isRemoteRun, StrategyWrapper wrapper)
/*     */   {
/*  53 */     printStartingStrategy(getStrategyName(wrapper), isRemoteRun);
/*     */   }
/*     */ 
/*     */   public static void startingStrategy(IStrategy strategy)
/*     */   {
/*  60 */     printStartingStrategy(getStrategyName(strategy), false);
/*     */   }
/*     */ 
/*     */   private static void printStartingStrategy(String strategyName, boolean isRemoteRun)
/*     */   {
/*  72 */     StringBuilder str = new StringBuilder();
/*  73 */     str.append("Starting \"").append(strategyName).append("\" strategy at ").append(DATE_FORMATTER.format(new Date()));
/*     */ 
/*  78 */     if (isRemoteRun)
/*  79 */       str.append(" on the remote server");
/*     */     else {
/*  81 */       str.append(" on the local computer");
/*     */     }
/*     */ 
/*  84 */     NotificationUtilsProvider.getNotificationUtils().postInfoMessage(str.toString(), false);
/*     */   }
/*     */ 
/*     */   public static void strategyIsStarted(boolean isRemoteRun, String strategyName, List<StrategyRunParameter> params) {
/*  88 */     printStrategyIsStarted(strategyName, getParameters(params), isRemoteRun, null);
/*     */   }
/*     */ 
/*     */   public static void strategyIsStarted(String strategyName, List<StrategyRunParameter> params, boolean isRemoteRun, Date date) {
/*  92 */     printStrategyIsStarted(strategyName, getParameters(params), isRemoteRun, date);
/*     */   }
/*     */ 
/*     */   public static void strategyIsStarted(IStrategy strategy) {
/*  96 */     printStrategyIsStarted(getStrategyName(strategy), getParameters(strategy), false, null);
/*     */   }
/*     */ 
/*     */   private static void printStrategyIsStarted(String strategyName, String parameters, boolean isRemoteRun, Date date)
/*     */   {
/* 105 */     if (date == null) {
/* 106 */       date = new Date();
/*     */     }
/*     */ 
/* 109 */     StringBuilder message = new StringBuilder();
/* 110 */     message.append("Strategy \"").append(strategyName).append("\" is started at ").append(DATE_FORMATTER.format(date));
/*     */ 
/* 115 */     if (isRemoteRun)
/* 116 */       message.append(" on the remote server");
/*     */     else {
/* 118 */       message.append(" on the local computer");
/*     */     }
/*     */ 
/* 121 */     if (parameters.length() > 0)
/* 122 */       message.append(" with parameters ").append(parameters);
/*     */     else {
/* 124 */       message.append(" with no parameters");
/*     */     }
/*     */ 
/* 127 */     NotificationUtilsProvider.getNotificationUtils().postInfoMessage(message.toString(), false);
/*     */   }
/*     */ 
/*     */   public static void strategyIsModified(boolean isRemoteRun, StrategyWrapper wrapper, List<StrategyRunParameter> params) {
/* 131 */     printStrategyIsModified(getStrategyName(wrapper), getParameters(params), isRemoteRun);
/*     */   }
/*     */ 
/*     */   public static void strategyIsModified(boolean isRemoteRun, String strategyName, IStrategy strategy) {
/* 135 */     printStrategyIsModified(strategyName, getParameters(strategy), isRemoteRun);
/*     */   }
/*     */ 
/*     */   public static void strategyIsModified(IStrategy strategy) {
/* 139 */     printStrategyIsModified(getStrategyName(strategy), getParameters(strategy), false);
/*     */   }
/*     */ 
/*     */   private static void printStrategyIsModified(String strategyName, String parameters, boolean isRemoteRun)
/*     */   {
/* 148 */     StringBuilder message = new StringBuilder();
/* 149 */     message.append("Strategy \"").append(strategyName).append("\" is modified at ").append(DATE_FORMATTER.format(new Date()));
/*     */ 
/* 154 */     if (isRemoteRun)
/* 155 */       message.append(" on the remote server");
/*     */     else {
/* 157 */       message.append(" on the local computer");
/*     */     }
/*     */ 
/* 160 */     if (parameters.length() > 0)
/* 161 */       message.append(" with parameters ").append(parameters);
/*     */     else {
/* 163 */       message.append(" with no parameters");
/*     */     }
/*     */ 
/* 166 */     NotificationUtilsProvider.getNotificationUtils().postInfoMessage(message.toString(), false);
/*     */   }
/*     */ 
/*     */   public static void stoppingStrategy(boolean isRemoteRun, StrategyWrapper wrapper) {
/* 170 */     printStoppingStrategy(getStrategyName(wrapper), isRemoteRun);
/*     */   }
/*     */ 
/*     */   public static void stoppingStrategy(boolean isRemoteRun, String strategyName) {
/* 174 */     printStoppingStrategy(strategyName, isRemoteRun);
/*     */   }
/*     */ 
/*     */   public static void stoppingStrategy(IStrategy strategy) {
/* 178 */     printStoppingStrategy(getStrategyName(strategy), false);
/*     */   }
/*     */ 
/*     */   private static void printStoppingStrategy(String strategyName, boolean isRemoteRun)
/*     */   {
/* 188 */     StringBuilder message = new StringBuilder();
/* 189 */     message.append("Stopping \"").append(strategyName).append("\" strategy at ").append(DATE_FORMATTER.format(new Date()));
/*     */ 
/* 194 */     if (isRemoteRun)
/* 195 */       message.append(" on the remote server");
/*     */     else {
/* 197 */       message.append(" on the local computer");
/*     */     }
/*     */ 
/* 200 */     NotificationUtilsProvider.getNotificationUtils().postInfoMessage(message.toString(), false);
/*     */   }
/*     */ 
/*     */   public static void strategyIsStopped(boolean isRemoteRun, String strategyName, List<StrategyRunParameter> params) {
/* 204 */     printStrategyIsStopped(strategyName, getParameters(params), isRemoteRun, null);
/*     */   }
/*     */ 
/*     */   public static void strategyIsStopped(String strategyName, List<StrategyRunParameter> params, boolean isRemoteRun, Date date) {
/* 208 */     printStrategyIsStopped(strategyName, getParameters(params), isRemoteRun, date);
/*     */   }
/*     */ 
/*     */   public static void strategyIsStopped(IStrategy strategy) {
/* 212 */     printStrategyIsStopped(getStrategyName(strategy), getParameters(strategy), false, null);
/*     */   }
/*     */ 
/*     */   private static void printStrategyIsStopped(String strategyName, String parameters, boolean isRemoteRun, Date date)
/*     */   {
/* 222 */     if (date == null) {
/* 223 */       date = new Date();
/*     */     }
/*     */ 
/* 226 */     StringBuilder message = new StringBuilder();
/* 227 */     message.append("Strategy \"").append(strategyName).append("\" is stopped at ").append(DATE_FORMATTER.format(new Date()));
/*     */ 
/* 232 */     if (isRemoteRun)
/* 233 */       message.append(" on the remote server");
/*     */     else {
/* 235 */       message.append(" on the local computer");
/*     */     }
/*     */ 
/* 238 */     if (parameters.length() > 0)
/* 239 */       message.append(" with parameters ").append(parameters);
/*     */     else {
/* 241 */       message.append(" with no parameters");
/*     */     }
/*     */ 
/* 244 */     NotificationUtilsProvider.getNotificationUtils().postInfoMessage(message.toString(), false);
/*     */   }
/*     */ 
/*     */   private static String getParameters(List<StrategyRunParameter> parameters) {
/* 248 */     StringBuilder result = new StringBuilder();
/* 249 */     if (parameters != null) {
/* 250 */       for (StrategyRunParameter strategyRunParameter : parameters) {
/* 251 */         String name = strategyRunParameter.getTitle();
/* 252 */         Variable var = strategyRunParameter.getVariable();
/* 253 */         boolean dateAsLong = strategyRunParameter.isDateAsLong();
/*     */ 
/* 255 */         appendNameValue(result, name, var.getType(), var.getValue(), dateAsLong);
/*     */       }
/*     */     }
/* 258 */     if (result.toString().endsWith(", ")) {
/* 259 */       result.setLength(result.length() - ", ".length());
/*     */     }
/* 261 */     return result.toString();
/*     */   }
/*     */ 
/*     */   private static String getParameters(IStrategy strategy)
/*     */   {
/* 269 */     StringBuilder result = new StringBuilder();
/*     */ 
/* 271 */     Field[] fields = strategy.getClass().getFields();
/* 272 */     for (Field field : fields) {
/* 273 */       Configurable configurable = (Configurable)field.getAnnotation(Configurable.class);
/* 274 */       if (configurable != null) { String uiName = configurable.value();
/* 276 */         Class type = field.getType();
/*     */         Object value;
/*     */         try { value = field.get(strategy);
/*     */         } catch (Exception ex) {
/* 281 */           String message = MessageFormat.format("Error getting value from field {0}.", new Object[] { uiName });
/* 282 */           LOGGER.debug(message, ex);
/* 283 */           value = null;
/*     */         }
/* 285 */         appendNameValue(result, uiName, type, value, configurable.datetimeAsLong());
/*     */       }
/*     */     }
/* 288 */     if (result.toString().endsWith(", ")) {
/* 289 */       result.setLength(result.length() - ", ".length());
/*     */     }
/* 291 */     return result.toString();
/*     */   }
/*     */   private static void appendNameValue(StringBuilder builder, String uiName, Class<?> type, Object value, boolean dateAsLong) {
/*     */     String valueAsString;
/*     */     try {
/* 297 */       valueAsString = valueAsString(type, value, dateAsLong);
/*     */     } catch (RuntimeException ex) {
/* 299 */       String message = MessageFormat.format("Error getting value from field {0}.", new Object[] { uiName });
/* 300 */       LOGGER.debug(message, ex);
/* 301 */       valueAsString = "?";
/*     */     }
/* 303 */     builder.append("\"").append(uiName).append("\"=[").append(valueAsString).append("]");
/* 304 */     builder.append(", ");
/*     */   }
/*     */ 
/*     */   private static String valueAsString(Class<?> fieldType, Object value, boolean datetimeAsLong)
/*     */     throws RuntimeException
/*     */   {
/* 311 */     if (value == null)
/* 312 */       return "null";
/* 313 */     if (((Long.TYPE.equals(fieldType)) || (Long.class.equals(fieldType))) && (datetimeAsLong))
/* 314 */       return DATE_FORMATTER.format(new Date(((Long)value).longValue()));
/* 315 */     if (Calendar.class.isAssignableFrom(fieldType))
/* 316 */       return DATE_FORMATTER.format(((Calendar)value).getTime());
/* 317 */     if (Date.class.isAssignableFrom(fieldType))
/* 318 */       return DATE_FORMATTER.format((Date)value);
/* 319 */     if ((Double.TYPE.equals(fieldType)) || (Double.class.equals(fieldType))) {
/* 320 */       return DECIMAL_FORMATTER.format(value);
/*     */     }
/* 322 */     return value.toString();
/*     */   }
/*     */ 
/*     */   private static String getStrategyName(IStrategy strategy)
/*     */   {
/* 327 */     return strategy.getClass().getSimpleName();
/*     */   }
/*     */ 
/*     */   private static String getStrategyName(StrategyWrapper wrapper) {
/* 331 */     String name = wrapper.getName();
/* 332 */     int index = name.lastIndexOf(".");
/* 333 */     if (index > 0) {
/* 334 */       return name.substring(0, index);
/*     */     }
/* 336 */     return name;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  41 */     DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  42 */     DECIMAL_FORMATTER = new DecimalFormat("0.0####");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.StrategyMessages
 * JD-Core Version:    0.6.0
 */