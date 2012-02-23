/*    */ package com.dukascopy.calculator;
/*    */ 
/*    */ import com.dukascopy.calculator.function.PObject;
/*    */ import java.util.LinkedList;
/*    */ 
/*    */ public class HistoryItem
/*    */ {
/*    */   public LinkedList<PObject> list;
/*    */   public AngleType angleType;
/*    */   public Base base;
/*    */   public Notation notation;
/*    */ 
/*    */   HistoryItem(LinkedList<PObject> list, AngleType angleType, Base base, Notation notation)
/*    */   {
/* 23 */     this.list = list;
/* 24 */     this.angleType = angleType;
/* 25 */     this.base = base;
/* 26 */     this.notation = notation;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.HistoryItem
 * JD-Core Version:    0.6.0
 */