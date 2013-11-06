package br.ufjf.avaliacao.persistent;

import java.util.List;

import br.ufjf.avaliacao.model.Disciplina;
import br.ufjf.avaliacao.model.Turma;

public interface ITurmaDAO {

	public Turma retornaTurma(String letraTurma, String semestre,
			Disciplina disciplina);

	public List<Turma> getTodasTurmas();

}
