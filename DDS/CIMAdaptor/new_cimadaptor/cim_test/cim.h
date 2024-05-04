#pragma once

#include <array>
#include <map>
#include <memory>
#include <string>
#include <vector>

namespace sku {

enum class UnitSymbol : int {
  UnitSymbol_none = 0,
  UnitSymbol_m = 2,
  UnitSymbol_s = 4,
  UnitSymbol_A = 5,
  UnitSymbol_deg = 9,
  UnitSymbol_rad = 10,
  UnitSymbol_degC = 23,
  UnitSymbol_F = 25,
  UnitSymbol_C = 26,
  UnitSymbol_H = 28,
  UnitSymbol_V = 29,
  UnitSymbol_ohm = 30,
  UnitSymbol_Hz = 33,
  UnitSymbol_W = 38,
  UnitSymbol_VA = 61,
  UnitSymbol_VAr = 63,
  UnitSymbol_cosPhi = 65,
  UnitSymbol_Vs = 66,
  UnitSymbol_V2 = 67,
  UnitSymbol_As = 68,
  UnitSymbol_A2 = 69,
  UnitSymbol_A2s = 70,
  UnitSymbol_VAh = 71,
  UnitSymbol_Wh = 72,
  UnitSymbol_VArh = 73,
  UnitSymbol_h = 84,
  UnitSymbol_min = 85,
  UnitSymbol_Q = 100,
  UnitSymbol_Qh = 101,
  UnitSymbol_V2h = 104,
  UnitSymbol_A2h = 105,
  UnitSymbol_Ah = 106,
  UnitSymbol_count = 111,
  UnitSymbol_sPers = 149,
  UnitSymbol_HzPerHz = 150,
  UnitSymbol_VPerV = 151,
  UnitSymbol_APerA = 152,
  UnitSymbol_VPerVA = 153
};
enum class UnitMultiplier : int {
  UnitMultiplier_y = -24,
  UnitMultiplier_z = -21,
  UnitMultiplier_a = -18,
  UnitMultiplier_f = -15,
  UnitMultiplier_p = -12,
  UnitMultiplier_n = -9,
  UnitMultiplier_micro = -6,
  UnitMultiplier_m = -3,
  UnitMultiplier_c = -2,
  UnitMultiplier_d = -1,
  UnitMultiplier_none = 0,
  UnitMultiplier_da = 1,
  UnitMultiplier_h = 2,
  UnitMultiplier_k = 3,
  UnitMultiplier_M = 6,
  UnitMultiplier_G = 9,
  UnitMultiplier_T = 12,
  UnitMultiplier_P = 15,
  UnitMultiplier_E = 18,
  UnitMultiplier_Z = 21,
  UnitMultiplier_Y = 24
};

enum class WindingConnection {
  WindingConnection_D,
  WindingConnection_Y,
  WindingConnection_Z,
  WindingConnection_Yn,
  WindingConnection_Zn,
  WindingConnection_A,
  WindingConnection_I
};

enum class PhaseCode : int {
  PhaseCode_ABCN = 225,
  PhaseCode_ABC = 224,
  PhaseCode_ABN = 193,
  PhaseCode_ACN = 41,
  PhaseCode_BCN = 97,
  PhaseCode_AB = 132,
  PhaseCode_AC = 96,
  PhaseCode_BC = 66,
  PhaseCode_AN = 129,
  PhaseCode_BN = 65,
  PhaseCode_CN = 33,
  PhaseCode_A = 128,
  PhaseCode_B = 64,
  PhaseCode_C = 32,
  PhaseCode_N = 16,
  PhaseCode_s1N = 528,
  PhaseCode_s2N = 272,
  PhaseCode_s12N = 784,
  PhaseCode_s1 = 512,
  PhaseCode_s2 = 256,
  PhaseCode_s12 = 768,
  PhaseCode_none = 0,
  PhaseCode_X = 1024,
  PhaseCode_XY = 3072,
  PhaseCode_XN = 1040,
  PhaseCode_XYN = 3088
};

enum class SinglePhaseKind : int { A = 1, B = 2, C = 3, N = 4, s1 = 5, s2 = 6 };

enum class BatteryStateKind {
  BatteryStateKind_discharging,
  BatteryStateKind_full,
  BatteryStateKind_waiting,
  BatteryStateKind_charging,
  BatteryStateKind_empty
};

enum class CsOperatingModeKind {
  CsOperatingModeKind_inverter,
  CsOperatingModeKind_rectifier
};

enum class CsPpccControlKind {
  CsPpccControlKind_activePower,
  CsPpccControlKind_dcVoltage,
  CsPpccControlKind_dcCurrent
};

enum class VsPpccControlKind {
  VsPpccControlKind_pPcc,
  VsPpccControlKind_udc,
  VsPpccControlKind_pPccAndUdcDroop,
  VsPpccControlKind_pPccAndUdcDroopWithCompensation,
  VsPpccControlKind_pPccAndUdcDroopPilot,
  VsPpccControlKind_phasePcc
};

enum class VsQpccControlKind {
  VsQpccControlKind_reactivePcc,
  VsQpccControlKind_voltagePcc,
  VsQpccControlKind_powerFactorPcc,
  VsQpccControlKind_pulseWidthModulation
};

enum class DCPolarityKind {
  DCPolarityKind_positive,
  DCPolarityKind_middle,
  DCPolarityKind_negative
};

enum class DCConverterOperatingModeKind {
  DCConverterOperatingModeKind_bipolar,
  DCConverterOperatingModeKind_monopolarMetallicReturn,
  DCConverterOperatingModeKind_monopolarGroundReturn
};

struct CommonUnit {
  UnitMultiplier multiplier_{UnitMultiplier::UnitMultiplier_none};
  UnitSymbol unit_{UnitSymbol::UnitSymbol_none};
  float value_{0};
};

using ActivePower = CommonUnit;

using AngleRadians = CommonUnit;

using AngleDegrees = CommonUnit;

using ApparentPower = CommonUnit;

using Length = CommonUnit;

using Reactance = CommonUnit;

using ReactivePower = CommonUnit;

using Resistance = CommonUnit;

using Voltage = CommonUnit;

using CurrentFlow = CommonUnit;

using ActivePowerPerCurrentFlow = CommonUnit;

using PerCent = CommonUnit;

using PU = CommonUnit;

using RealEnergy = CommonUnit;

using Seconds = CommonUnit;

using Capacitance = CommonUnit;

using Inductance = CommonUnit;

class IdentifiedObject {
 public:
  std::string get_desc() const { return description_; }
  void set_desc(const std::string& input) { description_ = input; }

