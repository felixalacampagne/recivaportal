package com.felixalacampagne.recivaportal;

import com.sun.istack.logging.Logger;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

@Path("/")
public class RecivaPortalRest
{
	final Logger log = Logger.getLogger(this.getClass());
   @GET
   @Path("/challenge")
   @Produces(MediaType.TEXT_PLAIN)
   public String getChallenge(@QueryParam("serial") String serial) 
   {
   	log.info("serial:" + serial);
   	// HEAD ?serial=0000df34&sp=v257-a-865-a-476
   	return "Return from getChallenge";
   }
   
   @HEAD
   @Path("/challenge")
   @Produces(MediaType.TEXT_PLAIN)
   public String getHeadChallenge(@QueryParam("serial") String serial) 
   {
   	log.info("serial:" + serial);
   	// HEAD ?serial=0000df34&sp=v257-a-865-a-476
   	return "Return from getHeadChallenge";
   }   

   @HEAD
   @Path("/{name}")
   @Produces(MediaType.TEXT_PLAIN)
   public String getHeadAny(@PathParam("name") String path, @Context UriInfo ui) 
   {
   	log.info("getHeadAny:" + path);
   	doAny(path, ui);
   	return "getHeadAny called: path:" + path;
   }
   
   @GET
   @Path("/{name}")
   @Produces(MediaType.TEXT_PLAIN)
   public String getAny(@PathParam("name") String path, @Context UriInfo ui) 
   {
   	log.info("getAny: path:" + path);
   	doAny(path, ui);
   	return "getAny called: path:" + path;
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
