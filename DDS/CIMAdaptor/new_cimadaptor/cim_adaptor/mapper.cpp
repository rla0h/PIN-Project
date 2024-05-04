#include "mapper.h"
#include <string>
#include <iostream>

namespace sku {

namespace {

constexpr unsigned long hashstr(const std::string_view& str, int h = 0) {
  return !str[h] ? 55 : (hashstr(str, h + 1) * 33) + (unsigned char)(str[h]);
}

}  // namespace

void ScadaToCimMapper::set_value(const IEC61850_ObjnameParser& parser,
                                 const int16_t val, ACLineSegment* acls) {
  std::cout <<"set_value acls int"<<std::endl;
  switch (hashstr(parser.get_do())) {
    case hashstr("LinLenkm"): {
      auto len = acls->get_Length();

      if (parser.get_da() == "SIUnit") {
        len.unit_ = static_cast<UnitSymbol>(val);
        acls->set_Length(len);
      } else {
        len.multiplier_ = static_cast<UnitMultiplier>(val);
        acls->set_Length(len);
      }
    } break;

    case hashstr("RPs"): {
      auto r = acls->get_r();

      if (parser.get_da() == "SIUnit") {
        r.unit_ = static_cast<UnitSymbol>(val);
        acls->set_r(r);
      } else if (parser.get_da() == "multiplier") {
        r.multiplier_ = static_cast<UnitMultiplier>(val);
        acls->set_r(r);
      }
    } break;

    case hashstr("RZer"): {
      auto r0 = acls->get_r0();

      if (parser.get_da() == "SIUnit") {
        r0.unit_ = static_cast<UnitSymbol>(val);
        acls->set_r0(r0);
      } else if (parser.get_da() == "multiplier") {
        r0.multiplier_ = static_cast<UnitMultiplier>(val);
        acls->set_r0(r0);
      }
    } break;

    case hashstr("XPs"): {
      auto x = acls->get_x();

      if (parser.get_da() == "SIUnit") {
        x.unit_ = static_cast<UnitSymbol>(val);
        acls->set_x(x);
      } else if (parser.get_da() == "multiplier") {
        x.multiplier_ = static_cast<UnitMultiplier>(val);
        acls->set_x(x);
      }
    } break;

    case hashstr("XZer"): {
      auto x0 = acls->get_x0();

      if (parser.get_da() == "SIUnit") {
        x0.unit_ = static_cast<UnitSymbol>(val);
        acls->set_x0(x0);
      } else if (parser.get_da() == "multiplier") {
        x0.multiplier_ = static_cast<UnitMultiplier>(val);
        acls->set_x0(x0);
      }
    } break;
  }
}

void ScadaToCimMapper::set_value(const IEC61850_ObjnameParser& parser,
                                 const float val, ACLineSegment* acls) {
  std::cout <<"set_value acls float"<<std::endl;
  switch (hashstr(parser.get_do())) {
    case hashstr("LinLenkm"): {
      auto len = acls->get_Length();

      if (parser.get_da() == "f") {
        len.value_ = val;
        acls->set_Length(len);
      }
    } break;

    case hashstr("RPs"): {
      auto r = acls->get_r();

      if (parser.get_da() == "f") {
        r.value_ = val;
        acls->set_r(r);
      }
    } break;

    case hashstr("RZer"): {
      auto r0 = acls->get_r0();

      if (parser.get_da() == "f") {
        r0.value_ = val;
        acls->set_r0(r0);
      }
    } break;

    case hashstr("XPs"): {
      auto x = acls->get_x();

      if (parser.get_da() == "f") {
        x.value_ = val;
        acls->set_x(x);
      }
    } break;

    case hashstr("XZer"): {
      auto x0 = acls->get_x0();

      if (parser.get_da() == "f") {
        x0.value_ = val;
        acls->set_x0(x0);
      }
    } break;
  }
}

void ScadaToCimMapper::set_value(const IEC61850_ObjnameParser& parser, const int16_t val,
                      Switch* sw) {
  std::cout <<"set_value sw int"<<std::endl;
  switch (hashstr(parser.get_do())) {
    case hashstr("Pos"): {
      if (parser.get_da() == "stVal") {
        // stVal CODED ENUM [intermediate-state(0) | off(1) | on(2) |
        // bad-state(3)]
        if (val == 1)
          sw->set_normalOpen(false);
        else if (val == 2)
          sw->set_normalOpen(true);
      }





    } break;
  }
}
}  // namespace sku