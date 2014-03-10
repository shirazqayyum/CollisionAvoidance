package com.hackathon.collisionavoidancewarning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.util.Log;

public class ClientServerMaker implements ConnectionInfoListener{

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
							write_to_client.println("A string from the server group owner");
							write_to_client.flush();
							Log.d("ClientServerMaker", "seeeeeeeeeeeeenntt");

							
							client_socket.close();
							
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
		}
	}
	
	WifiP2pInfo mInfo;

}
