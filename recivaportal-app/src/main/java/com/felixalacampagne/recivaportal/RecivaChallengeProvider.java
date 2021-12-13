package com.felixalacampagne.recivaportal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecivaChallengeProvider
{
	final static Logger log = LoggerFactory.getLogger(RecivaChallengeProvider.class);

private static RecivaChallenge [] challenges = 
{
	//                  Challenge(auth)     ChallengeResponse   Key                 MacKey
	new RecivaChallenge("3030303030303030", "aa084e55dea29a71", "7b3970508744e215", "f6d762cf35989dd4e4d069f804b1864f71323fc0412684ce"),
//	new RecivaChallenge("AA084E55DEA29A71", "7fc9710eefb973be", "c80c60752e847fc5", "7ba6acf9826f52c7bea51b6a3a3d0c0d45764abcc1528c27"),
//	new RecivaChallenge("7B3970508744E215", "21a05bec8d0f07ca", "fe04d40cc69e7831", "8d933a11c61b5e897c175d77ef46eb2f53703313a7890e7a"),
	
	new RecivaChallenge("3030303030303031", "88d8578cb7a204b7", "8d9eb04092d51342", "5f1ac971ac7099c03840df9422777f687cffa599eaa5fc60"),
	new RecivaChallenge("3030303030303032", "d313e883ce9b2b6b", "bf96d50aec6a1f06", "775ba72bbe158fbab9a6815a8598cc3d76e44654842685c7"),
	new RecivaChallenge("3030303030303033", "5f2c4fe31c6734d0", "54920e25f6b7aefc", "6ab28ac213fbc8b5552b159dc7dac2a8d0e0a61a5c532de2"),
	new RecivaChallenge("3030303030303034", "bc639d5a5c904491", "6921b82ab64a54a1", "0b7132ebb096ad3267365fdc11b6b366aea2d3e27c588673"),
	new RecivaChallenge("3030303030303035", "888588e66df51f1c", "658799e6e0555daa", "e2481645fb62e7ffc8b4191f50bab0858f95ac75844d11c0"),
	new RecivaChallenge("3030303030303036", "b15cca834bf88705", "c8a81e3596776f41", "8b15e0617dcb3c9d1523f5527951c92798057752c2608c4c"),
	new RecivaChallenge("3030303030303037", "86d948618110dbce", "d8e15fb03a59de33", "ed83e8492ad0aa6248dda7c961d855807d2bded97d67771f"),
	new RecivaChallenge("3030303030303038", "47d41b77ebcd02ae", "e7a1aca9e4edfb49", "551b5a1bf0e64cca5193f376cbc381c33d3c932b719ebc39"),
	new RecivaChallenge("3030303030303039", "a95afab7a832af25", "7db034471a3eaf8c", "6ddd831d8506ba4db41e8559c5aca58d64b19eebfca650b6"),
	new RecivaChallenge("303030303030303A", "96234b29b415d30b", "07e7e4e099d5a840", "48491e85192b966d12cc8be583908b2cdaf3a1cf014d4aae"),
	new RecivaChallenge("303030303030303B", "92c3ae64eac93f40", "a9aed9e543c239a0", "f82c479791d4d90bbca50303c648f30eb7d583e7d9244db1"),
	new RecivaChallenge("303030303030303C", "d652b5f756dbe9f2", "961dee393a5a06ac", "9f1089a8097cb7c73c97854062730ae6214770bb0b23b776"),
	new RecivaChallenge("303030303030303D", "c79a4bd2c58f5e10", "b50aaa233ca7495c", "27c617dcd82f66f40fb0eb0821a03a8c817065771bf146e3"),
	new RecivaChallenge("303030303030303E", "4177695e5887b2fb", "7b6174c3b65a49bc", "34614c3488ee91e0ae0d02828c6137a77503545afbe2332d"),
	new RecivaChallenge("303030303030303F", "ad7d29de6e6f19a6", "ab4e34a80ee0c511", "bb16721b56bfd91f12f4588ed051be90c4f12443ffd44c53"),
	new RecivaChallenge("3030303030303130", "de5b4fca90baf37e", "37a45bcd832923d0", "59aba2d10165627df8575b4223a418187d15663f5a886afc"),
	new RecivaChallenge("3030303030303131", "b95f1193ef0f0513", "d6a686474f90a059", "ead7bef1cfd08564a578d371388913218bdf6c73284a73e9"),
	new RecivaChallenge("3030303030303132", "0f74ae02c323ac6e", "f47e755ef5762026", "0128c572b661d6a5b7f90b98de2750bf475408821d846a96"),
	new RecivaChallenge("3030303030303133", "d166e648d6ac6513", "9847721474ee6c7a", "b416464d256fd6479d70624d208eafe003f53d567b81dddd"),
	new RecivaChallenge("3030303030303134", "795126c83da7c977", "9353067a1a060a10", "0a12a2381c8acf373931deddd1ff2c7be73d4d1c291f7541")

};
static int curchallenge = challenges.length; // Ensure 0 is the first challenge provided


	public static RecivaChallenge getNextChallenge()
	{
		curchallenge ++;
		if(curchallenge > challenges.length)
			curchallenge = 0;
		return challenges[curchallenge];
	}
	
	public static RecivaChallenge getChallenge(String hexchallenge)
	{
		RecivaChallenge key = null;
		String uclg = hexchallenge.toUpperCase();
		for(RecivaChallenge r : challenges)
		{
			if(r.getHexChallenge().equals(uclg))
			{
				key = r;
				break;
			}
		}
		if(key == null)
		{
			log.warn("getChallenge: no challenge found for " + hexchallenge);
		}
		return key;
	}
}
