package com.umfundi.wear.claywear;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.os.Vibrator;

import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.DismissOverlayView;
import android.support.wearable.view.WatchViewStub;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.umfundi.wear.claywear.model.StandScore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.abs;
import static java.lang.String.valueOf;

// import android.view.GestureDetector.OnGestureListener;
// import android.gesture.GestureOverlayView.OnGestureListener;
// todo standscore is a file copied to this project - nees to be commonised!!!!

public class ClayWearSportingActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GestureOverlayView.OnGestureListener {

    private GoogleApiClient client;
    private String nodeId;

    private TextView mTvStand;
    private TextView mTvCycle;
    private TextView mTvPair;
    private TextView mTvScore;

    private static final String DEBUG_TAG = "WatchSportingActivity";

    private static final long CONNECTION_TIME_OUT_MS = 100;

    private int cyclesize;

    private int currentstand;
    private int currentcycle;
    private int currenttarget;

    private int targetA;
    private int targetB;
    private int targetC;

    private ImageButton buttonA;
    private ImageButton buttonB;
    private ImageButton buttonC;

    private DismissOverlayView mDismissOverlay;
    private GestureDetector mDetector;

    private MessageReceiver mBCReceiver;
    private GoogleApiClient mGoogleApiClient;

    private ArrayList<Integer> s;

    private Map<Integer, ArrayList> scoresheet;
    private Map <Integer, ArrayList> scoreData;

    private List<StandScore> scorePattern;
    private Map<Integer, Map> layoutData;

    private SharedPreferences preferences;

    private LinearLayout theLayout;

    private WatchViewStub stub;

    private int targetSize;

    private Resources res;

    @Override
    protected void onCreate(Bundle bSIS) {
        super.onCreate(bSIS);

        res = getResources();
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // We May need to check if the Mobile is up and request the scoresheet from it.
        // If the mobile is actively running the app, we need to ask if we want to sync with it.

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mBCReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBCReceiver,
                new IntentFilter("ClayWear_DataUpdate"));

        final GestureOverlayView.OnGestureListener ref = this;


        // rebuild critical data from saved preferences if they are available
        preferences = getPreferences(MODE_PRIVATE);



