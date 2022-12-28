using FireMapper.Attributes;

namespace FireMapper.Test.DomainClasses {

    [FireCollection(("Persons"))]
    public class Person {
        [FireKey] public int Id { get; set; }
        public string Name { get; set; }
        [FireIgnore] public string Surname { get; set; }
        public Info Info { get; set; }

        public Person(int id, string name, string surname, Info info) {
            Id = id;
            Name = name;
            Surname = surname;
            Info = info;
        }

        public Person() { }
    }

    public struct Info {
        public int Height { get; set; }
        public int Age { get; set; }
        public char Sex { get; set; }

        public Info(int height, int age, char sex) {
            Height = height;
            Age = age;
            Sex = sex;
        }
    }
}