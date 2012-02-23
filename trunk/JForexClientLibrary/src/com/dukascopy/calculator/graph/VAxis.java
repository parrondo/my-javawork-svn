/*    */ package com.dukascopy.calculator.graph;
/*    */ 
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.font.TextLayout;
/*    */ import java.awt.geom.Line2D.Double;
/*    */ 
/*    */ public class VAxis extends Axis
/*    */ {
/*    */   protected double x;
/*    */ 
/*    */   public VAxis()
/*    */   {
/* 11 */     setX(0.0D);
/*    */   }
/*    */ 
/*    */   public void setX(double x)
/*    */   {
/* 19 */     this.x = x;
/*    */   }
/*    */ 
/*    */   public void draw(Model model, View view, Graphics2D graphics2d)
/*    */   {
/* 30 */     double top = view.getTransformation().toModelY(0.0D);
/* 31 */     double bottom = view.getTransformation().toModelY(view.getHeight());
/* 32 */     double x = view.getTransformation().toViewX(this.x);
/*    */ 
/* 34 */     Line2D.Double line = new Line2D.Double(x, 0.0D, x, view.getHeight());
/*    */ 
/* 36 */     graphics2d.draw(line);
/*    */ 
/* 38 */     if (getShowMajorUnit())
/*    */     {
/* 40 */       double unit = view.getTransformation().getYMajorUnit();
/* 41 */       double origin = view.getTransformation().getOriginY();
/* 42 */       int i = (int)Math.ceil(bottom / unit) - 2;
/* 43 */       for (; i < (int)Math.floor(top / unit) + 1; i++)
/*    */       {
/* 45 */         double tick = view.getTransformation().toViewY(i * unit);
/* 46 */         line = new Line2D.Double(x, tick, x - getMajorUnitTickLength(), tick);
/*    */ 
/* 49 */         graphics2d.draw(line);
/*    */ 
/* 52 */         if (i * unit != origin) {
/* 53 */           String s = convertDouble(i * unit);
/* 54 */           graphics2d.setFont(view.getFont());
/* 55 */           TextLayout textLayout = new TextLayout(s, graphics2d.getFont(), graphics2d.getFontRenderContext());
/*    */ 
/* 58 */           float width = textLayout.getAdvance();
/* 59 */           float height = textLayout.getAscent();
/* 60 */           graphics2d.drawString(s, (float)x - width - getMajorUnitTickLength() - 2.0F, (float)tick + 0.5F * height);
/*    */         }
/*    */       }
/*    */     }
/*    */ 
/* 65 */     if (getShowMinorUnit())
/*    */     {
/* 67 */       double unit = view.getTransformation().getYMinorUnit();
/* 68 */       int i = (int)Math.ceil(bottom / unit) - 2;
/* 69 */       for (; i < (int)Math.floor(top / unit) + 1; i++)
/*    */       {
/* 71 */         double tick = view.getTransformation().toViewY(i * unit);
/* 72 */         line = new Line2D.Double(x, tick, x - getMinorUnitTickLength(), tick);
/*    */ 
/* 75 */         graphics2d.draw(line);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.graph.VAxis
 * JD-Core Version:    0.6.0
 */