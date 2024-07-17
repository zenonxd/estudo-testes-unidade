# Estudo Testes de Unidade

![img.png](img.png)

Como vimos no estudo de introdução, testes de unidade seria para métodos ou classes, por exemplo.

Eles podem ser solitários e sociáveis.

- Solitários - É um teste que não interage com outras unidades. Uma classe calculadora,
por exemplo. O seu método de soma não depende de absolutamente nada. Quando falamos em depender,
estamos falando de banco de dados ou outros métodos externos.


- Sociável - É quando uma unidade conversa com outro método de código. Ou seja, quando há
alguma dependência no teste.
<hr>

## Criando API de Planetas

Aqui, criaremos um projeto spring padrão, e faremos a criação dos pacotes e classes. (Service,
Repositorie e Controller).

Padrão de sempre:

- Repositories: Guarda os métodos para serem criados. Dessa vez usaremos CrudRepository ao invés de JPA.
![img_3.png](img_3.png)
<hr>

- Service: Responsável pela criação, importa o repositorie.
![img_2.png](img_2.png)
<hr>

- Controller: Responsável pelo retorno HTTP, importa o service.
![img_1.png](img_1.png)

A partir do momento que rodarmos o código, as tables irão ser criadas dentro da Database starwars, veja:

![img_4.png](img_4.png)

Para realizar um post dentro do powershell: 
>curl.exe --% -ku user:passwd http://localhost:8080/planets  -H "Content-Type: application/json" -d "{\"name\":\"name\", \"climate\":\"climate\",\"terrain\":\"terrain\"}" -v
<hr>

## Cenários de Teste
Como saber que a nossa aplicação funciona? Bom, primeiro precisamos definir quais comportamentos ela terá
em determinadas situações.

Uma boa é a gente definir os possíveis cenários, veja no diagrama:

![Cenarios+de+Teste+-+Cadastro+de+Planeta.png](Cenarios+de+Teste+-+Cadastro+de+Planeta.png)
<hr>

## Cadastro de um usuário com sucesso

Primeiro. Vamos espelhar os nossos pacotes. Tudo que está dentro da API, vai para pasta de testes.

Como são testes unitários (de unidade), iremos considerar que tudo é uma classe (repository, service, controler...).

### Começaremos pela unidade que representa as regras de negocio, o **service**.

O nome da classe de teste será: ela + test ```public class PlanetServiceTest{}``` 

Sabemos que para realizar o teste, criaremos um metodo public void, veja:

1. Para nomear o método: operacao_estado_retornoEsperado
2. Importar o service e usar o @Autowired para ele ser instanciado pelo Spring.
3. Como a classe não tem um contexto spring, passamos @SpringBootTest. Ela irá montar o contexto 
da aplicação de teste e procurará por Beans que estão marcados no projeto principal e deixá-los disponíveis
para injeção.
Mas, como só queremos carregar uma unidade (service), nós podemos passar isso como parâmetro.

Dentro do método:

Uma estratégia válida para não ficar instanciando diversos planetas, é utilizar constrants.

Para isso, criaremos um pacote chamado: common. Nele, ficarão códigos comuns para teste.

Criaremos uma classe chamada PlanetConstrants:
```java
public class PlanetConstrants {
    public static final Planet PLANET = new Planet("name", "climate", "terrain");
}

```

Voltando para o método, importaremos de forma estática essa classe ```import static com.demo.swplanetapi.common.PlanetConstrants.PLANET;```

Criaremos uma variavel "sut" do tipo Planet, e instanciando-o. **SUT = system under test.**

E por fim, para testarmos e ver se o resultado é o que a gente espera, usamos o AssertJ.

Como é uma comparação de igualdade, importaremos o Apache na classe Planet para verificar a igualdade:
```java
    //clase Planet
    @Override
    public boolean equals(Object obj) {
        //primeiro parametro = objeto que estamos verificando se é igual.
        //segundo parametro = referenciando o proprio objeto (planet).
        return EqualsBuilder.reflectionEquals(obj, this);
    }
```

#### Código (ESSE CÓDIGO NO FIM DO ESTUDO MUDA POIS USAREMOS MOCKITO): 
```java
package com.demo.swplanetapi.domain;

import static com.demo.swplanetapi.common.PlanetConstrants.PLANET;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = PlanetService.class)
// passando o service.class para que o spring não instancie todos os Beans, somente esse.

public class PlanetServiceTest {
    private PlanetService planetService;

    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() {
       Planet sut = planetService.create(PLANET);

       //planeta criado pelo service é igual ao que criei agora?
       assertThat(sut).isEqualTo(PLANET);

       // como esse metódo ^ trabalha com igualdade, não esquecer de implementar
        // o equals do apache na classe planet.
    }
}
```

