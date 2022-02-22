package funcunit

import chisel3._
import chisel3.util._
import pipeline._

//class Alu extends RawModule {
//  val io = IO(new AluInterface)
//
//  val res = Wire(Vec(12, UInt(32.W)))
//  val shamt = Wire(UInt(5.W))
//
//  shamt := io.src(1)(4, 0)
//
//  for (i <- 0 to 11) {
//    res(i) := 0.U
//  }
//
//  res(0) := io.src(0) + io.src(1)               // add
//  res(1) := io.src(0) - io.src(1)               // sub
//  res(2) := io.src(0).asSInt < io.src(1).asSInt // slt
//  res(3) := io.src(0) < io.src(1)               // sltu
//  res(4) := io.src(0) & io.src(1)               // and
//  res(5) := ~(io.src(0) | io.src(1))            // nor
//  res(6) := io.src(0) | io.src(1)               // or
//  res(7) := io.src(0) ^ io.src(1)               // xor
//  res(8) := io.src(0) << shamt                  // sll
//  res(9) := io.src(0) >> shamt                  // srl
//  res(10) := (io.src(0).asSInt >> shamt).asUInt // sra
//  res(11) := Cat(io.src(1)(15, 0), 0.U(16.W))   // lui
//
//  io.res := Mux1H(io.aluop, res)
//  io.carry := false.B
//  io.overflow := false.B
//}

object Alu {
  val OP_ADD = 0
  val OP_SUB = 1
  val OP_AND = 2
  val OP_NOR = 3
  val OP_OR  = 4
  val OP_XOR = 5
  val OP_SLT = 6
  val OP_SLTU= 7
  val OP_SLL = 8
  val OP_SRA = 9
  val OP_SRL = 10
  val OP_LUI = 11
}

class Alu extends BlackBox {
  val io = IO(new Bundle() {
    val alu_src1 = Input(UInt(32.W))
    val alu_src2 = Input(UInt(32.W))
    val alu_ctrl = Input(UInt(12.W))

    val alu_overflow = Output(Bool())
    val alu_carryout = Output(Bool())
    val alu_zero = Output(Bool())
    val alu_result = Output(UInt(32.W))
    val mem_addr_result = Output(UInt(32.W))
  })
}
