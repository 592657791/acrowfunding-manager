package com.an.acrowfunding.manager.web.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ManagerListener implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext application = sce.getServletContext();
		String path = application.getContextPath();
		application.setAttribute("APP_PATH", path);
	}

}
