package pipeline

import chisel3._

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

// pre-inst-fetch and inst-fetch
class PfsToFsData extends Bundle {
  val pc = Output(UInt(32.W))
}

class PfsToFsBus extends Bundle {
  val fs_allowin = Input(Bool())
  val pfs_valid = Output(Bool())
  val data = new PfsToFsData
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
  val dest = Output(UInt(5.W))
  val res = Output(UInt(32.W))
  val req_mem = Output(Bool())
}

class DsToAluBus extends Bundle {
  val src1 = Output(UInt(32.W))
  val src2 = Output(UInt(32.W))
  val aluop = Output(Vec(12, Bool()))
  val res = Input(UInt(32.W))
}

class DsToEsBus extends Bundle {
  val es_allowin = Input(Bool())
  val ds_valid = Output(Bool())
  val data = new DsToEsData
}
