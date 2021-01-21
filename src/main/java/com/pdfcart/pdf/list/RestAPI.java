package com.pdfcart.pdf.list;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.owasp.encoder.Encode;

@Path("/pdf/")
public class RestAPI
{
	@GET
	@GZIP
	@Path("/")
	@Produces("text/html")
	public void getRandomResultsFirstPage(@Context HttpServletResponse response, @Context HttpServletRequest request,
			@QueryParam("page") String page) throws ServletException, IOException
	{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		if (page != null && page.trim().length() > 0)
		{
			if (Integer.valueOf(page) >= 100)
			{
				request.setAttribute("message", "Please narrow down your search! I can't bring " + "everything to you!");
				request.getRequestDispatcher("/noShow.jsp").forward(request, response);
				return;
			}
		}
		else
		{
			page = "1";
		}

		JsonObject jObject = RestAPIUtil.getRandomListToDisplayFromCache();
		request.setAttribute("searchKeyword", "");
		request.setAttribute("currPage", page);
		request.setAttribute("data", jObject);
		//request.setAttribute("domCache", (Set<String>) RestAPIUtil.getAllRecentDomainFromCache());
		request.getRequestDispatcher("/results.jsp").forward(request, response);
	}


	/*@GET
	@GZIP
	@Path("/domain")
	@Produces("text/html")
	public void getDomainCrawledPDFs(@Context HttpServletResponse response, @Context HttpServletRequest request,
			@QueryParam("q") String domain,
			@QueryParam("page") String page
			) throws ServletException, IOException
	{
		System.out.println("Domain : "+domain);
		if(domain == null || domain.trim().length() > 0)
		{
			request.setAttribute("message",
					"Please select a domain or use a search term!");
			request.getRequestDispatcher("/noShow.jsp").forward(request, response);
			return;
		}
		if (page != null && page.trim().length() > 0)
		{
			if (Integer.valueOf(page) >= 1000)
			{
				request.setAttribute("message",
						"Please narrow down your search! I can't bring " + "everything to you!");
				request.getRequestDispatcher("/noShow.jsp").forward(request, response);
				return;
			}
		}
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		JsonObject jObject = RestAPIUtil.getPDFListForDomainFromCache(domain);
		//System.out.println(jObject);
		request.setAttribute("data", jObject);
		request.setAttribute("domain", domain);
		//request.setAttribute("domCache", (Set<String>) RestAPIUtil.getAllRecentDomainFromCache());
		request.getRequestDispatcher("/domainResults.jsp").forward(request, response);
	}*/

	// Correct -
	// http://localhost:8080/pdfCart/pdf/single/this-is-a-test-pdf/C351ACA59FE5583E72C7A4A6B810CD8C
	// Wrong -
	// http://localhost:8080/pdfCart/pdf/single/this-is-a-test-pdf/C351ACA59FE5583E72C7A4A6B810CD8C

	@GET
	@GZIP
	@Path("/download/{pdfHash}/{pdfName}")
	@Produces("text/html")
	public void showSingleCrawledPdf(@Context HttpServletResponse response, @Context HttpServletRequest request,
			@PathParam("pdfName") String pdfName, @PathParam("pdfHash") String pdfHash)
			throws ServletException, IOException
	{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		pdfHash = Encode.forHtml(pdfHash);
		pdfName = Encode.forHtml(pdfName);
		System.out.println("pdfHash "+pdfHash);
		System.out.println("pdfName "+pdfName);
		JsonObject jObject = RestAPIUtil.getSingleCrawledPDF(pdfHash);
		//JsonElement _sourceElem = jObject.get("_source");
		//if (_sourceElem != null) {
			//jObject = _sourceElem.getAsJsonObject();
			request.setAttribute("data", jObject);
		//}
		//request.setAttribute("domCache", (Set<String>) RestAPIUtil.getAllRecentDomainFromCache());
		request.getRequestDispatcher("/downloadSingle.jsp").forward(request, response);
	}

	@GET
	@GZIP
	@Path("/download/2/{pdfHash}/{pdfName}")
	@Produces("text/html")
	public void showSingleCrawledPdfForLG(@Context HttpServletResponse response, @Context HttpServletRequest request,
									 @PathParam("pdfName") String pdfName, @PathParam("pdfHash") String pdfHash)
			throws ServletException, IOException
	{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		pdfHash = Encode.forHtml(pdfHash);
		pdfName = Encode.forHtml(pdfName);
		System.out.println("pdfHash "+pdfHash);
		JsonObject jObject = RestAPIUtil.getSingleCrawledPDFFromLG(pdfHash);
		//JsonElement _sourceElem = jObject.get("_source");
		//if (_sourceElem != null) {
		//jObject = _sourceElem.getAsJsonObject();
		request.setAttribute("data", jObject);
		request.setAttribute("isPDF2", Boolean.TRUE);
		//}
		System.out.println("pdfName "+jObject);
		//request.setAttribute("domCache", (Set<String>) RestAPIUtil.getAllRecentDomainFromCache());
		request.getRequestDispatcher("/downloadSingle.jsp").forward(request, response);
	}

