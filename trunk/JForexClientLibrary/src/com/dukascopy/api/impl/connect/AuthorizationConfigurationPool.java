/*    */ package com.dukascopy.api.impl.connect;
/*    */ 
/*    */ import java.net.MalformedURLException;
/*    */ import java.net.URL;
/*    */ import java.util.LinkedList;
/*    */ 
/*    */ public class AuthorizationConfigurationPool
/*    */ {
/*  9 */   private LinkedList<URL> authServerList = new LinkedList();
/*    */ 
/*    */   public int size() {
/* 12 */     return this.authServerList.size();
/*    */   }
/*    */ 
/*    */   public void add(String link) throws MalformedURLException {
/* 16 */     this.authServerList.addLast(new URL(link));
/*    */   }
/*    */ 
/*    */   public URL get() {
/* 20 */     return (URL)this.authServerList.getFirst();
/*    */   }
/*    */ 
/*    */   public void markLastUsedAsBad() {
/* 24 */     URL badOne = (URL)this.authServerList.pop();
/* 25 */     this.authServerList.addLast(badOne);
/*    */   }
/*    */ 
/*    */   public void clear() {
/* 29 */     this.authServerList.clear();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.AuthorizationConfigurationPool
 * JD-Core Version:    0.6.0
 */