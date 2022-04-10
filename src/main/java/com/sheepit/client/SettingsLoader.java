/*
 * Copyright (C) 2015 Laurent CLOUET
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.sheepit.client.Configuration.ComputeType;
import com.sheepit.client.hardware.gpu.GPU;
import com.sheepit.client.hardware.gpu.GPUDevice;
import com.sheepit.client.standalone.GuiText;
import com.sheepit.client.standalone.GuiTextOneLine;
import com.sheepit.client.os.OS;
import com.sheepit.client.os.Linux;
import com.sheepit.client.os.Mac;
import lombok.Data;

@Data
public class SettingsLoader {
	
	private enum PropertyNames {
		
		PRIORITY("priority"),
		CACHE_DIR("cache-dir"),
		COMPUTE_METHOD("compute-method"),
		GPU("compute-gpu"),
		CORES("cores"),
		CORES_BACKWARDS_COMPAT("cpu-cores"),
		RAM("ram"),
		RENDER_TIME("rendertime"),
		LOGIN("login"),
		PASSWORD("password"),
		PROXY("proxy"),
		HOSTNAME("hostname"),
		AUTO_SIGNIN("auto-signin"),
		USE_SYSTRAY("use-systray"),
		HEADLESS("headless"),
		UI("ui"),
		THEME("theme");

		String propertyName;

		PropertyNames(String prop) {
			this.propertyName = prop;
		}
		
		@Override
		public String toString() {
			return propertyName;
		}
		
	}
	
	public static final String ARG_SERVER = "-server";
	public static final String ARG_LOGIN = "-login";
	public static final String ARG_PASSWORD = "-password";
	public static final String ARG_CACHE_DIR = "-cache-dir";
	public static final String ARG_SHARED_ZIP = "-shared-zip";
	public static final String ARG_GPU = "-gpu";
	public static final String ARG_NO_GPU = "--no-gpu";
	public static final String ARG_COMPUTE_METHOD = "-compute-method";
	public static final String ARG_CORES = "-cores";
	public static final String ARG_MEMORY = "-memory";
	public static final String ARG_RENDERTIME = "-rendertime";
	public static final String ARG_VERBOSE = "--verbose";
	public static final String ARG_REQUEST_TIME = "-request-time";
	public static final String ARG_SHUTDOWN = "-shutdown";
	public static final String ARG_SHUTDOWN_MODE = "-shutdown-mode";
	public static final String ARG_PROXY = "-proxy";
	public static final String ARG_EXTRAS = "-extras";
	public static final String ARG_UI = "-ui";
	public static final String ARG_CONFIG = "-config";
	public static final String ARG_VERSION = "--version";
	public static final String ARG_SHOW_GPU = "--show-gpu";
	public static final String ARG_NO_SYSTRAY = "--no-systray";
	public static final String ARG_PRIORITY = "-priority";
	public static final String ARG_TITLE = "-title";
	public static final String ARG_THEME = "-theme";
	public static final String ARG_HOSTNAME = "-hostname";
	public static final String ARG_HEADLESS = "--headless";
	
	
	private String path;
	
	private Option<String> login;
	
	private Option<String> password;
	
	private Option<String> proxy;
	private Option<String> hostname;
	private Option<String> computeMethod;
	private Option<String> gpu;
	private Option<String> cores;
	private Option<String> ram;
	private Option<String> renderTime;
	private Option<String> cacheDir;
	private Option<String> autoSignIn;
	private Option<String> useSysTray;
	private Option<String> headless;
	private Option<String> ui;
	private Option<String> theme;
	private Option<Integer> priority;
	
	public SettingsLoader(String path_) {
		if (path_ == null) {
			path = OS.getOS().getDefaultConfigFilePath();
		}
		else {
			path = path_;
		}
	}
	
	public void setSettings(String path_, String login_, String password_, String proxy_, String hostname_,
		ComputeType computeMethod_, GPUDevice gpu_, Integer cores_, Long maxRam_,
		Integer maxRenderTime_, String cacheDir_, Boolean autoSignIn_, Boolean useSysTray_, Boolean isHeadless,
		String ui_,	String theme_, Integer priority_) {
		if (path_ == null) {
			path = OS.getOS().getDefaultConfigFilePath();
		}
		else {
			path = path_;
		}
		login = setValue(login_, login, ARG_LOGIN);
		password = setValue(password_, password, ARG_PASSWORD);
		proxy = setValue(proxy_, proxy, ARG_PROXY);
		hostname = setValue(hostname_, hostname, ARG_HOSTNAME);
		cacheDir = setValue(cacheDir_, cacheDir, ARG_CACHE_DIR);
		autoSignIn = setValue(autoSignIn_.toString(), autoSignIn, "");
		useSysTray = setValue(useSysTray_.toString(), useSysTray, ARG_NO_SYSTRAY);
		headless = setValue(isHeadless.toString(), headless, ARG_HEADLESS);
		ui = setValue(ui_, ui, ARG_UI);
		priority = setValue(priority_, priority, ARG_PRIORITY);
		theme = setValue(theme_, theme, ARG_THEME);
		
		if (cores_ > 0) {
			cores = setValue(cores_.toString(), cores, ARG_CORES);
		}
		if (maxRam_ > 0) {
			ram = setValue(maxRam_+ "k", ram, ARG_MEMORY);
		}
		if (maxRenderTime_ > 0) {
			renderTime = setValue(maxRenderTime_.toString(), renderTime, ARG_RENDERTIME);
		}
		if (computeMethod_ != null) {
			try {
				computeMethod = setValue(computeMethod_.name(), computeMethod, ARG_COMPUTE_METHOD);
			}
			catch (IllegalArgumentException e) {
			}
		}
		
		if (gpu_ != null) {
			gpu = setValue(gpu_.getId(), gpu, ARG_GPU);
		}
	}
	
	/**
	 * sets an option to a given value. If the option being passed on is null it will be created with the given value and returned.
	 * @param value The value to be set
	 * @param option The {@link Option} object that the value is going to be stored in. Can be null
	 * @param launchFlag A flag indicating whether the option was set via a launch argument or not
	 * @param <T> The type of the value stored within the option
	 * @return The {@link Option} object that has been passed on as a parameter with the value being set, or a newly created object if option was null
	 */
	private <T> Option<T> setValue(T value, Option<T> option, String launchFlag) {
		if (option == null && value != null) {
			option = new Option<>(value, launchFlag);
		}
		else if (value != null){
			option.setValue(value);
		}
		return option;
	}
	
	public String getFilePath() {
		return path;
	}
	
	/**
	 * Takes the list of launch parameters and marks every config setting corresponding to one of the set values as launch command, ensuring that they wont overwrite
	 * the one in the config file
	 * @param argsList a list of the launch arguments
	 */
	public void markLaunchSettings(List<String> argsList) {
		Option options[] = { login, password, proxy, hostname, computeMethod, gpu, cores, ram, renderTime, cacheDir, autoSignIn,
			useSysTray, headless, ui, theme, priority };
		
		for (Option option : options) {
			if (option != null && argsList.contains(option.getLaunchFlag())) {
				option.setLaunchCommand(true);
			}
		}
	}
	
	/**
	 * Selects the right setting to store to the config file between the value currently set in the config file and the option the client is working with
	 * currently, depending on whether the setting was set via launch argument or not
	 * @param saveTo the properties object representing the config file that is going to be written
	 * @param configFileProperties the properties object containing the current config file values
	 * @param property an enum representing the name of the setting
	 * @param option the option containing the currently used value
	 */
	private void setProperty(Properties saveTo, Properties configFileProperties, PropertyNames property, Option<String> option) {
		if (option != null) {
			if (option.isLaunchCommand()) {
				String configValue = configFileProperties.getProperty(property.propertyName);
				if (configValue != null) {
					saveTo.setProperty(property.propertyName, configValue);
				}
			}
			else {
				saveTo.setProperty(property.propertyName, option.getValue());
			}
		}
	}
	
	@SuppressWarnings("PointlessBooleanExpression") public void saveFile() {
		
		Properties configFileProp = new Properties();
		if (new File(path).exists()) {
			InputStream input = null;
			try {
				input = new FileInputStream(path);
				configFileProp.load(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Properties prop = new Properties();
		OutputStream output = null;
		try {
			output = new FileOutputStream(path);
			
			setProperty(prop, configFileProp, PropertyNames.PRIORITY,
				new Option<>(priority != null ? priority.getValue().toString() : null, priority.isLaunchCommand(), ARG_PRIORITY));
			setProperty(prop, configFileProp, PropertyNames.CACHE_DIR, cacheDir);
			setProperty(prop, configFileProp, PropertyNames.COMPUTE_METHOD, computeMethod);
			setProperty(prop, configFileProp, PropertyNames.GPU, gpu);
			setProperty(prop, configFileProp, PropertyNames.CORES, cores);
			setProperty(prop, configFileProp, PropertyNames.RAM, ram);
			setProperty(prop, configFileProp, PropertyNames.RENDER_TIME, renderTime);
			setProperty(prop, configFileProp, PropertyNames.LOGIN, login);
			setProperty(prop, configFileProp, PropertyNames.PASSWORD, password);
			setProperty(prop, configFileProp, PropertyNames.PROXY, proxy);
			setProperty(prop, configFileProp, PropertyNames.HOSTNAME, hostname);
			setProperty(prop, configFileProp, PropertyNames.AUTO_SIGNIN, autoSignIn);
			setProperty(prop, configFileProp, PropertyNames.USE_SYSTRAY, useSysTray);
			setProperty(prop, configFileProp, PropertyNames.HEADLESS, headless);
			setProperty(prop, configFileProp, PropertyNames.UI, ui);
			setProperty(prop, configFileProp, PropertyNames.THEME, theme);
			prop.store(output, null);
		}
		catch (IOException io) {
			io.printStackTrace();
		}
		finally {
			if (output != null) {
				try {
					output.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		// Set Owner read/write
		Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
		perms.add(PosixFilePermission.OWNER_READ);
		perms.add(PosixFilePermission.OWNER_WRITE);
		
		try {
			Files.setPosixFilePermissions(Paths.get(path), perms);
		}
		catch (UnsupportedOperationException e) {
			// most likely because it's MS Windows
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initializes or sets an option object to the corresponding value from the config file
	 * @param config the properties loaded from the config file
	 * @param property the name of the property
	 * @param option the option to store the property value
	 * @param launchFlag the launch argument corresponding to the respective option
	 */
	private Option<String> loadConfigOption(Properties config, PropertyNames property, Option<String> option, String launchFlag) {
		String configValue;
		if (config.containsKey(property.propertyName)) {
			configValue = config.getProperty(property.propertyName);
			if (option == null && configValue != null) {
				option = new Option<>(configValue, launchFlag);
			}
			else if (configValue != null){
				option.setValue(configValue);
			}
		}
		return option;
	}
	
	public void loadFile(boolean initialize) throws Exception {
		
		if (initialize)
			initWithDefaults();
		
		if (new File(path).exists() == false) {
			return;
		}
		
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(path);
			prop.load(input);
			
			cacheDir = loadConfigOption(prop, PropertyNames.CACHE_DIR, cacheDir, ARG_CACHE_DIR);
			
			computeMethod = loadConfigOption(prop, PropertyNames.COMPUTE_METHOD, computeMethod, ARG_COMPUTE_METHOD);
			
			gpu = loadConfigOption(prop, PropertyNames.GPU, gpu, ARG_GPU);
			
			cores = loadConfigOption(prop, PropertyNames.CORES_BACKWARDS_COMPAT, cores, ARG_CORES);
			
			cores = loadConfigOption(prop, PropertyNames.CORES, cores, ARG_CORES);
			
			ram = loadConfigOption(prop, PropertyNames.RAM, ram, ARG_MEMORY);
			
			renderTime = loadConfigOption(prop, PropertyNames.RENDER_TIME, renderTime, ARG_RENDERTIME);
			
			login = loadConfigOption(prop, PropertyNames.LOGIN, login, ARG_LOGIN);
			
			password = loadConfigOption(prop, PropertyNames.PASSWORD, password, ARG_PASSWORD);
			
			proxy = loadConfigOption(prop, PropertyNames.PROXY, proxy, ARG_PROXY);
			
			hostname = loadConfigOption(prop, PropertyNames.HOSTNAME, hostname, ARG_HOSTNAME);
			
			autoSignIn = loadConfigOption(prop, PropertyNames.AUTO_SIGNIN, autoSignIn, "");
			
			useSysTray = loadConfigOption(prop, PropertyNames.USE_SYSTRAY, useSysTray, ARG_NO_SYSTRAY);
			
			headless = loadConfigOption(prop, PropertyNames.HEADLESS, headless, ARG_HEADLESS);
			
			ui = loadConfigOption(prop, PropertyNames.UI, ui, ARG_UI);
			
			theme = loadConfigOption(prop, PropertyNames.THEME, theme, ARG_THEME);
			
			if (prop.containsKey(PropertyNames.PRIORITY.propertyName)) {
				int prio = Integer.parseInt(prop.getProperty(PropertyNames.PRIORITY.propertyName));
				if (priority == null) {
					priority = new Option<>(prio, ARG_PRIORITY);
				}
				else {
					priority.setValue(prio);
				}
			}
		}
		catch (Exception e) {	//We need the try-catch here to ensure that the input file will be closed though we'll deal with the exception in the calling method
			throw e;
		}
		finally {
			if (input != null) {
				try {
					input.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Merge the Settings file with the Configuration.
	 * The Configuration will have high priority.
	 * @param config the config file
	 * @param initialize whether to initialize all fields with default values, should only be true on first call
	 */
	public void merge(Configuration config, boolean initialize) {
		if (config == null) {
			System.out.println("SettingsLoader::merge config is null");
		}
		
		try {
			loadFile(initialize);
			applyConfigFileValues(config);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.err.println("Exception while reading the config file. Falling back to defaults");
			initWithDefaults();
			applyConfigFileValues(config);
		}
		
	}
	
	private void applyConfigFileValues(Configuration config) {
		if (config.getLogin().isEmpty() && login != null) {
			config.setLogin(login.getValue());
		}
		if (config.getPassword().isEmpty() && password != null) {
			config.setPassword(password.getValue());
		}
		
		if ((config.getProxy() == null || config.getProxy().isEmpty()) && proxy != null) {
			config.setProxy(proxy.getValue());
		}
		
		if ((config.getHostname() == null || config.getHostname().isEmpty() || config.getHostname().equals(config.getDefaultHostname())) && hostname != null) {
			config.setHostname(hostname.getValue());
		}
		
		if (config.isHeadless() == false && headless != null) {
			config.setHeadless(Boolean.parseBoolean(headless.getValue()));
		}
		
		if (config.getPriority() == 19) { // 19 is default value
			config.setUsePriority(priority.getValue());
		}
		try {
			if (config.getComputeMethod() == null && computeMethod == null) {
				config.setComputeMethod(ComputeType.CPU);
			}
			else if ((config.getComputeMethod() == null && computeMethod != null) || (computeMethod != null && config.getComputeMethod() != ComputeType
				.valueOf(computeMethod.getValue()))) {
				if (config.getComputeMethod() == null) {
					config.setComputeMethod(ComputeType.valueOf(computeMethod.getValue()));
				}
				
			}
		}
		catch (IllegalArgumentException e) {
			System.err.println("SettingsLoader::merge failed to handle compute method (raw value: '" + computeMethod + "')");
			computeMethod = null;
		}
		if (config.getGPUDevice() == null && gpu != null) {
			GPUDevice device = GPU.getGPUDevice(gpu.getValue());
			if (device != null) {
				config.setGPUDevice(device);
			}
			else if (config.getUIType() != null && (config.getUIType().equals(GuiText.type) || config.getUIType().equals(GuiTextOneLine.type))) {
				System.err.println("SettingsLoader::merge could not find specified GPU");
				System.exit(2);
			}
		}
		
		if (config.getNbCores() == -1 && cores != null) {
			config.setNbCores(Integer.parseInt(cores.getValue()));
		}
		
		if (config.getMaxAllowedMemory() == -1 && ram != null) {
			config.setMaxAllowedMemory(Utils.parseNumber(ram.getValue()) / 1000); // internal ram value is in KiB
		}
		
		if (config.getMaxRenderTime() == -1 && renderTime != null) {
			config.setMaxRenderTime(Integer.parseInt(renderTime.getValue()));
		}
		
		if (config.isUserHasSpecifiedACacheDir() == false && cacheDir != null) {
			config.setCacheDir(new File(cacheDir.getValue()));
		}
		
		if (config.getUIType() == null && ui != null) {
			config.setUIType(ui.getValue());
		}
		
		if (config.getTheme() == null) {
			if (this.theme != null && (this.theme.getValue().equals("dark") || this.theme.getValue().equals("light"))) {
				config.setTheme(this.theme.getValue());
			}
			else {
				config.setTheme("light");
			}
		}
		
		// if the user has invoked the app with --no-systray, then we just overwrite the existing configuration with (boolean)false. If no parameter has been
		// specified and the settings file contains use-systray=false, then deactivate as well.
		if (!config.isUseSysTray() || (config.isUseSysTray() && useSysTray != null && useSysTray.getValue().equals("false"))) {
			config.setUseSysTray(false);
		}
		
		if (config.isAutoSignIn() == false && autoSignIn != null) {
			config.setAutoSignIn(Boolean.parseBoolean(autoSignIn.getValue()));
		}
	}
	
	private void initWithDefaults() {
		Configuration defaultConfigValues = new Configuration(null, null, null);
		this.login = null;
		this.password = null;
		this.proxy = null;
		this.hostname = null;
		this.computeMethod = null;
		this.gpu = null;
		this.cacheDir = null;
		this.autoSignIn = null;
		this.useSysTray = new Option<>(String.valueOf(defaultConfigValues.isUseSysTray()), ARG_NO_SYSTRAY);
		this.headless = new Option<>(String.valueOf(defaultConfigValues.isHeadless()), ARG_HEADLESS);
		this.ui = null;
		this.priority = new Option<>(defaultConfigValues.getPriority(), ARG_PRIORITY); // must be the same default as Configuration
		this.ram = null;
		this.renderTime = null;
		this.theme = null;
		this.cores = new Option<>(String.valueOf(defaultConfigValues.getNbCores()), ARG_CORES);
		
		
	}
	
	@Override public String toString() {
		return String.format(
			"SettingsLoader [path=%s, login=%s, password=%s, computeMethod=%s, gpu=%s, cacheDir=%s, theme=%s, priority=%d, autosign=%s, usetray=%s, headless=%s]",
			path, login, password, computeMethod, gpu, cacheDir, theme, priority, autoSignIn, useSysTray, headless);
	}
}
