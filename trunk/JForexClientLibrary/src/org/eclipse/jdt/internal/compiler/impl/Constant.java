/*      */ package org.eclipse.jdt.internal.compiler.impl;
/*      */ 
/*      */ import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
/*      */ import org.eclipse.jdt.internal.compiler.problem.ShouldNotImplement;
/*      */ import org.eclipse.jdt.internal.compiler.util.Messages;
/*      */ 
/*      */ public abstract class Constant
/*      */   implements TypeIds, OperatorIds
/*      */ {
/*   20 */   public static final Constant NotAConstant = DoubleConstant.fromValue((0.0D / 0.0D));
/*      */ 
/*      */   public boolean booleanValue() {
/*   23 */     throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[] { typeName(), "boolean" }));
/*      */   }
/*      */ 
/*      */   public byte byteValue() {
/*   27 */     throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[] { typeName(), "byte" }));
/*      */   }
/*      */ 
/*      */   public final Constant castTo(int conversionToTargetType)
/*      */   {
/*   35 */     if (this == NotAConstant) return NotAConstant;
/*   36 */     switch (conversionToTargetType) { case 0:
/*   37 */       return this;
/*      */     case 51:
/*   53 */       return this;
/*      */     case 55:
/*   54 */       return ByteConstant.fromValue((byte)(int)longValue());
/*      */     case 52:
/*   55 */       return ByteConstant.fromValue((byte)shortValue());
/*      */     case 56:
/*   59 */       return ByteConstant.fromValue((byte)(int)doubleValue());
/*      */     case 57:
/*   60 */       return ByteConstant.fromValue((byte)(int)floatValue());
/*      */     case 50:
/*   62 */       return ByteConstant.fromValue((byte)charValue());
/*      */     case 58:
/*   63 */       return ByteConstant.fromValue((byte)intValue());
/*      */     case 115:
/*   66 */       return LongConstant.fromValue(byteValue());
/*      */     case 119:
/*   67 */       return this;
/*      */     case 116:
/*   68 */       return LongConstant.fromValue(shortValue());
/*      */     case 120:
/*   72 */       return LongConstant.fromValue(()doubleValue());
/*      */     case 121:
/*   73 */       return LongConstant.fromValue(()floatValue());
/*      */     case 114:
/*   75 */       return LongConstant.fromValue(charValue());
/*      */     case 122:
/*   76 */       return LongConstant.fromValue(intValue());
/*      */     case 67:
/*   79 */       return ShortConstant.fromValue(byteValue());
/*      */     case 71:
/*   80 */       return ShortConstant.fromValue((short)(int)longValue());
/*      */     case 68:
/*   81 */       return this;
/*      */     case 72:
/*   85 */       return ShortConstant.fromValue((short)(int)doubleValue());
/*      */     case 73:
/*   86 */       return ShortConstant.fromValue((short)(int)floatValue());
/*      */     case 66:
/*   88 */       return ShortConstant.fromValue((short)charValue());
/*      */     case 74:
/*   89 */       return ShortConstant.fromValue((short)intValue());
/*      */     case 187:
/*  109 */       return this;
/*      */     case 131:
/*  131 */       return DoubleConstant.fromValue(byteValue());
/*      */     case 135:
/*  132 */       return DoubleConstant.fromValue(longValue());
/*      */     case 132:
/*  133 */       return DoubleConstant.fromValue(shortValue());
/*      */     case 136:
/*  137 */       return this;
/*      */     case 137:
/*  138 */       return DoubleConstant.fromValue(floatValue());
/*      */     case 130:
/*  140 */       return DoubleConstant.fromValue(charValue());
/*      */     case 138:
/*  141 */       return DoubleConstant.fromValue(intValue());
/*      */     case 147:
/*  144 */       return FloatConstant.fromValue(byteValue());
/*      */     case 151:
/*  145 */       return FloatConstant.fromValue((float)longValue());
/*      */     case 148:
/*  146 */       return FloatConstant.fromValue(shortValue());
/*      */     case 152:
/*  150 */       return FloatConstant.fromValue((float)doubleValue());
/*      */     case 153:
/*  151 */       return this;
/*      */     case 146:
/*  153 */       return FloatConstant.fromValue(charValue());
/*      */     case 154:
/*  154 */       return FloatConstant.fromValue(intValue());
/*      */     case 85:
/*  165 */       return this;
/*      */     case 35:
/*  170 */       return CharConstant.fromValue((char)byteValue());
/*      */     case 39:
/*  171 */       return CharConstant.fromValue((char)(int)longValue());
/*      */     case 36:
/*  172 */       return CharConstant.fromValue((char)shortValue());
/*      */     case 40:
/*  176 */       return CharConstant.fromValue((char)(int)doubleValue());
/*      */     case 41:
/*  177 */       return CharConstant.fromValue((char)(int)floatValue());
/*      */     case 34:
/*  179 */       return this;
/*      */     case 42:
/*  180 */       return CharConstant.fromValue((char)intValue());
/*      */     case 163:
/*  183 */       return IntConstant.fromValue(byteValue());
/*      */     case 167:
/*  184 */       return IntConstant.fromValue((int)longValue());
/*      */     case 164:
/*  185 */       return IntConstant.fromValue(shortValue());
/*      */     case 168:
/*  189 */       return IntConstant.fromValue((int)doubleValue());
/*      */     case 169:
/*  190 */       return IntConstant.fromValue((int)floatValue());
/*      */     case 162:
/*  192 */       return IntConstant.fromValue(charValue());
/*      */     case 170:
/*  193 */       return this;
/*      */     }
/*      */ 
/*  196 */     return NotAConstant;
/*      */   }
/*      */ 
/*      */   public char charValue() {
/*  200 */     throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[] { typeName(), "char" }));
/*      */   }
/*      */ 
/*      */   public static final Constant computeConstantOperation(Constant cst, int id, int operator) {
/*  204 */     switch (operator) {
/*      */     case 11:
/*  206 */       return BooleanConstant.fromValue(!cst.booleanValue());
/*      */     case 14:
/*  208 */       return computeConstantOperationPLUS(IntConstant.fromValue(0), 10, cst, id);
/*      */     case 13:
/*  210 */       switch (id)
/*      */       {
/*      */       case 9:
/*      */         float f;
/*  212 */         if ((f = cst.floatValue()) != 0.0F)
/*      */           break;
/*  214 */         if (Float.floatToIntBits(f) == 0) {
/*  215 */           return FloatConstant.fromValue(-0.0F);
/*      */         }
/*  217 */         return FloatConstant.fromValue(0.0F);
/*      */       case 8:
/*      */         double d;
/*  220 */         if ((d = cst.doubleValue()) != 0.0D)
/*      */           break;
/*  222 */         if (Double.doubleToLongBits(d) == 0L) {
/*  223 */           return DoubleConstant.fromValue(-0.0D);
/*      */         }
/*  225 */         return DoubleConstant.fromValue(0.0D);
/*      */       }
/*      */ 
/*  228 */       return computeConstantOperationMINUS(IntConstant.fromValue(0), 10, cst, id);
/*      */     case 12:
/*  230 */       switch (id) { case 2:
/*  231 */         return IntConstant.fromValue(cst.charValue() ^ 0xFFFFFFFF);
/*      */       case 3:
/*  232 */         return IntConstant.fromValue(cst.byteValue() ^ 0xFFFFFFFF);
/*      */       case 4:
/*  233 */         return IntConstant.fromValue(cst.shortValue() ^ 0xFFFFFFFF);
/*      */       case 10:
/*  234 */         return IntConstant.fromValue(cst.intValue() ^ 0xFFFFFFFF);
/*      */       case 7:
/*  235 */         return LongConstant.fromValue(cst.longValue() ^ 0xFFFFFFFF);
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/*  236 */       case 9: } return NotAConstant;
/*      */     }
/*  238 */     return NotAConstant;
/*      */   }
/*      */ 
/*      */   public static final Constant computeConstantOperation(Constant left, int leftId, int operator, Constant right, int rightId)
/*      */   {
/*  243 */     switch (operator) { case 2:
/*  244 */       return computeConstantOperationAND(left, leftId, right, rightId);
/*      */     case 0:
/*  245 */       return computeConstantOperationAND_AND(left, leftId, right, rightId);
/*      */     case 9:
/*  246 */       return computeConstantOperationDIVIDE(left, leftId, right, rightId);
/*      */     case 6:
/*  247 */       return computeConstantOperationGREATER(left, leftId, right, rightId);
/*      */     case 7:
/*  248 */       return computeConstantOperationGREATER_EQUAL(left, leftId, right, rightId);
/*      */     case 10:
/*  249 */       return computeConstantOperationLEFT_SHIFT(left, leftId, right, rightId);
/*      */     case 4:
/*  250 */       return computeConstantOperationLESS(left, leftId, right, rightId);
/*      */     case 5:
/*  251 */       return computeConstantOperationLESS_EQUAL(left, leftId, right, rightId);
/*      */     case 13:
/*  252 */       return computeConstantOperationMINUS(left, leftId, right, rightId);
/*      */     case 15:
/*  253 */       return computeConstantOperationMULTIPLY(left, leftId, right, rightId);
/*      */     case 3:
/*  254 */       return computeConstantOperationOR(left, leftId, right, rightId);
/*      */     case 1:
/*  255 */       return computeConstantOperationOR_OR(left, leftId, right, rightId);
/*      */     case 14:
/*  256 */       return computeConstantOperationPLUS(left, leftId, right, rightId);
/*      */     case 16:
/*  257 */       return computeConstantOperationREMAINDER(left, leftId, right, rightId);
/*      */     case 17:
/*  258 */       return computeConstantOperationRIGHT_SHIFT(left, leftId, right, rightId);
/*      */     case 19:
/*  259 */       return computeConstantOperationUNSIGNED_RIGHT_SHIFT(left, leftId, right, rightId);
/*      */     case 8:
/*  260 */       return computeConstantOperationXOR(left, leftId, right, rightId);
/*      */     case 11:
/*      */     case 12:
/*  261 */     case 18: } return NotAConstant;
/*      */   }
/*      */ 
/*      */   public static final Constant computeConstantOperationAND(Constant left, int leftId, Constant right, int rightId)
/*      */   {
/*  266 */     switch (leftId) { case 5:
/*  267 */       return BooleanConstant.fromValue(left.booleanValue() & right.booleanValue());
/*      */     case 2:
/*  269 */       switch (rightId) { case 2:
/*  270 */         return IntConstant.fromValue(left.charValue() & right.charValue());
/*      */       case 3:
/*  271 */         return IntConstant.fromValue(left.charValue() & right.byteValue());
/*      */       case 4:
/*  272 */         return IntConstant.fromValue(left.charValue() & right.shortValue());
/*      */       case 10:
/*  273 */         return IntConstant.fromValue(left.charValue() & right.intValue());
/*      */       case 7:
/*  274 */         return LongConstant.fromValue(left.charValue() & right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/*  276 */       case 9: } break;
/*      */     case 3:
/*  278 */       switch (rightId) { case 2:
/*  279 */         return IntConstant.fromValue(left.byteValue() & right.charValue());
/*      */       case 3:
/*  280 */         return IntConstant.fromValue(left.byteValue() & right.byteValue());
/*      */       case 4:
/*  281 */         return IntConstant.fromValue(left.byteValue() & right.shortValue());
/*      */       case 10:
/*  282 */         return IntConstant.fromValue(left.byteValue() & right.intValue());
/*      */       case 7:
/*  283 */         return LongConstant.fromValue(left.byteValue() & right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/*  285 */       case 9: } break;
/*      */     case 4:
/*  287 */       switch (rightId) { case 2:
/*  288 */         return IntConstant.fromValue(left.shortValue() & right.charValue());
/*      */       case 3:
/*  289 */         return IntConstant.fromValue(left.shortValue() & right.byteValue());
/*      */       case 4:
/*  290 */         return IntConstant.fromValue(left.shortValue() & right.shortValue());
/*      */       case 10:
/*  291 */         return IntConstant.fromValue(left.shortValue() & right.intValue());
/*      */       case 7:
/*  292 */         return LongConstant.fromValue(left.shortValue() & right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/*  294 */       case 9: } break;
/*      */     case 10:
/*  296 */       switch (rightId) { case 2:
/*  297 */         return IntConstant.fromValue(left.intValue() & right.charValue());
/*      */       case 3:
/*  298 */         return IntConstant.fromValue(left.intValue() & right.byteValue());
/*      */       case 4:
/*  299 */         return IntConstant.fromValue(left.intValue() & right.shortValue());
/*      */       case 10:
/*  300 */         return IntConstant.fromValue(left.intValue() & right.intValue());
/*      */       case 7:
/*  301 */         return LongConstant.fromValue(left.intValue() & right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/*  303 */       case 9: } break;
/*      */     case 7:
/*  305 */       switch (rightId) { case 2:
/*  306 */         return LongConstant.fromValue(left.longValue() & right.charValue());
/*      */       case 3:
/*  307 */         return LongConstant.fromValue(left.longValue() & right.byteValue());
/*      */       case 4:
/*  308 */         return LongConstant.fromValue(left.longValue() & right.shortValue());
/*      */       case 10:
/*  309 */         return LongConstant.fromValue(left.longValue() & right.intValue());
/*      */       case 7:
/*  310 */         return LongConstant.fromValue(left.longValue() & right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/*      */       case 9: } case 6:
/*      */     case 8:
/*  313 */     case 9: } return NotAConstant;
/*      */   }
/*      */ 
/*      */   public static final Constant computeConstantOperationAND_AND(Constant left, int leftId, Constant right, int rightId) {
/*  317 */     return BooleanConstant.fromValue((left.booleanValue()) && (right.booleanValue()));
/*      */   }
/*      */ 
/*      */   public static final Constant computeConstantOperationDIVIDE(Constant left, int leftId, Constant right, int rightId)
/*      */   {
/*  322 */     switch (leftId) {
/*      */     case 2:
/*  324 */       switch (rightId) { case 2:
/*  325 */         return IntConstant.fromValue(left.charValue() / right.charValue());
/*      */       case 9:
/*  326 */         return FloatConstant.fromValue(left.charValue() / right.floatValue());
/*      */       case 8:
/*  327 */         return DoubleConstant.fromValue(left.charValue() / right.doubleValue());
/*      */       case 3:
/*  328 */         return IntConstant.fromValue(left.charValue() / right.byteValue());
/*      */       case 4:
/*  329 */         return IntConstant.fromValue(left.charValue() / right.shortValue());
/*      */       case 10:
/*  330 */         return IntConstant.fromValue(left.charValue() / right.intValue());
/*      */       case 7:
/*  331 */         return LongConstant.fromValue(left.charValue() / right.longValue());
/*      */       case 5:
/*  333 */       case 6: } break;
/*      */     case 9:
/*  335 */       switch (rightId) { case 2:
/*  336 */         return FloatConstant.fromValue(left.floatValue() / right.charValue());
/*      */       case 9:
/*  337 */         return FloatConstant.fromValue(left.floatValue() / right.floatValue());
/*      */       case 8:
/*  338 */         return DoubleConstant.fromValue(left.floatValue() / right.doubleValue());
/*      */       case 3:
/*  339 */         return FloatConstant.fromValue(left.floatValue() / right.byteValue());
/*      */       case 4:
/*  340 */         return FloatConstant.fromValue(left.floatValue() / right.shortValue());
/*      */       case 10:
/*  341 */         return FloatConstant.fromValue(left.floatValue() / right.intValue());
/*      */       case 7:
/*  342 */         return FloatConstant.fromValue(left.floatValue() / (float)right.longValue());
/*      */       case 5:
/*  344 */       case 6: } break;
/*      */     case 8:
/*  346 */       switch (rightId) { case 2:
/*  347 */         return DoubleConstant.fromValue(left.doubleValue() / right.charValue());
/*      */       case 9:
/*  348 */         return DoubleConstant.fromValue(left.doubleValue() / right.floatValue());
/*      */       case 8:
/*  349 */         return DoubleConstant.fromValue(left.doubleValue() / right.doubleValue());
/*      */       case 3:
/*  350 */         return DoubleConstant.fromValue(left.doubleValue() / right.byteValue());
/*      */       case 4:
/*  351 */         return DoubleConstant.fromValue(left.doubleValue() / right.shortValue());
/*      */       case 10:
/*  352 */         return DoubleConstant.fromValue(left.doubleValue() / right.intValue());
/*      */       case 7:
/*  353 */         return DoubleConstant.fromValue(left.doubleValue() / right.longValue());
/*      */       case 5:
/*  355 */       case 6: } break;
/*      */     case 3:
/*  357 */       switch (rightId) { case 2:
/*  358 */         return IntConstant.fromValue(left.byteValue() / right.charValue());
/*      */       case 9:
/*  359 */         return FloatConstant.fromValue(left.byteValue() / right.floatValue());
/*      */       case 8:
/*  360 */         return DoubleConstant.fromValue(left.byteValue() / right.doubleValue());
/*      */       case 3:
/*  361 */         return IntConstant.fromValue(left.byteValue() / right.byteValue());
/*      */       case 4:
/*  362 */         return IntConstant.fromValue(left.byteValue() / right.shortValue());
/*      */       case 10:
/*  363 */         return IntConstant.fromValue(left.byteValue() / right.intValue());
/*      */       case 7:
/*  364 */         return LongConstant.fromValue(left.byteValue() / right.longValue());
/*      */       case 5:
/*  366 */       case 6: } break;
/*      */     case 4:
/*  368 */       switch (rightId) { case 2:
/*  369 */         return IntConstant.fromValue(left.shortValue() / right.charValue());
/*      */       case 9:
/*  370 */         return FloatConstant.fromValue(left.shortValue() / right.floatValue());
/*      */       case 8:
/*  371 */         return DoubleConstant.fromValue(left.shortValue() / right.doubleValue());
/*      */       case 3:
/*  372 */         return IntConstant.fromValue(left.shortValue() / right.byteValue());
/*      */       case 4:
/*  373 */         return IntConstant.fromValue(left.shortValue() / right.shortValue());
/*      */       case 10:
/*  374 */         return IntConstant.fromValue(left.shortValue() / right.intValue());
/*      */       case 7:
/*  375 */         return LongConstant.fromValue(left.shortValue() / right.longValue());
/*      */       case 5:
/*  377 */       case 6: } break;
/*      */     case 10:
/*  379 */       switch (rightId) { case 2:
/*  380 */         return IntConstant.fromValue(left.intValue() / right.charValue());
/*      */       case 9:
/*  381 */         return FloatConstant.fromValue(left.intValue() / right.floatValue());
/*      */       case 8:
/*  382 */         return DoubleConstant.fromValue(left.intValue() / right.doubleValue());
/*      */       case 3:
/*  383 */         return IntConstant.fromValue(left.intValue() / right.byteValue());
/*      */       case 4:
/*  384 */         return IntConstant.fromValue(left.intValue() / right.shortValue());
/*      */       case 10:
/*  385 */         return IntConstant.fromValue(left.intValue() / right.intValue());
/*      */       case 7:
/*  386 */         return LongConstant.fromValue(left.intValue() / right.longValue());
/*      */       case 5:
/*  388 */       case 6: } break;
/*      */     case 7:
/*  390 */       switch (rightId) { case 2:
/*  391 */         return LongConstant.fromValue(left.longValue() / right.charValue());
/*      */       case 9:
/*  392 */         return FloatConstant.fromValue((float)left.longValue() / right.floatValue());
/*      */       case 8:
/*  393 */         return DoubleConstant.fromValue(left.longValue() / right.doubleValue());
/*      */       case 3:
/*  394 */         return LongConstant.fromValue(left.longValue() / right.byteValue());
/*      */       case 4:
/*  395 */         return LongConstant.fromValue(left.longValue() / right.shortValue());
/*      */       case 10:
/*  396 */         return LongConstant.fromValue(left.longValue() / right.intValue());
/*      */       case 7:
/*  397 */         return LongConstant.fromValue(left.longValue() / right.longValue());
/*      */       case 5:
/*      */       case 6: } case 5:
/*  400 */     case 6: } return NotAConstant;
/*      */   }
/*      */ 
/*      */   public static final Constant computeConstantOperationEQUAL_EQUAL(Constant left, int leftId, Constant right, int rightId) {
/*  404 */     switch (leftId) {
/*      */     case 5:
/*  406 */       if (rightId != 5) break;
/*  407 */       return BooleanConstant.fromValue(left.booleanValue() == right.booleanValue());
/*      */     case 2:
/*  411 */       switch (rightId) { case 2:
/*  412 */         return BooleanConstant.fromValue(left.charValue() == right.charValue());
/*      */       case 9:
/*  413 */         return BooleanConstant.fromValue(left.charValue() == right.floatValue());
/*      */       case 8:
/*  414 */         return BooleanConstant.fromValue(left.charValue() == right.doubleValue());
/*      */       case 3:
/*  415 */         return BooleanConstant.fromValue(left.charValue() == right.byteValue());
/*      */       case 4:
/*  416 */         return BooleanConstant.fromValue(left.charValue() == right.shortValue());
/*      */       case 10:
/*  417 */         return BooleanConstant.fromValue(left.charValue() == right.intValue());
/*      */       case 7:
/*  418 */         return BooleanConstant.fromValue(left.charValue() == right.longValue());
/*      */       case 5:
/*  419 */       case 6: } break;
/*      */     case 9:
/*  421 */       switch (rightId) { case 2:
/*  422 */         return BooleanConstant.fromValue(left.floatValue() == right.charValue());
/*      */       case 9:
/*  423 */         return BooleanConstant.fromValue(left.floatValue() == right.floatValue());
/*      */       case 8:
/*  424 */         return BooleanConstant.fromValue(left.floatValue() == right.doubleValue());
/*      */       case 3:
/*  425 */         return BooleanConstant.fromValue(left.floatValue() == right.byteValue());
/*      */       case 4:
/*  426 */         return BooleanConstant.fromValue(left.floatValue() == right.shortValue());
/*      */       case 10:
/*  427 */         return BooleanConstant.fromValue(left.floatValue() == right.intValue());
/*      */       case 7:
/*  428 */         return BooleanConstant.fromValue(left.floatValue() == (float)right.longValue());
/*      */       case 5:
/*  430 */       case 6: } break;
/*      */     case 8:
/*  432 */       switch (rightId) { case 2:
/*  433 */         return BooleanConstant.fromValue(left.doubleValue() == right.charValue());
/*      */       case 9:
/*  434 */         return BooleanConstant.fromValue(left.doubleValue() == right.floatValue());
/*      */       case 8:
/*  435 */         return BooleanConstant.fromValue(left.doubleValue() == right.doubleValue());
/*      */       case 3:
/*  436 */         return BooleanConstant.fromValue(left.doubleValue() == right.byteValue());
/*      */       case 4:
/*  437 */         return BooleanConstant.fromValue(left.doubleValue() == right.shortValue());
/*      */       case 10:
/*  438 */         return BooleanConstant.fromValue(left.doubleValue() == right.intValue());
/*      */       case 7:
/*  439 */         return BooleanConstant.fromValue(left.doubleValue() == right.longValue());
/*      */       case 5:
/*  441 */       case 6: } break;
/*      */     case 3:
/*  443 */       switch (rightId) { case 2:
/*  444 */         return BooleanConstant.fromValue(left.byteValue() == right.charValue());
/*      */       case 9:
/*  445 */         return BooleanConstant.fromValue(left.byteValue() == right.floatValue());
/*      */       case 8:
/*  446 */         return BooleanConstant.fromValue(left.byteValue() == right.doubleValue());
/*      */       case 3:
/*  447 */         return BooleanConstant.fromValue(left.byteValue() == right.byteValue());
/*      */       case 4:
/*  448 */         return BooleanConstant.fromValue(left.byteValue() == right.shortValue());
/*      */       case 10:
/*  449 */         return BooleanConstant.fromValue(left.byteValue() == right.intValue());
/*      */       case 7:
/*  450 */         return BooleanConstant.fromValue(left.byteValue() == right.longValue());
/*      */       case 5:
/*  452 */       case 6: } break;
/*      */     case 4:
/*  454 */       switch (rightId) { case 2:
/*  455 */         return BooleanConstant.fromValue(left.shortValue() == right.charValue());
/*      */       case 9:
/*  456 */         return BooleanConstant.fromValue(left.shortValue() == right.floatValue());
/*      */       case 8:
/*  457 */         return BooleanConstant.fromValue(left.shortValue() == right.doubleValue());
/*      */       case 3:
/*  458 */         return BooleanConstant.fromValue(left.shortValue() == right.byteValue());
/*      */       case 4:
/*  459 */         return BooleanConstant.fromValue(left.shortValue() == right.shortValue());
/*      */       case 10:
/*  460 */         return BooleanConstant.fromValue(left.shortValue() == right.intValue());
/*      */       case 7:
/*  461 */         return BooleanConstant.fromValue(left.shortValue() == right.longValue());
/*      */       case 5:
/*  463 */       case 6: } break;
/*      */     case 10:
/*  465 */       switch (rightId) { case 2:
/*  466 */         return BooleanConstant.fromValue(left.intValue() == right.charValue());
/*      */       case 9:
/*  467 */         return BooleanConstant.fromValue(left.intValue() == right.floatValue());
/*      */       case 8:
/*  468 */         return BooleanConstant.fromValue(left.intValue() == right.doubleValue());
/*      */       case 3:
/*  469 */         return BooleanConstant.fromValue(left.intValue() == right.byteValue());
/*      */       case 4:
/*  470 */         return BooleanConstant.fromValue(left.intValue() == right.shortValue());
/*      */       case 10:
/*  471 */         return BooleanConstant.fromValue(left.intValue() == right.intValue());
/*      */       case 7:
/*  472 */         return BooleanConstant.fromValue(left.intValue() == right.longValue());
/*      */       case 5:
/*  474 */       case 6: } break;
/*      */     case 7:
/*  476 */       switch (rightId) { case 2:
/*  477 */         return BooleanConstant.fromValue(left.longValue() == right.charValue());
/*      */       case 9:
/*  478 */         return BooleanConstant.fromValue((float)left.longValue() == right.floatValue());
/*      */       case 8:
/*  479 */         return BooleanConstant.fromValue(left.longValue() == right.doubleValue());
/*      */       case 3:
/*  480 */         return BooleanConstant.fromValue(left.longValue() == right.byteValue());
/*      */       case 4:
/*  481 */         return BooleanConstant.fromValue(left.longValue() == right.shortValue());
/*      */       case 10:
/*  482 */         return BooleanConstant.fromValue(left.longValue() == right.intValue());
/*      */       case 7:
/*  483 */         return BooleanConstant.fromValue(left.longValue() == right.longValue());
/*      */       case 5:
/*  485 */       case 6: } break;
/*      */     case 11:
/*  487 */       if (rightId != 11) {
/*      */         break;
/*      */       }
/*  490 */       return BooleanConstant.fromValue(((StringConstant)left).hasSameValue(right));
/*      */     case 12:
/*  494 */       if (rightId == 11) {
/*  495 */         return BooleanConstant.fromValue(false);
/*      */       }
/*  497 */       if (rightId != 12) break;
/*  498 */       return BooleanConstant.fromValue(true);
/*      */     case 6:
/*      */     }
/*      */ 
/*  502 */     return BooleanConstant.fromValue(false);
/*      */   }
/*      */ 
/*      */   public static final Constant computeConstantOperationGREATER(Constant left, int leftId, Constant right, int rightId) {
/*  506 */     switch (leftId) {
/*      */     case 2:
/*  508 */       switch (rightId) { case 2:
/*  509 */         return BooleanConstant.fromValue(left.charValue() > right.charValue());
/*      */       case 9:
/*  510 */         return BooleanConstant.fromValue(left.charValue() > right.floatValue());
/*      */       case 8:
/*  511 */         return BooleanConstant.fromValue(left.charValue() > right.doubleValue());
/*      */       case 3:
/*  512 */         return BooleanConstant.fromValue(left.charValue() > right.byteValue());
/*      */       case 4:
/*  513 */         return BooleanConstant.fromValue(left.charValue() > right.shortValue());
/*      */       case 10:
/*  514 */         return BooleanConstant.fromValue(left.charValue() > right.intValue());
/*      */       case 7:
/*  515 */         return BooleanConstant.fromValue(left.charValue() > right.longValue());
/*      */       case 5:
/*  517 */       case 6: } break;
/*      */     case 9:
/*  519 */       switch (rightId) { case 2:
/*  520 */         return BooleanConstant.fromValue(left.floatValue() > right.charValue());
/*      */       case 9:
/*  521 */         return BooleanConstant.fromValue(left.floatValue() > right.floatValue());
/*      */       case 8:
/*  522 */         return BooleanConstant.fromValue(left.floatValue() > right.doubleValue());
/*      */       case 3:
/*  523 */         return BooleanConstant.fromValue(left.floatValue() > right.byteValue());
/*      */       case 4:
/*  524 */         return BooleanConstant.fromValue(left.floatValue() > right.shortValue());
/*      */       case 10:
/*  525 */         return BooleanConstant.fromValue(left.floatValue() > right.intValue());
/*      */       case 7:
/*  526 */         return BooleanConstant.fromValue(left.floatValue() > (float)right.longValue());
/*      */       case 5:
/*  528 */       case 6: } break;
/*      */     case 8:
/*  530 */       switch (rightId) { case 2:
/*  531 */         return BooleanConstant.fromValue(left.doubleValue() > right.charValue());
/*      */       case 9:
/*  532 */         return BooleanConstant.fromValue(left.doubleValue() > right.floatValue());
/*      */       case 8:
/*  533 */         return BooleanConstant.fromValue(left.doubleValue() > right.doubleValue());
/*      */       case 3:
/*  534 */         return BooleanConstant.fromValue(left.doubleValue() > right.byteValue());
/*      */       case 4:
/*  535 */         return BooleanConstant.fromValue(left.doubleValue() > right.shortValue());
/*      */       case 10:
/*  536 */         return BooleanConstant.fromValue(left.doubleValue() > right.intValue());
/*      */       case 7:
/*  537 */         return BooleanConstant.fromValue(left.doubleValue() > right.longValue());
/*      */       case 5:
/*  539 */       case 6: } break;
/*      */     case 3:
/*  541 */       switch (rightId) { case 2:
/*  542 */         return BooleanConstant.fromValue(left.byteValue() > right.charValue());
/*      */       case 9:
/*  543 */         return BooleanConstant.fromValue(left.byteValue() > right.floatValue());
/*      */       case 8:
/*  544 */         return BooleanConstant.fromValue(left.byteValue() > right.doubleValue());
/*      */       case 3:
/*  545 */         return BooleanConstant.fromValue(left.byteValue() > right.byteValue());
/*      */       case 4:
/*  546 */         return BooleanConstant.fromValue(left.byteValue() > right.shortValue());
/*      */       case 10:
/*  547 */         return BooleanConstant.fromValue(left.byteValue() > right.intValue());
/*      */       case 7:
/*  548 */         return BooleanConstant.fromValue(left.byteValue() > right.longValue());
/*      */       case 5:
/*  550 */       case 6: } break;
/*      */     case 4:
/*  552 */       switch (rightId) { case 2:
/*  553 */         return BooleanConstant.fromValue(left.shortValue() > right.charValue());
/*      */       case 9:
/*  554 */         return BooleanConstant.fromValue(left.shortValue() > right.floatValue());
/*      */       case 8:
/*  555 */         return BooleanConstant.fromValue(left.shortValue() > right.doubleValue());
/*      */       case 3:
/*  556 */         return BooleanConstant.fromValue(left.shortValue() > right.byteValue());
/*      */       case 4:
/*  557 */         return BooleanConstant.fromValue(left.shortValue() > right.shortValue());
/*      */       case 10:
/*  558 */         return BooleanConstant.fromValue(left.shortValue() > right.intValue());
/*      */       case 7:
/*  559 */         return BooleanConstant.fromValue(left.shortValue() > right.longValue());
/*      */       case 5:
/*  561 */       case 6: } break;
/*      */     case 10:
/*  563 */       switch (rightId) { case 2:
/*  564 */         return BooleanConstant.fromValue(left.intValue() > right.charValue());
/*      */       case 9:
/*  565 */         return BooleanConstant.fromValue(left.intValue() > right.floatValue());
/*      */       case 8:
/*  566 */         return BooleanConstant.fromValue(left.intValue() > right.doubleValue());
/*      */       case 3:
/*  567 */         return BooleanConstant.fromValue(left.intValue() > right.byteValue());
/*      */       case 4:
/*  568 */         return BooleanConstant.fromValue(left.intValue() > right.shortValue());
/*      */       case 10:
/*  569 */         return BooleanConstant.fromValue(left.intValue() > right.intValue());
/*      */       case 7:
/*  570 */         return BooleanConstant.fromValue(left.intValue() > right.longValue());
/*      */       case 5:
/*  572 */       case 6: } break;
/*      */     case 7:
/*  574 */       switch (rightId) { case 2:
/*  575 */         return BooleanConstant.fromValue(left.longValue() > right.charValue());
/*      */       case 9:
/*  576 */         return BooleanConstant.fromValue((float)left.longValue() > right.floatValue());
/*      */       case 8:
/*  577 */         return BooleanConstant.fromValue(left.longValue() > right.doubleValue());
/*      */       case 3:
/*  578 */         return BooleanConstant.fromValue(left.longValue() > right.byteValue());
/*      */       case 4:
/*  579 */         return BooleanConstant.fromValue(left.longValue() > right.shortValue());
/*      */       case 10:
/*  580 */         return BooleanConstant.fromValue(left.longValue() > right.intValue());
/*      */       case 7:
/*  581 */         return BooleanConstant.fromValue(left.longValue() > right.longValue());
/*      */       case 5:
/*      */       case 6: } case 5:
/*      */     case 6:
/*  585 */     }return NotAConstant;
/*      */   }
/*      */ 
/*      */   public static final Constant computeConstantOperationGREATER_EQUAL(Constant left, int leftId, Constant right, int rightId) {
/*  589 */     switch (leftId) {
/*      */     case 2:
/*  591 */       switch (rightId) { case 2:
/*  592 */         return BooleanConstant.fromValue(left.charValue() >= right.charValue());
/*      */       case 9:
/*  593 */         return BooleanConstant.fromValue(left.charValue() >= right.floatValue());
/*      */       case 8:
/*  594 */         return BooleanConstant.fromValue(left.charValue() >= right.doubleValue());
/*      */       case 3:
/*  595 */         return BooleanConstant.fromValue(left.charValue() >= right.byteValue());
/*      */       case 4:
/*  596 */         return BooleanConstant.fromValue(left.charValue() >= right.shortValue());
/*      */       case 10:
/*  597 */         return BooleanConstant.fromValue(left.charValue() >= right.intValue());
/*      */       case 7:
/*  598 */         return BooleanConstant.fromValue(left.charValue() >= right.longValue());
/*      */       case 5:
/*  600 */       case 6: } break;
/*      */     case 9:
/*  602 */       switch (rightId) { case 2:
/*  603 */         return BooleanConstant.fromValue(left.floatValue() >= right.charValue());
/*      */       case 9:
/*  604 */         return BooleanConstant.fromValue(left.floatValue() >= right.floatValue());
/*      */       case 8:
/*  605 */         return BooleanConstant.fromValue(left.floatValue() >= right.doubleValue());
/*      */       case 3:
/*  606 */         return BooleanConstant.fromValue(left.floatValue() >= right.byteValue());
/*      */       case 4:
/*  607 */         return BooleanConstant.fromValue(left.floatValue() >= right.shortValue());
/*      */       case 10:
/*  608 */         return BooleanConstant.fromValue(left.floatValue() >= right.intValue());
/*      */       case 7:
/*  609 */         return BooleanConstant.fromValue(left.floatValue() >= (float)right.longValue());
/*      */       case 5:
/*  611 */       case 6: } break;
/*      */     case 8:
/*  613 */       switch (rightId) { case 2:
/*  614 */         return BooleanConstant.fromValue(left.doubleValue() >= right.charValue());
/*      */       case 9:
/*  615 */         return BooleanConstant.fromValue(left.doubleValue() >= right.floatValue());
/*      */       case 8:
/*  616 */         return BooleanConstant.fromValue(left.doubleValue() >= right.doubleValue());
/*      */       case 3:
/*  617 */         return BooleanConstant.fromValue(left.doubleValue() >= right.byteValue());
/*      */       case 4:
/*  618 */         return BooleanConstant.fromValue(left.doubleValue() >= right.shortValue());
/*      */       case 10:
/*  619 */         return BooleanConstant.fromValue(left.doubleValue() >= right.intValue());
/*      */       case 7:
/*  620 */         return BooleanConstant.fromValue(left.doubleValue() >= right.longValue());
/*      */       case 5:
/*  622 */       case 6: } break;
/*      */     case 3:
/*  624 */       switch (rightId) { case 2:
/*  625 */         return BooleanConstant.fromValue(left.byteValue() >= right.charValue());
/*      */       case 9:
/*  626 */         return BooleanConstant.fromValue(left.byteValue() >= right.floatValue());
/*      */       case 8:
/*  627 */         return BooleanConstant.fromValue(left.byteValue() >= right.doubleValue());
/*      */       case 3:
/*  628 */         return BooleanConstant.fromValue(left.byteValue() >= right.byteValue());
/*      */       case 4:
/*  629 */         return BooleanConstant.fromValue(left.byteValue() >= right.shortValue());
/*      */       case 10:
/*  630 */         return BooleanConstant.fromValue(left.byteValue() >= right.intValue());
/*      */       case 7:
/*  631 */         return BooleanConstant.fromValue(left.byteValue() >= right.longValue());
/*      */       case 5:
/*  633 */       case 6: } break;
/*      */     case 4:
/*  635 */       switch (rightId) { case 2:
/*  636 */         return BooleanConstant.fromValue(left.shortValue() >= right.charValue());
/*      */       case 9:
/*  637 */         return BooleanConstant.fromValue(left.shortValue() >= right.floatValue());
/*      */       case 8:
/*  638 */         return BooleanConstant.fromValue(left.shortValue() >= right.doubleValue());
/*      */       case 3:
/*  639 */         return BooleanConstant.fromValue(left.shortValue() >= right.byteValue());
/*      */       case 4:
/*  640 */         return BooleanConstant.fromValue(left.shortValue() >= right.shortValue());
/*      */       case 10:
/*  641 */         return BooleanConstant.fromValue(left.shortValue() >= right.intValue());
/*      */       case 7:
/*  642 */         return BooleanConstant.fromValue(left.shortValue() >= right.longValue());
/*      */       case 5:
/*  644 */       case 6: } break;
/*      */     case 10:
/*  646 */       switch (rightId) { case 2:
/*  647 */         return BooleanConstant.fromValue(left.intValue() >= right.charValue());
/*      */       case 9:
/*  648 */         return BooleanConstant.fromValue(left.intValue() >= right.floatValue());
/*      */       case 8:
/*  649 */         return BooleanConstant.fromValue(left.intValue() >= right.doubleValue());
/*      */       case 3:
/*  650 */         return BooleanConstant.fromValue(left.intValue() >= right.byteValue());
/*      */       case 4:
/*  651 */         return BooleanConstant.fromValue(left.intValue() >= right.shortValue());
/*      */       case 10:
/*  652 */         return BooleanConstant.fromValue(left.intValue() >= right.intValue());
/*      */       case 7:
/*  653 */         return BooleanConstant.fromValue(left.intValue() >= right.longValue());
/*      */       case 5:
/*  655 */       case 6: } break;
/*      */     case 7:
/*  657 */       switch (rightId) { case 2:
/*  658 */         return BooleanConstant.fromValue(left.longValue() >= right.charValue());
/*      */       case 9:
/*  659 */         return BooleanConstant.fromValue((float)left.longValue() >= right.floatValue());
/*      */       case 8:
/*  660 */         return BooleanConstant.fromValue(left.longValue() >= right.doubleValue());
/*      */       case 3:
/*  661 */         return BooleanConstant.fromValue(left.longValue() >= right.byteValue());
/*      */       case 4:
/*  662 */         return BooleanConstant.fromValue(left.longValue() >= right.shortValue());
/*      */       case 10:
/*  663 */         return BooleanConstant.fromValue(left.longValue() >= right.intValue());
/*      */       case 7:
/*  664 */         return BooleanConstant.fromValue(left.longValue() >= right.longValue());
/*      */       case 5:
/*      */       case 6: } case 5:
/*  667 */     case 6: } return NotAConstant;
/*      */   }
/*      */ 
/*      */   public static final Constant computeConstantOperationLEFT_SHIFT(Constant left, int leftId, Constant right, int rightId) {
/*  671 */     switch (leftId) {
/*      */     case 2:
/*  673 */       switch (rightId) { case 2:
/*  674 */         return IntConstant.fromValue(left.charValue() << right.charValue());
/*      */       case 3:
/*  675 */         return IntConstant.fromValue(left.charValue() << right.byteValue());
/*      */       case 4:
/*  676 */         return IntConstant.fromValue(left.charValue() << right.shortValue());
/*      */       case 10:
/*  677 */         return IntConstant.fromValue(left.charValue() << right.intValue());
/*      */       case 7:
/*  678 */         return IntConstant.fromValue(left.charValue() << (int)right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/*  680 */       case 9: } break;
/*      */     case 3:
/*  682 */       switch (rightId) { case 2:
/*  683 */         return IntConstant.fromValue(left.byteValue() << right.charValue());
/*      */       case 3:
/*  684 */         return IntConstant.fromValue(left.byteValue() << right.byteValue());
/*      */       case 4:
/*  685 */         return IntConstant.fromValue(left.byteValue() << right.shortValue());
/*      */       case 10:
/*  686 */         return IntConstant.fromValue(left.byteValue() << right.intValue());
/*      */       case 7:
/*  687 */         return IntConstant.fromValue(left.byteValue() << (int)right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/*  689 */       case 9: } break;
/*      */     case 4:
/*  691 */       switch (rightId) { case 2:
/*  692 */         return IntConstant.fromValue(left.shortValue() << right.charValue());
/*      */       case 3:
/*  693 */         return IntConstant.fromValue(left.shortValue() << right.byteValue());
/*      */       case 4:
/*  694 */         return IntConstant.fromValue(left.shortValue() << right.shortValue());
/*      */       case 10:
/*  695 */         return IntConstant.fromValue(left.shortValue() << right.intValue());
/*      */       case 7:
/*  696 */         return IntConstant.fromValue(left.shortValue() << (int)right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/*  698 */       case 9: } break;
/*      */     case 10:
/*  700 */       switch (rightId) { case 2:
/*  701 */         return IntConstant.fromValue(left.intValue() << right.charValue());
/*      */       case 3:
/*  702 */         return IntConstant.fromValue(left.intValue() << right.byteValue());
/*      */       case 4:
/*  703 */         return IntConstant.fromValue(left.intValue() << right.shortValue());
/*      */       case 10:
/*  704 */         return IntConstant.fromValue(left.intValue() << right.intValue());
/*      */       case 7:
/*  705 */         return IntConstant.fromValue(left.intValue() << (int)right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/*  707 */       case 9: } break;
/*      */     case 7:
/*  709 */       switch (rightId) { case 2:
/*  710 */         return LongConstant.fromValue(left.longValue() << right.charValue());
/*      */       case 3:
/*  711 */         return LongConstant.fromValue(left.longValue() << right.byteValue());
/*      */       case 4:
/*  712 */         return LongConstant.fromValue(left.longValue() << right.shortValue());
/*      */       case 10:
/*  713 */         return LongConstant.fromValue(left.longValue() << right.intValue());
/*      */       case 7:
/*  714 */         return LongConstant.fromValue(left.longValue() << (int)right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/*      */       case 9: } case 5:
/*      */     case 6:
/*      */     case 8:
/*  717 */     case 9: } return NotAConstant;
/*      */   }
/*      */ 
/*      */   public static final Constant computeConstantOperationLESS(Constant left, int leftId, Constant right, int rightId) {
/*  721 */     switch (leftId) {
/*      */     case 2:
/*  723 */       switch (rightId) { case 2:
/*  724 */         return BooleanConstant.fromValue(left.charValue() < right.charValue());
/*      */       case 9:
/*  725 */         return BooleanConstant.fromValue(left.charValue() < right.floatValue());
/*      */       case 8:
/*  726 */         return BooleanConstant.fromValue(left.charValue() < right.doubleValue());
/*      */       case 3:
/*  727 */         return BooleanConstant.fromValue(left.charValue() < right.byteValue());
/*      */       case 4:
/*  728 */         return BooleanConstant.fromValue(left.charValue() < right.shortValue());
/*      */       case 10:
/*  729 */         return BooleanConstant.fromValue(left.charValue() < right.intValue());
/*      */       case 7:
/*  730 */         return BooleanConstant.fromValue(left.charValue() < right.longValue());
/*      */       case 5:
/*  732 */       case 6: } break;
/*      */     case 9:
/*  734 */       switch (rightId) { case 2:
/*  735 */         return BooleanConstant.fromValue(left.floatValue() < right.charValue());
/*      */       case 9:
/*  736 */         return BooleanConstant.fromValue(left.floatValue() < right.floatValue());
/*      */       case 8:
/*  737 */         return BooleanConstant.fromValue(left.floatValue() < right.doubleValue());
/*      */       case 3:
/*  738 */         return BooleanConstant.fromValue(left.floatValue() < right.byteValue());
/*      */       case 4:
/*  739 */         return BooleanConstant.fromValue(left.floatValue() < right.shortValue());
/*      */       case 10:
/*  740 */         return BooleanConstant.fromValue(left.floatValue() < right.intValue());
/*      */       case 7:
/*  741 */         return BooleanConstant.fromValue(left.floatValue() < (float)right.longValue());
/*      */       case 5:
/*  743 */       case 6: } break;
/*      */     case 8:
/*  745 */       switch (rightId) { case 2:
/*  746 */         return BooleanConstant.fromValue(left.doubleValue() < right.charValue());
/*      */       case 9:
/*  747 */         return BooleanConstant.fromValue(left.doubleValue() < right.floatValue());
/*      */       case 8:
/*  748 */         return BooleanConstant.fromValue(left.doubleValue() < right.doubleValue());
/*      */       case 3:
/*  749 */         return BooleanConstant.fromValue(left.doubleValue() < right.byteValue());
/*      */       case 4:
/*  750 */         return BooleanConstant.fromValue(left.doubleValue() < right.shortValue());
/*      */       case 10:
/*  751 */         return BooleanConstant.fromValue(left.doubleValue() < right.intValue());
/*      */       case 7:
/*  752 */         return BooleanConstant.fromValue(left.doubleValue() < right.longValue());
/*      */       case 5:
/*  754 */       case 6: } break;
/*      */     case 3:
/*  756 */       switch (rightId) { case 2:
/*  757 */         return BooleanConstant.fromValue(left.byteValue() < right.charValue());
/*      */       case 9:
/*  758 */         return BooleanConstant.fromValue(left.byteValue() < right.floatValue());
/*      */       case 8:
/*  759 */         return BooleanConstant.fromValue(left.byteValue() < right.doubleValue());
/*      */       case 3:
/*  760 */         return BooleanConstant.fromValue(left.byteValue() < right.byteValue());
/*      */       case 4:
/*  761 */         return BooleanConstant.fromValue(left.byteValue() < right.shortValue());
/*      */       case 10:
/*  762 */         return BooleanConstant.fromValue(left.byteValue() < right.intValue());
/*      */       case 7:
/*  763 */         return BooleanConstant.fromValue(left.byteValue() < right.longValue());
/*      */       case 5:
/*  765 */       case 6: } break;
/*      */     case 4:
/*  767 */       switch (rightId) { case 2:
/*  768 */         return BooleanConstant.fromValue(left.shortValue() < right.charValue());
/*      */       case 9:
/*  769 */         return BooleanConstant.fromValue(left.shortValue() < right.floatValue());
/*      */       case 8:
/*  770 */         return BooleanConstant.fromValue(left.shortValue() < right.doubleValue());
/*      */       case 3:
/*  771 */         return BooleanConstant.fromValue(left.shortValue() < right.byteValue());
/*      */       case 4:
/*  772 */         return BooleanConstant.fromValue(left.shortValue() < right.shortValue());
/*      */       case 10:
/*  773 */         return BooleanConstant.fromValue(left.shortValue() < right.intValue());
/*      */       case 7:
/*  774 */         return BooleanConstant.fromValue(left.shortValue() < right.longValue());
/*      */       case 5:
/*  776 */       case 6: } break;
/*      */     case 10:
/*  778 */       switch (rightId) { case 2:
/*  779 */         return BooleanConstant.fromValue(left.intValue() < right.charValue());
/*      */       case 9:
/*  780 */         return BooleanConstant.fromValue(left.intValue() < right.floatValue());
/*      */       case 8:
/*  781 */         return BooleanConstant.fromValue(left.intValue() < right.doubleValue());
/*      */       case 3:
/*  782 */         return BooleanConstant.fromValue(left.intValue() < right.byteValue());
/*      */       case 4:
/*  783 */         return BooleanConstant.fromValue(left.intValue() < right.shortValue());
/*      */       case 10:
/*  784 */         return BooleanConstant.fromValue(left.intValue() < right.intValue());
/*      */       case 7:
/*  785 */         return BooleanConstant.fromValue(left.intValue() < right.longValue());
/*      */       case 5:
/*  787 */       case 6: } break;
/*      */     case 7:
/*  789 */       switch (rightId) { case 2:
/*  790 */         return BooleanConstant.fromValue(left.longValue() < right.charValue());
/*      */       case 9:
/*  791 */         return BooleanConstant.fromValue((float)left.longValue() < right.floatValue());
/*      */       case 8:
/*  792 */         return BooleanConstant.fromValue(left.longValue() < right.doubleValue());
/*      */       case 3:
/*  793 */         return BooleanConstant.fromValue(left.longValue() < right.byteValue());
/*      */       case 4:
/*  794 */         return BooleanConstant.fromValue(left.longValue() < right.shortValue());
/*      */       case 10:
/*  795 */         return BooleanConstant.fromValue(left.longValue() < right.intValue());
/*      */       case 7:
/*  796 */         return BooleanConstant.fromValue(left.longValue() < right.longValue());
/*      */       case 5:
/*      */       case 6: } case 5:
/*  799 */     case 6: } return NotAConstant;
/*      */   }
/*      */ 
/*      */   public static final Constant computeConstantOperationLESS_EQUAL(Constant left, int leftId, Constant right, int rightId) {
/*  803 */     switch (leftId) {
/*      */     case 2:
/*  805 */       switch (rightId) { case 2:
/*  806 */         return BooleanConstant.fromValue(left.charValue() <= right.charValue());
/*      */       case 9:
/*  807 */         return BooleanConstant.fromValue(left.charValue() <= right.floatValue());
/*      */       case 8:
/*  808 */         return BooleanConstant.fromValue(left.charValue() <= right.doubleValue());
/*      */       case 3:
/*  809 */         return BooleanConstant.fromValue(left.charValue() <= right.byteValue());
/*      */       case 4:
/*  810 */         return BooleanConstant.fromValue(left.charValue() <= right.shortValue());
/*      */       case 10:
/*  811 */         return BooleanConstant.fromValue(left.charValue() <= right.intValue());
/*      */       case 7:
/*  812 */         return BooleanConstant.fromValue(left.charValue() <= right.longValue());
/*      */       case 5:
/*  814 */       case 6: } break;
/*      */     case 9:
/*  816 */       switch (rightId) { case 2:
/*  817 */         return BooleanConstant.fromValue(left.floatValue() <= right.charValue());
/*      */       case 9:
/*  818 */         return BooleanConstant.fromValue(left.floatValue() <= right.floatValue());
/*      */       case 8:
/*  819 */         return BooleanConstant.fromValue(left.floatValue() <= right.doubleValue());
/*      */       case 3:
/*  820 */         return BooleanConstant.fromValue(left.floatValue() <= right.byteValue());
/*      */       case 4:
/*  821 */         return BooleanConstant.fromValue(left.floatValue() <= right.shortValue());
/*      */       case 10:
/*  822 */         return BooleanConstant.fromValue(left.floatValue() <= right.intValue());
/*      */       case 7:
/*  823 */         return BooleanConstant.fromValue(left.floatValue() <= (float)right.longValue());
/*      */       case 5:
/*  825 */       case 6: } break;
/*      */     case 8:
/*  827 */       switch (rightId) { case 2:
/*  828 */         return BooleanConstant.fromValue(left.doubleValue() <= right.charValue());
/*      */       case 9:
/*  829 */         return BooleanConstant.fromValue(left.doubleValue() <= right.floatValue());
/*      */       case 8:
/*  830 */         return BooleanConstant.fromValue(left.doubleValue() <= right.doubleValue());
/*      */       case 3:
/*  831 */         return BooleanConstant.fromValue(left.doubleValue() <= right.byteValue());
/*      */       case 4:
/*  832 */         return BooleanConstant.fromValue(left.doubleValue() <= right.shortValue());
/*      */       case 10:
/*  833 */         return BooleanConstant.fromValue(left.doubleValue() <= right.intValue());
/*      */       case 7:
/*  834 */         return BooleanConstant.fromValue(left.doubleValue() <= right.longValue());
/*      */       case 5:
/*  836 */       case 6: } break;
/*      */     case 3:
/*  838 */       switch (rightId) { case 2:
/*  839 */         return BooleanConstant.fromValue(left.byteValue() <= right.charValue());
/*      */       case 9:
/*  840 */         return BooleanConstant.fromValue(left.byteValue() <= right.floatValue());
/*      */       case 8:
/*  841 */         return BooleanConstant.fromValue(left.byteValue() <= right.doubleValue());
/*      */       case 3:
/*  842 */         return BooleanConstant.fromValue(left.byteValue() <= right.byteValue());
/*      */       case 4:
/*  843 */         return BooleanConstant.fromValue(left.byteValue() <= right.shortValue());
/*      */       case 10:
/*  844 */         return BooleanConstant.fromValue(left.byteValue() <= right.intValue());
/*      */       case 7:
/*  845 */         return BooleanConstant.fromValue(left.byteValue() <= right.longValue());
/*      */       case 5:
/*  847 */       case 6: } break;
/*      */     case 4:
/*  849 */       switch (rightId) { case 2:
/*  850 */         return BooleanConstant.fromValue(left.shortValue() <= right.charValue());
/*      */       case 9:
/*  851 */         return BooleanConstant.fromValue(left.shortValue() <= right.floatValue());
/*      */       case 8:
/*  852 */         return BooleanConstant.fromValue(left.shortValue() <= right.doubleValue());
/*      */       case 3:
/*  853 */         return BooleanConstant.fromValue(left.shortValue() <= right.byteValue());
/*      */       case 4:
/*  854 */         return BooleanConstant.fromValue(left.shortValue() <= right.shortValue());
/*      */       case 10:
/*  855 */         return BooleanConstant.fromValue(left.shortValue() <= right.intValue());
/*      */       case 7:
/*  856 */         return BooleanConstant.fromValue(left.shortValue() <= right.longValue());
/*      */       case 5:
/*  858 */       case 6: } break;
/*      */     case 10:
/*  860 */       switch (rightId) { case 2:
/*  861 */         return BooleanConstant.fromValue(left.intValue() <= right.charValue());
/*      */       case 9:
/*  862 */         return BooleanConstant.fromValue(left.intValue() <= right.floatValue());
/*      */       case 8:
/*  863 */         return BooleanConstant.fromValue(left.intValue() <= right.doubleValue());
/*      */       case 3:
/*  864 */         return BooleanConstant.fromValue(left.intValue() <= right.byteValue());
/*      */       case 4:
/*  865 */         return BooleanConstant.fromValue(left.intValue() <= right.shortValue());
/*      */       case 10:
/*  866 */         return BooleanConstant.fromValue(left.intValue() <= right.intValue());
/*      */       case 7:
/*  867 */         return BooleanConstant.fromValue(left.intValue() <= right.longValue());
/*      */       case 5:
/*  869 */       case 6: } break;
/*      */     case 7:
/*  871 */       switch (rightId) { case 2:
/*  872 */         return BooleanConstant.fromValue(left.longValue() <= right.charValue());
/*      */       case 9:
/*  873 */         return BooleanConstant.fromValue((float)left.longValue() <= right.floatValue());
/*      */       case 8:
/*  874 */         return BooleanConstant.fromValue(left.longValue() <= right.doubleValue());
/*      */       case 3:
/*  875 */         return BooleanConstant.fromValue(left.longValue() <= right.byteValue());
/*      */       case 4:
/*  876 */         return BooleanConstant.fromValue(left.longValue() <= right.shortValue());
/*      */       case 10:
/*  877 */         return BooleanConstant.fromValue(left.longValue() <= right.intValue());
/*      */       case 7:
/*  878 */         return BooleanConstant.fromValue(left.longValue() <= right.longValue());
/*      */       case 5:
/*      */       case 6: } case 5:
/*  881 */     case 6: } return NotAConstant;
/*      */   }
/*      */ 
/*      */   public static final Constant computeConstantOperationMINUS(Constant left, int leftId, Constant right, int rightId) {
/*  885 */     switch (leftId) {
/*      */     case 2:
/*  887 */       switch (rightId) { case 2:
/*  888 */         return IntConstant.fromValue(left.charValue() - right.charValue());
/*      */       case 9:
/*  889 */         return FloatConstant.fromValue(left.charValue() - right.floatValue());
/*      */       case 8:
/*  890 */         return DoubleConstant.fromValue(left.charValue() - right.doubleValue());
/*      */       case 3:
/*  891 */         return IntConstant.fromValue(left.charValue() - right.byteValue());
/*      */       case 4:
/*  892 */         return IntConstant.fromValue(left.charValue() - right.shortValue());
/*      */       case 10:
/*  893 */         return IntConstant.fromValue(left.charValue() - right.intValue());
/*      */       case 7:
/*  894 */         return LongConstant.fromValue(left.charValue() - right.longValue());
/*      */       case 5:
/*  896 */       case 6: } break;
/*      */     case 9:
/*  898 */       switch (rightId) { case 2:
/*  899 */         return FloatConstant.fromValue(left.floatValue() - right.charValue());
/*      */       case 9:
/*  900 */         return FloatConstant.fromValue(left.floatValue() - right.floatValue());
/*      */       case 8:
/*  901 */         return DoubleConstant.fromValue(left.floatValue() - right.doubleValue());
/*      */       case 3:
/*  902 */         return FloatConstant.fromValue(left.floatValue() - right.byteValue());
/*      */       case 4:
/*  903 */         return FloatConstant.fromValue(left.floatValue() - right.shortValue());
/*      */       case 10:
/*  904 */         return FloatConstant.fromValue(left.floatValue() - right.intValue());
/*      */       case 7:
/*  905 */         return FloatConstant.fromValue(left.floatValue() - (float)right.longValue());
/*      */       case 5:
/*  907 */       case 6: } break;
/*      */     case 8:
/*  909 */       switch (rightId) { case 2:
/*  910 */         return DoubleConstant.fromValue(left.doubleValue() - right.charValue());
/*      */       case 9:
/*  911 */         return DoubleConstant.fromValue(left.doubleValue() - right.floatValue());
/*      */       case 8:
/*  912 */         return DoubleConstant.fromValue(left.doubleValue() - right.doubleValue());
/*      */       case 3:
/*  913 */         return DoubleConstant.fromValue(left.doubleValue() - right.byteValue());
/*      */       case 4:
/*  914 */         return DoubleConstant.fromValue(left.doubleValue() - right.shortValue());
/*      */       case 10:
/*  915 */         return DoubleConstant.fromValue(left.doubleValue() - right.intValue());
/*      */       case 7:
/*  916 */         return DoubleConstant.fromValue(left.doubleValue() - right.longValue());
/*      */       case 5:
/*  918 */       case 6: } break;
/*      */     case 3:
/*  920 */       switch (rightId) { case 2:
/*  921 */         return IntConstant.fromValue(left.byteValue() - right.charValue());
/*      */       case 9:
/*  922 */         return FloatConstant.fromValue(left.byteValue() - right.floatValue());
/*      */       case 8:
/*  923 */         return DoubleConstant.fromValue(left.byteValue() - right.doubleValue());
/*      */       case 3:
/*  924 */         return IntConstant.fromValue(left.byteValue() - right.byteValue());
/*      */       case 4:
/*  925 */         return IntConstant.fromValue(left.byteValue() - right.shortValue());
/*      */       case 10:
/*  926 */         return IntConstant.fromValue(left.byteValue() - right.intValue());
/*      */       case 7:
/*  927 */         return LongConstant.fromValue(left.byteValue() - right.longValue());
/*      */       case 5:
/*  929 */       case 6: } break;
/*      */     case 4:
/*  931 */       switch (rightId) { case 2:
/*  932 */         return IntConstant.fromValue(left.shortValue() - right.charValue());
/*      */       case 9:
/*  933 */         return FloatConstant.fromValue(left.shortValue() - right.floatValue());
/*      */       case 8:
/*  934 */         return DoubleConstant.fromValue(left.shortValue() - right.doubleValue());
/*      */       case 3:
/*  935 */         return IntConstant.fromValue(left.shortValue() - right.byteValue());
/*      */       case 4:
/*  936 */         return IntConstant.fromValue(left.shortValue() - right.shortValue());
/*      */       case 10:
/*  937 */         return IntConstant.fromValue(left.shortValue() - right.intValue());
/*      */       case 7:
/*  938 */         return LongConstant.fromValue(left.shortValue() - right.longValue());
/*      */       case 5:
/*  940 */       case 6: } break;
/*      */     case 10:
/*  942 */       switch (rightId) { case 2:
/*  943 */         return IntConstant.fromValue(left.intValue() - right.charValue());
/*      */       case 9:
/*  944 */         return FloatConstant.fromValue(left.intValue() - right.floatValue());
/*      */       case 8:
/*  945 */         return DoubleConstant.fromValue(left.intValue() - right.doubleValue());
/*      */       case 3:
/*  946 */         return IntConstant.fromValue(left.intValue() - right.byteValue());
/*      */       case 4:
/*  947 */         return IntConstant.fromValue(left.intValue() - right.shortValue());
/*      */       case 10:
/*  948 */         return IntConstant.fromValue(left.intValue() - right.intValue());
/*      */       case 7:
/*  949 */         return LongConstant.fromValue(left.intValue() - right.longValue());
/*      */       case 5:
/*  951 */       case 6: } break;
/*      */     case 7:
/*  953 */       switch (rightId) { case 2:
/*  954 */         return LongConstant.fromValue(left.longValue() - right.charValue());
/*      */       case 9:
/*  955 */         return FloatConstant.fromValue((float)left.longValue() - right.floatValue());
/*      */       case 8:
/*  956 */         return DoubleConstant.fromValue(left.longValue() - right.doubleValue());
/*      */       case 3:
/*  957 */         return LongConstant.fromValue(left.longValue() - right.byteValue());
/*      */       case 4:
/*  958 */         return LongConstant.fromValue(left.longValue() - right.shortValue());
/*      */       case 10:
/*  959 */         return LongConstant.fromValue(left.longValue() - right.intValue());
/*      */       case 7:
/*  960 */         return LongConstant.fromValue(left.longValue() - right.longValue());
/*      */       case 5:
/*      */       case 6: } case 5:
/*  963 */     case 6: } return NotAConstant;
/*      */   }
/*      */ 
/*      */   public static final Constant computeConstantOperationMULTIPLY(Constant left, int leftId, Constant right, int rightId) {
/*  967 */     switch (leftId) {
/*      */     case 2:
/*  969 */       switch (rightId) { case 2:
/*  970 */         return IntConstant.fromValue(left.charValue() * right.charValue());
/*      */       case 9:
/*  971 */         return FloatConstant.fromValue(left.charValue() * right.floatValue());
/*      */       case 8:
/*  972 */         return DoubleConstant.fromValue(left.charValue() * right.doubleValue());
/*      */       case 3:
/*  973 */         return IntConstant.fromValue(left.charValue() * right.byteValue());
/*      */       case 4:
/*  974 */         return IntConstant.fromValue(left.charValue() * right.shortValue());
/*      */       case 10:
/*  975 */         return IntConstant.fromValue(left.charValue() * right.intValue());
/*      */       case 7:
/*  976 */         return LongConstant.fromValue(left.charValue() * right.longValue());
/*      */       case 5:
/*  978 */       case 6: } break;
/*      */     case 9:
/*  980 */       switch (rightId) { case 2:
/*  981 */         return FloatConstant.fromValue(left.floatValue() * right.charValue());
/*      */       case 9:
/*  982 */         return FloatConstant.fromValue(left.floatValue() * right.floatValue());
/*      */       case 8:
/*  983 */         return DoubleConstant.fromValue(left.floatValue() * right.doubleValue());
/*      */       case 3:
/*  984 */         return FloatConstant.fromValue(left.floatValue() * right.byteValue());
/*      */       case 4:
/*  985 */         return FloatConstant.fromValue(left.floatValue() * right.shortValue());
/*      */       case 10:
/*  986 */         return FloatConstant.fromValue(left.floatValue() * right.intValue());
/*      */       case 7:
/*  987 */         return FloatConstant.fromValue(left.floatValue() * (float)right.longValue());
/*      */       case 5:
/*  989 */       case 6: } break;
/*      */     case 8:
/*  991 */       switch (rightId) { case 2:
/*  992 */         return DoubleConstant.fromValue(left.doubleValue() * right.charValue());
/*      */       case 9:
/*  993 */         return DoubleConstant.fromValue(left.doubleValue() * right.floatValue());
/*      */       case 8:
/*  994 */         return DoubleConstant.fromValue(left.doubleValue() * right.doubleValue());
/*      */       case 3:
/*  995 */         return DoubleConstant.fromValue(left.doubleValue() * right.byteValue());
/*      */       case 4:
/*  996 */         return DoubleConstant.fromValue(left.doubleValue() * right.shortValue());
/*      */       case 10:
/*  997 */         return DoubleConstant.fromValue(left.doubleValue() * right.intValue());
/*      */       case 7:
/*  998 */         return DoubleConstant.fromValue(left.doubleValue() * right.longValue());
/*      */       case 5:
/* 1000 */       case 6: } break;
/*      */     case 3:
/* 1002 */       switch (rightId) { case 2:
/* 1003 */         return IntConstant.fromValue(left.byteValue() * right.charValue());
/*      */       case 9:
/* 1004 */         return FloatConstant.fromValue(left.byteValue() * right.floatValue());
/*      */       case 8:
/* 1005 */         return DoubleConstant.fromValue(left.byteValue() * right.doubleValue());
/*      */       case 3:
/* 1006 */         return IntConstant.fromValue(left.byteValue() * right.byteValue());
/*      */       case 4:
/* 1007 */         return IntConstant.fromValue(left.byteValue() * right.shortValue());
/*      */       case 10:
/* 1008 */         return IntConstant.fromValue(left.byteValue() * right.intValue());
/*      */       case 7:
/* 1009 */         return LongConstant.fromValue(left.byteValue() * right.longValue());
/*      */       case 5:
/* 1011 */       case 6: } break;
/*      */     case 4:
/* 1013 */       switch (rightId) { case 2:
/* 1014 */         return IntConstant.fromValue(left.shortValue() * right.charValue());
/*      */       case 9:
/* 1015 */         return FloatConstant.fromValue(left.shortValue() * right.floatValue());
/*      */       case 8:
/* 1016 */         return DoubleConstant.fromValue(left.shortValue() * right.doubleValue());
/*      */       case 3:
/* 1017 */         return IntConstant.fromValue(left.shortValue() * right.byteValue());
/*      */       case 4:
/* 1018 */         return IntConstant.fromValue(left.shortValue() * right.shortValue());
/*      */       case 10:
/* 1019 */         return IntConstant.fromValue(left.shortValue() * right.intValue());
/*      */       case 7:
/* 1020 */         return LongConstant.fromValue(left.shortValue() * right.longValue());
/*      */       case 5:
/* 1022 */       case 6: } break;
/*      */     case 10:
/* 1024 */       switch (rightId) { case 2:
/* 1025 */         return IntConstant.fromValue(left.intValue() * right.charValue());
/*      */       case 9:
/* 1026 */         return FloatConstant.fromValue(left.intValue() * right.floatValue());
/*      */       case 8:
/* 1027 */         return DoubleConstant.fromValue(left.intValue() * right.doubleValue());
/*      */       case 3:
/* 1028 */         return IntConstant.fromValue(left.intValue() * right.byteValue());
/*      */       case 4:
/* 1029 */         return IntConstant.fromValue(left.intValue() * right.shortValue());
/*      */       case 10:
/* 1030 */         return IntConstant.fromValue(left.intValue() * right.intValue());
/*      */       case 7:
/* 1031 */         return LongConstant.fromValue(left.intValue() * right.longValue());
/*      */       case 5:
/* 1033 */       case 6: } break;
/*      */     case 7:
/* 1035 */       switch (rightId) { case 2:
/* 1036 */         return LongConstant.fromValue(left.longValue() * right.charValue());
/*      */       case 9:
/* 1037 */         return FloatConstant.fromValue((float)left.longValue() * right.floatValue());
/*      */       case 8:
/* 1038 */         return DoubleConstant.fromValue(left.longValue() * right.doubleValue());
/*      */       case 3:
/* 1039 */         return LongConstant.fromValue(left.longValue() * right.byteValue());
/*      */       case 4:
/* 1040 */         return LongConstant.fromValue(left.longValue() * right.shortValue());
/*      */       case 10:
/* 1041 */         return LongConstant.fromValue(left.longValue() * right.intValue());
/*      */       case 7:
/* 1042 */         return LongConstant.fromValue(left.longValue() * right.longValue());
/*      */       case 5:
/*      */       case 6: } case 5:
/* 1045 */     case 6: } return NotAConstant;
/*      */   }
/*      */ 
/*      */   public static final Constant computeConstantOperationOR(Constant left, int leftId, Constant right, int rightId) {
/* 1049 */     switch (leftId) { case 5:
/* 1050 */       return BooleanConstant.fromValue(left.booleanValue() | right.booleanValue());
/*      */     case 2:
/* 1052 */       switch (rightId) { case 2:
/* 1053 */         return IntConstant.fromValue(left.charValue() | right.charValue());
/*      */       case 3:
/* 1054 */         return IntConstant.fromValue(left.charValue() | right.byteValue());
/*      */       case 4:
/* 1055 */         return IntConstant.fromValue(left.charValue() | right.shortValue());
/*      */       case 10:
/* 1056 */         return IntConstant.fromValue(left.charValue() | right.intValue());
/*      */       case 7:
/* 1057 */         return LongConstant.fromValue(left.charValue() | right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/* 1059 */       case 9: } break;
/*      */     case 3:
/* 1061 */       switch (rightId) { case 2:
/* 1062 */         return IntConstant.fromValue(left.byteValue() | right.charValue());
/*      */       case 3:
/* 1063 */         return IntConstant.fromValue(left.byteValue() | right.byteValue());
/*      */       case 4:
/* 1064 */         return IntConstant.fromValue(left.byteValue() | right.shortValue());
/*      */       case 10:
/* 1065 */         return IntConstant.fromValue(left.byteValue() | right.intValue());
/*      */       case 7:
/* 1066 */         return LongConstant.fromValue(left.byteValue() | right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/* 1068 */       case 9: } break;
/*      */     case 4:
/* 1070 */       switch (rightId) { case 2:
/* 1071 */         return IntConstant.fromValue(left.shortValue() | right.charValue());
/*      */       case 3:
/* 1072 */         return IntConstant.fromValue(left.shortValue() | right.byteValue());
/*      */       case 4:
/* 1073 */         return IntConstant.fromValue(left.shortValue() | right.shortValue());
/*      */       case 10:
/* 1074 */         return IntConstant.fromValue(left.shortValue() | right.intValue());
/*      */       case 7:
/* 1075 */         return LongConstant.fromValue(left.shortValue() | right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/* 1077 */       case 9: } break;
/*      */     case 10:
/* 1079 */       switch (rightId) { case 2:
/* 1080 */         return IntConstant.fromValue(left.intValue() | right.charValue());
/*      */       case 3:
/* 1081 */         return IntConstant.fromValue(left.intValue() | right.byteValue());
/*      */       case 4:
/* 1082 */         return IntConstant.fromValue(left.intValue() | right.shortValue());
/*      */       case 10:
/* 1083 */         return IntConstant.fromValue(left.intValue() | right.intValue());
/*      */       case 7:
/* 1084 */         return LongConstant.fromValue(left.intValue() | right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/* 1086 */       case 9: } break;
/*      */     case 7:
/* 1088 */       switch (rightId) { case 2:
/* 1089 */         return LongConstant.fromValue(left.longValue() | right.charValue());
/*      */       case 3:
/* 1090 */         return LongConstant.fromValue(left.longValue() | right.byteValue());
/*      */       case 4:
/* 1091 */         return LongConstant.fromValue(left.longValue() | right.shortValue());
/*      */       case 10:
/* 1092 */         return LongConstant.fromValue(left.longValue() | right.intValue());
/*      */       case 7:
/* 1093 */         return LongConstant.fromValue(left.longValue() | right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/*      */       case 9: } case 6:
/*      */     case 8:
/* 1096 */     case 9: } return NotAConstant;
/*      */   }
/*      */ 
/*      */   public static final Constant computeConstantOperationOR_OR(Constant left, int leftId, Constant right, int rightId) {
/* 1100 */     return BooleanConstant.fromValue((left.booleanValue()) || (right.booleanValue()));
/*      */   }
/*      */ 
/*      */   public static final Constant computeConstantOperationPLUS(Constant left, int leftId, Constant right, int rightId) {
/* 1104 */     switch (leftId) {
/*      */     case 1:
/* 1106 */       if (rightId != 11) break;
/* 1107 */       return StringConstant.fromValue(left.stringValue() + right.stringValue());
/*      */     case 5:
/* 1111 */       if (rightId != 11) break;
/* 1112 */       return StringConstant.fromValue(left.stringValue() + right.stringValue());
/*      */     case 2:
/* 1116 */       switch (rightId) { case 2:
/* 1117 */         return IntConstant.fromValue(left.charValue() + right.charValue());
/*      */       case 9:
/* 1118 */         return FloatConstant.fromValue(left.charValue() + right.floatValue());
/*      */       case 8:
/* 1119 */         return DoubleConstant.fromValue(left.charValue() + right.doubleValue());
/*      */       case 3:
/* 1120 */         return IntConstant.fromValue(left.charValue() + right.byteValue());
/*      */       case 4:
/* 1121 */         return IntConstant.fromValue(left.charValue() + right.shortValue());
/*      */       case 10:
/* 1122 */         return IntConstant.fromValue(left.charValue() + right.intValue());
/*      */       case 7:
/* 1123 */         return LongConstant.fromValue(left.charValue() + right.longValue());
/*      */       case 11:
/* 1124 */         return StringConstant.fromValue(left.stringValue() + right.stringValue());
/*      */       case 5:
/* 1126 */       case 6: } break;
/*      */     case 9:
/* 1128 */       switch (rightId) { case 2:
/* 1129 */         return FloatConstant.fromValue(left.floatValue() + right.charValue());
/*      */       case 9:
/* 1130 */         return FloatConstant.fromValue(left.floatValue() + right.floatValue());
/*      */       case 8:
/* 1131 */         return DoubleConstant.fromValue(left.floatValue() + right.doubleValue());
/*      */       case 3:
/* 1132 */         return FloatConstant.fromValue(left.floatValue() + right.byteValue());
/*      */       case 4:
/* 1133 */         return FloatConstant.fromValue(left.floatValue() + right.shortValue());
/*      */       case 10:
/* 1134 */         return FloatConstant.fromValue(left.floatValue() + right.intValue());
/*      */       case 7:
/* 1135 */         return FloatConstant.fromValue(left.floatValue() + (float)right.longValue());
/*      */       case 11:
/* 1136 */         return StringConstant.fromValue(left.stringValue() + right.stringValue());
/*      */       case 5:
/* 1138 */       case 6: } break;
/*      */     case 8:
/* 1140 */       switch (rightId) { case 2:
/* 1141 */         return DoubleConstant.fromValue(left.doubleValue() + right.charValue());
/*      */       case 9:
/* 1142 */         return DoubleConstant.fromValue(left.doubleValue() + right.floatValue());
/*      */       case 8:
/* 1143 */         return DoubleConstant.fromValue(left.doubleValue() + right.doubleValue());
/*      */       case 3:
/* 1144 */         return DoubleConstant.fromValue(left.doubleValue() + right.byteValue());
/*      */       case 4:
/* 1145 */         return DoubleConstant.fromValue(left.doubleValue() + right.shortValue());
/*      */       case 10:
/* 1146 */         return DoubleConstant.fromValue(left.doubleValue() + right.intValue());
/*      */       case 7:
/* 1147 */         return DoubleConstant.fromValue(left.doubleValue() + right.longValue());
/*      */       case 11:
/* 1148 */         return StringConstant.fromValue(left.stringValue() + right.stringValue());
/*      */       case 5:
/* 1150 */       case 6: } break;
/*      */     case 3:
/* 1152 */       switch (rightId) { case 2:
/* 1153 */         return IntConstant.fromValue(left.byteValue() + right.charValue());
/*      */       case 9:
/* 1154 */         return FloatConstant.fromValue(left.byteValue() + right.floatValue());
/*      */       case 8:
/* 1155 */         return DoubleConstant.fromValue(left.byteValue() + right.doubleValue());
/*      */       case 3:
/* 1156 */         return IntConstant.fromValue(left.byteValue() + right.byteValue());
/*      */       case 4:
/* 1157 */         return IntConstant.fromValue(left.byteValue() + right.shortValue());
/*      */       case 10:
/* 1158 */         return IntConstant.fromValue(left.byteValue() + right.intValue());
/*      */       case 7:
/* 1159 */         return LongConstant.fromValue(left.byteValue() + right.longValue());
/*      */       case 11:
/* 1160 */         return StringConstant.fromValue(left.stringValue() + right.stringValue());
/*      */       case 5:
/* 1162 */       case 6: } break;
/*      */     case 4:
/* 1164 */       switch (rightId) { case 2:
/* 1165 */         return IntConstant.fromValue(left.shortValue() + right.charValue());
/*      */       case 9:
/* 1166 */         return FloatConstant.fromValue(left.shortValue() + right.floatValue());
/*      */       case 8:
/* 1167 */         return DoubleConstant.fromValue(left.shortValue() + right.doubleValue());
/*      */       case 3:
/* 1168 */         return IntConstant.fromValue(left.shortValue() + right.byteValue());
/*      */       case 4:
/* 1169 */         return IntConstant.fromValue(left.shortValue() + right.shortValue());
/*      */       case 10:
/* 1170 */         return IntConstant.fromValue(left.shortValue() + right.intValue());
/*      */       case 7:
/* 1171 */         return LongConstant.fromValue(left.shortValue() + right.longValue());
/*      */       case 11:
/* 1172 */         return StringConstant.fromValue(left.stringValue() + right.stringValue());
/*      */       case 5:
/* 1174 */       case 6: } break;
/*      */     case 10:
/* 1176 */       switch (rightId) { case 2:
/* 1177 */         return IntConstant.fromValue(left.intValue() + right.charValue());
/*      */       case 9:
/* 1178 */         return FloatConstant.fromValue(left.intValue() + right.floatValue());
/*      */       case 8:
/* 1179 */         return DoubleConstant.fromValue(left.intValue() + right.doubleValue());
/*      */       case 3:
/* 1180 */         return IntConstant.fromValue(left.intValue() + right.byteValue());
/*      */       case 4:
/* 1181 */         return IntConstant.fromValue(left.intValue() + right.shortValue());
/*      */       case 10:
/* 1182 */         return IntConstant.fromValue(left.intValue() + right.intValue());
/*      */       case 7:
/* 1183 */         return LongConstant.fromValue(left.intValue() + right.longValue());
/*      */       case 11:
/* 1184 */         return StringConstant.fromValue(left.stringValue() + right.stringValue());
/*      */       case 5:
/* 1186 */       case 6: } break;
/*      */     case 7:
/* 1188 */       switch (rightId) { case 2:
/* 1189 */         return LongConstant.fromValue(left.longValue() + right.charValue());
/*      */       case 9:
/* 1190 */         return FloatConstant.fromValue((float)left.longValue() + right.floatValue());
/*      */       case 8:
/* 1191 */         return DoubleConstant.fromValue(left.longValue() + right.doubleValue());
/*      */       case 3:
/* 1192 */         return LongConstant.fromValue(left.longValue() + right.byteValue());
/*      */       case 4:
/* 1193 */         return LongConstant.fromValue(left.longValue() + right.shortValue());
/*      */       case 10:
/* 1194 */         return LongConstant.fromValue(left.longValue() + right.intValue());
/*      */       case 7:
/* 1195 */         return LongConstant.fromValue(left.longValue() + right.longValue());
/*      */       case 11:
/* 1196 */         return StringConstant.fromValue(left.stringValue() + right.stringValue());
/*      */       case 5:
/* 1198 */       case 6: } break;
/*      */     case 11:
/* 1200 */       switch (rightId) { case 2:
/* 1201 */         return StringConstant.fromValue(left.stringValue() + String.valueOf(right.charValue()));
/*      */       case 9:
/* 1202 */         return StringConstant.fromValue(left.stringValue() + String.valueOf(right.floatValue()));
/*      */       case 8:
/* 1203 */         return StringConstant.fromValue(left.stringValue() + String.valueOf(right.doubleValue()));
/*      */       case 3:
/* 1204 */         return StringConstant.fromValue(left.stringValue() + String.valueOf(right.byteValue()));
/*      */       case 4:
/* 1205 */         return StringConstant.fromValue(left.stringValue() + String.valueOf(right.shortValue()));
/*      */       case 10:
/* 1206 */         return StringConstant.fromValue(left.stringValue() + String.valueOf(right.intValue()));
/*      */       case 7:
/* 1207 */         return StringConstant.fromValue(left.stringValue() + String.valueOf(right.longValue()));
/*      */       case 11:
/* 1208 */         return StringConstant.fromValue(left.stringValue() + right.stringValue());
/*      */       case 5:
/* 1209 */         return StringConstant.fromValue(left.stringValue() + right.booleanValue());
/*      */       case 6:
/*      */       }
/*      */ 
/*      */     case 6:
/*      */     }
/*      */ 
/* 1225 */     return NotAConstant;
/*      */   }
/*      */ 
/*      */   public static final Constant computeConstantOperationREMAINDER(Constant left, int leftId, Constant right, int rightId) {
/* 1229 */     switch (leftId) {
/*      */     case 2:
/* 1231 */       switch (rightId) { case 2:
/* 1232 */         return IntConstant.fromValue(left.charValue() % right.charValue());
/*      */       case 9:
/* 1233 */         return FloatConstant.fromValue(left.charValue() % right.floatValue());
/*      */       case 8:
/* 1234 */         return DoubleConstant.fromValue(left.charValue() % right.doubleValue());
/*      */       case 3:
/* 1235 */         return IntConstant.fromValue(left.charValue() % right.byteValue());
/*      */       case 4:
/* 1236 */         return IntConstant.fromValue(left.charValue() % right.shortValue());
/*      */       case 10:
/* 1237 */         return IntConstant.fromValue(left.charValue() % right.intValue());
/*      */       case 7:
/* 1238 */         return LongConstant.fromValue(left.charValue() % right.longValue());
/*      */       case 5:
/* 1240 */       case 6: } break;
/*      */     case 9:
/* 1242 */       switch (rightId) { case 2:
/* 1243 */         return FloatConstant.fromValue(left.floatValue() % right.charValue());
/*      */       case 9:
/* 1244 */         return FloatConstant.fromValue(left.floatValue() % right.floatValue());
/*      */       case 8:
/* 1245 */         return DoubleConstant.fromValue(left.floatValue() % right.doubleValue());
/*      */       case 3:
/* 1246 */         return FloatConstant.fromValue(left.floatValue() % right.byteValue());
/*      */       case 4:
/* 1247 */         return FloatConstant.fromValue(left.floatValue() % right.shortValue());
/*      */       case 10:
/* 1248 */         return FloatConstant.fromValue(left.floatValue() % right.intValue());
/*      */       case 7:
/* 1249 */         return FloatConstant.fromValue(left.floatValue() % (float)right.longValue());
/*      */       case 5:
/* 1251 */       case 6: } break;
/*      */     case 8:
/* 1253 */       switch (rightId) { case 2:
/* 1254 */         return DoubleConstant.fromValue(left.doubleValue() % right.charValue());
/*      */       case 9:
/* 1255 */         return DoubleConstant.fromValue(left.doubleValue() % right.floatValue());
/*      */       case 8:
/* 1256 */         return DoubleConstant.fromValue(left.doubleValue() % right.doubleValue());
/*      */       case 3:
/* 1257 */         return DoubleConstant.fromValue(left.doubleValue() % right.byteValue());
/*      */       case 4:
/* 1258 */         return DoubleConstant.fromValue(left.doubleValue() % right.shortValue());
/*      */       case 10:
/* 1259 */         return DoubleConstant.fromValue(left.doubleValue() % right.intValue());
/*      */       case 7:
/* 1260 */         return DoubleConstant.fromValue(left.doubleValue() % right.longValue());
/*      */       case 5:
/* 1262 */       case 6: } break;
/*      */     case 3:
/* 1264 */       switch (rightId) { case 2:
/* 1265 */         return IntConstant.fromValue(left.byteValue() % right.charValue());
/*      */       case 9:
/* 1266 */         return FloatConstant.fromValue(left.byteValue() % right.floatValue());
/*      */       case 8:
/* 1267 */         return DoubleConstant.fromValue(left.byteValue() % right.doubleValue());
/*      */       case 3:
/* 1268 */         return IntConstant.fromValue(left.byteValue() % right.byteValue());
/*      */       case 4:
/* 1269 */         return IntConstant.fromValue(left.byteValue() % right.shortValue());
/*      */       case 10:
/* 1270 */         return IntConstant.fromValue(left.byteValue() % right.intValue());
/*      */       case 7:
/* 1271 */         return LongConstant.fromValue(left.byteValue() % right.longValue());
/*      */       case 5:
/* 1273 */       case 6: } break;
/*      */     case 4:
/* 1275 */       switch (rightId) { case 2:
/* 1276 */         return IntConstant.fromValue(left.shortValue() % right.charValue());
/*      */       case 9:
/* 1277 */         return FloatConstant.fromValue(left.shortValue() % right.floatValue());
/*      */       case 8:
/* 1278 */         return DoubleConstant.fromValue(left.shortValue() % right.doubleValue());
/*      */       case 3:
/* 1279 */         return IntConstant.fromValue(left.shortValue() % right.byteValue());
/*      */       case 4:
/* 1280 */         return IntConstant.fromValue(left.shortValue() % right.shortValue());
/*      */       case 10:
/* 1281 */         return IntConstant.fromValue(left.shortValue() % right.intValue());
/*      */       case 7:
/* 1282 */         return LongConstant.fromValue(left.shortValue() % right.longValue());
/*      */       case 5:
/* 1284 */       case 6: } break;
/*      */     case 10:
/* 1286 */       switch (rightId) { case 2:
/* 1287 */         return IntConstant.fromValue(left.intValue() % right.charValue());
/*      */       case 9:
/* 1288 */         return FloatConstant.fromValue(left.intValue() % right.floatValue());
/*      */       case 8:
/* 1289 */         return DoubleConstant.fromValue(left.intValue() % right.doubleValue());
/*      */       case 3:
/* 1290 */         return IntConstant.fromValue(left.intValue() % right.byteValue());
/*      */       case 4:
/* 1291 */         return IntConstant.fromValue(left.intValue() % right.shortValue());
/*      */       case 10:
/* 1292 */         return IntConstant.fromValue(left.intValue() % right.intValue());
/*      */       case 7:
/* 1293 */         return LongConstant.fromValue(left.intValue() % right.longValue());
/*      */       case 5:
/* 1295 */       case 6: } break;
/*      */     case 7:
/* 1297 */       switch (rightId) { case 2:
/* 1298 */         return LongConstant.fromValue(left.longValue() % right.charValue());
/*      */       case 9:
/* 1299 */         return FloatConstant.fromValue((float)left.longValue() % right.floatValue());
/*      */       case 8:
/* 1300 */         return DoubleConstant.fromValue(left.longValue() % right.doubleValue());
/*      */       case 3:
/* 1301 */         return LongConstant.fromValue(left.longValue() % right.byteValue());
/*      */       case 4:
/* 1302 */         return LongConstant.fromValue(left.longValue() % right.shortValue());
/*      */       case 10:
/* 1303 */         return LongConstant.fromValue(left.longValue() % right.intValue());
/*      */       case 7:
/* 1304 */         return LongConstant.fromValue(left.longValue() % right.longValue());
/*      */       case 5:
/*      */       case 6: } case 5:
/* 1307 */     case 6: } return NotAConstant;
/*      */   }
/*      */ 
/*      */   public static final Constant computeConstantOperationRIGHT_SHIFT(Constant left, int leftId, Constant right, int rightId) {
/* 1311 */     switch (leftId) {
/*      */     case 2:
/* 1313 */       switch (rightId) { case 2:
/* 1314 */         return IntConstant.fromValue(left.charValue() >> right.charValue());
/*      */       case 3:
/* 1315 */         return IntConstant.fromValue(left.charValue() >> right.byteValue());
/*      */       case 4:
/* 1316 */         return IntConstant.fromValue(left.charValue() >> right.shortValue());
/*      */       case 10:
/* 1317 */         return IntConstant.fromValue(left.charValue() >> right.intValue());
/*      */       case 7:
/* 1318 */         return IntConstant.fromValue(left.charValue() >> (int)right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/* 1320 */       case 9: } break;
/*      */     case 3:
/* 1322 */       switch (rightId) { case 2:
/* 1323 */         return IntConstant.fromValue(left.byteValue() >> right.charValue());
/*      */       case 3:
/* 1324 */         return IntConstant.fromValue(left.byteValue() >> right.byteValue());
/*      */       case 4:
/* 1325 */         return IntConstant.fromValue(left.byteValue() >> right.shortValue());
/*      */       case 10:
/* 1326 */         return IntConstant.fromValue(left.byteValue() >> right.intValue());
/*      */       case 7:
/* 1327 */         return IntConstant.fromValue(left.byteValue() >> (int)right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/* 1329 */       case 9: } break;
/*      */     case 4:
/* 1331 */       switch (rightId) { case 2:
/* 1332 */         return IntConstant.fromValue(left.shortValue() >> right.charValue());
/*      */       case 3:
/* 1333 */         return IntConstant.fromValue(left.shortValue() >> right.byteValue());
/*      */       case 4:
/* 1334 */         return IntConstant.fromValue(left.shortValue() >> right.shortValue());
/*      */       case 10:
/* 1335 */         return IntConstant.fromValue(left.shortValue() >> right.intValue());
/*      */       case 7:
/* 1336 */         return IntConstant.fromValue(left.shortValue() >> (int)right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/* 1338 */       case 9: } break;
/*      */     case 10:
/* 1340 */       switch (rightId) { case 2:
/* 1341 */         return IntConstant.fromValue(left.intValue() >> right.charValue());
/*      */       case 3:
/* 1342 */         return IntConstant.fromValue(left.intValue() >> right.byteValue());
/*      */       case 4:
/* 1343 */         return IntConstant.fromValue(left.intValue() >> right.shortValue());
/*      */       case 10:
/* 1344 */         return IntConstant.fromValue(left.intValue() >> right.intValue());
/*      */       case 7:
/* 1345 */         return IntConstant.fromValue(left.intValue() >> (int)right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/* 1347 */       case 9: } break;
/*      */     case 7:
/* 1349 */       switch (rightId) { case 2:
/* 1350 */         return LongConstant.fromValue(left.longValue() >> right.charValue());
/*      */       case 3:
/* 1351 */         return LongConstant.fromValue(left.longValue() >> right.byteValue());
/*      */       case 4:
/* 1352 */         return LongConstant.fromValue(left.longValue() >> right.shortValue());
/*      */       case 10:
/* 1353 */         return LongConstant.fromValue(left.longValue() >> right.intValue());
/*      */       case 7:
/* 1354 */         return LongConstant.fromValue(left.longValue() >> (int)right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/*      */       case 9: } case 5:
/*      */     case 6:
/*      */     case 8:
/* 1357 */     case 9: } return NotAConstant;
/*      */   }
/*      */ 
/*      */   public static final Constant computeConstantOperationUNSIGNED_RIGHT_SHIFT(Constant left, int leftId, Constant right, int rightId) {
/* 1361 */     switch (leftId) {
/*      */     case 2:
/* 1363 */       switch (rightId) { case 2:
/* 1364 */         return IntConstant.fromValue(left.charValue() >>> right.charValue());
/*      */       case 3:
/* 1365 */         return IntConstant.fromValue(left.charValue() >>> right.byteValue());
/*      */       case 4:
/* 1366 */         return IntConstant.fromValue(left.charValue() >>> right.shortValue());
/*      */       case 10:
/* 1367 */         return IntConstant.fromValue(left.charValue() >>> right.intValue());
/*      */       case 7:
/* 1368 */         return IntConstant.fromValue(left.charValue() >>> (int)right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/* 1370 */       case 9: } break;
/*      */     case 3:
/* 1372 */       switch (rightId) { case 2:
/* 1373 */         return IntConstant.fromValue(left.byteValue() >>> right.charValue());
/*      */       case 3:
/* 1374 */         return IntConstant.fromValue(left.byteValue() >>> right.byteValue());
/*      */       case 4:
/* 1375 */         return IntConstant.fromValue(left.byteValue() >>> right.shortValue());
/*      */       case 10:
/* 1376 */         return IntConstant.fromValue(left.byteValue() >>> right.intValue());
/*      */       case 7:
/* 1377 */         return IntConstant.fromValue(left.byteValue() >>> (int)right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/* 1379 */       case 9: } break;
/*      */     case 4:
/* 1381 */       switch (rightId) { case 2:
/* 1382 */         return IntConstant.fromValue(left.shortValue() >>> right.charValue());
/*      */       case 3:
/* 1383 */         return IntConstant.fromValue(left.shortValue() >>> right.byteValue());
/*      */       case 4:
/* 1384 */         return IntConstant.fromValue(left.shortValue() >>> right.shortValue());
/*      */       case 10:
/* 1385 */         return IntConstant.fromValue(left.shortValue() >>> right.intValue());
/*      */       case 7:
/* 1386 */         return IntConstant.fromValue(left.shortValue() >>> (int)right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/* 1388 */       case 9: } break;
/*      */     case 10:
/* 1390 */       switch (rightId) { case 2:
/* 1391 */         return IntConstant.fromValue(left.intValue() >>> right.charValue());
/*      */       case 3:
/* 1392 */         return IntConstant.fromValue(left.intValue() >>> right.byteValue());
/*      */       case 4:
/* 1393 */         return IntConstant.fromValue(left.intValue() >>> right.shortValue());
/*      */       case 10:
/* 1394 */         return IntConstant.fromValue(left.intValue() >>> right.intValue());
/*      */       case 7:
/* 1395 */         return IntConstant.fromValue(left.intValue() >>> (int)right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/* 1397 */       case 9: } break;
/*      */     case 7:
/* 1399 */       switch (rightId) { case 2:
/* 1400 */         return LongConstant.fromValue(left.longValue() >>> right.charValue());
/*      */       case 3:
/* 1401 */         return LongConstant.fromValue(left.longValue() >>> right.byteValue());
/*      */       case 4:
/* 1402 */         return LongConstant.fromValue(left.longValue() >>> right.shortValue());
/*      */       case 10:
/* 1403 */         return LongConstant.fromValue(left.longValue() >>> right.intValue());
/*      */       case 7:
/* 1404 */         return LongConstant.fromValue(left.longValue() >>> (int)right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/*      */       case 9: } case 5:
/*      */     case 6:
/*      */     case 8:
/* 1407 */     case 9: } return NotAConstant;
/*      */   }
/*      */ 
/*      */   public static final Constant computeConstantOperationXOR(Constant left, int leftId, Constant right, int rightId) {
/* 1411 */     switch (leftId) { case 5:
/* 1412 */       return BooleanConstant.fromValue(left.booleanValue() ^ right.booleanValue());
/*      */     case 2:
/* 1414 */       switch (rightId) { case 2:
/* 1415 */         return IntConstant.fromValue(left.charValue() ^ right.charValue());
/*      */       case 3:
/* 1416 */         return IntConstant.fromValue(left.charValue() ^ right.byteValue());
/*      */       case 4:
/* 1417 */         return IntConstant.fromValue(left.charValue() ^ right.shortValue());
/*      */       case 10:
/* 1418 */         return IntConstant.fromValue(left.charValue() ^ right.intValue());
/*      */       case 7:
/* 1419 */         return LongConstant.fromValue(left.charValue() ^ right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/* 1421 */       case 9: } break;
/*      */     case 3:
/* 1423 */       switch (rightId) { case 2:
/* 1424 */         return IntConstant.fromValue(left.byteValue() ^ right.charValue());
/*      */       case 3:
/* 1425 */         return IntConstant.fromValue(left.byteValue() ^ right.byteValue());
/*      */       case 4:
/* 1426 */         return IntConstant.fromValue(left.byteValue() ^ right.shortValue());
/*      */       case 10:
/* 1427 */         return IntConstant.fromValue(left.byteValue() ^ right.intValue());
/*      */       case 7:
/* 1428 */         return LongConstant.fromValue(left.byteValue() ^ right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/* 1430 */       case 9: } break;
/*      */     case 4:
/* 1432 */       switch (rightId) { case 2:
/* 1433 */         return IntConstant.fromValue(left.shortValue() ^ right.charValue());
/*      */       case 3:
/* 1434 */         return IntConstant.fromValue(left.shortValue() ^ right.byteValue());
/*      */       case 4:
/* 1435 */         return IntConstant.fromValue(left.shortValue() ^ right.shortValue());
/*      */       case 10:
/* 1436 */         return IntConstant.fromValue(left.shortValue() ^ right.intValue());
/*      */       case 7:
/* 1437 */         return LongConstant.fromValue(left.shortValue() ^ right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/* 1439 */       case 9: } break;
/*      */     case 10:
/* 1441 */       switch (rightId) { case 2:
/* 1442 */         return IntConstant.fromValue(left.intValue() ^ right.charValue());
/*      */       case 3:
/* 1443 */         return IntConstant.fromValue(left.intValue() ^ right.byteValue());
/*      */       case 4:
/* 1444 */         return IntConstant.fromValue(left.intValue() ^ right.shortValue());
/*      */       case 10:
/* 1445 */         return IntConstant.fromValue(left.intValue() ^ right.intValue());
/*      */       case 7:
/* 1446 */         return LongConstant.fromValue(left.intValue() ^ right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/* 1448 */       case 9: } break;
/*      */     case 7:
/* 1450 */       switch (rightId) { case 2:
/* 1451 */         return LongConstant.fromValue(left.longValue() ^ right.charValue());
/*      */       case 3:
/* 1452 */         return LongConstant.fromValue(left.longValue() ^ right.byteValue());
/*      */       case 4:
/* 1453 */         return LongConstant.fromValue(left.longValue() ^ right.shortValue());
/*      */       case 10:
/* 1454 */         return LongConstant.fromValue(left.longValue() ^ right.intValue());
/*      */       case 7:
/* 1455 */         return LongConstant.fromValue(left.longValue() ^ right.longValue());
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/*      */       case 9: } case 6:
/*      */     case 8:
/* 1458 */     case 9: } return NotAConstant;
/*      */   }
/*      */ 
/*      */   public double doubleValue() {
/* 1462 */     throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[] { typeName(), "double" }));
/*      */   }
/*      */ 
/*      */   public float floatValue() {
/* 1466 */     throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[] { typeName(), "float" }));
/*      */   }
/*      */ 
/*      */   public boolean hasSameValue(Constant otherConstant)
/*      */   {
/* 1474 */     if (this == otherConstant)
/* 1475 */       return true;
/*      */     int typeID;
/* 1477 */     if ((typeID = typeID()) != otherConstant.typeID())
/* 1478 */       return false;
/* 1479 */     switch (typeID) {
/*      */     case 5:
/* 1481 */       return booleanValue() == otherConstant.booleanValue();
/*      */     case 3:
/* 1483 */       return byteValue() == otherConstant.byteValue();
/*      */     case 2:
/* 1485 */       return charValue() == otherConstant.charValue();
/*      */     case 8:
/* 1487 */       return doubleValue() == otherConstant.doubleValue();
/*      */     case 9:
/* 1489 */       return floatValue() == otherConstant.floatValue();
/*      */     case 10:
/* 1491 */       return intValue() == otherConstant.intValue();
/*      */     case 4:
/* 1493 */       return shortValue() == otherConstant.shortValue();
/*      */     case 7:
/* 1495 */       return longValue() == otherConstant.longValue();
/*      */     case 11:
/* 1497 */       String value = stringValue();
/* 1498 */       return value == null ? 
/* 1499 */         false : otherConstant.stringValue() == null ? true : 
/* 1500 */         value.equals(otherConstant.stringValue());
/*      */     case 6:
/* 1502 */     }return false;
/*      */   }
/*      */ 
/*      */   public int intValue() {
/* 1506 */     throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[] { typeName(), "int" }));
/*      */   }
/*      */ 
/*      */   public long longValue() {
/* 1510 */     throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[] { typeName(), "long" }));
/*      */   }
/*      */ 
/*      */   public short shortValue() {
/* 1514 */     throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotConvertedTo, new String[] { typeName(), "short" }));
/*      */   }
/*      */ 
/*      */   public String stringValue() {
/* 1518 */     throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotConvertedTo, new String[] { typeName(), "String" }));
/*      */   }
/*      */ 
/*      */   public String toString() {
/* 1522 */     if (this == NotAConstant) return "(Constant) NotAConstant";
/* 1523 */     return super.toString();
/*      */   }
/*      */   public abstract int typeID();
/*      */ 
/* 1528 */   public String typeName() { switch (typeID()) { case 10:
/* 1529 */       return "int";
/*      */     case 3:
/* 1530 */       return "byte";
/*      */     case 4:
/* 1531 */       return "short";
/*      */     case 2:
/* 1532 */       return "char";
/*      */     case 9:
/* 1533 */       return "float";
/*      */     case 8:
/* 1534 */       return "double";
/*      */     case 5:
/* 1535 */       return "boolean";
/*      */     case 7:
/* 1536 */       return "long";
/*      */     case 11:
/* 1537 */       return "java.lang.String";
/*      */     case 12:
/* 1538 */       return "null";
/* 1539 */     case 6: } return "unknown";
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.impl.Constant
 * JD-Core Version:    0.6.0
 */