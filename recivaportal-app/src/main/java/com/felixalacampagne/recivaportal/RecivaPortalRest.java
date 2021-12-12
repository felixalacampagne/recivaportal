package com.felixalacampagne.recivaportal;


import java.net.URI;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.POST;
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
import static com.felixalacampagne.recivaportal.Utils.dumpBuffer;

@Path("/")
public class RecivaPortalRest
{
   static Status responseStatus = Status.OK;
	final Logger log = LoggerFactory.getLogger(this.getClass());
	final Utils utils = new Utils();
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

   @POST
   @Path("/session")
   @Produces(MediaType.TEXT_PLAIN)
   @Consumes(MediaType.APPLICATION_OCTET_STREAM)
   public Response postSession( 
   		@QueryParam("serial") String serial,
   		@QueryParam("auth") String auth,   // Looks like Base64
   		@QueryParam("sp") String sp,
   		@Context UriInfo ui,  
   		@Context HttpHeaders headers,
   		byte[] messageBody) 
   {
   	Response response = null;
   	RecivaEncryption renc = null;
   	RecivaProtocolHandler rph = new RecivaProtocolHandler();
   	String body = null;
   	try
   	{
	   	String path = ui.getPath();
	   	String authstr = Utils.base64ToString(auth); 
	   	log.info("postSession: request path: " + path);
	   	log.info("postSession: auth:" + auth + " -> " + authstr);
	   	log.info("postSession: request body length: " + messageBody.length);
	   	log.info("postSession: request body:\n" + dumpBuffer(messageBody));
	   	log.info("postSession: decryption key auth:" + auth);
	   	
	   	// Decrypt the request body - using the B64 auth to get the key
			renc = new RecivaEncryption(auth, true);
			byte [] messageBodyclear = renc.recivaDESdecrypt(messageBody);
			int chksum = rph.getCheckSum(messageBodyclear, messageBodyclear.length-1);
			log.info("postSession: decrypted request body:\n" + dumpBuffer(messageBodyclear));
			log.info("postSession: checksum: expected: " + chksum + " actual: " + (messageBodyclear[messageBodyclear.length-1] & 0xFF));	   	
//	   	Utils.dumpToFile("session_body_" + authstr + "_" + Utils.getTimestampFN() + ".dat", messageBody);

	   	String rspauth;
	   	// Numerous manipulations to get an encryption key have been tried - non result 
	   	// in the radio being able to decrypt the response, eg.
	   	//  - auth indicates the challenge response to use as encryption key - no
	   	//  - auth indicates a challenge response to use to get the encryption key - no
	   	//  - auth indicates the key to use - no
	   	// For now stick with the last option.
	     
	   	rspauth = auth;
	   	
	   	// Use the challenge response as a lookup for the response encryption key

//	   	byte [] clgrsp = Arrays.copyOf(messageBodyclear, 8);	   	
//	   	body = "<stations><station id=\"2765\" custommenuid=\"0\"><version>5127</version>\r\n"
//	      		+ "<data><stream id=\"2149\"><url>http://radios.argentina.fm:9270/stream</url>\r\n"
//	      		+ "<title>La 2x4 Tango Buenos Aires</title>\r\n"
//	      		+ "<protocol>http</protocol>\r\n"
//	      		+ "<metadata><use-metadata author=\"true\" title=\"true\"></use-metadata>\r\n"
//	      		+ "</metadata>\r\n"
//	      		+ "</stream>\r\n"
//	      		+ "</data>\r\n"
//	      		+ "<genres>23</genres>\r\n"
//	      		+ "<locations>34</locations>\r\n"
//	      		+ "</station></stations>";
	   	body =  "0123456789ABCDEF"; // "<stations></stations>";
 	   	
	   	
	   	log.info("postSession: session response encryption key auth:" + rspauth);
	   	byte [] payload; 
	   	payload = rph.makeSessionResponse(body); 
//	   	payload = rph.makeFirstDataBlock(body);
//	   	payload = body.getBytes();
	   	
	   	renc = new RecivaEncryption(rspauth, true);
	   	payload = renc.recivaDESencrypt(payload);
	   	
	   	ResponseBuilder responseBuilder = Response.status(200);
	      Calendar cal = Calendar.getInstance();
	
	      responseBuilder.lastModified(cal.getTime());
	      cal.add(Calendar.DAY_OF_MONTH,1);
	      responseBuilder.expires(cal.getTime());
	      //responseBuilder.encoding("UTF-8");
	      responseBuilder.header("Content-Length", "" + payload.length);
	      responseBuilder.type(MediaType.APPLICATION_OCTET_STREAM_TYPE);
	      responseBuilder.header("X-Reciva-Challenge-Format", "sernum");
	      // x-reciva-session-id
	      //responseBuilder.header("X-Reciva-Session-Id", body);

	      // Fake header added to keep track of the request in the reciva logs
	      responseBuilder.header("X-CPA-Auth", authstr);
	      // reciva-token
	      responseBuilder.header("Set-Cookie", "JSESSIONID=ABCD1234ABCD1234; Path=/");      
	      responseBuilder.entity(payload);
	      response = responseBuilder.build();
	      log.debug("postSession: response status: " + response.getStatusInfo().getStatusCode() + " " + response.getStatusInfo().getReasonPhrase());
	      log.debug("postSession: response header:\n" + getMultivaluedMapString(response.getStringHeaders()));    
	      
	      return response;
   	}
   	catch(Exception e)
   	{
   		log.error("postSession: Exception processing 'session' request: ", e);
   		response = Response.status(Status.INTERNAL_SERVER_ERROR).build();//responseBuilder.
   	}
   	return response;
   }
   
