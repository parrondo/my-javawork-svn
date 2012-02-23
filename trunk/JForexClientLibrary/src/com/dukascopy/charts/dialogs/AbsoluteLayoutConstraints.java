/*    */ package com.dukascopy.charts.dialogs;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class AbsoluteLayoutConstraints
/*    */   implements Cloneable, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   int x;
/*    */   int y;
/*    */   int width;
/*    */   int height;
/*    */ 
/*    */   public AbsoluteLayoutConstraints()
/*    */   {
/* 13 */     this(0, 0, 0, 0);
/*    */   }
/*    */ 
/*    */   public AbsoluteLayoutConstraints(int x, int y, int width, int height) {
/* 17 */     this.x = x;
/* 18 */     this.y = y;
/* 19 */     this.width = width;
/* 20 */     this.height = height;
/*    */   }
/*    */ 
/*    */   public int getX() {
/* 24 */     return this.x;
/*    */   }
/*    */ 
/*    */   public void setX(int x) {
/* 28 */     this.x = x;
/*    */   }
/*    */ 
/*    */   public int getY() {
/* 32 */     return this.y;
/*    */   }
/*    */ 
/*    */   public void setY(int y) {
/* 36 */     this.y = y;
/*    */   }
/*    */ 
/*    */   public int getWidth() {
/* 40 */     return this.width;
/*    */   }
/*    */ 
/*    */   public void setWidth(int width) {
/* 44 */     this.width = width;
/*    */   }
/*    */ 
/*    */   public int getHeight() {
/* 48 */     return this.height;
/*    */   }
/*    */ 
/*    */   public void setHeight(int height) {
/* 52 */     this.height = height;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 59 */     return this.x ^ this.y * 37 ^ this.width * 43 ^ this.height * 47;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object that) {
/* 63 */     if ((that instanceof AbsoluteLayoutConstraints)) {
/* 64 */       AbsoluteLayoutConstraints other = (AbsoluteLayoutConstraints)that;
/* 65 */       return (other.x == this.x) && (other.y == this.y) && (other.width == this.width) && (other.height == this.height);
/*    */     }
/* 67 */     return false;
/*    */   }
/*    */ 
/*    */   public Object clone() throws CloneNotSupportedException {
/* 71 */     return new AbsoluteLayoutConstraints(this.x, this.y, this.width, this.height);
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 75 */     return "XYConstraints[" + this.x + "," + this.y + "," + this.width + "," + this.height + "]";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.AbsoluteLayoutConstraints
 * JD-Core Version:    0.6.0
 */