using FireMapper.Attributes;

namespace FireMapper.Test.DomainClasses {

    [FireCollection("Stocks")]
    public class Stock {
        [FireKey] public long Reference { get; set; }
        public string InstName { get; set; }
        public long Price { get; set; }
        public bool Available { get; set; }

        public Stock() {
        }

        public Stock(long reference, string instName, long price, bool available) {
            Reference = reference;
            InstName = instName;
            Price = price;
            Available = available;
        }

        public static Stock Parse(string input) {
            string[] words = input.Split(';');
            return new Stock(
                long.Parse(words[0]),
                words[1],
                long.Parse(words[2]),
                bool.Parse(words[3]));
        }
    }
}