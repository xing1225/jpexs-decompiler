#!/usr/bin/env bash

# This is a comment, it starts with "#".

# Uncomment following and set it to higher value if you want more memory
# You need 64 bit OS and 64 bit java to set it to higher values
# FFDEC_MEMORY=1024m

# Uncomment following when you encounter StackOverFlowErrors. 
# If the app then terminates with OutOfMemory you can experiment with lower value.
# FFDEC_STACK_SIZE=32m

# Hide VLC error output
export VLC_VERBOSE=-1
export FFDEC_JARFILENAME=ffdec.jar
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
exec $SCRIPT_DIR/run-java.sh "$@"
