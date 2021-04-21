package SourceCode.GroovyPlugins;

import com.navis.argo.business.api.GroovyApi;
import com.navis.argo.business.model.GeneralReference;
import com.navis.billing.business.model.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class MTLBPuploadAteuRteu extends GroovyApi{
    public void execute(Map parameters) {
	//Use General Reference to find the path of .csv
	String sType = "FILE_UPLOAD";
	String sId1 = "ATEU_RTEU";
	String sValue1 = "";
	String sValue2 = "";
	GeneralReference gf1 = GeneralReference.findUniqueEntryById(sType, sId1);
	GeneralReference gf2 = GeneralReference.findUniqueEntryById(sType, sId1);
	sValue1 = gf1.getRefValue1();
	sValue2 = gf2.getRefValue2();
	log("sValue1:" + sValue1);
	log("sValue2:" + sValue2);
	String csvFile = sValue1 + sValue2;
		
	BufferedReader br = null;
	String line = "";
	String cvsSplitBy = ",";

	try {
            log("=====================MTLBpReadFileGeneralReference Start=====================");
            br = new BufferedReader(new FileReader(csvFile));
	    log("br ::" + br.toString());
	    int lineNumber = 0;
	    while ((line = br.readLine()) != null) {
		if (lineNumber == 0) {
		    lineNumber++;
		    continue;
		}
		lineNumber++;
		log("line >>>" + line);
		String[] ateu_rteu_data = line.split(cvsSplitBy);
		log("Size ::" + ateu_rteu_data.size());
				
		//Set the loop for i iterator
		int loopSize = ateu_rteu_data.size()-1;
								
		for (int i = 0; i <= loopSize; i++) {
		    log("ateu_rteu_data[" + i + "]>>>" + ateu_rteu_data[i]);
		}
				
		log("+++++++++++++++Flow Started++++++++++++++++++++");
		String tariffId = ateu_rteu_data[0];
		log("tariffId::" + tariffId);
		Tariff tariff = Tariff.findTariff(tariffId);
		log("tariff::"+tariff);
				
		double ateu = ateu_rteu_data[1].toDouble();
		double rteu = ateu_rteu_data[2].toDouble();
  
		tariff.setTariffFlexDouble01(ateu);
		tariff.setTariffFlexDouble02(rteu);
	    }
	    log("Done");
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    if (br != null) {
		try {
		    br.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
        }
    }
}