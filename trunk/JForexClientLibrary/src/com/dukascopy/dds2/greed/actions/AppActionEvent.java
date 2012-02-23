/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.util.event.ApplicationEvent;
/*    */ 
/*    */ public abstract class AppActionEvent extends ApplicationEvent
/*    */ {
/*    */   private final boolean updateGuiBefore;
/*    */   private final boolean updateGuiAfter;
/*    */ 
/*    */   public AppActionEvent(Object source, boolean updateGuiBefore, boolean updateGuiAfter)
/*    */   {
/* 29 */     super(source);
/* 30 */     this.updateGuiBefore = updateGuiBefore;
/* 31 */     this.updateGuiAfter = updateGuiAfter;
/*    */   }
/*    */ 
/*    */   public void doAction()
/*    */   {
/*    */   }
/*    */ 
/*    */   public final boolean isUpdateGuiBefore()
/*    */   {
/* 42 */     return this.updateGuiBefore;
/*    */   }
/*    */ 
/*    */   public final boolean isUpdateGuiAfter() {
/* 46 */     return this.updateGuiAfter;
/*    */   }
/*    */ 
/*    */   public void updateGuiBefore()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter()
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.AppActionEvent
 * JD-Core Version:    0.6.0
 */