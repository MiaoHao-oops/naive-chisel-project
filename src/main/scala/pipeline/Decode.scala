package pipeline

import chisel3._
import funcunit._

class Decode extends Module {
  // Interface
  val fs2ds_bus = IO(Flipped(new FsToDsBus))
  val ds2es_bus = IO(new DsToEsBus)
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
  ds2es_bus.data.dest := 0.U
  ds2es_bus.data.req_mem := false.B

  ds2es_bus.data.src(0) := 0.U
  ds2es_bus.data.src(1) := 0.U
  ds2es_bus.data.aluop(0) := inst_add_w
  ds2es_bus.data.aluop(1) := inst_add_w
  ds2es_bus.data.aluop(2) := inst_add_w
  ds2es_bus.data.aluop(3) := inst_add_w
  ds2es_bus.data.aluop(4) := inst_add_w
  ds2es_bus.data.aluop(5) := inst_add_w
  ds2es_bus.data.aluop(6) := inst_add_w
  ds2es_bus.data.aluop(7) := inst_add_w
  ds2es_bus.data.aluop(8) := inst_add_w
  ds2es_bus.data.aluop(9) := inst_add_w
  ds2es_bus.data.aluop(10) := inst_add_w
  ds2es_bus.data.aluop(11) := inst_add_w

  rf_read.raddr(0) := 0.U
  rf_read.raddr(1) := 0.U

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
