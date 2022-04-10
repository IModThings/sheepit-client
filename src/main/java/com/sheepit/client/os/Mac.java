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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import com.sheepit.client.Log;

public class Mac extends OS {
	private final String NICE_BINARY_PATH = "nice";
	private final String ID_COMMAND_INVOCATION = "id -u";
	
	public Mac() {
		super();
	}
	
	@Override public String name() {
		return "mac";
	}
	
	@Override public String getRenderBinaryPath() {
		return "Blender" + File.separator + "Blender.app" + File.separator + "Contents" + File.separator + "MacOS" + File.separator + "Blender";
	}
	
	@Override public Process exec(List<String> command, Map<String, String> env) throws IOException {
		List<String> actual_command = command;
		if (checkNiceAvailability()) {
			// launch the process in lowest priority
			if (env != null) {
				actual_command.add(0, env.get("PRIORITY"));
			}
			else {
				actual_command.add(0, "19");
			}
			actual_command.add(0, "-n");
			actual_command.add(0, NICE_BINARY_PATH);
		}
		else {
			Log.getInstance(null).error("No low priority binary, will not launch renderer in normal priority");
		}
		ProcessBuilder builder = new ProcessBuilder(actual_command);
		builder.redirectErrorStream(true);
		if (env != null) {
			builder.environment().putAll(env);
		}
		return builder.start();
	}
	
	@Override public boolean isSupported() {
		String[] ver = operatingSystem.getVersionInfo().getVersion().split("\\.");
		int majorVer = Integer.parseInt(ver[0]), minorVer = Integer.parseInt(ver[1]);
		return super.isSupported() && ((majorVer == 10 && minorVer >= 13) || majorVer >= 11);
	}
	
	@Override public String getCUDALib() {
		return "/usr/local/cuda/lib/libcuda.dylib";
	}
	
	@Override public boolean getSupportHighPriority() {
		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command("bash", "-c", ID_COMMAND_INVOCATION);
			builder.redirectErrorStream(true);
			
			Process process = builder.start();
			InputStream is = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			
			String userLevel = null;
			if ((userLevel = reader.readLine()) != null) {
				// Root user in *ix systems -independently of the alias used to login- has a id value of 0. On top of being a user with root capabilities,
				// to support changing the priority the nice tool must be accessible from the current user
				return (userLevel.equals("0")) & checkNiceAvailability();
			}
		}
		catch (IOException e) {
			System.err.println(String.format("ERROR Mac::getSupportHighPriority Unable to execute id command. IOException %s", e.getMessage()));
		}
		
		return false;
	}
	
	@Override public boolean checkNiceAvailability() {
		ProcessBuilder builder = new ProcessBuilder();
		builder.command(NICE_BINARY_PATH);
		builder.redirectErrorStream(true);
		
		Process process = null;
		boolean hasNiceBinary = false;
		try {
			process = builder.start();
			hasNiceBinary = true;
		}
		catch (IOException e) {
			Log.getInstance(null).error("Failed to find low priority binary, will not launch renderer in normal priority (" + e + ")");
		}
		finally {
			if (process != null) {
				process.destroy();
			}
		}
		return hasNiceBinary;
	}
	
	@Override public void shutdownComputer(int delayInMinutes) {
		try {
			// Shutdown the computer waiting delayInMinutes minutes to allow all SheepIt threads to close and exit the app
			ProcessBuilder builder = new ProcessBuilder("shutdown", "-h", String.valueOf(delayInMinutes));
			Process process = builder.inheritIO().start();
		}
		catch (IOException e) {
			System.err.println(String.format("Mac::shutdownComputer Unable to execute the 'shutdown -h 1' command. Exception %s", e.getMessage()));
		}
	}
}
