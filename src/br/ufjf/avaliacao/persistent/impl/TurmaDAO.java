package br.ufjf.avaliacao.persistent.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import br.ufjf.avaliacao.model.Curso;
import br.ufjf.avaliacao.model.Disciplina;
import br.ufjf.avaliacao.model.Turma;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.GenericoDAO;
import br.ufjf.avaliacao.persistent.ITurmaDAO;

public class TurmaDAO extends GenericoDAO implements ITurmaDAO {

	@Override
	public Turma retornaTurma(String letraTurma, String semestre,
			Disciplina disciplina) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT turma FROM Turma AS turma LEFT JOIN FETCH turma.disciplina AS d WHERE turma.letraTurma = :letraTurma AND turma.semestre = :semestre AND d = :disciplina");
			query.setParameter("letraTurma", letraTurma);
			query.setParameter("semestre", semestre);
			query.setParameter("disciplina", disciplina);

			Turma turma = (Turma) query.uniqueResult();

			getSession().close();

			if (turma != null)
				return turma;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Turma> getTodasTurmas() {
		try {
			Query query = getSession().createQuery("SELECT t FROM Turma AS t");
			List<Turma> turmas = query.list();

			getSession().close();

			if (turmas != null)
				return turmas;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Turma> getTurmasUsuario(Usuario usuario) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT t FROM Usuario AS u JOIN u.turmas as t WHERE u = :usuario");
			query.setParameter("usuario", usuario);

			List<Turma> turmas = query.list();

			getSession().close();

			if (turmas != null)
				return turmas;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<String> getAllSemestres() {
		try {
			Query query = getSession().createQuery("SELECT t FROM Turma AS t");

			@SuppressWarnings("unchecked")
			List<Turma> turmas = query.list();

			List<String> semestres = new ArrayList<String>();

			for (Turma t : turmas) {
				if (!semestres.contains(t.getSemestre()))
					semestres.add(t.getSemestre());
			}
			return semestres;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Turma> getAllTurmas(String semestre) {
		try {
			Query query = getSession().createQuery(
					"SELECT t FROM Turma AS t WHERE t.semestre =:semestre");
			query.setParameter("semestre", semestre);

			@SuppressWarnings("unchecked")
			List<Turma> turmas = query.list();

			return turmas;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<String> getSemestresUsuario(Usuario usuario) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT t FROM Usuario AS u JOIN u.turmas as t WHERE u = :usuario");
			query.setParameter("usuario", usuario);

			List<Turma> turmas = query.list();

			getSession().close();

			if (turmas != null) {
				List<String> semestres = new ArrayList<String>();
				for (int i = 0; i < turmas.size(); i++)
					if (!semestres.contains(turmas.get(i).getSemestre()))
						semestres.add(turmas.get(i).getSemestre());
				return semestres;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Turma> getTurmasUsuarioSemestre(Usuario usuario, String semestre) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT t FROM Usuario AS u JOIN u.turmas as t WHERE u = :usuario AND t.semestre =:semestre");
			query.setParameter("usuario", usuario);
			query.setParameter("semestre", semestre);

			List<Turma> turmas = query.list();

			getSession().close();

			if (turmas != null)
				return turmas;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Turma> getTurmasCursoSemestre(String semestre, Curso curso) {
		UsuarioDAO usuarioDAO = new UsuarioDAO();
		List<Usuario> professores = usuarioDAO.retornaProfessorCurso(curso);
		List<Turma> turmas = new ArrayList<Turma>();
		List<Integer> aux = new ArrayList<Integer>();
		for (int i = 0; i < professores.size(); i++) {
			List<Turma> turmasAux = getTurmasUsuarioSemestre(
					professores.get(i), semestre);
			for (int j = 0; j < turmasAux.size(); j++)
				if (!aux.contains(turmasAux.get(j).getIdTurma())) {
					aux.add(turmasAux.get(j).getIdTurma());
					turmas.add(turmasAux.get(j));
				}
		}
		return turmas;
	}

	public List<Turma> getAllTurmasCurso(String semestre, Curso curso) {
		UsuarioDAO usuarioDAO = new UsuarioDAO();
		List<Usuario> professores = usuarioDAO.retornaProfessorCurso(curso);
		List<Turma> turmas = new ArrayList<Turma>();
		List<Integer> aux = new ArrayList<Integer>();
		for (int i = 0; i < professores.size(); i++) {
			List<Turma> turmasAux = getTurmasUsuario(professores.get(i));
			for (int j = 0; j < turmasAux.size(); j++) {
				if (!aux.contains(turmasAux.get(j).getIdTurma())) {
					aux.add(turmasAux.get(j).getIdTurma());
					turmas.add(turmasAux.get(j));
				}
			}
		}
		return turmas;
	}
	
	public void trocarSemestreTurmasCurso(String semestre, String novoTituloSemestre, Curso curso) {
		UsuarioDAO usuarioDAO = new UsuarioDAO();
		List<Usuario> professores = usuarioDAO.retornaProfessorCurso(curso);
		List<Turma> turmas = new ArrayList<Turma>();
		List<Integer> aux = new ArrayList<Integer>();
		for (int i = 0; i < professores.size(); i++) {
			List<Turma> turmasAux = getTurmasUsuario(professores.get(i));
			for (int j = 0; j < turmasAux.size(); j++) {
				if (!aux.contains(turmasAux.get(j).getIdTurma())) {
					aux.add(turmasAux.get(j).getIdTurma());
					turmas.add(turmasAux.get(j));
				}
			}
		}
		TurmaDAO turmaDAO = new TurmaDAO();
		for(int i=0;i<turmas.size();i++)
		{
			if(turmas.get(i).getSemestre() == semestre)
			{
				turmas.get(i).setSemestre(novoTituloSemestre);
				turmaDAO.editar(turmas.get(i));
			}
		}
	}

}
