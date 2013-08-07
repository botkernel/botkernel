#!/bin/bash
#
#

export CLASSPATH=${CLASSPATH}:../jReddit/dist/jreddit.jar
export CLASSPATH=${CLASSPATH}:../jReddit/deps/json-simple-1.1.1.jar

export CLASSPATH=${CLASSPATH}:dist/botkernel.jar

#
# Allow configurable extensions to the classpath env for loading
# bots outside of this project.
#
if [ -f scratch/env.sh ]; then
    . scratch/env.sh
fi

