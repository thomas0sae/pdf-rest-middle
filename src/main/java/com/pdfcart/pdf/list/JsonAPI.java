package com.pdfcart.pdf.list;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jboss.resteasy.annotations.GZIP;
import org.owasp.encoder.Encode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/sedziezjkzd/")
public class JsonAPI {
    @GET
   //@GZIP
    @Path("/")
    @Produces("application/json")
    public Response getRandomResultsFirstPage(@Context HttpServletResponse response, @Context HttpServletRequest request,
                                              @QueryParam("page") String page) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        JsonObject jObject = RestAPIUtil.getRandomListToDisplayFromCache();
        Gson gson = new Gson();
        String result = gson.toJson(jObject);
        //.println("result "+result);
        return Response.status(200).entity(result).build();
    }

    @GET
    //@GZIP
    @Path("/single/{pdfHash}/{pdfName}")
    @Produces("application/json")
    public Response showSingleCrawledPdf(@Context HttpServletResponse response, @Context HttpServletRequest request,
                                         @PathParam("pdfName") String pdfName, @PathParam("pdfHash") String pdfHash)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        pdfHash = Encode.forHtml(pdfHash);
        pdfName = Encode.forHtml(pdfName);
        JsonObject jObject = RestAPIUtil.getSingleCrawledPDF(pdfHash);
        Gson gson = new Gson();
        String result = gson.toJson(jObject);
        //System.out.println("result "+result);
        return Response.status(200).entity(result).build();
    }

    @GET
    //@GZIP
    @Path("/single/2/{pdfHash}/{pdfName}")
    @Produces("application/json")
    public Response showSingleCrawledPdfForLG(@Context HttpServletResponse response, @Context HttpServletRequest request,
                                              @PathParam("pdfName") String pdfName, @PathParam("pdfHash") String pdfHash)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        pdfHash = Encode.forHtml(pdfHash);
        pdfName = Encode.forHtml(pdfName);
        JsonObject jObject = RestAPIUtil.getSingleCrawledPDFFromLG(pdfHash);
        Gson gson = new Gson();
        String result = gson.toJson(jObject);
        //System.out.println("result "+result);
        return Response.status(200).entity(result).build();

    }

    @GET
    //@GZIP
    @Path("/search")
    @Produces("application/json")
    public Response getSearchLandingPage(@Context HttpServletResponse response, @Context HttpServletRequest request,
                                         @QueryParam("q") String searchKeyword, @QueryParam("page") String page) throws ServletException, IOException {
        page = Encode.forHtml(page);
        int pgCount = 1;
        if (searchKeyword == null || searchKeyword.trim().equals("")) {
            return getRandomResultsFirstPage(response, request, page);
        } else {
            if (page != null && page.trim().length() > 0) {
                try {
                    pgCount = Integer.valueOf(page);
                } catch (NumberFormatException nfe) {
                }
                if (pgCount >= 1000) {
                    return Response.status(200).entity("{}").build();
                }
            }
            searchKeyword = Encode.forHtmlAttribute(searchKeyword);
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            JsonObject jObject = RestAPIUtil.getPDFListForSearchKeywordFromCache(searchKeyword,
                    ((pgCount - 1) * 30) + 1);
            Gson gson = new Gson();
            String result = gson.toJson(jObject);
            //System.out.println("result "+result);
            return Response.status(200).entity(result).build();
            /*
             * return Response.status(Response.Status.SEE_OTHER)
             * .header(HttpHeaders.LOCATION, "/pdf/") .build();
             */
        }
    }
}