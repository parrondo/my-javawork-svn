/*     */ package org.apache.lucene.util.fst;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.BitSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.apache.lucene.util.BytesRef;
/*     */ import org.apache.lucene.util.IntsRef;
/*     */ 
/*     */ public final class Util
/*     */ {
/*     */   public static <T> T get(FST<T> fst, IntsRef input)
/*     */     throws IOException
/*     */   {
/*  37 */     assert (fst.inputType == FST.INPUT_TYPE.BYTE4);
/*     */ 
/*  40 */     FST.Arc arc = fst.getFirstArc(new FST.Arc());
/*     */ 
/*  43 */     Object NO_OUTPUT = fst.outputs.getNoOutput();
/*  44 */     Object output = NO_OUTPUT;
/*  45 */     for (int i = 0; i < input.length; i++) {
/*  46 */       if (fst.findTargetArc(input.ints[(input.offset + i)], arc, arc) == null)
/*  47 */         return null;
/*  48 */       if (arc.output != NO_OUTPUT) {
/*  49 */         output = fst.outputs.add(output, arc.output);
/*     */       }
/*     */     }
/*     */ 
/*  53 */     if (fst.findTargetArc(-1, arc, arc) == null)
/*  54 */       return null;
/*  55 */     if (arc.output != NO_OUTPUT) {
/*  56 */       return fst.outputs.add(output, arc.output);
/*     */     }
/*  58 */     return output;
/*     */   }
/*     */ 
/*     */   public static <T> T get(FST<T> fst, char[] input, int offset, int length)
/*     */     throws IOException
/*     */   {
/*  66 */     assert (fst.inputType == FST.INPUT_TYPE.BYTE4);
/*     */ 
/*  69 */     FST.Arc arc = fst.getFirstArc(new FST.Arc());
/*     */ 
/*  71 */     int charIdx = offset;
/*  72 */     int charLimit = offset + length;
/*     */ 
/*  75 */     Object NO_OUTPUT = fst.outputs.getNoOutput();
/*  76 */     Object output = NO_OUTPUT;
/*  77 */     while (charIdx < charLimit) {
/*  78 */       int utf32 = Character.codePointAt(input, charIdx);
/*  79 */       charIdx += Character.charCount(utf32);
/*     */ 
/*  81 */       if (fst.findTargetArc(utf32, arc, arc) == null)
/*  82 */         return null;
/*  83 */       if (arc.output != NO_OUTPUT) {
/*  84 */         output = fst.outputs.add(output, arc.output);
/*     */       }
/*     */     }
/*     */ 
/*  88 */     if (fst.findTargetArc(-1, arc, arc) == null)
/*  89 */       return null;
/*  90 */     if (arc.output != NO_OUTPUT) {
/*  91 */       return fst.outputs.add(output, arc.output);
/*     */     }
/*  93 */     return output;
/*     */   }
/*     */ 
/*     */   public static <T> T get(FST<T> fst, CharSequence input)
/*     */     throws IOException
/*     */   {
/* 102 */     assert (fst.inputType == FST.INPUT_TYPE.BYTE4);
/*     */ 
/* 105 */     FST.Arc arc = fst.getFirstArc(new FST.Arc());
/*     */ 
/* 107 */     int charIdx = 0;
/* 108 */     int charLimit = input.length();
/*     */ 
/* 111 */     Object NO_OUTPUT = fst.outputs.getNoOutput();
/* 112 */     Object output = NO_OUTPUT;
/*     */ 
/* 114 */     while (charIdx < charLimit) {
/* 115 */       int utf32 = Character.codePointAt(input, charIdx);
/* 116 */       charIdx += Character.charCount(utf32);
/*     */ 
/* 118 */       if (fst.findTargetArc(utf32, arc, arc) == null)
/* 119 */         return null;
/* 120 */       if (arc.output != NO_OUTPUT) {
/* 121 */         output = fst.outputs.add(output, arc.output);
/*     */       }
/*     */     }
/*     */ 
/* 125 */     if (fst.findTargetArc(-1, arc, arc) == null)
/* 126 */       return null;
/* 127 */     if (arc.output != NO_OUTPUT) {
/* 128 */       return fst.outputs.add(output, arc.output);
/*     */     }
/* 130 */     return output;
/*     */   }
/*     */ 
/*     */   public static <T> T get(FST<T> fst, BytesRef input)
/*     */     throws IOException
/*     */   {
/* 137 */     assert (fst.inputType == FST.INPUT_TYPE.BYTE1);
/*     */ 
/* 140 */     FST.Arc arc = fst.getFirstArc(new FST.Arc());
/*     */ 
/* 143 */     Object NO_OUTPUT = fst.outputs.getNoOutput();
/* 144 */     Object output = NO_OUTPUT;
/* 145 */     for (int i = 0; i < input.length; i++) {
/* 146 */       if (fst.findTargetArc(input.bytes[(i + input.offset)] & 0xFF, arc, arc) == null)
/* 147 */         return null;
/* 148 */       if (arc.output != NO_OUTPUT) {
/* 149 */         output = fst.outputs.add(output, arc.output);
/*     */       }
/*     */     }
/*     */ 
/* 153 */     if (fst.findTargetArc(-1, arc, arc) == null)
/* 154 */       return null;
/* 155 */     if (arc.output != NO_OUTPUT) {
/* 156 */       return fst.outputs.add(output, arc.output);
/*     */     }
/* 158 */     return output;
/*     */   }
/*     */ 
/*     */   public static <T> void toDot(FST<T> fst, Writer out, boolean sameRank, boolean labelStates)
/*     */     throws IOException
/*     */   {
/* 194 */     String expandedNodeColor = "blue";
/*     */ 
/* 198 */     FST.Arc startArc = fst.getFirstArc(new FST.Arc());
/*     */ 
/* 201 */     List thisLevelQueue = new ArrayList();
/*     */ 
/* 204 */     List nextLevelQueue = new ArrayList();
/* 205 */     nextLevelQueue.add(startArc);
/*     */ 
/* 208 */     List sameLevelStates = new ArrayList();
/*     */ 
/* 211 */     BitSet seen = new BitSet();
/* 212 */     seen.set(startArc.target);
/*     */ 
/* 215 */     String stateShape = "circle";
/*     */ 
/* 218 */     out.write("digraph FST {\n");
/* 219 */     out.write("  rankdir = LR; splines=true; concentrate=true; ordering=out; ranksep=2.5; \n");
/*     */ 
/* 221 */     if (!labelStates) {
/* 222 */       out.write("  node [shape=circle, width=.2, height=.2, style=filled]\n");
/*     */     }
/*     */ 
/* 225 */     emitDotState(out, "initial", "point", "white", "");
/* 226 */     emitDotState(out, Integer.toString(startArc.target), "circle", fst.isExpandedTarget(startArc) ? "blue" : null, "");
/*     */ 
/* 229 */     out.write("  initial -> " + startArc.target + "\n");
/*     */ 
/* 231 */     Object NO_OUTPUT = fst.outputs.getNoOutput();
/* 232 */     int level = 0;
/*     */ 
/* 234 */     while (!nextLevelQueue.isEmpty())
/*     */     {
/* 236 */       thisLevelQueue.addAll(nextLevelQueue);
/* 237 */       nextLevelQueue.clear();
/*     */ 
/* 239 */       level++;
/* 240 */       out.write("\n  // Transitions and states at level: " + level + "\n");
/* 241 */       while (!thisLevelQueue.isEmpty()) {
/* 242 */         FST.Arc arc = (FST.Arc)thisLevelQueue.remove(thisLevelQueue.size() - 1);
/*     */ 
/* 244 */         if (fst.targetHasArcs(arc))
/*     */         {
/* 246 */           int node = arc.target;
/* 247 */           fst.readFirstTargetArc(arc, arc);
/*     */           while (true)
/*     */           {
/* 251 */             if ((arc.target >= 0) && (!seen.get(arc.target))) {
/* 252 */               boolean isExpanded = fst.isExpandedTarget(arc);
/* 253 */               emitDotState(out, Integer.toString(arc.target), "circle", isExpanded ? "blue" : null, labelStates ? Integer.toString(arc.target) : "");
/*     */ 
/* 256 */               seen.set(arc.target);
/* 257 */               nextLevelQueue.add(new FST.Arc().copyFrom(arc));
/* 258 */               sameLevelStates.add(Integer.valueOf(arc.target));
/*     */             }
/*     */             String outs;
/*     */             String outs;
/* 262 */             if (arc.output != NO_OUTPUT)
/* 263 */               outs = "/" + fst.outputs.outputToString(arc.output);
/*     */             else
/* 265 */               outs = "";
/*     */             String cl;
/*     */             String cl;
/* 269 */             if (arc.label == -1)
/* 270 */               cl = "~";
/*     */             else {
/* 272 */               cl = printableLabel(arc.label);
/*     */             }
/*     */ 
/* 275 */             out.write("  " + node + " -> " + arc.target + " [label=\"" + cl + outs + "\"]\n");
/*     */ 
/* 278 */             if (arc.isLast()) {
/*     */               break;
/*     */             }
/* 281 */             fst.readNextArc(arc);
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 287 */       if ((sameRank) && (sameLevelStates.size() > 1)) {
/* 288 */         out.write("  {rank=same; ");
/* 289 */         for (Iterator i$ = sameLevelStates.iterator(); i$.hasNext(); ) { int state = ((Integer)i$.next()).intValue();
/* 290 */           out.write(state + "; ");
/*     */         }
/* 292 */         out.write(" }\n");
/*     */       }
/* 294 */       sameLevelStates.clear();
/*     */     }
/*     */ 
/* 298 */     out.write("  -1 [style=filled, color=black, shape=circle, label=\"\"]\n\n");
/* 299 */     out.write("  {rank=sink; -1 }\n");
/*     */ 
/* 301 */     out.write("}\n");
/* 302 */     out.flush();
/*     */   }
/*     */ 
/*     */   private static void emitDotState(Writer out, String name, String shape, String color, String label)
/*     */     throws IOException
/*     */   {
/* 310 */     out.write("  " + name + " [" + (shape != null ? "shape=" + shape : "") + " " + (color != null ? "color=" + color : "") + " " + (label != null ? "label=\"" + label + "\"" : "label=\"\"") + " " + "]\n");
/*     */   }
/*     */ 
/*     */   private static String printableLabel(int label)
/*     */   {
/* 322 */     if ((label >= 32) && (label <= 125)) {
/* 323 */       return Character.toString((char)label);
/*     */     }
/* 325 */     return "0x" + Integer.toHexString(label);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.fst.Util
 * JD-Core Version:    0.6.0
 */