#!/bin/bash
set -euo pipefail

cd /tmp/

rm -Rf demo*
curl -v -O -J -L "http://localhost:9080/api/project?supportedServer=LIBERTY&mpVersion=MP30&javaSEVersion=SE12&selectedSpecs=MP_CONFIG&selectedSpecs=TESTS&selectedFeatures=POSTGRES"
#curl -v -O -J -L "http://localhost:9080/api/project?supportedServer=LIBERTY&mpVersion=MP30&javaSEVersion=SE12&selectedSpecs=MP_CONFIG&selectedSpecs=TESTS&selectedFeatures=KAFKA"
#curl -v -O -J -L "http://localhost:9080/api/project?supportedServer=LIBERTY&mpVersion=MP30&javaSEVersion=SE12&selectedSpecs=MP_CONFIG&selectedFeatures=POSTGRES"
#curl -v -O -J -L "http://localhost:9080/api/project?supportedServer=LIBERTY&mpVersion=MP30&javaSEVersion=SE12&selectedSpecs=MP_CONFIG"
unzip demo.zip
tree demo/
