/*     */ package com.dukascopy.dds2.greed.connector.parser.util;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.connector.helpers.ArrayHelpers;
/*     */ import com.dukascopy.dds2.greed.connector.helpers.MathHelpers;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class ImportDeclaration
/*     */   implements IDeclaration
/*     */ {
/*     */   String name;
/*     */   String interfaceName;
/*  16 */   public Map<String, IDeclaration> functions = new LinkedHashMap();
/*     */ 
/*     */   public String endText1()
/*     */   {
/*  20 */     StringBuilder buf = new StringBuilder();
/*     */ 
/*  22 */     for (IDeclaration declaration : getFunctions().values()) {
/*  23 */       FunctionDeclaration function = (FunctionDeclaration)declaration;
/*  24 */       String fnDefParam = DeclarationHelpers.functionDefaultParameters(function);
/*     */ 
/*  26 */       if ((fnDefParam != null) && (!fnDefParam.isEmpty())) {
/*  27 */         buf.append(fnDefParam);
/*  28 */         buf.append("{\r\n");
/*  29 */         buf.append(getInterfaceName());
/*  30 */         buf.append(".");
/*  31 */         buf.append(DeclarationHelpers.functionCallDefParam(function));
/*  32 */         buf.append("\r\n}\r\n");
/*     */       }
/*     */ 
/*  35 */       buf.append(DeclarationHelpers.functionOriginalClearDefParam(function));
/*  36 */       buf.append("{\r\n");
/*  37 */       buf.append(getInterfaceName());
/*  38 */       buf.append(".");
/*  39 */       buf.append(DeclarationHelpers.functionCall(function));
/*  40 */       buf.append("\r\n}\r\n");
/*     */     }
/*     */ 
/*  43 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public List<IDeclaration> getChildren()
/*     */   {
/*  49 */     return null;
/*     */   }
/*     */ 
/*     */   public Map<String, IDeclaration> getClasses()
/*     */   {
/*  55 */     return null;
/*     */   }
/*     */ 
/*     */   public Map<String, IDeclaration> getFunctions()
/*     */   {
/*  60 */     return this.functions;
/*     */   }
/*     */   public void addFunction(FunctionDeclaration fn) {
/*  63 */     this.functions.put(fn.getName(), fn);
/*     */   }
/*     */   public FunctionDeclaration getFunction(String name) {
/*  66 */     return (FunctionDeclaration)this.functions.get(name);
/*     */   }
/*     */ 
/*     */   public Map<String, IDeclaration> getVariables()
/*     */   {
/*  71 */     return null;
/*     */   }
/*     */ 
/*     */   public String startText()
/*     */   {
/*  76 */     StringBuilder buf = new StringBuilder();
/*  77 */     for (IDeclaration declaration : getFunctions().values()) {
/*  78 */       FunctionDeclaration function = (FunctionDeclaration)declaration;
/*     */ 
/*  88 */       buf.append("@DllMethod\n");
/*  89 */       buf.append(DeclarationHelpers.functionOriginalClearDefParam(function, false));
/*  90 */       buf.append(";");
/*  91 */       buf.append("\r\n");
/*     */     }
/*  93 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public String getName() {
/*  97 */     return this.name;
/*     */   }
/*     */ 
/*     */   public void setName(String name) {
/* 101 */     this.name = name.trim();
/* 102 */     String tmp = this.name.substring(0, this.name.lastIndexOf("."));
/*     */ 
/* 104 */     if ((tmp.startsWith("\"")) || (tmp.startsWith("'"))) {
/* 105 */       tmp = tmp.substring(1);
/*     */     }
/*     */ 
/* 108 */     if ((tmp.endsWith("\"")) || (tmp.endsWith("'"))) {
/* 109 */       tmp = tmp.substring(0, tmp.length());
/*     */     }
/* 111 */     tmp = tmp.replaceAll("-", "_");
/* 112 */     this.interfaceName = tmp;
/*     */   }
/*     */ 
/*     */   public String getInterfaceName() {
/* 116 */     return this.interfaceName;
/*     */   }
/*     */ 
/*     */   public String endText()
/*     */   {
/* 121 */     StringBuilder result = new StringBuilder("");
/* 122 */     for (IDeclaration declaration : getFunctions().values()) {
/* 123 */       FunctionDeclaration fun = (FunctionDeclaration)declaration;
/*     */ 
/* 125 */       HashMap fnNameList = new HashMap();
/*     */ 
/* 127 */       List instrumentList = DeclarationHelpers.getTypeParams(fun, "string");
/* 128 */       List colorList = DeclarationHelpers.getColorParamList(fun);
/*     */ 
/* 131 */       int[] instrumentIndexes = null;
/* 132 */       if (fun.isDefaultParameters()) {
/* 133 */         if (instrumentList.size() > 0) {
/* 134 */           int factorialIndex = (int)MathHelpers.factorial(instrumentList.size());
/* 135 */           instrumentIndexes = new int[instrumentList.size()];
/* 136 */           Arrays.fill(instrumentIndexes, -1);
/* 137 */           for (int i = 0; i < factorialIndex; i++)
/*     */           {
/* 139 */             while (i < instrumentList.size()) {
/* 140 */               instrumentIndexes[i] = ((Integer)instrumentList.get(i)).intValue();
/* 141 */               result.append(DeclarationHelpers.functionDefaultParameters(fun, instrumentIndexes, fnNameList));
/* 142 */               instrumentIndexes[i] = -1;
/* 143 */               i++;
/*     */             }
/* 145 */             instrumentIndexes[0] = ((Integer)instrumentList.get(0)).intValue();
/* 146 */             while (i < factorialIndex - 1) {
/* 147 */               for (int j = 1; j < instrumentList.size() - 1; j++) {
/* 148 */                 instrumentIndexes[j] = ((Integer)instrumentList.get(j)).intValue();
/* 149 */                 result.append(DeclarationHelpers.functionDefaultParameters(fun, instrumentIndexes, fnNameList));
/* 150 */                 instrumentIndexes = ArrayHelpers.reverse(instrumentIndexes);
/* 151 */                 result.append(DeclarationHelpers.functionDefaultParameters(fun, instrumentIndexes, fnNameList));
/* 152 */                 instrumentIndexes = ArrayHelpers.reverse(instrumentIndexes);
/*     */               }
/* 154 */               i++; i++;
/*     */             }
/* 156 */             instrumentIndexes[(instrumentIndexes.length - 1)] = ((Integer)instrumentList.get(instrumentList.size() - 1)).intValue();
/* 157 */             result.append(DeclarationHelpers.functionDefaultParameters(fun, instrumentIndexes, fnNameList));
/*     */           }
/*     */ 
/* 160 */           int[] emptyInstrumentIndexes = new int[instrumentList.size()];
/* 161 */           Arrays.fill(emptyInstrumentIndexes, -1);
/* 162 */           result.append(DeclarationHelpers.functionDefaultParameters(fun, emptyInstrumentIndexes, fnNameList));
/*     */         } else {
/* 164 */           result.append(DeclarationHelpers.functionDefaultParameters(fun, -1));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 169 */       if ((fun.getParams() != null) && (fun.getParams().size() > 0)) {
/* 170 */         String functionOriginalClearDefParam = DeclarationHelpers.functionOriginalClearDefParam(fun, instrumentList, colorList, false);
/* 171 */         if (!functionOriginalClearDefParam.isEmpty()) {
/* 172 */           result.append(functionOriginalClearDefParam);
/* 173 */           result.append(" throws JFException {\r\n");
/* 174 */           for (int i = 0; i < instrumentList.size(); i++) {
/* 175 */             VariableDeclaration instrumentParam = (VariableDeclaration)fun.getParams().get(((Integer)instrumentList.get(i)).intValue());
/* 176 */             result.append("\r\n if(");
/* 177 */             result.append(instrumentParam.getName());
/* 178 */             result.append("==null) {\r\n");
/* 179 */             result.append(instrumentParam.getName());
/* 180 */             result.append("=Instrument();\r\n}\r\n");
/*     */           }
/*     */ 
/* 183 */           if (!fun.getType().equals("void")) {
/* 184 */             result.append("return ");
/*     */           }
/* 186 */           result.append(getInterfaceName());
/* 187 */           result.append(".");
/* 188 */           result.append(DeclarationHelpers.functionCallDefParam(fun, -1, instrumentList, colorList));
/* 189 */           result.append(";}\r\n");
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 206 */     return result.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.ImportDeclaration
 * JD-Core Version:    0.6.0
 */