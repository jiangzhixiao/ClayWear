package com.umfundi.wear.claywear;

// todo check operation with alternate formats!!!!
//
// todo - All Round - comps - several layouts of different types.
//
//
//

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi.SendMessageResult;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.umfundi.wear.claywear.model.Layout;
import com.umfundi.wear.claywear.model.ScoreCard;
import com.umfundi.wear.claywear.model.StandScore;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.valueOf;

public class MobileMainActivity extends /*ActionBar*/ Activity implements
        DataApi.DataListener ,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MobileMainActivity";

    protected ClayWearApplication app;

//    private GoogleApiClient mGoogleApiClient;

    public static final int MAX_STAND_TARGETS = 10;

    private NumberPicker npStand;
    private NumberPicker npCycle;
    private NumberPicker npType;
    private NumberPicker npTargets;

    private TextView tvTStands;
    private TextView tvTTargets;
    private TextView tvTargets;
    private TextView tvDiscipline;
    private TextView tvFormat;


    private ImageButton btnTrnRem;

    private MessageReceiver mBCReceiver;

    private Layout currentLayout;

    private GoogleApiClient mGAClient;

    private int turn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (ClayWearApplication) this.getApplication();

        mGAClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // create a fresh round in the application space.
        app.df.createRound();

        //
        // todo either set to an existing score card or preference depending on settings
        // if this is a completely raw init then we default to ESP.
        //

        app.currentShooter = 1;

        // create a layout object and add it to the round

        String android_id = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String key = android_id + "-" + UUID.randomUUID().toString();

        Layout newLayout = new Layout(key, app.df.SPORTING, app.df.ESP, app);
        app.df.theRound.getLayouts().put(1, newLayout);
        app.df.theRound.setActiveLayoutIdx(1);

        // lets play with the current layout!
        currentLayout = (Layout) app.df.theRound.getLayouts().get(app.df.theRound.getActiveLayoutIdx());

        // We are looking at a layout from here on

        // if we are doing fixed layouts we must never get to this activity!
        // which is a problem if we go change scorecards!!!!
        //
        if (currentLayout.getFormat().equals(app.df.ESP)) {
            setContentView(R.layout.edit_layout_noturn);
        } else if ((currentLayout.getDiscipline().equals(app.df.TRAP)) || (currentLayout.getDiscipline().equals(app.df.SKEET))) {
            setContentView(R.layout.activity_setup_fixed);
        } else
            setContentView(R.layout.edit_layout_turned);

