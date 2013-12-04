package br.ufjf.avaliacao.persistent.impl;

import java.util.List;

import org.hibernate.Query;

import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.GenericoDAO;
import br.ufjf.avaliacao.persistent.IAvalicaoDAO;

public class AvaliacaoDAO extends GenericoDAO implements IAvalicaoDAO {

	@SuppressWarnings("unchecked")
	public boolean jaAvaliou(Usuario user, Questionario quest) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT a FROM Avaliacao AS a LEFT JOIN FETCH a.questionario AS q WHERE q =:questionario AND a.avaliando =:user AND q.dataInicial =:dataInicial ");
			query.setParameter("questionario", quest);
			query.setParameter("user", user);
			query.setParameter("dataInicial", quest.getDataInicial());

			List<Avaliacao> avaliacao = (List<Avaliacao>) query.list();

			if (!avaliacao.isEmpty())
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}