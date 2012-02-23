/*     */ package org.json;
/*     */ 
/*     */ import java.text.ParseException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.NoSuchElementException;
/*     */ 
/*     */ public class JSONArray
/*     */ {
/*     */   private ArrayList myArrayList;
/*     */ 
/*     */   public JSONArray()
/*     */   {
/*  89 */     this.myArrayList = new ArrayList();
/*     */   }
/*     */ 
/*     */   public JSONArray(JSONTokener x)
/*     */     throws ParseException
/*     */   {
/* 100 */     this();
/* 101 */     if (x.nextClean() != '[') {
/* 102 */       throw x.syntaxError("A JSONArray must start with '['");
/*     */     }
/* 104 */     if (x.nextClean() == ']') {
/* 105 */       return;
/*     */     }
/* 107 */     x.back();
/*     */     while (true) {
/* 109 */       if (x.nextClean() == ',') {
/* 110 */         x.back();
/* 111 */         this.myArrayList.add(null);
/*     */       } else {
/* 113 */         x.back();
/* 114 */         this.myArrayList.add(x.nextValue());
/*     */       }
/* 116 */       switch (x.nextClean()) {
/*     */       case ',':
/*     */       case ';':
/* 119 */         if (x.nextClean() == ']') {
/* 120 */           return;
/*     */         }
/* 122 */         x.back();
/*     */       case ']':
/*     */       }
/*     */     }
/* 125 */     return;
/*     */ 
/* 127 */     throw x.syntaxError("Expected a ',' or ']'");
/*     */   }
/*     */ 
/*     */   public JSONArray(String string)
/*     */     throws ParseException
/*     */   {
/* 141 */     this(new JSONTokener(string));
/*     */   }
/*     */ 
/*     */   public JSONArray(Collection collection)
/*     */   {
/* 150 */     this.myArrayList = new ArrayList(collection);
/*     */   }
/*     */ 
/*     */   public Object get(int index)
/*     */     throws NoSuchElementException
/*     */   {
/* 162 */     Object o = opt(index);
/* 163 */     if (o == null) {
/* 164 */       throw new NoSuchElementException("JSONArray[" + index + "] not found.");
/*     */     }
/*     */ 
/* 167 */     return o;
/*     */   }
/*     */ 
/*     */   ArrayList getArrayList()
/*     */   {
/* 176 */     return this.myArrayList;
/*     */   }
/*     */ 
/*     */   public boolean getBoolean(int index)
/*     */     throws ClassCastException, NoSuchElementException
/*     */   {
/* 191 */     Object o = get(index);
/* 192 */     if ((o.equals(Boolean.FALSE)) || (((o instanceof String)) && (((String)o).equalsIgnoreCase("false"))))
/*     */     {
/* 195 */       return false;
/* 196 */     }if ((o.equals(Boolean.TRUE)) || (((o instanceof String)) && (((String)o).equalsIgnoreCase("true"))))
/*     */     {
/* 199 */       return true;
/*     */     }
/* 201 */     throw new ClassCastException("JSONArray[" + index + "] not a Boolean.");
/*     */   }
/*     */ 
/*     */   public double getDouble(int index)
/*     */     throws NoSuchElementException, NumberFormatException
/*     */   {
/* 217 */     Object o = get(index);
/* 218 */     if ((o instanceof Number)) {
/* 219 */       return ((Number)o).doubleValue();
/*     */     }
/* 221 */     if ((o instanceof String)) {
/* 222 */       return new Double((String)o).doubleValue();
/*     */     }
/* 224 */     throw new NumberFormatException("JSONObject[" + index + "] is not a number.");
/*     */   }
/*     */ 
/*     */   public int getInt(int index)
/*     */     throws NoSuchElementException, NumberFormatException
/*     */   {
/* 240 */     Object o = get(index);
/* 241 */     return (o instanceof Number) ? ((Number)o).intValue() : (int)getDouble(index);
/*     */   }
/*     */ 
/*     */   public JSONArray getJSONArray(int index)
/*     */     throws NoSuchElementException
/*     */   {
/* 254 */     Object o = get(index);
/* 255 */     if ((o instanceof JSONArray)) {
/* 256 */       return (JSONArray)o;
/*     */     }
/* 258 */     throw new NoSuchElementException("JSONArray[" + index + "] is not a JSONArray.");
/*     */   }
/*     */ 
/*     */   public JSONObject getJSONObject(int index)
/*     */     throws NoSuchElementException
/*     */   {
/* 271 */     Object o = get(index);
/* 272 */     if ((o instanceof JSONObject)) {
/* 273 */       return (JSONObject)o;
/*     */     }
/* 275 */     throw new NoSuchElementException("JSONArray[" + index + "] is not a JSONObject.");
/*     */   }
/*     */ 
/*     */   public String getString(int index)
/*     */     throws NoSuchElementException
/*     */   {
/* 287 */     return get(index).toString();
/*     */   }
/*     */ 
/*     */   public boolean isNull(int index)
/*     */   {
/* 297 */     Object o = opt(index);
/* 298 */     return (o == null) || (o.equals(null));
/*     */   }
/*     */ 
/*     */   public String join(String separator)
/*     */   {
/* 310 */     int len = length();
/* 311 */     StringBuffer sb = new StringBuffer();
/* 312 */     for (int i = 0; i < len; i++) {
/* 313 */       if (i > 0) {
/* 314 */         sb.append(separator);
/*     */       }
/* 316 */       sb.append(JSONObject.valueToString(this.myArrayList.get(i)));
/*     */     }
/* 318 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public int length()
/*     */   {
/* 328 */     return this.myArrayList.size();
/*     */   }
/*     */ 
/*     */   public Object opt(int index)
/*     */   {
/* 339 */     return (index < 0) || (index >= length()) ? null : this.myArrayList.get(index);
/*     */   }
/*     */ 
/*     */   public boolean optBoolean(int index)
/*     */   {
/* 354 */     return optBoolean(index, false);
/*     */   }
/*     */ 
/*     */   public boolean optBoolean(int index, boolean defaultValue)
/*     */   {
/* 368 */     Object o = opt(index);
/* 369 */     if (o != null) {
/* 370 */       if ((o.equals(Boolean.FALSE)) || (((o instanceof String)) && (((String)o).equalsIgnoreCase("false"))))
/*     */       {
/* 373 */         return false;
/* 374 */       }if ((o.equals(Boolean.TRUE)) || (((o instanceof String)) && (((String)o).equalsIgnoreCase("true"))))
/*     */       {
/* 377 */         return true;
/*     */       }
/*     */     }
/* 380 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public double optDouble(int index)
/*     */   {
/* 393 */     return optDouble(index, (0.0D / 0.0D));
/*     */   }
/*     */ 
/*     */   public double optDouble(int index, double defaultValue)
/*     */   {
/* 407 */     Object o = opt(index);
/* 408 */     if (o != null) {
/* 409 */       if ((o instanceof Number))
/* 410 */         return ((Number)o).doubleValue();
/*     */       try
/*     */       {
/* 413 */         return new Double((String)o).doubleValue();
/*     */       }
/*     */       catch (Exception e) {
/* 416 */         return defaultValue;
/*     */       }
/*     */     }
/* 419 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public int optInt(int index)
/*     */   {
/* 432 */     return optInt(index, 0);
/*     */   }
/*     */ 
/*     */   public int optInt(int index, int defaultValue)
/*     */   {
/* 445 */     Object o = opt(index);
/* 446 */     if (o != null) {
/* 447 */       if ((o instanceof Number))
/* 448 */         return ((Number)o).intValue();
/*     */       try
/*     */       {
/* 451 */         return Integer.parseInt((String)o);
/*     */       }
/*     */       catch (Exception e) {
/* 454 */         return defaultValue;
/*     */       }
/*     */     }
/* 457 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public JSONArray optJSONArray(int index)
/*     */   {
/* 468 */     Object o = opt(index);
/* 469 */     return (o instanceof JSONArray) ? (JSONArray)o : null;
/*     */   }
/*     */ 
/*     */   public JSONObject optJSONObject(int index)
/*     */   {
/* 482 */     Object o = opt(index);
/* 483 */     return (o instanceof JSONObject) ? (JSONObject)o : null;
/*     */   }
/*     */ 
/*     */   public String optString(int index)
/*     */   {
/* 496 */     return optString(index, "");
/*     */   }
/*     */ 
/*     */   public String optString(int index, String defaultValue)
/*     */   {
/* 509 */     Object o = opt(index);
/* 510 */     return o != null ? o.toString() : defaultValue;
/*     */   }
/*     */ 
/*     */   public JSONArray put(boolean value)
/*     */   {
/* 521 */     put(Boolean.valueOf(value));
/* 522 */     return this;
/*     */   }
/*     */ 
/*     */   public JSONArray put(double value)
/*     */   {
/* 533 */     put(new Double(value));
/* 534 */     return this;
/*     */   }
/*     */ 
/*     */   public JSONArray put(int value)
/*     */   {
/* 545 */     put(new Integer(value));
/* 546 */     return this;
/*     */   }
/*     */ 
/*     */   public JSONArray put(Object value)
/*     */   {
/* 558 */     this.myArrayList.add(value);
/* 559 */     return this;
/*     */   }
/*     */ 
/*     */   public JSONArray put(int index, boolean value)
/*     */   {
/* 573 */     put(index, Boolean.valueOf(value));
/* 574 */     return this;
/*     */   }
/*     */ 
/*     */   public JSONArray put(int index, double value)
/*     */   {
/* 588 */     put(index, new Double(value));
/* 589 */     return this;
/*     */   }
/*     */ 
/*     */   public JSONArray put(int index, int value)
/*     */   {
/* 603 */     put(index, new Integer(value));
/* 604 */     return this;
/*     */   }
/*     */ 
/*     */   public JSONArray put(int index, Object value)
/*     */     throws NoSuchElementException, NullPointerException
/*     */   {
/* 620 */     if (index < 0) {
/* 621 */       throw new NoSuchElementException("JSONArray[" + index + "] not found.");
/*     */     }
/* 623 */     if (value == null)
/* 624 */       throw new NullPointerException();
/* 625 */     if (index < length()) {
/* 626 */       this.myArrayList.set(index, value);
/*     */     } else {
/* 628 */       while (index != length()) {
/* 629 */         put(null);
/*     */       }
/* 631 */       put(value);
/*     */     }
/* 633 */     return this;
/*     */   }
/*     */ 
/*     */   public JSONObject toJSONObject(JSONArray names)
/*     */   {
/* 646 */     if ((names == null) || (names.length() == 0) || (length() == 0)) {
/* 647 */       return null;
/*     */     }
/* 649 */     JSONObject jo = new JSONObject();
/* 650 */     for (int i = 0; i < names.length(); i++) {
/* 651 */       jo.put(names.getString(i), opt(i));
/*     */     }
/* 653 */     return jo;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 666 */     return '[' + join(",") + ']';
/*     */   }
/*     */ 
/*     */   public String toString(int indentFactor)
/*     */   {
/* 681 */     return toString(indentFactor, 0);
/*     */   }
/*     */ 
/*     */   String toString(int indentFactor, int indent)
/*     */   {
/* 695 */     int len = length();
/* 696 */     if (len == 0) {
/* 697 */       return "[]";
/*     */     }
/*     */ 
/* 700 */     StringBuffer sb = new StringBuffer("[");
/* 701 */     if (len == 1) {
/* 702 */       sb.append(JSONObject.valueToString(this.myArrayList.get(0), indentFactor, indent));
/*     */     }
/*     */     else {
/* 705 */       int newindent = indent + indentFactor;
/* 706 */       sb.append('\n');
/* 707 */       for (int i = 0; i < len; i++) {
/* 708 */         if (i > 0) {
/* 709 */           sb.append(",\n");
/*     */         }
/* 711 */         for (int j = 0; j < newindent; j++) {
/* 712 */           sb.append(' ');
/*     */         }
/* 714 */         sb.append(JSONObject.valueToString(this.myArrayList.get(i), indentFactor, newindent));
/*     */       }
/*     */ 
/* 717 */       sb.append('\n');
/* 718 */       for (i = 0; i < indent; i++) {
/* 719 */         sb.append(' ');
/*     */       }
/*     */     }
/* 722 */     sb.append(']');
/* 723 */     return sb.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     org.json.JSONArray
 * JD-Core Version:    0.6.0
 */