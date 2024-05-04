#pragma once

#include <bitset>
#include <map>
#include <string>
#include <vector>

namespace sku {

class IEC61850_ObjnameParser {
 public:
  IEC61850_ObjnameParser(const std::string& objname);

  std::string get_ld() const { return ld_; };
  std::string get_ln() const { return ln_; };
  std::string get_fc() const { return fc_; };
  std::string get_do() const { return do_; };
  std::vector<std::string> get_sd() const { return sd_; };
  std::string get_da() const { return da_; };

 private:
  std::vector<std::string> split_vec();
  std::string ld_;
  std::string ln_;
  std::string fc_;
  std::string do_;
  std::vector<std::string> sd_;
  std::string da_;

  std::string objname_;
};

// input data is contiguous index order
// struct g10v2Data {
//   std::bitset<8> bstr8;
// };
// using g10v2Data_t = g10v2Data;

// struct g40v2Data {
//   std::bitset<8> bstr8;
//   int16_t value{0};
// };
// using g40v2Data_t = g40v2Data;

// template <typename T>
// class DNP3Parser {
//  public:
//   DNP3Parser(const int start_idx, const std::vector<uint8_t>& data) {
//     do_parse(start_idx, data);
//   }

//   T get_data(const int32_t index) { return data_map_.at(index); }

//  private:
//   void do_parse(const int start_idx, const std::vector<uint8_t>& data) {}

//   std::map<int32_t, T> data_map_;
// };

// template <>
// void DNP3Parser<g10v2Data_t>::do_parse(const int start_idx,
//                                        const std::vector<uint8_t>& data) {
//   for (int i = 0; i < data.size(); ++i) {
//     g10v2Data_t dnp_data;
//     dnp_data.bstr8 = std::move(std::bitset<8>(data[i]));
//     data_map_[i + start_idx] = std::move(dnp_data);
//   }
// }

// template <>
// void DNP3Parser<g40v2Data_t>::do_parse(const int start_idx,
//                                        const std::vector<uint8_t>& data) {
//   if (data.size() % 3 != 0)
//     throw std::invalid_argument("DNP3 g40v3 Parsing error, size not divide 3");

//   for (int i = 0; i < data.size(); i += 3) {
//     g40v2Data_t dnp_data;
//     dnp_data.bstr8 = std::move(std::bitset<8>(data[i]));
//     dnp_data.value = data[i + 1] + (data[i + 2] << 8);

//     data_map_[i + start_idx] = std::move(dnp_data);
//   }
// }

// class DNP3g10v2Parser {
//  public:
//   DNP3g10v2Parser(const int start_idx, const std::vector<uint8_t>& data);

//   g10v2Data_t get_data(const int32_t index);

//  private:
//   void do_parse(const int start_idx, const std::vector<uint8_t>& data);

//   std::map<int32_t, g10v2Data_t> data_map_;
// };

// class DNP3g40v2Parser {
//  public:
//   DNP3g40v2Parser(const int start_idx, const std::vector<uint8_t>& data);

//   g40v2Data_t get_data(const int32_t index);

//  private:
//   void do_parse(const int start_idx, const std::vector<uint8_t>& data);

//   std::map<int32_t, g40v2Data_t> data_map_;
// };

// TODO : delete line start
// enum class DType {
//   kINT16,
//   kINT32,
//   kFP32,
// };
// using DType_t = DType;

// typedef struct IPCData {
//   std::string objname_;
//   DType_t type_;
//   std::vector<uint8_t> value_;
// } IPCData_t;

// class IPCDataParser {
//  public:
//   static IPCData_t parse(const std::vector<uint8_t>& data);
// };
// TODO : delete line end

}  // namespace sku
