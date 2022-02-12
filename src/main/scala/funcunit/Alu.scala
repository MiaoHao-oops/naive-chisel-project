package funcunit

import chisel3._
import chisel3.util._
import pipeline._

class Alu extends Module {
  val io = IO(new AluInterface)

  val res = Wire(Vec(12, UInt(32.W)))

  res(0) := io.src(0) + io.src(1)
  res(1) := io.src(0) - io.src(1)
  res(2) := io.src(0) & io.src(1)
  res(3) := io.src(0) | io.src(1)
  res(4) := io.src(0) ^ io.src(1)
  res(5) := ~(io.src(0) | io.src(1))
  res(6) := io.src(0) + io.src(1)
  res(7) := io.src(0) + io.src(1)
  res(8) := io.src(0) + io.src(1)
  res(9) := io.src(0) + io.src(1)
  res(10) := io.src(0) + io.src(1)
  res(11) := io.src(0) + io.src(1)

  io.res := Mux1H(io.aluop, res)
}

class AluInterface extends Bundle {
  val src = Input(Vec(2, UInt(32.W)))
  val aluop = Input(Vec(12, Bool()))
  val res = Output(UInt(32.W))
}
