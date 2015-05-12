package com.umfundi.wear.claywear;

// todo - scoring systems.
// currently default to hit. scorer needs to keep track of how many shots have been taken
//
// new scheme - not shot, missed hit or not scored hit miss.
//
// todo - long press selects current stand being shot.
//
//
//

import android.accounts.Account;
import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexicious.controls.core.ClassFactory;
import com.flexicious.controls.core.Function;
import com.flexicious.nestedtreedatagrid.FlexDataGrid;
import com.flexicious.nestedtreedatagrid.FlexDataGridBodyContainer;
import com.flexicious.nestedtreedatagrid.FlexDataGridColumn;
import com.flexicious.nestedtreedatagrid.interfaces.IFlexDataGridCell;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.umfundi.wear.claywear.fullscreen.util.SystemUiHider;
import com.umfundi.wear.claywear.model.Layout;
import com.umfundi.wear.claywear.model.ScoreCard;
import com.umfundi.wear.claywear.model.StandScore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SportScoringActivity extends Activity implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SportingScoring";

    protected static ClayWearApplication app;

    private FlexDataGrid scoreGrid;

    private Button cycleButton;

    LinearLayout ll;
    LinearLayout.LayoutParams lp;

    static boolean scoring = true;

    static private int currentShooter = 0;

    private static Layout theLayout;
    private static Map<Integer,Map> layoutData;
    private ScoreCard theScoreCard;
    private List<StandScore> scorePattern;

    public static GoogleApiClient mGoogleApiClient;

    public HashSet<String> nodes;

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link com.umfundi.wear.claywear.fullscreen.util.SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link com.umfundi.wear.claywear.fullscreen.util.SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

        // Content provider authority
        public static final String AUTHORITY = "com.example.android.datasync.provider";
        // Account type
        public static final String ACCOUNT_TYPE = "com.example.android.datasync";
        // Account
        public static final String ACCOUNT = "default_account";

    Account mAccount;

 //   @SuppressLint({"InflateParams", "UseValueOf"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

 //       mAccount = CreateSyncAccount(this);

        setContentView(R.layout.sporting_scoring);

        app = (ClayWearApplication) this.getApplication();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // get the layout data from the current layout in the round
        theLayout = (Layout)app.df.theRound.getLayouts().get(app.df.theRound.getActiveLayoutIdx());
        layoutData = theLayout.getLayoutData();

        // get the scorecard for round 1 , shooter 1
        theScoreCard = (ScoreCard) app.df.theRound.getScoreCard(1,1);
        scorePattern = theScoreCard.getScorePattern();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float density = metrics.density;
        float height = metrics.heightPixels;
        float width = metrics.widthPixels;

