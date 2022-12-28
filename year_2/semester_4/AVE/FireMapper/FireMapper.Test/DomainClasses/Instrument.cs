using FireMapper.Attributes;

namespace FireMapper.Test.DomainClasses {

    [FireCollection("Instruments")]
    public class Instrument {
        [FireKey] public string Name { get; set; }
        public string Brand { get; set; }
        public string Characteristics { get; set; }
        [FireIgnore] public string Description { get; set; }
        public string Category { get; set; }

        public Instrument() {
        }

        public Instrument(string name, string brand, string characteristics, string description, string category) {
            Name = name;
            Brand = brand;
            Characteristics = characteristics;
            Description = description;
            Category = category;
        }

        public static Instrument Parse(string input) {
            string[] words = input.Split(';');
            return new Instrument(
                words[0],
                words[1],
                words[2],
                words[3],
                words[4]);
        }
    }
}