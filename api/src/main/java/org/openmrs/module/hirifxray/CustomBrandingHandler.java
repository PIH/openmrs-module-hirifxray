/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.hirifxray;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Handles Custom Branding
 */
public class CustomBrandingHandler {
	
	protected static final Log log = LogFactory.getLog(CustomBrandingHandler.class);
	private static Map<String, String> replacedFiles = null;

	/**
	 * This is copied largely from Listener.java in core
	 */
	public synchronized static void insertCustomizationIntoWebapp(String baseToPath) {
		if (replacedFiles == null) {
			try {
				replacedFiles = new HashMap<String, String>();
				String baseFromPath = baseToPath + "view/module/hirifxray/customizations/";
				File dirToCopy = new File(baseFromPath);
				replaceContents(dirToCopy, baseFromPath, baseToPath);
			}
			catch (IOException ioe) {
				log.error("IOException while copying customization into webapp...", ioe);
			}
		}
	}
	
	/**
	 * Method for replacing contents from one location to another
	 */
	private synchronized static void replaceContents(File fileToCopy, String baseFromPath, String baseToPath) throws IOException {
		String pathToCopy = fileToCopy.getAbsolutePath();
		if (fileToCopy.exists() && !pathToCopy.startsWith(".")) {
			if (fileToCopy.isDirectory()) {
				for (File f : fileToCopy.listFiles()) {
					replaceContents(f, baseFromPath, baseToPath);
				}
			}
			else {
				// make sure all backslashes are slashes for string replacement to make it work under Windows
				pathToCopy = pathToCopy.replace('\\', '/');
				baseFromPath = baseFromPath.replace('\\', '/');
				String commonFileName = pathToCopy.replace(baseFromPath, "");
				File fileToReplace = new File(baseToPath + commonFileName);
				File backupFile = new File(baseToPath + commonFileName + ".bak");
				if (fileToReplace.exists()) {
					FileUtils.copyFile(fileToReplace, backupFile);
					replacedFiles.put(fileToReplace.getAbsolutePath(), backupFile.getAbsolutePath());
					FileUtils.copyFile(fileToCopy, fileToReplace, false);
					log.info("Replacing " + fileToReplace.getAbsolutePath() + " with " + fileToCopy.getAbsolutePath());
				}
			}
		}
	}
	
	/**
	 * Removes customizations from the webapp
	 */
	public synchronized static void removeCustomizationFromWebapp() {
		if (replacedFiles != null) {
			for (String filename : replacedFiles.keySet()) {
				File fileToCopy = new File(replacedFiles.get(filename));
				File fileToReplace = new File(filename);
				try {
					log.info("Replacing " + fileToReplace.getAbsolutePath() + " with " + fileToCopy.getAbsolutePath());
					FileUtils.copyFile(fileToCopy, fileToReplace, false);
					FileUtils.deleteQuietly(fileToCopy);
				}
				catch (IOException e) {
					log.error("IOException while reverting customization from webapp...", e);
				}
			}
			replacedFiles = null;
		}
	}
	
}
