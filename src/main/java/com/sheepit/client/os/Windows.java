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

import com.sheepit.client.os.windows.Kernel32Lib;
import com.sheepit.client.os.windows.WinProcess;
import com.sun.jna.Native;

public class Windows extends OS {
	
	private static final int MINIMUM_SUPPORTED_BUILD = 9600; //windows 8.1 and Server 2012 R2
	
	@Override public String name() {
		return "windows";
	}
	
	@Override public String getRenderBinaryPath() {
		return "rend.exe";
	}
	
	@Override public String getCUDALib() {
		return "nvcuda";
	}
	
	@Override public Process exec(List<String> command, Map<String, String> env) throws IOException {
		// disable a popup because the renderer might crash (seg fault)
		Kernel32Lib kernel32lib = null;
		try {
			kernel32lib = (Kernel32Lib) Native.load(Kernel32Lib.path, Kernel32Lib.class);
			kernel32lib.SetErrorMode(Kernel32Lib.SEM_NOGPFAULTERRORBOX);
		}
		catch (java.lang.UnsatisfiedLinkError e) {
			System.out.println("OS.Windows::exec failed to load kernel32lib " + e);
		}
		catch (java.lang.ExceptionInInitializerError e) {
			System.out.println("OS.Windows::exec failed to load kernel32lib " + e);
		}
		catch (Exception e) {
			System.out.println("OS.Windows::exec failed to load kernel32lib " + e);
		}
		
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);
		if (env != null) {
			builder.environment().putAll(env);
		}
		Process p = builder.start();
		WinProcess wproc = new WinProcess(p);
		if (env != null) {
			String priority = env.get("PRIORITY");
			wproc.setPriority(getPriorityClass(Integer.parseInt(priority)));
		}
		else {
			wproc.setPriority(WinProcess.PRIORITY_BELOW_NORMAL);
		}
		return p;
	}
	
	@Override public boolean isSupported() {
		long buildNumber = Long.MIN_VALUE;
		String arch = System.getProperty("os.arch").toLowerCase();
		try {
			buildNumber = Long.parseLong(operatingSystem.getVersionInfo().getBuildNumber());
		}
		catch(NumberFormatException e) {
			System.err.println("Windows::isSupported Failed to extract Windows build number: " + e);
		}
		catch (NullPointerException e) {
			System.err.println("Windows::isSupported Failed to extract Windows build number: " + e);
			String ver = operatingSystem.getVersionInfo().getVersion();
			List<String> supportedVersions = List.of("8.1", "10", "11", "Server 2016", "Server 2012 R2", "Server 2019", "Server 2022");
			return super.isSupported() && supportedVersions.contains(ver);
		}
		return
			super.isSupported() &&
			buildNumber >= MINIMUM_SUPPORTED_BUILD &&
			(
				"amd64".equals(arch) ||
				"x64".equals(arch) ||
				"x86_64".equals(arch)
			);
	}
	
	int getPriorityClass(int priority) {
		int process_class = WinProcess.PRIORITY_IDLE;
		switch (priority) {
			case 19:
			case 18:
			case 17:
			case 16:
			case 15:
				process_class = WinProcess.PRIORITY_IDLE;
				break;
			
			case 14:
			case 13:
			case 12:
			case 11:
			case 10:
			case 9:
			case 8:
			case 7:
			case 6:
			case 5:
				process_class = WinProcess.PRIORITY_BELOW_NORMAL;
				break;
			case 4:
			case 3:
			case 2:
			case 1:
			case 0:
			case -1:
			case -2:
			case -3:
				process_class = WinProcess.PRIORITY_NORMAL;
				break;
			case -4:
			case -5:
			case -6:
			case -7:
			case -8:
			case -9:
				process_class = WinProcess.PRIORITY_ABOVE_NORMAL;
				break;
			case -10:
			case -11:
			case -12:
			case -13:
			case -14:
				process_class = WinProcess.PRIORITY_HIGH;
				break;
			case -15:
			case -16:
			case -17:
			case -18:
			case -19:
				process_class = WinProcess.PRIORITY_REALTIME;
				break;
		}
		return process_class;
	}
	
	@Override public boolean kill(Process process) {
		if (process != null) {
			WinProcess wproc = new WinProcess(process);
			wproc.kill();
			return true;
		}
		return false;
	}
	
	@Override public boolean getSupportHighPriority() {
		return true;
	}
	
	@Override public boolean checkNiceAvailability() {
		// In windows, nice is not required and therefore we return always true to show the slider in the Settings GUI
		return true;
	}
	
	@Override public void shutdownComputer(int delayInMinutes) {
		try {
			// Shutdown the computer, waiting delayInMinutes minutes, force app closure and on the shutdown screen indicate that was initiated by SheepIt app
			ProcessBuilder builder = new ProcessBuilder("shutdown", "/s", "/f", "/t", String.valueOf(delayInMinutes * 60), "/c", "\"SheepIt App has initiated this computer shutdown.\"");
			Process process = builder.inheritIO().start();
		}
		catch (IOException e) {
			System.err.println(
					String.format("Windows::shutdownComputer Unable to execute the command 'shutdown /s /f /t 60...' command. Exception %s", e.getMessage()));
		}
	}

	@Override public String getDefaultConfigFilePath() {
		return System.getProperty("user.home") + File.separator + ".sheepit.conf";
	}
}
