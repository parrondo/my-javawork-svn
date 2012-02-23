/*     */ package org.eclipse.jdt.internal.compiler.apt.model;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.lang.model.element.AnnotationMirror;
/*     */ import javax.lang.model.element.AnnotationValue;
/*     */ import javax.lang.model.element.Element;
/*     */ import javax.lang.model.element.ElementKind;
/*     */ import javax.lang.model.element.ExecutableElement;
/*     */ import javax.lang.model.element.Name;
/*     */ import javax.lang.model.element.PackageElement;
/*     */ import javax.lang.model.element.TypeElement;
/*     */ import javax.lang.model.util.Elements;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.CompilationResult;
/*     */ import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
/*     */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Javadoc;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
/*     */ import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodVerifier;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
/*     */ 
/*     */ public class ElementsImpl
/*     */   implements Elements
/*     */ {
/*  64 */   private static final Pattern INITIAL_DELIMITER = Pattern.compile("^\\s*/\\*+");
/*     */   private final BaseProcessingEnvImpl _env;
/*     */ 
/*     */   public ElementsImpl(BaseProcessingEnvImpl env)
/*     */   {
/*  73 */     this._env = env;
/*     */   }
/*     */ 
/*     */   public List<? extends AnnotationMirror> getAllAnnotationMirrors(Element e)
/*     */   {
/*  85 */     if ((e.getKind() == ElementKind.CLASS) && ((e instanceof TypeElementImpl))) {
/*  86 */       List annotations = new ArrayList();
/*     */ 
/*  88 */       Set annotationTypes = new HashSet();
/*  89 */       ReferenceBinding binding = (ReferenceBinding)((TypeElementImpl)e)._binding;
/*  90 */       while (binding != null) {
/*  91 */         for (AnnotationBinding annotation : binding.getAnnotations())
/*  92 */           if (annotation != null) {
/*  93 */             ReferenceBinding annotationType = annotation.getAnnotationType();
/*  94 */             if (!annotationTypes.contains(annotationType)) {
/*  95 */               annotationTypes.add(annotationType);
/*  96 */               annotations.add(annotation);
/*     */             }
/*     */           }
/*  99 */         binding = binding.superclass();
/*     */       }
/* 101 */       List list = new ArrayList(annotations.size());
/* 102 */       for (AnnotationBinding annotation : annotations) {
/* 103 */         list.add(this._env.getFactory().newAnnotationMirror(annotation));
/*     */       }
/* 105 */       return Collections.unmodifiableList(list);
/*     */     }
/*     */ 
/* 108 */     return e.getAnnotationMirrors();
/*     */   }
/*     */ 
/*     */   public List<? extends Element> getAllMembers(TypeElement type)
/*     */   {
/* 132 */     if ((type == null) || (!(type instanceof TypeElementImpl))) {
/* 133 */       return Collections.emptyList();
/*     */     }
/* 135 */     ReferenceBinding binding = (ReferenceBinding)((TypeElementImpl)type)._binding;
/*     */ 
/* 137 */     Map types = new HashMap();
/*     */ 
/* 139 */     List fields = new ArrayList();
/*     */ 
/* 141 */     Map methods = new HashMap();
/* 142 */     Set superinterfaces = new LinkedHashSet();
/* 143 */     boolean ignoreVisibility = true;
/* 144 */     while (binding != null) {
/* 145 */       addMembers(binding, ignoreVisibility, types, fields, methods);
/* 146 */       Set newfound = new LinkedHashSet();
/* 147 */       collectSuperInterfaces(binding, superinterfaces, newfound);
/* 148 */       for (ReferenceBinding superinterface : newfound) {
/* 149 */         addMembers(superinterface, false, types, fields, methods);
/*     */       }
/* 151 */       superinterfaces.addAll(newfound);
/* 152 */       binding = binding.superclass();
/* 153 */       ignoreVisibility = false;
/*     */     }
/* 155 */     List allMembers = new ArrayList();
/* 156 */     for (ReferenceBinding nestedType : types.values()) {
/* 157 */       allMembers.add(this._env.getFactory().newElement(nestedType));
/*     */     }
/* 159 */     for (FieldBinding field : fields) {
/* 160 */       allMembers.add(this._env.getFactory().newElement(field));
/*     */     }
/* 162 */     for (Set sameNamedMethods : methods.values()) {
/* 163 */       for (MethodBinding method : sameNamedMethods) {
/* 164 */         allMembers.add(this._env.getFactory().newElement(method));
/*     */       }
/*     */     }
/* 167 */     return allMembers;
/*     */   }
/*     */ 
/*     */   private void collectSuperInterfaces(ReferenceBinding type, Set<ReferenceBinding> existing, Set<ReferenceBinding> newfound)
/*     */   {
/* 180 */     for (ReferenceBinding superinterface : type.superInterfaces())
/* 181 */       if ((!existing.contains(superinterface)) && (!newfound.contains(superinterface))) {
/* 182 */         newfound.add(superinterface);
/* 183 */         collectSuperInterfaces(superinterface, existing, newfound);
/*     */       }
/*     */   }
/*     */ 
/*     */   private void addMembers(ReferenceBinding binding, boolean ignoreVisibility, Map<String, ReferenceBinding> types, List<FieldBinding> fields, Map<String, Set<MethodBinding>> methods)
/*     */   {
/* 202 */     for (ReferenceBinding subtype : binding.memberTypes()) {
/* 203 */       if ((ignoreVisibility) || (!subtype.isPrivate())) {
/* 204 */         String name = new String(subtype.sourceName());
/* 205 */         if (types.get(name) == null) {
/* 206 */           types.put(name, subtype);
/*     */         }
/*     */       }
/*     */     }
/* 210 */     for (FieldBinding field : binding.fields()) {
/* 211 */       if ((ignoreVisibility) || (!field.isPrivate())) {
/* 212 */         fields.add(field);
/*     */       }
/*     */     }
/* 215 */     for (MethodBinding method : binding.methods())
/* 216 */       if ((!method.isSynthetic()) && ((ignoreVisibility) || ((!method.isPrivate()) && (!method.isConstructor())))) {
/* 217 */         String methodName = new String(method.selector);
/* 218 */         Set sameNamedMethods = (Set)methods.get(methodName);
/* 219 */         if (sameNamedMethods == null)
/*     */         {
/* 222 */           sameNamedMethods = new HashSet(4);
/* 223 */           methods.put(methodName, sameNamedMethods);
/* 224 */           sameNamedMethods.add(method);
/*     */         }
/*     */         else
/*     */         {
/* 228 */           boolean unique = true;
/* 229 */           if (!ignoreVisibility) {
/* 230 */             for (MethodBinding existing : sameNamedMethods) {
/* 231 */               MethodVerifier verifier = this._env.getLookupEnvironment().methodVerifier();
/* 232 */               if (verifier.doesMethodOverride(existing, method)) {
/* 233 */                 unique = false;
/* 234 */                 break;
/*     */               }
/*     */             }
/*     */           }
/* 238 */           if (unique)
/* 239 */             sameNamedMethods.add(method);
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   public Name getBinaryName(TypeElement type)
/*     */   {
/* 251 */     TypeElementImpl typeElementImpl = (TypeElementImpl)type;
/* 252 */     ReferenceBinding referenceBinding = (ReferenceBinding)typeElementImpl._binding;
/* 253 */     return new NameImpl(
/* 254 */       CharOperation.replaceOnCopy(referenceBinding.constantPoolName(), '/', '.'));
/*     */   }
/*     */ 
/*     */   public String getConstantExpression(Object value)
/*     */   {
/* 262 */     if ((!(value instanceof Integer)) && 
/* 263 */       (!(value instanceof Byte)) && 
/* 264 */       (!(value instanceof Float)) && 
/* 265 */       (!(value instanceof Double)) && 
/* 266 */       (!(value instanceof Long)) && 
/* 267 */       (!(value instanceof Short)) && 
/* 268 */       (!(value instanceof Character)) && 
/* 269 */       (!(value instanceof String)) && 
/* 270 */       (!(value instanceof Boolean))) {
/* 271 */       throw new IllegalArgumentException("Not a valid wrapper type : " + value.getClass());
/*     */     }
/* 273 */     if ((value instanceof Character)) {
/* 274 */       StringBuilder builder = new StringBuilder();
/* 275 */       builder.append('\'').append(value).append('\'');
/* 276 */       return String.valueOf(builder);
/*     */     }
/* 278 */     return String.valueOf(value);
/*     */   }
/*     */ 
/*     */   public String getDocComment(Element e)
/*     */   {
/* 286 */     char[] unparsed = getUnparsedDocComment(e);
/* 287 */     return formatJavadoc(unparsed);
/*     */   }
/*     */ 
/*     */   private char[] getUnparsedDocComment(Element e)
/*     */   {
/* 297 */     Javadoc javadoc = null;
/* 298 */     ReferenceContext referenceContext = null;
/* 299 */     switch ($SWITCH_TABLE$javax$lang$model$element$ElementKind()[e.getKind().ordinal()]) {
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/* 304 */       TypeElementImpl typeElementImpl = (TypeElementImpl)e;
/* 305 */       ReferenceBinding referenceBinding = (ReferenceBinding)typeElementImpl._binding;
/* 306 */       if (!(referenceBinding instanceof SourceTypeBinding)) break;
/* 307 */       SourceTypeBinding sourceTypeBinding = (SourceTypeBinding)referenceBinding;
/* 308 */       referenceContext = sourceTypeBinding.scope.referenceContext;
/* 309 */       javadoc = ((TypeDeclaration)referenceContext).javadoc;
/*     */ 
/* 311 */       break;
/*     */     case 1:
/* 314 */       PackageElementImpl packageElementImpl = (PackageElementImpl)e;
/* 315 */       PackageBinding packageBinding = (PackageBinding)packageElementImpl._binding;
/* 316 */       char[][] compoundName = CharOperation.arrayConcat(packageBinding.compoundName, TypeConstants.PACKAGE_INFO_NAME);
/* 317 */       ReferenceBinding type = this._env.getLookupEnvironment().getType(compoundName);
/* 318 */       if ((type == null) || (!type.isValidBinding()) || (!(type instanceof SourceTypeBinding))) break;
/* 319 */       SourceTypeBinding sourceTypeBinding = (SourceTypeBinding)type;
/* 320 */       referenceContext = sourceTypeBinding.scope.referenceContext;
/* 321 */       javadoc = ((TypeDeclaration)referenceContext).javadoc;
/*     */ 
/* 323 */       break;
/*     */     case 11:
/*     */     case 12:
/* 326 */       ExecutableElementImpl executableElementImpl = (ExecutableElementImpl)e;
/* 327 */       MethodBinding methodBinding = (MethodBinding)executableElementImpl._binding;
/* 328 */       AbstractMethodDeclaration sourceMethod = methodBinding.sourceMethod();
/* 329 */       if (sourceMethod == null) break;
/* 330 */       javadoc = sourceMethod.javadoc;
/* 331 */       referenceContext = sourceMethod;
/*     */ 
/* 333 */       break;
/*     */     case 6:
/*     */     case 7:
/* 336 */       VariableElementImpl variableElementImpl = (VariableElementImpl)e;
/* 337 */       FieldBinding fieldBinding = (FieldBinding)variableElementImpl._binding;
/* 338 */       FieldDeclaration sourceField = fieldBinding.sourceField();
/* 339 */       if (sourceField == null) break;
/* 340 */       javadoc = sourceField.javadoc;
/* 341 */       if (!(fieldBinding.declaringClass instanceof SourceTypeBinding)) break;
/* 342 */       SourceTypeBinding sourceTypeBinding = (SourceTypeBinding)fieldBinding.declaringClass;
/* 343 */       referenceContext = sourceTypeBinding.scope.referenceContext;
/*     */     case 8:
/*     */     case 9:
/*     */     case 10:
/* 347 */     }if ((javadoc != null) && (referenceContext != null)) {
/* 348 */       char[] contents = referenceContext.compilationResult().getCompilationUnit().getContents();
/* 349 */       if (contents != null) {
/* 350 */         return CharOperation.subarray(contents, javadoc.sourceStart, javadoc.sourceEnd - 1);
/*     */       }
/*     */     }
/* 353 */     return null;
/*     */   }
/*     */ 
/*     */   private static String formatJavadoc(char[] unparsed)
/*     */   {
/* 365 */     if ((unparsed == null) || (unparsed.length < 5)) {
/* 366 */       return null;
/*     */     }
/*     */ 
/* 369 */     String[] lines = new String(unparsed).split("\n");
/* 370 */     Matcher delimiterMatcher = INITIAL_DELIMITER.matcher(lines[0]);
/* 371 */     if (!delimiterMatcher.find()) {
/* 372 */       return null;
/*     */     }
/* 374 */     int iOpener = delimiterMatcher.end();
/* 375 */     lines[0] = lines[0].substring(iOpener);
/* 376 */     if (lines.length == 1)
/*     */     {
/* 379 */       StringBuilder sb = new StringBuilder();
/* 380 */       char[] chars = lines[0].toCharArray();
/* 381 */       boolean startingWhitespaces = true;
/* 382 */       for (char c : chars) {
/* 383 */         if (Character.isWhitespace(c)) {
/* 384 */           if (startingWhitespaces) {
/*     */             continue;
/*     */           }
/* 387 */           sb.append(c);
/*     */         } else {
/* 389 */           startingWhitespaces = false;
/* 390 */           sb.append(c);
/*     */         }
/*     */       }
/* 393 */       return sb.toString();
/*     */     }
/*     */ 
/* 397 */     int firstLine = lines[0].trim().length() > 0 ? 0 : 1;
/*     */ 
/* 400 */     int lastLine = lines[(lines.length - 1)].trim().length() > 0 ? lines.length - 1 : lines.length - 2;
/*     */ 
/* 402 */     StringBuilder sb = new StringBuilder();
/* 403 */     if ((lines[0].length() != 0) && (firstLine == 1))
/*     */     {
/* 405 */       sb.append('\n');
/*     */     }
/* 407 */     boolean preserveLineSeparator = lines[0].length() == 0;
/* 408 */     for (int line = firstLine; line <= lastLine; line++) {
/* 409 */       char[] chars = lines[line].toCharArray();
/* 410 */       int starsIndex = getStars(chars);
/* 411 */       int leadingWhitespaces = 0;
/* 412 */       boolean recordLeadingWhitespaces = true;
/* 413 */       int i = 0; for (int max = chars.length; i < max; i++) {
/* 414 */         char c = chars[i];
/* 415 */         switch (c) {
/*     */         case '\t':
/* 417 */           if (starsIndex == -1) {
/* 418 */             if (recordLeadingWhitespaces)
/* 419 */               leadingWhitespaces += 8;
/*     */             else
/* 421 */               sb.append(c);
/*     */           } else {
/* 423 */             if (i < starsIndex) continue;
/* 424 */             sb.append(c);
/*     */           }
/* 426 */           break;
/*     */         case ' ':
/* 428 */           if (starsIndex == -1) {
/* 429 */             if (recordLeadingWhitespaces)
/* 430 */               leadingWhitespaces++;
/*     */             else
/* 432 */               sb.append(c);
/*     */           } else {
/* 434 */             if (i < starsIndex) continue;
/* 435 */             sb.append(c);
/*     */           }
/* 437 */           break;
/*     */         default:
/* 440 */           recordLeadingWhitespaces = false;
/* 441 */           if (leadingWhitespaces != 0) {
/* 442 */             int numberOfTabs = leadingWhitespaces / 8;
/* 443 */             if (numberOfTabs != 0) {
/* 444 */               int j = 0; for (int max2 = numberOfTabs; j < max2; j++) {
/* 445 */                 sb.append("        ");
/*     */               }
/* 447 */               if (leadingWhitespaces % 8 >= 1)
/* 448 */                 sb.append(' ');
/*     */             }
/* 450 */             else if (line != 0)
/*     */             {
/* 452 */               int j = 0; for (int max2 = leadingWhitespaces; j < max2; j++) {
/* 453 */                 sb.append(' ');
/*     */               }
/*     */             }
/* 456 */             leadingWhitespaces = 0;
/* 457 */             sb.append(c); } else {
/* 458 */             if ((c == '*') && (i <= starsIndex)) continue;
/* 459 */             sb.append(c);
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 465 */       int end = lines.length - 1;
/* 466 */       if (line < end)
/* 467 */         sb.append('\n');
/* 468 */       else if ((preserveLineSeparator) && (line == end)) {
/* 469 */         sb.append('\n');
/*     */       }
/*     */     }
/* 472 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   private static int getStars(char[] line)
/*     */   {
/* 482 */     int i = 0; for (int max = line.length; i < max; i++) {
/* 483 */       char c = line[i];
/* 484 */       if (!Character.isWhitespace(c)) {
/* 485 */         if (c != '*') {
/*     */           break;
/*     */         }
/* 488 */         for (int j = i + 1; j < max; j++) {
/* 489 */           if (line[j] != '*') {
/* 490 */             return j;
/*     */           }
/*     */         }
/* 493 */         return max - 1;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 499 */     return -1;
/*     */   }
/*     */ 
/*     */   public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults(AnnotationMirror a)
/*     */   {
/* 511 */     return ((AnnotationMirrorImpl)a).getElementValuesWithDefaults();
/*     */   }
/*     */ 
/*     */   public Name getName(CharSequence cs)
/*     */   {
/* 519 */     return new NameImpl(cs);
/*     */   }
/*     */ 
/*     */   public PackageElement getPackageElement(CharSequence name)
/*     */   {
/* 524 */     LookupEnvironment le = this._env.getLookupEnvironment();
/* 525 */     if (name.length() == 0) {
/* 526 */       return new PackageElementImpl(this._env, le.defaultPackage);
/*     */     }
/* 528 */     char[] packageName = name.toString().toCharArray();
/* 529 */     PackageBinding packageBinding = le.createPackage(CharOperation.splitOn('.', packageName));
/* 530 */     if (packageBinding == null) {
/* 531 */       return null;
/*     */     }
/* 533 */     return new PackageElementImpl(this._env, packageBinding);
/*     */   }
/*     */ 
/*     */   public PackageElement getPackageOf(Element type)
/*     */   {
/* 538 */     switch ($SWITCH_TABLE$javax$lang$model$element$ElementKind()[type.getKind().ordinal()]) {
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/* 543 */       TypeElementImpl typeElementImpl = (TypeElementImpl)type;
/* 544 */       ReferenceBinding referenceBinding = (ReferenceBinding)typeElementImpl._binding;
/* 545 */       return (PackageElement)this._env.getFactory().newElement(referenceBinding.fPackage);
/*     */     case 1:
/* 547 */       return (PackageElement)type;
/*     */     case 11:
/*     */     case 12:
/* 550 */       ExecutableElementImpl executableElementImpl = (ExecutableElementImpl)type;
/* 551 */       MethodBinding methodBinding = (MethodBinding)executableElementImpl._binding;
/* 552 */       return (PackageElement)this._env.getFactory().newElement(methodBinding.declaringClass.fPackage);
/*     */     case 6:
/*     */     case 7:
/* 555 */       VariableElementImpl variableElementImpl = (VariableElementImpl)type;
/* 556 */       FieldBinding fieldBinding = (FieldBinding)variableElementImpl._binding;
/* 557 */       return (PackageElement)this._env.getFactory().newElement(fieldBinding.declaringClass.fPackage);
/*     */     case 8:
/* 559 */       VariableElementImpl variableElementImpl = (VariableElementImpl)type;
/* 560 */       LocalVariableBinding localVariableBinding = (LocalVariableBinding)variableElementImpl._binding;
/* 561 */       return (PackageElement)this._env.getFactory().newElement(localVariableBinding.declaringScope.classScope().referenceContext.binding.fPackage);
/*     */     case 9:
/*     */     case 10:
/*     */     case 13:
/*     */     case 14:
/*     */     case 15:
/*     */     case 16:
/* 568 */       return null;
/*     */     }
/*     */ 
/* 571 */     return null;
/*     */   }
/*     */ 
/*     */   public TypeElement getTypeElement(CharSequence name)
/*     */   {
/* 579 */     LookupEnvironment le = this._env.getLookupEnvironment();
/* 580 */     char[][] compoundName = CharOperation.splitOn('.', name.toString().toCharArray());
/* 581 */     ReferenceBinding binding = le.getType(compoundName);
/*     */ 
/* 584 */     if (binding == null) {
/* 585 */       ReferenceBinding topLevelBinding = null;
/* 586 */       int topLevelSegments = compoundName.length;
/*     */       do {
/* 588 */         char[][] topLevelName = new char[topLevelSegments][];
/* 589 */         for (int i = 0; i < topLevelSegments; i++) {
/* 590 */           topLevelName[i] = compoundName[i];
/*     */         }
/* 592 */         topLevelBinding = le.getType(topLevelName);
/* 593 */         if (topLevelBinding != null)
/*     */           break;
/* 587 */         topLevelSegments--; } while (topLevelSegments > 0);
/*     */ 
/* 597 */       if (topLevelBinding == null) {
/* 598 */         return null;
/*     */       }
/* 600 */       binding = topLevelBinding;
/* 601 */       for (int i = topLevelSegments; (binding != null) && (i < compoundName.length); i++) {
/* 602 */         binding = binding.getMemberType(compoundName[i]);
/*     */       }
/*     */     }
/* 605 */     if (binding == null) {
/* 606 */       return null;
/*     */     }
/* 608 */     return new TypeElementImpl(this._env, binding, null);
/*     */   }
/*     */ 
/*     */   public boolean hides(Element hider, Element hidden)
/*     */   {
/* 619 */     if (hidden == null)
/*     */     {
/* 621 */       throw new NullPointerException();
/*     */     }
/* 623 */     return ((ElementImpl)hider).hides(hidden);
/*     */   }
/*     */ 
/*     */   public boolean isDeprecated(Element e)
/*     */   {
/* 631 */     if (!(e instanceof ElementImpl)) {
/* 632 */       return false;
/*     */     }
/* 634 */     return (((ElementImpl)e)._binding.getAnnotationTagBits() & 0x0) != 0L;
/*     */   }
/*     */ 
/*     */   public boolean overrides(ExecutableElement overrider, ExecutableElement overridden, TypeElement type)
/*     */   {
/* 644 */     if ((overridden == null) || (type == null)) {
/* 645 */       throw new NullPointerException();
/*     */     }
/* 647 */     return ((ExecutableElementImpl)overrider).overrides(overridden, type);
/*     */   }
/*     */ 
/*     */   public void printElements(Writer w, Element[] elements)
/*     */   {
/* 655 */     String lineSeparator = System.getProperty("line.separator");
/* 656 */     for (Element element : elements)
/*     */       try {
/* 658 */         w.write(element.toString());
/* 659 */         w.write(lineSeparator);
/*     */       }
/*     */       catch (IOException localIOException1)
/*     */       {
/*     */       }
/*     */     try {
/* 665 */       w.flush();
/*     */     }
/*     */     catch (IOException localIOException2)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.model.ElementsImpl
 * JD-Core Version:    0.6.0
 */