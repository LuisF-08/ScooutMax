import sys
import os
import pandas as pd

# 1. Adiciona a raiz do projeto ao path
RAIZ_PROJETO = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
if RAIZ_PROJETO not in sys.path:
    sys.path.append(RAIZ_PROJETO)

from api.cartoPy import Api

api = Api()

# 2. Coleta os dados dos atletas
atletas = api.atletas_detalhados()
df = pd.DataFrame(atletas)

# 3. Expande os scouts em colunas individuais
if 'scout' in df.columns:
    df_scouts = pd.json_normalize(df['scout']).fillna(0)
    df = pd.concat([df.drop(columns=['scout']), df_scouts], axis=1)

# 4. Garante que a pasta destino (data/silver) existe
PASTA_SILVER = os.path.join(RAIZ_PROJETO, 'data', 'silver')
os.makedirs(PASTA_SILVER, exist_ok=True)

# 5. Salva os arquivos no caminho absoluto correto
caminho_csv = os.path.join(PASTA_SILVER, 'atletas_cartola.csv')
caminho_json = os.path.join(PASTA_SILVER, 'atletas_cartola.json')

df.to_csv(caminho_csv, index=False, encoding='utf-8-sig')
df.to_json(caminho_json, orient='records', indent=4, force_ascii=False)

print(f"Sucesso! Arquivos salvos em:\n - {caminho_csv}\n - {caminho_json}")