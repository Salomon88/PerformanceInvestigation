package prime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class BaseTest {

    @ParameterizedTest
    @ValueSource(ints = {10, 100 ,1000, 10_000})
    void test_that_old_and_new_version_are_same(int maxPrime) throws InterruptedException {
        assertIterableEquals(PrimeCalculator.getPrimes(maxPrime), FinalPrimeCalculator.getPrimes(maxPrime));
    }

//    @Test
//    void maxOld() throws InterruptedException {
//        for (int i = 400_000; i <= 1_000_000; i+=10_000) {
//            System.out.println("Iteration " + i);
//            try {
//                PrimeCalculator.getPrimes(i);
//            } catch (OutOfMemoryError e) {
//                System.out.println("Exception occurred on " + i);
//                return;
//            }
//        }
//    }

//    @Test
//    void maxNew() throws InterruptedException {
//        for (int i = 500_000; i <= 1_000_000; i+=10_000) {
//            System.out.println("Iteration " + i);
//            try {
//                FinalPrimeCalculator.getPrimes(i);
//            } catch (OutOfMemoryError e) {
//                System.out.println("Exception occurred on " + i);
//                return;
//            }
//        }
//    }

}
