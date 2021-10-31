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
		byte chksum = 0;
		try
		{
			// C:\Development\apache-tomcat-10.0.0\session_body_3030303030303030_2110311149.dat
			encbytes = Utils.readFileToBytes("E:\\Development\\workspace\\recivaportal\\tmp\\session_body_3030303030303030_2110311149.dat");
			RecivaChallenge rcvclg = RecivaChallengeProvider.getChallenge("3030303030303030");
			//hexkey = "e623fc320ee37784";
			key = rcvclg.getKey(); //.decodeHexString(hexkey);
			renc = new RecivaEncryption(key);
			clearbytes = renc.recivaDESdecrypt(encbytes);
			chksum = getCheckSum(clearbytes, clearbytes.length-2);
			log.info("testRecivaDESencrypt(key): byte[255]: actual " + String.format("%02X", clearbytes[255]) 
						+ ", expected " + String.format("%02X", chksum));

			// Finally figured out that the first 8bytes of the decrypted challenge response should match the
			// sread 'challenge response'.
			byte [] clgrsp = rcvclg.getChallengeResponse();
			byte [] rcvrsp = Arrays.copyOf(clearbytes, clgrsp.length);
			
			assertArrayEquals(clgrsp, rcvrsp, "Compare expected challenge response with decrypted received response");
			
			
			// In theory the checksum should be correct?
		}
		catch (Exception e)
		{
			log.error("Encryption failed:", e );
		}
	}
	
	private byte getCheckSum(byte[] type1, int len)
	{
		long sum = 0;
		for(int i=0; i < len; i++)
		{
			sum += type1[i];
		}
		return (byte)(sum % 256);
	}
}
