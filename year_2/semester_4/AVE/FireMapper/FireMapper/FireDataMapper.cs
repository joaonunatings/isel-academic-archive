using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using FireMapper.Attributes;
using FireMapper.Interfaces;
using FireMapper.Property;
using FireSource;

namespace FireMapper {

    public class FireDataMapper : IDataMapper {
        private readonly IDataSource DataSource;
        private IProperty[] IProp;
        private readonly ConstructorInfo Ctor; // Constructor for the current Collection.
        public IProperty Key { get; private set; }

        /*
         * Domain specifies the Collection/Object type
         */

        public FireDataMapper(Type domain, IDataSource dataSource) {
            DataSource = dataSource;
            Init(domain);
            Ctor = BuildConstructor(domain);
        }

        private void Init(Type objType) {
            PropertyInfo[] pInfo = objType.GetProperties();
            IProp = new IProperty[pInfo.Length];
            for (int i = 0; i < pInfo.Length; i++) {
                if (pInfo[i].PropertyType.IsDefined(typeof(FireCollectionAttribute), true)) {
                    IProp[i] = new PropertyComplex(pInfo[i], DataSourceBuilder(pInfo[i].PropertyType));
                    continue;
                }

                if (pInfo[i].IsDefined(typeof(FireKeyAttribute), true)) {
                    if (Key != null) throw new Exception("Multiple FireKey assigned");
                    Key = new PropertySimple(pInfo[i]);
                }

                IProp[i] = new PropertySimple(pInfo[i]);
            }

            if (Key == null) throw new Exception("FireKey not assigned");
        }

        private ConstructorInfo BuildConstructor(Type objType) {
            PropertyInfo[] properties = objType.GetProperties();
            Type[] propType = new Type[properties.Length];
            for (int i = 0; i < properties.Length; i++) {
                propType[i] = properties[i].PropertyType;
            }

            return objType.GetConstructor(propType.ToArray());
        }

        private IDataSource DataSourceBuilder(Type objType) {
            PropertyInfo[] properties = DataSource.GetType().GetProperties();
            ConstructorInfo constructor = BuildConstructor(DataSource.GetType());
            object[] parameters = new object[properties.Length];

            for (int i = 0; i < properties.Length; i++) {
                if (properties[i].Name == "Collection") {
                    FireCollectionAttribute collection =
                        (FireCollectionAttribute)Attribute.GetCustomAttribute(objType,
                            typeof(FireCollectionAttribute));
                    parameters[i] = collection.Collection;
                }
                else if (properties[i].Name == "Key") parameters[i] = GetKey(objType);
                else parameters[i] = properties[i].GetValue(DataSource);
            }

            return (IDataSource)constructor.Invoke(parameters);
        }

        private String GetKey(Type objType) {
            string key = null;
            foreach (PropertyInfo p in objType.GetProperties())
                if (p.IsDefined(typeof(FireKeyAttribute), true)) {
                    if (key != null) throw new Exception("Multiple FireKey assigned");
                    key = p.Name;
                }

            if (key == null) throw new Exception("FireKey not assigned");
            return key;
        }

        /*
         * Returns a IEnumerable of the Object Collection Type
         */

        public IEnumerable GetAll() {
            List<Dictionary<string, object>> dictList = DataSource.GetAll().ToList();
            if (dictList.Count == 0) return null;
            List<object> list = new List<object>(dictList.Count);
            foreach (var dict in dictList) {
                list.Add(DicToObject(dict));
            }

            return list;
        }

        /*
         * GetById method returns the object equivalent to the Type specified from the KeyValue object introduced
         */

        public object GetById(object keyValue) {
            Dictionary<string, object> dict = DataSource.GetById(keyValue);
            return DicToObject(dict);
        }

        /*
         * Add method adds a new Object to the database if it doesn't exist already
         */

        public void Add(object obj) {
            object pkey = null;
            foreach (IProperty pi in IProp) {
                if (pi.IsKey())
                    pkey = pi.GetValue(obj);
            }

            if (GetById(pkey) != null) return;
            DataSource.Add(ObjectToDic(obj));
        }

        /*
         * Updates a given Obj to change his values("null" in constructor if doesn't want to get changed)
         */

        public void Update(object obj) {
            object keyValue = GetById(Key.GetValue(obj));
            if (keyValue == null) return;
            DataSource.Update(ObjectToDic(obj));
        }

        /*
         * Deletes a given KeyValue or informs user if it didn't find any data
         */

        public void Delete(object keyValue) {
            if (GetById(keyValue) != null) DataSource.Delete(keyValue);
        }

        /*
         * Creates a Dictionary from the given Obj
         */

        private Dictionary<string, object> ObjectToDic(object obj) {
            Dictionary<string, object> dict = new Dictionary<string, object>();

            foreach (var prop in IProp) {
                if (!(prop.ToIgnore())) {
                    dict.Add(prop.GetName(), prop.GetValue(obj));
                }
            }
            return dict;
        }

        /*
         * Converts a given dict to the Object Collection Type
         */

        private object DicToObject(Dictionary<string, object> dict) {
            if (dict == null) return null;
            object objValue;

            ConstructorInfo emptyConstructor = Ctor.DeclaringType.GetConstructor(Type.EmptyTypes);
            object newDomain = emptyConstructor.Invoke(null);

            for (int i = 0; i < IProp.Length; i++) {
                if (IProp[i].ToIgnore())
                    IProp[i].SetValue(newDomain, null);
                else {
                    dict.TryGetValue(IProp[i].GetName(), out objValue);
                    IProp[i].SetValue(newDomain, objValue);
                }
            }

            return newDomain;
        }
    }
}