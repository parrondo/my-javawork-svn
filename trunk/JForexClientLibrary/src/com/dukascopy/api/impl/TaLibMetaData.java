/*     */ package com.dukascopy.api.impl;
/*     */ 
/*     */ import com.dukascopy.api.impl.talib.FuncInfoHolder;
/*     */ import com.dukascopy.api.impl.talib.Holder;
/*     */ import com.dukascopy.api.impl.talib.InputParameterInfoHolder;
/*     */ import com.dukascopy.api.impl.talib.IntegerListHolder;
/*     */ import com.dukascopy.api.impl.talib.IntegerRangeHolder;
/*     */ import com.dukascopy.api.impl.talib.OptInputParameterInfoHolder;
/*     */ import com.dukascopy.api.impl.talib.OutputParameterInfoHolder;
/*     */ import com.dukascopy.api.impl.talib.RealListHolder;
/*     */ import com.dukascopy.api.impl.talib.RealRangeHolder;
/*     */ import com.dukascopy.api.impl.talib.TaFuncService;
/*     */ import com.dukascopy.api.impl.talib.TaGrpService;
/*     */ import com.tictactec.ta.lib.CoreAnnotated;
/*     */ import com.tictactec.ta.lib.MAType;
/*     */ import com.tictactec.ta.lib.MInteger;
/*     */ import com.tictactec.ta.lib.RetCode;
/*     */ import com.tictactec.ta.lib.meta.PriceInputParameter;
/*     */ import com.tictactec.ta.lib.meta.annotation.FuncInfo;
/*     */ import com.tictactec.ta.lib.meta.annotation.InputParameterInfo;
/*     */ import com.tictactec.ta.lib.meta.annotation.InputParameterType;
/*     */ import com.tictactec.ta.lib.meta.annotation.IntegerList;
/*     */ import com.tictactec.ta.lib.meta.annotation.IntegerRange;
/*     */ import com.tictactec.ta.lib.meta.annotation.OptInputParameterInfo;
/*     */ import com.tictactec.ta.lib.meta.annotation.OptInputParameterType;
/*     */ import com.tictactec.ta.lib.meta.annotation.OutputParameterInfo;
/*     */ import com.tictactec.ta.lib.meta.annotation.OutputParameterType;
/*     */ import com.tictactec.ta.lib.meta.annotation.RealList;
/*     */ import com.tictactec.ta.lib.meta.annotation.RealRange;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.annotation.IncompleteAnnotationException;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ public class TaLibMetaData
/*     */   implements Comparable<TaLibMetaData>, Cloneable
/*     */ {
/*     */   private static final transient String PARAM_NOT_FOUND = "Parameter with specified index was not found";
/*     */   private static final transient String UNKNOWN_PARAM_TYPE = "Unknown parameter type";
/*     */   private static final transient String UNEXPECTED_PARAM_TYPE = "Unexpected parameter type";
/*     */   private static final transient String UNEXPECTED_PARAM_VALUE = "Unexpected parameter value";
/*     */   private static final transient String INDEX_OUT_OF_BOUNDS = "Index out of bounds";
/*     */   private static final transient String ILLEGAL_NUMBER_OF_ARGUMENTS = "Illegal number of arguments";
/*     */   private static final transient String ARRAY_IS_NULL = "Array is null";
/*     */   private static final transient String INT_ARRAY_EXPECTED = "int[] expected";
/*     */   private static final transient String DOUBLE_ARRAY_EXPECTED = "double[] expected";
/*     */   private static final transient String PRICE_EXPECTED = "PriceInputParameter object expected";
/*  62 */   private static final transient Class<CoreAnnotated> coreClass = CoreAnnotated.class;
/*     */   private static final transient String LOOKBACK_SUFFIX = "Lookback";
/*  65 */   private static transient CoreAnnotated taCore = null;
/*     */ 
/*  67 */   private String name = null;
/*  68 */   private Method function = null;
/*  69 */   private Method lookback = null;
/*     */ 
/*  71 */   private static transient Map<String, TaLibMetaData> taFuncMap = null;
/*  72 */   private static transient Map<String, Set<TaLibMetaData>> taGrpMap = null;
/*     */ 
/*  74 */   private transient Object[] callInputParams = null;
/*  75 */   private transient Object[] callOutputParams = null;
/*  76 */   private transient Object[] callOptInputParams = null;
/*     */   private transient FuncInfoHolder funcInfo;
/*     */   private transient Holder[][] parameterAnnotations;
/*     */ 
/*     */   protected TaLibMetaData()
/*     */   {
/*  83 */     synchronized (coreClass) {
/*  84 */       if (taCore == null)
/*  85 */         taCore = new CoreAnnotated();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int compareTo(TaLibMetaData arg)
/*     */   {
/*  91 */     return this.name.compareTo(arg.name);
/*     */   }
/*     */ 
/*     */   private static Map<String, TaLibMetaData> getAllFuncs() {
/*  95 */     synchronized (coreClass) {
/*  96 */       if (taFuncMap == null) {
/*  97 */         taFuncMap = getTaFuncMetaInfoMap();
/*     */       }
/*     */     }
/* 100 */     return taFuncMap;
/*     */   }
/*     */ 
/*     */   private static Map<String, Set<TaLibMetaData>> getAllGrps() {
/* 104 */     synchronized (coreClass) {
/* 105 */       if (taGrpMap == null) {
/* 106 */         taGrpMap = getTaGrpMetaInfoMap();
/*     */       }
/*     */     }
/* 109 */     return taGrpMap;
/*     */   }
/*     */ 
/*     */   private static Map<String, Method> getLookbackMethodMap() {
/* 113 */     Map map = new HashMap();
/* 114 */     Method[] ms = coreClass.getDeclaredMethods();
/* 115 */     for (Method m : ms) {
/* 116 */       if (m.getName().endsWith("Lookback")) {
/* 117 */         map.put(m.getName(), m);
/*     */       }
/*     */     }
/* 120 */     return map;
/*     */   }
/*     */ 
/*     */   private static Map<String, TaLibMetaData> getTaFuncMetaInfoMap() {
/* 124 */     Map result = new TreeMap();
/* 125 */     Method[] ms = coreClass.getDeclaredMethods();
/* 126 */     Map lookbackMap = getLookbackMethodMap();
/* 127 */     for (Method funcMethod : ms) {
/* 128 */       String fn = funcMethod.getName();
/* 129 */       if (!funcMethod.getReturnType().equals(RetCode.class)) {
/*     */         continue;
/*     */       }
/* 132 */       String lookbackName = fn + "Lookback";
/* 133 */       Method lookbackMethod = (Method)lookbackMap.get(lookbackName);
/* 134 */       if (lookbackMethod != null) {
/* 135 */         FuncInfoHolder info = getFuncInfo(funcMethod);
/* 136 */         String funcName = info.name;
/* 137 */         TaLibMetaData mi = new TaLibMetaData();
/* 138 */         mi.name = funcName;
/* 139 */         mi.function = funcMethod;
/* 140 */         mi.lookback = lookbackMethod;
/* 141 */         result.put(funcName, mi);
/*     */       }
/*     */     }
/*     */ 
/* 145 */     return result;
/*     */   }
/*     */ 
/*     */   private static Map<String, Set<TaLibMetaData>> getTaGrpMetaInfoMap() {
/* 149 */     if (taFuncMap == null) getAllFuncs();
/* 150 */     Map result = new TreeMap();
/* 151 */     for (String func : taFuncMap.keySet()) {
/* 152 */       TaLibMetaData mi = (TaLibMetaData)taFuncMap.get(func);
/* 153 */       String group = mi.getFuncInfo().group;
/* 154 */       Set set = (Set)result.get(group);
/* 155 */       if (set == null) {
/* 156 */         set = new TreeSet();
/* 157 */         result.put(group, set);
/*     */       }
/* 159 */       set.add(mi);
/*     */     }
/* 161 */     return result;
/*     */   }
/*     */ 
/*     */   private static FuncInfoHolder getFuncInfo(Method method) throws IncompleteAnnotationException {
/* 165 */     FuncInfo annotation = (FuncInfo)method.getAnnotation(FuncInfo.class);
/* 166 */     if (annotation != null) {
/* 167 */       FuncInfoHolder holder = new FuncInfoHolder();
/* 168 */       holder.name = annotation.name();
/* 169 */       holder.group = annotation.group();
/* 170 */       holder.hint = annotation.hint();
/* 171 */       holder.helpFile = annotation.helpFile();
/* 172 */       holder.flags = annotation.flags();
/* 173 */       holder.nbInput = annotation.nbInput();
/* 174 */       holder.nbOptInput = annotation.nbOptInput();
/* 175 */       holder.nbOutput = annotation.nbOutput();
/* 176 */       return holder;
/*     */     }
/* 178 */     throw new IncompleteAnnotationException(FuncInfo.class, "Method " + method.getName());
/*     */   }
/*     */ 
/*     */   static TaLibMetaData getFuncHandle(String name) throws NoSuchMethodException {
/* 182 */     TaLibMetaData mi = (TaLibMetaData)getAllFuncs().get(name.toUpperCase());
/* 183 */     if (mi == null) throw new NoSuchMethodException(name.toUpperCase());
/* 184 */     mi.callInputParams = null;
/* 185 */     mi.callOutputParams = null;
/* 186 */     mi.callOptInputParams = null;
/* 187 */     if (mi != null) return mi;
/* 188 */     throw new NoSuchMethodException("Function " + name);
/*     */   }
/*     */ 
/*     */   public static TaLibMetaData getInstance(String name)
/*     */     throws NoSuchMethodException
/*     */   {
/* 201 */     return getFuncHandle(name).clone();
/*     */   }
/*     */ 
/*     */   protected TaLibMetaData clone()
/*     */   {
/*     */     try {
/* 207 */       getFuncInfo();
/* 208 */       getParameterAnnotations();
/* 209 */       TaLibMetaData clone = (TaLibMetaData)super.clone();
/* 210 */       this.callInputParams = null;
/* 211 */       this.callOutputParams = null;
/* 212 */       this.callOptInputParams = null;
/* 213 */       return clone;
/*     */     } catch (CloneNotSupportedException e) {
/*     */     }
/* 216 */     return null;
/*     */   }
/*     */ 
/*     */   public FuncInfoHolder getFuncInfo()
/*     */     throws IncompleteAnnotationException
/*     */   {
/* 227 */     if (this.funcInfo == null) {
/* 228 */       this.funcInfo = getFuncInfo(this.function);
/*     */     }
/* 230 */     return this.funcInfo;
/*     */   }
/*     */ 
/*     */   private Holder getParameterInfo(int paramIndex, Class<? extends Object> paramAnnotation) {
/* 234 */     if (paramIndex < 0)
/* 235 */       throw new IllegalArgumentException("Index out of bounds");
/* 236 */     int i = 0;
/* 237 */     for (Holder[] annArray : getParameterAnnotations()) {
/* 238 */       for (Holder ann : annArray) {
/* 239 */         if ((ann.annotationType == paramAnnotation) && (paramIndex == i++)) {
/* 240 */           return ann;
/*     */         }
/*     */       }
/*     */     }
/* 244 */     return null;
/*     */   }
/*     */ 
/*     */   private Holder[][] getParameterAnnotations() {
/* 248 */     if (this.parameterAnnotations == null) {
/* 249 */       Annotation[][] annotations = this.function.getParameterAnnotations();
/* 250 */       this.parameterAnnotations = new Holder[annotations.length][];
/* 251 */       for (int i = 0; i < annotations.length; i++) {
/* 252 */         Annotation[] paramAnnotations = annotations[i];
/* 253 */         this.parameterAnnotations[i] = new Holder[paramAnnotations.length];
/* 254 */         for (int j = 0; j < paramAnnotations.length; j++) {
/* 255 */           Annotation annotation = paramAnnotations[j];
/* 256 */           Holder holder = null;
/* 257 */           Class annotationType = annotation.annotationType();
/* 258 */           if (annotationType == InputParameterInfo.class) {
/* 259 */             InputParameterInfo infoAnnotation = (InputParameterInfo)annotation;
/* 260 */             InputParameterInfoHolder infoHolder = new InputParameterInfoHolder();
/* 261 */             infoHolder.annotationType = InputParameterInfo.class;
/* 262 */             infoHolder.flags = infoAnnotation.flags();
/* 263 */             infoHolder.paramName = infoAnnotation.paramName();
/* 264 */             infoHolder.type = infoAnnotation.type();
/* 265 */             holder = infoHolder;
/* 266 */           } else if (annotationType == OptInputParameterInfo.class) {
/* 267 */             OptInputParameterInfo infoAnnotation = (OptInputParameterInfo)annotation;
/* 268 */             OptInputParameterInfoHolder infoHolder = new OptInputParameterInfoHolder();
/* 269 */             infoHolder.annotationType = OptInputParameterInfo.class;
/* 270 */             infoHolder.type = infoAnnotation.type();
/* 271 */             infoHolder.paramName = infoAnnotation.paramName();
/* 272 */             infoHolder.flags = infoAnnotation.flags();
/* 273 */             infoHolder.displayName = infoAnnotation.displayName();
/* 274 */             infoHolder.dataSet = infoAnnotation.dataSet();
/* 275 */             holder = infoHolder;
/* 276 */           } else if (annotationType == OutputParameterInfo.class) {
/* 277 */             OutputParameterInfo infoAnnotation = (OutputParameterInfo)annotation;
/* 278 */             OutputParameterInfoHolder infoHolder = new OutputParameterInfoHolder();
/* 279 */             infoHolder.annotationType = OutputParameterInfo.class;
/* 280 */             infoHolder.type = infoAnnotation.type();
/* 281 */             infoHolder.paramName = infoAnnotation.paramName();
/* 282 */             infoHolder.flags = infoAnnotation.flags();
/* 283 */             holder = infoHolder;
/* 284 */           } else if (annotationType == IntegerList.class) {
/* 285 */             IntegerList infoAnnotation = (IntegerList)annotation;
/* 286 */             IntegerListHolder infoHolder = new IntegerListHolder();
/* 287 */             infoHolder.annotationType = IntegerList.class;
/* 288 */             infoHolder.value = infoAnnotation.value();
/* 289 */             infoHolder.paramName = infoAnnotation.paramName();
/* 290 */             infoHolder.defaultValue = infoAnnotation.defaultValue();
/* 291 */             infoHolder.string = infoAnnotation.string();
/* 292 */             holder = infoHolder;
/* 293 */           } else if (annotationType == RealList.class) {
/* 294 */             RealList infoAnnotation = (RealList)annotation;
/* 295 */             RealListHolder infoHolder = new RealListHolder();
/* 296 */             infoHolder.annotationType = RealList.class;
/* 297 */             infoHolder.value = infoAnnotation.value();
/* 298 */             infoHolder.paramName = infoAnnotation.paramName();
/* 299 */             infoHolder.defaultValue = infoAnnotation.defaultValue();
/* 300 */             infoHolder.string = infoAnnotation.string();
/* 301 */             holder = infoHolder;
/* 302 */           } else if (annotationType == IntegerRange.class) {
/* 303 */             IntegerRange infoAnnotation = (IntegerRange)annotation;
/* 304 */             IntegerRangeHolder infoHolder = new IntegerRangeHolder();
/* 305 */             infoHolder.annotationType = IntegerRange.class;
/* 306 */             infoHolder.paramName = infoAnnotation.paramName();
/* 307 */             infoHolder.defaultValue = infoAnnotation.defaultValue();
/* 308 */             infoHolder.max = infoAnnotation.max();
/* 309 */             infoHolder.min = infoAnnotation.min();
/* 310 */             infoHolder.suggested_start = infoAnnotation.suggested_start();
/* 311 */             infoHolder.suggested_end = infoAnnotation.suggested_end();
/* 312 */             infoHolder.suggested_increment = infoAnnotation.suggested_increment();
/* 313 */             holder = infoHolder;
/* 314 */           } else if (annotationType == RealRange.class) {
/* 315 */             RealRange infoAnnotation = (RealRange)annotation;
/* 316 */             RealRangeHolder infoHolder = new RealRangeHolder();
/* 317 */             infoHolder.annotationType = RealRange.class;
/* 318 */             infoHolder.paramName = infoAnnotation.paramName();
/* 319 */             infoHolder.defaultValue = infoAnnotation.defaultValue();
/* 320 */             infoHolder.max = infoAnnotation.max();
/* 321 */             infoHolder.min = infoAnnotation.min();
/* 322 */             infoHolder.suggested_start = infoAnnotation.suggested_start();
/* 323 */             infoHolder.suggested_end = infoAnnotation.suggested_end();
/* 324 */             infoHolder.suggested_increment = infoAnnotation.suggested_increment();
/* 325 */             infoHolder.precision = infoAnnotation.precision();
/* 326 */             holder = infoHolder;
/*     */           }
/* 328 */           this.parameterAnnotations[i][j] = holder;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 333 */     return this.parameterAnnotations;
/*     */   }
/*     */ 
/*     */   private Holder getParameterInfo(int paramIndex, Class<? extends Object> paramAnnotation, Class<? extends Object> paramExtraAnnotation) {
/* 337 */     if (paramIndex < 0)
/* 338 */       throw new IllegalArgumentException("Index out of bounds");
/* 339 */     int i = 0;
/* 340 */     for (Holder[] annArray : getParameterAnnotations()) {
/* 341 */       for (Holder ann : annArray) {
/* 342 */         if ((ann.annotationType == paramAnnotation) && (paramIndex == i++)) {
/* 343 */           for (Holder annExt : annArray) {
/* 344 */             if (annExt.annotationType == paramExtraAnnotation) {
/* 345 */               return annExt;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 351 */     return null;
/*     */   }
/*     */ 
/*     */   public InputParameterInfoHolder getInputParameterInfo(int paramIndex)
/*     */     throws IllegalArgumentException
/*     */   {
/* 362 */     return (InputParameterInfoHolder)getParameterInfo(paramIndex, InputParameterInfo.class);
/*     */   }
/*     */ 
/*     */   public OutputParameterInfoHolder getOutputParameterInfo(int paramIndex)
/*     */     throws IllegalArgumentException
/*     */   {
/* 373 */     return (OutputParameterInfoHolder)getParameterInfo(paramIndex, OutputParameterInfo.class);
/*     */   }
/*     */ 
/*     */   public OptInputParameterInfoHolder getOptInputParameterInfo(int paramIndex)
/*     */     throws IllegalArgumentException
/*     */   {
/* 384 */     return (OptInputParameterInfoHolder)getParameterInfo(paramIndex, OptInputParameterInfo.class);
/*     */   }
/*     */ 
/*     */   public IntegerListHolder getOptInputIntegerList(int paramIndex)
/*     */     throws IllegalArgumentException
/*     */   {
/* 396 */     return (IntegerListHolder)getParameterInfo(paramIndex, OptInputParameterInfo.class, IntegerList.class);
/*     */   }
/*     */ 
/*     */   public IntegerRangeHolder getOptInputIntegerRange(int paramIndex)
/*     */     throws IllegalArgumentException
/*     */   {
/* 408 */     return (IntegerRangeHolder)getParameterInfo(paramIndex, OptInputParameterInfo.class, IntegerRange.class);
/*     */   }
/*     */ 
/*     */   public RealListHolder getOptInputRealList(int paramIndex)
/*     */     throws IllegalArgumentException
/*     */   {
/* 420 */     return (RealListHolder)getParameterInfo(paramIndex, OptInputParameterInfo.class, RealList.class);
/*     */   }
/*     */ 
/*     */   public RealRangeHolder getOptInputRealRange(int paramIndex)
/*     */     throws IllegalArgumentException
/*     */   {
/* 432 */     return (RealRangeHolder)getParameterInfo(paramIndex, OptInputParameterInfo.class, RealRange.class);
/*     */   }
/*     */ 
/*     */   public void setOptInputParamInteger(int paramIndex, int value)
/*     */     throws IllegalArgumentException
/*     */   {
/* 444 */     OptInputParameterInfoHolder param = getOptInputParameterInfo(paramIndex);
/* 445 */     if (param == null) throw new InternalError("Parameter with specified index was not found");
/* 446 */     if (param.type == OptInputParameterType.TA_OptInput_IntegerList) {
/* 447 */       IntegerListHolder list = getOptInputIntegerList(paramIndex);
/* 448 */       int[] values = list.value;
/* 449 */       for (int i = 0; i < values.length; i++) {
/* 450 */         if (value == values[i]) {
/* 451 */           String strValue = list.string[i];
/*     */ 
/* 459 */           MAType[] fields = MAType.values();
/* 460 */           for (MAType maType : fields)
/* 461 */             if (maType.name().toUpperCase().equals(strValue.toUpperCase())) {
/* 462 */               if (this.callOptInputParams == null) this.callOptInputParams = new Object[getFuncInfo().nbOptInput];
/* 463 */               this.callOptInputParams[paramIndex] = maType;
/* 464 */               return;
/*     */             }
/*     */         }
/*     */       }
/*     */     }
/* 469 */     else if (param.type == OptInputParameterType.TA_OptInput_IntegerRange) {
/* 470 */       IntegerRangeHolder range = getOptInputIntegerRange(paramIndex);
/* 471 */       if ((value >= range.min) && (value <= range.max)) {
/* 472 */         if (this.callOptInputParams == null) this.callOptInputParams = new Object[getFuncInfo().nbOptInput];
/* 473 */         this.callOptInputParams[paramIndex] = Integer.valueOf(value);
/* 474 */         return;
/*     */       }
/*     */     }
/* 477 */     throw new InternalError("Unknown parameter type");
/*     */   }
/*     */ 
/*     */   public void setOptInputParamInteger(int paramIndex, String string)
/*     */     throws IllegalArgumentException
/*     */   {
/*     */     try
/*     */     {
/* 490 */       Integer v = new Integer(string);
/* 491 */       setOptInputParamInteger(paramIndex, v.intValue());
/*     */     } catch (NumberFormatException e) {
/* 493 */       OptInputParameterInfoHolder param = getOptInputParameterInfo(paramIndex);
/* 494 */       if (param == null) throw new InternalError("Parameter with specified index was not found");
/* 495 */       if (param.type != OptInputParameterType.TA_OptInput_IntegerList) throw new InternalError("Unexpected parameter type");
/*     */ 
/* 503 */       MAType[] fields = MAType.values();
/* 504 */       for (MAType value : fields) {
/* 505 */         if (value.name().toUpperCase().equals(string.toUpperCase())) {
/* 506 */           if (this.callOptInputParams == null) this.callOptInputParams = new Object[getFuncInfo().nbOptInput];
/* 507 */           this.callOptInputParams[paramIndex] = value;
/* 508 */           return;
/*     */         }
/*     */       }
/* 511 */       throw new InternalError("Unexpected parameter value");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setOptInputParamReal(int paramIndex, double value)
/*     */     throws IllegalArgumentException
/*     */   {
/* 524 */     OptInputParameterInfoHolder param = getOptInputParameterInfo(paramIndex);
/* 525 */     if (param.type == OptInputParameterType.TA_OptInput_RealList) {
/* 526 */       RealListHolder list = getOptInputRealList(paramIndex);
/* 527 */       for (double entry : list.value)
/* 528 */         if (value == entry) {
/* 529 */           if (this.callOptInputParams == null) this.callOptInputParams = new Object[getFuncInfo().nbOptInput];
/* 530 */           this.callOptInputParams[paramIndex] = Double.valueOf(value);
/* 531 */           return;
/*     */         }
/*     */     }
/* 534 */     else if (param.type == OptInputParameterType.TA_OptInput_RealRange) {
/* 535 */       RealRangeHolder range = getOptInputRealRange(paramIndex);
/* 536 */       if ((value >= range.min) && (value <= range.max)) {
/* 537 */         if (this.callOptInputParams == null) this.callOptInputParams = new Object[getFuncInfo().nbOptInput];
/* 538 */         this.callOptInputParams[paramIndex] = Double.valueOf(value);
/* 539 */         return;
/*     */       }
/*     */     }
/* 542 */     throw new InternalError("Unknown parameter type");
/*     */   }
/*     */ 
/*     */   public void setOptInputParamReal(int paramIndex, String string)
/*     */     throws IllegalArgumentException
/*     */   {
/*     */     try
/*     */     {
/* 555 */       Double v = new Double(string);
/* 556 */       setOptInputParamReal(paramIndex, v.doubleValue());
/*     */     } catch (NumberFormatException e) {
/* 558 */       OptInputParameterInfoHolder param = getOptInputParameterInfo(paramIndex);
/* 559 */       if (param == null) throw new InternalError("Parameter with specified index was not found");
/* 560 */       if (param.type == OptInputParameterType.TA_OptInput_RealList) {
/* 561 */         RealListHolder list = getOptInputRealList(paramIndex);
/* 562 */         for (int i = 0; i < list.string.length; i++) {
/* 563 */           if (string.toUpperCase().equals(list.string[i])) {
/* 564 */             if (this.callOptInputParams == null) this.callOptInputParams = new Object[getFuncInfo().nbOptInput];
/* 565 */             double value = list.value[i];
/* 566 */             this.callOptInputParams[paramIndex] = Double.valueOf(value);
/* 567 */             return;
/*     */           }
/*     */         }
/* 570 */         throw new InternalError("Unexpected parameter value");
/*     */       }
/* 572 */       throw new InternalError("Unexpected parameter type");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setInputParamReal(int paramIndex, Object array)
/*     */     throws IllegalArgumentException, NullPointerException
/*     */   {
/* 586 */     if (array == null) throw new NullPointerException("Array is null");
/* 587 */     InputParameterInfoHolder param = getInputParameterInfo(paramIndex);
/* 588 */     if (param == null) throw new InternalError("Parameter with specified index was not found");
/* 589 */     if (param.type != InputParameterType.TA_Input_Real) throw new InternalError("Unexpected parameter type");
/* 590 */     if (!(array instanceof double[])) throw new IllegalArgumentException("double[] expected");
/* 591 */     if (this.callInputParams == null) this.callInputParams = new Object[getFuncInfo().nbInput];
/* 592 */     this.callInputParams[paramIndex] = array;
/*     */   }
/*     */ 
/*     */   public void setInputParamInteger(int paramIndex, Object array)
/*     */     throws IllegalArgumentException, NullPointerException
/*     */   {
/* 605 */     if (array == null) throw new NullPointerException("Array is null");
/* 606 */     InputParameterInfoHolder param = getInputParameterInfo(paramIndex);
/* 607 */     if (param == null) throw new InternalError("Parameter with specified index was not found");
/* 608 */     if (param.type != InputParameterType.TA_Input_Integer) throw new InternalError("Unexpected parameter type");
/* 609 */     if (!(array instanceof int[])) throw new IllegalArgumentException("int[] expected");
/* 610 */     if (this.callInputParams == null) this.callInputParams = new Object[getFuncInfo().nbInput];
/* 611 */     this.callInputParams[paramIndex] = array;
/*     */   }
/*     */ 
/*     */   public void setInputParamPrice(int paramIndex, double[] open, double[] high, double[] low, double[] close, double[] volume, double[] openInterest)
/*     */     throws IllegalArgumentException, NullPointerException
/*     */   {
/* 634 */     InputParameterInfoHolder param = getInputParameterInfo(paramIndex);
/* 635 */     if (param == null) throw new InternalError("Parameter with specified index was not found");
/* 636 */     if (param.type != InputParameterType.TA_Input_Price) throw new InternalError("Unexpected parameter type");
/* 637 */     if (this.callInputParams == null) this.callInputParams = new Object[getFuncInfo().nbInput];
/* 638 */     this.callInputParams[paramIndex] = new PriceInputParameter(param.flags, open, high, low, close, volume, openInterest);
/*     */   }
/*     */ 
/*     */   public void setOutputParamReal(int paramIndex, Object array)
/*     */     throws IllegalArgumentException, NullPointerException, ClassCastException
/*     */   {
/* 652 */     if (array == null) throw new NullPointerException("Array is null");
/* 653 */     OutputParameterInfoHolder param = getOutputParameterInfo(paramIndex);
/* 654 */     if (param == null) throw new InternalError("Parameter with specified index was not found");
/* 655 */     if (param.type != OutputParameterType.TA_Output_Real) throw new InternalError("Unexpected parameter type");
/* 656 */     if (!(array instanceof double[])) throw new IllegalArgumentException("double[] expected");
/* 657 */     if (this.callOutputParams == null) this.callOutputParams = new Object[getFuncInfo().nbOutput];
/* 658 */     this.callOutputParams[paramIndex] = array;
/*     */   }
/*     */ 
/*     */   public void setOutputParamInteger(int paramIndex, Object array)
/*     */     throws IllegalArgumentException, NullPointerException, ClassCastException
/*     */   {
/* 672 */     if (array == null) throw new NullPointerException("Array is null");
/* 673 */     OutputParameterInfoHolder param = getOutputParameterInfo(paramIndex);
/* 674 */     if (param == null) throw new InternalError("Parameter with specified index was not found");
/* 675 */     if (param.type != OutputParameterType.TA_Output_Integer) throw new InternalError("Unexpected parameter type");
/* 676 */     if (!(array instanceof int[])) throw new IllegalArgumentException("int[] expected");
/* 677 */     if (this.callOutputParams == null) this.callOutputParams = new Object[getFuncInfo().nbOutput];
/* 678 */     this.callOutputParams[paramIndex] = array;
/*     */   }
/*     */ 
/*     */   public static void forEachFunc(TaFuncService service)
/*     */     throws Exception
/*     */   {
/* 689 */     for (TaLibMetaData mi : getAllFuncs().values())
/* 690 */       service.execute(mi);
/*     */   }
/*     */ 
/*     */   public static void forEachGrp(TaGrpService service)
/*     */     throws Exception
/*     */   {
/* 702 */     for (String group : getAllGrps().keySet())
/* 703 */       service.execute(group, (Set)taGrpMap.get(group));
/*     */   }
/*     */ 
/*     */   private Object[] getOptInputParameters()
/*     */   {
/* 708 */     int size = getFuncInfo().nbOptInput;
/* 709 */     if (this.callOptInputParams == null) this.callOptInputParams = new Object[size];
/* 710 */     for (int i = 0; i < size; i++) {
/* 711 */       if (this.callOptInputParams[i] == null) {
/* 712 */         OptInputParameterInfoHolder param = getOptInputParameterInfo(i);
/* 713 */         if (param == null) throw new InternalError("Parameter with specified index was not found");
/* 714 */         if (param.type == OptInputParameterType.TA_OptInput_IntegerList) {
/* 715 */           IntegerListHolder list = getOptInputIntegerList(i);
/* 716 */           this.callOptInputParams[i] = Integer.valueOf(list.defaultValue);
/* 717 */         } else if (param.type == OptInputParameterType.TA_OptInput_IntegerRange) {
/* 718 */           IntegerRangeHolder range = getOptInputIntegerRange(i);
/* 719 */           this.callOptInputParams[i] = Integer.valueOf(range.defaultValue);
/* 720 */         } else if (param.type == OptInputParameterType.TA_OptInput_RealList) {
/* 721 */           RealListHolder list = getOptInputRealList(i);
/* 722 */           this.callOptInputParams[i] = Double.valueOf(list.defaultValue);
/* 723 */         } else if (param.type == OptInputParameterType.TA_OptInput_RealRange) {
/* 724 */           RealRangeHolder range = getOptInputRealRange(i);
/* 725 */           this.callOptInputParams[i] = Double.valueOf(range.defaultValue);
/*     */         } else {
/* 727 */           throw new InternalError("Unknown parameter type");
/*     */         }
/*     */       }
/*     */     }
/* 731 */     return this.callOptInputParams;
/*     */   }
/*     */ 
/*     */   public int getLookback()
/*     */     throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
/*     */   {
/* 747 */     Object[] params = getOptInputParameters();
/* 748 */     return ((Integer)this.lookback.invoke(taCore, params)).intValue();
/*     */   }
/*     */ 
/*     */   public void callFunc(int startIndex, int endIndex, MInteger outBegIdx, MInteger outNbElement)
/*     */     throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
/*     */   {
/* 771 */     int count = 0;
/* 772 */     for (Object item : this.callInputParams) {
/* 773 */       if (PriceInputParameter.class == item.getClass())
/* 774 */         count += ((PriceInputParameter)item).getCount();
/*     */       else {
/* 776 */         count++;
/*     */       }
/*     */     }
/* 779 */     count += this.callOutputParams.length;
/* 780 */     count += this.callOptInputParams.length;
/*     */ 
/* 782 */     Object[] params = new Object[count + 4];
/* 783 */     count = 0;
/* 784 */     params[(count++)] = Integer.valueOf(startIndex);
/* 785 */     params[(count++)] = Integer.valueOf(endIndex);
/*     */ 
/* 787 */     for (Object item : this.callInputParams) {
/* 788 */       if (PriceInputParameter.class == item.getClass()) {
/* 789 */         Object[] objs = ((PriceInputParameter)item).toArrays();
/* 790 */         for (int i = 0; i < objs.length; i++)
/* 791 */           params[(count++)] = objs[i];
/*     */       }
/*     */       else {
/* 794 */         params[(count++)] = item;
/*     */       }
/*     */     }
/*     */ 
/* 798 */     for (Object item : this.callOptInputParams) {
/* 799 */       params[(count++)] = item;
/*     */     }
/*     */ 
/* 802 */     params[(count++)] = outBegIdx;
/* 803 */     params[(count++)] = outNbElement;
/*     */ 
/* 805 */     for (Object item : this.callOutputParams) {
/* 806 */       params[(count++)] = item;
/*     */     }
/*     */ 
/* 809 */     Type[] types = this.function.getGenericParameterTypes();
/* 810 */     if (types.length != params.length) throw new IllegalArgumentException("Illegal number of arguments");
/*     */ 
/* 817 */     this.function.invoke(taCore, params);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.TaLibMetaData
 * JD-Core Version:    0.6.0
 */