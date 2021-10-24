package com.felixalacampagne.recivaportal;

import static org.junit.jupiter.api.Assertions.*;

import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.Test;

class UtilsTest
{

	@Test
	void testDumpBufferByteArray() throws UnsupportedEncodingException
	{
		byte [] barray =  new byte[256];
		String s;
		
		s = Utils.dumpBuffer(barray);
		System.out.println("Empty buffer:\n" + s);
		
		
		s = "This is a test\n";
		barray = s.getBytes("UTF-8");
		
		s = Utils.dumpBuffer(barray);
		System.out.println("Short string buffer:\n" + s);
	}

	@Test
	void testbase64ToByteArray()
	{
	String base64 = "NTU2Njc3ODg=";
	byte [] bytes = Utils.base64ToByteArray(base64);
	System.out.println("base64: " + base64 + "->\n" + Utils.dumpBuffer(bytes));
	}
}
