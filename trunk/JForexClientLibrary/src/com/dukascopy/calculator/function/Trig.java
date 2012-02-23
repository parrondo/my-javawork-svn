/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.AngleType;
/*    */ 
/*    */ public abstract class Trig extends RFunction
/*    */ {
/*    */   protected double scale;
/*    */   protected double iscale;
/*    */   protected AngleType angleType;
/*    */ 
/*    */   public Trig(AngleType angleType)
/*    */   {
/* 15 */     setScale(angleType);
/*    */   }
/*    */ 
/*    */   public void setScale(AngleType angleType)
/*    */   {
/* 23 */     switch (1.$SwitchMap$com$dukascopy$calculator$AngleType[angleType.ordinal()]) {
/*    */     case 1:
/* 25 */       this.scale = 1.0D;
/* 26 */       this.iscale = 1.0D;
/* 27 */       break;
/*    */     case 2:
/* 29 */       this.scale = 0.0174532925199433D;
/* 30 */       this.iscale = 57.295779513082323D;
/*    */     }
/*    */ 
/* 33 */     this.angleType = angleType;
/*    */   }
/*    */ 
/*    */   public final AngleType getAngleType()
/*    */   {
/* 41 */     return this.angleType;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Trig
 * JD-Core Version:    0.6.0
 */