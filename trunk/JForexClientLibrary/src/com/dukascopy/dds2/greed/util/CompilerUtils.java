/*     */ package com.dukascopy.dds2.greed.util;
/*     */ 
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.api.impl.ServiceWrapper;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.compiler.JFXCompiler;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.EditorFactory;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.Editor;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.EditorRegistry;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceLanguage;
/*     */ import com.dukascopy.dds2.greed.connector.IConverter;
/*     */ import com.dukascopy.dds2.greed.connector.helpers.ConverterHelpers;
/*     */ import com.dukascopy.dds2.greed.console.PlatformConsoleImpl;
/*     */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.ServiceSourceEditorPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JOptionPane;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class CompilerUtils
/*     */   implements ICompilerUtils
/*     */ {
/*  38 */   private static CompilerUtils instance = null;
/*  39 */   private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyConverter.class);
/*  40 */   private IChartTabsAndFramesController chartTabsController = ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getChartTabsController();
/*     */ 
/*     */   public static CompilerUtils getInstance()
/*     */   {
/*  46 */     if (instance == null) {
/*  47 */       instance = new CompilerUtils();
/*     */     }
/*  49 */     return instance;
/*     */   }
/*     */ 
/*     */   public boolean runCompilation(ServiceWrapper serviceWrapper)
/*     */   {
/*  54 */     boolean isSuccessfulCompile = false;
/*  55 */     PlatformConsoleImpl platformConsoleImpl = (PlatformConsoleImpl)GreedContext.get("platformConsole");
/*  56 */     ServiceSourceEditorPanel panel = this.chartTabsController.getEditorPanel(serviceWrapper);
/*     */ 
/*  58 */     if (panel != null) {
/*  59 */       if (panel.getSourceLanguage() == ServiceSourceLanguage.JAVA) {
/*  60 */         isSuccessfulCompile = JFXCompiler.compile(panel.getEditor().getFile(), platformConsoleImpl);
/*     */       }
/*     */       else {
/*  63 */         if (!panel.translate()) {
/*  64 */           return false;
/*     */         }
/*  66 */         File file = new File(new StringBuilder().append(ServiceSourceEditorPanel.cutFileName(panel.getEditor().getFile().getAbsolutePath())).append(".java").toString());
/*     */         try {
/*  68 */           panel.getJavaEditor().saveAs(file);
/*     */         } catch (IOException e) {
/*  70 */           LOGGER.error(e.getMessage(), e);
/*  71 */           JOptionPane.showMessageDialog((JFrame)GreedContext.get("clientGui"), new StringBuilder().append(LocalizationManager.getText("joption.pane.cannot.save")).append(e.getMessage()).toString(), LocalizationManager.getText("joption.pane.error"), 0);
/*     */         }
/*     */ 
/*  74 */         isSuccessfulCompile = JFXCompiler.compile(panel.getJavaEditor().getFile(), platformConsoleImpl);
/*  75 */         panel.getJavaEditor().getFile().delete();
/*  76 */         EditorFactory.getRegistry().removeReference(panel.getPanelId());
/*     */       }
/*     */     }
/*  79 */     else if (!serviceWrapper.getSourceFile().getName().endsWith("java")) {
/*  80 */       IConverter converter = getConverter(serviceWrapper.getSourceFile().getName().substring(serviceWrapper.getSourceFile().getName().lastIndexOf(".") + 1));
/*  81 */       File newJavaSource = null;
/*     */       try {
/*  83 */         serviceWrapper.setSourceFile(ServiceSourceEditorPanel.adaptToJavaClassFileName(serviceWrapper.getSourceFile()));
/*  84 */         ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceJTree().fireStrategyListChanged();
/*     */ 
/*  86 */         converter.convert(serviceWrapper.getSourceFile());
/*  87 */         String path = new StringBuilder().append(serviceWrapper.getSourceFile().getAbsolutePath().substring(0, serviceWrapper.getSourceFile().getAbsolutePath().lastIndexOf("."))).append(".java").toString();
/*     */ 
/*  89 */         newJavaSource = new File(path);
/*  90 */         populateFile(newJavaSource, converter.getConvertionResult());
/*     */ 
/*  92 */         isSuccessfulCompile = JFXCompiler.compile(newJavaSource, platformConsoleImpl);
/*     */ 
/*  94 */         if (newJavaSource.canExecute())
/*  95 */           newJavaSource.delete();
/*     */       }
/*     */       catch (JFException e) {
/*  98 */         NotificationUtils.getInstance().postErrorMessage(new StringBuilder().append(e.getMessage()).append(", ").append(e.getCause().getCause().getMessage()).toString());
/*     */ 
/* 100 */         if ((newJavaSource != null) && (newJavaSource.canExecute())) newJavaSource.delete();
/* 101 */         return isSuccessfulCompile = 0;
/*     */       }
/*     */     } else {
/* 104 */       isSuccessfulCompile = JFXCompiler.compile(serviceWrapper.getSourceFile(), platformConsoleImpl);
/*     */     }
/*     */ 
/* 107 */     return isSuccessfulCompile;
/*     */   }
/*     */ 
/*     */   public ServiceWrapper runTesterCompilation(ServiceWrapper serviceWrapper)
/*     */   {
/* 112 */     ServiceWrapper resultWrapper = serviceWrapper;
/* 113 */     ServiceSourceEditorPanel serviceSourceEditorPanel = this.chartTabsController.getEditorPanel(resultWrapper);
/* 114 */     if ((serviceSourceEditorPanel != null) && 
/* 115 */       (serviceSourceEditorPanel.getEditor().contentWasModified()))
/*     */     {
/* 117 */       serviceSourceEditorPanel.save();
/*     */     }
/*     */ 
/* 121 */     File sourceFile = resultWrapper.getSourceFile();
/* 122 */     File binaryFile = resultWrapper.getBinaryFile();
/*     */ 
/* 124 */     if (((sourceFile != null) && (binaryFile != null) && (sourceFile.lastModified() > binaryFile.lastModified())) || ((sourceFile != null) && (binaryFile == null)))
/*     */     {
/* 126 */       boolean isJavaSource = resultWrapper.getSourceFile().getAbsolutePath().endsWith("java");
/*     */ 
/* 128 */       File javaSource = null;
/* 129 */       if (isJavaSource) {
/* 130 */         javaSource = sourceFile;
/*     */       } else {
/* 132 */         IConverter converter = getConverter(serviceWrapper.getSourceFile().getName().substring(serviceWrapper.getSourceFile().getName().lastIndexOf(".") + 1));
/*     */         try {
/* 134 */           serviceWrapper.setSourceFile(ServiceSourceEditorPanel.adaptToJavaClassFileName(serviceWrapper.getSourceFile()));
/* 135 */           ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceJTree().fireStrategyListChanged();
/*     */ 
/* 137 */           converter.convert(serviceWrapper.getSourceFile());
/* 138 */           String path = new StringBuilder().append(serviceWrapper.getSourceFile().getAbsolutePath().substring(0, serviceWrapper.getSourceFile().getAbsolutePath().lastIndexOf("."))).append(".java").toString();
/*     */ 
/* 140 */           javaSource = new File(path);
/* 141 */           populateFile(javaSource, converter.getConvertionResult());
/*     */         }
/*     */         catch (JFException e) {
/* 144 */           NotificationUtils.getInstance().postErrorMessage(new StringBuilder().append(e.getMessage()).append(", ").append(e.getCause().getCause().getMessage()).toString());
/* 145 */           return null;
/*     */         }
/*     */       }
/*     */ 
/* 149 */       PlatformConsoleImpl platformConsoleImpl = (PlatformConsoleImpl)GreedContext.get("platformConsole");
/* 150 */       if (!JFXCompiler.compile(javaSource, platformConsoleImpl)) {
/* 151 */         JOptionPane.showMessageDialog((JFrame)GreedContext.get("clientGui"), LocalizationManager.getText("joption.pane.cannot.compile.strategy"), LocalizationManager.getText("joption.pane.comunication.error"), 1);
/* 152 */         if ((!isJavaSource) && (javaSource.canExecute())) {
/* 153 */           javaSource.delete();
/*     */         }
/* 155 */         return null;
/*     */       }
/*     */ 
/* 158 */       binaryFile = resultWrapper.getBinaryFile();
/* 159 */       if (binaryFile == null) {
/* 160 */         JOptionPane.showMessageDialog((JFrame)GreedContext.get("clientGui"), LocalizationManager.getText("joption.pane.cannot.compile.strategy"), LocalizationManager.getText("joption.pane.comunication.error"), 1);
/* 161 */         if ((!isJavaSource) && (javaSource.canExecute())) {
/* 162 */           javaSource.delete();
/*     */         }
/* 164 */         return null;
/*     */       }
/*     */ 
/* 167 */       if ((!isJavaSource) && (javaSource.canExecute())) {
/* 168 */         javaSource.delete();
/*     */       }
/*     */     }
/* 171 */     return resultWrapper;
/*     */   }
/*     */ 
/*     */   private static IConverter getConverter(String suffix) {
/* 175 */     IConverter converter = null;
/* 176 */     if (suffix.equals("mq4"))
/* 177 */       converter = ConverterHelpers.getMT4Converter();
/* 178 */     else if ((suffix.equals("mq5")) || (suffix.equals("cpp")) || (suffix.equals("hpp")) || (suffix.equals("c")) || (suffix.equals("h")))
/*     */     {
/* 180 */       converter = ConverterHelpers.getMT5Converter();
/* 181 */     }return converter;
/*     */   }
/*     */ 
/*     */   private static File populateFile(File file, StringBuilder content) throws JFException {
/* 185 */     File popFile = file;
/* 186 */     StringBuilder fileContent = content;
/*     */     try
/*     */     {
/* 189 */       FileWriter to = new FileWriter(popFile);
/* 190 */       to.write(fileContent.toString());
/* 191 */       to.close();
/*     */     } catch (UnsupportedEncodingException e) {
/* 193 */       LOGGER.error(e.getMessage(), e);
/* 194 */       throw new JFException(e.getMessage());
/*     */     } catch (IOException e) {
/* 196 */       LOGGER.error(e.getMessage(), e);
/* 197 */       throw new JFException(e.getMessage());
/*     */     }
/* 199 */     return popFile;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.CompilerUtils
 * JD-Core Version:    0.6.0
 */