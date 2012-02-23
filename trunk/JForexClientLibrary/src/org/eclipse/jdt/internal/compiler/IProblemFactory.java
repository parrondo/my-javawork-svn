package org.eclipse.jdt.internal.compiler;

import java.util.Locale;
import org.eclipse.jdt.core.compiler.CategorizedProblem;

public abstract interface IProblemFactory
{
  public abstract CategorizedProblem createProblem(char[] paramArrayOfChar, int paramInt1, String[] paramArrayOfString1, String[] paramArrayOfString2, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);

  public abstract CategorizedProblem createProblem(char[] paramArrayOfChar, int paramInt1, String[] paramArrayOfString1, int paramInt2, String[] paramArrayOfString2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);

  public abstract Locale getLocale();

  public abstract String getLocalizedMessage(int paramInt, String[] paramArrayOfString);

  public abstract String getLocalizedMessage(int paramInt1, int paramInt2, String[] paramArrayOfString);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.IProblemFactory
 * JD-Core Version:    0.6.0
 */