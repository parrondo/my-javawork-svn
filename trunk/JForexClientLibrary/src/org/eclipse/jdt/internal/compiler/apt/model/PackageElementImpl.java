/*     */ package org.eclipse.jdt.internal.compiler.apt.model;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import javax.lang.model.element.Element;
/*     */ import javax.lang.model.element.ElementKind;
/*     */ import javax.lang.model.element.ElementVisitor;
/*     */ import javax.lang.model.element.Name;
/*     */ import javax.lang.model.element.PackageElement;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
/*     */ import org.eclipse.jdt.internal.compiler.batch.FileSystem;
/*     */ import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
/*     */ 
/*     */ public class PackageElementImpl extends ElementImpl
/*     */   implements PackageElement
/*     */ {
/*     */   PackageElementImpl(BaseProcessingEnvImpl env, PackageBinding binding)
/*     */   {
/*  42 */     super(env, binding);
/*     */   }
/*     */ 
/*     */   public <R, P> R accept(ElementVisitor<R, P> v, P p)
/*     */   {
/*  48 */     return v.visitPackage(this, p);
/*     */   }
/*     */ 
/*     */   protected AnnotationBinding[] getAnnotationBindings()
/*     */   {
/*  54 */     PackageBinding packageBinding = (PackageBinding)this._binding;
/*  55 */     char[][] compoundName = CharOperation.arrayConcat(packageBinding.compoundName, TypeConstants.PACKAGE_INFO_NAME);
/*  56 */     ReferenceBinding type = this._env.getLookupEnvironment().getType(compoundName);
/*  57 */     AnnotationBinding[] annotations = (AnnotationBinding[])null;
/*  58 */     if ((type != null) && (type.isValidBinding())) {
/*  59 */       annotations = type.getAnnotations();
/*     */     }
/*  61 */     return annotations;
/*     */   }
/*     */ 
/*     */   public List<? extends Element> getEnclosedElements()
/*     */   {
/*  66 */     PackageBinding binding = (PackageBinding)this._binding;
/*  67 */     LookupEnvironment environment = binding.environment;
/*  68 */     char[][][] typeNames = (char[][][])null;
/*  69 */     INameEnvironment nameEnvironment = binding.environment.nameEnvironment;
/*  70 */     if ((nameEnvironment instanceof FileSystem)) {
/*  71 */       typeNames = ((FileSystem)nameEnvironment).findTypeNames(binding.compoundName);
/*     */     }
/*  73 */     HashSet set = new HashSet();
/*  74 */     if (typeNames != null) {
/*  75 */       for (char[][] typeName : typeNames) {
/*  76 */         ReferenceBinding type = environment.getType(typeName);
/*  77 */         if ((type != null) && (type.isValidBinding())) {
/*  78 */           set.add(this._env.getFactory().newElement(type));
/*     */         }
/*     */       }
/*     */     }
/*  82 */     ArrayList list = new ArrayList(set.size());
/*  83 */     list.addAll(set);
/*  84 */     return Collections.unmodifiableList(list);
/*     */   }
/*     */ 
/*     */   public Element getEnclosingElement()
/*     */   {
/*  90 */     return null;
/*     */   }
/*     */ 
/*     */   public ElementKind getKind()
/*     */   {
/*  95 */     return ElementKind.PACKAGE;
/*     */   }
/*     */ 
/*     */   PackageElement getPackage()
/*     */   {
/* 101 */     return this;
/*     */   }
/*     */ 
/*     */   public Name getSimpleName()
/*     */   {
/* 106 */     char[][] compoundName = ((PackageBinding)this._binding).compoundName;
/* 107 */     int length = compoundName.length;
/* 108 */     if (length == 0) {
/* 109 */       return new NameImpl(CharOperation.NO_CHAR);
/*     */     }
/* 111 */     return new NameImpl(compoundName[(length - 1)]);
/*     */   }
/*     */ 
/*     */   public Name getQualifiedName()
/*     */   {
/* 116 */     return new NameImpl(CharOperation.concatWith(((PackageBinding)this._binding).compoundName, '.'));
/*     */   }
/*     */ 
/*     */   public boolean isUnnamed()
/*     */   {
/* 121 */     PackageBinding binding = (PackageBinding)this._binding;
/* 122 */     return binding.compoundName == CharOperation.NO_CHAR_CHAR;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.model.PackageElementImpl
 * JD-Core Version:    0.6.0
 */