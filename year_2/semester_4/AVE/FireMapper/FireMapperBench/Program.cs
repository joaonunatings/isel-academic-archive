using System;
using System.IO;

namespace FireMapperBench {

    public class Program {

        private static FileStream ostrm;
        private static StreamWriter writer;
        public static void Main(string[] args) {
            int time = 1000, iterations = 10, calls = 32;
            if (args.Length == 3) {
                time = int.Parse(args[0]);
                iterations = int.Parse(args[1]);
                calls = int.Parse(args[2]);
            }
            
            Console.WriteLine("> Running benchmark");
            TextWriter defaultOut = Console.Out;
            
            OutputToFile();
            NBench.Bench(BenchmarkTests.PersonFireDataMapperTest, time, iterations, calls);
            NBench.Bench(BenchmarkTests.PersonDynamicDataMapperTest, time, iterations, calls);

            Console.SetOut(defaultOut);
            writer?.Close();
            ostrm?.Close();
            Console.WriteLine("> Finished!");
        }

        private static void OutputToFile() {
            String fileName = "./BenchmarkResults/bench_results_" + DateTime.Now.ToString("yyyy-MM-dd_hh-mm-ss") + ".txt";
            ostrm = new FileStream (fileName, FileMode.CreateNew, FileAccess.Write);
            writer = new StreamWriter (ostrm);
            Console.SetOut(writer);
        }
    }
}