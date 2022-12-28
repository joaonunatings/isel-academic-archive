using System;
using System.Collections.Generic;
using System.IO;
using FireSource.Test.DomainClasses;
using Google.Apis.Requests.Parameters;

namespace FireSource.Test {

    /// <summary>
    /// A single test context shared among all the tests.
    /// Test classes should implement IClassFixture<FireStoreFixture> and
    /// provide a constructor to inject a Fixture object.
    /// </summary>
    public class FireStoreFixture : IDisposable
    {
        private const string SOURCE_ITEMS = "Resources/isel-AVE-2021.txt";

        public readonly FireDataSource studentsDb;

        public readonly FireDataSource classroomsDb;

        public void Dispose() {
            ///
            /// ... clean up test data from the database ...
            ///
            Clear(studentsDb, "Number");
            Clear(classroomsDb, "Token");
        }

        private static void Clear(FireDataSource source, string key) {
            IEnumerable<Dictionary<string, object>> docs = source.GetAll();
            foreach (var pairs in docs) {
                source.Delete(pairs[key]);
            }
        }

        public FireStoreFixture()
        {
            string firebaseProjectId;
            var firebaseCredentialsPath = firebaseProjectId = null;
            Utils.extractCredentials(ref firebaseCredentialsPath, ref firebaseProjectId);
            studentsDb = new FireDataSource(
                firebaseProjectId,
                "Students",
                "Number",
                firebaseCredentialsPath);
            classroomsDb = new FireDataSource(
                firebaseProjectId,
                "Classrooms",
                "Token",
                firebaseCredentialsPath);
            
            CreateClassrooms();
            AddToFirestoreFrom(SOURCE_ITEMS);
        }

        private void CreateClassrooms() {
            InsertClassroomFor("TLI41D", "Miguel Gamboa");
            InsertClassroomFor("TLI42D", "Luís Falcão");
            InsertClassroomFor("TLI41N", "Miguel Gamboa");
            InsertClassroomFor("TLI4NXST", "NA");
            InsertClassroomFor("TLI4DXST", "NA");
        }

        private void InsertClassroomFor(string token, string teacher) {
            classroomsDb.Add(new Dictionary<string, object>() {
                {"Teacher", teacher},
                {"Token", token},
            });
        }

        private void AddToFirestoreFrom(string path) {
            foreach (string line in Lines(path)) {
                Student st = Student.Parse(line);
                studentsDb.Add(new Dictionary<string, object>() {
                    {"Name", st.Name},
                    {"Number", st.Number},
                    {"Classroom", st.Classroom},
                });
            }
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