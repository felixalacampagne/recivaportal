package com.felixalacampagne.recivaportal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils
{
	static final Logger log = LoggerFactory.getLogger(Utils.class);
	
	public static String dumpBuffer(byte[] data)
   {
		return dumpBuffer(data, false);
   }
	
	public static String dumpBuffer(byte[] data, boolean wide)
   {
		int widthSize = wide ? 32 : 16;
      int      endPt = widthSize;
      int      len = data.length;
      byte[]   tempBuffer = new byte[widthSize];
      StringBuilder sb = new StringBuilder();

      log.trace("dumpBuffer: entered");
   	if (wide)
      {
      	sb.append("Address  0 1 2 3  4 5 6 7  8 9 A B  C D E F  0 1 2 3  4 5 6 7  8 9 A B  C D E F  0123456789ABCDEF0123456789ABCDEF\n");
      	sb.append("------- -------- -------- -------- -------- -------- -------- -------- --------  --------------------------------\n");
      }
   	else
      {
         sb.append("Address   0  1  2  3   4  5  6  7   8  9  A  B   C  D  E  F  0123456789ABCDEF\n");
         sb.append("------- ------------ ------------ ------------ ------------  ----------------\n");
      }
 
   	for (int i=0; i < len; i+=widthSize)
   	{
   		if (i+widthSize >= len)
   			endPt = len - i;
 
   		for (int j=0; j < endPt; j++)
   		{
   			tempBuffer[j] = data[i+j];
   		}
   		sb.append(formatHex(tempBuffer, (i+widthSize < len?widthSize:len-i), i, widthSize )).append("\n");
      }
      log.trace("dumpBuffer: exit");
      return sb.toString();
   }
 
   /**
    * Format an array of bytes into a hex display
    * @param src a portion of the byte array
    * @param len length of this part of the byte array
    * @param index location of current position in data
    * @param width width of the HEX dump
    * @return
    */
   private static String formatHex(byte[] src, int lenSrc, int index, int width)
   {
      int i, j;
      int g = width / 4; /* number of groups of 4 bytes */
      int d = g * 13;    /* hex display width of a 4 block*/
      StringBuffer sb = new StringBuffer();
 
      if ( (src == null) ||
           (lenSrc < 1)  || (lenSrc > width) ||
           (index < 0)   ||
           (g % 4 != 0)  ||   /* only allow width of 16 / 32 / 48 etc. */
           (d < 36) )
      {
         return "";
      }
 
      String hdr = Integer.toHexString(index).toUpperCase();
      if (hdr.length() <= 6)
         sb.append("000000".substring(0, 6 - hdr.length()) + hdr + ": ");
      else
         sb.append(hdr + ": ");
 
      /* hex display 4 by 4 */
      for(i=0; i < lenSrc; i++)
      {
      	// gives StringIndexOutOfBoundsException: String index out of range: -8 
      	// in some cases, so why make it more difficult that it needs to be...
      	// sb.append(" "+"0123456789ABCDEF".charAt((src[i]) >> 4) + "0123456789ABCDEF".charAt((src[i] & 0x0F)));
      	sb.append(String.format(" %02X", (src[i] & 0xFF)));
 
         if (((i+1) % 4) == 0)
            sb.append(" ");
      }
 
      /* blank fill hex area if we do not have "width" bytes */
      if (lenSrc < width)
      {
         j = d - (lenSrc*3) - (lenSrc / 4);
         for(i=0; i < j; i++)
            sb.append(" ");
      }
 
      /* character display */
      sb.append(" ");
      for (i=0; i < lenSrc; i++)
      {
         if(Character.isISOControl((char)src[i]))
            sb.append(".");
         else
            sb.append((char)src[i]);
      }
 
      /* blank fill character area if we do not have "width" bytes */
      if (lenSrc < width)
      {
         j = width - lenSrc;
         for(i=0; i < j; i++)
            sb.append(" ");
      }
 
      return sb.toString();
   }
}
