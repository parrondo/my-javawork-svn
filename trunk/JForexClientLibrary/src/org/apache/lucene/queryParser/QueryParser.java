/*      */ package org.apache.lucene.queryParser;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.io.StringReader;
/*      */ import java.text.Collator;
/*      */ import java.text.DateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import org.apache.lucene.analysis.Analyzer;
/*      */ import org.apache.lucene.analysis.CachingTokenFilter;
/*      */ import org.apache.lucene.analysis.SimpleAnalyzer;
/*      */ import org.apache.lucene.analysis.TokenStream;
/*      */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*      */ import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
/*      */ import org.apache.lucene.document.DateField;
/*      */ import org.apache.lucene.document.DateTools;
/*      */ import org.apache.lucene.document.DateTools.Resolution;
/*      */ import org.apache.lucene.index.Term;
/*      */ import org.apache.lucene.search.BooleanClause;
/*      */ import org.apache.lucene.search.BooleanClause.Occur;
/*      */ import org.apache.lucene.search.BooleanQuery;
/*      */ import org.apache.lucene.search.BooleanQuery.TooManyClauses;
/*      */ import org.apache.lucene.search.FuzzyQuery;
/*      */ import org.apache.lucene.search.MatchAllDocsQuery;
/*      */ import org.apache.lucene.search.MultiPhraseQuery;
/*      */ import org.apache.lucene.search.MultiTermQuery;
/*      */ import org.apache.lucene.search.MultiTermQuery.RewriteMethod;
/*      */ import org.apache.lucene.search.PhraseQuery;
/*      */ import org.apache.lucene.search.PrefixQuery;
/*      */ import org.apache.lucene.search.Query;
/*      */ import org.apache.lucene.search.TermQuery;
/*      */ import org.apache.lucene.search.TermRangeQuery;
/*      */ import org.apache.lucene.search.WildcardQuery;
/*      */ import org.apache.lucene.util.Version;
/*      */ import org.apache.lucene.util.VirtualMethod;
/*      */ 
/*      */ public class QueryParser
/*      */   implements QueryParserConstants
/*      */ {
/*      */   private static final int CONJ_NONE = 0;
/*      */   private static final int CONJ_AND = 1;
/*      */   private static final int CONJ_OR = 2;
/*      */   private static final int MOD_NONE = 0;
/*      */   private static final int MOD_NOT = 10;
/*      */   private static final int MOD_REQ = 11;
/*      */   public static final Operator AND_OPERATOR;
/*      */   public static final Operator OR_OPERATOR;
/*  133 */   private Operator operator = OR_OPERATOR;
/*      */ 
/*  135 */   boolean lowercaseExpandedTerms = true;
/*  136 */   MultiTermQuery.RewriteMethod multiTermRewriteMethod = MultiTermQuery.CONSTANT_SCORE_AUTO_REWRITE_DEFAULT;
/*  137 */   boolean allowLeadingWildcard = false;
/*  138 */   boolean enablePositionIncrements = true;
/*      */   Analyzer analyzer;
/*      */   String field;
/*  142 */   int phraseSlop = 0;
/*  143 */   float fuzzyMinSim = 0.5F;
/*  144 */   int fuzzyPrefixLength = 0;
/*  145 */   Locale locale = Locale.getDefault();
/*      */ 
/*  148 */   DateTools.Resolution dateResolution = null;
/*      */ 
/*  150 */   Map<String, DateTools.Resolution> fieldToDateResolution = null;
/*      */ 
/*  154 */   Collator rangeCollator = null;
/*      */ 
/*      */   @Deprecated
/*      */   private static final VirtualMethod<QueryParser> getFieldQueryMethod;
/*      */ 
/*      */   @Deprecated
/*      */   private static final VirtualMethod<QueryParser> getFieldQueryWithQuotedMethod;
/*      */ 
/*      */   @Deprecated
/*  165 */   private final boolean hasNewAPI = VirtualMethod.compareImplementationDistance(getClass(), getFieldQueryWithQuotedMethod, getFieldQueryMethod) >= 0;
/*      */   private boolean autoGeneratePhraseQueries;
/*      */   public QueryParserTokenManager token_source;
/*      */   public Token token;
/*      */   public Token jj_nt;
/*      */   private int jj_ntk;
/*      */   private Token jj_scanpos;
/*      */   private Token jj_lastpos;
/*      */   private int jj_la;
/*      */   private int jj_gen;
/* 1622 */   private final int[] jj_la1 = new int[23];
/*      */   private static int[] jj_la1_0;
/*      */   private static int[] jj_la1_1;
/* 1635 */   private final JJCalls[] jj_2_rtns = new JJCalls[1];
/* 1636 */   private boolean jj_rescan = false;
/* 1637 */   private int jj_gc = 0;
/*      */ 
/* 1704 */   private final LookaheadSuccess jj_ls = new LookaheadSuccess(null);
/*      */ 
/* 1753 */   private List<int[]> jj_expentries = new ArrayList();
/*      */   private int[] jj_expentry;
/* 1755 */   private int jj_kind = -1;
/* 1756 */   private int[] jj_lasttokens = new int[100];
/*      */   private int jj_endpos;
/*      */ 
/*  183 */   public QueryParser(Version matchVersion, String f, Analyzer a) { this(new FastCharStream(new StringReader("")));
/*  184 */     this.analyzer = a;
/*  185 */     this.field = f;
/*  186 */     if (matchVersion.onOrAfter(Version.LUCENE_29))
/*  187 */       this.enablePositionIncrements = true;
/*      */     else {
/*  189 */       this.enablePositionIncrements = false;
/*      */     }
/*  191 */     if (matchVersion.onOrAfter(Version.LUCENE_31))
/*  192 */       setAutoGeneratePhraseQueries(false);
/*      */     else
/*  194 */       setAutoGeneratePhraseQueries(true); }
/*      */ 
/*      */   public Query parse(String query) throws ParseException
/*      */   {
/*  203 */     ReInit(new FastCharStream(new StringReader(query)));
/*      */     ParseException e;
/*      */     try {
/*  206 */       Query res = TopLevelQuery(this.field);
/*  207 */       return res != null ? res : newBooleanQuery(false);
/*      */     }
/*      */     catch (ParseException tme)
/*      */     {
/*  211 */       ParseException e = new ParseException("Cannot parse '" + query + "': " + tme.getMessage());
/*  212 */       e.initCause(tme);
/*  213 */       throw e;
/*      */     }
/*      */     catch (TokenMgrError tme) {
/*  216 */       ParseException e = new ParseException("Cannot parse '" + query + "': " + tme.getMessage());
/*  217 */       e.initCause(tme);
/*  218 */       throw e;
/*      */     }
/*      */     catch (BooleanQuery.TooManyClauses tmc) {
/*  221 */       e = new ParseException("Cannot parse '" + query + "': too many boolean clauses");
/*  222 */       e.initCause(tmc);
/*  223 */     }throw e;
/*      */   }
/*      */ 
/*      */   public Analyzer getAnalyzer()
/*      */   {
/*  231 */     return this.analyzer;
/*      */   }
/*      */ 
/*      */   public String getField()
/*      */   {
/*  238 */     return this.field;
/*      */   }
/*      */ 
/*      */   public final boolean getAutoGeneratePhraseQueries()
/*      */   {
/*  245 */     return this.autoGeneratePhraseQueries;
/*      */   }
/*      */ 
/*      */   public final void setAutoGeneratePhraseQueries(boolean value)
/*      */   {
/*  258 */     if ((!value) && (!this.hasNewAPI)) {
/*  259 */       throw new IllegalArgumentException("You must implement the new API: getFieldQuery(String,String,boolean) to use setAutoGeneratePhraseQueries(false)");
/*      */     }
/*  261 */     this.autoGeneratePhraseQueries = value;
/*      */   }
/*      */ 
/*      */   public float getFuzzyMinSim()
/*      */   {
/*  268 */     return this.fuzzyMinSim;
/*      */   }
/*      */ 
/*      */   public void setFuzzyMinSim(float fuzzyMinSim)
/*      */   {
/*  276 */     this.fuzzyMinSim = fuzzyMinSim;
/*      */   }
/*      */ 
/*      */   public int getFuzzyPrefixLength()
/*      */   {
/*  284 */     return this.fuzzyPrefixLength;
/*      */   }
/*      */ 
/*      */   public void setFuzzyPrefixLength(int fuzzyPrefixLength)
/*      */   {
/*  292 */     this.fuzzyPrefixLength = fuzzyPrefixLength;
/*      */   }
/*      */ 
/*      */   public void setPhraseSlop(int phraseSlop)
/*      */   {
/*  300 */     this.phraseSlop = phraseSlop;
/*      */   }
/*      */ 
/*      */   public int getPhraseSlop()
/*      */   {
/*  307 */     return this.phraseSlop;
/*      */   }
/*      */ 
/*      */   public void setAllowLeadingWildcard(boolean allowLeadingWildcard)
/*      */   {
/*  322 */     this.allowLeadingWildcard = allowLeadingWildcard;
/*      */   }
/*      */ 
/*      */   public boolean getAllowLeadingWildcard()
/*      */   {
/*  329 */     return this.allowLeadingWildcard;
/*      */   }
/*      */ 
/*      */   public void setEnablePositionIncrements(boolean enable)
/*      */   {
/*  343 */     this.enablePositionIncrements = enable;
/*      */   }
/*      */ 
/*      */   public boolean getEnablePositionIncrements()
/*      */   {
/*  350 */     return this.enablePositionIncrements;
/*      */   }
/*      */ 
/*      */   public void setDefaultOperator(Operator op)
/*      */   {
/*  362 */     this.operator = op;
/*      */   }
/*      */ 
/*      */   public Operator getDefaultOperator()
/*      */   {
/*  371 */     return this.operator;
/*      */   }
/*      */ 
/*      */   public void setLowercaseExpandedTerms(boolean lowercaseExpandedTerms)
/*      */   {
/*  380 */     this.lowercaseExpandedTerms = lowercaseExpandedTerms;
/*      */   }
/*      */ 
/*      */   public boolean getLowercaseExpandedTerms()
/*      */   {
/*  388 */     return this.lowercaseExpandedTerms;
/*      */   }
/*      */ 
/*      */   public void setMultiTermRewriteMethod(MultiTermQuery.RewriteMethod method)
/*      */   {
/*  402 */     this.multiTermRewriteMethod = method;
/*      */   }
/*      */ 
/*      */   public MultiTermQuery.RewriteMethod getMultiTermRewriteMethod()
/*      */   {
/*  410 */     return this.multiTermRewriteMethod;
/*      */   }
/*      */ 
/*      */   public void setLocale(Locale locale)
/*      */   {
/*  417 */     this.locale = locale;
/*      */   }
/*      */ 
/*      */   public Locale getLocale()
/*      */   {
/*  424 */     return this.locale;
/*      */   }
/*      */ 
/*      */   public void setDateResolution(DateTools.Resolution dateResolution)
/*      */   {
/*  435 */     this.dateResolution = dateResolution;
/*      */   }
/*      */ 
/*      */   public void setDateResolution(String fieldName, DateTools.Resolution dateResolution)
/*      */   {
/*  445 */     if (fieldName == null) {
/*  446 */       throw new IllegalArgumentException("Field cannot be null.");
/*      */     }
/*      */ 
/*  449 */     if (this.fieldToDateResolution == null)
/*      */     {
/*  451 */       this.fieldToDateResolution = new HashMap();
/*      */     }
/*      */ 
/*  454 */     this.fieldToDateResolution.put(fieldName, dateResolution);
/*      */   }
/*      */ 
/*      */   public DateTools.Resolution getDateResolution(String fieldName)
/*      */   {
/*  464 */     if (fieldName == null) {
/*  465 */       throw new IllegalArgumentException("Field cannot be null.");
/*      */     }
/*      */ 
/*  468 */     if (this.fieldToDateResolution == null)
/*      */     {
/*  470 */       return this.dateResolution;
/*      */     }
/*      */ 
/*  473 */     DateTools.Resolution resolution = (DateTools.Resolution)this.fieldToDateResolution.get(fieldName);
/*  474 */     if (resolution == null)
/*      */     {
/*  476 */       resolution = this.dateResolution;
/*      */     }
/*      */ 
/*  479 */     return resolution;
/*      */   }
/*      */ 
/*      */   public void setRangeCollator(Collator rc)
/*      */   {
/*  495 */     this.rangeCollator = rc;
/*      */   }
/*      */ 
/*      */   public Collator getRangeCollator()
/*      */   {
/*  503 */     return this.rangeCollator;
/*      */   }
/*      */ 
/*      */   protected void addClause(List<BooleanClause> clauses, int conj, int mods, Query q)
/*      */   {
/*  511 */     if ((clauses.size() > 0) && (conj == 1)) {
/*  512 */       BooleanClause c = (BooleanClause)clauses.get(clauses.size() - 1);
/*  513 */       if (!c.isProhibited()) {
/*  514 */         c.setOccur(BooleanClause.Occur.MUST);
/*      */       }
/*      */     }
/*  517 */     if ((clauses.size() > 0) && (this.operator == AND_OPERATOR) && (conj == 2))
/*      */     {
/*  522 */       BooleanClause c = (BooleanClause)clauses.get(clauses.size() - 1);
/*  523 */       if (!c.isProhibited()) {
/*  524 */         c.setOccur(BooleanClause.Occur.SHOULD);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  529 */     if (q == null)
/*  530 */       return;
/*      */     boolean prohibited;
/*      */     boolean required;
/*  532 */     if (this.operator == OR_OPERATOR)
/*      */     {
/*  535 */       boolean prohibited = mods == 10;
/*  536 */       boolean required = mods == 11;
/*  537 */       if ((conj == 1) && (!prohibited)) {
/*  538 */         required = true;
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  543 */       prohibited = mods == 10;
/*  544 */       required = (!prohibited) && (conj != 2);
/*      */     }
/*  546 */     if ((required) && (!prohibited))
/*  547 */       clauses.add(newBooleanClause(q, BooleanClause.Occur.MUST));
/*  548 */     else if ((!required) && (!prohibited))
/*  549 */       clauses.add(newBooleanClause(q, BooleanClause.Occur.SHOULD));
/*  550 */     else if ((!required) && (prohibited))
/*  551 */       clauses.add(newBooleanClause(q, BooleanClause.Occur.MUST_NOT));
/*      */     else
/*  553 */       throw new RuntimeException("Clause cannot be both required and prohibited");
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   protected Query getFieldQuery(String field, String queryText)
/*      */     throws ParseException
/*      */   {
/*  562 */     return getFieldQuery(field, queryText, true);
/*      */   }
/*      */ 
/*      */   protected Query getFieldQuery(String field, String queryText, boolean quoted)
/*      */     throws ParseException
/*      */   {
/*      */     TokenStream source;
/*      */     try
/*      */     {
/*  574 */       source = this.analyzer.reusableTokenStream(field, new StringReader(queryText));
/*  575 */       source.reset();
/*      */     } catch (IOException e) {
/*  577 */       source = this.analyzer.tokenStream(field, new StringReader(queryText));
/*      */     }
/*  579 */     CachingTokenFilter buffer = new CachingTokenFilter(source);
/*  580 */     CharTermAttribute termAtt = null;
/*  581 */     PositionIncrementAttribute posIncrAtt = null;
/*  582 */     int numTokens = 0;
/*      */ 
/*  584 */     boolean success = false;
/*      */     try {
/*  586 */       buffer.reset();
/*  587 */       success = true;
/*      */     }
/*      */     catch (IOException e) {
/*      */     }
/*  591 */     if (success) {
/*  592 */       if (buffer.hasAttribute(CharTermAttribute.class)) {
/*  593 */         termAtt = (CharTermAttribute)buffer.getAttribute(CharTermAttribute.class);
/*      */       }
/*  595 */       if (buffer.hasAttribute(PositionIncrementAttribute.class)) {
/*  596 */         posIncrAtt = (PositionIncrementAttribute)buffer.getAttribute(PositionIncrementAttribute.class);
/*      */       }
/*      */     }
/*      */ 
/*  600 */     int positionCount = 0;
/*  601 */     boolean severalTokensAtSamePosition = false;
/*      */ 
/*  603 */     boolean hasMoreTokens = false;
/*  604 */     if (termAtt != null)
/*      */       try {
/*  606 */         hasMoreTokens = buffer.incrementToken();
/*  607 */         while (hasMoreTokens) {
/*  608 */           numTokens++;
/*  609 */           int positionIncrement = posIncrAtt != null ? posIncrAtt.getPositionIncrement() : 1;
/*  610 */           if (positionIncrement != 0)
/*  611 */             positionCount += positionIncrement;
/*      */           else {
/*  613 */             severalTokensAtSamePosition = true;
/*      */           }
/*  615 */           hasMoreTokens = buffer.incrementToken();
/*      */         }
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/*      */       }
/*      */     try
/*      */     {
/*  623 */       buffer.reset();
/*      */ 
/*  626 */       source.close();
/*      */     }
/*      */     catch (IOException e)
/*      */     {
/*      */     }
/*      */ 
/*  632 */     if (numTokens == 0)
/*  633 */       return null;
/*  634 */     if (numTokens == 1) {
/*  635 */       String term = null;
/*      */       try {
/*  637 */         boolean hasNext = buffer.incrementToken();
/*  638 */         assert (hasNext == true);
/*  639 */         term = termAtt.toString();
/*      */       }
/*      */       catch (IOException e) {
/*      */       }
/*  643 */       return newTermQuery(new Term(field, term));
/*      */     }
/*  645 */     if ((severalTokensAtSamePosition) || ((!quoted) && (!this.autoGeneratePhraseQueries))) {
/*  646 */       if ((positionCount == 1) || ((!quoted) && (!this.autoGeneratePhraseQueries)))
/*      */       {
/*  648 */         BooleanQuery q = newBooleanQuery(positionCount == 1);
/*      */ 
/*  650 */         BooleanClause.Occur occur = (positionCount > 1) && (this.operator == AND_OPERATOR) ? BooleanClause.Occur.MUST : BooleanClause.Occur.SHOULD;
/*      */ 
/*  653 */         for (int i = 0; i < numTokens; i++) {
/*  654 */           String term = null;
/*      */           try {
/*  656 */             boolean hasNext = buffer.incrementToken();
/*  657 */             assert (hasNext == true);
/*  658 */             term = termAtt.toString();
/*      */           }
/*      */           catch (IOException e)
/*      */           {
/*      */           }
/*  663 */           Query currentQuery = newTermQuery(new Term(field, term));
/*      */ 
/*  665 */           q.add(currentQuery, occur);
/*      */         }
/*  667 */         return q;
/*      */       }
/*      */ 
/*  671 */       MultiPhraseQuery mpq = newMultiPhraseQuery();
/*  672 */       mpq.setSlop(this.phraseSlop);
/*  673 */       List multiTerms = new ArrayList();
/*  674 */       int position = -1;
/*  675 */       for (int i = 0; i < numTokens; i++) {
/*  676 */         String term = null;
/*  677 */         int positionIncrement = 1;
/*      */         try {
/*  679 */           boolean hasNext = buffer.incrementToken();
/*  680 */           assert (hasNext == true);
/*  681 */           term = termAtt.toString();
/*  682 */           if (posIncrAtt != null) {
/*  683 */             positionIncrement = posIncrAtt.getPositionIncrement();
/*      */           }
/*      */         }
/*      */         catch (IOException e)
/*      */         {
/*      */         }
/*  689 */         if ((positionIncrement > 0) && (multiTerms.size() > 0)) {
/*  690 */           if (this.enablePositionIncrements)
/*  691 */             mpq.add((Term[])multiTerms.toArray(new Term[0]), position);
/*      */           else {
/*  693 */             mpq.add((Term[])multiTerms.toArray(new Term[0]));
/*      */           }
/*  695 */           multiTerms.clear();
/*      */         }
/*  697 */         position += positionIncrement;
/*  698 */         multiTerms.add(new Term(field, term));
/*      */       }
/*  700 */       if (this.enablePositionIncrements)
/*  701 */         mpq.add((Term[])multiTerms.toArray(new Term[0]), position);
/*      */       else {
/*  703 */         mpq.add((Term[])multiTerms.toArray(new Term[0]));
/*      */       }
/*  705 */       return mpq;
/*      */     }
/*      */ 
/*  709 */     PhraseQuery pq = newPhraseQuery();
/*  710 */     pq.setSlop(this.phraseSlop);
/*  711 */     int position = -1;
/*      */ 
/*  714 */     for (int i = 0; i < numTokens; i++) {
/*  715 */       String term = null;
/*  716 */       int positionIncrement = 1;
/*      */       try
/*      */       {
/*  719 */         boolean hasNext = buffer.incrementToken();
/*  720 */         assert (hasNext == true);
/*  721 */         term = termAtt.toString();
/*  722 */         if (posIncrAtt != null) {
/*  723 */           positionIncrement = posIncrAtt.getPositionIncrement();
/*      */         }
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/*      */       }
/*  729 */       if (this.enablePositionIncrements) {
/*  730 */         position += positionIncrement;
/*  731 */         pq.add(new Term(field, term), position);
/*      */       } else {
/*  733 */         pq.add(new Term(field, term));
/*      */       }
/*      */     }
/*  736 */     return pq;
/*      */   }
/*      */ 
/*      */   protected Query getFieldQuery(String field, String queryText, int slop)
/*      */     throws ParseException
/*      */   {
/*  752 */     Query query = this.hasNewAPI ? getFieldQuery(field, queryText, true) : getFieldQuery(field, queryText);
/*      */ 
/*  754 */     if ((query instanceof PhraseQuery)) {
/*  755 */       ((PhraseQuery)query).setSlop(slop);
/*      */     }
/*  757 */     if ((query instanceof MultiPhraseQuery)) {
/*  758 */       ((MultiPhraseQuery)query).setSlop(slop);
/*      */     }
/*      */ 
/*  761 */     return query;
/*      */   }
/*      */ 
/*      */   protected Query getRangeQuery(String field, String part1, String part2, boolean inclusive)
/*      */     throws ParseException
/*      */   {
/*  773 */     if (this.lowercaseExpandedTerms) {
/*  774 */       part1 = part1.toLowerCase();
/*  775 */       part2 = part2.toLowerCase();
/*      */     }
/*      */     try {
/*  778 */       DateFormat df = DateFormat.getDateInstance(3, this.locale);
/*  779 */       df.setLenient(true);
/*  780 */       Date d1 = df.parse(part1);
/*  781 */       Date d2 = df.parse(part2);
/*  782 */       if (inclusive)
/*      */       {
/*  786 */         Calendar cal = Calendar.getInstance(this.locale);
/*  787 */         cal.setTime(d2);
/*  788 */         cal.set(11, 23);
/*  789 */         cal.set(12, 59);
/*  790 */         cal.set(13, 59);
/*  791 */         cal.set(14, 999);
/*  792 */         d2 = cal.getTime();
/*      */       }
/*  794 */       DateTools.Resolution resolution = getDateResolution(field);
/*  795 */       if (resolution == null)
/*      */       {
/*  799 */         part1 = DateField.dateToString(d1);
/*  800 */         part2 = DateField.dateToString(d2);
/*      */       } else {
/*  802 */         part1 = DateTools.dateToString(d1, resolution);
/*  803 */         part2 = DateTools.dateToString(d2, resolution);
/*      */       }
/*      */     }
/*      */     catch (Exception e) {
/*      */     }
/*  808 */     return newRangeQuery(field, part1, part2, inclusive);
/*      */   }
/*      */ 
/*      */   protected BooleanQuery newBooleanQuery(boolean disableCoord)
/*      */   {
/*  817 */     return new BooleanQuery(disableCoord);
/*      */   }
/*      */ 
/*      */   protected BooleanClause newBooleanClause(Query q, BooleanClause.Occur occur)
/*      */   {
/*  827 */     return new BooleanClause(q, occur);
/*      */   }
/*      */ 
/*      */   protected Query newTermQuery(Term term)
/*      */   {
/*  836 */     return new TermQuery(term);
/*      */   }
/*      */ 
/*      */   protected PhraseQuery newPhraseQuery()
/*      */   {
/*  844 */     return new PhraseQuery();
/*      */   }
/*      */ 
/*      */   protected MultiPhraseQuery newMultiPhraseQuery()
/*      */   {
/*  852 */     return new MultiPhraseQuery();
/*      */   }
/*      */ 
/*      */   protected Query newPrefixQuery(Term prefix)
/*      */   {
/*  861 */     PrefixQuery query = new PrefixQuery(prefix);
/*  862 */     query.setRewriteMethod(this.multiTermRewriteMethod);
/*  863 */     return query;
/*      */   }
/*      */ 
/*      */   protected Query newFuzzyQuery(Term term, float minimumSimilarity, int prefixLength)
/*      */   {
/*  875 */     return new FuzzyQuery(term, minimumSimilarity, prefixLength);
/*      */   }
/*      */ 
/*      */   protected Query newRangeQuery(String field, String part1, String part2, boolean inclusive)
/*      */   {
/*  887 */     TermRangeQuery query = new TermRangeQuery(field, part1, part2, inclusive, inclusive, this.rangeCollator);
/*  888 */     query.setRewriteMethod(this.multiTermRewriteMethod);
/*  889 */     return query;
/*      */   }
/*      */ 
/*      */   protected Query newMatchAllDocsQuery()
/*      */   {
/*  897 */     return new MatchAllDocsQuery();
/*      */   }
/*      */ 
/*      */   protected Query newWildcardQuery(Term t)
/*      */   {
/*  906 */     WildcardQuery query = new WildcardQuery(t);
/*  907 */     query.setRewriteMethod(this.multiTermRewriteMethod);
/*  908 */     return query;
/*      */   }
/*      */ 
/*      */   protected Query getBooleanQuery(List<BooleanClause> clauses)
/*      */     throws ParseException
/*      */   {
/*  925 */     return getBooleanQuery(clauses, false);
/*      */   }
/*      */ 
/*      */   protected Query getBooleanQuery(List<BooleanClause> clauses, boolean disableCoord)
/*      */     throws ParseException
/*      */   {
/*  945 */     if (clauses.size() == 0) {
/*  946 */       return null;
/*      */     }
/*  948 */     BooleanQuery query = newBooleanQuery(disableCoord);
/*  949 */     for (BooleanClause clause : clauses) {
/*  950 */       query.add(clause);
/*      */     }
/*  952 */     return query;
/*      */   }
/*      */ 
/*      */   protected Query getWildcardQuery(String field, String termStr)
/*      */     throws ParseException
/*      */   {
/*  978 */     if (("*".equals(field)) && 
/*  979 */       ("*".equals(termStr))) return newMatchAllDocsQuery();
/*      */ 
/*  981 */     if ((!this.allowLeadingWildcard) && ((termStr.startsWith("*")) || (termStr.startsWith("?"))))
/*  982 */       throw new ParseException("'*' or '?' not allowed as first character in WildcardQuery");
/*  983 */     if (this.lowercaseExpandedTerms) {
/*  984 */       termStr = termStr.toLowerCase();
/*      */     }
/*  986 */     Term t = new Term(field, termStr);
/*  987 */     return newWildcardQuery(t);
/*      */   }
/*      */ 
/*      */   protected Query getPrefixQuery(String field, String termStr)
/*      */     throws ParseException
/*      */   {
/* 1015 */     if ((!this.allowLeadingWildcard) && (termStr.startsWith("*")))
/* 1016 */       throw new ParseException("'*' not allowed as first character in PrefixQuery");
/* 1017 */     if (this.lowercaseExpandedTerms) {
/* 1018 */       termStr = termStr.toLowerCase();
/*      */     }
/* 1020 */     Term t = new Term(field, termStr);
/* 1021 */     return newPrefixQuery(t);
/*      */   }
/*      */ 
/*      */   protected Query getFuzzyQuery(String field, String termStr, float minSimilarity)
/*      */     throws ParseException
/*      */   {
/* 1037 */     if (this.lowercaseExpandedTerms) {
/* 1038 */       termStr = termStr.toLowerCase();
/*      */     }
/* 1040 */     Term t = new Term(field, termStr);
/* 1041 */     return newFuzzyQuery(t, minSimilarity, this.fuzzyPrefixLength);
/*      */   }
/*      */ 
/*      */   private String discardEscapeChar(String input)
/*      */     throws ParseException
/*      */   {
/* 1054 */     char[] output = new char[input.length()];
/*      */ 
/* 1059 */     int length = 0;
/*      */ 
/* 1063 */     boolean lastCharWasEscapeChar = false;
/*      */ 
/* 1067 */     int codePointMultiplier = 0;
/*      */ 
/* 1070 */     int codePoint = 0;
/*      */ 
/* 1072 */     for (int i = 0; i < input.length(); i++) {
/* 1073 */       char curChar = input.charAt(i);
/* 1074 */       if (codePointMultiplier > 0) {
/* 1075 */         codePoint += hexToInt(curChar) * codePointMultiplier;
/* 1076 */         codePointMultiplier >>>= 4;
/* 1077 */         if (codePointMultiplier == 0) {
/* 1078 */           output[(length++)] = (char)codePoint;
/* 1079 */           codePoint = 0;
/*      */         }
/* 1081 */       } else if (lastCharWasEscapeChar) {
/* 1082 */         if (curChar == 'u')
/*      */         {
/* 1084 */           codePointMultiplier = 4096;
/*      */         }
/*      */         else {
/* 1087 */           output[length] = curChar;
/* 1088 */           length++;
/*      */         }
/* 1090 */         lastCharWasEscapeChar = false;
/*      */       }
/* 1092 */       else if (curChar == '\\') {
/* 1093 */         lastCharWasEscapeChar = true;
/*      */       } else {
/* 1095 */         output[length] = curChar;
/* 1096 */         length++;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1101 */     if (codePointMultiplier > 0) {
/* 1102 */       throw new ParseException("Truncated unicode escape sequence.");
/*      */     }
/*      */ 
/* 1105 */     if (lastCharWasEscapeChar) {
/* 1106 */       throw new ParseException("Term can not end with escape character.");
/*      */     }
/*      */ 
/* 1109 */     return new String(output, 0, length);
/*      */   }
/*      */ 
/*      */   private static final int hexToInt(char c) throws ParseException
/*      */   {
/* 1114 */     if (('0' <= c) && (c <= '9'))
/* 1115 */       return c - '0';
/* 1116 */     if (('a' <= c) && (c <= 'f'))
/* 1117 */       return c - 'a' + 10;
/* 1118 */     if (('A' <= c) && (c <= 'F')) {
/* 1119 */       return c - 'A' + 10;
/*      */     }
/* 1121 */     throw new ParseException("None-hex character in unicode escape sequence: " + c);
/*      */   }
/*      */ 
/*      */   public static String escape(String s)
/*      */   {
/* 1130 */     StringBuilder sb = new StringBuilder();
/* 1131 */     for (int i = 0; i < s.length(); i++) {
/* 1132 */       char c = s.charAt(i);
/*      */ 
/* 1134 */       if ((c == '\\') || (c == '+') || (c == '-') || (c == '!') || (c == '(') || (c == ')') || (c == ':') || (c == '^') || (c == '[') || (c == ']') || (c == '"') || (c == '{') || (c == '}') || (c == '~') || (c == '*') || (c == '?') || (c == '|') || (c == '&'))
/*      */       {
/* 1137 */         sb.append('\\');
/*      */       }
/* 1139 */       sb.append(c);
/*      */     }
/* 1141 */     return sb.toString();
/*      */   }
/*      */ 
/*      */   public static void main(String[] args)
/*      */     throws Exception
/*      */   {
/* 1150 */     if (args.length == 0) {
/* 1151 */       System.out.println("Usage: java org.apache.lucene.queryParser.QueryParser <input>");
/* 1152 */       System.exit(0);
/*      */     }
/* 1154 */     QueryParser qp = new QueryParser(Version.LUCENE_CURRENT, "field", new SimpleAnalyzer());
/*      */ 
/* 1156 */     Query q = qp.parse(args[0]);
/* 1157 */     System.out.println(q.toString("field"));
/*      */   }
/*      */ 
/*      */   public final int Conjunction()
/*      */     throws ParseException
/*      */   {
/* 1163 */     int ret = 0;
/* 1164 */     switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */     case 8:
/*      */     case 9:
/* 1167 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */       case 8:
/* 1169 */         jj_consume_token(8);
/* 1170 */         ret = 1;
/* 1171 */         break;
/*      */       case 9:
/* 1173 */         jj_consume_token(9);
/* 1174 */         ret = 2;
/* 1175 */         break;
/*      */       default:
/* 1177 */         this.jj_la1[0] = this.jj_gen;
/* 1178 */         jj_consume_token(-1);
/* 1179 */         throw new ParseException();
/*      */       }
/*      */ 
/*      */     default:
/* 1183 */       this.jj_la1[1] = this.jj_gen;
/*      */     }
/*      */ 
/* 1186 */     return ret;
/*      */   }
/*      */ 
/*      */   public final int Modifiers() throws ParseException
/*      */   {
/* 1191 */     int ret = 0;
/* 1192 */     switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/* 1196 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */       case 11:
/* 1198 */         jj_consume_token(11);
/* 1199 */         ret = 11;
/* 1200 */         break;
/*      */       case 12:
/* 1202 */         jj_consume_token(12);
/* 1203 */         ret = 10;
/* 1204 */         break;
/*      */       case 10:
/* 1206 */         jj_consume_token(10);
/* 1207 */         ret = 10;
/* 1208 */         break;
/*      */       default:
/* 1210 */         this.jj_la1[2] = this.jj_gen;
/* 1211 */         jj_consume_token(-1);
/* 1212 */         throw new ParseException();
/*      */       }
/*      */ 
/*      */     default:
/* 1216 */       this.jj_la1[3] = this.jj_gen;
/*      */     }
/*      */ 
/* 1219 */     return ret;
/*      */   }
/*      */ 
/*      */   public final Query TopLevelQuery(String field)
/*      */     throws ParseException
/*      */   {
/* 1226 */     Query q = Query(field);
/* 1227 */     jj_consume_token(0);
/* 1228 */     return q;
/*      */   }
/*      */ 
/*      */   public final Query Query(String field) throws ParseException
/*      */   {
/* 1233 */     List clauses = new ArrayList();
/* 1234 */     Query firstQuery = null;
/*      */ 
/* 1236 */     int mods = Modifiers();
/* 1237 */     Query q = Clause(field);
/* 1238 */     addClause(clauses, 0, mods, q);
/* 1239 */     if (mods == 0)
/* 1240 */       firstQuery = q;
/*      */     while (true)
/*      */     {
/* 1243 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
/*      */       {
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/*      */       case 11:
/*      */       case 12:
/*      */       case 13:
/*      */       case 16:
/*      */       case 18:
/*      */       case 19:
/*      */       case 21:
/*      */       case 22:
/*      */       case 23:
/*      */       case 24:
/*      */       case 25:
/* 1259 */         break;
/*      */       case 14:
/*      */       case 15:
/*      */       case 17:
/*      */       case 20:
/*      */       default:
/* 1261 */         this.jj_la1[4] = this.jj_gen;
/* 1262 */         break;
/*      */       }
/* 1264 */       int conj = Conjunction();
/* 1265 */       mods = Modifiers();
/* 1266 */       q = Clause(field);
/* 1267 */       addClause(clauses, conj, mods, q);
/*      */     }
/* 1269 */     if ((clauses.size() == 1) && (firstQuery != null)) {
/* 1270 */       return firstQuery;
/*      */     }
/* 1272 */     return getBooleanQuery(clauses);
/*      */   }
/*      */ 
/*      */   public final Query Clause(String field)
/*      */     throws ParseException
/*      */   {
/* 1279 */     Token fieldToken = null; Token boost = null;
/* 1280 */     if (jj_2_1(2))
/* 1281 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */       case 19:
/* 1283 */         fieldToken = jj_consume_token(19);
/* 1284 */         jj_consume_token(15);
/* 1285 */         field = discardEscapeChar(fieldToken.image);
/* 1286 */         break;
/*      */       case 16:
/* 1288 */         jj_consume_token(16);
/* 1289 */         jj_consume_token(15);
/* 1290 */         field = "*";
/* 1291 */         break;
/*      */       default:
/* 1293 */         this.jj_la1[5] = this.jj_gen;
/* 1294 */         jj_consume_token(-1);
/* 1295 */         throw new ParseException();
/*      */       }
/*      */     Query q;
/* 1300 */     switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */     case 16:
/*      */     case 18:
/*      */     case 19:
/*      */     case 21:
/*      */     case 22:
/*      */     case 23:
/*      */     case 24:
/*      */     case 25:
/* 1309 */       q = Term(field);
/* 1310 */       break;
/*      */     case 13:
/* 1312 */       jj_consume_token(13);
/* 1313 */       q = Query(field);
/* 1314 */       jj_consume_token(14);
/* 1315 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */       case 17:
/* 1317 */         jj_consume_token(17);
/* 1318 */         boost = jj_consume_token(25);
/* 1319 */         break;
/*      */       default:
/* 1321 */         this.jj_la1[6] = this.jj_gen;
/*      */       }
/*      */ 
/* 1324 */       break;
/*      */     case 14:
/*      */     case 15:
/*      */     case 17:
/*      */     case 20:
/*      */     default:
/* 1326 */       this.jj_la1[7] = this.jj_gen;
/* 1327 */       jj_consume_token(-1);
/* 1328 */       throw new ParseException();
/*      */     }
/* 1330 */     if (boost != null) {
/* 1331 */       float f = 1.0F;
/*      */       try {
/* 1333 */         f = Float.valueOf(boost.image).floatValue();
/* 1334 */         q.setBoost(f); } catch (Exception ignored) {
/*      */       }
/*      */     }
/* 1337 */     return q;
/*      */   }
/*      */ 
/*      */   public final Query Term(String field) throws ParseException
/*      */   {
/* 1342 */     Token boost = null; Token fuzzySlop = null;
/* 1343 */     boolean prefix = false;
/* 1344 */     boolean wildcard = false;
/* 1345 */     boolean fuzzy = false;
/*      */     Token term;
/*      */     Query q;
/*      */     Token goop1;
/*      */     Token goop2;
/* 1347 */     switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */     case 16:
/*      */     case 19:
/*      */     case 21:
/*      */     case 22:
/*      */     case 25:
/* 1353 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */       case 19:
/* 1355 */         term = jj_consume_token(19);
/* 1356 */         break;
/*      */       case 16:
/* 1358 */         term = jj_consume_token(16);
/* 1359 */         wildcard = true;
/* 1360 */         break;
/*      */       case 21:
/* 1362 */         term = jj_consume_token(21);
/* 1363 */         prefix = true;
/* 1364 */         break;
/*      */       case 22:
/* 1366 */         term = jj_consume_token(22);
/* 1367 */         wildcard = true;
/* 1368 */         break;
/*      */       case 25:
/* 1370 */         term = jj_consume_token(25);
/* 1371 */         break;
/*      */       case 17:
/*      */       case 18:
/*      */       case 20:
/*      */       case 23:
/*      */       case 24:
/*      */       default:
/* 1373 */         this.jj_la1[8] = this.jj_gen;
/* 1374 */         jj_consume_token(-1);
/* 1375 */         throw new ParseException();
/*      */       }
/* 1377 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */       case 20:
/* 1379 */         fuzzySlop = jj_consume_token(20);
/* 1380 */         fuzzy = true;
/* 1381 */         break;
/*      */       default:
/* 1383 */         this.jj_la1[9] = this.jj_gen;
/*      */       }
/*      */ 
/* 1386 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */       case 17:
/* 1388 */         jj_consume_token(17);
/* 1389 */         boost = jj_consume_token(25);
/* 1390 */         switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */         case 20:
/* 1392 */           fuzzySlop = jj_consume_token(20);
/* 1393 */           fuzzy = true;
/* 1394 */           break;
/*      */         default:
/* 1396 */           this.jj_la1[10] = this.jj_gen;
/*      */         }
/*      */ 
/* 1399 */         break;
/*      */       default:
/* 1401 */         this.jj_la1[11] = this.jj_gen;
/*      */       }
/*      */ 
/* 1404 */       String termImage = discardEscapeChar(term.image);
/*      */       Query q;
/* 1405 */       if (wildcard) {
/* 1406 */         q = getWildcardQuery(field, termImage);
/*      */       }
/*      */       else
/*      */       {
/*      */         Query q;
/* 1407 */         if (prefix) {
/* 1408 */           q = getPrefixQuery(field, discardEscapeChar(term.image.substring(0, term.image.length() - 1)));
/*      */         }
/*      */         else
/*      */         {
/*      */           Query q;
/* 1411 */           if (fuzzy) {
/* 1412 */             float fms = this.fuzzyMinSim;
/*      */             try {
/* 1414 */               fms = Float.valueOf(fuzzySlop.image.substring(1)).floatValue(); } catch (Exception ignored) {
/*      */             }
/* 1416 */             if ((fms < 0.0F) || (fms > 1.0F)) {
/* 1417 */               throw new ParseException("Minimum similarity for a FuzzyQuery has to be between 0.0f and 1.0f !");
/*      */             }
/* 1419 */             q = getFuzzyQuery(field, termImage, fms);
/*      */           } else {
/* 1421 */             q = this.hasNewAPI ? getFieldQuery(field, termImage, false) : getFieldQuery(field, termImage);
/*      */           }
/*      */         }
/*      */       }
/* 1423 */       break;
/*      */     case 23:
/* 1425 */       jj_consume_token(23);
/* 1426 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */       case 29:
/* 1428 */         goop1 = jj_consume_token(29);
/* 1429 */         break;
/*      */       case 28:
/* 1431 */         goop1 = jj_consume_token(28);
/* 1432 */         break;
/*      */       default:
/* 1434 */         this.jj_la1[12] = this.jj_gen;
/* 1435 */         jj_consume_token(-1);
/* 1436 */         throw new ParseException();
/*      */       }
/* 1438 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */       case 26:
/* 1440 */         jj_consume_token(26);
/* 1441 */         break;
/*      */       default:
/* 1443 */         this.jj_la1[13] = this.jj_gen;
/*      */       }
/*      */ 
/* 1446 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */       case 29:
/* 1448 */         goop2 = jj_consume_token(29);
/* 1449 */         break;
/*      */       case 28:
/* 1451 */         goop2 = jj_consume_token(28);
/* 1452 */         break;
/*      */       default:
/* 1454 */         this.jj_la1[14] = this.jj_gen;
/* 1455 */         jj_consume_token(-1);
/* 1456 */         throw new ParseException();
/*      */       }
/* 1458 */       jj_consume_token(27);
/* 1459 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */       case 17:
/* 1461 */         jj_consume_token(17);
/* 1462 */         boost = jj_consume_token(25);
/* 1463 */         break;
/*      */       default:
/* 1465 */         this.jj_la1[15] = this.jj_gen;
/*      */       }
/*      */ 
/* 1468 */       if (goop1.kind == 28) {
/* 1469 */         goop1.image = goop1.image.substring(1, goop1.image.length() - 1);
/*      */       }
/* 1471 */       if (goop2.kind == 28) {
/* 1472 */         goop2.image = goop2.image.substring(1, goop2.image.length() - 1);
/*      */       }
/* 1474 */       q = getRangeQuery(field, discardEscapeChar(goop1.image), discardEscapeChar(goop2.image), true);
/* 1475 */       break;
/*      */     case 24:
/* 1477 */       jj_consume_token(24);
/* 1478 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */       case 33:
/* 1480 */         goop1 = jj_consume_token(33);
/* 1481 */         break;
/*      */       case 32:
/* 1483 */         goop1 = jj_consume_token(32);
/* 1484 */         break;
/*      */       default:
/* 1486 */         this.jj_la1[16] = this.jj_gen;
/* 1487 */         jj_consume_token(-1);
/* 1488 */         throw new ParseException();
/*      */       }
/* 1490 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */       case 30:
/* 1492 */         jj_consume_token(30);
/* 1493 */         break;
/*      */       default:
/* 1495 */         this.jj_la1[17] = this.jj_gen;
/*      */       }
/*      */ 
/* 1498 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */       case 33:
/* 1500 */         goop2 = jj_consume_token(33);
/* 1501 */         break;
/*      */       case 32:
/* 1503 */         goop2 = jj_consume_token(32);
/* 1504 */         break;
/*      */       default:
/* 1506 */         this.jj_la1[18] = this.jj_gen;
/* 1507 */         jj_consume_token(-1);
/* 1508 */         throw new ParseException();
/*      */       }
/* 1510 */       jj_consume_token(31);
/* 1511 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */       case 17:
/* 1513 */         jj_consume_token(17);
/* 1514 */         boost = jj_consume_token(25);
/* 1515 */         break;
/*      */       default:
/* 1517 */         this.jj_la1[19] = this.jj_gen;
/*      */       }
/*      */ 
/* 1520 */       if (goop1.kind == 32) {
/* 1521 */         goop1.image = goop1.image.substring(1, goop1.image.length() - 1);
/*      */       }
/* 1523 */       if (goop2.kind == 32) {
/* 1524 */         goop2.image = goop2.image.substring(1, goop2.image.length() - 1);
/*      */       }
/*      */ 
/* 1527 */       q = getRangeQuery(field, discardEscapeChar(goop1.image), discardEscapeChar(goop2.image), false);
/* 1528 */       break;
/*      */     case 18:
/* 1530 */       term = jj_consume_token(18);
/* 1531 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */       case 20:
/* 1533 */         fuzzySlop = jj_consume_token(20);
/* 1534 */         break;
/*      */       default:
/* 1536 */         this.jj_la1[20] = this.jj_gen;
/*      */       }
/*      */ 
/* 1539 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */       case 17:
/* 1541 */         jj_consume_token(17);
/* 1542 */         boost = jj_consume_token(25);
/* 1543 */         break;
/*      */       default:
/* 1545 */         this.jj_la1[21] = this.jj_gen;
/*      */       }
/*      */ 
/* 1548 */       int s = this.phraseSlop;
/*      */ 
/* 1550 */       if (fuzzySlop != null)
/*      */         try {
/* 1552 */           s = Float.valueOf(fuzzySlop.image.substring(1)).intValue();
/*      */         }
/*      */         catch (Exception ignored) {
/*      */         }
/* 1556 */       q = getFieldQuery(field, discardEscapeChar(term.image.substring(1, term.image.length() - 1)), s);
/* 1557 */       break;
/*      */     case 17:
/*      */     case 20:
/*      */     default:
/* 1559 */       this.jj_la1[22] = this.jj_gen;
/* 1560 */       jj_consume_token(-1);
/* 1561 */       throw new ParseException();
/*      */     }
/* 1563 */     if (boost != null) {
/* 1564 */       float f = 1.0F;
/*      */       try {
/* 1566 */         f = Float.valueOf(boost.image).floatValue();
/*      */       }
/*      */       catch (Exception ignored)
/*      */       {
/*      */       }
/*      */ 
/* 1575 */       if (q != null) {
/* 1576 */         q.setBoost(f);
/*      */       }
/*      */     }
/* 1579 */     return q;
/*      */   }
/*      */ 
/*      */   private boolean jj_2_1(int xla)
/*      */   {
/* 1584 */     this.jj_la = xla; this.jj_lastpos = (this.jj_scanpos = this.token);
/*      */     try { i = !jj_3_1() ? 1 : 0;
/*      */     }
/*      */     catch (LookaheadSuccess ls)
/*      */     {
/*      */       int i;
/* 1586 */       return 1; } finally {
/* 1587 */       jj_save(0, xla);
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean jj_3R_3() {
/* 1591 */     if (jj_scan_token(16)) return true;
/* 1592 */     return jj_scan_token(15);
/*      */   }
/*      */ 
/*      */   private boolean jj_3R_2()
/*      */   {
/* 1597 */     if (jj_scan_token(19)) return true;
/* 1598 */     return jj_scan_token(15);
/*      */   }
/*      */ 
/*      */   private boolean jj_3_1()
/*      */   {
/* 1604 */     Token xsp = this.jj_scanpos;
/* 1605 */     if (jj_3R_2()) {
/* 1606 */       this.jj_scanpos = xsp;
/* 1607 */       if (jj_3R_3()) return true;
/*      */     }
/* 1609 */     return false;
/*      */   }
/*      */ 
/*      */   private static void jj_la1_init_0()
/*      */   {
/* 1630 */     jj_la1_0 = new int[] { 768, 768, 7168, 7168, 65879808, 589824, 131072, 65871872, 40435712, 1048576, 1048576, 131072, 805306368, 67108864, 805306368, 131072, 0, 1073741824, 0, 131072, 1048576, 131072, 65863680 };
/*      */   }
/*      */   private static void jj_la1_init_1() {
/* 1633 */     jj_la1_1 = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 3, 0, 0, 0, 0 };
/*      */   }
/*      */ 
/*      */   protected QueryParser(CharStream stream)
/*      */   {
/* 1641 */     this.token_source = new QueryParserTokenManager(stream);
/* 1642 */     this.token = new Token();
/* 1643 */     this.jj_ntk = -1;
/* 1644 */     this.jj_gen = 0;
/* 1645 */     for (int i = 0; i < 23; i++) this.jj_la1[i] = -1;
/* 1646 */     for (int i = 0; i < this.jj_2_rtns.length; i++) this.jj_2_rtns[i] = new JJCalls();
/*      */   }
/*      */ 
/*      */   public void ReInit(CharStream stream)
/*      */   {
/* 1651 */     this.token_source.ReInit(stream);
/* 1652 */     this.token = new Token();
/* 1653 */     this.jj_ntk = -1;
/* 1654 */     this.jj_gen = 0;
/* 1655 */     for (int i = 0; i < 23; i++) this.jj_la1[i] = -1;
/* 1656 */     for (int i = 0; i < this.jj_2_rtns.length; i++) this.jj_2_rtns[i] = new JJCalls();
/*      */   }
/*      */ 
/*      */   protected QueryParser(QueryParserTokenManager tm)
/*      */   {
/* 1661 */     this.token_source = tm;
/* 1662 */     this.token = new Token();
/* 1663 */     this.jj_ntk = -1;
/* 1664 */     this.jj_gen = 0;
/* 1665 */     for (int i = 0; i < 23; i++) this.jj_la1[i] = -1;
/* 1666 */     for (int i = 0; i < this.jj_2_rtns.length; i++) this.jj_2_rtns[i] = new JJCalls();
/*      */   }
/*      */ 
/*      */   public void ReInit(QueryParserTokenManager tm)
/*      */   {
/* 1671 */     this.token_source = tm;
/* 1672 */     this.token = new Token();
/* 1673 */     this.jj_ntk = -1;
/* 1674 */     this.jj_gen = 0;
/* 1675 */     for (int i = 0; i < 23; i++) this.jj_la1[i] = -1;
/* 1676 */     for (int i = 0; i < this.jj_2_rtns.length; i++) this.jj_2_rtns[i] = new JJCalls();
/*      */   }
/*      */ 
/*      */   private Token jj_consume_token(int kind)
/*      */     throws ParseException
/*      */   {
/* 1681 */     Token oldToken;
/* 1681 */     if ((oldToken = this.token).next != null) this.token = this.token.next; else
/* 1682 */       this.token = (this.token.next = this.token_source.getNextToken());
/* 1683 */     this.jj_ntk = -1;
/* 1684 */     if (this.token.kind == kind) {
/* 1685 */       this.jj_gen += 1;
/* 1686 */       if (++this.jj_gc > 100) {
/* 1687 */         this.jj_gc = 0;
/* 1688 */         for (int i = 0; i < this.jj_2_rtns.length; i++) {
/* 1689 */           JJCalls c = this.jj_2_rtns[i];
/* 1690 */           while (c != null) {
/* 1691 */             if (c.gen < this.jj_gen) c.first = null;
/* 1692 */             c = c.next;
/*      */           }
/*      */         }
/*      */       }
/* 1696 */       return this.token;
/*      */     }
/* 1698 */     this.token = oldToken;
/* 1699 */     this.jj_kind = kind;
/* 1700 */     throw generateParseException();
/*      */   }
/*      */ 
/*      */   private boolean jj_scan_token(int kind)
/*      */   {
/* 1706 */     if (this.jj_scanpos == this.jj_lastpos) {
/* 1707 */       this.jj_la -= 1;
/* 1708 */       if (this.jj_scanpos.next == null)
/* 1709 */         this.jj_lastpos = (this.jj_scanpos = this.jj_scanpos.next = this.token_source.getNextToken());
/*      */       else
/* 1711 */         this.jj_lastpos = (this.jj_scanpos = this.jj_scanpos.next);
/*      */     }
/*      */     else {
/* 1714 */       this.jj_scanpos = this.jj_scanpos.next;
/*      */     }
/* 1716 */     if (this.jj_rescan) {
/* 1717 */       int i = 0; Token tok = this.token;
/* 1718 */       for (; (tok != null) && (tok != this.jj_scanpos); tok = tok.next) i++;
/* 1719 */       if (tok != null) jj_add_error_token(kind, i);
/*      */     }
/* 1721 */     if (this.jj_scanpos.kind != kind) return true;
/* 1722 */     if ((this.jj_la == 0) && (this.jj_scanpos == this.jj_lastpos)) throw this.jj_ls;
/* 1723 */     return false;
/*      */   }
/*      */ 
/*      */   public final Token getNextToken()
/*      */   {
/* 1729 */     if (this.token.next != null) this.token = this.token.next; else
/* 1730 */       this.token = (this.token.next = this.token_source.getNextToken());
/* 1731 */     this.jj_ntk = -1;
/* 1732 */     this.jj_gen += 1;
/* 1733 */     return this.token;
/*      */   }
/*      */ 
/*      */   public final Token getToken(int index)
/*      */   {
/* 1738 */     Token t = this.token;
/* 1739 */     for (int i = 0; i < index; i++) {
/* 1740 */       if (t.next != null) t = t.next; else
/* 1741 */         t = t.next = this.token_source.getNextToken();
/*      */     }
/* 1743 */     return t;
/*      */   }
/*      */ 
/*      */   private int jj_ntk() {
/* 1747 */     if ((this.jj_nt = this.token.next) == null) {
/* 1748 */       return this.jj_ntk = (this.token.next = this.token_source.getNextToken()).kind;
/*      */     }
/* 1750 */     return this.jj_ntk = this.jj_nt.kind;
/*      */   }
/*      */ 
/*      */   private void jj_add_error_token(int kind, int pos)
/*      */   {
/* 1760 */     if (pos >= 100) return;
/* 1761 */     if (pos == this.jj_endpos + 1) {
/* 1762 */       this.jj_lasttokens[(this.jj_endpos++)] = kind;
/* 1763 */     } else if (this.jj_endpos != 0) {
/* 1764 */       this.jj_expentry = new int[this.jj_endpos];
/* 1765 */       for (int i = 0; i < this.jj_endpos; i++) {
/* 1766 */         this.jj_expentry[i] = this.jj_lasttokens[i];
/*      */       }
/* 1768 */       for (Iterator it = this.jj_expentries.iterator(); it.hasNext(); ) {
/* 1769 */         int[] oldentry = (int[])(int[])it.next();
/* 1770 */         if (oldentry.length == this.jj_expentry.length) {
/* 1771 */           for (int i = 0; ; i++) { if (i >= this.jj_expentry.length) break label163; if (oldentry[i] != this.jj_expentry[i]) {
/*      */               break;
/*      */             }
/*      */           }
/* 1776 */           this.jj_expentries.add(this.jj_expentry);
/* 1777 */           break;
/*      */         }
/*      */       }
/* 1780 */       label163: if (pos != 0) { tmp193_192 = pos; this.jj_endpos = tmp193_192; this.jj_lasttokens[(tmp193_192 - 1)] = kind; }
/*      */     }
/*      */   }
/*      */ 
/*      */   public ParseException generateParseException()
/*      */   {
/* 1786 */     this.jj_expentries.clear();
/* 1787 */     boolean[] la1tokens = new boolean[34];
/* 1788 */     if (this.jj_kind >= 0) {
/* 1789 */       la1tokens[this.jj_kind] = true;
/* 1790 */       this.jj_kind = -1;
/*      */     }
/* 1792 */     for (int i = 0; i < 23; i++) {
/* 1793 */       if (this.jj_la1[i] == this.jj_gen) {
/* 1794 */         for (int j = 0; j < 32; j++) {
/* 1795 */           if ((jj_la1_0[i] & 1 << j) != 0) {
/* 1796 */             la1tokens[j] = true;
/*      */           }
/* 1798 */           if ((jj_la1_1[i] & 1 << j) != 0) {
/* 1799 */             la1tokens[(32 + j)] = true;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1804 */     for (int i = 0; i < 34; i++) {
/* 1805 */       if (la1tokens[i] != 0) {
/* 1806 */         this.jj_expentry = new int[1];
/* 1807 */         this.jj_expentry[0] = i;
/* 1808 */         this.jj_expentries.add(this.jj_expentry);
/*      */       }
/*      */     }
/* 1811 */     this.jj_endpos = 0;
/* 1812 */     jj_rescan_token();
/* 1813 */     jj_add_error_token(0, 0);
/* 1814 */     int[][] exptokseq = new int[this.jj_expentries.size()][];
/* 1815 */     for (int i = 0; i < this.jj_expentries.size(); i++) {
/* 1816 */       exptokseq[i] = ((int[])this.jj_expentries.get(i));
/*      */     }
/* 1818 */     return new ParseException(this.token, exptokseq, tokenImage);
/*      */   }
/*      */ 
/*      */   public final void enable_tracing()
/*      */   {
/*      */   }
/*      */ 
/*      */   public final void disable_tracing()
/*      */   {
/*      */   }
/*      */ 
/*      */   private void jj_rescan_token() {
/* 1830 */     this.jj_rescan = true;
/* 1831 */     for (int i = 0; i < 1; i++)
/*      */       try {
/* 1833 */         JJCalls p = this.jj_2_rtns[i];
/*      */         do {
/* 1835 */           if (p.gen > this.jj_gen) {
/* 1836 */             this.jj_la = p.arg; this.jj_lastpos = (this.jj_scanpos = p.first);
/* 1837 */             switch (i) { case 0:
/* 1838 */               jj_3_1();
/*      */             }
/*      */           }
/* 1841 */           p = p.next;
/* 1842 */         }while (p != null);
/*      */       } catch (LookaheadSuccess ls) {
/*      */       }
/* 1845 */     this.jj_rescan = false;
/*      */   }
/*      */ 
/*      */   private void jj_save(int index, int xla) {
/* 1849 */     JJCalls p = this.jj_2_rtns[index];
/* 1850 */     while (p.gen > this.jj_gen) {
/* 1851 */       if (p.next == null) { p = p.next = new JJCalls(); break; }
/* 1852 */       p = p.next;
/*      */     }
/* 1854 */     p.gen = (this.jj_gen + xla - this.jj_la); p.first = this.token; p.arg = xla;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  128 */     AND_OPERATOR = Operator.AND;
/*      */ 
/*  130 */     OR_OPERATOR = Operator.OR;
/*      */ 
/*  158 */     getFieldQueryMethod = new VirtualMethod(QueryParser.class, "getFieldQuery", new Class[] { String.class, String.class });
/*      */ 
/*  162 */     getFieldQueryWithQuotedMethod = new VirtualMethod(QueryParser.class, "getFieldQuery", new Class[] { String.class, String.class, Boolean.TYPE });
/*      */ 
/* 1626 */     jj_la1_init_0();
/* 1627 */     jj_la1_init_1();
/*      */   }
/*      */ 
/*      */   static final class JJCalls
/*      */   {
/*      */     int gen;
/*      */     Token first;
/*      */     int arg;
/*      */     JJCalls next;
/*      */   }
/*      */ 
/*      */   private static final class LookaheadSuccess extends Error
/*      */   {
/*      */   }
/*      */ 
/*      */   public static enum Operator
/*      */   {
/*  175 */     OR, AND;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.queryParser.QueryParser
 * JD-Core Version:    0.6.0
 */