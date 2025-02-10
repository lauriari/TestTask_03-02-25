#!/bin/bash
TEST="-f testfiles/test1.txt"
./run.sh $TEST
if diff resualt/1st_integers.txt integers.txt > /dev/null && diff resualt/1st_floats.txt floats.txt > /dev/null && diff resualt/1st_strings.txt strings.txt > /dev/null;
then
    echo "test1: $TEST - OK"
else
    echo "test1: $TEST - FAIL"
fi
TEST2="-a testfiles/test1.txt testfiles/test4.txt"
./run.sh $TEST2
if diff resualt/2nd_integers.txt integers.txt > /dev/null && diff resualt/2nd_floats.txt floats.txt > /dev/null && diff resualt/2nd_strings.txt strings.txt > /dev/null;
then
    echo "test2: $TEST2 - OK"
else
    echo "test2: $TEST2 - FAIL"
fi
TEST3="-p 3_ testfiles/test1.txt testfiles/test4.txt testfiles/test2.txt testfiles/test3.txt"
./run.sh $TEST3
if diff resualt/3rd_integers.txt 3_integers.txt > /dev/null && diff resualt/3rd_floats.txt 3_floats.txt > /dev/null && diff resualt/3rd_strings.txt 3_strings.txt > /dev/null;
then
    echo "test3: $TEST3 - OK"
else
    echo "test3: $TEST3 - FAIL"
fi

./clean.sh
