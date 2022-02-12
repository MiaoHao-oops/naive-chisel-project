import pipeline._
import chisel3._
import chisel3.stage._
import funcunit._

object Build {
  def main(args: Array[String]): Unit = {
    //  println(getVerilogString(new InstFetch))
    println("Generating Verilog Code")
    (new ChiselStage).emitVerilog(new Decode, Array("--target-dir", "build"))
    println("Done")
  }
}
