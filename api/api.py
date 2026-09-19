from cartoPy import Api

api = Api()

# --- 1. STATUS E CONTAGEM REGRESSIVA DO MERCADO ---
print("=================== 1. STATUS DO MERCADO ===================")
status_info = api.status_e_contagem()

print(f"Status do Mercado: {status_info['status_mercado']} (1=Aberto, 2=Fechado)")
print(f"Rodada Atual: {status_info['rodada_atual']}")

fechamento = status_info.get('fechamento')
if fechamento:
    print(f"Fechamento do Mercado: {fechamento['dia']}/{fechamento['mes']}/{fechamento['ano']} às {fechamento['hora']}:{fechamento['minuto']}")

print(f"Aviso Oficial: {status_info.get('aviso') or 'Nenhum aviso no momento'}\n")


# --- 2. INFORMAÇÕES DETALHADAS DAS PARTIDAS ---
print("=================== 2. JOGOS DA RODADA ===================")
partidas = api.partidas_detalhadas()

for jogo in partidas[0:]:  # Mostra todas as partidas
    valida = "Sim" if jogo['valida'] else "Não"
    print(f"{jogo['mandante']} x {jogo['visitante']}")
    print(f"  Data/Hora: {jogo['partida_data']} | Estádio: {jogo['local']}")
    print(f"  Jogo Válido p/ Cartola: {valida}")
    print(f"  Aproveitamento Mandante: {jogo['aproveitamento_mandante']}")
    print(f"  Aproveitamento Visitante: {jogo['aproveitamento_visitante']}\n")


# --- 3. ESTATÍSTICAS DOS ATLETAS NO MERCADO ---
print("=================== 3. ESTATÍSTICAS DOS ATLETAS ===================")
atletas = api.atletas_detalhados()

# Filtra atletas com mais de 3 jogos ordenados pelo menor valor mínimo para valorizar
atletas_filtrados = [a for a in atletas if a['jogos_num'] and a['jogos_num'] > 3]
atletas_ordenados = sorted(atletas_filtrados, key=lambda x: x['minimo_para_valorizar'] or 99)

for atleta in atletas_ordenados[:5]:  # Mostra 3 destaques de valorização
    min_val = atleta['minimo_para_valorizar'] or 0.0
    print(f"Atleta: {atleta['apelido']} ({atleta['posicao']} - {atleta['clube']})")
    print(f"  Preço: C$ {atleta['preco_num']:.2f} | Variação: {atleta['variacao_num']:.2f}")
    print(f"  Média: {atleta['media_num']:.2f} pts em {atleta['jogos_num']} jogos")
    print(f"{atleta['apelido']} (C$ {atleta['preco_num']:.2f}) -> Precisa de aproximadamente: {min_val:.2f} pts")
    print(f"  Scouts: {atleta['scout']}\n")