  std::string get_mRID() const { return mRID_; }
  void set_mRID(const std::string& input) { mRID_ = input; }

  std::string get_name() const { return name_; }
  void set_name(const std::string& input) { name_ = input; }

  std::string get_aliasName() const { return aliasName_; }
  void set_aliasName(const std::string& input) { aliasName_ = input; }

 private:
  std::string description_;
  std::string mRID_;
  std::string name_;
  std::string aliasName_;
};

class ACDCTerminal : public IdentifiedObject {
 public:
  bool get_connected() const { return connected_; }
  void set_connected(const bool input) { connected_ = input; }

  int32_t get_sequenceNumber() const { return sequenceNumber_; }
  void set_sequenceNumber(const int32_t input) { sequenceNumber_ = input; }

 private:
  bool connected_;
  int32_t sequenceNumber_;
};

class ConnectivityNode : public IdentifiedObject {};

class TransformerEnd : public IdentifiedObject {};

class PowerSystemResource : public IdentifiedObject {};

class Equipment : public PowerSystemResource {};

class PowerTransformerEnd : public TransformerEnd {
 public:
  WindingConnection get_connectionKind() const { return connectionKind_; }
  void set_connectionKind(const WindingConnection& input) {
    connectionKind_ = input;
  }

  Resistance get_r() const { return r_; }
  void set_r(const Resistance& input) { r_ = input; }

  Resistance get_r0() const { return r0_; }
  void set_r0(const Resistance& input) { r0_ = input; }

  ApparentPower get_ratedS() const { return ratedS_; }
  void set_ratedS(const ApparentPower& input) { ratedS_ = input; }

