package mobsec22.group25.wifiscanner.vo;

import android.location.Location;
import android.net.wifi.ScanResult;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

// wrapper class to encapsulate location data and scan result data
public class LocationScanResult implements Parcelable {
    private LatLng location;
    private ScanResult wifiScanResult;

    public LocationScanResult(Location location, ScanResult scanResult) {
        this.location = location != null ? new LatLng(location.getLatitude(), location.getLongitude()) : null;
        this.wifiScanResult = scanResult;
    }

    protected LocationScanResult(Parcel in) {
        location = in.readParcelable(LatLng.class.getClassLoader());
        wifiScanResult = in.readParcelable(ScanResult.class.getClassLoader());
    }

    public static final Creator<LocationScanResult> CREATOR = new Creator<LocationScanResult>() {
        @Override
        public LocationScanResult createFromParcel(Parcel in) {
            return new LocationScanResult(in);
        }

        @Override
        public LocationScanResult[] newArray(int size) {
            return new LocationScanResult[size];
        }
    };

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = new LatLng(location.getLatitude(), location.getLongitude());
    }

    public ScanResult getWifiScanResult() {
        return wifiScanResult;
    }

    public void setWifiScanResult(ScanResult wifiScanResult) {
        this.wifiScanResult = wifiScanResult;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(location, flags);
        dest.writeParcelable(wifiScanResult, flags);
    }
}
