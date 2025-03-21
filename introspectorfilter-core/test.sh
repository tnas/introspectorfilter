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

mvn install:install-file -Dfile=dj.jar -DgroupId=edu.neu.ccs.demeter -DartifactId=dj -Dversion=0.8.6 -Dpackaging=jar

GRAPH_SIZE=300

UNIT_TEST="DemeterDJTest#found_one_instance"
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=1" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=1" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=1" -Dtest="$UNIT_TEST" test

UNIT_TEST="IntrospectorFilterTest#found_one_instance_full_shared_node"
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=1" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=2" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=4" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=6" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=8" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=10" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=12" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=14" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=16" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=18" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=20" -Dtest="$UNIT_TEST" test

UNIT_TEST="IntrospectorFilterTest#found_one_instance_independent_path"
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=1" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=2" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=4" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=6" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=8" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=10" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=12" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=14" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=16" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=18" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=20" -Dtest="$UNIT_TEST" test

UNIT_TEST="DemeterDJTest#not_found_one_instance"
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=1" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=1" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=1" -Dtest="$UNIT_TEST" test

UNIT_TEST="IntrospectorFilterTest#not_found_one_instance_full_shared_node"
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=1" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=2" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=4" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=6" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=8" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=10" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=12" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=14" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=16" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=18" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=20" -Dtest="$UNIT_TEST" test

UNIT_TEST="IntrospectorFilterTest#not_found_one_instance_independent_path"
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=1" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=2" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=4" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=6" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=8" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=10" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=12" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=14" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=16" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=18" -Dtest="$UNIT_TEST" test
mvn -DargLine="-DGRAPH_SIZE=$GRAPH_SIZE -DNUM_THREADS=20" -Dtest="$UNIT_TEST" test
