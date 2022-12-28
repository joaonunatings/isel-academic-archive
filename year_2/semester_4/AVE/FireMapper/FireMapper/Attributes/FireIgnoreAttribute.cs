using System;

namespace FireMapper.Attributes {

    [AttributeUsage(AttributeTargets.Property)] //Default value: AllowMultiple = false
    public class FireIgnoreAttribute : Attribute {

        public FireIgnoreAttribute() {
        }
    }
}