package com.felixalacampagne.recivaportal;


import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;
// Gratuitous change

@Path("/")
public class RecivaPortalRest
{
   static Status responseStatus = Status.OK;
	final Logger log = LoggerFactory.getLogger(this.getClass());
   @GET
   @Path("/challenge")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   public Response getChallenge(@QueryParam("serial") String serial, @QueryParam("sp") String sp,
         @Context UriInfo ui,
         @Context HttpHeaders headers)
   {
   	log.info("getChallenge: serial:" + serial + " sp:" + sp);
   	log.debug("getChallenge: headers\n" + getHeadersString(headers));
   	log.debug("getChallenge: params\n" + getQueryParamsString(ui));
      return makeChallengeResponse(false, serial);
   }
   
   @HEAD
   @Path("/challenge")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   public Response getHeadChallenge(@QueryParam("serial") String serial, 
         @QueryParam("sp") String sp,
         @Context UriInfo ui,
         @Context HttpHeaders headers)
   {
   	// HEAD response should only return the Headers which would be returned by the equivalent GET request.
   	// A GET request should follow but haven't seen one from the radio yet...
   	log.info("getHeadChallenge: serial:" + serial + " sp:" + sp);
   	log.debug("getHeadChallenge: headers\n" + getHeadersString(headers));
   	log.debug("getHeadChallenge: params\n" + getQueryParamsString(ui));
      return makeChallengeResponse(true, serial);
   }   


   
	@HEAD
   @Path("/{name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response getHeadAny(@PathParam("name") String path, @Context UriInfo ui, @Context HttpHeaders headers) 
   {
   	log.info("getHeadAny:" + path);
   	doAny(path, ui, headers);
   	ResponseBuilder responseBuilder = Response.status(200);
      responseBuilder.lastModified(new Date());
      return responseBuilder.build();
   }
   
   @GET
   @Path("/{name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response getAny(@PathParam("name") String path, @Context UriInfo ui, @Context HttpHeaders headers) 
   {
   	log.info("getAny: path:" + path);
   	doAny(path, ui, headers);
   	ResponseBuilder responseBuilder = Response.status(200);
      responseBuilder.entity("getAny called: path:" + path);
      responseBuilder.lastModified(new Date());
      return responseBuilder.build();

   }

   private Response makeChallengeResponse(boolean bHead, String entity)
   {
      String challenge = "55667788";
//            "<stations><station id=\"2765\" custommenuid=\"0\"><version>5127</version>\r\n"
//      		+ "<data><stream id=\"2149\"><url>http://radios.argentina.fm:9270/stream</url>\r\n"
//      		+ "<title>La 2x4 Tango Buenos Aires</title>\r\n"
//      		+ "<protocol>http</protocol>\r\n"
//      		+ "<metadata><use-metadata author=\"true\" title=\"true\"></use-metadata>\r\n"
//      		+ "</metadata>\r\n"
//      		+ "</stream>\r\n"
//      		+ "</data>\r\n"
//      		+ "<genres>23</genres>\r\n"
//      		+ "<locations>34</locations>\r\n"
//      		+ "</station></stations>";
      // Date lm = new Date(108,1,1,12,0); // 1 Jan 2008 12:00
      // Typical Java: deprecate something nice and simple and replace it with multiple lines of code
      Calendar cal = Calendar.getInstance();
      // cal.set(2008, 1, 1, 12, 0); // 2008 is when I bought the radio - I'd like it to not bother contacting the server.
      Response response;
      Status rspstat = getResponseStatus();
    
      // Noticed a difference between repsonses from UPNP servers and the tomcat 10 server.
      // THe UPNP responses are logged as
      // HTTP/1.1 200 OK
      // but the tomcat responses are logged as
      // HTTP/1.1 200 200
      // I was assuming the ir.log message "OPSEL:Unknown result code: 200" was referring to the
      // numeric code but maybe it is expecting "OK" and consequently does not understand "200"?
      // To really fork things up those who spend their days forking other people up have come up with
      // a great way to really shaft me - they removed the sending of the reason phrase from tomcat.
      // THey didn't just make it optional, no they completly removed it. Appartently that last version to
      // support it is tomcat 8. None of this code will work with tomcat 8 because the self-same barstewards
      // changed the package names of all the Java WS classes! Fork and Shirt! So it will take weeks to test
      // whether my theory is correct... as I'll need to install a second old tomcat and configure it to be used
      // instead of the default one for the portal requests, then re-write this code... FORKING HELL!
      // Looks like I will have to go with a Jetty server instead of tomcat (having read some of the discussions
      // about this change I can only conclude that tomcat devs really are a bunch of grassholes - it seems that checking the
      // phrase is not uncommon in embedded systems, like the radio, and the tomcat devs response to making millions
      // of devices inoperable? Fork you, stupid user, you should known better and bought a better device!!)
   	ResponseBuilder responseBuilder = Response.status(rspstat.getStatusCode(), "Ok"); //rspstat.getReasonPhrase());
   	
            // OK);           OPSEL:Unknown result code: 200
   	      // CREATED);      OPSEL:Unknown result code: 201
   	      // ACCEPTED);     OPSEL:Unknown result code: 202
   	      // NO_CONTENT);   OPSEL:Unknown result code: 204
            // RESET_CONTENT);OPSEL:Unknown result code: 205
   	      // UNAUTHORIZED)  OPSEL:Bad error code back from curl: HTTP response code said error
   	
      responseBuilder.lastModified(cal.getTime());
      cal.add(Calendar.DAY_OF_MONTH,1);
      responseBuilder.expires(cal.getTime());
      
      
      responseBuilder.location(URI.create("/portal/location")); //  ui.getBaseUri());
      
      // location expands the relative path to a full URL but contentLocation just leaves it as a relative URL,
      // so invent a full URL for now.
      responseBuilder.contentLocation(URI.create("http://www.reciva.com/portal/content-location"));
      responseBuilder.encoding("UTF-8");
      responseBuilder.header("Content-Length", "" + challenge.getBytes().length);
      // responseBuilder.header("WWW-Authenticate", "Basic realm=\"User visible realm\""); // for UNAUTHORIZED
      
      //responseBuilder.type(MediaType.APPLICATION_OCTET_STREAM);
      //responseBuilder.type(MediaType.TEXT_XML);
      responseBuilder.type(MediaType.TEXT_PLAIN_TYPE);
      // ir contains this text 'X-Reciva-Challenge-Format: sernum' making me think it is something the the radio adds to 
      // a request rather than expects in the response.
      responseBuilder.header("X-Reciva-Challenge-Format", "sernum");
      // x-reciva-session-id
      responseBuilder.header("X-Reciva-Session-Id", challenge);
      // reciva-token
      responseBuilder.header("Set-Cookie", "JSESSIONID=ABCD1234ABCD1234; Path=/portal");
      //if(!bHead)
      {
      	responseBuilder.entity(challenge.getBytes());
      }

      response = responseBuilder.build();
      log.debug("makeChallengeResponse: response status: " + response.getStatusInfo().getStatusCode() + " " + response.getStatusInfo().getReasonPhrase());
      log.debug("makeChallengeResponse: response header:\n" + getMultivaluedMapString(response.getStringHeaders()));
      return response;
   }
   
	private void doAny(String path, UriInfo ui, HttpHeaders headers)
	{
		log.info("doAny: path:" + path);
		log.debug("doAny: params\n" + getQueryParamsString(ui));
		log.debug("doAny: headers\n" + getHeadersString(headers)); 
		
		return;
	}

   private String getHeadersString(HttpHeaders headers)
	{
   	return getMultivaluedMapString( headers.getRequestHeaders());
	}
   
	private String getQueryParamsString(UriInfo ui)
	{
		return getMultivaluedMapString( ui.getQueryParameters());
	}  
	
	private String getMultivaluedMapString(MultivaluedMap<String, String> mvm)
	{
      String str = mvm.entrySet()
            .stream()
            .map(e -> e.getKey() + ": " + e.getValue())
            .collect(Collectors.joining("\n")); 
      return str;
	}
	
	private Status getResponseStatus()
	{
//	   responseStatus++;
	   return responseStatus;
	}
}
