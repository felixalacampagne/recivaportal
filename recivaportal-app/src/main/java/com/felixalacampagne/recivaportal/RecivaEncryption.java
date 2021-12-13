package com.felixalacampagne.recivaportal;

import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
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
private static final String DES_TRANSFORM = "DES/CBC/NoPadding"; // Transform makes no difference to the size fo the encrypted data "DES/ECB/PKCS5Padding";

private static final byte [] DESIV = { (byte)0xBD, (byte)0xE7, (byte)0x32, (byte)0x66, 
		                                 (byte)0xb9, (byte)0x46, (byte)0xF3, (byte)0xAB };
private final IvParameterSpec ipsDesIV = new IvParameterSpec(DESIV);
private final Key deskey;
private final Cipher cipher;
private final RecivaChallenge rc;

	public RecivaEncryption(String b64orhexchallenge, boolean b64) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
	{
		String hexchallenge = b64orhexchallenge;
		if(b64)
		{
	      log.debug("<init>: B64 challenge: " + b64orhexchallenge);
			hexchallenge = Utils.base64ToString(b64orhexchallenge);
		}
		rc = RecivaChallengeProvider.getChallenge(hexchallenge);
		byte [] key = rc.getKey();
		log.debug("<init>: challenge: " + hexchallenge + " key: " + rc.getKeyHex());
		cipher = Cipher.getInstance(DES_TRANSFORM);
		deskey = new SecretKeySpec(key, DES_ALGORITHM);
	
	}

   public RecivaEncryption(byte [] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
	{
		cipher = Cipher.getInstance(DES_TRANSFORM);
		deskey = new SecretKeySpec(key, DES_ALGORITHM);
		rc = null;
	
	}

	public byte[] recivaDESencrypt(byte [] clearbytes) throws GeneralSecurityException
	{
		cipher.init(Cipher.ENCRYPT_MODE, deskey, ipsDesIV);

		byte[] encbytes = this.cipher.doFinal(clearbytes);
		log.debug("recivaDESencrypt: encrypted data block:\n" + dumpBuffer(encbytes));
		return encbytes;
	}

	// Encrypts using the challenge response instead of the key - only for testing
	public byte[] recivaDESCRencrypt(byte [] clearbytes) throws GeneralSecurityException
	{
		Key crdeskey = new SecretKeySpec(rc.getChallengeResponse(), DES_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, crdeskey, ipsDesIV);
		byte[] encbytes = this.cipher.doFinal(clearbytes);
		log.debug("recivaDESCRencrypt: encrypted data block:\n" + dumpBuffer(encbytes));
		return encbytes;
	}

	
	public byte[] recivaDESdecrypt(byte [] encbytes) throws GeneralSecurityException
	{
		cipher.init(Cipher.DECRYPT_MODE, deskey, ipsDesIV);
		byte[] clearbytes = this.cipher.doFinal(encbytes);
		log.debug("recivaDESdecrypt: decrypted data block:\n" + dumpBuffer(clearbytes));
		return clearbytes;
	}
}
