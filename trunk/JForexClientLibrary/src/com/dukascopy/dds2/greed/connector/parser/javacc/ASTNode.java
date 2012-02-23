/*     */ package com.dukascopy.dds2.greed.connector.parser.javacc;
/*     */ 
/*     */ public class ASTNode extends SimpleNode
/*     */ {
/*     */   protected String text;
/*     */   protected int beginLine;
/*     */   protected int beginColumn;
/*     */   protected int endLine;
/*     */   protected int endColumn;
/*     */   protected Token token;
/*     */   protected Token specialToken;
/*     */   protected ASTNode[] astNodesChildren;
/*     */   protected String name;
/*     */   protected Token beginToken;
/*     */   protected Token endToken;
/*     */   protected int startPos;
/*     */   protected int endPos;
/*     */ 
/*     */   public ASTNode(int i)
/*     */   {
/*  21 */     super(i);
/*     */   }
/*     */   public ASTNode(CPPParser p, int i) {
/*  24 */     super(p, i);
/*     */   }
/*     */ 
/*     */   public String getText() {
/*  28 */     return this.text;
/*     */   }
/*     */ 
/*     */   public void setText(String _text) {
/*  32 */     this.text = _text;
/*     */   }
/*     */ 
/*     */   public void appendText(String _text) {
/*  36 */     this.text = new StringBuilder().append(this.text).append(_text).toString();
/*     */   }
/*     */ 
/*     */   public int getBeginLine() {
/*  40 */     return this.beginLine;
/*     */   }
/*     */ 
/*     */   public void setBeginLine(int _beginLine) {
/*  44 */     this.beginLine = _beginLine;
/*     */   }
/*     */ 
/*     */   public int getBeginColumn() {
/*  48 */     return this.beginColumn;
/*     */   }
/*     */ 
/*     */   public void setBeginColumn(int _beginColumn) {
/*  52 */     this.beginColumn = _beginColumn;
/*     */   }
/*     */ 
/*     */   public int getEndLine() {
/*  56 */     return this.endLine;
/*     */   }
/*     */ 
/*     */   public void setEndLine(int _endLine) {
/*  60 */     this.endLine = _endLine;
/*     */   }
/*     */ 
/*     */   public int getEndColumn() {
/*  64 */     return this.endColumn;
/*     */   }
/*     */ 
/*     */   public void setEndColumn(int _endColumn) {
/*  68 */     this.endColumn = _endColumn;
/*     */   }
/*     */ 
/*     */   public Token getSpecialToken() {
/*  72 */     return this.specialToken;
/*     */   }
/*     */ 
/*     */   public void setSpecialToken(Token _specialToken) {
/*  76 */     this.specialToken = _specialToken;
/*     */   }
/*     */ 
/*     */   public void setParams(String _text, int _beginLine, int _beginColumn, int _endLine, int _endColumn)
/*     */   {
/*  81 */     this.text = _text;
/*  82 */     this.beginLine = _beginLine;
/*  83 */     this.beginColumn = _beginColumn;
/*  84 */     this.endLine = _endLine;
/*  85 */     this.endColumn = _endColumn;
/*     */   }
/*     */ 
/*     */   public int getId() {
/*  89 */     return this.id;
/*     */   }
/*     */ 
/*     */   public ASTNode[] getChildren() {
/*  93 */     if ((this.astNodesChildren == null) && 
/*  94 */       (this.children != null)) {
/*  95 */       this.astNodesChildren = new ASTNode[this.children.length];
/*  96 */       for (int i = 0; i < this.astNodesChildren.length; i++) {
/*  97 */         this.astNodesChildren[i] = ((ASTNode)this.children[i]);
/*     */       }
/*     */     }
/*     */ 
/* 101 */     return this.astNodesChildren;
/*     */   }
/*     */ 
/*     */   public ASTNodeIterator iterator() {
/* 105 */     return new ASTNodeIterator(this.children);
/*     */   }
/*     */ 
/*     */   public String toString(String prefix) {
/* 109 */     return new StringBuilder().append(super.toString(prefix)).append(" ").append(this.text != null ? this.text : "").toString();
/*     */   }
/*     */   public String getName() {
/* 112 */     return this.name;
/*     */   }
/*     */   public void setName(String name) {
/* 115 */     this.name = name;
/*     */   }
/*     */   public Token getBeginToken() {
/* 118 */     return this.beginToken;
/*     */   }
/*     */   public void setBeginToken(Token beginToken) {
/* 121 */     this.beginToken = beginToken;
/*     */   }
/*     */   public Token getEndToken() {
/* 124 */     return this.endToken;
/*     */   }
/*     */   public void setEndToken(Token endToken) {
/* 127 */     this.endToken = endToken;
/*     */   }
/*     */   public int getStartPos() {
/* 130 */     return this.startPos;
/*     */   }
/*     */   public void setStartPos(int startPos) {
/* 133 */     this.startPos = startPos;
/*     */   }
/*     */   public int getEndPos() {
/* 136 */     return this.endPos;
/*     */   }
/*     */   public void setEndPos(int endPos) {
/* 139 */     this.endPos = endPos;
/*     */   }
/*     */ 
/*     */   public StringBuilder print() {
/* 143 */     StringBuilder buf = new StringBuilder();
/*     */ 
/* 145 */     if (this.text != null) {
/* 146 */       buf.append(this.text);
/*     */     }
/*     */ 
/* 149 */     if (this.children != null) {
/* 150 */       for (int i = 0; i < this.children.length; i++) {
/* 151 */         ASTNode n = (ASTNode)this.children[i];
/* 152 */         if (n != null) {
/* 153 */           buf.append(n.print());
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 158 */     return buf;
/*     */   }
/*     */ 
/*     */   public void print(StringBuilder buf)
/*     */   {
/*     */   }
/*     */ 
/*     */   public Token getToken()
/*     */   {
/* 187 */     return this.token;
/*     */   }
/*     */   public void setToken(Token token) {
/* 190 */     this.token = token;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.javacc.ASTNode
 * JD-Core Version:    0.6.0
 */