  Voltage get_ratedU() const { return ratedU_; }
  void set_ratedU(const Voltage& input) { ratedU_ = input; }

  Reactance get_x() const { return x_; }
  void set_x(const Reactance& input) { x_ = input; }

  Reactance get_x0() const { return x0_; }
  void set_x0(const Reactance& input) { x0_ = input; }

 private:
  WindingConnection connectionKind_;
  Resistance r_;
  Resistance r0_;
  ApparentPower ratedS_;
  Voltage ratedU_;
  Reactance x_;
  Reactance x0_;
};

class ConductingEquipment : public PowerSystemResource {};

class Conductor : public ConductingEquipment {
 public:
  Length get_Length() const { return length_; }
  void set_Length(const Length& input) { length_ = input; }

 private:
  Length length_;
};

class EnergyConnection : public ConductingEquipment {};

class EnergyConsumer : public EnergyConnection {
 public:
  ActivePower get_p() const { return p_; }
  void set_p(const ActivePower& input) { p_ = input; }

  ReactivePower get_q() const { return q_; }
  void set_q(const ReactivePower& input) { q_ = input; }

 private:
  ActivePower p_;
  ReactivePower q_;
};

class EnergySource : public EnergyConnection {
 public:
  ActivePower get_activePower() const { return activePower_; }
  void set_activePower(const ActivePower& input) { activePower_ = input; }

  ReactivePower get_reactivePower() const { return reactivePower_; }
  void set_reactivePower(const ReactivePower& input) { reactivePower_ = input; }

  Voltage get_nominalVoltage() const { return nominalVoltage_; }
  void set_nominalVoltage(const Voltage& input) { nominalVoltage_ = input; }

  Resistance get_r() const { return r_; }
  void set_r(const Resistance& input) { r_ = input; }

  Resistance get_r0() const { return r0_; }
  void set_r0(const Resistance& input) { r0_ = input; }

  Resistance get_rn() const { return rn_; }
  void set_rn(const Resistance& input) { rn_ = input; }

  AngleRadians get_voltageAngle() const { return voltageAngle_; }
  void set_voltageAngle(const AngleRadians& input) { voltageAngle_ = input; }

  Voltage get_voltageMagnitude() const { return voltageMagnitude_; }
  void set_voltageMagnitude(const Voltage& input) { voltageMagnitude_ = input; }

  Reactance get_x() const { return x_; }
  void set_x(const Reactance& input) { x_ = input; }

  Reactance get_x0() const { return x0_; }
  void set_x0(const Reactance& input) { x0_ = input; }

  Reactance get_xn() const { return xn_; }
  void set_xn(const Reactance& input) { xn_ = input; }

  ActivePower get_pMin() const { return pMin_; }
  void set_pMin(const ActivePower& input) { pMin_ = input; }

  ActivePower get_pMax() const { return pMax_; }
  void set_pMax(const ActivePower& input) { pMax_ = input; }

 private:
  ActivePower activePower_;
  ReactivePower reactivePower_;
  Voltage nominalVoltage_;
  Resistance r_;
  Resistance r0_;
  Resistance rn_;
  AngleRadians voltageAngle_;
  Voltage voltageMagnitude_;
  Reactance x_;
  Reactance x0_;
  Reactance xn_;
  ActivePower pMin_;
  ActivePower pMax_;
};

class RegulatingCondEq : public EnergyConnection {
 public:
  bool get_controlEnabled() const { return controlEnabled_; }
  void set_controlEnabled(const bool input) { controlEnabled_ = input; }

 private:
  bool controlEnabled_;
};

class PowerElectronicsUnit : public Equipment {
 public:
  ActivePower get_maxP() const { return maxP_; }
  void set_maxP(const ActivePower& input) { maxP_ = input; }

  ActivePower get_minP() const { return minP_; }
  void set_minP(const ActivePower& input) { minP_ = input; }

  ActivePower get_p() const { return p_; }
  void set_p(const ActivePower& input) { p_ = input; }

  ReactivePower get_q() const { return q_; }
  void set_q(const ReactivePower& input) { q_ = input; }

