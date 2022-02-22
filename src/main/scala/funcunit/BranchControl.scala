package funcunit

import chisel3._
import pipeline._

object BranchControl {
  val TYPE_BEQ = 0
}

class BranchControl extends RawModule{
  val ds2bc_bus = IO(Flipped(new DsToBcBus))
  val es2bc_bus = IO(Flipped(new EsToBcBus))
  val bc2ds_bus = IO(new BcToDsBus)
  val bc2fs_bus = IO(new BcToFsBus)

  val br_target = Wire(UInt(32.W))
  val br_taken = Wire(Bool())

  bc2ds_bus.br_taken := br_taken & es2bc_bus.data.valid
  bc2fs_bus.br_taken := br_taken & es2bc_bus.data.valid
  bc2fs_bus.br_target := br_target

  br_taken := false.B
  br_target := ds2bc_bus.data.pc + es2bc_bus.data.offset

  when (es2bc_bus.data.br_type(0) && es2bc_bus.data.cond.zero) {
    br_taken := true.B
  }
}
