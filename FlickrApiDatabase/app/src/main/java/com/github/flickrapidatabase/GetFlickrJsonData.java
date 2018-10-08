package com.github.flickrapidatabase;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class GetFlickrJsonData extends AsyncTask<String, Void, List<Photo>> implements GetRawData.OnDownloadComplete {
    private static final String TAG = "GetFlickrJsonData";

    private List<Photo> mPhotoList;
    private String mBaseURL;
    private String mLanguage;
    private boolean mMatchAll;

    private final OnDataAvailable mCallback;
    private boolean runningOnSameThread;

    interface OnDataAvailable {
        void onDataAvailable(List<Photo> data, DownloadStatus status);
    }

    public GetFlickrJsonData(String BaseURL, String Language, boolean MatchAll, OnDataAvailable Callback) {
        Log.d(TAG, "GetFlickrJsonData: constructor run");
        this.mBaseURL = BaseURL;
        this.mLanguage = Language;
        this.mMatchAll = MatchAll;
        this.mCallback = Callback;
        this.mPhotoList = null;
        runningOnSameThread = false;
    }

    void executeOnSameThread(String searchCriteria){
        Log.d(TAG, "executeOnSameThread starts");
        runningOnSameThread = true;
        String destinationUri = createUri(searchCriteria, mLanguage, mMatchAll);

        GetRawData getRawData = new GetRawData(this);
        getRawData.execute(destinationUri);
        Log.d(TAG, "executeOnSameThread ends");
    }

    @Override
    protected void onPostExecute(List<Photo> photos) {
        Log.d(TAG, "onPostExecute: starts");

        if(mCallback != null){
            mCallback.onDataAvailable(mPhotoList, DownloadStatus.OK);
        }
    }

    @Override
    protected List<Photo> doInBackground(String... params) {
        Log.d(TAG, "doInBackground starts");
        String destinationUri = createUri(params[0], mLanguage, mMatchAll);

        GetRawData getRawData = new GetRawData(this);
        getRawData.runInSameThread(destinationUri);
        Log.d(TAG, "doInBackground ends");
        return mPhotoList;
    }

    private String createUri(String searchCriteria, String lang, boolean matchAll ){
        Log.d(TAG, "createUri starts");

        return Uri.parse(mBaseURL).buildUpon().appendQueryParameter("tags", searchCriteria)
                                              .appendQueryParameter("tagmode", matchAll ? "ALL" : "ANY")
                                              .appendQueryParameter("lang", lang)
                                              .appendQueryParameter("format", "json")
                                              .appendQueryParameter("nojsoncallback", "1")
                                              .build().toString();
    }

    @Override
    public void onDownloadComplete(String data, DownloadStatus status) {
        Log.d(TAG, "onDownloadComplete() starts. Status " + status);

        if(status == DownloadStatus.OK){
            mPhotoList = new ArrayList<>();

            try{
                JSONObject jsonData = new JSONObject(data);
                JSONArray jsonArray = jsonData.getJSONArray("items");

                for(int i=0; i< jsonArray.length(); i++){
                    JSONObject jsonPhoto = jsonArray.getJSONObject(i);
                    String title = jsonPhoto.getString("title");
                    String author = jsonPhoto.getString("author");
                    String authorId = jsonPhoto.getString("author_id");
                    String tags = jsonPhoto.getString("tags");

                    JSONObject media = jsonPhoto.getJSONObject("media");
                    String photoUrl = media.getString("m");

                    String link = photoUrl.replaceFirst("_m.", "_b.");

                    Photo photoObject = new Photo(title, author, authorId, link, tags, photoUrl);
                    mPhotoList.add(photoObject);

                    Log.d(TAG, "onDownloadComplete: " + photoObject.toString());
                }
            } catch (JSONException e){
                e.printStackTrace();
                Log.e(TAG, "onDownloadComplete: Error JSON data processing" + e.getMessage() );
                status = DownloadStatus.FAILED_OR_EMPTY;
            }

        }

        if(runningOnSameThread && mCallback != null){
            // now inform the caller that processing is done
            mCallback.onDataAvailable(mPhotoList, status);
        }

        Log.d(TAG, "onDownloadComplete: ends");
    }
}
