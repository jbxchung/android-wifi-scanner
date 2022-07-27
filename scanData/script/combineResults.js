const fs = require('fs');

const constants = require('./constants');

// flatten location data to same root level object as scan result
function flattenEntries(scanResultsWithLocation) {
    Object.keys(scanResultsWithLocation).forEach(bssid => {
        scanResultsWithLocation[bssid] = {
            ...scanResultsWithLocation[bssid].location,
            ...scanResultsWithLocation[bssid].wifiScanResult,
        };
    });
    return scanResultsWithLocation;
}

// combine results from all json files in given directory
function getCombinedResults(dataDir) {
    let combinedResults = {};
    console.log('Reading files...');
    fs.readdirSync(dataDir).forEach((file) => {
        const fileContent = JSON.parse(fs.readFileSync(`${dataDir}/${file}`).toString());
        combinedResults = {...combinedResults, ...flattenEntries(fileContent)};
        
        console.log(`${file}: ${Object.keys(fileContent).length} entries`);
    });
    
    console.log(`Found ${Object.keys(combinedResults).length} unique entries`);

    // key is BSSID - used for deduplication but no longer needed since it's also in the value
    return Object.values(combinedResults);
}

function isNetworkOpen(scanResult) {
    return !constants.AUTHENTICATION_METHODS.some(authMethod => scanResult[authMethod] === true);
}


// augment results with human-readable capabilities
function interpretCapabilities(scanResults) {
    scanResults.forEach((scanResult) => {
        // create all capability fields and initialize to false
        Object.values(constants.NETWORK_CAPABILITIES).forEach(capField => {
            scanResult[capField] = false;
        });

        // if a specific capability is found, set it to true
        scanResult.capabilities.split('][').forEach((rawCap) => {
            const cap = rawCap.replace(/[\]\[]/g, '');
            let capField = constants.CAPABILITY_MAP[cap];

            if (!Array.isArray(capField)) {
                capField = [capField];
            }
            capField.forEach(cf => {
                scanResult[cf] = true;
            });
        });

        scanResult[constants.NETWORK_CAPABILITIES.OPEN] = isNetworkOpen(scanResult);
    });

    return scanResults;
}

function getCsvString(scanResults, fields) {
    // start with field names
    const rows = [fields.join(',')];
    
    // populate rows
    scanResults.forEach((result) => {
        const row = [];
        fields.forEach(field => {
            row.push(`"${result[field]}"`);
        });
        rows.push(row.join(','));
    })

    return rows.join('\n');
}

// combine and augment our data
const scanResults = interpretCapabilities(getCombinedResults('./rawData'));

// output to json
console.log(`Writing to ${constants.OUTPUT_FILE_JSON}...`);
fs.writeFileSync(`./${constants.OUTPUT_FILE_JSON}`, JSON.stringify(scanResults));

// convert to CSV - specify which fields we want to see
console.log('Starting CSV conversion...');
const columns = ['BSSID', 'SSID', 'frequency', 'latitude', 'longitude', ...Object.values(constants.NETWORK_CAPABILITIES)];
const csvString = getCsvString(scanResults, columns);

// output to csv
console.log(`Writing to ${constants.OUTPUT_FILE_CSV}...`);
fs.writeFileSync(`./${constants.OUTPUT_FILE_CSV}`, csvString);

console.log('Done!');
