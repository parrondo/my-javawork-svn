/*      */ package org.json;
/*      */ 
/*      */ import java.text.ParseException;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ 
/*      */ public class JSONObject
/*      */ {
/*      */   private Map<String, Object> myHashMap;
/*  130 */   public static final Object NULL = new Null(null);
/*      */ 
/*      */   public JSONObject()
/*      */   {
/*  137 */     this.myHashMap = new ConcurrentHashMap();
/*      */   }
/*      */ 
/*      */   public Map getMap()
/*      */   {
/*  142 */     return this.myHashMap;
/*      */   }
/*      */ 
/*      */   public JSONObject(JSONObject jo, String[] sa)
/*      */   {
/*  153 */     this();
/*  154 */     for (int i = 0; i < sa.length; i++)
/*  155 */       putOpt(sa[i], jo.opt(sa[i]));
/*      */   }
/*      */ 
/*      */   public JSONObject(JSONTokener x)
/*      */     throws ParseException
/*      */   {
/*  166 */     this();
/*      */ 
/*  170 */     if (x.nextClean() != '{')
/*  171 */       throw x.syntaxError("A JSONObject must begin with '{'");
/*      */     while (true)
/*      */     {
/*  174 */       char c = x.nextClean();
/*  175 */       switch (c) {
/*      */       case '\000':
/*  177 */         throw x.syntaxError("A JSONObject must end with '}'");
/*      */       case '}':
/*  179 */         return;
/*      */       }
/*  181 */       x.back();
/*  182 */       String key = x.nextValue().toString();
/*      */ 
/*  189 */       c = x.nextClean();
/*  190 */       if (c == '=') {
/*  191 */         if (x.next() != '>')
/*  192 */           x.back();
/*      */       }
/*  194 */       else if (c != ':') {
/*  195 */         throw x.syntaxError("Expected a ':' after a key");
/*      */       }
/*  197 */       this.myHashMap.put(key, x.nextValue());
/*      */ 
/*  203 */       switch (x.nextClean()) {
/*      */       case ',':
/*      */       case ';':
/*  206 */         if (x.nextClean() == '}') {
/*  207 */           return;
/*      */         }
/*  209 */         x.back();
/*      */       case '}':
/*      */       }
/*      */     }
/*  212 */     return;
/*      */ 
/*  214 */     throw x.syntaxError("Expected a ',' or '}'");
/*      */   }
/*      */ 
/*      */   public JSONObject(Map map)
/*      */   {
/*  226 */     this.myHashMap = new ConcurrentHashMap(map);
/*      */   }
/*      */ 
/*      */   public JSONObject(String string)
/*      */     throws ParseException
/*      */   {
/*  239 */     this(new JSONTokener(string));
/*      */   }
/*      */ 
/*      */   public JSONObject accumulate(String key, Object value)
/*      */     throws NullPointerException
/*      */   {
/*  257 */     Object o = opt(key);
/*  258 */     if (o == null) {
/*  259 */       put(key, value);
/*  260 */     } else if ((o instanceof JSONArray)) {
/*  261 */       JSONArray a = (JSONArray)o;
/*  262 */       a.put(value);
/*      */     } else {
/*  264 */       JSONArray a = new JSONArray();
/*  265 */       a.put(o);
/*  266 */       a.put(value);
/*  267 */       put(key, a);
/*      */     }
/*  269 */     return this;
/*      */   }
/*      */ 
/*      */   public Object get(String key)
/*      */   {
/*  280 */     Object o = opt(key);
/*  281 */     return o;
/*      */   }
/*      */ 
/*      */   public boolean getBoolean(String key)
/*      */     throws ClassCastException, NoSuchElementException
/*      */   {
/*  296 */     Object o = get(key);
/*  297 */     if (o == null) {
/*  298 */       throw new NoSuchElementException("Element " + key + " not found");
/*      */     }
/*  300 */     if ((o.equals(Boolean.FALSE)) || (((o instanceof String)) && (((String)o).equalsIgnoreCase("false"))))
/*      */     {
/*  303 */       return false;
/*  304 */     }if ((o.equals(Boolean.TRUE)) || (((o instanceof String)) && (((String)o).equalsIgnoreCase("true"))))
/*      */     {
/*  307 */       return true;
/*      */     }
/*  309 */     throw new ClassCastException("JSONObject[" + quote(key) + "] is not a Boolean.");
/*      */   }
/*      */ 
/*      */   public double getDouble(String key)
/*      */     throws NoSuchElementException, NumberFormatException
/*      */   {
/*  325 */     Object o = get(key);
/*  326 */     if (o == null) {
/*  327 */       throw new NoSuchElementException("Element " + key + " not found");
/*      */     }
/*  329 */     if ((o instanceof Number)) {
/*  330 */       return ((Number)o).doubleValue();
/*      */     }
/*  332 */     if ((o instanceof String)) {
/*  333 */       return new Double((String)o).doubleValue();
/*      */     }
/*  335 */     throw new NumberFormatException("JSONObject[" + quote(key) + "] is not a number.");
/*      */   }
/*      */ 
/*      */   Map getHashMap()
/*      */   {
/*  345 */     return this.myHashMap;
/*      */   }
/*      */ 
/*      */   public int getInt(String key)
/*      */     throws NoSuchElementException, NumberFormatException
/*      */   {
/*  360 */     Object o = get(key);
/*  361 */     if (o == null) {
/*  362 */       throw new NoSuchElementException("Element " + key + " not found");
/*      */     }
/*  364 */     return (o instanceof Number) ? ((Number)o).intValue() : (int)getDouble(key);
/*      */   }
/*      */ 
/*      */   public JSONArray getJSONArray(String key)
/*      */     throws NoSuchElementException
/*      */   {
/*  378 */     Object o = get(key);
/*  379 */     if (o == null) {
/*  380 */       throw new NoSuchElementException("Element " + key + " not found");
/*      */     }
/*  382 */     if ((o instanceof JSONArray)) {
/*  383 */       return (JSONArray)o;
/*      */     }
/*  385 */     throw new NoSuchElementException("JSONObject[" + quote(key) + "] is not a JSONArray.");
/*      */   }
/*      */ 
/*      */   public JSONObject getJSONObject(String key)
/*      */   {
/*  398 */     Object o = get(key);
/*  399 */     if ((o instanceof JSONObject)) {
/*  400 */       return (JSONObject)o;
/*      */     }
/*  402 */     return null;
/*      */   }
/*      */ 
/*      */   public String getString(String key)
/*      */   {
/*  413 */     Object value = get(key);
/*  414 */     return value != null ? value.toString() : null;
/*      */   }
/*      */ 
/*      */   public boolean has(String key)
/*      */   {
/*  424 */     return this.myHashMap.containsKey(key);
/*      */   }
/*      */ 
/*      */   public boolean isNull(String key)
/*      */   {
/*  436 */     return NULL.equals(opt(key));
/*      */   }
/*      */ 
/*      */   public Iterator keys()
/*      */   {
/*  448 */     return this.myHashMap.keySet().iterator();
/*      */   }
/*      */ 
/*      */   public int length()
/*      */   {
/*  458 */     return this.myHashMap.size();
/*      */   }
/*      */ 
/*      */   public JSONArray names()
/*      */   {
/*  469 */     JSONArray ja = new JSONArray();
/*  470 */     Iterator keys = keys();
/*  471 */     while (keys.hasNext()) {
/*  472 */       ja.put(keys.next());
/*      */     }
/*  474 */     return ja.length() == 0 ? null : ja;
/*      */   }
/*      */ 
/*      */   public static String numberToString(Number n)
/*      */     throws ArithmeticException
/*      */   {
/*  485 */     if ((((n instanceof Float)) && ((((Float)n).isInfinite()) || (((Float)n).isNaN()))) || (((n instanceof Double)) && ((((Double)n).isInfinite()) || (((Double)n).isNaN()))))
/*      */     {
/*  490 */       throw new ArithmeticException("JSON can only serialize finite numbers.");
/*      */     }
/*      */ 
/*  496 */     String s = n.toString();
/*  497 */     if ((s.indexOf(46) > 0) && (s.indexOf(101) < 0) && (s.indexOf(69) < 0)) {
/*  498 */       while (s.endsWith("0")) {
/*  499 */         s = s.substring(0, s.length() - 1);
/*      */       }
/*  501 */       if (s.endsWith(".")) {
/*  502 */         s = s.substring(0, s.length() - 1);
/*      */       }
/*      */     }
/*  505 */     return s;
/*      */   }
/*      */ 
/*      */   public Object opt(String key)
/*      */     throws NullPointerException
/*      */   {
/*  516 */     if (key == null) {
/*  517 */       throw new NullPointerException("Null key");
/*      */     }
/*  519 */     return this.myHashMap.get(key);
/*      */   }
/*      */ 
/*      */   public boolean optBoolean(String key)
/*      */   {
/*  532 */     return optBoolean(key, false);
/*      */   }
/*      */ 
/*      */   public boolean optBoolean(String key, boolean defaultValue)
/*      */   {
/*  546 */     Object o = opt(key);
/*  547 */     if (o != null) {
/*  548 */       if ((o.equals(Boolean.FALSE)) || (((o instanceof String)) && (((String)o).equalsIgnoreCase("false"))))
/*      */       {
/*  551 */         return false;
/*  552 */       }if ((o.equals(Boolean.TRUE)) || (((o instanceof String)) && (((String)o).equalsIgnoreCase("true"))))
/*      */       {
/*  555 */         return true;
/*      */       }
/*      */     }
/*  558 */     return defaultValue;
/*      */   }
/*      */ 
/*      */   public double optDouble(String key)
/*      */   {
/*  572 */     return optDouble(key, (0.0D / 0.0D));
/*      */   }
/*      */ 
/*      */   public double optDouble(String key, double defaultValue)
/*      */   {
/*  587 */     Object o = opt(key);
/*  588 */     if (o != null) {
/*  589 */       if ((o instanceof Number))
/*  590 */         return ((Number)o).doubleValue();
/*      */       try
/*      */       {
/*  593 */         return new Double((String)o).doubleValue();
/*      */       }
/*      */       catch (Exception e) {
/*  596 */         return defaultValue;
/*      */       }
/*      */     }
/*  599 */     return defaultValue;
/*      */   }
/*      */ 
/*      */   public int optInt(String key)
/*      */   {
/*  613 */     return optInt(key, 0);
/*      */   }
/*      */ 
/*      */   public int optInt(String key, int defaultValue)
/*      */   {
/*  628 */     Object o = opt(key);
/*  629 */     if (o != null) {
/*  630 */       if ((o instanceof Number))
/*  631 */         return ((Number)o).intValue();
/*      */       try
/*      */       {
/*  634 */         return Integer.parseInt((String)o);
/*      */       } catch (Exception e) {
/*  636 */         return defaultValue;
/*      */       }
/*      */     }
/*  639 */     return defaultValue;
/*      */   }
/*      */ 
/*      */   public JSONArray optJSONArray(String key)
/*      */   {
/*  652 */     Object o = opt(key);
/*  653 */     return (o instanceof JSONArray) ? (JSONArray)o : null;
/*      */   }
/*      */ 
/*      */   public JSONObject optJSONObject(String key)
/*      */   {
/*  666 */     Object o = opt(key);
/*  667 */     return (o instanceof JSONObject) ? (JSONObject)o : null;
/*      */   }
/*      */ 
/*      */   public String optString(String key)
/*      */   {
/*  680 */     return optString(key, "");
/*      */   }
/*      */ 
/*      */   public String optString(String key, String defaultValue)
/*      */   {
/*  693 */     Object o = opt(key);
/*  694 */     return o != null ? o.toString() : defaultValue;
/*      */   }
/*      */ 
/*      */   public JSONObject put(String key, boolean value)
/*      */   {
/*  706 */     put(key, Boolean.valueOf(value));
/*  707 */     return this;
/*      */   }
/*      */ 
/*      */   public JSONObject put(String key, double value)
/*      */   {
/*  719 */     put(key, new Double(value));
/*  720 */     return this;
/*      */   }
/*      */ 
/*      */   public JSONObject put(String key, int value)
/*      */   {
/*  732 */     put(key, new Integer(value));
/*  733 */     return this;
/*      */   }
/*      */ 
/*      */   public JSONObject put(String key, Object value)
/*      */     throws NullPointerException
/*      */   {
/*  748 */     if (key == null) {
/*  749 */       throw new NullPointerException("Null key.");
/*      */     }
/*  751 */     if (value != null)
/*  752 */       this.myHashMap.put(key, value);
/*      */     else {
/*  754 */       remove(key);
/*      */     }
/*  756 */     return this;
/*      */   }
/*      */ 
/*      */   public JSONObject putOpt(String key, Object value)
/*      */     throws NullPointerException
/*      */   {
/*  772 */     if (value != null) {
/*  773 */       put(key, value);
/*      */     }
/*  775 */     return this;
/*      */   }
/*      */ 
/*      */   public static String quote(String string)
/*      */   {
/*  786 */     if ((string == null) || (string.length() == 0)) {
/*  787 */       return "\"\"";
/*      */     }
/*      */ 
/*  791 */     char c = '\000';
/*      */ 
/*  793 */     int len = string.length();
/*  794 */     StringBuffer sb = new StringBuffer(len + 4);
/*      */ 
/*  797 */     sb.append('"');
/*  798 */     for (int i = 0; i < len; i++) {
/*  799 */       char b = c;
/*  800 */       c = string.charAt(i);
/*  801 */       switch (c) {
/*      */       case '"':
/*      */       case '\\':
/*  804 */         sb.append('\\');
/*  805 */         sb.append(c);
/*  806 */         break;
/*      */       case '/':
/*  808 */         if (b == '<') {
/*  809 */           sb.append('\\');
/*      */         }
/*  811 */         sb.append(c);
/*  812 */         break;
/*      */       case '\b':
/*  814 */         sb.append("\\b");
/*  815 */         break;
/*      */       case '\t':
/*  817 */         sb.append("\\t");
/*  818 */         break;
/*      */       case '\n':
/*  820 */         sb.append("\\n");
/*  821 */         break;
/*      */       case '\f':
/*  823 */         sb.append("\\f");
/*  824 */         break;
/*      */       case '\r':
/*  826 */         sb.append("\\r");
/*  827 */         break;
/*      */       default:
/*  829 */         if (c < ' ') {
/*  830 */           String t = "000" + Integer.toHexString(c);
/*  831 */           sb.append("\\u" + t.substring(t.length() - 4));
/*      */         } else {
/*  833 */           sb.append(c);
/*      */         }
/*      */       }
/*      */     }
/*  837 */     sb.append('"');
/*  838 */     return sb.toString();
/*      */   }
/*      */ 
/*      */   public Object remove(String key)
/*      */   {
/*  848 */     return this.myHashMap.remove(key);
/*      */   }
/*      */ 
/*      */   public JSONArray toJSONArray(JSONArray names)
/*      */   {
/*  859 */     if ((names == null) || (names.length() == 0)) {
/*  860 */       return null;
/*      */     }
/*  862 */     JSONArray ja = new JSONArray();
/*  863 */     for (int i = 0; i < names.length(); i++) {
/*  864 */       ja.put(opt(names.getString(i)));
/*      */     }
/*  866 */     return ja;
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*  881 */     Iterator keys = keys();
/*  882 */     StringBuffer sb = new StringBuffer("{");
/*      */ 
/*  884 */     while (keys.hasNext()) {
/*  885 */       if (sb.length() > 1) {
/*  886 */         sb.append(',');
/*      */       }
/*  888 */       Object o = keys.next();
/*  889 */       sb.append(quote(o.toString()));
/*  890 */       sb.append(':');
/*  891 */       sb.append(valueToString(this.myHashMap.get(o)));
/*      */     }
/*  893 */     sb.append('}');
/*  894 */     return sb.toString();
/*      */   }
/*      */ 
/*      */   public String toString(int indentFactor)
/*      */   {
/*  910 */     return toString(indentFactor, 0);
/*      */   }
/*      */ 
/*      */   String toString(int indentFactor, int indent)
/*      */   {
/*  928 */     int n = length();
/*  929 */     if (n == 0) {
/*  930 */       return "{}";
/*      */     }
/*  932 */     Iterator keys = keys();
/*  933 */     StringBuffer sb = new StringBuffer("{");
/*  934 */     int newindent = indent + indentFactor;
/*      */ 
/*  936 */     if (n == 1) {
/*  937 */       Object o = keys.next();
/*  938 */       sb.append(quote(o.toString()));
/*  939 */       sb.append(": ");
/*  940 */       sb.append(valueToString(this.myHashMap.get(o), indentFactor, indent));
/*      */     } else {
/*  942 */       while (keys.hasNext()) {
/*  943 */         Object o = keys.next();
/*  944 */         if (sb.length() > 1)
/*  945 */           sb.append(",\n");
/*      */         else {
/*  947 */           sb.append('\n');
/*      */         }
/*  949 */         for (int i = 0; i < newindent; i++) {
/*  950 */           sb.append(' ');
/*      */         }
/*  952 */         sb.append(quote(o.toString()));
/*  953 */         sb.append(": ");
/*  954 */         sb.append(valueToString(this.myHashMap.get(o), indentFactor, newindent));
/*      */       }
/*      */ 
/*  957 */       if (sb.length() > 1) {
/*  958 */         sb.append('\n');
/*  959 */         for (int i = 0; i < indent; i++) {
/*  960 */           sb.append(' ');
/*      */         }
/*      */       }
/*      */     }
/*  964 */     sb.append('}');
/*  965 */     return sb.toString();
/*      */   }
/*      */ 
/*      */   static String valueToString(Object value)
/*      */   {
/*  980 */     if ((value == null) || (value.equals(null))) {
/*  981 */       return "null";
/*      */     }
/*  983 */     if ((value instanceof Number)) {
/*  984 */       return numberToString((Number)value);
/*      */     }
/*  986 */     if (((value instanceof Boolean)) || ((value instanceof JSONObject)) || ((value instanceof JSONArray)))
/*      */     {
/*  988 */       return value.toString();
/*      */     }
/*  990 */     return quote(value.toString());
/*      */   }
/*      */ 
/*      */   static String valueToString(Object value, int indentFactor, int indent)
/*      */   {
/* 1008 */     if ((value == null) || (value.equals(null))) {
/* 1009 */       return "null";
/*      */     }
/* 1011 */     if ((value instanceof Number)) {
/* 1012 */       return numberToString((Number)value);
/*      */     }
/* 1014 */     if ((value instanceof Boolean)) {
/* 1015 */       return value.toString();
/*      */     }
/* 1017 */     if ((value instanceof JSONObject)) {
/* 1018 */       return ((JSONObject)value).toString(indentFactor, indent);
/*      */     }
/* 1020 */     if ((value instanceof JSONArray)) {
/* 1021 */       return ((JSONArray)value).toString(indentFactor, indent);
/*      */     }
/* 1023 */     return quote(value.toString());
/*      */   }
/*      */ 
/*      */   private static final class Null
/*      */   {
/*      */     protected final Object clone()
/*      */     {
/*   93 */       return this;
/*      */     }
/*      */ 
/*      */     public boolean equals(Object object)
/*      */     {
/*  104 */       return (object == null) || (object == this);
/*      */     }
/*      */ 
/*      */     public String toString()
/*      */     {
/*  113 */       return "null";
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     org.json.JSONObject
 * JD-Core Version:    0.6.0
 */