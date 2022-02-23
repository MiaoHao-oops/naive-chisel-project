package funcunit

import chisel3._
import pipeline.{DsToFPBus, EsToFPBus, MsToFPBus}

class ForwardPath extends Module{
  val es2fp_bus = IO(Flipped(new EsToFPBus))
  val ms2fp_bus = IO(Flipped(new MsToFPBus))
  val ds2fp_bus = IO(Flipped(new DsToFPBus))
  val rf_read = IO(Flipped(new RFRead))

  val es_rel = Wire(Vec(2, Bool()))
  val ms_rel = Wire(Vec(2, Bool()))
  val es_hit = Wire(Vec(2, Bool()))
  val ms_hit = Wire(Vec(2, Bool()))

  rf_read.raddr := ds2fp_bus.rf_read.raddr

  for (i <- 0 to 1) {
    es_rel(i) := (ds2fp_bus.rf_read.raddr(i) === es2fp_bus.data.rf_wnum) && es2fp_bus.data.addr_valid && (ds2fp_bus.rf_read.raddr(i) =/= 0.U)
    es_hit(i) := es2fp_bus.data.data_valid

    ms_rel(i) := (ds2fp_bus.rf_read.raddr(i) === ms2fp_bus.data.rf_wnum) && ms2fp_bus.data.addr_valid && (ds2fp_bus.rf_read.raddr(i) =/= 0.U)
    ms_hit(i) := ms2fp_bus.data.data_valid

    when (es_rel(i) && es_hit(i)) {
      ds2fp_bus.rf_read.rdata(i) := es2fp_bus.data.rf_wdata
    }.elsewhen(ms_rel(i) && ms_hit(i)) {
      ds2fp_bus.rf_read.rdata(i) := ms2fp_bus.data.rf_wdata
    }.otherwise {
      ds2fp_bus.rf_read.rdata(i) := rf_read.rdata(i)
    }


  }

  when (es_rel(0) && !es_hit(0) || es_rel(1) && !es_hit(1)) {
    ds2fp_bus.stall := true.B
  }.otherwise {
    ds2fp_bus.stall := false.B
  }

}
