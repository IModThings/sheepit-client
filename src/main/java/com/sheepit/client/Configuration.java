/*
 * Copyright (C) 2010-2014 Laurent CLOUET
 * Author Laurent CLOUET <laurent.clouet@nopnop.net>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 2
 * of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.sheepit.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import com.sheepit.client.hardware.cpu.CPU;
import com.sheepit.client.hardware.gpu.GPUDevice;
import com.sheepit.client.os.OS;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data public class Configuration {
	
	public static final String jarVersion = getJarVersion();
	
	public enum ComputeType {
		CPU_GPU, CPU, GPU
	} // accept job for ...
	
	private String configFilePath;
	private File workingDirectory;
	private File sharedDownloadsDirectory;
	private File storageDirectory; // for permanent storage (binary archive)
	private File archiveDirectory;
	private boolean userHasSpecifiedACacheDir;
	private String static_exeDirName;
	private String login;
	private String password;
	private String proxy;
	private int maxUploadingJob;
	private int nbCores;
	private long maxAllowedMemory; // in KiB, max memory allowed for render
	private int maxRenderTime; // max render time per frame allowed
	private int priority;
	private ComputeType computeMethod;
	private GPUDevice GPUDevice;
	private boolean detectGPUs;
	private boolean printLog;
	private List<Pair<Calendar, Calendar>> requestTime;
	private long shutdownTime;
	private String shutdownMode;
	private String extras;
	private boolean autoSignIn;
	private boolean useSysTray;
	private boolean headless;
	private String UIType;
	private String hostname;
	private String theme;
	
	public Configuration(File cache_dir_, String login_, String password_) {
		this.configFilePath = null;
		this.login = login_;
		this.password = password_;
		this.proxy = null;
		this.hostname = this.getDefaultHostname();
		this.static_exeDirName = "exe";
		this.maxUploadingJob = 1;
		this.nbCores = -1; // ie not set
		this.maxAllowedMemory = -1; // ie not set
		this.maxRenderTime = -1; // ie not set
		this.priority = 19; // default lowest
		this.computeMethod = null;
		this.GPUDevice = null;
		this.userHasSpecifiedACacheDir = false;
		this.detectGPUs = true;
		this.workingDirectory = null;
		this.sharedDownloadsDirectory = null;
		this.storageDirectory = null;
		this.archiveDirectory = null;
		this.setCacheDir(cache_dir_);
		this.printLog = false;
		this.requestTime = null;
		this.shutdownTime = -1;
		this.shutdownMode = "soft";
		this.extras = "";
		this.autoSignIn = false;
		this.useSysTray = false;
		this.headless = java.awt.GraphicsEnvironment.isHeadless();
		this.UIType = null;
		this.theme = null;
	}
	
	public Configuration(Configuration config) {
		this(config.configFilePath, config.workingDirectory, config.sharedDownloadsDirectory, config.storageDirectory, config.archiveDirectory, config.userHasSpecifiedACacheDir,
			config.static_exeDirName, config.login, config.password, config.proxy, config.maxUploadingJob, config.nbCores, config.maxAllowedMemory, config.maxRenderTime,
			config.priority, config.computeMethod, config.GPUDevice, config.detectGPUs, config.printLog, config.requestTime, config.shutdownTime,
			config.shutdownMode, config.extras, config.autoSignIn, config.useSysTray, config.headless, config.UIType, config.hostname, config.theme);
	}
	
	public String toString() {
		String c = "    CFG: ", n ="\n";
		return
			c + "configFilePath:            " + configFilePath + n +
				c + "workingDirectory:          " + workingDirectory + n +
				c + "sharedDownloadsDirectory:  " + sharedDownloadsDirectory + n +
				c + "storageDirectory:          " + storageDirectory + n +
				c + "archiveDirectory:			" + archiveDirectory + n +
				c + "userHasSpecifiedACacheDir: " + userHasSpecifiedACacheDir + n +
				c + "static_exeDirName:         " + static_exeDirName + n +
				c + "login:                     " + login + n +
				c + "proxy:                     " + proxy + n +
				c + "maxUploadingJob:           " + maxUploadingJob + n +
				c + "nbCores:                   " + nbCores + n +
				c + "maxAllowedMemory:          " + maxAllowedMemory + n +
				c + "maxRenderTime:             " + maxRenderTime + n +
				c + "priority:                  " + priority + n +
				c + "computeMethod:             " + computeMethod + n +
				c + "GPUDevice:                 " + GPUDevice + n +
				c + "detectGPUs:                " + detectGPUs + n +
				c + "printLog:                  " + printLog + n +
				c + "requestTime:               " + requestTime + n +
				c + "shutdownTime:              " + shutdownTime + n +
				c + "shutdownMode:              " + shutdownMode + n +
				c + "extras:                    " + extras + n +
				c + "autoSignIn:                " + autoSignIn + n +
				c + "useSysTray:                " + useSysTray + n +
				c + "headless:                  " + headless + n +
				c + "UIType:                    " + UIType + n +
				c + "hostname:                  " + hostname + n +
				c + "theme:                     " + theme;
	}
	
	public void setUsePriority(int priority) {
		if (priority > 19)
			priority = 19;
		if (priority < -19)
			priority = -19;
		
		this.priority = priority;
		
	}
	
	public int computeMethodToInt() {
		return this.computeMethod.ordinal();
	}
	
	public void setCacheDir(File cache_dir_) {
		removeWorkingDirectory();
		if (cache_dir_ == null) {
			this.userHasSpecifiedACacheDir = false;
			try {
				this.workingDirectory = File.createTempFile("farm_", "");
				this.workingDirectory.createNewFile(); // hoho...
				this.workingDirectory.delete(); // hoho
				this.workingDirectory.mkdir();
				this.workingDirectory.deleteOnExit();
				
				// since there is no working directory and the client will be working in the system temp directory,
				// we can also set up a 'permanent' directory for immutable files (like renderer binary)
				
				this.storageDirectory = new File(this.workingDirectory.getParent() + File.separator + "sheepit_binary_cache");
				this.archiveDirectory = new File(this.workingDirectory.getParent() + File.separator + "sheepit_render_archive");
				this.storageDirectory.mkdir();
				this.archiveDirectory.mkdir();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			this.userHasSpecifiedACacheDir = true;
			this.workingDirectory = new File(cache_dir_.getAbsolutePath() + File.separator + "sheepit");
			this.storageDirectory = new File(cache_dir_.getAbsolutePath() + File.separator + "sheepit_binary_cache");
			this.archiveDirectory = new File(cache_dir_.getAbsolutePath() + File.separator + "sheepit_render_archive");
			this.workingDirectory.mkdirs();
			this.storageDirectory.mkdirs();
			this.archiveDirectory.mkdirs();
		}
		
		if (this.sharedDownloadsDirectory != null) {
			this.sharedDownloadsDirectory.mkdirs();
			
			if (!this.sharedDownloadsDirectory.exists()) {
				System.err.println("Configuration::setCacheDir Unable to create common directory " + this.sharedDownloadsDirectory.getAbsolutePath());
			}
		}
	}
	
	public void setStorageDir(File dir) {
		if (dir != null) {
			if (dir.exists() == false) {
				dir.mkdir();
			}
			this.storageDirectory = dir;
		}
	}
	
	public File getStorageDir() {
		if (this.storageDirectory == null) {
			return this.workingDirectory;
		}
		else {
			return this.storageDirectory;
		}
	}

	public File getArchiveDir() {
		return this.archiveDirectory;
	}
	
	public File getCacheDirForSettings() {
		if (this.userHasSpecifiedACacheDir == false) {
			return null;
		}
		else {
			// when the user have a cache directory a "sheepit" and "sheepit_binary_cache" is be automaticaly added
			return this.workingDirectory.getParentFile();
		}
	}
	
	public String getDefaultHostname() {
		try {
			return InetAddress.getLocalHost().getHostName();
		}
		catch (UnknownHostException e) {
			return "";
		}
	}
	
	public void cleanWorkingDirectory() {
		this.cleanDirectory(this.workingDirectory);
		this.cleanDirectory(this.storageDirectory);
	}
	
	public boolean cleanDirectory(File dir) {
		if (dir == null) {
			return false;
		}
		
		File[] files = dir.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					Utils.delete(file);
				}
				else {
					try {
						String extension = file.getName().substring(file.getName().lastIndexOf('.')).toLowerCase();
						String name = file.getName().substring(0, file.getName().length() - 1 * extension.length());
						if (extension.equals(".zip")) {
							// check if the md5 of the file is ok
							String md5_local = Utils.md5(file.getAbsolutePath());
							
							if (md5_local.equals(name) == false) {
								file.delete();
							}
							
							// TODO: remove old one
						}
						else {
							file.delete();
						}
					}
					catch (StringIndexOutOfBoundsException e) { // because the file does not have an . in his path
						file.delete();
					}
				}
			}
		}
		return true;
	}
	
	public void removeWorkingDirectory() {
		if (this.userHasSpecifiedACacheDir) {
			this.cleanWorkingDirectory();
		}
		else {
			Utils.delete(this.workingDirectory);
		}
	}
	
	public List<File> getLocalCacheFiles() {
		List<File> files_local = new LinkedList<File>();
		List<File> files = new LinkedList<File>();
		if (this.workingDirectory != null) {
			File[] filesInDirectory = this.workingDirectory.listFiles();
			if (filesInDirectory != null) {
				files.addAll(Arrays.asList(filesInDirectory));
			}
		}
		if (this.storageDirectory != null) {
			File[] filesInDirectory = this.storageDirectory.listFiles();
			if (filesInDirectory != null) {
				files.addAll(Arrays.asList(filesInDirectory));
			}
		}
		if (this.sharedDownloadsDirectory != null) {
			File[] filesInDirectory = this.sharedDownloadsDirectory.listFiles();
			if (filesInDirectory != null) {
				files.addAll(Arrays.asList(filesInDirectory));
			}
		}
		
		for (File file : files) {
			if (file.isFile()) {
				try {
					String extension = file.getName().substring(file.getName().lastIndexOf('.')).toLowerCase();
					String name = file.getName().substring(0, file.getName().length() - 1 * extension.length());
					if (extension.equals(".zip")) {
						// check if the md5 of the file is ok
						String md5_local = Utils.md5(file.getAbsolutePath());
						
						if (md5_local.equals(name)) {
							files_local.add(file);
						}
					}
				}
				catch (StringIndexOutOfBoundsException e) { // because the file does not have an . his path
				}
			}
		}
		return files_local;
	}
	
	private static String getJarVersion() {
		String versionPath = "/VERSION";
		String version = "6.0.0";
		
		InputStream versionStream = Client.class.getResourceAsStream(versionPath);
		if (versionStream != null) {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(versionStream));
				version = in.readLine();
			}
			catch (IOException ex) {
				System.err.println("Configuration::getJarVersion error while reading manifest file (" + versionPath + "): " + ex.getMessage());
			}
		}
		else {
			System.err.println("Configuration::getJarVersion Failed to get version file");
		}
		return version;
	}
	
	public boolean checkOSisSupported() {
		return OS.getOS() != null && OS.getOS().isSupported();
	}
	
	public boolean checkCPUisSupported() {
		OS os = OS.getOS();
		if (os != null) {
			CPU cpu = os.getCPU();
			return cpu != null && cpu.haveData();
		}
		return false;
	}
}