      //  final WatchViewStub
        stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {


            //  When the layout is inflated we initialise our view stuff
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mDismissOverlay = (DismissOverlayView) stub.findViewById(R.id.dismiss_overlay);
      //          mDismissOverlay.setIntroText(R.string.long_press_intro);
      //          mDismissOverlay.showIntroIfNecessary();

                if (preferences.contains("CURRENT_STAND") &&
                        preferences.contains("CURRENT_PAIR") &&
                        preferences.contains("SCORESHEET")) {
                    // todo add all the other bits

                    Log.v(DEBUG_TAG, "Restoring from saved preferences");
                    currentstand = preferences.getInt("CURRENT_STAND", 1);
                    currentcycle = preferences.getInt("CURRENT_PAIR", 1);
                    currenttarget = preferences.getInt("CURRENT_TARGET",1);
                    String JSON = preferences.getString("SCOREPATTERN", "DEFAULT");

                    ObjectMapper mapper = new ObjectMapper();
                    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

                    try {
                        scorePattern = mapper.readValue(JSON,
                                new TypeReference<HashMap<Integer, ArrayList>>() {
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.v(DEBUG_TAG, "Starting from null state");
                    theLayout = (LinearLayout) findViewById(R.id.buttonHolder);


    /*                //create a new Text View
                    TextView tv = new TextView(stub.getContext());
                    tv.setText("Awaiting Scoring sync");
                    theLayout.addView(tv);    */
                }


 /*               GestureOverlayView gov  = (GestureOverlayView) stub.findViewById(R.id.GOLView);
                // Dont draw gestures on the screen
                gov.setGestureVisible(true);
                gov.setEventsInterceptionEnabled(false);
                gov.addOnGestureListener(ref);
*/
                stub.setMotionEventSplittingEnabled(true);
                
                mDetector = new GestureDetector(stub.getContext(), new GestureListener() {

                    // On a long press we display the Dismiss overlay and go forward from there.
                    public void onLongPress(MotionEvent ev) {
                        mDismissOverlay.show();
                    }

                    // Most stuff is done with flings.
                    public boolean onFling(MotionEvent event1, MotionEvent event2,
                                           float velocityX, float velocityY) {

                        // lets look at the fling direction
                        // left fling
                        if (velocityX < -1000.0 && abs(velocityY) < 500.0) {
                            if (currentcycle < cyclesize) {
                                currentcycle++;
                                getTargetValues();
                                setTargetText();
                                mTvPair.invalidate();
                                updateMobile(false);
                            }
                            Log.d(DEBUG_TAG, " Fling left");
                        }
                        // right fling
                        else if (velocityX > 1000.0 && abs(velocityY) < 500.0) {
                            if (currentcycle > 1) {
                                currentcycle--;
                                getTargetValues();
                                setTargetText();
                                mTvPair.invalidate();
                                updateMobile(false);
                            }
                        Log.d(DEBUG_TAG, " Fling Right");
                        }
                        // up fling
                        else if (velocityY < -1000.0 && abs(velocityX) < 500.0) {
                            if (currentstand < layoutData.size()) {
                                currentstand++;
                                currentcycle = 1;
                                getTargetValues();
                                setTargetText();
                                mTvPair.invalidate();
                                updateMobile(false);
                            } else {
                                // trying to scroll past the known number of stands.
                                // do we want to add a stand or quit?
                            }
                            Log.d(DEBUG_TAG, " Fling Up");
                        }
                        // down fling
                        else if (velocityY > 1000.0 && abs(velocityX) < 500.0) {
                            if (currentstand > 1) {
                                currentstand--;
                                currentcycle = 1;
                                getTargetValues();
                                setTargetText();
                                mTvPair.invalidate();
                                updateMobile(false);
                            } else {
                                // We are trying o scroll past the top of the card.
                                // Could look at summary?
                            }
                            Log.d(DEBUG_TAG, " Fling down");
                        }

                        // Log.d(DEBUG_TAG, " onFling: ");
                        // Returning true says we have consumed the fling
                        return true;
                    }

                    public boolean onDown(MotionEvent event) {

                        // lets reset the start position.
                        //                       startX = event.getX();
                        //                       startY = event.getY();

                        //                       Log.d(DEBUG_TAG," onDown: " + event.toString());
                        return true;
                    }

                    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                            float distanceY) {

                        float deltaX = e1.getX() - e2.getX();
                        float deltaY = e1.getY() - e2.getY();

//                        Log.d(DEBUG_TAG, " onScroll: dx " + valueOf(deltaX)+ " dy "+ valueOf(deltaY)+ " E1 " + e1.toString()+ " E2 "+ e2.toString());

                        return true;
                    }

                    public boolean onSingleTapUp(MotionEvent event) {
                        //                       Log.d(DEBUG_TAG, " onSingleTapUp: " + event.toString());
                        return true;
                    }

                    public void onShowPress(MotionEvent event) {
                        //                       Log.d(DEBUG_TAG, " onShowPress: " + event.toString());
                    }

                });

//                mDetector = new GestureDetector(stub.getContext(),)
//                gov.addOnGestureListener( new MyGestureListener());

                mTvStand = (TextView) stub.findViewById(R.id.standnumber);
                mTvPair = (TextView) stub.findViewById(R.id.targetnumber);
                mTvScore = (TextView) stub.findViewById(R.id.scores);
                mTvCycle = (TextView) stub.findViewById(R.id.cyclelabel);

                if ( scorePattern != null ) {

                    setupWidgets(3);
                    getTargetValues();
                    setTargetText();
                }
                else {
                      theLayout = (LinearLayout) findViewById(R.id.buttonHolder);
     //               TextView tv = new TextView(stub.getContext());
     //               tv.setText("Awaiting Scoring sync");
     //               theLayout.addView(tv);
                }
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.i(DEBUG_TAG, "Touch Event");
        return mDetector.onTouchEvent(ev) || super.onTouchEvent(ev);
    }

    /**
     * Initializes the GoogleApiClient and gets the Node ID of the connected device.
     */
    private void initApi() {
        client = getGoogleApiClient(this);
        retrieveDeviceNode();
    }

    /**
     * Sets up the buttons for handling click events 1 for each target in this cycle.
     */
    private void setupWidgets(int targets) {

        theLayout = (LinearLayout) findViewById(R.id.buttonHolder);
        theLayout.removeAllViewsInLayout();
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(90,90);

        //create a new ImageButton
        buttonA = new ImageButton(this);
        buttonA.setLayoutParams(p);
        buttonA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickA();
            }
        });
        theLayout.addView(buttonA);

