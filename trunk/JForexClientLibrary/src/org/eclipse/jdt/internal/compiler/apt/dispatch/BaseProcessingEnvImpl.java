/*     */ package org.eclipse.jdt.internal.compiler.apt.dispatch;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.annotation.processing.Filer;
/*     */ import javax.annotation.processing.Messager;
/*     */ import javax.annotation.processing.ProcessingEnvironment;
/*     */ import javax.lang.model.SourceVersion;
/*     */ import javax.lang.model.util.Elements;
/*     */ import javax.lang.model.util.Types;
/*     */ import org.eclipse.jdt.internal.compiler.Compiler;
/*     */ import org.eclipse.jdt.internal.compiler.apt.model.ElementsImpl;
/*     */ import org.eclipse.jdt.internal.compiler.apt.model.Factory;
/*     */ import org.eclipse.jdt.internal.compiler.apt.model.TypesImpl;
/*     */ import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ 
/*     */ public abstract class BaseProcessingEnvImpl
/*     */   implements ProcessingEnvironment
/*     */ {
/*     */   protected Filer _filer;
/*     */   protected Messager _messager;
/*     */   protected Map<String, String> _processorOptions;
/*     */   protected Compiler _compiler;
/*     */   protected Elements _elementUtils;
/*     */   protected Types _typeUtils;
/*     */   private List<ICompilationUnit> _addedUnits;
/*     */   private List<ReferenceBinding> _addedClassFiles;
/*     */   private List<ICompilationUnit> _deletedUnits;
/*     */   private boolean _errorRaised;
/*     */   private Factory _factory;
/*     */ 
/*     */   public BaseProcessingEnvImpl()
/*     */   {
/*  55 */     this._addedUnits = new ArrayList();
/*  56 */     this._addedClassFiles = new ArrayList();
/*  57 */     this._deletedUnits = new ArrayList();
/*  58 */     this._elementUtils = new ElementsImpl(this);
/*  59 */     this._typeUtils = new TypesImpl(this);
/*  60 */     this._factory = new Factory(this);
/*  61 */     this._errorRaised = false;
/*     */   }
/*     */ 
/*     */   public void addNewUnit(ICompilationUnit unit) {
/*  65 */     this._addedUnits.add(unit);
/*     */   }
/*     */ 
/*     */   public void addNewClassFile(ReferenceBinding binding) {
/*  69 */     this._addedClassFiles.add(binding);
/*     */   }
/*     */ 
/*     */   public Compiler getCompiler() {
/*  73 */     return this._compiler;
/*     */   }
/*     */ 
/*     */   public ICompilationUnit[] getDeletedUnits() {
/*  77 */     ICompilationUnit[] result = new ICompilationUnit[this._deletedUnits.size()];
/*  78 */     this._deletedUnits.toArray(result);
/*  79 */     return result;
/*     */   }
/*     */ 
/*     */   public ICompilationUnit[] getNewUnits() {
/*  83 */     ICompilationUnit[] result = new ICompilationUnit[this._addedUnits.size()];
/*  84 */     this._addedUnits.toArray(result);
/*  85 */     return result;
/*     */   }
/*     */ 
/*     */   public Elements getElementUtils()
/*     */   {
/*  90 */     return this._elementUtils;
/*     */   }
/*     */ 
/*     */   public Filer getFiler()
/*     */   {
/*  95 */     return this._filer;
/*     */   }
/*     */ 
/*     */   public Messager getMessager()
/*     */   {
/* 100 */     return this._messager;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getOptions()
/*     */   {
/* 105 */     return this._processorOptions;
/*     */   }
/*     */ 
/*     */   public Types getTypeUtils()
/*     */   {
/* 110 */     return this._typeUtils;
/*     */   }
/*     */ 
/*     */   public LookupEnvironment getLookupEnvironment() {
/* 114 */     return this._compiler.lookupEnvironment;
/*     */   }
/*     */ 
/*     */   public SourceVersion getSourceVersion()
/*     */   {
/* 123 */     return SourceVersion.RELEASE_6;
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/* 131 */     this._addedUnits.clear();
/* 132 */     this._addedClassFiles.clear();
/* 133 */     this._deletedUnits.clear();
/*     */   }
/*     */ 
/*     */   public boolean errorRaised()
/*     */   {
/* 142 */     return this._errorRaised;
/*     */   }
/*     */ 
/*     */   public void setErrorRaised(boolean b)
/*     */   {
/* 151 */     this._errorRaised = true;
/*     */   }
/*     */ 
/*     */   public Factory getFactory()
/*     */   {
/* 156 */     return this._factory;
/*     */   }
/*     */ 
/*     */   public ReferenceBinding[] getNewClassFiles() {
/* 160 */     ReferenceBinding[] result = new ReferenceBinding[this._addedClassFiles.size()];
/* 161 */     this._addedClassFiles.toArray(result);
/* 162 */     return result;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl
 * JD-Core Version:    0.6.0
 */