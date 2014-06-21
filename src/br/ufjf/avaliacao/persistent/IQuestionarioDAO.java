package br.ufjf.avaliacao.persistent;

import java.util.List;

import br.ufjf.avaliacao.model.Curso;
import br.ufjf.avaliacao.model.Questionario;

public interface IQuestionarioDAO {
	public List<Questionario> retornaQuestinariosCurso(Curso curso);
}
