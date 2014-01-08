package br.ufjf.avaliacao.persistent.impl;

import java.util.List;

import org.hibernate.Query;

import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.PrazoQuestionario;
import br.ufjf.avaliacao.model.Turma;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.GenericoDAO;
import br.ufjf.avaliacao.persistent.IAvalicaoDAO;

public class AvaliacaoDAO extends GenericoDAO implements IAvalicaoDAO {

	public boolean jaAvaliou(Usuario usuario, Turma turma) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT a FROM Avaliacao AS a LEFT JOIN FETCH a.turma AS t WHERE a.avaliando = :usuario AND t = :turma");
			query.setParameter("turma", turma);
			query.setParameter("usuario", usuario);

			Avaliacao a = (Avaliacao) query.uniqueResult();

			getSession().close();

			if (a != null)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public List<Avaliacao> avaliacoes(){
		try {
			Query query = getSession()
					.createQuery(
							"SELECT a FROM Avaliacao AS a LEFT JOIN FETCH");

			@SuppressWarnings("unchecked")
			List<Avaliacao> a = query.list();
			
			getSession().close();

			return a;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	
	}
	
	
	public boolean jaAvaliouNestePrazo(PrazoQuestionario prazo) {// procura se há alguma avaliação no que possua o prazo em questão
		try {
			Query query = getSession()
					.createQuery(
							"SELECT a FROM Avaliacao AS a LEFT JOIN FETCH a.prazoQuestionario AS p WHERE p = :prazo");
			query.setParameter("prazo", prazo);

			@SuppressWarnings("unchecked")
			List<Avaliacao> a = query.list();
			
			getSession().close();

			if (a.size()!=0)// se sim retorna true
				return true;
			else					//se nao retorna false
				return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
