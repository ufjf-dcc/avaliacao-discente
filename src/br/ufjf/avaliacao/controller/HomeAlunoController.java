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

	private static boolean devoAvaliarCordenador=false;
	private static boolean avaliarAuto=false;
	
	@Init
	public void init() throws HibernateException, Exception {
		testaPermissaoAluno();

	}

	@Command
	public void avaliar(
			@BindingParam("questionario") Questionario questionario,
			@BindingParam("turma") Turma turma) {


	// AGORA TENHO QUE VERIFICAR SE JA AVALIOU O COOR A SI MESMO E A INFRA E ADICSIONAR OS QUEST NECESSARIOS

		Questionario questProf = questionario;

		HomeAlunoController.turmaAtual = turma;
		
		// se ainda não fez a  avaliação de coordenador e se tem uma avaliação de coordenador pra fazer:
		if(!avaliacaoDAO.jaAvaliouCoordenador(prazo, usuario)){
			System.out.println("OLELE");
			HomeAlunoController.questionarioAtual = questionarioDAO.retornaQuestinarioParaUsuarioCoord(usuario);//seta o questionario atual para um questionario de coordenador a ser avaliado
			Window window = (Window) Executions.createComponents("/avaliar.zul",
					null, null);
			window.doModal();
		}
		// se ainda não fez a auto avaliação e se tem uma auto avaliação pra fazer:
		if(!avaliacaoDAO.jaSeAvalior(prazo, usuario) && questionarioDAO.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario)!=null){
			HomeAlunoController.questionarioAtual = questionarioDAO.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario);//seta o questionario atual para um questionario de auto avaliação a ser avaliado
			Window window = (Window) Executions.createComponents("/avaliar.zul",
					null, null);
			window.doModal();
		}
		
		Questionario questInfra = questionarioDAO.retornaQuestinarioParaUsuarioInfra(usuario);

		HomeAlunoController.questionarioAtual = questProf;
		Window window = (Window) Executions.createComponents("/avaliar.zul",
				null, null);
		window.doModal();

		
	}
	
		
	//FUNÇÂO DE TESTE (EXCLUIR DEPOIS)
	@Command
	public void teste(
			@BindingParam("questionario") Questionario questionario,
			@BindingParam("turma") Turma turma) {


	// AGORA TENHO QUE VERIFICAR SE JA AVALIOU O COOR A SI MESMO E A INFRA E ADICSIONAR OS QUEST NECESSARIOS

		Questionario questProf = questionario;
		
		HomeAlunoController.turmaAtual = turma;
			
		// se ainda não fez a  avaliação de coordenador e se tem uma avaliação de coordenador pra fazer:
		if(!avaliacaoDAO.jaAvaliouCoordenador(prazo, usuario) && questionarioDAO.retornaQuestinarioParaUsuarioCoord(usuario)!=null){
			devoAvaliarCordenador = true;
			HomeAlunoController.questionarioAtual = questionarioDAO.retornaQuestinarioParaUsuarioCoord(usuario);//seta o questionario atual para um questionario de coordenador a ser avaliado
			Window window = (Window) Executions.createComponents("/avaliar.zul",
					null, null);
			window.doModal();
		}
		else
			System.out.println("ja avaliou coordenador");
		// se ainda não fez a auto avaliação e se tem uma auto avaliação pra fazer:
		if(!avaliacaoDAO.jaSeAvalior(prazo, usuario) && questionarioDAO.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario)!=null){
			HomeAlunoController.questionarioAtual = questionarioDAO.retornaQuestinarioParaUsuarioAutoAvaliacao(usuario);//seta o questionario atual para um questionario de auto avaliação a ser avaliado
			Window window = (Window) Executions.createComponents("/avaliar.zul",
					null, null);
			window.doModal();
		}
		
		Questionario questInfra = questionarioDAO.retornaQuestinarioParaUsuarioInfra(usuario);

		HomeAlunoController.questionarioAtual = questProf;
		Window window = (Window) Executions.createComponents("/avaliar.zul",
				null, null);
		window.doModal();
		
		Messagebox.show("Finalizado", "Concluído",
				Messagebox.OK, Messagebox.INFORMATION,
				new EventListener<Event>() {
					@Override
					public void onEvent(Event event) throws Exception {
						Executions.sendRedirect(null);
					}
				});
	

		
	}
	

	@Command
	public void terminarAvaliacaoProfessor() {
		
		Usuario avaliado = null;
		//GAMBIARRA 
		if(questionarioAtual.getTipoQuestionario() == 1)// se o avaliado for um professor(sendo avaliado int statico que indica quem esta sendo avaliado)
			avaliado = usuarioDAO.retornaProfessoresTurma(turmaAtual).get(0);
		if(devoAvaliarCordenador){
			avaliado = usuarioDAO.retornaCoordenadorCurso(usuario.getCurso());
			devoAvaliarCordenador = false;
		}
			
		
		Clients.showBusy("Salvando avaliação..");
		if (respostas.size() != questionarioAtual.getPerguntas().size()) {
			Clients.clearBusy();
			Messagebox.show("Responda todas as perguntas antes de finalizar!");
		} else {
			Avaliacao avaliacao = new Avaliacao();
			avaliacao.setAvaliando(usuario);
			avaliacao.setAvaliado(avaliado);
			avaliacao.setPrazoQuestionario(prazo);
			avaliacao.setTurma(turmaAtual);
			new AvaliacaoDAO().salvar(avaliacao);
			for (Resposta r : respostas) {
				r.setAvaliacao(avaliacao);
			}
			new RespostaDAO().salvarLista(respostas);
			Clients.clearBusy();
			Messagebox.show("Avaliação Salva com Sucesso");
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
