package funcunit

import chisel3._
import chisel3.util._
import funcunit.BranchControl.{TYPE_BEQ, TYPE_BNE, TYPE_JAL, TYPE_JR, NR_BR_INST}
import pipeline._

object BranchControl extends Enumeration {
  val TYPE_BEQ, TYPE_BNE, TYPE_JAL, TYPE_JR, NR_BR_INST = Value
//  val TYPE_BEQ = 0
//  val TYPE_BNE = 1

//  val TYPE_JAL = 2
//  val TYPE_JR  = 3

//  val NR_BR_INST = 4
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
  br_target := 0.U

  when (es2bc_bus.data.br_type(TYPE_BEQ.id) && es2bc_bus.data.br_cond.zero) {
    br_taken := true.B
    br_target := ds2bc_bus.data.pc + es2bc_bus.data.br_offset
  }

  when (es2bc_bus.data.br_type(TYPE_BNE.id) && !es2bc_bus.data.br_cond.zero) {
    br_taken := true.B
    br_target := ds2bc_bus.data.pc + es2bc_bus.data.br_offset
  }

  when (es2bc_bus.data.br_type(TYPE_JAL.id)) {
    br_taken := true.B
    br_target := Cat(ds2bc_bus.data.pc(31, 28), es2bc_bus.data.br_offset(27, 0))
  }

  when (es2bc_bus.data.br_type(TYPE_JR.id)) {
    br_taken := true.B
    br_target := es2bc_bus.data.br_offset
  }
}
