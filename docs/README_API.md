# ⚽ Cartola FC API Wrapper — Python

Uma biblioteca/wrapper em **Python puro**, utilizando `requests`, para consumir a API pública do **Cartola FC**, sem dependências de pacotes legados ou descontinuados.

Com esta API, é possível extrair informações sobre:

* ⚽ Partidas e tabela de jogos
* 📊 Estatísticas de atletas
* 💰 Mercado e valorização
* ⏱️ Status do mercado
* 📈 Pontuações parciais
* 🏟️ Times e clubes
* 👤 Dados do time autenticado

---

## 🚀 Funcionalidades

### ⚽ Partidas e Tabela

* Programação dos jogos
* Data e horário das partidas
* Local/estádio
* Indicação se a partida é válida para a rodada
* Histórico de aproveitamento dos times
* Consulta de partidas por rodada

### 💰 Mercado e Valorização

Lista de atletas disponíveis no mercado contendo:

* Preço em cartoletas (`C$`)
* Variação de preço
* Média de pontos
* Quantidade de jogos
* Scouts acumulados
* Cálculo estimado do **mínimo necessário para valorizar**

### ⏱️ Status do Mercado

Consulta informações como:

* Rodada atual
* Status do mercado
* Contagem regressiva para fechamento
* Data e horário de fechamento
* Avisos oficiais da plataforma

### 📈 Pontuações Parciais

Acompanhamento das pontuações dos atletas durante os jogos da rodada.

### 🏟️ Times e Clubes

* Busca de times por nome ou slug
* Lista de clubes do Brasileirão
* Informações dos clubes
* Escalação de usuários autenticados

---

## 💻 Pré-requisitos

* **Python 3.7+**
* Biblioteca `requests`

### Instalação

```bash
pip install requests
```

---

## 📦 Estrutura do Projeto

```text
api/
├── cartoPy.py
│   └── Classe principal (Api), responsável pelas chamadas HTTP
│       e pelo tratamento dos dados.
│
└── api.py
    └── Script de exemplo para consumo dos métodos
        e exibição dos dados.
```

---

# ⚡ Como Usar

## 1. Inicialização Básica

Para chamadas que utilizam dados públicos, como partidas, mercado, estatísticas, parciais e busca de times:

```python
from cartoPy import Api

api = Api()
```

---

## 2. Inicialização com Autenticação

Para acessar dados privados, como o time do usuário logado e o histórico individual de atletas:

```python
from cartoPy import Api

api = Api(
    email="seu_email@globo.com",
    password="sua_senha"
)
```

> ⚠️ **Importante:** não coloque seu e-mail e senha diretamente no código se o projeto for publicado no GitHub. Prefira utilizar variáveis de ambiente.

---

# 🛠️ Exemplos Práticos

## 1. Verificar Status do Mercado

```python
from cartoPy import Api

api = Api()

status = api.status_e_contagem()

print(f"Rodada Atual: {status['rodada_atual']}")

print(
    f"Status do Mercado: "
    f"{'Aberto' if status['status_mercado'] == 1 else 'Fechado'}"
)

fechamento = status.get("fechamento")

if fechamento:
    print(
        f"Fechamento: "
        f"{fechamento['dia']}/"
        f"{fechamento['mes']}/"
        f"{fechamento['ano']} "
        f"às "
        f"{fechamento['hora']}:"
        f"{fechamento['minuto']}"
    )
```

---

## 2. Listar Jogos e Tabela da Rodada

```python
from cartoPy import Api

api = Api()

partidas = api.partidas_detalhadas()

for jogo in partidas:
    valida = "Sim" if jogo["valida"] else "Não"

    print(f"{jogo['mandante']} x {jogo['visitante']}")
    print(
        f"  Data/Hora: {jogo['partida_data']} "
        f"| Estádio: {jogo['local']}"
    )
    print(f"  Válido p/ Cartola: {valida}\n")
```

---

## 3. Estatísticas de Atletas e Mínimo para Valorizar

O exemplo abaixo filtra atletas com mais de 3 jogos e os ordena pelo menor valor estimado necessário para valorização.

```python
from cartoPy import Api

api = Api()

atletas = api.atletas_detalhados()

# Filtra atletas com mais de 3 jogos
atletas_filtrados = [
    atleta
    for atleta in atletas
    if atleta["jogos_num"] and atleta["jogos_num"] > 3
]

# Ordena pelo menor mínimo para valorizar
atletas_ordenados = sorted(
    atletas_filtrados,
    key=lambda x: x["minimo_para_valorizar"]
)

for atleta in atletas_ordenados[:5]:
    print(
        f"{atleta['apelido']} "
        f"({atleta['posicao']} - {atleta['clube']})"
    )

    print(
        f"  Preço: C$ {atleta['preco_num']:.2f} "
        f"| Média: {atleta['media_num']:.2f}"
    )

    print(
        f"  Mínimo p/ Valorizar: "
        f"{atleta['minimo_para_valorizar']:.2f} pts\n"
    )
```

