import chisel3._
import chisel3.stage._

object HelloWorld extends App {
  println("[{(Generating Verilog file)}]")
  (new ChiselStage).emitVerilog(new Alu)
//  println(getVerilogString(new Alu))
}

class Alu extends Module {
  val io = IO(new Bundle{
    val src_a = Input(UInt(32.W))
    val src_b = Input(UInt(32.W))
    val op_code = Input(UInt(12.W))
    val res = Output(UInt(32.W))
  })

  when(io.op_code(0)) {
    io.res := io.src_a + io.src_b
  }.otherwise {
    io.res := io.src_a - io.src_b
  }
}
