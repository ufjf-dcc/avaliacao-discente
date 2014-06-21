package br.ufjf.avaliacao.persistent;

import java.util.List;

import org.hibernate.HibernateException;

import br.ufjf.avaliacao.model.Turma;
import br.ufjf.avaliacao.model.Usuario;

public interface IUsuarioDAO {
	public Usuario retornaUsuarioEmail(String email) throws HibernateException,
			Exception;

	public Usuario retornaUsuario(String email, String senha);

	public Usuario retornaUsuario(String nome);

	public List<Usuario> retornaProfessores();

	public List<Usuario> getTodosUsuarios();

	public List<Usuario> retornaProfessoresTurma(Turma turma);

	public List<Usuario> retornaAlunosTurma(Turma turma);

	public Usuario retornaCoordAvaliado(Usuario usuario);

}
