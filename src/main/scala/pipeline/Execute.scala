package pipeline

import chisel3._
import chisel3.util.Fill
import funcunit._

class Execute extends Module{
  // Interface
  val ds2es_bus = IO(Flipped(new DsToEsBus))
  val es2ms_bus = IO(new EsToMsBus)
  val es2bc_bus = IO(new EsToBcBus)
  val es2fp_bus = IO(new EsToFPBus)
  val data_sram_req = IO(new SramReq)

  // Stage Control
  val valid = RegInit(false.B)
  val es_ready_go = Wire(Bool())
  val data = Reg(new DsToEsData)

  // Functional Part
  val alu = Module(new Alu)

  /* Interface */
  ds2es_bus.es_allowin := !valid || es_ready_go && es2ms_bus.ms_allowin

  es2ms_bus.es_valid := valid && es_ready_go
  es2ms_bus.data.pc := data.pc
  es2ms_bus.data.rf.wen := Fill(4, data.rf_wen)
  es2ms_bus.data.rf.wnum := data.rf_wnum
  es2ms_bus.data.rf.wdata := alu.io.alu_result
  es2ms_bus.data.req_mem := data.req_mem

  es2bc_bus.data.br_offset := data.br_offset
  es2bc_bus.data.br_cond.result := alu.io.alu_result
  es2bc_bus.data.br_cond.carryout := alu.io.alu_carryout
  es2bc_bus.data.br_cond.overflow := alu.io.alu_overflow
  es2bc_bus.data.br_cond.zero := alu.io.alu_zero
  es2bc_bus.data.br_type := data.br_type
  es2bc_bus.data.valid := valid

  es2fp_bus.data.rf_wnum := data.rf_wnum
  es2fp_bus.data.rf_wdata := alu.io.alu_result
  es2fp_bus.data.addr_valid := valid && data.rf_wen
  es2fp_bus.data.data_valid := valid && !(data.ld_st_type.asUInt.orR) && data.rf_wen

  data_sram_req.en := data.req_mem && valid
  data_sram_req.wen := Mux(data.ld_st_type(1), "b1111".U, 0.U)
  data_sram_req.addr := alu.io.mem_addr_result
  data_sram_req.wdata := data.br_offset

  /* Stage Control Implement */
  es_ready_go := true.B
  when (ds2es_bus.es_allowin) {
    valid := ds2es_bus.ds_valid
  }

  when (ds2es_bus.ds_valid && ds2es_bus.es_allowin) {
    data := ds2es_bus.data
  }

  /* Functional Part */
  val alu_ctrl = data.aluop.asUInt
  alu.io.alu_src1 := data.src(0)
  alu.io.alu_src2 := data.src(1)
  alu.io.alu_ctrl := alu_ctrl
}
