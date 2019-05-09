# MAC Address Vendor Lookup Function for Apache Drill

This UDF looks up a vendor name for a given MAC address.  

## Data Source
This function uses the `nmap-mac-prefixes` which can be found here:
https://linuxnet.ca/ieee/oui/

Periodic updates of this data file are required.

## Usage
Usage of this function is straightforward:

```
SELECT getVendorName( <mac address> )
FROM ...
```

The function `getVendorNane()` will return the vendor name of the registered manufacturer of the  MAC address.

The function accepts MAC addresses in the following formats:

* 00:A0:C9:14:C8:29
* 00A0C914C829
* 00-A0-C9-14-C8-29

The function is case insensitive.