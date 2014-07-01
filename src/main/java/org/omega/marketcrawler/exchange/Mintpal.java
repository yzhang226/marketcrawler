package org.omega.marketcrawler.exchange;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omega.marketcrawler.common.Arith;
import org.omega.marketcrawler.common.Symbol;
import org.omega.marketcrawler.entity.TradeRecord;
import org.omega.marketcrawler.net.NetUtils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public final class Mintpal extends TradeOperator {
	
	private static final Log log = LogFactory.getLog(Mintpal.class);
	
	public static final String NAME = "mintpal";
	
	public static final String STATUS_SUCCESS = "success";
	public static final String TYPE_SELL = "SELL";
	public static final String TYPE_BUY = "BUY";
	
	public static final String KEY_STATUS = "status";
	public static final String KEY_COUNT = "count";
	public static final String KEY_DATA = "data";
	
	private static final Mintpal inst = new Mintpal();
	private Mintpal() {}
	
	public static Mintpal instance() {
		return inst;
	}
	
	// https://api.mintpal.com/v2/market/trades/{COIN}/{EXCHANGE}
	public String getHistoryJsonText(String watchedSymbol, String exchangeSymbol) {
		StringBuilder api = new StringBuilder("https://api.mintpal.com/v2/market/trades/");
		api.append(watchedSymbol).append("/").append(exchangeSymbol);
		
		return NetUtils.accessDirectly(api.toString());
	}

	/*
	 * 
	 * {"status":"success","count":100,"data":[{"time":"1404190797.5488","type":"SELL","price":"0.00000005","amount":"3168.76000000","total":"0.00015843"},
	 * {"time":"1404189552.6013","type":"BUY","price":"0.00000006","amount":"63000.00000000","total":"0.00377999"},
	 * @see org.omega.marketcrawler.exchange.TradeOperator#getHistory(java.lang.String, java.lang.String)
	 */
	/**
	 * NOTE: Type 0 refers to a BUY and type 1 refers to a SELL. Time is specified as a unix timestamp with microseconds.
	 */
	public List<TradeRecord> getHistory(String watchedSymbol, String exchangeSymbol) {
		List<TradeRecord> records = null;
		try {
//			String recordText = getHistoryJsonText(watchedSymbol, exchangeSymbol);
//			System.out.println(recordText);
			String recordText = "{\"status\":\"success\",\"count\":100,\"data\":[{\"time\":\"1404202769.9875\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"488.65000000\",\"total\":\"0.00019057\"},{\"time\":\"1404202610.8584\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"487.63000000\",\"total\":\"0.00019017\"},{\"time\":\"1404202371.6221\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"496.86000000\",\"total\":\"0.00019377\"},{\"time\":\"1404202135.4286\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"485.69000000\",\"total\":\"0.00018941\"},{\"time\":\"1404202015.3269\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"275.00000000\",\"total\":\"0.00010725\"},{\"time\":\"1404202009.3224\",\"type\":\"BUY\",\"price\":\"0.00000040\",\"amount\":\"275.00000000\",\"total\":\"0.00010999\"},{\"time\":\"1404201945.2832\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"275.00000000\",\"total\":\"0.00010725\"},{\"time\":\"1404201940.2794\",\"type\":\"BUY\",\"price\":\"0.00000040\",\"amount\":\"275.00000000\",\"total\":\"0.00010999\"},{\"time\":\"1404201912.248\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"494.38900000\",\"total\":\"0.00019281\"},{\"time\":\"1404201755.1196\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"345.87868198\",\"total\":\"0.00013489\"},{\"time\":\"1404201749.1044\",\"type\":\"BUY\",\"price\":\"0.00000040\",\"amount\":\"345.87868198\",\"total\":\"0.00013835\"},{\"time\":\"1404201672.0495\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"493.50000000\",\"total\":\"0.00019246\"},{\"time\":\"1404201647.0382\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"310.18222666\",\"total\":\"0.00012097\"},{\"time\":\"1404201641.0233\",\"type\":\"BUY\",\"price\":\"0.00000040\",\"amount\":\"310.18222666\",\"total\":\"0.00012407\"},{\"time\":\"1404201562.9749\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"299.19870195\",\"total\":\"0.00011668\"},{\"time\":\"1404201556.9523\",\"type\":\"BUY\",\"price\":\"0.00000040\",\"amount\":\"299.19870195\",\"total\":\"0.00011967\"},{\"time\":\"1404201410.8398\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"502.30000000\",\"total\":\"0.00019589\"},{\"time\":\"1404201166.6423\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"501.69000000\",\"total\":\"0.00019565\"},{\"time\":\"1404200942.477\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"500.69000000\",\"total\":\"0.00019526\"},{\"time\":\"1404200804.3602\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"28125.59997213\",\"total\":\"0.01096898\"},{\"time\":\"1404200748.3177\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"5540.36990000\",\"total\":\"0.00216074\"},{\"time\":\"1404200683.2617\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"498.65000000\",\"total\":\"0.00019447\"},{\"time\":\"1404200460.1051\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"1023.00000000\",\"total\":\"0.00039897\"},{\"time\":\"1404200244.911\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"499.00000000\",\"total\":\"0.00019461\"},{\"time\":\"1404199991.7212\",\"type\":\"BUY\",\"price\":\"0.00000041\",\"amount\":\"3579.48199652\",\"total\":\"0.00146758\"},{\"time\":\"1404199585.4066\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"303233.19802029\",\"total\":\"0.11826092\"},{\"time\":\"1404199585.3929\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"167130.69719763\",\"total\":\"0.06518099\"},{\"time\":\"1404199444.3065\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"32869.30280237\",\"total\":\"0.01281901\"},{\"time\":\"1404199444.2952\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"217130.69719763\",\"total\":\"0.08468099\"},{\"time\":\"1404199321.2436\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"257.00000000\",\"total\":\"0.00010023\"},{\"time\":\"1404199257.1505\",\"type\":\"BUY\",\"price\":\"0.00000040\",\"amount\":\"290.00000000\",\"total\":\"0.00011600\"},{\"time\":\"1404199224.126\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"300.00000000\",\"total\":\"0.00011700\"},{\"time\":\"1404199207.1121\",\"type\":\"BUY\",\"price\":\"0.00000040\",\"amount\":\"249.70000000\",\"total\":\"0.00009987\"},{\"time\":\"1404198833.8318\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"29810.75115508\",\"total\":\"0.01192430\"},{\"time\":\"1404197333.6701\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"11421.65223983\",\"total\":\"0.00456866\"},{\"time\":\"1404196628.1451\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"897.00000000\",\"total\":\"0.00035880\"},{\"time\":\"1404196617.1337\",\"type\":\"BUY\",\"price\":\"0.00000042\",\"amount\":\"561.08716145\",\"total\":\"0.00023566\"},{\"time\":\"1404196617.1206\",\"type\":\"BUY\",\"price\":\"0.00000042\",\"amount\":\"3278.05650400\",\"total\":\"0.00137678\"},{\"time\":\"1404196332.8936\",\"type\":\"BUY\",\"price\":\"0.00000041\",\"amount\":\"350.00000000\",\"total\":\"0.00014350\"},{\"time\":\"1404195114.9461\",\"type\":\"BUY\",\"price\":\"0.00000041\",\"amount\":\"230700.00000000\",\"total\":\"0.09458700\"},{\"time\":\"1404195019.8794\",\"type\":\"BUY\",\"price\":\"0.00000041\",\"amount\":\"295711.80123350\",\"total\":\"0.12124184\"},{\"time\":\"1404195019.8653\",\"type\":\"BUY\",\"price\":\"0.00000041\",\"amount\":\"4288.19876650\",\"total\":\"0.00175816\"},{\"time\":\"1404195003.8719\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"42312.30280237\",\"total\":\"0.01650178\"},{\"time\":\"1404195003.8599\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"87687.69719763\",\"total\":\"0.03419822\"},{\"time\":\"1404195003.8443\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"70000.00000000\",\"total\":\"0.02799999\"},{\"time\":\"1404194891.7549\",\"type\":\"BUY\",\"price\":\"0.00000040\",\"amount\":\"19032.59444997\",\"total\":\"0.00761303\"},{\"time\":\"1404194885.75\",\"type\":\"BUY\",\"price\":\"0.00000040\",\"amount\":\"1565.97966386\",\"total\":\"0.00062639\"},{\"time\":\"1404194823.6967\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"129401.42588617\",\"total\":\"0.05176057\"},{\"time\":\"1404194524.4601\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"99579.65000000\",\"total\":\"0.03983186\"},{\"time\":\"1404194524.4452\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"420.35000000\",\"total\":\"0.00016814\"},{\"time\":\"1404193904.9583\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"379.65000000\",\"total\":\"0.00015185\"},{\"time\":\"1404192114.5811\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"514.27081600\",\"total\":\"0.00020056\"},{\"time\":\"1404191594.1947\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"1100.00000000\",\"total\":\"0.00042900\"},{\"time\":\"1404188169.5335\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"595.28104416\",\"total\":\"0.00023215\"},{\"time\":\"1404188048.6089\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"102.75094221\",\"total\":\"0.00004007\"},{\"time\":\"1404188048.5606\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"611.00000000\",\"total\":\"0.00023829\"},{\"time\":\"1404187451.0047\",\"type\":\"SELL\",\"price\":\"0.00000039\",\"amount\":\"500.00000000\",\"total\":\"0.00019500\"},{\"time\":\"1404187450.9921\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"6932.00000000\",\"total\":\"0.00277280\"},{\"time\":\"1404187450.9759\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"25415.91203301\",\"total\":\"0.01016644\"},{\"time\":\"1404187443.0172\",\"type\":\"BUY\",\"price\":\"0.00000042\",\"amount\":\"500.00000000\",\"total\":\"0.00021000\"},{\"time\":\"1404187411.9475\",\"type\":\"BUY\",\"price\":\"0.00000042\",\"amount\":\"32347.91203301\",\"total\":\"0.01358612\"},{\"time\":\"1404186638.345\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"431142.47128148\",\"total\":\"0.17245698\"},{\"time\":\"1404186056.8916\",\"type\":\"BUY\",\"price\":\"0.00000042\",\"amount\":\"55084.20464541\",\"total\":\"0.02313536\"},{\"time\":\"1404185928.7993\",\"type\":\"BUY\",\"price\":\"0.00000042\",\"amount\":\"237738.65154649\",\"total\":\"0.09985023\"},{\"time\":\"1404185533.4897\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"500000.00000000\",\"total\":\"0.20000000\"},{\"time\":\"1404185245.2557\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"205000.00000000\",\"total\":\"0.08199999\"},{\"time\":\"1404184839.9311\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"14999.00000000\",\"total\":\"0.00599960\"},{\"time\":\"1404184479.6238\",\"type\":\"BUY\",\"price\":\"0.00000042\",\"amount\":\"237.80000000\",\"total\":\"0.00009987\"},{\"time\":\"1404184066.2923\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"40395.00000000\",\"total\":\"0.01615799\"},{\"time\":\"1404183510.8885\",\"type\":\"BUY\",\"price\":\"0.00000042\",\"amount\":\"42189.11870290\",\"total\":\"0.01771942\"},{\"time\":\"1404183130.5521\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"999.00000000\",\"total\":\"0.00039959\"},{\"time\":\"1404182919.3836\",\"type\":\"BUY\",\"price\":\"0.00000042\",\"amount\":\"57489.88184390\",\"total\":\"0.02414575\"},{\"time\":\"1404182520.0569\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"782048.61668551\",\"total\":\"0.31281941\"},{\"time\":\"1404182520.0453\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"217951.38331449\",\"total\":\"0.08718059\"},{\"time\":\"1404182444.9893\",\"type\":\"BUY\",\"price\":\"0.00000042\",\"amount\":\"9600.00000000\",\"total\":\"0.00403200\"},{\"time\":\"1404182411.9857\",\"type\":\"BUY\",\"price\":\"0.00000041\",\"amount\":\"1525.46754000\",\"total\":\"0.00062544\"},{\"time\":\"1404182411.9714\",\"type\":\"BUY\",\"price\":\"0.00000041\",\"amount\":\"10825.23243185\",\"total\":\"0.00443835\"},{\"time\":\"1404181862.5329\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"3999.00000000\",\"total\":\"0.00159959\"},{\"time\":\"1404181381.1537\",\"type\":\"BUY\",\"price\":\"0.00000041\",\"amount\":\"200000.00000000\",\"total\":\"0.08200000\"},{\"time\":\"1404181346.1387\",\"type\":\"BUY\",\"price\":\"0.00000041\",\"amount\":\"1000000.00000000\",\"total\":\"0.41000000\"},{\"time\":\"1404181345.1229\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"275.00000000\",\"total\":\"0.00010999\"},{\"time\":\"1404181335.1158\",\"type\":\"BUY\",\"price\":\"0.00000041\",\"amount\":\"275.00000000\",\"total\":\"0.00011275\"},{\"time\":\"1404181309.0948\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"9310.00000000\",\"total\":\"0.00372400\"},{\"time\":\"1404181288.078\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"799.99000000\",\"total\":\"0.00031999\"},{\"time\":\"1404181251.05\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"262001.00000000\",\"total\":\"0.10480040\"},{\"time\":\"1404181175.0068\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"5663.62668551\",\"total\":\"0.00226544\"},{\"time\":\"1404181174.9922\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"7947.85089049\",\"total\":\"0.00317915\"},{\"time\":\"1404178610.9814\",\"type\":\"BUY\",\"price\":\"0.00000041\",\"amount\":\"244.00000000\",\"total\":\"0.00010004\"},{\"time\":\"1404178176.648\",\"type\":\"BUY\",\"price\":\"0.00000041\",\"amount\":\"2500.00000000\",\"total\":\"0.00102500\"},{\"time\":\"1404178120.6064\",\"type\":\"BUY\",\"price\":\"0.00000041\",\"amount\":\"120265.45303995\",\"total\":\"0.04930883\"},{\"time\":\"1404177938.4576\",\"type\":\"BUY\",\"price\":\"0.00000041\",\"amount\":\"40400.00000000\",\"total\":\"0.01656400\"},{\"time\":\"1404177347.9678\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"2839.79277100\",\"total\":\"0.00113591\"},{\"time\":\"1404176516.2991\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"28000.00000000\",\"total\":\"0.01120000\"},{\"time\":\"1404176306.1363\",\"type\":\"BUY\",\"price\":\"0.00000042\",\"amount\":\"697.38012030\",\"total\":\"0.00029289\"},{\"time\":\"1404175849.8135\",\"type\":\"BUY\",\"price\":\"0.00000042\",\"amount\":\"7000.00000000\",\"total\":\"0.00294000\"},{\"time\":\"1404175849.7999\",\"type\":\"BUY\",\"price\":\"0.00000042\",\"amount\":\"5000.00000000\",\"total\":\"0.00210000\"},{\"time\":\"1404175679.6742\",\"type\":\"BUY\",\"price\":\"0.00000042\",\"amount\":\"30000.00000000\",\"total\":\"0.01260000\"},{\"time\":\"1404175570.5744\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"5271.31539992\",\"total\":\"0.00210852\"},{\"time\":\"1404175570.5565\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"44728.68460008\",\"total\":\"0.01789148\"},{\"time\":\"1404175474.4668\",\"type\":\"SELL\",\"price\":\"0.00000040\",\"amount\":\"5271.31539992\",\"total\":\"0.00210852\"}]}";
			JsonFactory jfactory = new JsonFactory();
			JsonParser parser = jfactory.createParser(recordText);
			records = readData(parser);
		} catch (Exception e) {
			log.error("try to get and convert json history to object error.", e);
		}
		
		return records;
	}
	
	
	List<TradeRecord> readData(JsonParser parser) throws Exception {
	  // Sanity check: verify that we got "Json Object":
	  if (parser.nextToken() != JsonToken.START_OBJECT) {
	    throw new Exception("Expected data to start with an Object");
	  }

	  List<TradeRecord> records = null;
	  int count = 0;
	  String fieldValue = null;
	  // Iterate over object fields:
	  
	  while (parser.nextToken() != JsonToken.END_OBJECT) {
	   String fieldName = parser.getCurrentName();
	   parser.nextToken(); // Let's move to value
	   
	   if (KEY_STATUS.equalsIgnoreCase(fieldName)) {
		   if (!STATUS_SUCCESS.equals(parser.getText())) {
			   break;
		   }
	   } else if (KEY_COUNT.equalsIgnoreCase(fieldName)) {
		   count = parser.getIntValue();
	   } else if (KEY_DATA.equalsIgnoreCase(fieldName)) {
		   records = new ArrayList<TradeRecord>(count);
		   TradeRecord re = null;
		   while (parser.nextToken() != JsonToken.END_ARRAY) {
			   if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
				   re = new TradeRecord();
				   parser.nextToken();
			   } else if (parser.getCurrentToken() == JsonToken.END_OBJECT) {
				   records.add(re);
				   continue;
			   }
			   
			   fieldName = parser.getCurrentName();
			   
			   parser.nextToken();
			   fieldValue = parser.getText();
			   
			   if (fieldName.equalsIgnoreCase("type")) {
				   if (TYPE_BUY.equalsIgnoreCase(fieldValue)) {
					   re.setTradeType(TradeRecord.TYPE_BUY);
				   } else if (TYPE_SELL.equalsIgnoreCase(fieldValue)) {
					   re.setTradeType(TradeRecord.TYPE_SELL);
				   }
			   } else if (fieldName.equalsIgnoreCase("price")) {
				   re.setPrice(Double.valueOf(fieldValue));
			   } else if (fieldName.equalsIgnoreCase("amount")) {
				   re.setTotalUnits(Double.valueOf(fieldValue));
			   } else if (fieldName.equalsIgnoreCase("total")) {
				   re.setTotalCost(Double.valueOf(fieldValue));
			   } else if (fieldName.equalsIgnoreCase("time")) {// NOTE: Time is specified as a unix timestamp with microseconds.
				   /*
				    * microsecond	μs	1 microsecond = 1,000 nanoseconds
					* millisecond	ms	1 millisecond = 1,000 microseconds
				    */
				   re.setTradeTime((long) Arith.multiply(Double.valueOf(fieldValue), 1000));
			   }
			   
		   }
	   }
	   
	  }
	  parser.close(); // important to close both parser and underlying File reader
	  
	  return records;
	 }
	
	public static void main(String[] args) {
//		Mintpal.instance().getHistory("DOGE", Symbol.BTC.name());
		String watchedSymbol = "BC";
		String exchangeSymbol = Symbol.BTC.name();
		List<TradeRecord> records = Mintpal.instance().getHistory(watchedSymbol, exchangeSymbol);
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.ss");
//		for (TradeRecord r : rs) {
//			System.out.println(sd.format(new Date(r.getTradeTime())) + ", " + r.toReadableText());
//		}
//		System.out.println(SqlUtils.getInsertSql4TradeRecord(watchedSymbol, exchangeSymbol));
	}

}
