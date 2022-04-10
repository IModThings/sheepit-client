package com.sheepit.client;

import com.sheepit.client.datamodel.SpeedTestTarget;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Speedtest {
	public static final int PORT = 443;
	private static final Comparator<SpeedTestTarget> ORDERED = Comparator.comparing(speedTestTarget -> speedTestTarget.getPing().getAverage());
	
	private Log log;
	
	public Speedtest(Log log) {
		this.log = log;
	}
	
	/**
	 * @param urls the urls to the speedtest payloads
	 * @param numberOfResults number of best mirrors to return
	 *
	 * @return An array of the mirrors with the best connection time. The size of the array is determined by <code>numberOfResults</code> or <code>urls.size()</code>
	 * if <code>numberOfResults > urls.size()</code>
	 */
	public List<SpeedTestTarget> doSpeedtests(List<String> urls, int numberOfResults) {
		
		List<SpeedTestTarget> pingResult = (urls
			.stream()
			.map(this::measure)
			.filter(target -> target.getPing().getAverage() > 0)
			.sorted(ORDERED)
			.collect(Collectors.toList())
		);
		
		numberOfResults = Math.min(numberOfResults, urls.size());
		
		List<SpeedTestTarget> result = new ArrayList<>(numberOfResults);
		
		int i = 0;
		while (result.size() < numberOfResults && i < pingResult.size()) {
			SpeedTestTarget m = pingResult.get(i);
			try {
				var speedtestResult = runTimed(() -> speedtest(m.getUrl()));
				m.setSpeedtest(
					Math.round(speedtestResult.second / (speedtestResult.first / (double) 1000L))	// number of bytes / time in seconds
				);
			}
			catch (Exception e) {
				this.log.error("Speedtest::doSpeedtests Exception " + e);
				i++;
				continue;
			}
			result.add(m);
			i++;
		}
		
		result.sort(Comparator.comparing(SpeedTestTarget::getSpeedtest).reversed());
		return result;
	}
	
	private SpeedTestTarget measure(String mirror) {
		long pingCount = 12;
		
		LongStream.Builder streamBuilder = LongStream.builder();
		
		for (int i = 0; i < pingCount; i++) {
			try {
				streamBuilder.add(runTimed(() -> ping(mirror, PORT)).first);
			}
			catch (Exception e) {
				this.log.error("Speedtest::ping Exception " + e);
				pingCount /= 2;
				streamBuilder.add(Long.MAX_VALUE);
			}
		}
		
		var pingStatistics = streamBuilder.build().summaryStatistics();
		
		return new SpeedTestTarget(mirror, -1, pingStatistics);
	}
	
	/**
	 * Will return both the time it took to complete the task and the result of it
	 * @param task the task whose execution time we want to measure
	 * @param <T> the return value of the task
	 * @return A pair where the first value is the execution time in ms and the second value is the task result
	 * @throws Exception
	 */
	private <T> Pair<Long, T> runTimed(Callable<T> task) throws Exception {
		long start, end;
		start = System.nanoTime();
		T callValue = task.call();
		end = System.nanoTime();
		return new Pair<>(Duration.ofNanos(end - start).toMillis(), callValue);
	}
	
	/**
	 * Downloads a payload from the given url and returns the number of downloaded bytes
	 * @param url the url pointing at the speedtest file
	 * @return the number of bytes read
	 */
	private int speedtest(String url) {
		try (InputStream stream = new URL(url).openStream()) {
			return stream.readAllBytes().length;
		}
		catch (MalformedURLException e) {
			throw new RuntimeException("Invalid speedtest URL: " + url, e);
		}
		catch (IOException e) {
			throw new RuntimeException("Unable to execute speedtest to: " + url, e);
		}
	}
	
	private static int ping(String url, int port) {
		InetAddress mirrorIP = null;
		try (Socket socket = new Socket()) {
			mirrorIP = InetAddress.getByName(new URL(url).getHost());
			SocketAddress socketAddress = new InetSocketAddress(mirrorIP, port);
			int maxWaitingTime_ms = 3000;
			socket.connect(socketAddress, maxWaitingTime_ms);
		}
		catch (IOException e) {
			String problemURL = mirrorIP != null ? mirrorIP + " (derived from: " + url + ")" : url;
			throw new RuntimeException("Unable to connect to " + problemURL, e);
		}
		return -1;
	}
}
