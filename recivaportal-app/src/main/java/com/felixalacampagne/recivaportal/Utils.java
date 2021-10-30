package com.felixalacampagne.recivaportal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils
{
	static final Logger log = LoggerFactory.getLogger(Utils.class);

	
	
	public static byte[] readFileToBytes(String fname)
	{
		return readFileToBytes(new File(fname));
	}
	
	private static byte[] readFileToBytes(File file)
	{
		byte [] bytes = null;

		try(FileInputStream fis = new FileInputStream(file))
		{
			bytes = new byte [ (int) file.length() ];
			fis.read(bytes);
			// fis Autocloses
		}
		catch (IOException e)
		{
			log.error("Failed to read bytes from " + file.getAbsolutePath(), e);
		}

		
		return bytes;
	}

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

   public static void dumpToFile(String fname, byte [] data)
   {
   	File file = new File(fname);
   	
   	try(FileOutputStream fos = new FileOutputStream(file))
		{
			
			fos.write(data);
			log.debug("dumpToFile: data dumped to '" + file.getAbsolutePath() + "'");

		}
		catch (IOException e)
		{
			log.error("dumpToFile: failed to dump data '" + file.getAbsolutePath() + "'", e);
		}
 
   }   
   
   public static void base64ToFile(String fname, String strb64)
   {
   	File file = new File(fname);
   	ByteArrayInputStream bis = new ByteArrayInputStream(strb64.getBytes());
   	try(FileOutputStream fos = new FileOutputStream(file))
		{
			
			base64ToBytes(bis, fos);

		}
		catch (IOException e)
		{
			log.error("base64ToFile: failed to decode base64 string to '" + file.getAbsolutePath() + "'", e);
		}
 
   }
   

   public static String base64ToString(String strb64)
   {
   	// For consistency in conversions
   	return new String(base64ToByteArray(strb64));
   }
   
   public static byte[] base64ToByteArray(String strb64)
   {
      if(strb64 == null)
         return null;

   // Initial size of bos can be the theoretical max size since the bos keeps track
   // of the number of valid entries.
   ByteArrayOutputStream bos = new ByteArrayOutputStream((strb64.length() * 3) / 4);
   ByteArrayInputStream bis = new ByteArrayInputStream(strb64.getBytes());
   byte [] clrbuf = null;

     try
     {
        base64ToBytes(bis, bos);
        clrbuf = bos.toByteArray();
        bos.close();
        bis.close();
     }
     catch(Exception ex)
     {
        ex.printStackTrace();
     }
     return clrbuf;
   }


   public static int base64ToBytes(InputStream strb64, OutputStream outclr)
   {
   char a = 0;
   int dat;
   //int datidx = 0;
   int o = 0;
   int restBit = 8;
   int prevBit = 0;
   int ic = 0;

     if(strb64 == null)
        return 1;

     try
     {
     while((ic = strb64.read()) > -1)
      {
         a = (char) ic;
         if ( a == '=' ) break;
         if (a >= 'A' && a <= 'Z')
            o = a - 'A';
         else if (a >= 'a' && a <= 'z')
            o = a - 'a' + 26;
         else if (a >= '0' && a <= '9')
            o = a - '0' + 52;
         else if (a == '+')
            o = 62;
         else if (a == '/')
            o = 63;
         else continue;
         if (restBit > 6)
         {
            prevBit = (prevBit<<6) | o;
            restBit -= 6;
         }
         else
         {
            dat = (prevBit << restBit) | ((o >> (6-restBit)) & 0x3f);
            restBit = 2+restBit;
            prevBit = o;

            //outbuf[datidx] = (byte) dat;
            //datidx++;
            outclr.write(dat);
         }
      }

      // No nice way to truncate an existing array so have to copy to a
      // new one. Maybe need to use something else to store the output
      // although I guess all methods will end up duplicating the data.
      //retbuf = new byte[datidx];
      //System.arraycopy(outbuf, 0, retbuf, 0, datidx);
     }
     catch(Exception ex)
     {
        ex.printStackTrace();
     }
      //return retbuf;
      return 0;
   }
   
	public static String getTimestampFN()
	{
	   return getTimestampFN(new Date());
	}
	
	public static String getTimestampFN(long date)
	{
	   return getTimestampFN(new Date(date));
	}
	public static String getTimestampFN(Date date)
	{
	SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
	   
	   return sdf.format(date);
	}
	
	// Ripped from baeldung to save time
	public static byte[] decodeHexString(String hexString) {
		Utils utils = new Utils();
	    if (hexString.length() % 2 == 1) {
	        throw new IllegalArgumentException(
	          "Invalid hexadecimal String supplied.");
	    }
	    
	    byte[] bytes = new byte[hexString.length() / 2];
	    for (int i = 0; i < hexString.length(); i += 2) {
	        bytes[i / 2] = utils.hexToByte(hexString.substring(i, i + 2));
	    }
	    return bytes;
	}
	private byte hexToByte(String hexString) {
	    int firstDigit = toDigit(hexString.charAt(0));
	    int secondDigit = toDigit(hexString.charAt(1));
	    return (byte) ((firstDigit << 4) + secondDigit);
	}

	private int toDigit(char hexChar) {
	    int digit = Character.digit(hexChar, 16);
	    if(digit == -1) {
	        throw new IllegalArgumentException(
	          "Invalid Hexadecimal Character: "+ hexChar);
	    }
	    return digit;
	}	
}
