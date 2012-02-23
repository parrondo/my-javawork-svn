package org.eclipse.jdt.internal.compiler;

import java.io.PrintWriter;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;

public abstract class AbstractAnnotationProcessorManager
{
  public abstract void configure(Object paramObject, String[] paramArrayOfString);

  public abstract void configureFromPlatform(Compiler paramCompiler, Object paramObject1, Object paramObject2);

  public abstract void setOut(PrintWriter paramPrintWriter);

  public abstract void setErr(PrintWriter paramPrintWriter);

  public abstract ICompilationUnit[] getNewUnits();

  public abstract ReferenceBinding[] getNewClassFiles();

  public abstract ICompilationUnit[] getDeletedUnits();

  public abstract void reset();

  public abstract void processAnnotations(CompilationUnitDeclaration[] paramArrayOfCompilationUnitDeclaration, ReferenceBinding[] paramArrayOfReferenceBinding, boolean paramBoolean);

  public abstract void setProcessors(Object[] paramArrayOfObject);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.AbstractAnnotationProcessorManager
 * JD-Core Version:    0.6.0
 */