using System.Reflection;
using FireMapper.Interfaces;
using FireMapper.Test.DomainClasses;
using FireMapper.Test.DummyProperties.CardProperties;
using FireSource;
using Xunit;
using Xunit.Abstractions;

namespace FireMapper.Test
{
    public class DynamicDataMapperTest {
        private ITestOutputHelper output;

        public DynamicDataMapperTest(ITestOutputHelper output) {
            this.output = output;
        }
        
        [Fact]
        public void ReferenceTypeDomainWithReferenceTypePropTest() {
            DynamicIPropertyBuilder propertyBuilder = DynamicIPropertyBuilder.Instance;
            Student student = new Student("47220", "João Nunes", new ClassroomInfo("1", "Luís Falcão"));
            
            PropertyInfo targetProperty = student.GetType().GetProperty("Classroom");
            Assert.NotNull(targetProperty);
            IProperty dynamicProperty = propertyBuilder.GenerateIPropertyFor(typeof(Student), targetProperty);
            
            Assert.Equal(targetProperty.GetValue(student), dynamicProperty.GetValue(student));
            ClassroomInfo newClassroom = new ClassroomInfo("2", "Miguel Gamboa");
            dynamicProperty.SetValue(student, newClassroom);
            Assert.Equal(targetProperty.GetValue(student), dynamicProperty.GetValue(student));
            Assert.Equal(newClassroom, dynamicProperty.GetValue(student));
        }

        [Fact]
        public void ReferenceTypeDomainWithValueTypePropTest() {
            DynamicIPropertyBuilder propertyBuilder = DynamicIPropertyBuilder.Instance;
            Person person = new Person(1, "João", "Nunes", new Info(160, 21, 'M'));
            
            PropertyInfo targetProperty = person.GetType().GetProperty("Info");
            Assert.NotNull(targetProperty);
            IProperty dynamicProperty = propertyBuilder.GenerateIPropertyFor(typeof(Person), targetProperty);
            
            Assert.Equal(targetProperty.GetValue(person), dynamicProperty.GetValue(person));
            Info newInfo = new Info(180, 21, 'M');
            dynamicProperty.SetValue(person, newInfo);
            Assert.Equal(targetProperty.GetValue(person), dynamicProperty.GetValue(person));
            Assert.Equal(newInfo, dynamicProperty.GetValue(person));
        }

        [Fact]
        public void ValueTypeDomainWithReferenceTypePropTest() {
            DynamicIPropertyBuilder propertyBuilder = DynamicIPropertyBuilder.Instance;
            Card card = new Card(1, "João Nunes", 21, 'M', new Info(180, 21, 'M'));
            
            PropertyInfo targetProperty = card.GetType().GetProperty("Name");
            Assert.NotNull(targetProperty);
            IProperty dynamicProperty = propertyBuilder.GenerateIPropertyFor(typeof(Card), targetProperty);
            
            Assert.Equal(targetProperty.GetValue(card), dynamicProperty.GetValue(card));
            card = (Card)dynamicProperty.SetValue(card, "Alexandre Silva");
            Assert.Equal(targetProperty.GetValue(card), dynamicProperty.GetValue(card));
        }

        [Fact]
        public void ValueTypeDomainWithValueTypePropTest() {
            DynamicIPropertyBuilder propertyBuilder = DynamicIPropertyBuilder.Instance;
            Card card = new Card(1, "João Nunes", 21, 'M', new Info(180, 21, 'M'));
            PropertyInfo targetProperty = card.GetType().GetProperty("Info");
            Assert.NotNull(targetProperty);
            IProperty dynamicProperty = propertyBuilder.GenerateIPropertyFor(typeof(Card), targetProperty);

            Assert.Equal(targetProperty.GetValue(card), dynamicProperty.GetValue(card));
            Info newInfo = new Info(170, 20, 'M');
            card = (Card)dynamicProperty.SetValue(card, newInfo);
            Assert.Equal(targetProperty.GetValue(card), dynamicProperty.GetValue(card));
            Assert.Equal(newInfo, dynamicProperty.GetValue(card));
        }

        [Fact]
        public void DynamicIsCollectionTest() {
            DynamicIPropertyBuilder propertyBuilder = DynamicIPropertyBuilder.Instance;
            Student student = new Student("47220", "João Nunes", new ClassroomInfo("1", "Luís Falcão"));
            PropertyInfo targetProperty = student.GetType().GetProperty("Classroom");
            IProperty dynamicProperty = propertyBuilder.GenerateIPropertyFor(typeof(Student), targetProperty);

            Assert.NotNull(targetProperty);
            Assert.NotNull(dynamicProperty);
            Assert.True(dynamicProperty.IsCollection());
        }

