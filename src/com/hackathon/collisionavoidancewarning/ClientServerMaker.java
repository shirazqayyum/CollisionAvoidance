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

import com.hackathon.collisionavoidancewarning.WriteGpsService.LocalBinder;

public class ClientServerMaker implements ConnectionInfoListener{
	
	public ClientServerMaker(Context c) {
		this.mContext = c;
		  /* Bind to LocalService */
        Intent intent = new Intent(mContext, WriteGpsService.class);
        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
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
							
							PrintWriter write_to_client = new PrintWriter(client_socket.getOutputStream(), true);
							BufferedReader read_from_client = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
							
							Log.d(TAG, "connection with client made - i am the server");
							mService.setWriter(write_to_client);
							//mContext.startService(new Intent(mContext, WriteGpsService.class));							
							Log.d(TAG, "Location writer is created");

							String msg;
							while ( (msg = read_from_client.readLine()) != "#\n" ) {
								Log.d("MyLoc", "from client: " + msg);
								mService.setExternalLocation(msg);
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
							
							PrintWriter write_to_server = new PrintWriter(socket_to_server.getOutputStream(), true);
							BufferedReader read_from_server = new BufferedReader(new InputStreamReader(socket_to_server.getInputStream()));

							//mContext.startService(new Intent(mContext, WriteGpsService.class));
							//mLoc_writer = new LocationWriter(write_to_server, mContext);
							Log.d(TAG, "connection with client made - i am the client");
							mService.setWriter(write_to_server);
			
							String msg;
							while ( (msg = read_from_server.readLine()) != "#\n" ) {
								Log.d("MyLoc", "from server: " + msg);
								mService.setExternalLocation(msg);
							}							
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
	
	   /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
	 
	private boolean mBound;
	private WriteGpsService  mService;
	private WifiP2pInfo mInfo;
	private Context mContext;
	private String TAG = "ClientServerMaker";

}
