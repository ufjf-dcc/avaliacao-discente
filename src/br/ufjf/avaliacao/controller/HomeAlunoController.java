package br.ufjf.avaliacao.controller;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.PrazoQuestionario;
import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.model.Resposta;
import br.ufjf.avaliacao.model.Turma;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.impl.AvaliacaoDAO;
import br.ufjf.avaliacao.persistent.impl.PrazoQuestionarioDAO;
import br.ufjf.avaliacao.persistent.impl.QuestionarioDAO;
import br.ufjf.avaliacao.persistent.impl.RespostaDAO;
import br.ufjf.avaliacao.persistent.impl.TurmaDAO;
import br.ufjf.avaliacao.persistent.impl.UsuarioDAO;

public class HomeAlunoController extends GenericController {

	private QuestionarioDAO questionarioDAO = new QuestionarioDAO();
	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	private AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
	private PrazoQuestionarioDAO prazoDAO = new PrazoQuestionarioDAO();
	private Questionario questionarioProf = questionarioDAO
			.retornaQuestionarioProf(usuario);
	private List<Turma> turmasDoUsuario = new TurmaDAO()
			.getTurmasUsuario(usuario);
	private PrazoQuestionario prazo = new PrazoQuestionarioDAO()
			.prazoQuestionario(questionarioProf);

