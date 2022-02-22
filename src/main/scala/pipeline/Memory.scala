package pipeline

import chisel3._
import funcunit._

class Memory extends Module{
  // Interface
  val es2msbus = IO(Flipped(new EsToMsBus))
  val rf_write = IO(Flipped(new RFWrite))
  val debug = IO(new DebugInterface)

  // Stage Control
  val valid = RegInit(false.B)
  val ms_ready_go = Wire(Bool())
  val data = Reg(new EsToMsData)

  /* Interface Implement */
  es2msbus.ms_allowin := !valid || ms_ready_go
  rf_write := data.rf
  debug.pc := data.pc
  debug.rf := data.rf

  /* Stage Control Implement */
  ms_ready_go := true.B
  when (es2msbus.ms_allowin) {
    valid := es2msbus.es_valid
  }

  when (es2msbus.es_valid && es2msbus.ms_allowin) {
    data := es2msbus.data
  }
  
}
