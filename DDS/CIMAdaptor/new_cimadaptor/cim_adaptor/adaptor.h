#pragma once

#include <functional>
#include <map>
#include <memory>
#include <string>

#include "cim.h"
#include "parser.h"

namespace sku {

class ScadaToCimAdaptor {
  using PubFunc = std::function<void(const IEC61850_ObjnameParser&,
                                     const IPCData_t&, void*)>;

 public:
  ScadaToCimAdaptor(const std::string& ip, const std::string& port,
                    const std::string& opendds_config);

  void run();

 private:
  void register_func_map();
  void handle();

  std::map<std::string, PubFunc> pub_map_;
  std::unique_ptr<udp_server> udp_server_;
  std::unique_ptr<Publisher> opendds_publisher_;
  ACLineSegment acls_;
  Switch sw_;
};

}  // namespace sku