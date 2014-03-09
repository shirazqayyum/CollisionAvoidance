package com.hackathon.collisionavoidancewarning;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements PeerListListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setupAdapter();
		setupWifi();
		
		discoverPeers = (Button) findViewById(R.id.discPeers);
		discoverPeers.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
            	 mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            		    @Override
            		    public void onSuccess() {
            		    	Log.d(TAG, "discover initiated");
            		    }

            		    @Override
            		    public void onFailure(int reasonCode) {
            		    	Log.d(TAG, "discovery failed");

            		    }
            		});                 
                 mAdapter.notifyDataSetChanged();
             }
         });	 
	}

	private void setupAdapter() {
		mAdapter = new ArrayAdapter<WifiP2pDevice>(this,android.R.layout.simple_list_item_1, mPeers);
		ListView listView = (ListView) findViewById(R.id.listViewPeers);
		listView.setAdapter(mAdapter);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
    
	/** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }
	
    /* PeerListListener interface method that needs to be implemented */
    @Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
    	 mPeers.clear();
         mPeers.addAll(peers.getDeviceList());	
         for (WifiP2pDevice x : mPeers) {
        	 Log.d(TAG, x.toString());
         }
         Toast.makeText(this, "onPeersAvailable called", Toast.LENGTH_LONG).show();
         mAdapter.notifyDataSetChanged();
	}
    
    
	private void setupWifi() {
		//  Indicates a change in the Wi-Fi P2P status.
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

	    // Indicates a change in the list of available peers.
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

	    // Indicates the state of Wi-Fi P2P connectivity has changed.
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

	    // Indicates this device's details have changed.
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
	    
	    mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
	    mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WifiBroadcastReceiver(mManager, mChannel, this);
	}
	
	
	/* instance variables */
	private final IntentFilter mIntentFilter = new IntentFilter();
	private Channel mChannel;
	private WifiP2pManager mManager;
	private WifiBroadcastReceiver mReceiver;
	public boolean wifiEnabled = false;
    private List<WifiP2pDevice> mPeers = new ArrayList<WifiP2pDevice>();
    private String TAG = "MainActivity";
    private Button discoverPeers;
    private ArrayAdapter<WifiP2pDevice> mAdapter;

	
	/* class variables */
}
