package com.felixalacampagne.recivaportal;


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
import jakarta.ws.rs.core.UriInfo;


@Path("/")
public class RecivaPortalRest
{
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
      String challenge = "11223344"; // 55667788";
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
      cal.set(2008, 1, 1, 12, 0); // 2008 is when I bought the radio - I'd like it to not bother contacting the server.

   	ResponseBuilder responseBuilder = Response.status(200);
   	Response response;
      responseBuilder.lastModified(cal.getTime());
      responseBuilder.header("Content-Length", "" + challenge.getBytes().length);
      //responseBuilder.type(MediaType.APPLICATION_OCTET_STREAM);
      //responseBuilder.type(MediaType.TEXT_XML);
      responseBuilder.type(MediaType.TEXT_PLAIN_TYPE);
      responseBuilder.header("X-Reciva-Challenge-Format", "sernum");
      // x-reciva-session-id
      // reciva-token
      //responseBuilder.header("Set-Cookie", "JSESSIONID=ABCD1234; Path=/portal");
      if(!bHead)
      {
      	responseBuilder.entity(challenge.getBytes());
      }
      response = responseBuilder.build();
      
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
}
