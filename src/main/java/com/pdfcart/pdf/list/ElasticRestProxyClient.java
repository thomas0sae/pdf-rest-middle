package com.pdfcart.pdf.list;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Consumes(value = MediaType.APPLICATION_JSON)
public interface ElasticRestProxyClient
{
	
	/*
	 * 1. One pdf link direct query
	 * 2. List of pdfs following a query
	 * 3. List of recently indexed pdfs
	 * 4. List of recently searched pdfs
	 * 5. 
	 */

    @GET
    @Path("pdf1/_doc/{pdfHash}")
    @Produces({"application/json;charset=utf-8"})
    public abstract Response getSingleLinkedPdf(@PathParam("pdfHash") String paramString);

    @POST
    @Path("/pdf1/_doc/_search")
    @Produces({"application/json;charset=utf-8"})
    public abstract Response getSearchResultsForQuery(@QueryParam("pretty") String paramString1, String paramString2);

    @GET
    @Path("pdf1/_doc/_search?q={searchKeyword}&from={nextStartIndex}&size=10&sort={sortBy}:desc")
    @Produces({"application/json;charset=utf-8"})
    public abstract Response getNextSearchResultsPdf(@PathParam("searchKeyword") String paramString1, @PathParam("nextStartIndex") String paramString2, @PathParam("sortBy") String paramString3);

}