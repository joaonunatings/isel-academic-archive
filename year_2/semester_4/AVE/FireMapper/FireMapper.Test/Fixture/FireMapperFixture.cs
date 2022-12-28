using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using FireMapper.Test.DomainClasses;
using FireSource;

namespace FireMapper.Test.Fixture {

    public class FireMapperFixture : IDisposable {
        
        private const string BRANDS_SOURCE_ITEMS = "Resources/isel-AVE-2021_g06_Brands.txt";
        private const string CATEGORIES_SOURCE_ITEMS = "Resources/isel-AVE-2021_g06_Categories.txt";
        private const string INSTRUMENTS_SOURCE_ITEMS = "Resources/isel-AVE-2021_g06_Instruments.txt";
        private const string STOCKS_SOURCE_ITEMS = "Resources/isel-AVE-2021_g06_Stocks.txt";

        public readonly FireDataMapper brandsDm = Init(typeof(Brand), "Brands", "Name");
        public readonly FireDataMapper categoriesDm = Init(typeof(Category), "Categories", "Name");
        public readonly FireDataMapper instrumentsDm = Init(typeof(Instrument), "Instruments", "Name");
        public readonly FireDataMapper stocksDm = Init(typeof(Stock), "Stocks", "Reference");
        public readonly FireDataMapper studentsDm = Init(typeof(Student), "Students", "Number");
        public readonly FireDataMapper classroomsDm = Init(typeof(ClassroomInfo), "Classrooms", "Token");

        private static FireDataMapper Init(Type type, string collection, string key) {
            //return new(type, new FireDataSource(FIREBASE_PROJECT_ID, collection, key, FIREBASE_CREDENTIALS_PATH));
            return new(type, new WeakDataSource(key));
        }

        public void Dispose() {
            Clear(brandsDm);
            Clear(categoriesDm);
            Clear(instrumentsDm);
            Clear(stocksDm);
            Clear(studentsDm);
            Clear(classroomsDm);
        }

        private static void Clear(FireDataMapper source) {
            IEnumerable objs = source.GetAll();
            foreach (var obj in objs) {
                source.Delete(source.Key.GetValue(obj));
            }
        }

        public FireMapperFixture() {
            AddBrandsToFirestoreFrom(BRANDS_SOURCE_ITEMS);
            AddCategoriesToFirestoreFrom(CATEGORIES_SOURCE_ITEMS);
            AddInstrumentsToFirestoreFrom(INSTRUMENTS_SOURCE_ITEMS);
            AddStocksToFirestoreFrom(STOCKS_SOURCE_ITEMS);
            CreateClassrooms();
            CreateStudents();
        }

        private void AddCategoriesToFirestoreFrom(string path) {
            foreach (string line in Lines(path)) {
                Category c = Category.Parse(line);
                categoriesDm.Add(c);
            }
        }

        private void AddBrandsToFirestoreFrom(string path) {
            foreach (string line in Lines(path)) {
                Brand b = Brand.Parse(line);
                brandsDm.Add(b);
            }
        }

        private void AddInstrumentsToFirestoreFrom(string path) {
            foreach (string line in Lines(path)) {
                Instrument i = Instrument.Parse(line);
                instrumentsDm.Add(i);
            }
        }

        private void AddStocksToFirestoreFrom(string path) {
            foreach (string line in Lines(path)) {
                Stock s = Stock.Parse(line);
                stocksDm.Add(s);
            }
        }

        private void CreateStudents() {
            InsertStudentFor("47220", "João Nunes", "1", "Luís Falcão");
            InsertStudentFor("47204", "Miguel Marques", "1", "Luís Falcão");
            InsertStudentFor("47192", "Alexandre Silva", "3", "Luís Falcão");
        }

        private void InsertStudentFor(string number, string name, string token, string teacher) {
            Student s = new Student(
                number,
                name,
                new ClassroomInfo(
                    token,
                    teacher));
            studentsDm.Add(s);
        }

        private void CreateClassrooms() {
            InsertClassroomFor("1", "Luís Falcão");
            InsertClassroomFor("3", "Luís Falcão");
            InsertClassroomFor("4", "Miguel Gamboa");
        }

        private void InsertClassroomFor(string token, string teacher) {
            ClassroomInfo c = new ClassroomInfo(
                token,
                teacher);
            classroomsDm.Add(c);
        }

        private static IEnumerable<string> Lines(string path) {
            string line;
            IList<string> res = new List<string>();
            using (StreamReader file = new StreamReader(path)) // <=> try-with resources do Java >= 7
            {
                while ((line = file.ReadLine()) != null) {
                    res.Add(line);
                }
            }

            return res;
        }
    }
}