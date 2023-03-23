#!/bin/bash

ARMADILLO_SETUP_VER=1.0.1
ARMADILLO_URL=https://github.com/molgenis/molgenis-service-armadillo/
ARMADILLO_PROFILE=default
ARMADILLO_PATH=/usr/share/armadillo
ARMADILLO_CFG_PATH=/etc/armadillo
ARMADILLO_SYS_USER=armadillo
ARMADILLO_LOG_PATH=/var/log/armadillo
ARMADILLO_AUDITLOG=$ARMADILLO_LOG_PATH/audit.log
ARMADILLO_DATADIR=$ARMADILLO_PATH/data

check_armadillo_update() {

  FILE=$(readlink "$ARMADILLO_PATH/application/armadillo.jar")
  VERSION_USED=$(echo $FILE | sed s/.*armadillo-// | sed s/.jar//)

  LATEST_RELEASE=$(curl -L -s -H 'Accept: application/json' -s $ARMADILLO_URL/releases/latest)
  #ARMADILLO_VERSION=$(echo "$LATEST_RELEASE" | sed -e 's/.*"tag_name":"\([^"]*\)".*/\1/')
  ARMADILLO_LATEST_VERSION=$(echo "$LATEST_RELEASE" | sed -e 's/.*"tag_name":"\([^"]*\)".*/\1/' | sed -e s/.*armadillo-service-//)



  echo $ARMADILLO_LATEST_VERSION vs $VERSION_USED
  

  if [[ "$ARMADILLO_LATEST_VERSION" =~ "2.2.3" ]]; then
  {
    
    echo "Armadillo 2 version not supported on this system"
    exit
  }
  fi
  
  if [ "$VERSION_USED" != "$ARMADILLO_LATEST_VERSION" ]; then
  {
    echo "HIER";
    DL_URL=https://github.com/molgenis/molgenis-service-armadillo/releases/download/armadillo-service-$ARMADILLO_LATEST_VERSION/armadillo-$ARMADILLO_LATEST_VERSION.jar
    echo $DL_URL
  

    wget -q -O $ARMADILLO_PATH/application/armadillo-"$ARMADILLO_LATEST_VERSION".jar "$DL_URL"

    ln -s -f $ARMADILLO_PATH/application/armadillo-"$ARMADILLO_LATEST_VERSION".jar $ARMADILLO_PATH/application/armadillo.jar
    echo "$ARMADILLO_LATEST_VERSION updated"

  }
  else
  {
    echo "No armadillo updates found"
  }
  fi



  #echo $VER

}


check_armadillo_updates