package com.hackathon.collisionavoidancewarning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.IBinder;
import android.util.Log;



public class ClientServerMaker implements ConnectionInfoListener{
	
	public ClientServerMaker(Context c) {
		this.mContext = c;
		  // Bind to LocalService
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
							ServerSocket server_socket = new ServerSocket(5002);
							Socket client_socket = server_socket.accept();
							
							PrintWriter write_to_client = new PrintWriter(client_socket.getOutputStream(), true);
							BufferedReader read_from_client = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
							
							Log.d(TAG, "connection with client made - i am the server");
							
							//mContext.startService(new Intent(mContext, WriteGpsService.class));							
							Log.d(TAG, "Location writer is created");

							String msg;
							while ( (msg = read_from_client.readLine()) != null ) {
								Log.d("MyLoc", "from client: " + msg);
								ctr = Integer.parseInt(msg);
								++ctr;
								write_to_client.println(ctr);								
								Log.d("MyLoc", "just sent to client: " + ctr);	
							}
							
							
							Log.d("MyLoc", "Should not get here - server");
							server_socket.close();
							
							
						} catch (IOException e) {
						
							e.printStackTrace();
							Log.d("MyLoc", "Exception - Should not get here - server");
						} 
						
					}
				});
				worker_server.start();
				
			} else {
				
				/* I am not a group owner hence try to connect to the server (maybe multiple times) */
				Thread worker_client = new Thread(new Runnable() {
					public void run() {
						try {
							Socket socket_to_server = new Socket(mInfo.groupOwnerAddress.getHostAddress(), 5002);
							Thread.sleep(2000);
							
							PrintWriter write_to_server = new PrintWriter(socket_to_server.getOutputStream(), true);
							BufferedReader read_from_server = new BufferedReader(new InputStreamReader(socket_to_server.getInputStream()));

							//mContext.startService(new Intent(mContext, WriteGpsService.class));
							//mLoc_writer = new LocationWriter(write_to_server, mContext);
							Log.d(TAG, "connection with client made - i am the client");
							
							write_to_server.println(ctr);
							Log.d(TAG, "initial string sent from client:" + ctr);
							

							
							
							String msg;
							while ( (msg = read_from_server.readLine()) != null ) {
								Log.d("MyLoc", "from server: " + msg);
								ctr = Integer.parseInt(msg);
								++ctr;
								write_to_server.println(ctr);
								Log.d("MyLoc", "just sent to server: " + ctr);
								
							}
							
							Log.d("MyLoc", "Should not get here -  client");
							
							socket_to_server.close();
							
						} catch (Exception e) {
							e.printStackTrace();
							Log.d("MyLoc", "Exception - Should not get here -  client");
						}
					}
				});
				
				worker_client.start();
			}
		} else {
			
		}
	}
	
	private boolean mBound;

	private WifiP2pInfo mInfo;
	private Context mContext;
	private String TAG = "ClientServerMaker";
	private String to_send = "ping pong string\n";
	private int ctr = 0;
}
