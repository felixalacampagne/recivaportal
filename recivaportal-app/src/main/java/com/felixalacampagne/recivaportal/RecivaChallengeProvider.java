package com.felixalacampagne.recivaportal;

public class RecivaChallengeProvider
{
// This will need to be made into something which can provide a challenge by cycling through the array in order 
// and wrapping back to the begining and also mapping a challenge into a key
private static RecivaChallenge [] challenges = 
{
	new RecivaChallenge("00000000", "7b3970508744e215", "aa084e55dea29a71", "f6d762cf35989dd4e4d069f804b1864f71323fc0412684ce"),
	new RecivaChallenge("00000001", "8d9eb04092d51342", "88d8578cb7a204b7", "5f1ac971ac7099c03840df9422777f687cffa599eaa5fc60"),
	
	// Placeholder at the moment
	new RecivaChallenge("00000002", "8d9eb04092d51342", "88d8578cb7a204b7", "5f1ac971ac7099c03840df9422777f687cffa599eaa5fc60"),
	new RecivaChallenge("00000003", "8d9eb04092d51342", "88d8578cb7a204b7", "5f1ac971ac7099c03840df9422777f687cffa599eaa5fc60"),
	new RecivaChallenge("00000004", "8d9eb04092d51342", "88d8578cb7a204b7", "5f1ac971ac7099c03840df9422777f687cffa599eaa5fc60"),
	new RecivaChallenge("00000005", "8d9eb04092d51342", "88d8578cb7a204b7", "5f1ac971ac7099c03840df9422777f687cffa599eaa5fc60"),
	new RecivaChallenge("00000006", "8d9eb04092d51342", "88d8578cb7a204b7", "5f1ac971ac7099c03840df9422777f687cffa599eaa5fc60"),
	new RecivaChallenge("00000007", "8d9eb04092d51342", "88d8578cb7a204b7", "5f1ac971ac7099c03840df9422777f687cffa599eaa5fc60"),
	new RecivaChallenge("00000008", "8d9eb04092d51342", "88d8578cb7a204b7", "5f1ac971ac7099c03840df9422777f687cffa599eaa5fc60"),
	new RecivaChallenge("00000009", "8d9eb04092d51342", "88d8578cb7a204b7", "5f1ac971ac7099c03840df9422777f687cffa599eaa5fc60"),
	new RecivaChallenge("0000000A", "8d9eb04092d51342", "88d8578cb7a204b7", "5f1ac971ac7099c03840df9422777f687cffa599eaa5fc60"),
	new RecivaChallenge("0000000B", "8d9eb04092d51342", "88d8578cb7a204b7", "5f1ac971ac7099c03840df9422777f687cffa599eaa5fc60"),
	new RecivaChallenge("0000000C", "8d9eb04092d51342", "88d8578cb7a204b7", "5f1ac971ac7099c03840df9422777f687cffa599eaa5fc60"),
	new RecivaChallenge("0000000D", "8d9eb04092d51342", "88d8578cb7a204b7", "5f1ac971ac7099c03840df9422777f687cffa599eaa5fc60"),
	new RecivaChallenge("0000000E", "8d9eb04092d51342", "88d8578cb7a204b7", "5f1ac971ac7099c03840df9422777f687cffa599eaa5fc60"),
	new RecivaChallenge("0000000F", "8d9eb04092d51342", "88d8578cb7a204b7", "5f1ac971ac7099c03840df9422777f687cffa599eaa5fc60"),
	new RecivaChallenge("00000010", "8d9eb04092d51342", "88d8578cb7a204b7", "5f1ac971ac7099c03840df9422777f687cffa599eaa5fc60"),
	new RecivaChallenge("00000011", "8d9eb04092d51342", "88d8578cb7a204b7", "5f1ac971ac7099c03840df9422777f687cffa599eaa5fc60"),
	new RecivaChallenge("00000012", "8d9eb04092d51342", "88d8578cb7a204b7", "5f1ac971ac7099c03840df9422777f687cffa599eaa5fc60"),
	new RecivaChallenge("00000013", "8d9eb04092d51342", "88d8578cb7a204b7", "5f1ac971ac7099c03840df9422777f687cffa599eaa5fc60"),
	new RecivaChallenge("00000014", "8d9eb04092d51342", "88d8578cb7a204b7", "5f1ac971ac7099c03840df9422777f687cffa599eaa5fc60"),
};
static int curchallenge = challenges.length; // Ensure 0 is the first challenge provided


	public static String getNextChallenge()
	{
		curchallenge ++;
		if(curchallenge > challenges.length)
			curchallenge = 0;
		return challenges[curchallenge].getChallenge();
	}
	
	public static String getKey(String challenge)
	{
	String key = null;
		for(RecivaChallenge r : challenges)
		{
			if(r.getChallenge().equals(challenge))
			{
				key = r.getKey();
				break;
			}
		}
		return key;
	}
}
