/*    */ package com.dukascopy.charts.drawings;
/*    */ 
/*    */ import com.dukascopy.charts.persistence.ITheme;
/*    */ 
/*    */ public class DrawingsLabelHelperContainer
/*    */ {
/*    */   private DrawingsLabelHelper mainLabelHelper;
/*    */   private DrawingsLabelHelper subLabelHelper;
/*    */ 
/*    */   public DrawingsLabelHelperContainer(ITheme theme)
/*    */   {
/* 20 */     this.mainLabelHelper = new DrawingsLabelHelper(theme);
/* 21 */     this.subLabelHelper = new DrawingsLabelHelper(theme);
/*    */   }
/*    */ 
/*    */   public DrawingsLabelHelper getLabelHelperForMain()
/*    */   {
/* 29 */     return this.mainLabelHelper;
/*    */   }
/*    */ 
/*    */   public DrawingsLabelHelper getLabelHelperForSubWindow(int subwindowId)
/*    */   {
/* 40 */     if (subwindowId < 0) {
/* 41 */       return getLabelHelperForMain();
/*    */     }
/* 43 */     return this.subLabelHelper;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.DrawingsLabelHelperContainer
 * JD-Core Version:    0.6.0
 */