using System.Reflection;
using FireSource;

namespace FireMapper.Property {

    public class PropertyComplex : AbstractProperty {
        private readonly FireDataMapper mapper;

        public PropertyComplex(PropertyInfo property, IDataSource source) : base(property) {
            mapper = new FireDataMapper(property.PropertyType, source);
        }

        public override object GetValue(object target) {
            return mapper.Key.GetValue(Property.GetValue(target));
        }

        public override object SetValue(object target, object value) {
            object toSet = mapper.GetById(value);
            Property.SetValue(target, toSet);
            return Property;
        }
    }
}