        [Fact (Skip = "not working")]
        public void DynamicGetIPropertyTypeTest() {
            DynamicIPropertyBuilder propertyBuilder = DynamicIPropertyBuilder.Instance;
            Student student = new Student("47220", "João Nunes", new ClassroomInfo("1", "Luís Falcão"));
            PropertyInfo targetProperty = student.GetType().GetProperty("Number");
            IProperty dynamicProperty = propertyBuilder.GenerateIPropertyFor(typeof(Student), targetProperty);

            Assert.NotNull(targetProperty);
            Assert.NotNull(dynamicProperty);
            Assert.False(dynamicProperty.IsCollection());
            Assert.Equal(targetProperty.PropertyType, dynamicProperty.GetIPropertyType());
        }

        [Fact]
        public void DynamicComplexPropertyTest() 
        {
            DynamicIPropertyBuilder propertyBuilder = DynamicIPropertyBuilder.Instance;
            Student student = new Student("47220", "João Nunes", new ClassroomInfo("1", "Luís Falcão"));
            PropertyInfo targetProperty = student.GetType().GetProperty("Classroom");
            IProperty dynamicProperty = propertyBuilder.GenerateIPropertyFor(typeof(Student), targetProperty);

            Assert.NotNull(targetProperty);
            Assert.NotNull(dynamicProperty);
            if (dynamicProperty.IsCollection()) {
                Assert.Equal(targetProperty.GetValue(student), dynamicProperty.GetValue(student));
                ClassroomInfo newClassroom = new ClassroomInfo("2", "Miguel Gamboa");
                dynamicProperty.SetValue(student, newClassroom);
                Assert.Equal(newClassroom, dynamicProperty.GetValue(student));
                Assert.Equal(newClassroom, targetProperty.GetValue(student));
            }
        }

        [Fact (Skip = "not working")]
        public void GetAllTest() {
            DynamicDataMapper dynamicDataMapper = new DynamicDataMapper(typeof(Student), new WeakDataSource("Number"));
            ClassroomInfo classroom1 = new ClassroomInfo("1", "Luís Falcão");
            ClassroomInfo classroom2 = new ClassroomInfo("2", "Miguel Gamboa"); 
            dynamicDataMapper.Add(new Student("47220", "João Nunes", classroom1));
            dynamicDataMapper.Add(new Student("47224", "Miguel Marques", classroom2));
            dynamicDataMapper.Add(new Student("47192", "Alexandre Silva", classroom2));
            int count = 0;
            foreach (var obj in dynamicDataMapper.GetAll()) {
                output.WriteLine("Student: {1} - {0}. Classroom: {2}", ((Student)obj).Name, ((Student)obj).Number, ((Student)obj).Classroom.Token);
                count++;
            }
            Assert.True(count == 3);
            dynamicDataMapper.Delete("47220");
            dynamicDataMapper.Delete("47224");
            dynamicDataMapper.Delete("47192");
        }

        [Fact]
        public void GetByIdTest() {
            DynamicDataMapper dynamicDataMapper = new DynamicDataMapper(typeof(Person), new WeakDataSource("Id"));
            Person person = new Person(1, "João", "Nunes", new Info(170, 21, 'M'));
            dynamicDataMapper.Add(person);
            Person getByIdPerson = (Person)dynamicDataMapper.GetById(person.Id);
            Person expected = person;
            expected.Surname = null;
            Assert.Equal(expected.Info, getByIdPerson.Info);
            Assert.Equal(expected.Id, getByIdPerson.Id);
        }

        [Fact]
        public void UpdateTest() {
            DynamicDataMapper dynamicDataMapper = new DynamicDataMapper(typeof(Person), new WeakDataSource("Id"));
            Person person = new Person(1, "João", "Nunes", new Info(170, 21, 'M'));
            Person updatePerson = new Person(1, "Pedro", "Nunes", new Info(150, 20, 'M'));
            dynamicDataMapper.Add(person);
            dynamicDataMapper.Update(new Person(1, "Pedro", "Nunes", new Info(150, 20, 'M')));
            Person getByIdPerson = (Person)dynamicDataMapper.GetById(1);
            Assert.Equal(updatePerson.Info, getByIdPerson.Info);
            Assert.Equal(updatePerson.Name, getByIdPerson.Name);
        }

        [Fact]
        public void DeleteTest() {
            DynamicDataMapper dynamicDataMapper = new DynamicDataMapper(typeof(Person), new WeakDataSource("Id"));
            Person person = new Person(1, "João", "Nunes", new Info(170, 21, 'M'));
            dynamicDataMapper.Add(person);
            dynamicDataMapper.Delete(1);
            Person getByIdPerson = (Person)dynamicDataMapper.GetById(1);
            Assert.Null(getByIdPerson);
        }
    }
}