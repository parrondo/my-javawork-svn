/*     */ package com.dukascopy.dds2.greed.gui.component.filechooser;
/*     */ 
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.ICurvesProtocolHandler;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.resizing.components.ResizableIcon;
/*     */ import com.dukascopy.transport.common.datafeed.FileType;
/*     */ import com.dukascopy.transport.common.datafeed.KeyNotFoundException;
/*     */ import com.dukascopy.transport.common.datafeed.Location;
/*     */ import com.dukascopy.transport.common.datafeed.StorageException;
/*     */ import com.dukascopy.transport.common.msg.strategy.FileItem;
/*     */ import com.dukascopy.transport.common.msg.strategy.FileItem.AccessType;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.FocusAdapter;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.UUID;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.JToggleButton;
/*     */ import javax.swing.ListSelectionModel;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
/*     */ import javax.swing.table.TableModel;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class TransportFileChooser extends JDialog
/*     */ {
/*  59 */   private static final Logger LOGGER = LoggerFactory.getLogger(TransportFileChooser.class);
/*     */   private static final long serialVersionUID = 6164173020736353853L;
/*  63 */   private static final Icon PROGRESS_IMAGE = new ResizableIcon("titlebar_icon_loading.gif");
/*  64 */   private static final Icon REFRESH_IMAGE = new ResizableIcon("toolbar_table_refresh.png");
/*  65 */   private static final Icon PARAMS_ICON = new ResizableIcon("transport_file_choser_params.png");
/*     */   private static final int SAVE = 1;
/*     */   private static final int OPEN = 0;
/*     */   private FileType fileType;
/*     */   private int opType;
/*  74 */   ChooserSelectionWrapper wrapper = null;
/*     */   private JTextField fileNameTF;
/*     */   private JTextField descriptionTF;
/*     */   private JTextField keyTF;
/*     */   private JButton findKeyBtn;
/*     */   private JPanel keyPanel;
/*     */   private JPanel controlPanel;
/*     */   private Component parentComponent;
/*     */   private MyPanel currentComponent;
/*     */   private Location currentLocation;
/*     */   private JTable currentTable;
/*     */   private JButtonBar toolbar;
/*  95 */   private Map<Location, JTable> tableMap = new HashMap();
/*     */   private FileProgressListener progressListener;
/*     */   private JFileChooser localChooser;
/*     */   private JPanel leftPanel;
/*     */   private ButtonGroup group;
/*     */   private Boolean multipleSelection;
/*     */   private JButton okBtn;
/*     */   private String clientType;
/*     */ 
/*     */   private TransportFileChooser(FileType fType, int opType, JFileChooser chooser, Component parent, String fileName, Boolean multipleSelection)
/*     */   {
/* 113 */     super(JOptionPane.getFrameForComponent(parent));
/*     */ 
/* 115 */     this.progressListener = new FileProgressListener();
/*     */ 
/* 117 */     this.multipleSelection = multipleSelection;
/*     */ 
/* 119 */     this.parentComponent = parent;
/*     */ 
/* 121 */     this.opType = opType;
/* 122 */     this.fileType = fType;
/*     */ 
/* 124 */     this.localChooser = chooser;
/*     */ 
/* 126 */     this.leftPanel = new JPanel(new BorderLayout());
/*     */ 
/* 128 */     setDefaultCloseOperation(2);
/*     */ 
/* 130 */     addWindowListener(new WindowAdapter()
/*     */     {
/*     */       public void windowClosed(WindowEvent e)
/*     */       {
/* 134 */         TransportFileChooser.this.stopBackgroundOperations();
/*     */       }
/*     */     });
/* 139 */     getContentPane().setLayout(new BorderLayout());
/*     */ 
/* 141 */     String opTypeLbl = "";
/*     */ 
/* 143 */     if (opType == 0) {
/* 144 */       opTypeLbl = LocalizationManager.getText("open.button.text");
/* 145 */       chooser.setDialogType(0);
/* 146 */     } else if (opType == 1) {
/* 147 */       opTypeLbl = LocalizationManager.getText("save.button.text");
/* 148 */       chooser.setDialogType(1);
/*     */     }
/*     */ 
/* 151 */     chooser.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 155 */         if (e.getActionCommand().equals("ApproveSelection")) {
/* 156 */           TransportFileChooser.this.approve();
/*     */         }
/*     */ 
/* 159 */         if (e.getActionCommand().equals("CancelSelection"))
/* 160 */           TransportFileChooser.this.cancel();
/*     */       }
/*     */     });
/* 165 */     setTitle(opTypeLbl);
/* 166 */     setModal(true);
/*     */ 
/* 168 */     addWindowListener(new WindowAdapter() {
/*     */       public void windowClosing(WindowEvent e) {
/* 170 */         TransportFileChooser.this.cancel();
/*     */       }
/*     */     });
/* 174 */     this.okBtn = new JButton(LocalizationManager.getText("button.ok"));
/* 175 */     this.okBtn.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 178 */         TransportFileChooser.this.approve();
/*     */       }
/*     */     });
/* 182 */     JButton cancel = new JButton(LocalizationManager.getText("button.cancel"));
/* 183 */     cancel.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 186 */         TransportFileChooser.this.cancel();
/*     */       }
/*     */     });
/* 190 */     this.fileNameTF = new JTextField();
/*     */ 
/* 192 */     this.fileNameTF.setText(fileName);
/*     */ 
/* 194 */     if (chooser.getSelectedFile() != null) {
/* 195 */       this.fileNameTF.setText(chooser.getSelectedFile().getName());
/*     */     }
/* 197 */     this.fileNameTF.addFocusListener(new FocusAdapter()
/*     */     {
/*     */       public void focusGained(FocusEvent e)
/*     */       {
/* 201 */         TransportFileChooser.this.highlightText(TransportFileChooser.this.fileNameTF);
/*     */       }
/*     */     });
/* 206 */     this.descriptionTF = new JTextField();
/*     */ 
/* 208 */     this.keyTF = new JTextField();
/* 209 */     this.keyTF.addFocusListener(new FocusAdapter()
/*     */     {
/*     */       public void focusGained(FocusEvent e)
/*     */       {
/* 213 */         TransportFileChooser.this.highlightText(TransportFileChooser.this.keyTF);
/*     */       }
/*     */     });
/* 218 */     this.keyPanel = new JPanel(new BorderLayout(5, 0));
/* 219 */     this.keyPanel.add(this.keyTF, "Center");
/*     */ 
/* 221 */     this.findKeyBtn = new JButton(LocalizationManager.getText("button.search"));
/* 222 */     this.findKeyBtn.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 226 */         TransportFileChooser.this.useKey();
/*     */       }
/*     */     });
/* 230 */     this.keyPanel.add(this.findKeyBtn, "East");
/*     */ 
/* 232 */     JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 3, 3));
/* 233 */     buttonPanel.add(this.okBtn);
/* 234 */     buttonPanel.add(cancel);
/*     */ 
/* 236 */     JPanel inputPanel = new JPanel(new GridBagLayout());
/* 237 */     int y = 0;
/* 238 */     inputPanel = new JPanel(new GridBagLayout());
/* 239 */     inputPanel.add(new JLabel(LocalizationManager.getText("chooser.file.name") + ":"), new GridBagConstraints(0, y, 1, 1, 0.0D, 0.0D, 17, 2, new Insets(3, 3, 3, 3), 0, 0));
/* 240 */     inputPanel.add(this.fileNameTF, new GridBagConstraints(1, y++, 2, 1, 1.0D, 0.0D, 10, 2, new Insets(3, 3, 3, 3), 0, 0));
/*     */ 
/* 242 */     inputPanel.add(new JLabel(LocalizationManager.getText("chooser.key") + ":"), new GridBagConstraints(0, y, 1, 1, 0.0D, 0.0D, 17, 2, new Insets(3, 3, 3, 3), 0, 0));
/* 243 */     inputPanel.add(this.keyPanel, new GridBagConstraints(1, y++, 2, 1, 1.0D, 0.0D, 10, 2, new Insets(3, 3, 3, 3), 0, 0));
/*     */ 
/* 245 */     inputPanel.add(new JLabel(LocalizationManager.getText("chooser.description") + ":"), new GridBagConstraints(0, y, 1, 1, 0.0D, 0.0D, 17, 2, new Insets(3, 3, 3, 3), 0, 0));
/* 246 */     inputPanel.add(this.descriptionTF, new GridBagConstraints(1, y++, 2, 1, 1.0D, 0.0D, 10, 2, new Insets(3, 3, 3, 3), 0, 0));
/*     */ 
/* 248 */     this.controlPanel = new JPanel(new BorderLayout());
/* 249 */     this.controlPanel.add(inputPanel, "Center");
/* 250 */     this.controlPanel.add(buttonPanel, "East");
/*     */ 
/* 252 */     if (opType == 0) {
/* 253 */       this.descriptionTF.setEditable(false);
/*     */     }
/*     */ 
/* 256 */     this.toolbar = new JButtonBar();
/* 257 */     this.group = new ButtonGroup();
/*     */ 
/* 259 */     addButton(LocalizationManager.getText("chooser.local"), PARAMS_ICON, makePanel(LocalizationManager.getText("chooser.local.ext"), Location.LOCAL), Location.LOCAL);
/* 260 */     addButton(LocalizationManager.getText("chooser.remote"), PARAMS_ICON, makePanel(LocalizationManager.getText("chooser.remote.ext"), Location.REMOTE_USER), Location.REMOTE_USER);
/* 261 */     addButton(LocalizationManager.getText("chooser.public"), PARAMS_ICON, makePanel(LocalizationManager.getText("chooser.public.ext"), Location.REMOTE_PUBLIC), Location.REMOTE_PUBLIC);
/*     */ 
/* 263 */     this.leftPanel.add(this.controlPanel, "South");
/*     */ 
/* 265 */     getContentPane().add(this.leftPanel, "Center");
/*     */   }
/*     */ 
/*     */   public Component getParentComponent()
/*     */   {
/* 270 */     return this.parentComponent;
/*     */   }
/*     */ 
/*     */   private void stopBackgroundOperations() {
/* 274 */     stopLoading();
/*     */   }
/*     */ 
/*     */   public void setLocalMode(boolean isLocal) {
/* 278 */     this.controlPanel.setVisible(!isLocal);
/*     */   }
/*     */ 
/*     */   public void setPublicMode(boolean isPublic) {
/* 282 */     this.keyTF.setEnabled((!isPublic) && (this.opType != 1));
/* 283 */     this.findKeyBtn.setEnabled((!isPublic) && (this.opType != 1));
/*     */   }
/*     */ 
/*     */   public void refreshFileList()
/*     */   {
/* 288 */     cancelLoading();
/*     */ 
/* 290 */     refresh();
/*     */   }
/*     */ 
/*     */   public void fileSelected(FileItem fileItem) {
/* 294 */     this.fileNameTF.setText(fileItem.getFileName());
/* 295 */     if (this.opType == 1) {
/* 296 */       this.keyTF.setText(UUID.randomUUID().toString());
/*     */     }
/*     */ 
/* 299 */     this.descriptionTF.setText(fileItem.getDescription());
/*     */   }
/*     */ 
/*     */   public void clearFileName() {
/* 303 */     if (this.opType == 0)
/* 304 */       this.fileNameTF.setText("");
/*     */   }
/*     */ 
/*     */   private void highlightText(JTextField tf) {
/* 308 */     String text = tf.getText();
/* 309 */     if ((text != null) && (text.length() > 0)) {
/* 310 */       tf.setSelectionStart(0);
/* 311 */       tf.setSelectionEnd(text.length());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void approve()
/*     */   {
/* 317 */     if (this.multipleSelection.booleanValue())
/* 318 */       this.wrapper = new ChooserSelectionWrapper(getSelectedFiles(), this.currentLocation);
/*     */     else {
/* 320 */       this.wrapper = new ChooserSelectionWrapper(getFile(), this.currentLocation);
/*     */     }
/*     */ 
/* 323 */     dispose();
/*     */   }
/*     */ 
/*     */   public void cancel() {
/* 327 */     this.wrapper = null;
/* 328 */     dispose();
/*     */   }
/*     */ 
/*     */   public String getDescription() {
/* 332 */     return this.descriptionTF.getText();
/*     */   }
/*     */ 
/*     */   public void setFile(File f) {
/* 336 */     this.fileNameTF.setText(f.getName());
/*     */   }
/*     */ 
/*     */   public ChooserSelectionWrapper showDialog()
/*     */   {
/* 341 */     setSize(640, 480);
/* 342 */     setLocationRelativeTo(this.parentComponent);
/* 343 */     setVisible(true);
/*     */ 
/* 345 */     return this.wrapper;
/*     */   }
/*     */ 
/*     */   public void setClientType(String ct) {
/* 349 */     this.clientType = ct;
/*     */   }
/*     */ 
/*     */   public FileItem getFile() {
/* 353 */     FileItem item = getSelectedFile();
/*     */ 
/* 355 */     if (item == null) {
/* 356 */       String fileName = this.fileNameTF.getText();
/*     */ 
/* 358 */       if ((fileName != null) && (fileName.trim().length() > 0))
/*     */       {
/* 360 */         item = new FileItem();
/* 361 */         item.setFileName(fileName);
/* 362 */         item.setDescription(getDescription());
/* 363 */         item.setFileType(this.fileType);
/*     */ 
/* 365 */         if (this.currentLocation == Location.REMOTE_USER) {
/* 366 */           item.setAccessType(FileItem.AccessType.PRIVATE);
/*     */         }
/*     */ 
/* 369 */         if (this.currentLocation == Location.REMOTE_PUBLIC)
/* 370 */           item.setAccessType(FileItem.AccessType.PUBLIC);
/*     */       }
/*     */       else
/*     */       {
/* 374 */         return null;
/*     */       }
/*     */     }
/*     */ 
/* 378 */     return item;
/*     */   }
/*     */ 
/*     */   public static ChooserSelectionWrapper showSaveDialog(FileType fileType, JFileChooser localChooser, Component parent, String fileName) {
/* 382 */     TransportFileChooser chooser = new TransportFileChooser(fileType, 1, localChooser, parent, fileName, Boolean.valueOf(false));
/*     */ 
/* 384 */     return chooser.showDialog();
/*     */   }
/*     */ 
/*     */   public static ChooserSelectionWrapper showOpenDialog(FileType fileType, JFileChooser localChooser, Component parent, Boolean allowMultiple, String clientType)
/*     */   {
/* 389 */     TransportFileChooser chooser = new TransportFileChooser(fileType, 0, localChooser, parent, "", allowMultiple);
/*     */ 
/* 391 */     chooser.setClientType(clientType);
/*     */ 
/* 393 */     return chooser.showDialog();
/*     */   }
/*     */ 
/*     */   private void addButton(String title, Icon icon, MyPanel component, Location location)
/*     */   {
/* 398 */     JToggleButton button = new JToggleButton();
/* 399 */     button.setText(title);
/* 400 */     button.setIcon(icon);
/*     */ 
/* 402 */     button.addItemListener(new ItemListener(component, location)
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e)
/*     */       {
/* 406 */         if (e.getStateChange() == 1)
/* 407 */           TransportFileChooser.this.show(this.val$component, this.val$location);
/*     */       }
/*     */     });
/* 412 */     this.toolbar.addButton(button);
/*     */ 
/* 414 */     this.group.add(button);
/*     */ 
/* 416 */     if (this.group.getSelection() == null) {
/* 417 */       button.setSelected(true);
/* 418 */       show(component, location);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void show(MyPanel component, Location location)
/*     */   {
/* 424 */     setLocalMode(location == Location.LOCAL);
/* 425 */     setPublicMode(location == Location.REMOTE_PUBLIC);
/*     */ 
/* 427 */     if (this.currentComponent != null) {
/* 428 */       this.leftPanel.remove(this.currentComponent);
/*     */     }
/*     */ 
/* 431 */     this.currentLocation = location;
/*     */ 
/* 433 */     this.leftPanel.add(this.currentComponent = component, "Center");
/*     */ 
/* 435 */     cancelLoading();
/*     */ 
/* 437 */     if (location != Location.LOCAL) {
/* 438 */       this.currentTable = ((JTable)this.tableMap.get(location));
/*     */ 
/* 440 */       loadFileList(this.currentTable, location, component);
/*     */     }
/*     */ 
/* 444 */     this.leftPanel.revalidate();
/* 445 */     this.leftPanel.repaint();
/*     */   }
/*     */ 
/*     */   private void cancelLoading()
/*     */   {
/* 450 */     synchronized (this.progressListener) {
/* 451 */       if (this.progressListener.isThreadStarted()) {
/* 452 */         this.progressListener.cancelLoading();
/*     */ 
/* 454 */         while (!this.progressListener.isThreadStopped())
/*     */           try {
/* 456 */             Thread.sleep(100L);
/*     */           }
/*     */           catch (Exception e) {
/*     */           }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void useKey() {
/* 465 */     String key = this.keyTF.getText();
/*     */ 
/* 467 */     if ((key != null) && (key.trim().length() > 0))
/*     */       try {
/* 469 */         FileItem item = FeedDataProvider.getCurvesProtocolHandler().useKey(key, this.fileType, new FileProgressListener(), this.clientType);
/* 470 */         if (item != null) {
/* 471 */           JOptionPane.showMessageDialog(this, "TODO: File found by key: " + item.getFileName());
/*     */ 
/* 473 */           refresh();
/*     */         }
/*     */       } catch (KeyNotFoundException e) {
/* 476 */         JOptionPane.showMessageDialog(this, "TODO: Invalid key");
/*     */       } catch (StorageException e) {
/* 478 */         JOptionPane.showMessageDialog(this, "TODO: Cannot use key");
/*     */       }
/*     */   }
/*     */ 
/*     */   private void loadFileList(JTable table, Location location, MyPanel panel)
/*     */   {
/* 485 */     this.okBtn.setEnabled(false);
/* 486 */     this.fileNameTF.setEditable(false);
/*     */ 
/* 488 */     new Thread(new Runnable(panel, table, location)
/*     */     {
/*     */       public void run()
/*     */       {
/*     */         try
/*     */         {
/* 495 */           this.val$panel.getIconLabel().setIcon(TransportFileChooser.PROGRESS_IMAGE);
/*     */ 
/* 497 */           TableModel m = this.val$table.getModel();
/*     */ 
/* 499 */           FileListTableModel model = null;
/*     */ 
/* 501 */           if ((m instanceof FileListTableModel)) {
/* 502 */             model = (FileListTableModel)m;
/* 503 */             model.clearData();
/*     */           }
/*     */ 
/* 506 */           TransportFileChooser.this.progressListener.reset();
/* 507 */           TransportFileChooser.this.progressListener.setThreadStarted();
/*     */ 
/* 509 */           FileItem.AccessType accType = null;
/*     */ 
/* 511 */           if (this.val$location == Location.REMOTE_PUBLIC) {
/* 512 */             accType = FileItem.AccessType.PUBLIC;
/*     */           }
/*     */ 
/* 515 */           if (this.val$location == Location.REMOTE_USER) {
/* 516 */             accType = FileItem.AccessType.PRIVATE;
/*     */           }
/*     */ 
/* 519 */           List data = FeedDataProvider.getCurvesProtocolHandler().getFileList(TransportFileChooser.this.fileType, accType, TransportFileChooser.this.progressListener);
/*     */ 
/* 521 */           if ((m instanceof FileListTableModel)) {
/* 522 */             model = (FileListTableModel)m;
/* 523 */             model.setData(data);
/*     */           } else {
/* 525 */             model = new FileListTableModel(data, TransportFileChooser.this.fileType);
/* 526 */             this.val$table.setModel(model);
/*     */           }
/*     */ 
/* 529 */           TransportFileChooser.this.clearFileName();
/*     */ 
/* 531 */           TransportFileChooser.this.okBtn.setEnabled(true);
/* 532 */           TransportFileChooser.this.fileNameTF.setEditable(true);
/*     */         }
/*     */         catch (CancelLoadingException ce) {
/* 535 */           TransportFileChooser.LOGGER.warn(ce.getMessage());
/*     */         } catch (Exception e) {
/* 537 */           String errMsg = LocalizationManager.getText("chooser.list.failed");
/* 538 */           String errTitle = LocalizationManager.getText("error.title");
/*     */ 
/* 540 */           TransportFileChooser.LOGGER.error(e.getMessage(), e);
/*     */ 
/* 542 */           if (!TransportFileChooser.this.isShowing()) {
/* 543 */             TransportFileChooser.access$1102(TransportFileChooser.this, TransportFileChooser.this.getParentComponent());
/*     */           }
/*     */ 
/* 546 */           JOptionPane.showMessageDialog(TransportFileChooser.this.parentComponent, errMsg, errTitle, 0);
/*     */         } finally {
/* 548 */           this.val$panel.getIconLabel().setIcon(null);
/* 549 */           TransportFileChooser.this.progressListener.setThreadStopped();
/*     */         }
/*     */       }
/*     */     }).start();
/*     */   }
/*     */ 
/*     */   private MyPanel makePanel(String title, Location location)
/*     */   {
/* 556 */     MyPanel panel = new MyPanel(new BorderLayout());
/*     */ 
/* 558 */     JLabel topLabel = new JLabel();
/* 559 */     topLabel.setText(title);
/* 560 */     topLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
/* 561 */     topLabel.setFont(topLabel.getFont().deriveFont(1));
/* 562 */     topLabel.setOpaque(false);
/* 563 */     topLabel.setBackground(panel.getBackground().brighter());
/*     */ 
/* 565 */     JButton refreshBtn = new JButton(REFRESH_IMAGE);
/* 566 */     refreshBtn.setToolTipText(LocalizationManager.getText("button.refresh"));
/* 567 */     refreshBtn.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 570 */         TransportFileChooser.this.refreshFileList();
/*     */       }
/*     */     });
/* 574 */     JPanel btnTopPanel = new JPanel();
/* 575 */     btnTopPanel.add(refreshBtn);
/*     */ 
/* 577 */     JPanel topPanel = new JPanel(new BorderLayout());
/* 578 */     topPanel.add(topLabel, "Center");
/* 579 */     topPanel.add(btnTopPanel, "East");
/*     */ 
/* 581 */     panel.setIconLabel(topLabel);
/* 582 */     panel.add(topPanel, "North");
/*     */ 
/* 584 */     if (location == Location.LOCAL) {
/* 585 */       panel.add(this.localChooser, "Center");
/* 586 */       btnTopPanel.setVisible(false);
/*     */     }
/*     */     else {
/* 589 */       btnTopPanel.setVisible(true);
/*     */ 
/* 591 */       JTable table = new JTable();
/* 592 */       table.setFillsViewportHeight(true);
/* 593 */       table.setSelectionMode(this.multipleSelection.booleanValue() ? 2 : 0);
/* 594 */       table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
/*     */       {
/*     */         public void valueChanged(ListSelectionEvent e)
/*     */         {
/* 598 */           TransportFileChooser.this.onRowSelect();
/*     */         }
/*     */       });
/* 602 */       this.tableMap.put(location, table);
/* 603 */       panel.add(new JScrollPane(table), "Center");
/*     */     }
/*     */ 
/* 606 */     panel.setPreferredSize(new Dimension(400, 300));
/* 607 */     panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
/* 608 */     return panel;
/*     */   }
/*     */ 
/*     */   public List<FileItem> getSelectedFiles()
/*     */   {
/* 613 */     List items = new ArrayList();
/*     */ 
/* 615 */     if (this.currentLocation == Location.LOCAL) {
/* 616 */       File[] files = this.localChooser.getSelectedFiles();
/*     */ 
/* 618 */       if (files != null)
/* 619 */         for (File file : files) {
/* 620 */           FileItem item = new FileItem();
/* 621 */           item.setFileName(file.getAbsolutePath());
/* 622 */           items.add(item);
/*     */         }
/*     */     }
/*     */     else
/*     */     {
/* 627 */       int[] selectedIndexes = this.currentTable.getSelectedRows();
/*     */ 
/* 629 */       if (selectedIndexes != null)
/*     */       {
/* 631 */         FileListTableModel model = (FileListTableModel)this.currentTable.getModel();
/*     */ 
/* 633 */         for (int selectedIndex : selectedIndexes) {
/* 634 */           FileItem item = model.getRow(selectedIndex);
/* 635 */           items.add(item);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 640 */     return items;
/*     */   }
/*     */ 
/*     */   public FileItem getSelectedFile()
/*     */   {
/* 646 */     if (this.currentLocation == Location.LOCAL) {
/* 647 */       File f = this.localChooser.getSelectedFile();
/*     */ 
/* 649 */       FileItem item = new FileItem();
/*     */ 
/* 651 */       if (f != null) {
/* 652 */         item.setFileName(f.getAbsolutePath());
/*     */       }
/* 654 */       return item;
/*     */     }
/*     */ 
/* 657 */     int selectedIndex = this.currentTable.getSelectedRow();
/*     */ 
/* 659 */     if (selectedIndex >= 0)
/*     */     {
/* 661 */       FileListTableModel model = (FileListTableModel)this.currentTable.getModel();
/*     */ 
/* 663 */       return model.getRow(selectedIndex);
/*     */     }
/*     */ 
/* 666 */     return null;
/*     */   }
/*     */ 
/*     */   private void onRowSelect()
/*     */   {
/* 671 */     FileItem fi = getSelectedFile();
/* 672 */     if (fi != null)
/* 673 */       fileSelected(fi);
/*     */   }
/*     */ 
/*     */   public void stopLoading()
/*     */   {
/* 678 */     this.progressListener.cancelLoading();
/*     */   }
/*     */ 
/*     */   public void refresh() {
/* 682 */     loadFileList(this.currentTable, this.currentLocation, this.currentComponent);
/*     */   }
/*     */ 
/*     */   class MyPanel extends JPanel
/*     */   {
/*     */     private static final long serialVersionUID = -2208843064917600643L;
/*     */     JLabel iconLabel;
/*     */ 
/*     */     public MyPanel() {
/*     */     }
/*     */ 
/*     */     public MyPanel(boolean isDoubleBuffered) {
/* 696 */       super();
/*     */     }
/*     */ 
/*     */     public MyPanel(LayoutManager layout, boolean isDoubleBuffered) {
/* 700 */       super(isDoubleBuffered);
/*     */     }
/*     */ 
/*     */     public MyPanel(LayoutManager layout) {
/* 704 */       super();
/*     */     }
/*     */ 
/*     */     public JLabel getIconLabel() {
/* 708 */       return this.iconLabel;
/*     */     }
/*     */ 
/*     */     public void setIconLabel(JLabel label) {
/* 712 */       this.iconLabel = label;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.filechooser.TransportFileChooser
 * JD-Core Version:    0.6.0
 */