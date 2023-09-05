# Performance Test in OpenDDS
[reference link](https://opendds.readthedocs.io/en/latest/internal/bench.html#cmdoption-test_controller-wait-for-nodes)
## First build
```bash
# AT $DDS_ROOT add --tests option
$ ./configure --verbose --java -doc-group3 --features=java_pre_jpms=0 --std=c++11 --rapidjson --tests
$ make
$ $DDS_ROOT/performance-tests/bench/
run ./run_test.pl
# test passed.
```