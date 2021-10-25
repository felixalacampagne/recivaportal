package com.felixalacampagne.recivaportal;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.felixalacampagne.recivaportal.Utils.dumpBuffer;
public class RecivaEncryption
{
final Logger log = LoggerFactory.getLogger(this.getClass());	

//	The DES and 3DES ciphers use fixed initialization vectors which are as
//	follows:
//	For DES: \xbd\xe7\x32\x66\xb9\x46\xf3\xab
//	For 3DES: \xed\x9e\xa8\x97\x7c\xee\xc8\xac
private static final String DES_ALGORITHM = "DES";
private static final String DES_TRANSFORM = "DES"; // Transform makes no difference to the size fo the encrypted data "DES/ECB/PKCS5Padding";

private static final byte [] DESIV = { (byte)0xBD, (byte)0xE7, (byte)0x32, (byte)0x66, 
		                                 (byte)0xb9, (byte)0x46, (byte)0xF3, (byte)0xAB };
private final Key deskey;
private final Cipher cipher;

	public RecivaEncryption(byte [] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
	{
		cipher = Cipher.getInstance(DES_TRANSFORM);
		deskey = new SecretKeySpec(key, DES_ALGORITHM);
	}
	

	public RecivaEncryption() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
	{
		String calg = null;
		cipher = Cipher.getInstance(DES_TRANSFORM);
		// Forking Java: can't use the getAlgorithm method to create the secret key
		// because it returns the "transformation" as specified in Cipher.getInstance
		// and that is not compatible with Cipher.init: InvalidKeyException: Wrong algorithm: DES required
//		calg = cipher.getAlgorithm();
		deskey = new SecretKeySpec(DESIV, DES_ALGORITHM);
	}
	
	public byte[] recivaDESencrypt(byte [] clearbytes) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		cipher.init(Cipher.ENCRYPT_MODE, deskey);
		byte[] encbytes = this.cipher.doFinal(clearbytes);
		log.debug("recivaDESencrypt: encrypted data block:\n" + dumpBuffer(encbytes));
		return encbytes;
	}
}
