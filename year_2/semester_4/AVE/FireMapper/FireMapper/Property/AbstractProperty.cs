using System;
using System.Reflection;
using FireMapper.Attributes;
using FireMapper.Interfaces;

namespace FireMapper.Property {

    public abstract class AbstractProperty : IProperty {
        protected readonly PropertyInfo Property;

        public AbstractProperty(PropertyInfo property) {
            Property = property;
        }

        public string GetName() {
            return Property.Name;
        }

        public abstract object GetValue(object target);

        public bool IsKey() {
            return Property.IsDefined(typeof(FireKeyAttribute));
        }

        public bool ToIgnore() {
            return Property.IsDefined(typeof(FireIgnoreAttribute));
        }

        public bool IsCollection() {
            return Property.IsDefined(typeof(FireCollectionAttribute));
        }

        public Type GetIPropertyType() {
            return Property.PropertyType;
        }

        public abstract object SetValue(object target, object value);
    }
}