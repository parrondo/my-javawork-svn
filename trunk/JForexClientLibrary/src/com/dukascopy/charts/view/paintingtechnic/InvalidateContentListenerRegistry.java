/*    */ package com.dukascopy.charts.view.paintingtechnic;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ 
/*    */ class InvalidateContentListenerRegistry
/*    */ {
/*  7 */   List<InvalidateContentListener> mainWidowListeners = new ArrayList();
/*  8 */   Map<Integer, List<InvalidateContentListener>> subWindowListeners = new HashMap();
/*    */ 
/*    */   public void registerMainWindowListener(InvalidateContentListener listener)
/*    */   {
/* 12 */     this.mainWidowListeners.add(listener);
/*    */   }
/*    */ 
/*    */   public void registerSubWindowListener(InvalidateContentListener listener, int id) {
/* 16 */     List subListeners = (List)this.subWindowListeners.get(Integer.valueOf(id));
/* 17 */     if (subListeners == null) {
/* 18 */       subListeners = new LinkedList();
/*    */     }
/* 20 */     subListeners.add(listener);
/* 21 */     this.subWindowListeners.put(Integer.valueOf(id), subListeners);
/*    */   }
/*    */ 
/*    */   public void unregisterSubListener(int subWindowId) {
/* 25 */     this.subWindowListeners.remove(Integer.valueOf(subWindowId));
/*    */   }
/*    */ 
/*    */   public void invalidateMainWindowsContent()
/*    */   {
/* 33 */     invalidateContent(null, this.mainWidowListeners);
/*    */   }
/*    */ 
/*    */   public void invalidateSubWindowsContent(int id) {
/* 37 */     List contentListeners = (List)this.subWindowListeners.get(Integer.valueOf(id));
/* 38 */     if (contentListeners == null) {
/* 39 */       return;
/*    */     }
/* 41 */     for (InvalidateContentListener contentListener : contentListeners)
/* 42 */       contentListener.invalidateContent();
/*    */   }
/*    */ 
/*    */   public void invalidateAllContent()
/*    */   {
/* 47 */     invalidateContent(null, this.mainWidowListeners);
/* 48 */     for (List listener : this.subWindowListeners.values())
/* 49 */       invalidateContent(null, listener);
/*    */   }
/*    */ 
/*    */   void invalidateContent(InvalidationContent contentToBeInvalidated, List<InvalidateContentListener> listeners)
/*    */   {
/* 61 */     for (InvalidateContentListener invalidateContentListener : listeners)
/* 62 */       if ((contentToBeInvalidated == null) || (contentToBeInvalidated == invalidateContentListener.getInvalidateContentType()))
/* 63 */         invalidateContentListener.invalidateContent();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.paintingtechnic.InvalidateContentListenerRegistry
 * JD-Core Version:    0.6.0
 */