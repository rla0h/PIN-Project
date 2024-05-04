#include "adaptor.h"

#include <iostream>
#include <thread>

#include "mapper.h"
//#include "ipc_data.pb.h"

namespace sku {

ScadaToCimAdaptor::ScadaToCimAdaptor(const std::string& ip,
                                     const std::string& port,
                                     const std::string& opendds_config) {
  boost::asio::ip::udp::endpoint listen_endpoint(
      boost::asio::ip::address::from_string(ip), std::stoi(port));
  udp_server_ = std::make_unique<udp_server>(listen_endpoint);
  opendds_publisher_ = std::make_unique<Publisher>(opendds_config);

  std::cout << "Waiting for OpenDDS Subscriber...\n";
  opendds_publisher_->wait_for_subscriber_acls();
  std::cout << "ACLineSegment ok\n";
  opendds_publisher_->wait_for_subscriber_sw();
  std::cout << "Switch ok\n";
}

void ScadaToCimAdaptor::run() {
  std::thread handle_thr([&]() { handle(); });
  handle_thr.join();
}

void ScadaToCimAdaptor::register_func_map() {
  pub_map_["ZLIN"] = [&](const IEC61850_ObjnameParser& p, const IPCData_t& data,
                         void* cim_class) {
    auto acls = static_cast<ACLineSegment*>(cim_class);
    ScadaToCimMapper mapper;

    if (data.type_ == DType::kINT16) {
      auto val = data.value_;
      auto val_ptr = reinterpret_cast<int16_t*>(val.data());
      mapper.set_value(p, *val_ptr, acls);
    } else if (data.type_ == DType::kFP32) {
      auto val = data.value_;
      auto val_ptr = reinterpret_cast<float*>(val.data());
      mapper.set_value(p, *val_ptr, acls);
    } else
      return;

    auto topic = CimToTopic::to_opendds_topic(*acls);
    opendds_publisher_->publish(topic);
  };

  pub_map_["XSWI"] = [&](const IEC61850_ObjnameParser& p, const IPCData_t& data,
                         void* cim_class) {
    auto sw = static_cast<Switch*>(cim_class);
    ScadaToCimMapper mapper;

    if (data.type_ == DType::kINT16) {
      auto val = data.value_;
      auto val_ptr = reinterpret_cast<int16_t*>(val.data());
      mapper.set_value(p, *val_ptr, sw);      
    } else
      return;

    auto topic = CimToTopic::to_opendds_topic(*sw);
    opendds_publisher_->publish(topic);
  };
}

void ScadaToCimAdaptor::handle() {
  while (true) {
    std::vector<uint8_t> data(1024);
    boost::system::error_code ec;
    auto recv_size = udp_server_->receive(boost::asio::buffer(data),
                                          boost::posix_time::pos_infin, ec);

    data.resize(recv_size);                                    
    
    SCADA::IPCData ipc_data;
    ipc_data.ParseFromArray(data.data(), data.size());

    auto buf = ipc_data.buf();

    std::vector<uint8_t> buf_uint8(buf.begin(), buf.end());

    int bt = 0;
    
    // // ipc parse
    // IPCDataParser parser;
    // auto ipc_data = parser.parse(data);

    // // IEC61850 obj parse
    // IEC61850_ObjnameParser obj_parser(ipc_data.objname_);

    // void* cim_cast = nullptr;
    // if (obj_parser.get_ln() == "ZLIN")
    //   cim_cast = static_cast<void*>(&acls_);
    // else if (obj_parser.get_ln() == "XSWI")
    //   cim_cast = static_cast<void*>(&sw_);

    // if (!cim_cast) continue;

    // // publish
    // if (pub_map_.find(obj_parser.get_ln()) != pub_map_.end()) {
    //   pub_map_[obj_parser.get_ln()](obj_parser, ipc_data, cim_cast);
    // }
  }
}

}  // namespace sku