package com.find_atm.android.atmfinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadURL {

    public String readUrl(String myUrl) throws IOException
    {
        String data = "";
        HttpURLConnection urlConnection;

        URL url = new URL(myUrl);
        urlConnection=(HttpURLConnection) url.openConnection();
        urlConnection.connect();

        try (InputStream inputStream = urlConnection.getInputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        Log.d("DownloadURL","Returning data= "+data);

        return data;
    }
}
