using System;
using System.IO;
using System.Runtime.Serialization.Json;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

public static class Utils
{
    public static void extractCredentials(ref string firebaseCredentialsPath, ref string firebaseProjectId)
    {
        if (firebaseCredentialsPath == null)
            if ((firebaseCredentialsPath = Environment.GetEnvironmentVariable("FIREBASE_CREDENTIALS_PATH")) == null)
                throw new Exception("Please set FIREBASE_CREDENTIALS_PATH environment variable");

        var firebaseJsonStr = File.ReadAllText(firebaseCredentialsPath);
        var firebaseJson = (JObject)JsonConvert.DeserializeObject(firebaseJsonStr)!;
        firebaseProjectId = firebaseJson.SelectToken("project_id")!.Value<string>();
    }
}