using FireMapper;
using FireMapper.Interfaces;
using FireMapper.Test.DomainClasses;
using FireSource;

namespace FireMapperBench {
    public class BenchmarkTests {
        
        private static readonly IDataMapper StudentFireDataMapper = new FireDataMapper(typeof(Student), 
            new WeakDataSource("Number"));
        private static readonly IDataMapper StudentDynamicDataMapper = new DynamicDataMapper(typeof(Student),
            new WeakDataSource("Number"));
        
        private static readonly IDataMapper PersonFireDataMapper = new FireDataMapper(typeof(Person), 
            new WeakDataSource("Id"));
        private static readonly IDataMapper PersonDynamicDataMapper = new DynamicDataMapper(typeof(Person), 
            new WeakDataSource("Id"));

        private static readonly IDataMapper CardFireDataMapper = new FireDataMapper(typeof(Card), 
            new WeakDataSource("Id"));
        private static readonly IDataMapper CardDynamicDataMapper = new DynamicDataMapper(typeof(Card), 
            new WeakDataSource("Id"));

        public static void StudentFireDataMapperTest() {
            StudentFireDataMapper.Add(new Student("47220", "João Nunes", new ClassroomInfo("1", "Luís Falcão")));
            StudentFireDataMapper.Add(new Student("47204", "Miguel Marques", new ClassroomInfo("1", "Luís Falcão")));
            StudentFireDataMapper.Add(new Student("47192", "Alexandre Silva", new ClassroomInfo("2", "Miguel Gamboa")));
            StudentFireDataMapper.Update(new Student("47220", "Pedro Nunes", new ClassroomInfo("1", "Luís Falcão")));
            StudentFireDataMapper.GetById("47220");
            StudentFireDataMapper.GetAll();
            StudentFireDataMapper.Delete("47220");
            StudentFireDataMapper.Delete("47204");
            StudentFireDataMapper.Delete("47192");
        }

        public static void StudentDynamicDataMapperTest() {
            StudentDynamicDataMapper.Add(new Student("47220", "João Nunes", new ClassroomInfo("1", "Luís Falcão")));
            StudentDynamicDataMapper.Add(new Student("47204", "Miguel Marques", new ClassroomInfo("1", "Luís Falcão")));
            StudentDynamicDataMapper.Add(new Student("47192", "Alexandre Silva", new ClassroomInfo("2", "Miguel Gamboa")));
            StudentDynamicDataMapper.Update(new Student("47220", "Pedro Nunes", new ClassroomInfo("1", "Luís Falcão")));
            StudentDynamicDataMapper.GetById("47220");
            StudentDynamicDataMapper.GetAll();
            StudentDynamicDataMapper.Delete("47220");
            StudentDynamicDataMapper.Delete("47204");
            StudentDynamicDataMapper.Delete("47192");
        }
        
        public static void PersonFireDataMapperTest() {
            PersonFireDataMapper.Add(new Person(1, "João", "Nunes", new Info(170, 21, 'M')));
            PersonFireDataMapper.Add(new Person(2, "Miguel", "Marques", new Info(193, 22, 'M')));
            PersonFireDataMapper.Add(new Person(3, "Alexandre", "Silva", new Info(180, 21, 'M')));
            PersonFireDataMapper.Update(new Person(1, "Pedro", "Nunes", new Info(170, 21, 'M')));
            PersonFireDataMapper.GetById(1);
            PersonFireDataMapper.GetAll();
            PersonFireDataMapper.Delete(1);
            PersonFireDataMapper.Delete(2);
            PersonFireDataMapper.Delete(3);
        }

        public static void PersonDynamicDataMapperTest() {
            PersonDynamicDataMapper.Add(new Person(1, "João", "Nunes", new Info(170, 21, 'M')));
            PersonDynamicDataMapper.Add(new Person(2, "Miguel", "Marques", new Info(193, 22, 'M')));
            PersonDynamicDataMapper.Add(new Person(3, "Alexandre", "Silva", new Info(180, 21, 'M')));
            PersonDynamicDataMapper.Update(new Person(1, "Pedro", "Nunes", new Info(170, 21, 'M')));
            PersonDynamicDataMapper.GetById(1);
            PersonDynamicDataMapper.GetAll();
            PersonDynamicDataMapper.Delete(1);
            PersonDynamicDataMapper.Delete(2);
            PersonDynamicDataMapper.Delete(3);
        }

        public static void CardFireDataMapperTest() {
            CardFireDataMapper.Add(new Card(1, "João Nunes", 21, 'M', new Info(170, 21, 'M')));
            CardFireDataMapper.Add(new Card(2, "Miguel Marques", 22, 'M', new Info(193, 22, 'M')));
            CardFireDataMapper.Add(new Card(3, "Alexandre Silva", 21, 'M',  new Info(180, 21, 'M')));
            CardFireDataMapper.Update(new Card(1, "Pedro Nunes", 21, 'M', new Info(170, 21, 'M')));
            CardFireDataMapper.GetById(1);
            CardFireDataMapper.GetAll();
            CardFireDataMapper.Delete(1);
            CardFireDataMapper.Delete(2);
            CardFireDataMapper.Delete(3);
        }

        public static void CardDynamicDataMapperTest() {
            CardDynamicDataMapper.Add(new Card(1, "João Nunes", 21, 'M', new Info(170, 21, 'M')));
            CardDynamicDataMapper.Add(new Card(2, "Miguel Marques", 22, 'M', new Info(193, 22, 'M')));
            CardDynamicDataMapper.Add(new Card(3, "Alexandre Silva", 21, 'M', new Info(180, 21, 'M')));
            CardDynamicDataMapper.Update(new Card(1, "Pedro Nunes", 21, 'M', new Info(170, 21, 'M')));
            CardDynamicDataMapper.GetById(1);
            CardDynamicDataMapper.GetAll();
            CardDynamicDataMapper.Delete(1);
            CardDynamicDataMapper.Delete(2);
            CardDynamicDataMapper.Delete(3);
        }
    }
}