 private:
  ActivePower maxP_;
  ActivePower minP_;
  ActivePower p_;
  ReactivePower q_;
};

class BatteryUnit : public PowerElectronicsUnit {
 public:
  BatteryStateKind get_batteryState() const { return batteryState_; }
  void set_batteryState(const BatteryStateKind& input) {
    batteryState_ = input;
  }

  RealEnergy get_ratedE() const { return ratedE_; }
  void set_ratedE(const RealEnergy& input) { ratedE_ = input; }

  RealEnergy get_storedE() const { return storedE_; }
  void set_storedE(const RealEnergy& input) { storedE_ = input; }

 private:
  BatteryStateKind batteryState_;
  RealEnergy ratedE_;
  RealEnergy storedE_;
};

class PowerElectronicsWindUnit : public PowerElectronicsUnit {};

class PhotoVoltaicUnit : public RegulatingCondEq {};

class PowerElectronicsConnection : public RegulatingCondEq {
 public:
  ReactivePower get_maxQ() const { return maxQ_; }
  void set_maxQ(const ReactivePower& input) { maxQ_ = input; }

  ReactivePower get_minQ() const { return minQ_; }
  void set_minQ(const ReactivePower& input) { minQ_ = input; }

  ApparentPower get_ratedS() const { return ratedS_; }
  void set_ratedS(const ApparentPower& input) { ratedS_ = input; }

  Voltage get_ratedU() const { return ratedU_; }
  void set_ratedU(const Voltage& input) { ratedU_ = input; }

  ActivePower get_p() const { return p_; }
  void set_p(const ActivePower& input) { p_ = input; }

  ReactivePower get_q() const { return q_; }
  void set_q(const ReactivePower& input) { q_ = input; }

  PU get_maxIFault() const { return maxIFault_; }
  void set_maxIFault(const PU& input) { maxIFault_ = input; }

  Resistance get_r() const { return r_; }
  void set_r(const Resistance& input) { r_ = input; }

  Resistance get_r0() const { return r0_; }
  void set_r0(const Resistance& input) { r0_ = input; }

  Resistance get_rn() const { return rn_; }
  void set_rn(const Resistance& input) { rn_ = input; }

  Reactance get_x() const { return x_; }
  void set_x(const Reactance& input) { x_ = input; }

  Reactance get_x0() const { return x0_; }
  void set_x0(const Reactance& input) { x0_ = input; }

  Reactance get_xn() const { return xn_; }
  void set_xn(const Reactance& input) { xn_ = input; }

  PowerElectronicsUnit get_peu() const { return peu_; }
  void set_peu(const PowerElectronicsUnit& input) { peu_ = input; }

 private:
  ReactivePower maxQ_;
  ReactivePower minQ_;
  ApparentPower ratedS_;
  Voltage ratedU_;
  ActivePower p_;
  ReactivePower q_;
  PU maxIFault_;
  Resistance r_;
  Resistance r0_;
  Resistance rn_;
  Reactance x_;
  Reactance x0_;
  Reactance xn_;
  PowerElectronicsUnit peu_;
};

class PowerTransformer : public ConductingEquipment {
 public:
  std::array<PowerTransformerEnd, 2> get_a_pte() const { return a_pte_; }
  void set_a_pte(const std::array<PowerTransformerEnd, 2>& input) {
    a_pte_ = input;
  }

 private:
  std::array<PowerTransformerEnd, 2> a_pte_;
};

class Switch : public ConductingEquipment {
 public:
  bool get_normalOpen() const { return normalOpen_; }
  void set_normalOpen(const bool input) { normalOpen_ = input; }

  bool get_open() const { return open_; }
  void set_open(const bool input) { open_ = input; }

  CurrentFlow get_ratedCurrent() const { return ratedCurrent_; }
  void set_ratedCurrent(const CurrentFlow& input) { ratedCurrent_ = input; }

 private:
  bool normalOpen_{false};
  bool open_{false};
  CurrentFlow ratedCurrent_;
};

class ProtectionEquipment : public Equipment {
 public:
  Seconds get_relayDelayTime() const { return relayDelayTime_; }
  void set_relayDelayTime(const Seconds& input) { relayDelayTime_ = input; }

