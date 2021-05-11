/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.feeders;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScanningSubject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MapscanFeeder extends AbstractFeeder {

	private List<InetAddress> addresses;

	int current;

	public MapscanFeeder(String ... ips) {
		initAddresses(ips);
	}

	public String getId() {
		return "feeder.mapscan";
	}
	
	private int initAddresses(String ... ips) {
		if (ips.length == 0)
			throw new IllegalArgumentException("no IP addresses specified");
		
		try {
			addresses = new ArrayList<>(ips.length);
			for (String s : ips) {
				addresses.add(InetAddress.getByName(s));
			}
		}
		catch (UnknownHostException e) {
			throw new FeederException("malformedIP");
		}
		return ips.length;
	}
		
	public boolean hasNext() {
		return current < addresses.size(); 
	}

	public ScanningSubject next() {
		return new ScanningSubject(addresses.get(current++));
	}

	public int percentageComplete() {
		return current * 100 / addresses.size();
	}
	
	public String getInfo() {
		return addresses.stream().map(v -> v.getHostAddress()).collect(Collectors.joining(","));
	}
}
