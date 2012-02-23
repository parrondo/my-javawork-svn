/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator;
/*     */ 
/*     */ import com.dukascopy.api.Configurable;
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.IIndicators.AppliedPrice;
/*     */ import com.dukascopy.api.IIndicators.MaType;
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.Unit;
/*     */ import com.dukascopy.api.impl.connect.AuthorizationClient;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.compiler.JFXPack;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.StrategyParameterLocal;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.StrategyPreset;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.util.CollectionParameterWrapper;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.TransportHelper;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.transport.common.msg.strategy.StrategyParameter;
/*     */ import com.dukascopy.transport.common.msg.strategy.StrategyRunChunkRequestMessage;
/*     */ import com.dukascopy.transport.common.msg.strategy.StrategyRunRequestMessage;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Field;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Arrays;
/*     */ import java.util.Calendar;
/*     */ import java.util.Collection;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import java.util.UUID;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public final class RemoteStrategiesUtil
/*     */ {
/*  53 */   private static final Logger LOGGER = LoggerFactory.getLogger(RemoteStrategiesUtil.class);
/*     */   private static final int MAX_CONTENT_SIZE = 14336;
/*     */   private static final int MAX_STRATEGY_SIZE = 10485760;
/*  60 */   private static final Class<?>[] REMOTELY_SUPPORTED_TYPES = { String.class, Integer.TYPE, Integer.class, Long.TYPE, Long.class, Double.TYPE, Double.class, Boolean.TYPE, Boolean.class, java.util.Date.class, java.sql.Date.class, Calendar.class, Period.class, Instrument.class, OfferSide.class, Filter.class, Unit.class, DataType.class, IIndicators.AppliedPrice.class, IIndicators.MaType.class };
/*     */ 
/*     */   public static boolean isRemotelySupported(StrategyNewBean strategy, INotificationUtils notification)
/*     */   {
/*  83 */     boolean notSupported = false;
/*  84 */     StringBuffer reason = new StringBuffer();
/*     */ 
/*  86 */     if ((strategy.getPack() != null) && (strategy.getPack().isFullAccessRequested())) {
/*  87 */       reason.append("Strategy requires full access");
/*  88 */       notSupported = true;
/*     */     } else {
/*  90 */       Field[] fields = strategy.getStrategy().getClass().getFields();
/*  91 */       for (Field field : fields) {
/*  92 */         Configurable configurable = (Configurable)field.getAnnotation(Configurable.class);
/*  93 */         if ((configurable == null) || 
/*  94 */           (isRemotelySupported(field.getType()))) continue;
/*  95 */         if (reason.length() > 0) {
/*  96 */           reason.append("; ");
/*     */         }
/*  98 */         reason.append("Parameter type is not supported : " + field.getType().getName());
/*  99 */         notSupported = true;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 105 */     if ((reason.length() > 0) && 
/* 106 */       (notification != null)) {
/* 107 */       notification.postErrorMessage(MessageFormat.format("Unable to run strategy remotely : {0}", new Object[] { reason }));
/*     */     }
/*     */ 
/* 113 */     return !notSupported;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static StrategyRunRequestMessage createRunRequest(StrategyNewBean strategy, Collection<Instrument> instruments) throws NoSuchAlgorithmException, UnsupportedEncodingException, IOException {
/* 119 */     String password = (String)GreedContext.getConfig(" ");
/* 120 */     String accountName = (String)GreedContext.getConfig("account_name");
/* 121 */     String passwordDigest = AuthorizationClient.encodeString(AuthorizationClient.encodeString(password) + accountName);
/* 122 */     Collection parameters = getConvertedParams(strategy);
/*     */ 
/* 124 */     StrategyRunRequestMessage request = new StrategyRunRequestMessage();
/*     */ 
/* 126 */     byte[] bytes = getBytes(strategy.getStrategyBinaryFile());
/* 127 */     request.setFileContent(bytes);
/*     */ 
/* 129 */     request.setFileName(strategy.getName());
/* 130 */     request.setPasswordDigest(passwordDigest);
/* 131 */     request.setParameters(parameters);
/*     */ 
/* 133 */     if ((instruments != null) && (instruments.size() > 0)) {
/* 134 */       Collection instrumentList = new LinkedList();
/* 135 */       for (Instrument instrument : instruments) {
/* 136 */         instrumentList.add(instrument.toString());
/*     */       }
/* 138 */       request.setInstruments(instrumentList);
/*     */     }
/* 140 */     return request;
/*     */   }
/*     */ 
/*     */   public static Set<StrategyRunChunkRequestMessage> createRunRequests(StrategyNewBean strategy, Collection<Instrument> instruments)
/*     */     throws NoSuchAlgorithmException, UnsupportedEncodingException, IOException
/*     */   {
/* 146 */     byte[] bytes = getBytes(strategy.getStrategyBinaryFile());
/* 147 */     int contentLength = bytes.length;
/* 148 */     if (contentLength > 10485760) {
/* 149 */       LOGGER.error("Max strategy size exceeded : {}/{}", Integer.valueOf(contentLength), Integer.valueOf(10485760));
/* 150 */       throw new IllegalArgumentException("Max strategy size exceeded");
/*     */     }
/*     */ 
/* 153 */     TreeSet result = new TreeSet();
/*     */ 
/* 155 */     int messageOrder = 0;
/* 156 */     String requestId = UUID.randomUUID().toString();
/* 157 */     String password = (String)GreedContext.getConfig(" ");
/* 158 */     String accountName = (String)GreedContext.getConfig("account_name");
/* 159 */     String passwordDigest = AuthorizationClient.encodeString(AuthorizationClient.encodeString(password) + accountName);
/* 160 */     Collection parameters = getConvertedParams(strategy);
/*     */ 
/* 162 */     StrategyRunChunkRequestMessage request = prepareRunRequestMessage(requestId, messageOrder++, strategy.getName(), passwordDigest, parameters, instruments);
/* 163 */     result.add(request);
/* 164 */     int i = 0;
/* 165 */     while (i < contentLength) {
/* 166 */       int to = i + 14336 > contentLength ? contentLength : i + 14336;
/*     */ 
/* 168 */       byte[] chunk = Arrays.copyOfRange(bytes, i, to);
/* 169 */       if (request == null) {
/* 170 */         request = prepareRunRequestMessage(requestId, messageOrder++, strategy.getName(), passwordDigest, parameters, instruments);
/* 171 */         result.add(request);
/*     */       }
/* 173 */       request.setFileContent(chunk);
/* 174 */       request = null;
/* 175 */       i = to;
/*     */     }
/* 177 */     ((StrategyRunChunkRequestMessage)result.last()).setFinished(Boolean.valueOf(true));
/* 178 */     return result;
/*     */   }
/*     */ 
/*     */   public static byte[] addAll(byte[] array1, byte[] array2)
/*     */   {
/* 183 */     byte[] merged = (byte[])(byte[])Array.newInstance(Byte.TYPE, array1.length + array2.length);
/* 184 */     System.arraycopy(array1, 0, merged, 0, array1.length);
/* 185 */     System.arraycopy(array2, 0, merged, array1.length, array2.length);
/* 186 */     return merged;
/*     */   }
/*     */ 
/*     */   public static boolean isRemotelySupported(Class<?> fieldType) {
/* 190 */     for (Class supported : REMOTELY_SUPPORTED_TYPES) {
/* 191 */       if (supported.equals(fieldType)) {
/* 192 */         return true;
/*     */       }
/*     */     }
/* 195 */     return false;
/*     */   }
/*     */ 
/*     */   public static Collection<StrategyParameter> getConvertedParams(StrategyNewBean strategy) {
/* 199 */     Collection result = new LinkedList();
/*     */ 
/* 201 */     if (strategy.getActivePreset() != null) {
/* 202 */       for (StrategyParameterLocal parameter : strategy.getActivePreset().getStrategyParameters())
/*     */       {
/* 204 */         if (parameter.getValue() == null)
/*     */         {
/*     */           continue;
/*     */         }
/* 208 */         String valueAsString = TransportHelper.valueToString(parameter.getValue(), parameter.getType());
/* 209 */         if (valueAsString != null) {
/* 210 */           StrategyParameter strategyParameter = new StrategyParameter();
/* 211 */           strategyParameter.setName(parameter.getId());
/* 212 */           strategyParameter.setType(parameter.getType());
/* 213 */           strategyParameter.setValue(valueAsString);
/* 214 */           strategyParameter.setTitle(parameter.getName());
/* 215 */           strategyParameter.setDescription(parameter.getDescription());
/* 216 */           strategyParameter.setObligatory(parameter.isMandatory());
/* 217 */           strategyParameter.setReadOnly(parameter.isReadOnly());
/* 218 */           strategyParameter.setStepSize(parameter.getStepSize());
/* 219 */           result.add(strategyParameter);
/*     */         }
/*     */       }
/*     */     }
/* 223 */     return result;
/*     */   }
/*     */ 
/*     */   public static Collection<PropertyChangeEvent> getPropertyChangeEvents(StrategyNewBean strategyBean)
/*     */   {
/* 228 */     Collection result = new LinkedList();
/*     */     IStrategy strategy;
/* 230 */     if ((strategyBean.getActivePreset() != null) && (strategyBean.getStrategy() != null)) {
/* 231 */       strategy = strategyBean.getStrategy();
/*     */ 
/* 233 */       for (StrategyParameterLocal parameter : strategyBean.getActivePreset().getStrategyParameters())
/*     */       {
/* 235 */         if (parameter.getValue() == null) {
/*     */           continue;
/*     */         }
/*     */         try
/*     */         {
/* 240 */           Field field = strategy.getClass().getField(parameter.getId());
/* 241 */           if (field != null) {
/* 242 */             Configurable configurable = (Configurable)field.getAnnotation(Configurable.class);
/* 243 */             if ((configurable != null) && (!configurable.readOnly())) {
/* 244 */               Object oldValue = field.get(strategy);
/* 245 */               Object newValue = null;
/* 246 */               if (field.getType().equals(File.class))
/* 247 */                 newValue = new File(String.valueOf(parameter.getValue()));
/* 248 */               else if ((parameter.getValue() instanceof CollectionParameterWrapper))
/* 249 */                 newValue = ((CollectionParameterWrapper)parameter.getValue()).getSelectedValue();
/*     */               else {
/* 251 */                 newValue = parameter.getValue();
/*     */               }
/*     */ 
/* 254 */               result.add(new PropertyChangeEvent(strategy, field.getName(), oldValue, newValue));
/*     */ 
/* 256 */               result.add(new PropertyChangeEvent(strategy, configurable.value(), oldValue, newValue));
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Exception ex) {
/* 261 */           LOGGER.error(ex.getMessage(), ex);
/* 262 */         }continue;
/*     */       }
/*     */     }
/*     */ 
/* 266 */     return result;
/*     */   }
/*     */ 
/*     */   private static byte[] getBytes(File file)
/*     */     throws IOException
/*     */   {
/* 274 */     ByteArrayOutputStream os = new ByteArrayOutputStream();
/*     */     try {
/* 276 */       FileInputStream fis = new FileInputStream(file);
/*     */       try {
/* 278 */         byte[] buffer = new byte[2048];
/*     */         while (true) {
/* 280 */           read = fis.read(buffer);
/* 281 */           if (read <= 0) {
/*     */             break;
/*     */           }
/* 284 */           os.write(buffer, 0, read);
/*     */         }
/*     */ 
/* 288 */         int read = os.toByteArray();
/*     */ 
/* 290 */         fis.close();
/*     */         return read;
/*     */       }
/*     */       finally
/*     */       {
/* 290 */         fis.close();
/*     */       }
/*     */     } finally {
/* 293 */       os.close(); } throw localObject2;
/*     */   }
/*     */ 
/*     */   private static StrategyRunChunkRequestMessage prepareRunRequestMessage(String requestId, int messageOrder, String name, String passwordDigest, Collection<StrategyParameter> parameters, Collection<Instrument> instruments)
/*     */   {
/* 298 */     StrategyRunChunkRequestMessage request = new StrategyRunChunkRequestMessage();
/* 299 */     request.setRequestId(requestId);
/* 300 */     request.setMessageOrder(Integer.valueOf(messageOrder));
/* 301 */     request.setTimestamp(new java.util.Date());
/*     */ 
/* 303 */     request.setFileName(name);
/* 304 */     request.setPasswordDigest(passwordDigest);
/* 305 */     request.setParameters(parameters);
/*     */ 
/* 307 */     if ((instruments != null) && (instruments.size() > 0)) {
/* 308 */       Collection instrumentList = new LinkedList();
/* 309 */       for (Instrument instrument : instruments) {
/* 310 */         instrumentList.add(instrument.toString());
/*     */       }
/* 312 */       request.setInstruments(instrumentList);
/*     */     }
/* 314 */     return request;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.RemoteStrategiesUtil
 * JD-Core Version:    0.6.0
 */