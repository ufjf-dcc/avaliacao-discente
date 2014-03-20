package br.ufjf.avaliacao.persistent.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.Resposta;
import br.ufjf.avaliacao.persistent.GenericoDAO;
import br.ufjf.avaliacao.persistent.IRespostaDAO;

public class RespostaDAO extends GenericoDAO implements IRespostaDAO{
	
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Resposta> respostasAvaliacao(Avaliacao a) {
		try {
			Query query = getSession().createQuery("SELECT rs FROM Resposta AS rs WHERE rs.avaliacao =:avaliacao");
			query.setParameter("avaliacao", a);
			
			List<Resposta> respostas = query.list();
			
			return respostas;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Resposta> getRespostasPerguntaSemestre(Pergunta p, String semestre) {
		try {
			Query query = getSession().createQuery("SELECT rs FROM Resposta AS rs WHERE rs.pergunta =:pergunta AND rs.semestre =:semestre");
			query.setParameter("pergunta", p);
			query.setParameter("semestre", semestre);
			
			List<Resposta> respostas = query.list();
			
			getSession().close();
			
			return respostas;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<String> getAllSemestres() {
		try {
			Query query = getSession().createQuery("SELECT r FROM Resposta AS r");

			@SuppressWarnings("unchecked")
			List<Resposta> r = query.list();

			List<String> semestres = new ArrayList<String>();

			for (int i=0 ;i<r.size();i++) {
				if(!semestres.contains(r.get(i).getSemestre()))
					semestres.add(r.get(i).getSemestre());
			}
			return semestres;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Resposta> retornaRespostaSemestre(String semestre) {
		try {
			Query query = getSession().createQuery("SELECT r FROM Resposta AS r WHERE r.semestre = : semestre");
			query.setParameter("semestre", semestre);
			
			@SuppressWarnings("unchecked")
			List<Resposta> r = query.list();

			System.out.println("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT "+r.size());
			if(r.size()>0)
				return r;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}