Ao testar, esse código dará um erro. Ele não consegue encontrar uma definição de bean do PlanetRepository.

Isso acontece porque o PlanetService depende de um Repository. Quando testamos e instanciamos o Service, 
ele também tenta achar o Repository. 

#### Bom, como testar uma unidade de forma isolada, que possui dependência? Como manter esse teste solitário?
Usaremos **dublês de teste.**
<hr>

## Dublês de Teste
São usados pelos testes solitários para simular o comportamento das duas dependências. Existem vários
tipos de dublês, veja:

1. Dummy (não é muito usado, só quando não queremos criar tudo na mesma hora)

![img_5.png](img_5.png)

Implementamos um DAO (objeto de acesso ao banco de dados) numa classe Dummy. Esse tipo exige a implementação
de alguns métodos, então colocamos qualquer coisinha. Nesse caso foi o lançamento de uma exception.
<hr>

2. Fake - Para banco de dados em memória. 

Implementação fake de um banco Oracle, por exemplo.

![img_6.png](img_6.png)

Nesse caso, usamos um Repository (ou um banco de dados) em lista (memória mesmo), para que seja possível
fazer o teste.
<hr>

3. Stub - Cenário mais Comum

A ideia dele é responder com definição pré estabelecida.

![img_7.png](img_7.png)

Nesse caso, temos uma lista. E quando o get é chamado pra obter o item da lista ele vai retornar
o mesmo valor.

Assim, é possível testar o service. Foi atribuito ao service a classe listSub (setup).

E depois checamos o estado.
<hr>

4. Spy - Um stub mais robusto

Se comporta igual ao stub (fazendo implementação fitícia), mas além de definir o que será retornado,
o Spy coleta informação de como esses objetos do método foram invocados.

![img_8.png](img_8.png)

Verifica ali, por exemplo, se o método "add" foi chamado e quantas vezes. E é possivel verificamos
o comportamento que ocorreu quando chamamos o alvo de teste.

O estado da lista muda!
<hr>

5. Mock - Geralmente o mais utilizado

Sua ideia é verificar o comportamento. Descreve a interação com a dependência, para ver se o fluxo
que a gente deseja foi invocado.

Então aqui a gente não verifica o estado final, e **sim o comportamento das chamadas durante o teste.**

![img_9.png](img_9.png)

Diferente do Spy, aqui o estado da lista não muda. Só verifica se o método da classe foi chamado,
seja com determinados parâmetros ou quantas vezes.
<hr>

A boa notícia, é que não precisamos implementar esses métodos na mão. Quando trabalhamos com Spring,
utilizamos frameworks para fazer tudo isso (criar os dubles + as técnicas). E para isso, usaremos o:
**Mockito!**

Uso do Mockito com dublês de teste:

![img_10.png](img_10.png)

Aqui testamos um serviço de carro, que retornará os detalhes de um carro de acordo com o nome passado.

O legal é, nesse código, o service de carro, depende de um repository e o Mockito consegue mocar esse
repository. E não precisa criar um Stub na mão, e sim usar o when.

![img_11.png](img_11.png)

Ou seja: quando uma operação for chamada (when). Quando isso acontecer, dará o retorno (thenReturn).

Assim que chamarmos o carService, ele usará o when e depois o assert irá verificar a condição.
<hr>

## Utilizando Mockito

Como nosso Sring não consegue localizar o Repository do nosso service (conforme visto acima), **utilizaremos o
Mockito para criar esse dublê de teste do planet repository.**

### Como mockar essa dependência?
Importamos o PlanetRepository e passamos a anotação.
```java
@MockBean
private PlanetRepository planetRepository;
```

Bom, ao rodarmos nosso código, ele não dará erro. Mas nosso SUT será null. Isso acontece, porque mesmo importando
o repository, ele continua sendo um Mock. Um Mock não é uma implementação real do componente, não possuem lógica.

Então precisamos definir espectativas claras do que vai ser chamado e retornado. Então aqui usaremos o dublê do tipo
**Stub**, veja:
```java
//antes do sut ser iniciado
when(planetRepository.save(PLANET)).thenReturn(PLANET);
```

Ou seja, quando o planetRepository.save for chamado exatamente com aquele planeta específico ele retornará o planeta.

