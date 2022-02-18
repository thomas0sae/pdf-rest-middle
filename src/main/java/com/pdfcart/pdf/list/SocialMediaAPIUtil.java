package com.pdfcart.pdf.list;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SocialMediaAPIUtil
{
	private static Random rand = new Random();

	public static final String elasticURL = "http://localhost:9200";

	private final static LoadingCache<String, JsonObject> cacheRecentSearchList = CacheBuilder.newBuilder().maximumSize(100)
			.expireAfterWrite(10, TimeUnit.HOURS).build(new CacheLoader<String, JsonObject>()
			{
				public JsonObject load(String searchKeyWord)
				{
					//System.out.println("Cache Hit for cacheRecentSearchList, loading fresh "+searchKeyWord);
					return getSDomainListForSearchKeyword(searchKeyWord);
				}
			});

	private final static LoadingCache<String, JsonObject> cacheRecentDomainList = CacheBuilder.newBuilder().maximumSize(100)
			.expireAfterWrite(10, TimeUnit.HOURS).build(new CacheLoader<String, JsonObject>()
			{
				public JsonObject load(String domain)
				{
					//System.out.println("Cache Hit for cacheRecentDomainList, loading fresh "+domain);
					return getSMediaListForDomain(domain);
				}
			});

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

	public static Set<String> getAllRecentDomainFromCacheSMedia()
	{
		//System.out.println("cacheRecentDomainList " + cacheRecentDomainList.asMap().keySet());
		return cacheRecentDomainList.asMap().keySet();
	}

	public static JsonObject getSMediaListForDomainFromCache(String domain)
	{
		return (JsonObject)cacheRecentDomainList.getUnchecked(domain);
	}

	private static JsonObject getSDomainListForSearchKeyword(String searchKeyword)
	{
		String boostQuery = "{\"query\":{\"bool\":{\"should\":[{\"query_string\":{\"query\":\"" + searchKeyword + "\"}}]}},\"size\": 30}";
		JsonObject jObject = getJsonObjectForSearchQuerySMedia(boostQuery);
		return jObject;
	}

	public static JsonObject getPDFListForSearchKeywordWithPagination(String searchKeyword, int paginStart)
	{
		String boostQuery = "{\"query\":{\"bool\":{\"should\":[{\"query_string\":{\"query\":\"" + searchKeyword + "\"}}]}},\"from\": " + paginStart + ",\"size\":30}";
		JsonObject jObject = getJsonObjectForSearchQuerySMedia(boostQuery);
		return jObject;
	}

	private static JsonObject getSMediaListForDomain(String domain)
	{
		String query = "{\"query\":{\"bool\":{\"must\":[{\"term\":{\"domain.keyword\":\"" + domain + "\"}}]}}}";
		JsonObject jObject = getJsonObjectForSearchQuerySMedia(query);
		return jObject;
	}

	private static JsonObject getJsonObjectForSearchQuerySMedia(String query)
	{
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://localhost:9200");
		target.register(new BasicAuthentication("elastic", "TechPass@123!"));
		ElasticRestProxyClient simple = (ElasticRestProxyClient)target.proxy(ElasticRestProxyClient.class);
		Response returned = simple.getSearchResultsForQuerySMedia("", query);
		String returnedString = (String)returned.readEntity(String.class);
		client.close();

		JsonElement gson = new JsonParser().parse(returnedString);
		return gson.getAsJsonObject();
	}

	public static JsonObject getSingleDomainHashSMedia(String smHash)
	{
		//System.out.println("pdfHash " + pdfHash);
		ResteasyClient client = new ResteasyClientBuilder().build();
		//System.out.println("client " + client);
		ResteasyWebTarget target = client.target("http://localhost:9200");
		target.register(new BasicAuthentication("elastic", "TechPass@123!"));
		//System.out.println("target " + target);
		ElasticRestProxyClient simple = (ElasticRestProxyClient)target.proxy(ElasticRestProxyClient.class);
		//System.out.println("simple " + simple);
		Response returned = simple.getSingleDomainSMedia(smHash);
		//System.out.println("returned " + returned);

		String returnedEntity = (String)returned.readEntity(String.class);
		//System.out.println("returnedEntity " + returnedEntity);
		client.close();
		JsonElement gson = new JsonParser().parse(returnedEntity);
		return gson.getAsJsonObject();
	}

}