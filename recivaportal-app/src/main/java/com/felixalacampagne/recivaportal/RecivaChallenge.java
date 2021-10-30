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

public RecivaChallenge(String challenge, String key, String challengeResponse, String mackey)
{
	this.challenge = challenge;
	this.key = key;
	this.challengeResponse = challengeResponse;
	this.mackey  = mackey;
}
public String getChallenge()
{
	return challenge;
}
public String getKey()
{
	return key;
}
public String getChallengeResponse()
{
	return challengeResponse;
}
public String getMackey()
{
	return mackey;
}
}
