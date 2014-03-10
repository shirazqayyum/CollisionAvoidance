package com.hackathon.collisionavoidancewarning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;
import android.widget.Toast;

public class WifiBroadcastReceiver extends BroadcastReceiver {

	public WifiBroadcastReceiver(WifiP2pManager manager, Channel channel, MainActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }

	 @Override
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
	            
	        	/* Determine if Wifi P2P mode is enabled or not, alert the Activity. */
	        	
	            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
	            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
	                mActivity.wifiEnabled = true;
	            } else {
	                mActivity.wifiEnabled = false;
	            }
	        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

	        	 /* request available peers from the wifi p2p manager. This is an
	        	  * asynchronous call and the calling activity is notified with a
	        	  * callback on PeerListListener.onPeersAvailable()
	        	  */
	            if (mManager != null) {
	                mManager.requestPeers(mChannel, mActivity);
	                Log.d(TAG, "peers requested");
	            }

	        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
	        	
	        	/* Connection information changed */
	        	
	        	if (mManager == null) return;
	        	NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
	        	if (networkInfo.isConnected()) {
	        		/* Setup sockets, use the connectionInfoListener i.e. ClientServerMaker */
	        		
			    	Toast.makeText(mActivity.getApplicationContext(), "connected" ,Toast.LENGTH_SHORT).show();
			    	mManager.requestConnectionInfo(mChannel, mActivity.getClientServerMaker());
	        	} else {
	        		/* close any stream of data */
	        	}

	        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

	        }
	    }
	 
	 
	 /* instance variables */
	 
	 	private WifiP2pManager mManager;
	    private Channel mChannel;
	    private MainActivity mActivity;
	    private String TAG = "Broadcast";
}
