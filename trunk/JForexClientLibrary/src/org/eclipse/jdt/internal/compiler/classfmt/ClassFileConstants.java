package org.eclipse.jdt.internal.compiler.classfmt;

public abstract interface ClassFileConstants
{
  public static final int AccDefault = 0;
  public static final int AccPublic = 1;
  public static final int AccPrivate = 2;
  public static final int AccProtected = 4;
  public static final int AccStatic = 8;
  public static final int AccFinal = 16;
  public static final int AccSynchronized = 32;
  public static final int AccVolatile = 64;
  public static final int AccBridge = 64;
  public static final int AccTransient = 128;
  public static final int AccVarargs = 128;
  public static final int AccNative = 256;
  public static final int AccInterface = 512;
  public static final int AccAbstract = 1024;
  public static final int AccStrictfp = 2048;
  public static final int AccSynthetic = 4096;
  public static final int AccAnnotation = 8192;
  public static final int AccEnum = 16384;
  public static final int AccSuper = 32;
  public static final int AccAnnotationDefault = 131072;
  public static final int AccDeprecated = 1048576;
  public static final int Utf8Tag = 1;
  public static final int IntegerTag = 3;
  public static final int FloatTag = 4;
  public static final int LongTag = 5;
  public static final int DoubleTag = 6;
  public static final int ClassTag = 7;
  public static final int StringTag = 8;
  public static final int FieldRefTag = 9;
  public static final int MethodRefTag = 10;
  public static final int InterfaceMethodRefTag = 11;
  public static final int NameAndTypeTag = 12;
  public static final int ConstantMethodRefFixedSize = 5;
  public static final int ConstantClassFixedSize = 3;
  public static final int ConstantDoubleFixedSize = 9;
  public static final int ConstantFieldRefFixedSize = 5;
  public static final int ConstantFloatFixedSize = 5;
  public static final int ConstantIntegerFixedSize = 5;
  public static final int ConstantInterfaceMethodRefFixedSize = 5;
  public static final int ConstantLongFixedSize = 9;
  public static final int ConstantStringFixedSize = 3;
  public static final int ConstantUtf8FixedSize = 3;
  public static final int ConstantNameAndTypeFixedSize = 5;
  public static final int MAJOR_VERSION_1_1 = 45;
  public static final int MAJOR_VERSION_1_2 = 46;
  public static final int MAJOR_VERSION_1_3 = 47;
  public static final int MAJOR_VERSION_1_4 = 48;
  public static final int MAJOR_VERSION_1_5 = 49;
  public static final int MAJOR_VERSION_1_6 = 50;
  public static final int MAJOR_VERSION_1_7 = 51;
  public static final int MINOR_VERSION_0 = 0;
  public static final int MINOR_VERSION_1 = 1;
  public static final int MINOR_VERSION_2 = 2;
  public static final int MINOR_VERSION_3 = 3;
  public static final int MINOR_VERSION_4 = 4;
  public static final long JDK1_1 = 2949123L;
  public static final long JDK1_2 = 3014656L;
  public static final long JDK1_3 = 3080192L;
  public static final long JDK1_4 = 3145728L;
  public static final long JDK1_5 = 3211264L;
  public static final long JDK1_6 = 3276800L;
  public static final long JDK1_7 = 3342336L;
  public static final long CLDC_1_1 = 2949124L;
  public static final long JDK_DEFERRED = 9223372036854775807L;
  public static final int INT_ARRAY = 10;
  public static final int BYTE_ARRAY = 8;
  public static final int BOOLEAN_ARRAY = 4;
  public static final int SHORT_ARRAY = 9;
  public static final int CHAR_ARRAY = 5;
  public static final int LONG_ARRAY = 11;
  public static final int FLOAT_ARRAY = 6;
  public static final int DOUBLE_ARRAY = 7;
  public static final int ATTR_SOURCE = 1;
  public static final int ATTR_LINES = 2;
  public static final int ATTR_VARS = 4;
  public static final int ATTR_STACK_MAP_TABLE = 8;
  public static final int ATTR_STACK_MAP = 16;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants
 * JD-Core Version:    0.6.0
 */