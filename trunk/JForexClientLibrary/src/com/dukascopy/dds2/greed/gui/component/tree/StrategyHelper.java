/*     */ package com.dukascopy.dds2.greed.gui.component.tree;
/*     */ 
/*     */ import com.dukascopy.api.Configurable;
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.IIndicators.AppliedPrice;
/*     */ import com.dukascopy.api.IIndicators.MaType;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.Unit;
/*     */ import com.dukascopy.api.impl.StrategyWrapper;
/*     */ import com.dukascopy.api.impl.connect.AuthorizationClient;
/*     */ import com.dukascopy.api.impl.execution.Task;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.ICurvesProtocolHandler;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ParametersDialog;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StrategyParameters;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StrategyRunParameter;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.params.Variable;
/*     */ import com.dukascopy.dds2.greed.gui.component.filechooser.FileProgressListener;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.IOUtils;
/*     */ import com.dukascopy.transport.common.datafeed.StorageException;
/*     */ import com.dukascopy.transport.common.msg.strategy.FileItem;
/*     */ import com.dukascopy.transport.common.msg.strategy.StrategyParameter;
/*     */ import com.dukascopy.transport.common.msg.strategy.StrategyRunChunkRequestMessage;
/*     */ import java.awt.Frame;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.lang.reflect.Field;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public final class StrategyHelper
/*     */ {
/*  52 */   private static final Logger LOGGER = LoggerFactory.getLogger(StrategyHelper.class);
/*  53 */   private static final Class<?>[] REMOTELY_SUPPORTED_TYPES = { String.class, Integer.TYPE, Integer.class, Long.TYPE, Long.class, Double.TYPE, Double.class, Boolean.TYPE, Boolean.class, java.util.Date.class, java.sql.Date.class, Calendar.class, Period.class, Instrument.class, OfferSide.class, Filter.class, Unit.class, DataType.class, IIndicators.AppliedPrice.class, IIndicators.MaType.class };
/*     */ 
/*     */   public static boolean downloadStrategy(RemoteStrategyWrapper wrapper, File strategyFile)
/*     */   {
/*     */     try
/*     */     {
/* 105 */       FileItem response = FeedDataProvider.getCurvesProtocolHandler().downloadFile(wrapper.getRemoteStrategyId().longValue(), new FileProgressListener());
/* 106 */       if (response != null) {
/* 107 */         byte[] data = response.getFileData();
/* 108 */         IOUtils.writeByteArrayToFile(strategyFile, data);
/* 109 */         setStrategyFile(wrapper, strategyFile);
/* 110 */         return true;
/*     */       }
/* 112 */       return false;
/*     */     }
/*     */     catch (IOException e) {
/* 115 */       LOGGER.error("Error saving remote stategy data to local file : " + strategyFile.getAbsolutePath(), e);
/* 116 */       return false;
/*     */     } catch (StorageException e) {
/* 118 */       LOGGER.error("Error retrieving remote data for strategy : " + wrapper.getName(), e);
/* 119 */     }return false;
/*     */   }
/*     */ 
/*     */   public static List<StrategyRunParameter> downloadParameters(RemoteStrategyWrapper wrapper)
/*     */   {
/*     */     try
/*     */     {
/* 128 */       List params = FeedDataProvider.getCurvesProtocolHandler().listStrategyParameters(wrapper.getRemoteStrategyId().longValue(), new FileProgressListener());
/* 129 */       List parameters = convert(params);
/* 130 */       return parameters;
/*     */     } catch (StorageException e) {
/* 132 */       LOGGER.error("Error retrieving parameters for strategy : " + wrapper.getName(), e);
/* 133 */     }return null;
/*     */   }
/*     */ 
/*     */   public static boolean isRemotelySupported(Class<?> fieldType)
/*     */   {
/* 144 */     for (Class supported : REMOTELY_SUPPORTED_TYPES) {
/* 145 */       if (supported.equals(fieldType)) {
/* 146 */         return true;
/*     */       }
/*     */     }
/* 149 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean isRemotelySupported(StrategyWrapper wrapper, INotificationUtils notification)
/*     */   {
/* 160 */     StringBuffer reason = new StringBuffer();
/*     */     try {
/* 162 */       if (wrapper.isFullAccessRequested()) {
/* 163 */         reason.append("Strategy requires full access");
/*     */       }
/*     */ 
/* 166 */       Field[] fields = wrapper.getStrategy().getClass().getFields();
/* 167 */       for (Field field : fields) {
/* 168 */         Configurable configurable = (Configurable)field.getAnnotation(Configurable.class);
/* 169 */         if (configurable == null)
/*     */           continue;
/* 171 */         if (!isRemotelySupported(field.getType())) {
/* 172 */           if (reason.length() > 0) {
/* 173 */             reason.append("; ");
/*     */           }
/* 175 */           reason.append("Parameter type is not supported : " + field.getType().getName());
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception ex) {
/* 180 */       LOGGER.error("Error checking strategy", ex);
/* 181 */       return false;
/*     */     }
/*     */ 
/* 184 */     if (reason.length() > 0) {
/* 185 */       if (notification != null) {
/* 186 */         notification.postErrorMessage(MessageFormat.format("Unable to run strategy remotely : {0}", new Object[] { reason }));
/*     */       }
/*     */ 
/* 190 */       return false;
/*     */     }
/* 192 */     return true;
/*     */   }
/*     */ 
/*     */   public static List<StrategyRunParameter> showParametersDialog(StrategyWrapper wrapper)
/*     */   {
/* 201 */     HashMap params = StrategyParameters.getParameters(wrapper.getName());
/* 202 */     Frame frame = (Frame)GreedContext.get("clientGui");
/*     */     try
/*     */     {
/* 205 */       ParametersDialog dialog = new ParametersDialog(frame, wrapper.getStrategy(), false, wrapper.getBinaryFile());
/* 206 */       Task rc = dialog.showParam(params);
/* 207 */       if (rc == null)
/*     */       {
/* 209 */         return null;
/*     */       }
/*     */ 
/* 212 */       Boolean canProceed = (Boolean)rc.call();
/* 213 */       if ((canProceed != null) && (canProceed.booleanValue()))
/*     */       {
/* 215 */         return dialog.getParameters();
/*     */       }
/*     */ 
/* 218 */       return null;
/*     */     }
/*     */     catch (Exception ex) {
/* 221 */       LOGGER.error("Error showing parameters dialog.", ex);
/* 222 */     }return null;
/*     */   }
/*     */ 
/*     */   public static void setStrategyFile(StrategyWrapper wrapper, File file)
/*     */   {
/* 227 */     if (file.getName().endsWith(".java"))
/* 228 */       wrapper.setSourceFile(file);
/* 229 */     else if (file.getName().endsWith(".jfx"))
/* 230 */       wrapper.setBinaryFile(file);
/* 231 */     else if (file.getName().endsWith(".mq4"))
/* 232 */       wrapper.setSourceFile(file);
/* 233 */     else if (file.getName().endsWith(".mq5"))
/* 234 */       wrapper.setSourceFile(file);
/*     */   }
/*     */ 
/*     */   private static Class<?> stringToType(String type)
/*     */   {
/* 239 */     for (Class remotelySupported : REMOTELY_SUPPORTED_TYPES) {
/* 240 */       if (remotelySupported.getName().equals(type)) {
/* 241 */         return remotelySupported;
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 246 */       return Class.forName(type);
/*     */     } catch (ClassNotFoundException e) {
/* 248 */       LOGGER.debug("Unsupported type: " + type, e);
/* 249 */     }return null;
/*     */   }
/*     */ 
/*     */   public static File createTempFile(String strategyFileName)
/*     */     throws IOException
/*     */   {
/* 256 */     File directory = File.createTempFile("jforex-strategy-", ".dir");
/* 257 */     directory.delete();
/* 258 */     directory.mkdir();
/*     */ 
/* 260 */     return new File(directory, strategyFileName);
/*     */   }
/*     */ 
/*     */   public static Collection<StrategyParameter> convert(List<StrategyRunParameter> params)
/*     */   {
/* 269 */     Collection result = new LinkedList();
/* 270 */     if (params != null) {
/* 271 */       for (StrategyRunParameter parameter : params) {
/* 272 */         Variable variable = parameter.getVariable();
/* 273 */         Object value = variable.getValue();
/* 274 */         if (value == null)
/*     */         {
/*     */           continue;
/*     */         }
/* 278 */         String valueAsString = TransportHelper.valueToString(value, variable.getType());
/* 279 */         if (valueAsString != null) {
/* 280 */           StrategyParameter strategyParameter = new StrategyParameter();
/* 281 */           strategyParameter.setName(parameter.getName());
/* 282 */           strategyParameter.setDescription(parameter.getDescription());
/* 283 */           strategyParameter.setType(variable.getType());
/* 284 */           strategyParameter.setValue(valueAsString);
/* 285 */           strategyParameter.setTitle(parameter.getTitle());
/* 286 */           strategyParameter.setObligatory(parameter.isMandatory());
/* 287 */           strategyParameter.setReadOnly(parameter.isReadOnly());
/* 288 */           strategyParameter.setDateTimeAsLong(parameter.isDateAsLong());
/* 289 */           strategyParameter.setStepSize(parameter.getStepSize());
/* 290 */           result.add(strategyParameter);
/*     */         }
/*     */       }
/*     */     }
/* 294 */     return result;
/*     */   }
/*     */ 
/*     */   public static List<StrategyRunParameter> convert(Collection<StrategyParameter> params)
/*     */   {
/* 303 */     List result = new LinkedList();
/* 304 */     if (params != null) {
/* 305 */       for (StrategyParameter param : params) {
/* 306 */         Class type = stringToType(param.getType());
/* 307 */         if (type != null) {
/* 308 */           Object value = TransportHelper.stringToValue(param.getValue(), type);
/*     */ 
/* 310 */           StrategyRunParameter parameter = new StrategyRunParameter(param.getName(), type, value);
/* 311 */           parameter.setTitle(param.getTitle());
/* 312 */           parameter.setMandatory(param.isObligatory());
/* 313 */           parameter.setDateAsLong(param.isDateTimeAsLong());
/* 314 */           parameter.setStepSize(param.getStepSize());
/* 315 */           result.add(parameter);
/*     */         }
/*     */       }
/*     */     }
/* 319 */     return result;
/*     */   }
/*     */ 
/*     */   public static StrategyRunChunkRequestMessage createRunRequest(StrategyWrapper wrapper, String strategyName, List<StrategyRunParameter> params, Collection<Instrument> instruments)
/*     */     throws NoSuchAlgorithmException, UnsupportedEncodingException, IOException
/*     */   {
/* 330 */     String password = (String)GreedContext.getConfig(" ");
/* 331 */     String accountName = (String)GreedContext.getConfig("account_name");
/* 332 */     String passwordDigest = AuthorizationClient.encodeString(AuthorizationClient.encodeString(password) + accountName);
/* 333 */     Collection parameters = convert(params);
/*     */ 
/* 335 */     StrategyRunChunkRequestMessage request = new StrategyRunChunkRequestMessage();
/* 336 */     if (wrapper.isRemote()) {
/* 337 */       Long id = ((RemoteStrategyWrapper)wrapper).getRemoteStrategyId();
/* 338 */       request.setFileId(id);
/*     */     } else {
/* 340 */       byte[] bytes = getBytes(wrapper.getBinaryFile());
/* 341 */       request.setFileContent(bytes);
/*     */     }
/* 343 */     request.setFileName(strategyName);
/* 344 */     request.setPasswordDigest(passwordDigest);
/* 345 */     request.setParameters(parameters);
/*     */ 
/* 347 */     if ((instruments != null) && (instruments.size() > 0)) {
/* 348 */       Collection instrumentList = new LinkedList();
/* 349 */       for (Instrument instrument : instruments) {
/* 350 */         instrumentList.add(instrument.toString());
/*     */       }
/* 352 */       request.setInstruments(instrumentList);
/*     */     }
/* 354 */     return request;
/*     */   }
/*     */ 
/*     */   private static byte[] getBytes(File file) throws IOException {
/* 358 */     ByteArrayOutputStream os = new ByteArrayOutputStream();
/*     */     try {
/* 360 */       FileInputStream fis = new FileInputStream(file);
/*     */       try {
/* 362 */         byte[] buffer = new byte[2048];
/*     */         while (true) {
/* 364 */           read = fis.read(buffer);
/* 365 */           if (read <= 0) {
/*     */             break;
/*     */           }
/* 368 */           os.write(buffer, 0, read);
/*     */         }
/*     */ 
/* 372 */         int read = os.toByteArray();
/*     */ 
/* 374 */         fis.close();
/*     */         return read;
/*     */       }
/*     */       finally
/*     */       {
/* 374 */         fis.close();
/*     */       }
/*     */     } finally {
/* 377 */       os.close(); } throw localObject2;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.StrategyHelper
 * JD-Core Version:    0.6.0
 */