/*
        app.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
*/
        mBCReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBCReceiver,
                new IntentFilter("Claywear_Local_Update"));

        // the Score button switches us to scoring mode.
        Button scoreButton = (Button) findViewById(R.id.databutton);
        scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the currently active layout
                Layout theLayout = (Layout) app.df.theRound.getLayouts().get(app.df.theRound.getActiveLayoutIdx());
                // we get the scoring template here, not a specific copy of a scorecard
                List<StandScore> scorePattern = theLayout.getScorePattern();
                // Build an object to wrap the scores.
                ScoreCard scoreCard = new ScoreCard(scorePattern);
                // to do Need to set the name and other data!
                scoreCard.setLayoutId(theLayout.getLayoutId());
                // put it to the round.
                app.df.theRound.addScoreCard(1, 1, scoreCard);

                // todo
                // this makes sure we only do stuff for shooter 1
                // this is superflous at the moment but there you go
                if (app.currentShooter == 1) {
                    // send the data to the Watch todo kick off in an async thread? data transfer is already a thread

                    // ??? todo move first data sync to the scoring activity!

                    //                  sendDataToWearable();
                }

                Intent intent;
                if (currentLayout.getFormat().equals(app.df.ESP)) {
                    intent = new Intent(v.getContext(), SportScoringActivity.class);
                    //                 else
                    //                 intent = new Intent(v.getContext(), MobileScoringActivity.class);
                    startActivity(intent);
                }
            }

        });

        //
        //Display what we are doing to the user
        //
        tvDiscipline = (TextView) findViewById(R.id.tvDiscipline);
        tvFormat = (TextView) findViewById(R.id.tvFormat);
        tvTStands = (TextView) findViewById(R.id.tvTStands);
        tvTTargets = (TextView) findViewById(R.id.tvTTargets);
        tvTargets = (TextView) findViewById(R.id.tvTargets);

        tvFormat.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(v.getContext(), MobileSetFormatActivity.class);
                startActivity(intent);
                return false;
            }
        });

        setTargetCount();

        if (!(currentLayout.getDiscipline().equals(app.df.TRAP)) && !(currentLayout.getDiscipline().equals(app.df.SKEET))) {

            npStand = (NumberPicker) findViewById(R.id.npStand);
            npStand.setMinValue(1);
            int totalStands = currentLayout.countStands();
            npStand.setMaxValue(totalStands);
            tvTStands.setText(valueOf(totalStands));
            npStand.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(
                        NumberPicker picker,
                        int oldVal,
                        int newVal) {

                    // When the stand selection is changed the Type picker needs to
                    // reflect the type of target on the first turn of the selected stand.
                    // The actions differ depending on the format!
                    int turn = 1;
                    // if this is not a 'non-turn' type then we need to set up the turn parameters
                    if (!currentLayout.getFormat().equals(app.df.ESP) &&
                            !currentLayout.getFormat().equals(app.df.F32)) {
                        npCycle.setValue(turn);
                        // set the number of turns available to the number of turns on this stand.
                        npCycle.setMaxValue(currentLayout.getCycleCount(newVal));
                    }

/*                    // Sort out the formats in which we use more than one turn per stand!
                    // and set the turn to 1. We don't set it in ESP and T32 but use the default anyway
                    if (!currentLayout.getFormat().equals(app.df.ESP) &&
                            !currentLayout.getFormat().equals(app.df.F32))
                        npCycle.setValue(turn);   */

                    setNpTypePerams(newVal, turn);
                    setNpTargetsPerams();
                    setTargetCount();
                }
            });

            //
            // Buttons to add and remove stands from the layout
            //

            ImageButton btnStdAdd = (ImageButton) findViewById(R.id.btnStdAdd);
            btnStdAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentLayout.addStandAt(npStand.getValue());
                    int totalStands = currentLayout.countStands();
                    npStand.setMaxValue(totalStands);
                    tvTStands.setText(valueOf(totalStands));
                    setNpTypePerams(npStand.getValue(), 1);
                    setTargetCount();
                }
            });

            ImageButton btnStdRem = (ImageButton) findViewById(R.id.btnStdRem);
            btnStdRem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentLayout.countStands() > 1) {
                        currentLayout.removeStandAt(npStand.getValue());
                        int totalStands = currentLayout.countStands();
                        npStand.setMaxValue(totalStands);
                        tvTStands.setText(valueOf(totalStands));
                        setNpTypePerams(npStand.getValue(), 1);
                        setTargetCount();
                    }
                }
            });

            //
            // The cycle selector is used for formats where multiple target types are presented
            // after a reload on a single stand
            //
            // Only do this lot if we are not in ESP or Fan 32!
            if (!currentLayout.getFormat().equals(app.df.ESP) &&
                    !currentLayout.getFormat().equals(app.df.F32)) {

                npCycle = (NumberPicker) findViewById(R.id.npCycle);
                npCycle.setMinValue(1);

                npCycle.setValue(1);
                // set the number of turns available to the number of turns on this stand.
                npCycle.setMaxValue(currentLayout.getCycleCount(npStand.getValue()));

                // When change we set the turn type to the new type.
                npCycle.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(
                            NumberPicker picker,
                            int oldVal,
                            int newVal) {

                        setNpTypePerams(npStand.getValue(), newVal);
                        setNpTargetsPerams();
                        setTargetCount();
                    }
                });

                ImageButton btnCycleAdd = (ImageButton) findViewById(R.id.btnCycleAdd);
                btnCycleAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentLayout.addCycleAt(npStand.getValue(), npCycle.getValue());
                        // set the number of turns available to the number of turns on this stand.
                        npCycle.setMaxValue(currentLayout.getCycleCount(npStand.getValue()));
                        setNpTypePerams(npStand.getValue(), npCycle.getValue());
                        setTargetCount();
                    }
                });

                btnTrnRem = (ImageButton) findViewById(R.id.btnTurnRem);
                btnTrnRem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentLayout.removeCycleAt(npStand.getValue(), npCycle.getValue());
                        // set the number of turns available to the number of turns on this stand.
                        npCycle.setMaxValue(currentLayout.getCycleCount(npStand.getValue()));
                        setNpTypePerams(npStand.getValue(), npCycle.getValue());
                        setTargetCount();
                    }
                });
            }

            //
            // Number of targets must be set up before type or turn!
            //
            npTargets = (NumberPicker) findViewById(R.id.npTargets);
            npTargets.setMinValue(1);
            npTargets.setMaxValue(20);

            if (!currentLayout.getFormat().equals(app.df.ESP) &&
                    !currentLayout.getFormat().equals(app.df.F32))
                turn = npCycle.getValue();
            else
                turn = 1;

            npTargets.setValue(currentLayout.getTargetCount(npStand.getValue(), turn));

            npTargets.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(
                        NumberPicker picker,
                        int oldVal,
                        int newVal) {

                    // Adjust the number of targets on the current stand
                    // Decide if it's a multi turn type and use the turn on the picker
                    // or just use turn = 1 if it's a single turn type
                    String currentFormat = currentLayout.getFormat();
                    if (!currentLayout.getFormat().equals(app.df.ESP) &&
                            !currentLayout.getFormat().equals(app.df.F32))
                        turn = npCycle.getValue();
                    else
                        turn = 1;

                    currentLayout.adjCycleTargetCount(newVal, npStand.getValue(), turn);
                    setTargetCount();
                }
            });

            //
            // Target type selector
            // Selects the type of target presented during the turn
            //

            npType = (NumberPicker) findViewById(R.id.npType);
            npType.setMinValue(0);

            // If we are SuperSporting then we use the full set of target types
            // Otherwise we use the limited set.
            // Nope sporting types too ( Sim etc ) but this is not for wear version really.
            if ((currentLayout.getFormat().compareTo(app.df.SUPER) == 0)) {
                npType.setMaxValue(app.df.superTargets.length - 1);
                npType.setDisplayedValues(app.df.superTargets);
            } else {
                npType.setMaxValue(app.df.sportingTargets.length - 1);
                npType.setDisplayedValues(app.df.sportingTargets);
            }

            //
            // Set the current target type = the type being used on selected stand and turn!
            //
            if (!currentLayout.getFormat().equals(app.df.ESP) &&
                    !currentLayout.getFormat().equals(app.df.F32))
                turn = npCycle.getValue();
            else
                turn = 1;

            setNpTypePerams(npStand.getValue(), turn);
            setNpTargetsPerams();

            // When change we set the turn type to the new type.
            npType.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(
                        NumberPicker picker,
                        int oldVal,
                        int newVal) {
                    // change the stand turn target type to the new value.
                    currentLayout.setCycleTargetType(npStand.getValue(), turn, newVal);
                    // Set the current target type = the type being used on selected stand and turn!
                    //
                    if (!currentLayout.getFormat().equals(app.df.ESP) &&
                            !currentLayout.getFormat().equals(app.df.F32))
                        turn = npCycle.getValue();
                    else
                        turn = 1;

                    setNpTypePerams(npStand.getValue(), turn);
                    setNpTargetsPerams();
                    setTargetCount();
                }
            });
        } // ends not trap or skeet.

        // Fire up the Wear activity.
 //       new StartWearableActivityTask().execute();

    } // ends OnCreate()

    // Sets the the target type based on stand and turn
    private void setNpTypePerams(int stand, int turn) {
        // Find out what type of targets are being used and set the picker to the same value.
        npType.setValue(currentLayout.getTargetType(stand, turn));
    }

    // Set the max number on the number picker dependant on the type of targets in the chosen stand and turn
    // 20 for singles and 10 for pairs ( 20 targets for pairs or 30 for trips )
    // selects the number corresponding to the sequence
    private void setNpTargetsPerams() {
        // Set the current target type = the type being used on selected stand and turn!
        //
        if (!currentLayout.getFormat().equals(app.df.ESP) &&
                !currentLayout.getFormat().equals(app.df.F32))
            turn = npCycle.getValue();
        else
            turn = 1;

        // Set the target count picker to the length of targets ( not the total number! )
        npTargets.setValue(currentLayout.getTargetLength(npStand.getValue(), turn));

        int ttype = currentLayout.getTargetType(npStand.getValue(), turn);
        // With single targets we set the Pairs/targets picker to different perameters
        // we also need to change the arrays that describe this pattern
        // also if it's singles we can select up to 20, doubles, just 10
        if (ttype == app.df.lAllTargetTypes.indexOf(app.df.SINGLES)) {
            tvTargets.setText(getResources().getString(R.string.labelTargets));
            npTargets.setMaxValue(20);
        } else if (ttype == app.df.lAllTargetTypes.indexOf(app.df.REPORT) ||
                ttype == app.df.lAllTargetTypes.indexOf(app.df.RAF) ||
                ttype == app.df.lAllTargetTypes.indexOf(app.df.SIM)) {
            tvTargets.setText(getResources().getString(R.string.labelPairs));
            npTargets.setMaxValue(10);
        } else {
            tvTargets.setText(getResources().getString(R.string.labelTrips));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Data layer stuff here
    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart connecting!");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop!");

        Wearable.DataApi.removeListener(mGAClient, this);
        mGAClient.disconnect();
    }


    @Override
    protected void onResume() {
        super.onStart();
        Log.i(TAG, "Main Resume!");

        onStartWearableActivityClick();

        // We MAY have changed the scoresheet while we were away!
        // which may require changing activity views!!!
        setTargetCount();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGAClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        //  Wearable.DataApi.addListener(mGoogleApiClient, this);
        Log.e(TAG, "Connection Suspended!");
    }
    @Override
    public void onConnectionFailed(ConnectionResult r) {
        Log.e(TAG, "Connection Failed!");
    }


    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGAClient, this);
        mGAClient.disconnect();
    }

    @Override //DataListener
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.i("Main Activity", "onDataChanged: " + dataEvents);
//        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
/*        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (DataEvent event : events) {
                    if (event.getType() == DataEvent.TYPE_CHANGED) {
                        mDataItemListAdapter.add(
                                new Event("DataItem Changed", event.getDataItem().toString()));
                    } else if (event.getType() == DataEvent.TYPE_DELETED) {
                        mDataItemListAdapter.add(
                                new Event("DataItem Deleted", event.getDataItem().toString()));
                    }
                }
            }
        });  */
    }
    //
    // calculate the total of all the targets in the layout
    private void setTargetCount() {
        tvTStands.setText(valueOf(currentLayout.countStands()));
        tvTTargets.setText(valueOf(currentLayout.getTotalTargets()));
        tvDiscipline.setText(" : " + currentLayout.getDiscipline());
        tvFormat.setText(" : " + currentLayout.getFormat());

        tvDiscipline.invalidate();
        tvFormat.invalidate();
        tvTTargets.invalidate();
    }

    private String now() {
        DateFormat dateFormat = android.text.format.DateFormat.getTimeFormat(this);
        return dateFormat.format(new Date());
    }

    private void sendNotification() {
        Log.e(TAG, "sendNotification!");
        if (mGAClient.isConnected()) {
            Log.e(TAG, "isconnected!");
            PutDataMapRequest dataMapRequest = PutDataMapRequest.create(Constants.NOTIFICATION_PATH);
            // Make sure the data item is unique. Usually, this will not be required, as the payload
            // (in this case the title and the content of the notification) will be different for almost all
            // situations. However, in this example, the text and the content are always the same, so we need
            // to disambiguate the data item by adding a field that contains teh current time in milliseconds.
            dataMapRequest.getDataMap().putDouble(Constants.NOTIFICATION_TIMESTAMP, System.currentTimeMillis());
            dataMapRequest.getDataMap().putString(Constants.NOTIFICATION_TITLE, "A Notification");
            dataMapRequest.getDataMap().putString(Constants.NOTIFICATION_CONTENT, "Claywear Ready!");
            PutDataRequest putDataRequest = dataMapRequest.asPutDataRequest();
            Wearable.DataApi.putDataItem(mGAClient, putDataRequest);
        } else {
            Log.e(TAG, "No connection to wearable available!");
        }
    }

    //
    // If the wearable changes stuff then we need to implement it here!
    // todo need to split the activity into setup, scoring, stats etc.
    //

    class SendToDataLayerThread extends Thread {
        String path;
        DataMap dataMap;

        // Constructor to send a message to the data layer
        SendToDataLayerThread(String p, DataMap data) {
            path = p;
            dataMap = data;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGAClient).await();
            for (Node node : nodes.getNodes()) {
                // Construct a DataRequest and send over the data layer
                PutDataMapRequest putDMR = PutDataMapRequest.create(path);
                putDMR.getDataMap().putAll(dataMap);
                PutDataRequest request = putDMR.asPutDataRequest();
                DataApi.DataItemResult result = Wearable.DataApi.putDataItem(mGAClient, request).await();
                if (result.getStatus().isSuccess()) {
                    Log.v(TAG, "DataMap: " + dataMap + " sent to: " + node.getDisplayName());
                } else {
                    // Log an error
                    Log.v(TAG, "ERROR: failed to send DataMap");
                }
            }
        }
    }

    // Handles data passed to the activity from the listening thread.
    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Display message in log
            Log.v(TAG, "Broadcast Message received " + intent);

