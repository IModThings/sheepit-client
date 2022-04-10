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
package com.sheepit.client.os;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.software.os.OperatingSystem;
import oshi.hardware.HardwareAbstractionLayer;
import com.sheepit.client.hardware.cpu.CPU;

public abstract class OS {
	private static SystemInfo systemInfo = new SystemInfo();
	
	public static OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
	
	private static HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
	
	private static OS instance = null;
	
	public abstract String name();
	
	public boolean isSupported() {  return "64bit".equals(getCPU().arch()); }
	
	/** Get the full version of the os.
	 * For example windows, should give "windows 8.1"
	 */
	public String getVersion() {
		return (name() + " " + operatingSystem.getVersionInfo()).toLowerCase();
	}
	
	public long getTotalMemory() {
		return hardwareAbstractionLayer.getMemory().getTotal() / 1024;
	}
	
	public long getFreeMemory() {
		return hardwareAbstractionLayer.getMemory().getAvailable() / 1024;
	}
	
	public abstract String getRenderBinaryPath();
	
	public String getCUDALib() {
		return null;
	}
	
	public abstract boolean getSupportHighPriority();
	
	public abstract boolean checkNiceAvailability();
	
	/**
	 * Shutdown the computer waiting delayInMinutes minutes to allow all SheepIt threads to close and exit the app
	 */
	public abstract void shutdownComputer(int delayInMinutes);
	
	public CPU getCPU() {
		CentralProcessor.ProcessorIdentifier cpuID = hardwareAbstractionLayer.getProcessor().getProcessorIdentifier();
		CPU ret = new CPU();
		ret.setName(cpuID.getName());
		ret.setModel(cpuID.getModel());
		ret.setFamily(cpuID.getFamily());
		return ret;
	}
	
	public Process exec(List<String> command, Map<String, String> env) throws IOException {
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);
		if (env != null) {
			builder.environment().putAll(env);
		}
		return builder.start();
	}
	
	public boolean kill(Process proc) {
		if (proc != null) {
			proc.destroy();
			return true;
		}
		return false;
	}
	
	public static OS getOS() {
		if (instance == null) {
			switch (operatingSystem.getManufacturer()){
				case "Microsoft":
					instance = new Windows();
					break;
				case "Apple":
					if ("aarch64".equalsIgnoreCase(System.getProperty("os.arch"))) { // ARM arch ?
						instance = new MacM1();
					}
					else {
						instance = new Mac();
					}
					break;
				case "GNU/Linux":
					instance = new Linux();
					break;
			}
		}
		return instance;
	}

	public String getDefaultConfigFilePath() {
		String xdgConfigHome = System.getenv("XDG_CONFIG_HOME");
		String userHome = System.getProperty("user.home");
		// fallback xdg config home should be ~/.config/
		if (xdgConfigHome == null || xdgConfigHome.isEmpty()) {
			xdgConfigHome = userHome + File.separator + ".config";
		}
		// add the config folder to the path 
		xdgConfigHome += File.separator + "sheepit";
		
		// check if file already exists in ~/.config/sheepit/sheepit.conf
		File file = new File(xdgConfigHome + File.separator + "sheepit.conf");
		if (file.exists()) {
			return file.getAbsolutePath();
		} 
		// if it doesn't exist, try $HOME/.sheepit.conf
		file = new File(userHome + File.separator + ".sheepit.conf");
		if (file.exists()) {
			return file.getAbsolutePath();
		}
		// if it doesn't exist, create the file in the XDG compliant location
		file = new File(xdgConfigHome);
		file.mkdirs();
		return file.getAbsolutePath() + File.separator + "sheepit.conf";
	}
}
