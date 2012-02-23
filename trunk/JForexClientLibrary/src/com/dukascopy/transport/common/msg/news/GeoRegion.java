/*     */ package com.dukascopy.transport.common.msg.news;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ 
/*     */ public enum GeoRegion
/*     */ {
/*  14 */   AFRICA("Africa", new HashSet() {  }
/*  14 */   , new GeoRegion[0]), 
/*  15 */   ASIA("Asia", new HashSet() {  }
/*  15 */   , new GeoRegion[0]), 
/*  16 */   CENTRAL_AMERICA("Central America", new HashSet() {  }
/*  16 */   , new GeoRegion[0]), 
/*  17 */   EUROPE("Europe", new HashSet() {  }
/*  17 */   , new GeoRegion[0]), 
/*  18 */   EU("European Union", new HashSet() {  }
/*  18 */   , new GeoRegion[] { EUROPE }), 
/*  19 */   EZ("Euro Zone", new HashSet() {  }
/*  19 */   , new GeoRegion[] { EUROPE }), 
/*  20 */   EASTERN_EUROPE("Eastern Europe", new HashSet() {  }
/*  20 */   , new GeoRegion[] { EUROPE }), 
/*  21 */   G10("Group Of Ten", new HashSet() {  }
/*  21 */   , new GeoRegion[0]), 
/*  22 */   G7("Group Of Seven", new HashSet() {  }
/*  22 */   , new GeoRegion[0]), 
/*  23 */   NORTH_AMERICA("North America", new HashSet() {  }
/*  23 */   , new GeoRegion[0]), 
/*  24 */   PACIFIC("Pacific", new HashSet() {  }
/*  24 */   , new GeoRegion[0]), 
/*  25 */   SOUTH_AMERICA("Sauth America", new HashSet() {  }
/*  25 */   , new GeoRegion[0]), 
/*  26 */   WESTERN_EUROPE("Western Europe", new HashSet() {  }
/*  26 */   , new GeoRegion[] { EUROPE }), 
/*     */ 
/*  31 */   AD("Andorra", new HashSet() {  }
/*  31 */   , new GeoRegion[] { EUROPE }), 
/*  32 */   AE("United Arab Emirates", new HashSet() {  }
/*  32 */   , new GeoRegion[] { ASIA }), 
/*  33 */   AR("Argentina", new HashSet() {  }
/*  33 */   , new GeoRegion[] { SOUTH_AMERICA }), 
/*  34 */   AT("Austria", new HashSet() {  }
/*  34 */   , new GeoRegion[] { EUROPE, EU, EZ, WESTERN_EUROPE }), 
/*  35 */   AU("Australia", new HashSet() {  }
/*  35 */   , new GeoRegion[] { PACIFIC }), 
/*  36 */   AZ("Azerbaijan", new HashSet() {  }
/*  36 */   , new GeoRegion[] { ASIA }), 
/*  37 */   BD("Bangladesh", new HashSet() {  }
/*  37 */   , new GeoRegion[] { ASIA }), 
/*  38 */   BE("Belgium", new HashSet() {  }
/*  38 */   , new GeoRegion[] { EUROPE, WESTERN_EUROPE, EU, EZ, G10 }), 
/*  39 */   BG("Bulgaria", new HashSet() {  }
/*  39 */   , new GeoRegion[] { EUROPE, EASTERN_EUROPE, EU }), 
/*  40 */   BH("Bahrain", new HashSet() {  }
/*  40 */   , new GeoRegion[] { ASIA }), 
/*  41 */   BR("Brazil", new HashSet() {  }
/*  41 */   , new GeoRegion[] { SOUTH_AMERICA }), 
/*  42 */   CA("Canada", new HashSet() {  }
/*  42 */   , new GeoRegion[] { NORTH_AMERICA, PACIFIC, G7, G10 }), 
/*  43 */   CI("Ivory Coast", new HashSet() {  }
/*  43 */   , new GeoRegion[] { AFRICA }), 
/*  44 */   CH("Switzerland", new HashSet() {  }
/*  44 */   , new GeoRegion[] { EUROPE, WESTERN_EUROPE, G10 }), 
/*  45 */   CL("Chile", new HashSet() {  }
/*  45 */   , new GeoRegion[] { SOUTH_AMERICA, PACIFIC }), 
/*  46 */   CN("China", new HashSet() {  }
/*  46 */   , new GeoRegion[] { ASIA, PACIFIC }), 
/*  47 */   CO("Colombia", new HashSet() {  }
/*  47 */   , new GeoRegion[] { SOUTH_AMERICA }), 
/*  48 */   CY("Cyprus", new HashSet() {  }
/*  48 */   , new GeoRegion[] { ASIA }), 
/*  49 */   CZ("Czech Republic", new HashSet() {  }
/*  49 */   , new GeoRegion[] { EUROPE, EASTERN_EUROPE, EU }), 
/*  50 */   DE("Germany", new HashSet() {  }
/*  50 */   , new GeoRegion[] { EUROPE, WESTERN_EUROPE, EU, EZ, G7, G10 }), 
/*  51 */   DK("Denmark", new HashSet() {  }
/*  51 */   , new GeoRegion[] { EUROPE, EU, EZ, WESTERN_EUROPE }), 
/*  52 */   DZ("Algeria", new HashSet() {  }
/*  52 */   , new GeoRegion[] { AFRICA }), 
/*  53 */   EC("Ecuador", new HashSet() {  }
/*  53 */   , new GeoRegion[] { SOUTH_AMERICA, PACIFIC }), 
/*  54 */   EE("Estonia", new HashSet() {  }
/*  54 */   , new GeoRegion[] { EUROPE, EU, EASTERN_EUROPE }), 
/*  55 */   EG("Egypt", new HashSet() {  }
/*  55 */   , new GeoRegion[] { AFRICA }), 
/*  56 */   ES("Spain", new HashSet() {  }
/*  56 */   , new GeoRegion[] { EUROPE, EU, EZ, WESTERN_EUROPE }), 
/*  57 */   FI("Finland", new HashSet() {  }
/*  57 */   , new GeoRegion[] { EUROPE, EU, EZ }), 
/*  58 */   FR("France", new HashSet() {  }
/*  58 */   , new GeoRegion[] { EUROPE, WESTERN_EUROPE, EU, EZ, G7, G10 }), 
/*  59 */   GR("Greece", new HashSet() {  }
/*  59 */   , new GeoRegion[] { EUROPE, EU, EZ }), 
/*  60 */   HK("Hong Kong", new HashSet() {  }
/*  60 */   , new GeoRegion[] { ASIA, PACIFIC }), 
/*  61 */   HU("Hungary", new HashSet() {  }
/*  61 */   , new GeoRegion[] { EUROPE, EASTERN_EUROPE, EU }), 
/*  62 */   ID("Indonesia", new HashSet() {  }
/*  62 */   , new GeoRegion[] { ASIA }), 
/*  63 */   IE("Ireland", new HashSet() {  }
/*  63 */   , new GeoRegion[] { EUROPE, EU, EZ }), 
/*  64 */   IL("Israel", new HashSet() {  }
/*  64 */   , new GeoRegion[] { ASIA }), 
/*  65 */   IN("India", new HashSet() {  }
/*  65 */   , new GeoRegion[] { ASIA }), 
/*  66 */   IQ("Iraq", new HashSet() {  }
/*  66 */   , new GeoRegion[] { ASIA }), 
/*  67 */   IR("Iran", new HashSet() {  }
/*  67 */   , new GeoRegion[] { ASIA }), 
/*  68 */   IS("Iceland", new HashSet() {  }
/*  68 */   , new GeoRegion[] { EUROPE, EU }), 
/*  69 */   IT("Italy", new HashSet() {  }
/*  69 */   , new GeoRegion[] { EUROPE, EU, EZ, G7, G10 }), 
/*  70 */   JP("Japan", new HashSet() {  }
/*  70 */   , new GeoRegion[] { ASIA, PACIFIC, G7, G10 }), 
/*  71 */   KE("Kenya", new HashSet() {  }
/*  71 */   , new GeoRegion[] { AFRICA }), 
/*  72 */   KH("Cambodia", new HashSet() {  }
/*  72 */   , new GeoRegion[] { ASIA }), 
/*  73 */   KP("North Korea", new HashSet() {  }
/*  73 */   , new GeoRegion[] { ASIA, PACIFIC }), 
/*  74 */   KR("South Korea", new HashSet() {  }
/*  74 */   , new GeoRegion[] { ASIA, PACIFIC }), 
/*  75 */   KW("Kuwait", new HashSet() {  }
/*  75 */   , new GeoRegion[] { ASIA }), 
/*  76 */   KY("Cayman Islands", new HashSet() {  }
/*  76 */   , new GeoRegion[0]), 
/*  77 */   LK("Sri Lanka", new HashSet() {  }
/*  77 */   , new GeoRegion[] { ASIA }), 
/*  78 */   LT("Lithuania", new HashSet() {  }
/*  78 */   , new GeoRegion[] { EUROPE, EU, EASTERN_EUROPE }), 
/*  79 */   LU("Luxembourg", new HashSet() {  }
/*  79 */   , new GeoRegion[] { EUROPE }), 
/*  80 */   LV("Latvia", new HashSet() {  }
/*  80 */   , new GeoRegion[] { EUROPE, EU, EASTERN_EUROPE }), 
/*  81 */   MC("Monaco", new HashSet() {  }
/*  81 */   , new GeoRegion[] { EUROPE, EU, EZ }), 
/*  82 */   MT("Malta", new HashSet() {  }
/*  82 */   , new GeoRegion[] { EUROPE, EU }), 
/*  83 */   MX("Mexico", new HashSet() {  }
/*  83 */   , new GeoRegion[] { NORTH_AMERICA, PACIFIC }), 
/*  84 */   MY("Malaysia", new HashSet() {  }
/*  84 */   , new GeoRegion[] { ASIA, PACIFIC }), 
/*  85 */   NA("Namibia", new HashSet() {  }
/*  85 */   , new GeoRegion[] { AFRICA }), 
/*  86 */   NG("Nigeria", new HashSet() {  }
/*  86 */   , new GeoRegion[] { AFRICA }), 
/*  87 */   NL("Netherlands", new HashSet() {  }
/*  87 */   , new GeoRegion[] { EUROPE, EU, EZ, WESTERN_EUROPE, G10 }), 
/*  88 */   NO("Norway", new HashSet() {  }
/*  88 */   , new GeoRegion[] { EUROPE, EU, WESTERN_EUROPE }), 
/*  89 */   NP("Nepal", new HashSet() {  }
/*  89 */   , new GeoRegion[] { ASIA }), 
/*  90 */   NZ("New Zealand", new HashSet() {  }
/*  90 */   , new GeoRegion[] { PACIFIC }), 
/*  91 */   PA("Panama", new HashSet() {  }
/*  91 */   , new GeoRegion[] { CENTRAL_AMERICA, PACIFIC }), 
/*  92 */   PE("Peru", new HashSet() {  }
/*  92 */   , new GeoRegion[] { SOUTH_AMERICA, PACIFIC }), 
/*  93 */   PH("Philippines", new HashSet() {  }
/*  93 */   , new GeoRegion[] { ASIA, PACIFIC }), 
/*  94 */   PK("Pakistan", new HashSet() {  }
/*  94 */   , new GeoRegion[] { ASIA }), 
/*  95 */   PL("Poland", new HashSet() {  }
/*  95 */   , new GeoRegion[] { EUROPE, EASTERN_EUROPE, EU }), 
/*  96 */   PT("Portugal", new HashSet() {  }
/*  96 */   , new GeoRegion[] { EUROPE, EU, EZ, WESTERN_EUROPE }), 
/*  97 */   RO("Romania", new HashSet() {  }
/*  97 */   , new GeoRegion[] { EUROPE, EASTERN_EUROPE, EU }), 
/*  98 */   QA("Qatar", new HashSet() {  }
/*  98 */   , new GeoRegion[] { ASIA }), 
/*  99 */   RU("Russia", new HashSet() {  }
/*  99 */   , new GeoRegion[] { EUROPE, EASTERN_EUROPE, PACIFIC }), 
/* 100 */   SA("Saudi Arabia", new HashSet() {  }
/* 100 */   , new GeoRegion[] { ASIA }), 
/* 101 */   SG("Singapore", new HashSet() {  }
/* 101 */   , new GeoRegion[] { ASIA, PACIFIC }), 
/* 102 */   SE("Sweden", new HashSet() {  }
/* 102 */   , new GeoRegion[] { EUROPE, EU, G10 }), 
/* 103 */   SI("Slovenia", new HashSet() {  }
/* 103 */   , new GeoRegion[] { EUROPE, EASTERN_EUROPE, EU }), 
/* 104 */   SK("Slovakia", new HashSet() {  }
/* 104 */   , new GeoRegion[] { EUROPE, EASTERN_EUROPE, EU }), 
/* 105 */   SL("Sierra Leone", new HashSet() {  }
/* 105 */   , new GeoRegion[] { AFRICA }), 
/* 106 */   TH("Thailand", new HashSet() {  }
/* 106 */   , new GeoRegion[] { ASIA, PACIFIC }), 
/* 107 */   TN("Tunisia", new HashSet() {  }
/* 107 */   , new GeoRegion[] { AFRICA }), 
/* 108 */   TR("Turkey", new HashSet() {  }
/* 108 */   , new GeoRegion[] { EUROPE, EASTERN_EUROPE }), 
/* 109 */   TW("Taiwan", new HashSet() {  }
/* 109 */   , new GeoRegion[] { ASIA, PACIFIC }), 
/* 110 */   UA("Ukraine", new HashSet() {  }
/* 110 */   , new GeoRegion[] { EUROPE, EASTERN_EUROPE }), 
/* 111 */   UK("United Kingdom", new HashSet() {  }
/* 111 */   , new GeoRegion[] { EUROPE, EU, WESTERN_EUROPE, G7, G10 }), 
/* 112 */   US("United States", new HashSet() {  }
/* 112 */   , new GeoRegion[] { NORTH_AMERICA, PACIFIC, G7, G10 }), 
/* 113 */   VE("Venezuela", new HashSet() {  }
/* 113 */   , new GeoRegion[] { SOUTH_AMERICA }), 
/* 114 */   VN("Vietnam", new HashSet() {  }
/* 114 */   , new GeoRegion[] { ASIA, PACIFIC }), 
/* 115 */   ZA("South Africa", new HashSet() {  }
/* 115 */   , new GeoRegion[] { AFRICA }), 
/* 116 */   ZM("Zambia", new HashSet() {  }
/* 116 */   , new GeoRegion[] { AFRICA }), 
/* 117 */   ZW("Zimbabwe", new HashSet() {  }
/* 117 */   , new GeoRegion[] { AFRICA });
/*     */ 
/*     */   public static final String PREFIX = "R";
/*     */   private String name;
/*     */   private GeoRegion[] parents;
/*     */   private Set<String> codeBodySet;
/*     */ 
/* 127 */   private GeoRegion(String name, Set<String> codeBodySet, GeoRegion[] parents) { this.name = name;
/* 128 */     this.parents = parents;
/* 129 */     this.codeBodySet = codeBodySet; }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 133 */     return this.name;
/*     */   }
/*     */ 
/*     */   public List<GeoRegion> getParents() {
/* 137 */     return Arrays.asList(this.parents);
/*     */   }
/*     */ 
/*     */   public List<GeoRegion> getChildren() {
/* 141 */     List children = new ArrayList();
/* 142 */     GeoRegion[] allRegions = values();
/* 143 */     for (GeoRegion region : allRegions) {
/* 144 */       if (region.getParents().contains(this)) {
/* 145 */         children.add(region);
/*     */       }
/*     */     }
/* 148 */     return children;
/*     */   }
/*     */ 
/*     */   public Set<String> getCodeBodySet() {
/* 152 */     return this.codeBodySet;
/*     */   }
/*     */ 
/*     */   public Set<String> getCodes() {
/* 156 */     Set codes = new HashSet();
/* 157 */     if (this.codeBodySet != null) {
/* 158 */       for (String codeBody : this.codeBodySet) {
/* 159 */         codes.add("R" + '/' + codeBody);
/*     */       }
/*     */     }
/* 162 */     return codes;
/*     */   }
/*     */ 
/*     */   public static GeoRegion toGeoRegion(String value) {
/* 166 */     if (value != null)
/* 167 */       value = value.trim();
/*     */     GeoRegion region;
/* 169 */     for (region : values()) {
/* 170 */       for (String codeBody : region.codeBodySet) {
/* 171 */         if (codeBody.equals(value)) {
/* 172 */           return region;
/*     */         }
/*     */       }
/*     */     }
/* 176 */     return null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.news.GeoRegion
 * JD-Core Version:    0.6.0
 */