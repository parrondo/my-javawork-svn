/*    */ package com.dukascopy.calculator.graph;
/*    */ 
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.font.TextLayout;
/*    */ import java.awt.geom.Line2D.Double;
/*    */ 
/*    */ public class HAxis extends Axis
/*    */ {
/*    */   protected double y;
/*    */ 
/*    */   public HAxis()
/*    */   {
/* 11 */     setY(0.0D);
/*    */   }
/*    */ 
/*    */   public void setY(double y)
/*    */   {
/* 19 */     this.y = y;
/*    */   }
/*    */ 
/*    */   public void draw(Model model, View view, Graphics2D graphics2d)
/*    */   {
/* 30 */     double left = view.getTransformation().toModelX(0.0D);
/* 31 */     double right = view.getTransformation().toModelX(view.getWidth());
/* 32 */     double y = view.getTransformation().toViewY(this.y);
/*    */ 
/* 34 */     Line2D.Double line = new Line2D.Double(0.0D, y, view.getWidth(), y);
/*    */ 
/* 36 */     graphics2d.draw(line);
/*    */ 
/* 38 */     if (getShowMajorUnit())
/*    */     {
/* 40 */       double unit = view.getTransformation().getXMajorUnit();
/* 41 */       double origin = view.getTransformation().getOriginY();
/* 42 */       int i = (int)Math.ceil(left / unit) - 1;
/* 43 */       for (; i < (int)Math.floor(right / unit) + 2; i++)
/*    */       {
/* 45 */         double tick = view.getTransformation().toViewX(i * unit);
/* 46 */         line = new Line2D.Double(tick, y, tick, y + getMajorUnitTickLength());
/*    */ 
/* 49 */         graphics2d.draw(line);
/*    */ 
/* 51 */         String s = convertDouble(i * unit);
/* 52 */         graphics2d.setFont(view.getFont());
/* 53 */         TextLayout textLayout = new TextLayout(s, graphics2d.getFont(), graphics2d.getFontRenderContext());
/*    */ 
/* 56 */         float width = textLayout.getVisibleAdvance();
/* 57 */         float height = textLayout.getAscent() + textLayout.getLeading();
/* 58 */         if (i * unit != origin) {
/* 59 */           graphics2d.drawString(s, (float)tick - 0.5F * width, (float)y + getMajorUnitTickLength() + height);
/*    */         }
/*    */         else
/*    */         {
/* 63 */           graphics2d.drawString(s, (float)tick - width - getMajorUnitTickLength() - 2.0F, (float)y + getMajorUnitTickLength() + height);
/*    */         }
/*    */ 
/*    */       }
/*    */ 
/*    */     }
/*    */ 
/* 70 */     if (getShowMinorUnit())
/*    */     {
/* 72 */       double unit = view.getTransformation().getXMinorUnit();
/* 73 */       int i = (int)Math.ceil(left / unit) - 1;
/* 74 */       for (; i < (int)Math.floor(right / unit) + 2; i++)
/*    */       {
/* 76 */         double tick = view.getTransformation().toViewX(i * unit);
/* 77 */         line = new Line2D.Double(tick, y, tick, y + getMinorUnitTickLength());
/*    */ 
/* 80 */         graphics2d.draw(line);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.graph.HAxis
 * JD-Core Version:    0.6.0
 */