/*      */ package com.dukascopy.dds2.greed.connector.parser.util;
/*      */ 
/*      */ import com.dukascopy.api.JFException;
/*      */ import com.dukascopy.dds2.greed.connector.helpers.ArrayHelpers;
/*      */ import com.dukascopy.dds2.greed.connector.parser.javacc.ASTNode;
/*      */ import com.dukascopy.dds2.greed.connector.parser.javacc.Node;
/*      */ import com.dukascopy.dds2.greed.connector.parser.javacc.Token;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.StringTokenizer;
/*      */ 
/*      */ public class DeclarationHelpers
/*      */ {
/*   22 */   private static final String[] globalIntFunctions = { "deinit", "init", "start" };
/*      */ 
/*      */   public static String functionClearDefParam(FunctionDeclaration function)
/*      */   {
/*   30 */     return functionClearDefParam(function, -1);
/*      */   }
/*      */ 
/*      */   public static String functionClearDefParam(FunctionDeclaration function, int instrumentIndex) {
/*   34 */     StringBuilder result = new StringBuilder();
/*   35 */     result.append(new StringBuilder().append(function.getVisibility()).append(" ").toString());
/*   36 */     result.append(new StringBuilder().append(function.getType()).append(" ").toString());
/*   37 */     result.append(new StringBuilder().append(function.getName()).append("(").toString());
/*   38 */     int i = 0;
/*   39 */     for (VariableDeclaration param : function.getParams()) {
/*   40 */       if (i > 0) {
/*   41 */         result.append(", ");
/*      */       }
/*   43 */       if ((instrumentIndex > -1) && (instrumentIndex == i))
/*      */       {
/*   45 */         result.append("Object");
/*      */       } else {
/*   47 */         if ((param.isNumeric()) && (!param.isArray()))
/*   48 */           result.append("Number");
/*      */         else {
/*   50 */           result.append(param.getType());
/*      */         }
/*   52 */         if (param.isArray()) {
/*   53 */           for (int j = 0; j < param.dimention.size(); j++) {
/*   54 */             result.append("[");
/*   55 */             if (param.getParamDimmention(j) > 0) {
/*   56 */               result.append(new StringBuilder().append(param.getParamDimmention(j)).append("").toString());
/*      */             }
/*   58 */             result.append("]");
/*      */           }
/*      */         }
/*      */       }
/*   62 */       result.append(" ");
/*   63 */       result.append(param.getName());
/*   64 */       i++;
/*      */     }
/*   66 */     result.append(")");
/*   67 */     return result.toString();
/*      */   }
/*      */ 
/*      */   public static String functionOriginalClearDefParam(FunctionDeclaration function, List<Integer> instrumentIndexes, List<Integer> colorIndexes)
/*      */   {
/*   84 */     return functionOriginalClearDefParam(function, instrumentIndexes, colorIndexes, true);
/*      */   }
/*      */ 
/*      */   public static String functionOriginalClearDefParam(FunctionDeclaration function, List<Integer> instrumentIndexes, List<Integer> colorIndexes, boolean printArraySize)
/*      */   {
/*   89 */     boolean needPrint = false;
/*   90 */     StringBuilder result = new StringBuilder();
/*      */ 
/*   92 */     result.append(new StringBuilder().append(function.getVisibility()).append(" ").toString());
/*   93 */     result.append(new StringBuilder().append(function.getType()).append(" ").toString());
/*   94 */     result.append(new StringBuilder().append(function.getName()).append("(").toString());
/*   95 */     for (int i = 0; i < function.getParams().size(); i++) {
/*   96 */       VariableDeclaration param = (VariableDeclaration)function.getParams().get(i);
/*   97 */       if (i > 0) {
/*   98 */         result.append(", ");
/*      */       }
/*      */ 
/*  104 */       if (!param.isArray()) {
/*  105 */         if (param.isNumeric()) {
/*  106 */           if ((colorIndexes != null) && (colorIndexes.size() > 0) && (colorIndexes.lastIndexOf(Integer.valueOf(i)) > -1)) {
/*  107 */             result.append("Color");
/*  108 */             param.setColor(true);
/*  109 */             needPrint = true;
/*      */           } else {
/*  111 */             result.append("Number");
/*  112 */             needPrint = true;
/*      */           }
/*  114 */         } else if ((instrumentIndexes != null) && (instrumentIndexes.size() > 0) && (instrumentIndexes.lastIndexOf(Integer.valueOf(i)) > -1)) {
/*  115 */           result.append("Object");
/*  116 */           needPrint = true;
/*      */         } else {
/*  118 */           result.append(param.getType());
/*      */         }
/*      */       }
/*      */       else {
/*  122 */         result.append(param.getType());
/*  123 */         for (int j = 0; j < param.dimention.size(); j++) {
/*  124 */           result.append("[");
/*      */ 
/*  128 */           result.append("]");
/*      */         }
/*      */       }
/*      */ 
/*  132 */       result.append(" ");
/*  133 */       result.append(param.getName());
/*      */     }
/*  135 */     result.append(")");
/*  136 */     if (needPrint) {
/*  137 */       return result.toString();
/*      */     }
/*  139 */     return "";
/*      */   }
/*      */ 
/*      */   public static String functionOriginalClearDefParam(FunctionDeclaration function, HashMap<String, String> fnNameList, List<Integer> instrumentIndexes, List<Integer> colorIndexes, int[] booleanIndexes, boolean printArraySize)
/*      */   {
/*  144 */     boolean needPrint = false;
/*  145 */     StringBuilder result = new StringBuilder();
/*  146 */     StringBuilder fnName = new StringBuilder();
/*      */ 
/*  148 */     result.append(new StringBuilder().append(function.getVisibility()).append(" ").toString());
/*  149 */     result.append(new StringBuilder().append(function.getType()).append(" ").toString());
/*  150 */     result.append(new StringBuilder().append(function.getName()).append("(").toString());
/*  151 */     fnName.append(new StringBuilder().append(function.getName()).append("_").toString());
/*      */ 
/*  153 */     for (int i = 0; i < function.getParams().size(); i++) {
/*  154 */       VariableDeclaration param = (VariableDeclaration)function.getParams().get(i);
/*  155 */       if (i > 0) {
/*  156 */         result.append(", ");
/*      */       }
/*      */ 
/*  162 */       if (!param.isArray()) {
/*  163 */         if (param.isNumeric()) {
/*  164 */           if ((colorIndexes != null) && (colorIndexes.size() > 0) && (colorIndexes.lastIndexOf(Integer.valueOf(i)) > -1)) {
/*  165 */             result.append("Color");
/*  166 */             fnName.append("Color_");
/*  167 */             param.setColor(true);
/*  168 */             needPrint = true;
/*  169 */           } else if ((booleanIndexes != null) && (booleanIndexes.length > 0) && (booleanIndexes[i] > 0)) {
/*  170 */             result.append("boolean");
/*  171 */             fnName.append("boolean_");
/*  172 */             needPrint = true;
/*      */           } else {
/*  174 */             result.append("Number");
/*  175 */             fnName.append("Number_");
/*  176 */             needPrint = true;
/*      */           }
/*  178 */         } else if ((instrumentIndexes != null) && (instrumentIndexes.size() > 0) && (instrumentIndexes.lastIndexOf(Integer.valueOf(i)) > -1)) {
/*  179 */           result.append("Object");
/*  180 */           fnName.append("Instrument_");
/*  181 */           needPrint = true;
/*      */         } else {
/*  183 */           result.append(param.getType());
/*  184 */           fnName.append(param.getType());
/*  185 */           fnName.append("_");
/*      */         }
/*      */       }
/*      */       else {
/*  189 */         result.append(param.getType());
/*  190 */         for (int j = 0; j < param.dimention.size(); j++) {
/*  191 */           result.append("[");
/*      */ 
/*  195 */           result.append("]");
/*  196 */           fnName.append(param.getType());
/*  197 */           fnName.append("[]_");
/*      */         }
/*      */       }
/*      */ 
/*  201 */       result.append(" ");
/*  202 */       result.append(param.getName());
/*      */     }
/*  204 */     result.append(")");
/*  205 */     if (needPrint) {
/*  206 */       if (!fnNameList.containsKey(fnName.toString()))
/*  207 */         fnNameList.put(fnName.toString(), "");
/*      */       else {
/*  209 */         result.setLength(0);
/*      */       }
/*      */ 
/*  212 */       return result.toString();
/*      */     }
/*  214 */     return "";
/*      */   }
/*      */ 
/*      */   public static String functionOriginalClearDefParam(FunctionDeclaration function)
/*      */   {
/*  223 */     return functionOriginalClearDefParam(function, true);
/*      */   }
/*      */ 
/*      */   public static String functionOriginalClearDefParam(FunctionDeclaration function, boolean printArraySize) {
/*  227 */     StringBuilder result = new StringBuilder();
/*      */ 
/*  229 */     result.append(new StringBuilder().append(function.getVisibility()).append(" ").toString());
/*  230 */     result.append(new StringBuilder().append(function.getType()).append(" ").toString());
/*  231 */     result.append(new StringBuilder().append(function.getName()).append("(").toString());
/*  232 */     int i = 0;
/*  233 */     for (VariableDeclaration param : function.getParams()) {
/*  234 */       if (i > 0) {
/*  235 */         result.append(", ");
/*      */       }
/*      */ 
/*  241 */       result.append(param.getType());
/*  242 */       if (param.isArray()) {
/*  243 */         for (int j = 0; j < param.dimention.size(); j++) {
/*  244 */           result.append("[");
/*      */ 
/*  248 */           result.append("]");
/*      */         }
/*      */       }
/*      */ 
/*  252 */       result.append(" ");
/*  253 */       result.append(param.getName());
/*  254 */       i++;
/*      */     }
/*  256 */     result.append(")");
/*  257 */     return result.toString();
/*      */   }
/*      */ 
/*      */   public static String functionFineDeclaration(FunctionDeclaration function, int defParamPos)
/*      */   {
/*  267 */     return functionFineDeclaration(function, defParamPos, -1);
/*      */   }
/*      */ 
/*      */   public static String functionFineDeclaration(FunctionDeclaration function, int defParamPos, int instrumentIndex) {
/*  271 */     return functionFineDeclaration(function, defParamPos, -1, true);
/*      */   }
/*      */ 
/*      */   public static String functionFineDeclaration(FunctionDeclaration function, int defParamPos, int instrumentIndex, boolean printArraySize) {
/*  275 */     StringBuilder result = new StringBuilder();
/*  276 */     if (defParamPos < 0) {
/*  277 */       defParamPos = function.getParams().size();
/*      */     }
/*  279 */     result.append(new StringBuilder().append(function.getType()).append(" ").toString());
/*  280 */     result.append(new StringBuilder().append(function.getName()).append("(").toString());
/*  281 */     int i = 0;
/*  282 */     for (VariableDeclaration param : function.getParams()) {
/*  283 */       if (i > defParamPos - 1) {
/*      */         break;
/*      */       }
/*  286 */       if (i > 0) {
/*  287 */         result.append(", ");
/*      */       }
/*  289 */       if ((instrumentIndex > -1) && (instrumentIndex == i))
/*      */       {
/*  291 */         result.append("Object");
/*  292 */         function.addInstrumentIndexes(i);
/*      */       } else {
/*  294 */         if ((param.isNumeric()) && (!param.isArray()))
/*  295 */           result.append("Number");
/*      */         else {
/*  297 */           result.append(param.getType());
/*      */         }
/*      */ 
/*  300 */         if (param.isArray()) {
/*  301 */           for (int j = 0; j < param.dimention.size(); j++) {
/*  302 */             result.append("[");
/*  303 */             if ((printArraySize) && (param.getParamDimmention(j) > 0)) {
/*  304 */               result.append(new StringBuilder().append(param.getParamDimmention(j)).append("").toString());
/*      */             }
/*  306 */             result.append("]");
/*      */           }
/*      */         }
/*      */       }
/*  310 */       result.append(" ");
/*  311 */       result.append(param.getName());
/*  312 */       i++;
/*      */     }
/*  314 */     result.append(")");
/*  315 */     return result.toString();
/*      */   }
/*      */ 
/*      */   public static String functionFineDeclaration(FunctionDeclaration function, int defParamPos, int[] instrumentIndex, HashMap<String, String> fnNameList)
/*      */   {
/*  320 */     return functionFineDeclaration(function, defParamPos, instrumentIndex, fnNameList, false);
/*      */   }
/*      */ 
/*      */   public static String functionFineDeclaration(FunctionDeclaration function, int defParamPos, int[] instrumentIndex, HashMap<String, String> fnNameList, boolean isPrivate)
/*      */   {
/*  325 */     StringBuilder result = new StringBuilder();
/*  326 */     StringBuilder fnName = new StringBuilder();
/*  327 */     if (defParamPos < 0) {
/*  328 */       defParamPos = function.getParams().size();
/*      */     }
/*  330 */     if (isPrivate)
/*  331 */       result.append("private ");
/*      */     else {
/*  333 */       result.append(new StringBuilder().append(function.getVisibility()).append(" ").toString());
/*      */     }
/*  335 */     result.append(new StringBuilder().append(function.getType()).append(" ").toString());
/*  336 */     result.append(new StringBuilder().append(function.getName()).append("(").toString());
/*  337 */     fnName.append(new StringBuilder().append(function.getName()).append("_").toString());
/*  338 */     int i = 0;
/*  339 */     for (VariableDeclaration param : function.getParams()) {
/*  340 */       boolean instrumentSelected = false;
/*  341 */       if (i > defParamPos - 1) {
/*      */         break;
/*      */       }
/*  344 */       if (i > 0) {
/*  345 */         result.append(", ");
/*      */       }
/*      */ 
/*  352 */       if (instrumentIndex != null) {
/*  353 */         for (int j = 0; j < instrumentIndex.length; j++) {
/*  354 */           if ((instrumentIndex[j] == -1) || (instrumentIndex[j] != i))
/*      */             continue;
/*  356 */           result.append("Object");
/*  357 */           instrumentSelected = true;
/*  358 */           fnName.append("Instrument_");
/*  359 */           break;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  364 */       if (!instrumentSelected) {
/*  365 */         if ((param.isNumeric()) && (!param.isArray())) {
/*  366 */           fnName.append("Number_");
/*  367 */           result.append("Number");
/*      */         } else {
/*  369 */           result.append(param.getType());
/*  370 */           fnName.append(new StringBuilder().append(param.getType()).append("_").toString());
/*      */         }
/*      */ 
/*  373 */         if (param.isArray()) {
/*  374 */           for (int j = 0; j < param.dimention.size(); j++) {
/*  375 */             result.append("[");
/*  376 */             if (param.getParamDimmention(j) > 0) {
/*  377 */               result.append(new StringBuilder().append(param.getParamDimmention(j)).append("").toString());
/*      */             }
/*  379 */             result.append("]");
/*      */           }
/*  381 */           fnName.append(param.getType());
/*  382 */           fnName.append("[]_");
/*      */         }
/*      */       }
/*      */ 
/*  386 */       result.append(" ");
/*  387 */       result.append(param.getName());
/*  388 */       i++;
/*      */     }
/*  390 */     result.append(")");
/*  391 */     if (!fnNameList.containsKey(fnName.toString()))
/*  392 */       fnNameList.put(fnName.toString(), "");
/*      */     else {
/*  394 */       result.setLength(0);
/*      */     }
/*  396 */     return result.toString();
/*      */   }
/*      */ 
/*      */   public static int getInstrumentParam(FunctionDeclaration function, String type, String name) {
/*  400 */     int result = -1;
/*  401 */     int index = 0;
/*  402 */     for (VariableDeclaration param : function.getParams()) {
/*  403 */       if ((param.getName().trim().equalsIgnoreCase(name.trim())) && (param.getType().trim().equalsIgnoreCase(type.trim()))) {
/*  404 */         result = index;
/*  405 */         break;
/*      */       }
/*  407 */       index++;
/*      */     }
/*  409 */     return result;
/*      */   }
/*      */ 
/*      */   public static List<Integer> getTypeParams(FunctionDeclaration function, String type) {
/*  413 */     List result = new ArrayList();
/*  414 */     int index = 0;
/*  415 */     for (VariableDeclaration param : function.getParams()) {
/*  416 */       if ((param.getType().trim().equalsIgnoreCase(type.trim())) && (!param.isArray())) {
/*  417 */         result.add(Integer.valueOf(index));
/*      */       }
/*  419 */       index++;
/*      */     }
/*  421 */     return result;
/*      */   }
/*      */ 
/*      */   public static List<Integer> getTypeNameParams(FunctionDeclaration function, String type, String name) {
/*  425 */     List result = new ArrayList();
/*  426 */     int index = 0;
/*  427 */     for (VariableDeclaration param : function.getParams()) {
/*  428 */       if ((param.getType().trim().equalsIgnoreCase(type.trim())) && (!param.isArray()) && (param.getName().indexOf(name) > -1)) {
/*  429 */         result.add(Integer.valueOf(index));
/*      */       }
/*  431 */       index++;
/*      */     }
/*  433 */     return result;
/*      */   }
/*      */ 
/*      */   public static String functionCallDefParam(FunctionDeclaration function) {
/*  437 */     return functionCall(function, 0);
/*      */   }
/*      */ 
/*      */   public static String functionCallDefParam(FunctionDeclaration function, int defParamPos)
/*      */   {
/*  447 */     return functionCallDefParam(function, defParamPos, -1);
/*      */   }
/*      */ 
/*      */   public static String functionCallDefParam(FunctionDeclaration function, int defParamPos, int instrumentIndex) {
/*  451 */     StringBuilder result = new StringBuilder();
/*  452 */     if (defParamPos < 0) {
/*  453 */       defParamPos = function.getParams().size();
/*      */     }
/*  455 */     result.append(new StringBuilder().append(function.getName()).append("(").toString());
/*  456 */     int pos = 0;
/*  457 */     function.getInstrumentIndexes();
/*  458 */     for (VariableDeclaration param : function.getParams()) {
/*  459 */       if (pos > 0) {
/*  460 */         result.append(", ");
/*      */       }
/*      */ 
/*  463 */       if (pos > defParamPos - 1)
/*  464 */         result.append(param.getValueToNumeric());
/*      */       else {
/*  466 */         result.append(param.getNameToPrimitiveNumeric());
/*      */       }
/*      */ 
/*  469 */       if ((instrumentIndex > -1) && (instrumentIndex == pos)) {
/*  470 */         result.append(".toString()");
/*      */       }
/*      */ 
/*  480 */       pos++;
/*      */     }
/*  482 */     result.append(")");
/*  483 */     return result.toString();
/*      */   }
/*      */ 
/*      */   public static Object functionCallDefParam(FunctionDeclaration function, int defParamPos, List<Integer> instrumentList, List<Integer> colorList)
/*      */   {
/*  515 */     return functionCallDefParam(function, defParamPos, instrumentList, colorList, null);
/*      */   }
/*      */ 
/*      */   public static Object functionCallDefParam(FunctionDeclaration function, int defParamPos, List<Integer> instrumentList, List<Integer> colorList, int[] booleanIndexes)
/*      */   {
/*  520 */     StringBuilder result = new StringBuilder();
/*  521 */     if (defParamPos < 0) {
/*  522 */       defParamPos = function.getParams().size();
/*      */     }
/*  524 */     result.append(new StringBuilder().append(function.getName()).append("(").toString());
/*  525 */     int pos = 0;
/*  526 */     for (VariableDeclaration param : function.getParams()) {
/*  527 */       boolean isPosColor = false;
/*  528 */       if (pos > 0) {
/*  529 */         result.append(", ");
/*      */       }
/*      */ 
/*  532 */       if (colorList != null) {
/*  533 */         for (int j = 0; j < colorList.size(); j++) {
/*  534 */           if (((Integer)colorList.get(j)).intValue() == pos) {
/*  535 */             result.append(param.getName());
/*  536 */             result.append(".getRGB()");
/*  537 */             isPosColor = true;
/*      */           }
/*      */         }
/*      */       }
/*  541 */       if (!isPosColor) {
/*  542 */         if ((booleanIndexes != null) && (booleanIndexes.length > 0) && (booleanIndexes[pos] > 0))
/*  543 */           result.append(param.getNameToNumeric());
/*  544 */         else if (pos > defParamPos - 1)
/*  545 */           result.append(param.getValueToNumeric());
/*      */         else {
/*  547 */           result.append(param.getNameToPrimitiveNumeric());
/*      */         }
/*      */       }
/*      */ 
/*  551 */       if (instrumentList != null) {
/*  552 */         for (int j = 0; j < instrumentList.size(); j++) {
/*  553 */           if (((Integer)instrumentList.get(j)).intValue() == pos) {
/*  554 */             result.append(".toString()");
/*      */           }
/*      */         }
/*      */       }
/*  558 */       pos++;
/*      */     }
/*  560 */     result.append(")");
/*  561 */     return result.toString();
/*      */   }
/*      */ 
/*      */   public static String functionCallDefParam(FunctionDeclaration function, int defParamPos, int[] instrumentIndex) {
/*  565 */     StringBuilder result = new StringBuilder();
/*  566 */     if (defParamPos < 0) {
/*  567 */       defParamPos = function.getParams().size();
/*      */     }
/*  569 */     result.append(new StringBuilder().append(function.getName()).append("(").toString());
/*  570 */     int pos = 0;
/*  571 */     function.getInstrumentIndexes();
/*  572 */     for (VariableDeclaration param : function.getParams()) {
/*  573 */       if (pos > 0) {
/*  574 */         result.append(", ");
/*      */       }
/*      */ 
/*  577 */       if (pos > defParamPos - 1)
/*  578 */         result.append(param.getValueToNumeric());
/*      */       else {
/*  580 */         result.append(param.getNameToPrimitiveNumeric());
/*      */       }
/*      */ 
/*  583 */       if (instrumentIndex != null) {
/*  584 */         for (int j = 0; j < instrumentIndex.length; j++) {
/*  585 */           if (instrumentIndex[j] == pos) {
/*  586 */             result.append(".toString()");
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  598 */       pos++;
/*      */     }
/*  600 */     result.append(")");
/*  601 */     return result.toString();
/*      */   }
/*      */ 
/*      */   public static String functionCall(FunctionDeclaration function)
/*      */   {
/*  611 */     return functionCall(function, 0);
/*      */   }
/*      */ 
/*      */   public static String functionCall(FunctionDeclaration function, int defParamPos) {
/*  615 */     StringBuilder result = new StringBuilder();
/*  616 */     if (defParamPos < 0) {
/*  617 */       defParamPos = function.getParams().size();
/*      */     }
/*  619 */     result.append(new StringBuilder().append(function.getName()).append("(").toString());
/*  620 */     int pos = 0;
/*  621 */     for (VariableDeclaration param : function.getParams()) {
/*  622 */       if (pos > defParamPos - 1) {
/*      */         break;
/*      */       }
/*  625 */       result.append(param.getName());
/*  626 */       pos++;
/*      */     }
/*  628 */     result.append(")");
/*  629 */     return result.toString();
/*      */   }
/*      */ 
/*      */   public static String functionDefaultParameters(FunctionDeclaration function)
/*      */   {
/*  639 */     return functionDefaultParameters(function, -1, true);
/*      */   }
/*      */ 
/*      */   public static String functionDefaultParameters(FunctionDeclaration function, boolean printArraySize) {
/*  643 */     return functionDefaultParameters(function, -1, printArraySize);
/*      */   }
/*      */ 
/*      */   public static String functionDefaultParameters(FunctionDeclaration function, int instrumentIndex) {
/*  647 */     return functionDefaultParameters(function, instrumentIndex, true);
/*      */   }
/*      */ 
/*      */   public static String functionDefaultParameters(FunctionDeclaration function, int instrumentIndex, boolean printArraySize) {
/*  651 */     StringBuilder fnDefParam = new StringBuilder();
/*      */ 
/*  653 */     int count = function.getParams().size();
/*  654 */     for (int i = 0; i < function.getParams().size(); i++) {
/*  655 */       count--;
/*  656 */       VariableDeclaration param = (VariableDeclaration)function.getParams().get(count);
/*  657 */       if ((param.getValue() != null) && (!param.getValue().isEmpty())) {
/*  658 */         fnDefParam.append(functionFineDeclaration(function, count, instrumentIndex, printArraySize));
/*  659 */         fnDefParam.append(" throws JFException {\n");
/*  660 */         if ((function.getInstrumentIndexes() != null) && (function.getInstrumentIndexes().size() > 0)) {
/*  661 */           for (int j = 0; j < function.getInstrumentIndexes().size(); j++)
/*      */           {
/*  665 */             fnDefParam.append("\n if(");
/*  666 */             fnDefParam.append(((VariableDeclaration)function.getParams().get(((Integer)function.getInstrumentIndexes().get(j)).intValue())).getName());
/*  667 */             fnDefParam.append("==null){\n");
/*  668 */             fnDefParam.append(((VariableDeclaration)function.getParams().get(((Integer)function.getInstrumentIndexes().get(j)).intValue())).getName());
/*  669 */             fnDefParam.append("=Instrument();}\n");
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  674 */         if (!function.getType().equals("void")) {
/*  675 */           fnDefParam.append("return ");
/*      */         }
/*  677 */         fnDefParam.append(functionCallDefParam(function, count, instrumentIndex));
/*  678 */         fnDefParam.append(";\n}\n");
/*      */       }
/*      */     }
/*  681 */     return fnDefParam.toString();
/*      */   }
/*      */ 
/*      */   public static String functionDefaultParameters(FunctionDeclaration function, int[] instrumentIndex, HashMap<String, String> fnNameList) {
/*  685 */     StringBuilder fnDefParam = new StringBuilder();
/*      */ 
/*  687 */     int count = function.getParams().size();
/*  688 */     for (int i = 0; i < function.getParams().size(); i++) {
/*  689 */       count--;
/*  690 */       VariableDeclaration param = (VariableDeclaration)function.getParams().get(count);
/*  691 */       if ((param.getValue() != null) && (!param.getValue().isEmpty())) {
/*  692 */         String functionFineDeclaration = functionFineDeclaration(function, count, instrumentIndex, fnNameList, true);
/*  693 */         if (functionFineDeclaration.length() > 0) {
/*  694 */           fnDefParam.append(functionFineDeclaration);
/*  695 */           fnDefParam.append(" throws JFException {\n");
/*  696 */           if ((function.getInstrumentIndexes() != null) && (function.getInstrumentIndexes().size() > 0)) {
/*  697 */             for (int j = 0; j < function.getInstrumentIndexes().size(); j++) {
/*  698 */               if (count < ((Integer)function.getInstrumentIndexes().get(j)).intValue()) {
/*  699 */                 fnDefParam.append("if(");
/*  700 */                 fnDefParam.append(((VariableDeclaration)function.getParams().get(((Integer)function.getInstrumentIndexes().get(j)).intValue())).getName());
/*  701 */                 fnDefParam.append("==null){\n");
/*  702 */                 fnDefParam.append(((VariableDeclaration)function.getParams().get(((Integer)function.getInstrumentIndexes().get(j)).intValue())).getName());
/*  703 */                 fnDefParam.append("=Instrument();}\n");
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/*  708 */           if (!function.getType().equals("void")) {
/*  709 */             fnDefParam.append("return ");
/*      */           }
/*  711 */           fnDefParam.append(functionCallDefParam(function, count, instrumentIndex));
/*  712 */           fnDefParam.append(";\n}\n");
/*      */         }
/*      */       }
/*      */     }
/*  716 */     return fnDefParam.toString();
/*      */   }
/*      */ 
/*      */   public static Class getFnType(String name) throws ClassNotFoundException {
/*  720 */     if (name.equals("byte"))
/*  721 */       return Byte.TYPE;
/*  722 */     if ((name.equals("short")) || (name.equals("unsigned")))
/*  723 */       return Short.TYPE;
/*  724 */     if (name.equals("int"))
/*  725 */       return Integer.TYPE;
/*  726 */     if (name.equals("long"))
/*  727 */       return Long.TYPE;
/*  728 */     if (name.equals("char"))
/*  729 */       return Character.TYPE;
/*  730 */     if (name.equals("float"))
/*  731 */       return Float.TYPE;
/*  732 */     if (name.equals("double"))
/*  733 */       return Double.TYPE;
/*  734 */     if (name.startsWith("bool"))
/*  735 */       return Boolean.TYPE;
/*  736 */     if (name.equals("void"))
/*  737 */       return Void.TYPE;
/*  738 */     if (name.equalsIgnoreCase("string"))
/*  739 */       return String.class;
/*  740 */     if (name.equals("datetime")) {
/*  741 */       return Long.TYPE;
/*      */     }
/*  743 */     return Class.forName(name);
/*      */   }
/*      */ 
/*      */   public static ASTNode getTokenNodeName(ASTNode root, String image) {
/*  747 */     ASTNode result = null;
/*  748 */     for (int i = 0; i < root.getChildren().length; i++) {
/*  749 */       ASTNode node = root.getChildren()[i];
/*  750 */       if ((node.getText() != null) && (node.getText().equals(image))) {
/*  751 */         result = node;
/*  752 */         break;
/*      */       }
/*  754 */       if ((node.getChildren() != null) && (node.getChildren().length > 0)) {
/*  755 */         result = getTokenNodeName(node, image);
/*      */       }
/*  757 */       if (result != null)
/*      */         break;
/*      */     }
/*  760 */     return result;
/*      */   }
/*      */ 
/*      */   public static ASTNode getTokenNode(List<Node> list, Token token) {
/*  764 */     ASTNode result = null;
/*  765 */     for (Node node : list) {
/*  766 */       ASTNode ast = (ASTNode)node;
/*  767 */       if (ast.getBeginToken() == token) {
/*  768 */         result = ast;
/*  769 */         break;
/*      */       }
/*  771 */       if (result != null)
/*      */         break;
/*  773 */       result = getTokenNode(ast, token);
/*  774 */       if (result != null)
/*      */         break;
/*      */     }
/*  777 */     return result;
/*      */   }
/*      */ 
/*      */   public static ASTNode getTokenNode(ASTNode root, Token token) {
/*  781 */     ASTNode result = null;
/*  782 */     if (root.getToken() == token) {
/*  783 */       result = root;
/*      */     }
/*  785 */     if ((result == null) && (root.getChildren() != null)) {
/*  786 */       for (ASTNode child : root.getChildren()) {
/*  787 */         if ((child.getToken() != token) || 
/*  796 */           (result == null)) {
/*  797 */           result = getTokenNode(child, token);
/*      */         }
/*  799 */         if (result != null)
/*      */           break;
/*      */       }
/*      */     }
/*  803 */     return result;
/*      */   }
/*      */ 
/*      */   public static FunctionDeclaration makeFunctionDeclaration(ASTNode node) throws ClassNotFoundException {
/*  807 */     FunctionDeclaration function = new FunctionDeclaration();
/*  808 */     String name = node.getName();
/*  809 */     Token token = node.getBeginToken();
/*  810 */     function.setDefaultParameters(false);
/*  811 */     function.setFirstToken(token);
/*  812 */     function.setName(name);
/*  813 */     int index = ArrayHelpers.binarySearch(globalIntFunctions, name);
/*  814 */     if (index > -1) {
/*  815 */       if (name.equals(globalIntFunctions[index]))
/*  816 */         function.setType("int");
/*      */     }
/*      */     else {
/*  819 */       function.setType(token.image);
/*      */     }
/*  821 */     function.setFnType(getFnType(function.getType()));
/*  822 */     while (!token.image.equals("(")) {
/*  823 */       token = token.next;
/*      */     }
/*  825 */     token = token.next;
/*      */ 
/*  828 */     int count = 0;
/*  829 */     while (!token.image.equals(")")) {
/*  830 */       VariableDeclaration param = new VariableDeclaration();
/*  831 */       param.setType(token.image);
/*  832 */       token = token.next;
/*  833 */       if ((!token.image.equals(",")) || (!token.image.equals("=")))
/*  834 */         param.setName(token.image);
/*      */       else {
/*  836 */         param.setName(new StringBuilder().append("param").append(count++).toString());
/*      */       }
/*  838 */       token = token.next;
/*  839 */       if (token.image.equals("=")) {
/*  840 */         ASTNode child = getTokenNode(node, token);
/*  841 */         if (child != null) {
/*  842 */           child.setText("");
/*      */         }
/*  844 */         token = token.next;
/*  845 */         String expr = "";
/*  846 */         while ((token != null) && (token.image != null) && (!token.image.equals(",")) && (!token.image.equals(")"))) {
/*  847 */           ASTNode child1 = getTokenNode(node, token);
/*  848 */           if (child1 != null) {
/*  849 */             child1.setText("");
/*      */           }
/*  851 */           expr = new StringBuilder().append(expr).append(token.image).toString();
/*  852 */           token = token.next;
/*      */         }
/*  854 */         param.setValue(expr);
/*  855 */         function.setDefaultParameters(true);
/*      */       }
/*      */ 
/*  858 */       if (token.image.equals(",")) {
/*  859 */         token = token.next;
/*      */       }
/*  861 */       function.setParam(param);
/*      */     }
/*      */ 
/*  864 */     if (!token.next.image.equals(";")) {
/*  865 */       function.setHasBody(true);
/*      */     }
/*  867 */     return function;
/*      */   }
/*      */ 
/*      */   public static VariableDeclaration fillDeclaratorToParam(ASTNode itemNode, VariableDeclaration param) {
/*  871 */     if (itemNode.getId() == 29) {
/*  872 */       param.setName(itemNode.getText());
/*  873 */       if ((itemNode.getChildren() != null) && (itemNode.getChildren().length > 0)) {
/*  874 */         int dimmIndex = -1;
/*  875 */         for (ASTNode childNode : itemNode.getChildren()) {
/*  876 */           if (childNode.getId() == 64) {
/*  877 */             param.setArray(true);
/*  878 */             param.dimention.add(new String("-1"));
/*  879 */             dimmIndex++;
/*  880 */           } else if (childNode.getId() == 71) {
/*  881 */             param.dimention.add(new String("-1"));
/*  882 */             dimmIndex++;
/*  883 */           } else if (childNode.getId() == 51) {
/*  884 */             param.dimention.set(dimmIndex, getAssignmentExpression(childNode));
/*  885 */           } else if (childNode.getId() == 29) {
/*  886 */             param = fillDeclaratorToParam(childNode, param); } else {
/*  887 */             if (childNode.getId() == 9)
/*      */               continue;
/*  889 */             if (childNode.getId() == 27)
/*  890 */               param.setReference(true);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  895 */     return param;
/*      */   }
/*      */ 
/*      */   public static FunctionDeclaration fillFunctionDeclaration(FunctionDeclaration function, ASTNode node)
/*      */     throws ClassNotFoundException
/*      */   {
/*  906 */     function.setDefaultParameters(false);
/*  907 */     Token token = node.getBeginToken();
/*  908 */     String name = function.getName();
/*  909 */     if (ArrayHelpers.binarySearch(globalIntFunctions, name) > -1)
/*  910 */       function.setType("int");
/*      */     else {
/*  912 */       function.setType(token.image);
/*      */     }
/*  914 */     function.setFnType(getFnType(function.getType()));
/*      */ 
/*  916 */     ASTNode parameterList = getFirstIdNode(node, 36);
/*      */ 
/*  918 */     ArrayList params = function.getParams();
/*  919 */     if (params == null) {
/*  920 */       params = new ArrayList();
/*  921 */       function.setParams(params);
/*      */     } else {
/*  923 */       function.clearParams();
/*      */     }
/*  925 */     if ((parameterList != null) && (parameterList.getChildren() != null) && (parameterList.getChildren().length > 0)) {
/*  926 */       for (ASTNode paramNode : parameterList.getChildren()) {
/*  927 */         if (paramNode.getId() == 37) {
/*  928 */           VariableDeclaration param = new VariableDeclaration();
/*      */ 
/*  930 */           for (ASTNode itemNode : paramNode.getChildren()) {
/*  931 */             if (itemNode.getId() == 6) {
/*  932 */               param.setType(getAssignmentExpression(itemNode));
/*  933 */             } else if (itemNode.getId() == 29) {
/*  934 */               param = fillDeclaratorToParam(itemNode, param); } else {
/*  935 */               if ((itemNode.getId() == 74) || (
/*  936 */                 (itemNode.getId() != 49) && (itemNode.getId() != 50)))
/*      */                 continue;
/*  938 */               param.setValue(getAssignmentExpression(itemNode));
/*  939 */               function.setDefaultParameters(true);
/*      */             }
/*      */           }
/*  942 */           function.setParam(param);
/*      */         }
/*      */       }
/*      */     }
/*  946 */     if (getFirstIdNode(node, 41) != null) {
/*  947 */       function.setHasBody(true);
/*      */     }
/*      */ 
/*  950 */     return function;
/*      */   }
/*      */ 
/*      */   public static FunctionDeclaration fillFunctionDeclaration(FunctionDeclaration function, Token token)
/*      */     throws ClassNotFoundException
/*      */   {
/*  961 */     function.setDefaultParameters(false);
/*  962 */     String name = function.getName();
/*  963 */     function.setName(name);
/*  964 */     if (ArrayHelpers.binarySearch(globalIntFunctions, name.trim()) > -1)
/*  965 */       function.setType("int");
/*      */     else {
/*  967 */       function.setType(token.image);
/*      */     }
/*  969 */     function.setFnType(getFnType(function.getType()));
/*  970 */     while (!token.image.equals("(")) {
/*  971 */       token = token.next;
/*      */     }
/*  973 */     token = token.next;
/*  974 */     ArrayList params = function.getParams();
/*  975 */     if (params == null) {
/*  976 */       params = new ArrayList();
/*  977 */       function.setParams(params);
/*      */     } else {
/*  979 */       function.clearParams();
/*      */     }
/*  981 */     int count = 0;
/*  982 */     while ((token != null) && (token.image != null) && (!token.image.equals(")"))) {
/*  983 */       VariableDeclaration param = new VariableDeclaration();
/*  984 */       param.setType(token.image);
/*  985 */       token = token.next;
/*  986 */       if (token.kind == 63) {
/*  987 */         param.setReference(true);
/*  988 */         token = token.next;
/*      */       }
/*      */ 
/*  991 */       if (token.kind == 38) {
/*  992 */         param.setArray(true);
/*  993 */         token = token.next;
/*      */ 
/*  995 */         while ((token.kind != 45) && (token.kind != 48) && (token.kind != 41))
/*      */         {
/*  997 */           if ((token.kind >= 145) && (token.kind <= 151))
/*  998 */             param.dimention.add(new String(token.image));
/*  999 */           else if ((token.kind == 39) && (token.next != null) && (token.next.kind == 38))
/*      */           {
/* 1001 */             param.dimention.add(new String("-1"));
/*      */           }
/*      */ 
/* 1004 */           token = token.next;
/*      */         }
/*      */       }
/*      */ 
/* 1008 */       if ((token.kind != 45) || (token.kind != 48))
/* 1009 */         param.setName(token.image);
/*      */       else {
/* 1011 */         param.setName(new StringBuilder().append("param").append(count++).toString());
/*      */       }
/* 1013 */       token = token.next;
/*      */ 
/* 1015 */       if ((token != null) && (token.kind == 38)) {
/* 1016 */         param.setArray(true);
/* 1017 */         token = token.next;
/*      */ 
/* 1019 */         while ((token.kind != 45) && (token.kind != 48) && (token.kind != 41))
/*      */         {
/* 1021 */           if ((token.kind >= 145) && (token.kind <= 151))
/* 1022 */             param.dimention.add(new String(token.image));
/* 1023 */           else if ((token.kind == 39) && (token.next != null) && (token.next.kind == 38))
/*      */           {
/* 1025 */             param.dimention.add(new String("-1"));
/*      */           }
/*      */ 
/* 1028 */           token = token.next;
/*      */         }
/*      */       }
/*      */ 
/* 1032 */       if ((token != null) && (token.kind == 48)) {
/* 1033 */         token = token.next;
/* 1034 */         String expr = "";
/*      */ 
/* 1036 */         while ((token != null) && (token.image != null) && (token.kind != 45) && (token.kind != 41)) {
/* 1037 */           expr = new StringBuilder().append(expr).append(token.image).toString();
/* 1038 */           token = token.next;
/*      */         }
/* 1040 */         param.setValue(expr);
/* 1041 */         function.setDefaultParameters(true);
/*      */       }
/* 1043 */       if ((token != null) && (token.image.equals(","))) {
/* 1044 */         token = token.next;
/*      */       }
/* 1046 */       function.setParam(param);
/*      */     }
/* 1048 */     return function;
/*      */   }
/*      */ 
/*      */   public static String makeTypedExpression(String fnReturnExpression, Class fnType)
/*      */   {
/* 1053 */     if (fnReturnExpression.startsWith("(")) {
/* 1054 */       fnReturnExpression = fnReturnExpression.substring(1, fnReturnExpression.length() - 1);
/*      */     }
/* 1056 */     if (fnType.equals(Integer.TYPE))
/* 1057 */       return new StringBuilder().append("toInt(").append(fnReturnExpression).append(")").toString();
/* 1058 */     if (fnType.equals(Long.TYPE))
/* 1059 */       return new StringBuilder().append("toLong(").append(fnReturnExpression).append(")").toString();
/* 1060 */     if (fnType.equals(Double.TYPE))
/* 1061 */       return new StringBuilder().append("toDouble(").append(fnReturnExpression).append(")").toString();
/* 1062 */     if (fnType.equals(Byte.TYPE))
/* 1063 */       return new StringBuilder().append("toByte(").append(fnReturnExpression).append(")").toString();
/* 1064 */     if (fnType.equals(Boolean.TYPE)) {
/* 1065 */       return new StringBuilder().append("Bool(").append(fnReturnExpression).append(")").toString();
/*      */     }
/* 1067 */     return fnReturnExpression;
/*      */   }
/*      */ 
/*      */   public static String makeTypedExpression(String fnReturnExpression, String fnType) {
/* 1071 */     if (fnReturnExpression.startsWith("(")) {
/* 1072 */       fnReturnExpression = fnReturnExpression.substring(1, fnReturnExpression.length() - 1);
/*      */     }
/* 1074 */     if (fnType.equals("int"))
/* 1075 */       return new StringBuilder().append("toInt(").append(fnReturnExpression).append(")").toString();
/* 1076 */     if (fnType.equals("long"))
/* 1077 */       return new StringBuilder().append("toLong(").append(fnReturnExpression).append(")").toString();
/* 1078 */     if (fnType.equals("double"))
/* 1079 */       return new StringBuilder().append("toDouble(").append(fnReturnExpression).append(")").toString();
/* 1080 */     if (fnType.equals("byte"))
/* 1081 */       return new StringBuilder().append("toByte(").append(fnReturnExpression).append(")").toString();
/* 1082 */     if (fnType.startsWith("bool"))
/* 1083 */       return new StringBuilder().append("Bool(").append(fnReturnExpression).append(")").toString();
/* 1084 */     if (fnType.indexOf("tring") > 0) {
/* 1085 */       return new StringBuilder().append("toString(").append(fnReturnExpression).append(")").toString();
/*      */     }
/* 1087 */     return fnReturnExpression;
/*      */   }
/*      */ 
/*      */   public static String makeBooleanExpression(String ifExpression) {
/* 1091 */     String result = "true";
/* 1092 */     if ((ifExpression != null) && (!ifExpression.isEmpty())) {
/* 1093 */       result = new StringBuilder().append("Bool(").append(ifExpression).append(")").toString();
/*      */     }
/* 1095 */     return result;
/*      */   }
/*      */ 
/*      */   public static boolean isDeclaredInList(List<Node> declarations, Token token)
/*      */   {
/* 1104 */     boolean result = false;
/* 1105 */     for (Node node : declarations) {
/* 1106 */       ASTNode ast = (ASTNode)node;
/* 1107 */       if (ast.getName() == null)
/*      */         continue;
/* 1109 */       if (ast.getName().equals(token.image)) {
/* 1110 */         Token beginToken = ast.getBeginToken();
/* 1111 */         while (!beginToken.image.equals(";")) {
/* 1112 */           if ((beginToken.image.equals(token.image)) && (beginToken.beginLine == token.beginLine)) {
/* 1113 */             result = true;
/*      */           }
/* 1115 */           beginToken = beginToken.next;
/*      */         }
/*      */       }
/*      */     }
/* 1119 */     return result;
/*      */   }
/*      */ 
/*      */   public static List<Node> getAllDeclaredInList(List<Node> declarations, Token token) {
/* 1123 */     List result = new ArrayList();
/* 1124 */     for (Node node : declarations) {
/* 1125 */       ASTNode ast = (ASTNode)node;
/* 1126 */       if (ast.getName() == null)
/*      */         continue;
/* 1128 */       if (ast.getName().equals(token.image)) {
/* 1129 */         result.add(ast);
/*      */       }
/*      */     }
/* 1132 */     return result;
/*      */   }
/*      */ 
/*      */   public static Node getFirstDeclaredInList(List<Node> declarations, Token token) {
/* 1136 */     Node result = null;
/* 1137 */     for (Node node : declarations) {
/* 1138 */       ASTNode ast = (ASTNode)node;
/* 1139 */       if (ast.getName() == null)
/*      */         continue;
/* 1141 */       if (ast.getName().equals(token.image)) {
/* 1142 */         Token beginToken = ast.getBeginToken();
/* 1143 */         while ((beginToken != null) && (beginToken.image != null) && (!beginToken.image.equals(";"))) {
/* 1144 */           if ((beginToken.image.equals(token.image)) && (beginToken.beginLine == token.beginLine)) {
/* 1145 */             result = ast;
/* 1146 */             break;
/*      */           }
/* 1148 */           beginToken = beginToken.next;
/*      */         }
/*      */       }
/*      */     }
/* 1152 */     return result;
/*      */   }
/*      */ 
/*      */   public static Node getDeclaredInList(Node[] declarations, Token token) {
/* 1156 */     Node result = null;
/* 1157 */     for (int i = 0; i < declarations.length; i++) {
/* 1158 */       ASTNode ast = (ASTNode)declarations[i];
/* 1159 */       if (((ast.getName() != null) && (ast.getName().equals(token.image))) || ((ast.getText() != null) && (ast.getText().equals(token.image))))
/*      */       {
/* 1161 */         result = declarations[i];
/* 1162 */         break;
/*      */       }
/* 1164 */       if (ast.jjtGetNumChildren() > 0) {
/* 1165 */         result = getDeclaredInList(ast.getChildren(), token);
/*      */       }
/* 1167 */       if (result != null)
/*      */       {
/*      */         break;
/*      */       }
/*      */     }
/* 1172 */     return result;
/*      */   }
/*      */ 
/*      */   public static Node getDeclaredInList(List<Node> declarations, Token token) {
/* 1176 */     Node result = null;
/* 1177 */     for (Node node : declarations) {
/* 1178 */       ASTNode ast = (ASTNode)node;
/* 1179 */       if (((ast.getName() != null) && (ast.getName().equals(token.image))) || ((ast.getText() != null) && (ast.getText().equals(token.image))))
/*      */       {
/* 1181 */         result = node;
/* 1182 */         break;
/*      */       }
/* 1184 */       if (ast.jjtGetNumChildren() > 0) {
/* 1185 */         result = getDeclaredInList(ast.getChildren(), token);
/*      */       }
/* 1187 */       if (result != null) {
/*      */         break;
/*      */       }
/*      */     }
/* 1191 */     return result;
/*      */   }
/*      */ 
/*      */   public static List<Node> getParentList(ASTNode node) {
/* 1195 */     List parentList = new ArrayList();
/* 1196 */     ASTNode parent = (ASTNode)node.jjtGetParent();
/* 1197 */     while (parent != null) {
/* 1198 */       if ((parent.getBeginToken() != null) && (!parent.getName().equals("return")) && (
/* 1199 */         (parent.getId() != 42) || (parent.getId() != 44) || (parent.getId() != 140)))
/*      */       {
/* 1202 */         parentList.add(parent);
/*      */       }
/*      */ 
/* 1205 */       parent = (ASTNode)parent.jjtGetParent();
/*      */     }
/* 1207 */     return parentList;
/*      */   }
/*      */ 
/*      */   public static boolean isDeclaredBefore(List<Node> declarations, Node owner, Token token) {
/* 1211 */     boolean result = false;
/* 1212 */     for (Node node : declarations) {
/* 1213 */       ASTNode ast = (ASTNode)node;
/* 1214 */       if (ast.getName() == null)
/*      */         continue;
/* 1216 */       if ((ast.getName().equals(token.image)) && 
/* 1217 */         (ast.getBeginToken().beginLine < token.beginLine)) {
/* 1218 */         result = true;
/*      */       }
/*      */     }
/*      */ 
/* 1222 */     return result;
/*      */   }
/*      */ 
/*      */   public static String getExpression(ASTNode childStmt) {
/* 1226 */     String expression = "";
/* 1227 */     ASTNode expressionNode = null;
/* 1228 */     for (Node node : childStmt.getChildren()) {
/* 1229 */       expressionNode = (ASTNode)node;
/* 1230 */       if (expressionNode.getBeginToken() != null)
/*      */         break;
/*      */     }
/* 1233 */     Token token = expressionNode.getBeginToken();
/* 1234 */     Token endToken = expressionNode.getEndToken();
/* 1235 */     token = token.next;
/* 1236 */     while (token != endToken)
/*      */     {
/* 1240 */       expression = new StringBuilder().append(expression).append(token.image.trim()).toString();
/* 1241 */       token = token.next;
/*      */     }
/* 1243 */     return expression;
/*      */   }
/*      */ 
/*      */   public static String getKeyword(int key) {
/* 1247 */     String result = "";
/* 1248 */     if (key > -1) {
/* 1249 */       result = com.dukascopy.dds2.greed.connector.parser.javacc.CPPParserConstants.tokenImage[key];
/*      */     }
/* 1251 */     if (result.length() > 0) {
/* 1252 */       int start = result.indexOf("\"");
/* 1253 */       int end = result.lastIndexOf("\"");
/* 1254 */       if (end > start) {
/* 1255 */         result = result.substring(start + 1, end);
/*      */       }
/*      */     }
/*      */ 
/* 1259 */     return result;
/*      */   }
/*      */ 
/*      */   public static int getExpressionEnd(StringBuilder buf, ASTNode expressionNode, int offset) {
/* 1263 */     Token token = expressionNode.getBeginToken();
/* 1264 */     Token endToken = expressionNode.getEndToken();
/* 1265 */     token = token.next;
/* 1266 */     int count = 0;
/* 1267 */     while (token != endToken) {
/* 1268 */       if (token.image.equals(")"))
/* 1269 */         count++;
/* 1270 */       token = token.next;
/*      */     }
/* 1272 */     int pos = expressionNode.getStartPos() + offset;
/* 1273 */     for (int i = 0; i < count; i++) {
/* 1274 */       pos = buf.toString().indexOf(")", pos + 1);
/*      */     }
/* 1276 */     return pos;
/*      */   }
/*      */ 
/*      */   public static ASTNode getFunctionParent(ASTNode childStmt) {
/* 1280 */     ASTNode parent = (ASTNode)childStmt.jjtGetParent();
/* 1281 */     while (parent.getId() != 2) {
/* 1282 */       parent = (ASTNode)parent.jjtGetParent();
/*      */     }
/* 1284 */     return parent;
/*      */   }
/*      */ 
/*      */   public static void getGlobalDeclarations(int id, ASTNode root, List<Node> declarations) {
/* 1288 */     for (int i = 0; i < root.jjtGetNumChildren(); i++) {
/* 1289 */       ASTNode node = (ASTNode)root.jjtGetChild(i);
/* 1290 */       if (node.getId() == id) {
/* 1291 */         declarations.add(node);
/*      */       }
/* 1293 */       if ((node.getId() != 0) && (node.getId() != 2) && (node.getId() != 41))
/*      */         continue;
/* 1295 */       getGlobalDeclarations(id, node, declarations);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void getNodes(int id, ASTNode root, List<Node> list)
/*      */   {
/* 1301 */     for (int j = 0; j < root.jjtGetNumChildren(); j++) {
/* 1302 */       ASTNode child = (ASTNode)root.jjtGetChild(j);
/* 1303 */       if (child.getId() == 2) {
/* 1304 */         getFuncDeclarations(id, child, list);
/*      */       }
/* 1306 */       if (child.getId() == id) {
/* 1307 */         list.add(child);
/*      */       }
/* 1309 */       getNodes(id, child, list);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void getFuncDeclarations(int id, ASTNode root, List<Node> declarations) {
/* 1314 */     for (int i = 0; i < root.jjtGetNumChildren(); i++) {
/* 1315 */       ASTNode node = (ASTNode)root.jjtGetChild(i);
/* 1316 */       if (node.getId() == 41)
/* 1317 */         getNodes(id, node, declarations);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static ASTNode getFirstIdNode(ASTNode root, int id)
/*      */   {
/* 1325 */     ASTNode result = null;
/* 1326 */     if (root.getChildren() != null) {
/* 1327 */       for (ASTNode child : root.getChildren()) {
/* 1328 */         if (child.getId() == id) {
/* 1329 */           result = child;
/* 1330 */           break;
/*      */         }
/* 1332 */         result = getFirstIdNode(child, id);
/* 1333 */         if (result != null)
/*      */           break;
/*      */       }
/*      */     }
/* 1337 */     return result;
/*      */   }
/*      */ 
/*      */   public static boolean isiCustomAssignmentExpression(ASTNode root) {
/* 1341 */     boolean result = false;
/* 1342 */     if (root.getChildren() != null) {
/* 1343 */       for (ASTNode child : root.getChildren()) {
/* 1344 */         if ((child.getId() == 168) && (child.getText().equals("iCustom"))) {
/* 1345 */           result = true;
/* 1346 */           break;
/*      */         }
/* 1348 */         result = isiCustomAssignmentExpression(child);
/* 1349 */         if (result == true)
/*      */         {
/*      */           break;
/*      */         }
/*      */       }
/*      */     }
/* 1355 */     return result;
/*      */   }
/*      */ 
/*      */   public static String getiCustomAssignmentExpression(ASTNode root) {
/* 1359 */     StringBuilder result = new StringBuilder();
/* 1360 */     if (root.getChildren() != null) {
/* 1361 */       for (ASTNode child : root.getChildren()) {
/* 1362 */         if (child.getId() == 58) {
/* 1363 */           boolean isLastParamAdded = false;
/* 1364 */           for (int i = 0; i < child.getChildren().length - 4; i++) {
/* 1365 */             ASTNode expression = child.getChildren()[i];
/* 1366 */             if ((i < 6) || (isLastParamAdded))
/*      */             {
/* 1368 */               if (expression.getId() != 50)
/* 1369 */                 result.append(expression.getText());
/*      */               else
/* 1371 */                 result.append(getAssignmentExpression(expression));
/*      */             }
/*      */             else {
/* 1374 */               int step = child.getChildren().length - 3;
/* 1375 */               while (step != child.getChildren().length) {
/* 1376 */                 ASTNode endexpression = child.getChildren()[step];
/* 1377 */                 if (endexpression.getId() != 50)
/* 1378 */                   result.append(endexpression.getText());
/*      */                 else {
/* 1380 */                   result.append(getAssignmentExpression(endexpression));
/*      */                 }
/* 1382 */                 step++;
/*      */               }
/* 1384 */               i--;
/* 1385 */               i--;
/* 1386 */               isLastParamAdded = true;
/*      */             }
/*      */           }
/*      */         } else {
/* 1390 */           if ((child.getText() != null) && (!child.getText().isEmpty())) {
/* 1391 */             result.append(child.getText());
/*      */           }
/* 1393 */           if (child.getChildren() != null) {
/* 1394 */             result.append(getiCustomAssignmentExpression(child));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1399 */     return result.toString();
/*      */   }
/*      */ 
/*      */   public static int[] getDateToArray(String value, String delim) {
/* 1403 */     int[] tokens = new int[3];
/* 1404 */     tokens[0] = 0;
/* 1405 */     tokens[1] = 0;
/* 1406 */     tokens[2] = 0;
/* 1407 */     Calendar c = Calendar.getInstance();
/* 1408 */     StringTokenizer tokenizer = new StringTokenizer(value, delim);
/* 1409 */     int pos = 0;
/* 1410 */     boolean yearCompleted = false;
/* 1411 */     while (tokenizer.hasMoreElements()) {
/* 1412 */       String token = tokenizer.nextToken();
/* 1413 */       int tokenValue = Integer.valueOf(token).intValue();
/* 1414 */       if (pos == 0) {
/* 1415 */         if (token.length() > 3) {
/* 1416 */           tokens[pos] = tokenValue;
/* 1417 */           yearCompleted = true;
/*      */         } else {
/* 1419 */           tokens[pos] = tokenValue;
/*      */         }
/*      */       }
/* 1422 */       if (pos == 1) {
/* 1423 */         tokens[pos] = (tokenValue - 1);
/*      */       }
/*      */ 
/* 1426 */       if (pos == 2) {
/* 1427 */         if (yearCompleted) {
/* 1428 */           tokens[pos] = tokenValue;
/* 1429 */         } else if (token.length() > 3) {
/* 1430 */           tokens[pos] = tokens[0];
/* 1431 */           tokens[0] = tokenValue;
/*      */         }
/* 1433 */         else if (tokenValue > 31) {
/* 1434 */           tokens[pos] = tokens[0];
/* 1435 */           tokens[0] = (2000 + tokenValue);
/*      */         } else {
/* 1437 */           tokens[0] += 2000;
/* 1438 */           tokens[pos] = tokenValue;
/*      */         }
/*      */       }
/*      */ 
/* 1442 */       pos++;
/*      */     }
/*      */ 
/* 1445 */     if (tokens[0] == 0) {
/* 1446 */       tokens[0] = c.get(1);
/*      */     }
/* 1448 */     if (tokens[1] == 0) {
/* 1449 */       tokens[1] = c.get(2);
/*      */     }
/* 1451 */     if (tokens[2] == 0) {
/* 1452 */       tokens[2] = c.get(5);
/*      */     }
/* 1454 */     return tokens;
/*      */   }
/*      */ 
/*      */   public static int[] getTimeToArray(String value, String delim) {
/* 1458 */     int[] tokens = new int[3];
/* 1459 */     tokens[0] = 0;
/* 1460 */     tokens[1] = 0;
/* 1461 */     tokens[2] = 0;
/* 1462 */     StringTokenizer tokenizer = new StringTokenizer(value, delim);
/* 1463 */     int pos = 0;
/* 1464 */     while (tokenizer.hasMoreElements()) {
/* 1465 */       String token = tokenizer.nextToken();
/* 1466 */       tokens[pos] = Integer.valueOf(token).intValue();
/* 1467 */       pos++;
/*      */     }
/* 1469 */     return tokens;
/*      */   }
/*      */ 
/*      */   public static Calendar getCalendarInitionalizerExpression(String macro) throws JFException {
/* 1473 */     Calendar calendar = null;
/*      */ 
/* 1475 */     if (macro.startsWith("D'")) {
/* 1476 */       calendar = Calendar.getInstance();
/*      */ 
/* 1478 */       String dateTimeStringValue = macro.substring(2, macro.length() - 1);
/*      */ 
/* 1481 */       int[] dateArray = null;
/* 1482 */       int[] timeArray = null;
/*      */ 
/* 1484 */       if (!dateTimeStringValue.isEmpty())
/*      */       {
/* 1486 */         if (dateTimeStringValue.indexOf(" ") > 0) {
/* 1487 */           String dateStringValue = dateTimeStringValue.substring(0, dateTimeStringValue.indexOf(" "));
/* 1488 */           String timeStringValue = dateTimeStringValue.substring(dateTimeStringValue.indexOf(" ") + 1);
/* 1489 */           dateArray = getDateToArray(dateStringValue, ".");
/* 1490 */           timeArray = getTimeToArray(timeStringValue, ":");
/* 1491 */         } else if (dateTimeStringValue.indexOf(".") > 0) {
/* 1492 */           String dateStringValue = dateTimeStringValue;
/* 1493 */           dateArray = getDateToArray(dateStringValue, ".");
/* 1494 */           timeArray = new int[3];
/* 1495 */           timeArray[0] = 0;
/* 1496 */           timeArray[1] = 0;
/* 1497 */           timeArray[2] = 0;
/* 1498 */         } else if (dateTimeStringValue.indexOf(":") > 0) {
/* 1499 */           String timeStringValue = dateTimeStringValue;
/* 1500 */           timeArray = getTimeToArray(timeStringValue, ":");
/* 1501 */           dateArray = new int[3];
/* 1502 */           Calendar c = Calendar.getInstance();
/* 1503 */           dateArray[0] = c.get(1);
/* 1504 */           dateArray[1] = c.get(2);
/* 1505 */           dateArray[2] = c.get(5);
/*      */         } else {
/* 1507 */           throw new JFException(new StringBuilder().append("Invalid datetime value: ").append(dateTimeStringValue).toString());
/*      */         }
/*      */       }
/* 1509 */       if ((dateArray != null) && (timeArray != null)) {
/* 1510 */         calendar.set(dateArray[0], dateArray[1], dateArray[2], timeArray[0], timeArray[1], timeArray[2]);
/*      */       }
/*      */     }
/* 1513 */     return calendar;
/*      */   }
/*      */ 
/*      */   public static Calendar getCalendarInitionalizerExpression(ASTNode root) throws JFException {
/* 1517 */     Calendar calendar = null;
/*      */ 
/* 1519 */     if ((root.getText() != null) && (!root.getText().isEmpty()) && 
/* 1520 */       (root.getText().startsWith("D'"))) {
/* 1521 */       calendar = Calendar.getInstance();
/* 1522 */       String dateTimeStringValue = root.getText().substring(2, root.getText().length() - 1);
/*      */ 
/* 1525 */       int[] dateArray = null;
/* 1526 */       int[] timeArray = null;
/*      */ 
/* 1528 */       if (!dateTimeStringValue.isEmpty())
/*      */       {
/* 1530 */         if (dateTimeStringValue.indexOf(" ") > 0) {
/* 1531 */           String dateStringValue = dateTimeStringValue.substring(0, dateTimeStringValue.indexOf(" "));
/* 1532 */           String timeStringValue = dateTimeStringValue.substring(dateTimeStringValue.indexOf(" ") + 1);
/* 1533 */           dateArray = getDateToArray(dateStringValue, ".");
/* 1534 */           timeArray = getTimeToArray(timeStringValue, ":");
/* 1535 */         } else if (dateTimeStringValue.indexOf(".") > 0) {
/* 1536 */           String dateStringValue = dateTimeStringValue;
/* 1537 */           dateArray = getDateToArray(dateStringValue, ".");
/* 1538 */           timeArray = new int[3];
/* 1539 */           timeArray[0] = 0;
/* 1540 */           timeArray[1] = 0;
/* 1541 */           timeArray[2] = 0;
/* 1542 */         } else if (dateTimeStringValue.indexOf(":") > 0) {
/* 1543 */           String timeStringValue = dateTimeStringValue;
/* 1544 */           timeArray = getTimeToArray(timeStringValue, ":");
/* 1545 */           dateArray = new int[3];
/* 1546 */           Calendar c = Calendar.getInstance();
/* 1547 */           dateArray[0] = c.get(1);
/* 1548 */           dateArray[1] = c.get(2);
/* 1549 */           dateArray[2] = c.get(5);
/*      */         } else {
/* 1551 */           throw new JFException(new StringBuilder().append("Invalid datetime value: ").append(dateTimeStringValue).toString());
/*      */         }
/*      */       }
/* 1553 */       if ((dateArray != null) && (timeArray != null)) {
/* 1554 */         calendar.set(dateArray[0], dateArray[1], dateArray[2], timeArray[0], timeArray[1], timeArray[2]);
/*      */       }
/*      */     }
/*      */ 
/* 1558 */     return calendar;
/*      */   }
/*      */ 
/*      */   public static String getDatetimeInitionalizerExpression(ASTNode root) throws JFException {
/* 1562 */     StringBuilder result = new StringBuilder();
/* 1563 */     if ((root.getText() != null) && (!root.getText().isEmpty()) && 
/* 1564 */       (root.getText().startsWith("D'"))) {
/* 1565 */       String dateTimeStringValue = root.getText().substring(2, root.getText().length() - 1);
/*      */ 
/* 1568 */       int[] dateArray = null;
/* 1569 */       int[] timeArray = null;
/*      */ 
/* 1571 */       if (dateTimeStringValue.isEmpty()) {
/* 1572 */         result.append("System.currentTimeMillis()");
/* 1573 */       } else if (dateTimeStringValue.indexOf(" ") > 0) {
/* 1574 */         String dateStringValue = dateTimeStringValue.substring(0, dateTimeStringValue.indexOf(" "));
/* 1575 */         String timeStringValue = dateTimeStringValue.substring(dateTimeStringValue.indexOf(" ") + 1);
/* 1576 */         dateArray = getDateToArray(dateStringValue, ".");
/* 1577 */         timeArray = getTimeToArray(timeStringValue, ":");
/* 1578 */       } else if (dateTimeStringValue.indexOf(".") > 0) {
/* 1579 */         String dateStringValue = dateTimeStringValue;
/* 1580 */         dateArray = getDateToArray(dateStringValue, ".");
/* 1581 */         timeArray = new int[3];
/* 1582 */         timeArray[0] = 0;
/* 1583 */         timeArray[1] = 0;
/* 1584 */         timeArray[2] = 0;
/* 1585 */       } else if (dateTimeStringValue.indexOf(":") > 0) {
/* 1586 */         String timeStringValue = dateTimeStringValue;
/* 1587 */         timeArray = getTimeToArray(timeStringValue, ":");
/* 1588 */         dateArray = new int[3];
/* 1589 */         Calendar c = Calendar.getInstance();
/* 1590 */         dateArray[0] = c.get(1);
/* 1591 */         dateArray[1] = c.get(2);
/* 1592 */         dateArray[2] = c.get(5);
/*      */       } else {
/* 1594 */         throw new JFException(new StringBuilder().append("Invalid datetime value: ").append(dateTimeStringValue).toString());
/*      */       }
/* 1596 */       if ((dateArray != null) && (timeArray != null)) {
/* 1597 */         Calendar c = Calendar.getInstance();
/* 1598 */         c.set(dateArray[0], dateArray[1], dateArray[2], timeArray[0], timeArray[1], timeArray[2]);
/* 1599 */         result.append(new StringBuilder().append("").append(c.getTimeInMillis()).toString());
/*      */       }
/*      */     }
/*      */ 
/* 1603 */     return result.toString();
/*      */   }
/*      */ 
/*      */   private static ASTNode getCastExpressionChildSecondNode(ASTNode root) {
/* 1607 */     ASTNode result = null;
/* 1608 */     if ((root.getId() == 52) && 
/* 1609 */       (root.getChildren() != null) && (root.getChildren().length > 1) && (root.getChildren()[0].getId() == 168))
/*      */     {
/* 1611 */       result = root.getChildren()[1];
/*      */     }
/*      */ 
/* 1614 */     return result;
/*      */   }
/*      */ 
/*      */   public static String getAssignmentExpression(ASTNode root) {
/* 1618 */     StringBuilder result = new StringBuilder();
/* 1619 */     if ((root != null) && (root.getText() != null) && (!root.getText().isEmpty())) {
/* 1620 */       result.append(root.getText());
/*      */     }
/*      */ 
/* 1623 */     boolean squareOpened = false;
/* 1624 */     boolean isFnExpression = false;
/* 1625 */     boolean isArrayExpression = false;
/* 1626 */     if ((root != null) && (root.getChildren() != null)) {
/* 1627 */       for (ASTNode child : root.getChildren()) {
/* 1628 */         if (getCastExpressionChildSecondNode(root) != null) {
/* 1629 */           if (getCastExpressionChildSecondNode(root).getId() == 66) {
/* 1630 */             isFnExpression = true;
/* 1631 */             isArrayExpression = false;
/* 1632 */           } else if (getCastExpressionChildSecondNode(root).getId() == 64) {
/* 1633 */             isArrayExpression = true;
/*      */           }
/*      */         }
/* 1636 */         if ((child.getText() != null) && (!child.getText().isEmpty())) {
/* 1637 */           if (child.getText().equals("[")) {
/* 1638 */             squareOpened = true;
/*      */ 
/* 1640 */             result.append(child.getText());
/* 1641 */             result.append("toInt(");
/* 1642 */           } else if (child.getText().equals(")")) {
/* 1643 */             result.append(child.getText());
/* 1644 */           } else if (child.getText().equals("]")) {
/* 1645 */             squareOpened = false;
/* 1646 */             result.append(")");
/* 1647 */             result.append(child.getText());
/* 1648 */           } else if (child.getText().equals(",")) {
/* 1649 */             if (isArrayExpression)
/* 1650 */               result.append(")][toInt(");
/*      */             else
/* 1652 */               result.append(child.getText());
/*      */           }
/* 1654 */           else if ((child.getId() == 171) || (child.getId() == 172) || (child.getId() == 175))
/*      */           {
/* 1656 */             result.append(normalizeInt(child.getText()));
/*      */           } else {
/* 1658 */             result.append(child.getText());
/*      */           }
/*      */         }
/* 1661 */         getAssignmentExpression(child, result, isArrayExpression);
/*      */       }
/*      */     }
/* 1664 */     return result.toString();
/*      */   }
/*      */ 
/*      */   public static boolean getAssignmentExpression(ASTNode root, StringBuilder result, boolean prevSquareOpened) {
/* 1668 */     boolean squareOpened = false;
/* 1669 */     boolean isFnExpression = false;
/* 1670 */     boolean isArrayExpression = prevSquareOpened;
/* 1671 */     if (result == null) {
/* 1672 */       result = new StringBuilder();
/* 1673 */       if ((root.getText() != null) && (!root.getText().isEmpty())) {
/* 1674 */         result.append(root.getText());
/*      */       }
/*      */     }
/* 1677 */     if (root.getChildren() != null) {
/* 1678 */       for (int i = 0; i < root.getChildren().length; i++) {
/* 1679 */         ASTNode child = root.getChildren()[i];
/* 1680 */         if (getCastExpressionChildSecondNode(root) != null) {
/* 1681 */           if (getCastExpressionChildSecondNode(root).getId() == 66) {
/* 1682 */             isFnExpression = true;
/* 1683 */             isArrayExpression = false;
/* 1684 */           } else if (getCastExpressionChildSecondNode(root).getId() == 64) {
/* 1685 */             isArrayExpression = true;
/*      */           }
/*      */         }
/* 1688 */         if ((child.getText() != null) && (!child.getText().isEmpty())) {
/* 1689 */           if (child.getText().equals("[")) {
/* 1690 */             squareOpened = true;
/* 1691 */             isArrayExpression = true;
/* 1692 */             result.append(child.getText());
/* 1693 */             result.append("toInt(");
/* 1694 */           } else if (child.getText().equals(")")) {
/* 1695 */             result.append(child.getText());
/* 1696 */           } else if (child.getText().equals("]"))
/*      */           {
/* 1698 */             isArrayExpression = false;
/* 1699 */             result.append(")");
/* 1700 */             result.append(child.getText());
/* 1701 */           } else if (child.getText().equals(",")) {
/* 1702 */             if (isArrayExpression)
/* 1703 */               result.append(")][toInt(");
/*      */             else
/* 1705 */               result.append(child.getText());
/*      */           }
/* 1707 */           else if ((child.getId() == 171) || (child.getId() == 172) || (child.getId() == 175))
/*      */           {
/* 1709 */             result.append(normalizeInt(child.getText()));
/*      */           } else {
/* 1711 */             result.append(child.getText());
/*      */           }
/*      */         }
/* 1714 */         if ((child.getChildren() != null) && (child.getChildren().length > 0)) {
/* 1715 */           getAssignmentExpression(child, result, isArrayExpression);
/*      */         }
/*      */       }
/*      */     }
/* 1719 */     return true;
/*      */   }
/*      */ 
/*      */   public static final ASTNode getOperatorNode(ASTNode root)
/*      */   {
/* 1729 */     ASTNode result = null;
/* 1730 */     if ((root != null) && (root.getChildren() != null) && (root.getChildren().length > 0)) {
/* 1731 */       for (ASTNode node : root.getChildren()) {
/* 1732 */         if ((node.getId() == 74) || (node.getId() == 75) || (node.getId() == 76) || (node.getId() == 77) || (node.getId() == 78) || (node.getId() == 79) || (node.getId() == 80) || (node.getId() == 81) || (node.getId() == 82) || (node.getId() == 83) || (node.getId() == 84))
/*      */         {
/* 1742 */           result = node;
/* 1743 */           break;
/*      */         }
/* 1745 */         result = getOperatorNode(node);
/* 1746 */         if (result != null)
/*      */         {
/*      */           break;
/*      */         }
/*      */       }
/*      */     }
/* 1752 */     return result;
/*      */   }
/*      */ 
/*      */   public static String normalizeInt(String image) {
/* 1756 */     String result = image;
/* 1757 */     if ((!image.startsWith("0x")) && (!image.startsWith("0X")))
/*      */     {
/* 1759 */       if ((image.startsWith("0")) && (image.length() > 1)) {
/* 1760 */         while ((result.startsWith("0")) && (!result.isEmpty())) {
/* 1761 */           result = result.substring(result.indexOf("0") + 1, result.length());
/*      */         }
/* 1763 */         if (result.isEmpty())
/* 1764 */           result = "0";
/*      */       }
/*      */     }
/* 1767 */     return result;
/*      */   }
/*      */ 
/*      */   public static boolean isCastExpressionArray(ASTNode node) {
/* 1771 */     boolean result = false;
/* 1772 */     if ((node != null) && (node.getChildren() != null) && (node.getChildren().length == 4) && 
/* 1773 */       (node.getChildren()[0].getId() == 168) && (node.getChildren()[1].getId() == 64) && (node.getChildren()[3].getId() == 65))
/*      */     {
/* 1776 */       result = true;
/*      */     }
/*      */ 
/* 1779 */     return result;
/*      */   }
/*      */ 
/*      */   public static boolean isCastExpressionFunction(ASTNode node) {
/* 1783 */     boolean result = false;
/* 1784 */     if ((node != null) && (node.getChildren() != null)) {
/* 1785 */       if (node.getChildren()[0].getId() == 57) {
/* 1786 */         node = node.getChildren()[1];
/*      */       }
/*      */ 
/* 1789 */       if ((node.getChildren() != null) && (node.getChildren().length == 4) && 
/* 1790 */         (node.getChildren()[0].getId() == 168) && (node.getChildren()[1].getId() == 66) && (node.getChildren()[3].getId() == 67))
/*      */       {
/* 1793 */         result = true;
/*      */       }
/*      */ 
/* 1796 */       if ((node.getChildren() != null) && (node.getChildren().length == 3) && 
/* 1797 */         (node.getChildren()[0].getId() == 168) && (node.getChildren()[1].getId() == 66) && (node.getChildren()[2].getId() == 67))
/*      */       {
/* 1800 */         result = true;
/*      */       }
/*      */     }
/*      */ 
/* 1804 */     return result;
/*      */   }
/*      */ 
/*      */   public static String getTypedValue(String expression, String type) {
/* 1808 */     StringBuilder result = new StringBuilder();
/* 1809 */     if ((expression != null) && (!expression.isEmpty())) {
/* 1810 */       if ((type != null) && (!type.isEmpty())) {
/* 1811 */         if (type.startsWith("int")) {
/* 1812 */           result.append("toInt(");
/* 1813 */           result.append(expression);
/* 1814 */           result.append(")");
/* 1815 */         } else if ((type.startsWith("long")) || (type.startsWith("datetime"))) {
/* 1816 */           result.append("toLong(");
/* 1817 */           result.append(expression);
/* 1818 */           if (!expression.endsWith("L"))
/*      */             try {
/* 1820 */               Long.valueOf(expression);
/* 1821 */               result.append("L");
/*      */             }
/*      */             catch (Exception ex) {
/*      */             }
/* 1825 */           result.append(")");
/* 1826 */         } else if (type.indexOf("tring") > -1) {
/* 1827 */           result.append("toString(");
/* 1828 */           result.append(expression);
/* 1829 */           result.append(")");
/* 1830 */         } else if (type.startsWith("double")) {
/* 1831 */           result.append("toDouble(");
/* 1832 */           result.append(expression);
/* 1833 */           result.append(")");
/* 1834 */         } else if (type.startsWith("bool")) {
/* 1835 */           result.append("Bool(");
/* 1836 */           result.append(expression);
/* 1837 */           result.append(")");
/* 1838 */         } else if ((type.startsWith("color")) || (type.startsWith("Color"))) {
/* 1839 */           result.append("toColor(");
/* 1840 */           result.append(expression);
/* 1841 */           result.append(")");
/*      */         } else {
/* 1843 */           result.append(expression);
/*      */         }
/*      */       }
/* 1846 */       else result.append(expression);
/*      */     }
/*      */ 
/* 1849 */     return result.toString();
/*      */   }
/*      */ 
/*      */   public static List<Integer> getColorParamList(FunctionDeclaration fun)
/*      */   {
/* 1858 */     String[] masks = { "color", "clr" };
/* 1859 */     String[] types = { "int", "long" };
/* 1860 */     List colorList = new ArrayList();
/* 1861 */     for (String type : types) {
/* 1862 */       for (String mask : masks) {
/* 1863 */         colorList.addAll(getTypeNameParams(fun, type, mask));
/* 1864 */         colorList.addAll(getTypeNameParams(fun, type, mask.toUpperCase()));
/*      */       }
/*      */     }
/* 1867 */     return colorList;
/*      */   }
/*      */ 
/*      */   public static List<Integer> getNumericParamList(FunctionDeclaration fun)
/*      */   {
/* 1876 */     String[] types = { "byte", "int", "long", "double" };
/* 1877 */     List colorList = new ArrayList();
/* 1878 */     for (String type : types) {
/* 1879 */       colorList.addAll(getTypeParams(fun, type));
/*      */     }
/* 1881 */     return colorList;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.DeclarationHelpers
 * JD-Core Version:    0.6.0
 */