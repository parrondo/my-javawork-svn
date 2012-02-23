/*    */ package com.dukascopy.calculator;
/*    */ 
/*    */ import com.dukascopy.calculator.complex.DoubleFormat;
/*    */ import java.util.Arrays;
/*    */ import java.util.ListIterator;
/*    */ import java.util.Vector;
/*    */ 
/*    */ public class StringArray extends Vector<Vector<String>>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public void add(String string)
/*    */   {
/* 12 */     Vector q = new Vector();
/* 13 */     q.add(string);
/* 14 */     add(q);
/*    */   }
/*    */ 
/*    */   public void add(char character) {
/* 18 */     add(Character.toString(character));
/*    */   }
/*    */ 
/*    */   public void add(String[] string) {
/* 22 */     Vector t = new Vector();
/* 23 */     t.addAll(Arrays.asList(string));
/* 24 */     add(t);
/*    */   }
/*    */ 
/*    */   public boolean isOne() {
/* 28 */     if (size() != 1) return false;
/* 29 */     if (((Vector)firstElement()).size() != 1) return false;
/* 30 */     return ((String)((Vector)firstElement()).elementAt(0)).equals("1");
/*    */   }
/*    */ 
/*    */   public boolean isZero()
/*    */   {
/* 35 */     if (size() != 1) return false;
/* 36 */     if (((Vector)firstElement()).size() != 1) return false;
/* 37 */     return ((String)((Vector)firstElement()).elementAt(0)).equals("0");
/*    */   }
/*    */ 
/*    */   public boolean isMinusOne()
/*    */   {
/* 42 */     if (size() != 1) return false;
/* 43 */     if (((Vector)firstElement()).size() != 2) return false;
/* 44 */     if (!((String)((Vector)firstElement()).elementAt(0)).equals(DoubleFormat.minus.elementAt(0)))
/*    */     {
/* 46 */       return false;
/* 47 */     }return ((String)((Vector)firstElement()).elementAt(1)).equals("1");
/*    */   }
/*    */ 
/*    */   public void removeDoubleSuperscripts() {
/* 51 */     int superscriptLevel = 0;
/* 52 */     for (Vector i : this)
/* 53 */       for (j = i.listIterator(); j.hasNext(); ) {
/* 54 */         String s = (String)j.next();
/*    */ 
/* 56 */         if (s.startsWith("<sup>")) {
/* 57 */           if (superscriptLevel > 0)
/*    */           {
/* 59 */             s = s.substring(5);
/* 60 */             j.set("^");
/* 61 */             j.add("(");
/* 62 */             j.previous();
/* 63 */             j.next();
/* 64 */             j.add(s);
/* 65 */             j.previous();
/* 66 */             j.next();
/*    */           }
/*    */ 
/* 69 */           superscriptLevel++;
/*    */         }
/*    */ 
/* 72 */         if (s.endsWith("</sup>"))
/*    */         {
/* 74 */           superscriptLevel--;
/* 75 */           if (superscriptLevel > 0)
/*    */           {
/* 77 */             s = s.substring(0, s.length() - 6);
/* 78 */             j.set(s);
/* 79 */             j.add(")");
/*    */           }
/*    */         }
/*    */       }
/*    */     ListIterator j;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.StringArray
 * JD-Core Version:    0.6.0
 */