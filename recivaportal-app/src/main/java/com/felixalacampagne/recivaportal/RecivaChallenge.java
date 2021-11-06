package com.felixalacampagne.recivaportal;

public class RecivaChallenge
{
private String challenge;
private String key;
private String challengeResponse;
private String mackey;

public RecivaChallenge(String challenge, String key)
{
	this.challenge = challenge;
	this.key = key;
}

// the parameters are hex string representations of the bytes
public RecivaChallenge(String challenge, String challengeResponse, String key, String mackey)
{
	this.challenge = challenge.toUpperCase();	
	this.key = key;
	this.challengeResponse = challengeResponse;
	this.mackey  = mackey;
}


public String getHexChallenge()
{
	return challenge;
}

public byte[] getChallenge()
{
	return Utils.decodeHexString(challenge);
}

public byte[] getKey()
{
	return Utils.decodeHexString(key);
}

public String getKeyHex()
{
	return key;
}

public byte[] getChallengeResponse()
{
	return Utils.decodeHexString(challengeResponse);
}

public byte[] getMackey()
{
	return Utils.decodeHexString(mackey);
}
}
