import pipeline._
import chisel3._
import funcunit._

class mycpu_top extends Module {
  val clk = IO(Input(Clock()))
  val resetn = IO(Input(Bool()))
  val inst_sram = IO(new SramInterface)
  val data_sram = IO(new SramInterface)
  val debug_wb = IO(new DebugInterface)
  val ext = IO(new ExternalInterrupt)

  val rst = Wire(Reset())

  rst := ~resetn

  withClockAndReset(clk, rst) {
    val fs = Module(new InstFetch)
    val ds = Module(new Decode)
    val es = Module(new Execute)
    val ms = Module(new Memory)
    val rf = Module(new RegFile)

    // Pipeline Interface
    fs.fs2dsbus <> ds.fs2ds_bus
    ds.ds2es_bus <> es.ds2esbus
    es.es2msbus <> ms.es2msbus

    // RegFile Interface
    ds.rf_read <> rf.read_channel
    ms.rf_write <> rf.write_channel

    // Sram Interface
    es.data_sram <> data_sram
    fs.inst_sram <> inst_sram

    // Debug Interface
    ms.debug <> debug_wb
  }
}
