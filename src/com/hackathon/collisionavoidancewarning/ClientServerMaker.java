package com.hackathon.collisionavoidancewarning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.content.Intent;
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
							
							Intent write_gps = new Intent(mContext, WriteGpsService.class);
							Intent read_gps = new Intent(mContext, ReadGpsService.class);
							
							startService(write_gps);
							startService(read_gps);
							
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
							InputStreamReader isr = new InputStreamReader(socket_to_server.getInputStream());
							BufferedReader buf = new BufferedReader(isr);
							
							Log.d("ClientServerMaker",buf.readLine());
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
	
	WifiP2pInfo mInfo;
	Context mContext;

}
