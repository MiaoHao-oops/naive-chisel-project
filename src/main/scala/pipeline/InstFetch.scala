package pipeline

import chisel3._

class InstFetch extends Module{
  // Interface
  val fs2ds_bus = IO(new FsToDsBus)
  val bc2fs_bus = IO(Flipped(new BcToFsBus))
  val inst_sram = IO(new SramInterface)

  // Stage Control
  val to_fs_valid = Wire(Bool())
  val fs_allowin = Wire(Bool())
  val fs_ready_go = Wire(Bool())
  val valid = RegInit(false.B)
  val next_pc = Wire(UInt(32.W))
  val seq_pc = Wire(UInt(32.W))
  val pc = RegInit(UInt(32.W), "hbfbf_fffc".U)

  /* Interface */
  fs2ds_bus.fs_valid := valid && fs_ready_go
  fs2ds_bus.data.pc := pc
  fs2ds_bus.data.inst := inst_sram.rdata

  inst_sram.en := to_fs_valid && fs_allowin
  inst_sram.wen := 0.U
  inst_sram.addr := next_pc
  inst_sram.wdata := 0.U

  /* Stage Control */
  to_fs_valid := true.B
  fs_allowin := !valid || fs_ready_go && fs2ds_bus.ds_allowin
  fs_ready_go := true.B
  when (fs_allowin) {
    valid := to_fs_valid
  }

  seq_pc := pc + 4.U
  next_pc := Mux(bc2fs_bus.br_taken, bc2fs_bus.br_target, seq_pc)
  when (to_fs_valid && fs_allowin) {
    pc := next_pc
  }
}
