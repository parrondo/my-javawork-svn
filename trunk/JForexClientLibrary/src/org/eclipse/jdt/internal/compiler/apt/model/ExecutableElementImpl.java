/*     */ package org.eclipse.jdt.internal.compiler.apt.model;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.lang.model.element.AnnotationValue;
/*     */ import javax.lang.model.element.Element;
/*     */ import javax.lang.model.element.ElementKind;
/*     */ import javax.lang.model.element.ElementVisitor;
/*     */ import javax.lang.model.element.ExecutableElement;
/*     */ import javax.lang.model.element.Modifier;
/*     */ import javax.lang.model.element.Name;
/*     */ import javax.lang.model.element.PackageElement;
/*     */ import javax.lang.model.element.TypeElement;
/*     */ import javax.lang.model.element.TypeParameterElement;
/*     */ import javax.lang.model.element.VariableElement;
/*     */ import javax.lang.model.type.TypeMirror;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
/*     */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Argument;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.AnnotationHolder;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.AptBinaryLocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodVerifier;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
/*     */ 
/*     */ public class ExecutableElementImpl extends ElementImpl
/*     */   implements ExecutableElement
/*     */ {
/*  50 */   private Name _name = null;
/*     */ 
/*     */   ExecutableElementImpl(BaseProcessingEnvImpl env, MethodBinding binding) {
/*  53 */     super(env, binding);
/*     */   }
/*     */ 
/*     */   public <R, P> R accept(ElementVisitor<R, P> v, P p)
/*     */   {
/*  59 */     return v.visitExecutable(this, p);
/*     */   }
/*     */ 
/*     */   protected AnnotationBinding[] getAnnotationBindings()
/*     */   {
/*  65 */     return ((MethodBinding)this._binding).getAnnotations();
/*     */   }
/*     */ 
/*     */   public AnnotationValue getDefaultValue()
/*     */   {
/*  70 */     MethodBinding binding = (MethodBinding)this._binding;
/*  71 */     Object defaultValue = binding.getDefaultValue();
/*  72 */     if (defaultValue != null) return new AnnotationMemberValue(this._env, defaultValue, binding);
/*  73 */     return null;
/*     */   }
/*     */ 
/*     */   public List<? extends Element> getEnclosedElements()
/*     */   {
/*  78 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   public Element getEnclosingElement()
/*     */   {
/*  83 */     MethodBinding binding = (MethodBinding)this._binding;
/*  84 */     if (binding.declaringClass == null) {
/*  85 */       return null;
/*     */     }
/*  87 */     return this._env.getFactory().newElement(binding.declaringClass);
/*     */   }
/*     */ 
/*     */   public String getFileName()
/*     */   {
/*  92 */     ReferenceBinding dc = ((MethodBinding)this._binding).declaringClass;
/*  93 */     char[] name = dc.getFileName();
/*  94 */     if (name == null)
/*  95 */       return null;
/*  96 */     return new String(name);
/*     */   }
/*     */ 
/*     */   public ElementKind getKind()
/*     */   {
/* 101 */     MethodBinding binding = (MethodBinding)this._binding;
/* 102 */     if (binding.isConstructor()) {
/* 103 */       return ElementKind.CONSTRUCTOR;
/*     */     }
/* 105 */     if (CharOperation.equals(binding.selector, TypeConstants.CLINIT)) {
/* 106 */       return ElementKind.STATIC_INIT;
/*     */     }
/* 108 */     if (CharOperation.equals(binding.selector, TypeConstants.INIT)) {
/* 109 */       return ElementKind.INSTANCE_INIT;
/*     */     }
/*     */ 
/* 112 */     return ElementKind.METHOD;
/*     */   }
/*     */ 
/*     */   public Set<Modifier> getModifiers()
/*     */   {
/* 118 */     MethodBinding binding = (MethodBinding)this._binding;
/* 119 */     return Factory.getModifiers(binding.modifiers, getKind());
/*     */   }
/*     */ 
/*     */   PackageElement getPackage()
/*     */   {
/* 125 */     MethodBinding binding = (MethodBinding)this._binding;
/* 126 */     if (binding.declaringClass == null) {
/* 127 */       return null;
/*     */     }
/* 129 */     return this._env.getFactory().newPackageElement(binding.declaringClass.fPackage);
/*     */   }
/*     */ 
/*     */   public List<? extends VariableElement> getParameters()
/*     */   {
/* 134 */     MethodBinding binding = (MethodBinding)this._binding;
/* 135 */     int length = binding.parameters == null ? 0 : binding.parameters.length;
/* 136 */     if (length != 0) {
/* 137 */       AbstractMethodDeclaration methodDeclaration = binding.sourceMethod();
/* 138 */       List params = new ArrayList(length);
/* 139 */       if (methodDeclaration != null) {
/* 140 */         for (Argument argument : methodDeclaration.arguments) {
/* 141 */           VariableElement param = new VariableElementImpl(this._env, argument.binding);
/* 142 */           params.add(param);
/*     */         }
/*     */       }
/*     */       else {
/* 146 */         boolean isEnumConstructor = (binding.isConstructor()) && 
/* 147 */           (binding.declaringClass.isEnum()) && 
/* 148 */           (binding.declaringClass.isBinaryBinding()) && 
/* 149 */           ((binding.modifiers & 0x40000000) == 0);
/* 150 */         AnnotationBinding[][] parameterAnnotationBindings = (AnnotationBinding[][])null;
/* 151 */         AnnotationHolder annotationHolder = binding.declaringClass.retrieveAnnotationHolder(binding, false);
/* 152 */         if (annotationHolder != null)
/* 153 */           parameterAnnotationBindings = annotationHolder.getParameterAnnotations();
/*     */         StringBuilder builder;
/*     */         VariableElement param;
/* 156 */         if (isEnumConstructor) {
/* 157 */           if (length == 2)
/*     */           {
/* 159 */             return Collections.emptyList();
/*     */           }
/* 161 */           for (int i = 2; i < length; i++) {
/* 162 */             TypeBinding typeBinding = binding.parameters[i];
/* 163 */             builder = new StringBuilder("arg");
/* 164 */             builder.append(i - 2);
/* 165 */             param = new VariableElementImpl(this._env, 
/* 166 */               new AptBinaryLocalVariableBinding(
/* 167 */               String.valueOf(builder).toCharArray(), 
/* 168 */               typeBinding, 
/* 169 */               0, 
/* 170 */               null, 
/* 171 */               binding));
/* 172 */             params.add(param);
/*     */           }
/*     */         } else {
/* 175 */           int i = 0;
/*     */           TypeBinding[] arrayOfTypeBinding;
/* 176 */           param = (arrayOfTypeBinding = binding.parameters).length; for (builder = 0; builder < param; builder++) { TypeBinding typeBinding = arrayOfTypeBinding[builder];
/* 177 */             StringBuilder builder = new StringBuilder("arg");
/* 178 */             builder.append(i);
/* 179 */             VariableElement param = new VariableElementImpl(this._env, 
/* 180 */               new AptBinaryLocalVariableBinding(
/* 181 */               String.valueOf(builder).toCharArray(), 
/* 182 */               typeBinding, 
/* 183 */               0, 
/* 184 */               parameterAnnotationBindings != null ? parameterAnnotationBindings[i] : null, 
/* 185 */               binding));
/* 186 */             params.add(param);
/* 187 */             i++;
/*     */           }
/*     */         }
/*     */       }
/* 191 */       return Collections.unmodifiableList(params);
/*     */     }
/* 193 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   public TypeMirror getReturnType()
/*     */   {
/* 198 */     MethodBinding binding = (MethodBinding)this._binding;
/* 199 */     if (binding.returnType == null) {
/* 200 */       return null;
/*     */     }
/* 202 */     return this._env.getFactory().newTypeMirror(binding.returnType);
/*     */   }
/*     */ 
/*     */   public Name getSimpleName()
/*     */   {
/* 207 */     MethodBinding binding = (MethodBinding)this._binding;
/* 208 */     if (this._name == null) {
/* 209 */       this._name = new NameImpl(binding.selector);
/*     */     }
/* 211 */     return this._name;
/*     */   }
/*     */ 
/*     */   public List<? extends TypeMirror> getThrownTypes()
/*     */   {
/* 216 */     MethodBinding binding = (MethodBinding)this._binding;
/* 217 */     if (binding.thrownExceptions.length == 0) {
/* 218 */       return Collections.emptyList();
/*     */     }
/* 220 */     List list = new ArrayList(binding.thrownExceptions.length);
/* 221 */     for (ReferenceBinding exception : binding.thrownExceptions) {
/* 222 */       list.add(this._env.getFactory().newTypeMirror(exception));
/*     */     }
/* 224 */     return list;
/*     */   }
/*     */ 
/*     */   public List<? extends TypeParameterElement> getTypeParameters()
/*     */   {
/* 229 */     MethodBinding binding = (MethodBinding)this._binding;
/* 230 */     TypeVariableBinding[] variables = binding.typeVariables();
/* 231 */     if (variables.length == 0) {
/* 232 */       return Collections.emptyList();
/*     */     }
/* 234 */     List params = new ArrayList(variables.length);
/* 235 */     for (TypeVariableBinding variable : variables) {
/* 236 */       params.add(this._env.getFactory().newTypeParameterElement(variable, this));
/*     */     }
/* 238 */     return Collections.unmodifiableList(params);
/*     */   }
/*     */ 
/*     */   public boolean hides(Element hidden)
/*     */   {
/* 244 */     if (!(hidden instanceof ExecutableElementImpl)) {
/* 245 */       return false;
/*     */     }
/* 247 */     MethodBinding hiderBinding = (MethodBinding)this._binding;
/* 248 */     MethodBinding hiddenBinding = (MethodBinding)((ExecutableElementImpl)hidden)._binding;
/* 249 */     if (hiderBinding == hiddenBinding) {
/* 250 */       return false;
/*     */     }
/* 252 */     if (hiddenBinding.isPrivate()) {
/* 253 */       return false;
/*     */     }
/*     */ 
/* 256 */     if ((!hiderBinding.isStatic()) || (!hiddenBinding.isStatic())) {
/* 257 */       return false;
/*     */     }
/*     */ 
/* 260 */     if (!CharOperation.equals(hiddenBinding.selector, hiderBinding.selector)) {
/* 261 */       return false;
/*     */     }
/*     */ 
/* 264 */     if (!this._env.getLookupEnvironment().methodVerifier().isMethodSubsignature(hiderBinding, hiddenBinding)) {
/* 265 */       return false;
/*     */     }
/* 267 */     return hiderBinding.declaringClass.findSuperTypeOriginatingFrom(hiddenBinding.declaringClass) != null;
/*     */   }
/*     */ 
/*     */   public boolean isVarArgs()
/*     */   {
/* 272 */     return ((MethodBinding)this._binding).isVarargs();
/*     */   }
/*     */ 
/*     */   public boolean overrides(ExecutableElement overridden, TypeElement type)
/*     */   {
/* 295 */     MethodBinding overriddenBinding = (MethodBinding)((ExecutableElementImpl)overridden)._binding;
/* 296 */     ReferenceBinding overriderContext = (ReferenceBinding)((TypeElementImpl)type)._binding;
/* 297 */     if (((MethodBinding)this._binding == overriddenBinding) || 
/* 298 */       (overriddenBinding.isStatic()) || 
/* 299 */       (overriddenBinding.isPrivate()) || 
/* 300 */       (((MethodBinding)this._binding).isStatic())) {
/* 301 */       return false;
/*     */     }
/* 303 */     char[] selector = ((MethodBinding)this._binding).selector;
/* 304 */     if (!CharOperation.equals(selector, overriddenBinding.selector)) {
/* 305 */       return false;
/*     */     }
/*     */ 
/* 310 */     if ((overriderContext.findSuperTypeOriginatingFrom(((MethodBinding)this._binding).declaringClass) == null) && 
/* 311 */       (((MethodBinding)this._binding).declaringClass.findSuperTypeOriginatingFrom(overriderContext) == null)) {
/* 312 */       return false;
/*     */     }
/* 314 */     MethodBinding overriderBinding = new MethodBinding((MethodBinding)this._binding, overriderContext);
/* 315 */     if (overriderBinding.isPrivate())
/*     */     {
/* 319 */       return false;
/*     */     }
/*     */ 
/* 322 */     TypeBinding match = overriderBinding.declaringClass.findSuperTypeOriginatingFrom(overriddenBinding.declaringClass);
/* 323 */     if (!(match instanceof ReferenceBinding)) return false;
/*     */ 
/* 325 */     MethodBinding[] superMethods = ((ReferenceBinding)match).getMethods(selector);
/* 326 */     LookupEnvironment lookupEnvironment = this._env.getLookupEnvironment();
/* 327 */     if (lookupEnvironment == null) return false;
/* 328 */     MethodVerifier methodVerifier = lookupEnvironment.methodVerifier();
/* 329 */     int i = 0; for (int length = superMethods.length; i < length; i++) {
/* 330 */       if (superMethods[i].original() == overriddenBinding) {
/* 331 */         return methodVerifier.doesMethodOverride(overriderBinding, superMethods[i]);
/*     */       }
/*     */     }
/* 334 */     return false;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.model.ExecutableElementImpl
 * JD-Core Version:    0.6.0
 */