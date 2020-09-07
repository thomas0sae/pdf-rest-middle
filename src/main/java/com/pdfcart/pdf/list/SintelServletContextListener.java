package com.pdfcart.pdf.list;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class SintelServletContextListener implements ServletContextListener
{
	public void contextDestroyed(ServletContextEvent arg0)
	{
		System.out.println("ServletContextListener destroyed");
	}

	public void contextInitialized(ServletContextEvent arg0)
    {
		System.out.println("ServletContextListener started");
    }
}
