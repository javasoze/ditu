#!/usr/bin/env bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

echo "installing open street map osmosis core"

mvn install:install-file -Dfile=$bin/../conf/ext/osmosis-core-0.42.jar -DgroupId=org.openstreetmap.osmosis -DartifactId=osmosis-core -Dversion=0.42 -Dpackaging=jar

echo "installing open street map osmosis pbf2"

mvn install:install-file -Dfile=$bin/../conf/ext/osmosis-pbf2-0.42.jar -DgroupId=org.openstreetmap.osmosis -DartifactId=osmosis-pbf2 -Dversion=0.42 -Dpackaging=jar

echo "installing open street map osmosis pbfmarshall"

mvn install:install-file -Dfile=$bin/../conf/ext/osmosis-pbfmarshall-0.42.jar -DgroupId=org.openstreetmap.osmosis -DartifactId=osmosis-pbfmarshall -Dversion=0.42 -Dpackaging=jar