# 🚀 Visão Geral do Projeto

Aplicação web de **palpites e análises esportivas**. Além de funcionalidades tradicionais, a plataforma contará com um modelo de **Machine Learning (ML)** para identificar as melhores oportunidades de aposta (**Value Bets**) com base em:

* Histórico de partidas e estatísticas avançadas.
* Comparação e variação de **Odds**.
* Métricas de desempenho de equipes e atletas.

---

## 📡 Fontes de Dados e APIs

| Nome / Fonte            | Tipo                    | Foco Principal                                                        | Status de Avaliação | Link                                                     |
| ----------------------- | ----------------------- | --------------------------------------------------------------------- | ------------------- | -------------------------------------------------------- |
| **Football-Data.org**   | API (REST)              | Tabelas, jogos, resultados de ligas europeias e Brasileirão           | 🟡 Testando         | [Acessar site](https://www.football-data.org/)           |
| **API-Football**        | API (REST via RapidAPI) | Estatísticas em tempo real, escalações, cartões e odds                | 🟡 Testando         | [Acessar site](https://www.api-football.com/)            |
| **Cartola FC (Globo)**  | API (REST / No-Key)     | Scouts detalhados e status de jogadores do futebol brasileiro         | 🟢 Selecionado      | [Acessar endpoint](https://api.cartola.globo.com/clubes) |
| **Football-Data.co.uk** | CSV Histórico           | Dados históricos de ligas europeias e odds de casas de apostas        | 🟡 Testando         | [Acessar dados](https://football-data.co.uk/data.php)    |
| **Football CSV**        | Dataset Open Source     | Resultados e tabelas de jogos globais sem necessidade de autenticação | ⚪ Não iniciado      | [Acessar GitHub](https://footballcsv.github.io/)         |
| **StatsBomb Open Data** | Dataset / Biblioteca    | Eventos de partida, mapas de chute e métricas avançadas (xG)          | 🟢 Selecionado      | [Acessar GitHub](https://github.com/statsbomb/open-data) |

---

## 🐍 Snippets de Código para Coleta

### 1. StatsBomb (`statsbombpy`)

#### Instalação

```bash
pip install statsbombpy
```

#### Python

```python
from statsbombpy import sb

# Listar todas as competições e temporadas gratuitas disponíveis
competitions = sb.competitions()

print(competitions)
```

---

### 2. Open Football (`football.json`)

```python
import requests

# Consulta direta de partidas (ex: Premier League 2023/24) sem API Key
url = "https://raw.githubusercontent.com/openfootball/football.json/master/2023-24/en.1.json"

response = requests.get(url)
data = response.json()

print(data["name"])
# Saída: English Premier League 2023/24
```

---

## 🛠️ Bibliotecas Python Recomendadas

### `soccerdata`

Automatiza a raspagem e o carregamento de dados de grandes portais analíticos, como **FBref, Understat e ClubElo**, diretamente para DataFrames do Pandas.

#### Instalação

```bash
pip install soccerdata
```

---

### `mplsoccer`

Biblioteca focada em **visualização de dados esportivos**. Permite gerar:

* Mapas de calor.
* Redes de passes.
* Mapas de finalizações.
* Gráficos táticos.

#### Instalação

```bash
pip install mplsoccer
```

---

## 📋 Guia Rápido de Decisão

| Necessidade                                            | Fonte Recomendada                     |
| ------------------------------------------------------ | ------------------------------------- |
| **Dados em Tempo Real & Live Scores**                  | `Football-Data.org` ou `API-Football` |
| **Treinamento de Modelos de ML / Backtesting de Odds** | `Football-Data.co.uk` ou `StatsBomb`  |
| **Projetos Sem Cadastro / No-Key**                     | `openfootball` ou `Cartola FC API`    |