  float get_highLimit() const { return highLimit_; }
  void set_highLimit(const float input) { highLimit_ = input; }

  float get_lowLimit() const { return lowLimit_; }
  void set_lowLimit(const float input) { lowLimit_ = input; }

  bool get_powerDirectionFlag() const { return powerDirectionFlag_; }
  void set_powerDirectionFlag(const bool input) { powerDirectionFlag_ = input; }

  UnitMultiplier get_unitMultiplier() const { return unitMultiplier_; }
  void set_unitMultiplier(const UnitMultiplier& input) {
    unitMultiplier_ = input;
  }

  UnitSymbol get_unitSymbol() const { return unitSymbol_; }
  void set_unitSymbol(const UnitSymbol& input) { unitSymbol_ = input; }

 private:
  Seconds relayDelayTime_;
  float highLimit_;
  float lowLimit_;
  bool powerDirectionFlag_;
  UnitMultiplier unitMultiplier_;
  UnitSymbol unitSymbol_;
};

class CurrentRelay : public ProtectionEquipment {
 public:
  CurrentFlow get_currentLimit1() const { return currentLimit1_; }
  void set_currentLimit1(const CurrentFlow& input) { currentLimit1_ = input; }

  CurrentFlow get_currentLimit2() const { return currentLimit2_; }
  void set_currentLimit2(const CurrentFlow& input) { currentLimit2_ = input; }

  CurrentFlow get_currentLimit3() const { return currentLimit3_; }
  void set_currentLimit3(const CurrentFlow& input) { currentLimit3_ = input; }

  bool get_inverseTimeFlag() const { return inverseTimeFlag_; }
  void set_inverseTimeFlag(const bool input) { inverseTimeFlag_ = input; }

  Seconds get_timeDelay1() const { return timeDelay1_; }
  void set_timeDelay1(const Seconds& input) { timeDelay1_; }

  Seconds get_timeDelay2() const { return timeDelay2_; }
  void set_timeDelay2(const Seconds& input) { timeDelay2_ = input; }

  Seconds get_timeDelay3() const { return timeDelay3_; }
  void set_timeDelay3(const Seconds& input) { timeDelay3_ = input; }

 private:
  CurrentFlow currentLimit1_;
  CurrentFlow currentLimit2_;
  CurrentFlow currentLimit3_;
  bool inverseTimeFlag_;
  Seconds timeDelay1_;
  Seconds timeDelay2_;
  Seconds timeDelay3_;
};

class RecloseSequence : public IdentifiedObject {
 public:
  Seconds get_recloseDelay() const { return recloseDelay_; }
  void set_recloseDelay(const Seconds& input) { recloseDelay_ = input; }

  int32_t get_recloseStep() const { return recloseStep_; }
  void set_recloseStep(const int32_t input) { recloseStep_ = input; }

 private:
  Seconds recloseDelay_;
  int32_t recloseStep_;
};

class ProtectedSwitch : public Switch {
 public:
  CurrentFlow get_breakingCapacity() const { return breakingCapacity_; }
  void set_breakingCapacity(const CurrentFlow& input) {
    breakingCapacity_ = input;
  }

  std::array<RecloseSequence, 4> get_r_seq() const { return r_seq_; }
  void set_r_seq(const std::array<RecloseSequence, 4>& input) {
    r_seq_ = input;
  }

 private:
  CurrentFlow breakingCapacity_;
  std::array<RecloseSequence, 4> r_seq_;
};

class Recloser : public ProtectedSwitch {};

class ACLineSegment : public Conductor {
 public:
  Resistance get_r() const { return r_; }
  void set_r(const Resistance& input) { r_ = input; }

  Resistance get_r0() const { return r0_; }
  void set_r0(const Resistance& input) { r0_ = input; }

  Reactance get_x() const { return x_; }
  void set_x(const Reactance& input) { x_ = input; }

  Reactance get_x0() const { return x0_; }
  void set_x0(const Reactance& input) { x0_ = input; }