	@GET
	@GZIP
	@Path("/linked/{pdfHash}/{pdfName}")
	@Produces("text/html")
	public void getSingleLinkedPdf(@Context HttpServletResponse response, @Context HttpServletRequest request,
			@PathParam("pdfName") String pdfName, @PathParam("pdfHash") String pdfHash)
			throws ServletException, IOException
	{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		pdfHash = Encode.forHtml(pdfHash);
		pdfName = Encode.forHtml(pdfName);
		System.out.println("pdfHash "+pdfHash);
		System.out.println("pdfName "+pdfName);
		JsonObject jObject = RestAPIUtil.getPDFDocumentForHash(pdfHash);
		// System.out.println(jObject);
		request.setAttribute("data", jObject);
		//request.setAttribute("domCache", (Set<String>) RestAPIUtil.getAllRecentDomainFromCache());
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}

	/*
	 * @GET
	 * 
	 * @GZIP
	 * 
	 * @Path("/search/{searchKeyword}")
	 * 
	 * @Produces("text/html") public void getFirstSearchResultsPdf(@Context
	 * HttpServletResponse response, @Context HttpServletRequest request,
	 * 
	 * @PathParam("searchKeyword") String searchKeyword) throws
	 * ServletException, IOException {
	 * System.out.println("searchKeyword "+searchKeyword); ResteasyClient client
	 * = new ResteasyClientBuilder().build(); ResteasyWebTarget target =
	 * client.target(RestAPIUtil.elasticURL); ElasticRestProxyClient simple =
	 * target.proxy(ElasticRestProxyClient.class); String boost =
	 * "{\"query\":{\"bool\":{\"should\":[{\"query_string\":{\"query\":\""+
	 * searchKeyword+"\"}}]}},\"size\": 30}"; Response returned =
	 * simple.getFirstSearchResultsPdf(searchKeyword, boost); client.close();
	 * JsonElement gson = new
	 * JsonParser().parse(returned.readEntity(String.class)); JsonObject jObject
	 * = gson.getAsJsonObject(); System.out.println(jObject);
	 * request.setAttribute("data", jObject);
	 * request.getRequestDispatcher("/searchResults.jsp").forward(request,
	 * response); }
	 */

	@GET
	@GZIP
	@Path("/search")
	@Produces("text/html")
	public void getSearchLandingPage(@Context HttpServletResponse response, @Context HttpServletRequest request,
			@QueryParam("q") String searchKeyword, @QueryParam("page") String page) throws ServletException, IOException
	{
		page = Encode.forHtml(page);
		int pgCount = 1;
		if (searchKeyword == null || searchKeyword.trim().equals(""))
		{
			getRandomResultsFirstPage(response, request, page);
		}
		else
		{

			if (page != null && page.trim().length() > 0)
			{
				try
				{
					pgCount = Integer.valueOf(page);
				}
				catch (NumberFormatException nfe)
				{

				}
				if (pgCount >= 100)
				{
					request.setAttribute("message", "Please narrow down your search!<br /> I can't bring " + "everything to you!");
					request.getRequestDispatcher("/noShow.jsp").forward(request, response);
					return;
				}
			}
			searchKeyword = Encode.forHtmlAttribute(searchKeyword);
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			//System.out.println("searchKeyword " + searchKeyword);
			// same as /

			// same as /pdf/search/book
			// System.out.println("searchKeyword "+searchKeyword);
			JsonObject jObject = RestAPIUtil.getPDFListForSearchKeywordFromCache(searchKeyword,
					((pgCount - 1) * 30) + 1);
			// System.out.println(jObject);
			request.setAttribute("searchKeyword", searchKeyword);
			request.setAttribute("currPage", pgCount);
			request.setAttribute("data", jObject);
			//request.setAttribute("domCache", (Set<String>) RestAPIUtil.getAllRecentDomainFromCache());
			request.getRequestDispatcher("/results.jsp").forward(request, response);

			/*
			 * return Response.status(Response.Status.SEE_OTHER)
			 * .header(HttpHeaders.LOCATION, "/pdf/") .build();
			 */

		}
	}

	@GET
	@GZIP
	@Path("/verify/{pdfHash}")
	@Produces("text/plain")
	public boolean verifyPDFExists(@Context HttpServletResponse response, @Context HttpServletRequest request,
			@PathParam("pdfHash") String pdfHash) throws UnsupportedEncodingException
	{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		pdfHash = Encode.forHtml(pdfHash);
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target(RestAPIUtil.elasticURL);
		target.register(new BasicAuthentication("elastic", "TechPass@123!"));
		ElasticRestProxyClient simple = target.proxy(ElasticRestProxyClient.class);
		Response returned = simple.getSingleLinkedPdf(pdfHash);
		client.close();
		JsonElement gson = new JsonParser().parse(returned.readEntity(String.class));
		JsonObject jObject = gson.getAsJsonObject();
		//System.out.println("Verification 1 " + jObject);
		JsonElement _sourceElem = jObject.get("_source");
		//System.out.println("Verification 2 " + _sourceElem);
		JsonObject _sourceObj = null;
		if (_sourceElem != null)
		{
			_sourceObj = _sourceElem.getAsJsonObject();
			//System.out.println("Verification 3 " + _sourceObj);
			String urlStr = _sourceObj.get("url").getAsString();
			return RestAPIUtil.verifyPDFExists(urlStr);
		}
		return false;
	}
}