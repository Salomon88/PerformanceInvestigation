#Hardware
 - Processor 2,6 GHz 6-Core Intel Core i7
 - Memory    16 GB 2667 MHz DDR4
 - Heap -Xmx 2048

#Measurements
**maxPrime** = 300_000  - used in snapshots in [initial version](snapshots/common_before.snapshot)  
**maxPrime** = 3_000_000  - used in snapshots in [final version](snapshots/common_after.snapshot)

##JMH results
|  maxPrime       | PrimeCalculator avg  seconds    | FinalPrimeCalculator  avg seconds|
| -------------   |:-------------:                  | -----:|
| 10              | 0.001                           | 0.001 |
| 100             | 0.006                           | 0.001 |
| 1000            | 0.131                           | 0.001 |
| 10000           | 0.891                           | 0.003 |
| 100000          | 6.754                           | 0.026 |
| 300000          | 109.678                         | 0.097 |

For detailed information look at [jmh results](snapshots/results_jmh.txt)


#Achievements unlocked
 - Ability to serve more than 1 million values instead of ~400_000 in the old version(Caused by the **OutOfMemoryError**);
 - Decreased time of work/cpu/memory usage.  

#Issues

You can find snapshots in [snapshots](snapshots) directory in root of project
 
##[issue_1](snapshots/issue_1) - Not optimal algorithm in `isPrime` method   
```
            for (Integer candidate : primeNumbers) {
                executors.submit(() -> {
                    if (!isPrime(candidate)) primeNumbersToRemove.add(candidate);
                    latch.countDown();
                });
            }
```
```
    private static boolean isPrime(Integer candidate) {
        for (int i = 2; i <= Math.sqrt(candidate); ++i) {
            if (candidate % i == 0) {
                return false;
            }
        }
        return true;
    }
```
Additionaly this method throws an Exception. It is rather 'expensive' for jvm than to return primitive boolean.
After changes above, cpu usage  will be decreased. 

##[issue_2](snapshots/issue_2) Inefficient thread pool size
 
`ExecutorService executors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())`

  - Inefficient thread pool size will lead to a thread 'starvation' situation or at worst case to `OutOfMemoryError` exception. 
I think that a good way to make this choice is to use common approach: **Number of threads = Number of Available Cores * (1 + Wait time / Service time)**
It gives a high cpu usage on start of app ~73%, but after ~5 seconds cpu usage and time of work of app decreased. In initial version cpu usage stands on ~63% till ~17 seconds.

  - Another approach simply to use IntStream.boxed().parallelStream with `commonPool`. From oracle doc: 
```
The Oracle's implementation[1] of parallel stream uses the current thread and in addition to that, 
if needed, also the threads that compose the default fork join pool ForkJoinPool.commonPool(), 
which has a default size equal to one less than the number of cores of your CPU
```

##[issue_3](snapshots/issue_3) `BigIntegerIterator` class 

`BigIntegerIterator` class is exceed here due to it takes a lot of memory per one object. All its destiny is to wrap simple `int`. Also try to use a `primitive int` in places where it is possible. Removing `BigIntegerIterator` will decrease memory usage

 ```
List<Integer> myFiller = Stream.generate(new Supplier<Integer>() {
            int i = 2;

            @Override
            public Integer get() {
                return i++;
            }
        }).limit(maxPrime).collect(Collectors.toList());
        primeNumbers.addAll(myFiller);
``` 
 
##Others

Issues in this paragraph difficult to show separate from each other. Below I will give a short description.

 - Large number of loops; 
 - Large number of copying collection from each other;
 - Exceed `synchronized (primeNumbersToRemove)`;
 - `Collections.synchronizedList(new LinkedList<>())` - is not a good idea due to `LinkedList` uses `Node` under the hood. It will lay on memory cost;
 - 'System.out.print(prime + "\n");' - any io operation will be expensive

#PS

If I had much more time I think that the best approach for `isPrime` method is https://en.wikipedia.org/wiki/Sieve_of_Eratosthenes 