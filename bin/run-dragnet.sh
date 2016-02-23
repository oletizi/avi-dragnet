#!/usr/bin/env bash

mydir=`dirname $0`
jar="${mydir}/../lib/avi-dragnet.jar"

cmd="java -jar $jar http://www.inoreader.com/stream/user/1005620749/tag/Conversations"
echo "Executing: $cmd..."
$cmd