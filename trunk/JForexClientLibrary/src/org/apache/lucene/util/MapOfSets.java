/*    */ package org.apache.lucene.util;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import java.util.HashSet;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class MapOfSets<K, V>
/*    */ {
/*    */   private final Map<K, Set<V>> theMap;
/*    */ 
/*    */   public MapOfSets(Map<K, Set<V>> m)
/*    */   {
/* 38 */     this.theMap = m;
/*    */   }
/*    */ 
/*    */   public Map<K, Set<V>> getMap()
/*    */   {
/* 45 */     return this.theMap;
/*    */   }
/*    */ 
/*    */   public int put(K key, V val)
/*    */   {
/*    */     Set theSet;
/*    */     Set theSet;
/* 55 */     if (this.theMap.containsKey(key)) {
/* 56 */       theSet = (Set)this.theMap.get(key);
/*    */     } else {
/* 58 */       theSet = new HashSet(23);
/* 59 */       this.theMap.put(key, theSet);
/*    */     }
/* 61 */     theSet.add(val);
/* 62 */     return theSet.size();
/*    */   }
/*    */ 
/*    */   public int putAll(K key, Collection<? extends V> vals)
/*    */   {
/*    */     Set theSet;
/*    */     Set theSet;
/* 72 */     if (this.theMap.containsKey(key)) {
/* 73 */       theSet = (Set)this.theMap.get(key);
/*    */     } else {
/* 75 */       theSet = new HashSet(23);
/* 76 */       this.theMap.put(key, theSet);
/*    */     }
/* 78 */     theSet.addAll(vals);
/* 79 */     return theSet.size();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.MapOfSets
 * JD-Core Version:    0.6.0
 */