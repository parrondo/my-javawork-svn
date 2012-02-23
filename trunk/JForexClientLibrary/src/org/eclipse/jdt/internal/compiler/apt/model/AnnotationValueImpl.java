/*     */ package org.eclipse.jdt.internal.compiler.apt.model;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.lang.model.element.AnnotationMirror;
/*     */ import javax.lang.model.element.AnnotationValue;
/*     */ import javax.lang.model.element.AnnotationValueVisitor;
/*     */ import javax.lang.model.element.VariableElement;
/*     */ import javax.lang.model.type.TypeMirror;
/*     */ import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.DoubleConstant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.FloatConstant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.LongConstant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ShouldNotImplement;
/*     */ 
/*     */ public class AnnotationValueImpl
/*     */   implements AnnotationValue, TypeIds
/*     */ {
/*     */   private static final int T_AnnotationMirror = -1;
/*     */   private static final int T_EnumConstant = -2;
/*     */   private static final int T_ClassObject = -3;
/*     */   private static final int T_ArrayType = -4;
/*     */   private final BaseProcessingEnvImpl _env;
/*     */   private final Object _value;
/*     */   private final int _kind;
/*     */ 
/*     */   public AnnotationValueImpl(BaseProcessingEnvImpl env, Object value, TypeBinding type)
/*     */   {
/*  86 */     this._env = env;
/*  87 */     int[] kind = new int[1];
/*  88 */     if (type == null) {
/*  89 */       this._value = convertToMirrorType(value, type, kind);
/*  90 */       this._kind = kind[0];
/*  91 */     } else if (type.isArrayType()) {
/*  92 */       List convertedValues = null;
/*  93 */       TypeBinding valueType = ((ArrayBinding)type).elementsType();
/*  94 */       if ((value instanceof Object[])) {
/*  95 */         Object[] values = (Object[])value;
/*  96 */         convertedValues = new ArrayList(values.length);
/*  97 */         for (Object oneValue : values)
/*  98 */           convertedValues.add(new AnnotationValueImpl(this._env, oneValue, valueType));
/*     */       }
/*     */       else {
/* 101 */         convertedValues = new ArrayList(1);
/* 102 */         convertedValues.add(new AnnotationValueImpl(this._env, value, valueType));
/*     */       }
/* 104 */       this._value = Collections.unmodifiableList(convertedValues);
/* 105 */       this._kind = -4;
/*     */     } else {
/* 107 */       this._value = convertToMirrorType(value, type, kind);
/* 108 */       this._kind = kind[0];
/*     */     }
/*     */   }
/*     */ 
/*     */   private Object convertToMirrorType(Object value, TypeBinding type, int[] kind)
/*     */   {
/* 125 */     if (type == null) {
/* 126 */       kind[0] = 11;
/* 127 */       return "<error>";
/* 128 */     }if (((type instanceof BaseTypeBinding)) || (type.id == 11)) {
/* 129 */       if (value == null) {
/* 130 */         if (((type instanceof BaseTypeBinding)) || 
/* 131 */           (type.id == 11))
/*     */         {
/* 133 */           kind[0] = 11;
/* 134 */           return "<error>";
/* 135 */         }if (type.isAnnotationType()) {
/* 136 */           kind[0] = -1;
/* 137 */           return this._env.getFactory().newAnnotationMirror(null);
/*     */         }
/* 139 */       } else if ((value instanceof Constant)) {
/* 140 */         if ((type instanceof BaseTypeBinding)) {
/* 141 */           kind[0] = ((BaseTypeBinding)type).id;
/*     */         }
/* 143 */         else if (type.id == 11) {
/* 144 */           kind[0] = ((Constant)value).typeID();
/*     */         }
/*     */         else {
/* 147 */           kind[0] = 11;
/* 148 */           return "<error>";
/*     */         }
/* 150 */         switch (kind[0]) {
/*     */         case 5:
/* 152 */           return Boolean.valueOf(((Constant)value).booleanValue());
/*     */         case 3:
/* 154 */           return Byte.valueOf(((Constant)value).byteValue());
/*     */         case 2:
/* 156 */           return Character.valueOf(((Constant)value).charValue());
/*     */         case 8:
/* 158 */           return Double.valueOf(((Constant)value).doubleValue());
/*     */         case 9:
/* 160 */           return Float.valueOf(((Constant)value).floatValue());
/*     */         case 10:
/*     */           try {
/* 163 */             if (((value instanceof LongConstant)) || 
/* 164 */               ((value instanceof DoubleConstant)) || 
/* 165 */               ((value instanceof FloatConstant)))
/*     */             {
/* 167 */               kind[0] = 11;
/* 168 */               return "<error>";
/*     */             }
/* 170 */             return Integer.valueOf(((Constant)value).intValue());
/*     */           } catch (ShouldNotImplement localShouldNotImplement) {
/* 172 */             kind[0] = 11;
/* 173 */             return "<error>";
/*     */           }
/*     */         case 11:
/* 176 */           return ((Constant)value).stringValue();
/*     */         case 7:
/* 178 */           return Long.valueOf(((Constant)value).longValue());
/*     */         case 4:
/* 180 */           return Short.valueOf(((Constant)value).shortValue());
/*     */         case 6:
/*     */         }
/*     */       }
/*     */     } else {
/* 183 */       if (type.isEnum()) {
/* 184 */         if ((value instanceof FieldBinding)) {
/* 185 */           kind[0] = -2;
/* 186 */           return (VariableElement)this._env.getFactory().newElement((FieldBinding)value);
/*     */         }
/* 188 */         kind[0] = 11;
/* 189 */         return "<error>";
/*     */       }
/* 191 */       if (type.isAnnotationType()) {
/* 192 */         if ((value instanceof AnnotationBinding)) {
/* 193 */           kind[0] = -1;
/* 194 */           return this._env.getFactory().newAnnotationMirror((AnnotationBinding)value);
/*     */         }
/* 196 */       } else if ((value instanceof TypeBinding)) {
/* 197 */         kind[0] = -3;
/* 198 */         return this._env.getFactory().newTypeMirror((TypeBinding)value);
/*     */       }
/*     */     }
/* 201 */     kind[0] = 11;
/* 202 */     return "<error>";
/*     */   }
/*     */ 
/*     */   public <R, P> R accept(AnnotationValueVisitor<R, P> v, P p)
/*     */   {
/* 208 */     switch (this._kind) {
/*     */     case 5:
/* 210 */       return v.visitBoolean(((Boolean)this._value).booleanValue(), p);
/*     */     case 3:
/* 212 */       return v.visitByte(((Byte)this._value).byteValue(), p);
/*     */     case 2:
/* 214 */       return v.visitChar(((Character)this._value).charValue(), p);
/*     */     case 8:
/* 216 */       return v.visitDouble(((Double)this._value).doubleValue(), p);
/*     */     case 9:
/* 218 */       return v.visitFloat(((Float)this._value).floatValue(), p);
/*     */     case 10:
/* 220 */       return v.visitInt(((Integer)this._value).intValue(), p);
/*     */     case 11:
/* 222 */       return v.visitString((String)this._value, p);
/*     */     case 7:
/* 224 */       return v.visitLong(((Long)this._value).longValue(), p);
/*     */     case 4:
/* 226 */       return v.visitShort(((Short)this._value).shortValue(), p);
/*     */     case -2:
/* 228 */       return v.visitEnumConstant((VariableElement)this._value, p);
/*     */     case -3:
/* 230 */       return v.visitType((TypeMirror)this._value, p);
/*     */     case -1:
/* 232 */       return v.visitAnnotation((AnnotationMirror)this._value, p);
/*     */     case -4:
/* 234 */       return v.visitArray((List)this._value, p);
/*     */     case 0:
/*     */     case 1:
/* 236 */     case 6: } return null;
/*     */   }
/*     */ 
/*     */   public Object getValue()
/*     */   {
/* 242 */     return this._value;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 247 */     if ((obj instanceof AnnotationValueImpl)) {
/* 248 */       return this._value.equals(((AnnotationValueImpl)obj)._value);
/*     */     }
/* 250 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 255 */     return this._value.hashCode() + this._kind;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 260 */     if (this._value == null) {
/* 261 */       return "null";
/*     */     }
/* 263 */     return this._value.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.model.AnnotationValueImpl
 * JD-Core Version:    0.6.0
 */