/*      */ package org.apache.lucene.index;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.text.NumberFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Comparator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.apache.lucene.document.Document;
/*      */ import org.apache.lucene.search.IndexSearcher;
/*      */ import org.apache.lucene.search.TermQuery;
/*      */ import org.apache.lucene.store.Directory;
/*      */ import org.apache.lucene.store.FSDirectory;
/*      */ import org.apache.lucene.store.IndexInput;
/*      */ import org.apache.lucene.util.BitVector;
/*      */ import org.apache.lucene.util.StringHelper;
/*      */ 
/*      */ public class CheckIndex
/*      */ {
/*      */   private PrintStream infoStream;
/*      */   private Directory dir;
/*      */   private static boolean assertsOn;
/*      */ 
/*      */   public CheckIndex(Directory dir)
/*      */   {
/*  261 */     this.dir = dir;
/*  262 */     this.infoStream = null;
/*      */   }
/*      */ 
/*      */   public void setInfoStream(PrintStream out)
/*      */   {
/*  268 */     this.infoStream = out;
/*      */   }
/*      */ 
/*      */   private void msg(String msg) {
/*  272 */     if (this.infoStream != null)
/*  273 */       this.infoStream.println(msg);
/*      */   }
/*      */ 
/*      */   public Status checkIndex()
/*      */     throws IOException
/*      */   {
/*  306 */     return checkIndex(null);
/*      */   }
/*      */ 
/*      */   public Status checkIndex(List<String> onlySegments)
/*      */     throws IOException
/*      */   {
/*  322 */     NumberFormat nf = NumberFormat.getInstance();
/*  323 */     SegmentInfos sis = new SegmentInfos();
/*  324 */     Status result = new Status();
/*  325 */     result.dir = this.dir;
/*      */     try {
/*  327 */       sis.read(this.dir);
/*      */     } catch (Throwable t) {
/*  329 */       msg("ERROR: could not read any segments file in directory");
/*  330 */       result.missingSegments = true;
/*  331 */       if (this.infoStream != null)
/*  332 */         t.printStackTrace(this.infoStream);
/*  333 */       return result;
/*      */     }
/*      */ 
/*  337 */     String oldest = Integer.toString(2147483647); String newest = Integer.toString(-2147483648);
/*  338 */     String oldSegs = null;
/*  339 */     boolean foundNonNullVersion = false;
/*  340 */     Comparator versionComparator = StringHelper.getVersionComparator();
/*  341 */     for (SegmentInfo si : sis) {
/*  342 */       String version = si.getVersion();
/*  343 */       if (version == null)
/*      */       {
/*  345 */         oldSegs = "pre-3.1";
/*  346 */       } else if (version.equals("2.x"))
/*      */       {
/*  348 */         oldSegs = "2.x";
/*      */       } else {
/*  350 */         foundNonNullVersion = true;
/*  351 */         if (versionComparator.compare(version, oldest) < 0) {
/*  352 */           oldest = version;
/*      */         }
/*  354 */         if (versionComparator.compare(version, newest) > 0) {
/*  355 */           newest = version;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  360 */     int numSegments = sis.size();
/*  361 */     String segmentsFileName = sis.getCurrentSegmentFileName();
/*  362 */     IndexInput input = null;
/*      */     try {
/*  364 */       input = this.dir.openInput(segmentsFileName);
/*      */     } catch (Throwable t) {
/*  366 */       msg("ERROR: could not open segments file in directory");
/*  367 */       if (this.infoStream != null)
/*  368 */         t.printStackTrace(this.infoStream);
/*  369 */       result.cantOpenSegments = true;
/*  370 */       return result;
/*      */     }
/*  372 */     int format = 0;
/*      */     try {
/*  374 */       format = input.readInt();
/*      */     } catch (Throwable t) {
/*  376 */       msg("ERROR: could not read segment file version in directory");
/*  377 */       if (this.infoStream != null)
/*  378 */         t.printStackTrace(this.infoStream);
/*  379 */       result.missingSegmentVersion = true;
/*  380 */       return result;
/*      */     } finally {
/*  382 */       if (input != null) {
/*  383 */         input.close();
/*      */       }
/*      */     }
/*  386 */     String sFormat = "";
/*  387 */     boolean skip = false;
/*      */ 
/*  389 */     if (format == -1)
/*  390 */       sFormat = "FORMAT [Lucene Pre-2.1]";
/*  391 */     if (format == -2) {
/*  392 */       sFormat = "FORMAT_LOCKLESS [Lucene 2.1]";
/*  393 */     } else if (format == -3) {
/*  394 */       sFormat = "FORMAT_SINGLE_NORM_FILE [Lucene 2.2]";
/*  395 */     } else if (format == -4) {
/*  396 */       sFormat = "FORMAT_SHARED_DOC_STORE [Lucene 2.3]";
/*      */     }
/*  398 */     else if (format == -5) {
/*  399 */       sFormat = "FORMAT_CHECKSUM [Lucene 2.4]";
/*  400 */     } else if (format == -6) {
/*  401 */       sFormat = "FORMAT_DEL_COUNT [Lucene 2.4]";
/*  402 */     } else if (format == -7) {
/*  403 */       sFormat = "FORMAT_HAS_PROX [Lucene 2.4]";
/*  404 */     } else if (format == -8) {
/*  405 */       sFormat = "FORMAT_USER_DATA [Lucene 2.9]";
/*  406 */     } else if (format == -9) {
/*  407 */       sFormat = "FORMAT_DIAGNOSTICS [Lucene 2.9]";
/*  408 */     } else if (format == -10) {
/*  409 */       sFormat = "FORMAT_HAS_VECTORS [Lucene 3.1]";
/*  410 */     } else if (format == -11) {
/*  411 */       sFormat = "FORMAT_3_1 [Lucene 3.1+]"; } else {
/*  412 */       if (format == -11)
/*  413 */         throw new RuntimeException("BUG: You should update this tool!");
/*  414 */       if (format < -11) {
/*  415 */         sFormat = "int=" + format + " [newer version of Lucene than this tool]";
/*  416 */         skip = true;
/*      */       } else {
/*  418 */         sFormat = format + " [Lucene 1.3 or prior]";
/*      */       }
/*      */     }
/*      */ 
/*  422 */     result.segmentsFileName = segmentsFileName;
/*  423 */     result.numSegments = numSegments;
/*  424 */     result.segmentFormat = sFormat;
/*  425 */     result.userData = sis.getUserData();
/*      */     String userDataString;
/*      */     String userDataString;
/*  427 */     if (sis.getUserData().size() > 0)
/*  428 */       userDataString = " userData=" + sis.getUserData();
/*      */     else {
/*  430 */       userDataString = "";
/*      */     }
/*      */ 
/*  433 */     String versionString = null;
/*  434 */     if (oldSegs != null) {
/*  435 */       if (foundNonNullVersion)
/*  436 */         versionString = "versions=[" + oldSegs + " .. " + newest + "]";
/*      */       else
/*  438 */         versionString = "version=" + oldSegs;
/*      */     }
/*      */     else {
/*  441 */       versionString = "versions=[" + oldest + " .. " + newest + "]";
/*      */     }
/*      */ 
/*  444 */     msg("Segments file=" + segmentsFileName + " numSegments=" + numSegments + " " + versionString + " format=" + sFormat + userDataString);
/*      */ 
/*  447 */     if (onlySegments != null) {
/*  448 */       result.partial = true;
/*  449 */       if (this.infoStream != null)
/*  450 */         this.infoStream.print("\nChecking only these segments:");
/*  451 */       for (String s : onlySegments) {
/*  452 */         if (this.infoStream != null)
/*  453 */           this.infoStream.print(" " + s);
/*      */       }
/*  455 */       result.segmentsChecked.addAll(onlySegments);
/*  456 */       msg(":");
/*      */     }
/*      */ 
/*  459 */     if (skip) {
/*  460 */       msg("\nERROR: this index appears to be created by a newer version of Lucene than this tool was compiled on; please re-compile this tool on the matching version of Lucene; exiting");
/*  461 */       result.toolOutOfDate = true;
/*  462 */       return result;
/*      */     }
/*      */ 
/*  466 */     result.newSegments = ((SegmentInfos)sis.clone());
/*  467 */     result.newSegments.clear();
/*  468 */     result.maxSegmentName = -1;
/*      */ 
/*  470 */     for (int i = 0; i < numSegments; i++) {
/*  471 */       SegmentInfo info = sis.info(i);
/*  472 */       int segmentName = Integer.parseInt(info.name.substring(1), 36);
/*  473 */       if (segmentName > result.maxSegmentName) {
/*  474 */         result.maxSegmentName = segmentName;
/*      */       }
/*  476 */       if ((onlySegments != null) && (!onlySegments.contains(info.name)))
/*      */         continue;
/*  478 */       CheckIndex.Status.SegmentInfoStatus segInfoStat = new CheckIndex.Status.SegmentInfoStatus();
/*  479 */       result.segmentInfos.add(segInfoStat);
/*  480 */       msg("  " + (1 + i) + " of " + numSegments + ": name=" + info.name + " docCount=" + info.docCount);
/*  481 */       segInfoStat.name = info.name;
/*  482 */       segInfoStat.docCount = info.docCount;
/*      */ 
/*  484 */       int toLoseDocCount = info.docCount;
/*      */ 
/*  486 */       SegmentReader reader = null;
/*      */       try
/*      */       {
/*  489 */         msg("    compound=" + info.getUseCompoundFile());
/*  490 */         segInfoStat.compound = info.getUseCompoundFile();
/*  491 */         msg("    hasProx=" + info.getHasProx());
/*  492 */         segInfoStat.hasProx = info.getHasProx();
/*  493 */         msg("    numFiles=" + info.files().size());
/*  494 */         segInfoStat.numFiles = info.files().size();
/*  495 */         segInfoStat.sizeMB = (info.sizeInBytes(true) / 1048576.0D);
/*  496 */         msg("    size (MB)=" + nf.format(segInfoStat.sizeMB));
/*  497 */         Map diagnostics = info.getDiagnostics();
/*  498 */         segInfoStat.diagnostics = diagnostics;
/*  499 */         if (diagnostics.size() > 0) {
/*  500 */           msg("    diagnostics = " + diagnostics);
/*      */         }
/*      */ 
/*  503 */         int docStoreOffset = info.getDocStoreOffset();
/*  504 */         if (docStoreOffset != -1) {
/*  505 */           msg("    docStoreOffset=" + docStoreOffset);
/*  506 */           segInfoStat.docStoreOffset = docStoreOffset;
/*  507 */           msg("    docStoreSegment=" + info.getDocStoreSegment());
/*  508 */           segInfoStat.docStoreSegment = info.getDocStoreSegment();
/*  509 */           msg("    docStoreIsCompoundFile=" + info.getDocStoreIsCompoundFile());
/*  510 */           segInfoStat.docStoreCompoundFile = info.getDocStoreIsCompoundFile();
/*      */         }
/*  512 */         String delFileName = info.getDelFileName();
/*  513 */         if (delFileName == null) {
/*  514 */           msg("    no deletions");
/*  515 */           segInfoStat.hasDeletions = false;
/*      */         }
/*      */         else {
/*  518 */           msg("    has deletions [delFileName=" + delFileName + "]");
/*  519 */           segInfoStat.hasDeletions = true;
/*  520 */           segInfoStat.deletionsFileName = delFileName;
/*      */         }
/*  522 */         if (this.infoStream != null)
/*  523 */           this.infoStream.print("    test: open reader.........");
/*  524 */         reader = SegmentReader.get(true, info, IndexReader.DEFAULT_TERMS_INDEX_DIVISOR);
/*      */ 
/*  526 */         segInfoStat.openReaderPassed = true;
/*      */ 
/*  528 */         int numDocs = reader.numDocs();
/*  529 */         toLoseDocCount = numDocs;
/*  530 */         if (reader.hasDeletions()) {
/*  531 */           if (reader.deletedDocs.count() != info.getDelCount()) {
/*  532 */             throw new RuntimeException("delete count mismatch: info=" + info.getDelCount() + " vs deletedDocs.count()=" + reader.deletedDocs.count());
/*      */           }
/*  534 */           if (reader.deletedDocs.count() > reader.maxDoc()) {
/*  535 */             throw new RuntimeException("too many deleted docs: maxDoc()=" + reader.maxDoc() + " vs deletedDocs.count()=" + reader.deletedDocs.count());
/*      */           }
/*  537 */           if (info.docCount - numDocs != info.getDelCount()) {
/*  538 */             throw new RuntimeException("delete count mismatch: info=" + info.getDelCount() + " vs reader=" + (info.docCount - numDocs));
/*      */           }
/*  540 */           segInfoStat.numDeleted = (info.docCount - numDocs);
/*  541 */           msg("OK [" + segInfoStat.numDeleted + " deleted docs]");
/*      */         } else {
/*  543 */           if (info.getDelCount() != 0) {
/*  544 */             throw new RuntimeException("delete count mismatch: info=" + info.getDelCount() + " vs reader=" + (info.docCount - numDocs));
/*      */           }
/*  546 */           msg("OK");
/*      */         }
/*  548 */         if (reader.maxDoc() != info.docCount) {
/*  549 */           throw new RuntimeException("SegmentReader.maxDoc() " + reader.maxDoc() + " != SegmentInfos.docCount " + info.docCount);
/*      */         }
/*      */ 
/*  552 */         if (this.infoStream != null) {
/*  553 */           this.infoStream.print("    test: fields..............");
/*      */         }
/*  555 */         Collection fieldNames = reader.getFieldNames(IndexReader.FieldOption.ALL);
/*  556 */         msg("OK [" + fieldNames.size() + " fields]");
/*  557 */         segInfoStat.numFields = fieldNames.size();
/*      */ 
/*  560 */         segInfoStat.fieldNormStatus = testFieldNorms(fieldNames, reader);
/*      */ 
/*  563 */         segInfoStat.termIndexStatus = testTermIndex(info, reader);
/*      */ 
/*  566 */         segInfoStat.storedFieldStatus = testStoredFields(info, reader, nf);
/*      */ 
/*  569 */         segInfoStat.termVectorStatus = testTermVectors(info, reader, nf);
/*      */ 
/*  573 */         if (segInfoStat.fieldNormStatus.error != null)
/*  574 */           throw new RuntimeException("Field Norm test failed");
/*  575 */         if (segInfoStat.termIndexStatus.error != null)
/*  576 */           throw new RuntimeException("Term Index test failed");
/*  577 */         if (segInfoStat.storedFieldStatus.error != null)
/*  578 */           throw new RuntimeException("Stored Field test failed");
/*  579 */         if (segInfoStat.termVectorStatus.error != null) {
/*  580 */           throw new RuntimeException("Term Vector test failed");
/*      */         }
/*      */ 
/*  583 */         msg("");
/*      */ 
/*  585 */         jsr 106; } catch (Throwable t) {
/*  586 */         msg("FAILED");
/*      */ 
/*  588 */         String comment = "fixIndex() would remove reference to this segment";
/*  589 */         msg("    WARNING: " + comment + "; full exception:");
/*  590 */         if (this.infoStream != null)
/*  591 */           t.printStackTrace(this.infoStream);
/*  592 */         msg("");
/*  593 */         result.totLoseDocCount += toLoseDocCount;
/*  594 */         result.numBadSegments += 1;
/*      */       }
/*      */       finally {
/*  597 */         jsr 6; } if (reader != null)
/*  598 */         reader.close(); ret;
/*      */ 
/*  602 */       result.newSegments.add((SegmentInfo)info.clone());
/*      */     }
/*      */ 
/*  605 */     if (0 == result.numBadSegments)
/*  606 */       result.clean = true;
/*      */     else {
/*  608 */       msg("WARNING: " + result.numBadSegments + " broken segments (containing " + result.totLoseDocCount + " documents) detected");
/*      */     }
/*  610 */     if ((result.validCounter = result.maxSegmentName < sis.counter ? 1 : 0) == 0) {
/*  611 */       result.clean = false;
/*  612 */       result.newSegments.counter = (result.maxSegmentName + 1);
/*  613 */       msg("ERROR: Next segment name counter " + sis.counter + " is not greater than max segment name " + result.maxSegmentName);
/*      */     }
/*      */ 
/*  616 */     if (result.clean) {
/*  617 */       msg("No problems were detected with this index.\n");
/*      */     }
/*      */ 
/*  620 */     return result;
/*      */   }
/*      */ 
/*      */   private CheckIndex.Status.FieldNormStatus testFieldNorms(Collection<String> fieldNames, SegmentReader reader)
/*      */   {
/*  627 */     CheckIndex.Status.FieldNormStatus status = new CheckIndex.Status.FieldNormStatus();
/*      */     try
/*      */     {
/*  631 */       if (this.infoStream != null) {
/*  632 */         this.infoStream.print("    test: field norms.........");
/*      */       }
/*  634 */       byte[] b = new byte[reader.maxDoc()];
/*  635 */       for (String fieldName : fieldNames) {
/*  636 */         if (reader.hasNorms(fieldName)) {
/*  637 */           reader.norms(fieldName, b, 0);
/*  638 */           status.totFields += 1L;
/*      */         }
/*      */       }
/*      */ 
/*  642 */       msg("OK [" + status.totFields + " fields]");
/*      */     } catch (Throwable e) {
/*  644 */       msg("ERROR [" + String.valueOf(e.getMessage()) + "]");
/*  645 */       status.error = e;
/*  646 */       if (this.infoStream != null) {
/*  647 */         e.printStackTrace(this.infoStream);
/*      */       }
/*      */     }
/*      */ 
/*  651 */     return status;
/*      */   }
/*      */ 
/*      */   private CheckIndex.Status.TermIndexStatus testTermIndex(SegmentInfo info, SegmentReader reader)
/*      */   {
/*  658 */     CheckIndex.Status.TermIndexStatus status = new CheckIndex.Status.TermIndexStatus();
/*      */ 
/*  660 */     IndexSearcher is = new IndexSearcher(reader);
/*      */     try
/*      */     {
/*  663 */       if (this.infoStream != null) {
/*  664 */         this.infoStream.print("    test: terms, freq, prox...");
/*      */       }
/*      */ 
/*  667 */       TermEnum termEnum = reader.terms();
/*  668 */       TermPositions termPositions = reader.termPositions();
/*      */ 
/*  671 */       MySegmentTermDocs myTermDocs = new MySegmentTermDocs(reader);
/*      */ 
/*  673 */       int maxDoc = reader.maxDoc();
/*  674 */       Term lastTerm = null;
/*  675 */       while (termEnum.next()) {
/*  676 */         status.termCount += 1L;
/*  677 */         Term term = termEnum.term();
/*  678 */         lastTerm = term;
/*      */ 
/*  680 */         int docFreq = termEnum.docFreq();
/*  681 */         termPositions.seek(term);
/*  682 */         int lastDoc = -1;
/*  683 */         int freq0 = 0;
/*  684 */         status.totFreq += docFreq;
/*  685 */         while (termPositions.next()) {
/*  686 */           freq0++;
/*  687 */           int doc = termPositions.doc();
/*  688 */           int freq = termPositions.freq();
/*  689 */           if (doc <= lastDoc)
/*  690 */             throw new RuntimeException("term " + term + ": doc " + doc + " <= lastDoc " + lastDoc);
/*  691 */           if (doc >= maxDoc) {
/*  692 */             throw new RuntimeException("term " + term + ": doc " + doc + " >= maxDoc " + maxDoc);
/*      */           }
/*  694 */           lastDoc = doc;
/*  695 */           if (freq <= 0) {
/*  696 */             throw new RuntimeException("term " + term + ": doc " + doc + ": freq " + freq + " is out of bounds");
/*      */           }
/*  698 */           int lastPos = -1;
/*  699 */           status.totPos += freq;
/*  700 */           for (int j = 0; j < freq; j++) {
/*  701 */             int pos = termPositions.nextPosition();
/*  702 */             if (pos < -1)
/*  703 */               throw new RuntimeException("term " + term + ": doc " + doc + ": pos " + pos + " is out of bounds");
/*  704 */             if (pos < lastPos)
/*  705 */               throw new RuntimeException("term " + term + ": doc " + doc + ": pos " + pos + " < lastPos " + lastPos);
/*  706 */             lastPos = pos;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  711 */         for (int idx = 0; idx < 7; idx++) {
/*  712 */           int skipDocID = (int)((idx + 1) * maxDoc / 8L);
/*  713 */           termPositions.seek(term);
/*  714 */           if (!termPositions.skipTo(skipDocID))
/*      */           {
/*      */             break;
/*      */           }
/*  718 */           int docID = termPositions.doc();
/*  719 */           if (docID < skipDocID) {
/*  720 */             throw new RuntimeException("term " + term + ": skipTo(docID=" + skipDocID + ") returned docID=" + docID);
/*      */           }
/*  722 */           int freq = termPositions.freq();
/*  723 */           if (freq <= 0) {
/*  724 */             throw new RuntimeException("termFreq " + freq + " is out of bounds");
/*      */           }
/*  726 */           int lastPosition = -1;
/*  727 */           for (int posUpto = 0; posUpto < freq; posUpto++) {
/*  728 */             int pos = termPositions.nextPosition();
/*  729 */             if (pos < 0) {
/*  730 */               throw new RuntimeException("position " + pos + " is out of bounds");
/*      */             }
/*      */ 
/*  733 */             if (pos < lastPosition) {
/*  734 */               throw new RuntimeException("position " + pos + " is < lastPosition " + lastPosition);
/*      */             }
/*  736 */             lastPosition = pos;
/*      */           }
/*      */ 
/*  739 */           if (!termPositions.next()) {
/*      */             break;
/*      */           }
/*  742 */           int nextDocID = termPositions.doc();
/*  743 */           if (nextDocID <= docID)
/*  744 */             throw new RuntimeException("term " + term + ": skipTo(docID=" + skipDocID + "), then .next() returned docID=" + nextDocID + " vs prev docID=" + docID);
/*      */         }
/*      */         int delCount;
/*      */         int delCount;
/*  752 */         if (reader.hasDeletions()) {
/*  753 */           myTermDocs.seek(term);
/*  754 */           while (myTermDocs.next());
/*  755 */           delCount = myTermDocs.delCount;
/*      */         } else {
/*  757 */           delCount = 0;
/*      */         }
/*      */ 
/*  760 */         if (freq0 + delCount != docFreq) {
/*  761 */           throw new RuntimeException("term " + term + " docFreq=" + docFreq + " != num docs seen " + freq0 + " + num docs deleted " + delCount);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  767 */       if (lastTerm != null) {
/*  768 */         is.search(new TermQuery(lastTerm), 1);
/*      */       }
/*      */ 
/*  771 */       msg("OK [" + status.termCount + " terms; " + status.totFreq + " terms/docs pairs; " + status.totPos + " tokens]");
/*      */     }
/*      */     catch (Throwable e) {
/*  774 */       msg("ERROR [" + String.valueOf(e.getMessage()) + "]");
/*  775 */       status.error = e;
/*  776 */       if (this.infoStream != null) {
/*  777 */         e.printStackTrace(this.infoStream);
/*      */       }
/*      */     }
/*      */ 
/*  781 */     return status;
/*      */   }
/*      */ 
/*      */   private CheckIndex.Status.StoredFieldStatus testStoredFields(SegmentInfo info, SegmentReader reader, NumberFormat format)
/*      */   {
/*  788 */     CheckIndex.Status.StoredFieldStatus status = new CheckIndex.Status.StoredFieldStatus();
/*      */     try
/*      */     {
/*  791 */       if (this.infoStream != null) {
/*  792 */         this.infoStream.print("    test: stored fields.......");
/*      */       }
/*      */ 
/*  796 */       for (int j = 0; j < info.docCount; j++) {
/*  797 */         if (!reader.isDeleted(j)) {
/*  798 */           status.docCount += 1;
/*  799 */           Document doc = reader.document(j);
/*  800 */           status.totFields += doc.getFields().size();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  805 */       if (status.docCount != reader.numDocs()) {
/*  806 */         throw new RuntimeException("docCount=" + status.docCount + " but saw " + status.docCount + " undeleted docs");
/*      */       }
/*      */ 
/*  809 */       msg("OK [" + status.totFields + " total field count; avg " + format.format((float)status.totFields / status.docCount) + " fields per doc]");
/*      */     }
/*      */     catch (Throwable e) {
/*  812 */       msg("ERROR [" + String.valueOf(e.getMessage()) + "]");
/*  813 */       status.error = e;
/*  814 */       if (this.infoStream != null) {
/*  815 */         e.printStackTrace(this.infoStream);
/*      */       }
/*      */     }
/*      */ 
/*  819 */     return status;
/*      */   }
/*      */ 
/*      */   private CheckIndex.Status.TermVectorStatus testTermVectors(SegmentInfo info, SegmentReader reader, NumberFormat format)
/*      */   {
/*  826 */     CheckIndex.Status.TermVectorStatus status = new CheckIndex.Status.TermVectorStatus();
/*      */     try
/*      */     {
/*  829 */       if (this.infoStream != null) {
/*  830 */         this.infoStream.print("    test: term vectors........");
/*      */       }
/*      */ 
/*  833 */       for (int j = 0; j < info.docCount; j++) {
/*  834 */         if (!reader.isDeleted(j)) {
/*  835 */           status.docCount += 1;
/*  836 */           TermFreqVector[] tfv = reader.getTermFreqVectors(j);
/*  837 */           if (tfv != null) {
/*  838 */             status.totVectors += tfv.length;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  843 */       msg("OK [" + status.totVectors + " total vector count; avg " + format.format((float)status.totVectors / status.docCount) + " term/freq vector fields per doc]");
/*      */     }
/*      */     catch (Throwable e) {
/*  846 */       msg("ERROR [" + String.valueOf(e.getMessage()) + "]");
/*  847 */       status.error = e;
/*  848 */       if (this.infoStream != null) {
/*  849 */         e.printStackTrace(this.infoStream);
/*      */       }
/*      */     }
/*      */ 
/*  853 */     return status;
/*      */   }
/*      */ 
/*      */   public void fixIndex(Status result)
/*      */     throws IOException
/*      */   {
/*  870 */     if (result.partial)
/*  871 */       throw new IllegalArgumentException("can only fix an index that was fully checked (this status checked a subset of segments)");
/*  872 */     result.newSegments.changed();
/*  873 */     result.newSegments.commit(result.dir);
/*      */   }
/*      */ 
/*      */   private static boolean testAsserts()
/*      */   {
/*  879 */     assertsOn = true;
/*  880 */     return true;
/*      */   }
/*      */ 
/*      */   private static boolean assertsOn() {
/*  884 */     assert (testAsserts());
/*  885 */     return assertsOn;
/*      */   }
/*      */ 
/*      */   public static void main(String[] args)
/*      */     throws IOException, InterruptedException
/*      */   {
/*  922 */     boolean doFix = false;
/*  923 */     List onlySegments = new ArrayList();
/*  924 */     String indexPath = null;
/*  925 */     int i = 0;
/*  926 */     while (i < args.length) {
/*  927 */       if (args[i].equals("-fix")) {
/*  928 */         doFix = true;
/*  929 */         i++; continue;
/*  930 */       }if (args[i].equals("-segment")) {
/*  931 */         if (i == args.length - 1) {
/*  932 */           System.out.println("ERROR: missing name for -segment option");
/*  933 */           System.exit(1);
/*      */         }
/*  935 */         onlySegments.add(args[(i + 1)]);
/*  936 */         i += 2; continue;
/*      */       }
/*  938 */       if (indexPath != null) {
/*  939 */         System.out.println("ERROR: unexpected extra argument '" + args[i] + "'");
/*  940 */         System.exit(1);
/*      */       }
/*  942 */       indexPath = args[i];
/*  943 */       i++;
/*      */     }
/*      */ 
/*  947 */     if (indexPath == null) {
/*  948 */       System.out.println("\nERROR: index path not specified");
/*  949 */       System.out.println("\nUsage: java org.apache.lucene.index.CheckIndex pathToIndex [-fix] [-segment X] [-segment Y]\n\n  -fix: actually write a new segments_N file, removing any problematic segments\n  -segment X: only check the specified segments.  This can be specified multiple\n              times, to check more than one segment, eg '-segment _2 -segment _a'.\n              You can't use this with the -fix option\n\n**WARNING**: -fix should only be used on an emergency basis as it will cause\ndocuments (perhaps many) to be permanently removed from the index.  Always make\na backup copy of your index before running this!  Do not run this tool on an index\nthat is actively being written to.  You have been warned!\n\nRun without -fix, this tool will open the index, report version information\nand report any exceptions it hits and what action it would take if -fix were\nspecified.  With -fix, this tool will remove any segments that have issues and\nwrite a new segments_N file.  This means all documents contained in the affected\nsegments will be removed.\n\nThis tool exits with exit code 1 if the index cannot be opened or has any\ncorruption, else 0.\n");
/*      */ 
/*  969 */       System.exit(1);
/*      */     }
/*      */ 
/*  972 */     if (!assertsOn()) {
/*  973 */       System.out.println("\nNOTE: testing will be more thorough if you run java with '-ea:org.apache.lucene...', so assertions are enabled");
/*      */     }
/*  975 */     if (onlySegments.size() == 0) {
/*  976 */       onlySegments = null;
/*  977 */     } else if (doFix) {
/*  978 */       System.out.println("ERROR: cannot specify both -fix and -segment");
/*  979 */       System.exit(1);
/*      */     }
/*      */ 
/*  982 */     System.out.println("\nOpening index @ " + indexPath + "\n");
/*  983 */     Directory dir = null;
/*      */     try {
/*  985 */       dir = FSDirectory.open(new File(indexPath));
/*      */     } catch (Throwable t) {
/*  987 */       System.out.println("ERROR: could not open directory \"" + indexPath + "\"; exiting");
/*  988 */       t.printStackTrace(System.out);
/*  989 */       System.exit(1);
/*      */     }
/*      */ 
/*  992 */     CheckIndex checker = new CheckIndex(dir);
/*  993 */     checker.setInfoStream(System.out);
/*      */ 
/*  995 */     Status result = checker.checkIndex(onlySegments);
/*  996 */     if (result.missingSegments) {
/*  997 */       System.exit(1);
/*      */     }
/*      */ 
/* 1000 */     if (!result.clean) {
/* 1001 */       if (!doFix) {
/* 1002 */         System.out.println("WARNING: would write new segments file, and " + result.totLoseDocCount + " documents would be lost, if -fix were specified\n");
/*      */       } else {
/* 1004 */         System.out.println("WARNING: " + result.totLoseDocCount + " documents will be lost\n");
/* 1005 */         System.out.println("NOTE: will write new segments file in 5 seconds; this will remove " + result.totLoseDocCount + " docs from the index. THIS IS YOUR LAST CHANCE TO CTRL+C!");
/* 1006 */         for (int s = 0; s < 5; s++) {
/* 1007 */           Thread.sleep(1000L);
/* 1008 */           System.out.println("  " + (5 - s) + "...");
/*      */         }
/* 1010 */         System.out.println("Writing...");
/* 1011 */         checker.fixIndex(result);
/* 1012 */         System.out.println("OK");
/* 1013 */         System.out.println("Wrote new segments file \"" + result.newSegments.getCurrentSegmentFileName() + "\"");
/*      */       }
/*      */     }
/* 1016 */     System.out.println("");
/*      */     int exitCode;
/*      */     int exitCode;
/* 1019 */     if (result.clean == true)
/* 1020 */       exitCode = 0;
/*      */     else
/* 1022 */       exitCode = 1;
/* 1023 */     System.exit(exitCode);
/*      */   }
/*      */ 
/*      */   private static class MySegmentTermDocs extends SegmentTermDocs
/*      */   {
/*      */     int delCount;
/*      */ 
/*      */     MySegmentTermDocs(SegmentReader p)
/*      */     {
/*  281 */       super();
/*      */     }
/*      */ 
/*      */     public void seek(Term term) throws IOException
/*      */     {
/*  286 */       super.seek(term);
/*  287 */       this.delCount = 0;
/*      */     }
/*      */ 
/*      */     protected void skippingDoc() throws IOException
/*      */     {
/*  292 */       this.delCount += 1;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class Status
/*      */   {
/*      */     public boolean clean;
/*      */     public boolean missingSegments;
/*      */     public boolean cantOpenSegments;
/*      */     public boolean missingSegmentVersion;
/*      */     public String segmentsFileName;
/*      */     public int numSegments;
/*      */     public String segmentFormat;
/*      */     public List<String> segmentsChecked;
/*      */     public boolean toolOutOfDate;
/*      */     public List<SegmentInfoStatus> segmentInfos;
/*      */     public Directory dir;
/*      */     SegmentInfos newSegments;
/*      */     public int totLoseDocCount;
/*      */     public int numBadSegments;
/*      */     public boolean partial;
/*      */     public int maxSegmentName;
/*      */     public boolean validCounter;
/*      */     public Map<String, String> userData;
/*      */ 
/*      */     public Status()
/*      */     {
/*   87 */       this.segmentsChecked = new ArrayList();
/*      */ 
/*   93 */       this.segmentInfos = new ArrayList();
/*      */     }
/*      */ 
/*      */     public static final class TermVectorStatus
/*      */     {
/*  249 */       public int docCount = 0;
/*      */ 
/*  252 */       public long totVectors = 0L;
/*      */ 
/*  255 */       public Throwable error = null;
/*      */     }
/*      */ 
/*      */     public static final class StoredFieldStatus
/*      */     {
/*  234 */       public int docCount = 0;
/*      */ 
/*  237 */       public long totFields = 0L;
/*      */ 
/*  240 */       public Throwable error = null;
/*      */     }
/*      */ 
/*      */     public static final class TermIndexStatus
/*      */     {
/*  216 */       public long termCount = 0L;
/*      */ 
/*  219 */       public long totFreq = 0L;
/*      */ 
/*  222 */       public long totPos = 0L;
/*      */ 
/*  225 */       public Throwable error = null;
/*      */     }
/*      */ 
/*      */     public static final class FieldNormStatus
/*      */     {
/*  205 */       public long totFields = 0L;
/*      */ 
/*  208 */       public Throwable error = null;
/*      */     }
/*      */ 
/*      */     public static class SegmentInfoStatus
/*      */     {
/*      */       public String name;
/*      */       public int docCount;
/*      */       public boolean compound;
/*      */       public int numFiles;
/*      */       public double sizeMB;
/*  151 */       public int docStoreOffset = -1;
/*      */       public String docStoreSegment;
/*      */       public boolean docStoreCompoundFile;
/*      */       public boolean hasDeletions;
/*      */       public String deletionsFileName;
/*      */       public int numDeleted;
/*      */       public boolean openReaderPassed;
/*      */       int numFields;
/*      */       public boolean hasProx;
/*      */       public Map<String, String> diagnostics;
/*      */       public CheckIndex.Status.FieldNormStatus fieldNormStatus;
/*      */       public CheckIndex.Status.TermIndexStatus termIndexStatus;
/*      */       public CheckIndex.Status.StoredFieldStatus storedFieldStatus;
/*      */       public CheckIndex.Status.TermVectorStatus termVectorStatus;
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.CheckIndex
 * JD-Core Version:    0.6.0
 */