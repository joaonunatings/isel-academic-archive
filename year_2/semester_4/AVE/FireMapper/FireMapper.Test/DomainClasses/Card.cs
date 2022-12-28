using FireMapper.Attributes;

namespace FireMapper.Test.DomainClasses {
    
    [FireCollection("Cards")]
    public struct Card {
        [FireKey] public int Id { get; set; }
        
        public string Name { get; set; }
        public int Age { get; set; }
        [FireIgnore] public char Sex { get; set; }
        
        public Info Info { get; set; }

        public Card(int id, string name, int age, char sex, Info info) {
            Id = id;
            Name  = name;
            Age = age;
            Sex = sex;
            Info = info;
        }
    }
}