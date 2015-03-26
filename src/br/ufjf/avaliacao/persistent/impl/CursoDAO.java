package br.ufjf.avaliacao.persistent.impl;

import java.util.List;

import org.hibernate.Query;

import br.ufjf.avaliacao.model.Curso;
import br.ufjf.avaliacao.model.Matricula;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.GenericoDAO;
import br.ufjf.avaliacao.persistent.ICursoDAO;

public class CursoDAO extends GenericoDAO implements ICursoDAO {

	@SuppressWarnings("unchecked")
	@Override
	public List<Curso> getTodosCursos() {
		try {
			Query query = getSession().createQuery(
					"SELECT c FROM Curso AS c ORDER BY c.nomeCurso");

			List<Curso> cursos = query.list();
			getSession().close();

			if (cursos != null)
				return cursos;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Curso getCursoNome(String nomeCurso) {
		try {
			Query query = getSession().createQuery(
					"SELECT c FROM Curso AS c WHERE c.nomeCurso = :nomeCurso");
			query.setParameter("nomeCurso", nomeCurso);

			Curso curso = (Curso) query.uniqueResult();
			getSession().close();

			if (curso != null)
				return curso;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Curso getCursoIdentificador(String identificador) {
		try {
			Query query = getSession().createQuery(
					"SELECT c FROM Curso AS c WHERE c.identificador = :identificador");
			query.setParameter("identificador", identificador);

			Curso curso = (Curso) query.uniqueResult();
			getSession().close();

			if (curso != null)
				return curso;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Curso> getAllCursos() {
		try {
			Query query = getSession().createQuery(
					"SELECT c FROM Curso");

			List<Curso> c = query.list();
			getSession().close();

			if (c != null)
				return c;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public Curso getCursoCoordenadorOuViceUsuario(Usuario usuario) //dado um usuario, verifica se ele Ã© coordenador, se dim ele reotna o curso que esse usuario Ã© coordenador
	{
		CursoDAO cDAO = new CursoDAO();
		List<Curso> cursos = cDAO.getTodosCursos();
		for(int i=0;i<cursos.size();i++)
		{
			if(cursos.get(i).getCoordenador()!=null)
			{
				if((cursos.get(i).getCoordenador().getNome().equals(usuario.getNome()) && cursos.get(i).getCoordenador().getCPF().equals(usuario.getCPF()))
						|| cursos.get(i).getViceCoordenador().getNome().equals(usuario.getNome()) && cursos.get(i).getViceCoordenador().getCPF().equals(usuario.getCPF()))
					return cursos.get(i);
			}
		}
		
		return null;
	}
}
