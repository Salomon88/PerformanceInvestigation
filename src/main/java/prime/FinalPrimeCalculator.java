package prime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FinalPrimeCalculator {

    public static void main(String[] args) throws InterruptedException {
        for (int prime : getPrimes(Integer.parseInt(args[0]))) {
            System.out.print(prime + "\n");
        }
    }

    public static List<Integer> getPrimes(int maxPrime) throws InterruptedException {

        List<Integer> primeNumbers = Collections.synchronizedList(new ArrayList<>());

        ExecutorService executors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (int candidate = 2; candidate <= maxPrime + 1; ++candidate) {
            final int finalCandidate = candidate;
            executors.submit(() -> {
                if (isPrime(finalCandidate)) primeNumbers.add(finalCandidate);
            });
        }
        executors.shutdown();
        boolean isFinished = executors.awaitTermination(1, TimeUnit.MINUTES);
        if (!isFinished) {
            //Better to throw a custom exception
            throw new RuntimeException("Not all tasks were finished until 1 minutes");
        }

        Collections.sort(primeNumbers);
        return primeNumbers;

        // Maybe will be better to do it in this way
        //Performance little bit better in version above
//        return IntStream
//                .range(2, maxPrime + 2)
//                .boxed()
//                .parallel()
//                .filter(FinalPrimeCalculator::isPrime)
//                .sorted()
//                .collect(Collectors.toList());
    }

    private static boolean isPrime(int candidate) {
        for (int i = 2; i <= Math.sqrt(candidate); ++i) {
            if (candidate % i == 0) {
                return false;
            }
        }
        return true;
    }
}