 private:
  Resistance r_;
  Resistance r0_;
  Reactance x_;
  Reactance x0_;
};

class ACDCConverter : public ConductingEquipment {
 public:
  ApparentPower get_baseS() const { return baseS_; }
  void set_baseS(const ApparentPower& input) { baseS_ = input; }

  CurrentFlow get_idc() const { return idc_; }
  void set_idc(const CurrentFlow& input) { idc_ = input; }

  ActivePower get_idleLoss() const { return idleLoss_; }
  void set_idleLoss(const ActivePower& input) { idleLoss_ = input; }

  Voltage get_maxUdc() const { return maxUdc_; }
  void set_maxUdc(const Voltage& input) { maxUdc_ = input; }

  Voltage get_minUdc() const { return minUdc_; }
  void set_minUdc(const Voltage& input) { minUdc_ = input; }

  int32_t get_numberOfValves() const { return numberOfValves_; }
  void set_numberOfValves(const int32_t& input) { numberOfValves_ = input; }

  ActivePower get_p() const { return p_; }
  void set_p(const ActivePower& input) { p_ = input; }

  ActivePower get_poleLossP() const { return poleLossP_; }
  void set_poleLossP(const ActivePower& input) { poleLossP_ = input; }

  ReactivePower get_q() const { return q_; }
  void set_q(const ReactivePower& input) { q_ = input; }

  Voltage get_ratedUdc() const { return ratedUdc_; }
  void set_ratedUdc(const Voltage& input) { ratedUdc_ = input; }

  Resistance get_resistiveLoss() const { return resistiveLoss_; }
  void set_resistiveLoss(const Resistance& input) { resistiveLoss_ = input; }

  ActivePowerPerCurrentFlow get_switchingLoss() const { return switchingLoss_; }
  void set_switchingLoss(const ActivePowerPerCurrentFlow& input) {
    switchingLoss_ = input;
  }

  ActivePower get_targetPpcc() const { return targetPpcc_; }
  void set_targetPpcc(const ActivePower& input) { targetPpcc_ = input; }

  Voltage get_targetUdc() const { return targetUdc_; }
  void set_targetUdc(const Voltage& input) { targetUdc_ = input; }

  Voltage get_uc() const { return uc_; }
  void set_uc(const Voltage& input) { uc_ = input; }

  Voltage get_udc() const { return udc_; }
  void set_udc(const Voltage& input) { udc_ = input; }

  Voltage get_valveU0() const { return valveU0_; }
  void set_valveU0(const Voltage& input) { valveU0_ = input; }

  ActivePower get_maxP() const { return maxP_; }
  void set_maxP(const ActivePower& input) { maxP_ = input; }

  ActivePower get_minP() const { return minP_; }
  void set_minP(const ActivePower& input) { minP_ = input; }

 private:
  ApparentPower baseS_;
  CurrentFlow idc_;
  ActivePower idleLoss_;
  Voltage maxUdc_;
  Voltage minUdc_;
  int32_t numberOfValves_;
  ActivePower p_;
  ActivePower poleLossP_;
  ReactivePower q_;
  Voltage ratedUdc_;
  Resistance resistiveLoss_;
  ActivePowerPerCurrentFlow switchingLoss_;
  ActivePower targetPpcc_;
  Voltage targetUdc_;
  Voltage uc_;
  Voltage udc_;
  Voltage valveU0_;
  ActivePower maxP_;
  ActivePower minP_;
};

class CsConverter : public ACDCConverter {
 public:
  AngleDegrees get_alpha() const { return alpha_; }
  void set_alpha(const AngleDegrees& input) { alpha_ = input; }

  AngleDegrees get_gamma() const { return gamma_; }
  void set_gamma(const AngleDegrees& input) { gamma_ = input; }

  AngleDegrees get_maxAlpha() const { return maxAlpha_; }
  void set_maxAlpha(const AngleDegrees& input) { maxAlpha_ = input; }

  AngleDegrees get_maxGamma() const { return maxGamma_; }
  void set_maxGamma(const AngleDegrees& input) { maxGamma_ = input; }

  CurrentFlow get_maxIdc() const { return maxIdc_; }
  void set_maxIdc(const CurrentFlow& input) { maxIdc_ = input; }

