package SourceCode.GroovyPlugins

import com.navis.argo.business.api.GroovyApi;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

class MTLBPExceptionReportScheduleJobForN4B extends GroovyApi {
    public void execute(Map maps) {
        Date startTime = new Date();
        String type = "CTL";
        String process = "DAY_END";
        String status =  "STARTED";
        String description = "02_" + process;
        String success = "SUCCESS";
        String error = "ERROR";
        SimpleDateFormat inDateformat = new SimpleDateFormat("yyyy/MM/dd");

        try {
            try {
                process = "MTLBPGenerateAndSaveRP404c";
                GroovyApi MTLBPGenerateAndSaveRP404c = (GroovyApi) (new GroovyApi()).getGroovyClassInstance("MTLBPGenerateAndSaveRP404c");
                MTLBPGenerateAndSaveRP404c.GenerateAndSaveRP404cFile(inDateformat.format(startTime));
            } catch (Exception e) {
                log(process + "Exception:" + e);
            }
        } catch (Exception e) {
            log("Exception:" + e);
        }
    }
}