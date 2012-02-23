/*     */ package org.eclipse.jdt.internal.compiler.apt.model;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.List<+Ljavax.lang.model.element.Element;>;
/*     */ import java.util.Set;
/*     */ import javax.lang.model.element.Element;
/*     */ import javax.lang.model.element.ElementKind;
/*     */ import javax.lang.model.element.ElementVisitor;
/*     */ import javax.lang.model.element.ExecutableElement;
/*     */ import javax.lang.model.element.Modifier;
/*     */ import javax.lang.model.element.Name;
/*     */ import javax.lang.model.element.NestingKind;
/*     */ import javax.lang.model.element.PackageElement;
/*     */ import javax.lang.model.element.TypeElement;
/*     */ import javax.lang.model.element.TypeParameterElement;
/*     */ import javax.lang.model.element.VariableElement;
/*     */ import javax.lang.model.type.TypeKind;
/*     */ import javax.lang.model.type.TypeMirror;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
/*     */ 
/*     */ public class TypeElementImpl extends ElementImpl
/*     */   implements TypeElement
/*     */ {
/*     */   private final ElementKind _kindHint;
/*     */ 
/*     */   TypeElementImpl(BaseProcessingEnvImpl env, ReferenceBinding binding, ElementKind kindHint)
/*     */   {
/*  54 */     super(env, binding);
/*  55 */     this._kindHint = kindHint;
/*     */   }
/*     */ 
/*     */   public <R, P> R accept(ElementVisitor<R, P> v, P p)
/*     */   {
/*  61 */     return v.visitType(this, p);
/*     */   }
/*     */ 
/*     */   protected AnnotationBinding[] getAnnotationBindings()
/*     */   {
/*  67 */     return ((ReferenceBinding)this._binding).getAnnotations();
/*     */   }
/*     */ 
/*     */   public List<? extends Element> getEnclosedElements()
/*     */   {
/*  72 */     ReferenceBinding binding = (ReferenceBinding)this._binding;
/*  73 */     List enclosed = new ArrayList(binding.fieldCount() + binding.methods().length);
/*  74 */     for (MethodBinding method : binding.methods()) {
/*  75 */       ExecutableElement executable = new ExecutableElementImpl(this._env, method);
/*  76 */       enclosed.add(executable);
/*     */     }
/*  78 */     for (FieldBinding field : binding.fields())
/*     */     {
/*  80 */       if (!field.isSynthetic()) {
/*  81 */         VariableElement variable = new VariableElementImpl(this._env, field);
/*  82 */         enclosed.add(variable);
/*     */       }
/*     */     }
/*  85 */     for (ReferenceBinding memberType : binding.memberTypes()) {
/*  86 */       TypeElement type = new TypeElementImpl(this._env, memberType, null);
/*  87 */       enclosed.add(type);
/*     */     }
/*  89 */     return (List<? extends Element>)Collections.unmodifiableList(enclosed);
/*     */   }
/*     */ 
/*     */   public Element getEnclosingElement()
/*     */   {
/*  94 */     ReferenceBinding binding = (ReferenceBinding)this._binding;
/*  95 */     ReferenceBinding enclosingType = binding.enclosingType();
/*  96 */     if (enclosingType == null)
/*     */     {
/*  98 */       return this._env.getFactory().newPackageElement(binding.fPackage);
/*     */     }
/*     */ 
/* 101 */     return this._env.getFactory().newElement(binding.enclosingType());
/*     */   }
/*     */ 
/*     */   public String getFileName()
/*     */   {
/* 107 */     char[] name = ((ReferenceBinding)this._binding).getFileName();
/* 108 */     if (name == null)
/* 109 */       return null;
/* 110 */     return new String(name);
/*     */   }
/*     */ 
/*     */   public List<? extends TypeMirror> getInterfaces()
/*     */   {
/* 115 */     ReferenceBinding binding = (ReferenceBinding)this._binding;
/* 116 */     if ((binding.superInterfaces() == null) || (binding.superInterfaces().length == 0)) {
/* 117 */       return Collections.emptyList();
/*     */     }
/* 119 */     List interfaces = new ArrayList(binding.superInterfaces().length);
/* 120 */     for (ReferenceBinding interfaceBinding : binding.superInterfaces())
/*     */     {
/* 124 */       if (!interfaceBinding.isValidBinding())
/*     */         continue;
/* 126 */       if (((interfaceBinding instanceof MissingTypeBinding)) || (
/* 127 */         ((interfaceBinding instanceof ParameterizedTypeBinding)) && 
/* 128 */         ((((ParameterizedTypeBinding)interfaceBinding).genericType() instanceof MissingTypeBinding))))
/*     */       {
/*     */         continue;
/*     */       }
/* 132 */       TypeMirror interfaceType = this._env.getFactory().newTypeMirror(interfaceBinding);
/* 133 */       interfaces.add(interfaceType);
/*     */     }
/*     */ 
/* 136 */     return Collections.unmodifiableList(interfaces);
/*     */   }
/*     */ 
/*     */   public ElementKind getKind()
/*     */   {
/* 141 */     if (this._kindHint != null) {
/* 142 */       return this._kindHint;
/*     */     }
/* 144 */     ReferenceBinding refBinding = (ReferenceBinding)this._binding;
/*     */ 
/* 146 */     if (refBinding.isEnum()) {
/* 147 */       return ElementKind.ENUM;
/*     */     }
/* 149 */     if (refBinding.isAnnotationType()) {
/* 150 */       return ElementKind.ANNOTATION_TYPE;
/*     */     }
/* 152 */     if (refBinding.isInterface()) {
/* 153 */       return ElementKind.INTERFACE;
/*     */     }
/* 155 */     if (refBinding.isClass()) {
/* 156 */       return ElementKind.CLASS;
/*     */     }
/*     */ 
/* 159 */     throw new IllegalArgumentException("TypeElement " + new String(refBinding.shortReadableName()) + 
/* 160 */       " has unexpected attributes " + refBinding.modifiers);
/*     */   }
/*     */ 
/*     */   public Set<Modifier> getModifiers()
/*     */   {
/* 167 */     ReferenceBinding refBinding = (ReferenceBinding)this._binding;
/* 168 */     int modifiers = refBinding.modifiers;
/* 169 */     if ((refBinding.isInterface()) && (refBinding.isNestedType())) {
/* 170 */       modifiers |= 8;
/*     */     }
/* 172 */     return Factory.getModifiers(modifiers, getKind(), refBinding.isBinaryBinding());
/*     */   }
/*     */ 
/*     */   public NestingKind getNestingKind()
/*     */   {
/* 177 */     ReferenceBinding refBinding = (ReferenceBinding)this._binding;
/* 178 */     if (refBinding.isAnonymousType())
/* 179 */       return NestingKind.ANONYMOUS;
/* 180 */     if (refBinding.isLocalType())
/* 181 */       return NestingKind.LOCAL;
/* 182 */     if (refBinding.isMemberType()) {
/* 183 */       return NestingKind.MEMBER;
/*     */     }
/* 185 */     return NestingKind.TOP_LEVEL;
/*     */   }
/*     */ 
/*     */   PackageElement getPackage()
/*     */   {
/* 191 */     ReferenceBinding binding = (ReferenceBinding)this._binding;
/* 192 */     return this._env.getFactory().newPackageElement(binding.fPackage);
/*     */   }
/*     */ 
/*     */   public Name getQualifiedName()
/*     */   {
/* 197 */     ReferenceBinding binding = (ReferenceBinding)this._binding;
/*     */     char[] qName;
/* 199 */     if (binding.isMemberType()) {
/* 200 */       char[] qName = CharOperation.concatWith(binding.enclosingType().compoundName, binding.sourceName, '.');
/* 201 */       CharOperation.replace(qName, '$', '.');
/*     */     } else {
/* 203 */       qName = CharOperation.concatWith(binding.compoundName, '.');
/*     */     }
/* 205 */     return new NameImpl(qName);
/*     */   }
/*     */ 
/*     */   public Name getSimpleName()
/*     */   {
/* 216 */     ReferenceBinding binding = (ReferenceBinding)this._binding;
/* 217 */     return new NameImpl(binding.sourceName());
/*     */   }
/*     */ 
/*     */   public TypeMirror getSuperclass()
/*     */   {
/* 222 */     ReferenceBinding binding = (ReferenceBinding)this._binding;
/* 223 */     ReferenceBinding superBinding = binding.superclass();
/* 224 */     if ((superBinding == null) || (binding.isInterface())) {
/* 225 */       return this._env.getFactory().getNoType(TypeKind.NONE);
/*     */     }
/*     */ 
/* 228 */     return this._env.getFactory().newDeclaredType(superBinding);
/*     */   }
/*     */ 
/*     */   public List<? extends TypeParameterElement> getTypeParameters()
/*     */   {
/* 233 */     ReferenceBinding binding = (ReferenceBinding)this._binding;
/* 234 */     TypeVariableBinding[] variables = binding.typeVariables();
/* 235 */     if (variables.length == 0) {
/* 236 */       return Collections.emptyList();
/*     */     }
/* 238 */     List params = new ArrayList(variables.length);
/* 239 */     for (TypeVariableBinding variable : variables) {
/* 240 */       params.add(this._env.getFactory().newTypeParameterElement(variable, this));
/*     */     }
/* 242 */     return Collections.unmodifiableList(params);
/*     */   }
/*     */ 
/*     */   public boolean hides(Element hidden)
/*     */   {
/* 248 */     if (!(hidden instanceof TypeElementImpl)) {
/* 249 */       return false;
/*     */     }
/* 251 */     ReferenceBinding hiddenBinding = (ReferenceBinding)((TypeElementImpl)hidden)._binding;
/* 252 */     if (hiddenBinding.isPrivate()) {
/* 253 */       return false;
/*     */     }
/* 255 */     ReferenceBinding hiderBinding = (ReferenceBinding)this._binding;
/* 256 */     if (hiddenBinding == hiderBinding) {
/* 257 */       return false;
/*     */     }
/* 259 */     if ((!hiddenBinding.isMemberType()) || (!hiderBinding.isMemberType())) {
/* 260 */       return false;
/*     */     }
/* 262 */     if (!CharOperation.equals(hiddenBinding.sourceName, hiderBinding.sourceName)) {
/* 263 */       return false;
/*     */     }
/* 265 */     return hiderBinding.enclosingType().findSuperTypeOriginatingFrom(hiddenBinding.enclosingType()) != null;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 270 */     ReferenceBinding binding = (ReferenceBinding)this._binding;
/* 271 */     char[] concatWith = CharOperation.concatWith(binding.compoundName, '.');
/* 272 */     if (binding.isNestedType()) {
/* 273 */       CharOperation.replace(concatWith, '$', '.');
/* 274 */       return new String(concatWith);
/*     */     }
/* 276 */     return new String(concatWith);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.model.TypeElementImpl
 * JD-Core Version:    0.6.0
 */