  AngleDegrees get_minAlpha() const { return minAlpha_; }
  void set_minAlpha(const AngleDegrees& input) { minAlpha_ = input; }

  AngleDegrees get_minGamma() const { return minGamma_; }
  void set_minGamma(const AngleDegrees& input) { minGamma_ = input; }

  CurrentFlow get_minIdc() const { return minIdc_; }
  void set_minIdc(const CurrentFlow& input) { minIdc_ = input; }

  CsOperatingModeKind get_operatingMode() const { return operatingMode_; }
  void set_operatingMode(const CsOperatingModeKind& input) {
    operatingMode_ = input;
  }

  CsPpccControlKind get_pPccControl() const { return pPccControl_; }
  void set_pPccControl(const CsPpccControlKind& input) { pPccControl_ = input; }

  CurrentFlow get_ratedIdc() const { return ratedIdc_; }
  void set_ratedIdc(const CurrentFlow& input) { ratedIdc_ = input; }

  AngleDegrees get_targetAlpha() const { return targetAlpha_; }
  void set_targetAlpha(const AngleDegrees& input) { targetAlpha_ = input; }

  AngleDegrees get_targetGamma() const { return targetGamma_; }
  void set_targetGamma(const AngleDegrees& input) { targetGamma_ = input; }

  CurrentFlow get_targetIdc() const { return targetIdc_; }
  void set_targetIdc(const CurrentFlow& input) { targetIdc_ = input; }

 private:
  AngleDegrees alpha_;
  AngleDegrees gamma_;
  AngleDegrees maxAlpha_;
  AngleDegrees maxGamma_;
  CurrentFlow maxIdc_;
  AngleDegrees minAlpha_;
  AngleDegrees minGamma_;
  CurrentFlow minIdc_;
  CsOperatingModeKind operatingMode_;
  CsPpccControlKind pPccControl_;
  CurrentFlow ratedIdc_;
  AngleDegrees targetAlpha_;
  AngleDegrees targetGamma_;
  CurrentFlow targetIdc_;
};

class VsConverter : public ACDCConverter {
 public:
  AngleDegrees get_delta() const { return delta_; }
  void set_delta(const AngleDegrees& input) { delta_ = input; }

  PU get_droop() const { return droop_; }
  void set_droop(const PU& input) { droop_ = input; }

  Resistance get_droopCompensation() const { return droopCompensation_; }
  void set_droopCompensation(const Resistance& input) {
    droopCompensation_ = input;
  }

  float get_maxModulationIndex() const { return maxModulationIndex_; }
  void set_maxModulationIndex(const float input) {
    maxModulationIndex_ = input;
  }

  CurrentFlow get_maxValveCurrent() const { return maxValveCurrent_; }
  void set_maxValveCurrent(const CurrentFlow& input) {
    maxValveCurrent_ = input;
  }

  VsPpccControlKind get_pPccControl() const { return pPccControl_; }
  void set_pPccControl(const VsPpccControlKind& input) { pPccControl_ = input; }

  VsQpccControlKind get_qPccControl() const { return qPccControl_; }
  void set_qPccControl(const VsQpccControlKind& input) { qPccControl_ = input; }

  PerCent get_qShare() const { return qShare_; }
  void set_qShare(const PerCent& input) { qShare_ = input; }

  ReactivePower get_targetQpcc() const { return targetQpcc_; }
  void set_targetQpcc(const ReactivePower& input) { targetQpcc_ = input; }

  Voltage get_targetUpcc() const { return targetUpcc_; }
  void set_targetUpcc(const Voltage& input) { targetUpcc_ = input; }

  Voltage get_uv() const { return uv_; }
  void set_uv(const Voltage& input) { uv_ = input; }

  float get_targetPowerFactorPcc() const { return targetPowerFactorPcc_; }
  void set_targetPowerFactorPcc(const float input) {
    targetPowerFactorPcc_ = input;
  }

  AngleDegrees get_targetPhasePcc() const { return targetPhasePcc_; }
  void set_targetPhasePcc(const AngleDegrees& input) {
    targetPhasePcc_ = input;
  }

