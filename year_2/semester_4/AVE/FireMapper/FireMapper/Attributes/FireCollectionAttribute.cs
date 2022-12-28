using System;

namespace FireMapper.Attributes {

    [AttributeUsage(AttributeTargets.Class | AttributeTargets.Struct)] //Default value: AllowMultiple = false
    public class FireCollectionAttribute : Attribute {
        public string Collection { get; private set; }

        public FireCollectionAttribute(string collection) {
            Collection = collection;
        }
    }
}