	private Questionario questionarioCoord = questionarioDAO
			.retornaQuestinarioParaUsuarioCoord(usuario);
	private Questionario questionarioAuto = questionarioDAO
			.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario);
	private Questionario questionarioInfra = questionarioDAO
			.retornaQuestinarioParaUsuarioInfra(usuario);

	private Resposta resposta = new Resposta();
	private List<Resposta> respostas = new ArrayList<Resposta>();
	private Usuario coordAvaliado = usuarioDAO.retornaCoordAvaliado(usuario);
	
	private List<Questionario> questionarios = new ArrayList<Questionario>();
	private List<Usuario> ordemProfessores = new ArrayList<Usuario>();
	private int quantidadeQuestionarios;
	private List<Questionario> questionariosExibidos = new ArrayList<Questionario>();

	
	private String selecionado = new String();
	
	private Questionario[] vetorQuestionarios;
	private int indiceQuestionario=0;
	
	public Questionario[] getQuestionarios() {//em formato vetor para o zul
		return vetorQuestionarios;
	}
	public int getQuantidadeQuestionarios() {//em formato vetor para o zul
		return quantidadeQuestionarios;
	}
	public int getIndiceQuestionario() {//em formato vetor para o zul
		return (int)session.getAttribute("indiceQuestionario");
	}
	

	@Init
	public void init() throws HibernateException, Exception {
		testaPermissaoAluno();
		
		if(questionarios.size()==0)
		{
			questionarios = questionariosDisponiveis();
			vetorQuestionarios = new Questionario[questionarios.size()];
			for(int i=0;i<questionarios.size();i++)
				vetorQuestionarios[i]=questionarios.get(i);
			quantidadeQuestionarios=questionarios.size();		
		}
		if(session.getAttribute("janelas")==null)
			session.setAttribute("janelas", new ArrayList<Window>());

		if(session.getAttribute("indiceJanela")==null)
			session.setAttribute("indiceJanela", 0);
		
		if(session.getAttribute("indiceQuestionario")==null)
			session.setAttribute("indiceQuestionario", 0);
	}

	@Command
	public void getCoor()
	{
		System.out.println("coor:"+usuario.getCurso().getCoordenador().getNome());
		System.out.println("vice:"+usuario.getCurso().getViceCoordenador().getNome());
	}
	
	public void funcao(){
		
		List<Questionario> todosQuestionarios = questionariosDisponiveis();
		
		
//		List<Window> todosQuestionarios = todosAsWindow;	
//		int indiceDasWindows;
		
//		validar windows quando confirmar
//			- cada window ou esta completamente preenchida ou nao está preenchida
//			- vai ter q poder limpar as respostas
//			- um botao pra passar pra proxima window com o procimo questionario tudo controlado
		
	}
	
	
	
	// essa função diz quem precisa ser avaliado agora
	private List<Questionario> questionariosDisponiveis() {
			
			List<Questionario> questionariosAAvaliar = new ArrayList<Questionario>();
			
			if (!avaliacaoDAO.jaAvaliouCoordenadorDataAtual(usuario) //verificando se ha questionrio coordenador pra ser avaliado
					&& questionarioDAO.retornaQuestinarioParaUsuarioCoord(usuario) != null
					&& prazoDAO.getPrazoQuestionarioDisponivel(questionarioDAO.retornaQuestinarioParaUsuarioCoord(usuario))!=null)
					
					questionariosAAvaliar.add(questionarioDAO.retornaQuestinarioParaUsuarioCoord(usuario));
		
			
			if (!avaliacaoDAO.jaSeAvaliouDataAtual(usuario) //verificando se ha questionrio autoavaliação pra ser avaliado
					&& questionarioDAO.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario) != null
					&& prazoDAO.getPrazoQuestionarioDisponivel(questionarioDAO.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario))!=null)
						
				questionariosAAvaliar.add(questionarioDAO.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario));
	
			if (!avaliacaoDAO.jaAvaliouInfraestruturaDataAtual(usuario)
					&& questionarioDAO.retornaQuestinarioParaUsuarioInfra(usuario) != null
					&& prazoDAO.getPrazoQuestionarioDisponivel(questionarioDAO.retornaQuestinarioParaUsuarioInfra(usuario))!=null) 
			
				questionariosAAvaliar.add(questionarioDAO.retornaQuestinarioParaUsuarioInfra(usuario));
			
				for(int j=0;j<turmasDoUsuario.size();j++)
				{
					if(!avaliacaoDAO.jaAvaliou(usuario, turmasDoUsuario.get(j)))
					{
						
						List<Usuario> professoresTurma = usuarioDAO.retornaProfessoresTurma(turmasDoUsuario.get(j));

						for(int i=0;i<professoresTurma.size();i++)
						{
							if(!avaliacaoDAO.alunoJaAvaliouEsteProfessor(usuario, professoresTurma.get(i),turmasDoUsuario.get(j)))
							{
								ordemProfessores.add(professoresTurma.get(i));
								questionariosAAvaliar.add(questionarioProf);
							}
						}

					}
				}
				return questionariosAAvaliar;
	}

		
	@Command
	public void proximoQuestionario()
	{
		if(questionarios.size()==0)
			questionarios=questionariosDisponiveis();
		
		if(((int)session.getAttribute("indiceQuestionario"))<questionarios.size())
		{
			if(((int)session.getAttribute("indiceJanela"))==((List<Window>) session.getAttribute("janelas")).size())// se o numero de janelas e  o indice forem iguais cria uma nova janela
			{
				session.setAttribute("questionarioAtual", questionarios.get((int)session.getAttribute("indiceQuestionario")));
				session.setAttribute("indiceQuestionario", 1 +(int) session.getAttribute("indiceQuestionario"));
				Window novaJanela= new Window();

				novaJanela = (Window) Executions.createComponents("/avaliacoes.zul",null, null);
				if(((List<Window>) session.getAttribute("janelas")).add(novaJanela));
				((List<Window>) session.getAttribute("janelas")).get(((List<Window>) session.getAttribute("janelas")).size()-1).doModal();
				session.setAttribute("indiceJanela", 1 + (int)session.getAttribute("indiceJanela"));
				if(((List<Window>) session.getAttribute("janelas")).size()>=2)
					((List<Window>) session.getAttribute("janelas")).get(((List<Window>) session.getAttribute("janelas")).size()-2).setVisible(false);
			}

			else
			{
				session.setAttribute("indiceQuestionario", 1 + (int)session.getAttribute("indiceQuestionario"));
				session.setAttribute("indiceJanela",1 + (int)session.getAttribute("indiceJanela"));
				session.setAttribute("questionarioAtual", questionarios.get((int)session.getAttribute("indiceQuestionario")));
				((List<Window>) session.getAttribute("janelas")).get((int)session.getAttribute("indiceJanela")).setVisible(true);
				if(((List<Window>) session.getAttribute("janelas")).size()>(-1 + (int)session.getAttribute("indiceJanela")))
					((List<Window>) session.getAttribute("janelas")).get(-1 + (int)session.getAttribute("indiceJanela")).setVisible(false);
			}
		}
		else
			Messagebox.show("Já chegou ao limite de questionarios");
		
	}


	@Command
	public void questionarioAnterior()
	{
		if((int) session.getAttribute("indiceJanela")>0)
		{
			session.setAttribute("indiceQuestionario", -1 + (int)session.getAttribute("indiceQuestionario"));
			session.setAttribute("indiceJanela",-1 + (int)session.getAttribute("indiceJanela"));
			session.setAttribute("questionarioAtual", questionarios.get((int)session.getAttribute("indiceQuestionario")));
			((List<Window>) session.getAttribute("janelas")).get((int)session.getAttribute("indiceJanela")).setVisible(true);
			if(((List<Window>) session.getAttribute("janelas")).size()>(1+(int)session.getAttribute("indiceJanela")))
				((List<Window>) session.getAttribute("janelas")).get(1 + (int)session.getAttribute("indiceJanela")).setVisible(false);
		}
	
		else
			Messagebox.show("Já chegou ao limite de questionarios");
	
	}

	
		
	@Command
	public void avaliar(
			@BindingParam("questionario") Questionario questionario,
			@BindingParam("turma") Turma turma) {
		session.setAttribute("questionario", questionario);
		session.setAttribute("turma", turma);
		session.setAttribute("questionarioInicial", questionario);
		session.setAttribute("questionarioAtual", questionario);
		session.setAttribute("numProfTurma", usuarioDAO
				.retornaProfessoresTurma(turma).size());
		avaliarAux();
	}

	@Command
	public void iniciarAvaliacao() {
		session.setAttribute("questionarioAtual",
				(Questionario) session.getAttribute("questionario"));
	}

	// essa função diz quem precisa ser avaliado agora
	private void avaliarAux() {
		// se ainda não fez a avaliação de coordenador e se tem uma
		// avaliação de
		// coordenador pra fazer
		if (!avaliacaoDAO.jaAvaliouCoordenadorDataAtual(usuario)
				&& questionarioDAO.retornaQuestinarioParaUsuarioCoord(usuario) != null
				&& prazoDAO.getPrazoQuestionarioDisponivel(questionarioDAO.retornaQuestinarioParaUsuarioCoord(usuario))!=null) {
			// setando qual é o questionario que deve ser avaliado
			session.setAttribute("questionarioAtual",
					questionarioDAO.retornaQuestinarioParaUsuarioCoord(usuario));// seta
																					// o
																					// questionario
																					// atual
																					// para
																					// um
																					// questionario
																					// de
																					// coordenador
																					// a
																					// ser
																					// avaliado
			Window window = (Window) Executions.createComponents(
					"/avaliar.zul", null, null);
			window.setTitle("Avaliação de Coordenador - "
					+ usuarioDAO.retornaCoordAvaliado(usuario).getNome());
			window.doModal();
					session.setAttribute("avaliarCoordenador", "/avaliar.zul");
		} else {
			// se ainda não fez a auto avaliação e se tem uma auto
			// avaliação pra
			// fazer
			if (!avaliacaoDAO.jaSeAvaliouDataAtual(usuario)
					&& questionarioDAO.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario) != null
					&& prazoDAO.getPrazoQuestionarioDisponivel(questionarioDAO.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario))!=null) {
					
				// setando qual é o questionario que deve ser avaliado
				session.setAttribute("questionarioAtual", questionarioDAO
						.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario));// seta
																				// o
																				// questionario
																				// atual
																				// para
																				// um
																				// questionario
																				// de
																				// auto
																				// avaliação
																				// a
																				// ser
																				// avaliado
				Window window = (Window) Executions.createComponents(
						"/avaliar.zul", null, null);
				window.setTitle("Autoavaliação");
				window.doModal();
			} else {
				if (!avaliacaoDAO.jaAvaliouInfraestruturaDataAtual(usuario)
						&& questionarioDAO.retornaQuestinarioParaUsuarioInfra(usuario) != null
						&& prazoDAO.getPrazoQuestionarioDisponivel(questionarioDAO.retornaQuestinarioParaUsuarioInfra(usuario))!=null) {
					// setando qual é o questionario que deve ser avaliado
					session.setAttribute("questionarioAtual", questionarioDAO
							.retornaQuestinarioParaUsuarioInfra(usuario));
					Window window = (Window) Executions.createComponents(
							"/avaliar.zul", null, null);
					window.setTitle("Avaliação de Infraestrutura");
					window.doModal();
				} else {
					if (!avaliacaoDAO.jaAvaliouTodosProfessoresTurma(usuario,
							(Turma) session.getAttribute("turma"))) {

						session.setAttribute("questionarioAtual",
								(Questionario) session
										.getAttribute("questionarioInicial"));
						Window window = (Window) Executions.createComponents(
								"/avaliar.zul", null, null);
						window.setTitle("Avaliação de Professor - "
								+ avaliacaoDAO
										.retornaProfessoresNaoAvaliados(
												usuario,
												(Turma) session
														.getAttribute("turma"))
										.get(0).getNome());
						window.doModal();
					}
				}
			}
		}
	}

	// salva a avaliação e verifica se vai precisar retornar na função que
	// verifica quem deve ser avalido agora (avaliarAuz)
	@Command
	public void terminarAvaliacao() {

		Usuario avaliado = null;
		Turma turmaUsada = null;
		prazo = prazoDAO.getPrazoQuestionarioDisponivel((Questionario) session
				.getAttribute("questionarioAtual"));
		

		// setando o usuario que vai ser avaliado e a
		// turma-----------------------------------------------------
		if (((Questionario) session.getAttribute("questionarioAtual"))
				.getTipoQuestionario() == 0) { // verifica se o
			// questionario é do
			// tipo coodenador
			avaliado = usuarioDAO.retornaCoordenadorCurso(usuario.getCurso());
		}
		if (((Questionario) session.getAttribute("questionarioAtual"))
				.getTipoQuestionario() == 1) { // verifica se o
			// questionario é do
			// tipo professor
			avaliado = avaliacaoDAO.retornaProfessoresNaoAvaliados(usuario,
					(Turma) session.getAttribute("turma")).get(0);
			turmaUsada = (Turma) session.getAttribute("turma");
			session.setAttribute("numProfTurma",
					((int) session.getAttribute("numProfTurma") - 1));

		}
		if (((Questionario) session.getAttribute("questionarioAtual"))
				.getTipoQuestionario() == 2) { // verifica se o
			// questionario é do
			// tipo auto
			// avaliação
			avaliado = usuario;

		}
		if (((Questionario) session.getAttribute("questionarioAtual"))
				.getTipoQuestionario() == 3) { // verifica se o
			// questionario é do
			// tipo
			// infraestrutura
			if (avaliacaoDAO.retornaProfessoresNaoAvaliados(usuario,
					(Turma) session.getAttribute("turma")).size() == 1)
				avaliado = null;
		}
		// -------------------------------------------------------------------------------

		Clients.showBusy("Salvando avaliação..");
		if (respostas.size() < ((Questionario) session
				.getAttribute("questionarioAtual")).getPerguntas().size()) {
			Clients.clearBusy();
			Messagebox.show("Responda todas as perguntas antes de finalizar!");
		} else {
			Avaliacao avaliacao = new Avaliacao();
			avaliacao.setAvaliando(usuario);
			avaliacao.setAvaliado(avaliado);
			avaliacao.setPrazoQuestionario(prazo);
			avaliacao.setTurma(turmaUsada);
			new AvaliacaoDAO().salvar(avaliacao);

			for (Resposta r : respostas) {
				r.setAvaliacao(avaliacao);
			}
			new RespostaDAO().salvarLista(respostas);
			Clients.clearBusy();

			// se tiver mais de um professor a ser avaliado ou nao for uma
			// avaliação de professor, ele verifica o que mais precisa ser
			// avaliado
			if (((Questionario) session.getAttribute("questionarioAtual"))
					.getTipoQuestionario() != 1
					|| ((int) session.getAttribute("numProfTurma")) > 0) {
				Messagebox.show("Avaliação Salva com Sucesso", "Concluído",
						Messagebox.OK, Messagebox.INFORMATION,
						new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								avaliarAux();
							}
						});

			}
			// se acabar de avaliar a ultima coisa(que seria o ultimo professor
			// da turma(ou o unico)) ele finaliza as avaliaçoes e da um refresh
			// na pagina
			else {
				Messagebox.show("Finalizado", "Concluído", Messagebox.OK,
						Messagebox.INFORMATION, new EventListener<Event>() {
							@Override
							public void onEvent(Event event) throws Exception {
								Executions.sendRedirect(null);
							}
						});
			}
		}

	}

	@Command
	public void criarCampoResposta(@BindingParam("label") Label l,
			@BindingParam("pergunta") Pergunta p) {
		switch (p.getTipoPergunta()) {
		case 0:
			l.getNextSibling().getNextSibling().getNextSibling().detach();
			l.getNextSibling().getNextSibling().detach();
			break;
		case 1:
			l.getNextSibling().getNextSibling().getNextSibling().detach();
			l.getNextSibling().detach();
			break;
		case 2:
			l.getNextSibling().getNextSibling().detach();
			l.getNextSibling().detach();
			break;
		case 3:
			l.getNextSibling().getNextSibling().detach();
			l.getNextSibling().detach();
			break;
		default:
			;
			break;
		}
	}

	@Command
	public void jaAvaliou(@BindingParam("div") Div d,
			@BindingParam("turma") Turma t) {

		if (!new AvaliacaoDAO().jaAvaliou(usuario, t)) {
			d.getFirstChild().setVisible(false);
			d.getLastChild().setVisible(true);
		} else {
			d.getFirstChild().setVisible(true);
			d.getLastChild().setVisible(false);
		}
	}

	@Command
	public void questDisponivel(@BindingParam("questionario") Questionario q,
			@BindingParam("row") Row r) {
		if (!new PrazoQuestionarioDAO().questionarioEstaDisponivel(q)) {
			r.detach();
		}
	}

	@Command
	public void escolha(@BindingParam("string") String escolha,
			@BindingParam("pergunta") Pergunta perg) {

		resposta.setResposta(escolha);
		resposta.setPergunta(perg);
		resposta.setSemestre(((Turma) session.getAttribute("turma")).getSemestre());

		for (int i = 0; i < respostas.size(); i++)
			if (respostas.get(i).getPergunta() == resposta.getPergunta()) {
				respostas.remove(respostas.get(i));
				break;
			}
		respostas.add(resposta);
		resposta = new Resposta();
	}

	@Command
	public void doChecked(@BindingParam("string") String escolha,
			@BindingParam("pergunta") Pergunta perg,
			@BindingParam("check") Checkbox box) {
		if (box.isChecked()) {
			resposta.setResposta(escolha);
			resposta.setPergunta(perg);
			resposta.setSemestre(((Turma) session.getAttribute("turma"))
					.getSemestre());
			respostas.add(resposta);
			resposta = new Resposta();
		} else {
			for (Resposta r : respostas) {
				if (r.getResposta() == escolha) {
					respostas.remove(r);
					break;
				}
			}
		}
	}

	@Command
	public void avaliado(@BindingParam("label") Label l) {
		l.setValue(coordAvaliado.getNome() + " - " + "Coordenador "
				+ coordAvaliado.getCurso().getNomeCurso());
	}

	public PrazoQuestionario getPrazo() {
		return prazo;
	}

	public void setPrazo(PrazoQuestionario prazo) {
		this.prazo = prazo;
	}

	public Questionario getQuestionarioAuto() {
		return questionarioAuto;
	}

	public Questionario getQuestionarioAtual() {
		return ((Questionario) session.getAttribute("questionarioAtual"));
	}

	public void setQuestionarioAtual(Questionario questionarioAtual) {
		session.setAttribute("questionarioAtual", questionarioAtual);
	}

	public void setQuestionarioAuto(Questionario questionarioAuto) {
		this.questionarioAuto = questionarioAuto;
	}

	public Questionario getQuestionarioCoord() {
		return questionarioCoord;
	}

	public void setQuestionarioCoord(Questionario questionarioCoord) {
		this.questionarioCoord = questionarioCoord;
	}

	public Questionario getQuestionarioInfra() {
		return questionarioInfra;
	}

	public void setQuestionarioInfra(Questionario questionarioInfra) {
		this.questionarioInfra = questionarioInfra;
	}

	public Questionario getQuestionarioProf() {
		return questionarioProf;
	}

	public void setQuestionarioProf(Questionario questionarioProf) {
		this.questionarioProf = questionarioProf;
	}

	public Resposta getResposta() {
		return resposta;
	}

	public void setResposta(Resposta resposta) {
		this.resposta = resposta;
	}

	public List<Resposta> getRespostas() {
		return respostas;
	}

	public void setRespostas(List<Resposta> respostas) {
		this.respostas = respostas;
	}

	public List<Turma> getTurmasDoUsuario() {
		return turmasDoUsuario;
	}

	public void setTurmasDoUsuario(List<Turma> turmasDoUsuario) {
		this.turmasDoUsuario = turmasDoUsuario;
	}

	

}
