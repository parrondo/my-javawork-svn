/*    */ package com.dukascopy.calculator.graph;
/*    */ 
/*    */ import java.awt.Graphics2D;
/*    */ import java.util.LinkedList;
/*    */ import java.util.ListIterator;
/*    */ 
/*    */ public class Model
/*    */ {
/*    */   private HAxis xAxis;
/*    */   private VAxis yAxis;
/*    */   protected LinkedList<Element> elements;
/*    */ 
/*    */   public Model()
/*    */   {
/* 14 */     this.xAxis = new HAxis();
/* 15 */     this.yAxis = new VAxis();
/* 16 */     this.elements = new LinkedList();
/* 17 */     this.elements.add(this.xAxis);
/* 18 */     this.elements.add(this.yAxis);
/*    */   }
/*    */ 
/*    */   public void add(Element element)
/*    */   {
/* 26 */     this.elements.add(element);
/*    */   }
/*    */ 
/*    */   public void reset(Locus locus)
/*    */   {
/* 34 */     this.elements.clear();
/* 35 */     this.elements.add(this.xAxis);
/* 36 */     this.elements.add(this.yAxis);
/* 37 */     this.elements.add(locus);
/*    */   }
/*    */ 
/*    */   public void draw(View view, Graphics2D graphics2d)
/*    */   {
/* 46 */     for (ListIterator i = this.elements.listIterator(); i.hasNext(); )
/* 47 */       ((Element)i.next()).draw(this, view, graphics2d);
/*    */   }
/*    */ 
/*    */   public void updatePaths()
/*    */   {
/* 56 */     for (ListIterator i = this.elements.listIterator(); i.hasNext(); ) {
/* 57 */       Element element = (Element)i.next();
/* 58 */       if ((element instanceof Locus)) {
/* 59 */         Locus locus = (Locus)element;
/* 60 */         locus.updatePath();
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   Axis getXAxis()
/*    */   {
/* 70 */     return this.xAxis;
/*    */   }
/*    */ 
/*    */   Axis getYAxis()
/*    */   {
/* 77 */     return this.yAxis;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.graph.Model
 * JD-Core Version:    0.6.0
 */