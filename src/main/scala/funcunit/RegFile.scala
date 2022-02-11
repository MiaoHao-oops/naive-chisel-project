package funcunit

import chisel3._

class RegFile extends Module {

  val read_channel = IO(new RFRead)
  val write_channel = IO(new RFWrite)

  val regs = Mem(32, UInt(32.W))

  when (write_channel.wen) {
    regs(write_channel.waddr) := write_channel.wdata
  }

  for (i <- 0 to 1) {
    when (read_channel.raddr(i) === "h0".U(5.W)) {
      read_channel.rdata(i) := "h0".U(32.W)
    } otherwise {
      read_channel.rdata(i) := regs(read_channel.raddr(i))
    }
  }
}

// RegFile Interface
class RFRead extends Bundle {
  val raddr = Input(Vec(2, UInt(5.W)))
  val rdata = Output(Vec(2, UInt(32.W)))
}

class RFWrite extends Bundle {
  val wen = Input(Bool())
  val waddr = Input(UInt(5.W))
  val wdata = Input(UInt(32.W))
}
