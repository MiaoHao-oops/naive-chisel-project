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
    val bc = Module(new BranchControl)
    val rf = Module(new RegFile)

    // Pipeline Interface
    fs.fs2ds_bus <> ds.fs2ds_bus
    ds.ds2es_bus <> es.ds2es_bus
    es.es2ms_bus <> ms.es2msbus

    // RegFile Interface
    ds.rf_read <> rf.read_channel
    ms.rf_write <> rf.write_channel

    // Sram Interface
    data_sram.en := es.data_sram_req.en
    data_sram.wen := es.data_sram_req.wen
    data_sram.addr := es.data_sram_req.addr
    data_sram.wdata := es.data_sram_req.wdata
    ms.data_sram_res.rdata := data_sram.rdata
    fs.inst_sram <> inst_sram

    // Debug Interface
    ms.debug <> debug_wb

    // Branch Control
    bc.ds2bc_bus <> ds.ds2bc_bus
    bc.es2bc_bus <> es.es2bc_bus
    bc.bc2ds_bus <> ds.bc2ds_bus
    bc.bc2fs_bus <> fs.bc2fs_bus
  }
}
