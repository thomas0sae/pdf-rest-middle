package com.pdfcart.pdf.list;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@ApplicationPath("")
public class DomainApp extends Application {

	public Set<Class<?>> getClasses() 
    {
        Set<Class<?>> s = new HashSet<Class<?>>();
        s.add(RestAPI.class);
        //s.add(JsonAPI.class);
        s.add(SocialMediaAPI.class);
        return s;
    }
}
