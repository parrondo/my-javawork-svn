/*     */ package com.dukascopy.dds2.greed.connector;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.swing.ListModel;
/*     */ import javax.swing.event.ListDataEvent;
/*     */ import javax.swing.event.ListDataListener;
/*     */ 
/*     */ class DataContainer
/*     */   implements ListModel
/*     */ {
/* 176 */   private List<String> list = new ArrayList();
/* 177 */   private Set<ListDataListener> listeners = new HashSet();
/*     */ 
/*     */   public void addListDataListener(ListDataListener listDataListener)
/*     */   {
/* 181 */     this.listeners.add(listDataListener);
/*     */   }
/*     */ 
/*     */   public Object getElementAt(int index)
/*     */   {
/* 186 */     String rc = (String)this.list.get(this.list.size() - 1 - index);
/*     */ 
/* 188 */     return rc;
/*     */   }
/*     */ 
/*     */   public int getSize()
/*     */   {
/* 193 */     return this.list.size();
/*     */   }
/*     */ 
/*     */   public void removeListDataListener(ListDataListener listDataListener)
/*     */   {
/* 198 */     this.listeners.remove(listDataListener);
/*     */   }
/*     */ 
/*     */   public void addString(String string) {
/* 202 */     this.list.add(string);
/* 203 */     for (ListDataListener dataListener : this.listeners)
/* 204 */       dataListener.contentsChanged(new ListDataEvent(this, 0, 0, this.list.size()));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.DataContainer
 * JD-Core Version:    0.6.0
 */