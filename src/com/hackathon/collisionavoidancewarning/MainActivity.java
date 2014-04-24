package com.hackathon.collisionavoidancewarning;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements PeerListListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Log.d(TAG, "onCreate called");
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
		
		collisionAlgo = new CollisionAlgorithm();
		
		textView1 = (TextView)findViewById(R.id.textView1);
		
		mNumPick = (NumberPicker)findViewById(R.id.numberPicker1);
		mNumPick.setMaxValue(100);
		mNumPick.setMinValue(5);
		mNumPick.setValue(35);
		mNumPick.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
	
		mRangeSet = (Button)findViewById(R.id.rangeSet);
		Log.d(TAG, "here5");
		mRangeSet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	collisionAlgo.setRange(mNumPick.getValue());
            }
        });
				
		mGpsReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            String s = intent.getStringExtra(WriteGpsService.DIST_MSG);
	    
	            Log.d(TAG, "float_distance: " + s);
	            if ( collisionAlgo.isClose(s) && !hasWarned ) {
	            	hasWarned = true;
	            	Intent i = new Intent(MainActivity.this,CarWarning.class);
		            startActivity(i); 
	            }
	            
	            textView1.setText(s);
	            
	            /* fixed 5 meter hysteresis on reseting the warning */
	            
	            if ( collisionAlgo.isAway(s) && hasWarned ) {
	            	hasWarned = false;
	            }
	        }
	    };
	}
	

	/**  Setup an array adapter to be used as a pipe b/w the mPeers array and the listView
	 *  
	 */
	private void setupAdapter() {
		
		mAdapter = new ArrayAdapter<WifiP2pDevice>(this,android.R.layout.simple_list_item_1, mPeers);
		Log.d(TAG, "setupAdapter1");
		ListView listView = (ListView)findViewById(R.id.listViewPeers);
		Log.d(TAG, "setupAdapter2");
		listView.setAdapter(mAdapter);
		Log.d(TAG, "setupAdapter3");
		/* 
		 * A user can select the device to connect to from the list view 
		 */
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final WifiP2pDevice device = mPeers.get(arg2);	
				WifiP2pConfig config = new WifiP2pConfig();
				config.deviceAddress = device.deviceAddress;
				mManager.connect(mChannel, config, null);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/* Inflate the menu; this adds items to the action bar if it is present.*/
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
    
	/** Register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver((mGpsReceiver), new IntentFilter(WriteGpsService.DIST_RESULT));
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
	

    @Override
    public void onDestroy() {
    	mWifi.setWifiEnabled(false);
    	unregisterReceiver(mReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mGpsReceiver);
    	super.onDestroy();
    }
    
    
    /** PeerListListener interface method that needs to be implemented which finally gives an updated
     * list of the peers available after discovery. It also gets called when the peers are no longer available
     * (though still in proximity) usually happens when the user does not take any action
     */
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
    
    
	/** A helper method to set up the Wi-Fi Direct for the application
	 * Sets up the intent filters and grabs WifiP2pManager alongwith
	 * the channel
	 */
	private void setupWifi() {
		
		mWifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		mWifi.setWifiEnabled(true);
		/*  Indicates a change in the Wi-Fi P2P status. */
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

	    /* Indicates a change in the list of available peers. */
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

	    /* Indicates the state of Wi-Fi P2P connectivity has changed.*/
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

	    /* Indicates this device's details have changed. */
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
	    
	    mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
	    mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WifiBroadcastReceiver(mManager, mChannel, this);
        
        /* Initialize the connectionInfoListener - used in WifiBroadcastReceiver when 
         * connection is made
         */
        mClientServerMaker = new ClientServerMaker(this);
	}
	
	/** Returns the local instance of the ClientServerMaker object 
	 * that the MainActivity class holds
	 * @return ClientServerMaker object that is used after an intial Wi-Fi direct
	 * 			connection is setup
	 */
	public ClientServerMaker getClientServerMaker() {
		return this.mClientServerMaker;
	}

	
	/* instance variables */
	
	private final IntentFilter mIntentFilter = new IntentFilter();
	private Channel mChannel;
	private WifiP2pManager mManager;
	private WifiBroadcastReceiver mReceiver;
	private BroadcastReceiver mGpsReceiver;
	public boolean wifiEnabled = false;
    private List<WifiP2pDevice> mPeers = new ArrayList<WifiP2pDevice>();
    private String TAG = "MainActivity";
    private Button discoverPeers;
    private ArrayAdapter<WifiP2pDevice> mAdapter;
    private WifiManager mWifi;
    private ClientServerMaker mClientServerMaker;
    public boolean mSocketConnected = false;  /* May create problems with multiple devices */
    private TextView textView1;
    private NumberPicker mNumPick;
    private Button mRangeSet;
    private CollisionAlgorithm collisionAlgo;
    private	String reset_distance;
	
	/* class variables */
	
	private static boolean hasWarned = false;
}
