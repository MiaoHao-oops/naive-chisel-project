package pipeline

import chisel3._
import chisel3.util.Fill
import funcunit._

class Execute extends Module{
  // Interface
  val ds2esbus = IO(Flipped(new DsToEsBus))
  val es2msbus = IO(new EsToMsBus)
  val es2bcbus = IO(new EsToBcBus)
  val data_sram = IO(new SramInterface)

  // Stage Control
  val valid = RegInit(false.B)
  val es_ready_go = Wire(Bool())
  val data = Reg(new DsToEsData)

  // Functional Part
  val alu = Module(new Alu)

  /* Interface */
  ds2esbus.es_allowin := !valid || es_ready_go && es2msbus.ms_allowin

  es2msbus.es_valid := valid && es_ready_go
  es2msbus.data.pc := data.pc
  es2msbus.data.rf.wen := Fill(4, data.rf_wen)
  es2msbus.data.rf.wnum := data.rf_wnum
  es2msbus.data.rf.wdata := Mux(data.req_mem, data_sram.rdata, alu.io.alu_result)

  es2bcbus.data.pc := data.pc
  es2bcbus.data.offset := data.src(1)
  es2bcbus.data.cond.result := alu.io.alu_result
  es2bcbus.data.cond.carryout := alu.io.alu_carryout
  es2bcbus.data.cond.overflow := alu.io.alu_overflow
  es2bcbus.data.br_type := data.br_type

  data_sram.en := data.req_mem
  data_sram.wen := 0.U
  data_sram.addr := alu.io.mem_addr_result
  data_sram.wdata := 0.U

  /* Stage Control Implement */
  es_ready_go := true.B
  when (ds2esbus.es_allowin) {
    valid := ds2esbus.ds_valid
  }

  when (ds2esbus.ds_valid && ds2esbus.es_allowin) {
    data := ds2esbus.data
  }

  /* Functional Part */
  val alu_ctrl = data.aluop.asUInt
  alu.io.alu_src1 := data.src(0)
  alu.io.alu_src2 := data.src(1)
  alu.io.alu_ctrl := alu_ctrl
}
