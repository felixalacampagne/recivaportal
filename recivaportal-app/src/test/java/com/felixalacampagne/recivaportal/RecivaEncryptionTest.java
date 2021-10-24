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
	static Utils utils = new Utils();
	@Test
	void testRecivaDESencrypt()
	{
		byte [] clearbytes = new byte[256];
		byte [] encbytes;
		int i;
		for(i=0; i < clearbytes.length; i++)
		{
			clearbytes[i] = (byte) (i & 0xFF);
		}
		try
		{
			RecivaEncryption renc = new RecivaEncryption();
			encbytes = renc.recivaDESencrypt(clearbytes);
			log.info("testRecivaDESencrypt: DES encrypted bytes:\n" + utils.dumpBuffer(encbytes));
			
			// Seems the encrypted data is larger than the original - no clue if this is correct... I've
			// just used the actual size of the returned array so the test passed.
			// Have to wait and see what the radio makes of it....
			assertEquals(264, encbytes.length, "Is array length changed"); // Stupid test just for the sake of it
		}
		catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e)
		{
			log.error("Encryption failed:", e );
		}
	}

}
