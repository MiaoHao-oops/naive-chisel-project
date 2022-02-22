import pipeline._
import chisel3._
import chisel3.stage._
import funcunit._

object Build {
  def main(args: Array[String]): Unit = {
    //  println(getVerilogString(new InstFetch))
    val build_dir = "./build"
    println(s"Generating Verilog Code in ${build_dir}")
    (new ChiselStage).emitVerilog(new mycpu_top, Array("--target-dir", build_dir))
    println("Done")
  }
}
