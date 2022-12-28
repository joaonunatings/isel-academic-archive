using System.Collections;
using System.Collections.Generic;
using FireMapper.Test.DomainClasses;
using FireMapper.Test.Fixture;
using Xunit;
using Xunit.Abstractions;

namespace FireMapper.Test
{
    [Collection("FireMapperFixture collection")]
    public class FireDataMapperTest
    {
        private readonly ITestOutputHelper output;
        private readonly FireMapperFixture fix;

        private readonly FireDataMapper brandsDm;
        private readonly FireDataMapper categoriesDm;
        private readonly FireDataMapper instrumentsDm;
        private readonly FireDataMapper stocksDm;
        private readonly FireDataMapper studentsDm;
        private readonly FireDataMapper classroomsDm;

        public FireDataMapperTest(ITestOutputHelper output, FireMapperFixture fix)
        {
            this.output = output;
            this.fix = fix;
            studentsDm = fix.studentsDm;
            classroomsDm = fix.classroomsDm;
            brandsDm = fix.brandsDm;
            categoriesDm = fix.categoriesDm;
            instrumentsDm = fix.instrumentsDm;
            stocksDm = fix.stocksDm;
        }

        [Fact]
        public void GetAll()
        {
            int count = 0;
            foreach (var obj in brandsDm.GetAll())
            {
                output.WriteLine(obj.ToString());
                count++;
            }

            Assert.Equal(5, count);
        }

        [Fact]
        public void GetById()
        {
            Category c = (Category)categoriesDm.GetById("Membranophones");
            Assert.Equal("Membranophones", c.Name);
        }

        [Fact]
        public void UpdateStock()
        {
            stocksDm.Update(new Stock(
                15648,
                "Steinway & Sons B-211",
                100,
                false));

            Stock s = (Stock)stocksDm.GetById((long)15648);
            Assert.Equal(100, s.Price);
        }

        [Fact]
        public void DeleteInstrument()
        {
            instrumentsDm.Delete("Karl Höfner Allegretto 4/4 Violin Outfit");
            Instrument i2 = (Instrument)instrumentsDm.GetById("Karl Höfner Allegretto 4/4 Violin Outfit");
            Assert.Null(i2);
        }

        [Fact]
        public void AddBrand()
        {
            Brand b = new Brand(
                "Arturia",
                "France",
                "Arturia is a French electronics company founded in 1999 and based in Grenoble, France");
            brandsDm.Add(b);

            Brand b2 = (Brand)brandsDm.GetById("Arturia");
            Assert.Equal("Arturia", b2.Name);
            Assert.Equal("France", b2.Country);
            Assert.Null(b2.Description);
            brandsDm.Delete("Arturia");
        }

        [Fact]
        public void GetAllEmptyCollection()
        {
            IEnumerable listOfCategories = categoriesDm.GetAll();
            foreach (Category category in listOfCategories)
            {
                categoriesDm.Delete(category.Name);
            }

            //Force Error
            IEnumerable collectionEmpty = categoriesDm.GetAll();
            Assert.Null(collectionEmpty);

            foreach (Category category in listOfCategories)
            {
                categoriesDm.Add(category);
            }
        }

        [Fact]
        public void AddAlreadyInDb()
        {
            Stock s = new Stock(
                17160,
                "Yamaha C40",
                250,
                false);

            stocksDm.Add(s);

            Stock s2 = (Stock)stocksDm.GetById(s.Reference);
            Assert.Equal(115, s2.Price);
        }

        [Fact]
        public void FailDelete()
        {
            int count = 0;
            foreach (var obj in instrumentsDm.GetAll())
            {
                count++;
            }

            instrumentsDm.Delete("Arturia Minilab MkII");

            int count2 = 0;
            foreach (var obj in instrumentsDm.GetAll())
            {
                count2++;
            }

            Assert.Equal(count, count2);
        }

        [Fact]
        public void StudentGetById()
        {
            Student s = (Student)studentsDm.GetById("47220");
            Assert.Equal("47220", s.Number);
            Assert.Equal("João Nunes", s.Name);
        }

        [Fact]
        public void GetAllStudents()
        {
            int count = 0;
            List<Student> studentsList = new();
            foreach (Student s in studentsDm.GetAll())
            {
                studentsList.Add(s);
                output.WriteLine($"Student: {s.Number}, {s.Name}"); //, {s.Classroom.Token}, {s.Classroom.Teacher}");
                count++;
            }

            Assert.Equal(3, count);
        }
        
        [Fact (Skip="To be fixed when we implement abstract tests")]
        public void UpdateStudent()
        {
            Student s = new Student(
                "47220",
                "João Nunes",
                new ClassroomInfo(
                    "4",
                    "Miguel Gamboa"));
            studentsDm.Update(s);

            Student s2 = (Student)studentsDm.GetById("47220");
            Assert.Equal("Miguel Gamboa", s2.Classroom.Teacher);

            Student s3 = new Student(
                "47220",
                "João Nunes",
                new ClassroomInfo(
                    "1",
                    "teacher"));
            studentsDm.Update(s3);
            Student s4 = (Student)studentsDm.GetById("47220");
            Assert.Equal("Luís Falcão", s4.Classroom.Teacher);
            Assert.Equal("1", s4.Classroom.Token);
        }

        [Fact]
        public void DeleteStudent()
        {
            studentsDm.Delete("47220");

            Student s1 = (Student)studentsDm.GetById("47220");
            Assert.Null(s1);

            Student s2 = new Student(
                "47220",
                "João Nunes",
                new ClassroomInfo(
                    "1",
                    "Luís Falcão"));
            studentsDm.Add(s2);
        }

        [Fact]
        public void AddStudent()
        {
            studentsDm.Add(new Student(
                "41111",
                "Teste",
                new ClassroomInfo(
                    "1",
                    "Gamboa")));

            Student s2 = (Student)studentsDm.GetById("41111");

            Assert.Equal("Teste", s2.Name);
            studentsDm.Delete("41111");
            Student s3 = (Student)studentsDm.GetById("41111");
            Assert.Null(s3);
        }

        [Fact]
        public void AddClassroom()
        {
            ClassroomInfo cI = new ClassroomInfo(
                "6",
                "Pedro Pereira");
            classroomsDm.Add(cI);
            ClassroomInfo cI2 = (ClassroomInfo)classroomsDm.GetById(cI.Token);
            Assert.Equal(cI.Token, cI2.Token);
        }

        /*
        [Fact]
        public void MultipleFireKeyTest() {
            FireDataMapper bookDm;
            var ex = Assert.Throws<Exception>(() => bookDm = new FireDataMapper(typeof(Book), new FireDataSource("fire-students-g06", "Book", "SerialNumber", "Resources/fire-students-g06-firebase-adminsdk-facck-0bf0b6e674.json")));
            Assert.Equal("Multiple FireKey assigned", ex.Message);
        }

        [Fact]
        public void NoFireKeyTest() {
            FireDataMapper librayDm;
            var ex = Assert.Throws<Exception>(() => librayDm = new FireDataMapper(typeof(Library), new FireDataSource("fire-students-g06", "Library", "Id", "Resources/fire-students-g06-firebase-adminsdk-facck-0bf0b6e674.json")));
            Assert.Equal("FireKey not assigned", ex.Message);
        }
        */
    }
}