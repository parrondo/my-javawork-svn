/*     */ package com.dukascopy.api.connector.helpers;
/*     */ 
/*     */ import com.dukascopy.api.JFException;
/*     */ import java.awt.Color;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class ColorHelpers
/*     */ {
/*  18 */   static final Map<String, Integer> webColor = new HashMap();
/*     */ 
/*     */   public static final Color colorFromHex(String hex)
/*     */   {
/* 160 */     int intValue = Integer.parseInt(hex, 16);
/* 161 */     Color color = new Color(intValue);
/* 162 */     return color;
/*     */   }
/*     */ 
/*     */   public static final Color colorFromInt(int value) {
/* 166 */     Color color = new Color(value);
/* 167 */     return color;
/*     */   }
/*     */ 
/*     */   public static final boolean isContainsColor(String value) {
/* 171 */     boolean result = false;
/* 172 */     if ((webColor != null) && (value != null) && (!value.isEmpty()) && (webColor.get(value) != null)) {
/* 173 */       result = ((Integer)webColor.get(value)).intValue() > 0;
/*     */     }
/* 175 */     return result;
/*     */   }
/*     */ 
/*     */   public static final Color colorFromString(String value) throws JFException {
/* 179 */     Integer intColor = (Integer)webColor.get(value.toUpperCase());
/* 180 */     if (intColor != null) {
/* 181 */       return new Color(intColor.intValue());
/*     */     }
/* 183 */     throw new JFException("Color [" + value + "] is not a correct MT constant.");
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  20 */     webColor.put("BLACK", Integer.valueOf(0));
/*  21 */     webColor.put("DARKGREEN", Integer.valueOf(25600));
/*  22 */     webColor.put("DARKSLATEGRAY", Integer.valueOf(3100495));
/*  23 */     webColor.put("OLIVE", Integer.valueOf(8421376));
/*  24 */     webColor.put("GREEN", Integer.valueOf(32768));
/*  25 */     webColor.put("TEAL", Integer.valueOf(32896));
/*  26 */     webColor.put("NAVY", Integer.valueOf(128));
/*  27 */     webColor.put("PURPLE", Integer.valueOf(8388736));
/*  28 */     webColor.put("MAROON", Integer.valueOf(8388608));
/*  29 */     webColor.put("INDIGO", Integer.valueOf(4915330));
/*  30 */     webColor.put("MIDNIGHTBLUE", Integer.valueOf(1644912));
/*  31 */     webColor.put("DARKBLUE", Integer.valueOf(139));
/*  32 */     webColor.put("DARKOLIVEGREEN", Integer.valueOf(5597999));
/*  33 */     webColor.put("SADDLEBROWN", Integer.valueOf(9127187));
/*  34 */     webColor.put("FORESTGREEN", Integer.valueOf(2263842));
/*  35 */     webColor.put("OLIVEDRAB", Integer.valueOf(7048739));
/*  36 */     webColor.put("SEAGREEN", Integer.valueOf(3050327));
/*  37 */     webColor.put("DARKGOLDENROD", Integer.valueOf(12092939));
/*  38 */     webColor.put("DARKSLATEBLUE", Integer.valueOf(4734347));
/*  39 */     webColor.put("SIENNA", Integer.valueOf(10506797));
/*  40 */     webColor.put("MEDIUMBLUE", Integer.valueOf(205));
/*  41 */     webColor.put("BROWN", Integer.valueOf(10824234));
/*  42 */     webColor.put("DARKTURQUOISE", Integer.valueOf(52945));
/*  43 */     webColor.put("DIMGRAY", Integer.valueOf(6908265));
/*  44 */     webColor.put("LIGHTSEAGREEN", Integer.valueOf(2142890));
/*  45 */     webColor.put("DARKVIOLET", Integer.valueOf(9699539));
/*  46 */     webColor.put("FIREBRICK", Integer.valueOf(11674146));
/*  47 */     webColor.put("MEDIUMVIOLETRED", Integer.valueOf(13047173));
/*  48 */     webColor.put("MEDIUMSEAGREEN", Integer.valueOf(3978097));
/*  49 */     webColor.put("CHOCOLATE", Integer.valueOf(13789470));
/*  50 */     webColor.put("CRIMSON", Integer.valueOf(14423100));
/*  51 */     webColor.put("STEELBLUE", Integer.valueOf(4620980));
/*  52 */     webColor.put("GOLDENROD", Integer.valueOf(14329120));
/*  53 */     webColor.put("MEDIUMSPRINGGREEN", Integer.valueOf(64154));
/*  54 */     webColor.put("LAWNGREEN", Integer.valueOf(8190976));
/*  55 */     webColor.put("CADETBLUE", Integer.valueOf(6266528));
/*  56 */     webColor.put("DARKORCHID", Integer.valueOf(10040012));
/*  57 */     webColor.put("YELLOWGREEN", Integer.valueOf(10145074));
/*  58 */     webColor.put("LIMEGREEN", Integer.valueOf(3329330));
/*  59 */     webColor.put("ORANGERED", Integer.valueOf(16729344));
/*  60 */     webColor.put("DARKORANGE", Integer.valueOf(16747520));
/*  61 */     webColor.put("ORANGE", Integer.valueOf(16753920));
/*  62 */     webColor.put("GOLD", Integer.valueOf(16766720));
/*  63 */     webColor.put("YELLOW", Integer.valueOf(16776960));
/*  64 */     webColor.put("CHARTREUSE", Integer.valueOf(8388352));
/*  65 */     webColor.put("LIME", Integer.valueOf(65280));
/*  66 */     webColor.put("SPRINGGREEN", Integer.valueOf(65407));
/*  67 */     webColor.put("AQUA", Integer.valueOf(65535));
/*  68 */     webColor.put("CYAN", Integer.valueOf(65535));
/*  69 */     webColor.put("DEEPSKYBLUE", Integer.valueOf(49151));
/*  70 */     webColor.put("BLUE", Integer.valueOf(255));
/*  71 */     webColor.put("MAGENTA", Integer.valueOf(16711935));
/*  72 */     webColor.put("RED", Integer.valueOf(16711680));
/*  73 */     webColor.put("GRAY", Integer.valueOf(8421504));
/*  74 */     webColor.put("SLATEGRAY", Integer.valueOf(7372944));
/*  75 */     webColor.put("PERU", Integer.valueOf(13468991));
/*  76 */     webColor.put("BLUEVIOLET", Integer.valueOf(9055202));
/*  77 */     webColor.put("LIGHTSLATEGRAY", Integer.valueOf(7833753));
/*  78 */     webColor.put("DEEPPINK", Integer.valueOf(16716947));
/*  79 */     webColor.put("MEDIUMTURQUOISE", Integer.valueOf(4772300));
/*  80 */     webColor.put("DODGERBLUE", Integer.valueOf(2003199));
/*  81 */     webColor.put("TURQUOISE", Integer.valueOf(4251856));
/*  82 */     webColor.put("ROYALBLUE", Integer.valueOf(4286945));
/*  83 */     webColor.put("SLATEBLUE", Integer.valueOf(6970061));
/*  84 */     webColor.put("DARKKHAKI", Integer.valueOf(12433259));
/*  85 */     webColor.put("INDIANRED", Integer.valueOf(13458524));
/*  86 */     webColor.put("MEDIUMORCHID", Integer.valueOf(12211667));
/*  87 */     webColor.put("GREENYELLOW", Integer.valueOf(11403055));
/*  88 */     webColor.put("MEDIUMAQUAMARINE", Integer.valueOf(6737322));
/*  89 */     webColor.put("DARKSEAGREEN", Integer.valueOf(9419919));
/*  90 */     webColor.put("TOMATO", Integer.valueOf(16737095));
/*  91 */     webColor.put("ROSYBROWN", Integer.valueOf(12357519));
/*  92 */     webColor.put("ORCHID", Integer.valueOf(14315734));
/*  93 */     webColor.put("MEDIUMPURPLE", Integer.valueOf(9662683));
/*  94 */     webColor.put("PALEVIOLETRED", Integer.valueOf(14381203));
/*  95 */     webColor.put("CORAL", Integer.valueOf(16744272));
/*  96 */     webColor.put("CORNFLOWERBLUE", Integer.valueOf(6591981));
/*  97 */     webColor.put("DARKGRAY", Integer.valueOf(11119017));
/*  98 */     webColor.put("SANDYBROWN", Integer.valueOf(16032864));
/*  99 */     webColor.put("MEDIUMSLATEBLUE", Integer.valueOf(8087790));
/* 100 */     webColor.put("TAN", Integer.valueOf(13808780));
/* 101 */     webColor.put("DARKSALMON", Integer.valueOf(15308410));
/* 102 */     webColor.put("BURLYWOOD", Integer.valueOf(14596231));
/* 103 */     webColor.put("HOTPINK", Integer.valueOf(16738740));
/* 104 */     webColor.put("SALMON", Integer.valueOf(16416882));
/* 105 */     webColor.put("VIOLET", Integer.valueOf(15631086));
/* 106 */     webColor.put("LIGHTCORAL", Integer.valueOf(15761536));
/* 107 */     webColor.put("SKYBLUE", Integer.valueOf(8900331));
/* 108 */     webColor.put("LIGHTSALMON", Integer.valueOf(16752762));
/* 109 */     webColor.put("PLUM", Integer.valueOf(14524637));
/* 110 */     webColor.put("KHAKI", Integer.valueOf(15787660));
/* 111 */     webColor.put("LIGHTGREEN", Integer.valueOf(9498256));
/* 112 */     webColor.put("AQUAMARINE", Integer.valueOf(8388564));
/* 113 */     webColor.put("SILVER", Integer.valueOf(12632256));
/* 114 */     webColor.put("LIGHTSKYBLUE", Integer.valueOf(8900346));
/* 115 */     webColor.put("LIGHTSTEELBLUE", Integer.valueOf(11584734));
/* 116 */     webColor.put("LIGHTBLUE", Integer.valueOf(11393254));
/* 117 */     webColor.put("PALEGREEN", Integer.valueOf(10025880));
/* 118 */     webColor.put("THISTLE", Integer.valueOf(14204888));
/* 119 */     webColor.put("POWDERBLUE", Integer.valueOf(11591910));
/* 120 */     webColor.put("PALEGOLDENROD", Integer.valueOf(15657130));
/* 121 */     webColor.put("PALETURQUOISE", Integer.valueOf(11529966));
/* 122 */     webColor.put("LIGHTGRAY", Integer.valueOf(13882323));
/* 123 */     webColor.put("LIGHTGREY", Integer.valueOf(13882323));
/* 124 */     webColor.put("WHEAT", Integer.valueOf(16113331));
/* 125 */     webColor.put("NAVAJOWHITE", Integer.valueOf(16768685));
/* 126 */     webColor.put("MOCCASIN", Integer.valueOf(16770229));
/* 127 */     webColor.put("LIGHTPINK", Integer.valueOf(16758465));
/* 128 */     webColor.put("GAINSBORO", Integer.valueOf(14474460));
/* 129 */     webColor.put("PEACHPUFF", Integer.valueOf(16767673));
/* 130 */     webColor.put("PINK", Integer.valueOf(16761035));
/* 131 */     webColor.put("BISQUE", Integer.valueOf(16770244));
/* 132 */     webColor.put("LIGHTGOLDENROD", Integer.valueOf(16448210));
/* 133 */     webColor.put("LIGHTGOLDENRODYELLOW", Integer.valueOf(16448210));
/* 134 */     webColor.put("BLANCHEDALMOND", Integer.valueOf(16772045));
/* 135 */     webColor.put("LEMONCHIFFON", Integer.valueOf(16775885));
/* 136 */     webColor.put("BEIGE", Integer.valueOf(16119260));
/* 137 */     webColor.put("ANTIQUEWHITE", Integer.valueOf(16444375));
/* 138 */     webColor.put("PAPAYAWHIP", Integer.valueOf(16773077));
/* 139 */     webColor.put("CORNSILK", Integer.valueOf(16775388));
/* 140 */     webColor.put("LIGHTYELLOW", Integer.valueOf(16777184));
/* 141 */     webColor.put("LIGHTCYAN", Integer.valueOf(14745599));
/* 142 */     webColor.put("LINEN", Integer.valueOf(16445670));
/* 143 */     webColor.put("LAVENDER", Integer.valueOf(15132410));
/* 144 */     webColor.put("MISTYROSE", Integer.valueOf(16770273));
/* 145 */     webColor.put("OLDLACE", Integer.valueOf(16643558));
/* 146 */     webColor.put("WHITESMOKE", Integer.valueOf(16119285));
/* 147 */     webColor.put("SEASHELL", Integer.valueOf(16774638));
/* 148 */     webColor.put("IVORY", Integer.valueOf(16777200));
/* 149 */     webColor.put("HONEYDEW", Integer.valueOf(15794160));
/* 150 */     webColor.put("ALICEBLUE", Integer.valueOf(15792383));
/* 151 */     webColor.put("LAVENDERBLUSH", Integer.valueOf(16773365));
/* 152 */     webColor.put("MINTCREAM", Integer.valueOf(16121850));
/* 153 */     webColor.put("SNOW", Integer.valueOf(16775930));
/* 154 */     webColor.put("WHITE", Integer.valueOf(16777215));
/* 155 */     webColor.put("FUCHSIA", Integer.valueOf(16711935));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.connector.helpers.ColorHelpers
 * JD-Core Version:    0.6.0
 */