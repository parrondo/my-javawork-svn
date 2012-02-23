/*     */ package org.eclipse.jdt.internal.compiler.apt.util;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class ManyToMany<T1, T2>
/*     */ {
/*  39 */   private final Map<T1, Set<T2>> _forward = new HashMap();
/*  40 */   private final Map<T2, Set<T1>> _reverse = new HashMap();
/*  41 */   private boolean _dirty = false;
/*     */ 
/*     */   public synchronized boolean clear()
/*     */   {
/*  49 */     boolean hadContent = (!this._forward.isEmpty()) || (!this._reverse.isEmpty());
/*  50 */     this._reverse.clear();
/*  51 */     this._forward.clear();
/*  52 */     this._dirty |= hadContent;
/*  53 */     return hadContent;
/*     */   }
/*     */ 
/*     */   public synchronized void clearDirtyBit()
/*     */   {
/*  63 */     this._dirty = false;
/*     */   }
/*     */ 
/*     */   public synchronized boolean containsKey(T1 key)
/*     */   {
/*  71 */     return this._forward.containsKey(key);
/*     */   }
/*     */ 
/*     */   public synchronized boolean containsKeyValuePair(T1 key, T2 value)
/*     */   {
/*  80 */     Set values = (Set)this._forward.get(key);
/*  81 */     if (values == null) {
/*  82 */       return false;
/*     */     }
/*  84 */     return values.contains(value);
/*     */   }
/*     */ 
/*     */   public synchronized boolean containsValue(T2 value)
/*     */   {
/*  93 */     return this._reverse.containsKey(value);
/*     */   }
/*     */ 
/*     */   public synchronized Set<T1> getKeys(T2 value)
/*     */   {
/* 103 */     Set keys = (Set)this._reverse.get(value);
/* 104 */     if (keys == null) {
/* 105 */       return Collections.emptySet();
/*     */     }
/* 107 */     return new HashSet(keys);
/*     */   }
/*     */ 
/*     */   public synchronized Set<T2> getValues(T1 key)
/*     */   {
/* 117 */     Set values = (Set)this._forward.get(key);
/* 118 */     if (values == null) {
/* 119 */       return Collections.emptySet();
/*     */     }
/* 121 */     return new HashSet(values);
/*     */   }
/*     */ 
/*     */   public synchronized Set<T1> getKeySet()
/*     */   {
/* 131 */     Set keys = new HashSet(this._forward.keySet());
/* 132 */     return keys;
/*     */   }
/*     */ 
/*     */   public synchronized Set<T2> getValueSet()
/*     */   {
/* 142 */     Set values = new HashSet(this._reverse.keySet());
/* 143 */     return values;
/*     */   }
/*     */ 
/*     */   public synchronized boolean isDirty()
/*     */   {
/* 155 */     return this._dirty;
/*     */   }
/*     */ 
/*     */   public synchronized boolean keyHasOtherValues(T1 key, T2 value)
/*     */   {
/* 169 */     Set values = (Set)this._forward.get(key);
/* 170 */     if (values == null)
/* 171 */       return false;
/* 172 */     int size = values.size();
/* 173 */     if (size == 0)
/* 174 */       return false;
/* 175 */     if (size > 1) {
/* 176 */       return true;
/*     */     }
/* 178 */     return !values.contains(value);
/*     */   }
/*     */ 
/*     */   public synchronized boolean put(T1 key, T2 value)
/*     */   {
/* 193 */     Set values = (Set)this._forward.get(key);
/* 194 */     if (values == null) {
/* 195 */       values = new HashSet();
/* 196 */       this._forward.put(key, values);
/*     */     }
/* 198 */     boolean added = values.add(value);
/* 199 */     this._dirty |= added;
/*     */ 
/* 202 */     Set keys = (Set)this._reverse.get(value);
/* 203 */     if (keys == null) {
/* 204 */       keys = new HashSet();
/* 205 */       this._reverse.put(value, keys);
/*     */     }
/* 207 */     keys.add(key);
/*     */ 
/* 209 */     assert (checkIntegrity());
/* 210 */     return added;
/*     */   }
/*     */ 
/*     */   public synchronized boolean remove(T1 key, T2 value)
/*     */   {
/* 221 */     Set values = (Set)this._forward.get(key);
/* 222 */     if (values == null) {
/* 223 */       assert (checkIntegrity());
/* 224 */       return false;
/*     */     }
/* 226 */     boolean removed = values.remove(value);
/* 227 */     if (values.isEmpty()) {
/* 228 */       this._forward.remove(key);
/*     */     }
/* 230 */     if (removed) {
/* 231 */       this._dirty = true;
/*     */ 
/* 233 */       Set keys = (Set)this._reverse.get(value);
/* 234 */       keys.remove(key);
/* 235 */       if (keys.isEmpty()) {
/* 236 */         this._reverse.remove(value);
/*     */       }
/*     */     }
/* 239 */     assert (checkIntegrity());
/* 240 */     return removed;
/*     */   }
/*     */ 
/*     */   public synchronized boolean removeKey(T1 key)
/*     */   {
/* 251 */     Set values = (Set)this._forward.get(key);
/* 252 */     if (values == null)
/*     */     {
/* 254 */       assert (checkIntegrity());
/* 255 */       return false;
/*     */     }
/* 257 */     for (Object value : values) {
/* 258 */       Set keys = (Set)this._reverse.get(value);
/* 259 */       if (keys != null) {
/* 260 */         keys.remove(key);
/* 261 */         if (keys.isEmpty()) {
/* 262 */           this._reverse.remove(value);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 267 */     this._forward.remove(key);
/* 268 */     this._dirty = true;
/* 269 */     assert (checkIntegrity());
/* 270 */     return true;
/*     */   }
/*     */ 
/*     */   public synchronized boolean removeValue(T2 value)
/*     */   {
/* 281 */     Set keys = (Set)this._reverse.get(value);
/* 282 */     if (keys == null)
/*     */     {
/* 284 */       assert (checkIntegrity());
/* 285 */       return false;
/*     */     }
/* 287 */     for (Object key : keys) {
/* 288 */       Set values = (Set)this._forward.get(key);
/* 289 */       if (values != null) {
/* 290 */         values.remove(value);
/* 291 */         if (values.isEmpty()) {
/* 292 */           this._forward.remove(key);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 297 */     this._reverse.remove(value);
/* 298 */     this._dirty = true;
/* 299 */     assert (checkIntegrity());
/* 300 */     return true;
/*     */   }
/*     */ 
/*     */   public synchronized boolean valueHasOtherKeys(T2 value, T1 key)
/*     */   {
/* 314 */     Set keys = (Set)this._reverse.get(key);
/* 315 */     if (keys == null)
/* 316 */       return false;
/* 317 */     int size = keys.size();
/* 318 */     if (size == 0)
/* 319 */       return false;
/* 320 */     if (size > 1) {
/* 321 */       return true;
/*     */     }
/* 323 */     return !keys.contains(key);
/*     */   }
/*     */ 
/*     */   private boolean checkIntegrity()
/*     */   {
/* 336 */     for (Map.Entry entry : this._forward.entrySet()) {
/* 337 */       Set values = (Set)entry.getValue();
/* 338 */       if (values.isEmpty()) {
/* 339 */         throw new IllegalStateException("Integrity compromised: forward map contains an empty set");
/*     */       }
/* 341 */       for (Object value : values) {
/* 342 */         Set keys = (Set)this._reverse.get(value);
/* 343 */         if ((keys == null) || (!keys.contains(entry.getKey()))) {
/* 344 */           throw new IllegalStateException("Integrity compromised: forward map contains an entry missing from reverse map: " + value);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 349 */     for (Map.Entry entry : this._reverse.entrySet()) {
/* 350 */       Set keys = (Set)entry.getValue();
/* 351 */       if (keys.isEmpty()) {
/* 352 */         throw new IllegalStateException("Integrity compromised: reverse map contains an empty set");
/*     */       }
/* 354 */       for (Object key : keys) {
/* 355 */         Set values = (Set)this._forward.get(key);
/* 356 */         if ((values == null) || (!values.contains(entry.getKey()))) {
/* 357 */           throw new IllegalStateException("Integrity compromised: reverse map contains an entry missing from forward map: " + key);
/*     */         }
/*     */       }
/*     */     }
/* 361 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.util.ManyToMany
 * JD-Core Version:    0.6.0
 */