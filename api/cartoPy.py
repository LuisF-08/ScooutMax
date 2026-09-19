# docs do cartolaPy -> https://pythonhosted.org/Python-CartolaFC/api.html#

import logging
import requests

logging.basicConfig(format='%(asctime)s - %(name)s - %(levelname)s - %(message)s', level=logging.INFO)

MERCADO_ABERTO = 1
MERCADO_FECHADO = 2


class Api(object):
    """API em Python pura para o Cartola FC, baseada na api do cartola FC feita em python"""

    def __init__(self, email=None, password=None, json=True):
        self._api_url = 'https://api.cartolafc.globo.com'
        self._auth_url = 'https://login.globo.com/api/authentication'
        self._email = email
        self._password = password
        self._glb_id = None

        if bool(email) != bool(password):
            raise Exception('E-mail ou senha ausente')
        elif all((email, password)):
            self.set_credentials(email, password)

    def set_credentials(self, email, password):
        self._email = email
        self._password = password
        response = requests.post(
            self._auth_url,
            json={'payload': {'email': self._email, 'password': self._password, 'serviceId': 4728}}
        )
        if response.status_code == 200:
            self._glb_id = response.json().get('glbId')
        else:
            raise Exception('Falha na autenticação na Globo.')

    def _get(self, endpoint, params=None, requires_auth=False):
        url = f"{self._api_url}{endpoint}"
        headers = {}
        if requires_auth:
            if not self._glb_id:
                raise Exception('Esta operação requer autenticação (email e senha).')
            headers['X-GLB-Token'] = self._glb_id

        response = requests.get(url, params=params, headers=headers)
        if response.status_code == 200:
            return response.json()
        raise Exception(f"Erro na requisição ({response.status_code}): {response.text}")

    # ==================== 1. PARTIDAS E TABELA ====================
    def partidas(self):
        """Retorna os dados brutos de jogos e clubes da rodada atual."""
        return self._get('/partidas')

    def partidas_detalhadas(self):
        """Retorna os jogos formatados com data, hora, local, status e aproveitamento."""
        dados = self.partidas()
        clubes = dados.get('clubes', {})
        partidas = dados.get('partidas', [])
        
        resultado = []
        for jogo in partidas:
            mandante_id = str(jogo.get('clube_casa_id'))
            visitante_id = str(jogo.get('clube_visitante_id'))

            resultado.append({
                'partida_id': jogo.get('partida_id'),
                'mandante': clubes.get(mandante_id, {}).get('nome', 'N/A'),
                'visitante': clubes.get(visitante_id, {}).get('nome', 'N/A'),
                'placar_mandante': jogo.get('placar_oficial_mandante'),
                'placar_visitante': jogo.get('placar_oficial_visitante'),
                'partida_data': jogo.get('partida_data'),
                'local': jogo.get('local'),
                'valida': jogo.get('valida'),
                'aproveitamento_mandante': jogo.get('aproveitamento_mandante', []),
                'aproveitamento_visitante': jogo.get('aproveitamento_visitante', [])
            })
        return resultado

    def partidas_rodada(self, rodada):
        """Retorna os jogos de uma rodada específica."""
        return self._get(f'/partidas/{rodada}')

    def rodadas(self):
        """Retorna a lista de todas as rodadas do campeonato."""
        return self._get('/rodadas')

    # ==================== 2. MERCADO E ESTATÍSTICAS ====================
    def mercado(self):
        """Retorna o status bruto do mercado."""
        return self._get('/mercado/status')

    def status_e_contagem(self):
        """Retorna o status, data/hora do fechamento e avisos do mercado."""
        dados = self.mercado()
        return {
            'status_mercado': dados.get('status_mercado'),
            'rodada_atual': dados.get('rodada_atual'),
            'fechamento': dados.get('fechamento'),
            'limite_compra_fechamento': dados.get('limite_compra_fechamento'),
            'aviso': dados.get('aviso'),
            'aviso_formatado': dados.get('aviso_formatado')
        }

    def atletas_mercado(self):
        """Retorna a lista bruta de atletas no mercado."""
        return self._get('/atletas/mercado')

    def atletas_detalhados(self):
        """Retorna atletas com preço, variação, média, mínimo para valorizar e scouts."""
        dados = self.atletas_mercado()
        atletas = dados.get('atletas', [])
        posicoes = dados.get('posicoes', {})
        clubes = dados.get('clubes', {})

        resultado = []
        for atleta in atletas:
            clube_id = str(atleta.get('clube_id'))
            posicao_id = str(atleta.get('posicao_id'))
            
            preco = atleta.get('preco_num') or 0.0
            min_val_api = atleta.get('minimo_para_valorizar')

            # Se a API retornar None, calcula a estimativa real (Preço * 0.375)
            if min_val_api is None:
                min_val_calculado = round(preco * 0.375, 2)
            else:
                min_val_calculado = min_val_api

            resultado.append({
                'atleta_id': atleta.get('atleta_id'),
                'apelido': atleta.get('apelido'),
                'clube': clubes.get(clube_id, {}).get('nome', 'N/A'),
                'posicao': posicoes.get(posicao_id, {}).get('nome', 'N/A'),
                'preco_num': preco,
                'variacao_num': atleta.get('variacao_num'),
                'media_num': atleta.get('media_num'),
                'jogos_num': atleta.get('jogos_num'),
                'minimo_para_valorizar': min_val_calculado,
                'scout': atleta.get('scout', {})
            })
        return resultado

    def parciais(self):
        """Retorna as pontuações parciais dos atletas na rodada em andamento."""
        status = self.mercado()
        if status.get('status_mercado') == MERCADO_FECHADO:
            return self._get('/atletas/pontuados')
        raise Exception('As pontuações parciais só ficam disponíveis com o mercado fechado.')

    # ==================== 3. TIMES E CLUBES ====================
    def clubes(self):
        """Retorna os clubes do Brasileirão."""
        return self._get('/clubes')

    def busca_times(self, termo):
        """Busca times pelo nome ou termo no Cartola."""
        return self._get('/times', params={'q': termo})

    def times(self, termo):
        """Atalho para busca_times."""
        return self.busca_times(termo)

    def time_por_slug(self, slug):
        """Retorna as informações e escalação de um time pelo slug."""
        return self._get(f'/time/slug/{slug}')

    def meu_time(self):
        """Retorna o time e escalação do usuário logado (requer autenticação)."""
        return self._get('/auth/time', requires_auth=True)

    def pontuacao_atleta(self, atleta_id):
        """Retorna o histórico de pontuações de um atleta específico (requer autenticação)."""
        return self._get(f'/auth/mercado/atleta/{atleta_id}/pontuacao', requires_auth=True)