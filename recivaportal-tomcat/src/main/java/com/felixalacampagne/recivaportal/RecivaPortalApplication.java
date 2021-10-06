package com.felixalacampagne.recivaportal;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

// First part of path is the name of the application jar file which must be "portal"
// I think this is determine by values in the pom.xml...
@ApplicationPath("/")
public class RecivaPortalApplication extends Application
{

}
