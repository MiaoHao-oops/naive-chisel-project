package pipeline

import chisel3._
import funcunit._

class Decode extends Module {
  // Interface
  val fs2ds_bus = IO(Flipped(new FsToDsBus))
  val ds2es_bus = IO(new DsToEsBus)
  val ds2alu_bus = IO(new DsToAluBus)
  val rf_read = IO(Flipped(new RFRead))

  // Stage Control
  val valid = RegInit(false.B)
  val ds_ready_go = Wire(Bool())

  // Stage Register
  val data = Reg(new FsToDsData)

  // Functional Part
  val inst_add_w = Wire(Bool())

  fs2ds_bus.ds_allowin := ~valid | ds_ready_go & ds2es_bus.es_allowin

  ds2es_bus.ds_valid := valid & ds_ready_go
  ds2es_bus.data.pc := data.pc
  ds2es_bus.data.dest := "h0".U(5.W)

  ds2alu_bus.src1 := "h0".U(32.W)
  ds2alu_bus.src2 := "h0".U(32.W)
  ds2alu_bus.aluop(0) := inst_add_w
  ds2alu_bus.aluop(1) := inst_add_w
  ds2alu_bus.aluop(2) := inst_add_w
  ds2alu_bus.aluop(3) := inst_add_w
  ds2alu_bus.aluop(4) := inst_add_w
  ds2alu_bus.aluop(5) := inst_add_w
  ds2alu_bus.aluop(6) := inst_add_w
  ds2alu_bus.aluop(7) := inst_add_w
  ds2alu_bus.aluop(8) := inst_add_w
  ds2alu_bus.aluop(9) := inst_add_w
  ds2alu_bus.aluop(10) := inst_add_w
  ds2alu_bus.aluop(11) := inst_add_w

  rf_read.raddr(0) := "b00000".U(5.W)
  rf_read.raddr(1) := "b00000".U(5.W)

  ds_ready_go := true.B
  when (fs2ds_bus.ds_allowin) {
    valid := fs2ds_bus.fs_valid
  }

  when (fs2ds_bus.fs_valid && fs2ds_bus.ds_allowin) {
    data := fs2ds_bus.data
  }

  inst_add_w := fs2ds_bus.data.inst(31, 26) === 0.U &&
    fs2ds_bus.data.inst(25, 24) === 0.U &&
    fs2ds_bus.data.inst(23, 22) === 0.U &&
    fs2ds_bus.data.inst(21, 20) === 1.U &&
    fs2ds_bus.data.inst(19, 18) === 0.U &&
    fs2ds_bus.data.inst(17, 15) === 0.U
}
