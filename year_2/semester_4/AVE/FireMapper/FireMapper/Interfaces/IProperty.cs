using System;

namespace FireMapper.Interfaces {

    public interface IProperty {

        string GetName();

        object GetValue(object target);

        bool IsKey();

        bool ToIgnore();

        bool IsCollection();

        Type GetIPropertyType();

        object SetValue(object target, object value);
    }
}