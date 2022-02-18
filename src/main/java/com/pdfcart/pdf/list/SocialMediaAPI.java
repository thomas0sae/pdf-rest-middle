package com.pdfcart.pdf.list;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jboss.resteasy.annotations.GZIP;
import org.owasp.encoder.Encode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.io.IOException;

@Path("/socialmedia")
public class SocialMediaAPI {
    @GET
    @Path("/info/{domainName}")
    @Produces("text/html")
    public void showSingleCrawledPdf(@Context HttpServletResponse response, @Context HttpServletRequest request,
                                         @PathParam("domainName") String domainName)
            throws ServletException, IOException {
        domainName = Encode.forHtmlAttribute(domainName);
        request.setAttribute("searchKeyword", domainName);
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        domainName = Encode.forHtml(domainName);
        //String jsonReturn = "{\"socialLinks\":{\"domain\":\"oembed.com\",\"url\":\"https://www.oembed.com\",\"fbLinks\":\"https://www.facebook.com/, https://developers.facebook.com/docs/instagram/oembed, https://developers.facebook.com/docs/plugins/oembed\",\"twitterLinks\":\"https://publish.twitter.com/oembed?url\\u003dhttps%3A%2F%2Ftwitter.com%2Fi%2Fmoments%2F650667182356082688, https://dev.twitter.com/rest/reference/get/statuses/oembed, http://www.twitter.com/\",\"youtube\":\"http://api.embed.ly/1/oembed?url\\u003dhttp%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3DB-m6JDYRFvk, https://www.youtube.com/, https://www.youtube.com/oembed?url\\u003dhttp%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3DiwGFalTRHDA, https://api.microlink.io?url\\u003dhttp%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3DB-m6JDYRFvk\\u0026iframe\"},\"domain\":\"oembed.com\",\"url\":\"https://www.oembed.com\"}";
        //JsonElement gson1 = new JsonParser().parse(jsonReturn);
        JsonObject jObject = SocialMediaAPIUtil.getSMediaListForDomainFromCache(domainName);
        //System.out.println("result "+jObject);
        request.setAttribute("smresults", jObject);
        request.getRequestDispatcher("/smresults.jsp").forward(request, response);
    }

    @GET
    @GZIP
    @Path("/search")
    @Produces("text/html")
    public void getSearchLandingPage(@Context HttpServletResponse response, @Context HttpServletRequest request,
                                     @QueryParam("q") String searchKeyword) throws ServletException, IOException
    {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        //System.out.println("searchKeyword "+searchKeyword);
        if (searchKeyword == null || searchKeyword.trim().equals(""))
        {
            searchKeyword = "nike.com";
        }
        searchKeyword = Encode.forHtmlAttribute(searchKeyword);
        request.setAttribute("searchKeyword", searchKeyword);
        JsonObject jObject = SocialMediaAPIUtil.getSMediaListForDomainFromCache(searchKeyword);
        //System.out.println("search result "+jObject);
        request.setAttribute("smresults", jObject);
        request.getRequestDispatcher("/smresults.jsp").forward(request, response);
    }
}