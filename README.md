Ditu - search-based Geocoder service built on top of [SenseiDB](http://senseidb.com)
=======================================================================

### Overview

Ditu (地图) means map in Chinese. It is a simple search-based geocoder service built on top of [SenseiDB](http://senseidb.com).

### Build

1. Download the data from [OpenStreeMap](http://wiki.openstreetmap.org/wiki/Planet.osm). Make sure you download the PBF version instead of XML, as it is the most compact and fastest to decode/read. It is the format Ditu supports.

2. We are using the OpenStreetMap data and parsing library. The OSM library (called [Osmosis](http://wiki.openstreetmap.org/wiki/Osmosis)) is not yet mavenized, we first need to install it to the local maven repo using the provided script:

   ** ./bin/mvn-install.sh **

3. Once installed, we are now able to build it:

   ** mvn package **

   Note that the output library will be copied and installed to conf/ext directory

4. Make sure you are pointing to the right data file by editing the file conf/sensei.properties, i.e. setting the location for the property:

   ** sensei.gateway.file = my-data-file.pbf **

5. Download a [SenseiDB distribution](https://github.com/senseidb/sensei/downloads) (this has been tested with SenseiDB version 1.5.0)

### Run

1. Start a SenseiDB node (from the sensei distribution top-level directory):

   ** ./bin/start-sensei-node.sh DITU-DIR/conf **

2. Look at the log:

   ** tail -f logs/sensei-main.log **

### Try it:

1. open either the sensei shell or the [web console](http://localhost:8080)

   ** ./bin/sensei-shell.sh **

2. try the BQL query (retrieving places match "san francisco" sorted in decreasing order by population):

   ** SELECT _uid where query is "san francisco" order by population  desc FETCHING STORED LIMIT 0, 10 ** 
