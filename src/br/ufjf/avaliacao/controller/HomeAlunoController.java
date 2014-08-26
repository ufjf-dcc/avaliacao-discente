package br.ufjf.avaliacao.controller;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

import br.ufjf.avaliacao.business.GenericBusiness;
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
import br.ufjf.avaliacao.persistent.impl.SemestreDAO;
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
		
		if(((List<Window>) session.getAttribute("janelas"))==null)
		{
			carregarJanelas();
		}
		
		if(session.getAttribute("indiceJanela")==null)
			session.setAttribute("indiceJanela", 0);
		
		if(session.getAttribute("indiceQuestionario")==null)
			session.setAttribute("indiceQuestionario", 1);
		
		if(session.getAttribute("indice_linha")==null)
			session.setAttribute("indice_linha", 0);
	}


	
	@Command
	public void carregarJanelas()
	{
		session.setAttribute("janelas", new ArrayList<Window>());
		session.setAttribute("questionarios", questionariosDisponiveis());
		for(int i=0;i<((List<Questionario>) session.getAttribute("questionarios")).size();i++)
		{
			((List<Window>) session.getAttribute("janelas")).add(null);
		}
		
	}
	
	@Command
	public void fecharQuestionarios()// torna todos os questionarios invisivel
	{
		for(int i=0;i < ((List<Window>) session.getAttribute("janelas")).size();i++)
		{
			if(((List<Window>) session.getAttribute("janelas")).get(i)!=null)
				((List<Window>) session.getAttribute("janelas")).get(i).setVisible(false);
		}
	}
	
	@Command
	public void escolherJanela(int indice)
	{
		
		respostas = new ArrayList<Resposta>();

		if(((List<Questionario>) session.getAttribute("questionarios")).get(indice).getTipoQuestionario()==0
				|| ((List<Questionario>) session.getAttribute("questionarios")).get(indice).getTipoQuestionario()==2
				|| ((List<Questionario>) session.getAttribute("questionarios")).get(indice).getTipoQuestionario()==3)
			session.setAttribute("turma",null);
		
	
		else{
			int adicionar = 0;
			
			if (questionarioDAO.retornaQuestinarioParaUsuarioCoord(usuario) != null //verificando se ha questionrio coordenador pra ser avaliado
					&& prazoDAO.getPrazoQuestionarioDisponivel(questionarioDAO.retornaQuestinarioParaUsuarioCoord(usuario))!=null)
				adicionar++;
		
			
			if (questionarioDAO.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario) != null //verificando se ha questionrio autoavaliação pra ser avaliado
					&& prazoDAO.getPrazoQuestionarioDisponivel(questionarioDAO.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario))!=null)
				adicionar++;	
			
			if (questionarioDAO.retornaQuestinarioParaUsuarioInfra(usuario) != null
					&& prazoDAO.getPrazoQuestionarioDisponivel(questionarioDAO.retornaQuestinarioParaUsuarioInfra(usuario))!=null) 
				adicionar++;
			
			
			if(((List<Questionario>) session.getAttribute("questionarios")).get(indice).getTipoQuestionario()==1)
			{	
				for(int j=0;j<turmasDoUsuario.size();j++)
				{
						List<Usuario> professoresTurma = usuarioDAO.retornaProfessoresTurma(turmasDoUsuario.get(j));
							
						int numprof = 0;
						for(int i=0;i<professoresTurma.size();i++)
						{
							if(adicionar==indice)
							{
								session.setAttribute("turma", turmasDoUsuario.get(j));
								session.setAttribute("numProfTurma",numprof);
								break;
							}
							else
							{
								adicionar++;
							}
							ordemProfessores.add(professoresTurma.get(i));
							numprof++;
						}
						if(adicionar==indice)
							break;
				}
			}
		}

		
		for(int i=0;i < ((List<Window>) session.getAttribute("janelas")).size();i++)
		{
			
			if(((List<Window>) session.getAttribute("janelas")).get(i)!=null)
			{
				if(i==indice)
				{
					session.setAttribute("questionarioAtual", ((List<Questionario>) session.getAttribute("questionarios")).get(i));
					((List<Window>) session.getAttribute("janelas")).get(i).setVisible(true);
				}
				else
					((List<Window>) session.getAttribute("janelas")).get(i).setVisible(false);
			}
			else
			{
				if(i==indice)
				{
					session.setAttribute("questionarioAtual", ((List<Questionario>) session.getAttribute("questionarios")).get(i));
					session.setAttribute("indiceQuestionario", i+1);
					Window novaJanela= new Window();
	
					novaJanela = (Window) Executions.createComponents("/avaliacoes.zul",null, null);
					((List<Window>) session.getAttribute("janelas")).set(i,novaJanela);
					((List<Window>) session.getAttribute("janelas")).get(i).doModal();
	
				}
			}
		}
	}
	
	@Command
	public void avaliarQuestionario(@BindingParam("butao") Button botao,//no zul essa função seleciona qual questionario vai ser avaliado incialmente
			@BindingParam("grid") Grid grid)
	{
		
	
		for(int i=0;i<grid.getChildren().get(1).getChildren().size();i++)
		{
			if(grid.getChildren().get(1).getChildren().get(i).getChildren().get(4).getChildren().get(1) == botao)
			{
				int adiciona = 0;
				//feito pra funcionar o homealuno para redirecionar direto em avaliar pois la não mostra de coordenador ou infra ou auto---------
				if (questionarioDAO.retornaQuestinarioParaUsuarioCoord(usuario) != null //verificando se ha questionrio coordenador pra ser avaliado
						&& prazoDAO.getPrazoQuestionarioDisponivel(questionarioDAO.retornaQuestinarioParaUsuarioCoord(usuario))!=null)
					adiciona++;
			
				
				if (questionarioDAO.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario) != null //verificando se ha questionrio autoavaliação pra ser avaliado
						&& prazoDAO.getPrazoQuestionarioDisponivel(questionarioDAO.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario))!=null)
					adiciona++;	
				
				if (questionarioDAO.retornaQuestinarioParaUsuarioInfra(usuario) != null
						&& prazoDAO.getPrazoQuestionarioDisponivel(questionarioDAO.retornaQuestinarioParaUsuarioInfra(usuario))!=null) 
					adiciona++;
				//-----------------------------------------------------------
				escolherJanela(i+adiciona);
			}
		}
	}
	
	public int getIndiceLinha()
	{
		session.setAttribute("indice_linha", 1 + (int)session.getAttribute("indice_linha"));
		return -1 + ((int) session.getAttribute("indice_linha"));
	}
	
	@Command
	public void avaliacaoGeral()
	{
		escolherJanela(0);
	}
	
	@Command
	public void teste(@BindingParam("indice") String indice)
	{
		int indiceQ = Integer.parseInt(indice);
		System.out.println(verificaAvaliado(indiceQ));
	
	}
	
	@Command
	public void teste2()
	{
		List<Pergunta> perguntas = ((Questionario) session.getAttribute("questionarioAtual")).getPerguntas();
		
		List<Integer> perguntasObrigatorias = new ArrayList();
		for(int i = (perguntas.size() - 1);i>=0;i--)
		{
			if(perguntas.get(i).isObrigatorio())
			{
				perguntasObrigatorias.add(perguntas.get(i).getIdPergunta());
			}
		}
		
		GenericBusiness gb = new GenericBusiness();
		for(int i= (respostas.size() - 1);i>=0;i--)
		{
			if(!gb.campoStrValido(respostas.get(i).getResposta()))
			{
				respostas.remove(i);
			}
		}
		
		List<Integer> perguntasRespondidas = new ArrayList();
		for(int i=0;i<respostas.size();i++)
		{
			perguntasRespondidas.add(respostas.get(i).getPergunta().getIdPergunta());
		}
		
		for(int i = 0;i<perguntasObrigatorias.size();i++)
		{
			if(!perguntasRespondidas.contains(perguntasObrigatorias.get(i)))
			{
				System.out.println("false");
			}
		}
			
	}
	
	
	public void funcao(){
		
		List<Questionario> todosQuestionarios = questionariosDisponiveis();
		
		
//		List<Window> todosQuestionarios = todosAsWindow;	
//		int indiceDasWindows;
		
//		validar windows quando confirmar
//			- cada window ou esta completamente preenchida ou nao está preenchida
//			- vai ter q poder limpar as respostas
//			- um botao pra passar pra proxima window com o proximo questionario tudo controlado
		
	}
	
	
	// salva a avaliação e verifica se vai precisar retornar na função que
		// verifica quem deve ser avalido agora (avaliarAuz)
		@Command
		public void terminarAvaliacao() {
			
			if(verificarRespostasQuestionario())
			{
		
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
				
				UsuarioDAO usuarioDAO = new UsuarioDAO();
				if(usuarioDAO.retornaProfessoresTurma((Turma) session.getAttribute("turma")).size()==1)
				{
					avaliado = usuarioDAO.retornaProfessoresTurma((Turma) session.getAttribute("turma")).get(0);
				}
				else
				{
					avaliado = usuarioDAO.retornaProfessoresTurma((Turma) session.getAttribute("turma")).get((int) session.getAttribute("numProfTurma"));
				}
				
				turmaUsada = (Turma) session.getAttribute("turma");
				
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
	
			}
			// -------------------------------------------------------------------------------
	
			Clients.showBusy("Salvando avaliação..");
			

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
					Messagebox.show("Salvo", "Concluído", Messagebox.OK,
							Messagebox.INFORMATION, new EventListener<Event>() {
								@Override
								public void onEvent(Event event) throws Exception {
									Executions.sendRedirect(null);
								}
							});
				}
			
			}
			else
			{
				Messagebox.show("Preencha as perguntas obrigatórias(*)");
			}
	
		}
		
	@Command
		public void escolha(@BindingParam("string") String escolha,
				@BindingParam("pergunta") Pergunta perg) {
		
			SemestreDAO semestreDAO = new SemestreDAO();
			resposta.setResposta(escolha);
			resposta.setPergunta(perg);
			resposta.setSemestre(semestreDAO.getSemestreAtualCurso(usuario.getCurso()).getNomeSemestre());
		
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
	public void checkAvaliado(@BindingParam("quest") Questionario quest,
			@BindingParam("check") Checkbox check)
	{
		List<Questionario> quests = questionariosDisponiveis();
		for(int i=0;i<quests.size();i++)
		{
			if(quests.get(i).getIdQuestionario() == quest.getIdQuestionario())
			{
				if(verificaAvaliado(i))
					check.setChecked(true);
				check.setDisabled(true);
				break;
			}
		}
	}
	
	public boolean verificaAvaliado(int indice)//mostra se o questionario ja foi avaliado esse indice é baseado no questionariosDisponiveis()
			
	{
		Questionario quest = questionariosDisponiveis().get(indice);
		
		AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
		
		if(quest.getTipoQuestionario()==0)//coordenação
		{
			return avaliacaoDAO.jaAvaliouCoordenadorDataAtual(usuario);
		}
		
		if(quest.getTipoQuestionario()==1)//professores
		{
			UsuarioDAO usuarioDAO = new UsuarioDAO();
			List<Usuario> professoresTurma = new ArrayList<Usuario>();
			List<Turma> turmasProfessor = new ArrayList<Turma>();
			for(int i=0;i<turmasDoUsuario.size();i++)
			{
				professoresTurma.addAll(usuarioDAO.retornaProfessoresTurma(turmasDoUsuario.get(i)));
				for(int j=0;j<usuarioDAO.retornaProfessoresTurma(turmasDoUsuario.get(i)).size();j++)
					turmasProfessor.add(turmasDoUsuario.get(i));
			}
			QuestionarioDAO questionarioDAO = new QuestionarioDAO();
			int adiciona = 0;
			if(questionarioDAO.retornaQuestinarioParaUsuarioCoord(usuario)!=null)
				adiciona++;
			if(questionarioDAO.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario)!=null)
				adiciona++;
			if(questionarioDAO.retornaQuestinarioParaUsuarioInfra(usuario)!=null)
				adiciona++;
			indice -= adiciona;
			avaliacaoDAO.alunoJaAvaliouEsteProfessor(usuario,professoresTurma.get(indice),turmasProfessor.get(indice));
		}
		
		if(quest.getTipoQuestionario()==2)//autoavaliação
		{
			return avaliacaoDAO.jaSeAvaliouDataAtual(usuario);
		}
		
		if(quest.getTipoQuestionario()==3)//infraestrutura
		{
			return avaliacaoDAO.jaAvaliouInfraestruturaDataAtual(usuario);
		}
		
		return false;
	}
	
	public boolean verificarRespostasQuestionario()//valida o questionario quanto a obrigatoriedade de responder pergunta
	{
	List<Pergunta> perguntas = ((Questionario) session.getAttribute("questionarioAtual")).getPerguntas();
		
		List<Integer> perguntasObrigatorias = new ArrayList();
		for(int i = (perguntas.size() - 1);i>=0;i--)
		{
			if(perguntas.get(i).isObrigatorio())
			{
				perguntasObrigatorias.add(perguntas.get(i).getIdPergunta());
			}
		}
		
		GenericBusiness gb = new GenericBusiness();
		for(int i= (respostas.size() - 1);i>=0;i--)
		{
			if(!gb.campoStrValido(respostas.get(i).getResposta()))
			{
				respostas.remove(i);
			}
		}
		
		List<Integer> perguntasRespondidas = new ArrayList();
		for(int i=0;i<respostas.size();i++)
		{
			perguntasRespondidas.add(respostas.get(i).getPergunta().getIdPergunta());
		}
		
		for(int i = 0;i<perguntasObrigatorias.size();i++)
		{
			if(!perguntasRespondidas.contains(perguntasObrigatorias.get(i)))
			{
				return false;
			}
		}
		
		return true;
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
	private List<Questionario> questionariosDisponiveis() {//função que retorna todos os questionarios que pode ser avaliados
			
			List<Questionario> questionariosAAvaliar = new ArrayList<Questionario>();
			
			if ( //verificando se ha questionrio coordenador pra ser avaliado
					questionarioDAO.retornaQuestinarioParaUsuarioCoord(usuario) != null
					&& prazoDAO.getPrazoQuestionarioDisponivel(questionarioDAO.retornaQuestinarioParaUsuarioCoord(usuario))!=null)
					
					questionariosAAvaliar.add(questionarioDAO.retornaQuestinarioParaUsuarioCoord(usuario));
		
			
			if ( //verificando se ha questionrio autoavaliação pra ser avaliado
					questionarioDAO.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario) != null
					&& prazoDAO.getPrazoQuestionarioDisponivel(questionarioDAO.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario))!=null)
						
				questionariosAAvaliar.add(questionarioDAO.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario));
	
			if (
					questionarioDAO.retornaQuestinarioParaUsuarioInfra(usuario) != null
					&& prazoDAO.getPrazoQuestionarioDisponivel(questionarioDAO.retornaQuestinarioParaUsuarioInfra(usuario))!=null) 
			
				questionariosAAvaliar.add(questionarioDAO.retornaQuestinarioParaUsuarioInfra(usuario));
			
				for(int j=0;j<turmasDoUsuario.size();j++)
				{
						List<Usuario> professoresTurma = usuarioDAO.retornaProfessoresTurma(turmasDoUsuario.get(j));
	
						for(int i=0;i<professoresTurma.size();i++)
						{
								ordemProfessores.add(professoresTurma.get(i));
								questionariosAAvaliar.add(questionarioProf);
						}
				}
				return questionariosAAvaliar;
	}
	@Command
	public void proximoQuestionario(@BindingParam("indiceQuestionario") String indice)
	{
		int indiceQuest = Integer.parseInt(indice);
		if(indiceQuest < ((List<Window>) session.getAttribute("janelas")).size())
		{
			escolherJanela(indiceQuest);
		}
		else
			Messagebox.show("Já chegou ao limite de questionarios");
		
	}


	@Command
	public void questionarioAnterior(@BindingParam("indiceQuestionario") String indice)
	{
		int indiceQuest = Integer.parseInt(indice)-2;
		if(indiceQuest >= 0)
		{
			escolherJanela(indiceQuest);
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
	public void questDisponivel(@BindingParam("questionario") Questionario q,
			@BindingParam("row") Row r) {
		if (!new PrazoQuestionarioDAO().questionarioEstaDisponivel(q)) {
			r.detach();
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
