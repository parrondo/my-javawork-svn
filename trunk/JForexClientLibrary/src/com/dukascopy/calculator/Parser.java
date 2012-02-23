/*     */ package com.dukascopy.calculator;
/*     */ 
/*     */ import com.dukascopy.calculator.complex.Complex;
/*     */ import com.dukascopy.calculator.function.AFunction;
/*     */ import com.dukascopy.calculator.function.Add;
/*     */ import com.dukascopy.calculator.function.BoolFunction;
/*     */ import com.dukascopy.calculator.function.Combination;
/*     */ import com.dukascopy.calculator.function.Container;
/*     */ import com.dukascopy.calculator.function.Cube;
/*     */ import com.dukascopy.calculator.function.DFunction;
/*     */ import com.dukascopy.calculator.function.E;
/*     */ import com.dukascopy.calculator.function.I;
/*     */ import com.dukascopy.calculator.function.LFunction;
/*     */ import com.dukascopy.calculator.function.LParen;
/*     */ import com.dukascopy.calculator.function.MFunction;
/*     */ import com.dukascopy.calculator.function.Multiply;
/*     */ import com.dukascopy.calculator.function.Numeral;
/*     */ import com.dukascopy.calculator.function.PObject;
/*     */ import com.dukascopy.calculator.function.RFunction;
/*     */ import com.dukascopy.calculator.function.RParen;
/*     */ import com.dukascopy.calculator.function.Subtract;
/*     */ import com.dukascopy.calculator.function.Trig;
/*     */ import com.dukascopy.calculator.function.Uminus;
/*     */ import com.dukascopy.calculator.function.Uplus;
/*     */ import java.io.PrintStream;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Vector;
/*     */ import javax.swing.JOptionPane;
/*     */ 
/*     */ public class Parser
/*     */ {
/*     */   private LinkedList<GObject> list;
/*     */   private Base base;
/*     */ 
/*     */   public Parser()
/*     */   {
/*  47 */     this.list = new LinkedList();
/*  48 */     this.base = Base.DECIMAL;
/*     */   }
/*     */ 
/*     */   private void convertExponentsToNumerals(List<GObject> list)
/*     */   {
/*  58 */     for (ListIterator i = list.listIterator(); i.hasNext(); ) {
/*  59 */       GObject o = (GObject)i.next();
/*  60 */       if ((o instanceof E)) {
/*  61 */         i.set(new Numeral('e'));
/*  62 */         boolean negative = false;
/*  63 */         for (o = (GObject)i.next(); ((o instanceof Add)) || ((o instanceof Subtract)); )
/*     */         {
/*  66 */           if ((o instanceof Subtract))
/*  67 */             negative = !negative;
/*  68 */           i.remove();
/*     */ 
/*  64 */           o = (GObject)i.next();
/*     */         }
/*     */ 
/*  70 */         i.previous();
/*  71 */         if (negative) {
/*  72 */           i.previous();
/*  73 */           i.next();
/*  74 */           i.add(new Numeral('-'));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void convertNumerals(List<GObject> list)
/*     */   {
/*  87 */     convertExponentsToNumerals(list);
/*  88 */     for (ListIterator i = list.listIterator(); i.hasNext(); ) {
/*  89 */       GObject o = (GObject)i.next();
/*  90 */       if ((o instanceof Numeral)) {
/*  91 */         i.remove();
/*  92 */         Numeral numeral = (Numeral)o;
/*  93 */         String number = numeral.name();
/*     */         boolean flag;
/*  95 */         while (((flag = i.hasNext())) && (((o = (GObject)i.next()) instanceof Numeral))) {
/*  96 */           i.remove();
/*  97 */           numeral = (Numeral)o;
/*  98 */           number = number.concat(numeral.name());
/*     */         }
/* 100 */         if (flag) {
/* 101 */           i.previous();
/*     */         }
/* 103 */         i.add(ParseBase.parseString(number, this.base));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void convertContainers(List<GObject> list)
/*     */   {
/* 116 */     convertNumerals(list);
/* 117 */     for (ListIterator i = list.listIterator(); i.hasNext(); ) {
/* 118 */       GObject o = (GObject)i.next();
/* 119 */       if ((o instanceof Container)) {
/* 120 */         if (((Container)o).error())
/* 121 */           throw new RuntimeException("Stat Error");
/* 122 */         i.set(((Container)o).value());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void convertIs(List<GObject> list)
/*     */   {
/* 134 */     convertContainers(list);
/*     */ 
/* 141 */     for (ListIterator i = list.listIterator(0); i.hasNext(); ) {
/* 142 */       Object o = i.next();
/* 143 */       if ((o instanceof I)) {
/* 144 */         i.set(Complex.I);
/*     */ 
/* 147 */         if (i.hasNext()) {
/* 148 */           o = i.next();
/* 149 */           if (((o instanceof I)) || ((o instanceof Complex))) {
/* 150 */             i.previous();
/* 151 */             i.add(new Multiply());
/* 152 */             i.previous();
/*     */           } else {
/* 154 */             i.previous();
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 159 */         i.previous();
/* 160 */         if (i.hasPrevious()) {
/* 161 */           o = i.previous();
/* 162 */           if ((o instanceof Complex)) {
/* 163 */             i.next();
/* 164 */             i.add(new Multiply());
/* 165 */             i.next();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void convertVariables(List<GObject> list)
/*     */   {
/* 179 */     convertIs(list);
/*     */ 
/* 189 */     for (ListIterator i = list.listIterator(0); i.hasNext(); ) {
/* 190 */       Object o = i.next();
/* 191 */       if ((o instanceof com.dukascopy.calculator.function.Variable)) {
/* 192 */         i.set(new com.dukascopy.calculator.expression.Variable((com.dukascopy.calculator.function.Variable)o));
/*     */ 
/* 195 */         if (i.hasNext()) {
/* 196 */           o = i.next();
/* 197 */           if (((o instanceof Complex)) || ((o instanceof com.dukascopy.calculator.function.Variable)))
/*     */           {
/* 199 */             i.previous();
/* 200 */             i.add(new Multiply());
/* 201 */             i.previous();
/*     */           } else {
/* 203 */             i.previous();
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 208 */         i.previous();
/* 209 */         if (i.hasPrevious()) {
/* 210 */           o = i.previous();
/* 211 */           if ((o instanceof Complex)) {
/* 212 */             i.next();
/* 213 */             i.add(new Multiply());
/* 214 */             i.next();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void convertARFunctions(List<GObject> list)
/*     */   {
/* 228 */     convertVariables(list);
/* 229 */     ListIterator i = list.listIterator(list.size());
/* 230 */     while (i.hasPrevious()) {
/* 231 */       GObject o = (GObject)i.previous();
/* 232 */       if ((o instanceof AFunction)) {
/* 233 */         AFunction a = (AFunction)o;
/*     */         boolean flag;
/* 235 */         if (((flag = !i.hasPrevious() ? 1 : 0) != 0) || ((!((o = (GObject)i.previous()) instanceof OObject)) && (!(o instanceof LFunction))))
/*     */         {
/* 238 */           if (!flag)
/* 239 */             i.next();
/* 240 */           i.next();
/* 241 */           if ((a instanceof Add))
/* 242 */             i.set(new Uplus());
/* 243 */           else if ((a instanceof Subtract))
/* 244 */             i.set(new Uminus());
/*     */           else
/* 246 */             throw new RuntimeException("+/- Error");
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void convertLFunctions(List<GObject> list)
/*     */   {
/* 259 */     convertARFunctions(list);
/* 260 */     for (ListIterator i = list.listIterator(); i.hasNext(); ) {
/* 261 */       GObject o = (GObject)i.next();
/* 262 */       if ((o instanceof LFunction)) {
/* 263 */         i.remove();
/* 264 */         LFunction l = (LFunction)o;
/*     */ 
/* 267 */         OObject x = (OObject)i.previous();
/* 268 */         i.set(l.function(x));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void convertDFunctions(List<GObject> list)
/*     */   {
/* 280 */     convertLFunctions(list);
/* 281 */     for (ListIterator i = list.listIterator(); i.hasNext(); ) {
/* 282 */       GObject o = (GObject)i.next();
/* 283 */       if ((o instanceof DFunction)) {
/* 284 */         i.remove();
/* 285 */         DFunction d = (DFunction)o;
/* 286 */         o = (GObject)i.next();
/* 287 */         boolean sign = true;
/*     */ 
/* 289 */         while (((o instanceof Uplus)) || ((o instanceof Uminus))) {
/* 290 */           sign ^= o instanceof Uminus;
/* 291 */           i.remove();
/* 292 */           o = (GObject)i.next();
/*     */         }
/*     */ 
/* 296 */         OObject y = (OObject)o;
/* 297 */         i.remove();
/*     */ 
/* 299 */         OObject x = (OObject)i.previous();
/* 300 */         i.set(sign ? d.function(x, y) : d.function(x, y.negate()));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void convertRFunctions(List<GObject> list)
/*     */   {
/* 312 */     convertDFunctions(list);
/* 313 */     ListIterator i = list.listIterator(list.size());
/* 314 */     while (i.hasPrevious()) {
/* 315 */       GObject o = (GObject)i.previous();
/* 316 */       if ((o instanceof RFunction)) {
/* 317 */         i.remove();
/* 318 */         RFunction r = (RFunction)o;
/*     */ 
/* 321 */         OObject x = (OObject)(OObject)i.next();
/*     */ 
/* 323 */         i.set(r.function(x));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void convertMFunctions(List<GObject> list)
/*     */   {
/* 335 */     convertRFunctions(list);
/* 336 */     for (ListIterator i = list.listIterator(); i.hasNext(); ) {
/* 337 */       GObject o = (GObject)i.next();
/* 338 */       if ((o instanceof MFunction)) {
/* 339 */         i.remove();
/* 340 */         MFunction m = (MFunction)o;
/*     */ 
/* 342 */         OObject y = (OObject)i.next();
/* 343 */         i.remove();
/*     */ 
/* 345 */         OObject x = (OObject)i.previous();
/* 346 */         i.set(m.function(x, y));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void convertAFunctions(List<GObject> list)
/*     */   {
/* 358 */     convertMFunctions(list);
/* 359 */     for (ListIterator i = list.listIterator(); i.hasNext(); ) {
/* 360 */       GObject o = (GObject)i.next();
/* 361 */       if ((o instanceof AFunction)) {
/* 362 */         i.remove();
/* 363 */         AFunction a = (AFunction)o;
/* 364 */         OObject y = (OObject)i.next();
/* 365 */         i.remove();
/* 366 */         OObject x = (OObject)i.previous();
/* 367 */         OObject q = a.function(x, y);
/* 368 */         i.set(q);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void convertBoolFunctions(List<GObject> list)
/*     */   {
/* 380 */     convertAFunctions(list);
/* 381 */     for (ListIterator i = list.listIterator(); i.hasNext(); ) {
/* 382 */       GObject o = (GObject)i.next();
/* 383 */       if ((o instanceof BoolFunction)) {
/* 384 */         i.remove();
/* 385 */         BoolFunction b = (BoolFunction)o;
/* 386 */         OObject y = (OObject)i.next();
/* 387 */         i.remove();
/* 388 */         OObject x = (OObject)i.previous();
/* 389 */         i.set(b.function(x, y));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void convertToProduct(List<GObject> list)
/*     */   {
/* 400 */     convertBoolFunctions(list);
/* 401 */     while (list.size() > 1) {
/* 402 */       OObject z = (OObject)(OObject)list.get(0);
/* 403 */       OObject c = (OObject)(OObject)list.remove(1);
/* 404 */       list.set(0, c.multiply((OObject)(OObject)list.get(0)));
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean stripParenthesis(List<GObject> list)
/*     */   {
/* 416 */     int lparen = -1;
/* 417 */     int rparen = -1;
/* 418 */     for (int i = 0; i < list.size(); i++) {
/* 419 */       GObject o = (GObject)list.get(i);
/* 420 */       if ((o instanceof LParen)) {
/* 421 */         lparen = i;
/* 422 */       } else if ((o instanceof RParen)) {
/* 423 */         rparen = i;
/* 424 */         break;
/*     */       }
/*     */     }
/* 427 */     if ((lparen == -1) && (rparen == -1))
/* 428 */       return false;
/* 429 */     if ((lparen == -1) || (rparen == -1))
/* 430 */       throw new RuntimeException("Parenthesis Error");
/* 431 */     list.remove(rparen);
/* 432 */     list.remove(lparen);
/* 433 */     convertToProduct(list.subList(lparen, rparen - 1));
/* 434 */     return true;
/*     */   }
/*     */ 
/*     */   public OObject evaluate(AngleType angleType)
/*     */   {
/* 454 */     for (GObject o : this.list)
/* 455 */       if ((o instanceof Trig))
/* 456 */         ((Trig)o).setScale(angleType);
/*     */     try {
/* 458 */       while (stripParenthesis(this.list));
/* 459 */       convertToProduct(this.list);
/*     */     } catch (Exception e) {
/* 461 */       System.out.println(e.getMessage());
/* 462 */       return new Error("Error");
/*     */     }
/* 464 */     if (this.list.size() != 1) {
/* 465 */       System.out.println(this.list.size());
/* 466 */       return new Error("Error");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 471 */       OObject c = (OObject)(OObject)this.list.remove(0);
/* 472 */       return c.auto_simplify();
/*     */     } catch (Exception e) {
/* 474 */       System.out.println(e.getMessage());
/* 475 */     }return new Error("Error");
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 484 */     return this.list.size();
/*     */   }
/*     */ 
/*     */   public void add(GObject o)
/*     */   {
/* 492 */     this.list.add(o);
/*     */   }
/*     */ 
/*     */   public void add(int index, GObject o)
/*     */   {
/* 502 */     if (index < this.list.size())
/* 503 */       this.list.add(index, o);
/* 504 */     else if (index == this.list.size())
/* 505 */       this.list.add(o);
/*     */     else
/* 507 */       throw new RuntimeException("Parser.add(): index out of range");
/*     */   }
/*     */ 
/*     */   public PObject del(int index)
/*     */   {
/* 516 */     if ((index > 0) && (index < this.list.size())) {
/* 517 */       GObject o = (GObject)this.list.remove(index - 1);
/* 518 */       if ((o instanceof PObject)) {
/* 519 */         return (PObject)o;
/*     */       }
/* 521 */       return null;
/* 522 */     }if (index == this.list.size()) {
/* 523 */       if (this.list.isEmpty())
/* 524 */         return null;
/* 525 */       GObject o = (GObject)this.list.remove(index - 1);
/* 526 */       if ((o instanceof PObject)) {
/* 527 */         return (PObject)o;
/*     */       }
/* 529 */       return null;
/* 530 */     }if (index == 0) {
/* 531 */       return null;
/*     */     }
/* 533 */     throw new RuntimeException("Parser.del(): index out of range");
/*     */   }
/*     */ 
/*     */   private Vector<String> getStringVector(int q)
/*     */   {
/* 547 */     Vector c = new Vector();
/*     */ 
/* 549 */     int i = 0;
/* 550 */     for (GObject o : this.list) {
/* 551 */       if ((o instanceof PObject)) {
/* 552 */         PObject p = (PObject)o;
/* 553 */         for (String s : p.name_array())
/* 554 */           c.add(s);
/*     */       }
/* 556 */       i++;
/* 557 */       if (i >= Math.min(q, this.list.size())) break;
/*     */     }
/* 559 */     return c;
/*     */   }
/*     */ 
/*     */   private Vector<String> getListAsStringVector()
/*     */   {
/* 565 */     Vector c = new Vector();
/*     */ 
/* 567 */     for (Object o : this.list) {
/* 568 */       if ((o instanceof PObject)) {
/* 569 */         PObject p = (PObject)o;
/* 570 */         for (String s : p.name_array())
/* 571 */           c.add(s);
/* 572 */       } else if ((o instanceof Complex)) {
/* 573 */         Complex z = (Complex)o;
/* 574 */         String s = new StringBuilder().append(Double.toString(z.real())).append("+i").append(Double.toString(z.imaginary())).toString();
/*     */ 
/* 576 */         c.add(s);
/* 577 */       } else if ((o instanceof Double)) {
/* 578 */         Double d = (Double)o;
/* 579 */         c.add(d.toString());
/*     */       }
/*     */     }
/* 582 */     return c;
/*     */   }
/*     */ 
/*     */   public String getExpression()
/*     */   {
/* 591 */     Vector c = getStringVector(this.list.size());
/* 592 */     StringBuilder s = new StringBuilder();
/* 593 */     for (int i = 0; i < c.size(); i++)
/* 594 */       s.append((String)c.elementAt(i));
/* 595 */     return s.toString();
/*     */   }
/*     */ 
/*     */   public String getExpression(int n)
/*     */   {
/* 606 */     Vector c = getStringVector(this.list.size());
/* 607 */     StringBuilder s = new StringBuilder();
/*     */ 
/* 610 */     int start = Math.max(c.size() - n, 0);
/* 611 */     for (int i = start; i < start + Math.min(c.size(), n); i++) {
/* 612 */       s.append((String)c.elementAt(i));
/*     */     }
/* 614 */     return s.toString();
/*     */   }
/*     */ 
/*     */   public void clearExpression()
/*     */   {
/* 621 */     this.list.clear();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 629 */     return this.list.isEmpty();
/*     */   }
/*     */ 
/*     */   public final PObject getLast()
/*     */   {
/* 638 */     if (this.list.isEmpty())
/* 639 */       return null;
/* 640 */     GObject o = (GObject)this.list.getLast();
/* 641 */     if ((o instanceof PObject)) {
/* 642 */       return (PObject)o;
/*     */     }
/* 644 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean del()
/*     */   {
/* 653 */     if (this.list.isEmpty())
/* 654 */       return false;
/* 655 */     ListIterator i = this.list.listIterator(this.list.size());
/* 656 */     i.previous();
/* 657 */     i.remove();
/* 658 */     return true;
/*     */   }
/*     */ 
/*     */   public LinkedList<PObject> getList()
/*     */   {
/* 666 */     LinkedList result = new LinkedList();
/* 667 */     for (GObject object : this.list)
/* 668 */       if ((object instanceof PObject))
/* 669 */         result.add((PObject)object);
/* 670 */     return result;
/*     */   }
/*     */ 
/*     */   public void setList(LinkedList<PObject> newlist)
/*     */   {
/* 680 */     this.list.clear();
/* 681 */     for (PObject object : newlist)
/* 682 */       this.list.add(object);
/*     */   }
/*     */ 
/*     */   public final Base base()
/*     */   {
/* 692 */     return this.base;
/*     */   }
/*     */ 
/*     */   public void base(Base base)
/*     */   {
/* 703 */     this.base = base;
/*     */   }
/*     */ 
/*     */   public static String font_size(float fontSize)
/*     */   {
/* 726 */     String s = new StringBuilder().append("style=\"font-size: ").append(Float.toString(fontSize)).append("\"").toString();
/*     */ 
/* 729 */     return s;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 736 */     Parser parser = new Parser();
/* 737 */     parser.add(new Numeral('3'));
/* 738 */     parser.add(new Numeral('.'));
/* 739 */     parser.add(new Numeral('1'));
/* 740 */     parser.add(new Numeral('4'));
/* 741 */     parser.add(new Multiply());
/* 742 */     parser.add(new LParen());
/* 743 */     parser.add(new Numeral('4'));
/* 744 */     parser.add(new Add());
/* 745 */     parser.add(new Numeral('5'));
/* 746 */     parser.add(new Cube());
/* 747 */     parser.add(new Subtract());
/* 748 */     parser.add(new Numeral('5'));
/* 749 */     parser.add(new Combination());
/* 750 */     parser.add(new Numeral('2'));
/* 751 */     parser.add(new RParen());
/* 752 */     JOptionPane.showMessageDialog(null, parser.getExpression());
/* 753 */     System.out.println(parser.evaluate(AngleType.DEGREES));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.Parser
 * JD-Core Version:    0.6.0
 */