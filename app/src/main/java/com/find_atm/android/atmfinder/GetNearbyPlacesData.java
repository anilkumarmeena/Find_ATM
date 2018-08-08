package com.find_atm.android.atmfinder;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    private String googlePlacesData;
    private GoogleMap mMap;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected String doInBackground(Object... objects){
        mMap = (GoogleMap)objects[0];
        String url = (String) objects[1];

        DownloadURL downloadURL = new DownloadURL();
        try {
            googlePlacesData = downloadURL.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s){

        List<HashMap<String, String>> nearbyPlaceList;
        DataParser parser = new DataParser();
        nearbyPlaceList = parser.parse(s);
        Log.d("nearbyplacesdata","called parse method");
        showNearbyPlaces(nearbyPlaceList);
    }

    private void showNearbyPlaces(final List<HashMap<String, String>> nearbyPlaceList)
    {
        for(int i = 0; i < nearbyPlaceList.size(); i++)
        {
            final MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlaceList.get(i);
            final String id=googlePlace.get("id");
            final String placeName = googlePlace.get("place_name");
            final String vicinity = googlePlace.get("vicinity");
            final double lat = Double.parseDouble( googlePlace.get("lat"));
            final double lng = Double.parseDouble( googlePlace.get("lng"));
            Log.d("place ki id::::::::::",id);

            //database manage money or not
            db.collection(id).document("user").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.red));
                            } else {
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.green));
                            }
                        }
                    LatLng latLng = new LatLng(lat, lng);
                    markerOptions.position(latLng);
                    markerOptions.title(placeName + " : " + vicinity);
                    markerOptions.snippet(id);
                    Log.d("give me place baby:::::", placeName + " : " + vicinity);

                    mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                }
            });


        }
    }

}
