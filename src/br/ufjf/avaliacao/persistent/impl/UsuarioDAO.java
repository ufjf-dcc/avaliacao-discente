package br.ufjf.avaliacao.persistent.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.Curso;
import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.PrazoQuestionario;
import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.model.Turma;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.GenericoDAO;
import br.ufjf.avaliacao.persistent.IUsuarioDAO;

public class UsuarioDAO extends GenericoDAO implements IUsuarioDAO {

	@Override
	public Usuario retornaUsuario(String email, String senha) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT u FROM Usuario AS u LEFT JOIN FETCH u.curso WHERE u.email = :email AND u.senha = :senha");
			query.setParameter("email", email);
			query.setParameter("senha", senha);

			Usuario usuario = (Usuario) query.uniqueResult();

			getSession().close();

			if (usuario != null)
				return usuario;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Usuario> getTodosUsuarios() {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT u FROM Usuario AS u LEFT JOIN FETCH u.curso ORDER BY u.nome");

			List<Usuario> usuarios = query.list();
			getSession().close();

			if (usuarios != null)
				return usuarios;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Usuario retornaUsuario(String nome) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT u FROM Usuario AS u LEFT JOIN FETCH u.curso WHERE u.nome = :nome");
			query.setParameter("nome", nome);

			Usuario usuario = (Usuario) query.uniqueResult();
			getSession().close();

			if (usuario != null)
				return usuario;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Usuario> retornaProfessores() {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT u FROM Usuario AS u WHERE u.tipoUsuario = :tipoUsuario");
			query.setParameter("tipoUsuario", 1);

			List<Usuario> professores = query.list();
			getSession().close();

			if (professores != null)
				return professores;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Usuario retornaUsuarioEmail(String email) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT u FROM Usuario AS u LEFT JOIN FETCH u.curso WHERE u.email = :email");
			query.setParameter("email", email);

			Usuario usuario = (Usuario) query.uniqueResult();
			getSession().close();
			if (usuario != null)
				return usuario;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Usuario> retornaProfessoresTurma(Turma turma) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT u FROM Turma AS t JOIN t.usuarios as u WHERE t = :turma AND u.tipoUsuario = :tipoUsuario");
			query.setParameter("turma", turma);
			query.setParameter("tipoUsuario", 1);

			List<Usuario> professores = query.list();
			getSession().close();
			if (professores != null)
				return professores;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Usuario> retornaAlunosTurma(Turma turma) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT u FROM Turma AS t JOIN t.usuarios as u WHERE t = :turma AND u.tipoUsuario = :tipoUsuario");
			query.setParameter("turma", turma);
			query.setParameter("tipoUsuario", 2);

			List<Usuario> usuarios = query.list();

			getSession().close();

			if (usuarios != null)
				return usuarios;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Usuario> retornaUsuariosTurma(Turma turma) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT u FROM Turma AS t JOIN t.usuarios as u WHERE t = :turma");
			query.setParameter("turma", turma);

			List<Usuario> usuario = query.list();

			getSession().close();

			if (usuario != null)
				return usuario;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Usuario retornaCoordAvaliado(Usuario usuario) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT u FROM Usuario AS u WHERE u.curso = :curso AND u.tipoUsuario = :tipoUsuario");
			query.setParameter("curso", usuario.getCurso());
			query.setParameter("tipoUsuario", 0);

			Usuario u = (Usuario) query.uniqueResult();

			getSession().close();
			if (u != null) {
				return u;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public List<Usuario> retornaProfessorCurso(Curso curso) {//nao tenho certeza se está funcioando
		try {
			Query query = getSession()
					.createQuery(
							"SELECT u FROM Usuario AS u WHERE u.curso = :curso AND u.tipoUsuario = :tipoUsuario");
			query.setParameter("curso", curso);
			query.setParameter("tipoUsuario", 1);

			List<Usuario> usuarios = query.list();

			getSession().close();
			if (usuarios != null) {
				return usuarios;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Usuario> retornaAlunoCurso(Curso curso) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT u FROM Usuario AS u WHERE u.curso = :curso AND u.tipoUsuario = :tipoUsuario");
			query.setParameter("curso", curso);
			query.setParameter("tipoUsuario", 2);

			List<Usuario> usuarios = query.list();

			getSession().close();
			if (usuarios != null) {
				return usuarios;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Usuario> getUsuarioCursoTipoQuestionario(Curso curso,
			int tipoQuestionario) {
		QuestionarioDAO questionarioDAO = new QuestionarioDAO();
		List<Questionario> questionarios = questionarioDAO
				.retornaQuestinariosCursoTipo(curso, tipoQuestionario);// pega
																		// os
																		// questionarios
																		// para
																		// coordenadores
		List<PrazoQuestionario> prazos = new ArrayList<PrazoQuestionario>();
		for (int i = 0; i < questionarios.size(); i++)
			prazos.addAll(questionarios.get(i).getPrazos()); // pega os prazos
																// desses
																// quesntionarios
		List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
		AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
		for (int i = 0; i < prazos.size(); i++)
			avaliacoes.addAll(avaliacaoDAO
					.getAvaliacoesPrazoQuestionario(prazos.get(i))); // olha a
																		// quais
																		// avalia�oes
																		// esse
																		// quesntionario
																		// pertence
		List<Usuario> coordenadores = new ArrayList<Usuario>();
		for (int i = 0; i < avaliacoes.size(); i++) {
			if (!coordenadores.contains(avaliacoes.get(i).getAvaliado()))
				;
			coordenadores.add(avaliacoes.get(i).getAvaliado());
		}
		return null;
	}

}
