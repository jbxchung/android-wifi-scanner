package mobsec22.group25.wifiscanner;

import android.content.ClipData;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.HashMap;
import java.util.Map;

import mobsec22.group25.wifiscanner.databinding.FragmentScanresultDetailBinding;

/**
 * A fragment representing a single ScanResult detail screen.
 * This fragment is either contained in a {@link ScanResultListFragment}
 * in two-pane mode (on larger screen devices) or self-contained
 * on handsets.
 */
@RequiresApi(api = Build.VERSION_CODES.R)
public class ScanResultDetailFragment extends Fragment {
    private static final String LOG_TAG = ScanResultDetailFragment.class.getCanonicalName();

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The placeholder content this fragment is presenting.
     */
    private ScanResult scanResult;
    private CollapsingToolbarLayout mToolbarLayout;
    private TextView bssidTextView;
    private TextView capabilitiesTextView;
    private TextView wifiStandardTextView;

    private final Map<Integer, String> wifiStandardMap = new HashMap<Integer, String>() {{
        put(ScanResult.WIFI_STANDARD_UNKNOWN, "Unknown");
        put(ScanResult.WIFI_STANDARD_LEGACY, "Legacy");
        put(ScanResult.WIFI_STANDARD_11N, "802.11N");
        put(ScanResult.WIFI_STANDARD_11AC, "802.11AC");
        put(ScanResult.WIFI_STANDARD_11AX, "802.11AX");
        put(ScanResult.WIFI_STANDARD_11AD, "802.11AD");
    }};

    private final View.OnDragListener dragListener = (v, event) -> {
        if (event.getAction() == DragEvent.ACTION_DROP) {
            ClipData.Item clipDataItem = event.getClipData().getItemAt(0);
            // todo: figure this out
//            mItem = PlaceholderContent.ITEM_MAP.get(clipDataItem.getText().toString());
            updateContent();
        }
        return true;
    };
    private FragmentScanresultDetailBinding binding;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ScanResultDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the placeholder content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            scanResult = getArguments().getParcelable(ARG_ITEM_ID);

            Log.d(LOG_TAG, "Scan Result Detail Fragment created for scan result: " + scanResult.BSSID);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentScanresultDetailBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        mToolbarLayout = rootView.findViewById(R.id.toolbar_layout);
        bssidTextView = binding.detailBssid;
        capabilitiesTextView = binding.detailCapabilities;
        wifiStandardTextView = binding.detailWifiStandard;

        // Show the placeholder content as text in a TextView & in the toolbar if available.
        updateContent();
        rootView.setOnDragListener(dragListener);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void updateContent() {
        if (scanResult != null) {

            String wifiStandard = wifiStandardMap.get(scanResult.getWifiStandard());

            wifiStandardTextView.setText("WiFi standard: " + wifiStandard);
            bssidTextView.setText("BSSID: " + scanResult.BSSID);
            capabilitiesTextView.setText("Capabilities: " + scanResult.capabilities);

            if (mToolbarLayout != null) {
                mToolbarLayout.setTitle(scanResult.SSID);
            }
        }
    }
}