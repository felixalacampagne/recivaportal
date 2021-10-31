package com.felixalacampagne.recivaportal;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.felixalacampagne.recivaportal.Utils.dumpBuffer;
public class RecivaProtocolHandler
{
final Logger log = LoggerFactory.getLogger(this.getClass());

//	The first data block contains an 8 byte header which contains the file size
//	(4 bytes) and an unknown (maybe fixed) value (also 4 bytes). The header
//	is followed by 246 bytes of payload, a single 0x00 byte and a checksum
//	(again, a simple modulo 256 sum).
//	All further (i.e. non-first) data blocks contain 254 bytes of payload,
//	a single 0x00 byte and a checksum.	

	// No idea how to handle this since I've no idea how requests for subsequent data blocks
	// will look
	// for now Assume there will only be one data block containing enough dummy data to stop
	// the radio from showing error messages
	public byte[] makeFirstDataBlock(String payload)
	{
		byte [] type1 = new byte[256];
		byte[] paybytes;
		try
		{
			paybytes = payload.getBytes("UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			// This is a stupid exception that will never happen as UTF-8 is builtin!
			paybytes = payload.getBytes();
		}
		int len = paybytes.length;
		int tmplen;
		int i = 0;
		for(i = 8; i < 256; i++)
		{
			type1[i] = 0x77; // padding
		}
		
		if(len > 246)
			len = 246;
		
		tmplen = len;
		for(i=0; i<4; i++)
		{
			type1[i] = (byte) (tmplen & 0xFF);
			tmplen >>= 8;
		}
		for(i=4; i<8; i++)
		{
			type1[i] = (byte) (0x00);
		}	
		
		for(i=0; i < len; i++)
		{
			type1[i+8] = paybytes[i];
		}
		type1[len+8] = 0x00;
		
		// Not exactly sure what is meant by a 'simple modulo 256 sum'
		// Assume that bytes in the entire block are summed and the low byte is the last byte of the block
		// It could mean that only the real data in the block is summed, ie. header and payload.
		int chksum = getCheckSum(type1, type1.length-1);
		type1[type1.length-1] = (byte)(chksum & 0xFF);
		
		log.debug("makeFirstDataBlock: type1 data block:\n" + dumpBuffer(type1));
		return type1;
	}
	
	// have no clue what is supposed to go in here since I haven't managed to decrypt
	// the session request, or if I have I don't know what the data means.
	// Try with just the payload, eg. 32byte session key, padding and checksum
	public byte [] makeSessionResponse(String payload)
	{
		byte [] type1 = new byte[256];
		byte[] paybytes;
		try
		{
			paybytes = payload.getBytes("UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			// This is a stupid exception that will never happen as UTF-8 is builtin!
			paybytes = payload.getBytes();
		}
		int len = paybytes.length;
		int i = 0;
		int maxlen = 253; // Allows for nul terminator and checksum byte
		if(len > maxlen)
			len = maxlen;

		for(i=0; i < len; i++)
		{
			type1[i] = paybytes[i];
		}
		
		// Don't know if a nul terminator is required for this
		//type1[len++] = 0x00;

		
		for(i = len; i < 255; i++)
		{
			type1[i] = 0x77; // padding
		}
		
		
		// Not exactly sure what is meant by a 'simple modulo 256 sum'
		// Assume that bytes in the entire block are summed and the low byte is the last byte of the block
		// It could mean that only the real data in the block is summed, ie. header and payload.
		int chksum = getCheckSum(type1, type1.length-1);
		type1[type1.length-1] = (byte)(chksum & 0xFF);

		log.debug("makeSessionResponse: data block:\n" + dumpBuffer(type1));
		return type1;		
	}


	public int getCheckSum(byte[] type1, int len)
	{
		long sum = 0; // -10;
		log.info("getCheckSum: starting with seed: " + String.format("%08x", sum));
		long ubyte = 0;
		for(int i=0; i < len; i++)
		{
			ubyte = ((long) type1[i]) & 0xFF;
			sum += ubyte;
			
			// This should not make any difference
			sum &= 0xFF;
		}

		return (int)(sum & 0xFF);
	}
}
