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
		Utils utils = new Utils();
		String s;
		
		s = utils.dumpBuffer(barray);
		System.out.println("Empty buffer:\n" + s);
		
		
		s = "This is a test\n";
		barray = s.getBytes("UTF-8");
		
		s = utils.dumpBuffer(barray);
		System.out.println("Short string buffer:\n" + s);
	}

}
