#!/bin/bash

RUNTIME_FILE="runtime_report.txt"
MEMORY_FILE="memory_report.txt"

if [ -e $RUNTIME_FILE ]
then
  rm -f $RUNTIME_FILE
fi

if [ -e $MEMORY_FILE ]
then
  rm -f $MEMORY_FILE
fi

GRAPH_SIZE=300
UNIT_TEST="IntrospectorFilterTest#lieberherr_one_instance"
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=1" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=2" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=4" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=6" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=8" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=10" -Dtest="$UNIT_TEST" test