        if (targets>1) {
            //create a new ImageButton
            buttonB = new ImageButton(this);
            buttonB.setLayoutParams(p);
            buttonB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickB();
                }
            });
            theLayout.addView(buttonB);
        }

        if (targets == 3){
            //create a new ImageButton
            buttonC = new ImageButton(this);
            buttonC.setLayoutParams(p);
            buttonC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickC();
                }
            });
            theLayout.addView(buttonC);
        }
    }

    /**
     * Returns a GoogleApiClient that can access the Wear API.
     *
     * @param context
     * @return A GoogleApiClient that can make calls to the Wear API
     */
    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    /**
     * Connects to the GoogleApiClient and retrieves the connected device's Node ID. If there are
     * multiple connected devices, the first Node ID is returned.
     */
    private void retrieveDeviceNode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();
                }
                client.disconnect();
            }
        }).start();
    }

    /**
     * ButtonA clicked.
     * <p/>
     * Buttons toggle the state of the target, hit ( default ) and miss.
     */
    private void clickA() {
        // locate the right stand data

        Map<Integer, Map> theStand = layoutData.get(currentstand);
        Map<Integer, ArrayList> theCycle = theStand.get("1");
        ArrayList<Integer> tinfo = theCycle.get("0");
        // get the target size
        targetSize = tinfo.get(1); // 1, 2 or 3
        // offset points to the first target in the cycle
        int offset = (currentcycle-1)*targetSize;

        StandScore standScore = scorePattern.get(currentstand-1);

        // todo - sporting mode vs other types ....
        if (targetA == 1) {
            targetA = 0;
            standScore.setPip(offset,0);
            buttonA.setImageResource(R.drawable.dashimage);
        }
        else {
            targetA = 1;
            standScore.setPip(offset, 1);
            buttonA.setImageResource(R.drawable.checkmarkcircle);
        }
        buttonA.invalidate();
        updateMobile(false);

    }

    /**
     * Button B clicked
     */
    private void clickB() {
        // iHm Context here?
//        ImageButton btn = (ImageButton) findViewById(R.id.btn_B);
        Map<Integer, Map> theStand = layoutData.get(currentstand);
        Map<Integer, ArrayList> theCycle = theStand.get("1");
        ArrayList<Integer> tinfo = theCycle.get("0");
        // get the target size
        targetSize = tinfo.get(1); // 1, 2 or 3
        // offset points to the first target in the cycle plus 1 as this is B Bird
        int offset = ((currentcycle-1)*targetSize)+1;

        StandScore standScore = scorePattern.get(currentstand-1);
        if (targetB == 1) {
            targetB = 0;
            standScore.setPip(offset,0);
            buttonB.setImageResource(R.drawable.dashimage);
        }
        else{
            targetB = 1;
            standScore.setPip(offset,1);
            buttonB.setImageResource(R.drawable.checkmarkcircle);
        }
        buttonB.invalidate();
        updateMobile(false);
    }

    /**
     * Button C clicked
     */
    private void clickC() {
        // iHm Context here?
        //       ImageButton btn = (ImageButton) findViewById(R.id.btn_C);
        Map<Integer, Map> theStand = layoutData.get(currentstand);
        Map<Integer, ArrayList> theCycle = theStand.get("1");
        ArrayList<Integer> tinfo = theCycle.get("0");
        // get the target size
        targetSize = tinfo.get(1); // 1, 2 or 3
        // offset points to the first target in the cycle plus 2 as this is C Bird
        int offset = ((currentcycle-1)*targetSize)+2;

        StandScore standScore = scorePattern.get(currentstand-1);        if (targetC == 1) {
            targetC = 0;
            standScore.setPip(offset,0);
            buttonC.setImageResource(R.drawable.dashimage);
        }
        else {
            targetC = 1;
            standScore.setPip(offset,1);
            buttonC.setImageResource(R.drawable.checkmarkcircle);
        }
        buttonC.invalidate();
        updateMobile(false);
    }

    /*
    * Set the target and stand texts
    */
    void setTargetText() {

        mTvStand.setText(valueOf(currentstand) + "/" + valueOf(layoutData.size()) + " ");
        // the target size tels us what to set the target name to.
        // 1 = Sgl
        // 2 = Pr
        // 3 = Trp
        mTvCycle.setText(res.getString(R.string.singles_abr));
        mTvCycle.setText(res.getString(R.string.pairs_abr));
        mTvCycle.setText(res.getString(R.string.trips_abr));

        if (targetSize == 3)
            mTvCycle.setText(res.getString(R.string.trips_abr)+":");
        else if (targetSize == 2)
            mTvCycle.setText(res.getString(R.string.pairs_abr)+":");
        else
             mTvCycle.setText(res.getString(R.string.singles_abr)+":");

        mTvPair.setText(valueOf(currentcycle) + "/" + valueOf(cyclesize));
//        mTvScore.setText(valueOf(totalscore) + "x" + valueOf(totaltargets));
        // todo - fix above
        mTvPair.invalidate();
        mTvScore.invalidate();
        mTvStand.invalidate();
    }

    /*
    * Get the last stand value and the totals.
    */
    void getTargetValues() {
        // todo why is currentstand = 0 ?????
        if (currentstand==0)
            currentstand = 1;

        Map<Integer, Map> theStand = layoutData.get(currentstand);
        Map<Integer, ArrayList> theCycle = theStand.get("1");
        ArrayList<Integer> tinfo = theCycle.get("0");
        // get the size of this cycle ( ie 5 pairs )
        cyclesize = tinfo.get(2);
        // get the target size
        targetSize = tinfo.get(1); // 1, 2 or 3

        StandScore thescores = scorePattern.get(currentstand-1);

        int offset = (currentcycle - 1) * targetSize;

        setupWidgets(targetSize);

        // todo iHm ???? check if null works
        Integer trgVal = thescores.getPip(offset);
        if (trgVal != null) {
            if (trgVal == 1) {
                targetA = 1;
                buttonA.setImageResource(R.drawable.checkmarkcircle);
            } else {
                targetA = 0;
                buttonA.setImageResource(R.drawable.dashimage);
            }
            buttonA.invalidate();
        }

        if (targetSize > 1){
            trgVal = thescores.getPip(offset+1);
            if (trgVal != null) {
                if (trgVal == 1) {
                    targetB = 1;
                    buttonB.setImageResource(R.drawable.checkmarkcircle);
                } else {
                    targetB = 0;
                    buttonB.setImageResource(R.drawable.dashimage);
                }
                buttonB.invalidate();
            }

        }
        if (targetSize == 3) {
            trgVal = thescores.getPip(offset + 2);
            if (trgVal != null) {
                if (trgVal == 1) {
                    targetC = 1;
                    buttonC.setImageResource(R.drawable.checkmarkcircle);
                } else {
                    targetC = 0;
                    buttonC.setImageResource(R.drawable.dashimage);
                }
                buttonC.invalidate();
            }
        }
    }

