# FireMapper

## Assignment 1

**DATA LIMITE de entrega**: 18 de Abril de 2021. Fazer _push_ a tag `trab1` no repositório do grupo.

Neste trabalho pretende-se desenvolver a biblioteca _FireMapper_ que
disponibiliza uma abstração sobre uma **colecção** de uma base de dados
documental FireStore. Em modo resumido, o FireStore é uma base de dados NoSQL
que armazena **colecções** de **documentos** JSON.

https://github.com/isel-leic-ave/FireMapper

Uma base de dados FireStore é gerida a partir de um projecto FireBase. 
**De modo a se familiarizar com a tecnologia deve em primeiro lugar seguir os
passos do guião 
“[_FireStore get started_](https://github.com/isel-leic-ave/FireMapper/blob/master/isel-AVE-2021-FireStore-get-started.md)”**
para criar alguns documentos e uma pequena aplicação em C# que se liga a essa
base de dados listando o seu conteúdo na consola.

O objectivo da biblioteca _FireMapper_ é facilitar o acesso aos **documentos**
de uma **colecção** por via de um `IDataMapper`. Esta interface especifica os
métodos de acesso à **coleção** e que correspondem às operações CRUD.

```csharp
    public interface IDataMapper
    {
        IEnumerable GetAll();
        object GetById(object keyValue);
        void Add(object obj);
        void Update(object obj);
        void Delete(object keyValue);
    }
```

Por cada colecção deve existir uma **classe de domínio** com propriedades
correspondentes às propriedades de um **documento**. Essas classes podem ter
informação complementar dada na forma de anotações, por via dos seguintes
_custom attributes_:
* `FireCollection` - aplicado a uma classe para identificar o nome da coleção Firestore.
* `FireKey` - identifica a propriedade que é chave única na pesquisa de um documento através do método `GetById`
* `FireIgnore` - propriedade a ignorar no mapeamento com um documento.

Exemplo:

```c#
[FireCollection("Students")]
public record Student(
    [property:FireKey] string Number,
    string Name, 
    [property:FireIgnore] string Classroom)  
{}
```

Students collection:

```js
{
    “Name”: “Zanda Cantanda”,
    “Number”: “72538”,
    “Classroom”: “TLI41D”
}
```



A classe `FireDataMapper` implementa a interface `IDataMapper` com um
comportamento dependente da classe de domínio (e.g. `Student`), cujo `Type` é
fornecido na sua instanciação.

```csharp
IDataMapper studentsMapper = new FireDataMapper(typeof(Student), ...);
```

A implementação de `FileDataMapper` deve ser feita com o suporte da classe
`FireDataSource` da biblioteca _FireSource_ disponibilizado no respectivo
projecto que integra a solução.


Enquanto a classe `FireDataSource` lida com dados fracamente tipificados na
forma de `Dictionary<string, object>`, a classe `FireDataMapper` trata objectos
de domínio, e.g. instâncias de `Student`.


1. Implemente os _custom attributes_ `FireCollection`, `FireKey` e `FireIgnore`.

1. Usando a API de Reflexão implemente a classe `FireDataMapper` que faz o
   mapeamento entre objectos de domínio e dados na forma de 
   `Dictionary<string, object>` manipulados por uma instância de `IDataSource`.
   Implemente os testes unitários que validem o correcto funcionamento dos métodos,
   incluíndo casos de excepção como por exemplo, ausência de anotações; mais que uma
   propriedade anotada com `FireKey`; etc.
   Garanta o máximo de cobertura observando o _coverage report_ obtido através do 
   procedimento descrito no README.md.

1. Faça uma implementação alternativa de `IDataSource` na classe
   `WeakDataSource` que mantém os dados apenas em memória (defina a estrutura de
   dados ao seu critério). Valide o funcionamento com testes unitários. Note que
   a classe `FireDataMapper` pode funcionar com qualquer implementação de
   `IDataSource` especificada por parâmetro do construtor.

1. Defina as classes de um modelo de domínio e crie uma nova base de dados para
   esse modelo no FireStore e teste com a sua biblioteca _FireMapper_. Exemplos:
   carros, filmes, música, desportos, jogadores de futebol, ligas de futebol,
   jogadores da NBA, videogames, surfistas, lutadores, séries de TV, surf spots,
   praias, cidades do mundo, resorts de neve, hotéis, marcas, etc.
   **Requisitos**:
   * O modelo definido deverá incluir no mínimo duas entidades (classes) com uma
     relação de associação. 
   * Use _auto id_ nos documentos da base de dados.
   * Veja o exemplo de associação criado nos testes do projecto
     _FireSource.Tests_ e como a propriedade `Classroom` de um documento
     `Student` corresponde à propriedade `Token` de um documento `Classroom`
     semelhante ao comportamento de uma _foreign key_.
   * Cada grupo de trabalho deverá usar um modelo de domínio distinto.
   * **Modelos de domínio mais ricos em termos de dados e relações entre si, serão valorizados.**

Faça um _pull request_ para o repositório
https://github.com/isel-leic-ave/FireMapper/ para adicionar um novo ficheiro na
pasta `Db` que identifica o modelo domínio. Cada grupo deve escolher um domínio
diferente. Os domínios são atribuídos de acordo com a ordem dos _pull request_.

Cada _pull request_ deve atender aos seguintes requisitos:
* Um novo ficheiro com o nome na forma: `<turma>`-`<grupo>`-nome-do-dominio.txt,
  por exemplo: i41d-g07-surf-spots.txt
* O conteúdo do ficheiro deve descrever o esquema (nomes de campo e tipo) de
  cada coleção. Deve ter pelo menos duas coleções distintas com uma associação
  (_foreign key_) entre elas.
* Um exemplo dos documentos.

5. `DataFireMapper` deve suportar classes de domínio com propriedades de tipo
   definido por outras classes de domínio. Neste caso deve criar uma outra
   instância de `DataFireMapper` auxiliar para o respectivo tipo da propriedade
   que permite aceder à respectiva colecção. Valide o funcionamento da
   associação em testes unitários.

Exemplo  a classe `ClassroomInfo` correspondente ao tipo da propriedade `Classroom`:

```csharp
[FireCollection("Students")]
public record Student( [property:FireKey] string Number, string Name, ClassroomInfo Classroom)  {}

[FireCollection("Classrooms")]
public record ClassroomInfo([property:FireKey] string Token, string Teacher) {}
```

## Run tests

1. place your Firebase key json file in folder `FireSource.Test\Resources`.
1. update the path in `<Content Include="Resources\dummydemo-11dd3-firebase-adminsdk-vp6c5-28b7f0fa93.json">` of `FireSource.Test.csproj`
1. update the `FireStoreFixture.cs` of `FireSource.Test` project with:
   * correct path to Firebase key json file in constant `FIREBASE_CREDENTIALS_PATH`
   * project id in constant `FIREBASE_PROJECT_ID`

**FIRST create your Firestore database following the guide [isel-AVE-2021-FireStore-get-started.md](isel-AVE-2021-FireStore-get-started.md)**

------

Run tests with `dotnet test --logger "console;verbosity=detailed"` to see `Console` output.

List tests with `dotnet test -t`

Select tests to run with `dotnet test --filter NameOfTheClassTest`

Run coverage with:

```
dotnet test --collect:"XPlat Code Coverage"
reportgenerator -reports:Project.Tests\TestResults\66e8839d-6844-4b8a-8067-dc9c32abed5d\coverage.cobertura.xml  -targetdir:coverage
coverage\index.htm
```

## UML Diagram

High level view of projects (in `<<...>>`) and core types:

<img src="C:\Users\j0a0n\OneDrive - Instituto Politécnico de Lisboa\ISEL\2ano\2semestre\AVE\Trabalhos\ave-2021-firemapper-i42d-g06\FireMapper.svg">

***

