package pipeline

import chisel3._
import chisel3.util._
import funcunit._
import Instructions._

class Decode extends Module {
  // Interface
  val fs2ds_bus = IO(Flipped(new FsToDsBus))
  val ds2es_bus = IO(new DsToEsBus)
  val bc2ds_bus = IO(Flipped(new BcToDsBus))
  val ds2bc_bus = IO(new DsToBcBus)
  val rf_read = IO(Flipped(new RFRead))

  // Stage Control
  val valid = RegInit(false.B)
  val ds_ready_go = Wire(Bool())
  val data = Reg(new FsToDsData)

  // Functional Part
  val inst = data.inst
  val rs = inst(25, 21)
  val rt = inst(20, 16)
  val rd = inst(15, 11)
  val simm16 = Cat(Fill(16, inst(15)), inst(15, 0))
  val simm18 = Cat(Fill(14, inst(15)), inst(15, 0), Fill(2, false.B))

  /* Interface Implement */
  fs2ds_bus.ds_allowin := !valid || ds_ready_go && ds2es_bus.es_allowin

  ds2es_bus.ds_valid := valid && ds_ready_go
  ds2es_bus.data.pc := data.pc
  ds2es_bus.data.rf_wnum := 0.U
  ds2es_bus.data.rf_wen := false.B
  ds2es_bus.data.req_mem := false.B
  ds2es_bus.data.src(0) := 0.U
  ds2es_bus.data.src(1) := 0.U
  for (i <- 0 to 11) {
    ds2es_bus.data.aluop(i) := false.B
  }
  for (i <- 0 to 0) {
    ds2es_bus.data.br_type(i) := false.B
  }
  ds2es_bus.data.br_offset := 0.U

  ds2bc_bus.data.pc := data.pc

  rf_read.raddr(0) := rs
  rf_read.raddr(1) := rt

  /* Stage Control Implement */
  ds_ready_go := true.B
  when (bc2ds_bus.br_taken) {
    valid := false.B
  }.elsewhen(fs2ds_bus.ds_allowin) {
    valid := fs2ds_bus.fs_valid
  }

  when (fs2ds_bus.fs_valid && fs2ds_bus.ds_allowin) {
    data := fs2ds_bus.data
  }

  /* Functional Part Implement */

  // R-type
  when (inst === ADD) {
    ds2es_bus.data.aluop(0) := true.B
    ds2es_bus.data.src(0) := rf_read.rdata(0)
    ds2es_bus.data.src(1) := rf_read.rdata(1)
    ds2es_bus.data.rf_wen := true.B
    ds2es_bus.data.rf_wnum := rd
  }

  // I-type
  when (inst === ADDI) {
    ds2es_bus.data.aluop(0) := true.B
    ds2es_bus.data.src(0) := rf_read.rdata(0)
    ds2es_bus.data.src(1) := simm16
    ds2es_bus.data.rf_wen := true.B
    ds2es_bus.data.rf_wnum := rt
  }

  when (inst === ADDIU) {
    ds2es_bus.data.aluop(0) := true.B
    ds2es_bus.data.src(0) := rf_read.rdata(0)
    ds2es_bus.data.src(1) := simm16
    ds2es_bus.data.rf_wen := true.B
    ds2es_bus.data.rf_wnum := rt
  }

  when (inst === BEQ) {
    ds2es_bus.data.aluop(1) := true.B
    ds2es_bus.data.src(0) := rf_read.rdata(0)
    ds2es_bus.data.src(1) := rf_read.rdata(1)
    ds2es_bus.data.br_offset := simm18
    ds2es_bus.data.br_type(0) := true.B
  }
}