/*
    @Override
    public boolean onDown(MotionEvent event) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent event) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent event, MotionEvent event2, float v, float v2) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        mDismissOverlay.show();
    }

    @Override
    public boolean onFling(MotionEvent event, MotionEvent event2, float v, float v2) {
        Log.d(DEBUG_TAG, " Fling");
        return false;
    }
*/

    @Override
    public void onGestureStarted(GestureOverlayView gestureOverlayView, MotionEvent event) {

    }

    @Override
    public void onGesture(GestureOverlayView gestureOverlayView, MotionEvent event) {

    }

    @Override
    public void onGestureEnded(GestureOverlayView gestureOverlayView, MotionEvent event) {
        Log.i(DEBUG_TAG, String.valueOf(event));
    }

    @Override
    public void onGestureCancelled(GestureOverlayView gestureOverlayView, MotionEvent event) {

    }

    // Handles data passed to the activity from the listening thread.
    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            DataMap dataMap = DataMap.fromBundle(intent.getBundleExtra("datamap"));
            // Display message in log
            Log.i(DEBUG_TAG, "Message rx: " + dataMap);

            // Retrieve the JSON representation of the scoresheet passed from the listener thread

            String JSONlayout = dataMap.getString("LayoutData");
            String JSONscore = dataMap.getString("Scores");

            // and turn it back into a scoresheet by reversing the JSON construction.
            ObjectMapper mapper = new ObjectMapper();

            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] vibrationPattern = {0};

            try {
                if (dataMap.getString("Type").equals("Initialize") || layoutData == null) {
                    // Update the layout and scorepattern
                    layoutData = mapper.readValue(JSONlayout, new TypeReference<LinkedHashMap<Integer, Map>>() {
                    });
                    vibrationPattern = new long[]{0, 100, 50, 100, 50, 100, 200, 100, 50, 100, 50, 300};
                } else {
                    // vibrationPattern = new long[]{0,50};
                }

                scorePattern = mapper.readValue(JSONscore, new TypeReference<List<StandScore>>() {
                });

                Log.i(DEBUG_TAG, "scorePattern built from JSON: " + mapper.writeValueAsString(scorePattern));

                for (int p = 1; p <= scorePattern.size(); p++) {
                    StandScore sp = scorePattern.get(p-1);
                    Log.i(DEBUG_TAG, "Stand " + p + " " + sp.getS1() + " " + sp.getS2() + " " + sp.getS3() + " "
                                               + sp.getS4() + " " + sp.getS5() + " " + sp.getS6() + " " + sp.getS7() + " "
                                                + sp.getS8() + " " + sp.getS9() + " " + sp.getS10());
                }

                // when we change scoresheet we had better go to 1*1 in case we are off the top of the array.
                currentstand = dataMap.getInt("Stand");
                currenttarget = dataMap.getInt("Target");
                currentcycle = dataMap.getInt("Cycle");

                setupWidgets(targetSize);
                getTargetValues();
                setTargetText();

            } catch (Exception e) {
                e.printStackTrace();
            }

            //-1 - don't repeat
            final int indexInPatternToRepeat = -1;
            vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);

  //          getTargetValues();

        }
    }

    @Override
    protected void onDestroy() {
        if (mBCReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mBCReceiver);
        }
        Log.v(DEBUG_TAG, "onDestroy");
        // Should we store state here?
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onStart();
        mGoogleApiClient.connect();
        Log.e(DEBUG_TAG, "Resumed!");
    }

    @Override
    public void onConnected(Bundle bundle) {
        // We dont listen on this thread!
        // Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        //  Wearable.DataApi.addListener(mGoogleApiClient, this);
        Log.e(DEBUG_TAG, "Connection Suspended!");
    }

    @Override
    public void onConnectionFailed(ConnectionResult r) {
        Log.e(DEBUG_TAG, "Connection Failed!");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // We dont listen on this thread!
        // Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
        Log.e(DEBUG_TAG, "Paused!");

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);


