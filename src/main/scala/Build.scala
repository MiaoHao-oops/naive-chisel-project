import pipeline._
import chisel3._
import chisel3.stage._
import funcunit._

object Build extends App {
  //  println(getVerilogString(new InstFetch))
  println("Generating Verilog Code")
  (new ChiselStage).emitVerilog(new Alu)
  println("Done")
}
