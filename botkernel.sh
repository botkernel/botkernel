#!/bin/bash
#
# Start the botkernel server.
#
# Also can be used to get service status and PID. 
# Can be used to force a shutdown. Though this is not preferred and
# instead shutdown should happen through a message sent to the admin bot.
#

#
# Requested operation
#
OP=$1

if [ -z "$OP" ]; then
    echo "Usage: $0 <start|stop|status>"
    exit 1
fi

#
# Source environment
#
. env.sh

#
# Find process id of a running bot kernel, if any.
#
PID=`ps -ef | grep com.jreddit.botkernel.BotKernel | grep -v grep | awk '{print $2}'`


LOGDIR=logs
LOG=$LOGDIR/botkernel.out

#
# Make sure our log dir exists
#
if [ ! -d "$LOGDIR" ]; then
    mkdir $LOGDIR
fi

case "$OP" in

    start) 
            if [ -z "$PID" ]; then
                # Start service
                echo "Starting service..."
                nohup java com.jreddit.botkernel.BotKernel > $LOG 2>&1 &
            else
                echo "BotKernel already running. PID $PID"
            fi
        ;;
    stop)
            if [ -z "$PID" ]; then
                echo "No running BotKernel service to stop."
            else
                echo "Stopping service..."
                kill -KILL $PID
            fi
        ;;
    status)
            if [ -z "$PID" ]; then 
                echo "No running BotKernel service found."
            else
                echo "BotKernel running $PID"
            fi
        ;;
esac