/*
        // Store values between instances here
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        try {
            // value to store
            editor.putString("SCORESHEET", mapper.writeValueAsString(scoresheet));
            editor.putInt("CURRENT_STAND", currentstand);
            editor.putInt("CURRENT_PAIR", currentcycle);
            // Commit to storage
            editor.apply();
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }   */

    }

    /*
    * Send the scoresheet to the mobile device.
    */
    private void updateMobile(boolean request) {

        Log.i(DEBUG_TAG, "updateMobile "+request);

        DataMap dataMap = new DataMap();

        if (!request) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            Date date = new Date();
            dataMap.putString("TS", date.toString());

            try {
                // display to Log
                Log.i(DEBUG_TAG, "Send to Mobile From UI thread: " /* + mapper.writeValueAsString(scorePattern) */ );
                dataMap.putString("Scores", mapper.writeValueAsString(scorePattern));
                dataMap.putInt("Stand", currentstand);
                dataMap.putInt("Cycle", currentcycle);
                new SendToDataLayerThread("/WearUpdate", dataMap).start();
            } catch (JsonGenerationException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        } else {
            new SendToDataLayerThread("/WearRequest", dataMap).start();
        }
    }

    class SendToDataLayerThread extends Thread {
        String path;
        DataMap dataMap;

        // Constructor to send a message to the data layer
        SendToDataLayerThread(String p, DataMap data) {
            path = p;
            dataMap = data;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
            for (Node node : nodes.getNodes()) {
                // Construct a DataRequest and send over the data layer
                PutDataMapRequest putDMR = PutDataMapRequest.create(path);
                putDMR.getDataMap().putAll(dataMap);
                PutDataRequest request = putDMR.asPutDataRequest();
                DataApi.DataItemResult result = Wearable.DataApi.putDataItem(mGoogleApiClient, request).await();
                if (result.getStatus().isSuccess()) {
                    Log.i(DEBUG_TAG, "DataMap succesfully sent to: " + node.getDisplayName());
                } else {
                    // Log an error
                    Log.i(DEBUG_TAG, "ERROR: failed to send DataMap");
                }
            }
        }
    }

}
