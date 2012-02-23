/*     */ package com.dukascopy.transport.util;
/*     */ 
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.Set;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class Config
/*     */ {
/*  26 */   private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
/*     */ 
/*  28 */   private static final Map<String, Config> bundles = new HashMap();
/*     */   private Properties properties;
/*  31 */   private Map<String, Object> overwrittenProperties = new HashMap();
/*     */ 
/*     */   /** @deprecated */
/*     */   public Config(ResourceBundle rb)
/*     */   {
/*  40 */     this.properties = new Properties();
/*  41 */     for (String key : rb.keySet())
/*  42 */       this.properties.put(key, rb.getString(key));
/*     */   }
/*     */ 
/*     */   public Config(Properties properties)
/*     */   {
/*  47 */     this.properties = properties;
/*     */   }
/*     */ 
/*     */   private void setProperties(Properties properties) {
/*  51 */     this.properties = properties;
/*     */   }
/*     */ 
/*     */   public static Config getConfig(String baseFileName)
/*     */   {
/*  61 */     synchronized (bundles) {
/*  62 */       Config config = (Config)bundles.get(baseFileName);
/*  63 */       if (config == null) {
/*  64 */         Properties props = new Properties();
/*     */ 
/*  66 */         InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(baseFileName);
/*  67 */         if (resourceAsStream == null) {
/*  68 */           resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(baseFileName + ".properties");
/*     */         }
/*  70 */         if (resourceAsStream == null)
/*     */           try
/*     */           {
/*  73 */             resourceAsStream = new FileInputStream(baseFileName);
/*     */           }
/*     */           catch (FileNotFoundException e)
/*     */           {
/*     */           }
/*  78 */         if (resourceAsStream == null)
/*     */           try
/*     */           {
/*  81 */             resourceAsStream = new FileInputStream(baseFileName + ".properties");
/*     */           }
/*     */           catch (FileNotFoundException e)
/*     */           {
/*     */           }
/*  86 */         if (resourceAsStream != null) {
/*     */           try {
/*  88 */             props.load(resourceAsStream);
/*     */           } catch (IOException e) {
/*  90 */             LOGGER.error(e.getMessage(), e);
/*     */           }
/*  92 */           config = new Config(props);
/*  93 */           bundles.put(baseFileName, config);
/*     */         }
/*     */       }
/*  96 */       return config;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Config getConfig(String baseFileName, Properties properties)
/*     */   {
/* 106 */     synchronized (bundles) {
/* 107 */       Config config = (Config)bundles.get(baseFileName);
/* 108 */       if (config == null) {
/* 109 */         config = new Config(properties);
/* 110 */         bundles.put(baseFileName, config);
/*     */       } else {
/* 112 */         config.setProperties(properties);
/*     */       }
/* 114 */       return config;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean exists(String startsWith)
/*     */   {
/* 125 */     for (Iterator i$ = this.properties.keySet().iterator(); i$.hasNext(); ) { Object key = i$.next();
/* 126 */       if (key.toString().startsWith(startsWith)) {
/* 127 */         return true;
/*     */       }
/*     */     }
/* 130 */     return false;
/*     */   }
/*     */ 
/*     */   public String getString(String propertyName)
/*     */   {
/* 140 */     if (this.overwrittenProperties.containsKey(propertyName)) {
/* 141 */       return (String)this.overwrittenProperties.get(propertyName);
/*     */     }
/* 143 */     return this.properties.getProperty(propertyName);
/*     */   }
/*     */ 
/*     */   public String getString(String propertyName, String defaultValue)
/*     */   {
/* 156 */     String value = getString(propertyName);
/* 157 */     return value == null ? defaultValue : value;
/*     */   }
/*     */ 
/*     */   public void putString(String name, String value) {
/* 161 */     this.overwrittenProperties.put(name, value);
/*     */   }
/*     */ 
/*     */   public Integer getInteger(String propertyName)
/*     */   {
/* 171 */     Integer value = null;
/*     */ 
/* 173 */     String s = getString(propertyName);
/* 174 */     if (s != null) {
/*     */       try {
/* 176 */         value = Integer.valueOf(Integer.parseInt(s));
/*     */       }
/*     */       catch (NumberFormatException nfe)
/*     */       {
/*     */       }
/*     */     }
/* 182 */     return value;
/*     */   }
/*     */ 
/*     */   public Integer getInteger(String propertyName, Integer defaultValue)
/*     */   {
/* 194 */     Integer value = getInteger(propertyName);
/* 195 */     return value == null ? defaultValue : value;
/*     */   }
/*     */ 
/*     */   public Long getLong(String propertyName)
/*     */   {
/* 205 */     Long value = null;
/*     */ 
/* 207 */     String s = getString(propertyName);
/* 208 */     if (s != null) {
/*     */       try {
/* 210 */         value = Long.valueOf(Long.parseLong(s));
/*     */       }
/*     */       catch (NumberFormatException nfe)
/*     */       {
/*     */       }
/*     */     }
/* 216 */     return value;
/*     */   }
/*     */ 
/*     */   public Long getLong(String propertyName, Long defaultValue)
/*     */   {
/* 228 */     Long value = getLong(propertyName);
/* 229 */     return value == null ? defaultValue : value;
/*     */   }
/*     */ 
/*     */   public Boolean getBoolean(String propertyName)
/*     */   {
/* 239 */     Boolean value = null;
/*     */ 
/* 241 */     String s = getString(propertyName);
/* 242 */     if (s != null) {
/*     */       try {
/* 244 */         value = Boolean.valueOf(Boolean.parseBoolean(s));
/*     */       }
/*     */       catch (NumberFormatException nfe)
/*     */       {
/*     */       }
/*     */     }
/* 250 */     return value;
/*     */   }
/*     */ 
/*     */   public Boolean getBoolean(String propertyName, Boolean defaultValue)
/*     */   {
/* 262 */     Boolean value = getBoolean(propertyName);
/* 263 */     return value == null ? defaultValue : value;
/*     */   }
/*     */ 
/*     */   public Float getFloat(String propertyName)
/*     */   {
/* 273 */     Float value = null;
/*     */ 
/* 275 */     String s = getString(propertyName);
/* 276 */     if (s != null) {
/*     */       try {
/* 278 */         value = Float.valueOf(Float.parseFloat(s));
/*     */       }
/*     */       catch (NumberFormatException nfe)
/*     */       {
/*     */       }
/*     */     }
/* 284 */     return value;
/*     */   }
/*     */ 
/*     */   public Float getFloat(String propertyName, Float defaultValue)
/*     */   {
/* 296 */     Float value = getFloat(propertyName);
/* 297 */     return value == null ? defaultValue : value;
/*     */   }
/*     */ 
/*     */   public Double getDouble(String propertyName)
/*     */   {
/* 307 */     Double value = null;
/*     */ 
/* 309 */     String s = getString(propertyName);
/* 310 */     if (s != null) {
/*     */       try {
/* 312 */         value = Double.valueOf(Double.parseDouble(s));
/*     */       }
/*     */       catch (NumberFormatException nfe)
/*     */       {
/*     */       }
/*     */     }
/* 318 */     return value;
/*     */   }
/*     */ 
/*     */   public Double getDouble(String propertyName, Double defaultValue)
/*     */   {
/* 330 */     Double value = getDouble(propertyName);
/* 331 */     return value == null ? defaultValue : value;
/*     */   }
/*     */ 
/*     */   public BigDecimal getBigDecimal(String propertyName)
/*     */   {
/* 341 */     BigDecimal value = null;
/*     */ 
/* 343 */     String s = getString(propertyName);
/* 344 */     if (s != null) {
/*     */       try {
/* 346 */         value = new BigDecimal(s);
/*     */       }
/*     */       catch (NumberFormatException nfe)
/*     */       {
/*     */       }
/*     */     }
/* 352 */     return value;
/*     */   }
/*     */ 
/*     */   public BigDecimal getBigDecimal(String propertyName, BigDecimal defaultValue)
/*     */   {
/* 364 */     BigDecimal value = getBigDecimal(propertyName);
/* 365 */     return value == null ? defaultValue : value;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.util.Config
 * JD-Core Version:    0.6.0
 */