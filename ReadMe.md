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

- Repositories: Guarda os métodos para serem criados. Dessa vez usams CrudRepository ao invés de JPA.
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

Como é uma comperação de igualdade, importaremos o Apache na classe Planet para verificar a igualdade:
```java
    //clase Planet
    @Override
    public boolean equals(Object obj) {
        //primeiro parametro = objeto que estamos verificando se é igual.
        //segundo parametro = referenciando o proprio objeto (planet).
        return EqualsBuilder.reflectionEquals(obj, this);
    }
```

#### Código: 
```java
package com.demo.swplanetapi.domain;

import static com.demo.swplanetapi.common.PlanetConstrants.PLANET;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = PlanetService.class)
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

Bom, como testar uma unidade de forma isolada, que possui dependência? Como manter esse teste solitário?
Usaremos **dublês de teste.**
<hr>

## Dublês de Teste
São usados pelos testes solitários para simular o comportamento das duas dependências. Existem vários
tipos de dubês, veja:

1. Dummy (não é muito usado, só quando não queremos criar tudo na mesma hora)

![img_5.png](img_5.png)

Implementos um DAO (objeto de acesso ao banco de dados) numa classe Dummy. Esse tipo exige a implementação
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

O legal é, nesse código o service de carro, depende de um repository e o Mockito consegue mocar esse
repository. E não precisa criar um Stub na mão, e sim usar o when.

![img_11.png](img_11.png)

Ou seja: quando uma operação for chamada (when). Quando isso acontecer, dará o retorno (thenReturn).

Assim que chamarmos o carService, ele usará o when e depois o assert irá verificar a condição.