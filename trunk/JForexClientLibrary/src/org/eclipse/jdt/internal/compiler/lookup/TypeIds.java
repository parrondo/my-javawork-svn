package org.eclipse.jdt.internal.compiler.lookup;

public abstract interface TypeIds
{
  public static final int T_undefined = 0;
  public static final int T_JavaLangObject = 1;
  public static final int T_char = 2;
  public static final int T_byte = 3;
  public static final int T_short = 4;
  public static final int T_boolean = 5;
  public static final int T_void = 6;
  public static final int T_long = 7;
  public static final int T_double = 8;
  public static final int T_float = 9;
  public static final int T_int = 10;
  public static final int T_JavaLangString = 11;
  public static final int T_null = 12;
  public static final int T_JavaLangClass = 16;
  public static final int T_JavaLangStringBuffer = 17;
  public static final int T_JavaLangSystem = 18;
  public static final int T_JavaLangError = 19;
  public static final int T_JavaLangReflectConstructor = 20;
  public static final int T_JavaLangThrowable = 21;
  public static final int T_JavaLangNoClassDefError = 22;
  public static final int T_JavaLangClassNotFoundException = 23;
  public static final int T_JavaLangRuntimeException = 24;
  public static final int T_JavaLangException = 25;
  public static final int T_JavaLangByte = 26;
  public static final int T_JavaLangShort = 27;
  public static final int T_JavaLangCharacter = 28;
  public static final int T_JavaLangInteger = 29;
  public static final int T_JavaLangLong = 30;
  public static final int T_JavaLangFloat = 31;
  public static final int T_JavaLangDouble = 32;
  public static final int T_JavaLangBoolean = 33;
  public static final int T_JavaLangVoid = 34;
  public static final int T_JavaLangAssertionError = 35;
  public static final int T_JavaLangCloneable = 36;
  public static final int T_JavaIoSerializable = 37;
  public static final int T_JavaLangIterable = 38;
  public static final int T_JavaUtilIterator = 39;
  public static final int T_JavaLangStringBuilder = 40;
  public static final int T_JavaLangEnum = 41;
  public static final int T_JavaLangIllegalArgumentException = 42;
  public static final int T_JavaLangAnnotationAnnotation = 43;
  public static final int T_JavaLangDeprecated = 44;
  public static final int T_JavaLangAnnotationDocumented = 45;
  public static final int T_JavaLangAnnotationInherited = 46;
  public static final int T_JavaLangOverride = 47;
  public static final int T_JavaLangAnnotationRetention = 48;
  public static final int T_JavaLangSuppressWarnings = 49;
  public static final int T_JavaLangAnnotationTarget = 50;
  public static final int T_JavaLangAnnotationRetentionPolicy = 51;
  public static final int T_JavaLangAnnotationElementType = 52;
  public static final int T_JavaIoPrintStream = 53;
  public static final int T_JavaLangReflectField = 54;
  public static final int T_JavaLangReflectMethod = 55;
  public static final int T_JavaIoExternalizable = 56;
  public static final int T_JavaIoObjectStreamException = 57;
  public static final int T_JavaIoException = 58;
  public static final int T_JavaUtilCollection = 59;
  public static final int NoId = 2147483647;
  public static final int IMPLICIT_CONVERSION_MASK = 255;
  public static final int COMPILE_TYPE_MASK = 15;
  public static final int Boolean2Int = 165;
  public static final int Boolean2String = 181;
  public static final int Boolean2Boolean = 85;
  public static final int Byte2Byte = 51;
  public static final int Byte2Short = 67;
  public static final int Byte2Char = 35;
  public static final int Byte2Int = 163;
  public static final int Byte2Long = 115;
  public static final int Byte2Float = 147;
  public static final int Byte2Double = 131;
  public static final int Byte2String = 179;
  public static final int Short2Byte = 52;
  public static final int Short2Short = 68;
  public static final int Short2Char = 36;
  public static final int Short2Int = 164;
  public static final int Short2Long = 116;
  public static final int Short2Float = 148;
  public static final int Short2Double = 132;
  public static final int Short2String = 180;
  public static final int Char2Byte = 50;
  public static final int Char2Short = 66;
  public static final int Char2Char = 34;
  public static final int Char2Int = 162;
  public static final int Char2Long = 114;
  public static final int Char2Float = 146;
  public static final int Char2Double = 130;
  public static final int Char2String = 178;
  public static final int Int2Byte = 58;
  public static final int Int2Short = 74;
  public static final int Int2Char = 42;
  public static final int Int2Int = 170;
  public static final int Int2Long = 122;
  public static final int Int2Float = 154;
  public static final int Int2Double = 138;
  public static final int Int2String = 186;
  public static final int Long2Byte = 55;
  public static final int Long2Short = 71;
  public static final int Long2Char = 39;
  public static final int Long2Int = 167;
  public static final int Long2Long = 119;
  public static final int Long2Float = 151;
  public static final int Long2Double = 135;
  public static final int Long2String = 183;
  public static final int Float2Byte = 57;
  public static final int Float2Short = 73;
  public static final int Float2Char = 41;
  public static final int Float2Int = 169;
  public static final int Float2Long = 121;
  public static final int Float2Float = 153;
  public static final int Float2Double = 137;
  public static final int Float2String = 185;
  public static final int Double2Byte = 56;
  public static final int Double2Short = 72;
  public static final int Double2Char = 40;
  public static final int Double2Int = 168;
  public static final int Double2Long = 120;
  public static final int Double2Float = 152;
  public static final int Double2Double = 136;
  public static final int Double2String = 184;
  public static final int String2String = 187;
  public static final int Object2String = 177;
  public static final int Null2Null = 204;
  public static final int Null2String = 188;
  public static final int Object2Object = 17;
  public static final int BOXING = 512;
  public static final int UNBOXING = 1024;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.TypeIds
 * JD-Core Version:    0.6.0
 */