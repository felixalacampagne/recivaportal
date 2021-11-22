package com.felixalacampagne.recivaportal;

import static org.junit.jupiter.api.Assertions.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

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
		String hexkey = null; 
		byte [] key = null;
		RecivaEncryption renc = null;
		RecivaProtocolHandler rph = new RecivaProtocolHandler();
		int chksum = 0;
		try
		{
			String hexchallenge = "";
			String fname = "";
			// C:\Development\apache-tomcat-10.0.0\session_body_3030303030303030_2110311149.dat
			String dir = "E:\\Development\\workspace\\recivaportal\\tmp\\session_body_";
			hexchallenge = "3030303030303030";
			fname =
					dir + hexchallenge 
//					+ "_2110311647.dat" // 4
//					+ "_2110311634.dat" // 3
//					+ "_2110311134.dat" // 0
//					+ "_2110311143.dat" // 0
					+ "_2110311149.dat" // 0
					;

			
			encbytes = Utils.readFileToBytes(fname);
			RecivaChallenge rcvclg = RecivaChallengeProvider.getChallenge(hexchallenge);			
			//hexkey = "e623fc320ee37784";
			key = rcvclg.getKey(); //.decodeHexString(hexkey);
			renc = new RecivaEncryption(key);
			clearbytes = renc.recivaDESdecrypt(encbytes);

			// Finally figured out that the first 8bytes of the decrypted challenge response should match the
			// sread 'challenge response'.
			byte [] clgrsp = rcvclg.getChallengeResponse();
			byte [] rcvrsp = Arrays.copyOf(clearbytes, clgrsp.length);
			
			assertArrayEquals(clgrsp, rcvrsp, "Compare expected challenge response with that in the decrypted received data");
			
			byte [] newenc = renc.recivaDESencrypt(clearbytes);
			assertArrayEquals(encbytes, newenc, "Compare reencrypted data with original encrypted data");
			
			int origchksum = ((int) clearbytes[clearbytes.length-1]) & 0xFF;
			chksum = rph.getCheckSum(clearbytes, clearbytes.length-1);
			log.info("testRecivaDESdecrypt: chksum diff orig-calc: " + origchksum + " + " + chksum + " = " + (origchksum - chksum)); 
			assertEquals(origchksum, chksum, "Compare expected checksum with that present in the decrypted received data");
			
			

		}
		catch (Exception e)
		{
			log.error("Encryption failed:", e );
		}
	}
}
