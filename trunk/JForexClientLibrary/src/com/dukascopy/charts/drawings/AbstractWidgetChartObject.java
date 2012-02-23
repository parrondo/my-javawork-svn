/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.drawings.IWidgetChartObject;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import com.dukascopy.charts.view.swing.AbstractChartWidgetPanel;
/*     */ import java.awt.Color;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import javax.swing.SwingUtilities;
/*     */ 
/*     */ public abstract class AbstractWidgetChartObject extends AbstractFillableChartObject
/*     */   implements IWidgetChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final float ADJUSTING_STEP = 0.005F;
/*     */   private transient AbstractChartWidgetPanel chartWidgetPanel;
/*     */   protected transient FormattersManager formattersManager;
/*  31 */   private boolean headerVisible = true;
/*     */ 
/*  33 */   protected float posX = 0.01F; protected float posY = 0.01F;
/*  34 */   protected int width = 350; protected int height = 40;
/*     */ 
/*     */   public AbstractWidgetChartObject(String key, IChart.Type type)
/*     */   {
/*  38 */     super(key, type);
/*     */ 
/*  40 */     setSticky(false);
/*  41 */     setUnderEdit(false);
/*     */   }
/*     */ 
/*     */   public AbstractWidgetChartObject(AbstractWidgetChartObject chartObject) {
/*  45 */     super(chartObject);
/*     */ 
/*  47 */     this.headerVisible = chartObject.headerVisible;
/*  48 */     this.posX = chartObject.posX;
/*  49 */     this.posY = chartObject.posY;
/*  50 */     this.width = chartObject.width;
/*  51 */     this.height = chartObject.height;
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  62 */     this.formattersManager = formattersManager;
/*     */   }
/*     */ 
/*     */   public FormattersManager getFormattersManager()
/*     */   {
/*  68 */     return this.formattersManager;
/*     */   }
/*     */ 
/*     */   public int getPointsCount()
/*     */   {
/*  73 */     return 0;
/*     */   }
/*     */ 
/*     */   public boolean hasPriceValue()
/*     */   {
/*  78 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean hasTimeValue()
/*     */   {
/*  83 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean intersects(Point point, IMapper mapper, int range)
/*     */   {
/*  88 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isHeaderVisible()
/*     */   {
/*  94 */     return this.headerVisible;
/*     */   }
/*     */ 
/*     */   public void setHeaderVisible(boolean visible)
/*     */   {
/*  99 */     this.headerVisible = visible;
/*     */ 
/* 101 */     if (this.chartWidgetPanel != null)
/* 102 */       SwingUtilities.invokeLater(new Runnable(visible)
/*     */       {
/*     */         public void run()
/*     */         {
/* 106 */           AbstractWidgetChartObject.this.chartWidgetPanel.setHeaderVisible(this.val$visible);
/*     */         }
/*     */       });
/*     */   }
/*     */ 
/*     */   public void setChartWidgetPanel(AbstractChartWidgetPanel panel) {
/* 113 */     this.chartWidgetPanel = panel;
/*     */   }
/*     */ 
/*     */   public AbstractChartWidgetPanel getChartWidgetPanel() {
/* 117 */     return this.chartWidgetPanel;
/*     */   }
/*     */ 
/*     */   public void setFillColor(Color fillColor)
/*     */   {
/* 122 */     super.setFillColor(fillColor);
/*     */ 
/* 124 */     repaintWidget();
/*     */   }
/*     */ 
/*     */   public void setFillOpacity(float fillAlpha)
/*     */   {
/* 129 */     super.setFillOpacity(fillAlpha);
/*     */ 
/* 131 */     repaintWidget();
/*     */   }
/*     */ 
/*     */   private void repaintWidget() {
/* 135 */     if (this.chartWidgetPanel != null)
/* 136 */       SwingUtilities.invokeLater(new Runnable()
/*     */       {
/*     */         public void run()
/*     */         {
/* 140 */           AbstractWidgetChartObject.this.chartWidgetPanel.repaint();
/*     */         }
/*     */       });
/*     */   }
/*     */ 
/*     */   public float getPosX()
/*     */   {
/* 149 */     return this.posX;
/*     */   }
/*     */ 
/*     */   public void setPosX(float value)
/*     */   {
/* 157 */     if (this.posX == value) {
/* 158 */       return;
/*     */     }
/* 160 */     Float old = new Float(this.posX);
/* 161 */     this.posX = value;
/*     */ 
/* 163 */     if (this.chartWidgetPanel != null) {
/* 164 */       int x = (int)(this.chartWidgetPanel.getParent().getWidth() * this.posX);
/* 165 */       this.chartWidgetPanel.setLocation(x, this.chartWidgetPanel.getLocation().y);
/*     */     }
/*     */ 
/* 168 */     firePropertyChange("widget.posx", old, new Float(value));
/*     */   }
/*     */ 
/*     */   public float getPosY()
/*     */   {
/* 173 */     return this.posY;
/*     */   }
/*     */ 
/*     */   public void setPosY(float value)
/*     */   {
/* 181 */     if (this.posY == value) {
/* 182 */       return;
/*     */     }
/* 184 */     Float old = new Float(this.posY);
/* 185 */     this.posY = value;
/*     */ 
/* 187 */     if (this.chartWidgetPanel != null) {
/* 188 */       int y = (int)(this.chartWidgetPanel.getParent().getHeight() * this.posY);
/* 189 */       this.chartWidgetPanel.setLocation(this.chartWidgetPanel.getLocation().x, y);
/*     */     }
/*     */ 
/* 192 */     firePropertyChange("widget.posy", old, new Float(value));
/*     */   }
/*     */ 
/*     */   public Dimension getSize()
/*     */   {
/* 197 */     return new Dimension(this.width, this.height);
/*     */   }
/*     */ 
/*     */   public void setPreferredSize(Dimension dimension)
/*     */   {
/* 202 */     this.width = dimension.width;
/* 203 */     this.height = dimension.height;
/*     */ 
/* 205 */     if (this.chartWidgetPanel != null)
/* 206 */       this.chartWidgetPanel.setSize(this.width, this.height);
/*     */   }
/*     */ 
/*     */   public void setColor(Color color)
/*     */   {
/* 212 */     super.setColor(color);
/*     */ 
/* 214 */     if (this.chartWidgetPanel != null)
/* 215 */       this.chartWidgetPanel.updateFonts(getFont().getFamily(), color);
/*     */   }
/*     */ 
/*     */   public void setFont(Font font)
/*     */   {
/* 221 */     super.setFont(font);
/*     */ 
/* 223 */     if (this.chartWidgetPanel != null)
/* 224 */       this.chartWidgetPanel.updateFonts(font.getFamily(), getColor());
/*     */   }
/*     */ 
/*     */   public void moveUp(IMapper mapper)
/*     */   {
/* 231 */     this.posY -= 0.005F;
/*     */   }
/*     */ 
/*     */   public void moveDown(IMapper mapper)
/*     */   {
/* 236 */     this.posY += 0.005F;
/*     */   }
/*     */ 
/*     */   public void moveLeft(IMapper mapper)
/*     */   {
/* 241 */     this.posX -= 0.005F;
/*     */   }
/*     */ 
/*     */   public void moveRight(IMapper mapper)
/*     */   {
/* 246 */     this.posX += 0.005F;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.AbstractWidgetChartObject
 * JD-Core Version:    0.6.0
 */