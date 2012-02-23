/*     */ package org.eclipse.jdt.internal.compiler.util;
/*     */ 
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Writer;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class GenericXMLWriter extends PrintWriter
/*     */ {
/*     */   private static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
/*     */   private String lineSeparator;
/*     */   private int tab;
/*     */ 
/*     */   private static void appendEscapedChar(StringBuffer buffer, char c)
/*     */   {
/*  25 */     String replacement = getReplacement(c);
/*  26 */     if (replacement != null) {
/*  27 */       buffer.append('&');
/*  28 */       buffer.append(replacement);
/*  29 */       buffer.append(';');
/*     */     } else {
/*  31 */       buffer.append(c);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static String getEscaped(String s) {
/*  35 */     StringBuffer result = new StringBuffer(s.length() + 10);
/*  36 */     for (int i = 0; i < s.length(); i++)
/*  37 */       appendEscapedChar(result, s.charAt(i));
/*  38 */     return result.toString();
/*     */   }
/*     */ 
/*     */   private static String getReplacement(char c)
/*     */   {
/*  43 */     switch (c) {
/*     */     case '<':
/*  45 */       return "lt";
/*     */     case '>':
/*  47 */       return "gt";
/*     */     case '"':
/*  49 */       return "quot";
/*     */     case '\'':
/*  51 */       return "apos";
/*     */     case '&':
/*  53 */       return "amp";
/*     */     }
/*  55 */     return null;
/*     */   }
/*     */ 
/*     */   public GenericXMLWriter(OutputStream stream, String lineSeparator, boolean printXmlVersion)
/*     */   {
/*  60 */     this(new PrintWriter(stream), lineSeparator, printXmlVersion);
/*     */   }
/*     */   public GenericXMLWriter(Writer writer, String lineSeparator, boolean printXmlVersion) {
/*  63 */     super(writer);
/*  64 */     this.tab = 0;
/*  65 */     this.lineSeparator = lineSeparator;
/*  66 */     if (printXmlVersion) {
/*  67 */       print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
/*  68 */       print(this.lineSeparator);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void endTag(String name, boolean insertTab, boolean insertNewLine) {
/*  72 */     this.tab -= 1;
/*  73 */     printTag('/' + name, null, insertTab, insertNewLine, false);
/*     */   }
/*     */ 
/*     */   public void printString(String string, boolean insertTab, boolean insertNewLine)
/*     */   {
/*  79 */     if (insertTab) {
/*  80 */       printTabulation();
/*     */     }
/*  82 */     print(string);
/*  83 */     if (insertNewLine)
/*  84 */       print(this.lineSeparator);
/*     */   }
/*     */ 
/*     */   private void printTabulation() {
/*  88 */     for (int i = 0; i < this.tab; i++) print('\t'); 
/*     */   }
/*     */ 
/*     */   public void printTag(String name, HashMap parameters, boolean insertTab, boolean insertNewLine, boolean closeTag) {
/*  91 */     if (insertTab) {
/*  92 */       printTabulation();
/*     */     }
/*  94 */     print('<');
/*  95 */     print(name);
/*  96 */     if (parameters != null) {
/*  97 */       int length = parameters.size();
/*  98 */       Map.Entry[] entries = new Map.Entry[length];
/*  99 */       parameters.entrySet().toArray(entries);
/* 100 */       Arrays.sort(entries, new Comparator() {
/*     */         public int compare(Object o1, Object o2) {
/* 102 */           Map.Entry entry1 = (Map.Entry)o1;
/* 103 */           Map.Entry entry2 = (Map.Entry)o2;
/* 104 */           return ((String)entry1.getKey()).compareTo((String)entry2.getKey());
/*     */         }
/*     */       });
/* 107 */       for (int i = 0; i < length; i++) {
/* 108 */         print(' ');
/* 109 */         print(entries[i].getKey());
/* 110 */         print("=\"");
/* 111 */         print(getEscaped(String.valueOf(entries[i].getValue())));
/* 112 */         print('"');
/*     */       }
/*     */     }
/* 115 */     if (closeTag)
/* 116 */       print("/>");
/*     */     else {
/* 118 */       print(">");
/*     */     }
/* 120 */     if (insertNewLine) {
/* 121 */       print(this.lineSeparator);
/*     */     }
/* 123 */     if ((parameters != null) && (!closeTag))
/* 124 */       this.tab += 1;
/*     */   }
/*     */ 
/*     */   public void startTag(String name, boolean insertTab) {
/* 128 */     printTag(name, null, insertTab, true, false);
/* 129 */     this.tab += 1;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.util.GenericXMLWriter
 * JD-Core Version:    0.6.0
 */