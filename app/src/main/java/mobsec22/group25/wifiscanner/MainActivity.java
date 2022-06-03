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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mobsec22.group25.wifiscanner.util.Constants;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getCanonicalName();
    // group 25 let's go!
    private static final Integer MAIN_PERMISSION_REQUEST_CODE = 25;

    private Context appContext;
    private WifiManager wifiManager;

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

        // launch activity of scan results
        Intent i = new Intent(this.appContext, ScanResultDetailHostActivity.class);
        i.putParcelableArrayListExtra(Constants.INTENT_EXTRA_SCAN_RESULTS, new ArrayList<>(scanResults));
        startActivity(i);
    }
}