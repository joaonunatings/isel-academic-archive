using FireMapper.Attributes;

namespace FireMapper.Test.DomainClasses {

    [FireCollection("Categories")]
    public class Category {
        [FireKey] public string Name { get; set; }
        [FireIgnore] public string FullName { get; set; }
        public string Description { get; set; }

        public Category() {
        }

        public Category(string name, string fullName, string description) {
            Name = name;
            FullName = fullName;
            Description = description;
        }

        public static Category Parse(string input) {
            string[] words = input.Split(';');
            return new Category(
                words[0],
                words[1],
                words[2]);
        }
    }
}