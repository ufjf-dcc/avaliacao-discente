package br.ufjf.avaliacao.persistent;

import java.util.List;

import br.ufjf.avaliacao.model.Disciplina;
import br.ufjf.avaliacao.model.Turma;
import br.ufjf.avaliacao.model.Usuario;

public interface ITurmaDAO {

	public Turma retornaTurma(String letraTurma, String semestre,
			Disciplina disciplina);

	public List<Turma> getTodasTurmas();

	public List<Turma> getTurmasUsuario(Usuario usuario);

	public List<String> getAllSemestres();

	public List<Turma> getAllTurmas(String semestre);

}
