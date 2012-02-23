/*     */ package com.dukascopy.dds2.greed.agent.strategy.params;
/*     */ 
/*     */ import com.dukascopy.api.Period;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import java.util.prefs.InvalidPreferencesFormatException;
/*     */ import java.util.prefs.Preferences;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class PresetsFormatHelper
/*     */ {
/*     */   private static final String PRESETS_NODE = "presets";
/*     */   private static final String PRESET_NODE = "preset";
/*     */   private static final String ID_KEY = "id";
/*     */   private static final String NAME_KEY = "name";
/*     */   private static final String PARAMETER_NODE = "parameter";
/*     */   private static final String TYPE_KEY = "type";
/*     */   private static final String VALUE_KEY = "value";
/*  30 */   private static final Logger LOGGER = LoggerFactory.getLogger(PresetsFormatHelper.class);
/*     */ 
/*     */   public PresetsModel loadPresetsFromNewFormat(PresetsModel defaultPreset, File presetsFile)
/*     */   {
/*  34 */     HashMap defaultParams = defaultPreset.getDefaultVariableMap();
/*     */ 
/*  36 */     PresetsModel result = new PresetsModel();
/*     */     try
/*     */     {
/*  39 */       Preferences.importPreferences(new FileInputStream(presetsFile));
/*  40 */       Preferences userPreferences = Preferences.userRoot();
/*     */ 
/*  42 */       Preferences presetsNode = userPreferences.node("presets");
/*  43 */       for (String presetNodeName : presetsNode.childrenNames())
/*     */       {
/*  45 */         Preferences presetNode = presetsNode.node(presetNodeName);
/*     */ 
/*  47 */         String presetId = presetNode.get("id", null);
/*  48 */         Preset currentPreset = new Preset(presetId, Preset.FILE_LOADED);
/*  49 */         HashMap params = new HashMap();
/*     */ 
/*  51 */         Iterator it = defaultParams.keySet().iterator();
/*     */ 
/*  53 */         while (it.hasNext())
/*     */         {
/*  55 */           String paramName = (String)it.next();
/*  56 */           String parameterNodeName = "parameter".concat("@").concat(paramName);
/*     */ 
/*  58 */           if (presetNode.nodeExists(parameterNodeName)) {
/*  59 */             Preferences parameterNode = presetNode.node(parameterNodeName);
/*     */ 
/*  61 */             String paramType = parameterNode.get("type", null);
/*  62 */             String paramValue = parameterNode.get("value", null);
/*     */ 
/*  64 */             Class typeClass = null;
/*     */             try {
/*  66 */               typeClass = getPrimitiveClass(paramType);
/*     */             } catch (IllegalArgumentException ex) {
/*  68 */               typeClass = Class.forName(paramType);
/*     */             }
/*     */ 
/*  71 */             Object resultValue = getValueByType(paramValue, typeClass);
/*     */ 
/*  73 */             Variable var = new Variable(resultValue, typeClass);
/*  74 */             params.put(paramName, var);
/*     */           }
/*     */           else
/*     */           {
/*  78 */             Variable defVar = (Variable)defaultParams.get(paramName);
/*  79 */             params.put(paramName, new Variable(defVar.getValue(), defVar.getType()));
/*     */           }
/*     */         }
/*     */ 
/*  83 */         result.addPreset(currentPreset, params);
/*     */       }
/*     */ 
/*  86 */       presetsNode.removeNode();
/*     */ 
/*  88 */       if (result.getAllPresets().size() > 0) {
/*  89 */         result.addPreset(defaultPreset.getDefaultPreset(), defaultPreset.getDefaultVariableMap());
/*  90 */         result.setDefaultPreset(defaultPreset.getDefaultPreset());
/*  91 */         return result;
/*     */       }
/*     */     }
/*     */     catch (InvalidPreferencesFormatException ex)
/*     */     {
/*  96 */       if (LOGGER.isDebugEnabled()) {
/*  97 */         LOGGER.debug(ex.getMessage(), ex);
/*     */       }
/*     */ 
/* 100 */       return null;
/*     */     }
/*     */     catch (Exception ex) {
/* 103 */       LOGGER.error(ex.getMessage(), ex);
/*     */     }
/*     */ 
/* 106 */     return null;
/*     */   }
/*     */ 
/*     */   public void savePresetsInNewFormat(PresetsModel model, File presetsFile)
/*     */   {
/* 111 */     if (presetsFile != null)
/*     */     {
/* 113 */       Preferences userRoot = Preferences.userRoot();
/*     */ 
/* 115 */       Preferences presetsNode = userRoot.node("presets");
/*     */ 
/* 117 */       Iterator it = model.getAllPresets().iterator();
/* 118 */       while (it.hasNext()) {
/* 119 */         Preset preset = (Preset)it.next();
/*     */ 
/* 121 */         if ((preset.getName().equalsIgnoreCase("default")) || (preset.getName().equalsIgnoreCase("DEFAULT_PRESET_ID")))
/*     */         {
/*     */           continue;
/*     */         }
/*     */ 
/* 126 */         Preferences presetNode = presetsNode.node("preset".concat("@").concat(preset.getName()));
/* 127 */         presetNode.put("id", preset.getName());
/* 128 */         presetNode.put("name", preset.getName());
/*     */ 
/* 130 */         HashMap params = model.getVariableMap(preset);
/* 131 */         Iterator paramsIt = params.keySet().iterator();
/* 132 */         while (paramsIt.hasNext())
/*     */         {
/* 134 */           String paramName = (String)paramsIt.next();
/* 135 */           Variable paramVar = (Variable)params.get(paramName);
/*     */ 
/* 137 */           Preferences parameterNode = presetNode.node("parameter".concat("@").concat(paramName));
/*     */ 
/* 139 */           parameterNode.put("id", paramName);
/*     */ 
/* 141 */           Class type = paramVar.getType();
/* 142 */           parameterNode.put("type", type.getName());
/*     */ 
/* 144 */           Object value = paramVar.getValue();
/*     */ 
/* 146 */           if (type.isEnum()) {
/* 147 */             parameterNode.put("value", ((Enum)value).name());
/* 148 */           } else if ((value instanceof Date)) {
/* 149 */             Date date = (Date)value;
/* 150 */             parameterNode.put("value", String.valueOf(date.getTime()));
/* 151 */           } else if ((value instanceof Period)) {
/* 152 */             parameterNode.put("value", ((Period)value).name());
/*     */           } else {
/* 154 */             parameterNode.put("value", value.toString());
/*     */           }
/*     */         }
/*     */       }
/*     */       try
/*     */       {
/* 160 */         if (!presetsFile.exists()) {
/* 161 */           presetsFile.createNewFile();
/*     */         }
/*     */ 
/* 164 */         OutputStream os = new BufferedOutputStream(new FileOutputStream(presetsFile));
/* 165 */         presetsNode.exportSubtree(os);
/* 166 */         presetsNode.removeNode();
/* 167 */         os.close();
/*     */       } catch (Exception ex) {
/* 169 */         LOGGER.error(ex.getMessage(), ex);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private Class<?> getPrimitiveClass(String name) {
/* 175 */     if (name.equals("byte"))
/* 176 */       return Byte.TYPE;
/* 177 */     if (name.equals("short"))
/* 178 */       return Short.TYPE;
/* 179 */     if (name.equals("int"))
/* 180 */       return Integer.TYPE;
/* 181 */     if (name.equals("long"))
/* 182 */       return Long.TYPE;
/* 183 */     if (name.equals("double"))
/* 184 */       return Double.TYPE;
/* 185 */     if (name.equals("boolean"))
/* 186 */       return Boolean.TYPE;
/* 187 */     if (name.equals("void")) {
/* 188 */       return Void.TYPE;
/*     */     }
/* 190 */     throw new IllegalArgumentException();
/*     */   }
/*     */ 
/*     */   private Object getValueByType(String paramValue, Class typeClass)
/*     */   {
/* 196 */     if (typeClass.isEnum())
/*     */     {
/* 198 */       Enum[] enumConstants = (Enum[])(Enum[])typeClass.getEnumConstants();
/* 199 */       for (Enum enumConstant : enumConstants)
/* 200 */         if ((enumConstant.toString().equals(paramValue)) || (enumConstant.name().equals(paramValue)))
/* 201 */           return enumConstant;
/*     */     }
/*     */     else
/*     */     {
/* 205 */       if (typeClass.isAssignableFrom(Byte.class))
/* 206 */         return new Byte(paramValue);
/* 207 */       if (typeClass.equals(Byte.TYPE))
/* 208 */         return Byte.valueOf(Byte.parseByte(paramValue));
/* 209 */       if (typeClass.isAssignableFrom(Short.class))
/* 210 */         return new Short(paramValue);
/* 211 */       if (typeClass.equals(Short.TYPE))
/* 212 */         return Short.valueOf(Short.parseShort(paramValue));
/* 213 */       if (typeClass.isAssignableFrom(Integer.class))
/* 214 */         return new Integer(paramValue);
/* 215 */       if (typeClass.equals(Integer.TYPE))
/* 216 */         return Integer.valueOf(Integer.parseInt(paramValue));
/* 217 */       if (typeClass.isAssignableFrom(Long.class))
/* 218 */         return new Long(paramValue);
/* 219 */       if (typeClass.equals(Long.TYPE))
/* 220 */         return Long.valueOf(Long.parseLong(paramValue));
/* 221 */       if (typeClass.isAssignableFrom(Double.class))
/* 222 */         return new Double(paramValue);
/* 223 */       if (typeClass.equals(Double.TYPE))
/* 224 */         return Double.valueOf(Double.parseDouble(paramValue));
/* 225 */       if (typeClass.isAssignableFrom(Boolean.class))
/* 226 */         return Boolean.valueOf(paramValue);
/* 227 */       if (typeClass.equals(Boolean.TYPE))
/* 228 */         return Boolean.valueOf(Boolean.parseBoolean(paramValue));
/* 229 */       if (typeClass.isAssignableFrom(String.class))
/* 230 */         return String.valueOf(paramValue);
/* 231 */       if (typeClass.isAssignableFrom(Date.class))
/* 232 */         return new Date(Long.parseLong(paramValue));
/* 233 */       if (typeClass.isAssignableFrom(Period.class)) {
/* 234 */         Period[] values = Period.values();
/* 235 */         for (int i = 0; i < values.length; i++) {
/* 236 */           Period period = values[i];
/* 237 */           if ((paramValue.equals(period.toString())) || (paramValue.equals(period.name()))) {
/* 238 */             return period;
/*     */           }
/*     */         }
/* 241 */         return null;
/* 242 */       }if (typeClass.isAssignableFrom(File.class)) {
/* 243 */         return new File(paramValue);
/*     */       }
/*     */     }
/* 246 */     LOGGER.warn("Unsupported type : [" + typeClass.getName() + "] , returning toString() implementation");
/*     */ 
/* 248 */     return paramValue.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.params.PresetsFormatHelper
 * JD-Core Version:    0.6.0
 */