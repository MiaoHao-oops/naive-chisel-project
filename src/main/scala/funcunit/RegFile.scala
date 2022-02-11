package funcunit

import chisel3._

class RegFile(NRReg: Int, XLen: Int) extends Module {
  def log2(x: Double): Int = (Math.log(x) / Math.log(2)).toInt
  val addr_len = log2(NRReg)

  val io = IO(new Bundle() {
    val raddr = Input(Vec(2, UInt(addr_len.W)))
    val rdata = Output(Vec(2, UInt(XLen.W)))

    val wen = Input(Bool())
    val waddr = Input(UInt(addr_len.W))
    val wdata = Input(UInt(XLen.W))
  })

  val regs = Mem(NRReg, UInt(XLen.W))

  when (io.wen) {
    regs(io.waddr) := io.wdata
  }

  for (i <- 0 to 1) {
    when (io.raddr(i) === "h0".U(5.W)) {
      io.rdata(i) := "h0".U(32.W)
    } otherwise {
      io.rdata(i) := regs(io.raddr(i))
    }
  }
}
