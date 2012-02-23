/*     */ package com.dukascopy.dds2.greed.connector.parser.util;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class FunctionDeclaration extends Declaration
/*     */ {
/*   8 */   boolean hasReturn = false;
/*   9 */   ClassDeclaration classDeclaration = null;
/*     */ 
/*  11 */   HashMap<String, List<VariableDeclaration>> allDeclaredVariables = new HashMap();
/*     */   ArrayList<VariableDeclaration> params;
/*     */   ArrayList<Integer> instrumentIndexes;
/*  16 */   boolean hasDefaultParameters = false;
/*  17 */   boolean hasBody = false;
/*     */   Class<?> fnType;
/*     */ 
/*     */   public void addDeclaredVariable(VariableDeclaration variable)
/*     */   {
/*  21 */     List list = null;
/*  22 */     if (!this.allDeclaredVariables.containsKey(variable.getName())) {
/*  23 */       list = new ArrayList();
/*  24 */       this.allDeclaredVariables.put(variable.getName(), list);
/*     */     } else {
/*  26 */       list = (List)this.allDeclaredVariables.get(variable.getName());
/*     */     }
/*     */ 
/*  29 */     if (!list.contains(variable))
/*  30 */       list.add(variable);
/*     */   }
/*     */ 
/*     */   public List<VariableDeclaration> getDeclaredVariableList(String variableName)
/*     */   {
/*  35 */     List list = null;
/*  36 */     if (this.allDeclaredVariables.containsKey(variableName)) {
/*  37 */       list = (List)this.allDeclaredVariables.get(variableName);
/*     */     }
/*  39 */     return list;
/*     */   }
/*     */ 
/*     */   public ArrayList<Integer> getInstrumentIndexes()
/*     */   {
/*  44 */     return this.instrumentIndexes;
/*     */   }
/*     */ 
/*     */   public void addInstrumentIndexes(int index) {
/*  48 */     if (this.instrumentIndexes == null) {
/*  49 */       this.instrumentIndexes = new ArrayList();
/*     */     }
/*  51 */     Integer ind = Integer.valueOf(index);
/*  52 */     if (!this.instrumentIndexes.contains(ind))
/*  53 */       this.instrumentIndexes.add(ind);
/*     */   }
/*     */ 
/*     */   public ArrayList<VariableDeclaration> getParams()
/*     */   {
/*  58 */     return this.params;
/*     */   }
/*     */ 
/*     */   public void clearParams() {
/*  62 */     if (this.params != null)
/*  63 */       this.params.clear();
/*     */   }
/*     */ 
/*     */   public void setParams(ArrayList<VariableDeclaration> params) {
/*  67 */     this.params = params;
/*     */   }
/*     */ 
/*     */   public void setParam(VariableDeclaration param) {
/*  71 */     param.setParam(true);
/*  72 */     this.params.add(param);
/*     */   }
/*     */ 
/*     */   public VariableDeclaration getParamByName(String name) {
/*  76 */     VariableDeclaration result = null;
/*  77 */     for (VariableDeclaration param : getParams()) {
/*  78 */       if (param.getName().equals(name)) {
/*  79 */         result = param;
/*  80 */         break;
/*     */       }
/*     */     }
/*  83 */     return result;
/*     */   }
/*     */ 
/*     */   public void setDefaultParameters(boolean b) {
/*  87 */     this.hasDefaultParameters = b;
/*     */   }
/*     */ 
/*     */   public void setFnType(Class<?> fnType) {
/*  91 */     this.fnType = fnType;
/*     */   }
/*     */ 
/*     */   public boolean isDefaultParameters() {
/*  95 */     return this.hasDefaultParameters;
/*     */   }
/*     */ 
/*     */   public Class<?> getFnType() {
/*  99 */     return this.fnType;
/*     */   }
/*     */ 
/*     */   public boolean isHasBody() {
/* 103 */     return this.hasBody;
/*     */   }
/*     */ 
/*     */   public void setHasBody(boolean hasBody) {
/* 107 */     this.hasBody = hasBody;
/*     */   }
/*     */ 
/*     */   public String endText()
/*     */   {
/* 112 */     StringBuilder buf = new StringBuilder();
/*     */ 
/* 114 */     buf.append("return");
/* 115 */     buf.append(" " + getDefaultValue(getType()));
/* 116 */     buf.append(";");
/*     */ 
/* 118 */     buf.append("\r\n}\r\n");
/* 119 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   private String getDefaultValue(String type) {
/* 123 */     String result = "";
/* 124 */     if (type.equals("int"))
/* 125 */       result = "0";
/* 126 */     else if (type.equals("long"))
/* 127 */       result = "0L";
/* 128 */     else if (type.equals("datetime"))
/* 129 */       result = "0L";
/* 130 */     else if (type.equals("Color"))
/* 131 */       result = "IColor.White";
/* 132 */     else if (type.equals("double"))
/* 133 */       result = "0.0";
/* 134 */     else if (type.startsWith("bool"))
/* 135 */       result = "true";
/* 136 */     else if (type.indexOf("tring") > 0) {
/* 137 */       result = "\"\"";
/*     */     }
/* 139 */     return result;
/*     */   }
/*     */ 
/*     */   public String getFunctionDefaultValue() {
/* 143 */     return getDefaultValue(getType());
/*     */   }
/*     */ 
/*     */   public String startText()
/*     */   {
/* 148 */     return "";
/*     */   }
/*     */ 
/*     */   public boolean isHasReturn() {
/* 152 */     return this.hasReturn;
/*     */   }
/*     */ 
/*     */   public void setHasReturn(boolean hasReturn) {
/* 156 */     this.hasReturn = hasReturn;
/*     */   }
/*     */ 
/*     */   public boolean hasNumberParam() {
/* 160 */     boolean result = false;
/* 161 */     for (VariableDeclaration param : getParams()) {
/* 162 */       if ((param.isNumeric()) && (!param.isArray())) {
/* 163 */         result = true;
/* 164 */         break;
/*     */       }
/*     */     }
/* 167 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean containVariable(String name) {
/* 171 */     boolean result = false;
/* 172 */     result = getVariables().containsKey(name);
/* 173 */     if ((!result) && 
/* 174 */       (getParams() != null)) {
/* 175 */       for (VariableDeclaration param : getParams()) {
/* 176 */         if (param.getName().equals(name)) {
/* 177 */           result = true;
/* 178 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 183 */     return result;
/*     */   }
/*     */ 
/*     */   public VariableDeclaration getVariable(String name) {
/* 187 */     VariableDeclaration result = null;
/* 188 */     result = (VariableDeclaration)getVariables().get(name);
/* 189 */     if ((result == null) && 
/* 190 */       (getParams() != null)) {
/* 191 */       for (VariableDeclaration param : getParams()) {
/* 192 */         if (param.getName().equals(name)) {
/* 193 */           result = param;
/* 194 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 199 */     return result;
/*     */   }
/*     */ 
/*     */   public ClassDeclaration getClassDeclaration() {
/* 203 */     return this.classDeclaration;
/*     */   }
/*     */ 
/*     */   public void setClassDeclaration(ClassDeclaration classDeclaration) {
/* 207 */     this.classDeclaration = classDeclaration;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.FunctionDeclaration
 * JD-Core Version:    0.6.0
 */