/*
 * Copyright (C) 2013-2014 Laurent CLOUET
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

public class Error {
	public enum Type {
		// id have to be kept synchronised with the server side.
		OK(0),
		UNKNOWN(99),
		WRONG_CONFIGURATION(1),
		AUTHENTICATION_FAILED(2),
		TOO_OLD_CLIENT(3),
		SESSION_DISABLED(4),
		RENDERER_NOT_AVAILABLE(5),
		MISSING_RENDERER(6),
		MISSING_SCENE(7),
		NOOUTPUTFILE(8),
		IMAGE_TOO_LARGE(26),
		DOWNLOAD_FILE(9),
		CAN_NOT_CREATE_DIRECTORY(10),
		NETWORK_ISSUE(11),
		RENDERER_CRASHED(12),
		RENDERER_CRASHED_PYTHON_ERROR(24),
		RENDERER_OUT_OF_VIDEO_MEMORY(13),
		RENDERER_OUT_OF_MEMORY(21),
		RENDERER_KILLED(14),
		RENDERER_KILLED_BY_USER(20),
		RENDERER_KILLED_BY_USER_OVER_TIME(23),
		RENDERER_KILLED_BY_SERVER(22),
		RENDERER_MISSING_LIBRARIES(15),
		FAILED_TO_EXECUTE(16),
		OS_NOT_SUPPORTED(17),
		CPU_NOT_SUPPORTED(18),
		GPU_NOT_SUPPORTED(19),
		ENGINE_NOT_AVAILABLE(27),
		VALIDATION_FAILED(25),
		IMAGE_WRONG_DIMENSION(26),
		CURRENTLY_HEADLESS(28),
		DENOISING_NOT_SUPPORTED(29),
		
		// internal error handling
		NO_SPACE_LEFT_ON_DEVICE(100),
		ERROR_BAD_UPLOAD_RESPONSE(101),
		PATH_INVALID(102),
		NO_WRITE_PERMISSION(103),
		SERVER_DOWN(104),
		ERROR_BAD_SERVER_RESPONSE(105),
		;
		
		private final int id;
		
		private Type(int id) {
			this.id = id;
		}
		
		public int getValue() {
			return id;
		}
	}
	
	public enum ServerCode {
		OK(0),
		UNKNOWN(999),
		
		CONFIGURATION_ERROR_NO_CLIENT_VERSION_GIVEN(100),
		CONFIGURATION_ERROR_CLIENT_TOO_OLD(101),
		CONFIGURATION_ERROR_AUTH_FAILED(102),
		CONFIGURATION_ERROR_WEB_SESSION_EXPIRED(103),
		CONFIGURATION_ERROR_MISSING_PARAMETER(104),
		
		JOB_REQUEST_NOJOB(200),
		JOB_REQUEST_ERROR_NO_RENDERING_RIGHT(201),
		JOB_REQUEST_ERROR_DEAD_SESSION(202),
		JOB_REQUEST_ERROR_SESSION_DISABLED(203),
		JOB_REQUEST_ERROR_SESSION_DISABLED_DENOISING_NOT_SUPPORTED(208),
		JOB_REQUEST_ERROR_INTERNAL_ERROR(204),
		JOB_REQUEST_ERROR_RENDERER_NOT_AVAILABLE(205),
		JOB_REQUEST_SERVER_IN_MAINTENANCE(206),
		JOB_REQUEST_SERVER_OVERLOADED(207),
		
		JOB_VALIDATION_ERROR_MISSING_PARAMETER(300),
		JOB_VALIDATION_ERROR_BROKEN_MACHINE(301), // in GPU the generated frame is black
		JOB_VALIDATION_ERROR_FRAME_IS_NOT_IMAGE(302),
		JOB_VALIDATION_ERROR_UPLOAD_FAILED(303),
		JOB_VALIDATION_ERROR_SESSION_DISABLED(304), // missing heartbeat or broken machine
		JOB_VALIDATION_IMAGE_TOO_LARGE(306),
		JOB_VALIDATION_ERROR_IMAGE_WRONG_DIMENSION(308),
		
		KEEPMEALIVE_STOP_RENDERING(400),
		
		// internal error handling
		ERROR_NO_ROOT(2),
		ERROR_BAD_RESPONSE(3),
		ERROR_REQUEST_FAILED(5),
		SERVER_CONNECTION_FAILED(6),
		;
		
		private final int id;
		
		private ServerCode(int id) {
			this.id = id;
		}
		
		public int getValue() {
			return id;
		}
		
		public static ServerCode fromInt(int val) {
			ServerCode[] As = ServerCode.values();
			for (ServerCode A : As) {
				if (A.getValue() == val) {
					return A;
				}
			}
			return ServerCode.UNKNOWN;
		}
	}
	
	public static Type ServerCodeToType(ServerCode sc) {
		switch (sc) {
			case OK:
				return Type.OK;
			case UNKNOWN:
				return Type.UNKNOWN;
			case CONFIGURATION_ERROR_CLIENT_TOO_OLD:
				return Type.TOO_OLD_CLIENT;
			case CONFIGURATION_ERROR_AUTH_FAILED:
				return Type.AUTHENTICATION_FAILED;
			
			case CONFIGURATION_ERROR_NO_CLIENT_VERSION_GIVEN:
			case CONFIGURATION_ERROR_WEB_SESSION_EXPIRED:
				return Type.WRONG_CONFIGURATION;
			
			case JOB_REQUEST_ERROR_SESSION_DISABLED:
			case JOB_VALIDATION_ERROR_SESSION_DISABLED:
				return Type.SESSION_DISABLED;
			
			case JOB_REQUEST_ERROR_RENDERER_NOT_AVAILABLE:
				return Type.RENDERER_NOT_AVAILABLE;
			
			default:
				return Type.UNKNOWN;
		}
	}
	
	public static String humanString(Type in) {
		switch (in) {
			case ERROR_BAD_UPLOAD_RESPONSE:
				return "Corrupt response from the server when trying to upload data. The server might be overloaded or be encountering other issues. Will try again in a few minutes.";
			case ERROR_BAD_SERVER_RESPONSE:
				return "Unexpected response from server received. The server might be overloaded or encountering other issues.";
			case NETWORK_ISSUE:
				return "Could not connect to the server, please check your connection to the internet.";
			case SERVER_DOWN:
				return "Server is down.";
			case TOO_OLD_CLIENT:
				return "This client is too old, you need to update it.";
			case AUTHENTICATION_FAILED:
				return "Failed to authenticate, please check your login and password.";
			case DOWNLOAD_FILE:
				return "Error while downloading the project files. Will try another project in a few minutes.";
			case NOOUTPUTFILE:
				return "The renderer shut down without saving an image. This could be a broken project, or you could be missing libraries that Blender needs. Will try another project in a few minutes.";
			case IMAGE_TOO_LARGE:
				return "The generated image is too big to be handled by the server. Will try another project in a few minutes.";
			case RENDERER_CRASHED:
				return "The renderer has crashed. This is usually because the project consumes too much memory, or is just broken. Will try another project in a few minutes.";
			case RENDERER_CRASHED_PYTHON_ERROR:
				return "The renderer has crashed due to a Python error. Will try another project in a few minutes.";
			case RENDERER_OUT_OF_VIDEO_MEMORY:
				return "Project tried to use too much video memory (VRAM). Will try another project in a few minutes.";
			case RENDERER_OUT_OF_MEMORY:
				return "Project tried to use too much memory. Will try another project in a few minutes.";
			case GPU_NOT_SUPPORTED:
				return "Project's Blender version requires a newer GPU, or your CUDA setup is broken. Will try another project in a few minutes.";
			case RENDERER_MISSING_LIBRARIES:
				return "Failed to launch renderer. Please check if you have all the necessary libraries installed and if you have enough free space in your working directory.";
			case RENDERER_KILLED:
				return "Render canceled because either you stopped it from the website or the server did automatically (usually for a render taking too long).";
			case RENDERER_KILLED_BY_USER:
				return "Render canceled because you've blocked the project.";
			case RENDERER_KILLED_BY_SERVER:
				return "Render canceled because the project has been stopped by the server. Usually because the project will take too much time or it's been paused.";
			case SESSION_DISABLED:
				return "The server has disabled your session. Your client may have generated a broken frame (GPU not compatible, not enough RAM/VRAM, etc).";
			case RENDERER_NOT_AVAILABLE:
				return "The official Blender builds don't support rendering on this hardware.";
			case MISSING_RENDERER:
				return "Unable to locate the Blender binaries within the working directory.";
			case OS_NOT_SUPPORTED:
				return "This Operating System is not supported.";
			case CPU_NOT_SUPPORTED:
				return "This CPU is not supported.";
			case DENOISING_NOT_SUPPORTED:
				return "Denoising is not supported on this device. Session can not continue. Please use CPU or Virtual Machine with SSE4.1 instruction set.";
			case ENGINE_NOT_AVAILABLE:
				return "The project requires a rendering engine that isn't supported on this machine. Will try another project in a few minutes.";
			case NO_SPACE_LEFT_ON_DEVICE:
				return "Your storage medium does not have enough free space available.";
			case PATH_INVALID:
				return "The working directory path is not valid";
			case NO_WRITE_PERMISSION:
				return "Can't create/modify files due to missing write permissions in working directory.";
			case IMAGE_WRONG_DIMENSION:
				return "The rendered image had the wrong resolution. This project is configured incorrectly. Switching to another project.";
			case CURRENTLY_HEADLESS:
				return "Blender can not open a display to create an OpenGL context needed for EEVEE/GPencil. Will try another project in a few minutes.";
			default:
				return in.toString();
		}
	}
}
