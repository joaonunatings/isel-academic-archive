using System;
using FireMapper.Interfaces;
using FireMapper.Test.DomainClasses;

namespace FireMapper.Test.DummyProperties.StudentProperties {

    public class StudentClassroomProperty : IProperty {

        public string GetName() {
            return "Classroom";
        }

        public object GetValue(object target) {
            return ((Student)target).Classroom.Token;
        }

        public bool IsKey() {
            return false;
        }

        public bool ToIgnore() {
            return false;
        }

        public bool IsCollection() {
            return true;
        }

        public Type GetIPropertyType() {
            return typeof(ClassroomInfo);
        }

        public object SetValue(object target, object value) {
            ((Student)target).Classroom = (ClassroomInfo)value;
            return target;
        }
    }
}