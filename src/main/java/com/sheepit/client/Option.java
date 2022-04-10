package com.sheepit.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
public class Option<T> {
	private T value;
	private boolean isLaunchCommand;
	private String launchFlag;
	
	public Option(T value, @NotNull String launchFlag) {
		this.value = value;
		this.launchFlag = launchFlag;
		this.isLaunchCommand = false;
	}
}