//        final View controlsView = findViewById(R.id.scoring_layout);
//        final View contentView = findViewById(R.id.scoring_layout);

        // switch button restarts the pattern activity
        // TODO needs changing as every time a new instance is created!
        Button exitButton = (Button) findViewById(R.id.switchButton1);
        exitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //			Intent intent = new Intent(v.getContext(), MainActivity.class);
                //			startActivity(intent);

                // We need to check if the grid needs to be saved before we finish.
                // data in theScorecard appears to be a copy of the layout not the same data

                // todo

                // convert the scores back to some useable format!

                ObjectMapper mapper = new ObjectMapper();
                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

                try {
                    // value to store !! It works !!
                    Log.i("Test", mapper.writeValueAsString(theScoreCard));


                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                // We have a completed ScoreCard
                // need to store it and pass it to the server!
                //

                // Pass the settings flags by inserting them in a bundle
                Bundle settingsBundle = new Bundle();
                settingsBundle.putBoolean(
                        ContentResolver.SYNC_EXTRAS_MANUAL, true);
                settingsBundle.putBoolean(
                        ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                /*
                 * Request the sync for the default account, authority, and
                 * manual sync settings
                 */
                ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);

                // todo need to do something with the watch!

                finish();


            }
        });

        cycleButton = (Button) findViewById(R.id.doitBtn);
        cycleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // We are doing sporting so we do targets till there done then skip to the next stand.
                cycleInc();
                sendDataToWearable("Cycle");
            }
        });


        String ss;

        app.currentStand = 1;
        app.currentCycle = 1;
        app.currentTarget = 0;

        // build the basic frame for the grid from an XML raw fragment
        Log.i("SportScoring", "Build initial grid ");
        scoreGrid = (FlexDataGrid) findViewById(R.id.scoreGrid);

        // Build the stand numbering column

        int maxwidth = theLayout.getMaxWidth();

        ll = (LinearLayout) findViewById(R.id.gridParent);

        FlexDataGridColumn ccol = buildScoringColumn("stand", "Stand");
        ccol.setColumnLockMode(FlexDataGridColumn.LOCK_MODE_LEFT);
        ccol.setWidth(100);
        try {
            ccol.setItemRenderer(new ClassFactory(Class.forName("com.umfundi.wear.claywear.itemRenderer.StandBoxViewRenderer")));
        } catch (Exception e) {
            System.out.println("Somehow the item renderer is missing!. " + e);
        }

        scoreGrid.addColumn(ccol);

        for (int i = 1; i <= maxwidth; i++) {
            ss = "S" + Integer.toString(i);
            ccol = buildScoringColumn(ss, "T" + (i));
            try {
               ccol.setItemRenderer(new ClassFactory(Class.forName("com.umfundi.wear.claywear.itemRenderer.TargetBoxViewRenderer")));
 //            ccol.setItemRenderer(new ClassFactory(Class.forName(".TargetBoxViewRenderer")));
            } catch (Exception e) {
                System.out.println("Somehow the item renderer is missing!. " + e);
            }
            ccol.setColumnWidthMode(FlexDataGridColumn.COLUMN_WIDTH_MODE_FIXED);
            ccol.setWidth(80);
            scoreGrid.addColumn(ccol);
        }

        // the totalising column
        ccol = buildScoringColumn("total", "Total");
        ccol.setColumnWidthMode(FlexDataGridColumn.COLUMN_WIDTH_MODE_FIXED);
        ccol.setColumnLockMode(FlexDataGridColumn.LOCK_MODE_RIGHT);
        ccol.setWidth(120);
        scoreGrid.addColumn(ccol);

        buildGrid(scoreGrid, R.raw.score);

        int gwidth = 220;       // left and right column widths
        int gheight = 74;       // header column height
        int cols = 0;
        int rows = 0;

        lp = (LinearLayout.LayoutParams) ll.getLayoutParams();

        while (gwidth < width -(80 /* + padding widths from base layout */) && cols < maxwidth)
            {
            gwidth += 80;
            cols ++;
            }

        while (gheight < height - (80 /* + padding heights from base layout */ + 400 /* height required for bottom layout & controls */) && rows < scorePattern.size())
        {
            gheight += 80;
            rows ++;
        }

        lp.width = gwidth;
        lp.height = gheight;
        ll.requestLayout();

        scoreGrid.setSelectionMode(FlexDataGrid.SELECTION_MODE_SINGLE_ROW);
        // put the data into the scoring grid
        scoreGrid.setDataProvider(scorePattern);
        scoreGrid.setSelectedIndex(app.currentStand-1);
        renderTargetText();
        sendDataToWearable("Initialize");
    }

    // Data layer stuff here
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "Sport Scoring onStart!");
        app.currentStand = 1;
        app.currentCycle = 1;
        app.currentTarget = 0;

        //       renderTargetText();

        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop!");
        mGoogleApiClient.disconnect();

        // lets try copy the scorecard back to the players data!

        for (int stand = 1; stand <= scorePattern.size();stand++) {
            scorePattern.get(stand - 1);
        }

    }


    @Override
    protected void onResume() {
        super.onStart();
        Log.i(TAG, "Main Resume!");

        // We MAY have changed the scoresheet while we were away!
        // which may require changing activity views!!!

//        setTargetCount();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
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
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public FlexDataGridColumn buildScoringColumn(String dataField, String headerText) {
        FlexDataGridColumn dgCol = new FlexDataGridColumn();
        dgCol.setDataField(dataField);
        dgCol.setHeaderText(headerText);
        dgCol.sortable = false;
        dgCol.setEnableCellClickRowSelect(false);
        dgCol.setHeaderAlign("center");
        dgCol.setColumnWidthMode("fixed");

        try {
             dgCol.setCellBackgroundColorFunction(new Function(this,"CellFormatting_getCellBackground"));
        } catch (Exception e) {
            System.out.println("Somehow the cell background formatting function is missing!. " + e);
        }
        return dgCol;
    }

    private void cycleInc() {
        Map<Integer, Map> theStand = layoutData.get(app.currentStand);
        // with sporting we only have one cycle per stand
        Map<Integer, ArrayList> theCycle = theStand.get(1);
        ArrayList <Integer> tinfo = theCycle.get(0);

        FlexDataGridBodyContainer bc = scoreGrid.getBodyContainer();

        app.currentTarget += tinfo.get(1);
        if (app.currentTarget >= tinfo.get(1)* tinfo.get(2)) {
            app.currentTarget = 0;
            if (app.currentStand < layoutData.size()) {
                app.currentStand++;
            } else {
                app.currentStand = 1;
            }
            scoreGrid.setSelectedIndex(app.currentStand-1);
//            bc.scrollToExistingRow(app.currentStand,false);
        }

        // find the cycle size
        app.currentCycle = (app.currentTarget/tinfo.get(1))+1;

        renderTargetText();
        scoreGrid.reDraw();
    }


    void renderTargetText() {
        TextView t = (TextView) findViewById(R.id.textTargets);
        String s = "";

        Map<Integer, Map> theStand = layoutData.get(app.currentStand);
        // with sporting we only have one cycle per stand
        Map<Integer, ArrayList> theCycle = theStand.get(1);
        ArrayList <Integer> tinfo = theCycle.get(0);

        s = "Stand " + app.currentStand+" ";

        if (tinfo.get(0) == app.df.listSporting.indexOf(app.df.SINGLES))
            s += tinfo.get(2) + " " + app.df.SINGLES + " Target ";
        else if (tinfo.get(0) == app.df.listSporting.indexOf(app.df.REPORT))
            s += tinfo.get(2) + " " + app.df.REPPAIRS + " Pair ";
        else if (tinfo.get(0) == app.df.listSporting.indexOf(app.df.SIM))
            s += tinfo.get(2) + " " + app.df.SIMPAIRS + " Pair ";

        s+= (app.currentCycle);

        t.setText(s);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.full_grid, menu);
        return true;
    }

    // Builds the grid from the XML config file in resources
    public void buildGrid(FlexDataGrid grid, Integer resource) {
        grid.delegate = this;
        BufferedReader reader = new BufferedReader(new InputStreamReader(this
                .getResources().openRawResource(resource)));
        StringBuilder builder = new StringBuilder();
        String aux = "";
        try {
            while ((aux = reader.readLine()) != null) {
                builder.append(aux);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String text = builder.toString();
        scoreGrid.buildFromXml(text);
    }

    public int getUIColorObjectFromHexString(String hexint) {
        hexint = hexint.replaceAll("0x", "#");
        return Color.parseColor(hexint);
    }

    // Sets the cell background colour
    // if the current row is being shot then we highlight the cell
    public Object CellFormatting_Textcolour(IFlexDataGridCell cell) {
        if (/*!cell.getEnabled() &&*/ cell.getRowInfo().rowPositionInfo.rowIndex == app.currentStand) {
            // Set the text red
            return Color.parseColor("#ed1c24");
        } else
            return null;
    }

    // Sets the cell background colour
    // if the current row is being shot then we highlight the cell
    public Object CellFormatting_getCellBackground(IFlexDataGridCell cell) {
        int c = cell.getColumn().getColIndex()-1;

        // get the stand or peg number from the column.
        int r = cell.getRowInfo().rowPositionInfo.rowIndex+1;

        // If the currentShooter is the same as the row
        if (r == app.currentStand) {
            // decide if the column is within the current target group

            Map<Integer, Map> theStand = layoutData.get(app.currentStand);
            // with sporting we only have one cycle per stand
            Map<Integer, ArrayList> theCycle = theStand.get(1);
            ArrayList <Integer> tinfo = theCycle.get(0);


            // We highlight the entire current cycle from its first target to it's last'

            int tmin = (app.currentCycle-1)*tinfo.get(1);

            int tmax = tmin + tinfo.get(1)-1;

            if (c >= tmin && c <= tmax )
                return Color.parseColor("#003388");
            else
                return null;
        }
        return null;
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
    Log.i(TAG, "INCOMING: DataChange");
        for (DataEvent event : dataEvents) {
            int i = event.getType();
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/WearUpdate") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    // We have seen a change to the scorecard here so we process it!
                    //              updateScorecard(dataMap.getInt(COUNT_KEY));
                    String JSONscore = dataMap.getString("Scores");
                    Log.i(TAG, "Scores String: " + JSONscore);
                    // and turn it back into a scoresheet by reversing the JSON construction.
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

                        scorePattern = mapper.readValue(JSONscore, new TypeReference<LinkedList<StandScore>>() {});
                        app.currentCycle = dataMap.getInt("Cycle");
                        app.currentStand = dataMap.getInt("Stand");

                        Message msg = Message.obtain();

                        msg.what=123;
                        reassign.sendMessage(msg);

                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (item.getUri().getPath().compareTo("/WearRequest") == 0) {
                    sendDataToWearable("Update");
                } else {
                    Log.i(TAG,"Path sent and ignored: "+item.getUri().getPath());
                }
                } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    private static void sendDataToWearable(String t) {
        // Convert the scoresheet to JSON and send it to the wearable via the data layer.
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        try {
//              Map<Integer, Map> layoutData = currentLayout.getLayoutData();

            DataMap dataMap = new DataMap();

            Date date = new Date();
            dataMap.putString("TS", date.toString());

            dataMap.putString("Type", t);
            // Not needed in this context as it's not changed!
            dataMap.putString("LayoutData", mapper.writeValueAsString(layoutData));
            dataMap.putString("Discipline", mapper.writeValueAsString(theLayout.getDiscipline()));
            dataMap.putString("Format", mapper.writeValueAsString(theLayout.getFormat()));
//               dataMap.putString("TargetNames", mapper.writeValueAsString(app.df.lAllTargetTypes));
            dataMap.putString("Shooter", "Ian");
            dataMap.putInt("Target", app.currentTarget);
            ScoreCard scoreCard = (ScoreCard)app.df.theRound.getScoreCard(1,1);
            List<StandScore> theList = scoreCard.getScorePattern();

            String p = mapper.writeValueAsString(theList);
            Log.i("Sending scorepattern",p);

            for (int q = 1; q <= theList.size(); q++) {
                StandScore sp = theList.get(q-1);
                Log.i(TAG, "Stand " + q + " " + sp.getS1() + " " + sp.getS2() + " " + sp.getS3() + " "
                        + sp.getS4() + " " + sp.getS5() + " " + sp.getS6() + " " + sp.getS7() + " "
                        + sp.getS8() + " " + sp.getS9() + " " + sp.getS10());
            }

            dataMap.putString("Scores", mapper.writeValueAsString(theList));
            dataMap.putInt("Stand", app.currentStand);
            dataMap.putInt("Cycle", app.currentCycle);

            Log.i("Scoring", "Data sync to wear " + mapper.writeValueAsString(theList));
            new SendToDataLayerThread("/MobileScoreData", dataMap).start();
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    static class SendToDataLayerThread extends Thread {
        String path;
        DataMap dataMap;

        // Constructor to send a message to the data layer
        SendToDataLayerThread(String p, DataMap data) {
            path = p;
            dataMap = data;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes;
            nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

            for (Node node : nodes.getNodes()) {
                // Construct a DataRequest and send over the data layer
                PutDataMapRequest putDMR = PutDataMapRequest.create(path);
                putDMR.getDataMap().putAll(dataMap);
                PutDataRequest request = putDMR.asPutDataRequest();
                DataApi.DataItemResult result = Wearable.DataApi.putDataItem(mGoogleApiClient,request).await();
                if (result.getStatus().isSuccess()) {
                    Log.v(TAG, "DataMap was sent to: "  + node.getDisplayName() );
                } else {
                    // Log an error
                    Log.v(TAG, "ERROR: failed to send DataMap to " + node.getDisplayName() );
                }
            }
        }
    }

    Handler reassign = new Handler(){

        @Override
        public void handleMessage(Message msg){
        Log.i("Scoring","Message rx'd "+msg);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            Log.i("Current scorepattern", mapper.writeValueAsString(scorePattern));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (msg.what == 123){

   //         scoreGrid.invalidateList();
   //         scoreGrid.processFilter();
   //         scoreGrid.redrawBody();
            // the only thing that works :(
            scoreGrid.setDataProvider(scorePattern);
            scoreGrid.setSelectedIndex(app.currentStand-1);
            // need to set the current Target?
            scoreGrid.refreshCells();
       }
        super.handleMessage(msg);
        }

    };

    public static Handler _handler = new Handler(){

        @Override
        public void handleMessage(Message msg){
        Log.i("Scoring","Message rx'd "+msg);
        if (msg.what == 999) {
            sendDataToWearable("Update");
        }
        super.handleMessage(msg);
        }

    };

}


