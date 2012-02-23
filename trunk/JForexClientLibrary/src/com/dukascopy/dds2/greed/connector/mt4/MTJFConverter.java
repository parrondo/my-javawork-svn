/*     */ package com.dukascopy.dds2.greed.connector.mt4;
/*     */ 
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.dds2.greed.connector.IConverter;
/*     */ import com.dukascopy.dds2.greed.connector.helpers.ConverterHelpers;
/*     */ import com.dukascopy.dds2.greed.connector.helpers.ExternalEngine;
/*     */ import com.dukascopy.dds2.greed.connector.parser.javacc.CPPParser;
/*     */ import com.dukascopy.dds2.greed.connector.parser.javacc.ParserManager;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class MTJFConverter
/*     */   implements IConverter
/*     */ {
/*  28 */   private static Logger log = LoggerFactory.getLogger(MTJFConverter.class);
/*     */ 
/*  31 */   public static String variableName = "[\\p{Alpha}_][a-zA-Z0-9_.@$?[^\\p{ASCII}]]*";
/*     */ 
/*  33 */   public static String asciiVariableName = "[\\p{Alpha}_][a-zA-Z0-9_.@$?]*";
/*  34 */   public static String notVariableName = "[^a-zA-Z0-9_.(@$?]";
/*     */   private static final String OPERATION_PATTERN = "[!#$%&*+,-./:;<=>?@\\^_|~()]";
/*  37 */   private ExternalEngine currentExternalEngine = ExternalEngine.MT4STRATEGY;
/*     */ 
/*  39 */   private List<String> errorLines = new ArrayList();
/*  40 */   private List<String> warningLines = new ArrayList();
/*  41 */   private List<String> allLines = new ArrayList();
/*  42 */   private String currentIncludeDir = "./";
/*     */ 
/*  46 */   static Pattern errorPattern = null;
/*  47 */   static Pattern warningPattern = null;
/*  48 */   String encoding = "UTF-8";
/*  49 */   String mt4headerPath = "./rc/converter/include/";
/*     */ 
/*  51 */   StringBuilder buff = new StringBuilder();
/*  52 */   private CPPParser cppParser = null;
/*     */ 
/*     */   public MTJFConverter(ExternalEngine externalEngine) {
/*  55 */     this.currentExternalEngine = externalEngine;
/*  56 */     init();
/*     */   }
/*     */ 
/*     */   public MTJFConverter() {
/*  60 */     init();
/*     */   }
/*     */ 
/*     */   private void init()
/*     */   {
/*  65 */     this.encoding = System.getProperty("encoding.codepage", this.encoding);
/*     */   }
/*     */   public void setParser(CPPParser parser) {
/*  68 */     this.cppParser = parser;
/*     */   }
/*     */ 
/*     */   private String removeComment(String src) {
/*  72 */     String result = src;
/*  73 */     if (src.indexOf("//") > 0) {
/*  74 */       result = src.substring(0, src.indexOf("//"));
/*     */     }
/*  76 */     if (src.indexOf("/*") > 0) {
/*  77 */       result = src.substring(0, src.indexOf("/*"));
/*     */     }
/*     */ 
/*  80 */     return result;
/*     */   }
/*     */ 
/*     */   private StringBuilder getIncludes(StringBuilder src, Map<String, String> includeList, String currentDir)
/*     */     throws Exception
/*     */   {
/*  87 */     return getIncludes(src, includeList, currentDir, ExternalEngine.MT4STRATEGY);
/*     */   }
/*     */   private StringBuilder getIncludes(StringBuilder src, Map<String, String> includeList, String currentDir, ExternalEngine engine) throws Exception {
/*  90 */     StringBuilder buff = new StringBuilder();
/*  91 */     includeList.put("stdlib.mqh", "");
/*  92 */     includeList.put("stderror.mqh", "");
/*  93 */     includeList.put("WinUser32.mqh", "");
/*  94 */     String[] groups = getGroupsRegExp(src, "#include\\p{Blank}([\\p{Print}]+)");
/*  95 */     for (String group : groups) {
/*  96 */       String include = null;
/*  97 */       group = removeComment(group).trim();
/*  98 */       if (group.endsWith("\"")) {
/*  99 */         include = group.substring(group.indexOf("\"") + 1, group.length() - 1);
/*     */       }
/* 101 */       if (group.endsWith(">")) {
/* 102 */         include = group.substring(group.indexOf("<") + 1, group.length() - 1);
/*     */       }
/* 104 */       if (!includeList.containsKey(include)) {
/* 105 */         includeList.put(include, "");
/* 106 */         if (!currentDir.endsWith("\\")) {
/* 107 */           currentDir = new StringBuilder().append(currentDir).append("\\").toString();
/*     */         }
/*     */ 
/* 110 */         File includeFile = new File(new StringBuilder().append(currentDir).append(include).toString());
/* 111 */         if (!includeFile.exists()) {
/* 112 */           includeFile = new File(new StringBuilder().append(this.mt4headerPath).append(include).toString());
/*     */         }
/* 114 */         if (includeFile.exists())
/*     */         {
/* 116 */           StringBuilder includeSrc = new StringBuilder(new String(ConverterHelpers.readFile(includeFile), this.encoding));
/* 117 */           includeList.put(include, includeSrc.toString());
/* 118 */           StringBuilder withoutImports = new StringBuilder();
/*     */ 
/* 120 */           StringBuilder imports = ConverterHelpers.convertImportToInterface(includeSrc, withoutImports);
/* 121 */           StringBuilder classes = convertIncludeToClass(withoutImports, currentDir, engine);
/*     */ 
/* 123 */           includeList.put(include, includeSrc.toString());
/* 124 */           int index = src.indexOf(group);
/* 125 */           src.delete(index, index + group.length());
/* 126 */           src.insert(index, includeSrc);
/*     */ 
/* 130 */           buff.append(imports);
/* 131 */           buff.append(classes);
/*     */         }
/*     */         else {
/* 134 */           throw new JFException(new StringBuilder().append("Include file [").append(include).append("] not found in directory [").append(currentDir).append("].").toString());
/*     */         }
/*     */       }
/*     */     }
/* 138 */     return buff;
/*     */   }
/*     */ 
/*     */   public boolean convert(StringBuilder src, String javaClassName) throws JFException
/*     */   {
/* 143 */     return convert(src, javaClassName, getCurrentIncludePath(), this.currentExternalEngine);
/*     */   }
/*     */ 
/*     */   public boolean convert(StringBuilder src, String javaClassName, String currentDir) throws JFException {
/* 147 */     return convert(src, javaClassName, currentDir, this.currentExternalEngine);
/*     */   }
/*     */ 
/*     */   public boolean convert(StringBuilder src, String javaClassName, String currentDir, ExternalEngine engine) throws JFException
/*     */   {
/* 152 */     boolean result = true;
/*     */     try
/*     */     {
/* 155 */       StringBuilder mql = firstStepConvertContent(src, currentDir, engine);
/*     */ 
/* 157 */       this.buff.setLength(0);
/* 158 */       this.buff.append(convertContent(mql, javaClassName, currentDir, engine));
/*     */     } catch (UnsupportedEncodingException e) {
/* 160 */       result = false;
/* 161 */       throw new JFException(e);
/*     */     } catch (Exception e) {
/* 163 */       e.printStackTrace();
/* 164 */       result = false;
/* 165 */       throw new JFException(e);
/*     */     }
/* 167 */     return result;
/*     */   }
/*     */ 
/*     */   public StringBuilder convert(File f, String prefClassName) throws JFException {
/* 171 */     return convert(f, prefClassName, ExternalEngine.MT4STRATEGY);
/*     */   }
/*     */ 
/*     */   public StringBuilder convert(File f, String prefClassName, ExternalEngine engine) throws JFException
/*     */   {
/* 176 */     boolean result = true;
/* 177 */     StringBuilder buf = new StringBuilder();
/*     */     try
/*     */     {
/* 180 */       String currentDir = f.getPath().substring(0, f.getPath().length() - f.getName().length());
/* 181 */       StringBuilder mql = new StringBuilder(new String(ConverterHelpers.readFile(f), this.encoding));
/* 182 */       buf.setLength(0);
/* 183 */       buf.append(convertContent(mql, prefClassName, currentDir, engine));
/*     */     }
/*     */     catch (UnsupportedEncodingException e) {
/* 186 */       result = false;
/* 187 */       throw new JFException(e);
/*     */     } catch (Exception e) {
/* 189 */       result = false;
/* 190 */       throw new JFException(e);
/*     */     }
/* 192 */     return buf;
/*     */   }
/*     */ 
/*     */   public boolean convert(File f)
/*     */     throws JFException
/*     */   {
/* 198 */     return convert(f, ExternalEngine.MT4STRATEGY);
/*     */   }
/*     */ 
/*     */   public boolean convert(File f, ExternalEngine engine) throws JFException
/*     */   {
/* 203 */     boolean result = true;
/*     */     try
/*     */     {
/* 206 */       String currentDir = f.getAbsolutePath().substring(0, f.getAbsolutePath().length() - f.getName().length());
/* 207 */       StringBuilder mql = new StringBuilder(new String(ConverterHelpers.readFile(f), this.encoding));
/* 208 */       this.buff.setLength(0);
/* 209 */       this.buff.append(convertContent(mql, f.getName().substring(0, f.getName().length() - 4), currentDir, engine));
/*     */     }
/*     */     catch (UnsupportedEncodingException e) {
/* 212 */       result = false;
/* 213 */       throw new JFException(e);
/*     */     } catch (Exception e) {
/* 215 */       result = false;
/* 216 */       throw new JFException(e);
/*     */     }
/* 218 */     return result;
/*     */   }
/*     */ 
/*     */   public StringBuilder getConvertionResult()
/*     */   {
/* 223 */     return this.buff;
/*     */   }
/*     */ 
/*     */   public String[] getCompilationErrors()
/*     */   {
/* 228 */     return (String[])this.errorLines.toArray(new String[this.errorLines.size()]);
/*     */   }
/*     */ 
/*     */   public String[] getCompilationWarnings()
/*     */   {
/* 233 */     return (String[])this.warningLines.toArray(new String[this.warningLines.size()]);
/*     */   }
/*     */ 
/*     */   public String[] getCompilationResultLines()
/*     */   {
/* 238 */     return (String[])this.allLines.toArray(new String[this.warningLines.size()]);
/*     */   }
/*     */ 
/*     */   public StringBuilder getCompilationResultLinesAsObject() {
/* 242 */     StringBuilder strBuff = new StringBuilder();
/* 243 */     for (String str : this.allLines) {
/* 244 */       strBuff.append(new StringBuilder().append(str).append("\n").toString());
/*     */     }
/* 246 */     return strBuff;
/*     */   }
/*     */ 
/*     */   private String replaceRegExp(String src, String regexpString, String replacement) throws Exception
/*     */   {
/* 251 */     Pattern pattern = Pattern.compile(regexpString, 32);
/* 252 */     Matcher matcher = pattern.matcher(src);
/* 253 */     return matcher.replaceAll(replacement);
/*     */   }
/*     */ 
/*     */   private StringBuilder replaceRegExp(StringBuilder src, String regexpString, String replacement) throws Exception {
/* 257 */     Pattern pattern = Pattern.compile(regexpString, 32);
/* 258 */     Matcher matcher = pattern.matcher(src);
/* 259 */     return new StringBuilder(matcher.replaceAll(replacement));
/*     */   }
/*     */ 
/*     */   private String genTemplate(String name, String content, ExternalEngine engine) {
/* 263 */     String version = getClass().getPackage().getImplementationVersion();
/* 264 */     if (version == null) {
/* 265 */       version = "";
/*     */     }
/* 267 */     StringBuilder template = new StringBuilder();
/* 268 */     template.append("package jforex.converted;\n");
/* 269 */     template.append("import java.awt.Color;\n");
/* 270 */     template.append("import com.dukascopy.api.*;\n");
/* 271 */     template.append("public class ");
/* 272 */     template.append(name);
/* 273 */     template.append(" extends ");
/* 274 */     if ((engine != ExternalEngine.MT4INDICATOR) && (engine != ExternalEngine.MT5INDICATOR))
/* 275 */       template.append("ConnectorStrategy");
/*     */     else {
/* 277 */       template.append("ConnectorIndicator");
/*     */     }
/* 279 */     template.append(" {\n");
/* 280 */     template.append(content);
/* 281 */     template.append("\n");
/* 282 */     if ((engine != ExternalEngine.MT4INDICATOR) && (engine != ExternalEngine.MT5INDICATOR)) {
/* 283 */       template.append("public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException{}\r\n");
/*     */     }
/* 285 */     template.append("/**/};");
/* 286 */     return template.toString();
/*     */   }
/*     */ 
/*     */   private StringBuilder firstStepConvertContent(StringBuilder mql, String currentDir, ExternalEngine engine) throws Exception {
/* 290 */     mql = replaceRegExp(mql, "FALSE", "false");
/* 291 */     mql = replaceRegExp(mql, "TRUE", "true");
/* 292 */     mql = replaceRegExp(mql, "False", "false");
/* 293 */     mql = replaceRegExp(mql, "True", "true");
/*     */ 
/* 296 */     mql = replaceRegExp(mql, "#import", "#import   ");
/*     */ 
/* 299 */     mql = replaceRegExp(mql, "([\\p{Alpha}])\\x3A\\x5C([\\p{Alnum}])", "$1:\\\\\\\\$2");
/* 300 */     mql = replaceRegExp(mql, "([\\p{Alnum}])\\x5C([\\p{Alnum}])", "$1\\\\\\\\$2");
/*     */ 
/* 303 */     if ((engine.equals(ExternalEngine.MT4STRATEGY)) || (engine.equals(ExternalEngine.MT4INDICATOR))) {
/* 304 */       mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("string\\p{Blank}*\\p{Punct}*\\p{Blank}+(").append(variableName).append(")").toString());
/* 305 */       mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("int\\p{Blank}+(").append(variableName).append(")").toString());
/* 306 */       mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("long\\p{Blank}+(").append(variableName).append(")").toString());
/* 307 */       mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("double\\p{Blank}+(").append(variableName).append(")").toString());
/* 308 */       mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("bool\\p{Blank}+(").append(variableName).append(")").toString());
/* 309 */       mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("datetime\\p{Blank}+(").append(variableName).append(")").toString());
/* 310 */       mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("color\\p{Blank}+(").append(variableName).append(")").toString());
/* 311 */       mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("void\\p{Blank}+(").append(variableName).append(")").toString());
/* 312 */       mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("#define\\p{Blank}+(").append(variableName).append(")").toString());
/* 313 */       mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("\\,\\p{Blank}*(").append(variableName).append(")\\p{Blank}*\\,").toString());
/* 314 */       mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("\\,\\p{Blank}*(").append(variableName).append(")\\p{Blank}*\\=").toString());
/* 315 */       mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("\\,\\p{Blank}*(").append(variableName).append(")\\p{Blank}*\\;").toString());
/* 316 */       mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("\\,\\p{Blank}*(").append(variableName).append(")\\p{Blank}*").toString());
/*     */ 
/* 318 */       mql = replaceRegExp(mql, "\\p{Blank}\\p{Punct}x", " 0x");
/*     */ 
/* 320 */       mql = replaceRegExp(mql, "(:?\\p{Blank}|:?\\p{Punct})(new)(:?\\p{Blank}|:?\\p{Punct})", "$1new_value$3");
/*     */ 
/* 322 */       mql = replaceRegExp(mql, "([\\p{Punct}*\\p{Blank}*]*)Time\\p{Blank}*\\[([\\p{Alnum}[!#$%&*+,-./:;<=>?@\\^_|~()]\\p{Blank}]+)\\]", "$1Time\\($2\\)");
/* 323 */       mql = replaceRegExp(mql, "([\\p{Punct}*\\p{Blank}*]*)Close\\p{Blank}*\\[([\\p{Alnum}[!#$%&*+,-./:;<=>?@\\^_|~()]\\p{Blank}]+)\\]", "$1Close\\($2\\)");
/* 324 */       mql = replaceRegExp(mql, "([\\p{Punct}*\\p{Blank}*]*)High\\p{Blank}*\\[([\\p{Alnum}[!#$%&*+,-./:;<=>?@\\^_|~()]\\p{Blank}]+)\\]", "$1High\\($2\\)");
/* 325 */       mql = replaceRegExp(mql, "([\\p{Punct}*\\p{Blank}*]*)Open\\p{Blank}*\\[([\\p{Alnum}[!#$%&*+,-./:;<=>?@\\^_|~()]\\p{Blank}]+)\\]", "$1Open\\($2\\)");
/* 326 */       mql = replaceRegExp(mql, "([\\p{Punct}*\\p{Blank}*]*)Low\\p{Blank}*\\[([\\p{Alnum}[!#$%&*+,-./:;<=>?@\\^_|~()]\\p{Blank}]+)\\]", "$1Low\\($2\\)");
/* 327 */       mql = replaceRegExp(mql, "([\\p{Punct}*\\p{Blank}*]*)Volume\\p{Blank}*\\[([\\p{Alnum}[!#$%&*+,-./:;<=>?@\\^_|~()]\\p{Blank}]+)\\]", "$1Volume\\($2\\)");
/*     */     }
/*     */ 
/* 333 */     return mql;
/*     */   }
/*     */ 
/*     */   private StringBuilder convertContent(StringBuilder mql, String currentDir, ExternalEngine engine) throws Exception {
/* 337 */     mql = replaceRegExp(mql, "#property", "//#property");
/* 338 */     mql = replaceRegExp(mql, "#include", "//#include");
/*     */ 
/* 341 */     mql = replaceRegExp(mql, "FALSE", "false");
/* 342 */     mql = replaceRegExp(mql, "TRUE", "true");
/* 343 */     mql = replaceRegExp(mql, "False", "false");
/* 344 */     mql = replaceRegExp(mql, "True", "true");
/*     */ 
/* 346 */     mql = replaceRegExp(mql, "NULL", "null");
/* 347 */     mql = replaceRegExp(mql, "static\\p{Blank}*string", "String");
/* 348 */     mql = replaceRegExp(mql, "static\\p{Blank}*int", "int");
/* 349 */     mql = replaceRegExp(mql, "static\\p{Blank}*long", "long");
/* 350 */     mql = replaceRegExp(mql, "static\\p{Blank}*double", "double");
/* 351 */     mql = replaceRegExp(mql, "static\\p{Blank}+bool\\p{Blank}*", "boolean");
/* 352 */     mql = replaceRegExp(mql, new StringBuilder().append("string\\p{Blank}+(").append(variableName).append(")").toString(), "String $1");
/* 353 */     mql = replaceRegExp(mql, "string\\[", "String[");
/* 354 */     mql = replaceRegExp(mql, new StringBuilder().append("bool\\p{Blank}+(").append(variableName).append(")").toString(), "boolean $1");
/* 355 */     mql = replaceRegExp(mql, new StringBuilder().append("bool\\p{Blank}+(").append(variableName).append(")\\p{Blank}*\\[").toString(), "boolean $1[");
/* 356 */     mql = replaceRegExp(mql, "bool\\p{Blank}*\\[", "boolean[");
/*     */ 
/* 359 */     if ((engine.equals(ExternalEngine.MT4STRATEGY)) || (engine.equals(ExternalEngine.MT4INDICATOR))) {
/* 360 */       mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("extern\\s+String\\s+(").append(variableName).append(")").toString());
/* 361 */       mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("extern\\s+int\\s+(").append(variableName).append(")").toString());
/* 362 */       mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("extern\\s+long\\s+(").append(variableName).append(")").toString());
/* 363 */       mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("extern\\s+double\\s+(").append(variableName).append(")").toString());
/* 364 */       mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("extern\\s+Boolean\\s+(").append(variableName).append(")").toString());
/*     */     }
/*     */ 
/* 367 */     mql = replaceRegExp(mql, "extern\\p{Blank}*datetime ", "@Configurable\\(value=\"\", datetimeAsLong=true\\) public long ");
/* 368 */     if ((engine == ExternalEngine.MT4INDICATOR) || (engine == ExternalEngine.MT5INDICATOR)) {
/* 369 */       mql = replaceRegExp(mql, "extern\\p{Blank}*((?:long)|(?:int)|(?:double)|(?:bool)|(?:Color)|(?:color))", "@Configurable\\(\"\"\\) public $1");
/* 370 */       mql = replaceRegExp(mql, "extern\\p{Blank}*string", "public String");
/* 371 */       mql = replaceRegExp(mql, "extern\\p{Blank}*String", "public String");
/*     */     } else {
/* 373 */       mql = replaceRegExp(mql, "extern\\p{Blank}*((?:long)|(?:int)|(?:double)|(?:bool)|(?:string)|(?:Color)|(?:String)|(?:color))", "@Configurable\\(\"\"\\) public $1");
/*     */     }
/*     */ 
/* 377 */     mql = replaceRegExp(mql, "object\\p{Blank}", "void ");
/*     */ 
/* 380 */     mql = replaceRegExp(mql, new StringBuilder().append("color\\p{Blank}+(").append(variableName).append(")").toString(), "Color $1");
/* 381 */     mql = replaceRegExp(mql, new StringBuilder().append("datetime\\p{Blank}(").append(variableName).append(")").toString(), "long $1");
/* 382 */     mql = replaceRegExp(mql, "datetime\\[", " long[");
/*     */ 
/* 384 */     mql = replaceRegExp(mql, new StringBuilder().append("#define\\s*(").append(variableName).append(")\\s*\"([\\p{Print}&&[^\"]]+)\"").toString(), "String $1 = \"$2\";");
/*     */ 
/* 386 */     mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("\\p{Blank}*String\\p{Blank}+(").append(variableName).append(")").toString());
/*     */ 
/* 388 */     mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("\\p{Blank}*String\\p{Blank}*\\&\\p{Blank}*(").append(variableName).append(")\\p{Blank}*").toString());
/*     */ 
/* 390 */     mql = replaceRegExp(mql, new StringBuilder().append("\\p{Blank}*String\\p{Blank}*\\p{Punct}\\p{Blank}*(").append(variableName).append(")\\p{Blank}\\[\\p{Blank}*([\\p{Print}]*)\\p{Blank}*\\]").toString(), "String[$2] $1");
/*     */ 
/* 393 */     mql = replaceRegExp(mql, new StringBuilder().append("\\p{Blank}*String\\p{Blank}*(").append(variableName).append(")\\s*\\[\\s*([\\p{Print}]+)\\s*\\]").toString(), "String[] $1 = new String[$2];");
/*     */ 
/* 395 */     mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("String\\s*\\p{Punct}\\p{Punct}\\s*(").append(variableName).append(")").toString());
/* 396 */     mql = replaceRegExp(mql, new StringBuilder().append("#define\\s*(").append(variableName).append(")\\s+(\\d+\\.+\\d+)").toString(), "double $1 = $2;");
/* 397 */     mql = replaceRegExp(mql, new StringBuilder().append("#define\\s*(").append(variableName).append(")\\s+(\\d+)").toString(), "int $1 = $2;");
/*     */ 
/* 400 */     mql = replaceRegExp(mql, "C'(\\d+),\\s*(\\d+),\\s*(\\d+)'\\s*;", "new Color($1,$2,$3);");
/*     */ 
/* 410 */     List allInstruments = new ArrayList();
/*     */ 
/* 412 */     allInstruments.add("AUDJPY");
/* 413 */     allInstruments.add("AUDNZD");
/* 414 */     allInstruments.add("AUDUSD");
/* 415 */     allInstruments.add("CADJPY");
/* 416 */     allInstruments.add("CHFJPY");
/* 417 */     allInstruments.add("EURAUD");
/* 418 */     allInstruments.add("EURCAD");
/* 419 */     allInstruments.add("EURCHF");
/* 420 */     allInstruments.add("EURDKK");
/* 421 */     allInstruments.add("EURGBP");
/* 422 */     allInstruments.add("EURHKD");
/* 423 */     allInstruments.add("EURJPY");
/* 424 */     allInstruments.add("EURNOK");
/* 425 */     allInstruments.add("EURSEK");
/* 426 */     allInstruments.add("EURUSD");
/* 427 */     allInstruments.add("GBPCHF");
/* 428 */     allInstruments.add("GBPJPY");
/* 429 */     allInstruments.add("GBPUSD");
/* 430 */     allInstruments.add("NZDUSD");
/* 431 */     allInstruments.add("USDCAD");
/* 432 */     allInstruments.add("USDCHF");
/* 433 */     allInstruments.add("USDDKK");
/* 434 */     allInstruments.add("USDHKD");
/* 435 */     allInstruments.add("USDJPY");
/* 436 */     allInstruments.add("USDMXN");
/* 437 */     allInstruments.add("USDNOK");
/* 438 */     allInstruments.add("USDSEK");
/* 439 */     allInstruments.add("USDSGD");
/* 440 */     allInstruments.add("USDTRY");
/*     */ 
/* 442 */     for (String instrument : allInstruments)
/*     */     {
/* 446 */       mql = replaceRegExp(mql, new StringBuilder().append("\"").append(instrument).append("\"").toString(), new StringBuilder().append("\"").append(instrument.substring(0, 3)).append("/").append(instrument.substring(3, 6)).append("\"").toString());
/*     */     }
/*     */ 
/* 451 */     mql = replaceRegExp(mql, "([^\\p{Alnum}+])Symbol\\p{Blank}*\\(\\p{Blank}*\\)", "$1Instrument()");
/* 452 */     mql = replaceRegExp(mql, new StringBuilder().append("((?:double)|(?:int))\\s+(").append(variableName).append(")\\s*;").toString(), "$1 $2 = 0;");
/* 453 */     mql = replaceRegExp(mql, new StringBuilder().append("boolean\\s+(").append(variableName).append(");").toString(), "boolean $1 = false;");
/*     */ 
/* 457 */     mql = replaceRegExp(mql, new StringBuilder().append("((?:long)|(?:int)|(?:double)|(?:String)|(?:Color))\\s+(").append(variableName).append(")\\[(\\d+)\\]\\[(\\d+)\\]\\[(\\d+)\\]").toString(), "$1[][][] $2 = new $1[$3][$4][$5]");
/* 458 */     mql = replaceRegExp(mql, new StringBuilder().append("((?:long)|(?:int)|(?:double)|(?:String)|(?:Color))\\s+(").append(variableName).append(")\\[(\\d+)\\,(\\d+)\\,(\\d+)\\]").toString(), "$1[][][] $2 = new $1[$3][$4][$5]");
/*     */ 
/* 460 */     mql = replaceRegExp(mql, new StringBuilder().append("((?:long)|(?:int)|(?:double)|(?:String)|(?:Color))\\s+(").append(variableName).append(")\\[(\\d+)\\]\\[(\\d+)\\]").toString(), "$1[][] $2 = new $1[$3][$4]");
/* 461 */     mql = replaceRegExp(mql, new StringBuilder().append("((?:long)|(?:int)|(?:double)|(?:String)|(?:Color))\\s+(").append(variableName).append(")\\[(\\d+)\\,(\\d+)\\]").toString(), "$1[][] $2 = new $1[$3][$4]");
/*     */ 
/* 463 */     mql = replaceRegExp(mql, new StringBuilder().append("((?:long)|(?:int)|(?:double)|(?:String)|(?:Color))\\s+(").append(variableName).append(")\\[(\\d+)\\]").toString(), "$1[] $2 = new $1[$3]");
/* 464 */     mql = replaceRegExp(mql, new StringBuilder().append("((?:long)|(?:int)|(?:double)|(?:String)|(?:Color))\\s+(").append(variableName).append(")\\[\\]").toString(), "$1[] $2 = null");
/*     */ 
/* 467 */     mql = replaceRegExp(mql, "((?:void)|(?:int))\\s+((?:deinit)|(?:init)|(?:start))\\p{Blank}*\\(\\)", "int $2()");
/*     */ 
/* 471 */     mql = replaceRegExp(mql, "((?:void)|(?:int))\\s+((?:OnDeinit)|(?:OnInit)|(?:OnStart))\\p{Blank}*\\(\\)", "int $2()");
/*     */ 
/* 473 */     if ((engine.equals(ExternalEngine.MT4STRATEGY)) || (engine.equals(ExternalEngine.MT4INDICATOR))) {
/* 474 */       mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("boolean\\s+(").append(variableName).append(")").toString());
/* 475 */       mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("int\\s+(").append(variableName).append(")").toString());
/* 476 */       mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("long\\s+(").append(variableName).append(")").toString());
/* 477 */       mql = normaliseAndReplaceRegExp(mql, new StringBuilder().append("double\\s+(").append(variableName).append(")").toString());
/*     */     }
/* 479 */     return mql;
/*     */   }
/*     */ 
/*     */   public StringBuilder parse(InputStream is)
/*     */     throws IOException, ClassNotFoundException, JFException
/*     */   {
/* 490 */     BufferedInputStream bis = new BufferedInputStream(is);
/*     */ 
/* 492 */     StringBuilder buf = new StringBuilder(ConverterHelpers.convertStreamToString(bis, this.encoding));
/* 493 */     return parse(buf);
/*     */   }
/*     */ 
/*     */   public StringBuilder parse(StringBuilder src) throws IOException, ClassNotFoundException, JFException {
/* 497 */     return parse(src, this.currentExternalEngine);
/*     */   }
/*     */ 
/*     */   public StringBuilder parse(StringBuilder src, ExternalEngine engine) throws IOException, ClassNotFoundException, JFException {
/* 501 */     StringBuilder result = new StringBuilder();
/* 502 */     ParserManager manager = new ParserManager();
/* 503 */     src.append("\r\n");
/* 504 */     result.append(manager.parse(src, engine, this.encoding));
/* 505 */     return result;
/*     */   }
/*     */ 
/*     */   public StringBuilder parse(File path) throws IOException, JFException {
/* 509 */     StringBuilder result = null;
/*     */     try {
/* 511 */       result = parse(new FileInputStream(path));
/*     */     } catch (ClassNotFoundException e) {
/* 513 */       throw new JFException(e);
/*     */     }
/*     */     catch (FileNotFoundException e) {
/* 516 */       throw new JFException(e);
/*     */     }
/*     */     catch (JFException e) {
/* 519 */       throw new JFException(e);
/*     */     }
/* 521 */     return result;
/*     */   }
/*     */ 
/*     */   private String convertContent(StringBuilder mql, String javaClassName, String currentDir, ExternalEngine engine) throws Exception {
/* 525 */     mql = firstStepConvertContent(mql, currentDir, engine);
/* 526 */     Map includeList = new HashMap();
/* 527 */     StringBuilder mqheaders = getIncludes(mql, includeList, currentDir, engine);
/*     */ 
/* 551 */     StringBuilder withoutImports = new StringBuilder();
/*     */ 
/* 553 */     mql = parse(mql, engine);
/* 554 */     withoutImports.setLength(0);
/* 555 */     mqheaders = convertContent(mqheaders, currentDir, engine);
/* 556 */     withoutImports.append(mqheaders);
/* 557 */     withoutImports.append("\n");
/* 558 */     withoutImports.append("\n");
/* 559 */     withoutImports.append(convertContent(mql, currentDir, engine));
/* 560 */     withoutImports.append("\n");
/* 561 */     String result = genTemplate(javaClassName, withoutImports.toString(), engine);
/* 562 */     return result;
/*     */   }
/*     */ 
/*     */   private String getVariableName(String group) {
/* 566 */     int lastSpacePos = group.lastIndexOf(" ");
/* 567 */     if (lastSpacePos < 1) {
/* 568 */       return group.trim();
/*     */     }
/* 570 */     return group.substring(lastSpacePos, group.length()).trim();
/*     */   }
/*     */ 
/*     */   private String[] getGroupsRegExp(String src, String regexpString) {
/* 574 */     List list = new ArrayList();
/* 575 */     Pattern pattern = Pattern.compile(regexpString, 32);
/* 576 */     Matcher matcher = pattern.matcher(src);
/* 577 */     boolean result = matcher.find();
/* 578 */     if (result) {
/*     */       do {
/* 580 */         list.add(matcher.group().trim());
/* 581 */         result = matcher.find();
/* 582 */       }while (result);
/*     */     }
/* 584 */     return (String[])list.toArray(new String[list.size()]);
/*     */   }
/*     */ 
/*     */   private String[] getGroupsRegExp(StringBuilder src, String regexpString) {
/* 588 */     List list = new ArrayList();
/* 589 */     Pattern pattern = Pattern.compile(regexpString, 32);
/* 590 */     Matcher matcher = pattern.matcher(src);
/* 591 */     boolean result = matcher.find();
/* 592 */     if (result) {
/*     */       do {
/* 594 */         list.add(matcher.group().trim());
/* 595 */         result = matcher.find();
/* 596 */       }while (result);
/*     */     }
/* 598 */     return (String[])list.toArray(new String[list.size()]);
/*     */   }
/*     */ 
/*     */   private StringBuilder normaliseAndReplaceRegExp(StringBuilder src, String regexpString)
/*     */     throws Exception
/*     */   {
/* 633 */     StringBuilder buf = src;
/* 634 */     Pattern pattern = Pattern.compile(regexpString, 32);
/* 635 */     Matcher matcher = pattern.matcher(src);
/* 636 */     matcher.reset();
/* 637 */     boolean result = matcher.find();
/*     */ 
/* 639 */     if (result) {
/*     */       do {
/* 641 */         String group = src.substring(matcher.start(1), matcher.end(1));
/* 642 */         String variableName1 = getVariableName(group);
/* 643 */         String normalizedVariableName = ConverterHelpers.normaliseStr(variableName1);
/* 644 */         if (!normalizedVariableName.equals(variableName1)) {
/* 645 */           int pos = 0;
/*     */ 
/* 647 */           int variableCounter = 0;
/* 648 */           while ((pos = buf.indexOf(normalizedVariableName)) > -1) {
/* 649 */             normalizedVariableName = new StringBuilder().append(normalizedVariableName).append(variableCounter).append("").toString();
/* 650 */             variableCounter++;
/*     */           }
/* 652 */           while ((pos = buf.indexOf(variableName1)) > -1) {
/* 653 */             buf = buf.delete(pos, pos + variableName1.length());
/* 654 */             buf = buf.insert(pos, normalizedVariableName);
/*     */           }
/*     */         }
/* 657 */         result = matcher.find();
/* 658 */       }while (result);
/*     */     }
/* 660 */     return buf;
/*     */   }
/*     */ 
/*     */   private StringBuilder convertMTDateToLong(StringBuilder src) throws Exception {
/* 664 */     String regexpString = new StringBuilder().append(variableName).append("\\p{Blank}*").append(variableName).append("\\p{Blank}*").append(variableName).append("\\p{Blank}*").append(variableName).append("\\p{Blank}*=\\p{Blank}*D'([0-9_\\,.:\\p{Blank}]*)'\\p{Blank}*;").toString();
/* 665 */     StringBuilder buf = src;
/* 666 */     Pattern pattern = Pattern.compile(regexpString, 32);
/* 667 */     Matcher matcher = pattern.matcher(src);
/* 668 */     matcher.reset();
/* 669 */     boolean result = matcher.find();
/*     */ 
/* 671 */     if (result) {
/*     */       do {
/* 673 */         String group = src.substring(matcher.start(1), matcher.end(1));
/* 674 */         String variableName1 = getVariableName(group);
/* 675 */         String normalizedVariableName = ConverterHelpers.normaliseStr(variableName1);
/* 676 */         if (!normalizedVariableName.equals(variableName1)) {
/* 677 */           int pos = 0;
/*     */ 
/* 679 */           int variableCounter = 0;
/* 680 */           while ((pos = buf.indexOf(normalizedVariableName)) > -1) {
/* 681 */             normalizedVariableName = new StringBuilder().append(normalizedVariableName).append(variableCounter).append("").toString();
/* 682 */             variableCounter++;
/*     */           }
/* 684 */           while ((pos = buf.indexOf(variableName1)) > -1) {
/* 685 */             buf = buf.delete(pos, pos + variableName1.length());
/* 686 */             buf = buf.insert(pos, normalizedVariableName);
/*     */           }
/*     */         }
/* 689 */         result = matcher.find();
/* 690 */       }while (result);
/*     */     }
/* 692 */     return buf;
/*     */   }
/*     */ 
/*     */   public StringBuilder convertIncludeToClass(StringBuilder includeStr, String currentDir)
/*     */     throws Exception
/*     */   {
/* 704 */     return convertIncludeToClass(includeStr, currentDir, this.currentExternalEngine);
/*     */   }
/*     */ 
/*     */   private StringBuilder convertIncludeToClass(StringBuilder includeStr, String currentDir, ExternalEngine engine) throws Exception
/*     */   {
/* 709 */     String javaClassStr = "";
/* 710 */     String implementations = "extends AbstractBridgeStrategy implements IConst, IColor";
/* 711 */     StringBuilder buff = new StringBuilder(includeStr);
/* 712 */     String[] classesGroup = getClassesGroup(includeStr);
/* 713 */     if ((classesGroup != null) && (classesGroup.length > 0)) {
/* 714 */       String className = classesGroup[0];
/* 715 */       String classNameFn = className.substring("class".length(), className.length()).trim();
/*     */ 
/* 717 */       String[] classFunctionGroups = getGroupsRegExp(includeStr, new StringBuilder().append("\\p{Alnum}+\\s+").append(classNameFn).append("::(\\s*\\p{Alpha}\\p{Alnum}+\\s*)").toString());
/*     */ 
/* 719 */       int functionsBegins = buff.indexOf(classFunctionGroups[0]);
/* 720 */       String classDecl = buff.substring(0, functionsBegins);
/* 721 */       int classEnd = classDecl.lastIndexOf("};");
/* 722 */       buff.delete(classEnd, classEnd + 2);
/* 723 */       for (String func : classFunctionGroups)
/*     */       {
/* 725 */         String funcType = func.substring(0, func.indexOf(classNameFn));
/* 726 */         String funcName = func.substring(func.lastIndexOf("::") + 2, func.length());
/*     */ 
/* 728 */         int funcDeclPos = buff.indexOf(funcName);
/* 729 */         buff.delete(buff.lastIndexOf(funcType, funcDeclPos), buff.indexOf(";", funcDeclPos) + 1);
/*     */       }
/*     */ 
/* 734 */       buff.append("\n}");
/*     */ 
/* 736 */       javaClassStr = buff.toString().replaceAll(new StringBuilder().append(classNameFn).append("::").toString(), " ");
/*     */ 
/* 739 */       javaClassStr = replaceRegExp(javaClassStr, new StringBuilder().append("\\p{Alnum}+\\s+").append(classNameFn).append("\\s*\\(").toString(), new StringBuilder().append(classNameFn).append("(").toString());
/*     */ 
/* 742 */       javaClassStr = replaceRegExp(javaClassStr, new StringBuilder().append("([\\p{Alpha}]+)\\s+~").append(classNameFn).append("\\s*\\(").toString(), new StringBuilder().append("//$1 ~").append(classNameFn).append("(").toString());
/*     */ 
/* 745 */       javaClassStr = replaceRegExp(javaClassStr, "class\\s*(\\s*\\p{Alpha}\\p{Alnum}+\\s*)", new StringBuilder().append("public class $1 ").append(implementations).toString());
/*     */ 
/* 748 */       javaClassStr = replaceRegExp(javaClassStr, "(public:)", "//public:\n");
/*     */ 
/* 750 */       javaClassStr = replaceRegExp(javaClassStr, "(private:)", "//private:\n");
/*     */ 
/* 752 */       javaClassStr = replaceRegExp(javaClassStr, "(protected:)", "//protected:\n");
/*     */ 
/* 755 */       javaClassStr = convertContent(new StringBuilder(javaClassStr), currentDir, engine).toString();
/*     */     }
/* 757 */     return new StringBuilder(javaClassStr);
/*     */   }
/*     */ 
/*     */   public String[] getClassesGroup(String includeStr) {
/* 761 */     String[] classes = getGroupsRegExp(includeStr, "class\\s*(\\s*\\p{Alpha}\\p{Alnum}+\\s*)");
/*     */ 
/* 763 */     return classes;
/*     */   }
/*     */ 
/*     */   public String[] getClassesGroup(StringBuilder includeStr) {
/* 767 */     String[] classes = getGroupsRegExp(includeStr, "class\\s*(\\s*\\p{Alpha}\\p{Alnum}+\\s*)");
/*     */ 
/* 769 */     return classes;
/*     */   }
/*     */ 
/*     */   public String getCurrentIncludePath() {
/* 773 */     return this.currentIncludeDir;
/*     */   }
/*     */ 
/*     */   public void setCurrentIncludePath(String path)
/*     */   {
/* 778 */     this.currentIncludeDir = path;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.mt4.MTJFConverter
 * JD-Core Version:    0.6.0
 */