package br.ufjf.avaliacao.persistent;

import java.util.List;

import br.ufjf.avaliacao.model.Disciplina;

public interface IDisciplinaDAO {

	public Disciplina retornaDisciplinaNome(String nomeDisciplina);

	public Disciplina retornaDisciplinaCod(String codDisciplina);

	public List<Disciplina> getTodasDisciplinas();
	

}
