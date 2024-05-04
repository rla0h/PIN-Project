#include "parser.h"

#include <cstring>
#include <stdexcept>
#include <iostream>

namespace sku {

IEC61850_ObjnameParser::IEC61850_ObjnameParser(const std::string& objname)
    : objname_(objname) {
  auto split = split_vec();

  if (split.size() < 5) throw std::invalid_argument("objname split size error");

  ld_ = split[0];
  ln_ = split[1];
  fc_ = split[2];
  do_ = split[3];
  da_ = *(split.end() - 1);

  sd_ = std::vector<std::string>(split.begin() + 4, split.end() - 1);
};

std::vector<std::string> IEC61850_ObjnameParser::split_vec() {
  std::vector<std::string> buf;

  char name_cp[512];

  std::strcpy(name_cp, objname_.c_str());

  const char* tok = std::strtok(name_cp, "/$");
  while (tok != NULL) {
    buf.emplace_back(std::string(tok));
    tok = std::strtok(NULL, "/$");
  }

  return buf;
}

// DNP3g40v2Parser::DNP3g40v2Parser(const int start_idx,
//                                  const std::vector<uint8_t>& data) {
//   do_parse(start_idx, data);
// }

// g40v2Data_t DNP3g40v2Parser::get_data(const int32_t index) {
//   return data_map_.at(index);
// }

// void DNP3g40v2Parser::do_parse(const int start_idx,
//                                const std::vector<uint8_t>& data) {
//   if (data.size() % 3 != 0)
//     throw std::invalid_argument("DNP3 g40v3 Parsing error, not divide 3");

//   for (int i = 0; i < data.size(); i += 3) {
//     g40v2Data_t dnp_data;
//     dnp_data.bstr8 = std::move(std::bitset<8>(data[i]));
//     dnp_data.value = data[i + 1] + (data[i + 2] << 8);

//     data_map_[i + start_idx] = std::move(dnp_data);
//   }
// }

}  // namespace sku
