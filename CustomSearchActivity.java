package com.houndify.CalcTipApp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;
import com.hound.android.sdk.VoiceSearch;
import com.hound.android.sdk.VoiceSearchInfo;
import com.hound.android.sdk.VoiceSearchListener;
import com.hound.android.sdk.VoiceSearchState;
import com.hound.android.sdk.audio.SimpleAudioByteStreamSource;
import com.hound.android.sdk.util.HoundRequestInfoFactory;
import com.hound.core.model.sdk.HoundRequestInfo;
import com.hound.core.model.sdk.HoundResponse;
import com.hound.core.model.sdk.PartialTranscript;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

/**
 * This sample demonstrates building a custom application UI and calling the Houndify API
 * directly.
 */
public class CustomSearchActivity extends Activity {

    private TextView textView;
    private Button button;

    private VoiceSearch voiceSearch;

    private LocationManager locationManager;

    private JsonNode lastConversationState;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_basic_search);

        textView = (TextView)findViewById(R.id.textView);
        button = (Button)findViewById(R.id.button);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        /** Setup a click listener for the search button to trigger the Voice Search */
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // No VoiceSearch is active, start one.
                if (voiceSearch == null) {
                    resetUIState();
                    startSearch();
                }
                // Else stop the current search
                else {
                    // voice search has already started.
                    if (voiceSearch.getState() == VoiceSearchState.STATE_STARTED) {
                        voiceSearch.stopRecording();
                    } else {
                        voiceSearch.abort();
                    }

                }
            }
        });
    }

    /**
     * Override the Activity's onStop() method to abort the search if the
     * user leaves the Activity while searching.
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (voiceSearch != null) {
            voiceSearch.abort();
        }
    }


    /**
     * Helper method called from the startSearch() method below to fill out user information
     * needed in the HoundRequestInfo query object sent to the Hound server.
     *
     * @return
     */
    private HoundRequestInfo getHoundRequestInfo() {
        final HoundRequestInfo requestInfo = HoundRequestInfoFactory.getDefault(this);

        // Client App is responsible for providing a UserId for their users which is meaningful to the client.
        requestInfo.setUserId("User ID");
        // Each request must provide a unique request ID.
        requestInfo.setRequestId(UUID.randomUUID().toString());
        // Providing the user's location is useful for geographic queries, such as, "Show me restaurants near me".
        setLocation( requestInfo, locationManager.getLastKnownLocation( LocationManager.PASSIVE_PROVIDER ));

        // for the first search lastConversationState will be null, this is okay.  However any future
        // searches may return us a conversation state to use.  Add it to the request info when we have one.
        requestInfo.setConversationState( lastConversationState );

        return requestInfo;
    }


    /**
     * Helper function for filling the user's location info into the query.
     *
     * @param requestInfo
     * @param location
     */
    public static void setLocation(final HoundRequestInfo requestInfo, final Location location) {
        if (location != null) {
            requestInfo.setLatitude(location.getLatitude());
            requestInfo.setLongitude(location.getLongitude());
            requestInfo.setPositionHorizontalAccuracy((double) location.getAccuracy());
        }
    }

    /**
     * Method used to start the VoiceSearch. Called from the Search Button handler.
     */
    private void startSearch() {
        if (voiceSearch != null) {
            return; // We are already searching
        }

        setProgressBarIndeterminateVisibility(true);

        /**
         * Example of using the VoiceSearch.Builder to configure a VoiceSearch object
         * which is then use to run the voice search.
         */
        voiceSearch = new VoiceSearch.Builder()
                .setRequestInfo( getHoundRequestInfo() )
                .setAudioSource( new SimpleAudioByteStreamSource() )
                .setClientId( Constants.CLIENT_ID )
                .setClientKey( Constants.CLIENT_KEY )
                .setListener( voiceListener )
                .build();

        textView.setText("Listening...");
        // Toggle the text on our search button to indicate pressing it now will abort the search.
        button.setText("Stop Recording");

        // Kickoff the search. This will start listening from the microphone and streaming
        // the audio to the Hound server, at the same time, waiting for a response which will be passed
        // back as a result to the voiceListener registered above.
        voiceSearch.start();
    }

    private void resetUIState() {
        setProgressBarIndeterminateVisibility(false);
        button.setEnabled(true);
        button.setText("Search");
    }


    /**
     * Implementation of the VoiceSearchListener interface used for receiving search state information
     * and the final search results.
     */
    private final VoiceSearchListener voiceListener = new VoiceSearchListener() {

        /**
         * Called every time a new partial transcription is received from the Hound server.
         * This is used for providing feedback to the user of the server's interpretation of their query.
         *
         * @param transcript
         */
        @Override
        public void onTranscriptionUpdate(final PartialTranscript transcript) {
            final StringBuilder str = new StringBuilder();
            switch (voiceSearch.getState()) {
                case STATE_STARTED:
                    str.append("Listening...");
                    break;
                case STATE_SEARCHING:
                    str.append("Receiving...");
                    break;
                default:
                    str.append("Unknown");
                    break;
            }
            str.append("\n\n");
            str.append(transcript.getPartialTranscript());

            textView.setText(str.toString());
        }

        @Override
        public void onResponse(final HoundResponse response, final VoiceSearchInfo info) {
            voiceSearch = null;
            resetUIState();

            if (!response.getResults().isEmpty()) {
                // Save off the conversation state.  This information will be returned to the server
                // in the next search. Note that at some point in the future the results CommandResult list
                // may contain more than one item. For now it does not, so just grab the first result's
                // conversation state and use it.
                lastConversationState = response.getResults().get(0).getConversationState();
            }

            textView.setText("Received response...displaying the JSON");

            // We put pretty printing JSON on a separate thread as the server JSON can be quite large and will stutter the UI

            // Not meant to be configuration change proof, this is just a demo
            //**new Thread(new Runnable() {
               //** @Override
                //**public void run() {
                    String message;
                    try {
                        JSONArray allResultsArray = new JSONObject(info.getContentBody()).getJSONArray("AllResults");
                        JSONObject firstObject = allResultsArray.getJSONObject(0);
                        JSONObject nativeData = firstObject.getJSONObject("NativeData");
                        JSONObject tipCalcInputData = nativeData.getJSONObject("TipCalculatorInputData");
                        JSONObject billAmountJSON = tipCalcInputData.getJSONObject("BillAmount");
                        int billAmount = billAmountJSON.getInt("Amount");
                        Integer i = billAmount;
                        //**** TODO: pass "billAmount:" to the CalcTip class ***
                        message = "Response\n\n" + i.toString();
                        textView.setText("Bad JSON\n\n$" + i.toString());
                        Intent intent =
                                new Intent(CustomSearchActivity.this, CalcTip.class);
                        intent.putExtra("doubleBillAmt", billAmount);
                        startActivity(intent);
                    }
                    catch (final JSONException ex) {
                        textView.setText("Bad JSON\n\n" + response);
                        message = "Bad JSON\n\n" + response;
                    }

                    final String finalMessage = message;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(finalMessage);
                        }
                    });
               //** }
           //** }).start();
        }

        /**
         * Called if the search fails do to some kind of error situation.
         *
         * @param ex
         * @param info
         */
        @Override
        public void onError(final Exception ex, final VoiceSearchInfo info) {
            voiceSearch = null;
            resetUIState();
            textView.setText(exceptionToString(ex));
        }

        /**
         * Called when the recording phase is completed.
         */
        @Override
        public void onRecordingStopped() {
            button.setText("Receiving");
            textView.setText("Receiving...");
        }

        /**
         * Called if the user aborted the search.
         *
         * @param info
         */
        @Override
        public void onAbort(final VoiceSearchInfo info) {
            voiceSearch = null;
            resetUIState();
            textView.setText("Aborted");
        }
    };

    /**
     * Helper method for converting an Exception to a String
     * with stack trace info.
     *
     * @param ex
     * @return
     */
    private static String exceptionToString(final Exception ex) {
        try {
            final StringWriter sw = new StringWriter(1024);
            final PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            pw.close();
            return sw.toString();
        }
        catch (final Exception e) {
            return "";
        }
    }
}
