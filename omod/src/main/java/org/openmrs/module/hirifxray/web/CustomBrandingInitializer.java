package org.openmrs.module.hirifxray.web;

import javax.servlet.ServletContext;

import org.openmrs.module.hirifxray.CustomBrandingHandler;
import org.springframework.web.context.ServletContextAware;

/**
 * Contains any logic we want to run at the time of ServletContext startup
 */
public class CustomBrandingInitializer implements ServletContextAware {

    /**
     * Sets the ServletContext
     */
    public void setServletContext(ServletContext servletContext) {
		CustomBrandingHandler.insertCustomizationIntoWebapp(servletContext.getRealPath("") + "/WEB-INF/");
    }
}  
