/*     */ package org.eclipse.jdt.internal.compiler.lookup;
/*     */ 
/*     */ public abstract interface TypeConstants
/*     */ {
/*  16 */   public static final char[] JAVA = "java".toCharArray();
/*  17 */   public static final char[] LANG = "lang".toCharArray();
/*  18 */   public static final char[] IO = "io".toCharArray();
/*  19 */   public static final char[] UTIL = "util".toCharArray();
/*  20 */   public static final char[] ANNOTATION = "annotation".toCharArray();
/*  21 */   public static final char[] REFLECT = "reflect".toCharArray();
/*  22 */   public static final char[] LENGTH = "length".toCharArray();
/*  23 */   public static final char[] CLONE = "clone".toCharArray();
/*  24 */   public static final char[] EQUALS = "equals".toCharArray();
/*  25 */   public static final char[] GETCLASS = "getClass".toCharArray();
/*  26 */   public static final char[] HASHCODE = "hashCode".toCharArray();
/*  27 */   public static final char[] OBJECT = "Object".toCharArray();
/*  28 */   public static final char[] MAIN = "main".toCharArray();
/*  29 */   public static final char[] SERIALVERSIONUID = "serialVersionUID".toCharArray();
/*  30 */   public static final char[] SERIALPERSISTENTFIELDS = "serialPersistentFields".toCharArray();
/*  31 */   public static final char[] READRESOLVE = "readResolve".toCharArray();
/*  32 */   public static final char[] WRITEREPLACE = "writeReplace".toCharArray();
/*  33 */   public static final char[] READOBJECT = "readObject".toCharArray();
/*  34 */   public static final char[] WRITEOBJECT = "writeObject".toCharArray();
/*  35 */   public static final char[] CharArray_JAVA_LANG_OBJECT = "java.lang.Object".toCharArray();
/*  36 */   public static final char[] CharArray_JAVA_LANG_ENUM = "java.lang.Enum".toCharArray();
/*  37 */   public static final char[] CharArray_JAVA_LANG_ANNOTATION_ANNOTATION = "java.lang.annotation.Annotation".toCharArray();
/*  38 */   public static final char[] CharArray_JAVA_IO_OBJECTINPUTSTREAM = "java.io.ObjectInputStream".toCharArray();
/*  39 */   public static final char[] CharArray_JAVA_IO_OBJECTOUTPUTSTREAM = "java.io.ObjectOutputStream".toCharArray();
/*  40 */   public static final char[] CharArray_JAVA_IO_OBJECTSTREAMFIELD = "java.io.ObjectStreamField".toCharArray();
/*  41 */   public static final char[] ANONYM_PREFIX = "new ".toCharArray();
/*  42 */   public static final char[] ANONYM_SUFFIX = "(){}".toCharArray();
/*  43 */   public static final char[] WILDCARD_NAME = { '?' };
/*  44 */   public static final char[] WILDCARD_SUPER = " super ".toCharArray();
/*  45 */   public static final char[] WILDCARD_EXTENDS = " extends ".toCharArray();
/*  46 */   public static final char[] WILDCARD_MINUS = { '-' };
/*  47 */   public static final char[] WILDCARD_STAR = { '*' };
/*  48 */   public static final char[] WILDCARD_PLUS = { '+' };
/*  49 */   public static final char[] WILDCARD_CAPTURE_NAME_PREFIX = "capture#".toCharArray();
/*  50 */   public static final char[] WILDCARD_CAPTURE_NAME_SUFFIX = "-of ".toCharArray();
/*  51 */   public static final char[] WILDCARD_CAPTURE = { '!' };
/*  52 */   public static final char[] BYTE = "byte".toCharArray();
/*  53 */   public static final char[] SHORT = "short".toCharArray();
/*  54 */   public static final char[] INT = "int".toCharArray();
/*  55 */   public static final char[] LONG = "long".toCharArray();
/*  56 */   public static final char[] FLOAT = "float".toCharArray();
/*  57 */   public static final char[] DOUBLE = "double".toCharArray();
/*  58 */   public static final char[] CHAR = "char".toCharArray();
/*  59 */   public static final char[] BOOLEAN = "boolean".toCharArray();
/*  60 */   public static final char[] NULL = "null".toCharArray();
/*  61 */   public static final char[] VOID = "void".toCharArray();
/*  62 */   public static final char[] VALUE = "value".toCharArray();
/*  63 */   public static final char[] VALUES = "values".toCharArray();
/*  64 */   public static final char[] VALUEOF = "valueOf".toCharArray();
/*  65 */   public static final char[] UPPER_SOURCE = "SOURCE".toCharArray();
/*  66 */   public static final char[] UPPER_CLASS = "CLASS".toCharArray();
/*  67 */   public static final char[] UPPER_RUNTIME = "RUNTIME".toCharArray();
/*  68 */   public static final char[] ANNOTATION_PREFIX = "@".toCharArray();
/*  69 */   public static final char[] ANNOTATION_SUFFIX = "()".toCharArray();
/*  70 */   public static final char[] TYPE = "TYPE".toCharArray();
/*  71 */   public static final char[] UPPER_FIELD = "FIELD".toCharArray();
/*  72 */   public static final char[] UPPER_METHOD = "METHOD".toCharArray();
/*  73 */   public static final char[] UPPER_PARAMETER = "PARAMETER".toCharArray();
/*  74 */   public static final char[] UPPER_CONSTRUCTOR = "CONSTRUCTOR".toCharArray();
/*  75 */   public static final char[] UPPER_LOCAL_VARIABLE = "LOCAL_VARIABLE".toCharArray();
/*  76 */   public static final char[] UPPER_ANNOTATION_TYPE = "ANNOTATION_TYPE".toCharArray();
/*  77 */   public static final char[] UPPER_PACKAGE = "PACKAGE".toCharArray();
/*     */ 
/*  80 */   public static final char[][] JAVA_LANG = { JAVA, LANG };
/*  81 */   public static final char[][] JAVA_IO = { JAVA, IO };
/*  82 */   public static final char[][] JAVA_LANG_ANNOTATION_ANNOTATION = { JAVA, LANG, ANNOTATION, "Annotation".toCharArray() };
/*  83 */   public static final char[][] JAVA_LANG_ASSERTIONERROR = { JAVA, LANG, "AssertionError".toCharArray() };
/*  84 */   public static final char[][] JAVA_LANG_CLASS = { JAVA, LANG, "Class".toCharArray() };
/*  85 */   public static final char[][] JAVA_LANG_CLASSNOTFOUNDEXCEPTION = { JAVA, LANG, "ClassNotFoundException".toCharArray() };
/*  86 */   public static final char[][] JAVA_LANG_CLONEABLE = { JAVA, LANG, "Cloneable".toCharArray() };
/*  87 */   public static final char[][] JAVA_LANG_ENUM = { JAVA, LANG, "Enum".toCharArray() };
/*  88 */   public static final char[][] JAVA_LANG_EXCEPTION = { JAVA, LANG, "Exception".toCharArray() };
/*  89 */   public static final char[][] JAVA_LANG_ERROR = { JAVA, LANG, "Error".toCharArray() };
/*  90 */   public static final char[][] JAVA_LANG_ILLEGALARGUMENTEXCEPTION = { JAVA, LANG, "IllegalArgumentException".toCharArray() };
/*  91 */   public static final char[][] JAVA_LANG_ITERABLE = { JAVA, LANG, "Iterable".toCharArray() };
/*  92 */   public static final char[][] JAVA_LANG_NOCLASSDEFERROR = { JAVA, LANG, "NoClassDefError".toCharArray() };
/*  93 */   public static final char[][] JAVA_LANG_OBJECT = { JAVA, LANG, OBJECT };
/*  94 */   public static final char[][] JAVA_LANG_STRING = { JAVA, LANG, "String".toCharArray() };
/*  95 */   public static final char[][] JAVA_LANG_STRINGBUFFER = { JAVA, LANG, "StringBuffer".toCharArray() };
/*  96 */   public static final char[][] JAVA_LANG_STRINGBUILDER = { JAVA, LANG, "StringBuilder".toCharArray() };
/*  97 */   public static final char[][] JAVA_LANG_SYSTEM = { JAVA, LANG, "System".toCharArray() };
/*  98 */   public static final char[][] JAVA_LANG_RUNTIMEEXCEPTION = { JAVA, LANG, "RuntimeException".toCharArray() };
/*  99 */   public static final char[][] JAVA_LANG_THROWABLE = { JAVA, LANG, "Throwable".toCharArray() };
/* 100 */   public static final char[][] JAVA_LANG_REFLECT_CONSTRUCTOR = { JAVA, LANG, REFLECT, "Constructor".toCharArray() };
/* 101 */   public static final char[][] JAVA_IO_PRINTSTREAM = { JAVA, IO, "PrintStream".toCharArray() };
/* 102 */   public static final char[][] JAVA_IO_SERIALIZABLE = { JAVA, IO, "Serializable".toCharArray() };
/* 103 */   public static final char[][] JAVA_LANG_BYTE = { JAVA, LANG, "Byte".toCharArray() };
/* 104 */   public static final char[][] JAVA_LANG_SHORT = { JAVA, LANG, "Short".toCharArray() };
/* 105 */   public static final char[][] JAVA_LANG_CHARACTER = { JAVA, LANG, "Character".toCharArray() };
/* 106 */   public static final char[][] JAVA_LANG_INTEGER = { JAVA, LANG, "Integer".toCharArray() };
/* 107 */   public static final char[][] JAVA_LANG_LONG = { JAVA, LANG, "Long".toCharArray() };
/* 108 */   public static final char[][] JAVA_LANG_FLOAT = { JAVA, LANG, "Float".toCharArray() };
/* 109 */   public static final char[][] JAVA_LANG_DOUBLE = { JAVA, LANG, "Double".toCharArray() };
/* 110 */   public static final char[][] JAVA_LANG_BOOLEAN = { JAVA, LANG, "Boolean".toCharArray() };
/* 111 */   public static final char[][] JAVA_LANG_VOID = { JAVA, LANG, "Void".toCharArray() };
/* 112 */   public static final char[][] JAVA_UTIL_COLLECTION = { JAVA, UTIL, "Collection".toCharArray() };
/* 113 */   public static final char[][] JAVA_UTIL_ITERATOR = { JAVA, UTIL, "Iterator".toCharArray() };
/* 114 */   public static final char[][] JAVA_LANG_DEPRECATED = { JAVA, LANG, "Deprecated".toCharArray() };
/* 115 */   public static final char[][] JAVA_LANG_ANNOTATION_DOCUMENTED = { JAVA, LANG, ANNOTATION, "Documented".toCharArray() };
/* 116 */   public static final char[][] JAVA_LANG_ANNOTATION_INHERITED = { JAVA, LANG, ANNOTATION, "Inherited".toCharArray() };
/* 117 */   public static final char[][] JAVA_LANG_OVERRIDE = { JAVA, LANG, "Override".toCharArray() };
/* 118 */   public static final char[][] JAVA_LANG_ANNOTATION_RETENTION = { JAVA, LANG, ANNOTATION, "Retention".toCharArray() };
/* 119 */   public static final char[][] JAVA_LANG_SUPPRESSWARNINGS = { JAVA, LANG, "SuppressWarnings".toCharArray() };
/* 120 */   public static final char[][] JAVA_LANG_ANNOTATION_TARGET = { JAVA, LANG, ANNOTATION, "Target".toCharArray() };
/* 121 */   public static final char[][] JAVA_LANG_ANNOTATION_RETENTIONPOLICY = { JAVA, LANG, ANNOTATION, "RetentionPolicy".toCharArray() };
/* 122 */   public static final char[][] JAVA_LANG_ANNOTATION_ELEMENTTYPE = { JAVA, LANG, ANNOTATION, "ElementType".toCharArray() };
/* 123 */   public static final char[][] JAVA_LANG_REFLECT_FIELD = { JAVA, LANG, REFLECT, "Field".toCharArray() };
/* 124 */   public static final char[][] JAVA_LANG_REFLECT_METHOD = { JAVA, LANG, REFLECT, "Method".toCharArray() };
/* 125 */   public static final char[][] JAVA_IO_OBJECTSTREAMEXCEPTION = { JAVA, IO, "ObjectStreamException".toCharArray() };
/* 126 */   public static final char[][] JAVA_IO_EXTERNALIZABLE = { JAVA, IO, "Externalizable".toCharArray() };
/* 127 */   public static final char[][] JAVA_IO_IOEXCEPTION = { JAVA, IO, "IOException".toCharArray() };
/* 128 */   public static final char[][] JAVA_IO_OBJECTOUTPUTSTREAM = { JAVA, IO, "ObjectOutputStream".toCharArray() };
/* 129 */   public static final char[][] JAVA_IO_OBJECTINPUTSTREAM = { JAVA, IO, "ObjectInputStream".toCharArray() };
/*     */ 
/* 131 */   public static final char[][] JAVAX_RMI_CORBA_STUB = { 
/* 132 */     "javax".toCharArray(), 
/* 133 */     "rmi".toCharArray(), 
/* 134 */     "CORBA".toCharArray(), 
/* 135 */     "Stub".toCharArray() };
/*     */   public static final int CONSTRAINT_EQUAL = 0;
/*     */   public static final int CONSTRAINT_EXTENDS = 1;
/*     */   public static final int CONSTRAINT_SUPER = 2;
/*     */   public static final int OK = 0;
/*     */   public static final int UNCHECKED = 1;
/*     */   public static final int MISMATCH = 2;
/* 149 */   public static final char[] INIT = "<init>".toCharArray();
/* 150 */   public static final char[] CLINIT = "<clinit>".toCharArray();
/* 151 */   public static final char[] SYNTHETIC_SWITCH_ENUM_TABLE = "$SWITCH_TABLE$".toCharArray();
/* 152 */   public static final char[] SYNTHETIC_ENUM_VALUES = "ENUM$VALUES".toCharArray();
/* 153 */   public static final char[] SYNTHETIC_ASSERT_DISABLED = "$assertionsDisabled".toCharArray();
/* 154 */   public static final char[] SYNTHETIC_CLASS = "class$".toCharArray();
/* 155 */   public static final char[] SYNTHETIC_OUTER_LOCAL_PREFIX = "val$".toCharArray();
/* 156 */   public static final char[] SYNTHETIC_ENCLOSING_INSTANCE_PREFIX = "this$".toCharArray();
/* 157 */   public static final char[] SYNTHETIC_ACCESS_METHOD_PREFIX = "access$".toCharArray();
/*     */ 
/* 160 */   public static final char[] PACKAGE_INFO_NAME = "package-info".toCharArray();
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.TypeConstants
 * JD-Core Version:    0.6.0
 */