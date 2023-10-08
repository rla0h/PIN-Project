# Performance Test in OpenDDS (Kubernetes)
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


## Pod(worker_node) to Pod(worker_node) performance test
### node_controller
* Before Start /etc/hosts file has pod information (ip hostname)
```bash
# repo pod terminal
$ DCPSInfoRepo -ORBListenEndPoints iiop://$(hostname -i):1212
# pub pod terminal
$ ./node_controller daemon-exit-on-error --domain NUMBER
$ ./test_controller ../example/ SCENARIO_NAME --domain NUMBER
# sub pod  terminal
$ ./node_controller daemon-exit-on-error --domain NUMBER
$ ./test_controller ../example/ SCENARIO_NAME --domain NUMBER

```