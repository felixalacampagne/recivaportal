package com.felixalacampagne.recivaportal;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

// First part of path is the name of the application jar file which must be "portal"
// I think this is determine by values in the pom.xml...

// So this worked as required when it was a single maven project but now that the actual REST API
// is in a sub-project which is then packaged into the tomcat WAR as a JAR file the previously
// working URLs (eg. http://server/portal/anything?x=y) give a tomcat 404 page instead the
// output from the API.
// So what would have been a trivial operation using the old-fashioned way once again
// becomes a nightmare using the modern way of doing things. 
//
// How on earth to get the magic working again, especially since I have no idea how to search
// for answers... so far googling just throws up spring boot questions which is definitely not
// what I'm doing. There is talk of adding things to the web.xml but the whole bloody point
// of using this jakarta.ws.rs magic is to NOT require the web.xml.
// Maybe I need some sort of reference to the rest api in here but what??
// So putting a copy of the REST code in the same directory as this class doesn't work.
// Putting an uninitialised reference to the REST code in here doesn't work.
// Putting some shirt in the web.xml referring to this and the REST code doesn't work.
// What else can I try!
// I guess the only thing I can think of is to go back to the original, working recivaportal project.
// Trying to guess how to fix the magic is going to waste too much of my time....
// Many, many, many hours later... seems the wind direction changed or maybe a solar flare
// passed by and now tomcat responds with the REST api output (again!).


@ApplicationPath("/")
public class RecivaPortalApplication extends Application
{
//	RecivaPortalRestTC restapi;
//	public RecivaPortalApplication()
//	{
//		super();
//	}
}
