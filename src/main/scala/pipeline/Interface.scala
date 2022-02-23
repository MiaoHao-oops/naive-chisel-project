package pipeline

import chisel3._
import funcunit._
import funcunit.BranchControl._
import funcunit.LdStControl._

// sram insterface
class SramReq extends Bundle {
  val en = Output(Bool())
  val wen = Output(UInt(4.W))
  val addr = Output(UInt(32.W))
  val wdata = Output(UInt(32.W))
}

class SramRes extends Bundle {
  val rdata = Input(UInt(32.W))
}

class SramInterface extends Bundle {
  val en = Output(Bool())
  val wen = Output(UInt(4.W))
  val addr = Output(UInt(32.W))
  val wdata = Output(UInt(32.W))
  val rdata = Input(UInt(32.W))
}
// inst-fetch and decode
class FsToDsData extends Bundle {
  val pc = Output(UInt(32.W))
  val inst = Output(UInt(32.W))
}

class FsToDsBus extends Bundle {
  val ds_allowin = Input(Bool())
  val fs_valid = Output(Bool())
  val data = new FsToDsData
}

// decode and execute
class DsToEsData extends Bundle {
  val pc = Output(UInt(32.W))
  // RF Interface
  val rf_wnum = Output(UInt(5.W))
  val rf_wen = Output(Bool())

  // ALU Interface
  val src = Output(Vec(2, UInt(32.W)))
  val aluop = Output(Vec(12, Bool()))

  // inst-type
  val br_type = Output(Vec(NR_BR_INST.id, Bool()))
  val br_offset = Output(UInt(32.W))

  val ld_st_type = Output(Vec(NR_LD_ST_INST.id, Bool()))

  // Sram Interface
  val req_mem = Output(Bool())
}

class DsToEsBus extends Bundle {
  val es_allowin = Input(Bool())
  val ds_valid = Output(Bool())
  val data = new DsToEsData
}

class DsToBcData extends Bundle {
  val pc = Output(UInt(32.W))
}

class DsToBcBus extends Bundle {
  val data = new DsToBcData
}

// execute and memory
class EsToMsData extends Bundle {
  val pc = Output(UInt(32.W))
  val rf = new FlippedRFWrite
  val req_mem = Output(Bool())
}

class EsToMsBus extends Bundle {
  val ms_allowin = Input(Bool())
  val es_valid = Output(Bool())
  val data = new EsToMsData
}

class EsToBcData extends Bundle {
  val br_offset = Output(UInt(32.W))
  val br_cond = new Bundle() {
    val result = Output(UInt(32.W))
    val carryout = Output(Bool())
    val overflow = Output(Bool())
    val zero = Output(Bool())
  }
  val br_type = Output(Vec(NR_BR_INST.id, Bool()))
  val valid = Output(Bool())
}

class EsToBcBus extends Bundle {
  val data = new EsToBcData
}

class EsToFPBus extends Bundle {
  val data = new StageToFPData
}

// memory and forward
class MsToFPBus extends Bundle {
  val data = new StageToFPData
}

// Debug Interface
class DebugInterface extends Bundle {
  val pc = Output(UInt(32.W))
  val rf = Flipped(new RFWrite)
}

class ExternalInterrupt extends Bundle {
  val int = Input(UInt(6.W))
}

// Branch Control Interface
class BcToDsBus extends Bundle {
  val br_taken = Output(Bool())
}

class BcToFsBus extends Bundle {
  val br_taken = Output(Bool())
  val br_target = Output(UInt(32.W))
}

// Forward Path Interface
class StageToFPData extends Bundle {
  val rf_wnum = Output(UInt(5.W))
  val rf_wdata = Output(UInt(32.W))
  val data_valid = Output(Bool())
  val addr_valid = Output(Bool())
}

class DsToFPBus extends Bundle {
  val rf_read = Flipped(new RFRead)
  val stall = Input(Bool())
}
