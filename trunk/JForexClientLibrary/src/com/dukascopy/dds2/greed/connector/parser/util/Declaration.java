/*     */ package com.dukascopy.dds2.greed.connector.parser.util;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.connector.parser.javacc.Token;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public abstract class Declaration
/*     */   implements IDeclaration
/*     */ {
/*     */   int declBegin;
/*     */   int declEnd;
/*  15 */   private String visibility = "protected";
/*  16 */   private String type = "";
/*     */   private String languageType;
/*     */   private String name;
/*  19 */   private int parentLevel = 0;
/*     */   private Declaration parent;
/*  21 */   public Map<String, IDeclaration> variables = new LinkedHashMap();
/*  22 */   public List<IDeclaration> children = new ArrayList();
/*     */   private Token parentToken;
/*     */   private Token firstToken;
/*     */   private Token lastToken;
/*     */ 
/*     */   public boolean isNumeric()
/*     */   {
/*  36 */     boolean result = false;
/*  37 */     if (getType() != null) {
/*  38 */       result |= getType().trim().equals("bool");
/*  39 */       result |= getType().trim().equals("boolean");
/*  40 */       result |= getType().trim().equals("int");
/*  41 */       result |= getType().trim().equals("long");
/*  42 */       result |= getType().trim().equals("double");
/*  43 */       result |= getType().trim().equals("datetime");
/*     */     }
/*  45 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean isNatural() {
/*  49 */     boolean result = false;
/*  50 */     if (getType() != null) {
/*  51 */       result = (getType().equals("int")) || (getType().equals("long"));
/*     */     }
/*  53 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean hasChildren() {
/*  57 */     return (this.children != null) && (this.children.size() > 0);
/*     */   }
/*     */   public boolean hasVariables() {
/*  60 */     return (this.variables != null) && (this.variables.size() > 0);
/*     */   }
/*     */ 
/*     */   public Declaration getParent() {
/*  64 */     return this.parent;
/*     */   }
/*     */ 
/*     */   public void setParent(Declaration parent) {
/*  68 */     this.parent = parent;
/*     */   }
/*     */ 
/*     */   public String getType()
/*     */   {
/*  76 */     return this.type;
/*     */   }
/*     */ 
/*     */   public void setType(String type) {
/*  80 */     if (type.equals("string")) {
/*  81 */       this.type = "String";
/*     */     }
/*     */     else
/*     */     {
/*  85 */       this.type = type;
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getLanguageType() {
/*  90 */     return this.languageType;
/*     */   }
/*     */ 
/*     */   public void setLanguageType(String languageType) {
/*  94 */     this.languageType = languageType;
/*     */   }
/*     */ 
/*     */   public String getName() {
/*  98 */     return this.name;
/*     */   }
/*     */ 
/*     */   public void setName(String name) {
/* 102 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public Token getFirstToken() {
/* 106 */     return this.firstToken;
/*     */   }
/*     */ 
/*     */   public void setFirstToken(Token token) {
/* 110 */     this.firstToken = token;
/*     */   }
/*     */ 
/*     */   public Token getLastToken() {
/* 114 */     return this.lastToken;
/*     */   }
/*     */ 
/*     */   public void setLastToken(Token lastToken) {
/* 118 */     this.lastToken = lastToken;
/*     */   }
/*     */ 
/*     */   public boolean isGlobal() {
/* 122 */     return this.parentToken == null;
/*     */   }
/*     */ 
/*     */   public int getDeclStart() {
/* 126 */     return this.declBegin;
/*     */   }
/*     */ 
/*     */   public void setDeclBegin(int declBegin) {
/* 130 */     this.declBegin = declBegin;
/*     */   }
/*     */ 
/*     */   public int getDeclEnd() {
/* 134 */     return this.declEnd;
/*     */   }
/*     */ 
/*     */   public void setDeclEnd(int declEnd) {
/* 138 */     this.declEnd = declEnd;
/*     */   }
/*     */ 
/*     */   public List<IDeclaration> getChildren() {
/* 142 */     return this.children;
/*     */   }
/*     */ 
/*     */   public Map<String, IDeclaration> getVariables() {
/* 146 */     return this.variables;
/*     */   }
/*     */   public Map<String, IDeclaration> getFunctions() {
/* 149 */     return null;
/*     */   }
/*     */   public Map<String, IDeclaration> getClasses() {
/* 152 */     return null;
/*     */   }
/*     */ 
/*     */   public int getParentLevel() {
/* 156 */     return this.parentLevel;
/*     */   }
/*     */ 
/*     */   public void setParentLevel(int parentLevel) {
/* 160 */     this.parentLevel = parentLevel;
/*     */   }
/*     */ 
/*     */   public String getVisibility() {
/* 164 */     return this.visibility;
/*     */   }
/*     */ 
/*     */   public void setVisibility(String visibility) {
/* 168 */     this.visibility = visibility;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.Declaration
 * JD-Core Version:    0.6.0
 */