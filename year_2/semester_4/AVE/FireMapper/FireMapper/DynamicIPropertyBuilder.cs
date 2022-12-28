using System;
using System.Reflection;
using System.Reflection.Emit;
using FireMapper.Attributes;
using FireMapper.Interfaces;

namespace FireMapper {

    public class DynamicIPropertyBuilder {
        private AssemblyBuilder assemblyBuilder;
        private ModuleBuilder moduleBuilder;
        private AssemblyName assemblyName = new AssemblyName("DynamicIProperty");

        private static DynamicIPropertyBuilder instance = new DynamicIPropertyBuilder();

        private DynamicIPropertyBuilder() {
            assemblyBuilder = AssemblyBuilder.DefineDynamicAssembly(assemblyName, AssemblyBuilderAccess.RunAndSave);
            moduleBuilder = assemblyBuilder.DefineDynamicModule(assemblyName.Name, assemblyName.Name + ".dll");
        }

        public static DynamicIPropertyBuilder Instance {
            get { return instance; }
        }

        ~DynamicIPropertyBuilder() {
            assemblyBuilder.Save(assemblyName.Name + ".dll");
        }

        public IProperty GenerateIPropertyFor(Type targetType, PropertyInfo targetProperty) {
            Type propertyType = BuildDynamicPropertyTypeFor(targetType, targetProperty);
            IProperty dynamicProperty =
                (IProperty)Activator.CreateInstance(propertyType, new object[] { }); // check for null constructor
            return dynamicProperty;
        }

        private Type BuildDynamicPropertyTypeFor(Type targetType, PropertyInfo targetProperty) {
            String typeName = targetType.Name + targetProperty.Name + "IProperty";
            if (moduleBuilder.GetType(typeName) != null)
                return moduleBuilder.GetType(typeName);
            TypeBuilder typeBuilder = moduleBuilder.DefineType(
                typeName,
                TypeAttributes.Public,
                null,
                new Type[] { typeof(IProperty) });

            AddGetName(typeBuilder, targetProperty);
            AddGetValue(typeBuilder, targetProperty, targetType);
            AddIsKey(typeBuilder, targetProperty);
            AddToIgnore(typeBuilder, targetProperty);
            AddIsCollection(typeBuilder, targetProperty);
            AddGetIPropertyType(typeBuilder, targetProperty);
            AddSetValue(typeBuilder, targetProperty, targetType);
            return typeBuilder.CreateType();
        }

        private void AddGetName(TypeBuilder typeBuilder, PropertyInfo targetProperty) {
            MethodBuilder methodBuilder = typeBuilder.DefineMethod(
                "GetName",
                MethodAttributes.Public | MethodAttributes.Virtual | MethodAttributes.HideBySig,
                typeof(string),
                new Type[0]);

            ILGenerator ilGen = methodBuilder.GetILGenerator();
            ilGen.Emit(OpCodes.Ldstr, targetProperty.Name);
            ilGen.Emit(OpCodes.Ret);
        }

        private void AddGetValue(TypeBuilder typeBuilder, PropertyInfo targetProperty, Type targetType) {
            MethodBuilder methodBuilder = typeBuilder.DefineMethod(
                "GetValue",
                MethodAttributes.Public | MethodAttributes.Virtual | MethodAttributes.HideBySig,
                typeof(object),
                new Type[] { typeof(object) }
            );

            ILGenerator ilGen = methodBuilder.GetILGenerator();
            
            ilGen.Emit(OpCodes.Ldarg_1);
            if (targetType.IsValueType) {
                LocalBuilder a = ilGen.DeclareLocal(targetType);
                ilGen.Emit(OpCodes.Unbox_Any, targetType);
                ilGen.Emit(OpCodes.Stloc, a);
                ilGen.Emit(OpCodes.Ldloca_S, a);
                ilGen.Emit(OpCodes.Call, targetProperty.GetGetMethod());
            }
            else {
                ilGen.Emit(OpCodes.Castclass, targetType);
                ilGen.Emit(OpCodes.Callvirt, targetProperty.GetGetMethod());                
            }
            if (targetProperty.PropertyType.IsValueType) ilGen.Emit(OpCodes.Box, targetProperty.PropertyType);  //value-type -> object (for reference)
            ilGen.Emit(OpCodes.Ret);
        }

