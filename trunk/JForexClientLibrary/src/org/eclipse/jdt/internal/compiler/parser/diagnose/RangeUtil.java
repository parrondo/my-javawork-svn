/*     */ package org.eclipse.jdt.internal.compiler.parser.diagnose;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Initializer;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ 
/*     */ public class RangeUtil
/*     */ {
/*     */   public static final int NO_FLAG = 0;
/*     */   public static final int LBRACE_MISSING = 1;
/*     */   public static final int IGNORE = 2;
/*     */ 
/*     */   public static boolean containsErrorInSignature(AbstractMethodDeclaration method)
/*     */   {
/* 117 */     return (method.sourceEnd + 1 == method.bodyStart) || (method.bodyEnd == method.declarationSourceEnd);
/*     */   }
/*     */ 
/*     */   public static int[][] computeDietRange(TypeDeclaration[] types) {
/* 121 */     if ((types == null) || (types.length == 0)) {
/* 122 */       return new int[3][0];
/*     */     }
/* 124 */     RangeResult result = new RangeResult();
/* 125 */     computeDietRange0(types, result);
/* 126 */     return result.getRanges();
/*     */   }
/*     */ 
/*     */   private static void computeDietRange0(TypeDeclaration[] types, RangeResult result)
/*     */   {
/* 131 */     for (int j = 0; j < types.length; j++)
/*     */     {
/* 133 */       TypeDeclaration[] memberTypeDeclarations = types[j].memberTypes;
/* 134 */       if ((memberTypeDeclarations != null) && (memberTypeDeclarations.length > 0)) {
/* 135 */         computeDietRange0(types[j].memberTypes, result);
/*     */       }
/*     */ 
/* 138 */       AbstractMethodDeclaration[] methods = types[j].methods;
/* 139 */       if (methods != null) {
/* 140 */         int length = methods.length;
/* 141 */         for (int i = 0; i < length; i++) {
/* 142 */           AbstractMethodDeclaration method = methods[i];
/* 143 */           if (containsIgnoredBody(method)) {
/* 144 */             if (containsErrorInSignature(method)) {
/* 145 */               method.bits |= 32;
/* 146 */               result.addInterval(method.declarationSourceStart, method.declarationSourceEnd, 2);
/*     */             } else {
/* 148 */               int flags = method.sourceEnd + 1 == method.bodyStart ? 1 : 0;
/* 149 */               result.addInterval(method.bodyStart, method.bodyEnd, flags);
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 156 */       FieldDeclaration[] fields = types[j].fields;
/* 157 */       if (fields != null) {
/* 158 */         int length = fields.length;
/* 159 */         for (int i = 0; i < length; i++)
/* 160 */           if ((fields[i] instanceof Initializer)) {
/* 161 */             Initializer initializer = (Initializer)fields[i];
/* 162 */             if ((initializer.declarationSourceEnd == initializer.bodyEnd) && (initializer.declarationSourceStart != initializer.declarationSourceEnd)) {
/* 163 */               initializer.bits |= 32;
/* 164 */               result.addInterval(initializer.declarationSourceStart, initializer.declarationSourceEnd, 2);
/*     */             } else {
/* 166 */               result.addInterval(initializer.bodyStart, initializer.bodyEnd);
/*     */             }
/*     */           }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean containsIgnoredBody(AbstractMethodDeclaration method)
/*     */   {
/* 177 */     return (!method.isDefaultConstructor()) && 
/* 176 */       (!method.isClinit()) && 
/* 177 */       ((method.modifiers & 0x1000000) == 0);
/*     */   }
/*     */ 
/*     */   static class RangeResult
/*     */   {
/*     */     private static final int INITIAL_SIZE = 10;
/*     */     int pos;
/*     */     int[] intervalStarts;
/*     */     int[] intervalEnds;
/*     */     int[] intervalFlags;
/*     */ 
/*     */     RangeResult()
/*     */     {
/*  35 */       this.pos = 0;
/*  36 */       this.intervalStarts = new int[10];
/*  37 */       this.intervalEnds = new int[10];
/*  38 */       this.intervalFlags = new int[10];
/*     */     }
/*     */ 
/*     */     void addInterval(int start, int end) {
/*  42 */       addInterval(start, end, 0);
/*     */     }
/*     */ 
/*     */     void addInterval(int start, int end, int flags) {
/*  46 */       if (this.pos >= this.intervalStarts.length) {
/*  47 */         System.arraycopy(this.intervalStarts, 0, this.intervalStarts = new int[this.pos * 2], 0, this.pos);
/*  48 */         System.arraycopy(this.intervalEnds, 0, this.intervalEnds = new int[this.pos * 2], 0, this.pos);
/*  49 */         System.arraycopy(this.intervalFlags, 0, this.intervalFlags = new int[this.pos * 2], 0, this.pos);
/*     */       }
/*  51 */       this.intervalStarts[this.pos] = start;
/*  52 */       this.intervalEnds[this.pos] = end;
/*  53 */       this.intervalFlags[this.pos] = flags;
/*  54 */       this.pos += 1;
/*     */     }
/*     */ 
/*     */     int[][] getRanges() {
/*  58 */       int[] resultStarts = new int[this.pos];
/*  59 */       int[] resultEnds = new int[this.pos];
/*  60 */       int[] resultFlags = new int[this.pos];
/*     */ 
/*  62 */       System.arraycopy(this.intervalStarts, 0, resultStarts, 0, this.pos);
/*  63 */       System.arraycopy(this.intervalEnds, 0, resultEnds, 0, this.pos);
/*  64 */       System.arraycopy(this.intervalFlags, 0, resultFlags, 0, this.pos);
/*     */ 
/*  66 */       if (resultStarts.length > 1) {
/*  67 */         quickSort(resultStarts, resultEnds, resultFlags, 0, resultStarts.length - 1);
/*     */       }
/*  69 */       return new int[][] { resultStarts, resultEnds, resultFlags };
/*     */     }
/*     */ 
/*     */     private void quickSort(int[] list, int[] list2, int[] list3, int left, int right) {
/*  73 */       int original_left = left;
/*  74 */       int original_right = right;
/*  75 */       int mid = list[(left + (right - left) / 2)];
/*     */       do {
/*  77 */         while (compare(list[left], mid) < 0) {
/*  78 */           left++;
/*     */         }
/*  80 */         while (compare(mid, list[right]) < 0) {
/*  81 */           right--;
/*     */         }
/*  83 */         if (left <= right) {
/*  84 */           int tmp = list[left];
/*  85 */           list[left] = list[right];
/*  86 */           list[right] = tmp;
/*     */ 
/*  88 */           tmp = list2[left];
/*  89 */           list2[left] = list2[right];
/*  90 */           list2[right] = tmp;
/*     */ 
/*  92 */           tmp = list3[left];
/*  93 */           list3[left] = list3[right];
/*  94 */           list3[right] = tmp;
/*     */ 
/*  96 */           left++;
/*  97 */           right--;
/*     */         }
/*     */       }
/*  99 */       while (left <= right);
/*     */ 
/* 101 */       if (original_left < right) {
/* 102 */         quickSort(list, list2, list3, original_left, right);
/*     */       }
/* 104 */       if (left < original_right)
/* 105 */         quickSort(list, list2, list3, left, original_right);
/*     */     }
/*     */ 
/*     */     private int compare(int i1, int i2)
/*     */     {
/* 110 */       return i1 - i2;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.parser.diagnose.RangeUtil
 * JD-Core Version:    0.6.0
 */