---

## 4. Consultar Parciais Durante a Rodada

```python
from cartoPy import Api, MERCADO_FECHADO

api = Api()

if api.mercado().get("status_mercado") == MERCADO_FECHADO:

    parciais = api.parciais()

    for atleta_id, dados in parciais.get("atletas", {}).items():
        print(
            f"Atleta ID: {atleta_id} "
            f"| Pontuação: {dados.get('pontuacao')} pts"
        )

else:
    print(
        "As parciais só estão disponíveis "
        "enquanto os jogos estão em andamento!"
    )
```

---

# 📚 Referência dos Métodos

| Método                    | Autenticação | Descrição                                                                 |
| ------------------------- | :----------: | ------------------------------------------------------------------------- |
| `partidas()`              |       ❌      | Retorna os dados brutos de jogos e clubes da rodada atual.                |
| `partidas_detalhadas()`   |       ❌      | Retorna partidas formatadas com data, horário, estádio e aproveitamento.  |
| `partidas_rodada(rodada)` |       ❌      | Retorna as partidas de uma rodada específica.                             |
| `rodadas()`               |       ❌      | Lista todas as rodadas do campeonato.                                     |
| `mercado()`               |       ❌      | Retorna os dados brutos do status do mercado.                             |
| `status_e_contagem()`     |       ❌      | Retorna o status, data/hora de fechamento e avisos.                       |
| `atletas_mercado()`       |       ❌      | Retorna a lista bruta de atletas no mercado.                              |
| `atletas_detalhados()`    |       ❌      | Retorna atletas com preço, média, variação, mínimo p/ valorizar e scouts. |
| `parciais()`              |       ❌      | Retorna as pontuações parciais em tempo real.                             |
| `clubes()`                |       ❌      | Retorna o dicionário de todos os clubes do Brasileirão.                   |
| `busca_times(termo)`      |       ❌      | Pesquisa times pelo nome ou apelido.                                      |
| `time_por_slug(slug)`     |       ❌      | Retorna informações e atletas de um time pelo slug.                       |
| `meu_time()`              |       ✅      | Retorna a escalação atual e patrimônio do usuário logado.                 |
| `pontuacao_atleta(id)`    |       ✅      | Retorna o histórico detalhado de pontuações de um atleta rodada a rodada. |

---

# 📝 Observação sobre o Mínimo para Valorizar

A API oficial da Globo não popula a chave `minimo_para_valorizar` durante o mercado aberto.

Por isso, a classe `cartoPy.py` realiza automaticamente um cálculo estimado utilizando a fórmula padrão:

$$
\text{Mínimo para Valorizar}
\approx
\text{Preço do Atleta} \times 0.375
$$

> **Observação:** esse valor é uma **estimativa calculada pelo wrapper** e não um valor fornecido diretamente pela API.

---

# 🔐 Autenticação

Os métodos que exigem autenticação são:

```text
meu_time()
pontuacao_atleta(id)
```

Exemplo:

```python
from cartoPy import Api

api = Api(
    email="seu_email@globo.com",
    password="sua_senha"
)

meu_time = api.meu_time()

print(meu_time)
```

Para projetos publicados, recomenda-se utilizar variáveis de ambiente:

```env
CARTOLA_EMAIL=seu_email@globo.com
CARTOLA_PASSWORD=sua_senha
```

---

# 📌 Exemplo de Uso

Uma utilização simples da biblioteca pode seguir este fluxo:

```text
Inicializar API
      │
      ▼
Consultar status do mercado
      │
      ▼
Consultar rodada atual
      │
      ▼
Obter partidas
      │
      ▼
Obter atletas disponíveis
      │
      ▼
Analisar preço, média e valorização
      │
      ▼
Acompanhar parciais
```

---

# 🎯 Objetivo do Projeto

Este projeto foi desenvolvido com fins **acadêmicos e de aprendizado**, servindo como uma forma prática de estudar:

* 🐍 Python
* 🌐 Consumo de APIs REST
* 📡 Requisições HTTP
* 🔄 Tratamento de dados JSON
* 📊 Análise de estatísticas
* 🧩 Criação de wrappers
* 🔐 Autenticação em APIs
* 🏗️ Organização de código

---

# 📜 Licença

Este projeto é um **wrapper não oficial** da API pública do Cartola FC e foi desenvolvido para fins acadêmicos e de aprendizado.

**Cartola FC** é uma marca pertencente ao **Grupo Globo**.

Este projeto **não possui vínculo oficial com o Cartola FC ou com o Grupo Globo**.
