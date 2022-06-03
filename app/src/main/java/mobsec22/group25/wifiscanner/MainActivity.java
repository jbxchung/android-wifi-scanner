package mobsec22.group25.wifiscanner;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import mobsec22.group25.wifiscanner.util.Constants;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getCanonicalName();
    // group 25 let's go!
    private static final Integer MAIN_PERMISSION_REQUEST_CODE = 25;

    private Context appContext;
    private WifiManager wifiManager;

    Gson gson = new Gson();

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appContext = this.getApplicationContext();
        this.wifiManager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MAIN_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && Arrays.stream(grantResults).allMatch(r -> r == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(MainActivity.this, "Required permissions granted! You may now initiate a scan.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Please allow required permission to scan for wifi networks.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void startWifiScan(View view) {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED)) {
            Log.d(LOG_TAG, "Requesting permissions");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE},
                    MAIN_PERMISSION_REQUEST_CODE);

            return;
        } else {
            Log.d(LOG_TAG, "Required permissions granted, proceeding with scan");
        }

        List<ScanResult> scanResults = wifiManager.getScanResults();
        Log.d(LOG_TAG, scanResults.toString());

        this.writeScanResultsToFile(scanResults);

        // launch activity of scan results
        Intent i = new Intent(this.appContext, ScanResultDetailHostActivity.class);
        i.putParcelableArrayListExtra(Constants.INTENT_EXTRA_SCAN_RESULTS, new ArrayList<>(scanResults));
        startActivity(i);
    }

    private void writeScanResultsToFile(List<ScanResult> newScanResults) {
        Log.d(LOG_TAG, "Writing test file");
        try {
            File path = appContext.getExternalFilesDir(null);
            File targetFile = new File(path, Constants.FILENAME_SCAN_RESULTS);

            // get existing data
            Map<String, ScanResult> existingScanResults = this.getPersistedScanResults();
            if (existingScanResults == null) {
                existingScanResults = new HashMap<>();
            }

            // store as map where key is BSSID
            Map<String, ScanResult> newScanResultMap = newScanResults.stream().collect(Collectors.toMap(sr -> sr.BSSID, Function.identity()));

            // append to existing data
            Map<String, ScanResult> allResults = new HashMap<>();
            allResults.putAll(existingScanResults);
            allResults.putAll(newScanResultMap);

            // get it all as a JSON string
            String fileContent = gson.toJson(allResults);

            // overwrite
            FileOutputStream outputStream = new FileOutputStream(targetFile);
            outputStream.write(fileContent.getBytes(StandardCharsets.UTF_8));
            outputStream.close();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error writing scan results to file");
            e.printStackTrace();
        }
    }

    private Map<String, ScanResult> getPersistedScanResults() {
        String savedDataRaw = null;

        try {
            File path = appContext.getExternalFilesDir(null);
            File savedScanResultsFile = new File(path, Constants.FILENAME_SCAN_RESULTS);


            FileInputStream fileInputStream = new FileInputStream(savedScanResultsFile);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            StringBuilder savedDataBuilder = new StringBuilder();

            String readString = reader.readLine();
            while (readString != null) {
                savedDataBuilder.append(readString);
                readString = reader.readLine();
            }

            inputStreamReader.close();

            savedDataRaw = savedDataBuilder.toString();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error reading existing scan results from file: " + e.toString());
        }

        Map<String, ScanResult> result = null;
        if (savedDataRaw != null) {
            try {
                result = gson.fromJson(savedDataRaw, Map.class);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error converting raw saved data to object: " + e.toString());
            }
        }

        return result;
    }
}