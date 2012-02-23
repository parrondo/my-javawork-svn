/*     */ package com.dukascopy.dds2.greed.connector.parser.util;
/*     */ 
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.api.connector.helpers.ColorHelpers;
/*     */ import com.dukascopy.dds2.greed.connector.helpers.ArrayHelpers;
/*     */ import com.dukascopy.dds2.greed.connector.parser.javacc.ASTNode;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class VariableDeclaration extends Declaration
/*     */   implements Cloneable
/*     */ {
/*  14 */   private static final String[] numericTypes = { "int", "long", "double", "datetime" };
/*  15 */   private static final String[] naturalTypes = { "int", "long", "datetime" };
/*  16 */   private static final String[] primitiveTypes = { "int", "long", "double", "datetime", "bool" };
/*     */ 
/*  19 */   boolean isDefine = false;
/*  20 */   boolean isCounter = false;
/*  21 */   boolean isParam = false;
/*     */ 
/*  23 */   boolean isArray = false;
/*  24 */   boolean isEmptyArray = true;
/*  25 */   boolean hasNew = false;
/*  26 */   boolean isColor = false;
/*     */ 
/*  28 */   boolean isReference = false;
/*  29 */   boolean isDeclaredUpper = false;
/*  30 */   boolean changeParent = false;
/*     */ 
/*  32 */   boolean isExpression = false;
/*  33 */   ASTNode expressionNode = null;
/*  34 */   String prefix = "";
/*     */ 
/*  36 */   String value = "";
/*  37 */   List<String> arrayvalues = new ArrayList();
/*  38 */   List<String> dimention = new ArrayList();
/*     */ 
/*     */   public final Object clone()
/*     */   {
/*  42 */     VariableDeclaration clone = new VariableDeclaration();
/*  43 */     clone.setType(getType());
/*  44 */     clone.setName(getName());
/*  45 */     clone.setValue(getValue());
/*  46 */     clone.setCounter(isCounter());
/*  47 */     clone.setPrefix(getPrefix());
/*  48 */     clone.setParent(getParent());
/*  49 */     clone.setExpressionNode(getExpressionNode());
/*  50 */     return clone;
/*     */   }
/*     */ 
/*     */   public String getValue() {
/*  54 */     if (this.isArray) {
/*  55 */       return null;
/*     */     }
/*  57 */     if (getType().equals("long")) {
/*  58 */       if (this.value == null) {
/*  59 */         this.value = "0";
/*     */       }
/*  61 */       if ((this.value.charAt(this.value.length() - 1) != 'L') && (this.value.charAt(this.value.length() - 1) != 'l')) {
/*  62 */         return new StringBuilder().append(this.value).append("L").toString();
/*     */       }
/*  64 */       return this.value;
/*     */     }
/*     */ 
/*  67 */     return this.value;
/*     */   }
/*     */ 
/*     */   private void setValue(String value, boolean validate)
/*     */   {
/*  73 */     if (!this.isArray)
/*  74 */       this.value = value;
/*     */   }
/*     */ 
/*     */   public void setValue(String value)
/*     */   {
/*  82 */     boolean isZeroStart = false;
/*  83 */     int zeroLength = 0;
/*  84 */     if ((isNatural()) && 
/*  85 */       (value.startsWith("0")) && (!value.startsWith("0x")) && (value.length() > 1)) {
/*  86 */       String withoutZeroValue = new String(value);
/*  87 */       isZeroStart = true;
/*  88 */       while (withoutZeroValue.startsWith("0")) {
/*  89 */         withoutZeroValue = withoutZeroValue.substring(1);
/*  90 */         zeroLength++;
/*     */       }
/*     */     }
/*     */ 
/*  94 */     if (!isZeroStart)
/*  95 */       setValue(value, true);
/*     */     else
/*  97 */       setValue(value.substring(zeroLength), true);
/*     */   }
/*     */ 
/*     */   public String endText()
/*     */   {
/* 103 */     return "";
/*     */   }
/*     */ 
/*     */   private Integer expressionToInt(String expression) {
/* 107 */     Integer i = null;
/*     */     try {
/* 109 */       i = new Integer(expression);
/*     */     }
/*     */     catch (Exception ex) {
/*     */     }
/* 113 */     return i;
/*     */   }
/*     */ 
/*     */   private Long expressionToLong(String expression) {
/* 117 */     Long i = null;
/*     */     try {
/* 119 */       i = new Long(expression);
/*     */     }
/*     */     catch (Exception ex) {
/*     */     }
/* 123 */     return i;
/*     */   }
/*     */ 
/*     */   private Double expressionToDouble(String expression) {
/* 127 */     Double i = null;
/*     */     try {
/* 129 */       i = new Double(expression);
/*     */     }
/*     */     catch (Exception ex) {
/*     */     }
/* 133 */     return i;
/*     */   }
/*     */ 
/*     */   private void checkExpression(String expression) {
/* 137 */     if (isNumeric())
/* 138 */       if (getType().equals("int")) {
/* 139 */         Integer i = expressionToInt(expression);
/* 140 */         if (i == null) {
/* 141 */           Long l = expressionToLong(expression);
/* 142 */           if (l == null) {
/* 143 */             Double d = expressionToDouble(expression);
/* 144 */             if (d != null) {
/* 145 */               setType("double");
/* 146 */               setValue(expression);
/*     */             }
/*     */           } else {
/* 149 */             setType("long");
/* 150 */             setValue(new StringBuilder().append(expression).append("L").toString());
/*     */           }
/*     */         } else {
/* 153 */           setValue(expression);
/*     */         }
/* 155 */       } else if (getType().equals("long")) {
/* 156 */         Long l = expressionToLong(expression);
/* 157 */         if (l == null) {
/* 158 */           Integer i = expressionToInt(expression);
/* 159 */           if (i == null) {
/* 160 */             Double d = expressionToDouble(expression);
/* 161 */             if (d != null) {
/* 162 */               setType("double");
/* 163 */               setValue(expression);
/*     */             }
/*     */           } else {
/* 166 */             setType("int");
/* 167 */             setValue(expression);
/*     */           }
/*     */         } else {
/* 170 */           setValue(new StringBuilder().append(expression).append("L").toString());
/*     */         }
/* 172 */       } else if (getType().equals("double")) {
/* 173 */         Double d = expressionToDouble(expression);
/* 174 */         if (d == null) {
/* 175 */           Integer i = expressionToInt(expression);
/* 176 */           if (i == null) {
/* 177 */             Long l = expressionToLong(expression);
/* 178 */             if (l != null) {
/* 179 */               setType("long");
/* 180 */               setValue(new StringBuilder().append(expression).append("L").toString());
/*     */             }
/*     */           } else {
/* 183 */             setType("int");
/* 184 */             setValue(expression);
/*     */           }
/*     */         } else {
/* 187 */           setValue(expression);
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   private String getEmptyArrayDim()
/*     */   {
/* 194 */     StringBuilder buf = new StringBuilder();
/* 195 */     for (int i = 0; i < getArrayDimentions(); i++) {
/* 196 */       if (getArrayDimention(i) != null) {
/* 197 */         buf.append("[]");
/*     */       }
/*     */     }
/* 200 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   private String getInitArrayDim(boolean isEmptyArray) {
/* 204 */     StringBuilder buf = new StringBuilder();
/* 205 */     for (int i = 0; i < getArrayDimentions(); i++) {
/* 206 */       String dimentionValue = (String)this.dimention.get(i);
/* 207 */       if (i < 1) {
/* 208 */         buf.append(new StringBuilder().append(" = new ").append(getType()).toString());
/*     */       }
/* 210 */       buf.append("[");
/* 211 */       if ((dimentionValue != null) && (!dimentionValue.isEmpty()))
/* 212 */         buf.append(dimentionValue);
/* 213 */       else if (i == 0) {
/* 214 */         buf.append("0");
/*     */       }
/* 216 */       buf.append("]");
/*     */     }
/* 218 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   private String printDefine() {
/* 222 */     StringBuilder buf = new StringBuilder();
/* 223 */     buf.append("public static final ");
/* 224 */     buf.append(getType());
/* 225 */     buf.append(" ");
/* 226 */     buf.append(getName());
/* 227 */     buf.append(" = ");
/* 228 */     buf.append(getValue());
/* 229 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public String startText() {
/* 233 */     validate();
/*     */ 
/* 235 */     StringBuilder buf = new StringBuilder();
/* 236 */     if (!"static".startsWith(this.prefix)) {
/* 237 */       buf.append(new StringBuilder().append(this.prefix).append(" ").toString());
/*     */     }
/*     */ 
/* 244 */     if (isDefine()) {
/* 245 */       buf.append(printDefine());
/* 246 */     } else if (!isArray()) {
/* 247 */       if (!isCounter()) {
/* 248 */         String expression = null;
/* 249 */         if (this.expressionNode != null) {
/* 250 */           expression = DeclarationHelpers.getAssignmentExpression(this.expressionNode);
/* 251 */           checkExpression(expression);
/* 252 */           if (getType().startsWith("bool")) {
/* 253 */             boolean isInteger = true;
/*     */             try {
/* 255 */               new Integer(expression);
/*     */             } catch (Exception ex) {
/* 257 */               isInteger = false;
/*     */             }
/* 259 */             if (isInteger) {
/* 260 */               setType("int");
/*     */             }
/*     */           }
/*     */         }
/* 264 */         buf.append(new StringBuilder().append(getType()).append(" ").append(getName()).toString());
/*     */ 
/* 266 */         if ((getValue() != null) && (!getValue().isEmpty()) && (!isChangeParent()))
/* 267 */           buf.append(new StringBuilder().append("=").append(getTypedValue(getValue())).toString());
/* 268 */         else if ((this.expressionNode != null) && (getParent() == null) && (!isChangeParent()))
/* 269 */           buf.append(new StringBuilder().append("=").append(getTypedValue(expression)).toString());
/*     */         else
/* 271 */           buf.append(initialisation(getType()));
/*     */       }
/*     */     }
/*     */     else {
/* 275 */       boolean initArray = false;
/* 276 */       boolean isIndicatorBuffer = false;
/* 277 */       if ((getType().equals("double")) && (getParent() == null) && (getArrayDimentions() == 1) && (getArrayDimentionAsInt(0) == 0)) {
/* 278 */         buf.append("@IndicatorBuffer (\"\") public ");
/* 279 */         isIndicatorBuffer = true;
/*     */       }
/* 281 */       buf.append(getType());
/* 282 */       for (int i = 0; i < getArrayDimentions(); i++) {
/* 283 */         if (getArrayDimention(i) != null) {
/* 284 */           buf.append("[]");
/*     */         }
/*     */       }
/* 287 */       buf.append(new StringBuilder().append(" ").append(getName()).toString());
/* 288 */       if ((this.arrayvalues.size() > 0) && (!this.hasNew)) {
/* 289 */         buf.append(" = ");
/* 290 */         int pos = 0;
/* 291 */         for (int i = 0; i < getArrayDimentions(); i++) {
/* 292 */           int lastCount = getArrayDimentionAsInt(getArrayDimentions() - 1);
/* 293 */           if (i != getArrayDimentions() - 1) {
/* 294 */             buf.append("{");
/* 295 */             for (int j = 0; j < getArrayDimentionAsInt(i); j++) {
/* 296 */               buf.append("{");
/* 297 */               for (int k = 0; k < lastCount; k++) {
/* 298 */                 if (k > 0) {
/* 299 */                   buf.append(", ");
/*     */                 }
/* 301 */                 buf.append(new StringBuilder().append("(").append(getType()).append(")").toString());
/* 302 */                 if (pos < this.arrayvalues.size())
/* 303 */                   buf.append(getArrayValue(pos));
/*     */                 else {
/* 305 */                   buf.append(getDefaultValue());
/*     */                 }
/* 307 */                 pos++;
/*     */               }
/* 309 */               if (j < getArrayDimentionAsInt(i))
/* 310 */                 buf.append("}, ");
/*     */               else
/* 312 */                 buf.append("}");
/*     */             }
/*     */           }
/*     */           else {
/* 316 */             buf.append("{");
/* 317 */             for (int k = 0; k < lastCount; k++) {
/* 318 */               if (k > 0) {
/* 319 */                 buf.append(", ");
/*     */               }
/* 321 */               buf.append(new StringBuilder().append("(").append(getType()).append(")").toString());
/* 322 */               if (pos < this.arrayvalues.size())
/* 323 */                 buf.append(getArrayValue(pos));
/*     */               else {
/* 325 */                 buf.append(getDefaultValue());
/*     */               }
/* 327 */               pos++;
/*     */             }
/* 329 */             buf.append("}");
/*     */           }
/* 331 */           if ((getArrayDimentions() - i == 1) && (getArrayDimentions() > 1))
/* 332 */             buf.append("}");
/*     */         }
/*     */       }
/* 335 */       else if ((this.arrayvalues.size() < 1) && (!isIndicatorBuffer)) {
/* 336 */         for (int i = 0; i < getArrayDimentions(); i++) {
/* 337 */           String dimentionValue = (String)this.dimention.get(i);
/* 338 */           if (i < 1) {
/* 339 */             buf.append(new StringBuilder().append(" = new ").append(getType()).toString());
/*     */           }
/* 341 */           buf.append("[");
/* 342 */           if ((dimentionValue != null) && (!dimentionValue.isEmpty()))
/* 343 */             buf.append(dimentionValue);
/* 344 */           else if (i < 1) {
/* 345 */             buf.append("ARRAY_MAX_SIZE");
/*     */           }
/* 347 */           buf.append("]");
/* 348 */           initArray = true;
/*     */         }
/* 350 */         if (!initArray)
/* 351 */           buf.append(getInitArrayDim(this.isEmptyArray));
/*     */       }
/* 353 */       if (isIndicatorBuffer) {
/* 354 */         buf.append(" = new double[0]");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 359 */     buf.append(";");
/* 360 */     buf.append("\r\n");
/* 361 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   private void validate() {
/* 365 */     if ((isArray()) && (this.arrayvalues.size() > 0)) {
/* 366 */       String value = (String)this.arrayvalues.get(0);
/*     */ 
/* 368 */       if ((!value.startsWith("\"")) && (!value.startsWith("'")) && (isNumeric()))
/*     */         try {
/* 370 */           if (ColorHelpers.colorFromString(value) != null)
/* 371 */             setType("Color");
/*     */         }
/*     */         catch (JFException e)
/*     */         {
/*     */         }
/*     */     }
/* 377 */     else if ((isArray()) || (getValue() == null) || (getValue().isEmpty()));
/*     */   }
/*     */ 
/*     */   private String initialisation(String type)
/*     */   {
/* 383 */     StringBuilder result = new StringBuilder();
/* 384 */     if (getType() != null) {
/* 385 */       result.append(" = ");
/* 386 */       if (getType().equalsIgnoreCase("color"))
/* 387 */         result.append("CLR_NONE");
/* 388 */       else if (getType().equals("int"))
/* 389 */         result.append("0");
/* 390 */       else if (getType().equals("long"))
/* 391 */         result.append("0L");
/* 392 */       else if (getType().equals("double"))
/* 393 */         result.append("0.0");
/* 394 */       else if (getType().equals("bool"))
/* 395 */         result.append("true");
/* 396 */       else if (getType().equals("datetime"))
/*     */       {
/* 398 */         result.append("System.currentTimeMillis()");
/* 399 */       } else if (getType().equalsIgnoreCase("String")) {
/* 400 */         result.append("\"\"");
/*     */       }
/*     */     }
/* 403 */     return result.toString();
/*     */   }
/*     */ 
/*     */   private String getTypedValue(String expression) {
/* 407 */     StringBuilder result = new StringBuilder();
/* 408 */     if (expression == null) {
/* 409 */       expression = DeclarationHelpers.getAssignmentExpression(this.expressionNode);
/*     */     }
/*     */ 
/* 412 */     result.append(DeclarationHelpers.getTypedValue(expression, getType()));
/*     */ 
/* 446 */     return result.toString();
/*     */   }
/*     */ 
/*     */   public boolean isCounter() {
/* 450 */     return this.isCounter;
/*     */   }
/*     */ 
/*     */   public void setCounter(boolean isCounter) {
/* 454 */     this.isCounter = isCounter;
/*     */   }
/*     */ 
/*     */   public String getPrefix() {
/* 458 */     return this.prefix;
/*     */   }
/*     */ 
/*     */   public void setPrefix(String prefix) {
/* 462 */     this.prefix = prefix;
/*     */   }
/*     */ 
/*     */   public boolean isArray() {
/* 466 */     return this.isArray;
/*     */   }
/*     */ 
/*     */   public void setArray(boolean isArray) {
/* 470 */     this.isArray = isArray;
/*     */   }
/*     */ 
/*     */   public int getArrayDimentions() {
/* 474 */     int result = 0;
/* 475 */     if (this.isArray) {
/* 476 */       result = this.dimention.size();
/*     */     }
/* 478 */     return result;
/*     */   }
/*     */ 
/*     */   public void addArrayDimention(String value)
/*     */   {
/* 488 */     if (this.isArray) {
/* 489 */       this.dimention.add(value);
/* 490 */       this.isEmptyArray &= value.isEmpty();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setArrayDimention(int pos, String value) {
/* 495 */     if (this.isArray)
/*     */     {
/* 500 */       this.dimention.set(pos, new String(value));
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getArrayDimention(int pos) {
/* 505 */     String result = null;
/* 506 */     if (this.isArray) {
/* 507 */       result = (String)this.dimention.get(pos);
/*     */     }
/* 509 */     return result;
/*     */   }
/*     */ 
/*     */   public int getArrayDimentionAsInt(int pos) {
/* 513 */     String stringresult = getArrayDimention(pos);
/* 514 */     int intresult = 0;
/* 515 */     if ((stringresult != null) && (!stringresult.isEmpty())) {
/* 516 */       Integer intValue = new Integer(stringresult);
/* 517 */       intresult = intValue.intValue();
/*     */     }
/* 519 */     return intresult;
/*     */   }
/*     */ 
/*     */   public void addArrayValue(String value) {
/* 523 */     if (this.isArray)
/* 524 */       this.arrayvalues.add(value);
/*     */   }
/*     */ 
/*     */   public void setArrayValue(int pos, String value)
/*     */   {
/* 529 */     if (this.isArray)
/* 530 */       this.arrayvalues.add(pos, value);
/*     */   }
/*     */ 
/*     */   public String getArrayValue(int pos)
/*     */   {
/* 535 */     String result = null;
/* 536 */     if (this.isArray) {
/* 537 */       result = (String)this.arrayvalues.get(pos);
/*     */     }
/* 539 */     return result;
/*     */   }
/*     */ 
/*     */   public ASTNode getExpressionNode() {
/* 543 */     return this.expressionNode;
/*     */   }
/*     */ 
/*     */   public void setExpressionNode(ASTNode expressionNode) {
/* 547 */     this.expressionNode = expressionNode;
/*     */   }
/*     */ 
/*     */   public boolean isDeclaredUpper() {
/* 551 */     return this.isDeclaredUpper;
/*     */   }
/*     */ 
/*     */   public void setDeclaredUpper(boolean isDeclaredUpper) {
/* 555 */     this.isDeclaredUpper = isDeclaredUpper;
/*     */   }
/*     */ 
/*     */   public boolean isEmptyArray() {
/* 559 */     boolean isEmpty = false;
/* 560 */     if (this.isArray) {
/* 561 */       for (String dim : this.dimention) {
/* 562 */         if ((dim == null) || (dim.isEmpty())) {
/* 563 */           isEmpty = true;
/* 564 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 568 */     return isEmpty;
/*     */   }
/*     */ 
/*     */   public boolean isParam()
/*     */   {
/* 573 */     return this.isParam;
/*     */   }
/*     */ 
/*     */   public void setParam(boolean isParam) {
/* 577 */     this.isParam = isParam;
/*     */   }
/*     */ 
/*     */   public boolean isPrimitiveType() {
/* 581 */     return ArrayHelpers.binarySearch(primitiveTypes, getType()) > -1;
/*     */   }
/*     */   public boolean isObjectType() {
/* 584 */     return !isPrimitiveType();
/*     */   }
/*     */ 
/*     */   public String getTypeToNumeric()
/*     */   {
/* 593 */     StringBuilder result = new StringBuilder();
/* 594 */     if ((isNumeric()) && (!isArray()))
/* 595 */       result.append("Number");
/*     */     else {
/* 597 */       result.append(getType());
/*     */     }
/* 599 */     return result.toString();
/*     */   }
/*     */ 
/*     */   public String getNameToNumeric() {
/* 603 */     StringBuilder result = new StringBuilder();
/* 604 */     if ((isNumeric()) && (!isArray())) {
/* 605 */       if (getType().equals("int"))
/* 606 */         result.append("toInt(");
/* 607 */       else if (getType().equals("long"))
/* 608 */         result.append("toLong(");
/* 609 */       else if (getType().equals("double"))
/* 610 */         result.append("toDouble(");
/* 611 */       else if (getType().equals("datetime")) {
/* 612 */         result.append("toLong(");
/*     */       }
/* 614 */       result.append(getName());
/* 615 */       result.append(")");
/*     */     }
/*     */ 
/* 618 */     return result.toString();
/*     */   }
/*     */ 
/*     */   public String getNameToPrimitiveNumeric() {
/* 622 */     StringBuilder result = new StringBuilder();
/* 623 */     result.append(getName());
/* 624 */     if ((isNumeric()) && (!isArray())) {
/* 625 */       if (getType().equals("int"))
/* 626 */         result.append(".intValue()");
/* 627 */       else if (getType().equals("long"))
/* 628 */         result.append(".longValue()");
/* 629 */       else if (getType().equals("double"))
/* 630 */         result.append(".doubleValue()");
/* 631 */       else if (getType().equals("datetime")) {
/* 632 */         result.append(".longValue()");
/*     */       }
/*     */     }
/* 635 */     return result.toString();
/*     */   }
/*     */ 
/*     */   public String getValueToNumeric() {
/* 639 */     StringBuilder result = new StringBuilder();
/* 640 */     if ((getValue() != null) && (!getValue().isEmpty())) {
/* 641 */       if ((isNumeric()) && (!isArray())) {
/* 642 */         if (getType().equals("int"))
/* 643 */           result.append("toInt(");
/* 644 */         else if (getType().equals("long"))
/* 645 */           result.append("toLong(");
/* 646 */         else if (getType().equals("double"))
/* 647 */           result.append("toDouble(");
/* 648 */         else if (getType().equals("datetime"))
/* 649 */           result.append("toLong(");
/* 650 */         else if (getType().startsWith("bool")) {
/* 651 */           result.append("Bool(");
/*     */         }
/* 653 */         result.append(getValue());
/* 654 */         result.append(")");
/*     */       } else {
/* 656 */         result.append(getValue());
/*     */       }
/*     */     }
/* 659 */     else result.append(getName());
/*     */ 
/* 661 */     return result.toString();
/*     */   }
/*     */ 
/*     */   public boolean isReference() {
/* 665 */     return this.isReference;
/*     */   }
/*     */ 
/*     */   public void setReference(boolean isReference) {
/* 669 */     this.isReference = isReference;
/*     */   }
/*     */ 
/*     */   public int getParamDimmention(int index) {
/* 673 */     int result = -1;
/* 674 */     if ((this.isParam) && (this.isArray)) {
/* 675 */       Integer value = new Integer((String)this.dimention.get(index));
/* 676 */       result = value.intValue();
/*     */     }
/* 678 */     return result;
/*     */   }
/*     */ 
/*     */   public String getDefaultValue() {
/* 682 */     String result = "null";
/* 683 */     if (getType().equals("int"))
/* 684 */       result = "0";
/* 685 */     else if (getType().equals("long"))
/* 686 */       result = "0L";
/* 687 */     else if (getType().equals("datetime"))
/* 688 */       result = "0L";
/* 689 */     else if (getType().equals("double"))
/* 690 */       result = "0.0";
/* 691 */     else if (getType().equals("bool")) {
/* 692 */       result = "true";
/*     */     }
/* 694 */     return result;
/*     */   }
/*     */ 
/*     */   public void setEmptyArray(boolean isEmptyArray) {
/* 698 */     this.isEmptyArray = isEmptyArray;
/*     */   }
/*     */ 
/*     */   public boolean isHasNew() {
/* 702 */     return this.hasNew;
/*     */   }
/*     */ 
/*     */   public void setHasNew(boolean hasNew) {
/* 706 */     this.hasNew = hasNew;
/*     */   }
/*     */ 
/*     */   public boolean isDefine() {
/* 710 */     return this.isDefine;
/*     */   }
/*     */ 
/*     */   public void setDefine(boolean isDefine) {
/* 714 */     this.isDefine = isDefine;
/*     */   }
/*     */ 
/*     */   public boolean isChangeParent() {
/* 718 */     return this.changeParent;
/*     */   }
/*     */ 
/*     */   public void setChangeParent(boolean changeParent) {
/* 722 */     this.changeParent = changeParent;
/*     */   }
/*     */ 
/*     */   public boolean isColor() {
/* 726 */     return this.isColor;
/*     */   }
/*     */ 
/*     */   public void setColor(boolean isColor) {
/* 730 */     this.isColor = isColor;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.VariableDeclaration
 * JD-Core Version:    0.6.0
 */