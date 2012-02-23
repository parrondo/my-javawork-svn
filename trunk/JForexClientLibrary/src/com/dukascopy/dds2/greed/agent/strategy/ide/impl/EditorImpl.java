/*     */ package com.dukascopy.dds2.greed.agent.strategy.ide.impl;
/*     */ 
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.ICurvesProtocolHandler;
/*     */ import com.dukascopy.dds2.greed.agent.compiler.JFXCompiler;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.Editor;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.Editor.Action;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.EditorRegistry;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.FileChangeListener;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceLanguage;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceType;
/*     */ import com.dukascopy.dds2.greed.gui.component.filechooser.ChooserSelectionWrapper;
/*     */ import com.dukascopy.dds2.greed.gui.component.filechooser.FileProgressListener;
/*     */ import com.dukascopy.transport.common.datafeed.FileType;
/*     */ import com.dukascopy.transport.common.datafeed.Location;
/*     */ import com.dukascopy.transport.common.msg.strategy.FileItem;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.FocusListener;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import javax.swing.ActionMap;
/*     */ import javax.swing.InputMap;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.event.CaretListener;
/*     */ import javax.swing.text.BadLocationException;
/*     */ import javax.swing.text.Document;
/*     */ import javax.swing.text.TextAction;
/*     */ import javax.swing.undo.UndoManager;
/*     */ import org.fife.rsta.ac.LanguageSupportFactory;
/*     */ import org.fife.rsta.ac.java.JarInfo;
/*     */ import org.fife.rsta.ac.java.JavaCompletionProvider;
/*     */ import org.fife.rsta.ac.java.JavaLanguageSupport;
/*     */ import org.fife.ui.autocomplete.BasicCompletion;
/*     */ import org.fife.ui.autocomplete.DefaultCompletionProvider;
/*     */ import org.fife.ui.rsyntaxtextarea.CodeTemplateManager;
/*     */ import org.fife.ui.rsyntaxtextarea.CompileErrorParser;
/*     */ import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
/*     */ import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
/*     */ import org.fife.ui.rsyntaxtextarea.templates.CodeTemplate;
/*     */ import org.fife.ui.rsyntaxtextarea.templates.StaticCodeTemplate;
/*     */ import org.fife.ui.rtextarea.Gutter;
/*     */ import org.fife.ui.rtextarea.IconGroup;
/*     */ import org.fife.ui.rtextarea.RTextScrollPane;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class EditorImpl
/*     */   implements Editor
/*     */ {
/*  59 */   private static final Logger LOGGER = LoggerFactory.getLogger(EditorImpl.class);
/*  60 */   private static final Dimension ZERO_SIZE = new Dimension(0, 0);
/*     */   private String syntaxStyle;
/*     */   RSyntaxTextArea area;
/*     */   RTextScrollPane scrollPane;
/*     */   EditorFileHandler editorFileHandler;
/*     */   EditorDocumentListener editorDocumentListener;
/*     */   EditorDialogManager editorDialogManager;
/*     */   UndoManager undoManager;
/*     */   ServiceSourceType sourceType;
/*     */   String clientMode;
/*     */   EditorRegistry registry;
/*     */   JavaLanguageSupport jls;
/*  77 */   List<FileChangeListener> fileChangeListeners = new LinkedList();
/*     */   private EditorFocusListener editorFocusListener;
/*  79 */   private static final Color SELECTION_COLOR = new Color(184, 207, 229);
/*     */   private CompileErrorParser compileErrorParser;
/*     */ 
/*     */   EditorImpl(EditorRegistry registry, EditorStatusBar statusBar, ActionListener actionListener, ServiceSourceType serviceSourceType, String clientMode, String syntaxStyle)
/*     */   {
/*  84 */     this(registry, statusBar, actionListener, serviceSourceType, null, clientMode, syntaxStyle);
/*     */   }
/*     */ 
/*     */   EditorImpl(EditorRegistry registry, EditorStatusBar statusBar, ActionListener actionListener, ServiceSourceType serviceSourceType, FocusListener focusListener, String clientMode, String syntaxStyle)
/*     */   {
/*  89 */     this.registry = registry;
/*     */ 
/*  91 */     this.area = new RSyntaxTextArea();
/*     */ 
/*  93 */     this.area.setMinimumSize(ZERO_SIZE);
/*  94 */     this.undoManager = new UndoManager();
/*     */ 
/*  96 */     this.clientMode = clientMode;
/*  97 */     this.sourceType = serviceSourceType;
/*  98 */     this.editorFileHandler = new EditorFileHandler();
/*  99 */     this.editorDocumentListener = new EditorDocumentListener();
/* 100 */     this.editorDialogManager = new EditorDialogManager(statusBar, this.area, serviceSourceType);
/* 101 */     this.editorFocusListener = new EditorFocusListener(this.area, this.editorFileHandler, this.editorDialogManager);
/*     */ 
/* 103 */     this.area.setMarkOccurrences(true);
/* 104 */     this.area.setFont(new Font("Monospaced", 0, 12));
/* 105 */     this.area.setAntiAliasingEnabled(true);
/*     */ 
/* 108 */     this.area.getDocument().addDocumentListener(this.editorDocumentListener);
/* 109 */     this.area.getDocument().addDocumentListener(statusBar);
/*     */ 
/* 112 */     IconGroup iconGroup = new IconGroup("Icons", "img");
/* 113 */     RSyntaxTextArea.setIconGroup(iconGroup);
/*     */ 
/* 115 */     this.area.setClearWhitespaceLinesEnabled(false);
/*     */ 
/* 118 */     this.area.setTabsEmulated(true);
/*     */ 
/* 120 */     this.area.setTabSize(4);
/*     */ 
/* 122 */     this.area.addFocusListener(this.editorFocusListener);
/* 123 */     if (focusListener != null) {
/* 124 */       this.area.addFocusListener(focusListener);
/*     */     }
/* 126 */     this.area.addCaretListener(statusBar);
/* 127 */     this.area.setSelectionColor(SELECTION_COLOR);
/*     */ 
/* 131 */     RSyntaxTextArea.setTemplatesEnabled(true);
/*     */ 
/* 137 */     CodeTemplateManager ctm = RSyntaxTextArea.getCodeTemplateManager();
/*     */ 
/* 142 */     CodeTemplate ct = new StaticCodeTemplate("sout", "System.out.println(", ")");
/* 143 */     ctm.addTemplate(ct);
/*     */ 
/* 147 */     ct = new StaticCodeTemplate("for", "for (int i=0; i<", "; i++) {\n\t\n}\n");
/* 148 */     ctm.addTemplate(ct);
/*     */ 
/* 150 */     ct = new StaticCodeTemplate("if", "if(", "){\n\t\n}else{\n\t\n}");
/* 151 */     ctm.addTemplate(ct);
/*     */ 
/* 155 */     this.area.setSyntaxEditingStyle(syntaxStyle);
/*     */ 
/* 160 */     this.compileErrorParser = new CompileErrorParser(JFXCompiler.getInstance(), (RSyntaxDocument)this.area.getDocument(), this.area.getParserManager());
/* 161 */     if (syntaxStyle.equals("text/java")) {
/* 162 */       this.area.addParser(this.compileErrorParser);
/*     */     }
/*     */ 
/* 167 */     this.area.setCodeFoldingEnabled(true);
/*     */ 
/* 172 */     File file = new File(JFXCompiler.prepareClasspath(IStrategy.class, "dds2-agent.jar"));
/* 173 */     File source = new File("resources/JForex-API");
/* 174 */     JarInfo jarInfo = new JarInfo(file);
/* 175 */     jarInfo.setSourceLocation(source);
/* 176 */     this.area.addJar(jarInfo);
/*     */ 
/* 178 */     this.jls = ((JavaLanguageSupport)LanguageSupportFactory.get().getSupportFor("text/java"));
/* 179 */     this.jls.install(this.area);
/*     */ 
/* 181 */     JavaCompletionProvider jcp = this.jls.getCompletionProvider(this.area);
/*     */ 
/* 184 */     DefaultCompletionProvider sourceProvider = (DefaultCompletionProvider)jcp.getDefaultCompletionProvider();
/*     */ 
/* 186 */     List completionsToAdd = new LinkedList();
/* 187 */     completionsToAdd.add(new BasicCompletion(sourceProvider, "while"));
/* 188 */     sourceProvider.addCompletions(completionsToAdd);
/*     */ 
/* 190 */     if (!syntaxStyle.equals("text/java")) {
/* 191 */       this.jls.uninstall(this.area);
/*     */     }
/*     */ 
/* 194 */     initInputMapForTextArea();
/* 195 */     initActionMapForTextArea(actionListener);
/*     */ 
/* 197 */     this.scrollPane = new RTextScrollPane(this.area, true);
/* 198 */     this.scrollPane.setMinimumSize(ZERO_SIZE);
/* 199 */     this.scrollPane.getGutter().setBookmarkIcon(StratUtils.loadIcon("resources/icons/bookmark.png"));
/* 200 */     this.scrollPane.getGutter().setBookmarkingEnabled(true);
/*     */   }
/*     */ 
/*     */   void initActionMapForTextArea(ActionListener compileActionListener)
/*     */   {
/* 207 */     this.area.getActionMap().put(Editor.Action.REDO, new TextAction(Editor.Action.REDO.name()) {
/*     */       public void actionPerformed(ActionEvent e) {
/* 209 */         if (!EditorImpl.this.undoManager.canRedo()) {
/* 210 */           return;
/*     */         }
/* 212 */         EditorImpl.this.undoManager.redo();
/*     */       }
/*     */     });
/* 215 */     this.area.getActionMap().put(Editor.Action.UNDO, new TextAction(Editor.Action.UNDO.name()) {
/*     */       public void actionPerformed(ActionEvent e) {
/* 217 */         if (!EditorImpl.this.undoManager.canUndo()) {
/* 218 */           return;
/*     */         }
/* 220 */         EditorImpl.this.undoManager.undo();
/*     */       }
/*     */     });
/* 223 */     this.area.getActionMap().put(Editor.Action.FIND, new TextAction(Editor.Action.FIND.name()) {
/*     */       public void actionPerformed(ActionEvent e) {
/* 225 */         EditorImpl.this.editorDialogManager.showFind();
/*     */       }
/*     */     });
/* 228 */     this.area.getActionMap().put(Editor.Action.REPLACE, new TextAction(Editor.Action.REPLACE.name()) {
/*     */       public void actionPerformed(ActionEvent e) {
/* 230 */         EditorImpl.this.editorDialogManager.showReplace();
/*     */       }
/*     */     });
/* 233 */     this.area.getActionMap().put(Editor.Action.SAVE, new TextAction(Editor.Action.SAVE.name(), compileActionListener) {
/*     */       public void actionPerformed(ActionEvent actionEvent) {
/* 235 */         this.val$compileActionListener.actionPerformed(new ActionEvent(actionEvent.getSource(), actionEvent.getID(), Editor.Action.SAVE.name()));
/*     */       }
/*     */     });
/* 238 */     this.area.getActionMap().put(Editor.Action.COMPILE, new TextAction(Editor.Action.COMPILE.name(), compileActionListener) {
/*     */       public void actionPerformed(ActionEvent actionEvent) {
/* 240 */         this.val$compileActionListener.actionPerformed(new ActionEvent(actionEvent.getSource(), actionEvent.getID(), Editor.Action.COMPILE.name()));
/*     */       }
/*     */     });
/* 243 */     this.area.getActionMap().put(Editor.Action.HELP, new TextAction(Editor.Action.HELP.name(), compileActionListener) {
/*     */       public void actionPerformed(ActionEvent actionEvent) {
/* 245 */         this.val$compileActionListener.actionPerformed(new ActionEvent(actionEvent.getSource(), actionEvent.getID(), Editor.Action.HELP.name()));
/*     */       } } );
/*     */   }
/*     */ 
/*     */   void initInputMapForTextArea() {
/* 251 */     for (Editor.Action action : Editor.Action.values())
/* 252 */       this.area.getInputMap().put(action.getKeyStroke(), action);
/*     */   }
/*     */ 
/*     */   public void selectLine(int lineNumber)
/*     */   {
/* 257 */     this.area.requestFocus();
/*     */ 
/* 259 */     lineNumber--; lineNumber = lineNumber > 0 ? lineNumber : lineNumber;
/*     */ 
/* 261 */     int start = -1;
/*     */     try
/*     */     {
/* 265 */       start = this.area.getLineStartOffset(lineNumber);
/*     */     }
/*     */     catch (BadLocationException e) {
/* 268 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */ 
/* 271 */     this.area.setCaretPosition(start);
/*     */   }
/*     */ 
/*     */   public void focus()
/*     */   {
/* 276 */     this.area.requestFocusInWindow();
/*     */   }
/*     */ 
/*     */   public void open(File file) {
/* 280 */     String content = this.editorFileHandler.readFromFile(file);
/* 281 */     this.area.setText(content);
/* 282 */     this.area.getDocument().addUndoableEditListener(new EditorUndoableEditListener(this.undoManager));
/* 283 */     this.editorDocumentListener.reset();
/* 284 */     this.area.setCaretPosition(0);
/*     */   }
/*     */ 
/*     */   public boolean save() throws IOException
/*     */   {
/* 289 */     if (this.editorFileHandler.contentWasModified(this.area.getText())) {
/* 290 */       this.editorFileHandler.writeToFile(this.area.getText());
/* 291 */       this.editorDocumentListener.reset();
/* 292 */       this.editorFocusListener.reset();
/*     */     }
/* 294 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean saveAs(Component parent, String fileName, ServiceSourceLanguage serviceSourceLanguage) throws IOException
/*     */   {
/* 299 */     FileType ft = this.sourceType == ServiceSourceType.INDICATOR ? FileType.INDICATOR : FileType.STRATEGY;
/*     */ 
/* 301 */     String extension = ".java";
/*     */ 
/* 303 */     if (serviceSourceLanguage == ServiceSourceLanguage.MQ4)
/* 304 */       extension = ".mq4";
/* 305 */     else if (serviceSourceLanguage == ServiceSourceLanguage.MQ5) {
/* 306 */       extension = ".mq5";
/*     */     }
/*     */ 
/* 309 */     ChooserSelectionWrapper selection = this.editorDialogManager.showSaveAsDialog(fileName, serviceSourceLanguage, ft, this.clientMode);
/*     */ 
/* 311 */     String newPath = "";
/*     */ 
/* 313 */     if (selection != null)
/*     */     {
/* 315 */       Location location = selection.getLocation();
/* 316 */       FileItem fileItem = selection.getFileItem();
/*     */ 
/* 318 */       if (location == Location.LOCAL)
/*     */       {
/* 320 */         File selectedFile = new File(fileItem.getFileName());
/*     */ 
/* 322 */         if (((selectedFile.getName().endsWith(".java")) && (serviceSourceLanguage == ServiceSourceLanguage.JAVA)) || ((selectedFile.getName().endsWith(".mq4")) && (serviceSourceLanguage == ServiceSourceLanguage.MQ4)) || ((selectedFile.getName().endsWith(".mq5")) && (serviceSourceLanguage == ServiceSourceLanguage.MQ5)))
/* 323 */           newPath = fileItem.getFileName();
/*     */         else {
/* 325 */           newPath = selectedFile.getAbsolutePath() + extension;
/*     */         }
/*     */ 
/* 328 */         String oldPath = this.editorFileHandler.getFile().getAbsolutePath();
/*     */ 
/* 330 */         this.editorFileHandler.writeToFile(new File(newPath), this.area.getText());
/*     */ 
/* 334 */         this.editorFocusListener.reset();
/* 335 */         this.editorDocumentListener.reset();
/*     */ 
/* 337 */         for (FileChangeListener fileChangeListener : this.fileChangeListeners) {
/* 338 */           fileChangeListener.fileChanged(new File(newPath));
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/*     */         try
/*     */         {
/* 345 */           fileItem.setFileData(this.area.getText().getBytes("UTF-8"));
/*     */ 
/* 347 */           FeedDataProvider.getCurvesProtocolHandler().uploadFile(fileItem, this.clientMode, new FileProgressListener());
/*     */         }
/*     */         catch (Exception e) {
/* 350 */           LOGGER.error("Error saving strategy in remote storage: " + fileItem.getFileName(), e);
/*     */         }
/*     */       }
/*     */ 
/* 354 */       return true;
/*     */     }
/*     */ 
/* 357 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean saveAs(File file)
/*     */     throws IOException
/*     */   {
/* 363 */     if (file == null) {
/* 364 */       return false;
/*     */     }
/*     */ 
/* 367 */     String oldPath = this.editorFileHandler.getFile().getAbsolutePath();
/*     */ 
/* 369 */     this.editorFileHandler.writeToFile(file, this.area.getText());
/* 370 */     String newPath = file.getAbsolutePath();
/*     */ 
/* 374 */     this.editorFocusListener.reset();
/* 375 */     this.editorDocumentListener.reset();
/*     */ 
/* 377 */     for (FileChangeListener fileChangeListener : this.fileChangeListeners) {
/* 378 */       fileChangeListener.fileChanged(file);
/*     */     }
/*     */ 
/* 381 */     return true;
/*     */   }
/*     */ 
/*     */   public void find() {
/* 385 */     this.editorDialogManager.showFind();
/*     */   }
/*     */ 
/*     */   public void replace() {
/* 389 */     this.editorDialogManager.showReplace();
/*     */   }
/*     */ 
/*     */   public boolean contentWasModified() {
/* 393 */     return this.editorFileHandler.contentWasModified(this.area.getText());
/*     */   }
/*     */ 
/*     */   public boolean close()
/*     */   {
/* 400 */     if ((this.editorDocumentListener.fileIsInModifiedState()) && 
/* 401 */       (this.editorFileHandler.contentWasModified(this.area.getText()))) {
/* 402 */       this.editorFileHandler.writeToFile(this.area.getText());
/*     */     }
/*     */ 
/* 406 */     return true;
/*     */   }
/*     */ 
/*     */   public File getFile() {
/* 410 */     return this.editorFileHandler.getFile();
/*     */   }
/*     */ 
/*     */   public boolean isFileModified() {
/* 414 */     return this.editorDocumentListener.fileIsInModifiedState();
/*     */   }
/*     */ 
/*     */   public void addFileChangeListener(FileChangeListener fileChangeListener) {
/* 418 */     this.fileChangeListeners.add(fileChangeListener);
/*     */   }
/*     */ 
/*     */   public boolean addCaretListener(CaretListener caretListener) {
/* 422 */     if (this.area != null) {
/* 423 */       this.area.addCaretListener(caretListener);
/*     */ 
/* 425 */       return true;
/*     */     }
/*     */ 
/* 428 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean removeCaretListener(CaretListener caretListener) {
/* 432 */     if (this.area != null) {
/* 433 */       this.area.removeCaretListener(caretListener);
/* 434 */       return true;
/*     */     }
/* 436 */     return false;
/*     */   }
/*     */ 
/*     */   public JComponent getGUIComponent() {
/* 440 */     return this.scrollPane;
/*     */   }
/*     */ 
/*     */   public void setSyntaxStyle(String syntaxStyle) {
/* 444 */     if (!this.area.getSyntaxEditingStyle().equals(syntaxStyle)) {
/* 445 */       if (syntaxStyle.equals("text/java")) {
/* 446 */         this.jls.install(this.area);
/* 447 */         this.area.addParser(this.compileErrorParser);
/*     */       } else {
/* 449 */         this.jls.uninstall(this.area);
/* 450 */         this.area.removeParser(this.compileErrorParser);
/*     */       }
/* 452 */       this.area.setSyntaxEditingStyle(syntaxStyle);
/* 453 */       this.syntaxStyle = syntaxStyle;
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getSyntaxStyle() {
/* 458 */     return this.syntaxStyle;
/*     */   }
/*     */ 
/*     */   public void setContent(String content) {
/* 462 */     if (this.area != null) {
/* 463 */       this.area.setText(content);
/* 464 */       this.area.getDocument().addUndoableEditListener(new EditorUndoableEditListener(this.undoManager));
/* 465 */       this.editorDocumentListener.reset();
/* 466 */       this.area.setCaretPosition(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getContent() {
/* 471 */     if (this.area != null) {
/* 472 */       return this.area.getText();
/*     */     }
/* 474 */     return "";
/*     */   }
/*     */ 
/*     */   public boolean isJavaSource() {
/* 478 */     if (this.area != null) {
/* 479 */       return this.area.getText().contains("class");
/*     */     }
/* 481 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isMQLSource() {
/* 485 */     if (this.area != null) {
/* 486 */       return (!isJavaSource()) && (this.area.getText().contains("start("));
/*     */     }
/* 488 */     return false;
/*     */   }
/*     */ 
/*     */   public void setEditable(boolean isEditable) {
/* 492 */     if (this.area != null)
/* 493 */       this.area.setEditable(isEditable);
/*     */   }
/*     */ 
/*     */   public boolean isEditable() {
/* 497 */     if (this.area != null) {
/* 498 */       return this.area.isEditable();
/*     */     }
/* 500 */     return false;
/*     */   }
/*     */ 
/*     */   public void reloadEditor(File oldFile, File newFile)
/*     */   {
/* 505 */     this.editorFileHandler.readFromFile(newFile);
/* 506 */     this.editorFocusListener.reset();
/* 507 */     this.editorDocumentListener.reset();
/*     */   }
/*     */ 
/*     */   public void organizeImports() {
/* 511 */     this.area.organizeImports();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.ide.impl.EditorImpl
 * JD-Core Version:    0.6.0
 */