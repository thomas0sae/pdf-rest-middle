package com.pdfcart.pdf.list;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RestAPIUtil
{
	private static Random rand = new Random();

	public static final String elasticURL = "http://localhost:9200";
	private final static LoadingCache<Integer, JsonObject> cacheRandomList = CacheBuilder.newBuilder().maximumSize(100)
			.expireAfterWrite(10, TimeUnit.HOURS).build(new CacheLoader<Integer, JsonObject>()
			{
				public JsonObject load(Integer index)
				{
					//System.out.println("Cache Hit for cacheRandomList, loading fresh "+index);
					return getRandomPDFList();
				}
			});

	private final static LoadingCache<String, JsonObject> cacheRecentSearchList = CacheBuilder.newBuilder().maximumSize(20)
			.expireAfterWrite(10, TimeUnit.HOURS).build(new CacheLoader<String, JsonObject>()
			{
				public JsonObject load(String searchKeyWord)
				{
					//System.out.println("Cache Hit for cacheRecentSearchList, loading fresh "+searchKeyWord);
					return getPDFListForSearchKeyword(searchKeyWord);
				}
			});


	private final static LoadingCache<String, JsonObject> cacheRecentDomainList = CacheBuilder.newBuilder().maximumSize(20)
			.expireAfterWrite(10, TimeUnit.HOURS).build(new CacheLoader<String, JsonObject>()
			{
				public JsonObject load(String domain)
				{
					//System.out.println("Cache Hit for cacheRecentDomainList, loading fresh "+domain);
					return getPDFListForDomain(domain);
				}
			});



	public static JsonObject getRandomListToDisplayFromCache()
	{
		Integer randomInt = rand.nextInt(101);
		return (JsonObject) cacheRandomList.getUnchecked(randomInt);
	}

	private static JsonObject getRandomPDFList()
	{
		String query = "{\"size\":10,\"query\":{\"function_score\":{\"functions\":[{\"random_score\":{\"seed\":\"" +
				System.currentTimeMillis() + "\"}}]}}}";
		JsonObject jObject = getJsonObjectForSearchQuery(query);
		return jObject;
	}

	public static Object[] getAllRecentSearchFromCache()
	{
		return cacheRecentSearchList.asMap().keySet().toArray();
	}

	public static JsonObject getPDFListForSearchKeywordFromCache(String searchKeyword, int page)
	{
		if (page > 1)
		{
			return getPDFListForSearchKeywordWithPagination(searchKeyword, page);
		}
		return (JsonObject)cacheRecentSearchList.getUnchecked(searchKeyword);
	}

	private static JsonObject getPDFListForSearchKeyword(String searchKeyword)
	{
		String boostQuery = "{\"query\":{\"bool\":{\"should\":[{\"query_string\":{\"query\":\"" + searchKeyword + "\"}}]}},\"size\": 30}";
		JsonObject jObject = getJsonObjectForSearchQuery(boostQuery);
		return jObject;
	}

	public static JsonObject getPDFListForSearchKeywordWithPagination(String searchKeyword, int paginStart)
	{
		String boostQuery = "{\"query\":{\"bool\":{\"should\":[{\"query_string\":{\"query\":\"" + searchKeyword + "\"}}]}},\"from\": " + paginStart + ",\"size\":30}";
		JsonObject jObject = getJsonObjectForSearchQuery(boostQuery);
		return jObject;
	}

	public static Set<String> getAllRecentDomainFromCache()
	{
		//System.out.println("cacheRecentDomainList " + cacheRecentDomainList.asMap().keySet());
		return cacheRecentDomainList.asMap().keySet();
	}

	public static JsonObject getPDFListForDomainFromCache(String domain)
	{
		return (JsonObject)cacheRecentDomainList.getUnchecked(domain);
	}

	private static JsonObject getPDFListForDomain(String domain)
	{
		String query = "{\"query\":{\"bool\":{\"must\":[{\"term\":{\"topDmn.keyword\":\"" + domain + "\"}}]}},\"from\":0,\"size\":15}";
		JsonObject jObject = getJsonObjectForSearchQuery(query);
		return jObject;
	}

	private static JsonObject getJsonObjectForSearchQuery(String query)
	{
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://localhost:9200");
		ElasticRestProxyClient simple = (ElasticRestProxyClient)target.proxy(ElasticRestProxyClient.class);
		Response returned = simple.getSearchResultsForQuery("", query);

		String returnedString = (String)returned.readEntity(String.class);
		client.close();

		JsonElement gson = new JsonParser().parse(returnedString);
		return gson.getAsJsonObject();
	}

	public static JsonObject getSingleCrawledPDF(String pdfHash)
	{
		//System.out.println("pdfHash " + pdfHash);
		ResteasyClient client = new ResteasyClientBuilder().build();
		//System.out.println("client " + client);
		ResteasyWebTarget target = client.target("http://localhost:9200");
		//System.out.println("target " + target);
		ElasticRestProxyClient simple = (ElasticRestProxyClient)target.proxy(ElasticRestProxyClient.class);
		//System.out.println("simple " + simple);
		Response returned = simple.getSingleLinkedPdf(pdfHash);
		//System.out.println("returned " + returned);

		String returnedEntity = (String)returned.readEntity(String.class);
		//System.out.println("returnedEntity " + returnedEntity);
		client.close();
		JsonElement gson = new JsonParser().parse(returnedEntity);
		return gson.getAsJsonObject();
	}

	public static JsonObject getPDFDocumentForHash(String pdfHash)
	{
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://localhost:9200");
		ElasticRestProxyClient simple = (ElasticRestProxyClient)target.proxy(ElasticRestProxyClient.class);
		Response returned = simple.getSingleLinkedPdf(pdfHash);
		client.close();

		JsonElement gson = new JsonParser().parse((String)returned.readEntity(String.class));
		return gson.getAsJsonObject();
	}


	public static boolean verifyPDFExists(String urlStr)
	{
		int responseCode = 404;
		URL u;
		try
		{
			u = new URL(urlStr);
			HttpURLConnection huc = (HttpURLConnection) u.openConnection();
			huc.setRequestMethod("GET");
			huc.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36");
			huc.connect();
			responseCode = huc.getResponseCode();
		}
		catch (Exception e)
		{
			//e.printStackTrace();
		}
		return responseCode == 200;
	}

	public static void main(String[] args) {
		System.out.println(verifyPDFExists("http://boulejoyeusedesiles.fr/images/document/CALENDRIER_DU_VAR_2020.pdf"));
		System.out.println(verifyPDFExists("https://blog.lsb-uso.com/docs/guiasindicalistasvascos.pdf"));
		System.out.println(verifyPDFExists("https://www.yourhealthylifeshop.co.uk/wp-content/uploads/2018/10/Privacy_policy_and_cookies.pdf"));
		System.out.println(verifyPDFExists("https://www.yachtfocus.com/wp-content/uploads/2020/03/YF186_CharterFocus.pdf"));
	}
}