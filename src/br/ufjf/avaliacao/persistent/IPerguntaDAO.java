package br.ufjf.avaliacao.persistent;

import java.util.List;

import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.Questionario;

public interface IPerguntaDAO {

	public boolean existePergunta(Pergunta pergunta);

	public List<Pergunta> getPerguntasQuestionario(Questionario questionario);

}
