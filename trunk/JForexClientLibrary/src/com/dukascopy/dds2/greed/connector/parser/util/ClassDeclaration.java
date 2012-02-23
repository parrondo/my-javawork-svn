/*    */ package com.dukascopy.dds2.greed.connector.parser.util;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ 
/*    */ public class ClassDeclaration extends Declaration
/*    */ {
/*  9 */   public Map<String, IDeclaration> functions = new ConcurrentHashMap();
/* 10 */   public Map<String, IDeclaration> classes = new ConcurrentHashMap();
/* 11 */   public List<IDeclaration> accessSpecifiers = new ArrayList();
/*    */ 
/*    */   public String endText()
/*    */   {
/* 15 */     return "\r\n}\r\n";
/*    */   }
/*    */ 
/*    */   public String startText()
/*    */   {
/* 20 */     StringBuilder result = new StringBuilder();
/* 21 */     result.append(getType() + " ");
/* 22 */     result.append(getName() + " {\r\n");
/*    */ 
/* 24 */     return result.toString();
/*    */   }
/*    */ 
/*    */   public Map<String, IDeclaration> getFunctions() {
/* 28 */     return this.functions;
/*    */   }
/*    */   public Map<String, IDeclaration> getClasses() {
/* 31 */     return this.classes;
/*    */   }
/*    */ 
/*    */   public List<IDeclaration> getAccessSpecifiers() {
/* 35 */     return this.accessSpecifiers;
/*    */   }
/*    */ 
/*    */   public void setAccessSpecifiers(List<IDeclaration> accessSpecifiers) {
/* 39 */     this.accessSpecifiers = accessSpecifiers;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.ClassDeclaration
 * JD-Core Version:    0.6.0
 */