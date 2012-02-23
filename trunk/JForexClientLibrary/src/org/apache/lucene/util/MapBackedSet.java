/*    */ package org.apache.lucene.util;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.AbstractSet;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ public final class MapBackedSet<E> extends AbstractSet<E>
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = -6761513279741915432L;
/*    */   private final Map<E, Boolean> map;
/*    */ 
/*    */   public MapBackedSet(Map<E, Boolean> map)
/*    */   {
/* 41 */     this.map = map;
/*    */   }
/*    */ 
/*    */   public int size()
/*    */   {
/* 46 */     return this.map.size();
/*    */   }
/*    */ 
/*    */   public boolean contains(Object o)
/*    */   {
/* 51 */     return this.map.containsKey(o);
/*    */   }
/*    */ 
/*    */   public boolean add(E o)
/*    */   {
/* 56 */     return this.map.put(o, Boolean.TRUE) == null;
/*    */   }
/*    */ 
/*    */   public boolean remove(Object o)
/*    */   {
/* 61 */     return this.map.remove(o) != null;
/*    */   }
/*    */ 
/*    */   public void clear()
/*    */   {
/* 66 */     this.map.clear();
/*    */   }
/*    */ 
/*    */   public Iterator<E> iterator()
/*    */   {
/* 71 */     return this.map.keySet().iterator();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.MapBackedSet
 * JD-Core Version:    0.6.0
 */