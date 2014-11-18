package br.ufjf.avaliacao.business;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import br.ufjf.avaliacao.library.ConfHandler;
import br.ufjf.avaliacao.model.Matricula;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.impl.MatriculaDAO;
import br.ufjf.ice.integra3.ws.login.interfaces.IWsLogin;
import br.ufjf.ice.integra3.ws.login.interfaces.Profile;
import br.ufjf.ice.integra3.ws.login.interfaces.WsLoginResponse;
import br.ufjf.ice.integra3.ws.login.interfaces.WsUserInfoResponse;
import br.ufjf.ice.integra3.ws.login.service.WSLogin;

public class IntegraBusiness {

	
	   public static String md5(String input) {
	         
	        String md5 = null;
	         
	        if(null == input) return null;
	         
	        try {
		        //Create MessageDigest object for MD5
		        MessageDigest digest = MessageDigest.getInstance("MD5");
		         
		        //Update input string in message digest
		        digest.update(input.getBytes(), 0, input.length());
		 
		        //Converts message digest value in base 16 (hex) 
		        md5 = new BigInteger(1, digest.digest()).toString(16);
	 
	        } catch (NoSuchAlgorithmException e) {
	 
	            e.printStackTrace();
	        }
	        return md5;
	    }

	   
	public static Usuario getusuarioIntegra(String login,String senha)
	{
		try {

			IWsLogin integra = new WSLogin().getWsLoginServicePort();
			WsLoginResponse user = integra.login(login,md5(senha),ConfHandler.getConf("INTEGRA.TOKEN"));//"CPF DO USUARIO","SENHA EM MD5","APP TOKEN"
			
			Usuario usuario = new Usuario();
			usuario.setNome(user.getName());
			WsUserInfoResponse infos = integra.getUserInformation(user.getToken()); // Pegando informa√ß√µes
			usuario.setCPF(infos.getCpf());
			usuario.setEmail(infos.getEmailSiga());
			
			List<Profile> profiles = (infos.getProfileList()).getProfile(); // Pegando a lista de Profiles

			List<Matricula> matriculas = new ArrayList<Matricula>();
			for(int i=0;i<profiles.size();i++)
			{
				Matricula matricula = new Matricula();
				matricula.setMatricula(profiles.get(i).getMatricula());
				matricula.setUsuario(usuario);
				matriculas.add(matricula);
			}
			usuario.setMatriculas(matriculas);
			if(profiles.get(0).getTipo()=="Aluno")
				usuario.setTipoUsuario(2);
			
			return usuario;
			
		}  catch (Exception e) {
			//Impress√£o de erros
			System.out.println("Problemas ao receber informaÁıes do Integra");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}
