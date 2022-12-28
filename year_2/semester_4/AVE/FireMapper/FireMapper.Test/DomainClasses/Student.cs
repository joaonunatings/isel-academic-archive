using FireMapper.Attributes;

namespace FireMapper.Test.DomainClasses {

    [FireCollection("Students")]
    public class Student {
        [property: FireKey] public string Number { get; set; }
        public string Name { get; set; }
        public ClassroomInfo Classroom { get; set; }

        public Student() {}

        public Student(string number, string name, ClassroomInfo classroom) {
            Number = number;
            Name = name;
            Classroom = classroom;
        }
    }

    [FireCollection("Classrooms")]
    public class ClassroomInfo {
        [property: FireKey] public string Token { get; set; }
        public string Teacher { get; set; }

        public ClassroomInfo() {
        }

        public ClassroomInfo(string token, string teacher) {
            Token = token;
            Teacher = teacher;
        }
    }
}