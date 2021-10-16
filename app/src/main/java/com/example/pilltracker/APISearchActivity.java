package com.example.pilltracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APISearchActivity extends AppCompatActivity {

    ImageButton medSearchButton, medClearButton;
    SearchView medSearchBar;
    TextView mName, mPurpose, mDrugType, mRoute, mSubstanceName, mIndication, mDosage, mWhenUsing, mAskDoctor, mActiveIng, mStorage;
    ProgressBar progressBar;
    MaterialCardView infoCard;

    private static final String TAG = "APISearchActivity";

    String uri;
    String purpose;
    String indications_and_usage;
    String dosage_and_administration;
    String when_using;
    String ask_doctor;
    String active_ingredient;
    String storage_and_handling;

    String route;
    String substance_name;
    String product_type;
    String generic_name;

    boolean flag = false;
    private static final String BASE_URI = "https://api.fda.gov/drug/label.json?search=openfda.generic_name:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apisearch);

        medSearchButton = findViewById(R.id.medSearchButton);
        medClearButton = findViewById(R.id.medClearButton);
        medSearchBar = findViewById(R.id.medSearchBar);
        infoCard = findViewById(R.id.infoCard);

        mName = findViewById(R.id.mName);
        mPurpose = findViewById(R.id.mPurpose);
        mDrugType = findViewById(R.id.mDrugType);
        mRoute = findViewById(R.id.mRoute);
        mSubstanceName = findViewById(R.id.mSubstanceName);
        mIndication = findViewById(R.id.mIndication);
        mDosage = findViewById(R.id.mDosage);
        mWhenUsing = findViewById(R.id.mWhenUsing);
        mAskDoctor = findViewById(R.id.mAskDoctor);
        mActiveIng = findViewById(R.id.mActiveIng);
        mStorage = findViewById(R.id.mStorage);

        progressBar = findViewById(R.id.progressBar);

        medClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });

        medSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDrugInfo();
            }
        });

    }

    private void refresh() {
        infoCard.setVisibility(View.GONE);
        medSearchBar.setQuery("", false);
    }

    @SuppressLint("RtlHardcoded")
    private void getDrugInfo() {
        String drugName = medSearchBar.getQuery().toString();
        if(drugName.equals("")) {
            Toast.makeText(APISearchActivity.this.getApplicationContext(), "Please enter a Drug Name.", Toast.LENGTH_SHORT).show();
            return;

        } else {
            uri = BASE_URI+drugName+"&limit=1";
            Log.d(TAG,"uri: " + uri);
            new RetrieveDataTask().execute();
        }


    }

    class RetrieveDataTask extends AsyncTask<Void, Void, String> {
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            infoCard.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(uri);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                Toast.makeText(APISearchActivity.this.getApplicationContext(), "There was an error! Please Try again later.", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.GONE);
            parseJSONData(response);
        }
    }

    private void parseJSONData(String response) {
        try {
            JSONObject jObject = new JSONObject(response);
            JSONArray resArray = jObject.getJSONArray("results");
            JSONObject res = resArray.getJSONObject(0);

            purpose = JSONArrayToString(res.optJSONArray("purpose"));
            indications_and_usage = JSONArrayToString(res.optJSONArray("indications_and_usage"));
            dosage_and_administration = JSONArrayToString(res.optJSONArray("dosage_and_administration"));
            when_using = JSONArrayToString(res.optJSONArray("when_using"));
            ask_doctor = JSONArrayToString(res.optJSONArray("ask_doctor"));
            active_ingredient = JSONArrayToString(res.optJSONArray("active_ingredient"));
            storage_and_handling = JSONArrayToString(res.optJSONArray("storage_and_handling"));

            JSONObject openFDA = res.getJSONObject("openfda");
            route = JSONArrayToString(openFDA.optJSONArray("route"));
            substance_name = JSONArrayToString(openFDA.optJSONArray("substance_name"));
            product_type = JSONArrayToString(openFDA.optJSONArray("product_type"));
            generic_name = JSONArrayToString(openFDA.optJSONArray("generic_name"));

            infoCard.setVisibility(View.VISIBLE);
            mName.setText(generic_name);
            mRoute.setText(route);
            mSubstanceName.setText(substance_name);
            mDrugType.setText(product_type);
            mPurpose.setText(purpose);
            mIndication.setText(indications_and_usage);
            mDosage.setText(dosage_and_administration);
            mWhenUsing.setText(when_using);
            mAskDoctor.setText(ask_doctor);
            mActiveIng.setText(active_ingredient);
            mStorage.setText(storage_and_handling);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String JSONArrayToString(JSONArray inpArray) {
        String result = "";
        try {
            for (int i = 0; i < inpArray.length(); i++) {
                result += inpArray.get(i);
                result+=" ";
            }

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        if(inpArray == null){
            return "Not Found";
        }
        return result;
    }
}

//Examples -
//naloxone hydrochloride
//tolnaftate
//benadryl
//paracetamol
//caffine
//ibuprofen