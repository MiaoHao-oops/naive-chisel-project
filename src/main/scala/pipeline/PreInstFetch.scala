package pipeline

import chisel3._

class PreInstFetch extends Module {
  // Interface
  val pfs2fs_bus = IO(new PfsToFsBus)
  val sram_req = IO(new SramReq)

  // Stage Control
  val to_pfs_valid = Wire(Bool())
  val pfs_allowin = Wire(Bool())
  val valid = RegInit(false.B)
  val pfs_ready_go = Wire(Bool())

  // Stage Register
  val data = RegInit("h1bff_fffc".U)

  pfs2fs_bus.pfs_valid := valid & pfs_ready_go
  pfs2fs_bus.data.pc := data
  sram_req.en := to_pfs_valid & pfs_allowin
  sram_req.wen := 0.U
  sram_req.addr := data
  sram_req.wdata := 0.U

  to_pfs_valid := true.B
  pfs_allowin := ~valid | pfs_ready_go & pfs2fs_bus.fs_allowin
  pfs_ready_go := true.B
  when (pfs_allowin) {
    valid := to_pfs_valid
  }

  when (to_pfs_valid && pfs_allowin) {
    data := data + "h4".U(32.W)
  }
}
