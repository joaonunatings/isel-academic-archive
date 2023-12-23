# FireMapper
Library for mapping data between Firestore fields and C# data classes. There are two available implementations: one using only reflection and the other using Emit from reflection.

## Information

Project for Virtual Execution Environments @ ISEL (now called [Languages and Managed Runtimes](https://www.isel.pt/en/leic/languages-and-managed-runtimes)).

This project was split into two phases: 
- Phase 1: development of _FireDataMapper_ which uses Reflection in order to map the data ([Problem description](Docs/problem-description1.md)).
- Phase 2: development of _DynamicDataMapper_ which uses Reflection.Emit (generation of IL) in order to map the data ([Problem description](Docs/problem-description2.md)).

## How to run

### Requirements
- .NET Framework 4.8
- Cloud Firestore ([tutorial](Docs/firestore-tutorial.md))

The following instructions must be executed in the [root directory of the project](./) (isel-academic-archive/year_2/semester_4/AVE/FireMapper).

### Run
- You can run tests using `dotnet test`.
- You can run benchmark in [_FireMapperBench_ folder](FireMapperBench): `dotnet run` and then check the results in [_BenchmarkResults_](FireMapperBench/BenchmarkResults). These results provide the difference in performance between the two implementations (_FireDataMapper_ & _DynamicDataMapper_).

## Authors
- João Nunes ([joaonunatings](https://github.com/joaonunatings))
- Alexandre Silva ([Cors00](https://github.com/Cors00))
- Miguel Marques ([mjbmarques](https://github.com/mjbmarques))