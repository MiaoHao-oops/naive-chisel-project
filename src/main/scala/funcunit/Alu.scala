package funcunit

import chisel3._
import chisel3.util._
import pipeline._

class Alu extends Module {
  val io = IO(Flipped(new DsToAluBus))

  val res = Wire(Vec(12, UInt(32.W)))

  res(0) := io.src1 + io.src2
  res(1) := io.src1 - io.src2
  res(2) := io.src1 & io.src2
  res(3) := io.src1 | io.src2
  res(4) := io.src1 ^ io.src2
  res(5) := ~(io.src1 | io.src2)
  res(6) := io.src1 + io.src2
  res(7) := io.src1 + io.src2
  res(8) := io.src1 + io.src2
  res(9) := io.src1 + io.src2
  res(10) := io.src1 + io.src2
  res(11) := io.src1 + io.src2

  io.res := Mux1H(io.aluop, res)
}
