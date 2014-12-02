package br.ufjf.avaliacao.persistent.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.Curso;
import br.ufjf.avaliacao.model.PrazoQuestionario;
import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.model.Usuario;
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

	public List<PrazoQuestionario> questionarioDisponivel(
			Questionario questionario) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT p FROM PrazoQuestionario AS p LEFT JOIN FETCH p.questionario AS q WHERE q = :questionario AND :dataAtual BETWEEN p.dataInicial AND p.dataFinal");
			query.setParameter("dataAtual", new Date());
			query.setParameter("questionario", questionario);

			@SuppressWarnings("unchecked")
			List<PrazoQuestionario> prazos = query.list();
			PrazoQuestionario prazo = (PrazoQuestionario) query.uniqueResult();

			getSession().close();

			if (prazos != null) {
				return prazos;
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

	public PrazoQuestionario getPrazoQuestionarioDisponivel(Questionario questionario)
	{	
		if(questionario!=null){
			List<PrazoQuestionario> prazos = questionario.getPrazos();
			if(prazos.size()>0)
			{
				PrazoQuestionario auxPrazo = prazos.get(0);
				for(int i=0;i<prazos.size();i++)
				{
					if(prazos.get(i).getDataFinal().after(auxPrazo.getDataFinal()))
						auxPrazo=prazos.get(i);
				}
				Date novaData = new Date();
				novaData.setDate(novaData.getDate()-1);
				Date novaData2 = new Date();
				novaData2.setDate(novaData2.getDate()+1);
				if(auxPrazo.getDataFinal().after(novaData) && auxPrazo.getDataInicial().before(novaData2))
				return auxPrazo;
			}
		}
		return null;
	}
	
		
	
	public PrazoQuestionario getPrazoAvaliacao(Avaliacao avaliacao) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT p FROM PrazoQuestionario AS p LEFT JOIN FETCH p.avaliacao AS a ");

			List<PrazoQuestionario> prazos = query.list();

			getSession().close();

			if (prazos != null) {
				return prazos.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}