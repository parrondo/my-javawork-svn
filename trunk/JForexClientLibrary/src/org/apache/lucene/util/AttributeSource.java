/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import java.util.WeakHashMap;
/*     */ import org.apache.lucene.analysis.tokenattributes.CharTermAttributeImpl;
/*     */ import org.apache.lucene.analysis.tokenattributes.TermAttribute;
/*     */ 
/*     */ public class AttributeSource
/*     */ {
/*     */   private final Map<Class<? extends Attribute>, AttributeImpl> attributes;
/*     */   private final Map<Class<? extends AttributeImpl>, AttributeImpl> attributeImpls;
/*     */   private final State[] currentState;
/*     */   private AttributeFactory factory;
/*     */   private static final WeakHashMap<Class<? extends AttributeImpl>, LinkedList<WeakReference<Class<? extends Attribute>>>> knownImplClasses;
/*     */ 
/*     */   public AttributeSource()
/*     */   {
/* 138 */     this(AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY);
/*     */   }
/*     */ 
/*     */   public AttributeSource(AttributeSource input)
/*     */   {
/* 145 */     if (input == null) {
/* 146 */       throw new IllegalArgumentException("input AttributeSource must not be null");
/*     */     }
/* 148 */     this.attributes = input.attributes;
/* 149 */     this.attributeImpls = input.attributeImpls;
/* 150 */     this.currentState = input.currentState;
/* 151 */     this.factory = input.factory;
/*     */   }
/*     */ 
/*     */   public AttributeSource(AttributeFactory factory)
/*     */   {
/* 158 */     this.attributes = new LinkedHashMap();
/* 159 */     this.attributeImpls = new LinkedHashMap();
/* 160 */     this.currentState = new State[1];
/* 161 */     this.factory = factory;
/*     */   }
/*     */ 
/*     */   public AttributeFactory getAttributeFactory()
/*     */   {
/* 168 */     return this.factory;
/*     */   }
/*     */ 
/*     */   public Iterator<Class<? extends Attribute>> getAttributeClassesIterator()
/*     */   {
/* 175 */     return Collections.unmodifiableSet(this.attributes.keySet()).iterator();
/*     */   }
/*     */ 
/*     */   public Iterator<AttributeImpl> getAttributeImplsIterator()
/*     */   {
/* 183 */     State initState = getCurrentState();
/* 184 */     if (initState != null)
/* 185 */       return new Iterator(initState) {
/* 186 */         private AttributeSource.State state = this.val$initState;
/*     */ 
/*     */         public void remove() {
/* 189 */           throw new UnsupportedOperationException();
/*     */         }
/*     */ 
/*     */         public AttributeImpl next() {
/* 193 */           if (this.state == null)
/* 194 */             throw new NoSuchElementException();
/* 195 */           AttributeImpl att = this.state.attribute;
/* 196 */           this.state = this.state.next;
/* 197 */           return att;
/*     */         }
/*     */ 
/*     */         public boolean hasNext() {
/* 201 */           return this.state != null;
/*     */         }
/*     */       };
/* 205 */     return Collections.emptySet().iterator();
/*     */   }
/*     */ 
/*     */   static LinkedList<WeakReference<Class<? extends Attribute>>> getAttributeInterfaces(Class<? extends AttributeImpl> clazz)
/*     */   {
/* 214 */     synchronized (knownImplClasses) {
/* 215 */       LinkedList foundInterfaces = (LinkedList)knownImplClasses.get(clazz);
/* 216 */       if (foundInterfaces == null)
/*     */       {
/* 219 */         knownImplClasses.put(clazz, foundInterfaces = new LinkedList());
/*     */ 
/* 222 */         Class actClazz = clazz;
/*     */         do {
/* 224 */           for (Class curInterface : actClazz.getInterfaces()) {
/* 225 */             if ((curInterface != Attribute.class) && (Attribute.class.isAssignableFrom(curInterface))) {
/* 226 */               foundInterfaces.add(new WeakReference(curInterface.asSubclass(Attribute.class)));
/*     */             }
/*     */           }
/* 229 */           actClazz = actClazz.getSuperclass();
/* 230 */         }while (actClazz != null);
/*     */       }
/* 232 */       return foundInterfaces;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addAttributeImpl(AttributeImpl att)
/*     */   {
/* 245 */     Class clazz = att.getClass();
/* 246 */     if (this.attributeImpls.containsKey(clazz)) return;
/* 247 */     LinkedList foundInterfaces = getAttributeInterfaces(clazz);
/*     */ 
/* 251 */     for (WeakReference curInterfaceRef : foundInterfaces) {
/* 252 */       Class curInterface = (Class)curInterfaceRef.get();
/*     */ 
/* 254 */       assert (curInterface != null) : "We have a strong reference on the class holding the interfaces, so they should never get evicted";
/*     */ 
/* 256 */       if (!this.attributes.containsKey(curInterface))
/*     */       {
/* 258 */         this.currentState[0] = null;
/* 259 */         this.attributes.put(curInterface, att);
/* 260 */         this.attributeImpls.put(clazz, att);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public <A extends Attribute> A addAttribute(Class<A> attClass)
/*     */   {
/* 272 */     AttributeImpl attImpl = (AttributeImpl)this.attributes.get(attClass);
/* 273 */     if (attImpl == null) {
/* 274 */       if ((!attClass.isInterface()) || (!Attribute.class.isAssignableFrom(attClass))) {
/* 275 */         throw new IllegalArgumentException("addAttribute() only accepts an interface that extends Attribute, but " + attClass.getName() + " does not fulfil this contract.");
/*     */       }
/*     */ 
/* 280 */       addAttributeImpl(attImpl = this.factory.createAttributeInstance(attClass));
/*     */     }
/* 282 */     return (Attribute)attClass.cast(attImpl);
/*     */   }
/*     */ 
/*     */   public boolean hasAttributes()
/*     */   {
/* 287 */     return !this.attributes.isEmpty();
/*     */   }
/*     */ 
/*     */   public boolean hasAttribute(Class<? extends Attribute> attClass)
/*     */   {
/* 295 */     return this.attributes.containsKey(attClass);
/*     */   }
/*     */ 
/*     */   public <A extends Attribute> A getAttribute(Class<A> attClass)
/*     */   {
/* 310 */     AttributeImpl attImpl = (AttributeImpl)this.attributes.get(attClass);
/* 311 */     if (attImpl == null) {
/* 312 */       throw new IllegalArgumentException("This AttributeSource does not have the attribute '" + attClass.getName() + "'.");
/*     */     }
/* 314 */     return (Attribute)attClass.cast(attImpl);
/*     */   }
/*     */ 
/*     */   private State getCurrentState() {
/* 318 */     State s = this.currentState[0];
/* 319 */     if ((s != null) || (!hasAttributes())) {
/* 320 */       return s;
/*     */     }
/* 322 */     State c = s = this.currentState[0] =  = new State();
/* 323 */     Iterator it = this.attributeImpls.values().iterator();
/* 324 */     c.attribute = ((AttributeImpl)it.next());
/* 325 */     while (it.hasNext()) {
/* 326 */       c.next = new State();
/* 327 */       c = c.next;
/* 328 */       c.attribute = ((AttributeImpl)it.next());
/*     */     }
/* 330 */     return s;
/*     */   }
/*     */ 
/*     */   public void clearAttributes()
/*     */   {
/* 338 */     for (State state = getCurrentState(); state != null; state = state.next)
/* 339 */       state.attribute.clear();
/*     */   }
/*     */ 
/*     */   public State captureState()
/*     */   {
/* 348 */     State state = getCurrentState();
/* 349 */     return state == null ? null : (State)state.clone();
/*     */   }
/*     */ 
/*     */   public void restoreState(State state)
/*     */   {
/* 368 */     if (state == null) return;
/*     */     do
/*     */     {
/* 371 */       AttributeImpl targetImpl = (AttributeImpl)this.attributeImpls.get(state.attribute.getClass());
/* 372 */       if (targetImpl == null) {
/* 373 */         throw new IllegalArgumentException("State contains AttributeImpl of type " + state.attribute.getClass().getName() + " that is not in in this AttributeSource");
/*     */       }
/*     */ 
/* 376 */       state.attribute.copyTo(targetImpl);
/* 377 */       state = state.next;
/* 378 */     }while (state != null);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 383 */     int code = 0;
/* 384 */     for (State state = getCurrentState(); state != null; state = state.next) {
/* 385 */       code = code * 31 + state.attribute.hashCode();
/*     */     }
/* 387 */     return code;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 392 */     if (obj == this) {
/* 393 */       return true;
/*     */     }
/*     */ 
/* 396 */     if ((obj instanceof AttributeSource)) {
/* 397 */       AttributeSource other = (AttributeSource)obj;
/*     */ 
/* 399 */       if (hasAttributes()) {
/* 400 */         if (!other.hasAttributes()) {
/* 401 */           return false;
/*     */         }
/*     */ 
/* 404 */         if (this.attributeImpls.size() != other.attributeImpls.size()) {
/* 405 */           return false;
/*     */         }
/*     */ 
/* 409 */         State thisState = getCurrentState();
/* 410 */         State otherState = other.getCurrentState();
/* 411 */         while ((thisState != null) && (otherState != null)) {
/* 412 */           if ((otherState.attribute.getClass() != thisState.attribute.getClass()) || (!otherState.attribute.equals(thisState.attribute))) {
/* 413 */             return false;
/*     */           }
/* 415 */           thisState = thisState.next;
/* 416 */           otherState = otherState.next;
/*     */         }
/* 418 */         return true;
/*     */       }
/* 420 */       return !other.hasAttributes();
/*     */     }
/*     */ 
/* 423 */     return false;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 440 */     StringBuilder sb = new StringBuilder().append('(');
/* 441 */     if (hasAttributes()) {
/* 442 */       for (State state = getCurrentState(); state != null; state = state.next) {
/* 443 */         if (sb.length() > 1) sb.append(',');
/* 444 */         sb.append(state.attribute.toString());
/*     */       }
/*     */     }
/* 447 */     return ')';
/*     */   }
/*     */ 
/*     */   public final String reflectAsString(boolean prependAttClass)
/*     */   {
/* 462 */     StringBuilder buffer = new StringBuilder();
/* 463 */     reflectWith(new AttributeReflector(buffer, prependAttClass) {
/*     */       public void reflect(Class<? extends Attribute> attClass, String key, Object value) {
/* 465 */         if (this.val$buffer.length() > 0) {
/* 466 */           this.val$buffer.append(',');
/*     */         }
/* 468 */         if (this.val$prependAttClass) {
/* 469 */           this.val$buffer.append(attClass.getName()).append('#');
/*     */         }
/* 471 */         this.val$buffer.append(key).append('=').append(value == null ? "null" : value);
/*     */       }
/*     */     });
/* 474 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public final void reflectWith(AttributeReflector reflector)
/*     */   {
/* 487 */     for (State state = getCurrentState(); state != null; state = state.next)
/* 488 */       state.attribute.reflectWith(reflector);
/*     */   }
/*     */ 
/*     */   public AttributeSource cloneAttributes()
/*     */   {
/* 500 */     AttributeSource clone = new AttributeSource(this.factory);
/*     */ 
/* 502 */     if (hasAttributes())
/*     */     {
/* 504 */       for (State state = getCurrentState(); state != null; state = state.next) {
/* 505 */         clone.attributeImpls.put(state.attribute.getClass(), (AttributeImpl)state.attribute.clone());
/*     */       }
/*     */ 
/* 509 */       for (Map.Entry entry : this.attributes.entrySet()) {
/* 510 */         clone.attributes.put(entry.getKey(), clone.attributeImpls.get(((AttributeImpl)entry.getValue()).getClass()));
/*     */       }
/*     */     }
/*     */ 
/* 514 */     return clone;
/*     */   }
/*     */ 
/*     */   public final void copyTo(AttributeSource target)
/*     */   {
/* 526 */     for (State state = getCurrentState(); state != null; state = state.next) {
/* 527 */       AttributeImpl targetImpl = (AttributeImpl)target.attributeImpls.get(state.attribute.getClass());
/* 528 */       if (targetImpl == null) {
/* 529 */         throw new IllegalArgumentException("This AttributeSource contains AttributeImpl of type " + state.attribute.getClass().getName() + " that is not in the target");
/*     */       }
/*     */ 
/* 532 */       state.attribute.copyTo(targetImpl);
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 210 */     knownImplClasses = new WeakHashMap();
/*     */   }
/*     */ 
/*     */   public static final class State
/*     */     implements Cloneable
/*     */   {
/*     */     AttributeImpl attribute;
/*     */     State next;
/*     */ 
/*     */     public Object clone()
/*     */     {
/* 115 */       State clone = new State();
/* 116 */       clone.attribute = ((AttributeImpl)this.attribute.clone());
/*     */ 
/* 118 */       if (this.next != null) {
/* 119 */         clone.next = ((State)this.next.clone());
/*     */       }
/*     */ 
/* 122 */       return clone;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract class AttributeFactory
/*     */   {
/*  57 */     public static final AttributeFactory DEFAULT_ATTRIBUTE_FACTORY = new DefaultAttributeFactory(null);
/*     */ 
/*     */     public abstract AttributeImpl createAttributeInstance(Class<? extends Attribute> paramClass);
/*     */ 
/*  60 */     private static final class DefaultAttributeFactory extends AttributeSource.AttributeFactory { private static final WeakHashMap<Class<? extends Attribute>, WeakReference<Class<? extends AttributeImpl>>> attClassImplMap = new WeakHashMap();
/*     */ 
/*     */       public AttributeImpl createAttributeInstance(Class<? extends Attribute> attClass)
/*     */       {
/*     */         try
/*     */         {
/*  68 */           return (AttributeImpl)getClassForInterface(attClass).newInstance();
/*     */         } catch (InstantiationException e) {
/*  70 */           throw new IllegalArgumentException("Could not instantiate implementing class for " + attClass.getName()); } catch (IllegalAccessException e) {
/*     */         }
/*  72 */         throw new IllegalArgumentException("Could not instantiate implementing class for " + attClass.getName());
/*     */       }
/*     */ 
/*     */       private static Class<? extends AttributeImpl> getClassForInterface(Class<? extends Attribute> attClass)
/*     */       {
/*  77 */         synchronized (attClassImplMap) {
/*  78 */           WeakReference ref = (WeakReference)attClassImplMap.get(attClass);
/*  79 */           Class clazz = ref == null ? null : (Class)ref.get();
/*  80 */           if (clazz == null)
/*     */           {
/*     */             try
/*     */             {
/*  85 */               if (TermAttribute.class.equals(attClass))
/*  86 */                 clazz = CharTermAttributeImpl.class;
/*     */               else {
/*  88 */                 clazz = Class.forName(attClass.getName() + "Impl", true, attClass.getClassLoader()).asSubclass(AttributeImpl.class);
/*     */               }
/*     */ 
/*  91 */               attClassImplMap.put(attClass, new WeakReference(clazz));
/*     */             }
/*     */             catch (ClassNotFoundException e)
/*     */             {
/*  95 */               throw new IllegalArgumentException("Could not find implementing class for " + attClass.getName());
/*     */             }
/*     */           }
/*  98 */           return clazz;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.AttributeSource
 * JD-Core Version:    0.6.0
 */