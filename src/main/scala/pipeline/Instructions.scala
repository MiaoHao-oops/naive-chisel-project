package pipeline

import chisel3._
import chisel3.util._

object Instructions {
  // R-type
  def ADD   = BitPat("b000000 ????? ????? ????? 00000 100000")

  // I-type
  def ADDI  = BitPat("b001000 ????? ????? ????? ????? ??????")
  def ADDIU = BitPat("b001001 ????? ????? ????? ????? ??????")
  def B     = BitPat("b000100 00000 00000 ????? ????? ??????")

}
