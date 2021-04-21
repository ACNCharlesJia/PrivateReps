package SourceCode.GroovyPlugins

import com.navis.argo.business.api.GroovyApi;
import com.navis.argo.business.model.GeneralReference;
import com.navis.extension.model.persistence.DynamicHibernatingEntity;
import com.navis.framework.business.Roastery;
import com.navis.framework.metafields.MetafieldIdFactory;
import com.navis.framework.persistence.HibernateApi;
import com.navis.framework.portal.query.DomainQuery;
import com.navis.framework.portal.query.PredicateFactory;
import com.navis.framework.portal.query.QueryFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

public class MTLHouseKeepingBW extends GroovyApi {
    public void execute(Map map) {
        String FN_NAME = ".execute " ;
        if (DEBUG_MODE) this.log(FN_NAME + "Start.");

        try {
            GeneralReference grConfig = GeneralReference.findUniqueEntryById("BP_CONFIGURATION", "HouseKeeping", "BW" );
            if (grConfig == null) {
                return;
            }

            Boolean flag = Boolean.parseBoolean(grConfig.getRefValue1());
            Integer dayAmount = Integer.parseInt(grConfig.getRefValue2());
            Integer preReadRowAmount = Integer.parseInt(grConfig.getRefValue3());
            String backUpPath = grConfig.getRefValue4();
            int index = 1;

            if (flag != null && dayAmount != null && backUpPath != null && preReadRowAmount != null) {
                if (flag && dayAmount > 30 && backUpPath != "") {
                    while (BATCHFLAG && index <= SAVELOOPNUM) {
                        log("index:" + index);
                        List backupDataList = GetData(dayAmount,preReadRowAmount);
                        log("backupDataList.size:"+backupDataList.size());
                        if(backupDataList == null || backupDataList.size() < 1){
                            BATCHFLAG = false;
                            break;
                        }

                        List<Long> GkeyList = new ArrayList<Long>();
                        BackupData(backUpPath,backupDataList,index,GkeyList);

                        log("GkeyList.size:"+ GkeyList.size());
                        removeTable(GkeyList);

                        index++;
                        break;
                    }
                }
            }
        } catch (Exception err) {
            log(FN_NAME + "Exception:" + err.getMessage());
        }
    }

    private List GetData(int dayAmount,int preReadRowAmount){
        String FN_NAME = ".GetData " ;
        if (DEBUG_MODE) log(FN_NAME + "Start.");

        GregorianCalendar cal = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        cal.setTime(new Date());
        cal.add(5,(0-dayAmount));
        Date actionDate =cal.getTime();
        String day = sdf.format(actionDate);
        String ds = day + " 00:00:00";

        String hql = "select log from DataExchange log where " +
                " customEntityFields.custombwdatacrtdte < to_date('" + ds + "','YYYY-MM-DD HH24:MI:SS')" +
                " and rownum <= " + preReadRowAmount;
        log(FN_NAME + "hql:" + hql);
        List resultList = Roastery.getHibernateApi().find(hql);
        log(FN_NAME + "hql.resultList:" + resultList.size());

        return resultList;
    }

    private void BackupData(String backupPath,List dataList,int backupIndexNum,List<Long> gkeyList){
        String FN_NAME = ".BackupData " ;
        if (DEBUG_MODE) this.log(FN_NAME + "Start.");

        try {
            String dateTime = new SimpleDateFormat('yyyyMMddhhmmss').format(new Date());
            String fileName = "MTLHK_DataExchange_" + dateTime + "_" + backupIndexNum.toString() + ".csv";

            List<String> finalStringsList = new ArrayList<String>();
            String line = GetHeaderLine();
            finalStringsList.add(line);

            for (int n = 0; n < dataList.size(); n++) {
                if (dataList[n] != null) {
                    DynamicHibernatingEntity entity = (DynamicHibernatingEntity) dataList[n];

                    if (entity != null) {
                        line = DataToLine(entity);
                        finalStringsList.add(line);
                        gkeyList.add(entity.getCustomEntityGkey());
                    }
                }

            }

            log("finalStringsList.size:" + finalStringsList.size());
            //log("finalStringsList.List:" + finalStringsList);


            File file = createBackupFile(backupPath, fileName);
            if (file != null) {
                writeStringsToFile(file, finalStringsList);
            }

        } catch (Exception err) {
            log(FN_NAME + "Exception." + err.getMessage());
        }
    }

