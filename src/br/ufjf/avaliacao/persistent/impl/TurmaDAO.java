package br.ufjf.avaliacao.persistent.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

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
			Query query = getSession().createQuery("SELECT t FROM Turma AS t WHERE t.semestre =:semestre");
			query.setParameter("semestre", semestre);

			@SuppressWarnings("unchecked")
			List<Turma> turmas = query.list();
			
			return turmas;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
