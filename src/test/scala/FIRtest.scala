import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class FIRtest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "FIR"
  // test class body here
  it should "test all zero" in {
    // test case body here
     test(new FIR(0, 0, 0, 0)) { c =>
        c.io.in.poke(0.U)
        c.io.out.expect(0.U)
        c.clock.step(1)
        c.io.in.poke(4.U)
        c.io.out.expect(0.U)
        c.clock.step(1)
        c.io.in.poke(5.U)
        c.io.out.expect(0.U)
        c.clock.step(1)
        c.io.in.poke(2.U)
        c.io.out.expect(0.U)
      }
  }
  println("Success")
}
