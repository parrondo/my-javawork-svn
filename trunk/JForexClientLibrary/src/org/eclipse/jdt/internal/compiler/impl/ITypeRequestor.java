package org.eclipse.jdt.internal.compiler.impl;

import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.ISourceType;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;

public abstract interface ITypeRequestor
{
  public abstract void accept(IBinaryType paramIBinaryType, PackageBinding paramPackageBinding, AccessRestriction paramAccessRestriction);

  public abstract void accept(ICompilationUnit paramICompilationUnit, AccessRestriction paramAccessRestriction);

  public abstract void accept(ISourceType[] paramArrayOfISourceType, PackageBinding paramPackageBinding, AccessRestriction paramAccessRestriction);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.impl.ITypeRequestor
 * JD-Core Version:    0.6.0
 */