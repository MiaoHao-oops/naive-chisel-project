package pipeline

import chisel3._
import chisel3.stage
import chisel3.stage.ChiselStage

class InstFetch extends Module {
  // Interface
  val pfs2fs_bus = IO(Flipped(new PfsToFsBus))
  val fs2ds_bus = IO(new FsToDsBus)
  val sram_res = IO(new SramRes)

  // Stage Control
  val valid = RegInit(false.B)
  val fs_ready_go = Wire(Bool())

  // Stage Register
  val data = Reg(new PfsToFsData)

  pfs2fs_bus.fs_allowin := ~valid | fs_ready_go & fs2ds_bus.ds_allowin
  fs2ds_bus.fs_valid := valid & fs_ready_go
  fs2ds_bus.data.inst := sram_res.rdata
  fs2ds_bus.data.pc := data.pc

  fs_ready_go := true.B
  when (pfs2fs_bus.fs_allowin) {
    valid := pfs2fs_bus.pfs_valid
  }

  when (pfs2fs_bus.pfs_valid && pfs2fs_bus.fs_allowin) {
    data := pfs2fs_bus.data
  }
}