        private void AddIsKey(TypeBuilder typeBuilder, PropertyInfo targetProperty) {
            MethodBuilder methodBuilder = typeBuilder.DefineMethod(
                "IsKey",
                MethodAttributes.Public | MethodAttributes.Virtual | MethodAttributes.HideBySig,
                typeof(bool),
                new Type[0]);

            ILGenerator ilGen = methodBuilder.GetILGenerator();

            ilGen.Emit(targetProperty.IsDefined(typeof(FireKeyAttribute)) ? OpCodes.Ldc_I4_1 : OpCodes.Ldc_I4_0);

            ilGen.Emit(OpCodes.Ret);
        }

        private void AddToIgnore(TypeBuilder typeBuilder, PropertyInfo targetProperty) {
            MethodBuilder methodBuilder = typeBuilder.DefineMethod(
                "ToIgnore",
                MethodAttributes.Public | MethodAttributes.Virtual | MethodAttributes.HideBySig,
                typeof(bool),
                new Type[0]);

            ILGenerator ilGen = methodBuilder.GetILGenerator();

            ilGen.Emit(targetProperty.IsDefined(typeof(FireIgnoreAttribute)) ? OpCodes.Ldc_I4_1 : OpCodes.Ldc_I4_0);

            ilGen.Emit(OpCodes.Ret);
        }

        private void AddIsCollection(TypeBuilder typeBuilder, PropertyInfo targetProperty) {
            MethodBuilder methodBuilder = typeBuilder.DefineMethod(
                "IsCollection",
                MethodAttributes.Public | MethodAttributes.Virtual | MethodAttributes.HideBySig,
                typeof(bool),
                new Type[0]);

            ILGenerator ilGen = methodBuilder.GetILGenerator();
            if (targetProperty.PropertyType.IsDefined(typeof(FireCollectionAttribute))) ilGen.Emit(OpCodes.Ldc_I4_1);
            else ilGen.Emit(OpCodes.Ldc_I4_0);
            ilGen.Emit(OpCodes.Ret);
        }

        private void AddGetIPropertyType(TypeBuilder typeBuilder, PropertyInfo targetProperty) {
            MethodBuilder methodBuilder = typeBuilder.DefineMethod(
                "GetIPropertyType",
                MethodAttributes.Public | MethodAttributes.Virtual | MethodAttributes.HideBySig,
                typeof(Type),
                new Type[0]);

            ILGenerator ilGen = methodBuilder.GetILGenerator();
            Type propertyType = targetProperty.PropertyType;
            ilGen.Emit(OpCodes.Ldtoken, propertyType);
            ilGen.Emit(OpCodes.Ret);
        }

        private void AddSetValue(TypeBuilder typeBuilder, PropertyInfo targetProperty, Type targetType) {
            MethodBuilder methodBuilder = typeBuilder.DefineMethod(
                "SetValue",
                MethodAttributes.Public | MethodAttributes.Virtual | MethodAttributes.HideBySig,
                typeof(object),
                new Type[] { typeof(object), typeof(object) }
            );

            ILGenerator ilGen = methodBuilder.GetILGenerator();
            ilGen.Emit(OpCodes.Ldarg_1);
            if (targetType.IsValueType) {
                LocalBuilder a = ilGen.DeclareLocal(targetType);
                ilGen.Emit(OpCodes.Unbox_Any, targetType);
                ilGen.Emit(OpCodes.Stloc, a);
                ilGen.Emit(OpCodes.Ldloca_S, a);
            }
            else {
                ilGen.Emit(OpCodes.Castclass, targetType);
            }
            ilGen.Emit(OpCodes.Ldarg_2);
            if (targetProperty.PropertyType.IsValueType)
                ilGen.Emit(OpCodes.Unbox_Any, targetProperty.PropertyType);
            else
                ilGen.Emit(OpCodes.Castclass, targetProperty.PropertyType);
            if (targetType.IsValueType) {
                ilGen.Emit(OpCodes.Call, targetProperty.GetSetMethod());
                ilGen.Emit(OpCodes.Ldloc_0);
                ilGen.Emit(OpCodes.Box, targetType);
            }
            else {
                ilGen.Emit(OpCodes.Callvirt, targetProperty.GetSetMethod());
                ilGen.Emit(OpCodes.Ldarg_1);
            }
            ilGen.Emit(OpCodes.Ret);
        }
    }
}