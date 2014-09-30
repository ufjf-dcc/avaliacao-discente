package br.ufjf.avaliacao.persistent.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufjf.avaliacao.model.Curso;
import br.ufjf.avaliacao.model.Semestre;
import br.ufjf.avaliacao.persistent.GenericoDAO;
import br.ufjf.avaliacao.persistent.ISemestreDAO;

public class SemestreDAO extends GenericoDAO implements ISemestreDAO {
	
	@Override
	public Semestre getSemestreAtual() {
		return null;
	}
	
	@Override
	public List<Semestre> getAllSemestres() {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT s FROM Semestre AS s");

			@SuppressWarnings("unchecked")
			List<Semestre> s = query.list();
			
			getSession().close();
			
			if(s.size()>0)
				return s;
			return null;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Semestre> getSemestresCurso(Curso curso) {
		try {
			Query query = getSession().createQuery(
					"SELECT s FROM Semestre AS s WHERE s.curso = :curso");
			query.setParameter("curso", curso);

			@SuppressWarnings("unchecked")
			List<Semestre> s = query.list();
			
			getSession().close();

			if(s == null)
			{
				s = new ArrayList<Semestre>();
			}
			return s;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Semestre getSemestreAtualCurso(Curso curso) {
		List<Semestre> semestres = getSemestresCurso(curso);
		
		Date dataAtual = new Date();
		dataAtual.setDate(dataAtual.getDate()-1);//prevenção do before
				
		for(int i=0;i<semestres.size();i++)
		{
			if(dataAtual.before(semestres.get(i).getDataFinalSemestre()))
			{
				return semestres.get(i);
			}
		}
		return null;
	}
	
	public boolean validacaoTituloCurso(Curso curso, String nomeTitulo) {
		List<Semestre> semestres = getSemestresCurso(curso);
		for(int i=0;i<semestres.size();i++)
		{
			if(semestres.get(i).getNomeSemestre() == nomeTitulo)
			{
				return false;
			}
		}
		return true;
	}
	
	public void alterarSemestres()
	{
		
	}

}
