# README (Cartola FC 2018 - 2026)

> **Como o documento foi feito:** as explicações dos códigos (status, posição, scouts) seguem o padrão do Cartola FC e foram **conferidas contra os dados do próprio arquivo** (por exemplo, os pesos dos scouts reproduzem exatamente a coluna de pontos). Onde há inferência, isso está indicado.
<br>
> **Adendo:** Em 2021 não foi capturado como csv e sim com json e está  de uns Mercado_X.txt , sendo "X" repectivamente a cada rodada.
---

## 1. Identificação do atleta

| Coluna | O que significa |
|---|---|
| `atletas.atleta_id` | ID único do atleta. **É a melhor chave primária** (não se repete em nenhuma das 691 linhas). |
| `atletas.nome` | Nome completo. |
| `atletas.apelido` | Nome pelo qual o atleta é conhecido (ex.: "Fábio"). Pode se repetir (643 valores únicos para 691 linhas). |
| `atletas.apelido_abreviado` | Versão curta do apelido (ex.: "D. Júnior"). |
| `atletas.slug` | Apelido em formato de URL (minúsculo, sem acento, com hífens). Também pode se repetir. |
| `atletas.foto` | Link da imagem. Neste arquivo todos os links são **silhuetas por clube** e o final `FORMATO.png` é um espaço reservado onde normalmente entra o tamanho da imagem. Não é foto individual. |

## 2. Clube, posição e rodada

| Coluna | O que significa |
|---|---|
| `atletas.clube_id` | ID numérico do clube (tabela abaixo). |
| `atletas.clube.id.full.name` | Sigla do clube (apesar do nome da coluna, **não é o nome completo**, é a sigla de 3 letras). |
| `atletas.posicao_id` | Posição do atleta (tabela abaixo). |
| `atletas.rodada_id` | Rodada a que os dados se referem. Aqui é sempre `1`. |

### Posições (`atletas.posicao_id`)

| ID | Posição(Mudou em 2023 para IDs) | Linhas |
|---|---|---|
| 1 | Goleiro(gol) | 66 |
| 2 | Lateral(lat) | 105 |
| 3 | Zagueiro(zag) | 113 |
| 4 | Meia(mei) | 217 |
| 5 | Atacante(ata) | 170 |
| 6 | Técnico(tec) | 20 |

> **OBS:** O cartola FC mudou a forma como coloca **atletas.status_id, atletas.posicao_id, atletas.clube.id.full.name[ de 2014 ate 2024]** (por exemplo, os pesos dos scouts reproduzem exatamente a coluna de pontos). Onde há inferência, isso está indicado.


### Clubes (`atletas.clube_id` → sigla)

| ID | Sigla(possuia  o nome completo nos de 2024 para trás) | 
|---|---|
| 262 | FLA | 
| 263 | BOT | 
| 264 | COR | 
| 265 | BAH | 
| 266 | FLU | 
| 267 | VAS | 
| 275 | PAL | 
| 276 | SAO | 
| 277 | SAN | 
| 280 | RBB | 
| 282 | CAM |
| 283 | CRU |
| 284 | GRE |
| 285 | INT |
| 287 | VIT |
| 293 | CAP |
| 294 | CFC |
| 315 | CHA |
| 364 | REM |
| 2305 | MIR |

> As siglas vêm do próprio arquivo. Os IDs são os usados pela API do Cartola; a correspondência sigla ↔ ID foi tirada das linhas do CSV, não de uma lista externa.

## 3. Status (`atletas.status_id`)

Indica a **condição do atleta para escalação**.

| ID | Status(Presente com string nos antigos csv) | Significado | Linhas |
|---|---|---|---|
| 2 | **Dúvida** | Atleta com dúvida física/médica; pode ou não jogar. | 31 |
| 3 | **Suspenso** | Cumpre suspensão e não pode atuar. | 3 |
| 5 | **Contundido** | Lesionado, fora dos gramados. | 33 |
| 6 | **Nulo** | Sem status definido (não foi indicado como provável nem tem restrição registrada). | 423 |
| 7 | **Provável** | Previsto para jogar. | 201 |

Não aparece nenhum atleta com `status_id = 4` neste arquivo.

**Observação importante (inferência):** o status parece refletir o estado **depois** da rodada, ou seja, a situação para a rodada seguinte. Evidências:

- Os 3 **suspensos** (Josué, Jorginho e Michel Araújo) **jogaram** na Rodada 1 e todos têm `CV = 1` (cartão vermelho), ou seja, estão suspensos pela expulsão.
- Quase todos os **contundidos** (94%) não entraram em campo.
- Os **prováveis** quase sempre jogaram (96%).

