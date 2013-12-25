package br.ufjf.avaliacao.persistent.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufjf.avaliacao.model.PrazoQuestionario;
import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.persistent.GenericoDAO;
import br.ufjf.avaliacao.persistent.IPrazoQuestionarioDAO;

public class PrazoQuestionarioDAO extends GenericoDAO implements
		IPrazoQuestionarioDAO {
	

	public List<PrazoQuestionario> getPrazos(Questionario questionario) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT p FROM PrazoQuestionario AS p LEFT JOIN FETCH p.questionario AS q WHERE q = :questionario");
			query.setParameter("questionario", questionario);

			@SuppressWarnings("unchecked")
			List<PrazoQuestionario> prazos = query.list();

			getSession().close();

			return prazos;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public PrazoQuestionario prazoQuestionario(Questionario questionario) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT p FROM PrazoQuestionario AS p LEFT JOIN FETCH p.questionario AS q WHERE q = :questionario AND :dataAtual BETWEEN p.dataInicial AND p.dataFinal");
			query.setParameter("dataAtual", new Date());
			query.setParameter("questionario", questionario);

			PrazoQuestionario prazo = (PrazoQuestionario) query.uniqueResult();

			getSession().close();

			if (prazo != null) {
				return prazo;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean questionarioEstaDisponivel(Questionario questionario) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT p FROM PrazoQuestionario AS p LEFT JOIN FETCH p.questionario AS q WHERE q = :questionario AND :dataAtual BETWEEN p.dataInicial AND p.dataFinal");
			query.setParameter("dataAtual", new Date());
			query.setParameter("questionario", questionario);
			PrazoQuestionario prazo = (PrazoQuestionario) query.uniqueResult();

			getSession().close();

			if (prazo != null) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}