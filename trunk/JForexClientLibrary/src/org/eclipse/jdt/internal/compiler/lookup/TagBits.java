package org.eclipse.jdt.internal.compiler.lookup;

public abstract interface TagBits
{
  public static final long IsArrayType = 1L;
  public static final long IsBaseType = 2L;
  public static final long IsNestedType = 4L;
  public static final long IsMemberType = 8L;
  public static final long ContainsNestedTypeReferences = 2048L;
  public static final long MemberTypeMask = 2060L;
  public static final long IsLocalType = 16L;
  public static final long LocalTypeMask = 2068L;
  public static final long IsAnonymousType = 32L;
  public static final long AnonymousTypeMask = 2100L;
  public static final long IsBinaryBinding = 64L;
  public static final long HasMissingType = 128L;
  public static final long HasUncheckedTypeArgumentForBoundCheck = 256L;
  public static final long BeginHierarchyCheck = 256L;
  public static final long EndHierarchyCheck = 512L;
  public static final long HasParameterAnnotations = 1024L;
  public static final long KnowsDefaultAbstractMethods = 1024L;
  public static final long IsArgument = 1024L;
  public static final long ClearPrivateModifier = 1024L;
  public static final long AreFieldsSorted = 4096L;
  public static final long AreFieldsComplete = 8192L;
  public static final long AreMethodsSorted = 16384L;
  public static final long AreMethodsComplete = 32768L;
  public static final long HasNoMemberTypes = 65536L;
  public static final long HierarchyHasProblems = 131072L;
  public static final long TypeVariablesAreConnected = 262144L;
  public static final long PassedBoundCheck = 4194304L;
  public static final long IsBoundParameterizedType = 8388608L;
  public static final long HasUnresolvedTypeVariables = 16777216L;
  public static final long HasUnresolvedSuperclass = 33554432L;
  public static final long HasUnresolvedSuperinterfaces = 67108864L;
  public static final long HasUnresolvedEnclosingType = 134217728L;
  public static final long HasUnresolvedMemberTypes = 268435456L;
  public static final long HasTypeVariable = 536870912L;
  public static final long HasDirectWildcard = 1073741824L;
  public static final long BeginAnnotationCheck = 2147483648L;
  public static final long EndAnnotationCheck = 4294967296L;
  public static final long AnnotationResolved = 8589934592L;
  public static final long DeprecatedAnnotationResolved = 17179869184L;
  public static final long AnnotationTarget = 34359738368L;
  public static final long AnnotationForType = 68719476736L;
  public static final long AnnotationForField = 137438953472L;
  public static final long AnnotationForMethod = 274877906944L;
  public static final long AnnotationForParameter = 549755813888L;
  public static final long AnnotationForConstructor = 1099511627776L;
  public static final long AnnotationForLocalVariable = 2199023255552L;
  public static final long AnnotationForAnnotationType = 4398046511104L;
  public static final long AnnotationForPackage = 8796093022208L;
  public static final long AnnotationTargetMASK = 17557826306048L;
  public static final long AnnotationSourceRetention = 17592186044416L;
  public static final long AnnotationClassRetention = 35184372088832L;
  public static final long AnnotationRuntimeRetention = 52776558133248L;
  public static final long AnnotationRetentionMASK = 52776558133248L;
  public static final long AnnotationDeprecated = 70368744177664L;
  public static final long AnnotationDocumented = 140737488355328L;
  public static final long AnnotationInherited = 281474976710656L;
  public static final long AnnotationOverride = 562949953421312L;
  public static final long AnnotationSuppressWarnings = 1125899906842624L;
  public static final long AllStandardAnnotationsMask = 2251765453946880L;
  public static final long DefaultValueResolved = 2251799813685248L;
  public static final long HasNonPrivateConstructor = 4503599627370496L;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.TagBits
 * JD-Core Version:    0.6.0
 */