/*     */ package com.dukascopy.dds2.properties;
/*     */ 
/*     */ import com.dukascopy.transport.util.Config;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class AbstractConfigParams
/*     */ {
/*  26 */   private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConfigParams.class);
/*     */   private Keys[] keys;
/*  28 */   protected Map<Keys, Object> values = new HashMap();
/*     */   private Config config;
/*     */ 
/*     */   protected AbstractConfigParams()
/*     */   {
/*  46 */     Class keyClass = getKeysEnumClass();
/*     */     try {
/*  48 */       this.keys = ((Keys[])(Keys[])keyClass.getMethod("values", new Class[0]).invoke(null, new Object[0]));
/*     */     } catch (IllegalAccessException e) {
/*  50 */       LOGGER.error(e.getMessage(), e);
/*     */     } catch (InvocationTargetException e) {
/*  52 */       LOGGER.error(e.getMessage(), e);
/*     */     } catch (NoSuchMethodException e) {
/*  54 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected abstract Class<? extends Keys> getKeysEnumClass();
/*     */ 
/*     */   public void readProperties(Config properties) throws Exception {
/*  61 */     this.config = properties;
/*  62 */     for (Keys key : this.keys)
/*  63 */       if (key.isIndexed()) {
/*     */         Keys[] indexedKeys;
/*     */         try { indexedKeys = (Keys[])(Keys[])key.getClazz().getMethod("values", new Class[0]).invoke(null, new Object[0]);
/*     */         } catch (Exception e) {
/*  68 */           throw new Exception(e);
/*     */         }
/*  70 */         List indexedValuesList = new ArrayList();
/*  71 */         this.values.put(key, indexedValuesList);
/*  72 */         int index = 0;
/*  73 */         while (properties.exists(MessageFormat.format(key.getKey(), new Object[] { Integer.valueOf(index) }))) {
/*  74 */           Map indexedValues = new HashMap();
/*  75 */           for (Keys indexedKey : indexedKeys) {
/*  76 */             indexedValues.put(indexedKey, getValue(indexedKey, index));
/*     */           }
/*  78 */           indexedValuesList.add(indexedValues);
/*  79 */           index++;
/*     */         }
/*     */       } else {
/*  82 */         this.values.put(key, getValue(key, -2147483648));
/*     */       }
/*     */   }
/*     */ 
/*     */   private Object getValue(Keys key, int index)
/*     */     throws Exception
/*     */   {
/*     */     String propertyKey;
/*     */     String propertyKey;
/*  90 */     if (index == -2147483648)
/*  91 */       propertyKey = key.getKey();
/*     */     else
/*  93 */       propertyKey = MessageFormat.format(key.getKey(), new Object[] { Integer.valueOf(index) });
/*     */     String value;
/*  95 */     if (key.isMandatory()) {
/*  96 */       String value = this.config.getString(propertyKey);
/*  97 */       if (value == null)
/*  98 */         throw new Exception("Property with key [" + propertyKey + "] is mandatory and was not found in properties file");
/*     */     }
/*     */     else
/*     */     {
/* 102 */       value = this.config.getString(propertyKey, key.getDefaultValue());
/*     */ 
/* 104 */       this.config.putString(propertyKey, value);
/*     */     }
/* 106 */     Class keyClazz = key.getClazz();
/* 107 */     if (keyClazz.equals(String.class))
/* 108 */       return value;
/* 109 */     if (value == null)
/* 110 */       return null;
/* 111 */     if (keyClazz.equals(Integer.class))
/*     */       try {
/* 113 */         return Integer.valueOf(Integer.parseInt(value));
/*     */       } catch (NumberFormatException e) {
/* 115 */         throw new Exception("Incorrect number format for integer [" + value + "] for key [" + propertyKey + "]");
/*     */       }
/* 117 */     if (keyClazz.equals(Long.class))
/*     */       try {
/* 119 */         return Long.valueOf(Long.parseLong(value));
/*     */       } catch (NumberFormatException e) {
/* 121 */         throw new Exception("Incorrect number format for long [" + value + "] for key [" + propertyKey + "]");
/*     */       }
/* 123 */     if (keyClazz.equals(Double.class))
/*     */       try {
/* 125 */         return Double.valueOf(Double.parseDouble(value));
/*     */       } catch (NumberFormatException e) {
/* 127 */         throw new Exception("Incorrect number format for double [" + value + "] for key [" + propertyKey + "]");
/*     */       }
/* 129 */     if (keyClazz.equals(Boolean.class)) {
/* 130 */       if (value.equalsIgnoreCase("true"))
/* 131 */         return Boolean.valueOf(true);
/* 132 */       if (value.equalsIgnoreCase("false")) {
/* 133 */         return Boolean.valueOf(false);
/*     */       }
/* 135 */       throw new Exception("Incorrect boolean format [" + value + "] for key [" + propertyKey + "], must be true or false");
/*     */     }
/*     */ 
/* 138 */     throw new Exception("Unknown type of the property with key [" + propertyKey + "]");
/*     */   }
/*     */ 
/*     */   public Object get(Keys key)
/*     */   {
/* 143 */     if (key.isIndexed()) {
/* 144 */       List indexedValues = (List)this.values.get(key);
/* 145 */       return Integer.valueOf(indexedValues.size());
/*     */     }
/* 147 */     return this.values.get(key);
/*     */   }
/*     */ 
/*     */   public void set(Keys key, Object valueObj)
/*     */     throws Exception
/*     */   {
/* 153 */     String propertyKey = key.getKey();
/*     */ 
/* 155 */     if (key.isIndexed()) {
/* 156 */       throw new Exception("Can not set value of the indexed key");
/*     */     }
/*     */ 
/* 159 */     boolean found = false;
/* 160 */     for (Keys knownKey : this.keys) {
/* 161 */       if (knownKey == key) {
/* 162 */         found = true;
/* 163 */         break;
/*     */       }
/*     */     }
/* 166 */     if (!found) {
/* 167 */       throw new Exception("Can not find key among known keys");
/*     */     }
/*     */ 
/* 170 */     if ((key.isMandatory()) && 
/* 171 */       (valueObj == null))
/* 172 */       throw new Exception("Property with key [" + propertyKey + "] is mandatory and can not be null");
/*     */     String value;
/* 175 */     if (valueObj == null) {
/* 176 */       value = key.getDefaultValue();
/*     */     } else {
/* 178 */       Class keyClazz = key.getClazz();
/*     */       String value;
/* 179 */       if (keyClazz.equals(String.class)) {
/* 180 */         value = valueObj.toString();
/*     */       }
/*     */       else
/*     */       {
/*     */         String value;
/* 181 */         if (keyClazz.equals(Integer.class)) {
/* 182 */           if (!(valueObj instanceof Integer)) {
/* 183 */             throw new Exception("Incorrect value type for key [" + propertyKey + "], must be Integer");
/*     */           }
/* 185 */           value = valueObj.toString();
/*     */         }
/*     */         else
/*     */         {
/*     */           String value;
/* 186 */           if (keyClazz.equals(Long.class)) {
/* 187 */             if (!(valueObj instanceof Long)) {
/* 188 */               throw new Exception("Incorrect value type for key [" + propertyKey + "], must be Long");
/*     */             }
/* 190 */             value = valueObj.toString();
/*     */           }
/*     */           else
/*     */           {
/*     */             String value;
/* 191 */             if (keyClazz.equals(Double.class)) {
/* 192 */               if (!(valueObj instanceof Double)) {
/* 193 */                 throw new Exception("Incorrect value type for key [" + propertyKey + "], must be Double");
/*     */               }
/* 195 */               value = valueObj.toString();
/*     */             }
/*     */             else
/*     */             {
/*     */               String value;
/* 196 */               if (keyClazz.equals(Boolean.class)) {
/* 197 */                 if (!(valueObj instanceof Boolean)) {
/* 198 */                   throw new Exception("Incorrect value type for key [" + propertyKey + "], must be Boolean");
/*     */                 }
/* 200 */                 value = ((Boolean)valueObj).booleanValue() ? "true" : "false";
/*     */               } else {
/* 202 */                 throw new Exception("Unknown type of the property with key [" + propertyKey + "]");
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     String value;
/* 206 */     this.config.putString(propertyKey, value);
/* 207 */     this.values.put(key, valueObj);
/*     */   }
/*     */ 
/*     */   public String getString(Keys key) {
/* 211 */     Object value = get(key);
/* 212 */     return value == null ? null : value.toString();
/*     */   }
/*     */ 
/*     */   public int getInt(Keys key) {
/* 216 */     Object value = get(key);
/* 217 */     return value == null ? -2147483648 : ((Number)value).intValue();
/*     */   }
/*     */ 
/*     */   public long getLong(Keys key) {
/* 221 */     Object value = get(key);
/* 222 */     return value == null ? -9223372036854775808L : ((Number)value).longValue();
/*     */   }
/*     */ 
/*     */   public double getDouble(Keys key) {
/* 226 */     Object value = get(key);
/* 227 */     return value == null ? (0.0D / 0.0D) : ((Number)value).doubleValue();
/*     */   }
/*     */ 
/*     */   public boolean getBoolean(Keys key) {
/* 231 */     Object value = get(key);
/* 232 */     return (value == null ? Boolean.FALSE : (Boolean)value).booleanValue();
/*     */   }
/*     */ 
/*     */   public Object get(Keys key, int index, Keys indexedKey) {
/* 236 */     if (key.isIndexed())
/*     */     {
/* 238 */       List indexedValues = (List)this.values.get(key);
/* 239 */       return ((Map)indexedValues.get(index)).get(indexedKey);
/*     */     }
/* 241 */     throw new IllegalArgumentException("Key [" + ((Enum)key).name() + "] is not indexed");
/*     */   }
/*     */ 
/*     */   public String getString(Keys key, int index, Keys indexedKey)
/*     */   {
/* 246 */     return (String)get(key, index, indexedKey);
/*     */   }
/*     */ 
/*     */   public int getInt(Keys key, int index, Keys indexedKey) {
/* 250 */     Object value = get(key, index, indexedKey);
/* 251 */     return value == null ? -2147483648 : ((Number)value).intValue();
/*     */   }
/*     */ 
/*     */   public long getLong(Keys key, int index, Keys indexedKey) {
/* 255 */     Object value = get(key, index, indexedKey);
/* 256 */     return value == null ? -9223372036854775808L : ((Number)value).longValue();
/*     */   }
/*     */ 
/*     */   public double getDouble(Keys key, int index, Keys indexedKey) {
/* 260 */     Object value = get(key, index, indexedKey);
/* 261 */     return value == null ? (0.0D / 0.0D) : ((Number)value).doubleValue();
/*     */   }
/*     */ 
/*     */   public boolean getBoolean(Keys key, int index, Keys indexedKey) {
/* 265 */     Object value = get(key, index, indexedKey);
/* 266 */     return (value == null ? Boolean.FALSE : (Boolean)value).booleanValue();
/*     */   }
/*     */ 
/*     */   public Config getConfig() {
/* 270 */     return this.config;
/*     */   }
/*     */ 
/*     */   public static abstract interface Keys
/*     */   {
/*     */     public abstract String getKey();
/*     */ 
/*     */     public abstract boolean isMandatory();
/*     */ 
/*     */     public abstract String getDefaultValue();
/*     */ 
/*     */     public abstract Class getClazz();
/*     */ 
/*     */     public abstract boolean isIndexed();
/*     */ 
/*     */     public abstract int ordinal();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.properties.AbstractConfigParams
 * JD-Core Version:    0.6.0
 */