package pipeline

import chisel3._
import chisel3.util._
import funcunit._
import Instructions._

class Decode extends Module {
  // Interface
  val fs2ds_bus = IO(Flipped(new FsToDsBus))
  val ds2es_bus = IO(new DsToEsBus)
  val rf_read = IO(Flipped(new RFRead))

  // Stage Control
  val valid = RegInit(false.B)
  val ds_ready_go = Wire(Bool())

  // Stage Register
  val data = Reg(new FsToDsData)

  // Functional Part
  val inst = fs2ds_bus.data.inst
  val rd = inst(4, 0)
  val rj = inst(9, 5)
  val rk = inst(14, 10)
  val ra = inst(19, 15)
  val si12 = Cat(Fill(20, inst(21)), inst(21, 10))
  val ui12 = Cat(Fill(20, 0.U(1.W)), inst(21, 10))
  val inst_add_w = WireInit(false.B)

  // Interface Implement
  fs2ds_bus.ds_allowin := ~valid | ds_ready_go & ds2es_bus.es_allowin

  ds2es_bus.ds_valid := valid & ds_ready_go
  ds2es_bus.data.pc := data.pc
  ds2es_bus.data.dest := rd
  ds2es_bus.data.req_mem := false.B
  ds2es_bus.data.src(0) := rf_read.raddr(0)
  ds2es_bus.data.src(1) := rf_read.raddr(1)
  for (i <- 0 to 11) {
    ds2es_bus.data.aluop(i) := false.B
  }

  rf_read.raddr(0) := rk
  rf_read.raddr(1) := rj

  // Stage Control Implement
  ds_ready_go := true.B
  when (fs2ds_bus.ds_allowin) {
    valid := fs2ds_bus.fs_valid
  }

  // Stage Register Implement
  when (fs2ds_bus.fs_valid && fs2ds_bus.ds_allowin) {
    data := fs2ds_bus.data
  }

  // Functional Part Implement
  when (inst === ADD_W) {
    ds2es_bus.data.aluop(0) := true.B
  }
}
