package br.ufjf.avaliacao.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zul.Window;

import br.ufjf.avaliacao.business.UsuarioBusiness;
import br.ufjf.avaliacao.library.GetCurso;
import br.ufjf.avaliacao.model.Curso;
import br.ufjf.avaliacao.model.Matricula;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.impl.CursoDAO;
import br.ufjf.avaliacao.persistent.impl.MatriculaDAO;
import br.ufjf.avaliacao.persistent.impl.UsuarioDAO;

public class AuthController implements Initiator {

	@Override
	public void doInit(Page page, Map<String, Object> args) throws Exception {
		Clients.showBusy("Aguarde o redirecionamento...");
		Session session = Sessions.getCurrent();
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		
		UsuarioBusiness usuarioBusiness = new UsuarioBusiness();
		if (!usuarioBusiness.checaLogin(usuario)) {
			Executions.sendRedirect("/index.zul");
			return;
		}
		Clients.clearBusy();
	}
	
	@Command
	public void exit(){
		Session session = Sessions.getCurrent();
		session.setAttribute("usuario",null);
		Executions.sendRedirect("/index.zul");
	}
	
	public String getMatricula() // pega matricula do usuario na sessao. Ã‰ usada no menu do aluno
	{
		Session session = Sessions.getCurrent();
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		if(usuario!=null)
		{
			if(usuario.getMatriculaAtiva()!=null)
			{
				return "Matricula ativa: "+usuario.getMatriculaAtiva().getMatricula();
			}
		}
		return "Sem matricula ativa no nosso sistema";
	}
	
	public List<Matricula> getMatriculas()//quando o se quer trocar a matricula aqui pega a lista de matriculas do usuario na sessao
	{
		Session session = Sessions.getCurrent();
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		if(usuario!=null)
		{
			MatriculaDAO mDAO = new MatriculaDAO();
			return mDAO.getMatriculasUsuario(usuario);
		}
		return new ArrayList<Matricula>();
	}
	
	@Command
	public void mudarMatricula() // abre uma janela com o zul para mudar matricula ativa, qua toma conta do curso que esta recebendo/avaliando agora
	{
		Window window = (Window) Executions.createComponents(
				"/mudarMatricula.zul", null, null);
		window.doModal();
		
	}
	
	@Command //nÃ£o ha ainda a verificaÃ§Ã£o se o tipo de usuario mudou, por exemplo um aluno virar professor
	public void salvarMatricula(@BindingParam("matricula") String matricula)// salva a matricula escolhida, caso seja nÃ£o exista o curso relacionado a essa matricula ele Ã© criado e adicionado ao BD
	{
		Session session = Sessions.getCurrent();
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		MatriculaDAO mDAO = new MatriculaDAO();
		List<Matricula> matriculas = mDAO.getMatriculasUsuario(usuario);
		for(int i=0;i<matriculas.size();i++)
		{
			if(matricula.equals(matriculas.get(i).getMatricula()))
			{
				usuario.setMatriculaAtiva(matriculas.get(i));
				
				CursoDAO cDAO = new CursoDAO();
				Curso curso = cDAO.getCursoIdentificador(GetCurso.getInfoCurso(matricula));
				
				if(curso==null)
				{
					curso = GetCurso.getConf(GetCurso.getInfoCurso(matricula));
					cDAO.salvar(curso);
				}
				usuario.setCurso(curso);
				UsuarioDAO uDAO = new UsuarioDAO();
				uDAO.editar(usuario);
				break;
			}
		}
		Executions.sendRedirect(null);
	}
	
	
}