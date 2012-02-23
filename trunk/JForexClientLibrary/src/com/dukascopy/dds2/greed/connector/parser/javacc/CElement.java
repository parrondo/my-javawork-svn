/*     */ package com.dukascopy.dds2.greed.connector.parser.javacc;
/*     */ 
/*     */ public class CElement
/*     */ {
/*     */   public static final int T_CFILE = 1;
/*     */   public static final int T_FIELD = 2;
/*     */   public static final int T_STRUCT = 3;
/*     */   public static final int T_UNION = 4;
/*     */   public static final int T_CLASS = 5;
/*     */   public static final int T_FUNCTION = 6;
/*     */   public static final int T_INCLUDE = 7;
/*     */   public static final int T_DEFINE = 8;
/*     */   protected int fType;
/*     */   protected CElement fParent;
/*     */   protected String fName;
/*     */   protected int fStartPos;
/*     */   protected int fLength;
/*     */   protected int fIdStartPos;
/*     */   protected int fIdLength;
/*     */ 
/*     */   public CElement(CElement parent, String name, int type)
/*     */   {
/*  40 */     this.fParent = parent;
/*  41 */     this.fName = name;
/*  42 */     this.fType = type;
/*     */   }
/*     */ 
/*     */   public CElement getParent() {
/*  46 */     return this.fParent;
/*     */   }
/*     */ 
/*     */   public void setPos(int startPos, int length)
/*     */   {
/*  52 */     this.fStartPos = startPos;
/*  53 */     this.fLength = length;
/*     */   }
/*     */ 
/*     */   public void setIdPos(int startPos, int length) {
/*  57 */     this.fIdStartPos = startPos;
/*  58 */     this.fIdLength = length;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  64 */     return this.fName;
/*     */   }
/*     */ 
/*     */   public int getStartPos() {
/*  68 */     return this.fStartPos;
/*     */   }
/*     */ 
/*     */   public int getLength() {
/*  72 */     return this.fLength;
/*     */   }
/*     */ 
/*     */   public int getIdStartPos() {
/*  76 */     return this.fIdStartPos;
/*     */   }
/*     */ 
/*     */   public int getIdLength() {
/*  80 */     return this.fIdLength;
/*     */   }
/*     */ 
/*     */   public int getElementType() {
/*  84 */     return this.fType;
/*     */   }
/*     */ 
/*     */   public void addMember(CElement member)
/*     */   {
/*     */   }
/*     */ 
/*     */   public CElement[] getMembers()
/*     */   {
/* 102 */     return null;
/*     */   }
/*     */ 
/*     */   public CElement[] getMembers(int type)
/*     */   {
/* 110 */     CElement[] members = getMembers();
/* 111 */     if (members != null) {
/* 112 */       int count = 0;
/* 113 */       for (int i = 0; i < members.length; i++) {
/* 114 */         if (members[i].getElementType() == type) {
/* 115 */           count++;
/*     */         }
/*     */       }
/* 118 */       CElement[] res = new CElement[count];
/* 119 */       for (int i = 0; i < members.length; i++) {
/* 120 */         if (members[i].getElementType() == type) {
/* 121 */           res[i] = members[i];
/*     */         }
/*     */       }
/* 124 */       return res;
/*     */     }
/* 126 */     return null;
/*     */   }
/*     */ 
/*     */   public CElement findEqualMember(CElement elem)
/*     */   {
/* 133 */     CElement[] members = getMembers();
/* 134 */     if (members != null) {
/* 135 */       for (int i = members.length - 1; i >= 0; i--) {
/* 136 */         CElement curr = members[i];
/* 137 */         if (curr.isEqual(elem)) {
/* 138 */           return curr;
/*     */         }
/* 140 */         CElement res = curr.findEqualMember(elem);
/* 141 */         if (res != null) {
/* 142 */           return res;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 147 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isEqual(CElement element)
/*     */   {
/* 154 */     return (element != null) && (this.fType == element.fType) && (this.fName.equals(element.fName)) && ((this.fParent == element.fParent) || ((this.fParent != null) && (this.fParent.isEqual(element.fParent))));
/*     */   }
/*     */ 
/*     */   public static String getTypeString(int type)
/*     */   {
/* 176 */     switch (type) { case 1:
/* 177 */       return "CFILE";
/*     */     case 2:
/* 178 */       return "T_FIELD";
/*     */     case 3:
/* 179 */       return "T_STRUCT";
/*     */     case 4:
/* 180 */       return "T_UNION";
/*     */     case 5:
/* 181 */       return "T_CLASS";
/*     */     case 6:
/* 182 */       return "T_FUNCTION";
/*     */     case 7:
/* 183 */       return "T_INCLUDE";
/*     */     case 8:
/* 184 */       return "T_DEFINE"; }
/* 185 */     return "UNKNOWN";
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.javacc.CElement
 * JD-Core Version:    0.6.0
 */