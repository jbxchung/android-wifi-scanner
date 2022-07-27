const OUTPUT_FILE_JSON = 'combinedResults.json';
const OUTPUT_FILE_CSV = 'combinedResults.csv';

const NETWORK_CAPABILITIES = {
    WEP: 'WEP',
    WPA_PERSONAL: 'WPA Personal',
    WPA_ENTERPRISE: 'WPA Enterprise',
    WPA2_PERSONAL: 'WPA2 Personal',
    WPA2_ENTERPRISE: 'WPA2 Enterprise',
    WPA3_PERSONAL: 'WPA3 Personal',
    WPA3_ENTERPRISE: 'WPA3 Enterprise',
    OPEN: 'Open',
    WPS: 'Wi-Fi Protected Setup',
    MOBILE_HOTSPOT: 'Mobile Hotspot',
    FAST_TRANSITION: 'Fast Transition',
    ACCESS_POINT: 'Access Point',
    AD_HOC: 'Ad Hoc',
    WIFI6: 'Wi-Fi 6',
};

const AUTHENTICATION_METHODS = [
    NETWORK_CAPABILITIES.WEP,
    NETWORK_CAPABILITIES.WPA_PERSONAL,
    NETWORK_CAPABILITIES.WPA_ENTERPRISE,
    NETWORK_CAPABILITIES.WPA2_PERSONAL,
    NETWORK_CAPABILITIES.WPA2_ENTERPRISE,
    NETWORK_CAPABILITIES.WPA3_PERSONAL,
    NETWORK_CAPABILITIES.WPA3_ENTERPRISE,
];

// this mapping is manually entered with information gathered from:
// https://stackoverflow.com/questions/11956874/scanresult-capabilities-interpretation
// https://superuser.com/questions/864502/wpa-cli-what-does-wpa2-psk-ccmpess-utstarcom-imply
// https://android.stackexchange.com/questions/144492/what-are-the-sec80-and-secd00-android-wi-fi-capabilities
// https://www.cisco.com/c/en/us/products/collateral/wireless/white-paper-c11-740788.html
const CAPABILITY_MAP = {
    'WPA2-PSK-CCMP': NETWORK_CAPABILITIES.WPA2_PERSONAL,
    'RSN-PSK-CCMP': NETWORK_CAPABILITIES.WPA2_PERSONAL,
    'ESS': NETWORK_CAPABILITIES.ACCESS_POINT,
    'WPA2-EAP/SHA1-CCMP': NETWORK_CAPABILITIES.WPA2_ENTERPRISE,
    'RSN-EAP/SHA1-CCMP': NETWORK_CAPABILITIES.WPA2_ENTERPRISE,
    'WPS': NETWORK_CAPABILITIES.WPS,
    'WPA2-PSK-CCMP+TKIP': NETWORK_CAPABILITIES.WPA2_PERSONAL,
    'RSN-PSK-CCMP+TKIP': NETWORK_CAPABILITIES.WPA2_PERSONAL,
    'WPA-PSK-CCMP+TKIP': NETWORK_CAPABILITIES.WPA_PERSONAL,
    'WPA2-EAP-CCMP': NETWORK_CAPABILITIES.WPA2_ENTERPRISE,
    'WPA-EAP-CCMP+TKIP': NETWORK_CAPABILITIES.WPA_ENTERPRISE,
    'WPA2-EAP-CCMP+TKIP': NETWORK_CAPABILITIES.WPA2_ENTERPRISE,
    'RSN-SAE-CCMP': [NETWORK_CAPABILITIES.WPA3_ENTERPRISE, NETWORK_CAPABILITIES.ACCESS_POINT],
    'SEC80': NETWORK_CAPABILITIES.MOBILE_HOTSPOT,
    'SECD00': NETWORK_CAPABILITIES.MOBILE_HOTSPOT,
    'RSN-EAP-CCMP': NETWORK_CAPABILITIES.WPA2_ENTERPRISE,
    'WPA2-EAP+FT/EAP-CCMP': NETWORK_CAPABILITIES.WPA2_ENTERPRISE,
    'RSN-EAP+FT/EAP-CCMP': NETWORK_CAPABILITIES.WPA2_ENTERPRISE,
    'WPA2-EAP': NETWORK_CAPABILITIES.WPA2_ENTERPRISE,
    'RSN-EAP': NETWORK_CAPABILITIES.WPA2_ENTERPRISE,
    'WPA2-PSK+FT/PSK-CCMP': [NETWORK_CAPABILITIES.WPA2_PERSONAL, NETWORK_CAPABILITIES.FAST_TRANSITION],
    'RSN-PSK+FT/PSK-CCMP': [NETWORK_CAPABILITIES.WPA2_PERSONAL, NETWORK_CAPABILITIES.FAST_TRANSITION],
    'WPA-PSK-CCMP': NETWORK_CAPABILITIES.WPA_PERSONAL,
    'WPA-PSK-TKIP': NETWORK_CAPABILITIES.WPA_PERSONAL,
    'RSN-PSK+SAE-CCMP': NETWORK_CAPABILITIES.WPA3_PERSONAL,
    'WEP': NETWORK_CAPABILITIES.WEP,
    'WPA2-PSK+FT/PSK-CCMP+TKIP': [NETWORK_CAPABILITIES.WPA2_PERSONAL, NETWORK_CAPABILITIES.FAST_TRANSITION],
    'RSN-PSK+FT/PSK-CCMP+TKIP': [NETWORK_CAPABILITIES.WPA2_PERSONAL, NETWORK_CAPABILITIES.FAST_TRANSITION],
    'WPA-PSK-TKIP+CCMP': NETWORK_CAPABILITIES.WPA_PERSONAL,
    'WPA2-PSK-TKIP+CCMP': NETWORK_CAPABILITIES.WPA2_PERSONAL,
    'RSN-PSK-TKIP+CCMP': NETWORK_CAPABILITIES.WPA2_PERSONAL,
    'WPA2-PSK+PSK-SHA256-None+CCMP': NETWORK_CAPABILITIES.WPA2_PERSONAL,
    'RSN-PSK+PSK-SHA256-None+CCMP': NETWORK_CAPABILITIES.WPA2_PERSONAL,
    'WPA2-PSK-TKIP': NETWORK_CAPABILITIES.WPA2_PERSONAL,
    'RSN-PSK-TKIP': NETWORK_CAPABILITIES.WPA2_PERSONAL,
    'IBSS': NETWORK_CAPABILITIES.AD_HOC,
    'RSN-EAP-CCMP+TKIP': NETWORK_CAPABILITIES.WPA2_ENTERPRISE,
    'WFA-HE-READY': NETWORK_CAPABILITIES.WIFI6,
};

module.exports = {
    AUTHENTICATION_METHODS,
    CAPABILITY_MAP,
    NETWORK_CAPABILITIES,
    OUTPUT_FILE_CSV,
    OUTPUT_FILE_JSON,
};
