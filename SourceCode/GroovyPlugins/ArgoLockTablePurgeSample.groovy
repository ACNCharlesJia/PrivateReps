package SourceCode.GroovyPlugins

import com.navis.argo.ArgoEntity
import com.navis.argo.ArgoField
import com.navis.argo.business.api.ArgoUtils
import com.navis.argo.business.api.GroovyApi
import com.navis.framework.persistence.HibernateApi
import com.navis.framework.portal.QueryUtils
import com.navis.framework.portal.UserContext
import com.navis.framework.portal.context.UserContextUtils
import com.navis.framework.portal.query.DomainQuery
import com.navis.framework.portal.query.PredicateFactory
import com.navis.framework.util.DateUtil
import org.apache.log4j.Logger;

/**
 * Created by sundaga on 30-09-2015.
 * This groovy is about purging the Argo Lock table when the table size has grown significantly large 
 * This groovy can be executed directly through Groovy Job for purging ArgoLock table
 * keeping 10 days record(by default).
 * For user defined days Configure In General reference screen of N4 billing configure as Type :"DAYSTORETAIN"
 * Identifier 1 :"DAYSTORETAIN"
 * Value-1: "1"(user-configurable) 
 * If DAYSTORETAIN is not given , Argo lock records below 10 days from the date this groovy run(if this groovy is 
 * executed in 30-09-2015, then below 20-09-2015 created records will get purged)
 */

public class ArgoLockTablePurgeSample extends GroovyApi {
  public void execute(Map inParameters) {
    GroovyApi groovyApi = new GroovyApi();
    String daysToRetainStr = groovyApi.getReferenceValue(DAYSTORETAIN, DAYSTORETAIN, null, null, 1);
    LOGGER.warn("ArgoLockTablePurgeSample Started");
    UserContext usrContext = UserContextUtils.getSystemUserContext();
    Long daysToRetain = 10L;
    if (daysToRetainStr != null && !daysToRetainStr.isEmpty()) {
      daysToRetain = Long.valueOf(daysToRetainStr);
    }
    long now = ArgoUtils.timeNowMillis();
    Date cutoff = new Date(now - DateUtil.MILLIS_PER_DAY * daysToRetain);
    LOGGER.warn("Date to consider for Purging ArgoLock " + cutoff);
    DomainQuery dq = QueryUtils.createDomainQuery(ArgoEntity.ARGO_LOCK)
            .addDqPredicate(PredicateFactory.lt(ArgoField.LOCK_CREATED, cutoff));
    LOGGER.warn("dq" + dq);

    HibernateApi.getInstance().deleteByDomainQuery(dq);
    LOGGER.warn("ArgoLockTablePurgeSample Ended");
  }
  private static final Logger LOGGER = Logger.getLogger(ArgoLockTablePurgeSample.class);
  private static String DAYSTORETAIN ="DAYSTORETAIN";
}