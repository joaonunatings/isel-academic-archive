using System;
using FireMapper.Interfaces;
using FireMapper.Test.DomainClasses;

namespace FireMapper.Test.DummyProperties.StudentProperties {

    public class StudentNumberProperty : IProperty {

        public string GetName() {
            return "Number";
        }

        public object GetValue(object target) {
            return ((Student)target).Number;
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
            return typeof(string);
        }

        public object SetValue(object target, object value) {
            ((Student)target).Number = (string)value;
            return target;
        }
    }
}