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
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
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
	private Questionario questionarioProf = questionarioDAO
			.retornaQuestionarioProf(usuario);
	private List<Turma> turmasDoUsuario = new TurmaDAO()
			.getTurmasUsuario(usuario);
	private PrazoQuestionario prazo = new PrazoQuestionarioDAO()
			.prazoQuestionario(questionarioProf);
	private static Questionario questionarioAtual = new Questionario();
	private static Turma turmaAtual = new Turma();

	private Questionario questionarioCoord = questionarioDAO
			.retornaQuestinarioParaUsuarioCoord(usuario);
	private Questionario questionarioAuto = questionarioDAO
			.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario);
	private Questionario questionarioInfra = questionarioDAO
			.retornaQuestinarioParaUsuarioInfra(usuario);

	private Resposta resposta = new Resposta();
	private List<Resposta> respostas = new ArrayList<Resposta>();
	private Usuario coordAvaliado = usuarioDAO.retornaCoordAvaliado(usuario);
	private static Questionario questionarioInicial = new Questionario();
	

	@Init
	public void init() throws HibernateException, Exception {
		testaPermissaoAluno();

	}

	@Command
	public void avaliar(
			@BindingParam("questionario") Questionario questionario,
			@BindingParam("turma") Turma turma) {
				
	}
	
		
	//FUNÇÂO DE TESTE (EXCLUIR DEPOIS)
	@Command
	public void teste(
			@BindingParam("questionario") Questionario questionario,
			@BindingParam("turma") Turma turma) {

		HomeAlunoController.questionarioInicial = questionario;
		questionarioAtual = questionario;
		turmaAtual = turma;
		avaliarAux();
	}

	// AGORA TEM QUE VERIFICAR SE JA AVALIOU O COOR A SI MESMO E A INFRA E ADICIONAR OS QUEST NECESSARIOS PARA O USUARIO AVALIAR
		// se ainda não fez a  avaliação de coordenador e se tem uma avaliação de coordenador pra fazer

	public void avaliarAux(){
		
		if(!avaliacaoDAO.jaAvaliouCoordenadorDataAtual(usuario) && questionarioDAO.retornaQuestinarioParaUsuarioCoord(usuario)!=null){
			//setando qual é o questionario que deve ser avaliado
			System.out.println("avaliando coor");
			questionarioAtual = questionarioDAO.retornaQuestinarioParaUsuarioCoord(usuario);//seta o questionario atual para um questionario de coordenador a ser avaliado
			Window window = (Window) Executions.createComponents("/avaliar.zul",
					null, null);
			window.doModal();
			
		}
		else{
			// se ainda não fez a auto avaliação e se tem uma auto avaliação pra fazer
			if(!avaliacaoDAO.jaSeAvaliorDataAtual(usuario) && questionarioDAO.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario)!=null){
				System.out.println("avaliando auto");
				//setando qual é o questionario que deve ser avaliado
				questionarioAtual = questionarioDAO.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario);//seta o questionario atual para um questionario de auto avaliação a ser avaliado
				Window window = (Window) Executions.createComponents("/avaliar.zul",
						null, null);
				window.doModal();
			}
			else{
				if(!avaliacaoDAO.jaAvaliouInfraestruturaDataAtual(usuario)){
					//setando qual é o questionario que deve ser avaliado
					System.out.println("avaliando infra");
					questionarioAtual = questionarioDAO.retornaQuestinarioParaUsuarioInfra(usuario);
					Window window = (Window) Executions.createComponents("/avaliar.zul",
							null, null);
					window.doModal();
				}
				else{
					if(!avaliacaoDAO.jaAvaliou(usuario,turmaAtual)){
						System.out.println("avaliando prof");
						questionarioAtual = HomeAlunoController.questionarioInicial;
						Window window = (Window) Executions.createComponents("/avaliar.zul",
								null, null);
						window.doModal();
						
					
					}
				}
			}
		}
	}

	
	@Command
	public void terminarAvaliacaoProfessor(@BindingParam("window") Window win) {

		Usuario avaliado = null;
		Turma turmaUsada = null;

		//setando o usuario que vai ser avaliado---------------------------------------------
		// verificando se antes deve avaliar alguma coisa, nao estou conseguindo importar o tipo de questionario pois
		
		System.out.println(")))))_____--------"+questionarioAtual.getTipoQuestionario());
		
		if(questionarioAtual.getTipoQuestionario() == 0){ // verifica se o questionario é do tipo coodenador
			avaliado = usuarioDAO.retornaCoordenadorCurso(usuario.getCurso());
			System.out.println("avaliando coor");
		}
		if(questionarioAtual.getTipoQuestionario() == 1){ // verifica se o questionario é do tipo professor
			avaliado = usuario;
			turmaUsada = turmaAtual;
			System.out.println("avaliando prof");
			

		}
		if(questionarioAtual.getTipoQuestionario() == 2){ // verifica se o questionario é do tipo auto avaliação
			avaliado = usuario;
			System.out.println("avaliando auto");

		}
		if(questionarioAtual.getTipoQuestionario() == 3){ // verifica se o questionario é do tipo infraestrutura
			avaliado = null;
			System.out.println("avaliando infra");

		}

		//-------------------------------------------------------------------------------
					
		Clients.showBusy("Salvando avaliação..");
		if (respostas.size() != questionarioAtual.getPerguntas().size()) {
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
			if(questionarioAtual.getTipoQuestionario() != 1)
			Messagebox.show("Avaliação Salva com Sucesso", "Concluído",
					Messagebox.OK, Messagebox.INFORMATION,
					new EventListener<Event>() {
						@Override
						public void onEvent(Event event) throws Exception {
							avaliarAux();
						}
					});
			else
				Messagebox.show("Finalizado", "Concluído",
						Messagebox.OK, Messagebox.INFORMATION,
						new EventListener<Event>() {
							@Override
							public void onEvent(Event event) throws Exception {
								Executions.sendRedirect(null);
							}
						});
		}

	}

	
	@Command
	public void criarCampoResposta(@BindingParam("row") Row row,
			@BindingParam("tipoPergunta") Integer tipoPergunta) {
		Component component = row.getFirstChild();
		switch (tipoPergunta) {
		case 0:
			component.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().detach();
			component.getNextSibling().getNextSibling().getNextSibling()
					.detach();
			component.getNextSibling().getNextSibling().detach();
			break;
		case 1:
			component.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().detach();
			component.getNextSibling().getNextSibling().getNextSibling()
					.detach();
			component.getNextSibling().detach();
			break;
		case 2:
			component.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().detach();
			component.getNextSibling().getNextSibling().detach();
			component.getNextSibling().detach();
			break;
		case 3:
			component.getNextSibling().getNextSibling().getNextSibling()
					.detach();
			component.getNextSibling().getNextSibling().detach();
			component.getNextSibling().detach();
			break;
		default:
			;
			break;
		}
	}

	@Command
	public void jaAvaliou(@BindingParam("div") Div d,
			@BindingParam("turma") Turma t) {
		
		if(!new AvaliacaoDAO().jaAvaliou(usuario, t)){
			d.getFirstChild().setVisible(false);
			d.getLastChild().setVisible(true);
		}
		else {
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

		for (int i = 0; i < respostas.size(); i++)
			if (respostas.get(i).getPergunta() == resposta.getPergunta()) {
				respostas.remove(respostas.get(i));
				break;
			}

		respostas.add(resposta);
		resposta = new Resposta();
	}

	@Command
	public void avaliado(@BindingParam("label") Label l) {
		l.setValue(coordAvaliado.getNome() + " - " + "Coordenador "
				+ coordAvaliado.getCurso().getNomeCurso());
	}

	public Questionario getQuestionarioAtual() {
		return questionarioAtual;
	}

	public void setQuestionarioAtual(Questionario questionarioAtual) {
		HomeAlunoController.questionarioAtual = questionarioAtual;
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

	public Questionario getQuestionarioCoord() {
		return questionarioCoord;
	}

	public void setQuestionarioCoord(Questionario questionarioCoord) {
		this.questionarioCoord = questionarioCoord;
	}

	public Questionario getQuestionarioAuto() {
		return questionarioAuto;
	}

	public void setQuestionarioAuto(Questionario questionarioAuto) {
		this.questionarioAuto = questionarioAuto;
	}

	public Questionario getQuestionarioInfra() {
		return questionarioInfra;
	}

	public void setQuestionarioInfra(Questionario questionarioInfra) {
		this.questionarioInfra = questionarioInfra;
	}

	public List<Turma> getTurmasDoUsuario() {
		return turmasDoUsuario;
	}

	public void setTurmasDoUsuario(List<Turma> turmasDoUsuario) {
		this.turmasDoUsuario = turmasDoUsuario;
	}

	public Questionario getQuestionarioProf() {
		return questionarioProf;
	}

	public void setQuestionarioProf(Questionario questionarioProf) {
		this.questionarioProf = questionarioProf;
	}

	public Turma getTurmaAtual() {
		return turmaAtual;
	}

	public void setTurmaAtual(Turma turmaAtual) {
		HomeAlunoController.turmaAtual = turmaAtual;
	}

	public PrazoQuestionario getPrazo() {
		return prazo;
	}

	public void setPrazo(PrazoQuestionario prazo) {
		this.prazo = prazo;
	}

}
