# Aspeto importantes de desenho e implementação:

### Server.cs
- Devido à limitação da utilização do Enumerável `_status`, uma forma assíncrona que implementámos para lidar com o `Join()` foi a criação de `RequestWithoutCancel` que como implementa TaskCompletionToken basta ser resolvido e o servidor tem percepção de que pode assíncronamente, sem espera, de finalmente ter parado.

- Usufruímos da utilização de exclusão mútua com `_lock` para prevenir concorrência entre as várias threads que eventualmente pudessem estar a aceder às variaves globais distribuídas pelos métodos publicos do servidor. E também para não haverem '*races*' entre modificações de variáveis que deveriam de ser feitas numa sequência especifica sem interrupções ou leituras que levassem a uma conclusão errada numa thread diferente em um método diferente.

- Para manipulação do número máximo de clientes foi utilizado um `SemaphoreSlim` que da forma como está a ser utilizado está sempre com um lugar reservado, tendo o número de clientes usados sempre como +1 do que os que foram aceites; E de forma assíncrona impedindo que mais clientes se conectem com o servidor quando o número máximo de clientes for atingido.

- Foi implementado um cancelation token `_ct` que é usado para o cancelamento de `WaitAsync()` do `SemaphoreSlim` e de "todos os clientes". Gerando um resultado de quando ativado, os clientes são todos terminados bem como o último pedido de espera ao semáforo. Esse cancelation token é ativado em `Stop()`.

### AsyncTaskSynchronizer

- Foi criado o sincronizador assíncrono com o intuito de controlar os clientes no sentido de os ter todos na mesma coleção e saber quando cada um deles já acabou a sua execução.
### ConnectedClient.cs
- Para cancelamento de um cliente foi introduzido um parâmetro que representa um cancelation token e no construtor do cliente é aplicado `_ct.Register()` com o respetivo callback que envia uma mensagem que solicita o fecho do cliente.
- O `Join()` passou a ser assíncrono e espera pela task `_mainTask`que representa o loop principal `MainLoop()`, e quando o mesmo acaba então significa que o cliente já fechou.
- Foi criada uma message queue assíncrona `AsyncQueue.cs` cujo objetivo é substituir a coleção não assíncrona anterior que recebe as mensagens todas e vai buscá-las quando necessário.
- para controlar a corrida entre "sair de um room" e "receber uma mensagem" foi adicionada uma variável global `_prevousRoom` cujo valor é alterado sempre que recebido algo pela stream. Sendo esse valor sempre comparado com o `_currentRoom` antes de enviar mensagens para o utilizador.
- No fecho de um cliente é possível uma Task estar ocupada com a espera de algum valor vindo da stream a partir de `ReadLineAsync()`. Mas como não é possível cancelar esse processo de forma normal, fecha-se a stream, provocando o lançamento de excepção que posteriormente é apanhada.