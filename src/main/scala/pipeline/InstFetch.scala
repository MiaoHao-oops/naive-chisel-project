package pipeline

import chisel3._

class InstFetch extends Module{
  // Interface
  val fs2dsbus = IO(new FsToDsBus)
  val inst_sram = IO(new SramInterface)

  // Stage Control
  val to_fs_valid = Wire(Bool())
  val fs_allowin = Wire(Bool())
  val fs_ready_go = Wire(Bool())
  val valid = RegInit(false.B)
  val next_pc = Wire(UInt(32.W))
  val pc = RegInit(UInt(32.W), "hbfbf_fffc".U)

  /* Interface */
  fs2dsbus.fs_valid := valid && fs_ready_go
  fs2dsbus.data.pc := pc
  fs2dsbus.data.inst := inst_sram.rdata

  inst_sram.en := to_fs_valid && fs_allowin
  inst_sram.wen := 0.U
  inst_sram.addr := next_pc
  inst_sram.wdata := 0.U

  /* Stage Control */
  to_fs_valid := true.B
  fs_allowin := !valid || fs_ready_go && fs2dsbus.ds_allowin
  fs_ready_go := true.B
  when (fs_allowin) {
    valid := to_fs_valid
  }

  next_pc := pc + 4.U
  when (to_fs_valid && fs_allowin) {
    pc := next_pc
  }
}
