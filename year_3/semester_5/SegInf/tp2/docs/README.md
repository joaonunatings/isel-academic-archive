# Segurança Informática - Segundo Trabalho

## 1.
### (a) 
No sub-protocolo _record protocol_ é possível garantir a autenticidade das mensagens a partir do algoritmo MAC. Isto é, a mensagem da aplicação é fragmentada e é adicionada uma _tag_ MAC que deve ser verificada na receção.

### (b)
#### i.
Existem vários fatores que indicam que a mensagem sofreu adulteração ou inserção de mais mensagems por um atacante. Em qualquer situação em que isto acontence, o protocolo termina a sessão com um erro a partir dos _error alerts_. Na adulteração de mensagens o _handshake_ pode falhar tanto na autenticidade como na decifra da mensagem. E na inserção de mensagens pode falhar uma vez que o _handshake_ é definido por um número de pedidos _roundtrip_ entre o cliente e servidor (clientHello, serverHello, etc). No fim (Finished message) é feito um sumário do _handshake_ de forma a verificar a troca de chave e autenticação, esta mensagem também verifica se existiu alguma alteração às mensagens.

#### ii.
Na situação em que é necessário verificar a autenticidade do cliente, o servidor envia um _Certificate Request_ após o seu _Server Hello_. O cliente que recebe este pedido deve enviar o seu certificado ou um aviso indicando que não existe certificado para completar este pedido.

### (c)
_Perfect Forward Security_ implica que, se a chaves privadas forem comprometidas, não se consegue decifrar nenhuma data de sessões anteriores ou futuras. No entanto, na situação em que se usa a chave pública do servidor para cifrar a _pre-master key_ do cliente, esta propriedade não se verifica dado que basta o atacante obter a chave privada do servidor usada para decifrar a _pre-master key_.

## 2.
Apesar de se ter obtido os _hashes_ com os respetivos _salts_, o ataque à interface de autenticação não é facilitado uma vez que este tipo de ataque usa um dicionário de _passwords_ e testa diretamente com o mecanismo de autenticação que por sua vez, nesta aplicação _web_, tem uma proteção contra este tipo de ataque (número de tentativas é limitado).
No entanto, um ataque com pré-computação possibilita a obtenção da password dos utilizadores usando a seguinte estratégia: 
1. Gerar um dicionário de _hashes_ previamente calculados.
2. Encontrar o _hash_ correspondente ao _hash_ da password (sem o respetivo _salt_).
3. A partir da função geradora de _hash_ decifrar para _plaintext_ a password do utilizador.
Este ataque não utiliza nenhuma tentativa da interface de autenticação sendo que este _leak_ causado pela aplicação _Web_ facilitou o ataque de dicionário com pré-computação.

## 3.
Para cada pedido que o _browser_ faz ao servidor é necessário enviar o _cookie_ da sessão autenticada. Para isto, é necessário que o servidor crie um _cookie_ e envie para o _browser_ a partir do _header_ _Set-Cookie_. A partir daí, para o cliente se manter autenticado, deve enviar em todos os seus pedidos subsequentes o _cookie_ recebido a partir do _header_ _Cookie_. Normalmente a geração deste tipo de _cookie_ é realizado quando um cliente quer iniciar sessão a partir das suas credenciais.

## 4. 

### (a)
O _access_token_ tem como objetivo ser usado no acesso a recursos protegidos da aplicação/API destino.
O _id_token_ serve para verificar a autenticidade e obter informação do utilizador.

### (b)
Em qualquer situação em que existe um _redirect_ (302) de modo a obter a informação necessária. 
Por exemplo, na autorização do cliente é necessário obter um _code_. Neste caso, a resposta do servidor é a partir de um _callback URI_ que contém o _code_ (podendo conter mais informações).

### (c)
Para comunicação com o servidor de autorização, o cliente necessita de um _client_id_ e um _client_secret_. No entanto, o dono de recursos necessita de um _access_token_ logo não tem qualquer controlo sobre o _client_id_ ou o _client_secret_.

### (d)
O atacante a partir de um _reflected_ ou _stored_ XSRF (_Cross-site Request Forgery_) na aplicação cliente, consegue executar ações usando a autenticação do utilizador obtidas a partir do OAuth2.

## 5.

### (a)
A família de modelos RBAC contribui para este princípio no sentido em que as permissões dadas a cada entidade/utilizador são as mínimas necessárias para poderem realizar cada operação. Neste caso, cada utilizador tem uma função sendo que essa função limita as permissões do mesmo. O modelo também dita que cada função deve ser autorizada, isto limita certos utilizadores de receberem certas funções. Por fim, cada função tem um conjunto de permissões.

### (b)
Nesta situação, _u2_ pertence a _r2_. A _role_ _r2_ tem as permissões de _r0_ e _r1_ (de acordo com a _Role Hierarchy_), sendo estas: _pa_ (de _r0_) e _pb_ (de _r1_).
O recurso R necessita de _pc_ e _pb_ logo, o utilizador _u2_ não pode aceder a este recurso.

## 6.

### Converter os certificados e _keystores_ em ficheiros com extensão PEM

- Para converter os certificados X509 (_.cer_) foi usado o seguinte comando:

`openssl x509 -inform der -in certificate.cer -out certificate.pem`

- Para converter os _keystores_ em formato PKCS#12 foi usado o seguinte comando:

`openssl pkcs12 -in keystore.pfx -out keystore.pem -nodes`

No fim, é necessário importar os certificados _CA1-int_ e _CA1_ de modo a validar o certificado cliente.

### Ficheiro _hosts_
Foi necessário editor o ficheiro _hosts_ e adicionar o seguinte _mapping_: 
`127.0.1.1 www.secure-server.edu`

### Adicionar _root certificate_ ao servidor
Na opção _ca_ é necessário adicionar o caminho do certificado raíz, neste caso, sendo _CA1_.

### Adicionar certificados ao _browser_
Para o _browser_ verificar a autenticidade do servidor é necessário adicionar os certificados CA ao _browser_ que foi utilizado para testar, neste caso, sendo _CA1-int_ e _CA1_.

### Executar o servidor
Por fim, executa-se o servidor usando:
- Sem autenticação do cliente:

  `node server-no-client-auth.js`
- Com autenticação do cliente:

  `node server-client-auth.js`

### Testar servidor

- Sem autenticação do cliente é necessário verificar a autenticidade do servidor, caso contrário aparece o seguinte erro:

`SEC_ERROR_UNKNOWN_ISSUER`

- Com autenticação do cliente é necessário a mesma verificação anterior e um certificado do cliente, caso contrário, na ausência de certificado aparece o seguinte erro:

`SSL_ERROR_RX_CERTIFICATE_REQUIRED_ALERT`

## 8.
Correr servidor: Na pasta raíz, executar `npm start`

Correr _front-end_: Na pasta _client_, executar `npm start`

**Estrutura:**

![Application structure](https://github.com/isel-deetc-computersecurity/seginf-v2122-trabs-grupo9/blob/main/tp2/docs/ex8diagram.png)
