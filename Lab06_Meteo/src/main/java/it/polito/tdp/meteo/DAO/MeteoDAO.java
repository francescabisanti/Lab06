package it.polito.tdp.meteo.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.meteo.model.Citta;
import it.polito.tdp.meteo.model.Rilevamento;

public class MeteoDAO {
	int meseRilevamento;
	
	public List<Rilevamento> getAllRilevamenti() {

		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {
		final String sql = "SELECT Localita, Data, Umidita "
				+ "FROM situazione "
				+ "WHERE Localita=? AND MONTH(DATA)=? "
				+ "ORDER BY DATA ASC";

		List<Rilevamento> rilevamentiLocalita = new ArrayList<Rilevamento>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, localita);
			st.setInt(2, mese);
			this.meseRilevamento=mese;
			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				
				rilevamentiLocalita.add(r);
			}
			
			conn.close();
			return rilevamentiLocalita;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		
	}
	public Map<String, Float> getUmiditaMedia(int mese) {
		final String sql = "SELECT Localita, AVG (Umidita) AS avg "
				+ "FROM situazione "
				+ "WHERE MONTH(DATA)=? "
				+ "GROUP BY Localita";

		Map <String, Float> rilevamentiMAP= new HashMap <String, Float>();
		

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setInt(1, mese);
			
			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				
				String loc= rs.getString("Localita");
				float media= rs.getFloat("avg");
				
				rilevamentiMAP.put(loc, media);
			}
			
			conn.close();
			return rilevamentiMAP;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		
	}

	public List<Citta> getAllCitta() {
		final String sql = "SELECT DISTINCT Localita "
				+ "FROM situazione";

		List <Citta> leCitta= new ArrayList <Citta>();
		

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			
			
			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				
				String loc= rs.getString("Localita");
				List <Rilevamento>rilcitt=null;
				Citta c= new Citta (loc, rilcitt);
				leCitta.add(c);
				
			}
			
			conn.close();
			return leCitta;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
	}