  float get_targetPWMfactor() const { return targetPWMfactor_; }
  void set_targetPWMfactor(const float input) { targetPWMfactor_ = input; }

 private:
  AngleDegrees delta_;
  PU droop_;
  Resistance droopCompensation_;
  float maxModulationIndex_;
  CurrentFlow maxValveCurrent_;
  VsPpccControlKind pPccControl_;
  VsQpccControlKind qPccControl_;
  PerCent qShare_;
  ReactivePower targetQpcc_;
  Voltage targetUpcc_;
  Voltage uv_;
  float targetPowerFactorPcc_;
  AngleDegrees targetPhasePcc_;
  float targetPWMfactor_;
};

class ConnectivityNodeContainer : public PowerSystemResource {};

class EquipmentContainer : public ConnectivityNodeContainer {};

class DCEquipmentContainer : public EquipmentContainer {};

class DCLine : public DCEquipmentContainer {};

class DCConverterUnit : public DCEquipmentContainer {
 public:
  DCConverterOperatingModeKind get_operationMode() const {
    return operationMode_;
  }
  void set_operationMode(const DCConverterOperatingModeKind& input) {
    operationMode_ = input;
  }

 private:
  DCConverterOperatingModeKind operationMode_;
};

class DCConductingEquipment : public Equipment {
 public:
  Voltage get_ratedUdc() const { return ratedUdc_; }
  void set_ratedUdc(const Voltage& input) { ratedUdc_ = input; }

 private:
  Voltage ratedUdc_;
};

class DCLineSegment : public DCConductingEquipment {
 public:
  Capacitance get_capacitance() const { return capacitance_; }
  void set_capacitance(const Capacitance& input) { capacitance_ = input; }

  Inductance get_inductance() const { return inductance_; }
  void set_inductance(const Inductance& input) { inductance_ = input; }

  Resistance get_resistance() const { return resistance_; }
  void set_resistance(const Resistance& input) { resistance_ = input; }

  Length get_length() const { return length_; }
  void set_length(const Length& input) { length_ = input; }

 private:
  Capacitance capacitance_;
  Inductance inductance_;
  Resistance resistance_;
  Length length_;
};

class DCSwitch : public DCConductingEquipment {};

class DCBreaker : public DCSwitch {};

class DCDisconnector : public DCSwitch {};

class DCNode : public IdentifiedObject {};

class DCBaseTerminal : public ACDCTerminal {
 public:
  DCNode get_a_dc_nd() const { return a_dc_nd_; }
  void set_a_dc_nd(const DCNode& input) { a_dc_nd_ = input; }

 private:
  DCNode a_dc_nd_;
};

class DCTerminal : public DCBaseTerminal {
 public:
  DCConductingEquipment get_a_dc_ce() const { return a_dc_ce_; }
  void set_a_dc_ce(const DCConductingEquipment& input) { a_dc_ce_ = input; }

 private:
  DCConductingEquipment a_dc_ce_;
};

class ACDCConverterDCTerminal : public DCBaseTerminal {
 public:
  DCPolarityKind get_polarity() const { return polarity_; }
  void set_polarity(const DCPolarityKind& input) { polarity_ = input; }

  ACDCConverter get_a_acdc_conv() const { return a_acdc_conv_; }
  void set_a_acdc_conv(const ACDCConverter& input) { a_acdc_conv_ = input; }

 private:
  DCPolarityKind polarity_;
  ACDCConverter a_acdc_conv_;
};

class Terminal : public ACDCTerminal {
 public:
  PhaseCode get_phases() const { return phases_; }
  void set_phases(const PhaseCode& input) { phases_ = input; }

  ConductingEquipment get_a_ce() const { return a_ce_; }
  void set_a_ce(const ConductingEquipment& input) { a_ce_ = input; }

  ConnectivityNode get_a_cn() const { return a_cn_; }
  void set_a_cn(const ConnectivityNode& input) { a_cn_ = input; }

 private:
  PhaseCode phases_;
  ConductingEquipment a_ce_;
  ConnectivityNode a_cn_;
};

}  // namespace sku