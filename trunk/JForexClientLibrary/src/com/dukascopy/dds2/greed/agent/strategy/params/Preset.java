/*    */ package com.dukascopy.dds2.greed.agent.strategy.params;
/*    */ 
/*    */ import java.text.Collator;
/*    */ 
/*    */ public class Preset
/*    */   implements Comparable<Preset>
/*    */ {
/* 14 */   public static byte FILE_LOADED = 0;
/* 15 */   public static byte USER_ADDED = 1;
/*    */   private byte creationType;
/*    */   private String name;
/*    */ 
/*    */   public Preset(String name, byte creationType)
/*    */   {
/* 22 */     this.name = name;
/* 23 */     this.creationType = creationType;
/*    */   }
/*    */ 
/*    */   public byte getCreationType() {
/* 27 */     return this.creationType;
/*    */   }
/*    */ 
/*    */   public void setCreationType(byte creationType) {
/* 31 */     this.creationType = creationType;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 35 */     return this.name;
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 39 */     return this.name;
/*    */   }
/*    */ 
/*    */   public void setName(String newName) {
/* 43 */     this.name = newName;
/*    */   }
/*    */ 
/*    */   public int compareTo(Preset toCompare) {
/* 47 */     String nameCompareTo = toCompare.getName();
/* 48 */     return Collator.getInstance().compare(this.name, nameCompareTo);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.params.Preset
 * JD-Core Version:    0.6.0
 */