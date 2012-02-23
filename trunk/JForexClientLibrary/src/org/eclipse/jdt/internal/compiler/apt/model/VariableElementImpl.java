/*     */ package org.eclipse.jdt.internal.compiler.apt.model;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.lang.model.element.Element;
/*     */ import javax.lang.model.element.ElementKind;
/*     */ import javax.lang.model.element.ElementVisitor;
/*     */ import javax.lang.model.element.Modifier;
/*     */ import javax.lang.model.element.Name;
/*     */ import javax.lang.model.element.PackageElement;
/*     */ import javax.lang.model.element.VariableElement;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.AptBinaryLocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.AptSourceLocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
/*     */ 
/*     */ public class VariableElementImpl extends ElementImpl
/*     */   implements VariableElement
/*     */ {
/*     */   VariableElementImpl(BaseProcessingEnvImpl env, VariableBinding binding)
/*     */   {
/*  49 */     super(env, binding);
/*     */   }
/*     */ 
/*     */   public <R, P> R accept(ElementVisitor<R, P> v, P p)
/*     */   {
/*  55 */     return v.visitVariable(this, p);
/*     */   }
/*     */ 
/*     */   protected AnnotationBinding[] getAnnotationBindings()
/*     */   {
/*  61 */     return ((VariableBinding)this._binding).getAnnotations();
/*     */   }
/*     */ 
/*     */   public Object getConstantValue()
/*     */   {
/*  66 */     VariableBinding variableBinding = (VariableBinding)this._binding;
/*  67 */     Constant constant = variableBinding.constant();
/*  68 */     if ((constant == null) || (constant == Constant.NotAConstant)) return null;
/*  69 */     TypeBinding type = variableBinding.type;
/*  70 */     switch (type.id) {
/*     */     case 5:
/*  72 */       return Boolean.valueOf(constant.booleanValue());
/*     */     case 3:
/*  74 */       return Byte.valueOf(constant.byteValue());
/*     */     case 2:
/*  76 */       return Character.valueOf(constant.charValue());
/*     */     case 8:
/*  78 */       return Double.valueOf(constant.doubleValue());
/*     */     case 9:
/*  80 */       return Float.valueOf(constant.floatValue());
/*     */     case 10:
/*  82 */       return Integer.valueOf(constant.intValue());
/*     */     case 11:
/*  84 */       return constant.stringValue();
/*     */     case 7:
/*  86 */       return Long.valueOf(constant.longValue());
/*     */     case 4:
/*  88 */       return Short.valueOf(constant.shortValue());
/*     */     case 6:
/*  90 */     }return null;
/*     */   }
/*     */ 
/*     */   public List<? extends Element> getEnclosedElements()
/*     */   {
/*  95 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   public Element getEnclosingElement()
/*     */   {
/* 100 */     if ((this._binding instanceof FieldBinding)) {
/* 101 */       return this._env.getFactory().newElement(((FieldBinding)this._binding).declaringClass);
/*     */     }
/* 103 */     if ((this._binding instanceof AptSourceLocalVariableBinding))
/* 104 */       return this._env.getFactory().newElement(((AptSourceLocalVariableBinding)this._binding).methodBinding);
/* 105 */     if ((this._binding instanceof AptBinaryLocalVariableBinding)) {
/* 106 */       return this._env.getFactory().newElement(((AptBinaryLocalVariableBinding)this._binding).methodBinding);
/*     */     }
/* 108 */     return null;
/*     */   }
/*     */ 
/*     */   public ElementKind getKind()
/*     */   {
/* 113 */     if ((this._binding instanceof FieldBinding)) {
/* 114 */       if (((FieldBinding)this._binding).declaringClass.isEnum()) {
/* 115 */         return ElementKind.ENUM_CONSTANT;
/*     */       }
/*     */ 
/* 118 */       return ElementKind.FIELD;
/*     */     }
/*     */ 
/* 122 */     return ElementKind.PARAMETER;
/*     */   }
/*     */ 
/*     */   public Set<Modifier> getModifiers()
/*     */   {
/* 129 */     if ((this._binding instanceof VariableBinding)) {
/* 130 */       return Factory.getModifiers(((VariableBinding)this._binding).modifiers, getKind());
/*     */     }
/* 132 */     return Collections.emptySet();
/*     */   }
/*     */ 
/*     */   PackageElement getPackage()
/*     */   {
/* 138 */     if ((this._binding instanceof FieldBinding)) {
/* 139 */       PackageBinding pkgBinding = ((FieldBinding)this._binding).declaringClass.fPackage;
/* 140 */       return this._env.getFactory().newPackageElement(pkgBinding);
/*     */     }
/*     */ 
/* 144 */     throw new UnsupportedOperationException("NYI: VariableElmentImpl.getPackage() for method parameter");
/*     */   }
/*     */ 
/*     */   public Name getSimpleName()
/*     */   {
/* 150 */     return new NameImpl(((VariableBinding)this._binding).name);
/*     */   }
/*     */ 
/*     */   public boolean hides(Element hiddenElement)
/*     */   {
/* 156 */     if ((this._binding instanceof FieldBinding)) {
/* 157 */       if (!(((ElementImpl)hiddenElement)._binding instanceof FieldBinding)) {
/* 158 */         return false;
/*     */       }
/* 160 */       FieldBinding hidden = (FieldBinding)((ElementImpl)hiddenElement)._binding;
/* 161 */       if (hidden.isPrivate()) {
/* 162 */         return false;
/*     */       }
/* 164 */       FieldBinding hider = (FieldBinding)this._binding;
/* 165 */       if (hidden == hider) {
/* 166 */         return false;
/*     */       }
/* 168 */       if (!CharOperation.equals(hider.name, hidden.name)) {
/* 169 */         return false;
/*     */       }
/* 171 */       return hider.declaringClass.findSuperTypeOriginatingFrom(hidden.declaringClass) != null;
/*     */     }
/*     */ 
/* 174 */     return false;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 179 */     return new String(((VariableBinding)this._binding).name);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.model.VariableElementImpl
 * JD-Core Version:    0.6.0
 */