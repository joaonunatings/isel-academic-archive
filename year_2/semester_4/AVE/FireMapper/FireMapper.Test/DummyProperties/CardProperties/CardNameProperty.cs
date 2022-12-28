using System;
using FireMapper.Test.DomainClasses;

namespace FireMapper.Test.DummyProperties.CardProperties {
    public class CardNameProperty {
        public string GetName() {
            return "Name";
        }

        public object GetValue(object target) {
            return ((Card)target).Name;
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
            return typeof(Card);
        }

        public object SetValue(object target, object value) {
            Card card = (Card)target;
            card.Name = (string)value;
            return card;
        }
    }
}