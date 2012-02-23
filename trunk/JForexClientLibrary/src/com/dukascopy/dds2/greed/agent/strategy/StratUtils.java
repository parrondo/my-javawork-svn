/*     */ package com.dukascopy.dds2.greed.agent.strategy;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
/*     */ import java.awt.Toolkit;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.MathContext;
/*     */ import java.math.RoundingMode;
/*     */ import java.net.URL;
/*     */ import java.text.DecimalFormat;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Random;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.ImageIcon;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class StratUtils
/*     */ {
/*  39 */   private static final Logger LOGGER = LoggerFactory.getLogger(StratUtils.class);
/*     */ 
/*  41 */   private static final Random random = new Random();
/*     */ 
/* 190 */   private static String tempDirectory = null;
/*     */ 
/* 378 */   static DecimalFormat df = new DecimalFormat("#,##0.#");
/*     */ 
/* 409 */   private static Map<Integer, String> symbolsMap = new HashMap();
/*     */ 
/*     */   public static Image loadImage(String path)
/*     */   {
/*  44 */     Image rc = null;
/*     */     try {
/*  46 */       InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
/*  47 */       ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
/*  48 */       turboPipe(inputStream, arrayOutputStream);
/*  49 */       rc = Toolkit.getDefaultToolkit().createImage(arrayOutputStream.toByteArray());
/*  50 */       inputStream.close();
/*     */     } catch (Throwable e) {
/*  52 */       LOGGER.warn(" Can't load icon image with name: " + path);
/*     */     }
/*  54 */     return rc;
/*     */   }
/*     */ 
/*     */   public static ImageIcon loadImageIcon(String path) {
/*  58 */     return new ImageIcon(loadImage(path));
/*     */   }
/*     */ 
/*     */   public static byte[] loadResource(String path) {
/*  62 */     byte[] rc = null;
/*     */     try {
/*  64 */       InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
/*  65 */       ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
/*  66 */       turboPipe(inputStream, arrayOutputStream);
/*  67 */       rc = arrayOutputStream.toByteArray();
/*  68 */       inputStream.close();
/*     */     } catch (Throwable e) {
/*  70 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*  72 */     return rc;
/*     */   }
/*     */ 
/*     */   public static int turboPipe(InputStream inputStream, OutputStream outputStream)
/*     */   {
/*  77 */     if ((inputStream == null) || (outputStream == null)) {
/*  78 */       return 0;
/*     */     }
/*     */ 
/*  81 */     byte[] buffer = new byte[4096];
/*     */ 
/*  83 */     int counter = 0;
/*     */     try {
/*     */       while (true) {
/*  86 */         int bytes_read = inputStream.read(buffer);
/*  87 */         if (bytes_read == -1) {
/*     */           break;
/*     */         }
/*  90 */         outputStream.write(buffer, 0, bytes_read);
/*  91 */         counter += bytes_read;
/*     */       }
/*     */     } catch (Exception e) {
/*  94 */       LOGGER.error(e.getMessage(), e);
/*     */     } finally {
/*     */     }
/*  97 */     return counter;
/*     */   }
/*     */ 
/*     */   public static byte[] readFile(String fileFullPath) {
/* 101 */     File f = new File(fileFullPath);
/* 102 */     if (!f.exists()) {
/* 103 */       return new byte[0];
/*     */     }
/* 105 */     int size = (int)f.length();
/* 106 */     if (size <= 0) {
/* 107 */       return new byte[0];
/*     */     }
/* 109 */     byte[] data = new byte[size];
/*     */     try {
/* 111 */       FileInputStream fis = new FileInputStream(f);
/* 112 */       int bytes_read = 0;
/* 113 */       while (bytes_read < size) {
/* 114 */         bytes_read += fis.read(data, bytes_read, size - bytes_read);
/*     */       }
/* 116 */       fis.close();
/*     */     } catch (Exception e) {
/* 118 */       LOGGER.error(e.getMessage(), e);
/* 119 */       return new byte[0];
/*     */     }
/* 121 */     return data;
/*     */   }
/*     */ 
/*     */   public static Icon loadIcon(String path) {
/* 125 */     Icon icon = new ImageIcon(loadImage(path));
/*     */ 
/* 127 */     if (icon == null)
/* 128 */       icon = new Icon()
/*     */       {
/*     */         public int getIconHeight() {
/* 131 */           return 16;
/*     */         }
/*     */ 
/*     */         public int getIconWidth()
/*     */         {
/* 136 */           return 30;
/*     */         }
/*     */ 
/*     */         public void paintIcon(Component c, Graphics g, int x, int y)
/*     */         {
/* 142 */           g.setColor(Color.LIGHT_GRAY);
/*     */ 
/* 144 */           g.setColor(Color.DARK_GRAY);
/* 145 */           g.drawString("+++", x + 2, y + 10);
/*     */         }
/*     */       };
/* 149 */     return icon;
/*     */   }
/*     */ 
/*     */   public static boolean copyFileFromResource(InputStream inputStream, File outFile)
/*     */   {
/*     */     try {
/* 155 */       FileOutputStream to = null;
/*     */       try
/*     */       {
/* 158 */         to = new FileOutputStream(outFile);
/* 159 */         byte[] buffer = new byte[4096];
/*     */         int bytesRead;
/* 162 */         while ((bytesRead = inputStream.read(buffer)) != -1)
/*     */         {
/* 164 */           to.write(buffer, 0, bytesRead);
/*     */         }
/*     */       } finally {
/* 167 */         if (inputStream != null)
/*     */           try {
/* 169 */             inputStream.close();
/*     */           }
/*     */           catch (IOException e)
/*     */           {
/*     */           }
/* 174 */         if (to != null)
/*     */           try {
/* 176 */             to.close();
/*     */           }
/*     */           catch (IOException e)
/*     */           {
/*     */           }
/*     */       }
/* 182 */       return true;
/*     */     } catch (Exception e) {
/* 184 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/* 186 */     return false;
/*     */   }
/*     */ 
/*     */   public static String getTempDirectory()
/*     */   {
/* 193 */     if (tempDirectory == null) {
/* 194 */       tempDirectory = System.getProperty("java.io.tmpdir");
/*     */     }
/* 196 */     return tempDirectory;
/*     */   }
/*     */ 
/*     */   public static double roundHalfUp(double value, int scale) {
/* 200 */     return round(value, scale);
/*     */   }
/*     */ 
/*     */   public static double round(double value, int scale) {
/* 204 */     boolean negative = false;
/* 205 */     if (value < 0.0D) {
/* 206 */       negative = true;
/* 207 */       value = -value;
/*     */     }
/* 209 */     if (value == 0.0D) {
/* 210 */       return value;
/*     */     }
/* 212 */     double multiplier = 1.0D;
/* 213 */     while (scale > 0) {
/* 214 */       multiplier *= 10.0D;
/* 215 */       scale--;
/*     */     }
/* 217 */     while ((scale < 0) && (value * multiplier / 10.0D >= 1.0D)) {
/* 218 */       multiplier /= 10.0D;
/* 219 */       scale++;
/*     */     }
/* 221 */     while ((scale <= 0) && (value * multiplier < 1.0D)) {
/* 222 */       multiplier *= 10.0D;
/*     */     }
/* 224 */     value *= multiplier;
/* 225 */     long longValue = ()(value + 0.5D);
/* 226 */     value = longValue / multiplier;
/* 227 */     return negative ? -value : value;
/*     */   }
/*     */ 
/*     */   public static BigDecimal roundHalfUp(BigDecimal value, int precision) {
/* 231 */     return round(value, precision);
/*     */   }
/*     */ 
/*     */   public static BigDecimal round(BigDecimal value, int precision) {
/* 235 */     int diff = value.precision() - value.scale();
/* 236 */     precision = diff + precision;
/* 237 */     if (precision <= 0) {
/* 238 */       precision = 1;
/*     */     }
/* 240 */     value = value.round(new MathContext(precision, RoundingMode.HALF_UP));
/*     */ 
/* 242 */     return value.stripTrailingZeros();
/*     */   }
/*     */ 
/*     */   public static double roundHalfDown(double value, int scale) {
/* 246 */     boolean negative = false;
/* 247 */     if (value < 0.0D) {
/* 248 */       negative = true;
/* 249 */       value = -value;
/*     */     }
/* 251 */     if (value == 0.0D) {
/* 252 */       return value;
/*     */     }
/* 254 */     double multiplier = 1.0D;
/* 255 */     while (scale > 0) {
/* 256 */       multiplier *= 10.0D;
/* 257 */       scale--;
/*     */     }
/* 259 */     while ((scale < 0) && (value * multiplier / 10.0D >= 1.0D)) {
/* 260 */       multiplier /= 10.0D;
/* 261 */       scale++;
/*     */     }
/* 263 */     while ((scale <= 0) && (scale <= 0) && (value * multiplier < 1.0D)) {
/* 264 */       multiplier *= 10.0D;
/*     */     }
/* 266 */     value *= multiplier;
/* 267 */     value = Math.ceil(value - 0.5D);
/* 268 */     value /= multiplier;
/* 269 */     return negative ? -value : value;
/*     */   }
/*     */ 
/*     */   public static BigDecimal roundHalfDown(BigDecimal value, int precision) {
/* 273 */     int diff = value.precision() - value.scale();
/* 274 */     precision = diff + precision;
/* 275 */     if (precision <= 0) {
/* 276 */       precision = 1;
/*     */     }
/* 278 */     value = value.round(new MathContext(precision, RoundingMode.HALF_DOWN));
/*     */ 
/* 280 */     return value.stripTrailingZeros();
/*     */   }
/*     */ 
/*     */   public static double roundHalfEven(double value, int scale) {
/* 284 */     boolean negative = false;
/* 285 */     if (value < 0.0D) {
/* 286 */       negative = true;
/* 287 */       value = -value;
/*     */     }
/* 289 */     if (value == 0.0D) {
/* 290 */       return value;
/*     */     }
/* 292 */     double multiplier = 1.0D;
/* 293 */     while (scale > 0) {
/* 294 */       multiplier *= 10.0D;
/* 295 */       scale--;
/*     */     }
/* 297 */     while ((scale < 0) && (value * multiplier / 10.0D >= 1.0D)) {
/* 298 */       multiplier /= 10.0D;
/* 299 */       scale++;
/*     */     }
/* 301 */     while ((scale <= 0) && (value * multiplier < 1.0D)) {
/* 302 */       multiplier *= 10.0D;
/*     */     }
/* 304 */     value *= multiplier;
/* 305 */     double twoToThe52 = 4503599627370496.0D;
/* 306 */     if (value < 4503599627370496.0D) {
/* 307 */       value = 4503599627370496.0D + value - 4503599627370496.0D;
/*     */     }
/* 309 */     value /= multiplier;
/* 310 */     return negative ? -value : value;
/*     */   }
/*     */ 
/*     */   public static BigDecimal roundHalfEven(BigDecimal value, int precision) {
/* 314 */     int diff = value.precision() - value.scale();
/* 315 */     precision = diff + precision;
/* 316 */     if (precision <= 0) {
/* 317 */       precision = 1;
/*     */     }
/* 319 */     value = value.round(new MathContext(precision, RoundingMode.HALF_EVEN));
/*     */ 
/* 321 */     return value.stripTrailingZeros();
/*     */   }
/*     */ 
/*     */   public static BigDecimal round05(BigDecimal value, int scale) {
/* 325 */     BigDecimal bd2 = BigDecimal.valueOf(2L);
/* 326 */     value = value.multiply(bd2);
/*     */ 
/* 328 */     int diff = value.precision() - value.scale();
/* 329 */     int precision = diff + scale;
/* 330 */     if (precision <= 0) {
/* 331 */       precision = 1;
/*     */     }
/* 333 */     value = value.round(new MathContext(precision, RoundingMode.HALF_UP));
/* 334 */     value = value.divide(bd2, scale < 0 ? 0 : scale + 1, RoundingMode.CEILING);
/*     */ 
/* 336 */     return value;
/*     */   }
/*     */ 
/*     */   public static double round05Pips(double value) {
/* 340 */     int pipsMultiplier = value <= 20.0D ? 10000 : 100;
/* 341 */     int rounded = (int)(value * pipsMultiplier * 10.0D + 0.5D);
/* 342 */     rounded *= 2;
/* 343 */     rounded = (int)(rounded / 10.0D + 0.5D);
/* 344 */     value = rounded / 2.0D;
/* 345 */     value /= pipsMultiplier;
/* 346 */     return value;
/*     */   }
/*     */ 
/*     */   public static double round05(double value, int scale) {
/* 350 */     boolean negative = false;
/* 351 */     if (value < 0.0D) {
/* 352 */       negative = true;
/* 353 */       value = -value;
/*     */     }
/* 355 */     if (value == 0.0D) {
/* 356 */       return value;
/*     */     }
/* 358 */     double multiplier = 1.0D;
/* 359 */     while (scale > 0) {
/* 360 */       multiplier *= 10.0D;
/* 361 */       scale--;
/*     */     }
/* 363 */     while ((scale < 0) && (value * multiplier / 10.0D >= 1.0D)) {
/* 364 */       multiplier /= 10.0D;
/* 365 */       scale++;
/*     */     }
/* 367 */     while ((scale <= 0) && (value * multiplier < 1.0D)) {
/* 368 */       multiplier *= 10.0D;
/*     */     }
/* 370 */     value *= multiplier;
/* 371 */     value *= 2.0D;
/* 372 */     value = ()(value + 0.5D);
/* 373 */     value /= 2.0D;
/* 374 */     value /= multiplier;
/* 375 */     return negative ? -value : value;
/*     */   }
/*     */ 
/*     */   public static double parseDouble(String str)
/*     */   {
/* 381 */     if (str == null) {
/* 382 */       return 0.0D;
/*     */     }
/* 384 */     return Double.parseDouble(str);
/*     */   }
/*     */ 
/*     */   public static long parseLong(String str) {
/* 388 */     if (str == null) {
/* 389 */       return 0L;
/*     */     }
/*     */ 
/* 395 */     return Long.parseLong(str);
/*     */   }
/*     */ 
/*     */   public static int parseInt(String str) {
/* 399 */     if (str == null) {
/* 400 */       return 0;
/*     */     }
/*     */ 
/* 406 */     return Integer.parseInt(str);
/*     */   }
/*     */ 
/*     */   public static String normalizeSymbol(String symbol)
/*     */   {
/* 412 */     symbol = symbol.toUpperCase();
/* 413 */     if ((symbol.indexOf("/") == -1) && (symbol.length() == 6)) {
/* 414 */       symbol = symbol.substring(0, 3) + "/" + symbol.substring(3, 6);
/*     */     }
/*     */ 
/* 417 */     if ((symbol.indexOf("/") != -1) && (symbol.length() > 10)) {
/*     */       try {
/* 419 */         int index = symbol.indexOf("/");
/* 420 */         symbol = symbol.substring(index - 3, index + 4);
/*     */       } catch (Exception e) {
/* 422 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 429 */     int hash = symbol.hashCode();
/* 430 */     String temp = (String)symbolsMap.get(Integer.valueOf(hash));
/* 431 */     if (temp == null) {
/* 432 */       symbolsMap.put(Integer.valueOf(hash), symbol);
/*     */     }
/* 434 */     symbol = (String)symbolsMap.get(Integer.valueOf(hash));
/* 435 */     return symbol;
/*     */   }
/*     */ 
/*     */   public static void returnURL(URL url, OutputStream out)
/*     */     throws IOException
/*     */   {
/* 446 */     InputStream in = url.openStream();
/* 447 */     byte[] buf = new byte[4096];
/*     */     int bytesRead;
/* 449 */     while ((bytesRead = in.read(buf)) != -1)
/* 450 */       out.write(buf, 0, bytesRead);
/*     */   }
/*     */ 
/*     */   public static String regex(String regexp, String source)
/*     */   {
/* 455 */     String rc = null;
/* 456 */     Pattern urlPattern = Pattern.compile(regexp);
/* 457 */     Matcher matcher = urlPattern.matcher(source);
/* 458 */     if (matcher.find()) {
/* 459 */       rc = matcher.group(1);
/*     */     }
/* 461 */     return rc;
/*     */   }
/*     */ 
/*     */   public static String generateLabel() {
/* 465 */     String label = "JF";
/*     */ 
/* 467 */     while (label.length() < 10) {
/* 468 */       label = label + Integer.toString(random.nextInt(100000000), 36);
/*     */     }
/* 470 */     label = label.substring(0, 9);
/* 471 */     label = label.toLowerCase();
/* 472 */     return label;
/*     */   }
/*     */ 
/*     */   public static String getExtension(String fileName)
/*     */   {
/* 477 */     String ext = null;
/*     */ 
/* 479 */     int i = fileName.lastIndexOf(46);
/*     */ 
/* 481 */     if ((i > 0) && (i < fileName.length() - 1)) {
/* 482 */       ext = fileName.substring(i + 1).toLowerCase();
/*     */     }
/* 484 */     return ext;
/*     */   }
/*     */ 
/*     */   public static int compare(double d1, double d2, double precision)
/*     */   {
/* 497 */     double diff = d1 - d2;
/*     */ 
/* 499 */     if (Math.abs(diff) <= precision) {
/* 500 */       return 0;
/*     */     }
/*     */ 
/* 503 */     if (diff > 0.0D) {
/* 504 */       return 1;
/*     */     }
/* 506 */     if (diff < 0.0D) {
/* 507 */       return -1;
/*     */     }
/*     */ 
/* 510 */     long d1Bits = Double.doubleToLongBits(d1);
/* 511 */     long d2Bits = Double.doubleToLongBits(d2);
/*     */ 
/* 513 */     return d1Bits > d2Bits ? -1 : d1Bits == d2Bits ? 0 : 1;
/*     */   }
/*     */ 
/*     */   public static int compare(double d1, double d2)
/*     */   {
/* 521 */     return compare(d1, d2, 1.0E-010D);
/*     */   }
/*     */ 
/*     */   public static int div2(int val)
/*     */   {
/* 528 */     if (val < 0) {
/* 529 */       return -(-val >> 1);
/*     */     }
/* 531 */     return val >> 1;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.StratUtils
 * JD-Core Version:    0.6.0
 */