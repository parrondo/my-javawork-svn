/*     */ package org.eclipse.jdt.internal.compiler.tool;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CoderResult;
/*     */ import java.nio.charset.CodingErrorAction;
/*     */ import java.nio.charset.IllegalCharsetNameException;
/*     */ import java.nio.charset.UnsupportedCharsetException;
/*     */ import java.util.ArrayList;
/*     */ import javax.tools.FileObject;
/*     */ 
/*     */ public final class Util
/*     */ {
/*  32 */   public static String LINE_SEPARATOR = System.getProperty("line.separator");
/*     */ 
/*     */   public static char[] getInputStreamAsCharArray(InputStream stream, int length, String encoding)
/*     */     throws IOException
/*     */   {
/* 120 */     Charset charset = null;
/*     */     try {
/* 122 */       charset = Charset.forName(encoding);
/*     */     } catch (IllegalCharsetNameException localIllegalCharsetNameException) {
/* 124 */       System.err.println("Illegal charset name : " + encoding);
/* 125 */       return null;
/*     */     } catch (UnsupportedCharsetException localUnsupportedCharsetException) {
/* 127 */       System.err.println("Unsupported charset : " + encoding);
/* 128 */       return null;
/*     */     }
/* 130 */     CharsetDecoder charsetDecoder = charset.newDecoder();
/* 131 */     charsetDecoder.onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
/* 132 */     byte[] contents = org.eclipse.jdt.internal.compiler.util.Util.getInputStreamAsByteArray(stream, length);
/* 133 */     ByteBuffer byteBuffer = ByteBuffer.allocate(contents.length);
/* 134 */     byteBuffer.put(contents);
/* 135 */     byteBuffer.flip();
/* 136 */     return charsetDecoder.decode(byteBuffer).array();
/*     */   }
/*     */ 
/*     */   public static CharSequence getCharContents(FileObject fileObject, boolean ignoreEncodingErrors, byte[] contents, String encoding) throws IOException {
/* 140 */     if (contents == null) return null;
/* 141 */     Charset charset = null;
/*     */     try {
/* 143 */       charset = Charset.forName(encoding);
/*     */     } catch (IllegalCharsetNameException localIllegalCharsetNameException) {
/* 145 */       System.err.println("Illegal charset name : " + encoding);
/* 146 */       return null;
/*     */     } catch (UnsupportedCharsetException localUnsupportedCharsetException) {
/* 148 */       System.err.println("Unsupported charset : " + encoding);
/* 149 */       return null;
/*     */     }
/* 151 */     CharsetDecoder charsetDecoder = charset.newDecoder();
/* 152 */     ByteBuffer byteBuffer = ByteBuffer.allocate(contents.length);
/* 153 */     byteBuffer.put(contents);
/* 154 */     byteBuffer.flip();
/* 155 */     if (ignoreEncodingErrors) {
/* 156 */       charsetDecoder.onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
/* 157 */       return charsetDecoder.decode(byteBuffer);
/*     */     }
/* 159 */     charsetDecoder.onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
/* 160 */     CharBuffer out = CharBuffer.allocate(contents.length);
/* 161 */     CoderResult result = null;
/* 162 */     String replacement = charsetDecoder.replacement();
/* 163 */     int replacementLength = replacement.length();
/* 164 */     EncodingErrorCollector collector = null;
/*     */     while (true) {
/* 166 */       result = charsetDecoder.decode(byteBuffer, out, true);
/* 167 */       if ((result.isMalformed()) || (result.isUnmappable()))
/*     */       {
/* 171 */         if (collector == null) {
/* 172 */           collector = new EncodingErrorCollector(fileObject, encoding);
/*     */         }
/* 174 */         reportEncodingError(collector, out.position(), result.length());
/* 175 */         if (out.position() + replacementLength >= out.capacity())
/*     */         {
/* 177 */           CharBuffer temp = CharBuffer.allocate(out.capacity() * 2);
/* 178 */           out.flip();
/* 179 */           temp.put(out);
/* 180 */           out = temp;
/*     */         }
/* 182 */         out.append(replacement);
/* 183 */         byteBuffer.position(byteBuffer.position() + result.length());
/* 184 */         continue;
/*     */       }
/* 186 */       if (!result.isOverflow()) break;
/* 187 */       CharBuffer temp = CharBuffer.allocate(out.capacity() * 2);
/* 188 */       out.flip();
/* 189 */       temp.put(out);
/* 190 */       out = temp;
/*     */     }
/*     */ 
/* 195 */     out.flip();
/* 196 */     if (collector != null) {
/* 197 */       collector.reportAllEncodingErrors(out.toString());
/*     */     }
/* 199 */     return out;
/*     */   }
/*     */ 
/*     */   private static void reportEncodingError(EncodingErrorCollector collector, int position, int length)
/*     */   {
/* 204 */     collector.collect(position, -length);
/*     */   }
/*     */ 
/*     */   public static class EncodingError
/*     */   {
/*     */     int position;
/*     */     int length;
/*     */ 
/*     */     public EncodingError(int position, int length)
/*     */     {
/*  38 */       this.position = position;
/*  39 */       this.length = length;
/*     */     }
/*     */ 
/*     */     public String getSource(char[] unitSource)
/*     */     {
/*  50 */       int startPosition = this.position;
/*  51 */       int endPosition = this.position + this.length - 1;
/*     */ 
/*  53 */       if ((startPosition > endPosition) || 
/*  54 */         ((startPosition < 0) && (endPosition < 0)) || 
/*  55 */         (unitSource.length == 0)) {
/*  56 */         return "No source available";
/*     */       }
/*  58 */       StringBuffer errorBuffer = new StringBuffer();
/*  59 */       errorBuffer.append('\t');
/*     */ 
/*  71 */       int sourceLength = unitSource.length;
/*  72 */       for (int begin = startPosition >= sourceLength ? sourceLength - 1 : startPosition; begin > 0; begin--)
/*     */       {
/*     */         char c;
/*  73 */         if (((c = unitSource[(begin - 1)]) == '\n') || (c == '\r')) break;
/*     */       }
/*  75 */       for (int end = endPosition >= sourceLength ? sourceLength - 1 : endPosition; end + 1 < sourceLength; end++)
/*     */       {
/*     */         char c;
/*  76 */         if (((c = unitSource[(end + 1)]) == '\r') || (c == '\n'))
/*     */           break;
/*     */       }
/*     */       char c;
/*  80 */       while (((c = unitSource[begin]) == ' ') || (c == '\t'))
/*     */       {
/*     */         char c;
/*  80 */         begin++;
/*     */       }
/*     */ 
/*  84 */       errorBuffer.append(unitSource, begin, end - begin + 1);
/*  85 */       errorBuffer.append(Util.LINE_SEPARATOR).append("\t");
/*     */ 
/*  88 */       for (int i = begin; i < startPosition; i++) {
/*  89 */         errorBuffer.append(unitSource[i] == '\t' ? '\t' : ' ');
/*     */       }
/*  91 */       for (int i = startPosition; i <= (endPosition >= sourceLength ? sourceLength - 1 : endPosition); i++) {
/*  92 */         errorBuffer.append('^');
/*     */       }
/*  94 */       return errorBuffer.toString(); } 
/*     */   }
/*  98 */   public static class EncodingErrorCollector { ArrayList<Util.EncodingError> encodingErrors = new ArrayList();
/*     */     FileObject fileObject;
/*     */     String encoding;
/*     */ 
/*     */     public EncodingErrorCollector(FileObject fileObject, String encoding) {
/* 103 */       this.fileObject = fileObject;
/* 104 */       this.encoding = encoding;
/*     */     }
/*     */     public void collect(int position, int length) {
/* 107 */       this.encodingErrors.add(new Util.EncodingError(position, length));
/*     */     }
/*     */ 
/*     */     public void reportAllEncodingErrors(String string) {
/* 111 */       char[] unitSource = string.toCharArray();
/* 112 */       for (Util.EncodingError error : this.encodingErrors) {
/* 113 */         System.err.println(this.fileObject.getName() + " Unmappable character for encoding " + this.encoding);
/* 114 */         System.err.println(error.getSource(unitSource));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.tool.Util
 * JD-Core Version:    0.6.0
 */