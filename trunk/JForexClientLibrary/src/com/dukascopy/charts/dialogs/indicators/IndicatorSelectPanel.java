/*     */ package com.dukascopy.charts.dialogs.indicators;
/*     */ 
/*     */ import com.dukascopy.api.impl.IndicatorHolder;
/*     */ import com.dukascopy.api.indicators.IIndicator;
/*     */ import com.dukascopy.api.indicators.IndicatorInfo;
/*     */ import com.dukascopy.charts.dialogs.indicators.listener.IndicatorAWTEventDelegateListener;
/*     */ import com.dukascopy.charts.dialogs.indicators.listener.IndicatorAWTEventListener;
/*     */ import com.dukascopy.charts.dialogs.indicators.node.ExtendedMutableTreeNode;
/*     */ import com.dukascopy.charts.math.indicators.IndicatorsProvider;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.swing.AbstractListModel;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JRadioButton;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JSplitPane;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
/*     */ import javax.swing.event.TreeSelectionEvent;
/*     */ import javax.swing.event.TreeSelectionListener;
/*     */ import javax.swing.text.Document;
/*     */ import javax.swing.tree.DefaultTreeCellRenderer;
/*     */ import javax.swing.tree.DefaultTreeModel;
/*     */ import javax.swing.tree.TreeNode;
/*     */ import javax.swing.tree.TreePath;
/*     */ import javax.swing.tree.TreeSelectionModel;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ class IndicatorSelectPanel extends JPanel
/*     */ {
/*  62 */   private static final Logger LOGGER = LoggerFactory.getLogger(IndicatorSelectPanel.class);
/*     */   private static final int HEIGHT = 250;
/*     */   private static final String COMMON_GROUP = "Common";
/*     */   private static final String CUSTOM_GROUP = "Custom";
/*     */   private final boolean isOnlySubIndicators;
/*     */   private final ActionListener actionListener;
/*     */   private final IndicatorSelectionListener indicatorSelectionListener;
/*     */   private final GroupsTreeModel groupsTreeModel;
/*     */   private final GroupsTree groupsTree;
/*     */   private final IndicatorsListModel indicatorsListModel;
/*     */   private final IndicatorsList indicatorsList;
/*     */   private final IndicatorsSplitPane splitPane;
/*     */   private final BottomPanel bottomPanel;
/*     */   private QuickSearchTextField quickSearchTextField;
/*     */   private static IndicatorAWTEventListener indicatorAWTEventListener;
/*     */ 
/*     */   public IndicatorSelectPanel(boolean isOnlySubIndicators, ActionListener actionListener, IndicatorSelectionListener indicatorSelectionListener)
/*     */   {
/*  91 */     super(new BorderLayout());
/*  92 */     this.isOnlySubIndicators = isOnlySubIndicators;
/*  93 */     this.actionListener = actionListener;
/*  94 */     this.indicatorSelectionListener = indicatorSelectionListener;
/*     */ 
/*  96 */     setPreferredSize(new Dimension(0, 250));
/*     */ 
/*  98 */     add(this.splitPane = new IndicatorsSplitPane(this.groupsTree = new GroupsTree(this.groupsTreeModel = new GroupsTreeModel(null)), this.indicatorsList = new IndicatorsList(this.indicatorsListModel = new IndicatorsListModel())), "Center");
/*     */ 
/* 103 */     add(this.bottomPanel = new BottomPanel(), "Last");
/*     */ 
/* 105 */     updateGroups();
/*     */ 
/* 108 */     Toolkit.getDefaultToolkit().removeAWTEventListener(getIndicatorAWTEventListener());
/* 109 */     Toolkit.getDefaultToolkit().addAWTEventListener(getIndicatorAWTEventListener(), 8L);
/*     */   }
/*     */ 
/*     */   public boolean isPreviewEnabled()
/*     */   {
/* 114 */     return this.bottomPanel.isPreviewEnabled();
/*     */   }
/*     */ 
/*     */   public void updateGroups() {
/* 118 */     this.groupsTreeModel.update();
/* 119 */     this.groupsTree.expandAll();
/* 120 */     if (this.groupsTree.getRowCount() > 0)
/* 121 */       this.groupsTree.setSelectionRow(1);
/*     */   }
/*     */ 
/*     */   public void select(String indicatorName)
/*     */   {
/* 126 */     if (this.groupsTree.isVisible()) {
/* 127 */       String groupName = null;
/* 128 */       IndicatorsProvider indicatorsProvider = IndicatorsProvider.getInstance();
/* 129 */       for (String group : indicatorsProvider.getGroups()) {
/* 130 */         for (String name : indicatorsProvider.getNames(group)) {
/* 131 */           if (indicatorName.equals(name)) {
/* 132 */             groupName = group;
/* 133 */             break;
/*     */           }
/*     */         }
/* 136 */         if (groupName != null)
/*     */         {
/*     */           break;
/*     */         }
/*     */       }
/* 141 */       TreeNode root = (TreeNode)this.groupsTreeModel.getRoot();
/* 142 */       if ((groupName == null) || (indicatorsProvider.getCustomIndicatorGroups().contains(groupName))) {
/* 143 */         ExtendedMutableTreeNode treeNode = (ExtendedMutableTreeNode)root.getChildAt(1);
/* 144 */         this.groupsTree.setSelectionPath(new TreePath(treeNode.getPath()));
/*     */       } else {
/* 146 */         TreeNode commonNode = root.getChildAt(0);
/* 147 */         for (int i = 0; i < commonNode.getChildCount(); i++) {
/* 148 */           ExtendedMutableTreeNode treeNode = (ExtendedMutableTreeNode)commonNode.getChildAt(i);
/* 149 */           if (treeNode.getUserObject().equals(groupName)) {
/* 150 */             this.groupsTree.setSelectionPath(new TreePath(treeNode.getPath()));
/* 151 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 157 */     for (int i = 0; i < this.indicatorsListModel.getSize(); i++) {
/* 158 */       IndicatorListObject indicatorListObject = this.indicatorsListModel.getElementAt(i);
/* 159 */       if (indicatorListObject.name.equals(indicatorName)) {
/* 160 */         this.indicatorsList.setSelectedValue(indicatorListObject, true);
/* 161 */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isSelectionValid() {
/* 167 */     if (this.indicatorsList.getSelectedValue() == null) {
/* 168 */       JOptionPane.showMessageDialog(this, LocalizationManager.getText("message.no.indicator.selected"), LocalizationManager.getText("title.wrong.selection"), 1);
/*     */ 
/* 174 */       return false;
/*     */     }
/* 176 */     return true;
/*     */   }
/*     */ 
/*     */   public QuickSearchTextField getQuickSearchTextField()
/*     */   {
/* 712 */     if (this.quickSearchTextField == null) {
/* 713 */       this.quickSearchTextField = new QuickSearchTextField();
/* 714 */       getIndicatorAWTEventListener().removeAllDelegateListeners();
/* 715 */       getIndicatorAWTEventListener().addDelegateListener(this.quickSearchTextField);
/*     */     }
/* 717 */     return this.quickSearchTextField;
/*     */   }
/*     */ 
/*     */   public static IndicatorAWTEventListener getIndicatorAWTEventListener() {
/* 721 */     if (indicatorAWTEventListener == null) {
/* 722 */       indicatorAWTEventListener = new IndicatorAWTEventListener();
/*     */     }
/* 724 */     return indicatorAWTEventListener;
/*     */   }
/*     */ 
/*     */   public class GroupsTreeModel extends DefaultTreeModel
/*     */   {
/*     */     private final ExtendedMutableTreeNode rootNode;
/*     */     private final ExtendedMutableTreeNode commonNode;
/*     */     private final ExtendedMutableTreeNode customNode;
/* 601 */     private String pattern = "";
/*     */ 
/*     */     private GroupsTreeModel() {
/* 604 */       super();
/*     */ 
/* 606 */       this.rootNode = new ExtendedMutableTreeNode();
/* 607 */       this.commonNode = new ExtendedMutableTreeNode("Common");
/* 608 */       this.customNode = new ExtendedMutableTreeNode("Custom");
/*     */ 
/* 610 */       update();
/*     */     }
/*     */ 
/*     */     public String getPattern() {
/* 614 */       return this.pattern;
/*     */     }
/*     */ 
/*     */     private void update() {
/* 618 */       update(this.pattern);
/*     */     }
/*     */ 
/*     */     public void update(String pattern) {
/* 622 */       if (IndicatorSelectPanel.LOGGER.isDebugEnabled()) {
/* 623 */         IndicatorSelectPanel.LOGGER.debug(new StringBuilder().append("Apply pattern : [").append(pattern).append("]").toString());
/*     */       }
/*     */ 
/* 626 */       this.pattern = pattern;
/* 627 */       reset();
/*     */       try
/*     */       {
/* 630 */         IndicatorsProvider provider = IndicatorsProvider.getInstance();
/*     */ 
/* 632 */         boolean isCustomIndicatorsExists = false;
/* 633 */         Set groups = new HashSet();
/*     */ 
/* 635 */         Collection allIndicatorNames = provider.getAllNames();
/*     */ 
/* 637 */         for (String name : allIndicatorNames) {
/* 638 */           IIndicator indicator = provider.getIndicatorHolder(name).getIndicator();
/* 639 */           IndicatorInfo indicatorInfo = indicator.getIndicatorInfo();
/* 640 */           if (indicatorInfo == null) {
/*     */             continue;
/*     */           }
/* 643 */           String groupName = indicatorInfo.getGroupName();
/* 644 */           String indicatorName = indicatorInfo.getName().toUpperCase();
/*     */ 
/* 646 */           if ((indicatorName != null) && (!provider.isEnabledOnCharts(indicatorName)))
/*     */           {
/*     */             continue;
/*     */           }
/* 650 */           boolean isCustomIndicator = provider.getCustomIndictorNames().contains(indicatorName);
/*     */ 
/* 652 */           if ((!isCustomIndicator) && (groups.contains(groupName)))
/*     */           {
/*     */             continue;
/*     */           }
/* 656 */           String indicatorTitle = indicatorInfo.getTitle();
/* 657 */           if ((matches(pattern, indicatorName)) || (matches(pattern, indicatorTitle))) {
/* 658 */             if ((IndicatorSelectPanel.this.isOnlySubIndicators) && (indicatorInfo.isOverChart()))
/*     */             {
/*     */               continue;
/*     */             }
/* 662 */             if (IndicatorSelectPanel.LOGGER.isDebugEnabled()) {
/* 663 */               IndicatorSelectPanel.LOGGER.debug(new StringBuilder().append("Match : ").append(isCustomIndicator ? "[Custom] " : "").append(indicatorName).append(" - ").append(indicatorTitle).append(" @ ").append(groupName).toString());
/*     */             }
/*     */ 
/* 666 */             if (!isCustomIndicator) {
/* 667 */               if (!groupName.isEmpty())
/* 668 */                 groups.add(groupName);
/*     */             }
/*     */             else {
/* 671 */               isCustomIndicatorsExists = true;
/*     */             }
/*     */ 
/* 674 */             if ((!isCustomIndicator) && (this.commonNode.getParent() == null)) {
/* 675 */               this.rootNode.add(this.commonNode);
/*     */             }
/*     */ 
/* 678 */             if ((!isCustomIndicator) && (!groupName.isEmpty())) {
/* 679 */               this.commonNode.add(new ExtendedMutableTreeNode(groupName));
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 685 */         if (isCustomIndicatorsExists)
/* 686 */           this.rootNode.add(this.customNode);
/*     */       }
/*     */       catch (Exception e) {
/* 689 */         e.printStackTrace();
/*     */       }
/*     */ 
/* 692 */       this.commonNode.sortChildren();
/* 693 */       setRoot(this.rootNode);
/*     */     }
/*     */ 
/*     */     private boolean matches(String pattern, String value) {
/* 697 */       return (value != null) && (value.toLowerCase().contains(pattern.toLowerCase()));
/*     */     }
/*     */ 
/*     */     private void reset() {
/* 701 */       this.rootNode.removeAllChildren();
/* 702 */       this.commonNode.removeAllChildren();
/* 703 */       this.customNode.removeAllChildren();
/*     */     }
/*     */ 
/*     */     public ExtendedMutableTreeNode getCommonNode() {
/* 707 */       return this.commonNode;
/*     */     }
/*     */   }
/*     */ 
/*     */   public class GroupsTree extends JTree
/*     */   {
/*     */     public GroupsTree(IndicatorSelectPanel.GroupsTreeModel treeModel)
/*     */     {
/* 556 */       super();
/* 557 */       setRootVisible(false);
/* 558 */       setShowsRootHandles(false);
/* 559 */       setToggleClickCount(2);
/* 560 */       getSelectionModel().setSelectionMode(1);
/* 561 */       setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*     */ 
/* 563 */       setCellRenderer(new DefaultTreeCellRenderer(IndicatorSelectPanel.this) {
/*     */         public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
/* 565 */           return super.getTreeCellRendererComponent(tree, value, sel, expanded, false, row, hasFocus);
/*     */         }
/*     */       });
/* 569 */       getSelectionModel().addTreeSelectionListener(new TreeSelectionListener(IndicatorSelectPanel.this, treeModel)
/*     */       {
/*     */         public void valueChanged(TreeSelectionEvent e) {
/* 572 */           TreePath path = IndicatorSelectPanel.GroupsTree.this.getSelectionPath();
/* 573 */           String group = "Common";
/* 574 */           if (path != null) {
/* 575 */             ExtendedMutableTreeNode treeNode = (ExtendedMutableTreeNode)path.getLastPathComponent();
/* 576 */             group = (String)treeNode.getUserObject();
/*     */           }
/*     */ 
/* 579 */           IndicatorSelectPanel.this.indicatorsListModel.addIndicators(group, this.val$treeModel.getPattern());
/* 580 */           IndicatorSelectPanel.this.indicatorsList.setSelectedValue(null, false);
/*     */         } } );
/*     */     }
/*     */ 
/*     */     public void expandAll() {
/* 586 */       for (int i = 0; i < getRowCount(); i++)
/* 587 */         expandRow(i);
/*     */     }
/*     */   }
/*     */ 
/*     */   public class IndicatorsListModel extends AbstractListModel
/*     */   {
/* 480 */     private final List<IndicatorSelectPanel.IndicatorListObject> indicators = new ArrayList();
/* 481 */     private String pattern = "";
/*     */ 
/*     */     public IndicatorsListModel() {
/*     */     }
/* 485 */     public IndicatorSelectPanel.IndicatorListObject getElementAt(int index) { return (IndicatorSelectPanel.IndicatorListObject)this.indicators.get(index);
/*     */     }
/*     */ 
/*     */     public int getSize()
/*     */     {
/* 490 */       return this.indicators.size();
/*     */     }
/*     */ 
/*     */     public void reset() {
/* 494 */       if (!this.indicators.isEmpty()) {
/* 495 */         fireIntervalRemoved(this, 0, this.indicators.size() - 1);
/* 496 */         this.indicators.clear();
/*     */       }
/*     */     }
/*     */ 
/*     */     public void addIndicators(String groupName, String namePattern) {
/* 501 */       if (IndicatorSelectPanel.LOGGER.isDebugEnabled()) {
/* 502 */         IndicatorSelectPanel.LOGGER.debug("Add indicators [" + namePattern + " ] @ " + groupName);
/*     */       }
/*     */ 
/* 505 */       reset();
/* 506 */       this.pattern = namePattern;
/*     */ 
/* 508 */       IndicatorsProvider provider = IndicatorsProvider.getInstance();
/*     */       Collection names;
/*     */       Collection names;
/* 510 */       if (groupName == null) {
/* 511 */         names = provider.getAllNames();
/*     */       }
/*     */       else
/*     */       {
/*     */         Collection names;
/* 513 */         if (groupName.equals("Common")) {
/* 514 */           names = provider.getNames("");
/*     */         }
/*     */         else
/*     */         {
/*     */           Collection names;
/* 516 */           if (groupName.equals("Custom"))
/* 517 */             names = provider.getCustomIndictorNames();
/*     */           else
/* 519 */             names = provider.getNames(groupName);
/*     */         }
/*     */       }
/* 522 */       List sortedNames = new ArrayList(names);
/* 523 */       Collections.sort(sortedNames);
/* 524 */       for (String name : sortedNames) {
/* 525 */         if ((groupName != null) && (!groupName.equals("Custom")) && (provider.getCustomIndictorNames().contains(name))) {
/*     */           continue;
/*     */         }
/* 528 */         if (provider.isEnabledOnCharts(name)) {
/* 529 */           String title = provider.getTitle(name);
/* 530 */           if ((namePattern == null) || (matches(namePattern, name)) || (matches(namePattern, title))) {
/* 531 */             if ((IndicatorSelectPanel.this.isOnlySubIndicators) && (provider.getIndicatorHolder(name).getIndicator().getIndicatorInfo().isOverChart()))
/*     */             {
/*     */               continue;
/*     */             }
/* 535 */             this.indicators.add(new IndicatorSelectPanel.IndicatorListObject(IndicatorSelectPanel.this, name, title, namePattern));
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 540 */       if (!this.indicators.isEmpty())
/* 541 */         fireIntervalAdded(this, 0, this.indicators.size() - 1);
/*     */     }
/*     */ 
/*     */     private boolean matches(String pattern, String value)
/*     */     {
/* 546 */       return (value != null) && (value.toLowerCase().contains(pattern.toLowerCase()));
/*     */     }
/*     */   }
/*     */ 
/*     */   public class IndicatorsList extends JList
/*     */   {
/* 411 */     private List<String> selectedIndicatorsOrdered = Collections.synchronizedList(new ArrayList());
/*     */ 
/* 413 */     public IndicatorsList(IndicatorSelectPanel.IndicatorsListModel listModel) { super();
/*     */ 
/* 415 */       setSelectionMode(2);
/* 416 */       setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*     */ 
/* 418 */       addListSelectionListener(new ListSelectionListener(IndicatorSelectPanel.this)
/*     */       {
/*     */         public void valueChanged(ListSelectionEvent e)
/*     */         {
/* 422 */           if (e.getValueIsAdjusting()) {
/* 423 */             return;
/*     */           }
/*     */ 
/* 426 */           IndicatorSelectPanel.IndicatorsList.access$1202(IndicatorSelectPanel.IndicatorsList.this, IndicatorSelectPanel.IndicatorsList.this.getIdicatorList(IndicatorSelectPanel.this.indicatorsList.getSelectedValues()));
/*     */ 
/* 428 */           if (!IndicatorSelectPanel.IndicatorsList.this.selectedIndicatorsOrdered.isEmpty())
/* 429 */             IndicatorSelectPanel.this.indicatorSelectionListener.selectedIndicators(IndicatorSelectPanel.IndicatorsList.this.selectedIndicatorsOrdered);
/*     */           else {
/* 431 */             IndicatorSelectPanel.this.indicatorSelectionListener.selectedIndicators(null);
/*     */           }
/*     */ 
/* 434 */           if (IndicatorSelectPanel.IndicatorsList.this.selectedIndicatorsOrdered.size() > 1)
/* 435 */             IndicatorSelectPanel.this.bottomPanel.setPreviewEnabled(false);
/*     */           else
/* 437 */             IndicatorSelectPanel.this.bottomPanel.setPreviewEnabled(true);
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/*     */     private synchronized List<String> getIdicatorList(Object[] selectedItems)
/*     */     {
/* 446 */       if (selectedItems == null) {
/* 447 */         return null;
/*     */       }
/* 449 */       boolean isOrderesListEmpty = this.selectedIndicatorsOrdered.isEmpty();
/* 450 */       List newSelection = new ArrayList();
/* 451 */       List orderedSelection = new ArrayList();
/*     */ 
/* 453 */       for (Object indicator : selectedItems) {
/* 454 */         newSelection.add(IndicatorSelectPanel.IndicatorListObject.access$200((IndicatorSelectPanel.IndicatorListObject)indicator));
/*     */       }
/*     */ 
/* 457 */       if (!isOrderesListEmpty) {
/* 458 */         for (String indicatorName : this.selectedIndicatorsOrdered) {
/* 459 */           if (newSelection.contains(indicatorName)) {
/* 460 */             orderedSelection.add(indicatorName);
/*     */           }
/*     */         }
/*     */       }
/* 464 */       for (String indicatorName : newSelection) {
/* 465 */         if (!this.selectedIndicatorsOrdered.contains(indicatorName)) {
/* 466 */           orderedSelection.add(indicatorName);
/*     */         }
/*     */       }
/* 469 */       return orderedSelection;
/*     */     }
/*     */   }
/*     */ 
/*     */   public class IndicatorListObject
/*     */   {
/*     */     private final String name;
/*     */     private final String value;
/*     */ 
/*     */     public IndicatorListObject(String name, String title, String pattern)
/*     */     {
/* 365 */       this.name = name;
/* 366 */       if ((pattern != null) && (!pattern.isEmpty()))
/* 367 */         this.value = new StringBuilder().append("<html>").append(highlight(name, pattern)).append(" - ").append(highlight(title, pattern)).append("</html>").toString();
/*     */       else
/* 369 */         this.value = new StringBuilder().append(name).append(" - ").append(title).toString();
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 375 */       return this.value;
/*     */     }
/*     */ 
/*     */     private String highlight(String value, String pattern) {
/* 379 */       if ((pattern.isEmpty()) || (!value.toLowerCase().contains(pattern.toLowerCase()))) {
/* 380 */         return value;
/*     */       }
/* 382 */       StringBuilder result = new StringBuilder();
/*     */ 
/* 384 */       int index = 0;
/* 385 */       while (index < value.length()) {
/* 386 */         int i = value.toLowerCase().indexOf(pattern.toLowerCase(), index);
/*     */ 
/* 388 */         if (i >= 0) {
/* 389 */           result.append(value.substring(index, i));
/* 390 */           result.append("<font bgcolor='yellow' color='black'>").append(value.substring(i, i + pattern.length())).append("</font>");
/*     */ 
/* 394 */           index = i + pattern.length();
/*     */         } else {
/* 396 */           result.append(value.substring(index));
/* 397 */           break;
/*     */         }
/*     */       }
/*     */ 
/* 401 */       return result.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */   public class QuickSearchTextField extends JTextField
/*     */     implements IndicatorAWTEventDelegateListener
/*     */   {
/*     */     public QuickSearchTextField()
/*     */     {
/* 303 */       super();
/*     */ 
/* 305 */       getDocument().addDocumentListener(new DocumentListener(IndicatorSelectPanel.this)
/*     */       {
/*     */         public void removeUpdate(DocumentEvent e) {
/* 308 */           applyPattern();
/*     */         }
/*     */ 
/*     */         public void insertUpdate(DocumentEvent e)
/*     */         {
/* 313 */           applyPattern();
/*     */         }
/*     */ 
/*     */         public void changedUpdate(DocumentEvent e)
/*     */         {
/*     */         }
/*     */ 
/*     */         private void applyPattern()
/*     */         {
/* 322 */           IndicatorSelectPanel.this.indicatorsListModel.reset();
/* 323 */           String namePattern = IndicatorSelectPanel.QuickSearchTextField.this.getText().trim();
/* 324 */           if (IndicatorSelectPanel.this.splitPane.isTreeVisible()) {
/* 325 */             IndicatorSelectPanel.this.groupsTreeModel.update(namePattern);
/* 326 */             IndicatorSelectPanel.this.groupsTree.expandAll();
/* 327 */             IndicatorSelectPanel.this.groupsTree.setSelectionRow(IndicatorSelectPanel.access$300(IndicatorSelectPanel.this).commonNode.getChildCount() > 0 ? 1 : 0);
/*     */           } else {
/* 329 */             IndicatorSelectPanel.this.indicatorsListModel.addIndicators(null, namePattern);
/* 330 */             IndicatorSelectPanel.this.indicatorsList.setSelectedIndex(-1);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/*     */     public void eventDispatched(AWTEvent event) {
/* 338 */       if ((event instanceof KeyEvent)) {
/* 339 */         KeyEvent keyEvent = (KeyEvent)event;
/* 340 */         int keyCode = keyEvent.getKeyCode();
/* 341 */         Object source = keyEvent.getSource();
/*     */ 
/* 343 */         if ((((source instanceof IndicatorSelectPanel.IndicatorsList)) || ((source instanceof IndicatorSelectPanel.GroupsTree))) && 
/* 344 */           (402 == keyEvent.getID()) && (
/* 344 */           ((keyCode > 47) && (keyCode < 58)) || ((keyCode > 64) && (keyCode < 91))))
/*     */         {
/* 347 */           setText(getText() + keyEvent.getKeyChar());
/* 348 */           requestFocus();
/* 349 */           keyEvent.consume();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class BottomPanel extends JPanel
/*     */   {
/*     */     private final JCheckBox previewCheckbox;
/*     */ 
/*     */     public BottomPanel()
/*     */     {
/* 234 */       super();
/*     */ 
/* 236 */       ButtonGroup sortButtonGroup = new ButtonGroup();
/*     */ 
/* 238 */       GridBagConstraints gbc = new GridBagConstraints();
/* 239 */       gbc.insets.left = 5;
/* 240 */       gbc.insets.right = 5;
/* 241 */       gbc.anchor = 17;
/*     */ 
/* 243 */       gbc.gridx = 0;
/*     */ 
/* 245 */       add(new JRadioButton(LocalizationManager.getText("radio.sort.by.group"), IndicatorSelectPanel.this, sortButtonGroup)
/*     */       {
/*     */       }
/*     */       , gbc);
/*     */ 
/* 256 */       gbc.gridx = 1;
/* 257 */       add(new JRadioButton(LocalizationManager.getText("radio.sort.alphabetical"), IndicatorSelectPanel.this, sortButtonGroup)
/*     */       {
/*     */       }
/*     */       , gbc);
/*     */ 
/* 267 */       gbc.gridx = 2;
/* 268 */       gbc.weightx = 1.0D;
/* 269 */       gbc.fill = 2;
/* 270 */       add(this.previewCheckbox = new JCheckBox(LocalizationManager.getText("checkbox.preview"), IndicatorSelectPanel.this)
/*     */       {
/*     */       }
/*     */       , gbc);
/*     */ 
/* 275 */       gbc.weightx = 0.0D;
/* 276 */       gbc.fill = 0;
/* 277 */       gbc.anchor = 13;
/*     */ 
/* 279 */       gbc.gridx = 3;
/* 280 */       JLabel titleQuickSearch = new JLabel(LocalizationManager.getText("title.quick.search"));
/* 281 */       add(titleQuickSearch, gbc);
/*     */ 
/* 283 */       gbc.insets.right = 10;
/* 284 */       gbc.gridx = 4;
/* 285 */       add(IndicatorSelectPanel.this.getQuickSearchTextField(), gbc);
/*     */     }
/*     */ 
/*     */     public boolean isPreviewEnabled() {
/* 289 */       return this.previewCheckbox.isSelected();
/*     */     }
/*     */ 
/*     */     protected void setPreviewEnabled(boolean flag) {
/* 293 */       this.previewCheckbox.setEnabled(flag);
/*     */     }
/*     */   }
/*     */ 
/*     */   public class IndicatorsSplitPane extends JSplitPane
/*     */   {
/*     */     public IndicatorsSplitPane(JTree groupsTree, JList indicatorsList)
/*     */     {
/* 188 */       super(true, new JScrollPane(IndicatorSelectPanel.this, groupsTree)
/*     */       {
/*     */         public Dimension getMinimumSize()
/*     */         {
/* 192 */           Dimension treePreferedSize = this.val$groupsTree.getPreferredSize();
/* 193 */           return new Dimension(treePreferedSize.width + 10, treePreferedSize.height);
/*     */         }
/*     */       }
/*     */       , new JScrollPane(indicatorsList));
/*     */     }
/*     */ 
/*     */     public void setTreeVisible(boolean value)
/*     */     {
/* 201 */       getLeftComponent().setVisible(value);
/* 202 */       if (value) {
/* 203 */         setDividerSize(10);
/* 204 */         setDividerLocation(getLastDividerLocation());
/*     */ 
/* 206 */         IndicatorSelectPanel.this.groupsTreeModel.update();
/* 207 */         IndicatorSelectPanel.this.groupsTree.expandAll();
/*     */ 
/* 209 */         if (IndicatorSelectPanel.this.groupsTree.getRowCount() > 0)
/* 210 */           IndicatorSelectPanel.this.groupsTree.setSelectionRow(1);
/*     */       }
/*     */       else {
/* 213 */         setDividerSize(0);
/* 214 */         setDividerLocation(0);
/*     */ 
/* 216 */         IndicatorSelectPanel.this.indicatorsListModel.addIndicators(null, IndicatorSelectPanel.access$500(IndicatorSelectPanel.this).pattern);
/* 217 */         IndicatorSelectPanel.this.indicatorsList.setSelectedIndex(-1);
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean isTreeVisible() {
/* 222 */       return getDividerSize() > 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract interface IndicatorSelectionListener
/*     */   {
/*     */     public abstract void selectedIndicators(List<String> paramList);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.indicators.IndicatorSelectPanel
 * JD-Core Version:    0.6.0
 */