/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import java.awt.Color;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ 
/*     */ public abstract class AbstractLeveledChartObject extends AbstractStickablePointsChartObject
/*     */   implements IRetracementLevels
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected List<Object[]> levels;
/*     */ 
/*     */   public AbstractLeveledChartObject(String key, IChart.Type type)
/*     */   {
/*  23 */     super(key, type);
/*  24 */     setUnderEdit(true);
/*  25 */     setLevels(getDefaults());
/*     */   }
/*     */ 
/*     */   public AbstractLeveledChartObject(AbstractLeveledChartObject chartObject) {
/*  29 */     super(chartObject);
/*     */ 
/*  31 */     setLevels(chartObject.getLevels());
/*     */   }
/*     */ 
/*     */   public void setLevels(List<Object[]> levels)
/*     */   {
/*  37 */     this.levels = levels;
/*     */   }
/*     */ 
/*     */   public List<Object[]> getLevels()
/*     */   {
/*  42 */     return this.levels;
/*     */   }
/*     */ 
/*     */   public String getLevelLabel(int index)
/*     */   {
/*  47 */     validateLevelIndex(index);
/*  48 */     List levels = getLevels();
/*  49 */     return (String)((Object[])levels.get(index))[0];
/*     */   }
/*     */ 
/*     */   public void setLevelLabel(int index, String label)
/*     */   {
/*  54 */     validateLevelIndex(index);
/*  55 */     List levels = getLevels();
/*  56 */     ((Object[])levels.get(index))[0] = label;
/*     */   }
/*     */ 
/*     */   public Double getLevelValue(int index)
/*     */   {
/*  61 */     validateLevelIndex(index);
/*  62 */     List levels = getLevels();
/*  63 */     return (Double)((Object[])levels.get(index))[1];
/*     */   }
/*     */ 
/*     */   public void setLevelValue(int index, Double value)
/*     */   {
/*  68 */     validateLevelIndex(index);
/*  69 */     List levels = getLevels();
/*  70 */     ((Object[])levels.get(index))[1] = value;
/*     */   }
/*     */ 
/*     */   public Color getLevelColor(int index)
/*     */   {
/*  75 */     validateLevelIndex(index);
/*  76 */     List levels = getLevels();
/*  77 */     return (Color)((Object[])levels.get(index))[2];
/*     */   }
/*     */ 
/*     */   public void setLevelColor(int index, Color color)
/*     */   {
/*  82 */     validateLevelIndex(index);
/*  83 */     List levels = getLevels();
/*  84 */     ((Object[])levels.get(index))[2] = color;
/*     */   }
/*     */ 
/*     */   public void addLevel(String label, Double value, Color color)
/*     */   {
/*  89 */     List levels = getLevels();
/*  90 */     Object[] level = { label, value, color };
/*  91 */     levels.add(level);
/*     */   }
/*     */ 
/*     */   public void removeLevel(int index)
/*     */   {
/*  96 */     validateLevelIndex(index);
/*  97 */     List levels = getLevels();
/*  98 */     levels.remove(index);
/*     */   }
/*     */ 
/*     */   public int getLevelsCount()
/*     */   {
/* 103 */     return getLevels().size();
/*     */   }
/*     */ 
/*     */   public boolean compareLevels(List<Object[]> levels1, List<Object[]> levels2)
/*     */   {
/* 108 */     boolean result = true;
/*     */ 
/* 110 */     if ((levels1 != null) && (levels2 != null) && (levels1.size() == levels2.size()))
/*     */     {
/* 112 */       sortLevels(levels1);
/* 113 */       sortLevels(levels2);
/*     */ 
/* 115 */       for (int i = 0; i < levels1.size(); i++) {
/* 116 */         Object[] obj1 = (Object[])levels1.get(i);
/* 117 */         Object[] obj2 = (Object[])levels2.get(i);
/*     */ 
/* 119 */         result &= ((String.valueOf(obj1[0]).equals(String.valueOf(obj2[0]))) && (StratUtils.round(((Double)obj1[1]).doubleValue(), 3) == StratUtils.round(((Double)obj2[1]).doubleValue(), 3)));
/*     */ 
/* 122 */         result &= (((obj1[2] == null) && (obj2[2] == null)) || ((obj1[2] == null) && (obj2[2] != null) && (getColor().equals(obj2[2]))) || ((obj2[2] == null) && (obj1[2] != null) && (getColor().equals(obj1[2]))) || ((obj1[2] != null) && (((Color)obj1[2]).equals((Color)obj2[2]))));
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 130 */       result = false;
/*     */     }
/*     */ 
/* 133 */     return result;
/*     */   }
/*     */ 
/*     */   private void sortLevels(List<Object[]> levels) {
/* 137 */     Collections.sort(levels, new Comparator()
/*     */     {
/*     */       public int compare(Object[] o1, Object[] o2) {
/* 140 */         if (((Double)o1[1]).doubleValue() > ((Double)o2[1]).doubleValue()) {
/* 141 */           return -1;
/*     */         }
/*     */ 
/* 144 */         return 1;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void validateLevelIndex(int levelIndex) {
/* 151 */     List levels = getLevels();
/* 152 */     if ((levelIndex < 0) || (levelIndex > levels.size()))
/* 153 */       throw new IllegalArgumentException("Object index [" + levelIndex + "] is invalid. Please specify valid object index in <0," + levels.size() + "> bounds or greater.");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.AbstractLeveledChartObject
 * JD-Core Version:    0.6.0
 */