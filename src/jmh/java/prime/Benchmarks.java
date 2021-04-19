package prime;


import org.openjdk.jmh.annotations.*;

import java.util.List;
import java.util.concurrent.TimeUnit;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
@State(Scope.Benchmark)
public class Benchmarks {

    //Method parameters should be either @State classes - investigate how to fix it
    @Param({"10", "100", "1000", "10000", "100000", "300000"})
    public int maxPrime;

    @Benchmark
    public List<Integer> initialVersion() throws InterruptedException {
        return PrimeCalculator.getPrimes(maxPrime);
    }

    @Benchmark
    public List<Integer> newVersion() throws InterruptedException {
        return FinalPrimeCalculator.getPrimes(maxPrime);
    }

}
