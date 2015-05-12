package com.umfundi.wear.claywear;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import static com.google.android.gms.wearable.PutDataRequest.WEAR_URI_SCHEME;

public class NotificationUpdateService extends WearableListenerService {

    private int notificationId = 001;
    private static final String TAG = "Wear NU_Service";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStart!");
        if (null != intent) {
            String action = intent.getAction();
            if (Constants.ACTION_DISMISS.equals(action)) {
                dismissNotification();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.i(TAG, "Data event!");
        for(DataEvent dataEvent: dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                Log.i(TAG, "Type changed!");
                DataMap dataMap;
                if (Constants.NOTIFICATION_PATH.equals(dataEvent.getDataItem().getUri().getPath())) {

                    DataMapItem dataMapItem = DataMapItem.fromDataItem(dataEvent.getDataItem());
                    String title = dataMapItem.getDataMap().getString(Constants.NOTIFICATION_TITLE);
                    String content = dataMapItem.getDataMap().getString(Constants.NOTIFICATION_CONTENT);
                    sendNotification(title, content);
                }

                String path = dataEvent.getDataItem().getUri().getPath();
                if (path.equals("/MobileScoreData")) {
                    Log.i(TAG, "Processed " + path);
                    dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();

                    // Broadcast message to the activity
                    Intent messageIntent = new Intent("ClayWear_DataUpdate");
       // iHm investigate why this kills the messaging!
       //     messageIntent.setAction(Intent.ACTION_SEND);
                    messageIntent.putExtra("datamap", dataMap.toBundle());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
                } else {
                    Log.i(TAG, "Discarded DataPath is " + path);
                }
            }
        }
    }

    private void sendNotification(String title, String content) {
        Log.e(TAG, "Send notification!");
        // this intent will open the activity when the user taps the "open" action on the notification
        Intent viewIntent = new Intent(this, ClayWearSportingActivity.class);
        PendingIntent pendingViewIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);

        // this intent will be sent when the user swipes the notification to dismiss it
        Intent dismissIntent = new Intent(Constants.ACTION_DISMISS);
        PendingIntent pendingDeleteIntent = PendingIntent.getService(this, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.card_frame)
                .setContentTitle(title)
                .setContentText(content)
                .setDeleteIntent(pendingDeleteIntent)
                .setContentIntent(pendingViewIntent);

        Notification notification = builder.build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationId++, notification);
    }

    private void dismissNotification() {
        new DismissNotificationCommand(this).execute();
    }

    private class DismissNotificationCommand implements GoogleApiClient.ConnectionCallbacks, ResultCallback<DataApi.DeleteDataItemsResult>, GoogleApiClient.OnConnectionFailedListener {

        private static final String TAG = "DismissNotification";

        private final GoogleApiClient mGoogleApiClient;

        public DismissNotificationCommand(Context context) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        public void execute() {
            mGoogleApiClient.connect();
        }

        @Override
        public void onConnected(Bundle bundle) {
            Log.e(TAG, "on Connected!");
            final Uri dataItemUri =
                    new Uri.Builder().scheme(WEAR_URI_SCHEME).path(Constants.NOTIFICATION_PATH).build();
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Deleting Uri: " + dataItemUri.toString());
            }
            Wearable.DataApi.deleteDataItems(
                    mGoogleApiClient, dataItemUri).setResultCallback(this);
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG, "onConnectionSuspended");
        }

        @Override
        public void onResult(DataApi.DeleteDataItemsResult deleteDataItemsResult) {
            if (!deleteDataItemsResult.getStatus().isSuccess()) {
                Log.e(TAG, "dismissWearableNotification(): failed to delete DataItem");
            }
            mGoogleApiClient.disconnect();
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.d(TAG, "onConnectionFailed");
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.i(TAG, "onMessageReceived: " + messageEvent);

        // Check to see if the message is to start an activity
        if (messageEvent.getPath().equals("START_CLAYWEAR_ACTIVITY")) {
            Intent startIntent = new Intent(this, ClayWearSportingActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);
        }
    }

}