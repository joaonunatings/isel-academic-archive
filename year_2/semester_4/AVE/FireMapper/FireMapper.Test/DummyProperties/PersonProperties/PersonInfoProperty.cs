using System;
using FireMapper.Interfaces;
using FireMapper.Test.DomainClasses;

namespace FireMapper.Test.DummyProperties.PersonProperties {

    public class PersonInfoProperty : IProperty {

        public string GetName() {
            return "Info";
        }

        public object GetValue(object target) {
            return ((Person)target).Info;
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
            return typeof(Info);
        }

        public object SetValue(object target, object value) {
            ((Person)target).Info = (Info)value;
            return target;
        }
    }
}