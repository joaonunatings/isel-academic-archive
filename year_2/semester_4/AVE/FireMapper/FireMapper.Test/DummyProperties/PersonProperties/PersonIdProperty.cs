using System;
using FireMapper.Interfaces;
using FireMapper.Test.DomainClasses;

namespace FireMapper.Test.DummyProperties.PersonProperties {

    public class PersonIdProperty : IProperty {

        public string GetName() {
            return "Id";
        }

        public object GetValue(object target) {
            return ((Person)target).Id;
        }

        public bool IsKey() {
            return true;
        }

        public bool ToIgnore() {
            return false;
        }

        public bool IsCollection() {
            return false;
        }

        public Type GetIPropertyType() {
            return typeof(int);
        }

        public object SetValue(object target, object value) {
            Person person = (Person)target;
            person.Id = (int)value;
            return person;
        }
    }
}