/*      */ package org.eclipse.jdt.internal.compiler.codegen;
/*      */ 
/*      */ import org.eclipse.jdt.core.compiler.CharOperation;
/*      */ import org.eclipse.jdt.internal.compiler.ClassFile;
/*      */ import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
/*      */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*      */ import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;
/*      */ import org.eclipse.jdt.internal.compiler.util.Util;
/*      */ 
/*      */ public class ConstantPool
/*      */   implements ClassFileConstants, TypeIds
/*      */ {
/*      */   public static final int DOUBLE_INITIAL_SIZE = 5;
/*      */   public static final int FLOAT_INITIAL_SIZE = 3;
/*      */   public static final int INT_INITIAL_SIZE = 248;
/*      */   public static final int LONG_INITIAL_SIZE = 5;
/*      */   public static final int UTF8_INITIAL_SIZE = 778;
/*      */   public static final int STRING_INITIAL_SIZE = 761;
/*      */   public static final int METHODS_AND_FIELDS_INITIAL_SIZE = 450;
/*      */   public static final int CLASS_INITIAL_SIZE = 86;
/*      */   public static final int NAMEANDTYPE_INITIAL_SIZE = 272;
/*      */   public static final int CONSTANTPOOL_INITIAL_SIZE = 2000;
/*      */   public static final int CONSTANTPOOL_GROW_SIZE = 6000;
/*      */   protected DoubleCache doubleCache;
/*      */   protected FloatCache floatCache;
/*      */   protected IntegerCache intCache;
/*      */   protected LongCache longCache;
/*      */   public CharArrayCache UTF8Cache;
/*      */   protected CharArrayCache stringCache;
/*      */   protected HashtableOfObject methodsAndFieldsCache;
/*      */   protected CharArrayCache classCache;
/*      */   protected HashtableOfObject nameAndTypeCacheForFieldsAndMethods;
/*      */   public byte[] poolContent;
/*   47 */   public int currentIndex = 1;
/*      */   public int currentOffset;
/*      */   public int[] offsets;
/*      */   public ClassFile classFile;
/*   52 */   public static final char[] Append = "append".toCharArray();
/*   53 */   public static final char[] ARRAY_NEWINSTANCE_NAME = "newInstance".toCharArray();
/*   54 */   public static final char[] ARRAY_NEWINSTANCE_SIGNATURE = "(Ljava/lang/Class;[I)Ljava/lang/Object;".toCharArray();
/*   55 */   public static final char[] ArrayCopy = "arraycopy".toCharArray();
/*   56 */   public static final char[] ArrayCopySignature = "(Ljava/lang/Object;ILjava/lang/Object;II)V".toCharArray();
/*   57 */   public static final char[] ArrayJavaLangClassConstantPoolName = "[Ljava/lang/Class;".toCharArray();
/*   58 */   public static final char[] ArrayJavaLangObjectConstantPoolName = "[Ljava/lang/Object;".toCharArray();
/*   59 */   public static final char[] booleanBooleanSignature = "(Z)Ljava/lang/Boolean;".toCharArray();
/*   60 */   public static final char[] BooleanConstrSignature = "(Z)V".toCharArray();
/*   61 */   public static final char[] BOOLEANVALUE_BOOLEAN_METHOD_NAME = "booleanValue".toCharArray();
/*   62 */   public static final char[] BOOLEANVALUE_BOOLEAN_METHOD_SIGNATURE = "()Z".toCharArray();
/*   63 */   public static final char[] byteByteSignature = "(B)Ljava/lang/Byte;".toCharArray();
/*   64 */   public static final char[] ByteConstrSignature = "(B)V".toCharArray();
/*   65 */   public static final char[] BYTEVALUE_BYTE_METHOD_NAME = "byteValue".toCharArray();
/*   66 */   public static final char[] BYTEVALUE_BYTE_METHOD_SIGNATURE = "()B".toCharArray();
/*   67 */   public static final char[] charCharacterSignature = "(C)Ljava/lang/Character;".toCharArray();
/*   68 */   public static final char[] CharConstrSignature = "(C)V".toCharArray();
/*   69 */   public static final char[] CHARVALUE_CHARACTER_METHOD_NAME = "charValue".toCharArray();
/*   70 */   public static final char[] CHARVALUE_CHARACTER_METHOD_SIGNATURE = "()C".toCharArray();
/*   71 */   public static final char[] Clinit = "<clinit>".toCharArray();
/*   72 */   public static final char[] DefaultConstructorSignature = "()V".toCharArray();
/*   73 */   public static final char[] ClinitSignature = DefaultConstructorSignature;
/*   74 */   public static final char[] DesiredAssertionStatus = "desiredAssertionStatus".toCharArray();
/*   75 */   public static final char[] DesiredAssertionStatusSignature = "()Z".toCharArray();
/*   76 */   public static final char[] DoubleConstrSignature = "(D)V".toCharArray();
/*   77 */   public static final char[] doubleDoubleSignature = "(D)Ljava/lang/Double;".toCharArray();
/*   78 */   public static final char[] DOUBLEVALUE_DOUBLE_METHOD_NAME = "doubleValue".toCharArray();
/*   79 */   public static final char[] DOUBLEVALUE_DOUBLE_METHOD_SIGNATURE = "()D".toCharArray();
/*   80 */   public static final char[] Exit = "exit".toCharArray();
/*   81 */   public static final char[] ExitIntSignature = "(I)V".toCharArray();
/*   82 */   public static final char[] FloatConstrSignature = "(F)V".toCharArray();
/*   83 */   public static final char[] floatFloatSignature = "(F)Ljava/lang/Float;".toCharArray();
/*   84 */   public static final char[] FLOATVALUE_FLOAT_METHOD_NAME = "floatValue".toCharArray();
/*   85 */   public static final char[] FLOATVALUE_FLOAT_METHOD_SIGNATURE = "()F".toCharArray();
/*   86 */   public static final char[] ForName = "forName".toCharArray();
/*   87 */   public static final char[] ForNameSignature = "(Ljava/lang/String;)Ljava/lang/Class;".toCharArray();
/*   88 */   public static final char[] GET_BOOLEAN_METHOD_NAME = "getBoolean".toCharArray();
/*   89 */   public static final char[] GET_BOOLEAN_METHOD_SIGNATURE = "(Ljava/lang/Object;)Z".toCharArray();
/*   90 */   public static final char[] GET_BYTE_METHOD_NAME = "getByte".toCharArray();
/*   91 */   public static final char[] GET_BYTE_METHOD_SIGNATURE = "(Ljava/lang/Object;)B".toCharArray();
/*   92 */   public static final char[] GET_CHAR_METHOD_NAME = "getChar".toCharArray();
/*   93 */   public static final char[] GET_CHAR_METHOD_SIGNATURE = "(Ljava/lang/Object;)C".toCharArray();
/*   94 */   public static final char[] GET_DOUBLE_METHOD_NAME = "getDouble".toCharArray();
/*   95 */   public static final char[] GET_DOUBLE_METHOD_SIGNATURE = "(Ljava/lang/Object;)D".toCharArray();
/*   96 */   public static final char[] GET_FLOAT_METHOD_NAME = "getFloat".toCharArray();
/*   97 */   public static final char[] GET_FLOAT_METHOD_SIGNATURE = "(Ljava/lang/Object;)F".toCharArray();
/*   98 */   public static final char[] GET_INT_METHOD_NAME = "getInt".toCharArray();
/*   99 */   public static final char[] GET_INT_METHOD_SIGNATURE = "(Ljava/lang/Object;)I".toCharArray();
/*  100 */   public static final char[] GET_LONG_METHOD_NAME = "getLong".toCharArray();
/*  101 */   public static final char[] GET_LONG_METHOD_SIGNATURE = "(Ljava/lang/Object;)J".toCharArray();
/*  102 */   public static final char[] GET_OBJECT_METHOD_NAME = "get".toCharArray();
/*  103 */   public static final char[] GET_OBJECT_METHOD_SIGNATURE = "(Ljava/lang/Object;)Ljava/lang/Object;".toCharArray();
/*  104 */   public static final char[] GET_SHORT_METHOD_NAME = "getShort".toCharArray();
/*  105 */   public static final char[] GET_SHORT_METHOD_SIGNATURE = "(Ljava/lang/Object;)S".toCharArray();
/*  106 */   public static final char[] GetClass = "getClass".toCharArray();
/*  107 */   public static final char[] GetClassSignature = "()Ljava/lang/Class;".toCharArray();
/*  108 */   public static final char[] GetComponentType = "getComponentType".toCharArray();
/*  109 */   public static final char[] GetComponentTypeSignature = GetClassSignature;
/*  110 */   public static final char[] GetConstructor = "getConstructor".toCharArray();
/*  111 */   public static final char[] GetConstructorSignature = "([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;".toCharArray();
/*  112 */   public static final char[] GETDECLAREDCONSTRUCTOR_NAME = "getDeclaredConstructor".toCharArray();
/*  113 */   public static final char[] GETDECLAREDCONSTRUCTOR_SIGNATURE = "([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;".toCharArray();
/*      */ 
/*  115 */   public static final char[] GETDECLAREDFIELD_NAME = "getDeclaredField".toCharArray();
/*  116 */   public static final char[] GETDECLAREDFIELD_SIGNATURE = "(Ljava/lang/String;)Ljava/lang/reflect/Field;".toCharArray();
/*  117 */   public static final char[] GETDECLAREDMETHOD_NAME = "getDeclaredMethod".toCharArray();
/*  118 */   public static final char[] GETDECLAREDMETHOD_SIGNATURE = "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;".toCharArray();
/*  119 */   public static final char[] GetMessage = "getMessage".toCharArray();
/*  120 */   public static final char[] GetMessageSignature = "()Ljava/lang/String;".toCharArray();
/*  121 */   public static final char[] HasNext = "hasNext".toCharArray();
/*  122 */   public static final char[] HasNextSignature = "()Z".toCharArray();
/*  123 */   public static final char[] Init = "<init>".toCharArray();
/*  124 */   public static final char[] IntConstrSignature = "(I)V".toCharArray();
/*  125 */   public static final char[] ITERATOR_NAME = "iterator".toCharArray();
/*  126 */   public static final char[] ITERATOR_SIGNATURE = "()Ljava/util/Iterator;".toCharArray();
/*  127 */   public static final char[] Intern = "intern".toCharArray();
/*  128 */   public static final char[] InternSignature = GetMessageSignature;
/*  129 */   public static final char[] IntIntegerSignature = "(I)Ljava/lang/Integer;".toCharArray();
/*  130 */   public static final char[] INTVALUE_INTEGER_METHOD_NAME = "intValue".toCharArray();
/*  131 */   public static final char[] INTVALUE_INTEGER_METHOD_SIGNATURE = "()I".toCharArray();
/*  132 */   public static final char[] INVOKE_METHOD_METHOD_NAME = "invoke".toCharArray();
/*  133 */   public static final char[] INVOKE_METHOD_METHOD_SIGNATURE = "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;".toCharArray();
/*  134 */   public static final char[][] JAVA_LANG_REFLECT_ACCESSIBLEOBJECT = { TypeConstants.JAVA, TypeConstants.LANG, TypeConstants.REFLECT, "AccessibleObject".toCharArray() };
/*  135 */   public static final char[][] JAVA_LANG_REFLECT_ARRAY = { TypeConstants.JAVA, TypeConstants.LANG, TypeConstants.REFLECT, "Array".toCharArray() };
/*      */ 
/*  137 */   public static final char[] JavaIoPrintStreamSignature = "Ljava/io/PrintStream;".toCharArray();
/*  138 */   public static final char[] JavaLangAssertionErrorConstantPoolName = "java/lang/AssertionError".toCharArray();
/*  139 */   public static final char[] JavaLangBooleanConstantPoolName = "java/lang/Boolean".toCharArray();
/*  140 */   public static final char[] JavaLangByteConstantPoolName = "java/lang/Byte".toCharArray();
/*  141 */   public static final char[] JavaLangCharacterConstantPoolName = "java/lang/Character".toCharArray();
/*  142 */   public static final char[] JavaLangClassConstantPoolName = "java/lang/Class".toCharArray();
/*  143 */   public static final char[] JavaLangClassNotFoundExceptionConstantPoolName = "java/lang/ClassNotFoundException".toCharArray();
/*  144 */   public static final char[] JavaLangClassSignature = "Ljava/lang/Class;".toCharArray();
/*  145 */   public static final char[] JavaLangDoubleConstantPoolName = "java/lang/Double".toCharArray();
/*  146 */   public static final char[] JavaLangEnumConstantPoolName = "java/lang/Enum".toCharArray();
/*  147 */   public static final char[] JavaLangErrorConstantPoolName = "java/lang/Error".toCharArray();
/*  148 */   public static final char[] JavaLangExceptionConstantPoolName = "java/lang/Exception".toCharArray();
/*  149 */   public static final char[] JavaLangFloatConstantPoolName = "java/lang/Float".toCharArray();
/*  150 */   public static final char[] JavaLangIntegerConstantPoolName = "java/lang/Integer".toCharArray();
/*  151 */   public static final char[] JavaLangLongConstantPoolName = "java/lang/Long".toCharArray();
/*  152 */   public static final char[] JavaLangNoClassDefFoundErrorConstantPoolName = "java/lang/NoClassDefFoundError".toCharArray();
/*  153 */   public static final char[] JavaLangNoSuchFieldErrorConstantPoolName = "java/lang/NoSuchFieldError".toCharArray();
/*  154 */   public static final char[] JavaLangObjectConstantPoolName = "java/lang/Object".toCharArray();
/*  155 */   public static final char[] JAVALANGREFLECTACCESSIBLEOBJECT_CONSTANTPOOLNAME = "java/lang/reflect/AccessibleObject".toCharArray();
/*  156 */   public static final char[] JAVALANGREFLECTARRAY_CONSTANTPOOLNAME = "java/lang/reflect/Array".toCharArray();
/*  157 */   public static final char[] JavaLangReflectConstructorConstantPoolName = "java/lang/reflect/Constructor".toCharArray();
/*  158 */   public static final char[] JavaLangReflectConstructorNewInstanceSignature = "([Ljava/lang/Object;)Ljava/lang/Object;".toCharArray();
/*  159 */   public static final char[] JAVALANGREFLECTFIELD_CONSTANTPOOLNAME = "java/lang/reflect/Field".toCharArray();
/*  160 */   public static final char[] JAVALANGREFLECTMETHOD_CONSTANTPOOLNAME = "java/lang/reflect/Method".toCharArray();
/*  161 */   public static final char[] JavaLangShortConstantPoolName = "java/lang/Short".toCharArray();
/*  162 */   public static final char[] JavaLangStringBufferConstantPoolName = "java/lang/StringBuffer".toCharArray();
/*  163 */   public static final char[] JavaLangStringBuilderConstantPoolName = "java/lang/StringBuilder".toCharArray();
/*  164 */   public static final char[] JavaLangStringConstantPoolName = "java/lang/String".toCharArray();
/*  165 */   public static final char[] JavaLangStringSignature = "Ljava/lang/String;".toCharArray();
/*  166 */   public static final char[] JavaLangObjectSignature = "Ljava/lang/Object;".toCharArray();
/*  167 */   public static final char[] JavaLangSystemConstantPoolName = "java/lang/System".toCharArray();
/*  168 */   public static final char[] JavaLangThrowableConstantPoolName = "java/lang/Throwable".toCharArray();
/*  169 */   public static final char[] JavaLangVoidConstantPoolName = "java/lang/Void".toCharArray();
/*  170 */   public static final char[] JavaUtilIteratorConstantPoolName = "java/util/Iterator".toCharArray();
/*  171 */   public static final char[] LongConstrSignature = "(J)V".toCharArray();
/*  172 */   public static final char[] longLongSignature = "(J)Ljava/lang/Long;".toCharArray();
/*  173 */   public static final char[] LONGVALUE_LONG_METHOD_NAME = "longValue".toCharArray();
/*  174 */   public static final char[] LONGVALUE_LONG_METHOD_SIGNATURE = "()J".toCharArray();
/*  175 */   public static final char[] NewInstance = "newInstance".toCharArray();
/*  176 */   public static final char[] NewInstanceSignature = "(Ljava/lang/Class;[I)Ljava/lang/Object;".toCharArray();
/*  177 */   public static final char[] Next = "next".toCharArray();
/*  178 */   public static final char[] NextSignature = "()Ljava/lang/Object;".toCharArray();
/*  179 */   public static final char[] ObjectConstrSignature = "(Ljava/lang/Object;)V".toCharArray();
/*  180 */   public static final char[] ObjectSignature = "Ljava/lang/Object;".toCharArray();
/*  181 */   public static final char[] Ordinal = "ordinal".toCharArray();
/*  182 */   public static final char[] OrdinalSignature = "()I".toCharArray();
/*  183 */   public static final char[] Out = "out".toCharArray();
/*  184 */   public static final char[] SET_BOOLEAN_METHOD_NAME = "setBoolean".toCharArray();
/*  185 */   public static final char[] SET_BOOLEAN_METHOD_SIGNATURE = "(Ljava/lang/Object;Z)V".toCharArray();
/*  186 */   public static final char[] SET_BYTE_METHOD_NAME = "setByte".toCharArray();
/*  187 */   public static final char[] SET_BYTE_METHOD_SIGNATURE = "(Ljava/lang/Object;B)V".toCharArray();
/*  188 */   public static final char[] SET_CHAR_METHOD_NAME = "setChar".toCharArray();
/*  189 */   public static final char[] SET_CHAR_METHOD_SIGNATURE = "(Ljava/lang/Object;C)V".toCharArray();
/*  190 */   public static final char[] SET_DOUBLE_METHOD_NAME = "setDouble".toCharArray();
/*  191 */   public static final char[] SET_DOUBLE_METHOD_SIGNATURE = "(Ljava/lang/Object;D)V".toCharArray();
/*  192 */   public static final char[] SET_FLOAT_METHOD_NAME = "setFloat".toCharArray();
/*  193 */   public static final char[] SET_FLOAT_METHOD_SIGNATURE = "(Ljava/lang/Object;F)V".toCharArray();
/*  194 */   public static final char[] SET_INT_METHOD_NAME = "setInt".toCharArray();
/*  195 */   public static final char[] SET_INT_METHOD_SIGNATURE = "(Ljava/lang/Object;I)V".toCharArray();
/*  196 */   public static final char[] SET_LONG_METHOD_NAME = "setLong".toCharArray();
/*  197 */   public static final char[] SET_LONG_METHOD_SIGNATURE = "(Ljava/lang/Object;J)V".toCharArray();
/*  198 */   public static final char[] SET_OBJECT_METHOD_NAME = "set".toCharArray();
/*  199 */   public static final char[] SET_OBJECT_METHOD_SIGNATURE = "(Ljava/lang/Object;Ljava/lang/Object;)V".toCharArray();
/*  200 */   public static final char[] SET_SHORT_METHOD_NAME = "setShort".toCharArray();
/*  201 */   public static final char[] SET_SHORT_METHOD_SIGNATURE = "(Ljava/lang/Object;S)V".toCharArray();
/*  202 */   public static final char[] SETACCESSIBLE_NAME = "setAccessible".toCharArray();
/*  203 */   public static final char[] SETACCESSIBLE_SIGNATURE = "(Z)V".toCharArray();
/*  204 */   public static final char[] ShortConstrSignature = "(S)V".toCharArray();
/*  205 */   public static final char[] shortShortSignature = "(S)Ljava/lang/Short;".toCharArray();
/*  206 */   public static final char[] SHORTVALUE_SHORT_METHOD_NAME = "shortValue".toCharArray();
/*  207 */   public static final char[] SHORTVALUE_SHORT_METHOD_SIGNATURE = "()S".toCharArray();
/*  208 */   public static final char[] StringBufferAppendBooleanSignature = "(Z)Ljava/lang/StringBuffer;".toCharArray();
/*  209 */   public static final char[] StringBufferAppendCharSignature = "(C)Ljava/lang/StringBuffer;".toCharArray();
/*  210 */   public static final char[] StringBufferAppendDoubleSignature = "(D)Ljava/lang/StringBuffer;".toCharArray();
/*  211 */   public static final char[] StringBufferAppendFloatSignature = "(F)Ljava/lang/StringBuffer;".toCharArray();
/*  212 */   public static final char[] StringBufferAppendIntSignature = "(I)Ljava/lang/StringBuffer;".toCharArray();
/*  213 */   public static final char[] StringBufferAppendLongSignature = "(J)Ljava/lang/StringBuffer;".toCharArray();
/*  214 */   public static final char[] StringBufferAppendObjectSignature = "(Ljava/lang/Object;)Ljava/lang/StringBuffer;".toCharArray();
/*  215 */   public static final char[] StringBufferAppendStringSignature = "(Ljava/lang/String;)Ljava/lang/StringBuffer;".toCharArray();
/*  216 */   public static final char[] StringBuilderAppendBooleanSignature = "(Z)Ljava/lang/StringBuilder;".toCharArray();
/*  217 */   public static final char[] StringBuilderAppendCharSignature = "(C)Ljava/lang/StringBuilder;".toCharArray();
/*  218 */   public static final char[] StringBuilderAppendDoubleSignature = "(D)Ljava/lang/StringBuilder;".toCharArray();
/*  219 */   public static final char[] StringBuilderAppendFloatSignature = "(F)Ljava/lang/StringBuilder;".toCharArray();
/*  220 */   public static final char[] StringBuilderAppendIntSignature = "(I)Ljava/lang/StringBuilder;".toCharArray();
/*  221 */   public static final char[] StringBuilderAppendLongSignature = "(J)Ljava/lang/StringBuilder;".toCharArray();
/*  222 */   public static final char[] StringBuilderAppendObjectSignature = "(Ljava/lang/Object;)Ljava/lang/StringBuilder;".toCharArray();
/*  223 */   public static final char[] StringBuilderAppendStringSignature = "(Ljava/lang/String;)Ljava/lang/StringBuilder;".toCharArray();
/*  224 */   public static final char[] StringConstructorSignature = "(Ljava/lang/String;)V".toCharArray();
/*  225 */   public static final char[] This = "this".toCharArray();
/*  226 */   public static final char[] ToString = "toString".toCharArray();
/*  227 */   public static final char[] ToStringSignature = GetMessageSignature;
/*  228 */   public static final char[] TYPE = "TYPE".toCharArray();
/*  229 */   public static final char[] ValueOf = "valueOf".toCharArray();
/*  230 */   public static final char[] ValueOfBooleanSignature = "(Z)Ljava/lang/String;".toCharArray();
/*  231 */   public static final char[] ValueOfCharSignature = "(C)Ljava/lang/String;".toCharArray();
/*  232 */   public static final char[] ValueOfDoubleSignature = "(D)Ljava/lang/String;".toCharArray();
/*  233 */   public static final char[] ValueOfFloatSignature = "(F)Ljava/lang/String;".toCharArray();
/*  234 */   public static final char[] ValueOfIntSignature = "(I)Ljava/lang/String;".toCharArray();
/*  235 */   public static final char[] ValueOfLongSignature = "(J)Ljava/lang/String;".toCharArray();
/*  236 */   public static final char[] ValueOfObjectSignature = "(Ljava/lang/Object;)Ljava/lang/String;".toCharArray();
/*  237 */   public static final char[] ValueOfStringClassSignature = "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;".toCharArray();
/*  238 */   public static final char[] JAVA_LANG_ANNOTATION_DOCUMENTED = "Ljava/lang/annotation/Documented;".toCharArray();
/*  239 */   public static final char[] JAVA_LANG_ANNOTATION_ELEMENTTYPE = "Ljava/lang/annotation/ElementType;".toCharArray();
/*  240 */   public static final char[] JAVA_LANG_ANNOTATION_RETENTION = "Ljava/lang/annotation/Retention;".toCharArray();
/*  241 */   public static final char[] JAVA_LANG_ANNOTATION_RETENTIONPOLICY = "Ljava/lang/annotation/RetentionPolicy;".toCharArray();
/*  242 */   public static final char[] JAVA_LANG_ANNOTATION_TARGET = "Ljava/lang/annotation/Target;".toCharArray();
/*  243 */   public static final char[] JAVA_LANG_DEPRECATED = "Ljava/lang/Deprecated;".toCharArray();
/*  244 */   public static final char[] JAVA_LANG_ANNOTATION_INHERITED = "Ljava/lang/annotation/Inherited;".toCharArray();
/*      */ 
/*      */   public ConstantPool(ClassFile classFile)
/*      */   {
/*  249 */     this.UTF8Cache = new CharArrayCache(778);
/*  250 */     this.stringCache = new CharArrayCache(761);
/*  251 */     this.methodsAndFieldsCache = new HashtableOfObject(450);
/*  252 */     this.classCache = new CharArrayCache(86);
/*  253 */     this.nameAndTypeCacheForFieldsAndMethods = new HashtableOfObject(272);
/*  254 */     this.offsets = new int[5];
/*  255 */     initialize(classFile);
/*      */   }
/*      */   public void initialize(ClassFile givenClassFile) {
/*  258 */     this.poolContent = givenClassFile.header;
/*  259 */     this.currentOffset = givenClassFile.headerOffset;
/*      */ 
/*  261 */     this.currentIndex = 1;
/*  262 */     this.classFile = givenClassFile;
/*      */   }
/*      */ 
/*      */   public byte[] dumpBytes()
/*      */   {
/*  268 */     System.arraycopy(this.poolContent, 0, this.poolContent = new byte[this.currentOffset], 0, this.currentOffset);
/*  269 */     return this.poolContent;
/*      */   }
/*      */ 
/*      */   public int literalIndex(byte[] utf8encoding, char[] stringCharArray)
/*      */   {
/*      */     int index;
/*  273 */     if ((index = this.UTF8Cache.putIfAbsent(stringCharArray, this.currentIndex)) < 0)
/*      */     {
/*  275 */       if ((index = -index) > 65535) {
/*  276 */         this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
/*      */       }
/*  278 */       this.currentIndex += 1;
/*      */ 
/*  280 */       int length = this.offsets.length;
/*  281 */       if (length <= index)
/*      */       {
/*  283 */         System.arraycopy(this.offsets, 0, this.offsets = new int[index * 2], 0, length);
/*      */       }
/*  285 */       this.offsets[index] = this.currentOffset;
/*  286 */       writeU1(1);
/*  287 */       int utf8encodingLength = utf8encoding.length;
/*  288 */       if (this.currentOffset + 2 + utf8encodingLength >= this.poolContent.length)
/*      */       {
/*  291 */         resizePoolContents(2 + utf8encodingLength);
/*      */       }
/*  293 */       this.poolContent[(this.currentOffset++)] = (byte)(utf8encodingLength >> 8);
/*  294 */       this.poolContent[(this.currentOffset++)] = (byte)utf8encodingLength;
/*      */ 
/*  296 */       System.arraycopy(utf8encoding, 0, this.poolContent, this.currentOffset, utf8encodingLength);
/*  297 */       this.currentOffset += utf8encodingLength;
/*      */     }
/*  299 */     return index;
/*      */   }
/*      */   public int literalIndex(TypeBinding binding) {
/*  302 */     TypeBinding typeBinding = binding.leafComponentType();
/*  303 */     if ((typeBinding.tagBits & 0x800) != 0L) {
/*  304 */       Util.recordNestedType(this.classFile, typeBinding);
/*      */     }
/*  306 */     return literalIndex(binding.signature());
/*      */   }
/*      */ 
/*      */   public int literalIndex(char[] utf8Constant)
/*      */   {
/*      */     int index;
/*  316 */     if ((index = this.UTF8Cache.putIfAbsent(utf8Constant, this.currentIndex)) < 0) {
/*  317 */       if ((index = -index) > 65535) {
/*  318 */         this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
/*      */       }
/*      */ 
/*  322 */       int length = this.offsets.length;
/*  323 */       if (length <= index)
/*      */       {
/*  325 */         System.arraycopy(this.offsets, 0, this.offsets = new int[index * 2], 0, length);
/*      */       }
/*  327 */       this.offsets[index] = this.currentOffset;
/*  328 */       writeU1(1);
/*      */ 
/*  330 */       int savedCurrentOffset = this.currentOffset;
/*  331 */       if (this.currentOffset + 2 >= this.poolContent.length)
/*      */       {
/*  334 */         resizePoolContents(2);
/*      */       }
/*  336 */       this.currentOffset += 2;
/*  337 */       length = 0;
/*  338 */       for (int i = 0; i < utf8Constant.length; i++) {
/*  339 */         char current = utf8Constant[i];
/*  340 */         if ((current >= '\001') && (current <= ''))
/*      */         {
/*  342 */           writeU1(current);
/*  343 */           length++;
/*      */         }
/*  345 */         else if (current > '߿')
/*      */         {
/*  347 */           length += 3;
/*  348 */           writeU1(0xE0 | current >> '\f' & 0xF);
/*  349 */           writeU1(0x80 | current >> '\006' & 0x3F);
/*  350 */           writeU1(0x80 | current & 0x3F);
/*      */         }
/*      */         else
/*      */         {
/*  354 */           length += 2;
/*  355 */           writeU1(0xC0 | current >> '\006' & 0x1F);
/*  356 */           writeU1(0x80 | current & 0x3F);
/*      */         }
/*      */       }
/*      */ 
/*  360 */       if (length >= 65535) {
/*  361 */         this.currentOffset = (savedCurrentOffset - 1);
/*  362 */         this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceForConstant(this.classFile.referenceBinding.scope.referenceType());
/*      */       }
/*  364 */       if (index > 65535) {
/*  365 */         this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
/*      */       }
/*  367 */       this.currentIndex += 1;
/*      */ 
/*  370 */       this.poolContent[savedCurrentOffset] = (byte)(length >> 8);
/*  371 */       this.poolContent[(savedCurrentOffset + 1)] = (byte)length;
/*      */     }
/*  373 */     return index;
/*      */   }
/*      */ 
/*      */   public int literalIndex(char[] stringCharArray, byte[] utf8encoding)
/*      */   {
/*      */     int index;
/*  377 */     if ((index = this.stringCache.putIfAbsent(stringCharArray, this.currentIndex)) < 0)
/*      */     {
/*  379 */       this.currentIndex += 1;
/*  380 */       if ((index = -index) > 65535) {
/*  381 */         this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
/*      */       }
/*      */ 
/*  384 */       int length = this.offsets.length;
/*  385 */       if (length <= index)
/*      */       {
/*  387 */         System.arraycopy(this.offsets, 0, this.offsets = new int[index * 2], 0, length);
/*      */       }
/*  389 */       this.offsets[index] = this.currentOffset;
/*  390 */       writeU1(8);
/*      */ 
/*  392 */       int stringIndexOffset = this.currentOffset;
/*  393 */       if (this.currentOffset + 2 >= this.poolContent.length) {
/*  394 */         resizePoolContents(2);
/*      */       }
/*  396 */       this.currentOffset += 2;
/*      */ 
/*  398 */       int stringIndex = literalIndex(utf8encoding, stringCharArray);
/*  399 */       this.poolContent[(stringIndexOffset++)] = (byte)(stringIndex >> 8);
/*  400 */       this.poolContent[stringIndexOffset] = (byte)stringIndex;
/*      */     }
/*  402 */     return index;
/*      */   }
/*      */ 
/*      */   public int literalIndex(double key)
/*      */   {
/*  419 */     if (this.doubleCache == null)
/*  420 */       this.doubleCache = new DoubleCache(5);
/*      */     int index;
/*  422 */     if ((index = this.doubleCache.putIfAbsent(key, this.currentIndex)) < 0) {
/*  423 */       if ((index = -index) > 65535) {
/*  424 */         this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
/*      */       }
/*  426 */       this.currentIndex += 2;
/*      */ 
/*  429 */       int length = this.offsets.length;
/*  430 */       if (length <= index)
/*      */       {
/*  432 */         System.arraycopy(this.offsets, 0, this.offsets = new int[index * 2], 0, length);
/*      */       }
/*  434 */       this.offsets[index] = this.currentOffset;
/*  435 */       writeU1(6);
/*      */ 
/*  437 */       long temp = Double.doubleToLongBits(key);
/*  438 */       length = this.poolContent.length;
/*  439 */       if (this.currentOffset + 8 >= length) {
/*  440 */         resizePoolContents(8);
/*      */       }
/*  442 */       this.poolContent[(this.currentOffset++)] = (byte)(int)(temp >>> 56);
/*  443 */       this.poolContent[(this.currentOffset++)] = (byte)(int)(temp >>> 48);
/*  444 */       this.poolContent[(this.currentOffset++)] = (byte)(int)(temp >>> 40);
/*  445 */       this.poolContent[(this.currentOffset++)] = (byte)(int)(temp >>> 32);
/*  446 */       this.poolContent[(this.currentOffset++)] = (byte)(int)(temp >>> 24);
/*  447 */       this.poolContent[(this.currentOffset++)] = (byte)(int)(temp >>> 16);
/*  448 */       this.poolContent[(this.currentOffset++)] = (byte)(int)(temp >>> 8);
/*  449 */       this.poolContent[(this.currentOffset++)] = (byte)(int)temp;
/*      */     }
/*  451 */     return index;
/*      */   }
/*      */ 
/*      */   public int literalIndex(float key)
/*      */   {
/*  466 */     if (this.floatCache == null)
/*  467 */       this.floatCache = new FloatCache(3);
/*      */     int index;
/*  469 */     if ((index = this.floatCache.putIfAbsent(key, this.currentIndex)) < 0) {
/*  470 */       if ((index = -index) > 65535) {
/*  471 */         this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
/*      */       }
/*  473 */       this.currentIndex += 1;
/*      */ 
/*  476 */       int length = this.offsets.length;
/*  477 */       if (length <= index)
/*      */       {
/*  479 */         System.arraycopy(this.offsets, 0, this.offsets = new int[index * 2], 0, length);
/*      */       }
/*  481 */       this.offsets[index] = this.currentOffset;
/*  482 */       writeU1(4);
/*      */ 
/*  484 */       int temp = Float.floatToIntBits(key);
/*  485 */       if (this.currentOffset + 4 >= this.poolContent.length) {
/*  486 */         resizePoolContents(4);
/*      */       }
/*  488 */       this.poolContent[(this.currentOffset++)] = (byte)(temp >>> 24);
/*  489 */       this.poolContent[(this.currentOffset++)] = (byte)(temp >>> 16);
/*  490 */       this.poolContent[(this.currentOffset++)] = (byte)(temp >>> 8);
/*  491 */       this.poolContent[(this.currentOffset++)] = (byte)temp;
/*      */     }
/*  493 */     return index;
/*      */   }
/*      */ 
/*      */   public int literalIndex(int key)
/*      */   {
/*  508 */     if (this.intCache == null)
/*  509 */       this.intCache = new IntegerCache(248);
/*      */     int index;
/*  511 */     if ((index = this.intCache.putIfAbsent(key, this.currentIndex)) < 0) {
/*  512 */       this.currentIndex += 1;
/*  513 */       if ((index = -index) > 65535) {
/*  514 */         this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
/*      */       }
/*      */ 
/*  518 */       int length = this.offsets.length;
/*  519 */       if (length <= index)
/*      */       {
/*  521 */         System.arraycopy(this.offsets, 0, this.offsets = new int[index * 2], 0, length);
/*      */       }
/*  523 */       this.offsets[index] = this.currentOffset;
/*  524 */       writeU1(3);
/*      */ 
/*  526 */       if (this.currentOffset + 4 >= this.poolContent.length) {
/*  527 */         resizePoolContents(4);
/*      */       }
/*  529 */       this.poolContent[(this.currentOffset++)] = (byte)(key >>> 24);
/*  530 */       this.poolContent[(this.currentOffset++)] = (byte)(key >>> 16);
/*  531 */       this.poolContent[(this.currentOffset++)] = (byte)(key >>> 8);
/*  532 */       this.poolContent[(this.currentOffset++)] = (byte)key;
/*      */     }
/*  534 */     return index;
/*      */   }
/*      */ 
/*      */   public int literalIndex(long key)
/*      */   {
/*  551 */     if (this.longCache == null)
/*  552 */       this.longCache = new LongCache(5);
/*      */     int index;
/*  554 */     if ((index = this.longCache.putIfAbsent(key, this.currentIndex)) < 0) {
/*  555 */       if ((index = -index) > 65535) {
/*  556 */         this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
/*      */       }
/*  558 */       this.currentIndex += 2;
/*      */ 
/*  561 */       int length = this.offsets.length;
/*  562 */       if (length <= index)
/*      */       {
/*  564 */         System.arraycopy(this.offsets, 0, this.offsets = new int[index * 2], 0, length);
/*      */       }
/*  566 */       this.offsets[index] = this.currentOffset;
/*  567 */       writeU1(5);
/*      */ 
/*  569 */       if (this.currentOffset + 8 >= this.poolContent.length) {
/*  570 */         resizePoolContents(8);
/*      */       }
/*  572 */       this.poolContent[(this.currentOffset++)] = (byte)(int)(key >>> 56);
/*  573 */       this.poolContent[(this.currentOffset++)] = (byte)(int)(key >>> 48);
/*  574 */       this.poolContent[(this.currentOffset++)] = (byte)(int)(key >>> 40);
/*  575 */       this.poolContent[(this.currentOffset++)] = (byte)(int)(key >>> 32);
/*  576 */       this.poolContent[(this.currentOffset++)] = (byte)(int)(key >>> 24);
/*  577 */       this.poolContent[(this.currentOffset++)] = (byte)(int)(key >>> 16);
/*  578 */       this.poolContent[(this.currentOffset++)] = (byte)(int)(key >>> 8);
/*  579 */       this.poolContent[(this.currentOffset++)] = (byte)(int)key;
/*      */     }
/*  581 */     return index;
/*      */   }
/*      */ 
/*      */   public int literalIndex(String stringConstant)
/*      */   {
/*  591 */     char[] stringCharArray = stringConstant.toCharArray();
/*      */     int index;
/*  592 */     if ((index = this.stringCache.putIfAbsent(stringCharArray, this.currentIndex)) < 0)
/*      */     {
/*  594 */       this.currentIndex += 1;
/*  595 */       if ((index = -index) > 65535) {
/*  596 */         this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
/*      */       }
/*      */ 
/*  599 */       int length = this.offsets.length;
/*  600 */       if (length <= index)
/*      */       {
/*  602 */         System.arraycopy(this.offsets, 0, this.offsets = new int[index * 2], 0, length);
/*      */       }
/*  604 */       this.offsets[index] = this.currentOffset;
/*  605 */       writeU1(8);
/*      */ 
/*  607 */       int stringIndexOffset = this.currentOffset;
/*  608 */       if (this.currentOffset + 2 >= this.poolContent.length) {
/*  609 */         resizePoolContents(2);
/*      */       }
/*  611 */       this.currentOffset += 2;
/*  612 */       int stringIndex = literalIndex(stringCharArray);
/*  613 */       this.poolContent[(stringIndexOffset++)] = (byte)(stringIndex >> 8);
/*  614 */       this.poolContent[stringIndexOffset] = (byte)stringIndex;
/*      */     }
/*  616 */     return index;
/*      */   }
/*      */ 
/*      */   public int literalIndexForType(char[] constantPoolName)
/*      */   {
/*      */     int index;
/*  620 */     if ((index = this.classCache.putIfAbsent(constantPoolName, this.currentIndex)) < 0)
/*      */     {
/*  622 */       this.currentIndex += 1;
/*  623 */       if ((index = -index) > 65535) {
/*  624 */         this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
/*      */       }
/*  626 */       int length = this.offsets.length;
/*  627 */       if (length <= index)
/*      */       {
/*  629 */         System.arraycopy(this.offsets, 0, this.offsets = new int[index * 2], 0, length);
/*      */       }
/*  631 */       this.offsets[index] = this.currentOffset;
/*  632 */       writeU1(7);
/*      */ 
/*  635 */       int nameIndexOffset = this.currentOffset;
/*  636 */       if (this.currentOffset + 2 >= this.poolContent.length) {
/*  637 */         resizePoolContents(2);
/*      */       }
/*  639 */       this.currentOffset += 2;
/*  640 */       int nameIndex = literalIndex(constantPoolName);
/*  641 */       this.poolContent[(nameIndexOffset++)] = (byte)(nameIndex >> 8);
/*  642 */       this.poolContent[nameIndexOffset] = (byte)nameIndex;
/*      */     }
/*  644 */     return index;
/*      */   }
/*      */ 
/*      */   public int literalIndexForType(TypeBinding binding)
/*      */   {
/*  652 */     TypeBinding typeBinding = binding.leafComponentType();
/*  653 */     if ((typeBinding.tagBits & 0x800) != 0L) {
/*  654 */       Util.recordNestedType(this.classFile, typeBinding);
/*      */     }
/*  656 */     return literalIndexForType(binding.constantPoolName());
/*      */   }
/*      */ 
/*      */   public int literalIndexForMethod(char[] declaringClass, char[] selector, char[] signature, boolean isInterface)
/*      */   {
/*      */     int index;
/*  660 */     if ((index = putInCacheIfAbsent(declaringClass, selector, signature, this.currentIndex)) < 0)
/*      */     {
/*  662 */       this.currentIndex += 1;
/*  663 */       if ((index = -index) > 65535) {
/*  664 */         this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
/*      */       }
/*      */ 
/*  668 */       int length = this.offsets.length;
/*  669 */       if (length <= index)
/*      */       {
/*  671 */         System.arraycopy(this.offsets, 0, this.offsets = new int[index * 2], 0, length);
/*      */       }
/*  673 */       this.offsets[index] = this.currentOffset;
/*  674 */       writeU1(isInterface ? 11 : 10);
/*      */ 
/*  676 */       int classIndexOffset = this.currentOffset;
/*  677 */       if (this.currentOffset + 4 >= this.poolContent.length) {
/*  678 */         resizePoolContents(4);
/*      */       }
/*  680 */       this.currentOffset += 4;
/*      */ 
/*  682 */       int classIndex = literalIndexForType(declaringClass);
/*  683 */       int nameAndTypeIndex = literalIndexForNameAndType(selector, signature);
/*      */ 
/*  685 */       this.poolContent[(classIndexOffset++)] = (byte)(classIndex >> 8);
/*  686 */       this.poolContent[(classIndexOffset++)] = (byte)classIndex;
/*  687 */       this.poolContent[(classIndexOffset++)] = (byte)(nameAndTypeIndex >> 8);
/*  688 */       this.poolContent[classIndexOffset] = (byte)nameAndTypeIndex;
/*      */     }
/*  690 */     return index;
/*      */   }
/*      */   public int literalIndexForMethod(TypeBinding declaringClass, char[] selector, char[] signature, boolean isInterface) {
/*  693 */     if ((declaringClass.tagBits & 0x800) != 0L) {
/*  694 */       Util.recordNestedType(this.classFile, declaringClass);
/*      */     }
/*  696 */     return literalIndexForMethod(declaringClass.constantPoolName(), selector, signature, isInterface);
/*      */   }
/*      */ 
/*      */   public int literalIndexForNameAndType(char[] name, char[] signature)
/*      */   {
/*      */     int index;
/*  700 */     if ((index = putInNameAndTypeCacheIfAbsent(name, signature, this.currentIndex)) < 0)
/*      */     {
/*  702 */       this.currentIndex += 1;
/*  703 */       if ((index = -index) > 65535) {
/*  704 */         this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
/*      */       }
/*  706 */       int length = this.offsets.length;
/*  707 */       if (length <= index)
/*      */       {
/*  709 */         System.arraycopy(this.offsets, 0, this.offsets = new int[index * 2], 0, length);
/*      */       }
/*  711 */       this.offsets[index] = this.currentOffset;
/*  712 */       writeU1(12);
/*  713 */       int nameIndexOffset = this.currentOffset;
/*  714 */       if (this.currentOffset + 4 >= this.poolContent.length) {
/*  715 */         resizePoolContents(4);
/*      */       }
/*  717 */       this.currentOffset += 4;
/*      */ 
/*  719 */       int nameIndex = literalIndex(name);
/*  720 */       int typeIndex = literalIndex(signature);
/*  721 */       this.poolContent[(nameIndexOffset++)] = (byte)(nameIndex >> 8);
/*  722 */       this.poolContent[(nameIndexOffset++)] = (byte)nameIndex;
/*  723 */       this.poolContent[(nameIndexOffset++)] = (byte)(typeIndex >> 8);
/*  724 */       this.poolContent[nameIndexOffset] = (byte)typeIndex;
/*      */     }
/*  726 */     return index;
/*      */   }
/*      */ 
/*      */   public int literalIndexForField(char[] declaringClass, char[] name, char[] signature)
/*      */   {
/*      */     int index;
/*  730 */     if ((index = putInCacheIfAbsent(declaringClass, name, signature, this.currentIndex)) < 0) {
/*  731 */       this.currentIndex += 1;
/*      */ 
/*  733 */       if ((index = -index) > 65535) {
/*  734 */         this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
/*      */       }
/*      */ 
/*  738 */       int length = this.offsets.length;
/*  739 */       if (length <= index)
/*      */       {
/*  741 */         System.arraycopy(this.offsets, 0, this.offsets = new int[index * 2], 0, length);
/*      */       }
/*  743 */       this.offsets[index] = this.currentOffset;
/*  744 */       writeU1(9);
/*  745 */       int classIndexOffset = this.currentOffset;
/*  746 */       if (this.currentOffset + 4 >= this.poolContent.length) {
/*  747 */         resizePoolContents(4);
/*      */       }
/*  749 */       this.currentOffset += 4;
/*      */ 
/*  751 */       int classIndex = literalIndexForType(declaringClass);
/*  752 */       int nameAndTypeIndex = literalIndexForNameAndType(name, signature);
/*      */ 
/*  754 */       this.poolContent[(classIndexOffset++)] = (byte)(classIndex >> 8);
/*  755 */       this.poolContent[(classIndexOffset++)] = (byte)classIndex;
/*  756 */       this.poolContent[(classIndexOffset++)] = (byte)(nameAndTypeIndex >> 8);
/*  757 */       this.poolContent[classIndexOffset] = (byte)nameAndTypeIndex;
/*      */     }
/*  759 */     return index;
/*      */   }
/*      */ 
/*      */   public int literalIndexForLdc(char[] stringCharArray)
/*      */   {
/*  768 */     int savedCurrentIndex = this.currentIndex;
/*  769 */     int savedCurrentOffset = this.currentOffset;
/*      */     int index;
/*  771 */     if ((index = this.stringCache.putIfAbsent(stringCharArray, this.currentIndex)) < 0) {
/*  772 */       if ((index = -index) > 65535) {
/*  773 */         this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
/*      */       }
/*      */ 
/*  776 */       this.currentIndex += 1;
/*      */ 
/*  778 */       int length = this.offsets.length;
/*  779 */       if (length <= index)
/*      */       {
/*  781 */         System.arraycopy(this.offsets, 0, this.offsets = new int[index * 2], 0, length);
/*      */       }
/*  783 */       this.offsets[index] = this.currentOffset;
/*  784 */       writeU1(8);
/*      */ 
/*  787 */       int stringIndexOffset = this.currentOffset;
/*  788 */       if (this.currentOffset + 2 >= this.poolContent.length) {
/*  789 */         resizePoolContents(2);
/*      */       }
/*  791 */       this.currentOffset += 2;
/*      */       int stringIndex;
/*  794 */       if ((stringIndex = this.UTF8Cache.putIfAbsent(stringCharArray, this.currentIndex)) < 0) {
/*  795 */         if ((stringIndex = -stringIndex) > 65535) {
/*  796 */           this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
/*      */         }
/*      */ 
/*  799 */         this.currentIndex += 1;
/*      */ 
/*  801 */         length = this.offsets.length;
/*  802 */         if (length <= stringIndex)
/*      */         {
/*  804 */           System.arraycopy(this.offsets, 0, this.offsets = new int[stringIndex * 2], 0, length);
/*      */         }
/*  806 */         this.offsets[stringIndex] = this.currentOffset;
/*  807 */         writeU1(1);
/*      */ 
/*  809 */         int lengthOffset = this.currentOffset;
/*  810 */         if (this.currentOffset + 2 >= this.poolContent.length)
/*      */         {
/*  813 */           resizePoolContents(2);
/*      */         }
/*  815 */         this.currentOffset += 2;
/*  816 */         length = 0;
/*  817 */         for (int i = 0; i < stringCharArray.length; i++) {
/*  818 */           char current = stringCharArray[i];
/*  819 */           if ((current >= '\001') && (current <= ''))
/*      */           {
/*  821 */             length++;
/*  822 */             if (this.currentOffset + 1 >= this.poolContent.length)
/*      */             {
/*  825 */               resizePoolContents(1);
/*      */             }
/*  827 */             this.poolContent[(this.currentOffset++)] = (byte)current;
/*      */           }
/*  829 */           else if (current > '߿')
/*      */           {
/*  831 */             length += 3;
/*  832 */             if (this.currentOffset + 3 >= this.poolContent.length)
/*      */             {
/*  835 */               resizePoolContents(3);
/*      */             }
/*  837 */             this.poolContent[(this.currentOffset++)] = (byte)(0xE0 | current >> '\f' & 0xF);
/*  838 */             this.poolContent[(this.currentOffset++)] = (byte)(0x80 | current >> '\006' & 0x3F);
/*  839 */             this.poolContent[(this.currentOffset++)] = (byte)(0x80 | current & 0x3F);
/*      */           } else {
/*  841 */             if (this.currentOffset + 2 >= this.poolContent.length)
/*      */             {
/*  844 */               resizePoolContents(2);
/*      */             }
/*      */ 
/*  848 */             length += 2;
/*  849 */             this.poolContent[(this.currentOffset++)] = (byte)(0xC0 | current >> '\006' & 0x1F);
/*  850 */             this.poolContent[(this.currentOffset++)] = (byte)(0x80 | current & 0x3F);
/*      */           }
/*      */         }
/*  853 */         if (length >= 65535) {
/*  854 */           this.currentOffset = savedCurrentOffset;
/*  855 */           this.currentIndex = savedCurrentIndex;
/*  856 */           this.stringCache.remove(stringCharArray);
/*  857 */           this.UTF8Cache.remove(stringCharArray);
/*  858 */           return 0;
/*      */         }
/*  860 */         this.poolContent[(lengthOffset++)] = (byte)(length >> 8);
/*  861 */         this.poolContent[lengthOffset] = (byte)length;
/*      */       }
/*  863 */       this.poolContent[(stringIndexOffset++)] = (byte)(stringIndex >> 8);
/*  864 */       this.poolContent[stringIndexOffset] = (byte)stringIndex;
/*      */     }
/*  866 */     return index;
/*      */   }
/*      */ 
/*      */   private int putInNameAndTypeCacheIfAbsent(char[] key1, char[] key2, int value)
/*      */   {
/*  876 */     Object key1Value = this.nameAndTypeCacheForFieldsAndMethods.get(key1);
/*      */     int index;
/*  877 */     if (key1Value == null) {
/*  878 */       CachedIndexEntry cachedIndexEntry = new CachedIndexEntry(key2, value);
/*  879 */       int index = -value;
/*  880 */       this.nameAndTypeCacheForFieldsAndMethods.put(key1, cachedIndexEntry);
/*  881 */     } else if ((key1Value instanceof CachedIndexEntry))
/*      */     {
/*  883 */       CachedIndexEntry entry = (CachedIndexEntry)key1Value;
/*      */       int index;
/*  884 */       if (CharOperation.equals(key2, entry.signature)) {
/*  885 */         index = entry.index;
/*      */       } else {
/*  887 */         CharArrayCache charArrayCache = new CharArrayCache();
/*  888 */         charArrayCache.putIfAbsent(entry.signature, entry.index);
/*  889 */         int index = charArrayCache.putIfAbsent(key2, value);
/*  890 */         this.nameAndTypeCacheForFieldsAndMethods.put(key1, charArrayCache);
/*      */       }
/*      */     } else {
/*  893 */       CharArrayCache charArrayCache = (CharArrayCache)key1Value;
/*  894 */       index = charArrayCache.putIfAbsent(key2, value);
/*      */     }
/*  896 */     return index;
/*      */   }
/*      */ 
/*      */   private int putInCacheIfAbsent(char[] key1, char[] key2, char[] key3, int value)
/*      */   {
/*  907 */     HashtableOfObject key1Value = (HashtableOfObject)this.methodsAndFieldsCache.get(key1);
/*      */     int index;
/*  908 */     if (key1Value == null) {
/*  909 */       key1Value = new HashtableOfObject();
/*  910 */       this.methodsAndFieldsCache.put(key1, key1Value);
/*  911 */       CachedIndexEntry cachedIndexEntry = new CachedIndexEntry(key3, value);
/*  912 */       int index = -value;
/*  913 */       key1Value.put(key2, cachedIndexEntry);
/*      */     } else {
/*  915 */       Object key2Value = key1Value.get(key2);
/*  916 */       if (key2Value == null) {
/*  917 */         CachedIndexEntry cachedIndexEntry = new CachedIndexEntry(key3, value);
/*  918 */         int index = -value;
/*  919 */         key1Value.put(key2, cachedIndexEntry);
/*  920 */       } else if ((key2Value instanceof CachedIndexEntry))
/*      */       {
/*  922 */         CachedIndexEntry entry = (CachedIndexEntry)key2Value;
/*      */         int index;
/*  923 */         if (CharOperation.equals(key3, entry.signature)) {
/*  924 */           index = entry.index;
/*      */         } else {
/*  926 */           CharArrayCache charArrayCache = new CharArrayCache();
/*  927 */           charArrayCache.putIfAbsent(entry.signature, entry.index);
/*  928 */           int index = charArrayCache.putIfAbsent(key3, value);
/*  929 */           key1Value.put(key2, charArrayCache);
/*      */         }
/*      */       } else {
/*  932 */         CharArrayCache charArrayCache = (CharArrayCache)key2Value;
/*  933 */         index = charArrayCache.putIfAbsent(key3, value);
/*      */       }
/*      */     }
/*  936 */     return index;
/*      */   }
/*      */ 
/*      */   public void resetForClinit(int constantPoolIndex, int constantPoolOffset)
/*      */   {
/*  945 */     this.currentIndex = constantPoolIndex;
/*  946 */     this.currentOffset = constantPoolOffset;
/*  947 */     if (this.UTF8Cache.get(AttributeNamesConstants.CodeName) >= constantPoolIndex) {
/*  948 */       this.UTF8Cache.remove(AttributeNamesConstants.CodeName);
/*      */     }
/*  950 */     if (this.UTF8Cache.get(ClinitSignature) >= constantPoolIndex) {
/*  951 */       this.UTF8Cache.remove(ClinitSignature);
/*      */     }
/*  953 */     if (this.UTF8Cache.get(Clinit) >= constantPoolIndex)
/*  954 */       this.UTF8Cache.remove(Clinit);
/*      */   }
/*      */ 
/*      */   private final void resizePoolContents(int minimalSize)
/*      */   {
/*  962 */     int length = this.poolContent.length;
/*  963 */     int toAdd = length;
/*  964 */     if (toAdd < minimalSize)
/*  965 */       toAdd = minimalSize;
/*  966 */     System.arraycopy(this.poolContent, 0, this.poolContent = new byte[length + toAdd], 0, length);
/*      */   }
/*      */ 
/*      */   protected final void writeU1(int value)
/*      */   {
/*  974 */     if (this.currentOffset + 1 >= this.poolContent.length) {
/*  975 */       resizePoolContents(1);
/*      */     }
/*  977 */     this.poolContent[(this.currentOffset++)] = (byte)value;
/*      */   }
/*      */ 
/*      */   protected final void writeU2(int value)
/*      */   {
/*  985 */     if (this.currentOffset + 2 >= this.poolContent.length) {
/*  986 */       resizePoolContents(2);
/*      */     }
/*  988 */     this.poolContent[(this.currentOffset++)] = (byte)(value >>> 8);
/*  989 */     this.poolContent[(this.currentOffset++)] = (byte)value;
/*      */   }
/*      */   public void reset() {
/*  992 */     if (this.doubleCache != null) this.doubleCache.clear();
/*  993 */     if (this.floatCache != null) this.floatCache.clear();
/*  994 */     if (this.intCache != null) this.intCache.clear();
/*  995 */     if (this.longCache != null) this.longCache.clear();
/*  996 */     this.UTF8Cache.clear();
/*  997 */     this.stringCache.clear();
/*  998 */     this.methodsAndFieldsCache.clear();
/*  999 */     this.classCache.clear();
/* 1000 */     this.nameAndTypeCacheForFieldsAndMethods.clear();
/* 1001 */     this.currentIndex = 1;
/* 1002 */     this.currentOffset = 0;
/*      */   }
/*      */   public void resetForAttributeName(char[] attributeName, int constantPoolIndex, int constantPoolOffset) {
/* 1005 */     this.currentIndex = constantPoolIndex;
/* 1006 */     this.currentOffset = constantPoolOffset;
/* 1007 */     if (this.UTF8Cache.get(attributeName) >= constantPoolIndex)
/* 1008 */       this.UTF8Cache.remove(attributeName);
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.codegen.ConstantPool
 * JD-Core Version:    0.6.0
 */