Por isso, não use `status_id` como "estava disponível antes do jogo" sem cuidado.

## 4. Participação e pontuação

| Coluna | O que significa |
|---|---|
| `atletas.entrou_em_campo` | `True` se o atleta atuou na rodada, `False` se não. (335 jogaram, 356 não.) |
| `atletas.jogos_num` | Número de jogos disputados. Na Rodada 1 é `1` ou `0` e **coincide com `entrou_em_campo`**. |
| `atletas.pontos_num` | **Pontuação do atleta na rodada.** Quem não entrou em campo tem sempre `0`. |
| `atletas.media_num` | Média de pontos na temporada. Na Rodada 1 é **idêntica** a `pontos_num` (só há um jogo). |

## 5. Preço

| Coluna | O que significa |
|---|---|
| `atletas.preco_num` | Preço do atleta em **cartoletas** (C$). Varia de 0,71 a 20,09. |
| `atletas.variacao_num` | Quanto o preço mudou após a rodada (positivo = valorizou, negativo = desvalorizou). |

Verificação: `preco_num − variacao_num` dá valores inteiros entre 1 e 23, o que indica que **`preco_num` é o preço já atualizado após a rodada** e que o preço inicial era `preco_num − variacao_num`. (Ex.: Fábio, goleiro do FLU: preço 10,55, variação −1,45, preço inicial 12,00.)

## 6. Scouts (ações do jogo)

São **contagens** de ações. Células vazias (`NaN`) significam que a ação **não ocorreu** (equivale a zero). A pontuação de cada scout abaixo foi **confirmada por regressão** nos dados: ela reproduz `pontos_num` sem nenhum erro para jogadores de linha e goleiros.

### Scouts positivos

| Coluna | Significado | Pontos |
|---|---|---|
| `G` | Gol | **+8,0** |
| `A` | Assistência | **+5,0** |
| `FT` | Finalização na trave | **+3,0** |
| `FD` | Finalização defendida (chute no gol que o goleiro defendeu) | **+1,2** |
| `FF` | Finalização para fora | **+0,8** |
| `FS` | Falta sofrida | **+0,5** |
| `DS` | Desarme | **+1,5** |
| `PS` | Pênalti sofrido | **+1,0** |
| `SG` | Jogo sem sofrer gol (só faz sentido para goleiro, lateral e zagueiro) | **+5,0** |
| `DE` | Defesa (só goleiros) | **+1,3** |

### Scouts negativos

| Coluna | Significado | Pontos |
|---|---|---|
| `GC` | Gol contra | **−3,0** |
| `CV` | Cartão vermelho | **−3,0** |
| `CA` | Cartão amarelo | **−1,0** |
| `GS` | Gol sofrido (só goleiros) | **−1,0** |
| `PC` | Pênalti cometido | **−1,0** |
| `FC` | Falta cometida | **−0,3** |
| `I` | Impedimento | **−0,1** |

### Scout exclusivo de técnico

| Coluna | Significado |
|---|---|
| `V` | **Vitória** da equipe do técnico. Aparece em 9 técnicos. |

### Pontos que merecem atenção

- A pontuação dos **técnicos** (`posicao_id = 6`) **não** é a soma simples dos scouts. Ex.: Tite tem 1,37 pontos sem nenhum scout preenchido. Ela é calculada de outra forma (desempenho da equipe), então o peso de `V` não é fixo e a conta acima não vale para eles.
- `DE` e `GS` só aparecem para **goleiros**. `SG` só aparece para **goleiros, laterais e zagueiros**.
- Os pesos acima foram **estimados e validados nos dados desta rodada**. Como as regras do Cartola podem mudar de uma temporada para outra, vale conferir na regra oficial de 2026 caso use isso em algo importante.

---

## 7. Exemplo de leitura de uma linha

```
Fábio | FLU | Goleiro | status 7 (Provável) | entrou_em_campo True
pontos_num 2.1 | DE 2 | FS 1 | GS 1
```

Cálculo: 2 defesas × 1,3 = 2,6 + 1 falta sofrida × 0,5 = 0,5 + 1 gol sofrido × (−1,0) = **2,1 pontos**. ✔

## 8. Dicas para análise

- Use `atletas.atleta_id` como chave, não o apelido.
- Para análises de desempenho, filtre `atletas.entrou_em_campo == True` (senão 356 linhas com 0 ponto distorcem as médias).
- Trate `NaN` nos scouts como `0` (`df[scouts].fillna(0)`).
- Separe **técnicos** (`posicao_id == 6`) dos demais, pois a pontuação deles segue outra lógica.