using Xunit;

namespace FireMapper.Test.Fixture {

    [CollectionDefinition("FireMapperFixture collection")]
    public class FireMapperFixtureCollection : ICollectionFixture<FireMapperFixture> {
        // This class has no code, and is never created. Its purpose is simply
        // to be the place to apply [CollectionDefinition] and all the
        // ICollectionFixture<> interfaces.
    }
}