    private String GetHeaderLine(){
        String line =  "\"customEntityGkey\"," +
                "\"" + FieldPrefix + "ateu\"," +
                "\"" + FieldPrefix + "bildte\"," +
                "\"" + FieldPrefix + "bilitmamt\"," +
                "\"" + FieldPrefix + "ccde\"," +
                "\"" + FieldPrefix + "ccid\"," +
                "\"" + FieldPrefix + "crtdte\"," +
                "\"" + FieldPrefix + "dnum\"," +
                "\"" + FieldPrefix + "optcde\"," +
                "\"" + FieldPrefix + "qty\"," +
                "\"" + FieldPrefix + "rteu\"," +
                "\"" + FieldPrefix + "svccde\"," +
                "\"" + FieldPrefix + "tarcde\"," +
                "\"" + FieldPrefix + "tarcur\"," +
                "\"" + FieldPrefix + "tarrate\"";

        return line;
    }

    private String DataToLine(DynamicHibernatingEntity entity){
        String line ="";
        if(entity != null){
            Map rowMap = entity.getCustomFlexFields();
            line += "\"" + entity.getCustomEntityGkey() + "\"," ;
            line += "\"" + rowMap.get(FieldPrefix + "ateu") + "\"," ;
            line += "\"" + rowMap.get(FieldPrefix + "bildte") + "\"," ;
            line += "\"" + rowMap.get(FieldPrefix + "bilitmamt") + "\"," ;
            line += "\"" + rowMap.get(FieldPrefix + "ccde") + "\"," ;
            line += "\"" + rowMap.get(FieldPrefix + "ccid") + "\"," ;
            line += "\"" + rowMap.get(FieldPrefix + "crtdte") + "\"," ;
            line += "\"" + rowMap.get(FieldPrefix + "dnum") + "\"," ;
            line += "\"" + rowMap.get(FieldPrefix + "optcde") + "\"," ;
            line += "\"" + rowMap.get(FieldPrefix + "qty") + "\"," ;
            line += "\"" + rowMap.get(FieldPrefix + "rteu") + "\"," ;
            line += "\"" + rowMap.get(FieldPrefix + "svccde") + "\"," ;
            line += "\"" + rowMap.get(FieldPrefix + "tarcde") + "\"," ;
            line += "\"" + rowMap.get(FieldPrefix + "tarcur") + "\"," ;
            line += "\"" + rowMap.get(FieldPrefix + "tarrate") + "\"" ;
        }

        return line;
    }

    private File createBackupFile(String pathDir, String fileName) {
        String FN_NAME = ".createBackupFile " ;
        if (DEBUG_MODE) this.log(FN_NAME + "Start.");
        File file =  null;
        try {
            File dir = new File(pathDir);
            if (dir != null) {
                if (dir.isDirectory()) {
                    String path = pathDir + File.separator + fileName;
                    file = new File(path);
                    if (file != null) {
                        BufferedWriter buffWriter = new BufferedWriter(new FileWriter(file, false));
                        buffWriter.write();
                        buffWriter.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private void removeTable(List<Long> GkeyList){
        List<Long> gkeys = new ArrayList<>();
        int result = 0;

        if (GkeyList.size() > 0) {
            for (int n = 1; n <= GkeyList.size(); n++) {
                if ((n % 999) == 0) {
                    gkeys.add(GkeyList[n - 1]);
                    result = ExecuteRemoveSQL("DataExchange", gkeys);
                    gkeys.clear();

                    if (result == 0) {
                        break;
                    }

                    continue;
                } else {
                    gkeys.add(GkeyList[n - 1]);
                }

                if (n == GkeyList.size()) {
                    result = ExecuteRemoveSQL("DataExchange", gkeys);
                    if (result == 0) {
                        break;
                    }
                    gkeys.clear();
                }
            }
        }
    }

    private int ExecuteRemoveSQL(String enityName,List<Long> gkeys){
        int result = 1;
        try {
            DomainQuery dq = QueryFactory.createDomainQuery(enityName)
                    .addDqPredicate(PredicateFactory.in(MetafieldIdFactory.valueOf("customEntityGkey"), gkeys.toArray()));
            HibernateApi.getInstance().deleteByDomainQuery(dq);
        } catch (Exception any) {
            result = 0;
        }
        return result;
    }

    private void writeStringsToFile(File file, List<String> strings) {
        try {
            if(file != null) {
                BufferedWriter buffWriter = new BufferedWriter(new FileWriter(file, true));
                int size = strings.size();
                for (int i=0; i<size; i++) {
                    buffWriter.write(strings[i] + NEXT_LINE);
                }
                buffWriter.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Integer SAVELOOPNUM = 1000;
    private Boolean BATCHFLAG = true;
    private static String  NEXT_LINE = "\r\n";
    private String FieldPrefix = "custombwdata";
    private final boolean DEBUG_MODE = true;
}