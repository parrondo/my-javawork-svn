/*     */ package org.eclipse.jdt.internal.compiler.codegen;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ 
/*     */ public class VerificationTypeInfo
/*     */ {
/*     */   public static final int ITEM_TOP = 0;
/*     */   public static final int ITEM_INTEGER = 1;
/*     */   public static final int ITEM_FLOAT = 2;
/*     */   public static final int ITEM_DOUBLE = 3;
/*     */   public static final int ITEM_LONG = 4;
/*     */   public static final int ITEM_NULL = 5;
/*     */   public static final int ITEM_UNINITIALIZED_THIS = 6;
/*     */   public static final int ITEM_OBJECT = 7;
/*     */   public static final int ITEM_UNINITIALIZED = 8;
/*     */   public int tag;
/*     */   private int id;
/*     */   private char[] constantPoolName;
/*     */   public int offset;
/*     */ 
/*     */   private VerificationTypeInfo()
/*     */   {
/*     */   }
/*     */ 
/*     */   public VerificationTypeInfo(int id, char[] constantPoolName)
/*     */   {
/*  73 */     this(id, 7, constantPoolName);
/*     */   }
/*     */   public VerificationTypeInfo(int id, int tag, char[] constantPoolName) {
/*  76 */     this.id = id;
/*  77 */     this.tag = tag;
/*  78 */     this.constantPoolName = constantPoolName;
/*     */   }
/*     */   public VerificationTypeInfo(int tag, TypeBinding binding) {
/*  81 */     this(binding);
/*  82 */     this.tag = tag;
/*     */   }
/*     */   public VerificationTypeInfo(TypeBinding binding) {
/*  85 */     this.id = binding.id;
/*  86 */     switch (binding.id) {
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/*     */     case 10:
/*  92 */       this.tag = 1;
/*  93 */       break;
/*     */     case 9:
/*  95 */       this.tag = 2;
/*  96 */       break;
/*     */     case 7:
/*  98 */       this.tag = 4;
/*  99 */       break;
/*     */     case 8:
/* 101 */       this.tag = 3;
/* 102 */       break;
/*     */     case 12:
/* 104 */       this.tag = 5;
/* 105 */       break;
/*     */     case 6:
/*     */     case 11:
/*     */     default:
/* 107 */       this.tag = 7;
/* 108 */       this.constantPoolName = binding.constantPoolName();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setBinding(TypeBinding binding) {
/* 112 */     this.constantPoolName = binding.constantPoolName();
/* 113 */     int typeBindingId = binding.id;
/* 114 */     this.id = typeBindingId;
/* 115 */     switch (typeBindingId) {
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/*     */     case 10:
/* 121 */       this.tag = 1;
/* 122 */       break;
/*     */     case 9:
/* 124 */       this.tag = 2;
/* 125 */       break;
/*     */     case 7:
/* 127 */       this.tag = 4;
/* 128 */       break;
/*     */     case 8:
/* 130 */       this.tag = 3;
/* 131 */       break;
/*     */     case 12:
/* 133 */       this.tag = 5;
/* 134 */       break;
/*     */     case 6:
/*     */     case 11:
/*     */     default:
/* 136 */       this.tag = 7;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int id() {
/* 140 */     return this.id;
/*     */   }
/*     */   public String toString() {
/* 143 */     StringBuffer buffer = new StringBuffer();
/* 144 */     switch (this.tag) {
/*     */     case 6:
/* 146 */       buffer.append("uninitialized_this(").append(readableName()).append(")");
/* 147 */       break;
/*     */     case 8:
/* 149 */       buffer.append("uninitialized(").append(readableName()).append(")");
/* 150 */       break;
/*     */     case 7:
/* 152 */       buffer.append(readableName());
/* 153 */       break;
/*     */     case 3:
/* 155 */       buffer.append('D');
/* 156 */       break;
/*     */     case 2:
/* 158 */       buffer.append('F');
/* 159 */       break;
/*     */     case 1:
/* 161 */       buffer.append('I');
/* 162 */       break;
/*     */     case 4:
/* 164 */       buffer.append('J');
/* 165 */       break;
/*     */     case 5:
/* 167 */       buffer.append("null");
/* 168 */       break;
/*     */     case 0:
/* 170 */       buffer.append("top");
/*     */     }
/*     */ 
/* 173 */     return String.valueOf(buffer);
/*     */   }
/*     */   public VerificationTypeInfo duplicate() {
/* 176 */     VerificationTypeInfo verificationTypeInfo = new VerificationTypeInfo();
/* 177 */     verificationTypeInfo.id = this.id;
/* 178 */     verificationTypeInfo.tag = this.tag;
/* 179 */     verificationTypeInfo.constantPoolName = this.constantPoolName;
/* 180 */     verificationTypeInfo.offset = this.offset;
/* 181 */     return verificationTypeInfo;
/*     */   }
/*     */   public boolean equals(Object obj) {
/* 184 */     if ((obj instanceof VerificationTypeInfo)) {
/* 185 */       VerificationTypeInfo info1 = (VerificationTypeInfo)obj;
/* 186 */       return (info1.tag == this.tag) && (CharOperation.equals(info1.constantPoolName(), constantPoolName()));
/*     */     }
/* 188 */     return false;
/*     */   }
/*     */   public int hashCode() {
/* 191 */     return this.tag + this.id + this.constantPoolName.length + this.offset;
/*     */   }
/*     */   public char[] constantPoolName() {
/* 194 */     return this.constantPoolName;
/*     */   }
/*     */   public char[] readableName() {
/* 197 */     return this.constantPoolName;
/*     */   }
/*     */   public void replaceWithElementType() {
/* 200 */     if (this.constantPoolName[1] == 'L') {
/* 201 */       this.constantPoolName = CharOperation.subarray(this.constantPoolName, 2, this.constantPoolName.length - 1);
/*     */     } else {
/* 203 */       this.constantPoolName = CharOperation.subarray(this.constantPoolName, 1, this.constantPoolName.length);
/* 204 */       if (this.constantPoolName.length == 1)
/* 205 */         switch (this.constantPoolName[0]) {
/*     */         case 'I':
/* 207 */           this.id = 10;
/* 208 */           break;
/*     */         case 'B':
/* 210 */           this.id = 3;
/* 211 */           break;
/*     */         case 'S':
/* 213 */           this.id = 4;
/* 214 */           break;
/*     */         case 'C':
/* 216 */           this.id = 2;
/* 217 */           break;
/*     */         case 'J':
/* 219 */           this.id = 7;
/* 220 */           break;
/*     */         case 'F':
/* 222 */           this.id = 9;
/* 223 */           break;
/*     */         case 'D':
/* 225 */           this.id = 8;
/* 226 */           break;
/*     */         case 'Z':
/* 228 */           this.id = 5;
/* 229 */           break;
/*     */         case 'N':
/* 231 */           this.id = 12;
/* 232 */           break;
/*     */         case 'V':
/* 234 */           this.id = 6;
/*     */         case 'E':
/*     */         case 'G':
/*     */         case 'H':
/*     */         case 'K':
/*     */         case 'L':
/*     */         case 'M':
/*     */         case 'O':
/*     */         case 'P':
/*     */         case 'Q':
/*     */         case 'R':
/*     */         case 'T':
/*     */         case 'U':
/*     */         case 'W':
/*     */         case 'X':
/*     */         case 'Y':
/*     */         }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.codegen.VerificationTypeInfo
 * JD-Core Version:    0.6.0
 */