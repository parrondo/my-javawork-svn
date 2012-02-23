/*      */ package org.eclipse.jdt.core.compiler;
/*      */ 
/*      */ import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;
/*      */ 
/*      */ public final class CharOperation
/*      */ {
/*   27 */   public static final char[] NO_CHAR = new char[0];
/*      */ 
/*   32 */   public static final char[][] NO_CHAR_CHAR = new char[0][];
/*      */ 
/*   38 */   public static final String[] NO_STRINGS = new String[0];
/*      */ 
/*      */   public static final char[] append(char[] array, char suffix)
/*      */   {
/*   64 */     if (array == null)
/*   65 */       return new char[] { suffix };
/*   66 */     int length = array.length;
/*   67 */     System.arraycopy(array, 0, array = new char[length + 1], 0, length);
/*   68 */     array[length] = suffix;
/*   69 */     return array;
/*      */   }
/*      */ 
/*      */   public static final char[] append(char[] target, int index, char[] array, int start, int end)
/*      */   {
/*  116 */     int targetLength = target.length;
/*  117 */     int subLength = end - start;
/*  118 */     int newTargetLength = subLength + index;
/*  119 */     if (newTargetLength > targetLength) {
/*  120 */       System.arraycopy(target, 0, target = new char[newTargetLength * 2], 0, index);
/*      */     }
/*  122 */     System.arraycopy(array, start, target, index, subLength);
/*  123 */     return target;
/*      */   }
/*      */ 
/*      */   public static final char[][] arrayConcat(char[][] first, char[][] second)
/*      */   {
/*  165 */     if (first == null)
/*  166 */       return second;
/*  167 */     if (second == null) {
/*  168 */       return first;
/*      */     }
/*  170 */     int length1 = first.length;
/*  171 */     int length2 = second.length;
/*  172 */     char[][] result = new char[length1 + length2][];
/*  173 */     System.arraycopy(first, 0, result, 0, length1);
/*  174 */     System.arraycopy(second, 0, result, length1, length2);
/*  175 */     return result;
/*      */   }
/*      */ 
/*      */   public static final boolean camelCaseMatch(char[] pattern, char[] name)
/*      */   {
/*  239 */     if (pattern == null)
/*  240 */       return true;
/*  241 */     if (name == null) {
/*  242 */       return false;
/*      */     }
/*  244 */     return camelCaseMatch(pattern, 0, pattern.length, name, 0, name.length, false);
/*      */   }
/*      */ 
/*      */   public static final boolean camelCaseMatch(char[] pattern, char[] name, boolean samePartCount)
/*      */   {
/*  318 */     if (pattern == null)
/*  319 */       return true;
/*  320 */     if (name == null) {
/*  321 */       return false;
/*      */     }
/*  323 */     return camelCaseMatch(pattern, 0, pattern.length, name, 0, name.length, samePartCount);
/*      */   }
/*      */ 
/*      */   public static final boolean camelCaseMatch(char[] pattern, int patternStart, int patternEnd, char[] name, int nameStart, int nameEnd)
/*      */   {
/*  425 */     return camelCaseMatch(pattern, patternStart, patternEnd, name, nameStart, nameEnd, false);
/*      */   }
/*      */ 
/*      */   public static final boolean camelCaseMatch(char[] pattern, int patternStart, int patternEnd, char[] name, int nameStart, int nameEnd, boolean samePartCount)
/*      */   {
/*  546 */     if (name == null)
/*  547 */       return false;
/*  548 */     if (pattern == null)
/*  549 */       return true;
/*  550 */     if (patternEnd < 0) patternEnd = pattern.length;
/*  551 */     if (nameEnd < 0) nameEnd = name.length;
/*      */ 
/*  553 */     if (patternEnd <= patternStart) return nameEnd <= nameStart;
/*  554 */     if (nameEnd <= nameStart) return false;
/*      */ 
/*  556 */     if (name[nameStart] != pattern[patternStart])
/*      */     {
/*  558 */       return false; } 
/*      */ int iPattern = patternStart;
/*  563 */     int iName = nameStart;
/*      */     char patternChar;
/*      */     char nameChar;
/*      */     label357: 
/*      */     do { do { iPattern++;
/*  569 */         iName++;
/*      */ 
/*  571 */         if (iPattern == patternEnd)
/*      */         {
/*  573 */           if ((!samePartCount) || (iName == nameEnd)) return true;
/*      */ 
/*      */           while (true)
/*      */           {
/*  577 */             if (iName == nameEnd)
/*      */             {
/*  579 */               return true;
/*      */             }
/*  581 */             char nameChar = name[iName];
/*      */ 
/*  583 */             if (nameChar < '') {
/*  584 */               if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[nameChar] & 0x20) != 0) {
/*  585 */                 return false;
/*      */               }
/*      */             }
/*  588 */             else if ((!Character.isJavaIdentifierPart(nameChar)) || (Character.isUpperCase(nameChar))) {
/*  589 */               return false;
/*      */             }
/*  591 */             iName++;
/*      */           }
/*      */         }
/*      */ 
/*  595 */         if (iName == nameEnd)
/*      */         {
/*  597 */           return false;
/*      */         }
/*      */       }
/*      */ 
/*  601 */       while ((patternChar = pattern[iPattern]) == name[iName]);
/*      */ 
/*  606 */       if (patternChar < '') {
/*  607 */         if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[patternChar] & 0x24) == 0) {
/*  608 */           return false;
/*      */         }
/*      */       }
/*  611 */       else if ((Character.isJavaIdentifierPart(patternChar)) && (!Character.isUpperCase(patternChar)) && (!Character.isDigit(patternChar))) {
/*  612 */         return false;
/*      */       }
/*      */ 
/*      */       while (true)
/*      */       {
/*  617 */         if (iName == nameEnd)
/*      */         {
/*  619 */           return false;
/*      */         }
/*      */ 
/*  622 */         nameChar = name[iName];
/*  623 */         if (nameChar < '') {
/*  624 */           int charNature = ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[nameChar];
/*  625 */           if ((charNature & 0x90) != 0)
/*      */           {
/*  627 */             iName++; continue;
/*  628 */           }if ((charNature & 0x4) != 0)
/*      */           {
/*  630 */             if (patternChar == nameChar) break;
/*  631 */             iName++; continue;
/*      */           }
/*  633 */           if (patternChar == nameChar)
/*      */             break;
/*  635 */           return false;
/*      */         }
/*      */ 
/*  642 */         if ((Character.isJavaIdentifierPart(nameChar)) && (!Character.isUpperCase(nameChar))) {
/*  643 */           iName++; continue;
/*  644 */         }if (!Character.isDigit(nameChar)) break label357; if (patternChar == nameChar) break;
/*  646 */         iName++;
/*      */       } }
/*  647 */     while (patternChar == nameChar);
/*  648 */     return false;
/*      */   }
/*      */ 
/*      */   public static String[] charArrayToStringArray(char[][] charArrays)
/*      */   {
/*  666 */     if (charArrays == null)
/*  667 */       return null;
/*  668 */     int length = charArrays.length;
/*  669 */     if (length == 0)
/*  670 */       return NO_STRINGS;
/*  671 */     String[] strings = new String[length];
/*  672 */     for (int i = 0; i < length; i++)
/*  673 */       strings[i] = new String(charArrays[i]);
/*  674 */     return strings;
/*      */   }
/*      */ 
/*      */   public static String charToString(char[] charArray)
/*      */   {
/*  685 */     if (charArray == null) return null;
/*  686 */     return new String(charArray);
/*      */   }
/*      */ 
/*      */   public static final char[][] arrayConcat(char[][] first, char[] second)
/*      */   {
/*  722 */     if (second == null)
/*  723 */       return first;
/*  724 */     if (first == null) {
/*  725 */       return new char[][] { second };
/*      */     }
/*  727 */     int length = first.length;
/*  728 */     char[][] result = new char[length + 1][];
/*  729 */     System.arraycopy(first, 0, result, 0, length);
/*  730 */     result[length] = second;
/*  731 */     return result;
/*      */   }
/*      */ 
/*      */   public static final int compareTo(char[] array1, char[] array2)
/*      */   {
/*  747 */     int length1 = array1.length;
/*  748 */     int length2 = array2.length;
/*  749 */     int min = Math.min(length1, length2);
/*  750 */     for (int i = 0; i < min; i++) {
/*  751 */       if (array1[i] != array2[i]) {
/*  752 */         return array1[i] - array2[i];
/*      */       }
/*      */     }
/*  755 */     return length1 - length2;
/*      */   }
/*      */ 
/*      */   public static final int compareWith(char[] array, char[] prefix)
/*      */   {
/*  813 */     int arrayLength = array.length;
/*  814 */     int prefixLength = prefix.length;
/*  815 */     int min = Math.min(arrayLength, prefixLength);
/*  816 */     int i = 0;
/*  817 */     while (min-- != 0) {
/*  818 */       char c1 = array[i];
/*  819 */       char c2 = prefix[(i++)];
/*  820 */       if (c1 != c2)
/*  821 */         return c1 - c2;
/*      */     }
/*  823 */     if (prefixLength == i)
/*  824 */       return 0;
/*  825 */     return -1;
/*      */   }
/*      */ 
/*      */   public static final char[] concat(char[] first, char[] second)
/*      */   {
/*  861 */     if (first == null)
/*  862 */       return second;
/*  863 */     if (second == null) {
/*  864 */       return first;
/*      */     }
/*  866 */     int length1 = first.length;
/*  867 */     int length2 = second.length;
/*  868 */     char[] result = new char[length1 + length2];
/*  869 */     System.arraycopy(first, 0, result, 0, length1);
/*  870 */     System.arraycopy(second, 0, result, length1, length2);
/*  871 */     return result;
/*      */   }
/*      */ 
/*      */   public static final char[] concat(char[] first, char[] second, char[] third)
/*      */   {
/*  930 */     if (first == null)
/*  931 */       return concat(second, third);
/*  932 */     if (second == null)
/*  933 */       return concat(first, third);
/*  934 */     if (third == null) {
/*  935 */       return concat(first, second);
/*      */     }
/*  937 */     int length1 = first.length;
/*  938 */     int length2 = second.length;
/*  939 */     int length3 = third.length;
/*  940 */     char[] result = new char[length1 + length2 + length3];
/*  941 */     System.arraycopy(first, 0, result, 0, length1);
/*  942 */     System.arraycopy(second, 0, result, length1, length2);
/*  943 */     System.arraycopy(third, 0, result, length1 + length2, length3);
/*  944 */     return result;
/*      */   }
/*      */ 
/*      */   public static final char[] concat(char[] first, char[] second, char separator)
/*      */   {
/*  989 */     if (first == null)
/*  990 */       return second;
/*  991 */     if (second == null) {
/*  992 */       return first;
/*      */     }
/*  994 */     int length1 = first.length;
/*  995 */     if (length1 == 0)
/*  996 */       return second;
/*  997 */     int length2 = second.length;
/*  998 */     if (length2 == 0) {
/*  999 */       return first;
/*      */     }
/* 1001 */     char[] result = new char[length1 + length2 + 1];
/* 1002 */     System.arraycopy(first, 0, result, 0, length1);
/* 1003 */     result[length1] = separator;
/* 1004 */     System.arraycopy(second, 0, result, length1 + 1, length2);
/* 1005 */     return result;
/*      */   }
/*      */ 
/*      */   public static final char[] concat(char[] first, char sep1, char[] second, char sep2, char[] third)
/*      */   {
/* 1074 */     if (first == null)
/* 1075 */       return concat(second, third, sep2);
/* 1076 */     if (second == null)
/* 1077 */       return concat(first, third, sep1);
/* 1078 */     if (third == null) {
/* 1079 */       return concat(first, second, sep1);
/*      */     }
/* 1081 */     int length1 = first.length;
/* 1082 */     int length2 = second.length;
/* 1083 */     int length3 = third.length;
/* 1084 */     char[] result = new char[length1 + length2 + length3 + 2];
/* 1085 */     System.arraycopy(first, 0, result, 0, length1);
/* 1086 */     result[length1] = sep1;
/* 1087 */     System.arraycopy(second, 0, result, length1 + 1, length2);
/* 1088 */     result[(length1 + length2 + 1)] = sep2;
/* 1089 */     System.arraycopy(third, 0, result, length1 + length2 + 2, length3);
/* 1090 */     return result;
/*      */   }
/*      */ 
/*      */   public static final char[] concat(char prefix, char[] array, char suffix)
/*      */   {
/* 1122 */     if (array == null) {
/* 1123 */       return new char[] { prefix, suffix };
/*      */     }
/* 1125 */     int length = array.length;
/* 1126 */     char[] result = new char[length + 2];
/* 1127 */     result[0] = prefix;
/* 1128 */     System.arraycopy(array, 0, result, 1, length);
/* 1129 */     result[(length + 1)] = suffix;
/* 1130 */     return result;
/*      */   }
/*      */ 
/*      */   public static final char[] concatWith(char[] name, char[][] array, char separator)
/*      */   {
/* 1171 */     int nameLength = name == null ? 0 : name.length;
/* 1172 */     if (nameLength == 0) {
/* 1173 */       return concatWith(array, separator);
/*      */     }
/* 1175 */     int length = array == null ? 0 : array.length;
/* 1176 */     if (length == 0) {
/* 1177 */       return name;
/*      */     }
/* 1179 */     int size = nameLength;
/* 1180 */     int index = length;
/*      */     do {
/* 1182 */       if (array[index].length > 0)
/* 1183 */         size += array[index].length + 1;
/* 1181 */       index--; } while (index >= 0);
/*      */ 
/* 1184 */     char[] result = new char[size];
/* 1185 */     index = size;
/* 1186 */     for (int i = length - 1; i >= 0; i--) {
/* 1187 */       int subLength = array[i].length;
/* 1188 */       if (subLength > 0) {
/* 1189 */         index -= subLength;
/* 1190 */         System.arraycopy(array[i], 0, result, index, subLength);
/* 1191 */         index--; result[index] = separator;
/*      */       }
/*      */     }
/* 1194 */     System.arraycopy(name, 0, result, 0, nameLength);
/* 1195 */     return result;
/*      */   }
/*      */ 
/*      */   public static final char[] concatWith(char[][] array, char[] name, char separator)
/*      */   {
/* 1236 */     int nameLength = name == null ? 0 : name.length;
/* 1237 */     if (nameLength == 0) {
/* 1238 */       return concatWith(array, separator);
/*      */     }
/* 1240 */     int length = array == null ? 0 : array.length;
/* 1241 */     if (length == 0) {
/* 1242 */       return name;
/*      */     }
/* 1244 */     int size = nameLength;
/* 1245 */     int index = length;
/*      */     do {
/* 1247 */       if (array[index].length > 0)
/* 1248 */         size += array[index].length + 1;
/* 1246 */       index--; } while (index >= 0);
/*      */ 
/* 1249 */     char[] result = new char[size];
/* 1250 */     index = 0;
/* 1251 */     for (int i = 0; i < length; i++) {
/* 1252 */       int subLength = array[i].length;
/* 1253 */       if (subLength > 0) {
/* 1254 */         System.arraycopy(array[i], 0, result, index, subLength);
/* 1255 */         index += subLength;
/* 1256 */         result[(index++)] = separator;
/*      */       }
/*      */     }
/* 1259 */     System.arraycopy(name, 0, result, index, nameLength);
/* 1260 */     return result;
/*      */   }
/*      */ 
/*      */   public static final char[] concatWith(char[][] array, char separator)
/*      */   {
/* 1287 */     int length = array == null ? 0 : array.length;
/* 1288 */     if (length == 0) {
/* 1289 */       return NO_CHAR;
/*      */     }
/* 1291 */     int size = length - 1;
/* 1292 */     int index = length;
/*      */     do {
/* 1294 */       if (array[index].length == 0)
/* 1295 */         size--;
/*      */       else
/* 1297 */         size += array[index].length;
/* 1293 */       index--; } while (index >= 0);
/*      */ 
/* 1299 */     if (size <= 0)
/* 1300 */       return NO_CHAR;
/* 1301 */     char[] result = new char[size];
/* 1302 */     index = length;
/*      */     do {
/* 1304 */       length = array[index].length;
/* 1305 */       if (length > 0) {
/* 1306 */         System.arraycopy(
/* 1307 */           array[index], 
/* 1308 */           0, 
/* 1309 */           result, 
/* 1310 */           size -= length, 
/* 1311 */           length);
/* 1312 */         size--; if (size >= 0)
/* 1313 */           result[size] = separator;
/*      */       }
/* 1303 */       index--; } while (index >= 0);
/*      */ 
/* 1316 */     return result;
/*      */   }
/*      */ 
/*      */   public static final boolean contains(char character, char[][] array)
/*      */   {
/* 1346 */     int i = array.length;
/*      */     do { char[] subarray = array[i];
/* 1348 */       int j = subarray.length;
/*      */       do { if (subarray[j] == character)
/* 1350 */           return true;
/* 1348 */         j--; } while (j >= 0);
/*      */ 
/* 1346 */       i--; } while (i >= 0);
/*      */ 
/* 1352 */     return false;
/*      */   }
/*      */ 
/*      */   public static final boolean contains(char character, char[] array)
/*      */   {
/* 1382 */     int i = array.length;
/*      */     do { if (array[i] == character)
/* 1384 */         return true;
/* 1382 */       i--; } while (i >= 0);
/*      */ 
/* 1385 */     return false;
/*      */   }
/*      */ 
/*      */   public static final boolean contains(char[] characters, char[] array)
/*      */   {
/* 1416 */     int i = array.length;
/*      */     do { int j = characters.length;
/*      */       do { if (array[i] == characters[j])
/* 1419 */           return true;
/* 1417 */         j--; } while (j >= 0);
/*      */ 
/* 1416 */       i--; } while (i >= 0);
/*      */ 
/* 1420 */     return false;
/*      */   }
/*      */ 
/*      */   public static final char[][] deepCopy(char[][] toCopy)
/*      */   {
/* 1431 */     int toCopyLength = toCopy.length;
/* 1432 */     char[][] result = new char[toCopyLength][];
/* 1433 */     for (int i = 0; i < toCopyLength; i++) {
/* 1434 */       char[] toElement = toCopy[i];
/* 1435 */       int toElementLength = toElement.length;
/* 1436 */       char[] resultElement = new char[toElementLength];
/* 1437 */       System.arraycopy(toElement, 0, resultElement, 0, toElementLength);
/* 1438 */       result[i] = resultElement;
/*      */     }
/* 1440 */     return result;
/*      */   }
/*      */ 
/*      */   public static final boolean endsWith(char[] array, char[] toBeFound)
/*      */   {
/* 1471 */     int i = toBeFound.length;
/* 1472 */     int j = array.length - i;
/*      */ 
/* 1474 */     if (j < 0)
/* 1475 */       return false;
/*      */     do {
/* 1477 */       if (toBeFound[i] != array[(i + j)])
/* 1478 */         return false;
/* 1476 */       i--; } while (i >= 0);
/*      */ 
/* 1479 */     return true;
/*      */   }
/*      */ 
/*      */   public static final boolean equals(char[][] first, char[][] second)
/*      */   {
/* 1519 */     if (first == second)
/* 1520 */       return true;
/* 1521 */     if ((first == null) || (second == null))
/* 1522 */       return false;
/* 1523 */     if (first.length != second.length) {
/* 1524 */       return false;
/*      */     }
/* 1526 */     int i = first.length;
/*      */     do { if (!equals(first[i], second[i]))
/* 1528 */         return false;
/* 1526 */       i--; } while (i >= 0);
/*      */ 
/* 1529 */     return true;
/*      */   }
/*      */ 
/*      */   public static final boolean equals(char[][] first, char[][] second, boolean isCaseSensitive)
/*      */   {
/* 1582 */     if (isCaseSensitive) {
/* 1583 */       return equals(first, second);
/*      */     }
/* 1585 */     if (first == second)
/* 1586 */       return true;
/* 1587 */     if ((first == null) || (second == null))
/* 1588 */       return false;
/* 1589 */     if (first.length != second.length) {
/* 1590 */       return false;
/*      */     }
/* 1592 */     int i = first.length;
/*      */     do { if (!equals(first[i], second[i], false))
/* 1594 */         return false;
/* 1592 */       i--; } while (i >= 0);
/*      */ 
/* 1595 */     return true;
/*      */   }
/*      */ 
/*      */   public static final boolean equals(char[] first, char[] second)
/*      */   {
/* 1635 */     if (first == second)
/* 1636 */       return true;
/* 1637 */     if ((first == null) || (second == null))
/* 1638 */       return false;
/* 1639 */     if (first.length != second.length) {
/* 1640 */       return false;
/*      */     }
/* 1642 */     int i = first.length;
/*      */     do { if (first[i] != second[i])
/* 1644 */         return false;
/* 1642 */       i--; } while (i >= 0);
/*      */ 
/* 1645 */     return true;
/*      */   }
/*      */ 
/*      */   public static final boolean equals(char[] first, char[] second, int secondStart, int secondEnd)
/*      */   {
/* 1697 */     return equals(first, second, secondStart, secondEnd, true);
/*      */   }
/*      */ 
/*      */   public static final boolean equals(char[] first, char[] second, int secondStart, int secondEnd, boolean isCaseSensitive)
/*      */   {
/* 1761 */     if (first == second)
/* 1762 */       return true;
/* 1763 */     if ((first == null) || (second == null))
/* 1764 */       return false;
/* 1765 */     if (first.length != secondEnd - secondStart)
/* 1766 */       return false;
/* 1767 */     if (isCaseSensitive) {
/* 1768 */       int i = first.length;
/*      */       do { if (first[i] != second[(i + secondStart)])
/* 1770 */           return false;
/* 1768 */         i--; } while (i >= 0);
/*      */     }
/*      */     else
/*      */     {
/* 1772 */       int i = first.length;
/*      */       do { if (ScannerHelper.toLowerCase(first[i]) != ScannerHelper.toLowerCase(second[(i + secondStart)]))
/* 1774 */           return false;
/* 1772 */         i--; } while (i >= 0);
/*      */     }
/*      */ 
/* 1776 */     return true;
/*      */   }
/*      */ 
/*      */   public static final boolean equals(char[] first, char[] second, boolean isCaseSensitive)
/*      */   {
/* 1829 */     if (isCaseSensitive) {
/* 1830 */       return equals(first, second);
/*      */     }
/* 1832 */     if (first == second)
/* 1833 */       return true;
/* 1834 */     if ((first == null) || (second == null))
/* 1835 */       return false;
/* 1836 */     if (first.length != second.length) {
/* 1837 */       return false;
/*      */     }
/* 1839 */     int i = first.length;
/*      */     do { if (ScannerHelper.toLowerCase(first[i]) != 
/* 1841 */         ScannerHelper.toLowerCase(second[i]))
/* 1842 */         return false;
/* 1839 */       i--; } while (i >= 0);
/*      */ 
/* 1843 */     return true;
/*      */   }
/*      */ 
/*      */   public static final boolean fragmentEquals(char[] fragment, char[] name, int startIndex, boolean isCaseSensitive)
/*      */   {
/* 1902 */     int max = fragment.length;
/* 1903 */     if (name.length < max + startIndex)
/* 1904 */       return false;
/* 1905 */     if (isCaseSensitive) {
/* 1906 */       int i = max;
/*      */       do
/*      */       {
/* 1909 */         if (fragment[i] != name[(i + startIndex)])
/* 1910 */           return false;
/* 1907 */         i--; } while (i >= 0);
/*      */ 
/* 1911 */       return true;
/*      */     }
/* 1913 */     int i = max;
/*      */     do
/*      */     {
/* 1916 */       if (ScannerHelper.toLowerCase(fragment[i]) != 
/* 1917 */         ScannerHelper.toLowerCase(name[(i + startIndex)]))
/* 1918 */         return false;
/* 1914 */       i--; } while (i >= 0);
/*      */ 
/* 1919 */     return true;
/*      */   }
/*      */ 
/*      */   public static final int hashCode(char[] array)
/*      */   {
/* 1930 */     int length = array.length;
/* 1931 */     int hash = length == 0 ? '\037' : array[0];
/* 1932 */     if (length < 8) {
/* 1933 */       int i = length;
/*      */       do { hash = hash * 31 + array[i];
/*      */ 
/* 1933 */         i--; } while (i > 0);
/*      */     }
/*      */     else
/*      */     {
/* 1937 */       int i = length - 1; for (int last = i > 16 ? i - 16 : 0; i > last; i -= 2)
/* 1938 */         hash = hash * 31 + array[i];
/*      */     }
/* 1940 */     return hash & 0x7FFFFFFF;
/*      */   }
/*      */ 
/*      */   public static boolean isWhitespace(char c)
/*      */   {
/* 1965 */     return (c < '') && ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x100) != 0);
/*      */   }
/*      */ 
/*      */   public static final int indexOf(char toBeFound, char[] array)
/*      */   {
/* 1996 */     return indexOf(toBeFound, array, 0);
/*      */   }
/*      */ 
/*      */   public static final int indexOf(char[] toBeFound, char[] array, boolean isCaseSensitive)
/*      */   {
/* 2029 */     return indexOf(toBeFound, array, isCaseSensitive, 0);
/*      */   }
/*      */ 
/*      */   public static final int indexOf(char[] toBeFound, char[] array, boolean isCaseSensitive, int start)
/*      */   {
/* 2063 */     return indexOf(toBeFound, array, isCaseSensitive, start, array.length);
/*      */   }
/*      */ 
/*      */   public static final int indexOf(char[] toBeFound, char[] array, boolean isCaseSensitive, int start, int end)
/*      */   {
/* 2098 */     int arrayLength = end;
/* 2099 */     int toBeFoundLength = toBeFound.length;
/* 2100 */     if (toBeFoundLength > arrayLength) return -1;
/* 2101 */     if (toBeFoundLength == 0) return 0;
/* 2102 */     if (toBeFoundLength == arrayLength) {
/* 2103 */       if (isCaseSensitive) {
/* 2104 */         for (int i = start; i < arrayLength; i++) {
/* 2105 */           if (array[i] != toBeFound[i]) return -1;
/*      */         }
/* 2107 */         return 0;
/*      */       }
/* 2109 */       for (int i = start; i < arrayLength; i++) {
/* 2110 */         if (ScannerHelper.toLowerCase(array[i]) != ScannerHelper.toLowerCase(toBeFound[i])) return -1;
/*      */       }
/* 2112 */       return 0;
/*      */     }
/*      */ 
/* 2115 */     if (isCaseSensitive) {
/* 2116 */       int i = start; for (int max = arrayLength - toBeFoundLength + 1; i < max; i++)
/* 2117 */         if (array[i] == toBeFound[0]) {
/* 2118 */           int j = 1;
/* 2119 */           while (array[(i + j)] == toBeFound[j])
/*      */           {
/* 2118 */             j++; if (j >= toBeFoundLength)
/*      */             {
/* 2121 */               return i;
/*      */             }
/*      */           }
/*      */         }
/*      */     } else {
/* 2125 */       int i = start; for (int max = arrayLength - toBeFoundLength + 1; i < max; i++)
/* 2126 */         if (ScannerHelper.toLowerCase(array[i]) == ScannerHelper.toLowerCase(toBeFound[0])) {
/* 2127 */           int j = 1;
/* 2128 */           while (ScannerHelper.toLowerCase(array[(i + j)]) == ScannerHelper.toLowerCase(toBeFound[j]))
/*      */           {
/* 2127 */             j++; if (j >= toBeFoundLength)
/*      */             {
/* 2130 */               return i;
/*      */             }
/*      */           }
/*      */         }
/*      */     }
/* 2134 */     return -1;
/*      */   }
/*      */ 
/*      */   public static final int indexOf(char toBeFound, char[] array, int start)
/*      */   {
/* 2177 */     for (int i = start; i < array.length; i++)
/* 2178 */       if (toBeFound == array[i])
/* 2179 */         return i;
/* 2180 */     return -1;
/*      */   }
/*      */ 
/*      */   public static final int indexOf(char toBeFound, char[] array, int start, int end)
/*      */   {
/* 2225 */     for (int i = start; i < end; i++)
/* 2226 */       if (toBeFound == array[i])
/* 2227 */         return i;
/* 2228 */     return -1;
/*      */   }
/*      */ 
/*      */   public static final int lastIndexOf(char toBeFound, char[] array)
/*      */   {
/* 2260 */     int i = array.length;
/*      */     do { if (toBeFound == array[i])
/* 2262 */         return i;
/* 2260 */       i--; } while (i >= 0);
/*      */ 
/* 2263 */     return -1;
/*      */   }
/*      */ 
/*      */   public static final int lastIndexOf(char toBeFound, char[] array, int startIndex)
/*      */   {
/* 2309 */     int i = array.length;
/*      */     do { if (toBeFound == array[i])
/* 2311 */         return i;
/* 2309 */       i--; } while (i >= startIndex);
/*      */ 
/* 2312 */     return -1;
/*      */   }
/*      */ 
/*      */   public static final int lastIndexOf(char toBeFound, char[] array, int startIndex, int endIndex)
/*      */   {
/* 2363 */     int i = endIndex;
/*      */     do { if (toBeFound == array[i])
/* 2365 */         return i;
/* 2363 */       i--; } while (i >= startIndex);
/*      */ 
/* 2366 */     return -1;
/*      */   }
/*      */ 
/*      */   public static final char[] lastSegment(char[] array, char separator)
/*      */   {
/* 2384 */     int pos = lastIndexOf(separator, array);
/* 2385 */     if (pos < 0)
/* 2386 */       return array;
/* 2387 */     return subarray(array, pos + 1, array.length);
/*      */   }
/*      */ 
/*      */   public static final boolean match(char[] pattern, char[] name, boolean isCaseSensitive)
/*      */   {
/* 2435 */     if (name == null)
/* 2436 */       return false;
/* 2437 */     if (pattern == null) {
/* 2438 */       return true;
/*      */     }
/* 2440 */     return match(
/* 2441 */       pattern, 
/* 2442 */       0, 
/* 2443 */       pattern.length, 
/* 2444 */       name, 
/* 2445 */       0, 
/* 2446 */       name.length, 
/* 2447 */       isCaseSensitive);
/*      */   }
/*      */ 
/*      */   public static final boolean match(char[] pattern, int patternStart, int patternEnd, char[] name, int nameStart, int nameEnd, boolean isCaseSensitive)
/*      */   {
/* 2503 */     if (name == null)
/* 2504 */       return false;
/* 2505 */     if (pattern == null)
/* 2506 */       return true;
/* 2507 */     int iPattern = patternStart;
/* 2508 */     int iName = nameStart;
/*      */ 
/* 2510 */     if (patternEnd < 0)
/* 2511 */       patternEnd = pattern.length;
/* 2512 */     if (nameEnd < 0) {
/* 2513 */       nameEnd = name.length;
/*      */     }
/*      */ 
/* 2516 */     char patternChar = '\000';
/* 2517 */     while ((iPattern < patternEnd) && 
/* 2518 */       ((patternChar = pattern[iPattern]) != '*')) {
/* 2519 */       if (iName == nameEnd)
/* 2520 */         return false;
/* 2521 */       if (patternChar != 
/* 2522 */         (isCaseSensitive ? 
/* 2523 */         name[iName] : 
/* 2524 */         ScannerHelper.toLowerCase(name[iName]))) {
/* 2525 */         if (patternChar != '?')
/* 2526 */           return false;
/*      */       }
/* 2528 */       iName++;
/* 2529 */       iPattern++;
/*      */     }
/*      */     int segmentStart;
/*      */     int segmentStart;
/* 2533 */     if (patternChar == '*') {
/* 2534 */       iPattern++; segmentStart = iPattern;
/*      */     } else {
/* 2536 */       segmentStart = 0;
/*      */     }
/* 2538 */     int prefixStart = iName;
/* 2539 */     while (iName < nameEnd) {
/* 2540 */       if (iPattern == patternEnd) {
/* 2541 */         iPattern = segmentStart;
/* 2542 */         prefixStart++; iName = prefixStart;
/*      */       }
/* 2546 */       else if ((patternChar = pattern[iPattern]) == '*') {
/* 2547 */         iPattern++; segmentStart = iPattern;
/* 2548 */         if (segmentStart == patternEnd) {
/* 2549 */           return true;
/*      */         }
/* 2551 */         prefixStart = iName;
/*      */       }
/*      */       else
/*      */       {
/* 2555 */         if ((isCaseSensitive ? name[iName] : ScannerHelper.toLowerCase(name[iName])) != 
/* 2556 */           patternChar)
/* 2557 */           if (patternChar != '?') {
/* 2558 */             iPattern = segmentStart;
/* 2559 */             prefixStart++; iName = prefixStart;
/* 2560 */             continue;
/*      */           }
/* 2562 */         iName++;
/* 2563 */         iPattern++;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2568 */     return (segmentStart == patternEnd) || 
/* 2567 */       ((iName == nameEnd) && (iPattern == patternEnd)) || (
/* 2568 */       (iPattern == patternEnd - 1) && (pattern[iPattern] == '*'));
/*      */   }
/*      */ 
/*      */   public static final boolean pathMatch(char[] pattern, char[] filepath, boolean isCaseSensitive, char pathSeparator)
/*      */   {
/* 2595 */     if (filepath == null)
/* 2596 */       return false;
/* 2597 */     if (pattern == null) {
/* 2598 */       return true;
/*      */     }
/*      */ 
/* 2601 */     int pSegmentStart = pattern[0] == pathSeparator ? 1 : 0;
/* 2602 */     int pLength = pattern.length;
/* 2603 */     int pSegmentEnd = indexOf(pathSeparator, pattern, pSegmentStart + 1);
/* 2604 */     if (pSegmentEnd < 0) pSegmentEnd = pLength;
/*      */ 
/* 2607 */     boolean freeTrailingDoubleStar = pattern[(pLength - 1)] == pathSeparator;
/*      */ 
/* 2610 */     int fLength = filepath.length;
/*      */     int fSegmentStart;
/*      */     int fSegmentStart;
/* 2611 */     if (filepath[0] != pathSeparator)
/* 2612 */       fSegmentStart = 0;
/*      */     else {
/* 2614 */       fSegmentStart = 1;
/*      */     }
/* 2616 */     if (fSegmentStart != pSegmentStart) {
/* 2617 */       return false;
/*      */     }
/* 2619 */     int fSegmentEnd = indexOf(pathSeparator, filepath, fSegmentStart + 1);
/* 2620 */     if (fSegmentEnd < 0) fSegmentEnd = fLength;
/*      */ 
/* 2623 */     while ((pSegmentStart < pLength) && 
/* 2624 */       ((pSegmentEnd != pLength) || (!freeTrailingDoubleStar)) && (
/* 2625 */       (pSegmentEnd != pSegmentStart + 2) || 
/* 2626 */       (pattern[pSegmentStart] != '*') || 
/* 2627 */       (pattern[(pSegmentStart + 1)] != '*')))
/*      */     {
/* 2629 */       if (fSegmentStart >= fLength) {
/* 2630 */         return false;
/*      */       }
/* 2632 */       if (!match(
/* 2633 */         pattern, 
/* 2634 */         pSegmentStart, 
/* 2635 */         pSegmentEnd, 
/* 2636 */         filepath, 
/* 2637 */         fSegmentStart, 
/* 2638 */         fSegmentEnd, 
/* 2639 */         isCaseSensitive)) {
/* 2640 */         return false;
/*      */       }
/*      */ 
/* 2644 */       pSegmentEnd = 
/* 2645 */         indexOf(
/* 2646 */         pathSeparator, 
/* 2647 */         pattern, 
/* 2648 */         pSegmentStart = pSegmentEnd + 1);
/*      */ 
/* 2650 */       if (pSegmentEnd < 0) {
/* 2651 */         pSegmentEnd = pLength;
/*      */       }
/* 2653 */       fSegmentEnd = 
/* 2654 */         indexOf(
/* 2655 */         pathSeparator, 
/* 2656 */         filepath, 
/* 2657 */         fSegmentStart = fSegmentEnd + 1);
/*      */ 
/* 2659 */       if (fSegmentEnd >= 0) continue; fSegmentEnd = fLength;
/*      */     }
/*      */     int pSegmentRestart;
/*      */     int pSegmentRestart;
/* 2664 */     if (((pSegmentStart >= pLength) && (freeTrailingDoubleStar)) || (
/* 2665 */       (pSegmentEnd == pSegmentStart + 2) && 
/* 2666 */       (pattern[pSegmentStart] == '*') && 
/* 2667 */       (pattern[(pSegmentStart + 1)] == '*'))) {
/* 2668 */       pSegmentEnd = 
/* 2669 */         indexOf(
/* 2670 */         pathSeparator, 
/* 2671 */         pattern, 
/* 2672 */         pSegmentStart = pSegmentEnd + 1);
/*      */ 
/* 2674 */       if (pSegmentEnd < 0) pSegmentEnd = pLength;
/* 2675 */       pSegmentRestart = pSegmentStart;
/*      */     } else {
/* 2677 */       if (pSegmentStart >= pLength) return fSegmentStart >= fLength;
/* 2678 */       pSegmentRestart = 0;
/*      */     }
/* 2680 */     int fSegmentRestart = fSegmentStart;
/* 2681 */     while (fSegmentStart < fLength)
/*      */     {
/* 2683 */       if (pSegmentStart >= pLength) {
/* 2684 */         if (freeTrailingDoubleStar) return true;
/*      */ 
/* 2686 */         pSegmentEnd = 
/* 2687 */           indexOf(pathSeparator, pattern, pSegmentStart = pSegmentRestart);
/* 2688 */         if (pSegmentEnd < 0) pSegmentEnd = pLength;
/*      */ 
/* 2690 */         fSegmentRestart = 
/* 2691 */           indexOf(pathSeparator, filepath, fSegmentRestart + 1);
/*      */ 
/* 2693 */         if (fSegmentRestart < 0)
/* 2694 */           fSegmentRestart = fLength;
/*      */         else {
/* 2696 */           fSegmentRestart++;
/*      */         }
/* 2698 */         fSegmentEnd = 
/* 2699 */           indexOf(pathSeparator, filepath, fSegmentStart = fSegmentRestart);
/* 2700 */         if (fSegmentEnd >= 0) continue; fSegmentEnd = fLength;
/*      */       }
/* 2705 */       else if ((pSegmentEnd == pSegmentStart + 2) && 
/* 2706 */         (pattern[pSegmentStart] == '*') && 
/* 2707 */         (pattern[(pSegmentStart + 1)] == '*')) {
/* 2708 */         pSegmentEnd = 
/* 2709 */           indexOf(pathSeparator, pattern, pSegmentStart = pSegmentEnd + 1);
/*      */ 
/* 2711 */         if (pSegmentEnd < 0) pSegmentEnd = pLength;
/* 2712 */         pSegmentRestart = pSegmentStart;
/* 2713 */         fSegmentRestart = fSegmentStart;
/* 2714 */         if (pSegmentStart >= pLength) return true;
/*      */ 
/*      */       }
/* 2718 */       else if (!match(
/* 2719 */         pattern, 
/* 2720 */         pSegmentStart, 
/* 2721 */         pSegmentEnd, 
/* 2722 */         filepath, 
/* 2723 */         fSegmentStart, 
/* 2724 */         fSegmentEnd, 
/* 2725 */         isCaseSensitive))
/*      */       {
/* 2727 */         pSegmentEnd = 
/* 2728 */           indexOf(pathSeparator, pattern, pSegmentStart = pSegmentRestart);
/* 2729 */         if (pSegmentEnd < 0) pSegmentEnd = pLength;
/*      */ 
/* 2731 */         fSegmentRestart = 
/* 2732 */           indexOf(pathSeparator, filepath, fSegmentRestart + 1);
/*      */ 
/* 2734 */         if (fSegmentRestart < 0)
/* 2735 */           fSegmentRestart = fLength;
/*      */         else {
/* 2737 */           fSegmentRestart++;
/*      */         }
/* 2739 */         fSegmentEnd = 
/* 2740 */           indexOf(pathSeparator, filepath, fSegmentStart = fSegmentRestart);
/* 2741 */         if (fSegmentEnd >= 0) continue; fSegmentEnd = fLength;
/*      */       }
/*      */       else
/*      */       {
/* 2745 */         pSegmentEnd = 
/* 2746 */           indexOf(
/* 2747 */           pathSeparator, 
/* 2748 */           pattern, 
/* 2749 */           pSegmentStart = pSegmentEnd + 1);
/*      */ 
/* 2751 */         if (pSegmentEnd < 0) {
/* 2752 */           pSegmentEnd = pLength;
/*      */         }
/* 2754 */         fSegmentEnd = 
/* 2755 */           indexOf(
/* 2756 */           pathSeparator, 
/* 2757 */           filepath, 
/* 2758 */           fSegmentStart = fSegmentEnd + 1);
/*      */ 
/* 2760 */         if (fSegmentEnd < 0) {
/* 2761 */           fSegmentEnd = fLength;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2769 */     return (pSegmentRestart >= pSegmentEnd) || 
/* 2765 */       ((fSegmentStart >= fLength) && (pSegmentStart >= pLength)) || 
/* 2766 */       ((pSegmentStart == pLength - 2) && 
/* 2767 */       (pattern[pSegmentStart] == '*') && 
/* 2768 */       (pattern[(pSegmentStart + 1)] == '*')) || (
/* 2769 */       (pSegmentStart == pLength) && (freeTrailingDoubleStar));
/*      */   }
/*      */ 
/*      */   public static final int occurencesOf(char toBeFound, char[] array)
/*      */   {
/* 2799 */     int count = 0;
/* 2800 */     for (int i = 0; i < array.length; i++)
/* 2801 */       if (toBeFound == array[i])
/* 2802 */         count++;
/* 2803 */     return count;
/*      */   }
/*      */ 
/*      */   public static final int occurencesOf(char toBeFound, char[] array, int start)
/*      */   {
/* 2841 */     int count = 0;
/* 2842 */     for (int i = start; i < array.length; i++)
/* 2843 */       if (toBeFound == array[i])
/* 2844 */         count++;
/* 2845 */     return count;
/*      */   }
/*      */ 
/*      */   public static final int parseInt(char[] array, int start, int length)
/*      */     throws NumberFormatException
/*      */   {
/* 2860 */     if (length == 1) {
/* 2861 */       int result = array[start] - '0';
/* 2862 */       if ((result < 0) || (result > 9)) {
/* 2863 */         throw new NumberFormatException("invalid digit");
/*      */       }
/* 2865 */       return result;
/*      */     }
/* 2867 */     return Integer.parseInt(new String(array, start, length));
/*      */   }
/*      */ 
/*      */   public static final boolean prefixEquals(char[] prefix, char[] name)
/*      */   {
/* 2898 */     int max = prefix.length;
/* 2899 */     if (name.length < max)
/* 2900 */       return false;
/* 2901 */     int i = max;
/*      */     do
/*      */     {
/* 2904 */       if (prefix[i] != name[i])
/* 2905 */         return false;
/* 2902 */       i--; } while (i >= 0);
/*      */ 
/* 2906 */     return true;
/*      */   }
/*      */ 
/*      */   public static final boolean prefixEquals(char[] prefix, char[] name, boolean isCaseSensitive)
/*      */   {
/* 2943 */     int max = prefix.length;
/* 2944 */     if (name.length < max)
/* 2945 */       return false;
/* 2946 */     if (isCaseSensitive) {
/* 2947 */       int i = max;
/*      */       do
/*      */       {
/* 2950 */         if (prefix[i] != name[i])
/* 2951 */           return false;
/* 2948 */         i--; } while (i >= 0);
/*      */ 
/* 2952 */       return true;
/*      */     }
/*      */ 
/* 2955 */     int i = max;
/*      */     do
/*      */     {
/* 2958 */       if (ScannerHelper.toLowerCase(prefix[i]) != 
/* 2959 */         ScannerHelper.toLowerCase(name[i]))
/* 2960 */         return false;
/* 2956 */       i--; } while (i >= 0);
/*      */ 
/* 2961 */     return true;
/*      */   }
/*      */ 
/*      */   public static final char[] remove(char[] array, char toBeRemoved)
/*      */   {
/* 2992 */     if (array == null) return null;
/* 2993 */     int length = array.length;
/* 2994 */     if (length == 0) return array;
/* 2995 */     char[] result = (char[])null;
/* 2996 */     int count = 0;
/* 2997 */     for (int i = 0; i < length; i++) {
/* 2998 */       char c = array[i];
/* 2999 */       if (c == toBeRemoved) {
/* 3000 */         if (result == null) {
/* 3001 */           result = new char[length];
/* 3002 */           System.arraycopy(array, 0, result, 0, i);
/* 3003 */           count = i;
/*      */         }
/* 3005 */       } else if (result != null) {
/* 3006 */         result[(count++)] = c;
/*      */       }
/*      */     }
/* 3009 */     if (result == null) return array;
/* 3010 */     System.arraycopy(result, 0, result = new char[count], 0, count);
/* 3011 */     return result;
/*      */   }
/*      */ 
/*      */   public static final void replace(char[] array, char toBeReplaced, char replacementChar)
/*      */   {
/* 3046 */     if (toBeReplaced != replacementChar) {
/* 3047 */       int i = 0; for (int max = array.length; i < max; i++)
/* 3048 */         if (array[i] == toBeReplaced)
/* 3049 */           array[i] = replacementChar;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static final void replace(char[] array, char[] toBeReplaced, char replacementChar)
/*      */   {
/* 3077 */     replace(array, toBeReplaced, replacementChar, 0, array.length);
/*      */   }
/*      */ 
/*      */   public static final void replace(char[] array, char[] toBeReplaced, char replacementChar, int start, int end)
/*      */   {
/* 3107 */     int i = end;
/*      */     do { int j = toBeReplaced.length;
/*      */       do { if (array[i] == toBeReplaced[j])
/* 3110 */           array[i] = replacementChar;
/* 3108 */         j--; } while (j >= 0);
/*      */ 
/* 3107 */       i--; } while (i >= start);
/*      */   }
/*      */ 
/*      */   public static final char[] replace(char[] array, char[] toBeReplaced, char[] replacementChars)
/*      */   {
/* 3147 */     int max = array.length;
/* 3148 */     int replacedLength = toBeReplaced.length;
/* 3149 */     int replacementLength = replacementChars.length;
/*      */ 
/* 3151 */     int[] starts = new int[5];
/* 3152 */     int occurrenceCount = 0;
/*      */ 
/* 3154 */     if (!equals(toBeReplaced, replacementChars))
/*      */     {
/* 3156 */       for (int i = 0; i < max; ) {
/* 3157 */         int index = indexOf(toBeReplaced, array, true, i);
/* 3158 */         if (index == -1) {
/* 3159 */           i++;
/*      */         }
/*      */         else {
/* 3162 */           if (occurrenceCount == starts.length) {
/* 3163 */             System.arraycopy(
/* 3164 */               starts, 
/* 3165 */               0, 
/* 3166 */               starts = new int[occurrenceCount * 2], 
/* 3167 */               0, 
/* 3168 */               occurrenceCount);
/*      */           }
/* 3170 */           starts[(occurrenceCount++)] = index;
/* 3171 */           i = index + replacedLength;
/*      */         }
/*      */       }
/*      */     }
/* 3174 */     if (occurrenceCount == 0)
/* 3175 */       return array;
/* 3176 */     char[] result = 
/* 3177 */       new char[max + 
/* 3178 */       occurrenceCount * (replacementLength - replacedLength)];
/* 3179 */     int inStart = 0; int outStart = 0;
/* 3180 */     for (int i = 0; i < occurrenceCount; i++) {
/* 3181 */       int offset = starts[i] - inStart;
/* 3182 */       System.arraycopy(array, inStart, result, outStart, offset);
/* 3183 */       inStart += offset;
/* 3184 */       outStart += offset;
/* 3185 */       System.arraycopy(
/* 3186 */         replacementChars, 
/* 3187 */         0, 
/* 3188 */         result, 
/* 3189 */         outStart, 
/* 3190 */         replacementLength);
/* 3191 */       inStart += replacedLength;
/* 3192 */       outStart += replacementLength;
/*      */     }
/* 3194 */     System.arraycopy(array, inStart, result, outStart, max - inStart);
/* 3195 */     return result;
/*      */   }
/*      */ 
/*      */   public static final char[] replaceOnCopy(char[] array, char toBeReplaced, char replacementChar)
/*      */   {
/* 3233 */     char[] result = (char[])null;
/* 3234 */     int i = 0; for (int length = array.length; i < length; i++) {
/* 3235 */       char c = array[i];
/* 3236 */       if (c == toBeReplaced) {
/* 3237 */         if (result == null) {
/* 3238 */           result = new char[length];
/* 3239 */           System.arraycopy(array, 0, result, 0, i);
/*      */         }
/* 3241 */         result[i] = replacementChar;
/* 3242 */       } else if (result != null) {
/* 3243 */         result[i] = c;
/*      */       }
/*      */     }
/* 3246 */     if (result == null) return array;
/* 3247 */     return result;
/*      */   }
/*      */ 
/*      */   public static final char[][] splitAndTrimOn(char divider, char[] array)
/*      */   {
/* 3289 */     int length = array == null ? 0 : array.length;
/* 3290 */     if (length == 0) {
/* 3291 */       return NO_CHAR_CHAR;
/*      */     }
/* 3293 */     int wordCount = 1;
/* 3294 */     for (int i = 0; i < length; i++)
/* 3295 */       if (array[i] == divider)
/* 3296 */         wordCount++;
/* 3297 */     char[][] split = new char[wordCount][];
/* 3298 */     int last = 0; int currentWord = 0;
/* 3299 */     for (int i = 0; i < length; i++) {
/* 3300 */       if (array[i] == divider) {
/* 3301 */         int start = last; int end = i - 1;
/*      */         do {
/* 3303 */           start++;
/*      */ 
/* 3302 */           if (start >= i) break; 
/* 3302 */         }while (array[start] == ' ');
/*      */ 
/* 3304 */         while ((end > start) && (array[end] == ' '))
/* 3305 */           end--;
/* 3306 */         split[currentWord] = new char[end - start + 1];
/* 3307 */         System.arraycopy(
/* 3308 */           array, 
/* 3309 */           start, 
/* 3310 */           split[(currentWord++)], 
/* 3311 */           0, 
/* 3312 */           end - start + 1);
/* 3313 */         last = i + 1;
/*      */       }
/*      */     }
/* 3316 */     int start = last; int end = length - 1;
/*      */     do {
/* 3318 */       start++;
/*      */ 
/* 3317 */       if (start >= length) break; 
/* 3317 */     }while (array[start] == ' ');
/*      */ 
/* 3319 */     while ((end > start) && (array[end] == ' '))
/* 3320 */       end--;
/* 3321 */     split[currentWord] = new char[end - start + 1];
/* 3322 */     System.arraycopy(
/* 3323 */       array, 
/* 3324 */       start, 
/* 3325 */       split[(currentWord++)], 
/* 3326 */       0, 
/* 3327 */       end - start + 1);
/* 3328 */     return split;
/*      */   }
/*      */ 
/*      */   public static final char[][] splitOn(char divider, char[] array)
/*      */   {
/* 3362 */     int length = array == null ? 0 : array.length;
/* 3363 */     if (length == 0) {
/* 3364 */       return NO_CHAR_CHAR;
/*      */     }
/* 3366 */     int wordCount = 1;
/* 3367 */     for (int i = 0; i < length; i++)
/* 3368 */       if (array[i] == divider)
/* 3369 */         wordCount++;
/* 3370 */     char[][] split = new char[wordCount][];
/* 3371 */     int last = 0; int currentWord = 0;
/* 3372 */     for (int i = 0; i < length; i++) {
/* 3373 */       if (array[i] == divider) {
/* 3374 */         split[currentWord] = new char[i - last];
/* 3375 */         System.arraycopy(
/* 3376 */           array, 
/* 3377 */           last, 
/* 3378 */           split[(currentWord++)], 
/* 3379 */           0, 
/* 3380 */           i - last);
/* 3381 */         last = i + 1;
/*      */       }
/*      */     }
/* 3384 */     split[currentWord] = new char[length - last];
/* 3385 */     System.arraycopy(array, last, split[currentWord], 0, length - last);
/* 3386 */     return split;
/*      */   }
/*      */ 
/*      */   public static final char[][] splitOn(char divider, char[] array, int start, int end)
/*      */   {
/* 3418 */     int length = array == null ? 0 : array.length;
/* 3419 */     if ((length == 0) || (start > end)) {
/* 3420 */       return NO_CHAR_CHAR;
/*      */     }
/* 3422 */     int wordCount = 1;
/* 3423 */     for (int i = start; i < end; i++)
/* 3424 */       if (array[i] == divider)
/* 3425 */         wordCount++;
/* 3426 */     char[][] split = new char[wordCount][];
/* 3427 */     int last = start; int currentWord = 0;
/* 3428 */     for (int i = start; i < end; i++) {
/* 3429 */       if (array[i] == divider) {
/* 3430 */         split[currentWord] = new char[i - last];
/* 3431 */         System.arraycopy(
/* 3432 */           array, 
/* 3433 */           last, 
/* 3434 */           split[(currentWord++)], 
/* 3435 */           0, 
/* 3436 */           i - last);
/* 3437 */         last = i + 1;
/*      */       }
/*      */     }
/* 3440 */     split[currentWord] = new char[end - last];
/* 3441 */     System.arraycopy(array, last, split[currentWord], 0, end - last);
/* 3442 */     return split;
/*      */   }
/*      */ 
/*      */   public static final char[][] subarray(char[][] array, int start, int end)
/*      */   {
/* 3478 */     if (end == -1)
/* 3479 */       end = array.length;
/* 3480 */     if (start > end)
/* 3481 */       return null;
/* 3482 */     if (start < 0)
/* 3483 */       return null;
/* 3484 */     if (end > array.length) {
/* 3485 */       return null;
/*      */     }
/* 3487 */     char[][] result = new char[end - start][];
/* 3488 */     System.arraycopy(array, start, result, 0, end - start);
/* 3489 */     return result;
/*      */   }
/*      */ 
/*      */   public static final char[] subarray(char[] array, int start, int end)
/*      */   {
/* 3525 */     if (end == -1)
/* 3526 */       end = array.length;
/* 3527 */     if (start > end)
/* 3528 */       return null;
/* 3529 */     if (start < 0)
/* 3530 */       return null;
/* 3531 */     if (end > array.length) {
/* 3532 */       return null;
/*      */     }
/* 3534 */     char[] result = new char[end - start];
/* 3535 */     System.arraycopy(array, start, result, 0, end - start);
/* 3536 */     return result;
/*      */   }
/*      */ 
/*      */   public static final char[] toLowerCase(char[] chars)
/*      */   {
/* 3563 */     if (chars == null)
/* 3564 */       return null;
/* 3565 */     int length = chars.length;
/* 3566 */     char[] lowerChars = (char[])null;
/* 3567 */     for (int i = 0; i < length; i++) {
/* 3568 */       char c = chars[i];
/* 3569 */       char lc = ScannerHelper.toLowerCase(c);
/* 3570 */       if ((c != lc) || (lowerChars != null)) {
/* 3571 */         if (lowerChars == null) {
/* 3572 */           System.arraycopy(
/* 3573 */             chars, 
/* 3574 */             0, 
/* 3575 */             lowerChars = new char[length], 
/* 3576 */             0, 
/* 3577 */             i);
/*      */         }
/* 3579 */         lowerChars[i] = lc;
/*      */       }
/*      */     }
/* 3582 */     return lowerChars == null ? chars : lowerChars;
/*      */   }
/*      */ 
/*      */   public static final char[] toUpperCase(char[] chars)
/*      */   {
/* 3611 */     if (chars == null)
/* 3612 */       return null;
/* 3613 */     int length = chars.length;
/* 3614 */     char[] upperChars = (char[])null;
/* 3615 */     for (int i = 0; i < length; i++) {
/* 3616 */       char c = chars[i];
/* 3617 */       char lc = ScannerHelper.toUpperCase(c);
/* 3618 */       if ((c != lc) || (upperChars != null)) {
/* 3619 */         if (upperChars == null) {
/* 3620 */           System.arraycopy(
/* 3621 */             chars, 
/* 3622 */             0, 
/* 3623 */             upperChars = new char[length], 
/* 3624 */             0, 
/* 3625 */             i);
/*      */         }
/* 3627 */         upperChars[i] = lc;
/*      */       }
/*      */     }
/* 3630 */     return upperChars == null ? chars : upperChars;
/*      */   }
/*      */ 
/*      */   public static final char[] trim(char[] chars)
/*      */   {
/* 3657 */     if (chars == null) {
/* 3658 */       return null;
/*      */     }
/* 3660 */     int start = 0; int length = chars.length; int end = length - 1;
/*      */     do {
/* 3662 */       start++;
/*      */ 
/* 3661 */       if (start >= length) break; 
/* 3661 */     }while (chars[start] == ' ');
/*      */ 
/* 3664 */     while ((end > start) && (chars[end] == ' ')) {
/* 3665 */       end--;
/*      */     }
/* 3667 */     if ((start != 0) || (end != length - 1)) {
/* 3668 */       return subarray(chars, start, end + 1);
/*      */     }
/* 3670 */     return chars;
/*      */   }
/*      */ 
/*      */   public static final String toString(char[][] array)
/*      */   {
/* 3695 */     char[] result = concatWith(array, '.');
/* 3696 */     return new String(result);
/*      */   }
/*      */ 
/*      */   public static final String[] toStrings(char[][] array)
/*      */   {
/* 3707 */     if (array == null) return NO_STRINGS;
/* 3708 */     int length = array.length;
/* 3709 */     if (length == 0) return NO_STRINGS;
/* 3710 */     String[] result = new String[length];
/* 3711 */     for (int i = 0; i < length; i++)
/* 3712 */       result[i] = new String(array[i]);
/* 3713 */     return result;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.core.compiler.CharOperation
 * JD-Core Version:    0.6.0
 */