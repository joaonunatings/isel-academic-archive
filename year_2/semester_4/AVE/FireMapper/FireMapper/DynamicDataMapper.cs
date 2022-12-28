using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using FireMapper.Interfaces;
using FireSource;

namespace FireMapper {

    public class DynamicDataMapper : IDataMapper {
        private readonly DynamicIPropertyBuilder PropertyBuilder;
        private readonly IDataSource DataSource;
        private IProperty[] IProp;
        private readonly ConstructorInfo Ctor;
        public IProperty Key { get; private set; }

        public DynamicDataMapper(Type domain, IDataSource dataSource) {
            DataSource = dataSource;
            PropertyBuilder = DynamicIPropertyBuilder.Instance;
            Init(domain);
            Ctor = domain.GetConstructor(Type.EmptyTypes);
        }

        private void Init(Type objType) {
            PropertyInfo[] pInfo = objType.GetProperties();
            IProp = new IProperty[pInfo.Length];
            for (int i = 0; i < pInfo.Length; i++) {
                IProp[i] = PropertyBuilder.GenerateIPropertyFor(objType,
                    pInfo[i]);
                if (IProp[i].IsKey()) Key = IProp[i];
            }

            if (Key == null) throw new Exception("FireKey not assigned");
        }

        public IEnumerable GetAll() {
            List<Dictionary<string, object>> dictList = DataSource.GetAll().ToList();
            if (dictList.Count == 0) return null;
            List<object> list = new List<object>(dictList.Count);
            foreach (var dict in dictList) {
                list.Add(DicToObject(dict));
            }

            return list;
        }

        public object GetById(object keyValue) {
            Dictionary<string, object> dict = DataSource.GetById(keyValue);
            return DicToObject(dict);
        }

        public void Add(object obj) {
            object pkey = null;
            foreach (IProperty pi in IProp) {
                if (pi.IsKey())
                    pkey = pi.GetValue(obj);
            }

            if (GetById(pkey) != null) return;
            DataSource.Add(ObjectToDic(obj));
        }

        public void Update(object obj) {
            object keyValue = GetById(Key.GetValue(obj));
            if (keyValue == null) return;
            DataSource.Update(ObjectToDic(obj));
        }

        public void Delete(object keyValue) {
            if (GetById(keyValue) != null) DataSource.Delete(keyValue);
        }

        private Dictionary<string, object> ObjectToDic(object obj) {
            Dictionary<string, object> dict = new Dictionary<string, object>();

            foreach (IProperty prop in IProp) {
                if (!prop.ToIgnore()) {
                    dict.Add(prop.GetName(),
                        prop.IsCollection() ? GetIPropertyKey(prop, prop.GetValue(obj)) : prop.GetValue(obj));
                }
            }

            return dict;
        }

        private object GetIPropertyKey(IProperty property, object obj) {
            foreach (PropertyInfo pi in property.GetIPropertyType().GetProperties()) {
                IProperty newIProp = PropertyBuilder.GenerateIPropertyFor(property.GetIPropertyType(), pi);
                if (newIProp.IsKey()) return newIProp.GetValue(obj);
            }

            throw new Exception("Domain key value not found.");
        }

        private object DicToObject(Dictionary<string, object> dict) {
            if (dict == null) return null;
            object objValue;

            object newDomain = Ctor.Invoke(null);

            for (int i = 0; i < IProp.Length; i++) {
                if (IProp[i].ToIgnore())
                    newDomain = IProp[i].SetValue(newDomain, null);
                else {
                    dict.TryGetValue(IProp[i].GetName(), out objValue);
                    if (IProp[i].IsCollection()) {
                        ConstructorInfo newCtor = IProp[i].GetIPropertyType().GetConstructor(Type.EmptyTypes);
                        object propertyDomain = newCtor.Invoke(null);
                        foreach (PropertyInfo pi in IProp[i].GetIPropertyType().GetProperties()) {
                            IProperty newIProp = PropertyBuilder.GenerateIPropertyFor(IProp[i].GetIPropertyType(), pi);
                            if (newIProp.IsKey()) propertyDomain = newIProp.SetValue(propertyDomain, objValue);
                        }
                        newDomain = IProp[i].SetValue(newDomain, propertyDomain);
                    }
                    else newDomain = IProp[i].SetValue(newDomain, objValue);
                }
            }
            return newDomain;
        }
    }
}