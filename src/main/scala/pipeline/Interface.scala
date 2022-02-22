package pipeline

import chisel3._
import funcunit._

// sram insterface
trait SramReq extends Bundle {
  val en = Output(Bool())
  val wen = Output(UInt(4.W))
  val addr = Output(UInt(32.W))
  val wdata = Output(UInt(32.W))
}

trait SramRes extends Bundle {
  val rdata = Input(UInt(32.W))
}

class SramInterface extends Bundle with SramReq with SramRes

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
  val br_type = Output(Vec(1, Bool()))

  // Sram Interface
  val req_mem = Output(Bool())
}

class DsToEsBus extends Bundle {
  val es_allowin = Input(Bool())
  val ds_valid = Output(Bool())
  val data = new DsToEsData
}

// execute and memory
class EsToMsData extends Bundle {
  val pc = Output(UInt(32.W))

  // RF Interface
  val rf = new FlippedRFWrite
}

class EsToMsBus extends Bundle {
  val ms_allowin = Input(Bool())
  val es_valid = Output(Bool())
  val data = new EsToMsData
}

class EsToBcData extends Bundle {
  val pc = Output(UInt(32.W))
  val offset = Output(UInt(32.W))
  val cond = new Bundle() {
    val result = Output(UInt(32.W))
    val carryout = Output(Bool())
    val overflow = Output(Bool())
  }
  val br_type = Output(Vec(1, Bool()))
}

class EsToBcBus extends Bundle {
  val data = new EsToBcData
}

// Debug Interface
class DebugInterface extends Bundle {
  val pc = Output(UInt(32.W))
  val rf = Flipped(new RFWrite)
}

class ExternalInterrupt extends Bundle {
  val int = Input(UInt(6.W))
}
