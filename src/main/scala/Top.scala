import pipeline._
import chisel3._

class Top extends Module {
  val sram_req = IO(new SramReq)
  val sram_res = IO(new SramRes)

  val pfs = Module(new PreInstFetch)
  val fs = Module(new InstFetch)

  pfs.sram_req <> sram_req
  pfs.pfs2fs_bus <> fs.pfs2fs_bus
  fs.sram_res <> sram_res

  fs.fs2ds_bus.ds_allowin := true.B
}