/*            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] vibrationPattern = {0, 100};
            //-1 - don't repeat
            final int indexInPatternToRepeat = -1;
            vibrator.vibrate(vibrationPattern, indexInPatternToRepeat); */
        }
    }

    /**
     * Sends an RPC to start a fullscreen Activity on the wearable.
     */
    public void onStartWearableActivityClick( /* View view */) {
        Log.i(TAG, "Generating RP calls");

        // Triggers an AsyncTask that will query for a list of connected
        // nodes and send a "start-activity" message to each connected node.
 //       new StartWearableActivityTask().execute();
    }

    private class StartWearableActivityTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            Collection<String> nodes = getNodes();
            for (String node : nodes) {
                sendStartActivityMessage(node);
            }
            return null;
        }
    }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGAClient).await();

        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        nodes = (NodeApi.GetConnectedNodesResult) results;
        return results;
    }

    private void sendStartActivityMessage(String node) {
        Log.i(TAG, "Kicking device " + node);
        Wearable.MessageApi.sendMessage(
                mGAClient, node, "START_CLAYWEAR_ACTIVITY",

                new byte[0]).setResultCallback(
                new ResultCallback<SendMessageResult>() {
                    @Override
                    public void onResult(SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Failed to send msg with status code: "
                                    + sendMessageResult.getStatus().getStatusCode());
                        }
                    }
                }
        );
    }

}

