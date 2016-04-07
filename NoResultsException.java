package com.houndify.CalcTipApp;

/**
 * Created by anne on 4/6/16.
 */
public class NoResultsException  extends Exception {
    private String transcription;

    public NoResultsException (String s) {
        transcription = s;
    }
    public String getTranscription () {
        return transcription;
    }
}
