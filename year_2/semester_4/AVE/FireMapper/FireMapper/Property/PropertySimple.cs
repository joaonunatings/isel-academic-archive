using System.Reflection;
using FireMapper.Attributes;

namespace FireMapper.Property {

    public class PropertySimple : AbstractProperty {

        public PropertySimple(PropertyInfo property) : base(property) {
        }

        public override object GetValue(object target) {
            return Property.GetValue(target);
        }

        public override object SetValue(object target, object value) {
            Property.SetValue(target, value);
            return Property;
        }
    }
}