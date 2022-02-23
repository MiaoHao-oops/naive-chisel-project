package pipeline

import Chisel.Fill
import chisel3._
import funcunit._

class Memory extends Module{
  // Interface
  val data_sram_res = IO(new SramRes)
  val es2ms_bus = IO(Flipped(new EsToMsBus))
  val ms2fp_bus = IO(new MsToFPBus)
  val rf_write = IO(Flipped(new RFWrite))
  val debug = IO(new DebugInterface)

  // Stage Control
  val valid = RegInit(false.B)
  val ms_ready_go = Wire(Bool())
  val data = Reg(new EsToMsData)

  /* Interface Implement */
  es2ms_bus.ms_allowin := !valid || ms_ready_go

  ms2fp_bus.data.rf_wnum := rf_write.wnum
  ms2fp_bus.data.rf_wdata := rf_write.wdata
  ms2fp_bus.data.addr_valid := valid && data.rf.wen(0)
  ms2fp_bus.data.data_valid := valid && data.rf.wen(0)

  rf_write.wen := data.rf.wen & Fill(4, valid)
  rf_write.wdata := Mux(data.req_mem, data_sram_res.rdata, data.rf.wdata)
  rf_write.wnum := data.rf.wnum

  debug.pc := data.pc
  debug.rf := rf_write


  /* Stage Control Implement */
  ms_ready_go := true.B
  when (es2ms_bus.ms_allowin) {
    valid := es2ms_bus.es_valid
  }

  when (es2ms_bus.es_valid && es2ms_bus.ms_allowin) {
    data := es2ms_bus.data
  }
  
}
