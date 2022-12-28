using System;
using FireMapper.Test.DomainClasses;

namespace FireMapper.Test.DummyProperties.CardProperties {
    public class CardIdProperty {
        
        public string GetName() {
            return "Id";
        }

        public object GetValue(object target) {
            return ((Card)target).Id;
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
            Card card = (Card)target;
            card.Id = (int)value;
            return card;
        }
    }
}