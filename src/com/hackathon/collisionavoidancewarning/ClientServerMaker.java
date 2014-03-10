package com.hackathon.collisionavoidancewarning;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;

public class ClientServerMaker implements ConnectionInfoListener{

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		if (info.groupFormed) {
			if (info.isGroupOwner) {
				/* I am the group owner so create a server socket */
			} else {
				/* I am not a group owner hence try to connect to the server (maybe multiple times) */
			}
		}
	}

}
