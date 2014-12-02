package br.ufjf.avaliacao.business;

import java.util.List;

import org.hibernate.HibernateException;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import br.ufjf.avaliacao.model.Matricula;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.impl.CursoDAO;
import br.ufjf.avaliacao.persistent.impl.MatriculaDAO;
import br.ufjf.avaliacao.persistent.impl.UsuarioDAO;

public class UsuarioBusiness extends GenericBusiness {

	
	public boolean login(String login, String senha) throws HibernateException,//recebe o usuario do integra e faz as devidas operaÃƒÆ’Ã‚Â§ÃƒÆ’Ã‚Âµes
			Exception {
		Usuario usuario = IntegraBusiness.getusuarioIntegra(login, senha);//pega informaÃƒÆ’Ã‚Â§oes do usuario do integra
		if(usuario!=null)
		{
						
			UsuarioDAO uDAO = new UsuarioDAO();
			Usuario usuarioAux = uDAO.retornaUsuarioCPF(login);//verifica se ja foi cadastrado
			if(usuarioAux != null)
			{
				usuarioAux.setEmail(usuario.getEmail());
				uDAO.editar(usuarioAux);//atualizando email, caso tenha mudado	
	
				MatriculaDAO mDAO = new MatriculaDAO();
				List<Matricula> matriculas = mDAO.getMatriculasUsuario(usuarioAux);
				List<Matricula> matriculasAux = usuario.getMatriculas(); 
				
				for(int i=0;i<matriculasAux.size();i++)
				{
					boolean existeNoBanco = false;
					for(int j=0;j<matriculas.size();j++)
					{
						if(matriculasAux.get(i).getMatricula().equals(matriculas.get(j).getMatricula()))
						{
							existeNoBanco = true;
						}
					}
					if(!existeNoBanco)
					{
						matriculasAux.get(i).setUsuario(usuarioAux);
						mDAO.salvar(matriculasAux.get(i));
					}
				}
				
				Session session = Sessions.getCurrent();
				session.setAttribute("usuario", usuarioAux);
				return true;
			}
			
		}

		else
		{
			UsuarioDAO uDAO = new UsuarioDAO();
			Usuario usuarioAux = uDAO.retornaUsuarioCPF(login);//verifica se ja foi cadastrado
			
			if(usuarioAux!=null)
			{
				if(usuarioAux.getTipoUsuario()==3 || usuarioAux.getTipoUsuario()==1)
				{
					if(usuarioAux.getEmail().equals(senha))
					{
						CursoDAO cDAO = new CursoDAO();
						usuarioAux.setCurso(cDAO.getCursoCoordenadorOuViceUsuario(usuarioAux));
						Session session = Sessions.getCurrent();
						session.setAttribute("usuario", usuarioAux);
						return true;
					}
				}
							
			}
		}
		return false;
	}
	
	public boolean checaLogin(Usuario usuario) throws HibernateException,
			Exception {
		if (usuario != null) {
			return true;
		}
		return false;
	}

	public boolean cadastrado(String email, String nome)
			throws HibernateException, Exception {
		UsuarioDAO usuarioDAO = new UsuarioDAO();
		if (usuarioDAO.retornaUsuarioNome(nome) != null
				|| usuarioDAO.retornaUsuarioEmail(email) != null) {
			return true;
		} else
			return false;
	}

	public boolean cadatroValido(Usuario usuario) {
		if (usuario.getTipoUsuario() != null)
			if (usuario.getTipoUsuario() == 1)//coordenador
			{
				if (campoStrValido(usuario.getNome())
						
						&& campoStrValido(usuario.getEmail()))
					return true;
				else
					return false;
			}
			else
			{
				if(usuario.getTipoUsuario() == 2)//aluno
				{
					if (campoStrValido(usuario.getNome())
						&& campoStrValido(usuario.getEmail())
						&& campoStrValido(usuario.getMatriculaAtiva().getMatricula())
						&& (usuario.getCurso() != null))
						return true;
					else
						return false;
				}
				
			}
		else
		{
			if(usuario.getTipoUsuario() == 3)//aluno
			{
				if (campoStrValido(usuario.getNome())
					&& campoStrValido(usuario.getEmail()))
					return true;
				else
					return false;
			}
		}
		return false;
	}

	public boolean usuarioUsado(Usuario usuario) {
		if (usuario.getTurmas().isEmpty())
			return false;
		return true;
	}

}