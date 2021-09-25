package com.felixalacampagne.recivaportal;


import java.util.Date;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.HeaderParam;
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
   public Response getChallenge(@QueryParam("serial") String serial, @QueryParam("sp") String sp)
   {
   	log.info("getChallenge: serial:" + serial + " sp:" + sp);
      return makeChallengeResponse(false, serial);
   }
   
   @HEAD
   @Path("/challenge")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   public Response getHeadChallenge(@QueryParam("serial") String serial, 
         @QueryParam("sp") String sp,
         @Context HttpHeaders headers)
   {
   	// HEAD response should only return the Headers which would be returned by the equivalent GET request.
   	// A GET request should follow but haven't seen one from the radio yet...
   	log.info("getHeadChallenge: serial:" + serial + " sp:" + sp);
   	
   	
      MultivaluedMap<String, String> rh = headers.getRequestHeaders();
      String str = rh.entrySet()
                     .stream()
                     .map(e -> e.getKey() + " = " + e.getValue())
                     .collect(Collectors.joining("\n"));   	
   	log.debug("getHeadChallenge: headers:\n" + str); // once again toString is forking useless! headers.toString());
      return makeChallengeResponse(true, serial);
   }   

   @HEAD
   @Path("/{name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response getHeadAny(@PathParam("name") String path, @Context UriInfo ui) 
   {
   	log.info("getHeadAny:" + path);
   	doAny(path, ui);
   	ResponseBuilder responseBuilder = Response.status(200);
      responseBuilder.lastModified(new Date());
      return responseBuilder.build();
   }
   
   @GET
   @Path("/{name}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response getAny(@PathParam("name") String path, @Context UriInfo ui) 
   {
   	log.info("getAny: path:" + path);
   	doAny(path, ui);
   	ResponseBuilder responseBuilder = Response.status(200);
      responseBuilder.entity("getAny called: path:" + path);
      responseBuilder.lastModified(new Date());
      return responseBuilder.build();

   }

   private Response makeChallengeResponse(boolean bHead, String entity)
   {
      String challenge = "1122334455667788";
      Date lm = new Date(108,1,1,12,0); // 1 Jan 2008 12:00
   	ResponseBuilder responseBuilder = Response.status(200);
   	Response response;
      responseBuilder.lastModified(lm);
      responseBuilder.header("Content-Length", "" + challenge.getBytes().length);
      responseBuilder.type(MediaType.APPLICATION_OCTET_STREAM);
      responseBuilder.header("X-Reciva-Challenge-Format", "sernum");
      responseBuilder.header("Set-Cookie", "JSESSIONID=ABCD1234; Path=/portal");
      if(!bHead)
      {
      	responseBuilder.entity(challenge.getBytes());
      }
      response = responseBuilder.build();
      // TODO log the header - response.toString() does not do this
//      log.debug("makeChallengeResponse: Response:\n" + response.toString());
      return response;
   }
   
	private void doAny(String path, UriInfo ui)
	{
		log.info("doAny: path:" + path);
		MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
//		MultivaluedMap<String, String> pathParams = ui.getPathParameters();
		for(String k : queryParams.keySet())
		{
			log.info("param: " + k + " value: " + queryParams.getFirst(k));
		}
	}   
}