Isso que fizemos segue até um princípio dos testes, chamado AAA.
```java
    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() {
        //AAA

        //ARRANGE - ARRUMA OS DADOS PRO TESTE
        when(planetRepository.save(PLANET)).thenReturn(PLANET);

        //ACT - FAZ A OPERAÇÃO DE FATO QUE QUEREMOS TESTAR
       Planet sut = planetService.create(PLANET);

       //planeta criado pelo service é igual ao que criei agora?

        //ASSERT - AFERE SE O SISTEMA SOB TESTE É O QUE ESPERAMOS.
       assertThat(sut).isEqualTo(PLANET);

       // como esse metódo ^ trabalha com igualdade, não esquecer de implementar
        // o equals do apache na classe planet.
    }
```

Pequena correção. Quando usamos a anotação do Springboot para iniciar o Service, acaba que ele cria muitos logs só
para injetar o service. Então a ideia é usarmos o Mockito, criando um teste de unidade puro! Veja como:
1. Tirar anotação @SpringBootTest e passar @ExtendWith(MockitoExtension.class);
2. Não injetaremos mais com @AutoWired nem @MockBean, porque não tem mais Spring. Substituiremos por:
   3. @InjectMocks no service. Essa anotação instancia o service (cria instancia real) e todas as dependências dele
   já são injetadas pelo Mock.
   No caso do service, ainda precisamos passar o @Mock (pois precisamos fazer o stub ainda).

Agora o teste será executado muito mais rápido que antes.
### CÓDIGO FINAL
```java
package com.demo.swplanetapi.domain;

import static com.demo.swplanetapi.common.PlanetConstrants.PLANET;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlanetServiceTest {
    @InjectMocks
    private PlanetService planetService;
    @Mock
    private PlanetRepository planetRepository;
    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() {
        //AAA
        //ARRANGE - ARRUMA OS DADOS PRO TESTE
        when(planetRepository.save(PLANET)).thenReturn(PLANET);

        //ACT - FAZ A OPERAÇÃO DE FATO QUE QUEREMOS TESTAR
       Planet sut = planetService.create(PLANET);

       //planeta criado pelo service é igual ao que criei agora?

        //ASSERT - AFERE SE O SISTEMA SOB TESTE É O QUE ESPERAMOS.
       assertThat(sut).isEqualTo(PLANET);

       // como esse metódo ^ trabalha com igualdade, não esquecer de implementar
        // o equals do apache na classe planet.
    }
}
```
<hr>

## Trabalhando com Cenários de Erro

Neste estudo específico, sabemos que teremos dois cenários possíveis para erro.

1. Quando não é informado os dados obrigatórios.
2. Quando tentamos criar um planeta que já existe.

Não seria muito bacana validar esses dados dentro do service com um monte de If. O ideal seria validar na camada de
Controladores, para quando receber o request ele já retornar o badrequest pro cliente. E por segurança, também colocar
essa validação no repository (a nivel de banco de dados).

Vamos validar o comportamento do repository (que o service chama), quando passamos dados não integros, veja:


**- Quando não é informado os dados obrigatórios.**
1. Criaremos um método createPlanet_WithInvalidData_ThrowsException;
2. Criaremos uma variável em PlanetConstraints chamada INVALID_PLANET e todas as suas variáveis serão vazias;
   3. Essa variável será importada de forma estática.
4. Quando criamos esse INVALID_PLANET, assim como na função lá de cima, ele vai depender do repository dentro do
service. E asssim como lá em cima, criaremos um stub, amarrando uma condição e uma resposta associada a ela e
retornaremos uma exception.
5. Ao invés de usarmos o AssertThat, usaremos assertThatThrownBy. Ele verifica se uma operação lançou uma exceção.
<hr>

**- Quando o planeta já existe.**

A ideia seria fazer igual falamos ali em cima. criar as limitações (constraints) no banco de dados, 
deixaremos ele criar a exceção (se um planeta já existir) e trataremos essa exceção no Controlador.

Mas esse teste que fizemos em cima, já atende essa condição de **planeta já existente**.
<hr>

## Exercício 1











## FIM
E aqui finalizemos os cenários de erro a nivel de serviço. Mas... não temos a garantia ainda de que o sistema está
tratando dados invalidos. De fato isso é verdade, a gente precisa testar as camadas que fazem essa validação,
Controller e Repositories.

 Mas essas camadas possuem integração (controller com web) e (repositorie com banco de dados). Por isso agora,
 utilizaremos [Testes de Integração]().