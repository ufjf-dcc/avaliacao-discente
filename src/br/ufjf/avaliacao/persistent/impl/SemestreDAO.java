package br.ufjf.avaliacao.persistent.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufjf.avaliacao.model.Semestre;
import br.ufjf.avaliacao.persistent.GenericoDAO;
import br.ufjf.avaliacao.persistent.ISemestreDAO;

public class SemestreDAO extends GenericoDAO implements ISemestreDAO {
	
	@Override
	public Semestre getSemestreAtual() {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT s FROM Semestre AS p WHERE p.dataFinal > =: dataAtual");
			query.setParameter("dataAtual", new Date());

			@SuppressWarnings("unchecked")
			Semestre s = (Semestre) query.uniqueResult();

			getSession().close();

			return s;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public List<Semestre> getAllSemestres() {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT s FROM Semestre AS p");

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
	

}
