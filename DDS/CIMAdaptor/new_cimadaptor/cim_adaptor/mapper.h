#pragma once

#include <functional>
#include <map>

#include "cim.h"
#include "parser.h"

namespace sku {

class ScadaToCimMapper {
 public:
  // IEC61850 ZLIN LN
  static void set_value(const IEC61850_ObjnameParser& parser, const int16_t val,
                        ACLineSegment* acls);

  static void set_value(const IEC61850_ObjnameParser& parser, const float val,
                        ACLineSegment* acls);

  // IEC61850 XSWI LN
  static void set_value(const IEC61850_ObjnameParser& parser, const int16_t val,
                        Switch* sw);
};


}  // namespace sku