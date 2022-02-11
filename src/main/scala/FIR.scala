import chisel3._

object FIR extends App {
//  println("[{(Generating Verilog file)}]")
//  (new ChiselStage).emitVerilog(new FIR(0, 0, 0, 0))
}

class FIR(b0: Int, b1: Int, b2: Int, b3: Int) extends Module {
  val io = IO(new Bundle() {
    val in = Input(UInt(8.W))
    val out = Output(UInt(8.W))
  })

  val shift_0 = RegNext(io.in, 0.U)
  val shift_1 = RegNext(shift_0, 0.U)
  val shift_2 = RegNext(shift_1, 0.U)

  io.out := io.in * b0.U(8.W) +
    shift_0 * b1.U(8.W) +
    shift_1 * b2.U(8.W) +
    shift_2 * b3.U(8.W)
}
