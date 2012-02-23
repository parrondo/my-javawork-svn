/*     */ package com.dukascopy.dds2.greed.gui.component.dialog;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.AppActionEvent;
/*     */ import com.dukascopy.dds2.greed.actions.InstrumentSubscribeAction;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.component.BasicDecoratedFrame;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrderCommonTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.TableSorter;
/*     */ import com.dukascopy.dds2.greed.gui.component.ticker.TickerPanel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableHeaderPanel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.model.AccountStatement;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.dds2.greed.util.CurrencyConverter;
/*     */ import com.dukascopy.dds2.greed.util.SimpleAlphabeticInstrumentComparator;
/*     */ import com.dukascopy.transport.common.model.type.Position;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Currency;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeSet;
/*     */ import javax.swing.AbstractListModel;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.ListModel;
/*     */ import javax.swing.ListSelectionModel;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class InstrumentSelectorDialog extends BasicDecoratedFrame
/*     */ {
/*  71 */   private static final Logger LOGGER = LoggerFactory.getLogger(InstrumentSelectorDialog.class);
/*     */   public static final String DEST = "DEST";
/*     */   public static final String SOURCE = "SOURCE";
/*  75 */   public static final Dimension preferredSize = new Dimension(600, 400);
/*     */ 
/*  77 */   private final Dimension BUTTON_DIMENSION = new Dimension(150, 25);
/*     */   private JPanel content;
/*     */   private JLabel sourceLabel;
/*     */   private JList sourceList;
/*     */   private InstrumentsListModel sourceListModel;
/*     */   private JList destList;
/*     */   private InstrumentsListModel destListModel;
/*     */   private JLabel destLabel;
/*     */   private JLocalizableButton addButton;
/*     */   private JLocalizableButton removeButton;
/*  87 */   private JLocalizableButton okButton = new JLocalizableButton("button.ok");
/*     */   private ClientSettingsStorage clientSettingsStorage;
/*     */   private MarketView marketView;
/*     */ 
/*     */   public InstrumentSelectorDialog(TickerPanel panel)
/*     */   {
/*  93 */     this.marketView = ((MarketView)GreedContext.get("marketView"));
/*  94 */     this.clientSettingsStorage = ((ClientSettingsStorage)GreedContext.get("settingsStorage"));
/*  95 */     initScreen();
/*     */   }
/*     */ 
/*     */   public void clearSourceListModel() {
/*  99 */     this.sourceListModel.clear();
/*     */   }
/*     */ 
/*     */   public void addSourceElements(ListModel newValue) {
/* 103 */     fillListModel(this.sourceListModel, newValue);
/*     */   }
/*     */ 
/*     */   private void fillListModel(InstrumentsListModel model, ListModel newValues) {
/* 107 */     int size = newValues.getSize();
/* 108 */     for (int i = 0; i < size; i++)
/* 109 */       model.add(newValues.getElementAt(i));
/*     */   }
/*     */ 
/*     */   public void addSourceElements(Object[] newValue)
/*     */   {
/* 114 */     fillListModel(this.sourceListModel, newValue);
/*     */   }
/*     */ 
/*     */   public void addDestinationElements(Object[] newValue) {
/* 118 */     fillListModel(this.destListModel, newValue);
/*     */   }
/*     */ 
/*     */   private void fillListModel(InstrumentsListModel model, Object[] newValues) {
/* 122 */     model.addAll(newValues);
/*     */   }
/*     */ 
/*     */   private void clearSourceSelected(List<Instrument> elementsToRemove) {
/* 126 */     Object[] selected = this.sourceList.getSelectedValues();
/* 127 */     for (int i = selected.length - 1; i >= 0; i--) {
/* 128 */       this.sourceListModel.removeElement(selected[i]);
/*     */     }
/* 130 */     for (Instrument elementToRemove : elementsToRemove) {
/* 131 */       this.sourceListModel.removeElement(elementToRemove.toString());
/*     */     }
/* 133 */     this.sourceList.getSelectionModel().clearSelection();
/*     */   }
/*     */ 
/*     */   private void clearDestinationSelected() {
/* 137 */     Object[] selected = this.destList.getSelectedValues();
/* 138 */     for (int i = selected.length - 1; i >= 0; i--) {
/* 139 */       this.destListModel.removeElement(selected[i]);
/*     */     }
/* 141 */     this.destList.getSelectionModel().clearSelection();
/*     */   }
/*     */ 
/*     */   private void initScreen() {
/* 145 */     setTitle("frame.currency.selector");
/*     */ 
/* 147 */     this.content = new JPanel();
/* 148 */     this.content.setLayout(new BorderLayout());
/* 149 */     this.content.setSize(preferredSize);
/* 150 */     this.content.setBorder(BorderFactory.createEmptyBorder(5, 5, 3, 5));
/* 151 */     setLayout(new BorderLayout(5, 5));
/*     */ 
/* 153 */     JPanel leftPart = initLeftPart();
/* 154 */     JPanel centerPart = initCenterPart();
/* 155 */     JPanel rightPart = initRightPart();
/*     */ 
/* 157 */     this.content.add(leftPart, "West");
/* 158 */     this.content.add(centerPart, "Center");
/* 159 */     this.content.add(rightPart, "East");
/*     */ 
/* 161 */     JPanel buttonPanel = initBottomButtons();
/*     */ 
/* 163 */     this.content.add(buttonPanel, "South");
/*     */ 
/* 165 */     Container cont = getContentPane();
/* 166 */     cont.setLayout(new BoxLayout(cont, 1));
/*     */ 
/* 168 */     cont.add(new JLocalizableHeaderPanel("header.instr.selector", false));
/* 169 */     cont.add(this.content);
/*     */ 
/* 171 */     pack();
/* 172 */     setVisible(true);
/* 173 */     setResizable(false);
/*     */   }
/*     */ 
/*     */   private JPanel initBottomButtons() {
/* 177 */     JPanel buttonPanel = new JPanel();
/* 178 */     buttonPanel.setLayout(new BoxLayout(buttonPanel, 0));
/* 179 */     buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
/* 180 */     this.okButton.setPreferredSize(this.BUTTON_DIMENSION);
/* 181 */     Component itself = this;
/* 182 */     this.okButton.addActionListener(new OkButtonActionListener(itself, null));
/* 183 */     buttonPanel.add(Box.createHorizontalGlue());
/* 184 */     buttonPanel.add(this.okButton);
/* 185 */     JLocalizableButton cancellButton = new JLocalizableButton("button.cancel");
/* 186 */     cancellButton.setPreferredSize(this.BUTTON_DIMENSION);
/* 187 */     cancellButton.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent arg0) {
/* 189 */         InstrumentSelectorDialog.this.setVisible(false);
/*     */       }
/*     */     });
/* 192 */     buttonPanel.add(cancellButton);
/* 193 */     buttonPanel.add(Box.createHorizontalGlue());
/* 194 */     return buttonPanel;
/*     */   }
/*     */ 
/*     */   private JPanel initRightPart() {
/* 198 */     JPanel rightPart = new JPanel();
/* 199 */     rightPart.setLayout(new BorderLayout(0, 5));
/* 200 */     rightPart.setPreferredSize(new Dimension((int)this.content.getSize().getWidth() / 3, (int)this.content.getSize().getHeight()));
/* 201 */     this.destLabel = new JLocalizableLabel("label.selected.currencies");
/* 202 */     this.destListModel = new InstrumentsListModel("DEST");
/* 203 */     this.destList = new JList(this.destListModel);
/* 204 */     rightPart.add(this.destLabel, "North");
/* 205 */     rightPart.add(new JScrollPane(this.destList), "Center");
/* 206 */     return rightPart;
/*     */   }
/*     */ 
/*     */   private JPanel initCenterPart() {
/* 210 */     GridBagConstraints c = new GridBagConstraints();
/* 211 */     c.fill = 3;
/* 212 */     this.addButton = new JLocalizableButton("button.add.instr");
/* 213 */     this.addButton.setPreferredSize(this.BUTTON_DIMENSION);
/* 214 */     c.gridx = 2;
/* 215 */     c.gridy = 2;
/* 216 */     JPanel centerPart = new JPanel();
/* 217 */     centerPart.setLayout(new GridBagLayout());
/* 218 */     centerPart.setPreferredSize(new Dimension((int)(this.content.getSize().getWidth() / 3.0D), (int)this.content.getSize().getHeight()));
/* 219 */     centerPart.add(this.addButton, c);
/* 220 */     this.addButton.addActionListener(new AddButtonListener(null));
/*     */ 
/* 222 */     this.removeButton = new JLocalizableButton("button.remove.instr");
/* 223 */     this.removeButton.setPreferredSize(this.BUTTON_DIMENSION);
/* 224 */     c.gridx = 2;
/* 225 */     c.gridy += 2;
/* 226 */     this.removeButton.addActionListener(new RemoveButtonListener(null));
/*     */ 
/* 228 */     JLocalizableButton addAllButton = new JLocalizableButton("button.add.all.instr");
/* 229 */     addAllButton.setPreferredSize(this.BUTTON_DIMENSION);
/* 230 */     addAllButton.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 232 */         InstrumentSelectorDialog.this.destListModel.addAll(InstrumentSelectorDialog.this.sourceListModel.getAll().toArray());
/* 233 */         InstrumentSelectorDialog.this.sourceListModel.clear();
/* 234 */         InstrumentSelectorDialog.this.okButton.setEnabled(true);
/*     */       }
/*     */     });
/* 237 */     c.gridx = 2;
/* 238 */     c.gridy += 2;
/* 239 */     centerPart.add(addAllButton, c);
/*     */ 
/* 241 */     JLocalizableButton removeAllButton = new JLocalizableButton("button.remove.all.instr");
/* 242 */     removeAllButton.setPreferredSize(this.BUTTON_DIMENSION);
/* 243 */     removeAllButton.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 245 */         InstrumentSelectorDialog.this.sourceListModel.addAll(InstrumentSelectorDialog.this.destListModel.getAll().toArray());
/* 246 */         InstrumentSelectorDialog.this.destListModel.clear();
/* 247 */         InstrumentSelectorDialog.this.okButton.setEnabled(false);
/*     */       }
/*     */     });
/* 250 */     c.gridx = 2;
/* 251 */     c.gridy += 2;
/* 252 */     centerPart.add(removeAllButton, c);
/* 253 */     centerPart.validate();
/* 254 */     return centerPart;
/*     */   }
/*     */ 
/*     */   private JPanel initLeftPart() {
/* 258 */     JPanel leftPart = new JPanel();
/* 259 */     leftPart.setLayout(new BorderLayout(0, 5));
/* 260 */     leftPart.setPreferredSize(new Dimension((int)this.content.getSize().getWidth() / 3, (int)this.content.getSize().getHeight()));
/* 261 */     this.sourceLabel = new JLocalizableLabel("label.available.currencies");
/* 262 */     this.sourceListModel = new InstrumentsListModel("SOURCE");
/* 263 */     this.sourceList = new JList(this.sourceListModel);
/* 264 */     leftPart.add(this.sourceLabel, "North");
/* 265 */     leftPart.add(new JScrollPane(this.sourceList), "Center");
/* 266 */     return leftPart;
/*     */   }
/*     */ 
/*     */   private Set<Instrument> openPositionInstruments()
/*     */   {
/* 334 */     Set result = new HashSet();
/* 335 */     ClientForm clientGui = (ClientForm)GreedContext.get("clientGui");
/* 336 */     List positionList = ((PositionsTableModel)clientGui.getPositionsPanel().getTable().getModel()).getPositions();
/* 337 */     for (Position pos : positionList) {
/* 338 */       result.add(Instrument.fromString(pos.getInstrument()));
/*     */     }
/* 340 */     return result;
/*     */   }
/*     */ 
/*     */   private Set<Instrument> openOrderInstruments() {
/* 344 */     ClientForm clientGui = (ClientForm)GreedContext.get("clientGui");
/* 345 */     Set result = new HashSet();
/* 346 */     if ((clientGui != null) && (clientGui.getOrdersPanel() != null) && (clientGui.getOrdersPanel().getOrdersTable().getModel() != null))
/*     */     {
/* 349 */       OrderCommonTableModel orderModel = (OrderCommonTableModel)((TableSorter)clientGui.getOrdersPanel().getOrdersTable().getModel()).getTableModel();
/* 350 */       int i = 0; for (int n = orderModel.getRowCount(); i < n; i++) {
/* 351 */         String instrument = orderModel.getOrder(i).getInstrument();
/* 352 */         result.add(Instrument.fromString(instrument));
/*     */       }
/*     */     }
/* 355 */     return result;
/*     */   }
/*     */ 
/*     */   private List<Instrument> fetchDependantInstuments(Set<Instrument> selectedInstruments, List<Instrument> subscribedToInstruments)
/*     */   {
/* 361 */     Set newInstrumentSet = new HashSet(subscribedToInstruments);
/* 362 */     newInstrumentSet.addAll(selectedInstruments);
/*     */ 
/* 364 */     AccountStatement accountStatement = (AccountStatement)GreedContext.get("accountStatement");
/* 365 */     Currency accountCurrency = accountStatement.getLastAccountState().getCurrency();
/* 366 */     List missingPairs = new ArrayList();
/* 367 */     if (newInstrumentSet.size() > 0)
/*     */     {
/* 369 */       for (Instrument subscInstrument : new HashSet(newInstrumentSet)) {
/* 370 */         Set dependentInstruments = CurrencyConverter.getConversionDeps(subscInstrument.getSecondaryCurrency(), accountCurrency);
/*     */ 
/* 375 */         missingPairs.addAll(dependentInstruments);
/*     */       }
/*     */     }
/*     */ 
/* 379 */     Set restrictedInstruments = this.marketView.getRestrictedInstruments();
/* 380 */     LOGGER.debug("restricted instruments: " + restrictedInstruments);
/* 381 */     missingPairs.removeAll(restrictedInstruments);
/* 382 */     if (LOGGER.isDebugEnabled()) {
/* 383 */       LOGGER.debug("missing pairs = " + missingPairs);
/*     */     }
/*     */ 
/* 386 */     return missingPairs;
/*     */   }
/*     */ 
/*     */   public void initModels()
/*     */   {
/* 399 */     this.destListModel.initModel("DEST");
/* 400 */     this.sourceListModel.initModel("SOURCE");
/*     */   }
/*     */ 
/*     */   class InstrumentsListModel extends AbstractListModel
/*     */   {
/*     */     SortedSet<String> model;
/*     */     private String name;
/* 408 */     List<String> restoredSelectedInstruments = null;
/*     */ 
/*     */     public InstrumentsListModel(String name) {
/* 411 */       this.name = name;
/* 412 */       this.model = new TreeSet(new SimpleAlphabeticInstrumentComparator());
/* 413 */       initModel(name);
/*     */     }
/*     */ 
/*     */     private void initModel(String name) {
/* 417 */       this.restoredSelectedInstruments = InstrumentSelectorDialog.this.clientSettingsStorage.restoreSelectedInstruments();
/* 418 */       InstrumentSelectorDialog.LOGGER.debug("--- initiating model: " + name);
/* 419 */       InstrumentSelectorDialog.LOGGER.debug("restored = " + this.restoredSelectedInstruments);
/* 420 */       if ("SOURCE".equals(name)) {
/* 421 */         InstrumentSelectorDialog.LOGGER.debug("available: " + InstrumentSelectorDialog.this.marketView.getAvailableInstruments());
/* 422 */         this.model.removeAll(this.restoredSelectedInstruments);
/* 423 */         this.model.addAll(InstrumentSelectorDialog.this.marketView.getAvailableInstruments());
/*     */       } else {
/* 425 */         this.model.addAll(this.restoredSelectedInstruments);
/*     */       }
/* 427 */       this.model.removeAll(InstrumentSelectorDialog.this.marketView.getRestrictedInstruments());
/* 428 */       fireContentsChanged(this, 0, getSize());
/*     */     }
/*     */ 
/*     */     public int getSize() {
/* 432 */       return this.model.size();
/*     */     }
/*     */ 
/*     */     public Object getElementAt(int index) {
/* 436 */       if (this.model.size() < 1) {
/* 437 */         return null;
/*     */       }
/* 439 */       return this.model.toArray()[index];
/*     */     }
/*     */ 
/*     */     public void add(Object element)
/*     */     {
/* 444 */       if (this.model.add((String)element))
/* 445 */         fireContentsChanged(this, 0, getSize());
/*     */     }
/*     */ 
/*     */     public void addAll(Object[] elements)
/*     */     {
/* 451 */       Collection c = Arrays.asList(elements);
/* 452 */       this.model.addAll(c);
/* 453 */       fireContentsChanged(this, 0, getSize());
/*     */     }
/*     */ 
/*     */     public void clear() {
/* 457 */       this.model.clear();
/* 458 */       fireContentsChanged(this, 0, getSize());
/*     */     }
/*     */ 
/*     */     public boolean contains(Object element) {
/* 462 */       return this.model.contains(element);
/*     */     }
/*     */ 
/*     */     public Set<String> getAll() {
/* 466 */       return this.model;
/*     */     }
/*     */ 
/*     */     public boolean removeElement(Object element) {
/* 470 */       boolean removed = this.model.remove(element);
/* 471 */       if (removed) {
/* 472 */         fireContentsChanged(this, 0, getSize());
/*     */       }
/* 474 */       return removed;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class RemoveButtonListener
/*     */     implements ActionListener
/*     */   {
/*     */     private RemoveButtonListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent e)
/*     */     {
/* 391 */       Object[] selected = InstrumentSelectorDialog.this.destList.getSelectedValues();
/* 392 */       InstrumentSelectorDialog.this.addSourceElements(selected);
/* 393 */       InstrumentSelectorDialog.this.clearDestinationSelected();
/* 394 */       InstrumentSelectorDialog.this.okButton.setEnabled(0 != InstrumentSelectorDialog.this.destListModel.getSize());
/*     */     }
/*     */   }
/*     */ 
/*     */   private class AddButtonListener
/*     */     implements ActionListener
/*     */   {
/*     */     private AddButtonListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent e)
/*     */     {
/* 301 */       if (!InstrumentSelectorDialog.this.sourceList.getSelectionModel().isSelectionEmpty()) {
/* 302 */         Object[] selected = InstrumentSelectorDialog.this.sourceList.getSelectedValues();
/*     */ 
/* 304 */         Collection c = Arrays.asList(selected);
/* 305 */         Set selectedInstrumentSet = new HashSet();
/* 306 */         for (Iterator i$ = c.iterator(); i$.hasNext(); ) { Object object = i$.next();
/* 307 */           selectedInstrumentSet.add(Instrument.fromString((String)object));
/*     */         }
/*     */ 
/* 310 */         Set posInstruments = InstrumentSelectorDialog.this.openPositionInstruments();
/* 311 */         Set orderInstruments = InstrumentSelectorDialog.this.openOrderInstruments();
/* 312 */         List combinedSelected = new ArrayList(selectedInstrumentSet);
/* 313 */         combinedSelected.addAll(posInstruments);
/* 314 */         combinedSelected.addAll(orderInstruments);
/* 315 */         List dependant = InstrumentSelectorDialog.this.fetchDependantInstuments(selectedInstrumentSet, combinedSelected);
/* 316 */         combinedSelected.addAll(dependant);
/*     */ 
/* 318 */         if (combinedSelected != null) {
/* 319 */           String[] instrumentsArray = new String[combinedSelected.size()];
/* 320 */           for (int i = 0; i < combinedSelected.size(); i++) {
/* 321 */             instrumentsArray[i] = ((Instrument)combinedSelected.get(i)).toString();
/*     */           }
/* 323 */           InstrumentSelectorDialog.this.addDestinationElements(instrumentsArray);
/*     */         }
/* 325 */         InstrumentSelectorDialog.this.clearSourceSelected(combinedSelected);
/*     */       }
/*     */ 
/* 328 */       InstrumentSelectorDialog.this.okButton.setEnabled(0 != InstrumentSelectorDialog.this.destListModel.getSize());
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class OkButtonActionListener
/*     */     implements ActionListener
/*     */   {
/*     */     private final Component parent;
/*     */ 
/*     */     private OkButtonActionListener(Component itself)
/*     */     {
/* 274 */       this.parent = itself;
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent arg0) {
/* 278 */       int size = InstrumentSelectorDialog.this.destListModel.getSize();
/* 279 */       if (0 >= size) {
/* 280 */         JOptionPane.showMessageDialog(this.parent, LocalizationManager.getText("joption.pane.currency.list.cannot.be.empty"), GuiUtilsAndConstants.LABEL_SHORT_NAME, 0);
/*     */ 
/* 285 */         InstrumentSelectorDialog.this.initModels();
/* 286 */         return;
/*     */       }
/*     */ 
/* 292 */       AppActionEvent event = new InstrumentSubscribeAction(this, new HashSet(InstrumentSelectorDialog.this.destListModel.getAll()));
/* 293 */       GreedContext.publishEvent(event);
/* 294 */       InstrumentSelectorDialog.this.setVisible(false);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dialog.InstrumentSelectorDialog
 * JD-Core Version:    0.6.0
 */