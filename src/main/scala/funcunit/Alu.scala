package funcunit

import chisel3._
import chisel3.util._
import pipeline._

class Alu extends Module {
  val io = IO(new AluInterface)

  val res = Wire(Vec(12, UInt(32.W)))

  for (i <- 0 to 11) {
    res(i) := 0.U
  }

  res(0) := io.src(0) + io.src(1)

  io.res := Mux1H(io.aluop, res)
}

class AluInterface extends Bundle {
  val src = Input(Vec(2, UInt(32.W)))
  val aluop = Input(Vec(12, Bool()))
  val res = Output(UInt(32.W))
}
