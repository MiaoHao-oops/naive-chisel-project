package pipeline

import chisel3._
import chisel3.util._
import funcunit._
import funcunit.Alu._
import funcunit.BranchControl._
import funcunit.LdStControl._
import Instructions._

class Decode extends Module {
  // Interface
  val fs2ds_bus = IO(Flipped(new FsToDsBus))
  val ds2es_bus = IO(new DsToEsBus)
  val bc2ds_bus = IO(Flipped(new BcToDsBus))
  val ds2bc_bus = IO(new DsToBcBus)
  val data_fetch = IO(new DsToFPBus)

  // Stage Control
  val valid = RegInit(false.B)
  val ds_ready_go = Wire(Bool())
  val data = Reg(new FsToDsData)

  // Functional Part
  val inst = data.inst
  val rs = inst(25, 21)
  val rt = inst(20, 16)
  val rd = inst(15, 11)
  val sa = inst(10, 6)
  val uimm16 = Cat(Fill(16, false.B), inst(15, 0))
  val simm16 = Cat(Fill(16, inst(15)), inst(15, 0))
  val simm18 = Cat(Fill(14, inst(15)), inst(15, 0), Fill(2, false.B))
  val imm28 = Cat(Fill(4, false.B), inst(25, 0), Fill(2, false.B))

  // Operand Helper Methods
  def ITypeOp(aluop: Int): Unit = {
    ds2es_bus.data.aluop(aluop) := true.B
    ds2es_bus.data.src(0) := data_fetch.rf_read.rdata(0)
    ds2es_bus.data.src(1) := simm16
    ds2es_bus.data.rf_wen := true.B
    ds2es_bus.data.rf_wnum := rt
  }

  def ITypeOp_Br(aluop: Int, br_type: Int): Unit = {
    ds2es_bus.data.aluop(aluop) := true.B
    ds2es_bus.data.src(0) := data_fetch.rf_read.rdata(0)
    ds2es_bus.data.src(1) := data_fetch.rf_read.rdata(1)
    ds2es_bus.data.br_offset := simm18
    ds2es_bus.data.br_type(br_type) := true.B
  }

  def ItypeOp_Ld(ld_type: Int): Unit = {
    ds2es_bus.data.src(0) := data_fetch.rf_read.rdata(0)
    ds2es_bus.data.src(1) := simm16
    ds2es_bus.data.rf_wen := true.B
    ds2es_bus.data.rf_wnum := rt
    ds2es_bus.data.req_mem := true.B
    ds2es_bus.data.ld_st_type(ld_type) := true.B
  }

  def ItypeOp_St(st_type: Int): Unit = {
    ds2es_bus.data.src(0) := data_fetch.rf_read.rdata(0)
    ds2es_bus.data.src(1) := simm16
    ds2es_bus.data.req_mem := true.B
    ds2es_bus.data.ld_st_type(st_type) := true.B
    // now br_offset is store data
    ds2es_bus.data.br_offset := data_fetch.rf_read.rdata(1)
  }

  def RTypeOp(aluop: Int): Unit = {
    ds2es_bus.data.aluop(aluop) := true.B
    ds2es_bus.data.src(0) := data_fetch.rf_read.rdata(0)
    ds2es_bus.data.src(1) := data_fetch.rf_read.rdata(1)
    ds2es_bus.data.rf_wen := true.B
    ds2es_bus.data.rf_wnum := rd
  }

  def RTypeOp_ShiftSa(aluop: Int): Unit = {
    ds2es_bus.data.aluop(aluop) := true.B
    ds2es_bus.data.src(0) := sa
    ds2es_bus.data.src(1) := data_fetch.rf_read.rdata(1)
    ds2es_bus.data.rf_wen := true.B
    ds2es_bus.data.rf_wnum := rd
  }

  def RTypeOp_Jr(): Unit = {
    ds2es_bus.data.br_type(TYPE_JR.id) := true.B
    ds2es_bus.data.br_offset := data_fetch.rf_read.rdata(0)
  }

  def JTypeOp_Jal(): Unit = {
    ds2es_bus.data.aluop(OP_ADD) := true.B
    ds2es_bus.data.src(0) := data.pc
    ds2es_bus.data.src(1) := 8.U
    ds2es_bus.data.rf_wen := true.B
    ds2es_bus.data.rf_wnum := 31.U
    ds2es_bus.data.br_type(TYPE_JAL.id) := true.B
    ds2es_bus.data.br_offset := imm28
  }

  def DsToEsBusDataInit(): Unit = {
    ds2es_bus.data.pc := data.pc
    ds2es_bus.data.rf_wnum := 0.U
    ds2es_bus.data.rf_wen := false.B
    ds2es_bus.data.req_mem := false.B
    ds2es_bus.data.src(0) := 0.U
    ds2es_bus.data.src(1) := 0.U
    for (i <- 0 to 11) {
      ds2es_bus.data.aluop(i) := false.B
    }
    for (i <- 0 until NR_BR_INST.id) {
      ds2es_bus.data.br_type(i) := false.B
    }
    for (i <- 0 until NR_LD_ST_INST.id) {
      ds2es_bus.data.ld_st_type(i) := false.B
    }
    ds2es_bus.data.br_offset := 0.U
  }

  /* Interface Implement */
  fs2ds_bus.ds_allowin := !valid || ds_ready_go && ds2es_bus.es_allowin

  ds2es_bus.ds_valid := valid && ds_ready_go
  DsToEsBusDataInit()

  ds2bc_bus.data.pc := data.pc

  data_fetch.rf_read.raddr(0) := rs
  data_fetch.rf_read.raddr(1) := rt

  /* Stage Control Implement */
  ds_ready_go := !data_fetch.stall
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
    RTypeOp(OP_ADD)
  }

  when (inst === ADDU) {
    RTypeOp(OP_ADD)
  }

  when (inst === SUBU) {
    RTypeOp(OP_SUB)
  }

  when (inst === AND) {
    RTypeOp(OP_AND)
  }

  when (inst === OR) {
    RTypeOp(OP_OR)
  }

  when (inst === XOR) {
    RTypeOp(OP_XOR)
  }

  when (inst === NOR) {
    RTypeOp(OP_NOR)
  }

  when (inst === SLT) {
    RTypeOp(OP_SLT)
  }

  when (inst === SLTU) {
    RTypeOp(OP_SLTU)
  }

  when (inst === SLL) {
    RTypeOp_ShiftSa(OP_SLL)
  }

  when (inst === SRL) {
    RTypeOp_ShiftSa(OP_SRL)
  }

  when (inst === SRA) {
    RTypeOp_ShiftSa(OP_SRA)
  }

  when (inst === JR) {
    RTypeOp_Jr()
  }

  // I-type
  when (inst === BEQ) {
    ITypeOp_Br(OP_SUB, TYPE_BEQ.id)
  }

  when (inst === BNE) {
    ITypeOp_Br(OP_SUB, TYPE_BNE.id)
  }

  when (inst === ADDI) {
    ITypeOp(OP_ADD)
  }

  when (inst === ADDIU) {
    ITypeOp(OP_ADD)
  }

  when (inst === ORI) {
    ITypeOp(OP_OR)
  }

  when (inst === LUI) {
    ITypeOp(OP_LUI)
  }

  when (inst === LW) {
    ItypeOp_Ld(TYPE_LW.id)
  }

  when (inst === SW) {
    ItypeOp_St(TYPE_SW.id)
  }

  // J-type
  when (inst === JAL) {
    JTypeOp_Jal()
  }
}
