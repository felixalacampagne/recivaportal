package com.felixalacampagne.recivaportal;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RecivaProtocolHandlerTest
{
	final Logger log = LoggerFactory.getLogger(this.getClass());
	static Utils utils = new Utils();
	@Test
	void testMakeFirstDataBlock()
	{
   	String body =  "<stations></stations>";
   	
   	RecivaProtocolHandler rph = new RecivaProtocolHandler();
   	byte [] type1 = rph.makeFirstDataBlock(body);
   	log.info("testMakeFirstDataBlock: type1 block for '" + body + "':\n" + utils.dumpBuffer(type1));
   	assertEquals(type1.length, 256); // Stupid test just for the sake of it
	}

}
