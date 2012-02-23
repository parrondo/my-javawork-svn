/*     */ package org.eclipse.jdt.internal.compiler.apt.dispatch;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.Set<+Ljavax.lang.model.element.Element;>;
/*     */ import javax.annotation.processing.RoundEnvironment;
/*     */ import javax.lang.model.element.Element;
/*     */ import javax.lang.model.element.ElementKind;
/*     */ import javax.lang.model.element.TypeElement;
/*     */ import javax.lang.model.util.ElementFilter;
/*     */ import javax.lang.model.util.Elements;
/*     */ import org.eclipse.jdt.internal.compiler.apt.model.Factory;
/*     */ import org.eclipse.jdt.internal.compiler.apt.model.TypeElementImpl;
/*     */ import org.eclipse.jdt.internal.compiler.apt.util.ManyToMany;
/*     */ import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*     */ 
/*     */ public class RoundEnvImpl
/*     */   implements RoundEnvironment
/*     */ {
/*     */   private final BaseProcessingEnvImpl _processingEnv;
/*     */   private final boolean _isLastRound;
/*     */   private final CompilationUnitDeclaration[] _units;
/*     */   private final ManyToMany<TypeElement, Element> _annoToUnit;
/*     */   private final ReferenceBinding[] _binaryTypes;
/*     */   private final Factory _factory;
/*  44 */   private Set<Element> _rootElements = null;
/*     */ 
/*     */   public RoundEnvImpl(CompilationUnitDeclaration[] units, ReferenceBinding[] binaryTypeBindings, boolean isLastRound, BaseProcessingEnvImpl env) {
/*  47 */     this._processingEnv = env;
/*  48 */     this._isLastRound = isLastRound;
/*  49 */     this._units = units;
/*  50 */     this._factory = this._processingEnv.getFactory();
/*     */ 
/*  53 */     AnnotationDiscoveryVisitor visitor = new AnnotationDiscoveryVisitor(this._processingEnv);
/*  54 */     if (this._units != null) {
/*  55 */       for (CompilationUnitDeclaration unit : this._units) {
/*  56 */         unit.traverse(visitor, unit.scope);
/*     */       }
/*     */     }
/*  59 */     this._annoToUnit = visitor._annoToElement;
/*  60 */     if (binaryTypeBindings != null) collectAnnotations(binaryTypeBindings);
/*  61 */     this._binaryTypes = binaryTypeBindings;
/*     */   }
/*     */ 
/*     */   private void collectAnnotations(ReferenceBinding[] referenceBindings) {
/*  65 */     for (ReferenceBinding referenceBinding : referenceBindings)
/*     */     {
/*  67 */       AnnotationBinding[] annotationBindings = referenceBinding.getAnnotations();
/*     */       AnnotationBinding[] arrayOfAnnotationBinding1;
/*  68 */       TypeElement localTypeElement2 = (arrayOfAnnotationBinding1 = annotationBindings).length; for (TypeElement localTypeElement1 = 0; localTypeElement1 < localTypeElement2; localTypeElement1++) { AnnotationBinding annotationBinding = arrayOfAnnotationBinding1[localTypeElement1];
/*  69 */         anno = (TypeElement)this._factory.newElement(annotationBinding.getAnnotationType());
/*  70 */         Element element = this._factory.newElement(referenceBinding);
/*  71 */         this._annoToUnit.put(anno, element);
/*     */       }
/*  73 */       FieldBinding[] fieldBindings = referenceBinding.fields();
/*  74 */       TypeElement localTypeElement3 = (anno = fieldBindings).length;
/*     */       AnnotationBinding annotationBinding;
/*     */       TypeElement anno;
/*  74 */       for (localTypeElement2 = 0; localTypeElement2 < localTypeElement3; localTypeElement2++) { FieldBinding fieldBinding = anno[localTypeElement2];
/*  75 */         annotationBindings = fieldBinding.getAnnotations();
/*  76 */         for (annotationBinding : annotationBindings) {
/*  77 */           anno = (TypeElement)this._factory.newElement(annotationBinding.getAnnotationType());
/*  78 */           Element element = this._factory.newElement(fieldBinding);
/*  79 */           this._annoToUnit.put(anno, element);
/*     */         }
/*     */       }
/*  82 */       MethodBinding[] methodBindings = referenceBinding.methods();
/*  83 */       TypeElement anno = (annotationBinding = methodBindings).length; for (localTypeElement3 = 0; localTypeElement3 < anno; localTypeElement3++) { MethodBinding methodBinding = annotationBinding[localTypeElement3];
/*  84 */         annotationBindings = methodBinding.getAnnotations();
/*  85 */         for (AnnotationBinding annotationBinding : annotationBindings) {
/*  86 */           TypeElement anno = (TypeElement)this._factory.newElement(annotationBinding.getAnnotationType());
/*  87 */           Element element = this._factory.newElement(methodBinding);
/*  88 */           this._annoToUnit.put(anno, element);
/*     */         }
/*     */       }
/*  91 */       ReferenceBinding[] memberTypes = referenceBinding.memberTypes();
/*  92 */       collectAnnotations(memberTypes);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<TypeElement> getRootAnnotations()
/*     */   {
/* 104 */     return Collections.unmodifiableSet(this._annoToUnit.getKeySet());
/*     */   }
/*     */ 
/*     */   public boolean errorRaised()
/*     */   {
/* 110 */     return this._processingEnv.errorRaised();
/*     */   }
/*     */ 
/*     */   public Set<? extends Element> getElementsAnnotatedWith(TypeElement a)
/*     */   {
/* 122 */     if (a.getKind() != ElementKind.ANNOTATION_TYPE) {
/* 123 */       throw new IllegalArgumentException("Argument must represent an annotation type");
/*     */     }
/* 125 */     Binding annoBinding = ((TypeElementImpl)a)._binding;
/* 126 */     if (0L != (annoBinding.getAnnotationTagBits() & 0x0)) {
/* 127 */       Set annotatedElements = new HashSet(this._annoToUnit.getValues(a));
/*     */ 
/* 130 */       ReferenceBinding annoTypeBinding = (ReferenceBinding)((TypeElementImpl)a)._binding;
/* 131 */       for (TypeElement element : ElementFilter.typesIn(getRootElements())) {
/* 132 */         ReferenceBinding typeBinding = (ReferenceBinding)((TypeElementImpl)element)._binding;
/* 133 */         addAnnotatedElements(annoTypeBinding, typeBinding, annotatedElements);
/*     */       }
/* 135 */       return Collections.unmodifiableSet(annotatedElements);
/*     */     }
/* 137 */     return Collections.unmodifiableSet(this._annoToUnit.getValues(a));
/*     */   }
/*     */ 
/*     */   private void addAnnotatedElements(ReferenceBinding anno, ReferenceBinding type, Set<Element> result)
/*     */   {
/* 148 */     if ((type.isClass()) && 
/* 149 */       (inheritsAnno(type, anno))) {
/* 150 */       result.add(this._factory.newElement(type));
/*     */     }
/*     */ 
/* 153 */     for (ReferenceBinding element : type.memberTypes())
/* 154 */       addAnnotatedElements(anno, element, result);
/*     */   }
/*     */ 
/*     */   private boolean inheritsAnno(ReferenceBinding element, ReferenceBinding anno)
/*     */   {
/*     */     do
/*     */     {
/* 166 */       AnnotationBinding[] annos = element.getAnnotations();
/* 167 */       for (AnnotationBinding annoBinding : annos)
/* 168 */         if (annoBinding.getAnnotationType() == anno)
/*     */         {
/* 170 */           return true;
/*     */         }
/*     */     }
/* 173 */     while ((element = element.superclass()) != null);
/* 174 */     return false;
/*     */   }
/*     */ 
/*     */   public Set<? extends Element> getElementsAnnotatedWith(Class<? extends Annotation> a)
/*     */   {
/* 180 */     String canonicalName = a.getCanonicalName();
/* 181 */     if (canonicalName == null)
/*     */     {
/* 183 */       throw new IllegalArgumentException("Argument must represent an annotation type");
/*     */     }
/* 185 */     TypeElement annoType = this._processingEnv.getElementUtils().getTypeElement(canonicalName);
/* 186 */     return getElementsAnnotatedWith(annoType);
/*     */   }
/*     */ 
/*     */   public Set<? extends Element> getRootElements()
/*     */   {
/* 192 */     if (this._units == null) {
/* 193 */       return Collections.emptySet();
/*     */     }
/* 195 */     if (this._rootElements == null) {
/* 196 */       Set elements = new HashSet(this._units.length);
/* 197 */       for (CompilationUnitDeclaration unit : this._units) {
/* 198 */         if ((unit.scope == null) || (unit.scope.topLevelTypes == null))
/*     */           continue;
/* 200 */         for (SourceTypeBinding binding : unit.scope.topLevelTypes) {
/* 201 */           Element element = this._factory.newElement(binding);
/* 202 */           if (element == null) {
/* 203 */             throw new IllegalArgumentException("Top-level type binding could not be converted to element: " + binding);
/*     */           }
/* 205 */           elements.add(element);
/*     */         }
/*     */       }
/* 208 */       if (this._binaryTypes != null) {
/* 209 */         for (ReferenceBinding typeBinding : this._binaryTypes) {
/* 210 */           TypeElement element = (TypeElement)this._factory.newElement(typeBinding);
/* 211 */           if (element == null) {
/* 212 */             throw new IllegalArgumentException("Top-level type binding could not be converted to element: " + typeBinding);
/*     */           }
/* 214 */           elements.add(element);
/*     */         }
/*     */       }
/* 217 */       this._rootElements = elements;
/*     */     }
/* 219 */     return (Set<? extends Element>)this._rootElements;
/*     */   }
/*     */ 
/*     */   public boolean processingOver()
/*     */   {
/* 225 */     return this._isLastRound;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.dispatch.RoundEnvImpl
 * JD-Core Version:    0.6.0
 */