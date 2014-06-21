package br.ufjf.avaliacao.persistent.impl;

import java.util.List;

import org.hibernate.Query;

import br.ufjf.avaliacao.model.Disciplina;
import br.ufjf.avaliacao.persistent.GenericoDAO;
import br.ufjf.avaliacao.persistent.IDisciplinaDAO;

public class DisciplinaDAO extends GenericoDAO implements IDisciplinaDAO {

	@Override
	public Disciplina retornaDisciplinaCod(String codDisciplina) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT d FROM Disciplina AS d WHERE d.codDisciplina = :codDisciplina");
			query.setParameter("codDisciplina", codDisciplina);

			Disciplina disciplina = (Disciplina) query.uniqueResult();

			getSession().close();

			if (disciplina != null)
				return disciplina;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Disciplina retornaDisciplinaNome(String nomeDisciplina) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT disciplina FROM Disciplina AS disciplina WHERE disciplina.nomeDisciplina = :nomeDisciplina");
			query.setParameter("nomeDisciplina", nomeDisciplina);

			Disciplina disciplina = (Disciplina) query.uniqueResult();

			getSession().close();

			if (disciplina != null)
				return disciplina;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Disciplina> getTodasDisciplinas() {
		try {
			Query query = getSession().createQuery(
					"SElECT d FROM Disciplina AS d ORDER BY d.nomeDisciplina");
			List<Disciplina> disciplinas = query.list();
			getSession().close();

			if (disciplinas != null)
				return disciplinas;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
