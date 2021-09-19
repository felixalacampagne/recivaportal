package com.felixalacampagne.recivaportal;


import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
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
   @Produces(MediaType.TEXT_PLAIN)
   public Response getChallenge(@QueryParam("serial") String serial, @QueryParam("sp") String sp)
   {
   	log.info("getChallenge: serial:" + serial + " sp:" + sp);
   	ResponseBuilder responseBuilder = makeChallengeResponse(false, serial);
      return responseBuilder.build();
   }
   
   @HEAD
   @Path("/challenge")
   @Produces(MediaType.TEXT_PLAIN)
   public Response getHeadChallenge(@QueryParam("serial") String serial, @QueryParam("sp") String sp)
   {
   	// HEAD response should only return the Headers which would be returned by the equivalent GET request.
   	// A GET request should follow but haven't seen one from the radio yet...
   	log.info("getHeadChallenge: serial:" + serial + " sp:" + sp);
   	ResponseBuilder responseBuilder = makeChallengeResponse(true, serial);
      return responseBuilder.build();
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

   private ResponseBuilder makeChallengeResponse(boolean bHead, String entity)
   {
   	ResponseBuilder responseBuilder = Response.status(200);
      responseBuilder.lastModified(new Date());
      responseBuilder.header("Content-Length", "" + entity.toString().length());
      responseBuilder.header("x-reciva-challenge-format", "sernum");
      responseBuilder.header("Set-Cookie", "JSESSIONID=ABCD1234; Path=/portal");
      if(!bHead)
      {
      	responseBuilder.entity(entity.toString());
      }
      return responseBuilder;
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
