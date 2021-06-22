package com.example.fetchrewards;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView lv;

    int id, listId;
    String name;

    private static String JSON_URL = "https://fetch-hiring.s3.amazonaws.com/hiring.json";

    ArrayList<HashMap<String,String>> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataList = new ArrayList<>();
        lv = findViewById(R.id.listView);

        GetData getData = new GetData();
        getData.execute();

    }

    public class GetData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String current = "";

            try{
                URL url;
                HttpURLConnection urlConnection = null;

                try{
                    url = new URL(JSON_URL);
                    urlConnection = (HttpURLConnection) url.openConnection();

                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(in);

                    int data = isr.read();
                    while(data != -1){
                        current += (char) data;
                        data = isr.read();
                    }

                    return current;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    if(urlConnection != null){
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }

            return current;
        }

        @Override
        protected void onPostExecute(String s){

            try{
                JSONArray jsonArray = new JSONArray(s);

                ArrayList<dataStore> ar1 = new ArrayList();
                ArrayList<dataStore> ar2 = new ArrayList();
                ArrayList<dataStore> ar3 = new ArrayList();
                ArrayList<dataStore> ar4 = new ArrayList();

                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    id = jsonObject.getInt("id");
                    listId = jsonObject.getInt("listId");
                    name = jsonObject.getString("name");

                    if(name.equals("null") || name.equals("")){

                    } else {
                        switch(listId){
                            case 1:
                                ar1.add(new dataStore(id,listId,name));
                                break;
                            case 2:
                                ar2.add(new dataStore(id,listId,name));
                                break;
                            case 3:
                                ar3.add(new dataStore(id,listId,name));
                                break;
                            case 4:
                                ar4.add(new dataStore(id,listId,name));
                                break;
                            default:
                                break;
                        }
                    }

                }

                Collections.sort(ar1);
                Collections.sort(ar2);
                Collections.sort(ar3);
                Collections.sort(ar4);

                convertToHash(ar1,dataList);
                convertToHash(ar2,dataList);
                convertToHash(ar3,dataList);
                convertToHash(ar4,dataList);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this,
                    dataList,
                    R.layout.row_layout,
                    new String[] {"id","listId", "name"},
                    new int[]{R.id.textView, R.id.textView2, R.id.textView3}
            );
            lv.setAdapter(adapter);
        }
    }

    public void convertToHash(ArrayList<dataStore> a, ArrayList<HashMap<String, String>> map){


        for(int i = 0; i < a.size(); ++i){
            HashMap<String, String> hash = new HashMap<>();

            hash.put("id", Integer.toString(a.get(i).getId()));
            hash.put("listId", Integer.toString(a.get(i).getListId()));
            hash.put("name", a.get(i).getName());

            map.add(hash);
        }

    }
}

class dataStore implements Comparable<dataStore>{
    int id;
    int listId;
    String name;

    public dataStore() {

    }

    public dataStore(int i, int li, String n) {
        this.id = i;
        this.listId = li;
        this.name = n;
    }

    public int getId(){
        return this.id;
    }

    public int getListId(){
        return this.listId;
    }

    public String getName(){
        return this.name;
    }

    public void setId(int i){
        this.id = i;
    }

    public void setListId(int li){
        this.listId = li;
    }

    public void setName(String n){
        this.name = n;
    }

    @Override
    public int compareTo(dataStore o) {
        return this.getName().compareTo(o.getName());
    }
}