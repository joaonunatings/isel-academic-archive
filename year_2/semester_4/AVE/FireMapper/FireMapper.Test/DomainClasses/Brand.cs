using FireMapper.Attributes;

namespace FireMapper.Test.DomainClasses {

    [FireCollection("Brands")]
    public class Brand {
        [property: FireKey] public string Name { get; set; }
        public string Country { get; set; }
        [property: FireIgnore] public string Description { get; set; }

        public Brand() {
        }

        public Brand(string name, string country, string description) {
            Name = name;
            Country = country;
            Description = description;
        }

        public static Brand Parse(string input) {
            string[] words = input.Split(';');
            return new Brand(
                words[0],
                words[1],
                words[2]);
        }
    }
}