	@HEAD
   @Path("/{name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response getHeadAny(@PathParam("name") String path, @Context UriInfo ui, @Context HttpHeaders headers) 
   {
   	log.info("getHeadAny:" + path);
   	logRequest(path, ui, headers);
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
   	String body = logRequest(path, ui, headers);
   	ResponseBuilder responseBuilder = Response.status(200);
      responseBuilder.entity("getAny called: path:" + path);
      responseBuilder.lastModified(new Date());
      responseBuilder.header("Content-Length", "" + body.getBytes().length);
      responseBuilder.entity(body);
      return responseBuilder.build();

   }

   @POST
   @Path("/{name}")
   @Produces(MediaType.TEXT_PLAIN)
   @Consumes(MediaType.APPLICATION_OCTET_STREAM)
   public Response postAny(@PathParam("name") String path, 
   		@Context UriInfo ui,  
   		@Context HttpHeaders headers,
   		byte[] messageBody) 
   {
   	log.info("postAny: path:" + path);
   	log.info("postAny: body length: " + messageBody.length);
   	String body = logRequest(path, ui, headers);
   	
   	ResponseBuilder responseBuilder = Response.status(200);
      responseBuilder.entity("postAny: path:" + path);
      responseBuilder.lastModified(new Date());
      responseBuilder.header("Content-Length", "" + body.getBytes().length);
      responseBuilder.entity(body);
      return responseBuilder.build();

   }

    
   
   private Response makeChallengeResponse(boolean bHead, String entity)
   {
      byte [] challenge = RecivaChallengeProvider.getNextChallenge().getChallenge(); // "00000000"; // Use 3030303030303030 for sread

      Calendar cal = Calendar.getInstance();
      Response response;
      Status rspstat = getResponseStatus();
      
      // Finally received a GET request for challenge (with the URL /portal-newformat.
      // It was followed by a POST request for session... the next phase begins, but first some tidy up.
    
      // NB. tomcat strips out the reasonphrase, and the apache server-tomcat interface puts something else in its place!
   	ResponseBuilder responseBuilder = Response.status(rspstat.getStatusCode(), "Ok"); //rspstat.getReasonPhrase());
   	
      responseBuilder.lastModified(cal.getTime());
      cal.add(Calendar.DAY_OF_MONTH,1);
      responseBuilder.expires(cal.getTime());
      responseBuilder.encoding("UTF-8");
      responseBuilder.header("Content-Length", "" + challenge.length);
      responseBuilder.type(MediaType.TEXT_PLAIN_TYPE);
      responseBuilder.header("X-Reciva-Challenge-Format", "sernum");
      // x-reciva-session-id
      responseBuilder.header("X-Reciva-Session-Id", new String(challenge));  // Assume it consists of printable characters
      // reciva-token
      responseBuilder.header("Set-Cookie", "JSESSIONID=ABCD1234ABCD1234; Path=/portal-newformt");
      if(!bHead)
      {
      	responseBuilder.entity(challenge);
      }

      response = responseBuilder.build();
      log.debug("makeChallengeResponse: response status: " + response.getStatusInfo().getStatusCode() + " " + response.getStatusInfo().getReasonPhrase());
      log.debug("makeChallengeResponse: response header:\n" + getMultivaluedMapString(response.getStringHeaders()));
      return response;
   }
   
	private String logRequest(String path, UriInfo ui, HttpHeaders headers)
	{
		log.info("logRequest: path:" + path);
		log.debug("logRequest: Request URL:\n" + ui.getRequestUri().toASCIIString());
		log.debug("logRequest: params\n" + getQueryParamsString(ui));
		log.debug("logRequest: headers\n" + getHeadersString(headers)); 
		StringBuilder sb = new StringBuilder();
		sb.append("Request URL:\n").append(ui.getRequestUri().toASCIIString()).append("\n\n");
		sb.append("Request Header:\n").append(getHeadersString(headers)).append("\n\n");
		
		return sb.toString();
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
