package com.procodecg.codingmom.ehealth.asynctask;

/**
 * Created by idedevteam on 2/12/18.
 */

public interface AsyncResponse {
    void taskComplete(String output, String timestamp);
    void tokenRequest(String output);
    void hostDetected(Boolean output);
}
