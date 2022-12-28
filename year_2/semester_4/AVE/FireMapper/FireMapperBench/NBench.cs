using System;

public class NBench {

    public static void Bench(Action handler, int time, int iterations, int calls) {
        Console.WriteLine("> Benchmarking: {0}", handler.Method.Name);
        Console.WriteLine("time: {0}; iterations: {1}; calls: {2}", time, iterations, calls);
        Perform(handler, time, iterations, calls);
    }

    private static void Perform(Action handler, int time, int iterations, int calls) {
        GC.Collect();
        GC.WaitForPendingFinalizers();
        GC.Collect();
        Result res = new Result();
        long maxThroughput = 0;
        double sum = 0;
        for (int i = 0; i < iterations; i++) {
            Console.Write("> Iteration {0,2}: ", i);
            res = CallWhile(handler, time, calls);
            long curr = res.OpsPerMs;
            sum += res.OpsPerMs;
            Console.WriteLine("{0} ops/ms | {1} ops/s", curr, res.OpsPerSec);
            if (curr > maxThroughput) maxThroughput = curr;
            GC.Collect();
        }
        Console.WriteLine("> Best: {0 } ops/ms", maxThroughput);
        Console.WriteLine("> Executed handler {0} times", res.ops);
        Console.WriteLine();
    }

    private static Result CallWhile(Action handler, int time, int calls) {
        int start = Environment.TickCount;
        int end = start + time;
        int curr = start;
        Result res = new Result();
        do {
            // Check for perfomance in this for
            for (int i = 0; i < calls; i++)
                handler();
            curr = Environment.TickCount;
            res.ops += calls;
        } while (curr < end);
        res.durInMs = curr - start;
        return res;
    }

    private struct Result {
        public long ops;
        public int durInMs;

        public long OpsPerMs {
            get {
                return ops / durInMs;
            }
        }

        public long OpsPerSec {
            get {
                return (ops * 1000) / durInMs;
            }
        }
    }
}