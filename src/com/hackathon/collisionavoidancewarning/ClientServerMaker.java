package com.hackathon.collisionavoidancewarning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.util.Log;

public class ClientServerMaker implements ConnectionInfoListener{
	public ClientServerMaker(Context c) {
		this.mContext = c;
	}
	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		mInfo = info;

		if (info.groupFormed) {
			if (info.isGroupOwner) {
				/* I am the group owner so create a server socket */
				Thread worker_server = new Thread(new Runnable(){
					public void run() {
						try {
							ServerSocket server_socket = new ServerSocket(5000);
							Socket client_socket = server_socket.accept();
							
							PrintWriter write_to_client = new PrintWriter(client_socket.getOutputStream());
							BufferedReader read_from_client = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
							
							Log.d(TAG, "connection with client made - i am the server");
							//mLoc_writer = new LocationWriter(write_to_client, mContext);
							
							Log.d(TAG, "Location writer is created");

							String msg;
							while ( (msg = read_from_client.readLine()) != null ) {
								Log.d(TAG, "from client: " + msg);
							}
							
							server_socket.close();
							
						} catch (IOException e) {
						
							e.printStackTrace();
						} 
						
					}
				});
				worker_server.start();
				
			} else {
				
				/* I am not a group owner hence try to connect to the server (maybe multiple times) */
				Thread worker_client = new Thread(new Runnable() {
					public void run() {
						try {
							Socket socket_to_server = new Socket(mInfo.groupOwnerAddress.getHostAddress(), 5000);
							Thread.sleep(2000);
							
							PrintWriter write_to_server = new PrintWriter(socket_to_server.getOutputStream());
							BufferedReader read_from_server = new BufferedReader(new InputStreamReader(socket_to_server.getInputStream()));

							mContext.startService(this, WriteGpsService.class);
							//mLoc_writer = new LocationWriter(write_to_server, mContext);
							
							String msg;
							while ( (msg = read_from_server.readLine()) != null ) {
								Log.d(TAG, "from server: " + msg);
							}
							
							socket_to_server.close();
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				
				worker_client.start();
			}
		} else {
			
		}
	}
	
	private WifiP2pInfo mInfo;
	private Context mContext;
	private LocationWriter mLoc_writer;
	private String TAG = "ClientServerMaker";

}
