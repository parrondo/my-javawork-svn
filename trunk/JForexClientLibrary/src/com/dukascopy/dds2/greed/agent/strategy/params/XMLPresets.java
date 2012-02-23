/*     */ package com.dukascopy.dds2.greed.agent.strategy.params;
/*     */ 
/*     */ import com.dukascopy.api.Configurable;
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.util.FilePathManager;
/*     */ import java.io.File;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.Calendar;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.SAXParseException;
/*     */ 
/*     */ public class XMLPresets
/*     */ {
/*  36 */   private static final Logger LOGGER = LoggerFactory.getLogger(XMLPresets.class);
/*     */   private static final String ENUM_PREFIX = "enum#";
/*     */   private static final String NULL = "NULL";
/*     */ 
/*     */   public static PresetsModel loadModel(IStrategy strategy, File binaryFile)
/*     */   {
/*  43 */     PresetsModel strategyConfig = readDefaultPresetFromTarget(strategy);
/*  44 */     if (strategyConfig == null) {
/*  45 */       return null;
/*     */     }
/*     */ 
/*  50 */     String fileName = strategy.getClass().getSimpleName();
/*  51 */     File strategyDir = FilePathManager.getInstance().getStrategiesFolder();
/*     */ 
/*  53 */     if (binaryFile != null) {
/*  54 */       strategyDir = binaryFile.getParentFile();
/*     */     }
/*     */ 
/*  57 */     File xmlFile = new File(strategyDir.getPath() + File.separator + fileName + ".xml");
/*     */ 
/*  59 */     if (!xmlFile.exists()) {
/*  60 */       return strategyConfig;
/*     */     }
/*     */ 
/*  64 */     PresetsFormatHelper helper = new PresetsFormatHelper();
/*  65 */     PresetsModel result = helper.loadPresetsFromNewFormat(strategyConfig, xmlFile);
/*  66 */     if (result != null) {
/*  67 */       return result;
/*     */     }
/*     */     try
/*     */     {
/*  71 */       DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
/*  72 */       DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
/*  73 */       Document doc = docBuilder.parse(xmlFile);
/*  74 */       if (doc == null) {
/*  75 */         return strategyConfig;
/*     */       }
/*     */ 
/*  78 */       doc.getDocumentElement().normalize();
/*  79 */       return readModel(doc, strategyConfig, strategy);
/*     */     } catch (SAXParseException err) {
/*  81 */       return strategyConfig;
/*     */     } catch (SAXException e) {
/*  83 */       Exception x = e.getException();
/*  84 */       x = x == null ? e : x;
/*  85 */       LOGGER.error(x.getMessage(), x);
/*  86 */       return strategyConfig;
/*     */     } catch (Throwable t) {
/*  88 */       LOGGER.error(t.getMessage(), t);
/*  89 */     }return strategyConfig;
/*     */   }
/*     */ 
/*     */   private static PresetsModel readDefaultPresetFromTarget(IStrategy target)
/*     */   {
/*  94 */     Preset preset = new Preset("Default", Preset.FILE_LOADED);
/*  95 */     HashMap varMap = new HashMap();
/*  96 */     Field[] allFields = target.getClass().getFields();
/*     */ 
/*  98 */     for (Field field : allFields) {
/*  99 */       Configurable config = (Configurable)field.getAnnotation(Configurable.class);
/* 100 */       if (config == null)
/*     */         continue;
/*     */       try {
/* 103 */         if (isParamTypeAcceptable(field.getType())) {
/* 104 */           Variable variable = new Variable(field.get(target), field.getType());
/* 105 */           varMap.put(field.getName(), variable);
/*     */         }
/*     */       } catch (IllegalAccessException iae) {
/* 108 */         LOGGER.error(iae.getMessage(), iae);
/* 109 */         return null;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 114 */     if (varMap.size() == 0) {
/* 115 */       return null;
/*     */     }
/*     */ 
/* 118 */     return new PresetsModel(preset, varMap);
/*     */   }
/*     */ 
/*     */   private static PresetsModel readModel(Document doc, PresetsModel strategyConfig, IStrategy strategy) {
/* 122 */     HashMap strategyVariableMap = strategyConfig.getDefaultVariableMap();
/* 123 */     NodeList listOfPresets = doc.getElementsByTagName("preset");
/* 124 */     PresetsModel result = new PresetsModel();
/* 125 */     if (listOfPresets.getLength() == 0) {
/* 126 */       return strategyConfig;
/*     */     }
/*     */ 
/* 129 */     for (int i = 0; i < listOfPresets.getLength(); i++) {
/* 130 */       Node presetNode = listOfPresets.item(i);
/* 131 */       if (presetNode.getNodeType() == 1) {
/* 132 */         Element preset = (Element)presetNode;
/*     */ 
/* 134 */         String presetName = preset.getAttribute("name");
/* 135 */         Preset currentPreset = new Preset(presetName, Preset.FILE_LOADED);
/*     */ 
/* 137 */         String presetDefault = preset.getAttribute("default");
/*     */ 
/* 139 */         if ((presetDefault != null) && (presetDefault.equals("true")))
/*     */         {
/* 141 */           result.setDefaultPreset(currentPreset);
/*     */         }
/*     */ 
/* 144 */         HashMap variableMap = new HashMap();
/* 145 */         NodeList variableList = preset.getElementsByTagName("variable");
/*     */ 
/* 147 */         for (int j = 0; j < variableList.getLength(); j++) {
/* 148 */           Node variableNode = variableList.item(j);
/* 149 */           Element variableElement = (Element)variableNode;
/*     */ 
/* 151 */           String name = readXMLParameter(variableElement, "name");
/* 152 */           String value = readXMLParameter(variableElement, "value");
/* 153 */           String type = readXMLParameter(variableElement, "type");
/*     */ 
/* 155 */           Variable newVariable = parseVariable(value, type, strategy.getClass().getClassLoader());
/*     */ 
/* 157 */           if ((newVariable == null) || (type == null) || (name == null)) {
/* 158 */             return strategyConfig;
/*     */           }
/* 160 */           if (!checkStrategyValidity(strategyVariableMap, name, newVariable)) {
/* 161 */             return strategyConfig;
/*     */           }
/*     */ 
/* 164 */           variableMap.put(name, newVariable);
/*     */         }
/*     */ 
/* 167 */         if (variableMap.size() != strategyVariableMap.size())
/*     */         {
/* 169 */           return strategyConfig;
/*     */         }
/*     */ 
/* 172 */         result.addPreset(currentPreset, variableMap);
/*     */       } else {
/* 174 */         return strategyConfig;
/*     */       }
/*     */     }
/* 177 */     result.addPreset(strategyConfig.getDefaultPreset(), strategyConfig.getDefaultVariableMap());
/* 178 */     if (result.getDefaultPreset() == null) {
/* 179 */       result.setDefaultPreset(strategyConfig.getDefaultPreset());
/*     */     }
/*     */ 
/* 182 */     return result;
/*     */   }
/*     */ 
/*     */   private static String readXMLParameter(Element from, String paramName) {
/* 186 */     NodeList nameAttributeList = from.getElementsByTagName(paramName);
/* 187 */     Element nameElement = (Element)nameAttributeList.item(0);
/* 188 */     if ((nameElement != null) && (nameElement.getChildNodes() != null) && (nameElement.getChildNodes().item(0) != null))
/*     */     {
/* 191 */       return nameElement.getChildNodes().item(0).getNodeValue().trim();
/*     */     }
/* 193 */     return null;
/*     */   }
/*     */ 
/*     */   private static Variable parseVariable(String value, String type, ClassLoader classLoader)
/*     */   {
/* 198 */     if (type.equals(Integer.TYPE.getSimpleName()))
/*     */       try {
/* 200 */         int intValue = StratUtils.parseInt(value);
/* 201 */         return new Variable(new Integer(intValue), Integer.TYPE);
/*     */       } catch (NumberFormatException nfe) {
/* 203 */         return null;
/*     */       }
/* 205 */     if (type.equals(Long.TYPE.getSimpleName()))
/*     */       try {
/* 207 */         long longValue = StratUtils.parseLong(value);
/* 208 */         return new Variable(new Long(longValue), Long.TYPE);
/*     */       } catch (NumberFormatException nfe) {
/* 210 */         return null;
/*     */       }
/* 212 */     if (type.equals(Double.TYPE.getSimpleName()))
/*     */       try {
/* 214 */         double doubleValue = StratUtils.parseDouble(value);
/* 215 */         return new Variable(new Double(doubleValue), Double.TYPE);
/*     */       } catch (NumberFormatException nfe) {
/* 217 */         return null;
/*     */       }
/* 219 */     if (type.equals(Boolean.TYPE.getSimpleName())) {
/* 220 */       if (value != null) {
/* 221 */         if (value.equals("true"))
/* 222 */           return new Variable(new Boolean(true), Boolean.TYPE);
/* 223 */         if (value.equals("false")) {
/* 224 */           return new Variable(new Boolean(false), Boolean.TYPE);
/*     */         }
/* 226 */         return null;
/*     */       }
/*     */ 
/* 229 */       return null;
/*     */     }
/* 231 */     if (type.equals(String.class.getName())) {
/* 232 */       if (value == null) {
/* 233 */         value = "";
/*     */       }
/* 235 */       return new Variable(value, String.class);
/* 236 */     }if (type.equals(Instrument.class.getName()))
/*     */     {
/*     */       Instrument instrument;
/*     */       Instrument instrument;
/* 239 */       if (value.equals("NULL"))
/* 240 */         instrument = null;
/*     */       else {
/* 242 */         instrument = Instrument.valueOf(value);
/*     */       }
/* 244 */       return new Variable(instrument, Instrument.class);
/*     */     }
/* 246 */     if (type.equals(Period.class.getName()))
/*     */     {
/*     */       Period period;
/*     */       Period period;
/* 249 */       if (value.equals("NULL"))
/* 250 */         period = null;
/*     */       else {
/* 252 */         period = Period.valueOf(value);
/*     */       }
/* 254 */       return new Variable(period, Period.class);
/*     */     }
/* 256 */     if (type.equals(File.class.getName())) {
/* 257 */       if (value == null) {
/* 258 */         value = "";
/*     */       }
/* 260 */       File file = new File(value);
/* 261 */       return new Variable(file, File.class);
/* 262 */     }if (type.startsWith("enum#"))
/*     */       try {
/* 264 */         if (classLoader == null) {
/* 265 */           classLoader = ClassLoader.getSystemClassLoader();
/*     */         }
/*     */ 
/* 268 */         Class enumType = classLoader.loadClass(type.split("#")[1]);
/*     */         Object enumValue;
/*     */         Object enumValue;
/* 271 */         if (value.equals("NULL"))
/* 272 */           enumValue = null;
/*     */         else {
/* 274 */           enumValue = Enum.valueOf(enumType, value);
/*     */         }
/*     */ 
/* 277 */         return new Variable(enumValue, enumType);
/*     */       }
/*     */       catch (ClassNotFoundException cnfe) {
/* 280 */         return null;
/*     */       }
/* 282 */     if (type.equals(Calendar.class.getName())) {
/* 283 */       Calendar calendar = Calendar.getInstance();
/* 284 */       calendar.setTimeInMillis(Long.valueOf(value).longValue());
/* 285 */       if (calendar != null) {
/* 286 */         return new Variable(calendar, Calendar.class);
/*     */       }
/* 288 */       return null;
/*     */     }
/* 290 */     if (type.equals(java.util.Date.class.getName())) {
/* 291 */       java.util.Date date = new java.util.Date(Long.valueOf(value).longValue());
/* 292 */       if (date != null) {
/* 293 */         return new Variable(date, java.util.Date.class);
/*     */       }
/* 295 */       return null;
/*     */     }
/* 297 */     if (type.equals(java.sql.Date.class.getName())) {
/* 298 */       java.sql.Date date = new java.sql.Date(Long.valueOf(value).longValue());
/* 299 */       if (date != null) {
/* 300 */         return new Variable(date, java.sql.Date.class);
/*     */       }
/* 302 */       return null;
/*     */     }
/*     */ 
/* 307 */     return null;
/*     */   }
/*     */ 
/*     */   private static boolean checkStrategyValidity(Map<String, Variable> strategyData, String varName, Variable var) {
/* 311 */     Variable strategyVar = (Variable)strategyData.get(varName);
/* 312 */     if (strategyVar == null) {
/* 313 */       return false;
/*     */     }
/*     */ 
/* 317 */     return (var == null) || 
/* 316 */       (var.getType().equals(strategyVar.getType()));
/*     */   }
/*     */ 
/*     */   public static boolean saveModel(IStrategy strategy, PresetsModel model, File binaryFile)
/*     */   {
/* 330 */     File strategyDir = FilePathManager.getInstance().getStrategiesFolder();
/* 331 */     if (binaryFile != null) {
/* 332 */       strategyDir = binaryFile.getParentFile();
/*     */     }
/*     */ 
/* 335 */     String strategyName = strategy.getClass().getSimpleName();
/*     */ 
/* 337 */     File xmlFile = new File(strategyDir.getPath() + File.separator + strategyName + ".xml");
/*     */     try
/*     */     {
/* 341 */       if (!xmlFile.exists()) {
/* 342 */         xmlFile.createNewFile();
/*     */       }
/*     */ 
/* 346 */       PresetsFormatHelper helper = new PresetsFormatHelper();
/* 347 */       helper.savePresetsInNewFormat(model, xmlFile);
/* 348 */       return true;
/*     */     }
/*     */     catch (Exception ex) {
/* 351 */       LOGGER.error(ex.getMessage(), ex);
/* 352 */     }return false;
/*     */   }
/*     */ 
/*     */   private static String paramToString(Object value)
/*     */   {
/* 441 */     if (value == null)
/*     */     {
/* 443 */       return "NULL";
/* 444 */     }if ((value instanceof Integer))
/* 445 */       return String.valueOf(value);
/* 446 */     if ((value instanceof Long))
/* 447 */       return String.valueOf(value);
/* 448 */     if ((value instanceof Double))
/* 449 */       return String.valueOf(value);
/* 450 */     if ((value instanceof Boolean))
/* 451 */       return String.valueOf(value);
/* 452 */     if ((value instanceof String))
/* 453 */       return (String)value;
/* 454 */     if ((value instanceof Period))
/* 455 */       return ((Period)value).name();
/* 456 */     if ((value instanceof Instrument))
/* 457 */       return ((Instrument)value).name();
/* 458 */     if ((value instanceof File))
/* 459 */       return ((File)value).getPath();
/* 460 */     if (Calendar.class.isAssignableFrom(value.getClass()))
/* 461 */       return String.valueOf(((Calendar)value).getTimeInMillis());
/* 462 */     if (java.util.Date.class.isAssignableFrom(value.getClass()))
/* 463 */       return String.valueOf(((java.util.Date)value).getTime());
/* 464 */     if (value.getClass().isEnum()) {
/* 465 */       return ((Enum)value).name();
/*     */     }
/*     */ 
/* 468 */     return "";
/*     */   }
/*     */ 
/*     */   private static boolean isParamTypeAcceptable(Class<?> clazz)
/*     */   {
/* 486 */     return (Integer.TYPE.equals(clazz)) || (Integer.class.equals(clazz)) || (Long.TYPE.equals(clazz)) || (Long.class.equals(clazz)) || (Double.TYPE.equals(clazz)) || (Double.class.equals(clazz)) || (Boolean.TYPE.equals(clazz)) || (Boolean.class.equals(clazz)) || (Short.TYPE.equals(clazz)) || (Short.class.equals(clazz)) || (String.class.equals(clazz)) || (Instrument.class.equals(clazz)) || (Period.class.equals(clazz)) || (File.class.equals(clazz)) || (Calendar.class.isAssignableFrom(clazz)) || (java.util.Date.class.isAssignableFrom(clazz)) || (clazz.isEnum());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.params.XMLPresets
 * JD-Core Version:    0.6.0
 */