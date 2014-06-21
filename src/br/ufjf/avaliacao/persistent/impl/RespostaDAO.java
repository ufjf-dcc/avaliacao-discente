package br.ufjf.avaliacao.persistent.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.Resposta;
import br.ufjf.avaliacao.model.Turma;
import br.ufjf.avaliacao.model.Usuario;
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
	
	
	public List<Resposta> getRespostasPerguntaTurmaSemestreAvaliado(Pergunta p, String semestre, Turma turma,Usuario avaliado) {
		try {
			Query query = getSession().createQuery("SELECT rs FROM Resposta AS rs LEFT JOIN FETCH rs.avaliacao AS ava WHERE ava.turma =:turma AND ava.avaliado =:avaliado AND rs.pergunta =:pergunta AND rs.semestre =:semestre");
			query.setParameter("pergunta", p);
			query.setParameter("semestre", semestre);
			query.setParameter("turma", turma);
			query.setParameter("avaliado", avaliado);
			
			List<Resposta> respostas = query.list();
			
			getSession().close();
			
			return respostas;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	@SuppressWarnings("unchecked")
	public List<Resposta> getRespostasPerguntaTurmaSemestre(Pergunta p, String semestre, Turma turma) {
		try {
			Query query = getSession().createQuery("SELECT rs FROM Resposta AS rs LEFT JOIN FETCH rs.avaliacao AS ava WHERE ava.turma =:turma AND rs.pergunta =:pergunta AND rs.semestre =:semestre");
			query.setParameter("pergunta", p);
			query.setParameter("semestre", semestre);
			query.setParameter("turma", turma);
			
			List<Resposta> respostas = query.list();
			
			getSession().close();
			
			return respostas;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Resposta> getRespostasPerguntaTurmaAvaliado(Pergunta p, Turma turma,Usuario avaliado) {
		try {
			Query query = getSession().createQuery("SELECT rs FROM Resposta AS rs LEFT JOIN FETCH rs.avaliacao AS ava WHERE ava.turma =:turma AND ava.avaliado =:avaliado AND rs.pergunta =:pergunta");
			query.setParameter("pergunta", p);
			query.setParameter("turma", turma);
			query.setParameter("avaliado", avaliado);
			
			List<Resposta> respostas = query.list();
			
			getSession().close();
			
			return respostas;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Resposta> getRespostasPerguntaSemestreAvaliado(Pergunta p, String semestre, Usuario avaliado) {
		try {
			Query query = getSession().createQuery("SELECT rs FROM Resposta AS rs LEFT JOIN FETCH rs.avaliacao AS ava WHERE ava.avaliado =:avaliado AND rs.pergunta =:pergunta AND rs.semestre =:semestre");
			query.setParameter("pergunta", p);
			query.setParameter("semestre", semestre);
			query.setParameter("avaliado", avaliado);
			
			List<Resposta> respostas = query.list();
			
			getSession().close();
			
			return respostas;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Resposta> getRespostasPerguntaTurma(Pergunta p, Turma turma) {
		try {
			Query query = getSession().createQuery("SELECT rs FROM Resposta AS rs LEFT JOIN FETCH rs.avaliacao AS ava WHERE ava.turma =:turma AND rs.pergunta =:pergunta");
			query.setParameter("pergunta", p);
			query.setParameter("turma", turma);
			
			List<Resposta> respostas = query.list();
			
			getSession().close();
			
			return respostas;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
		
	public List<Resposta> getRespostasPerguntaSemestre(Pergunta p, String semestre) {
		try {
			Query query = getSession().createQuery("SELECT rs FROM Resposta AS rs LEFT JOIN FETCH rs.avaliacao AS ava WHERE  rs.pergunta =:pergunta AND rs.semestre =:semestre");
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
	
	public List<Resposta> getRespostasPerguntaAvaliado(Pergunta p, Usuario avaliado) {
		try {
			Query query = getSession().createQuery("SELECT rs FROM Resposta AS rs LEFT JOIN FETCH rs.avaliacao AS ava WHERE ava.avaliado =:avaliado AND rs.pergunta =:pergunta");
			query.setParameter("pergunta", p);
			query.setParameter("avaliado", avaliado);
			
			List<Resposta> respostas = query.list();
			
			getSession().close();
			
			return respostas;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public List<Resposta> getRespostasPergunta(Pergunta p) {
		try {
			Query query = getSession().createQuery("SELECT rs FROM Resposta AS rs LEFT JOIN FETCH rs.avaliacao AS ava WHERE  rs.pergunta =:pergunta");
			query.setParameter("pergunta", p);
			
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

			if(r!=null)
				return r;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}