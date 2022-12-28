using System;
using System.Collections.Generic;

namespace FireSource {
    public record WeakDataSource(string Key) : IDataSource {
        private readonly List<Dictionary<string, object>> ListDicts = new();

        public void Add(Dictionary<string, object> obj) {
            if (GetDictIfExists(obj) == null) ListDicts.Add(obj);
        }

        public void Delete(object KeyValue) {
            Dictionary<string, object> toRemove;
            if ((toRemove = GetById(KeyValue)) != null) ListDicts.Remove(toRemove);
        }

        public IEnumerable<Dictionary<string, object>> GetAll() {
            return ListDicts;
        }

        public Dictionary<string, object> GetById(object KeyValue) {
            object toCompare;
            foreach (var dicts in ListDicts) {
                dicts.TryGetValue(Key, out toCompare);
                if (toCompare != null && toCompare.Equals(KeyValue)) return dicts;
            }
            return null;
        }

        public void Update(Dictionary<string, object> obj) {
            Dictionary<string, object> toUpdate;
            if ((toUpdate = GetDictIfExists(obj)) != null) {
                object toCompare;
                foreach (var keyValue in obj) {
                    toUpdate.TryGetValue(keyValue.Key, out toCompare);
                    if (toCompare != null && !toCompare.Equals(keyValue.Value)) {
                        toUpdate.Remove(keyValue.Key);
                        toUpdate.Add(keyValue.Key, keyValue.Value);
                    }
                }
            }
            else throw new ArgumentException("No document in database for given Key = " + obj[Key]);
        }

        private Dictionary<string, object> GetDictIfExists(Dictionary<string, object> dict) {
            object toCompare;
            dict.TryGetValue(Key, out toCompare);
            return GetById(toCompare);
        }
    }
}