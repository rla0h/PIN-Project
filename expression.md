My study Expression
---
* 127.0.0.1
  * Local host 또는 Loop back 주소라고 부른다
  * 모든 컴퓨터는 127.0.0.1를 로컬 호스트의 주소로 사용할 수 있다.
  * 그러나 실제 IP 주소처럼 다른 컴퓨터와 이 주소로 통신할 수 없다
  * 보통 통신 테스트를 할때 사용
  * Loop back 처럼 자신이 보낸 패킷을 자신이 수신.
* pub scenario
```json
  {
  "name": "Continuous Integration Rapid-Fire Echo Test",
  "desc": "This is a small quick test for rapid-fire data send / receive capability",
  "scenario_parameters": [
    {
      "name": "Base",
      "desc": "Scenario Base",
      "value": { "$discriminator": "PK_STRING", "string_param": "echo" }
    },
    {
      "name": "Bytes",
      "desc": "Payload Bytes",
      "value": { "$discriminator": "PK_NUMBER", "number_param": 100 }
    }
  ],
  "any_node": [
    {
      "config": "pub.json",
      "count": 1
    }
  ],
  "timeout": 120
}
```
* pub.json
```json
{
  "create_time": { "sec": -1, "nsec": 0 },
  "enable_time": { "sec": -1, "nsec": 0 },
  "start_time": { "sec": -10, "nsec": 0 },
  "stop_time": { "sec": -15, "nsec": 0 },
  "destruction_time": { "sec": -1, "nsec": 0 },

  "wait_for_discovery": false,
  "wait_for_discovery_seconds": 0,

  "process": {
    "config_sections": [
      { "name": "common",
        "properties": [
          { "name": "DCPSDefaultDiscovery",
            "value":"rtps_disc"
          },
          { "name": "DCPSGlobalTransportConfig",
            "value":"$file"
          },
          { "name": "DCPSDebugLevel",
            "value": "0"
          },
          { "name": "DCPSPendingTimeout",
            "value": "3"
          }
        ]
      },
      { "name": "rtps_discovery/rtps_disc",
        "properties": [
          { "name": "ResendPeriod",
            "value": "2",
            "name": "SedpMulticast",
            "value": "0",
            "name": "SpdpSendAddrs",
            "value": ":8410"
          }
        ]
      },
      { "name": "transport/rtps_transport",
        "properties": [
          { "name": "transport_type",
            "value": "rtps_udp",
            "name": "use_multicast",
            "value": "0",
            "name": "local_address",
            "value": ":"
          }
        ]
      }
    ],
    "participants": [
      { "name": "participant_01",
        "domain": 4,

        "qos": { "entity_factory": { "autoenable_created_entities": false } },
        "qos_mask": { "entity_factory": { "has_autoenable_created_entities": false } },

        "topics": [
          { "name": "topic_01",
            "type_name": "Bench::Data"
          }
        ],
        "publishers": [
          { "name": "publisher_01",

            "qos": { "partition": { "name": [ "bench_partition" ] } },
            "qos_mask": { "partition": { "has_name": true } },

            "datawriters": [
              { "name": "datawriter_01",
                "topic_name": "topic_01",
                "listener_type_name": "bench_dwl",
                "listener_status_mask": 4294967295,
                "listener_properties": [
                  { "name": "expected_match_count",
                    "value": { "$discriminator": "PVK_ULL", "ull_prop": 1 }
                  }
                ],

                "qos": { "reliability": { "kind": "RELIABLE_RELIABILITY_QOS" },
                         "history": { "kind": "KEEP_ALL_HISTORY_QOS" }
                       },
                "qos_mask": { "reliability": { "has_kind": true },
                              "history": { "has_kind": true }
                            }
              }
            ]
          }
        ]
      }
    ]
  },
  "actions": [
    {
      "name": "write_action_01",
      "type": "write",
      "writers": [ "datawriter_01" ],
      "params": [
        { "name": "max_count",
          "value": { "$discriminator": "PVK_ULL", "ull_prop": 1000 }
        },
        { "name": "total_hops",
          "value": { "$discriminator": "PVK_ULL", "ull_prop": 2 }
        },
        { "name": "data_buffer_bytes",
          "value": { "$discriminator": "PVK_ULL", "ull_prop": 100 }
        },
        { "name": "write_frequency",
          "value": { "$discriminator": "PVK_DOUBLE", "double_prop": 100.0 }
        }
      ]
    }
  ]
}
```
* sub scenario
```json
{
  "name": "Continuous Integration Rapid-Fire Echo Test",
  "desc": "This is a small quick test for rapid-fire data send / receive capability",
  "scenario_parameters": [
    {
      "name": "Base",
      "desc": "Scenario Base",
      "value": { "$discriminator": "PK_STRING", "string_param": "echo" }
    },
    {
      "name": "Bytes",
      "desc": "Payload Bytes",
      "value": { "$discriminator": "PK_NUMBER", "number_param": 100 }
    }
  ],
  "any_node": [
    {
      "config": "sub.json",
      "count": 1
    }
  ],
  "timeout": 120
}
```
* sub json
```json
{
  "create_time": { "sec": -1, "nsec": 0 },
  "enable_time": { "sec": -1, "nsec": 0 },
  "start_time": { "sec": -10, "nsec": 0 },
  "stop_time": { "sec": -15, "nsec": 0 },
  "destruction_time": { "sec": -1, "nsec": 0 },

  "wait_for_discovery": false,
  "wait_for_discovery_seconds": 0,

  "process": {
    "config_sections": [
      { "name": "common",
        "properties": [
          { "name": "DCPSDefaultDiscovery",
            "value":"rtps_disc"
          },
          { "name": "DCPSGlobalTransportConfig",
            "value":"$file"
          },
          { "name": "DCPSDebugLevel",
            "value": "0"
          },
          { "name": "DCPSPendingTimeout",
            "value": "3"
          }
        ]
      },
      { "name": "rtps_discovery/rtps_disc",
        "properties": [
          { "name": "ResendPeriod",
            "value": "2",
            "name": "SedpMulticast",
            "value": "0",
            "name": "SpdpSendAddrs",
            "value": ":8410"
          }
        ]
      },
      { "name": "transport/rtps_transport",
        "properties": [
          { "name": "transport_type",
            "value": "rtps_udp",
            "name": "use_multicast",
            "value": "0",
            "name": "local_address",
            "value": ":"
          }
        ]
      }
    ],
    "participants": [
      { "name": "participant_01",
        "domain": 4,

        "qos": { "entity_factory": { "autoenable_created_entities": false } },
        "qos_mask": { "entity_factory": { "has_autoenable_created_entities": false } },

        "topics": [
          { "name": "topic_01",
            "type_name": "Bench::Data"
          }
        ],
        "subscribers": [
          { "name": "subscriber_01",

            "qos": { "partition": { "name": [ "bench_partition" ] } },
            "qos_mask": { "partition": { "has_name": true } },

            "datareaders": [
              { "name": "datareader_01",
                "topic_name": "topic_01",
                "listener_type_name": "bench_drl",
                "listener_status_mask": 4294967295,
                "listener_properties": [
                  { "name": "expected_match_count",
                    "value": { "$discriminator": "PVK_ULL", "ull_prop": 1 }
                  },
                  { "name": "expected_sample_count",
                    "value": { "$discriminator": "PVK_ULL", "ull_prop": 1000 }
                  },
                  { "name": "expected_per_writer_sample_count",
                    "value": { "$discriminator": "PVK_ULL", "ull_prop": 1000 }
                  }
                ],

                "qos": { "reliability": { "kind": "RELIABLE_RELIABILITY_QOS" },
                         "history": { "kind": "KEEP_ALL_HISTORY_QOS" }
                       },
                "qos_mask": { "reliability": { "has_kind": true },
                              "history": { "has_kind": true }
                            }
              }
            ]
          }
        ]
      }
    ]
  },
  "actions": [
    {
      "name": "forward_action_01",
      "type": "forward",
      "readers": [ "datareader_01" ],
      "writers": [ "datawriter_01" ]
    }
  ]
}
```
