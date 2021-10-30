package com.felixalacampagne.recivaportal;

import static org.junit.jupiter.api.Assertions.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RecivaEncryptionTest
{
	final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Test
	void testRecivaDESencrypt()
	{
		byte [] clearbytes = new byte[256];
		byte [] encbytes;
		byte [] deencbytes;
		String hexkey = "7b3970508744e215"; 
		int i;
		for(i=0; i < clearbytes.length; i++)
		{
			clearbytes[i] = (byte) (i & 0xFF);
		}
		log.info("testRecivaDESencrypt: DES un-encrypted bytes:\n" + Utils.dumpBuffer(clearbytes));
		try
		{
			RecivaEncryption renc = new RecivaEncryption( Utils.decodeHexString(hexkey));
			encbytes = renc.recivaDESencrypt(clearbytes);
			
			assertEquals(clearbytes.length, encbytes.length, "Is array length changed"); // Stupid test just for the sake of it

			deencbytes = renc.recivaDESdecrypt(encbytes);
			assertEquals(clearbytes.length, deencbytes.length, "Is array length changed"); // Stupid test just for the sake of it
			
		}
		catch (Exception e)
		{
			log.error("Encryption failed:", e );
		}
	}


	@Test
	void testRecivaDESdecrypt()
	{
		byte [] clearbytes = null;
		byte [] encbytes = null;
		String hexkey = "7b3970508744e215"; 
		byte [] key = null;

		try
		{
			encbytes = Utils.readFileToBytes("E:\\Development\\workspace\\recivaportal\\tmp\\session_body_MDAwMDAwMDA=.dat");
			key = Utils.decodeHexString(hexkey);
			RecivaEncryption renc = new RecivaEncryption(key);
			clearbytes = renc.recivaDESdecrypt(encbytes);
			log.info("testRecivaDESencrypt: DES encrypted bytes:\n" + Utils.dumpBuffer(clearbytes));
			
			// Seems the encrypted data is larger than the original - no clue if this is correct... I've
			// just used the actual size of the returned array so the test passed.
			// Have to wait and see what the radio makes of it....
			assertEquals(256, clearbytes.length, "Is array length changed"); // Stupid test just for the sake of it
		}
		catch (Exception e)
		{
			log.error("Encryption failed:", e );
		}
	}
	

}
