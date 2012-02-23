/*     */ package com.dukascopy.api.impl.connect;
/*     */ 
/*     */ import com.dukascopy.charts.persistence.EnabledIndicatorBean;
/*     */ import com.dukascopy.charts.persistence.SettingsStorage;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.prefs.BackingStoreException;
/*     */ import java.util.prefs.Preferences;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class IndicatorsSettingsStorage
/*     */   implements SettingsStorage
/*     */ {
/*  19 */   private static final Logger LOGGER = LoggerFactory.getLogger(IndicatorsSettingsStorage.class);
/*     */   private static final String STORAGE_ROOT = "com/dukascopy/dds2/platform/gui/";
/*     */   private static final String JFOREX = "jforex";
/*     */   private static final String CUSTOM_INDICATOR_SOURCE_FILE_NAME = "custIndSourceFileName";
/*     */   private static final String CUSTOM_INDICATOR_BINARY_FILE_NAME = "custIndBinaryFileName";
/*     */   private static final String CUSTOM_ENABLED_INDICATORS = "custEnabledIndicators";
/*     */   private String accountName;
/*     */ 
/*     */   public IndicatorsSettingsStorage(String accountName)
/*     */   {
/*  32 */     this.accountName = accountName;
/*     */   }
/*     */ 
/*     */   public List<EnabledIndicatorBean> getEnabledIndicators()
/*     */   {
/*  37 */     return loadEnabledIndicators();
/*     */   }
/*     */ 
/*     */   public void saveEnabledIndicator(EnabledIndicatorBean indicatorBean)
/*     */   {
/*  42 */     Preferences nodes = getCustomEnabledIndicatorsNode();
/*  43 */     Preferences node = nodes.node(indicatorBean.getName());
/*  44 */     node.put("custIndSourceFileName", indicatorBean.getSourceFullFileName());
/*  45 */     node.put("custIndBinaryFileName", indicatorBean.getBinaryFullFileName());
/*  46 */     flush(nodes);
/*     */   }
/*     */ 
/*     */   public void removeEnabledIndicator(EnabledIndicatorBean indicatorBean)
/*     */   {
/*  51 */     Preferences nodes = getCustomEnabledIndicatorsNode();
/*  52 */     Preferences node = nodes.node(indicatorBean.getName());
/*  53 */     removeNode(node);
/*  54 */     flush(nodes);
/*     */   }
/*     */ 
/*     */   List<EnabledIndicatorBean> loadEnabledIndicators() {
/*  58 */     Preferences customIndicatorsNode = getCustomEnabledIndicatorsNode();
/*  59 */     String[] ids = getChildrenNames(customIndicatorsNode);
/*  60 */     if ((ids == null) || (ids.length == 0)) {
/*  61 */       return Collections.emptyList();
/*     */     }
/*  63 */     LinkedList beans = new LinkedList();
/*  64 */     for (String customIndicatorName : ids) {
/*  65 */       Preferences node = customIndicatorsNode.node(customIndicatorName);
/*  66 */       String sourceFullFileName = node.get("custIndSourceFileName", null);
/*  67 */       String binaryFullFileName = node.get("custIndBinaryFileName", null);
/*  68 */       EnabledIndicatorBean bean = new EnabledIndicatorBean(customIndicatorName, sourceFullFileName, binaryFullFileName);
/*  69 */       beans.add(bean);
/*     */     }
/*     */ 
/*  72 */     return beans;
/*     */   }
/*     */ 
/*     */   private String[] getChildrenNames(Preferences parentNode) {
/*     */     try {
/*  77 */       return parentNode.childrenNames(); } catch (BackingStoreException e) {
/*     */     }
/*  79 */     return null;
/*     */   }
/*     */ 
/*     */   void removeNode(Preferences nodeToBeRemoved)
/*     */   {
/*     */     try {
/*  85 */       nodeToBeRemoved.removeNode();
/*     */     } catch (BackingStoreException e) {
/*  87 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void flush(Preferences nodes) {
/*     */     try {
/*  93 */       nodes.flush();
/*     */     } catch (BackingStoreException e) {
/*  95 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private Preferences getCustomEnabledIndicatorsNode() {
/* 100 */     return getJForexNode().node("custEnabledIndicators");
/*     */   }
/*     */ 
/*     */   private Preferences getJForexNode() {
/* 104 */     return Preferences.userRoot().node("com/dukascopy/dds2/platform/gui/" + getUsername()).node("jforex");
/*     */   }
/*     */ 
/*     */   private String getUsername() {
/* 108 */     return this.accountName.toLowerCase();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.IndicatorsSettingsStorage
 * JD-Core Version:    0.6.0
 */