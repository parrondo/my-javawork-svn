package org.eclipse.jdt.internal.compiler.lookup;

public abstract interface ProblemReasons
{
  public static final int NoError = 0;
  public static final int NotFound = 1;
  public static final int NotVisible = 2;
  public static final int Ambiguous = 3;
  public static final int InternalNameProvided = 4;
  public static final int InheritedNameHidesEnclosingName = 5;
  public static final int NonStaticReferenceInConstructorInvocation = 6;
  public static final int NonStaticReferenceInStaticContext = 7;
  public static final int ReceiverTypeNotVisible = 8;
  public static final int IllegalSuperTypeVariable = 9;
  public static final int ParameterBoundMismatch = 10;
  public static final int TypeParameterArityMismatch = 11;
  public static final int ParameterizedMethodTypeMismatch = 12;
  public static final int TypeArgumentsForRawGenericMethod = 13;
  public static final int InvalidTypeForStaticImport = 14;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.ProblemReasons
 * JD-Core Version:    0.6.0
 */