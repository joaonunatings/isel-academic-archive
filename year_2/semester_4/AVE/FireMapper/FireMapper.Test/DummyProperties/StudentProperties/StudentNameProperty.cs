using System;
using FireMapper.Interfaces;
using FireMapper.Test.DomainClasses;

namespace FireMapper.Test.DummyProperties.StudentProperties {

    public class StudentNameProperty : IProperty {

        public string GetName() {
            return "Name";
        }

        public object GetValue(object target) {
            return ((Student)target).Name;
        }

        public bool IsKey() {
            return false;
        }

        public bool ToIgnore() {
            return false;
        }

        public bool IsCollection() {
            return false;
        }

        public Type GetIPropertyType() {
            return typeof(string);
        }

        public object SetValue(object target, object value) {
            ((Student)target).Name = (string)value;
            return target;
        }
    }
}