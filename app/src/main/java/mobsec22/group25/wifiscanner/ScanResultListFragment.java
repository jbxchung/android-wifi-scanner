package mobsec22.group25.wifiscanner;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import mobsec22.group25.wifiscanner.databinding.FragmentScanresultListBinding;
import mobsec22.group25.wifiscanner.databinding.ScanresultListContentBinding;
import mobsec22.group25.wifiscanner.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Scan Results. This fragment
 * has different presentations for handset and larger screen devices. On
 * handsets, the fragment presents a list of items, which when touched,
 * lead to a {@link ScanResultDetailFragment} representing
 * item details. On larger screens, the Navigation controller presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ScanResultListFragment extends Fragment {
    private static final String LOG_TAG = ScanResultListFragment.class.getCanonicalName();

    /**
     * Method to intercept global key events in the
     * item list fragment to trigger keyboard shortcuts
     * Currently provides a toast when Ctrl + Z and Ctrl + F
     * are triggered
     */
    ViewCompat.OnUnhandledKeyEventListenerCompat unhandledKeyEventListenerCompat = (v, event) -> {
        if (event.getKeyCode() == KeyEvent.KEYCODE_Z && event.isCtrlPressed()) {
            Toast.makeText(
                    v.getContext(),
                    "Undo (Ctrl + Z) shortcut triggered",
                    Toast.LENGTH_LONG
            ).show();
            return true;
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_F && event.isCtrlPressed()) {
            Toast.makeText(
                    v.getContext(),
                    "Find (Ctrl + F) shortcut triggered",
                    Toast.LENGTH_LONG
            ).show();
            return true;
        }
        return false;
    };

    private FragmentScanresultListBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Scan Result List Fragment created");
        binding = FragmentScanresultListBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewCompat.addOnUnhandledKeyEventListener(view, unhandledKeyEventListenerCompat);

        RecyclerView recyclerView = binding.scanresultList;

        // Leaving this not using view binding as it relies on if the view is visible the current
        // layout configuration (layout, layout-sw600dp)
        View itemDetailFragmentContainer = view.findViewById(R.id.scanresult_detail_nav_container);

        setupRecyclerView(recyclerView, itemDetailFragmentContainer);
    }

    private void setupRecyclerView(
            RecyclerView recyclerView,
            View itemDetailFragmentContainer
    ) {
        // get scan results passed into this fragment's parent activity via intent
        // we can't use getArguments because this fragment is rendered from XML layout
        // (as opposed to programmatically, where we could put arguments
        ArrayList<ScanResult> scanResults = getActivity().getIntent().getParcelableArrayListExtra(Constants.INTENT_EXTRA_SCAN_RESULTS);
        if (scanResults != null) {
            Log.d(LOG_TAG, scanResults.toString());

            recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(
                    scanResults,
                    itemDetailFragmentContainer
            ));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<ScanResult> mValues;
        private final View mItemDetailFragmentContainer;

        SimpleItemRecyclerViewAdapter(List<ScanResult> items,
                                      View itemDetailFragmentContainer) {
            mValues = items;
            mItemDetailFragmentContainer = itemDetailFragmentContainer;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            ScanresultListContentBinding binding =
                    ScanresultListContentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);

        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            // MAC address should be unique
            holder.mBSSIDView.setText(mValues.get(position).BSSID);
            String ssid = mValues.get(position).SSID;
            if (ssid.isEmpty()) {
                ssid = "[Hidden SSID]";
            }
            holder.mSSIDView.setText(ssid);
            holder.mContentView.setText(mValues.get(position).capabilities);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(itemView -> {
                ScanResult item = (ScanResult) itemView.getTag();
                Bundle arguments = new Bundle();
                // pass entire ScanResult object into detail fragment
                arguments.putParcelable(ScanResultDetailFragment.ARG_ITEM_ID, item);
                if (mItemDetailFragmentContainer != null) {
                    Navigation.findNavController(mItemDetailFragmentContainer)
                            .navigate(R.id.fragment_scanresult_detail, arguments);
                } else {
                    Navigation.findNavController(itemView).navigate(R.id.show_scanresult_detail, arguments);
                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                /*
                 * Context click listener to handle Right click events
                 * from mice and trackpad input to provide a more native
                 * experience on larger screen devices
                 */
                holder.itemView.setOnContextClickListener(v -> {
                    ScanResult item = (ScanResult) holder.itemView.getTag();
                    Toast.makeText(
                            holder.itemView.getContext(),
                            "Context click of item " + item.BSSID,
                            Toast.LENGTH_LONG
                    ).show();
                    return true;
                });
            }
            holder.itemView.setOnLongClickListener(v -> {
                // Setting the item id as the clip data so that the drop target is able to
                // identify the id of the content
                ClipData.Item clipItem = new ClipData.Item(mValues.get(position).BSSID);
                ClipData dragData = new ClipData(
                        ((ScanResult) v.getTag()).BSSID,
                        new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                        clipItem
                );

                if (Build.VERSION.SDK_INT >= 24) {
                    v.startDragAndDrop(
                            dragData,
                            new View.DragShadowBuilder(v),
                            null,
                            0
                    );
                } else {
                    v.startDrag(
                            dragData,
                            new View.DragShadowBuilder(v),
                            null,
                            0
                    );
                }
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mBSSIDView;
            final TextView mSSIDView;
            final TextView mContentView;

            ViewHolder(ScanresultListContentBinding binding) {
                super(binding.getRoot());
                mBSSIDView = binding.BSSID;
                mSSIDView = binding.SSID;
                mContentView = binding.content;
            }
        }
    }
}