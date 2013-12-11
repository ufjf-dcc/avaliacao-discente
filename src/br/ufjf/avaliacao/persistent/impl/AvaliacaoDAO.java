package br.ufjf.avaliacao.persistent.impl;

import java.util.Date;

import org.hibernate.Query;

import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.GenericoDAO;
import br.ufjf.avaliacao.persistent.IAvalicaoDAO;

public class AvaliacaoDAO extends GenericoDAO implements IAvalicaoDAO {

	//Método para verificar se existe uma avaliação de coordenador, infraestrutura ou autoavaliação
	public boolean jaAvaliouOutros(Usuario aluno, Questionario quest) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT a FROM Avaliacao AS a LEFT JOIN FETCH a.prazoQuestionario AS p WHERE p.questionario = :questionario AND a.avaliando = :aluno AND :dataAtual BETWEEN p.dataInicial AND p.dataFinal");
			query.setParameter("questionario", quest);
			query.setParameter("aluno", aluno);
			query.setParameter("dataAtual", new Date());

			Avaliacao avaliacao = (Avaliacao) query.uniqueResult();

			if (avaliacao!=null)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}