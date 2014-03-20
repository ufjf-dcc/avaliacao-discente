package br.ufjf.avaliacao.persistent;

import java.util.List;

import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.Resposta;

public interface IRespostaDAO {

	public List<Resposta> respostasAvaliacao(Avaliacao a);

}
