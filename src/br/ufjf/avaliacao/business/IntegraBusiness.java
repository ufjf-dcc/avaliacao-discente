package br.ufjf.avaliacao.business;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.ice.integra3.ws.login.interfaces.IWsLogin;
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
			WsLoginResponse user = integra.login(login,md5(senha),"token");//"CPF DO USUARIO","SENHA EM MD5","APP TOKEN"
			
			System.out.println("Integra"); //excluir

			Usuario usuario = new Usuario();
			usuario.setNome(user.getName());

			WsUserInfoResponse infos = integra.getUserInformation(user.getToken()); // Pegando informações
			usuario.setCPF(infos.getCpf());

			return usuario;
			
		}  catch (Exception e) {
			//Impressão de erros
			System.out.println("erro");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	


	

}
