package it.polito.tdp.poweroutages.DAO;

import java.sql.Connection;

import it.polito.tdp.poweroutages.model.NercIdMap;

public class TestPowerOutagesDAO {

	public static void main(String[] args) {
		NercIdMap nim = new NercIdMap();
		
		try {
			Connection connection = ConnectDB.getConnection();
			connection.close();
			System.out.println("Connection Test PASSED");

		} catch (Exception e) {
			System.err.println("Test FAILED");
		}
		
		PowerOutageDAO dao = new PowerOutageDAO();
		
		System.out.println(dao.getNercList(nim));
		System.out.println(dao.getPowerOutageList(nim));
	}

}
