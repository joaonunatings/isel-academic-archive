using System;
using FireMapper.Test.DomainClasses;

namespace FireMapper.Test.DummyProperties.CardProperties {
    public class CardInfoProperty {
        public string GetName() {
            return "Info";
        }

        public object GetValue(object target) {
            return ((Card)target).Info;
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
            Card card = (Card)target;
            card.Info = (Info)value;
            return card;
        }
    }
}