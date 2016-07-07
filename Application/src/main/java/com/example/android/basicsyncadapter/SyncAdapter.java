/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.basicsyncadapter;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.provider.CalendarContract.*;
import android.util.Log;

import com.example.android.Api;
import com.example.android.basicsyncadapter.net.FeedParser;
import com.example.android.basicsyncadapter.provider.FeedContract;
import com.example.android.common.accounts.GenericAccountService;
import com.example.android.data.model.CalendarsEventsList;
import com.example.android.data.model.Event;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Response;

/**
 * Define a sync adapter for the app.
 *
 * <p>This class is instantiated in {@link SyncService}, which also binds SyncAdapter to the system.
 * SyncAdapter should only be initialized in SyncService, never anywhere else.
 *
 * <p>The system calls onPerformSync() via an RPC call through the IBinder object supplied by
 * SyncService.
 */
class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = "SyncAdapter";


    /**
     * URL to fetch content from during a sync.
     *
     * <p>This points to the Android Developers Blog. (Side note: We highly recommend reading the
     * Android Developer Blog to stay up to date on the latest Android platform developments!)
     */
    private static final String FEED_URL = "http://android-developers.blogspot.com/atom.xml";

    /**
     * Network connection timeout, in milliseconds.
     */
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000;  // 15 seconds

    /**
     * Network read timeout, in milliseconds.
     */
    private static final int NET_READ_TIMEOUT_MILLIS = 10000;  // 10 seconds

    /**
     * Content resolver, for performing database operations.
     */
    private final ContentResolver mContentResolver;

    /**
     * Project used when querying content provider. Returns all known fields.
     */
    private static final String[] PROJECTION = new String[] {
            FeedContract.Entry._ID,
            FeedContract.Entry.COLUMN_NAME_ENTRY_ID,
            FeedContract.Entry.COLUMN_NAME_TITLE,
            FeedContract.Entry.COLUMN_NAME_LINK,
            FeedContract.Entry.COLUMN_NAME_PUBLISHED};

    // Constants representing column positions from PROJECTION.
    public static final int COLUMN_ID = 0;
    public static final int COLUMN_ENTRY_ID = 1;
    public static final int COLUMN_TITLE = 2;
    public static final int COLUMN_LINK = 3;
    public static final int COLUMN_PUBLISHED = 4;

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    /**
     * Called by the Android system in response to a request to run the sync adapter. The work
     * required to read data from the network, parse it, and store it in the content provider is
     * done here. Extending AbstractThreadedSyncAdapter ensures that all methods within SyncAdapter
     * run on a background thread. For this reason, blocking I/O and other long-running tasks can be
     * run <em>in situ</em>, and you don't have to set up a separate thread for them.
     .
     *
     * <p>This is where we actually perform any work required to perform a sync.
     * {@link android.content.AbstractThreadedSyncAdapter} guarantees that this will be called on a non-UI thread,
     * so it is safe to peform blocking I/O here.
     *
     * <p>The syncResult argument allows you to pass information back to the method that triggered
     * the sync.
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.i(TAG, "Beginning network synchronization");


        syncCalendar(provider);





//        try {
//            final URL location = new URL(FEED_URL);
//            InputStream stream = null;
//            try {
//                Log.i(TAG, "Streaming data from network: " + location);
//                stream = downloadUrl(location);
//                updateLocalFeedData(stream, syncResult);
//                // Makes sure that the InputStream is closed after the app is
//                // finished using it.
//            } finally {
//                if (stream != null) {
//                    stream.close();
//                }
//            }
//        } catch (MalformedURLException e) {
//            Log.e(TAG, "Feed URL is malformed", e);
//            syncResult.stats.numParseExceptions++;
//            return;
//        } catch (IOException e) {
//            Log.e(TAG, "Error reading from network: " + e.toString());
//            syncResult.stats.numIoExceptions++;
//            return;
//        } catch (XmlPullParserException e) {
//            Log.e(TAG, "Error parsing feed: " + e.toString());
//            syncResult.stats.numParseExceptions++;
//            return;
//        } catch (ParseException e) {
//            Log.e(TAG, "Error parsing feed: " + e.toString());
//            syncResult.stats.numParseExceptions++;
//            return;
//        } catch (RemoteException e) {
//            Log.e(TAG, "Error updating database: " + e.toString());
//            syncResult.databaseError = true;
//            return;
//        } catch (OperationApplicationException e) {
//            Log.e(TAG, "Error updating database: " + e.toString());
//            syncResult.databaseError = true;
//            return;
//        }
//        Log.i(TAG, "Network synchronization complete");
    }

    private void syncCalendar(ContentProviderClient provider) {


        Response<CalendarsEventsList> response = null;
        try {
//            response = Api.getService().calendarsEventsList("819feadcdae142448ca7124cb85c84a5", "2015-10-21", "2016-10-21").execute();
            response = Api.getService().calendarsEventsList("e2f9A3MPav63VjzDgE9WKgyFxQsxVLFma99086f1d272475d8a0275f3982b49de", "2015-10-21", "2016-10-21").execute();

            Log.d(TAG, "Response code: " + response.code());

            if(response.isSuccessful()) {

                CalendarsEventsList body = response.body();
                Log.d(TAG, "Response: " + body);

                //        long calID = 3;
                long calID = createCalendarWithName(provider, "Team Zeus");

                Log.i(TAG, "Calendar ID: " + calID);


                for (Event event : body.getEvents()) {
                    createEvent(provider, calID, event);
                }


            } else {
                Log.d(TAG, "Response message: " + response.message());
            }

        } catch (IOException e) {
            Log.e(TAG, "api call error", e);
        }


        Log.d(TAG, "DONE");
    }


    private void createEvent(ContentProviderClient provider, long calID, Event event) {



//        long startMillis = 0;
//        long endMillis = 0;
//        Calendar beginTime = Calendar.getInstance();
////        beginTime.set(2012, 9, 14, 7, 30);
//        startMillis = beginTime.getTimeInMillis();
//        Calendar endTime = Calendar.getInstance();
//        endTime.set(2016, 9, 14, 8, 45);
//        endMillis = endTime.getTimeInMillis();

        long startMillis = event.getStart().getTime();
        long endMillis = event.getEnd().getTime();


        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, event.getName());
        values.put(CalendarContract.Events.DESCRIPTION, event.getName());
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
//        values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles");
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Rome");
//        values.put(Events.EVENT_COLOR, Integer.valueOf("0x" + event.getColor().substring(1)));
//        values.put(Events.EVENT_COLOR, Integer.parseInt(event.getColor().substring(1), 16));

        values.put(Events.ALL_DAY, event.isAllDay() ? 1 : 0);

        values.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE);

        values.put(CalendarContract.Events.CUSTOM_APP_PACKAGE, "com.teamzeusapp");
        values.put(CalendarContract.Events.CUSTOM_APP_URI, Uri.decode("udini://udini")); // Not sure how to work with it
        // XXX


//        values.put(Events.ACCOUNT_NAME, accountName);
//        values.put(Events.ACCOUNT_TYPE, accountType);

        values.put(Events._SYNC_ID, event.getId());
        values.put(Events.DIRTY, 0);


//        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        Uri uri = null;
        try {

            Uri target = Uri.parse(CalendarContract.Events.CONTENT_URI.toString());
            target = target.buildUpon().appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
                    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, accountType).build();

            uri = provider.insert(target, values);
        } catch (RemoteException e) {
            Log.e(TAG, "errr", e);
        }

// get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());
//
// ... do something with event ID
//
//


    }

    static String accountName = GenericAccountService.ACCOUNT_NAME; // XXX
    static String accountType = SyncUtils.ACCOUNT_TYPE;

    public static long createCalendarWithName(ContentProviderClient provider, String name) {


        Uri target = Uri.parse(CalendarContract.Calendars.CONTENT_URI.toString());
        target = target.buildUpon().appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, accountType).build();


        ContentValues values = new ContentValues();
        values.put(Calendars.ACCOUNT_NAME, accountName);
        values.put(Calendars.ACCOUNT_TYPE, accountType);
        values.put(Calendars.NAME, name);
        values.put(Calendars.CALENDAR_DISPLAY_NAME, name);
        values.put(Calendars.CALENDAR_COLOR, 0xF0FF00);
//        values.put(Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_ROOT);
        values.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_CONTRIBUTOR);
        values.put(Calendars.OWNER_ACCOUNT, accountName);
        values.put(Calendars.VISIBLE, 1);
        values.put(Calendars.SYNC_EVENTS, 1);
//        values.put(Calendars.CALENDAR_TIME_ZONE, "Europe/Rome");
        values.put(Calendars.CAN_PARTIALLY_UPDATE, 1);
//        values.put(Calendars.CAL_SYNC1, "https://www.google.com/calendar/feeds/" + accountName + "/private/full");
//        values.put(Calendars.CAL_SYNC2, "https://www.google.com/calendar/feeds/default/allcalendars/full/" + accountName);
//        values.put(Calendars.CAL_SYNC3, "https://www.google.com/calendar/feeds/default/allcalendars/full/" + accountName);
//        values.put(Calendars.CAL_SYNC4, 1);
//        values.put(Calendars.CAL_SYNC5, 0);
//        values.put(Calendars.CAL_SYNC8, System.currentTimeMillis());

        Uri newCalendar = null;
        try {
            newCalendar = provider.insert(target, values);
        } catch (RemoteException e) {
            Log.e(TAG, "errr", e);
        }

        long calendarID = Long.parseLong(newCalendar.getLastPathSegment());

        return calendarID;
    }






    /**
     * Read XML from an input stream, storing it into the content provider.
     *
     * <p>This is where incoming data is persisted, committing the results of a sync. In order to
     * minimize (expensive) disk operations, we compare incoming data with what's already in our
     * database, and compute a merge. Only changes (insert/update/delete) will result in a database
     * write.
     *
     * <p>As an additional optimization, we use a batch operation to perform all database writes at
     * once.
     *
     * <p>Merge strategy:
     * 1. Get cursor to all items in feed<br/>
     * 2. For each item, check if it's in the incoming data.<br/>
     *    a. YES: Remove from "incoming" list. Check if data has mutated, if so, perform
     *            database UPDATE.<br/>
     *    b. NO: Schedule DELETE from database.<br/>
     * (At this point, incoming database only contains missing items.)<br/>
     * 3. For any items remaining in incoming list, ADD to database.
     */
    public void updateLocalFeedData(final InputStream stream, final SyncResult syncResult)
            throws IOException, XmlPullParserException, RemoteException,
            OperationApplicationException, ParseException {
        final FeedParser feedParser = new FeedParser();
        final ContentResolver contentResolver = getContext().getContentResolver();

        Log.i(TAG, "Parsing stream as Atom feed");
        final List<FeedParser.Entry> entries = feedParser.parse(stream);
        Log.i(TAG, "Parsing complete. Found " + entries.size() + " entries");


        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        // Build hash table of incoming entries
        HashMap<String, FeedParser.Entry> entryMap = new HashMap<String, FeedParser.Entry>();
        for (FeedParser.Entry e : entries) {
            entryMap.put(e.id, e);
        }

        // Get list of all items
        Log.i(TAG, "Fetching local entries for merge");
        Uri uri = FeedContract.Entry.CONTENT_URI; // Get all entries
        Cursor c = contentResolver.query(uri, PROJECTION, null, null, null);
        assert c != null;
        Log.i(TAG, "Found " + c.getCount() + " local entries. Computing merge solution...");

        // Find stale data
        int id;
        String entryId;
        String title;
        String link;
        long published;
        while (c.moveToNext()) {
            syncResult.stats.numEntries++;
            id = c.getInt(COLUMN_ID);
            entryId = c.getString(COLUMN_ENTRY_ID);
            title = c.getString(COLUMN_TITLE);
            link = c.getString(COLUMN_LINK);
            published = c.getLong(COLUMN_PUBLISHED);
            FeedParser.Entry match = entryMap.get(entryId);
            if (match != null) {
                // Entry exists. Remove from entry map to prevent insert later.
                entryMap.remove(entryId);
                // Check to see if the entry needs to be updated
                Uri existingUri = FeedContract.Entry.CONTENT_URI.buildUpon()
                        .appendPath(Integer.toString(id)).build();
                if ((match.title != null && !match.title.equals(title)) ||
                        (match.link != null && !match.link.equals(link)) ||
                        (match.published != published)) {
                    // Update existing record
                    Log.i(TAG, "Scheduling update: " + existingUri);
                    batch.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(FeedContract.Entry.COLUMN_NAME_TITLE, match.title)
                            .withValue(FeedContract.Entry.COLUMN_NAME_LINK, match.link)
                            .withValue(FeedContract.Entry.COLUMN_NAME_PUBLISHED, match.published)
                            .build());
                    syncResult.stats.numUpdates++;
                } else {
                    Log.i(TAG, "No action: " + existingUri);
                }
            } else {
                // Entry doesn't exist. Remove it from the database.
                Uri deleteUri = FeedContract.Entry.CONTENT_URI.buildUpon()
                        .appendPath(Integer.toString(id)).build();
                Log.i(TAG, "Scheduling delete: " + deleteUri);
                batch.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        // Add new items
        for (FeedParser.Entry e : entryMap.values()) {
            Log.i(TAG, "Scheduling insert: entry_id=" + e.id);
            batch.add(ContentProviderOperation.newInsert(FeedContract.Entry.CONTENT_URI)
                    .withValue(FeedContract.Entry.COLUMN_NAME_ENTRY_ID, e.id)
                    .withValue(FeedContract.Entry.COLUMN_NAME_TITLE, e.title)
                    .withValue(FeedContract.Entry.COLUMN_NAME_LINK, e.link)
                    .withValue(FeedContract.Entry.COLUMN_NAME_PUBLISHED, e.published)
                    .build());
            syncResult.stats.numInserts++;
        }
        Log.i(TAG, "Merge solution ready. Applying batch update");
        mContentResolver.applyBatch(FeedContract.CONTENT_AUTHORITY, batch);
        mContentResolver.notifyChange(
                FeedContract.Entry.CONTENT_URI, // URI where data was modified
                null,                           // No local observer
                false);                         // IMPORTANT: Do not sync to network
        // This sample doesn't support uploads, but if *your* code does, make sure you set
        // syncToNetwork=false in the line above to prevent duplicate syncs.
    }

    /**
     * Given a string representation of a URL, sets up a connection and gets an input stream.
     */
    private InputStream downloadUrl(final URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(NET_READ_TIMEOUT_MILLIS /* milliseconds */);
        conn.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}
