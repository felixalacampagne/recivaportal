package com.felixalacampagne.recivaportal;


import java.net.URI;
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
   	try
   	{
	   	String path = ui.getPath();
	   	log.info("postSession: request path: " + path);
	   	log.info("postSession: request body length: " + messageBody.length);
	   	log.info("postSession: request body:\n" + dumpBuffer(messageBody));
	   	String body = doAny(path, ui, headers);
	   	body = "<stations><station id=\"2765\" custommenuid=\"0\"><version>5127</version>\r\n"
	      		+ "<data><stream id=\"2149\"><url>http://radios.argentina.fm:9270/stream</url>\r\n"
	      		+ "<title>La 2x4 Tango Buenos Aires</title>\r\n"
	      		+ "<protocol>http</protocol>\r\n"
	      		+ "<metadata><use-metadata author=\"true\" title=\"true\"></use-metadata>\r\n"
	      		+ "</metadata>\r\n"
	      		+ "</stream>\r\n"
	      		+ "</data>\r\n"
	      		+ "<genres>23</genres>\r\n"
	      		+ "<locations>34</locations>\r\n"
	      		+ "</station></stations>";
	   	body =  "<stations></stations>";
	   	
	   	
	   	byte[] authbytes = Utils.base64ToByteArray(auth);
	   	log.info("postSession: auth bytes:\n" + dumpBuffer(authbytes));
	   	
	   	RecivaProtocolHandler rph = new RecivaProtocolHandler();
	   	RecivaEncryption renc = new RecivaEncryption();
	   	
	   	
	   	byte [] payload = rph.makeFirstDataBlock(body);

	   	payload = renc.recivaDESencrypt(payload);
	   	ResponseBuilder responseBuilder = Response.status(200);
	 
	      
	      Calendar cal = Calendar.getInstance();
	      // cal.set(2008, 1, 1, 12, 0); // 2008 is when I bought the radio - I'd like it to not bother contacting the server.
	
	      responseBuilder.lastModified(cal.getTime());
	      cal.add(Calendar.DAY_OF_MONTH,1);
	      responseBuilder.expires(cal.getTime());
	      responseBuilder.encoding("UTF-8");
	      responseBuilder.header("Content-Length", "" + payload.length);
	      responseBuilder.type(MediaType.APPLICATION_OCTET_STREAM_TYPE);
	      //responseBuilder.header("X-Reciva-Challenge-Format", "sernum");
	      // x-reciva-session-id
	      responseBuilder.header("X-Reciva-Session-Id", auth);
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
   	String body = doAny(path, ui, headers);
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
   	String body = doAny(path, ui, headers);
   	
   	ResponseBuilder responseBuilder = Response.status(200);
      responseBuilder.entity("postAny: path:" + path);
      responseBuilder.lastModified(new Date());
      responseBuilder.header("Content-Length", "" + body.getBytes().length);
      responseBuilder.entity(body);
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
      
      // Finally received a GET request for challenge (with the URL /portal-newformat.
      // It was followed by a POST request for session... the next phase begins, but first some tidy up.
    
      // NB. tomcat strips out the reasonphrase, and the apache server-tomcat interface puts something else in its place!
   	ResponseBuilder responseBuilder = Response.status(rspstat.getStatusCode(), "Ok"); //rspstat.getReasonPhrase());
   	
      responseBuilder.lastModified(cal.getTime());
      cal.add(Calendar.DAY_OF_MONTH,1);
      responseBuilder.expires(cal.getTime());
      responseBuilder.encoding("UTF-8");
      responseBuilder.header("Content-Length", "" + challenge.getBytes().length);
      responseBuilder.type(MediaType.TEXT_PLAIN_TYPE);
      responseBuilder.header("X-Reciva-Challenge-Format", "sernum");
      // x-reciva-session-id
      responseBuilder.header("X-Reciva-Session-Id", challenge);
      // reciva-token
      responseBuilder.header("Set-Cookie", "JSESSIONID=ABCD1234ABCD1234; Path=/portal");
      if(!bHead)
      {
      	responseBuilder.entity(challenge.getBytes());
      }

      response = responseBuilder.build();
      log.debug("makeChallengeResponse: response status: " + response.getStatusInfo().getStatusCode() + " " + response.getStatusInfo().getReasonPhrase());
      log.debug("makeChallengeResponse: response header:\n" + getMultivaluedMapString(response.getStringHeaders()));
      return response;
   }
   
	private String doAny(String path, UriInfo ui, HttpHeaders headers)
	{
		log.info("doAny: path:" + path);
		log.debug("doAny: params\n" + getQueryParamsString(ui));
		log.debug("doAny: headers\n" + getHeadersString(headers)); 
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
